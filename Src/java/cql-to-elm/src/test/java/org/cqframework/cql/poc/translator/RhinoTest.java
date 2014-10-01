package org.cqframework.cql.poc.translator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RhinoTest {
    @Test
    public void testRhinoWorks() {
        Context cx = Context.enter();
        try {
            Scriptable scope = cx.initStandardObjects();
            String command = "1+1";
            Object result = cx.evaluateString(scope, command, "<cmd>", 1, null);
            assertThat(Context.toString(result), is("2"));
        } finally {
            Context.exit();
        }
    }
}
