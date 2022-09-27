package org.cqframework.cql.elm.analyzing;

import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.ExpressionRef;

public class DeprecatedAnalyzer implements Analyzer {
    @Override
    public void analyze(Element element, VisitorContext context) {
        if (element instanceof ExpressionRef) {
            ExpressionRef expressionRef = (ExpressionRef) element;

            Annotation a = null;
            for (Object o : expressionRef.getAnnotation()) {
                if (o instanceof Annotation) {
                    a = (Annotation) o;
                    a.getT().forEach(tag -> {


//                    if(tag.getName().equalsIgnoreCase("Deprecated")) {
//                        context.warn("");
//                    }
                    });
                }
            }
        }
    }
}
