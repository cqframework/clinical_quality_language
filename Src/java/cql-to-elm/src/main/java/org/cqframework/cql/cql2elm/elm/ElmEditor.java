package org.cqframework.cql.cql2elm.elm;

import java.util.List;
import java.util.Objects;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.utility.Visitors;
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Library;

public class ElmEditor {

    private final List<IElmEdit> edits;
    private final FunctionalElmVisitor<Trackable, Void> visitor;

    public ElmEditor(List<IElmEdit> edits) {
        this.edits = Objects.requireNonNull(edits);
        this.visitor = Visitors.from((elm, context) -> elm, this::aggregateResults);
    }

    public void edit(Library library) {
        this.visitor.visitLibrary(library, null);

        // This is needed because aggregateResults is not called on the library itself.
        this.applyEdits(library);
    }

    protected Trackable aggregateResults(Trackable aggregate, Trackable nextResult) {
        applyEdits(nextResult);
        return aggregate;
    }

    protected void applyEdits(Trackable trackable) {
        if (trackable instanceof Element) {
            for (IElmEdit edit : edits) {
                edit.edit((Element) trackable);
            }
        }
    }
}
