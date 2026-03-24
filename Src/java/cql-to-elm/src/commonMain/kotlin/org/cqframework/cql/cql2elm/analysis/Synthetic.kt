package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.model.Conversion
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.hl7.cql.ast.Expression
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType

/**
 * An implicit type conversion to be applied during emission. Each variant records enough
 * information for the emitter to produce the correct ELM wrapper.
 *
 * Synthetics are strictly type conversions — they change the type of an operand. Structural
 * transformations (boundary selectors, phrase expansion, operator rewrites) belong in the lowering
 * phase, not here.
 */
sealed interface Synthetic {
    /** Wrap operand in a conversion function (ToDecimal, ToLong, ToString, etc.) */
    data class OperatorConversion(val operatorName: String) : Synthetic

    /** Wrap operand in implicit As(targetType) — used for casts, null-As, and choice wrapping. */
    data class ImplicitCast(val targetType: DataType) : Synthetic

    /**
     * Wrap list in Query(source=list, return=ConversionOp(AliasRef(X))) for element-level
     * conversion. When [innerLibraryName] is non-null, the conversion is a library function (e.g.,
     * FHIRHelpers.ToDateTime) rather than a system operator.
     */
    data class ListConversion(
        val innerOperatorName: String,
        val innerLibraryName: String? = null,
        val innerResultType: DataType? = null,
    ) : Synthetic

    /** Wrap list in Query(source=list, return=As(AliasRef(X), targetType)) for list demotion. */
    data class ListDemotion(val targetElementType: DataType, val resultType: DataType) : Synthetic

    /**
     * Wrap interval bounds with inner operator conversion. When [innerLibraryName] is non-null, the
     * conversion is a library function rather than a system operator. Only applies to interval
     * literals; for non-literal intervals, the synthetic is a no-op.
     */
    data class IntervalConversion(
        val innerOperatorName: String,
        val innerLibraryName: String? = null,
        val innerResultType: DataType? = null,
    ) : Synthetic

    /**
     * Wrap operand in a FunctionRef to a library conversion function (e.g.,
     * FHIRHelpers.ToDateTime). Unlike [OperatorConversion] which emits system unary operators
     * (ToDecimal etc.), this emits a qualified FunctionRef node with libraryName.
     */
    data class LibraryConversion(
        val libraryName: String,
        val functionName: String,
        val resultType: DataType,
    ) : Synthetic
}

/**
 * Identifies which operand slot of a parent expression a synthetic applies to. Keyed by parent
 * expression identity + slot, so AST immutability is preserved.
 */
sealed interface Slot {
    data object Left : Slot

    data object Right : Slot

    data object Operand : Slot

    data class Argument(val index: Int) : Slot

    /** List literal element at the given index. */
    data class ListElement(val index: Int) : Slot

    /** Interval literal low bound. */
    data object IntervalLow : Slot

    /** Interval literal high bound. */
    data object IntervalHigh : Slot

    /** If-expression then branch. */
    data object ThenBranch : Slot

    /** If-expression or case-expression else branch. */
    data object ElseBranch : Slot

    /** Case-expression branch result at the given index. */
    data class CaseBranch(val index: Int) : Slot

    /** Case-expression comparand condition at the given index. */
    data class CaseCondition(val index: Int) : Slot

    /** Property access result — wraps the emitted Property node with a conversion. */
    data object PropertyResult : Slot
}

/**
 * Side table that records implicit type conversions keyed by parent expression identity and slot.
 * This keeps the AST immutable — conversions are applied during emission by reading this table.
 *
 * **Not thread-safe.** Each instance is owned by a single analysis pass.
 */
class SyntheticTable {
    private val entries = IdentityHashMap<Expression, MutableMap<Slot, MutableList<Synthetic>>>()

    /** Total synthetics inserted across all calls to [add]. */
    var syntheticsInserted: Int = 0
        private set

    /** Record a synthetic at the given parent/slot. */
    fun add(parent: Expression, slot: Slot, synthetic: Synthetic) {
        val slotMap = entries.getOrPut(parent) { mutableMapOf() }
        val list = slotMap.getOrPut(slot) { mutableListOf() }
        list.add(synthetic)
        syntheticsInserted++
    }

    /** Get all synthetics recorded at the given parent/slot, or empty list. */
    fun get(parent: Expression, slot: Slot): List<Synthetic> {
        val slotMap = entries[parent] ?: return emptyList()
        return slotMap[slot] ?: emptyList()
    }

