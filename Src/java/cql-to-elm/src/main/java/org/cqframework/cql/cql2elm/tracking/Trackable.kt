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
    private fun Any.refHash(): Int = System.identityHashCode(this)

    private val trackerIdsByElement = HashMap<Int, UUID>()
    var Element.trackerId: UUID
        get() = trackerIdsByElement.getOrPut(this.refHash()) { UUID.randomUUID() }
        set(value) {
            trackerIdsByElement[this.refHash()] = value
        }

    private val trackbacksByElement = HashMap<Int, MutableList<TrackBack>>()
    var Element.trackbacks: MutableList<TrackBack>
        get() = trackbacksByElement.getOrPut(this.refHash()) { ArrayList() }
        set(value) {
            trackbacksByElement[this.refHash()] = value
        }

    private val resultTypesByElement = HashMap<Int, DataType?>()
    var Element.resultType: DataType?
        get() = resultTypesByElement[this.refHash()]
        set(value) {
            resultTypesByElement[this.refHash()] = value
        }

    fun <T : Element> T.withResultType(resultType: DataType?): T {
        this.resultType = resultType
        return this
    }

    fun clear() {
        trackerIdsByElement.clear()
        trackbacksByElement.clear()
        resultTypesByElement.clear()
    }
}
