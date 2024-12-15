package org.cqframework.cql.cql2elm.tracking

import java.util.*
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Element

/**
 * This class is used to track the resultType of an element. It implements a few extension
 * properties to make it more concise to use. Java does not support extension properties, so we have
 * to use a helper object to achieve the same effect. Once more of the code is converted to Kotlin,
 * we can remove this class and use extension properties directly.
 *
 * In Java use Trackable.INSTANCE to access the same functionality.
 */
object Trackable {
    private val trackerIdsByElement = IdentityHashMap<Element, UUID>()
    var Element.trackerId: UUID
        get() = trackerIdsByElement.getOrPut(this) { UUID.randomUUID() }
        set(value) {
            trackerIdsByElement[this] = value
        }

    private val trackbacksByElement = IdentityHashMap<Element, MutableList<TrackBack>>()
    var Element.trackbacks: MutableList<TrackBack>
        get() = trackbacksByElement.getOrPut(this) { ArrayList() }
        set(value) {
            trackbacksByElement[this] = value
        }

    private val resultTypesByElement = IdentityHashMap<Element, DataType?>()
    var Element.resultType: DataType?
        get() = resultTypesByElement[this]
        set(value) {
            resultTypesByElement[this] = value
        }

    fun <T : Element> T.withResultType(resultType: DataType?): T {
        this.resultType = resultType
        return this
    }
}
