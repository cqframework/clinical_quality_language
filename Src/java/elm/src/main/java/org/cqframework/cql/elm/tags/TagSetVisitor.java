package org.cqframework.cql.elm.tags;

import java.util.List;

import com.sun.jdi.VoidType;
import com.sun.jdi.VoidValue;
import org.cqframework.cql.elm.analyzing.VisitorContext;
import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.UsingDef;

public class TagSetVisitor extends ElmBaseLibraryVisitor<Void, VisitorContext>  {

    public TagSetVisitor() {
        super();
    }

    @Override
    public Void visitLibrary(Library library, VisitorContext context) {
        context.enterLibrary(library.getIdentifier());
        try {
            if (library.getAnnotation() != null && !library.getAnnotation().isEmpty()) {
                Annotation annotation = context.getAnnotation(library.getAnnotation());
                if (annotation != null && !annotation.getT().isEmpty()) {
                    annotation.getT().forEach(t ->
                    {
                        context.getTagSet().add(new TagInfo(context.getCurrentLibraryIdentifier(), ElementType.HEADER,
                                    t.getName(), null, t.getValue(), annotation.getLocator()));
                    });
                }
            }
            super.visitLibrary(library, context);
        }
        finally {
            context.exitLibrary();
        }
        return null;
    }

    @Override
    public Void visitExpressionDef(ExpressionDef elm, VisitorContext context) {
        context.enterExpressionDef(elm);
        try {
            Annotation annotation = context.getAnnotation(elm.getAnnotation());
            if (annotation != null && !annotation.getT().isEmpty()) {
                annotation.getT().forEach(t ->
                {
                    context.getTagSet().add(new TagInfo(context.getCurrentLibraryIdentifier(), ElementType.EXPRESSION,
                                t.getName(), elm.getName(), t.getValue(), annotation.getLocator()));
                });
            }
        } finally {
            context.exitExpressionDef(elm);
        }
        return null;
    }

    @Override
    public Void visitUsingDef(UsingDef def, VisitorContext context) {
        return null;
    }

    private void reportTags(List<CqlToElmBase> annotations) {
        if (annotations == null) {
            return;
        }

        if (annotations.isEmpty()) {
            return;
        }

        for (CqlToElmBase a : annotations) {
            if (a instanceof Annotation) {
                reportTags((Annotation) a);
            }
        }
    }

    private void reportTags(Annotation annotation) {
        System.out.println("Report annotation tags :" + annotation.getT());
    }
}
