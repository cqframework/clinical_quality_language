package org.cqframework.cql.elm.tracking

import jakarta.xml.bind.annotation.XmlTransient
import java.util.*
import org.hl7.cql.model.DataType

@kotlinx.serialization.Serializable
open class Trackable {
    @get:XmlTransient @kotlinx.serialization.Transient val trackerId: UUID = UUID.randomUUID()

    @get:XmlTransient
    @kotlinx.serialization.Transient
    val trackbacks: MutableList<TrackBack> = ArrayList()

    @get:XmlTransient @kotlinx.serialization.Transient var resultType: DataType? = null

    fun withResultType(resultType: DataType?): Trackable {
        this.resultType = resultType
        return this
    }
}
