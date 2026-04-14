package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.model.PropertyResolution
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.shared.QName
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.ModelContext
import org.hl7.cql.model.NamedType
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier

/**
 * Owns type compatibility checks, conversion-aware type verification, and utilities for projecting
 * [DataType] values into ELM [QName]/[TypeSpecifier] form.
 *
 * Extracted from [Cql2ElmContext] as part of the ongoing split of builder responsibilities. Holds a
 * back-reference to [Cql2ElmContext] for access to the model-aware type resolver
 * (`resolveTypeName`, `resolveLabel`), the property resolver, the conversion-map (via
 * `findConversion`/`convertExpression`), and the `TypeBuilder`.
 */
@Suppress("TooManyFunctions", "MaxLineLength")
class TypeResolver(private val lb: Cql2ElmContext, private val of: IdObjectFactory) {
    fun dataTypeToQName(type: DataType?): QName = lb.typeBuilder.dataTypeToQName(type)

    fun dataTypeToTypeSpecifier(type: DataType?): TypeSpecifier =
        lb.typeBuilder.dataTypeToTypeSpecifier(type)

    fun dataTypesToTypeSpecifiers(types: List<DataType>): List<TypeSpecifier> =
        lb.typeBuilder.dataTypesToTypeSpecifiers(types)

    /**
     * Walk a dotted path expression against [sourceType], returning the terminal type. Uses the
     * configured property resolver at each step.
     */
    fun resolvePath(sourceType: DataType?, path: String): DataType? {
        var current: DataType? = sourceType
        val identifiers = path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (id in identifiers) {
            val resolution: PropertyResolution? = lb.resolveProperty(current, id)
            current = resolution!!.type
        }
        return current
    }

    /**
     * Verify [actualType] is assignable to [expectedType], allowing implicit conversion. Falls back
     * to [DataTypes.verifyType] (which throws) when no path is found.
     */
    fun verifyType(actualType: DataType, expectedType: DataType) {
        if (expectedType.isSuperTypeOf(actualType) || actualType.isCompatibleWith(expectedType)) {
            return
        }
        val conversion =
            lb.findConversion(
                actualType,
                expectedType,
                implicit = true,
                allowPromotionAndDemotion = false,
            )
        if (conversion != null) return
        DataTypes.verifyType(actualType, expectedType)
    }

    /**
     * Return the most specific type assignable from both [first] and [second], taking implicit
     * conversions into account but refusing to reduce [ChoiceType] alternatives.
     */
    @Suppress("ReturnCount")
    fun findCompatibleType(first: DataType?, second: DataType?): DataType? {
        if (first == null || second == null) return null
        if (first == DataType.ANY) return second
        if (second == DataType.ANY) return first
        if (first.isSuperTypeOf(second) || second.isCompatibleWith(first)) return first
        if (second.isSuperTypeOf(first) || first.isCompatibleWith(second)) return second

        // Either side is a choice → don't allow conversions (would collapse choice alternatives).
        if (first is ChoiceType || second is ChoiceType) return null

        var conversion =
            lb.findConversion(second, first, implicit = true, allowPromotionAndDemotion = false)
        if (conversion != null) return first
        conversion =
            lb.findConversion(first, second, implicit = true, allowPromotionAndDemotion = false)
        if (conversion != null) return second
        return null
    }

    @Suppress("ReturnCount")
    fun ensureCompatibleTypes(first: DataType?, second: DataType): DataType? {
        val compatibleType = findCompatibleType(first, second)
        if (compatibleType != null) return compatibleType
        if (first != null && !second.isSubTypeOf(first)) return ChoiceType(first, second)
        // Choice construction above guarantees this branch is unreachable.
        DataTypes.verifyType(second, first)
        return first
    }

    fun ensureCompatible(expression: Expression?, targetType: DataType?): Expression {
        if (targetType == null) return of.createNull()
        return if (!targetType.isSuperTypeOf(expression!!.resultType!!)) {
            lb.convertExpression(expression, targetType, true)
        } else expression
    }

    fun enforceCompatible(expression: Expression?, targetType: DataType?): Expression {
        if (targetType == null) return of.createNull()
        return if (!targetType.isSuperTypeOf(expression!!.resultType!!)) {
            lb.convertExpression(expression, targetType, false)
        } else expression
    }

    // ========================================================================
    // Model-aware type resolution. Queries the loaded-model registry on
    // Cql2ElmContext for labels, context names, and type names. The result is
    // a [DataType] (or [ModelContext]) from the model's schema.
    // ========================================================================

    @Suppress("NestedBlockDepth")
    fun resolveLabel(modelName: String?, label: String): ClassType? {
        var result: ClassType? = null
        if (modelName == null || modelName == "") {
            for (model: Model? in lb.models.values) {
                val modelResult: ClassType? = model!!.resolveLabel(label)
                if (modelResult != null) {
                    require(result == null) {
                        "Label $label is ambiguous between ${(result as ClassType).label} and ${modelResult.label}."
                    }
                    result = modelResult
                }
            }
        } else {
            result = lb.getModel(modelName).resolveLabel(label)
        }
        return result
    }

