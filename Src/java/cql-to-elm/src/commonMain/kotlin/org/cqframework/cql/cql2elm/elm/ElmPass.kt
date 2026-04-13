package org.cqframework.cql.cql2elm.elm

import org.hl7.elm.r1.Library

/**
 * A post-visit transformation applied to a completed [Library] tree.
 *
 * Passes run after the ELM tree is fully constructed, and may walk the tree, rewrite nodes, drop
 * nodes, or decorate them. The pipeline is ordered: each pass observes the output of all prior
 * passes. See [ElmPassPipeline] for the runner.
 */
interface ElmPass {
    /** Short name used for logging and ordering declarations. */
    val name: String

    fun apply(library: Library)
}
