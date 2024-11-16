package org.cqframework.cql.cql2elm.elm

import org.cqframework.cql.elm.tracking.Trackable
import org.cqframework.cql.elm.utility.Visitors
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library

class ElmEditor(private val edits: List<IElmEdit>) {
    private val visitor: FunctionalElmVisitor<Trackable?, Void> =
        Visitors.from({ t, _ -> t }, { current, next -> this.aggregateResults(current, next) })

    fun edit(library: Library) {
        visitor.visitLibrary(library, null)

        // This is needed because aggregateResults is not called on the library itself.
        this.applyEdits(library)
    }

    private fun aggregateResults(aggregate: Trackable?, nextResult: Trackable?): Trackable? {
        applyEdits(nextResult)
        return aggregate
    }

    fun applyEdits(trackable: Trackable?) {
        if (trackable is Element) {
            for (edit in edits) {
                edit.edit(trackable)
            }
        }
    }
}
