package org.cqframework.cql.elm.analyzing;

import org.cqframework.cql.elm.tags.TagInfo;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionRef;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.List;
import java.util.function.Predicate;

public class DeprecateAnalyzer implements Analyzer {
    @Override
    public void analyze(Element element, VisitorContext context) {
        if (element instanceof ExpressionRef) {
            ExpressionRef expressionRef = (ExpressionRef) element;
            VersionedIdentifier library = context.getCurrentLibraryIdentifier();

            List<TagInfo> deprecatedTagList = context.getTagSet().select(getDeprecatedTagInfo);
            List<TagInfo> noWarningTagList = context.getTagSet().select(getNoWarningTagInfo);

            if (!matchNoWarning(noWarningTagList, library, context.getCurrentExpressionDef().getName()) &&
                    matchDeprecated(deprecatedTagList, library, expressionRef.getName())) {
                System.out.println(String.format("Warning : Usage of deprecated expression definition `%s` in `%s`",
                        expressionRef.getName(), context.getCurrentExpressionDef().getName()));
            }
        }
    }

    private boolean matchNoWarning(List<TagInfo> list, VersionedIdentifier library, String expressionDefName) {
        for (TagInfo tagInfo : list) {
            if (tagInfo.expressionName().equals(expressionDefName) &&
                    tagInfo.library().equals(library)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchDeprecated(List<TagInfo> list, VersionedIdentifier library, String expressionRefName) {
        for (TagInfo tagInfo : list) {
            if (tagInfo.expressionName().equals(expressionRefName) &&
                    tagInfo.library().equals(library)) {
                return true;
            }
        }
        return false;
    }

    private Predicate<TagInfo> getDeprecatedTagInfo = tagInfo -> tagInfo.name().equalsIgnoreCase("deprecated");

    private Predicate<TagInfo> getNoWarningTagInfo = tagInfo -> tagInfo.name().equalsIgnoreCase("nowarning");
}
