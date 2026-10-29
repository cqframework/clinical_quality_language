package org.opencds.cqf.cql.engine.runtime

import kotlin.js.JsName
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

/**
 * Represents an instance of a non-system-defined class (instance of a named structured type defined
 * outside the System model), e.g. a FHIR.Patient.
 *
 * The elements are stored either in a map, or in an array of values with one slot per element of a
 * [ClassLayout] shared by every instance of the type. The array form avoids a map entry per
 * element, which matters when a patient has many large resources. Either way, [elements] is a
 * mutable map of the element values, and an absent element reads as null.
 */
@JsOnlyExport
class ClassInstance(override val type: QName, elements: MutableMap<kotlin.String, Value?>) :
    StructuredValue(), NamedTypeValue {
    // Set only by the layout constructor, which needs the primary constructor to be the map one
    private var layout: ClassLayout? = null
    private var slots: Array<Value?>? = null
    // The elements of a map-backed instance, or the elements of an array-backed instance that its
    // layout doesn't name (e.g. ones set by an Instance expression), created when first needed
    private var map: MutableMap<kotlin.String, Value?>? = elements

    /**
     * Creates an instance whose element values are stored in [slots], in the order of [layout]. The
     * instance takes ownership of the array.
     */
    @JsName("withLayout")
    constructor(type: QName, layout: ClassLayout, slots: Array<Value?>) : this(type, NO_ELEMENTS) {
        require(slots.size == layout.size) {
            "Expected ${layout.size} element values for $type, got ${slots.size}."
        }
        this.layout = layout
        this.slots = slots
        this.map = null
    }

    override val elements: MutableMap<kotlin.String, Value?>
        get() {
            val slots = slots ?: return map!!
            return SlotElements(layout!!, slots)
        }

    override fun get(elementName: kotlin.String): Value? {
        val index = layout?.indexOf(elementName) ?: -1
        return if (index >= 0) slots!![index] else map?.get(elementName)
    }

    override fun has(elementName: kotlin.String): kotlin.Boolean {
        val index = layout?.indexOf(elementName) ?: -1
        return if (index >= 0) slots!![index] != null else map?.containsKey(elementName) == true
    }

    // An absent element and a null-valued element are the same thing for a class instance, so
    // null-valued entries are ignored when comparing and hashing.
    override fun equals(other: Any?): kotlin.Boolean {
        if (this === other) return true
        if (other !is ClassInstance) return false
        if (type != other.type) return false
        if (onlySlotsOfSameLayoutAs(other)) {
            return slots.contentEquals(other.slots)
        }
        for ((key, value) in elements) {
            if (value != null && value != other[key]) return false
        }
        for ((key, value) in other.elements) {
            if (value != null && this[key] == null) return false
        }
        return true
    }

    private fun onlySlotsOfSameLayoutAs(other: ClassInstance): kotlin.Boolean =
        layout != null &&
            layout === other.layout &&
            map.isNullOrEmpty() &&
            other.map.isNullOrEmpty()

    override fun hashCode(): Int {
        var result = 0
        val slots = slots
        if (slots != null) {
            for ((i, value) in slots.withIndex()) {
                if (value != null) {
                    result += layout!!.elementName(i).hashCode() xor value.hashCode()
                }
            }
        }
        map?.forEach { (key, value) ->
            if (value != null) {
                result += key.hashCode() xor value.hashCode()
            }
        }
        return 31 * type.hashCode() + result
    }

    private fun overflow(): MutableMap<kotlin.String, Value?> =
        map ?: LinkedHashMap<kotlin.String, Value?>().also { map = it }

    /**
     * A map view of the elements of an array-backed instance: the non-null slots, in layout order,
     * followed by any elements the layout doesn't name.
     */
    private inner class SlotElements(
        private val layout: ClassLayout,
        private val slots: Array<Value?>,
    ) : AbstractMutableMap<kotlin.String, Value?>() {
        override val size: Int
            get() = slots.count { it != null } + (map?.size ?: 0)

        override fun containsKey(key: kotlin.String): kotlin.Boolean = has(key)

        override fun get(key: kotlin.String): Value? = this@ClassInstance[key]

        override fun put(key: kotlin.String, value: Value?): Value? {
            val index = layout.indexOf(key)
            if (index < 0) return overflow().put(key, value)
            val previous = slots[index]
            slots[index] = value
            return previous
        }

        override fun remove(key: kotlin.String): Value? {
            val index = layout.indexOf(key)
            if (index < 0) return map?.remove(key)
            val previous = slots[index]
            slots[index] = null
            return previous
        }

        override val entries: MutableSet<MutableMap.MutableEntry<kotlin.String, Value?>>
            get() =
                object : AbstractMutableSet<MutableMap.MutableEntry<kotlin.String, Value?>>() {
                    override val size: Int
                        get() = this@SlotElements.size

                    override fun add(
                        element: MutableMap.MutableEntry<kotlin.String, Value?>
                    ): kotlin.Boolean = throw UnsupportedOperationException()

                    override fun iterator():
                        MutableIterator<MutableMap.MutableEntry<kotlin.String, Value?>> =
                        EntryIterator(layout, slots)
                }
    }

    private inner class EntryIterator(
        private val layout: ClassLayout,
        private val slots: Array<Value?>,
    ) : MutableIterator<MutableMap.MutableEntry<kotlin.String, Value?>> {
        private var next = nextSlot(0)
        private var last = -1
        private val overflow = map?.entries?.iterator()
        private var inOverflow = false

        private fun nextSlot(from: Int): Int {
            var i = from
            while (i < slots.size && slots[i] == null) i++
            return i
        }

        override fun hasNext() = next < slots.size || overflow?.hasNext() == true

        override fun next(): MutableMap.MutableEntry<kotlin.String, Value?> {
            if (next < slots.size) {
                last = next
                next = nextSlot(next + 1)
                return SlotEntry(layout, slots, last)
            }
            if (overflow == null) throw NoSuchElementException()
            inOverflow = true
            return overflow.next()
        }

        override fun remove() {
            if (inOverflow) {
                overflow!!.remove()
            } else {
                check(last >= 0)
                slots[last] = null
                last = -1
            }
        }
    }

    private class SlotEntry(
        private val layout: ClassLayout,
        private val slots: Array<Value?>,
        private val index: Int,
    ) : MutableMap.MutableEntry<kotlin.String, Value?> {
        override val key: kotlin.String
            get() = layout.elementName(index)

        override val value: Value?
            get() = slots[index]

        override fun setValue(newValue: Value?): Value? {
            val previous = slots[index]
            slots[index] = newValue
            return previous
        }

        override fun equals(other: Any?) =
            other is Map.Entry<*, *> && other.key == key && other.value == value

        override fun hashCode() = key.hashCode() xor (value?.hashCode() ?: 0)

        override fun toString() = "$key=$value"
    }

    private companion object {
        // Placeholder for the primary constructor's map when the layout constructor is used
        val NO_ELEMENTS: MutableMap<kotlin.String, Value?> = HashMap()
    }
}
