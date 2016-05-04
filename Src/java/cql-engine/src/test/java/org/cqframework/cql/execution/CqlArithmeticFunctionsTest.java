package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlArithmeticFunctionsTest extends CqlExecutionTestBase {
    /**
     * {@link org.cqframework.cql.elm.execution.Abs#evaluate(Context)}
     */
    @Test
    public void testAbs() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "AbsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Abs0").getExpression().evaluate(context);
        assertThat(result, is(new Integer(0)));

        result = context.resolveExpressionRef(library, "AbsNeg1").getExpression().evaluate(context);
        assertThat(result, is(new Integer(1)));

        result = context.resolveExpressionRef(library, "AbsNeg1Dec").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Abs0Dec").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        //TODO: Un-comment this once Quantity can be added.
//        result = context.resolveExpressionRef(library, "Add1D1D").getExpression().evaluate(context);
//        assertThat(result, is(new BigDecimal("2.0")));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Add#evaluate(Context)}
     */
    @Test
    public void testAdd() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "Add11").getExpression().evaluate(context);
        assertThat(result, is(new Integer(2)));

        result = context.resolveExpressionRef(library, "Add1D1D").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("2.0")));

        //TODO: Un-comment this once Quantity can be added.
//        result = context.resolveExpressionRef(library, "Add1Q1Q").getExpression().evaluate(context);
//        assertThat(result, is(new BigDecimal("2")));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Ceiling#evaluate(Context)}
     */
    @Test
    public void testCeiling() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "Ceiling1").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Ceiling1D").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Ceiling1D1").getExpression().evaluate(context);
        assertThat(result, is(new Double(2)));

        result = context.resolveExpressionRef(library, "CeilingNegD1").getExpression().evaluate(context);
        assertThat(result, is(-(new Double(0))));

        result = context.resolveExpressionRef(library, "CeilingNeg1").getExpression().evaluate(context);
        assertThat(result, is(new Double(-1)));

        result = context.resolveExpressionRef(library, "CeilingNeg1D1").getExpression().evaluate(context);
        assertThat(result, is(new Double(-1)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Divide#evaluate(Context)}
     */
    @Test
    public void testDivide() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "Divide10").getExpression().evaluate(context);
        assertThat(result, is(Double.POSITIVE_INFINITY));

        result = context.resolveExpressionRef(library, "Divide01").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Divide11").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Divide1d1d").getExpression().evaluate(context);
        assertThat(result, is(new Integer(1)));

        //TODO: Un-comment this once Quantity can be added.
//        result = context.resolveExpressionRef(library, "Divide1Q1").getExpression().evaluate(context);
//        assertThat(result, is(new Double(1)));
//
//        result = context.resolveExpressionRef(library, "Divide1Q1Q").getExpression().evaluate(context);
//        assertThat(result, is(new Double(1)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Floor#evaluate(Context)}
     */
    @Test
    public void testFloor() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "Floor1").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Floor1D").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Floor1D1").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "FloorNegD1").getExpression().evaluate(context);
        assertThat(result, is((new Double(-1))));

        result = context.resolveExpressionRef(library, "FloorNeg1").getExpression().evaluate(context);
        assertThat(result, is(new Double(-1)));

        result = context.resolveExpressionRef(library, "FloorNeg1D1").getExpression().evaluate(context);
        assertThat(result, is(new Double(-2)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Exp#evaluate(Context)}
     */
    @Test
    public void testExp() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Log#evaluate(Context)}
     */
    @Test
    public void testLog() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Ln#evaluate(Context)}
     */
    @Test
    public void testLn() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Max#evaluate(Context)}
     */
    @Test
    public void testMaximum() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Min#evaluate(Context)}
     */
    @Test
    public void testMinimum() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Modulo#evaluate(Context)}
     */
    @Test
    public void testModulo() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Multiply#evaluate(Context)}
     */
    @Test
    public void testMultiply() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "MultiplyAB").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("50.00")));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Negate#evaluate(Context)}
     */
    @Test
    public void testNegate() throws JAXBException {
    }

    Context context = new Context(library);
    Object result;

    /**
     * {@link org.cqframework.cql.elm.execution.Predecessor#evaluate(Context)}
     */
    @Test
    public void testPredecessor() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Power#evaluate(Context)}
     */
    @Test
    public void testPower() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Round#evaluate(Context)}
     */
    @Test
    public void testRound() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Subtract#evaluate(Context)}
     */
    @Test
    public void testSubtract() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "SubtractAB").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("5.0")));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Successor#evaluate(Context)}
     */
    @Test
    public void testSuccessor() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Truncate#evaluate(Context)}
     */
    @Test
    public void testTruncate() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.TruncatedDivide#evaluate(Context)}
     */
    @Test
    public void testTruncatedDivide() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }
}
