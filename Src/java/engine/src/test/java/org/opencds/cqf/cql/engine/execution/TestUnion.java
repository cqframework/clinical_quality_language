package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TestUnion extends CqlTestBase {

    @Test
    void union() {
        var results = engine.evaluate(toElmIdentifier("TestUnion"));

        var value = results.forExpression("NullAndNullList").value();
        assertNotNull(value);
        assertTrue(((List<?>) value).isEmpty());

        value = results.forExpression("NullAndNullInterval").value();
        assertNull(value);

        value = results.forExpression("NullAndNullUntyped").value();
        assertNull(value);

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
