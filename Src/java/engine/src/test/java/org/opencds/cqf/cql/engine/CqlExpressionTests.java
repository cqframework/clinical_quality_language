package org.opencds.cqf.cql.engine;

import static org.opencds.cqf.cql.engine.execution.CqlEngine.ExpressionText.text;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.testng.annotations.Test;

public class CqlExpressionTests {
    @Test
    public void test() {
        var engine = new CqlEngine(new Environment(new LibraryManager(new ModelManager())));

        var results = engine
            .evaluate(text("define \"Ten\": 5 + 5\ndefine \"Eleven\": 5 + 6\n"));

        assertEquals(10, results.forExpression("Ten").value());
        assertEquals(11, results.forExpression("Eleven").value());

        var result = engine
            .evaluate(text("define \"return\": 5 + 5"))
            .forExpression("return")
            .value();

        assertEquals(10, result);
    }

    @Test
    public void testInvalid() {
        var engine = new CqlEngine(new Environment(new LibraryManager(new ModelManager())));
        assertThrows(IllegalArgumentException.class, () -> engine.evaluate(text("Errors are cool.")));
    }
}
