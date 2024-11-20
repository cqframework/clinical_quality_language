package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class ContextDefinitionInfo extends BaseInfo {
    private String context;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public cqlParser.ContextDefinitionContext getDefinition() {
        return (cqlParser.ContextDefinitionContext) super.getDefinition();
    }

    public void setDefinition(cqlParser.ContextDefinitionContext definition) {
        super.setDefinition(definition);
    }
}
