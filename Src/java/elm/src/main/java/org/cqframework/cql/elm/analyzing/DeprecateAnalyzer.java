package org.cqframework.cql.elm.analyzing;

import org.cqframework.cql.elm.tags.TagInfo;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionRef;

import java.util.List;

public class DeprecateAnalyzer implements Analyzer {
    @Override
    public void analyze(Element element, VisitorContext context) {
        System.out.println("DeprecateAnalyzer: analyze");
        if (element instanceof ExpressionRef) {
            System.out.println("Expression ref found");
            ExpressionRef expressionRef = (ExpressionRef) element;
            System.out.println(expressionRef.getName());

           List<TagInfo> list =  context.getTagSet().select(
                    tagInfo -> tagInfo.library().getId().equals(context.getCurrentLibraryIdentifier().getId())  &&
                            tagInfo.expressionName() != null && tagInfo.expressionName().equals(expressionRef.getName()) && tagInfo.name().equalsIgnoreCase("deprecated"));

           list.forEach(tagInfo -> System.out.println("Warning: found the usage of deprecated"));
        }
    }
}
