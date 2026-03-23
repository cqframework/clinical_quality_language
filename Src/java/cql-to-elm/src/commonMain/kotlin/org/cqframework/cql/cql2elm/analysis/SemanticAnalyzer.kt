@file:Suppress("UnusedParameter")

package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.hl7.cql.ast.CodeDefinition
import org.hl7.cql.ast.CodeSystemDefinition
import org.hl7.cql.ast.ConceptDefinition
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IncludeDefinition
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.OperandDefinition
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.RewritingFold
import org.hl7.cql.ast.UsingDefinition
import org.hl7.cql.ast.ValueSetDefinition
import org.hl7.cql.ast.rewriteLibrary
import org.hl7.cql.model.DataType

/**
 * Entry point for semantic analysis of a CQL [Library]. Orchestrates the full pipeline from parsed
 * AST to a [SemanticModel] that codegen ([ElmEmitter]) reads mechanically.
 *
 * ## Pipeline phases (in order)
 * 1. **ModelContext construction** — resolves [UsingDefinition]s via [ModelManager] into a
 *    [ModelContext] that all downstream phases share for model-type and property lookup.
 * 2. **AgeIn desugaring** — structural rewrite ([AgeInDesugarer], a [RewritingFold]) that expands
 *    `AgeIn*()` into `CalculateAgeIn*(Patient.birthDate)`. Needs model info (birth-date property
 *    name) but not types, so it runs before inference.
 * 3. **Symbol collection** — [SymbolCollector] builds the [SymbolTable] (expression definitions,
 *    function definitions, parameter definitions) from top-level library declarations.
 * 4. **INFER → UNIFY convergence loop** (max 2 iterations):
 *     - *INFER*: [TypeResolver] walks the AST, infers types, resolves operators and identifiers,
 *       populating a [TypeTable]. Reads the [SyntheticTable] for effective types of operands (e.g.,
 *       a null literal that has been synthetically cast to Integer).
 *     - *UNIFY*: [TypeUnifier] discovers implicit conversions (branch unification, null wrapping,
 *       choice wrapping, interval bound propagation, DateTime null-arg conversions) and records
 *       them in the [SyntheticTable] without mutating the AST.
 *     - The loop exists because type inference and conversion discovery are mutually dependent: a
 *       new synthetic may change the effective type of an operand, which may change operator
 *       resolution, which may expose new conversion needs. Convergence is reached when no new
 *       synthetics are inserted.
 * 5. **Semantic validation** — [SemanticValidator] flags errors (unresolved identifiers, undeclared
 *    functions, invalid casts, etc.) in the [SemanticModel].
 * 6. **Normalization** — [Normalizer] performs type-directed structural lowering (phrase → operator
 *    trees, concat coalescing, interval expansion, heterogeneous flatten). Produces a new AST with
 *    rewritten parent nodes.
 * 7. **Post-normalization re-collection and re-typing** — new AST nodes created by normalization
 *    need symbols and types. The post-normalization [TypeTable] is merged with the cumulative
 *    pre-normalization table so that unchanged expressions keep their types.
 *
 * ## Result
 *
 * A [SemanticModel] bundling [SymbolTable], [TypeTable], [OperatorRegistry], [SyntheticTable], and
 * [ModelContext]. The [ElmEmitter] reads this to produce ELM output.
 *
 * ## Extending
 *
 * To add a new analysis phase: implement [ExpressionFold]<R> (which gives compile-time exhaustive
 * dispatch — adding a new Expression subtype without a handler is a compile error), then wire the
 * new phase into [analyze] at the appropriate point in the pipeline.
 */
