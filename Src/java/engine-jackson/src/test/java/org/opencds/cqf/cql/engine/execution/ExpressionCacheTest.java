package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.testng.annotations.Test;

public class ExpressionCacheTest extends CqlExecutionTestBase {
    @Test
    public void testExpressionsCachedPerLibrary() {
        Context context = new Context(library);
        context.setExpressionCaching(true);
        context.registerLibraryLoader(new TestLibraryLoader(getLibraryManager()));

        // The top level library has a define called "Expression". Evaluating it caches the result.
        Object result = context.resolveExpressionRef("Expression").evaluate(context);
        assertNotNull(result);
        assertThat(result, is(5));

        assertTrue(context.isExpressionCached(library.getIdentifier(), "Expression"));

        // The included library also has a define called "Expression" Previously it'd return 5 since
        // the expressions were cached only by name. This is the behavior that was broken.
        Boolean enteredLibrary = context.enterLibrary("Common");
        VersionedIdentifier commonId = context.getCurrentLibrary().getIdentifier();
        result = context.resolveExpressionRef("Expression").evaluate(context);
        assertNotNull(result);
        assertThat(result, is(3));

        assertTrue(context.isExpressionCached(commonId, "Expression"));

        context.exitLibrary(enteredLibrary);
        result = context.resolveExpressionRef("Expression").evaluate(context);
        assertNotNull(result);
        assertThat(result, is(5));
    }
}