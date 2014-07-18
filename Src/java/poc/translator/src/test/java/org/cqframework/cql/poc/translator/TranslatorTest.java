package org.cqframework.cql.poc.translator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TranslatorTest {
    @Test
    public void testRhinoWorks() {
        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();
            String command = "1";
            Object result = cx.evaluateString(scope, command, "<cmd>", 1, null);
            assertEquals(Context.toString(result), command);
        } finally {
            Context.exit();
        }
    }
}
