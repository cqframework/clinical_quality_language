package org.cqframework.cql.elm.visiting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Library.Statements;
import org.junit.jupiter.api.Test;

class FunctionalElmVisitorTest {

    @Test
    void countTest() {
        // set up visitor that counts all visited elements
        var trackableCounter = new FunctionalElmVisitor<Integer, Void>((elm, context) -> 1, Integer::sum);

        var library = new Library();
        library.setStatements(new Statements());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());

        var result = trackableCounter.visitLibrary(library, null);
        assertEquals(4, result.intValue()); // ELM elements

        // set up visitor that counts all visited ELM elements
        var elmCounter =
                new FunctionalElmVisitor<Integer, Void>((elm, context) -> elm instanceof Element ? 1 : 0, Integer::sum);

        result = elmCounter.visitLibrary(library, null);
        assertEquals(4, result.intValue());

        var maxThreeCounter = new FunctionalElmVisitor<Integer, Void>(
                (elm, context) -> 1, (aggregate, nextResult) -> aggregate >= 3 ? aggregate : aggregate + nextResult);

        result = maxThreeCounter.visitLibrary(library, null);
        assertEquals(3, result.intValue());
    }

    @Test
    void nullVisitorTest() {
        assertThrows(NullPointerException.class, () -> new FunctionalElmVisitor<Integer, Void>(null, null));
        assertThrows(NullPointerException.class, () -> new FunctionalElmVisitor<Integer, Void>(null, Integer::sum));
        assertThrows(NullPointerException.class, () -> new FunctionalElmVisitor<Integer, Void>((x, y) -> 1, null));
    }

    @Test
    void constructVisitorTest() {
        // set up visitor that counts all visited elements
        var trackableCounter = FunctionalElmVisitor.Companion.from((elm, context) -> 1, Integer::sum);

        var library = new Library();
        library.setStatements(new Statements());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());

        var result = trackableCounter.visitLibrary(library, null);
        assertEquals(4, result.intValue()); // ELM elements

        // This visitor returns the context object that's passed in
        var contextReturner = FunctionalElmVisitor.Companion.from((t, c) -> c);
        var context = new Object();
        assertEquals(context, contextReturner.visitLibrary(library, context));
    }

    @Test
    void nullFactoryTest() {
        assertThrows(NullPointerException.class, () -> FunctionalElmVisitor.Companion.from(null));
        assertThrows(NullPointerException.class, () -> FunctionalElmVisitor.Companion.from(null, null));
        assertThrows(NullPointerException.class, () -> FunctionalElmVisitor.Companion.from(null, (a, b) -> b));
        assertThrows(NullPointerException.class, () -> FunctionalElmVisitor.Companion.from((t, c) -> null, null));
    }
}
