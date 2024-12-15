package org.cqframework.cql.cql2elm.tracking

import java.util.*
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Element

object Trackable {
    val trackerId: UUID = UUID.randomUUID()

    private val trackerIds = WeakHashMap<Element, UUID>()
    var Element.trackerId: UUID?
        get() = trackerIds[this]
        set(value) {
            trackerIds[this] = value
        }

    private val trackbacksByElement = WeakHashMap<Element, MutableList<TrackBack>>()
    var Element.trackbacks: MutableList<TrackBack>
        get() = trackbacksByElement.getOrPut(this) { ArrayList() }
        set(value) {
            trackbacksByElement[this] = value
        }

    private val resultTypes = WeakHashMap<Element, DataType?>()
    var Element.resultType: DataType?
        get() = resultTypes[this]
        set(value) {
            resultTypes[this] = value
        }

    fun <T : Element> T.withResultType(resultType: DataType?): T {
        this.resultType = resultType
        return this
    }
}
