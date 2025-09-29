package org.cqframework.cql.cql2elm.model

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import org.hl7.cql.model.DataType
import org.hl7.cql.model.NamespaceManager
import org.hl7.cql_annotations.r1.Annotation
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.VersionedIdentifier

@Suppress("TooManyFunctions")
class CompiledLibrary {
    var identifier: VersionedIdentifier? = null
    var library: Library? = null
    private val namespace: MutableMap<String, Element> = HashMap()
    val operatorMap: OperatorMap = OperatorMap()
    private val functionDefs: MutableMap<Operator, FunctionDef> = HashMap()
    private val conversions: MutableList<Conversion> = ArrayList()

    private fun checkNamespace(identifier: String) {
        val existingResolvedIdentifierContext = resolve(identifier)
        existingResolvedIdentifierContext.exactMatchElement?.let {
            throw IllegalArgumentException(
                "Identifier $identifier is already in use in this library."
            )
        }
    }

    fun add(using: UsingDef) {
        checkNamespace(using.localIdentifier!!)
        namespace[using.localIdentifier!!] = using
    }

    fun add(include: IncludeDef) {
        checkNamespace(include.localIdentifier!!)
        namespace[include.localIdentifier!!] = include
    }

    fun add(codesystem: CodeSystemDef) {
        checkNamespace(codesystem.name!!)
        namespace[codesystem.name!!] = codesystem
    }

    fun add(valueset: ValueSetDef) {
        checkNamespace(valueset.name!!)
        namespace[valueset.name!!] = valueset
    }

    fun add(code: CodeDef) {
        checkNamespace(code.name!!)
        namespace[code.name!!] = code
    }

    fun add(concept: ConceptDef) {
        checkNamespace(concept.name!!)
        namespace[concept.name!!] = concept
    }

    fun add(parameter: ParameterDef) {
        checkNamespace(parameter.name!!)
        namespace[parameter.name!!] = parameter
    }

    fun add(expression: ExpressionDef) {
        if (expression is FunctionDef) {
            // Register the operator signature
            add(expression, Operator(expression))
        } else {
            checkNamespace(expression.name!!)
            namespace[expression.name!!] = expression
        }
    }

    fun remove(expression: ExpressionDef) {
        require(expression !is FunctionDef) { "FunctionDef cannot be removed." }
        namespace.remove(expression.name)
    }

    private fun ensureLibrary(operator: Operator) {
        // The operator must be defined in the library in which it is registered
        // If the operator is not defined in a library, it is assumed to be defined in this library
        // If this library has no identifier, the operator must not have an identifier
        operator.libraryName = operator.libraryName ?: identifier?.id
        require(operator.libraryName == identifier?.id) {
            "Operator ${operator.name} cannot be registered in library ${identifier?.id ?: "<anonymous>"}" +
                "because it is defined in library ${operator.libraryName}."
        }
    }

    fun add(functionDef: FunctionDef, operator: Operator) {
        ensureLibrary(operator)
        operatorMap.addOperator(operator)
        functionDefs[operator] = functionDef
    }

    fun contains(functionDef: FunctionDef): Boolean {
        return contains(Operator(functionDef))
    }

    fun contains(operator: Operator): Boolean {
        return operatorMap.containsOperator(operator)
    }

    fun add(conversion: Conversion) {
        require(!conversion.isCast) {
            "Casting conversions cannot be registered as part of a library."
        }

        conversions.add(conversion)
    }

    fun resolve(identifier: String): ResolvedIdentifierContext {
        if (namespace.containsKey(identifier)) {
            return ResolvedIdentifierContext.exactMatch(identifier, namespace[identifier])
        }

        return namespace.entries
            .filter { it.key.equals(identifier, ignoreCase = true) }
            .map { it.value }
            .map { ResolvedIdentifierContext.caseInsensitiveMatch(identifier, it) }
            .firstOrNull() ?: ResolvedIdentifierContext.caseInsensitiveMatch(identifier, null)
    }

