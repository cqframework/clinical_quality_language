package org.cqframework.cql.elm.analyzing;

import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.elm.r1.ExpressionDef;
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
        try {
            super.visitLibrary(library, context);
        } finally {
            context.exitLibrary();
        }

        return null;
    }

    @Override
    public Void visitExpressionDef(ExpressionDef def, VisitorContext context) {
        context.enterExpressionDef(def);
        try {
            super.visitExpressionDef(def, context);
        }
        finally {
            context.exitExpressionDef(def);
        }
        return null;
    }

    @Override
    public Void visitExpressionRef(ExpressionRef elm, VisitorContext context) {
        analyzerList.forEach(analyzer -> analyzer.analyze(elm, context));
        return null;
    }
}
