package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.DateTime;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Now;
import org.hl7.elm.r1.Today;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DateTimeOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(DateTimeOperatorsTest.class.getResourceAsStream("../OperatorTests/DateTimeOperators.cql"), modelManager, new LibraryManager(modelManager));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testDateTime() {
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

    @Test
    public void testNow() {
        ExpressionDef def = defs.get("NowExpression");
        assertThat(def, hasTypeAndResult(Now.class, "System.DateTime"));
    }

    @Test
    public void testToday() {
        ExpressionDef def = defs.get("TodayExpression");
        assertThat(def, hasTypeAndResult(Today.class, "System.DateTime"));
    }
}
