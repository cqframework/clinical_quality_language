package org.opencds.cqf.cql.engine.debug

/*
Specifies a Debug entry point (breakpoint)
Can be
* nodeId: corresponding to the localId element of a node in the ELM
* nodeType: corresponding to the type of the node (will match all nodes of that type)
* locator: corresponding to a range in the source (will match all nodes with a locator range that includes the locator)
* exceptionType: corresponding to the type of an exception (will match whenever an exception of this type occurs)
 */
class DebugLocator {
    enum class DebugLocatorType {
        NODE_ID,
        NODE_TYPE,
        LOCATION,
        EXCEPTION_TYPE,
    }

    val locatorType: DebugLocatorType

    val locator: String

    val location: Location?

    constructor(location: Location) {
        this.locatorType = DebugLocatorType.LOCATION
        this.locator = location.toLocator()
        this.location = location
    }

    private fun guardLocator(locator: String) {
        require(locator.isNotBlank()) { "nodeId locator required" }
    }

    constructor(type: DebugLocatorType, locator: String) {
        this.locatorType = type
        when (type) {
            DebugLocatorType.NODE_ID,
            DebugLocatorType.EXCEPTION_TYPE -> {
                guardLocator(locator)
                this.locator = locator
                this.location = null
            }
            DebugLocatorType.NODE_TYPE -> {
                guardLocator(locator)
                if (!locator.endsWith("Evaluator")) {
                    this.locator = locator + "Evaluator"
                } else {
                    this.locator = locator
                }
                this.location = null
            }
            DebugLocatorType.LOCATION -> {
                this.location = Location.Companion.fromLocator(locator)
                this.locator = locator
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || this::class != other::class) {
            return false
        }

        other as DebugLocator

        if (this.locatorType != other.locatorType) {
            return false
        }

        if (locator !== other.locator) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = locator.hashCode()
        result = 31 * result + locatorType.ordinal
        return result
    }

    override fun toString(): String {
        return "DebugLocator{" + " type=" + locatorType.toString() + ", locator=" + locator + '}'
    }
}
