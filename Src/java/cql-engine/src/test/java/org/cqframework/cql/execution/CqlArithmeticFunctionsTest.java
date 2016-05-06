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

        result = context.resolveExpressionRef(library, "AbsEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

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

        result = context.resolveExpressionRef(library, "AddEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "AddNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

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

        result = context.resolveExpressionRef(library, "CeilingEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "CeilingNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

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

        result = context.resolveExpressionRef(library, "DivideEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "DivideNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Divide10").getExpression().evaluate(context);
        assertThat(result, is(Double.POSITIVE_INFINITY));

        result = context.resolveExpressionRef(library, "Divide01").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Divide11").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Divide1d1d").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal(1)));

        result = context.resolveExpressionRef(library, "Divide103").getExpression().evaluate(context);
        assertThat(result, is(new Double(10) / new Double(3)));

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

        result = context.resolveExpressionRef(library, "FloorEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "FloorNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

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

        result = context.resolveExpressionRef(library, "ExpEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "ExpNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Exp0").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "ExpNeg0").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Exp1").getExpression().evaluate(context);
        assertThat(result, is(Math.exp(new Double(1))));

        result = context.resolveExpressionRef(library, "ExpNeg1").getExpression().evaluate(context);
        assertThat(result, is(Math.exp(new Double(-1))));

        result = context.resolveExpressionRef(library, "Exp1000").getExpression().evaluate(context);
        assertThat(result, is(Double.POSITIVE_INFINITY));

        result = context.resolveExpressionRef(library, "Exp1000D").getExpression().evaluate(context);
        assertThat(result, is(Double.POSITIVE_INFINITY));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Log#evaluate(Context)}
     */
    @Test
    public void testLog() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "LogEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "LogEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "LogNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Log1BaseNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Log1Base1").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Log1Base2").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Log1Base100").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Log16Base2").getExpression().evaluate(context);
        assertThat(result, is(new Double(4)));

        result = context.resolveExpressionRef(library, "LogD125Base2").getExpression().evaluate(context);
        assertThat(result, is(new Double(-3)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Ln#evaluate(Context)}
     */
    @Test
    public void testLn() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "LnEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "LnNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Ln0").getExpression().evaluate(context);
        assertThat(result, is(Double.NEGATIVE_INFINITY));

        result = context.resolveExpressionRef(library, "LnNeg0").getExpression().evaluate(context);
        assertThat(result, is(Double.NEGATIVE_INFINITY));

        result = context.resolveExpressionRef(library, "Ln1").getExpression().evaluate(context);
        assertThat(result, is(Math.log(new Double(1))));

        result = context.resolveExpressionRef(library, "LnNeg1").getExpression().evaluate(context);
        assertThat(result, is(Math.log(new Double(-1))));

        result = context.resolveExpressionRef(library, "Ln1000").getExpression().evaluate(context);
        assertThat(result, is(Math.log(new Integer(1000))));

        result = context.resolveExpressionRef(library, "Ln1000D").getExpression().evaluate(context);
        assertThat(result, is(Math.log(new Double(1000))));
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

        result = context.resolveExpressionRef(library, "ModuloEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "ModuloNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Modulo0By0").getExpression().evaluate(context);
        assertThat(result, is(Double.NaN));

        result = context.resolveExpressionRef(library, "Modulo4By2").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Modulo4DBy2D").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Modulo10By3").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Modulo10DBy3D").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        //TODO: Un-comment this once Quantity can be added.
//        result = context.resolveExpressionRef(library, "Mode4CMBy2CM").getExpression().evaluate(context);
//        assertThat(result, is(new Double(0)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Multiply#evaluate(Context)}
     */
    @Test
    public void testMultiply() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "MultiplyEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "MultiplyNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Multiply1By1").getExpression().evaluate(context);
        assertThat(result, is(new Integer(1)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Negate#evaluate(Context)}
     */
    @Test
    public void testNegate() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "NegateEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "NegateNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Negate0").getExpression().evaluate(context);
        assertThat(result, is(new Integer(0)));

        result = context.resolveExpressionRef(library, "NegateNeg0").getExpression().evaluate(context);
        assertThat(result, is(new Integer(0)));

        result = context.resolveExpressionRef(library, "Negate1").getExpression().evaluate(context);
        assertThat(result, is(new Integer(-1)));

        result = context.resolveExpressionRef(library, "NegateNeg1").getExpression().evaluate(context);
        assertThat(result, is(new Integer(1)));

        result = context.resolveExpressionRef(library, "Negate0D").getExpression().evaluate(context);
        assertThat(result, is(-(new Double(0))));

        result = context.resolveExpressionRef(library, "NegateNeg0D").getExpression().evaluate(context);
        assertThat(result, is(new Double(0)));

        result = context.resolveExpressionRef(library, "Negate1D").getExpression().evaluate(context);
        assertThat(result, is(new Double(-1)));

        result = context.resolveExpressionRef(library, "NegateNeg1D").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));

        result = context.resolveExpressionRef(library, "Negate1CM").getExpression().evaluate(context);
        assertThat(((org.cqframework.cql.runtime.Quantity) result).getValue(), is(new BigDecimal(1).negate()));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Predecessor#evaluate(Context)}
     */
    @Test
    public void testPredecessor() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "PredecessorEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "PredecessorNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "PredecessorOf0").getExpression().evaluate(context);
        assertThat(result, is(new Integer(-1)));

        result = context.resolveExpressionRef(library, "PredecessorOf1").getExpression().evaluate(context);
        assertThat(result, is(new Integer(0)));

        result = context.resolveExpressionRef(library, "PredecessorOf1D").getExpression().evaluate(context);
        assertThat(result, is(Math.nextDown(new Double(1))));

        result = context.resolveExpressionRef(library, "PredecessorOf101D").getExpression().evaluate(context);
        assertThat(result, is(Math.nextDown(new Double(1.01))));

        result = context.resolveExpressionRef(library, "PredecessorOfJan12000").getExpression().evaluate(context);
        assertThat(result, is(""));

        result = context.resolveExpressionRef(library, "PredecessorOfNoon").getExpression().evaluate(context);
        assertThat(result, is(new Double(1)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Power#evaluate(Context)}
     */
    @Test
    public void testPower() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "PowerEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "PowerNullToNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Power0To0").getExpression().evaluate(context);
        assertThat(result, is(new Integer(1)));

        result = context.resolveExpressionRef(library, "Power2To2").getExpression().evaluate(context);
        assertThat(result, is(new Integer(4)));

        result = context.resolveExpressionRef(library, "PowerNeg2To2").getExpression().evaluate(context);
        assertThat(result, is(new Integer(4)));

        result = context.resolveExpressionRef(library, "Power2ToNeg2").getExpression().evaluate(context);
        assertThat(result, is(new Double(.25)));

        result = context.resolveExpressionRef(library, "Power2DTo2D").getExpression().evaluate(context);
        assertThat(result, is(new Double(4)));

        result = context.resolveExpressionRef(library, "PowerNeg2DTo2D").getExpression().evaluate(context);
        assertThat(result, is(new Double(4)));

        result = context.resolveExpressionRef(library, "Power2DToNeg2D").getExpression().evaluate(context);
        assertThat(result, is(new Double(.25)));

        result = context.resolveExpressionRef(library, "Power2DTo2").getExpression().evaluate(context);
        assertThat(result, is(new Double(4)));

        result = context.resolveExpressionRef(library, "Power2To2D").getExpression().evaluate(context);
        assertThat(result, is(new Double(4)));
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

        result = context.resolveExpressionRef(library, "SubtractEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SubtractNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Subtract1And1").getExpression().evaluate(context);
        assertThat(result, is(new Integer(0)));
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
