package org.cqframework.cql.cql2elm.elm

import org.hl7.elm.r1.Library

/**
 * Runs a list of [ElmPass] implementations against a [Library] in declared order.
 *
 * The order in which passes are registered defines their ordering in the pipeline. Callers should
 * prefer semantically-meaningful rewrites (e.g. date-range promotion) before destructive strip-down
 * passes (e.g. result-type removal).
 */
class ElmPassPipeline(private val passes: List<ElmPass>) {
    fun run(library: Library) {
        for (pass in passes) {
            pass.apply(library)
        }
    }
}
