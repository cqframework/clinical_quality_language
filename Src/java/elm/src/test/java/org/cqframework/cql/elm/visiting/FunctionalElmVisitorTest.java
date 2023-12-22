package org.cqframework.cql.elm.visiting;

import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Library.Statements;
import org.junit.Test;

public class FunctionalElmVisitorTest {

    @Test
    public void countTest() {
        // set up visitor that counts all visited elements
        var trackableCounter = new FunctionalElmVisitor<Integer, Void>((elm, context) -> 1, Integer::sum);

        var library = new Library();
        library.setStatements(new Statements());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());
        library.getStatements().getDef().add(new ExpressionDef());

        var result = trackableCounter.visitLibrary(library, null);
        assertEquals(4 + 3, result.intValue()); // ELM elements + implicit access modifiers

        // set up visitor that counts all visited ELM elements
        var elmCounter =
                new FunctionalElmVisitor<Integer, Void>((elm, context) -> elm instanceof Element ? 1 : 0, Integer::sum);

        result = elmCounter.visitLibrary(library, null);
        assertEquals(4, result.intValue());

        var maxFiveCounter = new FunctionalElmVisitor<Integer, Void>(
                (elm, context) -> 1, (aggregate, nextResult) -> aggregate >= 5 ? aggregate : aggregate + nextResult);

        result = maxFiveCounter.visitLibrary(library, null);
        assertEquals(5, result.intValue());
    }

    @Test
    public void nullVisitorTest() {
        assertThrows(NullPointerException.class, () -> new FunctionalElmVisitor<Integer, Void>(null, null));
        assertThrows(
                NullPointerException.class, () -> new FunctionalElmVisitor<Integer, Void>(null, Integer::sum));
        assertThrows(
                NullPointerException.class, () -> new FunctionalElmVisitor<Integer, Void>((x, y) -> 1, null));
    }
}
