package org.cqframework.cql.cql2elm.elm;

import java.util.List;
import java.util.function.BiFunction;

import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.visiting.ElmFunctionalVisitor;
import org.hl7.elm.r1.Element;

public class ElmEditor extends ElmFunctionalVisitor<Void, List<ElmEdit>> {

    private static final BiFunction<Trackable, List<ElmEdit>, Void> APPLY_EDITS = (x, y) -> {

        if (!(x instanceof Element)) {
            return null;
        }

        for (ElmEdit edit : y) {
            edit.edit((Element) x);
        }

        return null;
    };

    public ElmEditor() {
        super(APPLY_EDITS, (x, y) -> y); // dummy aggregateResult
    }

}