    /** Record a synthetic if not already present at the given parent/slot. */
    fun addIfAbsent(parent: Expression, slot: Slot, synthetic: Synthetic) {
        if (get(parent, slot).contains(synthetic)) return
        add(parent, slot, synthetic)
    }

    /**
     * Transfer all synthetics from [source] to [target]. Used by the lowering phase when an
     * expression is rewritten — synthetics recorded against the original expression identity need
     * to follow to the new expression.
     */
    fun transfer(source: Expression, target: Expression) {
        val slotMap = entries[source] ?: return
        for ((slot, synthetics) in slotMap) {
            for (s in synthetics) {
                val targetSlotMap = entries.getOrPut(target) { mutableMapOf() }
                val targetList = targetSlotMap.getOrPut(slot) { mutableListOf() }
                targetList.add(s)
            }
        }
    }

    /**
     * Compute the post-conversion [DataType] given a source type and the synthetics at this slot.
     * Walks the synthetic chain: e.g., Integer + OperatorConversion("ToDecimal") → Decimal. Returns
     * null if the source type is null or a conversion can't be resolved.
     */
    @Suppress("ReturnCount")
    fun effectiveType(
        parent: Expression,
        slot: Slot,
        sourceType: DataType?,
        operatorRegistry: OperatorRegistry,
    ): DataType? {
        if (sourceType == null) return null
        val synthetics = get(parent, slot)
        if (synthetics.isEmpty()) return sourceType
        var currentType = sourceType
        for (s in synthetics) {
            currentType =
                when (s) {
                    is Synthetic.OperatorConversion -> {
                        val resolution =
                            operatorRegistry.resolve(s.operatorName, listOf(currentType!!))
                        resolution?.operator?.resultType ?: return null
                    }
                    is Synthetic.ImplicitCast -> s.targetType
                    is Synthetic.ListConversion -> {
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
                    is Synthetic.ListDemotion -> s.resultType
                    is Synthetic.IntervalConversion -> {
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
                    is Synthetic.LibraryConversion -> s.resultType
                }
        }
        return currentType
    }
}

/**
 * Convert a [Conversion] from an [OperatorResolution] to a [Synthetic], or null if the conversion
 * kind isn't handled by the side table.
 */
internal fun conversionToSynthetic(conversion: Conversion, registry: OperatorRegistry): Synthetic? {
    val operator = conversion.operator
    if (operator != null) {
        val libraryName = operator.libraryName
        return if (libraryName != null && libraryName != "System") {
            Synthetic.LibraryConversion(libraryName, operator.name, conversion.toType)
        } else {
            Synthetic.OperatorConversion(operator.name)
        }
    }
    if (conversion.isCast) return Synthetic.ImplicitCast(conversion.toType)
    if (
        conversion.isListConversion &&
            conversion.conversion != null &&
            conversion.conversion.operator != null
    ) {
        val inner = conversion.conversion.operator
        val lib = inner.libraryName?.takeIf { it != "System" }
        val resultType = if (lib != null) conversion.conversion.toType else null
        return Synthetic.ListConversion(inner.name, lib, resultType)
    }
    if (
        conversion.isIntervalConversion &&
            conversion.conversion != null &&
            conversion.conversion.operator != null
    ) {
        val inner = conversion.conversion.operator
        val lib = inner.libraryName?.takeIf { it != "System" }
        val resultType = if (lib != null) conversion.conversion.toType else null
        return Synthetic.IntervalConversion(inner.name, lib, resultType)
    }
    return null
}

/**
 * Record synthetics from an [OperatorResolution]'s conversions for each slot. Called by
 * TypeResolver at each setOperatorResolution site and by TypeUnifier when re-deriving resolution
 * conversions is needed.
 */
internal fun recordResolutionSynthetics(
    syntheticTable: SyntheticTable,
    parent: Expression,
    resolution: OperatorResolution,
    slots: List<Slot>,
    registry: OperatorRegistry,
) {
    if (!resolution.hasConversions()) return
    resolution.conversions.forEachIndexed { index, conversion ->
        if (conversion != null && index < slots.size) {
            val synthetic = conversionToSynthetic(conversion, registry) ?: return@forEachIndexed
            syntheticTable.addIfAbsent(parent, slots[index], synthetic)
        }
    }
}