class SemanticAnalyzer(
    private val symbolCollector: SymbolCollector = SymbolCollector(),
    private val operatorRegistry: OperatorRegistry = OperatorRegistry.createSystemRegistry(),
    private val semanticValidator: SemanticValidator = SemanticValidator(),
    val modelManager: ModelManager? = null,
    val options: CqlCompilerOptions? = null,
    val libraryManager: LibraryManager? = null,
) {
    data class Result(
        val library: Library,
        val semanticModel: SemanticModel,
        val diagnostics: kotlin.collections.List<Diagnostic> = emptyList(),
    )

    @Suppress("MagicNumber")
    fun analyze(library: Library): Result {
        // Build ModelContext from UsingDefinitions before any analysis phase.
        // This is the single model-resolution interface used by all downstream phases.
        val modelContext = buildModelContext(library)

        // Desugar AgeIn*() → CalculateAgeIn*(Patient.birthDate) before type inference.
        // This is purely structural (needs model info, not types) so the convergence loop
        // can resolve CalculateAgeIn* against the operator registry and discover conversions.
        val desugared = desugarAgeInFunctions(library, modelContext)
        val symbols = symbolCollector.collect(desugared)

        // INFER → UNIFY convergence loop.
        // Each iteration: infer types (using effective types from synthetics), then record
        // all conversion kinds in the SyntheticTable. No AST mutation — the AST stays immutable.
        // Converges when no new synthetics are recorded (or max iterations reached).
        val cumulativeTypeTable = TypeTable()
        var totalConversions = 0
        val conversionsPerIteration = mutableListOf<Int>()
        val maxIterations = 2
        val syntheticTable = SyntheticTable()

        for (iteration in 1..maxIterations) {
            // INFER: type resolution + overload resolution (reads synthetics for effective types)
            val resolver =
                TypeResolver(operatorRegistry, syntheticTable, modelContext, libraryManager)
            val iterationTypeTable = resolver.resolve(desugared, symbols)

            // Merge this iteration's results into the cumulative table.
            // Later iterations may lose types that earlier iterations resolved (e.g., when
            // effective types from synthetics break generic resolution). mergeFrom preserves
            // the earliest successful type/resolution for each expression.
            cumulativeTypeTable.mergeFrom(iterationTypeTable)

            // UNIFY: type unification, null-wrapping, choice wrapping (no AST mutation)
            val unifier = TypeUnifier(cumulativeTypeTable, operatorRegistry, syntheticTable)
            unifier.analyzeLibrary(desugared)

            val inserted = unifier.newSyntheticsInserted
            conversionsPerIteration.add(inserted)
            totalConversions += inserted

            // CHECK: converged if no new synthetics recorded
            if (inserted == 0) break
        }

        val finalTypeTable = cumulativeTypeTable

        val preNormModel =
            SemanticModel(
                symbols,
                finalTypeTable,
                operatorRegistry,
                options,
                syntheticTable = syntheticTable,
            )
        semanticValidator.validate(desugared, symbols, preNormModel)

        // NORMALIZATION: structural rewrites (phrases → operator trees, coalesce wrapping, etc.)
        // Produces a new AST with complex phrases rewritten into simpler nodes.
        val normalizer = Normalizer(preNormModel)
        val normalizedLibrary = normalizer.normalizeLibrary(desugared)

        // Re-collect symbols and re-type the normalized AST: normalization may create new
        // expressions
        // (QueryExpression for flatten, ConversionExpression for type casts) that need typing.
        // Merge the pre-normalization cumulative table so unchanged expressions keep their types.
        val normalizedSymbols = symbolCollector.collect(normalizedLibrary)
        val normalizedResolver =
            TypeResolver(operatorRegistry, syntheticTable, modelContext, libraryManager)
        val normalizedTypeTable = normalizedResolver.resolve(normalizedLibrary, normalizedSymbols)
        normalizedTypeTable.mergeFrom(finalTypeTable)

        val semanticModel =
            SemanticModel(
                normalizedSymbols,
                normalizedTypeTable,
                operatorRegistry,
                options,
                errors = preNormModel.errors,
                syntheticTable = syntheticTable,
                modelContext = modelContext,
            )

        semanticModel.metrics =
            AnalysisMetrics(
                definitionCount = normalizedLibrary.definitions.size,
                statementCount = normalizedLibrary.statements.size,
                expressionCount = finalTypeTable.expressionCount,
                typedCount = finalTypeTable.typedCount,
                unresolvedCount = finalTypeTable.expressionCount - finalTypeTable.typedCount,
                operatorResolutionCount = finalTypeTable.operatorResolutionCount,
                identifierResolutionCount = finalTypeTable.identifierResolutionCount,
                conversionsInserted = totalConversions,
                inferConvertIterations = conversionsPerIteration.size,
                newConversionsPerIteration = conversionsPerIteration,
                errorCount = preNormModel.errors.size,
            )
        return Result(normalizedLibrary, semanticModel)
    }

    /**
     * Build a [ModelContext] from the library's using definitions and the ModelManager. The System
     * model is implicit per the CQL spec, so this always returns a non-null ModelContext —
     * [ModelContext.systemOnly] when no ModelManager or UsingDefinitions are available.
     */
    private fun buildModelContext(library: Library): ModelContext {
        val mm = modelManager ?: return ModelContext.systemOnly()
        val usingDefs = library.definitions.filterIsInstance<UsingDefinition>()
        if (usingDefs.isEmpty()) return ModelContext.systemOnly()
        return ModelContext(mm, usingDefs)
    }

    /**
     * Desugar AgeIn*() and AgeIn*At() calls by injecting Patient.birthDate from the model. Purely
     * structural — needs model info but not types. Returns the library unchanged if no model is
     * available or no AgeIn* calls are present.
     */
    private fun desugarAgeInFunctions(library: Library, modelContext: ModelContext): Library {
        val usingDef =
            library.definitions.filterIsInstance<UsingDefinition>().firstOrNull {
                it.modelIdentifier.simpleName != "System"
            } ?: return library
        val model =
            try {
                modelContext.resolveModel(
                    usingDef.modelIdentifier.simpleName,
                    usingDef.version?.value,
                )
            } catch (_: Exception) {
                return library
            }
        val birthDateProp = model.modelInfo.patientBirthDatePropertyName ?: return library
        val desugarer = AgeInDesugarer(birthDateProp)
        return rewriteLibrary(desugarer, library)
    }
}

