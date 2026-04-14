package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.PropertyResolution
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.shared.QName
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.TypeSpecifier

/**
 * Owns type compatibility checks, conversion-aware type verification, and utilities for projecting
 * [DataType] values into ELM [QName]/[TypeSpecifier] form.
 *
 * Extracted from [LibraryBuilder] as part of the ongoing split of builder responsibilities. Holds a
 * back-reference to [LibraryBuilder] for access to the model-aware type resolver
 * (`resolveTypeName`, `resolveLabel`), the property resolver, the conversion-map (via
 * `findConversion`/`convertExpression`), and the `TypeBuilder`.
 */
class TypeResolver(private val lb: LibraryBuilder, private val of: IdObjectFactory) {
    fun dataTypeToQName(type: DataType?): QName = lb.typeBuilderInternal.dataTypeToQName(type)

    fun dataTypeToTypeSpecifier(type: DataType?): TypeSpecifier =
        lb.typeBuilderInternal.dataTypeToTypeSpecifier(type)

    fun dataTypesToTypeSpecifiers(types: List<DataType>): List<TypeSpecifier> =
        lb.typeBuilderInternal.dataTypesToTypeSpecifiers(types)

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
}
