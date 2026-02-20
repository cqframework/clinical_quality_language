package org.opencds.cqf.cql.engine.debug

import org.hl7.elm.r1.Element
import org.opencds.cqf.cql.engine.debug.DebugLocator.DebugLocatorType

class DebugLibraryMapEntry(val libraryName: String?) {
    private val nodeEntries: MutableMap<String?, DebugMapEntry?> = HashMap()
    private val locationEntries: MutableMap<String?, DebugMapEntry> = HashMap()

    @Suppress("ReturnCount")
    fun shouldDebug(node: Element?): DebugAction? {
        if (node != null) {
            val nodeEntry = nodeEntries[node.localId]
            if (nodeEntry != null && nodeEntry.action != DebugAction.NONE) {
                return nodeEntry.action
            }

            for (entry in locationEntries.values) {
                if (node.locator != null) {
                    val nodeLocation: Location = Location.fromLocator(node.locator!!)
                    if (
                        entry.locator.location!!.includes(nodeLocation) &&
                            entry.action != DebugAction.NONE
                    ) {
                        return entry.action
                    }
                }
            }
        }

        return DebugAction.NONE
    }

    fun addEntry(debugLocator: DebugLocator, action: DebugAction?) {
        addEntry(DebugMapEntry(debugLocator, action))
    }

    fun addEntry(entry: DebugMapEntry) {
        when (entry.locator.locatorType) {
            DebugLocatorType.NODE_ID -> nodeEntries[entry.locator.locator] = entry
            DebugLocatorType.LOCATION -> locationEntries[entry.locator.locator] = entry
            else ->
                throw IllegalArgumentException(
                    "Library debug map entry can only contain node id or location debug entries"
                )
        }
    }

    fun removeEntry(debugLocator: DebugLocator) {
        when (debugLocator.locatorType) {
            DebugLocatorType.NODE_ID -> nodeEntries.remove(debugLocator.locator)
            DebugLocatorType.LOCATION -> locationEntries.remove(debugLocator.locator)
            else ->
                throw IllegalArgumentException(
                    "Library debug map entry only contains node id or location debug entries"
                )
        }
    }
}
