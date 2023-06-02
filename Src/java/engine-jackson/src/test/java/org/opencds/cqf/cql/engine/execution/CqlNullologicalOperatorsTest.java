package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlNullologicalOperatorsTest extends CqlExecutionTestBase {

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.CoalesceEvaluator#evaluate(Context)}
     */
    @Test
    public void testCoalesce() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("CoalesceANull").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef("CoalesceNullA").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef("CoalesceEmptyList").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("CoalesceListFirstA").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef("CoalesceListLastA").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef("CoalesceFirstList").getExpression().evaluate(context);
        assertThat(result, is(Collections.singletonList("a")));

        result = context.resolveExpressionRef("CoalesceLastList").getExpression().evaluate(context);
        assertThat(result, is(Collections.singletonList("a")));

        result = context.resolveExpressionRef("DateTimeCoalesce").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 5, 18)));

        result = context.resolveExpressionRef("DateTimeListCoalesce").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 5, 18)));

        result = context.resolveExpressionRef("TimeCoalesce").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 15, 33, 556)));

        result = context.resolveExpressionRef("TimeListCoalesce").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 15, 33, 556)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IsNullEvaluator#evaluate(Context)}
     */
    @Test
    public void testIsNull() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IsNullTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IsNullFalseEmptyString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IsNullAlsoFalseAbcString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IsNullAlsoFalseNumber1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IsNullAlsoFalseNumberZero").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IsFalseEvaluator#evaluate(Context)}
     */
    @Test
    public void testIsFalse() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IsFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IsFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IsFalseNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IsTrueEvaluator#evaluate(Context)}
     */
    @Test
    public void testIsTrue() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IsTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IsTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IsTrueNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }
}
