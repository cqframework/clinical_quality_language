package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;

import static org.cqframework.cql.cql2elm.matchers.ConvertsToDecimalFrom.convertsToDecimalFromAlias;
import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AggregateOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(AggregateOperatorsTest.class.getResourceAsStream("../OperatorTests/AggregateOperators.cql"), new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.None)));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }
    }

    @Test
    public void testAllTrue() {
        ExpressionDef def = defs.get("AllTrueExpression");
        assertThat(def, hasTypeAndResult(AllTrue.class, "System.Boolean"));

        AllTrue exp = (AllTrue) def.getExpression();
        assertThat(exp.getSource(), listOfLiterals(true, true, true));
    }

    @Test
    public void testAnyTrue() {
        ExpressionDef def = defs.get("AnyTrueExpression");
        assertThat(def, hasTypeAndResult(AnyTrue.class, "System.Boolean"));

        AnyTrue exp = (AnyTrue) def.getExpression();
        assertThat(exp.getSource(), listOfLiterals(false, true, false));
    }

    @Test
    public void testAverage() {
        ExpressionDef def = defs.get("IntegerAvg");
        assertThat(def, hasTypeAndResult(Avg.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((Avg) def.getExpression()).getSource());

        def = defs.get("DecimalAvg");
        assertThat(def, hasTypeAndResult(Avg.class, "System.Decimal"));
        assertThat(((Avg) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

        def = defs.get("QuantityAvg");
        assertThat(def, hasTypeAndResult(Avg.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Avg) def.getExpression()).getSource());
    }

    @Test
    public void testCount() {
        ExpressionDef def = defs.get("CountExpression");
        assertThat(def, hasTypeAndResult(Count.class, "System.Integer"));
        assertThat(((Count) def.getExpression()).getSource(), listOfLiterals(1, 2, 3, 4, 5));
    }

    @Test
    public void testGeometricMean() {
        ExpressionDef def = defs.get("GeometricMeanExpression");
        assertThat(def, hasTypeAndResult(GeometricMean.class, "System.Decimal"));
        assertThat(((GeometricMean)def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0, 5.0));
    }

    @Test
    public void testMax() {
        ExpressionDef def = defs.get("IntegerMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.Integer"));
        assertThat(((Max) def.getExpression()).getSource(), listOfLiterals(1, 2, 3, 4, 5));

        def = defs.get("DecimalMax");
        assertThat(def, hasTypeAndResult(Max.class, "System.Decimal"));
        assertThat(((Max) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

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
        assertThat(((Max) def.getExpression()).getSource(), listOfLiterals("a", "b", "c", "d", "e"));
    }

    @Test
    public void testMin() {
        ExpressionDef def = defs.get("IntegerMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.Integer"));
        assertThat(((Min) def.getExpression()).getSource(), listOfLiterals(1, 2, 3, 4, 5));

        def = defs.get("DecimalMin");
        assertThat(def, hasTypeAndResult(Min.class, "System.Decimal"));
        assertThat(((Min) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

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
        assertThat(((Min) def.getExpression()).getSource(), listOfLiterals("a", "b", "c", "d", "e"));
    }

    @Test
    public void testMedian() {
        ExpressionDef def = defs.get("IntegerMedian");
        assertThat(def, hasTypeAndResult(Median.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((Median) def.getExpression()).getSource());

        def = defs.get("DecimalMedian");
        assertThat(def, hasTypeAndResult(Median.class, "System.Decimal"));
        assertThat(((Median) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

        def = defs.get("QuantityMedian");
        assertThat(def, hasTypeAndResult(Median.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Median) def.getExpression()).getSource());
    }

    @Test
    public void testMode() {
        ExpressionDef def = defs.get("IntegerMode");
        assertThat(def, hasTypeAndResult(Mode.class, "System.Integer"));
        assertThat(((Mode) def.getExpression()).getSource(), listOfLiterals(1, 2, 3, 4, 5));

        def = defs.get("DecimalMode");
        assertThat(def, hasTypeAndResult(Mode.class, "System.Decimal"));
        assertThat(((Mode) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));
    }

    @Test
    public void testPopulationStdDev() {
        ExpressionDef def = defs.get("IntegerPopulationStdDev");
        assertThat(def, hasTypeAndResult(PopulationStdDev.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((PopulationStdDev) def.getExpression()).getSource());

        def = defs.get("DecimalPopulationStdDev");
        assertThat(def, hasTypeAndResult(PopulationStdDev.class, "System.Decimal"));
        assertThat(((PopulationStdDev) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

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
        assertThat(((PopulationVariance) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

        def = defs.get("QuantityPopulationVariance");
        assertThat(def, hasTypeAndResult(PopulationVariance.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((PopulationVariance) def.getExpression()).getSource());
    }

    @Test
    public void testProduct() {
        ExpressionDef def = defs.get("IntegerProduct");
        assertThat(def, hasTypeAndResult(Product.class, "System.Integer"));
        assertThat(((Product) def.getExpression()).getSource(), listOfLiterals(1, 2, 3, 4, 5));

        def = defs.get("DecimalProduct");
        assertThat(def, hasTypeAndResult(Product.class, "System.Decimal"));
        assertThat(((Product) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0, 5.0));
    }

    @Test
    public void testStdDev() {
        ExpressionDef def = defs.get("IntegerStdDev");
        assertThat(def, hasTypeAndResult(StdDev.class, "System.Decimal"));
        assertDecimalConversionForIntegerListOneToFive(((StdDev) def.getExpression()).getSource());

        def = defs.get("DecimalStdDev");
        assertThat(def, hasTypeAndResult(StdDev.class, "System.Decimal"));
        assertThat(((StdDev) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

        def = defs.get("QuantityStdDev");
        assertThat(def, hasTypeAndResult(StdDev.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((StdDev) def.getExpression()).getSource());
    }

    @Test
    public void testSum() {
        ExpressionDef def = defs.get("IntegerSum");
        assertThat(def, hasTypeAndResult(Sum.class, "System.Integer"));
        assertThat(((Sum) def.getExpression()).getSource(), listOfLiterals(1, 2, 3, 4, 5));

        def = defs.get("DecimalSum");
        assertThat(def, hasTypeAndResult(Sum.class, "System.Decimal"));
        assertThat(((Sum) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

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
        assertThat(((Variance) def.getExpression()).getSource(), listOfLiterals(1.0, 2.0, 3.0, 4.0));

        def = defs.get("QuantityVariance");
        assertThat(def, hasTypeAndResult(Variance.class, "System.Quantity"));
        assertQuantityListOneMeterToFourMeters(((Variance) def.getExpression()).getSource());
    }

    private void assertDecimalConversionForIntegerListOneToFive(Expression source) {
        assertThat(source, instanceOf(Query.class));

        Query q = (Query) source;
        assertThat(q.getSource(), hasSize(1));
        assertThat(q.getLet(), hasSize(0));
        assertThat(q.getRelationship(), hasSize(0));
        assertThat(q.getSort(), nullValue());
        assertThat(q.getWhere(), nullValue());
        AliasedQuerySource aqs = q.getSource().get(0);
        assertThat(aqs.getExpression(), listOfLiterals(1, 2, 3, 4, 5));
        String alias = aqs.getAlias();
        assertThat(q.getReturn().isDistinct(), is(false));
        assertThat(q.getReturn().getExpression(), instanceOf(ToDecimal.class));
        assertThat(q.getReturn().getExpression(), convertsToDecimalFromAlias(alias));
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
}
