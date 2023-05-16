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
        engineVisitor.getCache().setExpressionCaching(true);

        VersionedIdentifier libId = toElmIdentifier("ExpressionCacheTest");

        evaluationResult = engineVisitor.evaluate(libId, null, null, null, null, null);
        Object result;

        result = evaluationResult.expressionResults.get("Expression").value();
        assertNotNull(result);
        assertThat(result, is(5));

        // The included library also has a define called "Expression" Previously it'd return 5 since
        // the expressions were cached only by name. This is the behavior that was broken.
        Boolean enteredLibrary = engineVisitor.getState().enterLibrary("Common");
        VersionedIdentifier commonId = engineVisitor.getState().getCurrentLibrary().getIdentifier();

        result = engineVisitor.visitExpressionDef(engineVisitor.getState().resolveExpressionRef("Expression"), engineVisitor.getState());
        assertNotNull(result);
        assertThat(result, is(3));

        assertTrue(engineVisitor.getCache().isExpressionCached(commonId, "Expression"));

        engineVisitor.getState().exitLibrary(enteredLibrary);
        result = evaluationResult.expressionResults.get("Expression").value();
        assertNotNull(result);
        assertThat(result, is(5));
    }
}
