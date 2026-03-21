@file:Suppress("UnusedParameter")

package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.hl7.cql.ast.CodeDefinition
import org.hl7.cql.ast.CodeSystemDefinition
import org.hl7.cql.ast.ConceptDefinition
import org.hl7.cql.ast.ContextDefinition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IncludeDefinition
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.UsingDefinition
import org.hl7.cql.ast.ValueSetDefinition
import org.hl7.cql.model.DataType

/**
 * Entry point for semantic analysis. Orchestrates three passes over the AST:
 * 1. [SymbolCollector] — builds the [SymbolTable]
 * 2. [TypeResolver] — infers types and resolves operators, building the [TypeTable]
 * 3. [SemanticValidator] — detects errors and flags expressions in the [SemanticModel]
 *
 * The result is a [SemanticModel] that codegen reads mechanically.
 */
class SemanticAnalyzer(
    private val symbolCollector: SymbolCollector = SymbolCollector(),
    private val operatorRegistry: OperatorRegistry = OperatorRegistry.createSystemRegistry(),
    private val semanticValidator: SemanticValidator = SemanticValidator(),
    val modelManager: ModelManager? = null,
    val options: CqlCompilerOptions? = null,
) {
    data class Result(
        val library: Library,
        val semanticModel: SemanticModel,
        val diagnostics: kotlin.collections.List<Diagnostic> = emptyList(),
    )

    @Suppress("MagicNumber")
    fun analyze(library: Library): Result {
        // Desugar AgeIn*() → CalculateAgeIn*(Patient.birthDate) before type inference.
        // This is purely structural (needs model info, not types) so the convergence loop
        // can resolve CalculateAgeIn* against the operator registry and discover conversions.
        val desugared = desugarAgeInFunctions(library)
        val symbols = symbolCollector.collect(desugared)

        // INFER → RECORD convergence loop.
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
            val resolver = TypeResolver(operatorRegistry, syntheticTable, modelManager)
            val iterationTypeTable = resolver.resolve(desugared, symbols)

            // Merge this iteration's results into the cumulative table.
            // Later iterations may lose types that earlier iterations resolved (e.g., when
            // effective types from synthetics break generic resolution). mergeFrom preserves
            // the earliest successful type/resolution for each expression.
            cumulativeTypeTable.mergeFrom(iterationTypeTable)

            // RECORD: all conversions in side table (no AST mutation)
            val analyzer = ConversionAnalyzer(cumulativeTypeTable, operatorRegistry, syntheticTable)
            analyzer.analyzeLibrary(desugared)

            val inserted = analyzer.newSyntheticsInserted
            conversionsPerIteration.add(inserted)
            totalConversions += inserted

            // CHECK: converged if no new synthetics recorded
            if (inserted == 0) break
        }

        val finalTypeTable = cumulativeTypeTable

        val preLowerModel =
            SemanticModel(
                symbols,
                finalTypeTable,
                operatorRegistry,
                options,
                syntheticTable = syntheticTable,
            )
        semanticValidator.validate(desugared, symbols, preLowerModel)

        // LOWERING: structural rewrites (phrases → operator trees, coalesce wrapping, etc.)
        // Produces a new AST with complex phrases desugared into simpler nodes.
        val lowering = ExpressionLowering(preLowerModel)
        val loweredLibrary = lowering.lowerLibrary(desugared)

        // Re-collect symbols and re-type the lowered AST: lowering may create new expressions
        // (QueryExpression for flatten, ConversionExpression for type casts) that need typing.
        // Merge the pre-lowering cumulative table so unchanged expressions keep their types.
        val loweredSymbols = symbolCollector.collect(loweredLibrary)
        val loweredResolver = TypeResolver(operatorRegistry, syntheticTable)
        val loweredTypeTable = loweredResolver.resolve(loweredLibrary, loweredSymbols)
        loweredTypeTable.mergeFrom(finalTypeTable)

        val semanticModel =
            SemanticModel(
                loweredSymbols,
                loweredTypeTable,
                operatorRegistry,
                options,
                errors = preLowerModel.errors,
                syntheticTable = syntheticTable,
            )

        semanticModel.metrics =
            AnalysisMetrics(
                definitionCount = loweredLibrary.definitions.size,
                statementCount = loweredLibrary.statements.size,
                expressionCount = finalTypeTable.expressionCount,
                typedCount = finalTypeTable.typedCount,
                unresolvedCount = finalTypeTable.expressionCount - finalTypeTable.typedCount,
                operatorResolutionCount = finalTypeTable.operatorResolutionCount,
                identifierResolutionCount = finalTypeTable.identifierResolutionCount,
                conversionsInserted = totalConversions,
                inferConvertIterations = conversionsPerIteration.size,
                newConversionsPerIteration = conversionsPerIteration,
                errorCount = preLowerModel.errors.size,
            )
        return Result(loweredLibrary, semanticModel)
    }

    /**
     * Desugar AgeIn*() and AgeIn*At() calls by injecting Patient.birthDate from the model. Purely
     * structural — needs model info but not types. Returns the library unchanged if no model is
     * available or no AgeIn* calls are present.
     */
    private fun desugarAgeInFunctions(library: Library): Library {
        val mm = modelManager ?: return library
        val usingDef =
            library.definitions.filterIsInstance<UsingDefinition>().firstOrNull {
                it.modelIdentifier.simpleName != "System"
            } ?: return library
        val model =
            try {
                mm.resolveModel(usingDef.modelIdentifier.simpleName, usingDef.version?.value)
            } catch (_: Exception) {
                return library
            }
        val birthDateProp = model.modelInfo.patientBirthDatePropertyName ?: return library
        val desugarer = AgeInDesugarer(birthDateProp)
        return desugarer.visitLibrary(library)
    }
}

/**
 * AST Transformer that rewrites AgeIn*() and AgeIn*At() calls into CalculateAgeIn* calls with the
 * Patient birth date property injected as the first argument.
 */
private class AgeInDesugarer(private val birthDatePropertyName: String) :
    org.hl7.cql.ast.Transformer() {
    override fun visitFunctionCallExpression(
        expression: org.hl7.cql.ast.FunctionCallExpression
    ): org.hl7.cql.ast.Expression {
        val name = expression.function.value
        val isAgeIn = name.startsWith("AgeIn") && !name.startsWith("AgeIncludes")
        if (!isAgeIn) return super.visitFunctionCallExpression(expression)

        val isAt = name.endsWith("At")
        val args = expression.arguments

        // 0-arg AgeIn*() or 1-arg AgeIn*At(date)
        if (!isAt && args.isNotEmpty()) return super.visitFunctionCallExpression(expression)
        if (isAt && args.size != 1) return super.visitFunctionCallExpression(expression)

        var birthDateExpr: org.hl7.cql.ast.Expression = buildBirthDateExpr(expression)
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
                    locator = expression.locator,
                )
        }

        val visitedArgs = args.map { visitExpression(it) }
        val newArgs = listOf(birthDateExpr) + visitedArgs

        return expression.copy(
            function = org.hl7.cql.ast.Identifier(calculateName),
            arguments = newArgs,
        )
    }

    private fun buildBirthDateExpr(
        context: org.hl7.cql.ast.Expression
    ): org.hl7.cql.ast.Expression {
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
