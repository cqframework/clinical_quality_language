package org.cqframework.cql.elm.tags;

import java.util.List;

import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.UsingDef;

public class TagSetVisitor extends ElmBaseLibraryVisitor<Void, TagSet>  {

    @Override
    public Void visitUsingDef(UsingDef def, TagSet tagSet) {
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
