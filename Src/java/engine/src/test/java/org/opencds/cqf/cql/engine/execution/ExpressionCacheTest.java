package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("removal")
class ExpressionCacheTest extends CqlTestBase {

    @Test
    void expression_cache() {

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
        final var state = engine.getState();
        final var definition =
                Libraries.resolveExpressionRef("Expression", engine.getState().getCurrentLibrary());
        state.beginEvaluation();
        state.pushActivationFrame(definition);
        try {
            value = engine.getEvaluationVisitor().visitExpressionDef(definition, engine.getState());
        } finally {
            state.popActivationFrame();
            state.endEvaluation();
        }
        assertNotNull(value);
        assertThat(value, is(3));

        assertTrue(engine.getCache().isExpressionCached(commonId, "Expression"));

        engine.getState().exitLibrary(enteredLibrary);
        value = results.forExpression("Expression").value();
        assertNotNull(value);
        assertThat(value, is(5));
    }
}
