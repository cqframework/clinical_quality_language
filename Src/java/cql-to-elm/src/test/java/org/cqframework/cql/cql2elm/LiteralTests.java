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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.nullValue;

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

        def = defs.get("TimeZoneTimeLiteral");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        Time time = (Time)def.getExpression();
        assertThat(time.getTimezoneOffset(), literalFor(-7.0));

        def = defs.get("TimeZonePositiveTimeLiteral");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        time = (Time)def.getExpression();
        assertThat(time.getTimezoneOffset(), literalFor(7.0));
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
    }
}
