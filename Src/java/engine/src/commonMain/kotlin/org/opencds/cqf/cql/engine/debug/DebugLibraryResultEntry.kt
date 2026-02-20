package org.opencds.cqf.cql.engine.debug

import org.hl7.elm.r1.Element
import org.opencds.cqf.cql.engine.debug.DebugLocator.DebugLocatorType

class DebugLibraryResultEntry(val libraryName: String?) {
    val results: MutableMap<DebugLocator?, MutableList<DebugResultEntry?>>

    init {
        this.results = HashMap<DebugLocator?, MutableList<DebugResultEntry?>>()
    }

    private fun logDebugResult(locator: DebugLocator?, result: Any?) {
        if (!results.containsKey(locator)) {
            results.put(locator, ArrayList<DebugResultEntry?>())
        }
        val debugResults: MutableList<DebugResultEntry?> = results.get(locator)!!
        debugResults.add(DebugResultEntry(result))
    }

    fun logDebugResultEntry(node: Element, result: Any?) {
        if (node!!.localId != null) {
            val locator = DebugLocator(DebugLocatorType.NODE_ID, node.localId!!)
            logDebugResult(locator, result)
        }

        if (node.locator != null) {
            val locator = DebugLocator(Location.fromLocator(node.locator!!))
            logDebugResult(locator, result)
        }
    }
}
