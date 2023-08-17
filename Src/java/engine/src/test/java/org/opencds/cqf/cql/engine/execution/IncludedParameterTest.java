package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.HashSet;

import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.Test;

public class IncludedParameterTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("IncludedParameterTest");

    @Test
    public void test_gets_global_param_value() {
        var expressions = new HashSet<String>();
        expressions.add("Included Parameter");
        expressions.add("Local Parameter");

        var params = new HashMap<String, Object>();
        params.put("Measurement Period", 1);

        var result = engine.evaluate(library, expressions, params);
        // Parameter added as a global should affect all expressions
        assertEquals(result.forExpression("Included Parameter").value(), 1);
        assertEquals(result.forExpression("Local Parameter").value(), 1);
    }

    @Test
    public void test_local_param_value() {
        var expressions = new HashSet<String>();
        expressions.add("Included Parameter");
        expressions.add("Local Parameter");

        var params = new HashMap<String, Object>();
        params.put("IncludedParameterTest.Measurement Period", 1);

        var result = engine.evaluate(library, expressions, params);
        // Parameter added as a local should only impact the local value
        assertNull(result.forExpression("Included Parameter").value());
        assertEquals(result.forExpression("Local Parameter").value(), 1);
    }

    @Test
    public void test_include_param_value() {
        var expressions = new HashSet<String>();
        expressions.add("Included Parameter");
        expressions.add("Local Parameter");

        var params = new HashMap<String, Object>();
        params.put("IncludedParameterTestCommon.Measurement Period", 1);

        var result = engine.evaluate(library, expressions, params);
        // Parameter added as a local should only impact the local value
        assertNull(result.forExpression("Local Parameter").value());
        assertEquals(result.forExpression("Included Parameter").value(), 1);
    }

    @Test
    public void test_local_override_param_value() {

        var expressions = new HashSet<String>();
        expressions.add("Included Parameter");
        expressions.add("Local Parameter");

        var params = new HashMap<String, Object>();
        params.put("Measurement Period", 2);
        params.put("IncludedParameterTestCommon.Measurement Period", 1);

        var result = engine.evaluate(library, expressions, params);
        // If a library-specific parameter is not specified, the global
        // value should be used
        assertEquals(result.forExpression("Local Parameter").value(), 2);
        assertEquals(result.forExpression("Included Parameter").value(), 1);
    }

}