    @Suppress("NestedBlockDepth")
    fun resolveContextName(modelName: String?, contextName: String): ModelContext? {
        var result: ModelContext? = null
        if (modelName == null || modelName == "") {
            if (lb.defaultModel != null) {
                val modelResult: ModelContext? = lb.defaultModel!!.resolveContextName(contextName)
                if (modelResult != null) return modelResult
            }
            for (model: Model? in lb.models.values) {
                val modelResult: ModelContext? = model!!.resolveContextName(contextName)
                if (modelResult != null) {
                    require(result == null) {
                        "Context name $contextName is ambiguous between ${(result as ModelContext).name} and ${modelResult.name}."
                    }
                    result = modelResult
                }
            }
        } else {
            result = lb.getModel(modelName).resolveContextName(contextName)
        }
        return result
    }

    fun resolveTypeName(typeName: String): DataType? = resolveTypeName(null, typeName)

    @Suppress("NestedBlockDepth")
    fun resolveTypeName(modelName: String?, typeName: String): DataType? {
        var result: DataType? = resolveLabel(modelName, typeName)
        if (result == null) {
            if (modelName == null || modelName == "") {
                if (lb.defaultModel != null) {
                    val modelResult: DataType? = lb.defaultModel!!.resolveTypeName(typeName)
                    if (modelResult != null) return modelResult
                }
                for (model: Model? in lb.models.values) {
                    val modelResult: DataType? = model!!.resolveTypeName(typeName)
                    if (modelResult != null) {
                        require(result == null) {
                            "Type name $typeName is ambiguous between ${(result as NamedType).name} and ${(modelResult as NamedType).name}."
                        }
                        result = modelResult
                    }
                }
            } else {
                result = lb.getModel(modelName).resolveTypeName(typeName)
            }
        }

        // Types introduced in CQL 1.5: Long, Vocabulary, ValueSet, CodeSystem. Permit these
        // under a lower compatibility level only when resolving inside FHIRHelpers (so the
        // library will still compile even if the operator can't be invoked).
        if (result != null && result is NamedType) {
            when ((result as NamedType).name) {
                "System.Long",
                "System.Vocabulary",
                "System.CodeSystem",
                "System.ValueSet" ->
                    require(lb.isCompatibleWith("1.5") || isFHIRHelpers(lb.compiledLibrary)) {
                        "The type ${(result as NamedType).name} was introduced in CQL 1.5 and cannot be referenced at compatibility level ${lb.compatibilityLevel}"
                    }
            }
        }
        return result
    }

    private fun isFHIRHelpers(library: CompiledLibrary?): Boolean =
        library != null &&
            library.identifier != null &&
            library.identifier!!.id != null &&
            library.identifier!!.id == "FHIRHelpers"

    fun resolveTypeSpecifier(typeSpecifier: String?): DataType? {
        requireNotNull(typeSpecifier) { "typeSpecifier is null" }
        return when {
            typeSpecifier.lowercase().startsWith("interval<") -> {
                val pointType =
                    resolveTypeSpecifier(
                        typeSpecifier.substring(
                            typeSpecifier.indexOf('<') + 1,
                            typeSpecifier.lastIndexOf('>'),
                        )
                    )
                IntervalType(pointType!!)
            }
            else ->
                if (typeSpecifier.lowercase().startsWith("list<")) {
                    val elementType =
                        resolveTypeName(
                            typeSpecifier.substring(
                                typeSpecifier.indexOf('<') + 1,
                                typeSpecifier.lastIndexOf('>'),
                            )
                        )
                    ListType(elementType!!)
                } else if (typeSpecifier.indexOf(".") >= 0) {
                    val modelName = typeSpecifier.substring(0, typeSpecifier.indexOf("."))
                    val typeName = typeSpecifier.substring(typeSpecifier.indexOf(".") + 1)
                    resolveTypeName(modelName, typeName)
                } else {
                    resolveTypeName(typeSpecifier)
                }
        }
    }

    // ========================================================================
    // NamedTypeSpecifier result cache. Used by the visitor to memoize the
    // resolution of named-type-specifier literals (e.g. "FHIR.Observation")
    // across repeated references so that later visits reuse the same result.
    // ========================================================================

    private val nameTypeSpecifiers:
        MutableMap<String, ResultWithPossibleError<NamedTypeSpecifier?>> =
        HashMap()

    fun getNamedTypeSpecifierResult(
        namedTypeSpecifierIdentifier: String
    ): ResultWithPossibleError<NamedTypeSpecifier?>? =
        nameTypeSpecifiers[namedTypeSpecifierIdentifier]

    fun addNamedTypeSpecifierResult(
        namedTypeSpecifierIdentifier: String,
        namedTypeSpecifierResult: ResultWithPossibleError<NamedTypeSpecifier?>,
    ) {
        if (!nameTypeSpecifiers.containsKey(namedTypeSpecifierIdentifier)) {
            nameTypeSpecifiers[namedTypeSpecifierIdentifier] = namedTypeSpecifierResult
        }
    }
}
