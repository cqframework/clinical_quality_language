package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlTranslatorOptions;

import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.comparesEqualTo;

public class EngineTests extends CqlTestBase {
    @Test
    public void test_all_evaluator() throws IOException {

        Environment environment = new Environment(getLibraryManager());
        CqlEngineVisitor engineVisitor = new CqlEngineVisitor(environment, null, null,null, CqlTranslatorOptions.defaultOptions());

        Set<String> set = new HashSet<>();
        set.add("AbsNull");
        set.add("Add11");
        set.add("TestAfterNull");
        set.add("AllTrueAllTrue");
        set.add("TrueAndTrue");
        set.add("AnyTrueAllTrue");
        set.add("AnyTrueAllFalse");
        set.add("AsQuantity");
        set.add("CastAsQuantity");
        set.add("AsDateTime");
        set.add("CeilingNull");
        set.add("Ceiling1D");
        set.add("Ceiling1D1");
        set.add("DivideNull");
        set.add("Divide10");
        set.add("Divide01");
        set.add("Divide11");
        set.add("FloorNull");
        set.add("Floor1");
        set.add("Floor1D");
        set.add("ExpNull");
        set.add("Exp0");
        set.add("ExpNeg0");

        set.add("HighBoundaryDec");
        set.add("HighBoundaryDate");
        set.add("HighBoundaryDateTime");

        set.add("LogNullNull");
        set.add("Log1BaseNull");
        set.add("Log1Base1");

        set.add("LowBoundaryDec");
        set.add("LowBoundaryDate");
        set.add("LowBoundaryDateTime");

//        set.add("LnNull");
//        set.add("Ln0");
//        set.add("LnNeg0");
//        set.add("Ln1");



        EvaluationResult result = engineVisitor.evaluate(toElmIdentifier("CqlAllInOne", "1"), set);
        assertThat(result.expressionResults.get("AllTrueAllTrue").value(), is(true));
        assertThat(result.expressionResults.get("AbsNull").value(), is(nullValue()));
        assertThat(result.expressionResults.get("Add11").value(), is(2));
        assertThat(result.expressionResults.get("TestAfterNull").value(), is(nullValue()));
        assertThat(result.expressionResults.get("TrueAndTrue").value(), is(true));
        assertThat(result.expressionResults.get("AnyTrueAllTrue").value(), is(true));
        assertThat(result.expressionResults.get("AnyTrueAllFalse").value(), is(false));

        Object obj = result.expressionResults.get("AsQuantity").value();
        Assert.assertTrue(((Quantity)obj).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        obj = result.expressionResults.get("CastAsQuantity").value();
        Assert.assertTrue(((Quantity)obj).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        obj = result.expressionResults.get("AsDateTime").value();
        Assert.assertTrue(((DateTime)obj).equal(new DateTime(null, 2014, 1, 1)));


        obj = result.expressionResults.get("CeilingNull").value();
        assertThat(obj, is(nullValue()));

        obj = result.expressionResults.get("Ceiling1D").value();
        assertThat(obj, is(1));

        obj = result.expressionResults.get("Ceiling1D1").value();
        assertThat(obj, is(2));


        obj = result.expressionResults.get("DivideNull").value();
        assertThat(obj, is(nullValue()));


        obj = result.expressionResults.get("Divide10").value();
        assertThat(obj, is(nullValue()));


        obj = result.expressionResults.get("Divide01").value();
        assertThat(((BigDecimal)obj), comparesEqualTo(new BigDecimal("0.0")));


        obj = result.expressionResults.get("Divide11").value();
        assertThat(((BigDecimal)obj), comparesEqualTo(new BigDecimal("1.0")));

        obj = result.expressionResults.get("FloorNull").value();
        assertThat(obj, is(nullValue()));

        obj = result.expressionResults.get("Floor1").value();
        assertThat(obj, is(1));

        obj = result.expressionResults.get("Floor1D").value();
        assertThat(obj, is(1));

        obj = result.expressionResults.get("ExpNull").value();
        assertThat(obj, is(nullValue()));

        obj = result.expressionResults.get("Exp0").value();
        assertThat(((BigDecimal)obj), comparesEqualTo(new BigDecimal("1.0")));

        obj = result.expressionResults.get("ExpNeg0").value();
        assertThat(((BigDecimal)obj), comparesEqualTo(new BigDecimal("1.0")));

        obj = result.expressionResults.get("HighBoundaryDec").value();
        assertThat(((BigDecimal)obj), comparesEqualTo(new BigDecimal("1.58799999")));


        obj = result.expressionResults.get("HighBoundaryDate").value();
        Assert.assertTrue(((Date)obj).equal(new Date(2014, 12)));

        obj = result.expressionResults.get("HighBoundaryDateTime").value();
        Assert.assertTrue(((DateTime)obj).equal(new DateTime(null, 2014, 1, 1, 8, 59, 59, 999)));


        obj = result.expressionResults.get("LogNullNull").value();
        assertThat(obj, is(nullValue()));

        obj = result.expressionResults.get("Log1BaseNull").value();
        assertThat(obj, is(nullValue()));

        obj = result.expressionResults.get("Log1Base1").value();
        assertThat(((BigDecimal)obj), comparesEqualTo(new BigDecimal(0d)));

        obj = result.expressionResults.get("LowBoundaryDec").value();
        assertThat(((BigDecimal) obj), comparesEqualTo((new BigDecimal("1.58700000"))));

        obj = result.expressionResults.get("LowBoundaryDate").value();
        Assert.assertTrue(((Date)obj).equal(new Date(2014, 1)));

        obj = result.expressionResults.get("LowBoundaryDateTime").value();
        Assert.assertTrue(((DateTime)obj).equal(new DateTime(null, 2014, 1, 1, 8, 0, 0, 0)));


//        obj = result.expressionResults.get("LnNull").value();
//        assertThat(obj, is(nullValue()));

//        try {
//            obj = result.expressionResults.get("Ln0").value();
//        } catch (UndefinedResult ae) {
//            assertThat(ae.getMessage(), is("Results in negative infinity"));
//        }
//
//        try {
//            obj = result.expressionResults.get("LnNeg0").value();
//        } catch (UndefinedResult ae) {
//            assertThat(ae.getMessage(), is("Results in negative infinity"));
//        }
//
//        obj = result.expressionResults.get("Ln1").value();
//        assertThat(((BigDecimal)obj), comparesEqualTo(new BigDecimal(0)));



//
//        obj = result.expressionResults.get("").value();
//        assertThat(obj, is(nullValue()));//
//
//        obj = result.expressionResults.get("").value();
//        assertThat(obj, is(nullValue()));
//
//        obj = result.expressionResults.get("").value();
//        assertThat(obj, is(nullValue()));//
//
//        obj = result.expressionResults.get("").value();
//        assertThat(obj, is(nullValue()));
//
//        obj = result.expressionResults.get("").value();
//        assertThat(obj, is(nullValue()));

    }


}

