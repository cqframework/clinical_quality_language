package org.opencds.cqf.cql.engine.debug

import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.Profile

class DebugResult {
    val libraryResults: MutableMap<String?, DebugLibraryResultEntry?> = HashMap()
    private val messages: ArrayList<CqlException?> = ArrayList()
    var profile: Profile? = null
        private set

    fun logDebugResult(node: Element, currentLibrary: Library, result: Any?, action: DebugAction?) {
        if (action == DebugAction.NONE) {
            return
        }

        try {
            var libraryResultEntry = libraryResults[currentLibrary.identifier!!.id]
            if (libraryResultEntry == null) {
                libraryResultEntry = DebugLibraryResultEntry(currentLibrary.identifier!!.id)
                libraryResults[libraryResultEntry.libraryName] = libraryResultEntry
            }
            libraryResultEntry.logDebugResultEntry(node, result)

            if (action == DebugAction.LOG) {
                DebugUtilities.logDebugResult(node, currentLibrary, result)
            }
        } catch (e: Exception) {
            // do nothing, an exception logging debug helps no one
        }
    }

    fun logDebugError(exception: CqlException?) {
        messages.add(exception)
    }

    fun getMessages(): MutableList<CqlException?> {
        return messages
    }

    fun ensureProfile(): Profile {
        if (this.profile == null) {
            this.profile = Profile()
        }
        return this.profile!!
    }
}
