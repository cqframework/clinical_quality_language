package org.cqframework.cql.cql2elm.tracking

import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Element
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.Uuid.Companion.random

/**
 * This class is used to track the resultType of an element. It implements a few extension
 * properties to make it more concise to use. Java does not support extension properties, so we have
 * to use a helper object to achieve the same effect. Once more of the code is converted to Kotlin,
 * we can remove this class and use extension properties directly.
 *
 * In Java use Trackable.INSTANCE to access the same functionality.
 */
@OptIn(ExperimentalUuidApi::class)
object Trackable {

    private data class ExtensionProperties constructor(
        val trackerId: Uuid = random(),
        val trackbacks: MutableList<TrackBack> = arrayListOf(),
        var resultType: DataType? = null
    )

    private val extensionPropertiesByElement = WeakIdentityHashMap<Element, ExtensionProperties>()
    private val Element.extensions: ExtensionProperties
        get() = extensionPropertiesByElement.getOrPut(this) { ExtensionProperties() }

    val Element.trackerId: Uuid
        get() = this.extensions.trackerId

    val Element.trackbacks: MutableList<TrackBack>
        get() = this.extensions.trackbacks

    var Element.resultType: DataType?
        get() = this.extensions.resultType
        set(value) {
            this.extensions.resultType = value
        }

    fun <T : Element> T.withResultType(resultType: DataType?): T {
        this.resultType = resultType
        return this
    }
}
