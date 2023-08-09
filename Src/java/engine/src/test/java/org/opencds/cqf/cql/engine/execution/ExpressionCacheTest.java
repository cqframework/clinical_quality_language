package org.opencds.cqf.cql.engine.execution;


import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


public class ExpressionCacheTest extends CqlTestBase {

    @Test
    public void test_expression_cache() {

        EvaluationResult evaluationResult;
        engine.getCache().setExpressionCaching(true);

        VersionedIdentifier libId = toElmIdentifier("ExpressionCacheTest");

        evaluationResult = engine.evaluate(libId);
        Object result;

        result = evaluationResult.forExpression("Expression").value();
        assertNotNull(result);
        assertThat(result, is(5));

        // The included library also has a define called "Expression" Previously it'd return 5 since
        // the expressions were cached only by name. This is the behavior that was broken.
        Boolean enteredLibrary = engine.getState().enterLibrary("Common");
        VersionedIdentifier commonId = engine.getState().getCurrentLibrary().getIdentifier();

        result = engine.getEvaluationVisitor().visitExpressionDef(Libraries.resolveExpressionRef("Expression", engine.getState().getCurrentLibrary()), engine.getState());
        assertNotNull(result);
        assertThat(result, is(3));

        assertTrue(engine.getCache().isExpressionCached(commonId, "Expression"));

        engine.getState().exitLibrary(enteredLibrary);
        result = evaluationResult.forExpression("Expression").value();
        assertNotNull(result);
        assertThat(result, is(5));
    }
}
