package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.hl7.cql.ast.Expression
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType

/**
 * An implicit type conversion to be applied during emission. Each variant records enough
 * information for the emitter to produce the correct ELM wrapper.
 *
 * Implicit conversions are strictly type conversions — they change the type of an operand.
 * Structural transformations (boundary selectors, phrase expansion, operator rewrites) belong in
 * the lowering phase, not here.
 */
sealed interface ImplicitConversion {
    /** Wrap operand in a conversion function (ToDecimal, ToLong, ToString, etc.) */
    data class OperatorConversion(val operatorName: String) : ImplicitConversion

    /** Wrap operand in implicit As(targetType) — used for casts, null-As, and choice wrapping. */
    data class ImplicitCast(val targetType: DataType) : ImplicitConversion

    /**
     * Wrap list in Query(source=list, return=ConversionOp(AliasRef(X))) for element-level
     * conversion. When [innerLibraryName] is non-null, the conversion is a library function (e.g.,
     * FHIRHelpers.ToDateTime) rather than a system operator.
     */
    data class ListConversion(
        val innerOperatorName: String,
        val innerLibraryName: String? = null,
        val innerResultType: DataType? = null,
    ) : ImplicitConversion

    /** Wrap list in Query(source=list, return=As(AliasRef(X), targetType)) for list demotion. */
    data class ListDemotion(val targetElementType: DataType, val resultType: DataType) : ImplicitConversion

    /**
     * Wrap interval bounds with inner operator conversion. When [innerLibraryName] is non-null, the
     * conversion is a library function rather than a system operator. Only applies to interval
     * literals; for non-literal intervals, the implicit conversion is a no-op.
     */
    data class IntervalConversion(
        val innerOperatorName: String,
        val innerLibraryName: String? = null,
        val innerResultType: DataType? = null,
    ) : ImplicitConversion

    /**
     * Wrap operand in a FunctionRef to a library conversion function (e.g.,
     * FHIRHelpers.ToDateTime). Unlike [OperatorConversion] which emits system unary operators
     * (ToDecimal etc.), this emits a qualified FunctionRef node with libraryName.
     */
    data class LibraryConversion(
        val libraryName: String,
        val functionName: String,
        val resultType: DataType,
    ) : ImplicitConversion
}

/**
 * Identifies which operand slot of a parent expression an implicit conversion applies to. Keyed by
 * parent expression identity + slot, so AST immutability is preserved.
 */
sealed interface ConversionSlot {
    data object Left : ConversionSlot

    data object Right : ConversionSlot

    data object Operand : ConversionSlot

    data class Argument(val index: Int) : ConversionSlot

    /** List literal element at the given index. */
    data class ListElement(val index: Int) : ConversionSlot

    /** Interval literal low bound. */
    data object IntervalLow : ConversionSlot

    /** Interval literal high bound. */
    data object IntervalHigh : ConversionSlot

    /** If-expression then branch. */
    data object ThenBranch : ConversionSlot

    /** If-expression or case-expression else branch. */
    data object ElseBranch : ConversionSlot

    /** Case-expression branch result at the given index. */
    data class CaseBranch(val index: Int) : ConversionSlot

    /** Case-expression comparand condition at the given index. */
    data class CaseCondition(val index: Int) : ConversionSlot

    /** Property access result — wraps the emitted Property node with a conversion. */
    data object PropertyResult : ConversionSlot
}

/**
 * Side table that records implicit type conversions keyed by parent expression identity and slot.
 * This keeps the AST immutable — conversions are applied during emission by reading this table.
 *
 * **Not thread-safe.** Each instance is owned by a single analysis pass.
 */
class ConversionTable {
    private val entries = IdentityHashMap<Expression, MutableMap<ConversionSlot, MutableList<ImplicitConversion>>>()

    /** Total conversions inserted across all calls to [add]. */
    var conversionsInserted: Int = 0
        private set

    /** Record a conversion at the given parent/slot. */
    fun add(parent: Expression, slot: ConversionSlot, conversion: ImplicitConversion) {
        val slotMap = entries.getOrPut(parent) { mutableMapOf() }
        val list = slotMap.getOrPut(slot) { mutableListOf() }
        list.add(conversion)
        conversionsInserted++
    }

    /** Get all conversions recorded at the given parent/slot, or empty list. */
    fun get(parent: Expression, slot: ConversionSlot): List<ImplicitConversion> {
        val slotMap = entries[parent] ?: return emptyList()
        return slotMap[slot] ?: emptyList()
    }

    /** Record a conversion if not already present at the given parent/slot. */
    fun addIfAbsent(parent: Expression, slot: ConversionSlot, conversion: ImplicitConversion) {
        if (get(parent, slot).contains(conversion)) return
        add(parent, slot, conversion)
    }

    /**
     * Transfer all conversions from [source] to [target]. Used by the lowering phase when an
     * expression is rewritten — conversions recorded against the original expression identity need
     * to follow to the new expression.
     */
    fun transfer(source: Expression, target: Expression) {
        val slotMap = entries[source] ?: return
        for ((slot, conversions) in slotMap) {
            for (s in conversions) {
                val targetSlotMap = entries.getOrPut(target) { mutableMapOf() }
                val targetList = targetSlotMap.getOrPut(slot) { mutableListOf() }
                targetList.add(s)
            }
        }
    }

