package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

public class CqlConditionalOperatorsTest extends CqlExecutionTestBase {

  /**
   * {@link org.opencds.cqf.cql.engine.elm.execution.IfEvaluator#evaluate(Context)}
   */
  @Test
  public void testIfThenElse() {
    Context context = new Context(library);

    Object result = context.resolveExpressionRef("IfTrue1").getExpression().evaluate(context);
    assertThat(result, is(5));

    result = context.resolveExpressionRef("IfFalse1").getExpression().evaluate(context);
    assertThat(result, is(5));

    result = context.resolveExpressionRef("IfNull1").getExpression().evaluate(context);
    assertThat(result, is(10));
  }

  /**
   * {@link org.opencds.cqf.cql.engine.elm.execution.CaseEvaluator#evaluate(Context)}
   */
  @Test
  public void testStandardCase() {
    Context context = new Context(library);

    Object result = context.resolveExpressionRef("StandardCase1").getExpression().evaluate(context);
    assertThat(result, is(5));

    result = context.resolveExpressionRef("StandardCase2").getExpression().evaluate(context);
    assertThat(result, is(5));

    result = context.resolveExpressionRef("StandardCase3").getExpression().evaluate(context);
    assertThat(result, is(15));
  }

  /**
   * {@link org.opencds.cqf.cql.engine.elm.execution.CaseEvaluator#evaluate(Context)}
   */
  @Test
  public void testSelectedCase() {
    Context context = new Context(library);

    Object result = context.resolveExpressionRef("SelectedCase1").getExpression().evaluate(context);
    assertThat(result, is(12));

    result = context.resolveExpressionRef("SelectedCase2").getExpression().evaluate(context);
    assertThat(result, is(15));

    result = context.resolveExpressionRef("SelectedCase3").getExpression().evaluate(context);
    assertThat(result, is(5));
  }

}
