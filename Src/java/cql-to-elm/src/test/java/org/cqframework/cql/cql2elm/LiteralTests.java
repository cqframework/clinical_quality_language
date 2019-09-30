package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Bryn on 11/21/2017.
 */
public class LiteralTests {

    private Map<String, ExpressionDef> defs;

    @Test
    public void dateTimeLiteralTests() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("DateTimeLiteralTest.cql", 0);
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("TimeZoneDateTimeLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        DateTime dateTime = (DateTime)def.getExpression();
        assertThat(dateTime.getTimezoneOffset(), literalFor(-7.0));

        def = defs.get("TimeZonePositiveDateTimeLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dateTime = (DateTime)def.getExpression();
        assertThat(dateTime.getTimezoneOffset(), literalFor(7.0));

        def = defs.get("YearLiteral");
        assertThat(def, hasTypeAndResult(Date.class, "System.Date"));

        def = defs.get("DateTimeYearLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));

        def = defs.get("UTCYearLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));

        def = defs.get("YearMonthLiteral");
        assertThat(def, hasTypeAndResult(Date.class, "System.Date"));

        def = defs.get("DateTimeYearMonthLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));

        def = defs.get("UTCYearMonthLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));

        def = defs.get("DateLiteral");
        assertThat(def, hasTypeAndResult(Date.class, "System.Date"));

        def = defs.get("DateTimeDateLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));

        def = defs.get("UTCDateLiteral");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
    }

    @Test
    public void quantityLiteralTests() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("QuantityLiteralTest.cql", 1);
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("ValidQuantityLiteral");
        assertThat(def, hasTypeAndResult(Quantity.class, "System.Quantity"));
        Quantity quantity = (Quantity)def.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(10)));
        assertThat(quantity.getUnit(), is("mm[Hg]"));

        def = defs.get("InvalidQuantityLiteral");
        assertThat("Invalid quantity literal is returned as a Null", def.getExpression() instanceof Null);

        def = defs.get("UnitQuantityLiteral");
        assertThat(def, hasTypeAndResult(Quantity.class, "System.Quantity"));
        quantity = (Quantity)def.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(10)));
        assertThat(quantity.getUnit(), is("1"));

        def = defs.get("AnnotationQuantityLiteral");
        assertThat(def, hasTypeAndResult(Quantity.class, "System.Quantity"));
        quantity = (Quantity)def.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(10)));
        assertThat(quantity.getUnit(), is("{shab-shab-shab}"));

        def = defs.get("QuantityConversionTest");
        assertThat(def, hasTypeAndResult(ConvertQuantity.class, "System.Quantity"));
        ConvertQuantity convertQuantity = (ConvertQuantity)def.getExpression();
        assertThat(convertQuantity.getOperand().get(0), instanceOf(Quantity.class));
        quantity = (Quantity)convertQuantity.getOperand().get(0);
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(5)));
        assertThat(quantity.getUnit(), is("mg"));
        assertThat(convertQuantity.getOperand().get(1), instanceOf(Literal.class));
        Literal literal = (Literal)convertQuantity.getOperand().get(1);
        assertThat(literal.getValue(), is("g"));

        def = defs.get("QuantityConversionWeekTest");
        assertThat(def, hasTypeAndResult(ConvertQuantity.class, "System.Quantity"));
        convertQuantity = (ConvertQuantity)def.getExpression();
        assertThat(convertQuantity.getOperand().get(0), instanceOf(Quantity.class));
        quantity = (Quantity)convertQuantity.getOperand().get(0);
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(28)));
        assertThat(quantity.getUnit(), is("days"));
        assertThat(convertQuantity.getOperand().get(1), instanceOf(Literal.class));
        literal = (Literal)convertQuantity.getOperand().get(1);
        assertThat(literal.getValue(), is("wk"));
    }

    @Test
    public void RatioLiteralTests() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("RatioLiteralTest.cql", 0);
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("SimpleRatio");
        assertThat(def, hasTypeAndResult(Ratio.class, "System.Ratio"));
        Ratio ratio = (Ratio)def.getExpression();
        assertThat(ratio.getNumerator().getValue(), is(BigDecimal.valueOf(5)));
        assertThat(ratio.getDenominator().getValue(), is(BigDecimal.valueOf(5)));

        def = defs.get("QuantityRatio");
        assertThat(def, hasTypeAndResult(Ratio.class, "System.Ratio"));
        ratio = (Ratio)def.getExpression();
        assertThat(ratio.getNumerator().getValue(), is(BigDecimal.valueOf(5)));
        assertThat(ratio.getNumerator().getUnit(), is("mg"));
        assertThat(ratio.getDenominator().getValue(), is(BigDecimal.valueOf(100)));
        assertThat(ratio.getDenominator().getUnit(), is("mL"));
    }
}
