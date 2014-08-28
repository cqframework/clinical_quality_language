package org.cqframework.cql.poc.translator.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class LetStatementInfo {
    private String name;
    private cqlParser.LetStatementContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.LetStatementContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.LetStatementContext value) {
        definition = value;
    }

    public LetStatementInfo withName(String value) {
        setName(value);
        return this;
    }

    public LetStatementInfo withDefinition(cqlParser.LetStatementContext value) {
        setDefinition(value);
        return this;
    }
}
