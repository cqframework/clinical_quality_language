package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import static org.cqframework.cql.cql2elm.matchers.ConvertsToDecimalFrom.convertsToDecimalFromAlias;
import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AggregateOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        CqlTranslator translator = CqlTranslator.fromStream(AggregateOperatorsTest.class.getResourceAsStream("../OperatorTests/AggregateOperators.cql"));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testAllTrue() {
        ExpressionDef def = defs.get("AllTrueExpression");
        assertThat(def, hasTypeAndResult(AllTrue.class, "System.Boolean"));

        AllTrue exp = (AllTrue) def.getExpression();
        assertThat(exp.getSource(), instanceOf(List.class));

        List args = (List) exp.getSource();
        assertThat(args.getElement(), hasSize(3));
        for (Expression arg : args.getElement()) {
            assertThat(arg, literalFor(Boolean.TRUE));
        }
    }

    @Test
    public void testAnyTrue() {
        ExpressionDef def = defs.get("AnyTrueExpression");
        assertThat(def, hasTypeAndResult(AnyTrue.class, "System.Boolean"));

        AnyTrue exp = (AnyTrue) def.getExpression();
        assertThat(exp.getSource(), instanceOf(List.class));

        List args = (List) exp.getSource();
        assertThat(args.getElement(), hasSize(3));
        assertThat(args.getElement().get(0), literalFor(Boolean.FALSE));
        assertThat(args.getElement().get(1), literalFor(Boolean.TRUE));
        assertThat(args.getElement().get(2), literalFor(Boolean.FALSE));
    }

    @Test
    public void testAverage() {
        ExpressionDef def = defs.get("IntegerAvg");
        assertThat(def, hasTypeAndResult(Avg.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((Avg) def.getExpression()).getSource());

        def = defs.get("DecimalAvg");
        assertThat(def, hasTypeAndResult(Avg.class, "System.Decimal"));
        assertDecimalListOneToFour(((Avg) def.getExpression()).getSource());

        def = defs.get("QuantityAvg");
        assertThat(def, hasTypeAndResult(Avg.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Avg) def.getExpression()).getSource());
    }

    @Test
    public void testCount() {
        ExpressionDef def = defs.get("CountExpression");
        assertThat(def, hasTypeAndResult(Count.class, "System.Integer"));
        assertIntegerListOneToFive(((Count) def.getExpression()).getSource());
    }

    @Test
    public void testMax() {
        ExpressionDef def = defs.get("IntegerMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.Integer"));
        assertIntegerListOneToFive(((Max) def.getExpression()).getSource());

        def = defs.get("DecimalMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.Decimal"));
        assertDecimalListOneToFour(((Max) def.getExpression()).getSource());

        def = defs.get("QuantityMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Max) def.getExpression()).getSource());

        def = defs.get("DateTimeMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.DateTime"));
        assertDateTimeListJanOne2012to2015(((Max) def.getExpression()).getSource());

        def = defs.get("TimeMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.Time"));
        assertTime0to18everySixHours(((Max) def.getExpression()).getSource());

        def = defs.get("StringMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.String"));
        assertStringAtoE(((Max) def.getExpression()).getSource());
    }

    @Test
    public void testMin() {
        ExpressionDef def = defs.get("IntegerMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.Integer"));
        assertIntegerListOneToFive(((Min) def.getExpression()).getSource());

        def = defs.get("DecimalMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.Decimal"));
        assertDecimalListOneToFour(((Min) def.getExpression()).getSource());

        def = defs.get("QuantityMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Min) def.getExpression()).getSource());

        def = defs.get("DateTimeMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.DateTime"));
        assertDateTimeListJanOne2012to2015(((Min) def.getExpression()).getSource());

        def = defs.get("TimeMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.Time"));
        assertTime0to18everySixHours(((Min) def.getExpression()).getSource());

        def = defs.get("StringMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.String"));
        assertStringAtoE(((Min) def.getExpression()).getSource());
    }

    @Test
    public void testMedian() {
        ExpressionDef def = defs.get("IntegerMedian");
        assertThat(def, hasTypeAndResult(Median.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((Median) def.getExpression()).getSource());

        def = defs.get("DecimalMedian");
        assertThat(def, hasTypeAndResult(Median.class, "System.Decimal"));
        assertDecimalListOneToFour(((Median) def.getExpression()).getSource());

        def = defs.get("QuantityMedian");
        assertThat(def, hasTypeAndResult(Median.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Median) def.getExpression()).getSource());
    }

    @Test
    public void testMode() {
        ExpressionDef def = defs.get("IntegerMode");
        assertThat(def, hasTypeAndResult(Mode.class, "System.Integer"));
        assertIntegerListOneToFive(((Mode) def.getExpression()).getSource());

        def = defs.get("DecimalMode");
        assertThat(def, hasTypeAndResult(Mode.class, "System.Decimal"));
        assertDecimalListOneToFour(((Mode) def.getExpression()).getSource());
    }

    @Test
    public void testPopulationStdDev() {
        ExpressionDef def = defs.get("IntegerPopulationStdDev");
        assertThat(def, hasTypeAndResult(PopulationStdDev.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((PopulationStdDev) def.getExpression()).getSource());

        def = defs.get("DecimalPopulationStdDev");
        assertThat(def, hasTypeAndResult(PopulationStdDev.class, "System.Decimal"));
        assertDecimalListOneToFour(((PopulationStdDev) def.getExpression()).getSource());

        def = defs.get("QuantityPopulationStdDev");
        assertThat(def, hasTypeAndResult(PopulationStdDev.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((PopulationStdDev) def.getExpression()).getSource());
    }

    @Test
    public void testPopulationVariance() {
        ExpressionDef def = defs.get("IntegerPopulationVariance");
        assertThat(def, hasTypeAndResult(PopulationVariance.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((PopulationVariance) def.getExpression()).getSource());

        def = defs.get("DecimalPopulationVariance");
        assertThat(def, hasTypeAndResult(PopulationVariance.class, "System.Decimal"));
        assertDecimalListOneToFour(((PopulationVariance) def.getExpression()).getSource());

        def = defs.get("QuantityPopulationVariance");
        assertThat(def, hasTypeAndResult(PopulationVariance.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((PopulationVariance) def.getExpression()).getSource());
    }

    @Test
    public void testStdDev() {
        ExpressionDef def = defs.get("IntegerStdDev");
        assertThat(def, hasTypeAndResult(StdDev.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((StdDev) def.getExpression()).getSource());

        def = defs.get("DecimalStdDev");
        assertThat(def, hasTypeAndResult(StdDev.class, "System.Decimal"));
        assertDecimalListOneToFour(((StdDev) def.getExpression()).getSource());

        def = defs.get("QuantityStdDev");
        assertThat(def, hasTypeAndResult(StdDev.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((StdDev) def.getExpression()).getSource());
    }

    @Test
    public void testSum() {
        ExpressionDef def = defs.get("IntegerSum");
        assertThat(def, hasTypeAndResult(Sum.class, "System.Integer"));
        assertIntegerListOneToFive(((Sum) def.getExpression()).getSource());

        def = defs.get("DecimalSum");
        assertThat(def, hasTypeAndResult(Sum.class, "System.Decimal"));
        assertDecimalListOneToFour(((Sum) def.getExpression()).getSource());

        def = defs.get("QuantitySum");
        assertThat(def, hasTypeAndResult(Sum.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Sum) def.getExpression()).getSource());
    }

    @Test
    public void testVariance() {
        ExpressionDef def = defs.get("IntegerVariance");
        assertThat(def, hasTypeAndResult(Variance.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((Variance) def.getExpression()).getSource());

        def = defs.get("DecimalVariance");
        assertThat(def, hasTypeAndResult(Variance.class, "System.Decimal"));
        assertDecimalListOneToFour(((Variance) def.getExpression()).getSource());

        def = defs.get("QuantityVariance");
        assertThat(def, hasTypeAndResult(Variance.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Variance) def.getExpression()).getSource());
    }

    private void assertDecimalConversionForIntegerListOneToFive(Expression source) {
        assertThat(source, instanceOf(Query.class));

        Query q = (Query) source;
        assertThat(q.getSource(), hasSize(1));
        assertThat(q.getDefine(), hasSize(0));
        assertThat(q.getRelationship(), hasSize(0));
        assertThat(q.getSort(), nullValue());
        assertThat(q.getWhere(), nullValue());
        AliasedQuerySource aqs = q.getSource().get(0);
        assertIntegerListOneToFive(aqs.getExpression());
        String alias = aqs.getAlias();
        assertThat(q.getReturn().isDistinct(), is(false));
        assertThat(q.getReturn().getExpression(), instanceOf(FunctionRef.class));
        assertThat(q.getReturn().getExpression(), convertsToDecimalFromAlias(alias));
    }

    private void assertIntegerListOneToFive(Expression source) {
        assertThat(source, instanceOf(List.class));

        List args = (List) source;
        assertThat(args.getElement(), hasSize(5));
        int i = 1;
        for (Expression arg : args.getElement()) {
            assertThat(arg, literalFor(i++));
        }
    }

    private void assertDecimalListOneToFour(Expression source) {
        assertThat(source, instanceOf(List.class));

        List args = (List) source;
        assertThat(args.getElement(), hasSize(4));
        int i = 1;
        for (Expression arg : args.getElement()) {
            assertThat(arg, literalFor((double) i++));
        }
    }

    private void assertQuantityListOneMeterToFourMeters(Expression source) {
        assertThat(source, instanceOf(List.class));

        List args = (List) source;
        assertThat(args.getElement(), hasSize(4));
        int i = 1;
        for (Expression arg : args.getElement()) {
            assertThat(arg, instanceOf(Quantity.class));
            Quantity q = (Quantity) arg;
            assertThat(q.getValue().intValueExact(), is(i++));
            assertThat(q.getUnit(), is("m"));
        }
    }

    private void assertDateTimeListJanOne2012to2015(Expression source) {
        assertThat(source, instanceOf(List.class));

        List args = (List) source;
        assertThat(args.getElement(), hasSize(4));
        int i = 2012;
        for (Expression arg : args.getElement()) {
            assertThat(arg, instanceOf(DateTime.class));
            DateTime d = (DateTime) arg;
            assertThat(d.getYear(), literalFor(i++));
            assertThat(d.getMonth(), literalFor(1));
            assertThat(d.getDay(), literalFor(1));
            assertThat(d.getHour(), literalFor(0));
            assertThat(d.getMinute(), literalFor(0));
            assertThat(d.getSecond(), literalFor(0));
            assertThat(d.getMillisecond(), nullValue());
        }
    }

    private void assertTime0to18everySixHours(Expression source) {
        assertThat(source, instanceOf(List.class));

        List args = (List) source;
        assertThat(args.getElement(), hasSize(4));
        int i = 0;
        for (Expression arg : args.getElement()) {
            assertThat(arg, instanceOf(Time.class));
            Time t = (Time) arg;
            assertThat(t.getHour(), literalFor(i));
            assertThat(t.getMinute(), literalFor(0));
            assertThat(t.getSecond(), literalFor(0));
            assertThat(t.getMillisecond(), nullValue());
            i += 6;
        }
    }

    private void assertStringAtoE(Expression source) {
        assertThat(source, instanceOf(List.class));

        List args = (List) source;
        assertThat(args.getElement(), hasSize(5));
        char c = 'a';
        for (Expression arg : args.getElement()) {
            assertThat(arg, literalFor(Character.toString(c++)));
        }
    }


}