    /**
     * Compute the post-conversion [DataType] given a source type and the conversions at this slot.
     * Walks the conversion chain: e.g., Integer + OperatorConversion("ToDecimal") → Decimal.
     * Returns null if the source type is null or a conversion can't be resolved.
     */
    @Suppress("ReturnCount")
    fun effectiveType(
        parent: Expression,
        slot: ConversionSlot,
        sourceType: DataType?,
        operatorRegistry: OperatorRegistry,
    ): DataType? {
        if (sourceType == null) return null
        val conversions = get(parent, slot)
        if (conversions.isEmpty()) return sourceType
        var currentType = sourceType
        for (s in conversions) {
            currentType =
                when (s) {
                    is ImplicitConversion.OperatorConversion -> {
                        val resolution =
                            operatorRegistry.resolve(s.operatorName, listOf(currentType!!))
                        resolution?.operator?.resultType ?: return null
                    }
                    is ImplicitConversion.ImplicitCast -> s.targetType
                    is ImplicitConversion.ListConversion -> {
                        val elemType = (currentType as? ListType)?.elementType ?: return null
                        val resultElemType =
                            if (s.innerResultType != null) {
                                s.innerResultType
                            } else {
                                val res =
                                    operatorRegistry.resolve(s.innerOperatorName, listOf(elemType))
                                res?.operator?.resultType ?: return null
                            }
                        ListType(resultElemType)
                    }
                    is ImplicitConversion.ListDemotion -> s.resultType
                    is ImplicitConversion.IntervalConversion -> {
                        val pointType = (currentType as? IntervalType)?.pointType ?: return null
                        val resultPointType =
                            if (s.innerResultType != null) {
                                s.innerResultType
                            } else {
                                val res =
                                    operatorRegistry.resolve(s.innerOperatorName, listOf(pointType))
                                res?.operator?.resultType ?: return null
                            }
                        IntervalType(resultPointType)
                    }
                    is ImplicitConversion.LibraryConversion -> s.resultType
                }
        }
        return currentType
    }
}

/**
 * Convert a [Conversion] from an [OperatorResolution] to a list of [ImplicitConversion]s, or empty
 * if the conversion kind isn't handled by the side table. Returns multiple implicit conversions
 * when a conversion decomposes into sequential steps (e.g., single-branch choice: ImplicitCast +
 * inner).
 */
internal fun conversionToImplicits(
    conversion: Conversion,
    registry: OperatorRegistry,
): List<ImplicitConversion> {
    val operator = conversion.operator
    if (operator != null) {
        val libraryName = operator.libraryName
        return if (libraryName != null && libraryName != "System") {
            listOf(ImplicitConversion.LibraryConversion(libraryName, operator.name, conversion.toType))
        } else {
            listOf(ImplicitConversion.OperatorConversion(operator.name))
        }
    }
    // Choice narrowing: isCast with an inner conversion from a ChoiceType.
    if (conversion.isCast && conversion.conversion != null && conversion.fromType is ChoiceType) {
        val innerConversions = conversionToImplicits(conversion.conversion, registry)
        if (conversion.hasAlternativeConversions()) {
            // Multi-branch: handled by the Lowering, not implicit conversions.
            return emptyList()
        } else {
            // Single branch: decompose into As(fromType) + inner conversion chain.
            // No Case needed — matches old compiler behavior.
            return listOf(ImplicitConversion.ImplicitCast(conversion.conversion.fromType)) + innerConversions
        }
    }
    if (conversion.isCast) return listOf(ImplicitConversion.ImplicitCast(conversion.toType))
    if (
        conversion.isListConversion &&
            conversion.conversion != null &&
            conversion.conversion.operator != null
    ) {
        val inner = conversion.conversion.operator
        val lib = inner.libraryName?.takeIf { it != "System" }
        val resultType = if (lib != null) conversion.conversion.toType else null
        return listOf(ImplicitConversion.ListConversion(inner.name, lib, resultType))
    }
    if (
        conversion.isIntervalConversion &&
            conversion.conversion != null &&
            conversion.conversion.operator != null
    ) {
        val inner = conversion.conversion.operator
        val lib = inner.libraryName?.takeIf { it != "System" }
        val resultType = if (lib != null) conversion.conversion.toType else null
        return listOf(ImplicitConversion.IntervalConversion(inner.name, lib, resultType))
    }
    return emptyList()
}

/**
 * Record implicit conversions from an [OperatorResolution]'s conversions for each slot. Called by
 * TypeResolver at each setOperatorResolution site and by ConversionPlanner when re-deriving
 * resolution conversions is needed.
 */
internal fun recordResolutionConversions(
    conversionTable: ConversionTable,
    parent: Expression,
    resolution: OperatorResolution,
    slots: List<ConversionSlot>,
    registry: OperatorRegistry,
) {
    if (!resolution.hasConversions()) return
    resolution.conversions.forEachIndexed { index, conversion ->
        if (conversion != null && index < slots.size) {
            val conversions = conversionToImplicits(conversion, registry)
            for (c in conversions) {
                conversionTable.addIfAbsent(parent, slots[index], c)
            }
        }
    }
}
