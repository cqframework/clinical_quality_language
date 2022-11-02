package org.cqframework.cql.elm.analyzing;

import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.elm.r1.ExpressionRef;
import org.hl7.elm.r1.Library;

import java.util.ArrayList;
import java.util.List;

public class AnalysisVisitor  extends ElmBaseLibraryVisitor<Void, VisitorContext> {

    public AnalysisVisitor() {
        super();
        analyzerList = new ArrayList<>();
    }

    List<Analyzer> analyzerList;

    public void registerAnalyser(Analyzer analyzer) {
        analyzerList.add(analyzer);
    }

    @Override
    public Void visitLibrary(Library library, VisitorContext context) {
        context.enterLibrary(library.getIdentifier());
        super.visitLibrary(library, context);
        context.exitLibrary();

        return null;
    }

    @Override
    public Void visitExpressionRef(ExpressionRef elm, VisitorContext context) {
        analyzerList.forEach(analyzer -> analyzer.analyze(elm, context));
        return null;
    }
}
