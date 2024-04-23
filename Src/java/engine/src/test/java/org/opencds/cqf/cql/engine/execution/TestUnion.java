package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import org.testng.annotations.Test;

public class TestUnion extends CqlTestBase {

    @Test
    public void testUnion() {
        var results = engine.evaluate(toElmIdentifier("TestUnion"));
        var value = results.forExpression("NullAndNull").value();
        assertNotNull(value);
        assertTrue(((List<?>) value).isEmpty());

        value = results.forExpression("NullAndEmpty").value();
        assertNotNull(value);
        assertTrue(((List<?>) value).isEmpty());

        value = results.forExpression("EmptyAndNull").value();
        assertNotNull(value);
        assertTrue(((List<?>) value).isEmpty());

        value = results.forExpression("NullAndSingle").value();
        assertNotNull(value);
        assertEquals(1, ((List<?>) value).size());
        assertEquals(1, ((List<?>) value).get(0));

        value = results.forExpression("SingleAndNull").value();
        assertNotNull(value);
        assertEquals(1, ((List<?>) value).size());
        assertEquals(1, ((List<?>) value).get(0));
    }
}
