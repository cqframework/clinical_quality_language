package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.hl7.cql.ast.Expression
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType

/**
 * A synthetic transformation to be applied during emission, without mutating the AST. Each variant
 * records enough information for the emitter to produce the correct ELM wrapper.
 */
sealed interface Synthetic {
    /** Wrap operand in a conversion function (ToDecimal, ToLong, ToString, etc.) */
    data class OperatorConversion(val operatorName: String) : Synthetic

    /** Wrap operand in implicit As(targetType) — used for casts, null-As, and choice wrapping. */
    data class ImplicitCast(val targetType: DataType) : Synthetic

    /** Wrap operand in Coalesce(operand, '') for CONCAT null-coalescing. */
    data object CoalesceWrap : Synthetic

    /** Wrap list in Query(source=list, return=ToXxx(AliasRef(X))) for element-level conversion. */
    data class ListConversion(val innerOperatorName: String) : Synthetic

    /** Wrap list in Query(source=list, return=As(AliasRef(X), targetType)) for list demotion. */
    data class ListDemotion(val targetElementType: DataType, val resultType: DataType) : Synthetic

    /**
     * Wrap interval bounds with inner operator conversion. Only applies to interval literals; for
     * non-literal intervals, the synthetic is a no-op (same behavior as the legacy
     * ConversionInserter).
     */
    data class IntervalConversion(val innerOperatorName: String) : Synthetic

    /**
     * Replace the expression's operator at emission time. Used on [Slot.Self] to rewrite a binary
     * operator (e.g., Add → Concatenate when operand types resolve to String).
     */
    data class OperatorRewrite(val targetOperator: String) : Synthetic

    /**
     * Promote a point to a degenerate interval: `If(IsNull(p), Null, Interval[p, p])`. Used when a
     * before/after/within phrase has a point operand against an interval operand.
     */
    data object PointToInterval : Synthetic

    /** Extract interval bound: wraps in `Start()` (true) or `End()` (false). */
    data class IntervalBound(val start: Boolean) : Synthetic
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

    data object Self : Slot

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
}

/**
 * Side table that records synthetic transformations keyed by parent expression identity and slot.
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
                    is Synthetic.CoalesceWrap -> currentType // no type change
                    is Synthetic.ListConversion -> {
                        val elemType = (currentType as? ListType)?.elementType ?: return null
                        val resolution =
                            operatorRegistry.resolve(s.innerOperatorName, listOf(elemType))
                        ListType(resolution?.operator?.resultType ?: return null)
                    }
                    is Synthetic.ListDemotion -> s.resultType
                    is Synthetic.IntervalConversion -> {
                        val pointType = (currentType as? IntervalType)?.pointType ?: return null
                        val resolution =
                            operatorRegistry.resolve(s.innerOperatorName, listOf(pointType))
                        IntervalType(resolution?.operator?.resultType ?: return null)
                    }
                    is Synthetic.OperatorRewrite -> currentType // no type change
                    is Synthetic.PointToInterval -> IntervalType(currentType!!)
                    is Synthetic.IntervalBound ->
                        (currentType as? IntervalType)?.pointType ?: currentType
                }
        }
        return currentType
    }
}
