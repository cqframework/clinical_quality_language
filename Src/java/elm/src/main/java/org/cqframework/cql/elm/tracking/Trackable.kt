package org.cqframework.cql.elm.tracking

import jakarta.xml.bind.annotation.XmlTransient
import java.util.*
import org.hl7.cql.model.DataType

open class Trackable {
    @get:XmlTransient val trackerId: UUID = UUID.randomUUID()

    @get:XmlTransient val trackbacks: MutableList<TrackBack> = ArrayList()

    @get:XmlTransient var resultType: DataType? = null

    fun withResultType(resultType: DataType?): Trackable {
        this.resultType = resultType
        return this
    }
}