/**
 * AST rewriting fold that rewrites AgeIn*() and AgeIn*At() calls into CalculateAgeIn* calls with
 * the Patient birth date property injected as the first argument.
 */
private class AgeInDesugarer(private val birthDatePropertyName: String) : RewritingFold() {
    override fun onFunctionCall(
        expr: org.hl7.cql.ast.FunctionCallExpression,
        target: Expression?,
        arguments: List<Expression>,
    ): Expression {
        val name = expr.function.value
        val isAgeIn = name.startsWith("AgeIn") && !name.startsWith("AgeIncludes")
        if (!isAgeIn) return super.onFunctionCall(expr, target, arguments)

        val isAt = name.endsWith("At")
        val args = expr.arguments

        // 0-arg AgeIn*() or 1-arg AgeIn*At(date)
        if (!isAt && args.isNotEmpty()) return super.onFunctionCall(expr, target, arguments)
        if (isAt && args.size != 1) return super.onFunctionCall(expr, target, arguments)

        var birthDateExpr: Expression = buildBirthDateExpr(expr)
        val suffix = name.removePrefix("AgeIn")
        val calculateName = "CalculateAgeIn$suffix"

        // CQL spec: AgeIn(Years|Months)() operates on Date precision.
        // Wrap birthDate (DateTime) in ToDate for Year/Month 0-arg calls.
        if (!isAt && (suffix == "Years" || suffix == "Months")) {
            birthDateExpr =
                org.hl7.cql.ast.ConversionExpression(
                    operand = birthDateExpr,
                    destinationType =
                        org.hl7.cql.ast.NamedTypeSpecifier(
                            name = org.hl7.cql.ast.QualifiedIdentifier(listOf("Date"))
                        ),
                    locator = expr.locator,
                )
        }

        val newArgs = listOf(birthDateExpr) + arguments

        return expr.copy(function = org.hl7.cql.ast.Identifier(calculateName), arguments = newArgs)
    }

    private fun buildBirthDateExpr(context: Expression): Expression {
        val patientRef =
            org.hl7.cql.ast.IdentifierExpression(
                org.hl7.cql.ast.QualifiedIdentifier(listOf("Patient")),
                locator = context.locator,
            )
        return org.hl7.cql.ast.PropertyAccessExpression(
            target = patientRef,
            property = org.hl7.cql.ast.Identifier(birthDatePropertyName),
            locator = context.locator,
        )
    }
}

/** Backward-compatible alias. */
@Deprecated("Use SemanticAnalyzer", ReplaceWith("SemanticAnalyzer"))
typealias CompilerFrontend = SemanticAnalyzer

/**
 * Maps AST [Expression] nodes to their inferred [DataType]. Uses an [IdentityHashMap] keyed by
 * reference identity so that structurally identical nodes at different positions never collide —
 * even when they share the same [org.hl7.cql.ast.Locator].
 */
class TypeTable {
    private val types = IdentityHashMap<Expression, DataType>()
    private val operatorResolutions = IdentityHashMap<Expression, OperatorResolution>()
    private val identifierResolutions = IdentityHashMap<IdentifierExpression, Resolution>()
    private val functionCallResolutions =
        IdentityHashMap<FunctionCallExpression, FunctionDefinition>()
    private val operandTypes = IdentityHashMap<OperandDefinition, DataType>()
    private val externalFunctionReturnTypes = IdentityHashMap<FunctionDefinition, DataType>()
    /** Total expressions that had type inference attempted. */
    var expressionCount: Int = 0
        internal set

    /** Expressions that have a non-null inferred type. */
    var typedCount: Int = 0
        internal set

    /** Number of operator resolutions recorded. */
    var operatorResolutionCount: Int = 0
        internal set

    /** Number of identifier resolutions recorded. */
    var identifierResolutionCount: Int = 0
        internal set

    operator fun get(expression: Expression): DataType? = types[expression]

