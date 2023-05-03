package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlTranslatorOptions;

import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class EngineTests extends CqlTestBase {
    @Test
    public void test_all_evaluator() throws IOException {

        Environment environment = new Environment(getLibraryManager());
        CqlEngineVisitor engineVisitor = new CqlEngineVisitor(environment, null, null,null, CqlTranslatorOptions.defaultOptions());

        Set<String> set = new HashSet<>();
//        set.add("AbsNull");
//        set.add("Add11");
//        set.add("TestAfterNull");
//        set.add("AllTrueAllTrue");
//        set.add("TrueAndTrue");
//        set.add("AnyTrueAllTrue");
//        set.add("AnyTrueAllFalse");
        set.add("AsQuantity");
//        set.add("CastAsQuantity");
//        set.add("AsDateTime");


        EvaluationResult result = engineVisitor.evaluate(toElmIdentifier("CqlAllInOne", "1"), set);
//        assertThat(result.expressionResults.get("AllTrueAllTrue").value(), is(true));
//        assertThat(result.expressionResults.get("AbsNull").value(), is(nullValue()));
//        assertThat(result.expressionResults.get("Add11").value(), is(2));
//        assertThat(result.expressionResults.get("TestAfterNull").value(), is(nullValue()));
//        assertThat(result.expressionResults.get("TrueAndTrue").value(), is(true));
//        assertThat(result.expressionResults.get("AnyTrueAllTrue").value(), is(true));
//        assertThat(result.expressionResults.get("AnyTrueAllFalse").value(), is(false));

        Object obj = result.expressionResults.get("AsQuantity").value();
        Assert.assertTrue(((Quantity)obj).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

//        result = context.resolveExpressionRef("CastAsQuantity").getExpression().evaluate(context);
//        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));
//
//        result = context.resolveExpressionRef("AsDateTime").getExpression().evaluate(context);
//        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));

    }


}

