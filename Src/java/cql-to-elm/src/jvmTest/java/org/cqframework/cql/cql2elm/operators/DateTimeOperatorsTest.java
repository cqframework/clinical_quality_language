package org.cqframework.cql.cql2elm.operators;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DateTimeOperatorsTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromSource(
                buffered(asSource(DateTimeOperatorsTest.class.getResourceAsStream("../OperatorTests/DateTimeOperators.cql"))),
                new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.None)));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    void dateTime() {
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
    void date() {
        ExpressionDef def = defs.get("DateYear");
        assertThat(def, hasTypeAndResult(Date.class, "System.Date"));
        Date d = (Date) def.getExpression();
        assertThat(d.getYear(), literalFor(2014));
        assertThat(d.getMonth(), nullValue());
        assertThat(d.getDay(), nullValue());

        def = defs.get("DateMonth");
        assertThat(def, hasTypeAndResult(Date.class, "System.Date"));
        d = (Date) def.getExpression();
        assertThat(d.getYear(), literalFor(2014));
        assertThat(d.getMonth(), literalFor(1));
        assertThat(d.getDay(), nullValue());

        def = defs.get("DateDay");
        assertThat(def, hasTypeAndResult(Date.class, "System.Date"));
        d = (Date) def.getExpression();
        assertThat(d.getYear(), literalFor(2014));
        assertThat(d.getMonth(), literalFor(1));
        assertThat(d.getDay(), literalFor(1));
    }

    @Test
    void time() {
        ExpressionDef def = defs.get("TimeHour");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        Time dt = (Time) def.getExpression();
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), nullValue());
        assertThat(dt.getSecond(), nullValue());
        assertThat(dt.getMillisecond(), nullValue());

        def = defs.get("TimeMinute");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        dt = (Time) def.getExpression();
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), nullValue());
        assertThat(dt.getMillisecond(), nullValue());

        def = defs.get("TimeSecond");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        dt = (Time) def.getExpression();
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), literalFor(0));
        assertThat(dt.getMillisecond(), nullValue());

        def = defs.get("TimeMillisecond");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        dt = (Time) def.getExpression();
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), literalFor(0));
        assertThat(dt.getMillisecond(), literalFor(0));
    }

    @Test
    void dateExtractor() {
        ExpressionDef def = defs.get("DateExtractor");
        assertThat(def, hasTypeAndResult(DateFrom.class, "System.Date"));
    }

    @Test
    void timeExtractor() {
        ExpressionDef def = defs.get("TimeExtractor");
        assertThat(def, hasTypeAndResult(TimeFrom.class, "System.Time"));
    }

    @Test
    void now() {
        ExpressionDef def = defs.get("NowExpression");
        assertThat(def, hasTypeAndResult(Now.class, "System.DateTime"));
    }

    @Test
    void today() {
        ExpressionDef def = defs.get("TodayExpression");
        assertThat(def, hasTypeAndResult(Today.class, "System.Date"));
    }

    @Test
    void timeOfDay() {
        ExpressionDef def = defs.get("TimeOfDayExpression");
        assertThat(def, hasTypeAndResult(TimeOfDay.class, "System.Time"));
    }
}
