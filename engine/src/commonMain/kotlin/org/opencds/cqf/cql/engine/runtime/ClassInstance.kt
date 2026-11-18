package org.opencds.cqf.cql.engine.runtime

import kotlin.js.JsName
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

/**
 * Represents an instance of a non-system-defined class (instance of a named structured type defined
 * outside the System model), e.g. a FHIR.Patient.
 *
 * Instances are immutable. [elements] is either an arbitrary map, or, for instances created with a
 * [ClassLayout], a read-only view of an array of values with one slot per element of the layout; a
 * slot without a value is a key that maps to null. The layout is shared by every instance of the
 * type, which avoids a map entry per element and matters when a patient has many large resources.
 * An absent element reads as null.
 */
@JsOnlyExport
class ClassInstance(override val type: QName, override val elements: Map<kotlin.String, Value?>) :
    StructuredValue, NamedTypeValue {

    /**
     * Creates an instance whose element values are stored in [slots], in the order of [layout]. The
     * instance takes ownership of the array, which must not be modified afterwards.
     */
    @JsName("withLayout")
    constructor(
        type: QName,
        layout: ClassLayout,
        slots: Array<Value?>,
    ) : this(type, SlotElements(layout, slots))

    private var hash = 0

    override fun hashCode(): Int {
        var result = hash
        if (result == 0) {
            result = 31 * type.hashCode() + elements.hashCode()
            hash = result
        }
        return result
    }

    override fun equals(other: Any?): kotlin.Boolean =
        this === other ||
            (other is ClassInstance && type == other.type && elements == other.elements)

    override fun toString(): kotlin.String = toStructuredValueString()
}

/**
 * An immutable map view of the element values of an instance of a [ClassLayout]. Every element of
 * the layout is a key, in layout order; a slot without a value is a key that maps to null, exactly
 * as in a map-backed instance.
 */
private class SlotElements(private val layout: ClassLayout, private val slots: Array<Value?>) :
    AbstractMap<kotlin.String, Value?>() {

    init {
        require(slots.size == layout.size) {
            "Expected ${layout.size} element values, got ${slots.size}."
        }
    }

    override val size: Int
        get() = slots.size

    override fun containsKey(key: kotlin.String): kotlin.Boolean = layout.indexOf(key) >= 0

    override fun get(key: kotlin.String): Value? {
        val index = layout.indexOf(key)
        return if (index >= 0) slots[index] else null
    }

    override val entries: Set<Map.Entry<kotlin.String, Value?>>
        get() =
            object : AbstractSet<Map.Entry<kotlin.String, Value?>>() {
                override val size: Int
                    get() = slots.size

                override fun iterator(): Iterator<Map.Entry<kotlin.String, Value?>> = iterator {
                    for (i in slots.indices) {
                        yield(SlotEntry(layout.elementName(i), slots[i]))
                    }
                }
            }

    // Instances of the same layout compare slot by slot, without creating entries
    override fun equals(other: Any?): kotlin.Boolean =
        if (other is SlotElements && other.layout === layout) {
            slots.contentEquals(other.slots)
        } else {
            super.equals(other)
        }

    override fun hashCode(): Int {
        var result = 0
        for (i in slots.indices) {
            result += layout.elementName(i).hashCode() xor (slots[i]?.hashCode() ?: 0)
        }
        return result
    }
}

private class SlotEntry(override val key: kotlin.String, override val value: Value?) :
    Map.Entry<kotlin.String, Value?> {
    override fun equals(other: Any?) =
        other is Map.Entry<*, *> && other.key == key && other.value == value

    override fun hashCode() = key.hashCode() xor (value?.hashCode() ?: 0)

    override fun toString() = "$key=$value"
}
