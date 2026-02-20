package org.opencds.cqf.cql.engine.debug

import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library
import org.opencds.cqf.cql.engine.debug.DebugLocator.DebugLocatorType

class DebugMap {
    private val libraryMaps: MutableMap<String?, DebugLibraryMapEntry?>
    private val nodeTypeEntries: MutableMap<String?, DebugMapEntry?>
    private val exceptionTypeEntries: MutableMap<String?, DebugMapEntry?>

    @Suppress("ReturnCount")
    fun shouldDebug(e: Exception): DebugAction? {
        if (exceptionTypeEntries.size == 0) {
            return DebugAction.LOG
        } else {
            val exceptionTypeEntry = exceptionTypeEntries.get(e::class.simpleName)
            if (exceptionTypeEntry != null) return exceptionTypeEntry.action
        }

        // Exceptions are always logged (unless explicitly disabled by a DebugAction.NONE for the
        // specific type)
        return DebugAction.LOG
    }

    @Suppress("ReturnCount")
    fun shouldDebug(node: Element, currentLibrary: Library): DebugAction? {
        val libraryMap = libraryMaps.get(currentLibrary.identifier!!.id)
        if (libraryMap != null) {
            val action = libraryMap.shouldDebug(node)
            if (action != DebugAction.NONE) {
                return action
            }
        }

        val nodeEntry = nodeTypeEntries.get(node::class.simpleName)
        if (nodeEntry != null && nodeEntry.action != DebugAction.NONE) {
            return nodeEntry.action
        }

        if (isLoggingEnabled) {
            return DebugAction.LOG
        }

        if (isCoverageEnabled) {
            return DebugAction.TRACE
        }

        return DebugAction.NONE
    }

    private fun getLibraryMap(libraryName: String?): DebugLibraryMapEntry? {
        return libraryMaps.get(libraryName)
    }

    private fun ensureLibraryMap(libraryName: String?): DebugLibraryMapEntry {
        var libraryMap = libraryMaps.get(libraryName)
        if (libraryMap == null) {
            libraryMap = DebugLibraryMapEntry(libraryName)
            libraryMaps.put(libraryName, libraryMap)
        }

        return libraryMap
    }

    // private void addLibraryMapEntry(String libraryName, DebugLibraryMapEntry libraryMapEntry) {
    //     libraryMaps.put(libraryName, libraryMapEntry);
    // }
    fun addDebugEntry(debugLocator: DebugLocator, action: DebugAction?) {
        addDebugEntry(null, debugLocator, action)
    }

    fun addDebugEntry(libraryName: String?, debugLocator: DebugLocator, action: DebugAction?) {
        when (debugLocator.locatorType) {
            DebugLocatorType.NODE_TYPE ->
                nodeTypeEntries.put(debugLocator.locator, DebugMapEntry(debugLocator, action))
            DebugLocatorType.EXCEPTION_TYPE ->
                exceptionTypeEntries.put(debugLocator.locator, DebugMapEntry(debugLocator, action))
            else -> {
                requireNotNull(libraryName) { "Library entries must have a library name specified" }
                val libraryMap = getLibraryMap(libraryName)
                libraryMap?.addEntry(debugLocator, action)
            }
        }
    }

    fun removeDebugEntry(libraryName: String?, debugLocator: DebugLocator) {
        when (debugLocator.locatorType) {
            DebugLocatorType.NODE_TYPE -> nodeTypeEntries.remove(debugLocator.locator)
            DebugLocatorType.EXCEPTION_TYPE -> exceptionTypeEntries.remove(debugLocator.locator)
            else -> {
                requireNotNull(libraryName) { "Library entries must have a library name specified" }
                val libraryMap = getLibraryMap(libraryName)
                libraryMap?.removeEntry(debugLocator)
            }
        }
    }

    fun removeDebugEntry(debugLocator: DebugLocator) {
        removeDebugEntry(null, debugLocator)
    }

    var isLoggingEnabled: Boolean = false

    var isCoverageEnabled: Boolean = false

    init {
        libraryMaps = HashMap<String?, DebugLibraryMapEntry?>()
        nodeTypeEntries = HashMap<String?, DebugMapEntry?>()
        exceptionTypeEntries = HashMap<String?, DebugMapEntry?>()
    }
}
