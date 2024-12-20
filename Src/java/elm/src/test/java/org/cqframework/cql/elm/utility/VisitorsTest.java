package org.cqframework.cql.elm.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Library.Statements;
import org.junit.jupiter.api.Test;

class VisitorsTest {

    @Test
    void constructVisitorTest() {
        // set up visitor that counts all visited elements
        var trackableCounter = Visitors.from((elm, context) -> 1, Integer::sum);

        var library = new Library();
        library.setStatements(new Statements());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());

        var result = trackableCounter.visitLibrary(library, null);
        assertEquals(4, result.intValue()); // ELM elements

        // This visitor returns the context object that's passed in
        var contextReturner = Visitors.from((t, c) -> c);
        var context = new Object();
        assertEquals(context, contextReturner.visitLibrary(library, context));
    }

    @Test
    void nullVisitorTest() {
        assertThrows(NullPointerException.class, () -> Visitors.<Object, Object>from(null));
        assertThrows(NullPointerException.class, () -> Visitors.<Object, Object>from(null, null));
        assertThrows(NullPointerException.class, () -> Visitors.<Object, Object>from(null, (a, b) -> b));
        assertThrows(NullPointerException.class, () -> Visitors.<Object, Object>from((t, c) -> null, null));
    }
}