    operator fun set(expression: Expression, type: DataType) {
        if (types[expression] == null) typedCount++
        types[expression] = type
    }

    fun contains(expression: Expression): Boolean = expression in types

    fun getOperatorResolution(expression: Expression): OperatorResolution? =
        operatorResolutions[expression]

    fun setOperatorResolution(expression: Expression, resolution: OperatorResolution) {
        operatorResolutions[expression] = resolution
        operatorResolutionCount++
    }

    fun getIdentifierResolution(expression: IdentifierExpression): Resolution? =
        identifierResolutions[expression]

    fun setIdentifierResolution(expression: IdentifierExpression, resolution: Resolution) {
        identifierResolutions[expression] = resolution
        identifierResolutionCount++
    }

    /** Record that a function call resolved to a user-defined function. */
    fun setFunctionCallResolution(
        expression: FunctionCallExpression,
        definition: FunctionDefinition,
    ) {
        functionCallResolutions[expression] = definition
    }

    /** Check if a function call resolved to a user-defined function. */
    fun getFunctionCallResolution(expression: FunctionCallExpression): FunctionDefinition? =
        functionCallResolutions[expression]

    /** Record the resolved type for a function operand definition. */
    fun setOperandType(operand: OperandDefinition, type: DataType) {
        operandTypes[operand] = type
    }

    /** Look up the resolved type for a function operand definition. */
    fun getOperandType(operand: OperandDefinition): DataType? = operandTypes[operand]

    /** Record the resolved return type for an external function definition. */
    fun setExternalFunctionReturnType(funcDef: FunctionDefinition, type: DataType) {
        externalFunctionReturnTypes[funcDef] = type
    }

    /** Look up the resolved return type for an external function definition. */
    fun getExternalFunctionReturnType(funcDef: FunctionDefinition): DataType? =
        externalFunctionReturnTypes[funcDef]

    /**
     * Merge entries from [other] into this table. For each expression in [other]:
     * - If this table has no type for the expression, copy the type from [other].
     * - If this table already has a type, keep the existing one. Same logic for operator and
     *   identifier resolutions.
     *
     * This preserves types from earlier convergence loop iterations that may be lost in later
     * iterations (e.g., when effective types from synthetics break generic resolution).
     */
    fun mergeFrom(other: TypeTable) {
        for ((expression, type) in other.types) {
            if (types[expression] == null) {
                types[expression] = type
                typedCount++
            }
        }
        for ((expression, resolution) in other.operatorResolutions) {
            if (operatorResolutions[expression] == null) {
                operatorResolutions[expression] = resolution
                operatorResolutionCount++
            }
        }
        for ((expression, resolution) in other.identifierResolutions) {
            if (identifierResolutions[expression] == null) {
                identifierResolutions[expression] = resolution
                identifierResolutionCount++
            }
        }
        for ((expression, definition) in other.functionCallResolutions) {
            if (functionCallResolutions[expression] == null) {
                functionCallResolutions[expression] = definition
            }
        }
        for ((operand, type) in other.operandTypes) {
            if (operandTypes[operand] == null) {
                operandTypes[operand] = type
            }
        }
        for ((funcDef, type) in other.externalFunctionReturnTypes) {
            if (externalFunctionReturnTypes[funcDef] == null) {
                externalFunctionReturnTypes[funcDef] = type
            }
        }
    }
}

/** Describes a resolved reference to a declaration. */
sealed interface Resolution {
    data class ExpressionRef(val definition: ExpressionDefinition) : Resolution

    data class ParameterRef(val definition: ParameterDefinition) : Resolution

    data class ContextRef(val definition: ContextDefinition) : Resolution

    data class OperandRef(val name: String, val type: DataType) : Resolution

    data class AliasRef(val name: String, val type: DataType) : Resolution

    data class QueryLetRef(val name: String, val type: DataType) : Resolution

    data class CodeSystemRef(val definition: CodeSystemDefinition) : Resolution

    data class ValueSetRef(val definition: ValueSetDefinition) : Resolution

    data class CodeRef(val definition: CodeDefinition) : Resolution

    data class ConceptRef(val definition: ConceptDefinition) : Resolution

    /** The identifier resolved to a library alias introduced by an `include` statement. */
    data class IncludeRef(val alias: String, val definition: IncludeDefinition) : Resolution
}

/**
 * Symbol table that retains definitions, contexts, and inferred types discovered during analysis.
 */