    fun resolveUsingRef(identifier: String): UsingDef? {
        return resolveIdentifier(identifier, UsingDef::class)
    }

    fun resolveIncludeRef(identifier: String): IncludeDef? {
        return resolveIdentifier(identifier, IncludeDef::class)
    }

    fun resolveIncludeAlias(identifier: VersionedIdentifier?): String? {
        return when {
            identifier != null && library?.includes?.def != null -> {
                val libraryPath = NamespaceManager.getPath(identifier.system, identifier.id!!)
                library!!.includes!!.def.firstOrNull { it.path == libraryPath }?.localIdentifier
            }
            else -> null
        }
    }

    fun resolveCodeSystemRef(identifier: String): CodeSystemDef? {
        return resolveIdentifier(identifier, CodeSystemDef::class)
    }

    fun resolveValueSetRef(identifier: String): ValueSetDef? {
        return resolveIdentifier(identifier, ValueSetDef::class)
    }

    fun resolveCodeRef(identifier: String): CodeDef? {
        return resolveIdentifier(identifier, CodeDef::class)
    }

    fun resolveConceptRef(identifier: String): ConceptDef? {
        return resolveIdentifier(identifier, ConceptDef::class)
    }

    fun resolveParameterRef(identifier: String): ParameterDef? {
        return resolveIdentifier(identifier, ParameterDef::class)
    }

    fun resolveExpressionRef(identifier: String): ExpressionDef? {
        return resolveIdentifier(identifier, ExpressionDef::class)
    }

    private fun <T : Element> resolveIdentifier(identifier: String, clazz: KClass<T>): T? {
        return resolve(identifier).resolveIdentifier(clazz)
    }

    fun resolveFunctionRef(identifier: String): Iterable<FunctionDef> {
        val results = ArrayList<FunctionDef>()
        for (ed in library!!.statements!!.def) {
            if (ed is FunctionDef && ed.name == identifier) {
                results.add(ed)
            }
        }

        return results
    }

    fun resolveFunctionRef(
        functionName: String,
        signature: List<DataType>?,
    ): Iterable<FunctionDef?> {
        return when (signature) {
            null -> resolveFunctionRef(functionName)
            else -> {
                val cc =
                    CallContext(
                        this.identifier!!.id,
                        functionName,
                        allowPromotionAndDemotion = false,
                        allowFluent = false,
                        mustResolve = false,
                        operandTypes = signature,
                    )
                val resolution = resolveCall(cc, ConversionMap())
                val results = ArrayList<FunctionDef?>()
                if (resolution != null) {
                    results.add(resolution.operator.functionDef)
                }
                results
            }
        }
    }

    fun resolveCall(callContext: CallContext, conversionMap: ConversionMap): OperatorResolution? {
        val resolution = operatorMap.resolveOperator(callContext, conversionMap)

        if (resolution != null) {
            // For backwards compatibility, a library can indicate that functions it exports are
            // allowed to be invoked
            // with fluent syntax. This is used in FHIRHelpers to allow fluent resolution, which is
            // implicit in 1.4.
            if (callContext.allowFluent && !resolution.operator.fluent) {
                resolution.allowFluent = getBooleanTag("allowFluent")
            }

            // The resolution needs to carry with it the full versioned identifier of the library so
            // that it can be
            // correctly
            // reflected via the alias for the library in the calling context.
            resolution.libraryIdentifier = this.identifier
        }

        return resolution
    }

    fun getConversions(): List<Conversion> {
        return conversions
    }

    private val annotation: Annotation?
        get() = library?.annotation?.firstOrNull { it is Annotation } as Annotation?

    private fun getTag(tagName: String): String? {
        return annotation?.t?.firstOrNull { it.name == tagName }?.value
    }

    private fun getBooleanTag(tagName: String): Boolean {
        return getTag(tagName)?.toBoolean() ?: false
    }
}
