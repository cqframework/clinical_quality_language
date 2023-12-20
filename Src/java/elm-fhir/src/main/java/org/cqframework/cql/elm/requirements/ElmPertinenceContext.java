package org.cqframework.cql.elm.requirements;

import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Tag;
import org.hl7.elm.r1.ExpressionDef;

public class ElmPertinenceContext {

    public ElmPertinenceContext(ExpressionDef expressionDef) {
        if (expressionDef == null) {
            throw new IllegalArgumentException("expressionDef is required");
        }
        this.expressionDef = expressionDef;
    }

    private ExpressionDef expressionDef;

    public ExpressionDef getExpressionDef() {
        return this.expressionDef;
    }

    private String pertinenceValue;

    public String getPertinenceValue() {
        return this.pertinenceValue;
    }

    public boolean checkPertinenceTag() {
        boolean pertinenceFound = false;
        Annotation a = null;
        for (Object o : expressionDef.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation) o;
            }
            for (int i = 0; i < a.getT().size(); i++) {
                Tag t = a.getT().get(i);
                if (t.getName() != null && t.getName().equals("pertinence")) {
                    pertinenceFound = true;
                    pertinenceValue = t.getValue();
                }
            }
        }
        return pertinenceFound;
    }
}
