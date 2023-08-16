package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;


public class TestUnion extends CqlTestBase {

    @Test
    public void testUnion() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("TestUnion"));
        Object result = evaluationResult.forExpression("NullAndNull").value();
        assertNotNull(result);
        assertTrue(((List<?>)result).isEmpty());

        result = evaluationResult.forExpression("NullAndEmpty").value();
        assertNotNull(result);
        assertTrue(((List<?>)result).isEmpty());

        result = evaluationResult.forExpression("EmptyAndNull").value();
        assertNotNull(result);
        assertTrue(((List<?>)result).isEmpty());

        result = evaluationResult.forExpression("NullAndSingle").value();
        assertNotNull(result);
        assertEquals(1, ((List<?>)result).size());
        assertEquals(1, ((List<?>)result).get(0));

        result = evaluationResult.forExpression("SingleAndNull").value();
        assertNotNull(result);
        assertEquals(1, ((List<?>)result).size());
        assertEquals(1, ((List<?>)result).get(0));
    }
}
