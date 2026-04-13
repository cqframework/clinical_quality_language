package org.cqframework.cql.cql2elm.elm

import org.cqframework.cql.elm.visiting.FunctionalElmVisitor
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library

/**
 * An [ElmPass] that applies a list of per-element [IElmEdit]s to every element in a library.
 *
 * Used primarily for strip-down operations driven by compiler options (removing locators, result
 * types, and narrative annotations when the corresponding options are disabled).
 */
class ElmEditor(private val edits: List<IElmEdit>) : ElmPass {
    override val name: String = "ElmEditor"

    private val visitor: FunctionalElmVisitor<Element?, Unit> =
        FunctionalElmVisitor.from(
            { t, _ -> t },
            { current, next -> this.aggregateResults(current, next) },
        )

    override fun apply(library: Library) {
        visitor.visitLibrary(library, Unit)

        // aggregateResults is not called on the library itself, so apply edits explicitly.
        this.applyEdits(library)
    }

    private fun aggregateResults(aggregate: Element?, nextResult: Element?): Element? {
        nextResult?.let { applyEdits(it) }
        return aggregate
    }

    fun applyEdits(trackable: Element) {
        for (edit in edits) {
            edit.edit(trackable)
        }
    }
}
