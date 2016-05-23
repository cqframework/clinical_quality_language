package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

/**
 * Created by Bryn on 5/22/2016.
 */
public class ConceptDefinitionInfo {
    private String name;
    private cqlParser.ConceptDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.ConceptDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.ConceptDefinitionContext value) {
        definition = value;
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
