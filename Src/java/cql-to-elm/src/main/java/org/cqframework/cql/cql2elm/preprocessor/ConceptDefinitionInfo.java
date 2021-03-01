package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.cqframework.cql.gen.cqlParser;

/**
 * Created by Bryn on 5/22/2016.
 */
public class ConceptDefinitionInfo extends BaseInfo {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    @Override
    public cqlParser.ConceptDefinitionContext getDefinition() {
        return (cqlParser.ConceptDefinitionContext)super.getDefinition();
    }

    public void setDefinition(cqlParser.ConceptDefinitionContext value) {
        super.setDefinition(value);
    }

    public ConceptDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public ConceptDefinitionInfo withDefinition(cqlParser.ConceptDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
