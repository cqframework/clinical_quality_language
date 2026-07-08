package org.opencds.cqf.cql.engine.retrieve

import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

abstract class TerminologyAwareRetrieveProvider : RetrieveProvider {
    var terminologyProvider: TerminologyProvider? = null

    // TODO: Think about how to best handle the decision to expand value sets... Should it be part
    // of the
    // terminology provider if it detects support for "code:in"? How does that feed back to the
    // retriever?
    var isExpandValueSets: Boolean = false
        protected set

    fun setExpandValueSets(expandValueSets: Boolean): TerminologyAwareRetrieveProvider {
        this.isExpandValueSets = expandValueSets
        return this
    }
}
