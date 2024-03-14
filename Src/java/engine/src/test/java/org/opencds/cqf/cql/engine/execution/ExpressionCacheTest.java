package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.Test;

@SuppressWarnings("removal")
public class ExpressionCacheTest extends CqlTestBase {

    @Test
    public void test_expression_cache() {

        engine.getCache().setExpressionCaching(true);

        VersionedIdentifier libId = toElmIdentifier("ExpressionCacheTest");

        var results = engine.evaluate(libId);
        var value = results.forExpression("Expression").value();
        assertNotNull(value);
        assertThat(value, is(5));

        // The included library also has a define called "Expression" Previously it'd return 5 since
        // the expressions were cached only by name. This is the behavior that was broken.
        Boolean enteredLibrary = engine.getState().enterLibrary("Common");
        VersionedIdentifier commonId = engine.getState().getCurrentLibrary().getIdentifier();

        value = engine.getEvaluationVisitor()
                .visitExpressionDef(
                        Libraries.resolveExpressionRef(
                                "Expression", engine.getState().getCurrentLibrary()),
                        engine.getState());
        assertNotNull(value);
        assertThat(value, is(3));

        assertTrue(engine.getCache().isExpressionCached(commonId, "Expression"));

        engine.getState().exitLibrary(enteredLibrary);
        value = results.forExpression("Expression").value();
        assertNotNull(value);
        assertThat(value, is(5));
    }
}
