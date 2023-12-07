package org.cqframework.cql.cql2elm.elm;

import java.util.List;
import java.util.Objects;

import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.utility.Visitors;
import org.cqframework.cql.elm.visiting.ElmFunctionalVisitor;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Library;

public class ElmEditor {

    private final List<ElmEdit> edits;
    private final ElmFunctionalVisitor<Void, List<ElmEdit>> visitor;

    public ElmEditor(List<ElmEdit> edits) {
        this.edits = Objects.requireNonNull(edits);
        this.visitor = Visitors.from(ElmEditor::applyEdits);
    }

    public void edit(Library library) {
        this.visitor.visitLibrary(library, edits);
    }

    protected static Void applyEdits(Trackable trackable, List<ElmEdit> edits) {
        if (!(trackable instanceof Element)) {
            return null;
        }

        for (ElmEdit edit : edits) {
            edit.edit((Element) trackable);
        }

        return null;
    }
}