data class SymbolTable(
    val usingDefinitions: List<UsingDefinition> = emptyList(),
    val expressionDefinitions: Map<String, ExpressionDefinition> = emptyMap(),
    val parameterDefinitions: Map<String, ParameterDefinition> = emptyMap(),
    val contextDefinitions: List<ContextDefinition> = emptyList(),
    val functionDefinitions: Map<String, List<FunctionDefinition>> = emptyMap(),
    val includeDefinitions: Map<String, IncludeDefinition> = emptyMap(),
    val codeSystemDefinitions: Map<String, CodeSystemDefinition> = emptyMap(),
    val valueSetDefinitions: Map<String, ValueSetDefinition> = emptyMap(),
    val codeDefinitions: Map<String, CodeDefinition> = emptyMap(),
    val conceptDefinitions: Map<String, ConceptDefinition> = emptyMap(),
) {
    fun resolveContext(name: String): Resolution.ContextRef? =
        contextDefinitions
            .firstOrNull { it.context.value == name }
            ?.let { Resolution.ContextRef(it) }

    fun resolveExpression(name: String): Resolution.ExpressionRef? =
        expressionDefinitions[name]?.let { Resolution.ExpressionRef(it) }

    fun resolveParameter(name: String): Resolution.ParameterRef? =
        parameterDefinitions[name]?.let { Resolution.ParameterRef(it) }

    fun resolveFunctions(name: String): List<FunctionDefinition> =
        functionDefinitions[name] ?: emptyList()

    fun resolveCodeSystem(name: String): Resolution.CodeSystemRef? =
        codeSystemDefinitions[name]?.let { Resolution.CodeSystemRef(it) }

    fun resolveValueSet(name: String): Resolution.ValueSetRef? =
        valueSetDefinitions[name]?.let { Resolution.ValueSetRef(it) }

    fun resolveCode(name: String): Resolution.CodeRef? =
        codeDefinitions[name]?.let { Resolution.CodeRef(it) }

    fun resolveConcept(name: String): Resolution.ConceptRef? =
        conceptDefinitions[name]?.let { Resolution.ConceptRef(it) }

    fun resolveInclude(name: String): Resolution.IncludeRef? =
        includeDefinitions[name]?.let { Resolution.IncludeRef(name, it) }
}

/** Represents a diagnostic message produced during compilation. */
data class Diagnostic(val severity: Severity, val message: String) {
    enum class Severity {
        INFO,
        WARNING,
        ERROR,
    }
}

class SymbolCollector {
    fun collect(library: Library): SymbolTable {
        val usingDefs = mutableListOf<UsingDefinition>()
        val expressionDefs = mutableMapOf<String, ExpressionDefinition>()
        val parameterDefs = mutableMapOf<String, ParameterDefinition>()
        val contextDefs = mutableListOf<ContextDefinition>()
        val functionDefs = mutableMapOf<String, MutableList<FunctionDefinition>>()
        val includeDefs = mutableMapOf<String, IncludeDefinition>()
        val codeSystemDefs = mutableMapOf<String, CodeSystemDefinition>()
        val valueSetDefs = mutableMapOf<String, ValueSetDefinition>()
        val codeDefs = mutableMapOf<String, CodeDefinition>()
        val conceptDefs = mutableMapOf<String, ConceptDefinition>()

        for (definition in library.definitions) {
            when (definition) {
                is UsingDefinition -> usingDefs.add(definition)
                is ParameterDefinition -> parameterDefs[definition.name.value] = definition
                is IncludeDefinition -> {
                    val alias = definition.alias?.value ?: definition.libraryIdentifier.simpleName
                    includeDefs[alias] = definition
                }
                is CodeSystemDefinition -> codeSystemDefs[definition.name.value] = definition
                is ValueSetDefinition -> valueSetDefs[definition.name.value] = definition
                is CodeDefinition -> codeDefs[definition.name.value] = definition
                is ConceptDefinition -> conceptDefs[definition.name.value] = definition
                else -> {}
            }
        }

        for (statement in library.statements) {
            when (statement) {
                is ContextDefinition -> contextDefs.add(statement)
                is ExpressionDefinition -> expressionDefs[statement.name.value] = statement
                is FunctionDefinition ->
                    functionDefs.getOrPut(statement.name.value) { mutableListOf() }.add(statement)
                else -> {}
            }
        }

        return SymbolTable(
            usingDefinitions = usingDefs,
            expressionDefinitions = expressionDefs,
            parameterDefinitions = parameterDefs,
            contextDefinitions = contextDefs,
            functionDefinitions = functionDefs,
            includeDefinitions = includeDefs,
            codeSystemDefinitions = codeSystemDefs,
            valueSetDefinitions = valueSetDefs,
            codeDefinitions = codeDefs,
            conceptDefinitions = conceptDefs,
        )
    }
}
