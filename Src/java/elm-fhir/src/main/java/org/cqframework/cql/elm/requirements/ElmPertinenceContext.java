package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.ExpressionDef;

public class ElmPertinenceContext {

    public ElmPertinenceContext(ExpressionDef expressionDef, String pertinenceValue) {
        if (expressionDef == null) {
            throw new IllegalArgumentException("expressionDef is required");
        }
        this.expressionDef = expressionDef;

        if (pertinenceValue == null) {
            throw new IllegalArgumentException("pertinenceValue is equired");
        }
        this.pertinenceValue = pertinenceValue;
    }

    private ExpressionDef expressionDef;
    public ExpressionDef getExpressionDef() {
        return this.expressionDef;
    }

    private String pertinenceValue;
    public String getPertinenceValue() {
        return this.pertinenceValue;
    }

}