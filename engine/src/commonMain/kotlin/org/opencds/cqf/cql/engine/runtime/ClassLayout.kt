package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport

/**
 * The element names of a class type, in slot order. One layout is shared by every [ClassInstance]
 * of the type that uses it, so each instance only stores its element values.
 */
@JsOnlyExport
class ClassLayout(elementNames: Iterable<kotlin.String>) {
    private val names: Array<kotlin.String> = elementNames.toList().toTypedArray()
    private val indexes: Map<kotlin.String, Int> =
        HashMap<kotlin.String, Int>(names.size * 2).also { map ->
            names.forEachIndexed { i, name -> map[name] = i }
        }

    init {
        require(indexes.size == names.size) {
            "Duplicate element names in layout: ${names.toList()}"
        }
    }

    /** The number of elements in the layout. */
    val size: Int
        get() = names.size

    /** The name of the element in the given slot. */
    fun elementName(index: Int): kotlin.String = names[index]

    /** The slot of the named element, or -1 if the layout has no such element. */
    fun indexOf(elementName: kotlin.String): Int = indexes[elementName] ?: -1
}
