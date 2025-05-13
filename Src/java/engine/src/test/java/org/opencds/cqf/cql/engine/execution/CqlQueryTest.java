package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.Tuple;

class CqlQueryTest extends CqlTestBase {

    @Test
    void all_query_operators() {
        var results = engine.evaluate(toElmIdentifier("CqlQueryTests"));
        var value = results.forExpression("RightShift").value();
        assertEquals(value, Arrays.asList(null, "A", "B", "C"));
        value = results.forExpression("LeftShift").value();
        assertEquals(value, Arrays.asList("B", "C", "D", null));
        value = results.forExpression("LeftShift2").value();
        assertEquals(value, Arrays.asList("B", "C", "D", null));

        value = results.forExpression("Multisource").value();
        assertTrue(value instanceof List);
        List<?> list = (List<?>) value;
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof Tuple);
        Tuple resultTuple = (Tuple) list.get(0);
        assertTrue(resultTuple.getElements().containsKey("A")
                && resultTuple.getElements().containsKey("B"));

        value = results.forExpression("Complex Multisource").value();
        assertTrue(value instanceof List);
        list = (List<?>) value;
        assertEquals(4, list.size());

        value = results.forExpression("Let Test Fails").value();

        value = results.forExpression("Triple Source Query").value();
        assertTrue(value instanceof List);
        list = (List<?>) value;
        assertEquals(27, list.size());

        value = results.forExpression("Let Expression in Multi Source Query").value();
        assertTrue(value instanceof List);
        list = (List<?>) value;
        assertEquals(1, list.size());
        assertTrue(EquivalentEvaluator.equivalent(list.get(0), 3));

        value = results.forExpression("Accessing Third Element of Triple Source Query")
                .value();
        assertTrue(value instanceof List);
        list = (List<?>) value;
        assertEquals(1, list.size());
        assertTrue(EquivalentEvaluator.equivalent(list.get(0), 3));
    }

    @Test
    void sort_by_fluent_function() {
        var results = engine.evaluate(toElmIdentifier("CqlQueryTests"));
        var value = results.forExpression("Sorted Tuples").value();
        assertInstanceOf(List.class, value);
        var list = (List<?>) value;
        assertEquals(3, list.size());
        assertTrue(list.get(0) instanceof Tuple);
        var tuple = (Tuple) list.get(0);
        assertTrue(tuple.getElement("x").equals(3));
        tuple = (Tuple) list.get(1);
        assertTrue(tuple.getElement("x").equals(2));
        tuple = (Tuple) list.get(2);
        assertTrue(tuple.getElement("x").equals(1));
    }
}
