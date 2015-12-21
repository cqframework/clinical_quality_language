package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.hl7.elm.r1.Abs;
import org.hl7.elm.r1.Ceiling;
import org.hl7.elm.r1.DateTime;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Floor;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Ln;
import org.hl7.elm.r1.Log;
import org.hl7.elm.r1.Negate;
import org.hl7.elm.r1.Quantity;
import org.hl7.elm.r1.Round;
import org.hl7.elm.r1.Truncate;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.matchers.ConvertsToDecimalFrom.convertsToDecimalFrom;
import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DateTimeOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        CqlTranslator translator = CqlTranslator.fromStream(DateTimeOperatorsTest.class.getResourceAsStream("../OperatorTests/DateTimeOperators.cql"));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testDateTime() {
        //DateTimeMillisecond: DateTime(2014, 1, 1, 0, 0, 0, 0)
        ExpressionDef def = defs.get("DateTimeYear");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        DateTime dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), nullValue());
        assertThat(dt.getDay(), nullValue());
        assertThat(dt.getHour(), nullValue());
        assertThat(dt.getMinute(), nullValue());
        assertThat(dt.getSecond(), nullValue());
        assertThat(dt.getMillisecond(), nullValue());
        assertThat(dt.getTimezoneOffset(), nullValue());

        def = defs.get("DateTimeMonth");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), nullValue());
        assertThat(dt.getHour(), nullValue());
        assertThat(dt.getMinute(), nullValue());
        assertThat(dt.getSecond(), nullValue());
        assertThat(dt.getMillisecond(), nullValue());
        assertThat(dt.getTimezoneOffset(), nullValue());

        def = defs.get("DateTimeDay");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), nullValue());
        assertThat(dt.getMinute(), nullValue());
        assertThat(dt.getSecond(), nullValue());
        assertThat(dt.getMillisecond(), nullValue());
        assertThat(dt.getTimezoneOffset(), nullValue());

        def = defs.get("DateTimeHour");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), nullValue());
        assertThat(dt.getSecond(), nullValue());
        assertThat(dt.getMillisecond(), nullValue());
        assertThat(dt.getTimezoneOffset(), nullValue());

        def = defs.get("DateTimeMinute");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), nullValue());
        assertThat(dt.getMillisecond(), nullValue());
        assertThat(dt.getTimezoneOffset(), nullValue());

        def = defs.get("DateTimeSecond");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), literalFor(0));
        assertThat(dt.getMillisecond(), nullValue());
        assertThat(dt.getTimezoneOffset(), nullValue());

        def = defs.get("DateTimeMillisecond");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), literalFor(0));
        assertThat(dt.getMillisecond(), literalFor(0));
        assertThat(dt.getTimezoneOffset(), nullValue());

        def = defs.get("DateTimeMillisecondOffset");
        assertThat(def, hasTypeAndResult(DateTime.class, "System.DateTime"));
        dt = (DateTime) def.getExpression();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), literalFor(0));
        assertThat(dt.getMillisecond(), literalFor(0));
        assertThat(dt.getTimezoneOffset(), literalFor(5.5));
    }
}
