package org.opencds.cqf.cql.engine.retrieve;

import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;


public abstract class TerminologyAwareRetrieveProvider implements RetrieveProvider {

    protected TerminologyProvider terminologyProvider;

    // TODO: Think about how to best handle the decision to expand value sets... Should it be part of the
    // terminology provider if it detects support for "code:in"? How does that feed back to the retriever?
    protected boolean expandValueSets;
    public boolean isExpandValueSets() {
        return expandValueSets;
    }
    public TerminologyAwareRetrieveProvider setExpandValueSets(boolean expandValueSets) {
        this.expandValueSets = expandValueSets;
        return this;
    }

    public TerminologyProvider getTerminologyProvider() {
        return this.terminologyProvider;
    }

    public void setTerminologyProvider(TerminologyProvider terminologyProvider) {
        this.terminologyProvider = terminologyProvider;
    }
}

