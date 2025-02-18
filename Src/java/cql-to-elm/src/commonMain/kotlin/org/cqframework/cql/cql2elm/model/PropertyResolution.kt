package org.cqframework.cql.cql2elm.model

import kotlin.jvm.JvmOverloads
import org.hl7.cql.model.ClassTypeElement
import org.hl7.cql.model.DataType
import org.hl7.cql.model.SearchType
import org.hl7.cql.model.TupleTypeElement

/** Created by Bryn on 4/19/2019. */
data class PropertyResolution(
    val type: DataType,
    val name: String,
    val targetMap: String? = null,
    val isSearch: Boolean = false
) {
    constructor(e: ClassTypeElement) : this(e.type, e.name, e.target)

    constructor(e: TupleTypeElement) : this(e.type, e.name)

    constructor(s: SearchType) : this(s.type, s.name, isSearch = true)

    @JvmOverloads
    constructor(
        type: DataType,
        name: String,
        targetMaps: Map<DataType, String>? = null
    ) : this(type, name, targetMaps.toTargetMapString())

    companion object {
        private fun Map<DataType, String>?.toTargetMapString(): String? {
            return when {
                this.isNullOrEmpty() -> null
                else -> {
                    val builder = StringBuilder()
                    for ((key, value) in this) {
                        if (builder.isNotEmpty()) {
                            builder.append(";")
                        }
                        if (this.size > 1) {
                            builder.append(key.toString())
                            builder.append(":")
                        }
                        builder.append(value)
                    }
                    builder.toString()
                }
            }
        }
    }
}
