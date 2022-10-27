package org.cqframework.cql.elm.analyzing;

import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.elm.r1.ExpressionRef;
import org.hl7.elm.r1.Library;

public class AnalysisVisitor  extends ElmBaseLibraryVisitor<Void, VisitorContext> {

    public AnalysisVisitor() {
        super();
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
        System.out.println("Expression ref: " + elm.getName());
        return null;
    }

    //instantiate VisitorContext
    //populateTagSet();

//    ELMAnalyzers analyzers
//    VisitorContext context
//    beforeVisit (context.parents.add(this))
//    afterVisit (context.parents.pop())
//    visit(ExpressionDef e ) {
//        // foreach Analyzer : analyzers
//        ///   analyzer.definitionMatcher(e, context);
//    }
}
