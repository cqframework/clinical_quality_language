package org.cqframework.cql.cql2elm.elm;

import java.util.List;
import java.util.Objects;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.utility.Visitors;
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Library;

public class ElmEditor {

    private final List<ElmEdit> edits;
    private final FunctionalElmVisitor<Trackable, Void> visitor;

    public ElmEditor(List<ElmEdit> edits) {
        this.edits = Objects.requireNonNull(edits);
        this.visitor = Visitors.from((elm, context) -> elm, this::applyEdits);
    }

    public void edit(Library library) {
        this.visitor.visitLibrary(library, null);
    }

    protected Trackable applyEdits(Trackable aggregate, Trackable nextResult) {
        if (nextResult instanceof Element) {
            for (ElmEdit edit : edits) {
                edit.edit((Element) nextResult);
            }
        }

        return aggregate;
    }
}
