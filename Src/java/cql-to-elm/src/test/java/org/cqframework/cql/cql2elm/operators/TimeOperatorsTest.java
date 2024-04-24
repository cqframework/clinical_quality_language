package org.cqframework.cql.cql2elm.operators;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TimeOperatorsTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(
                TimeOperatorsTest.class.getResourceAsStream("../OperatorTests/TimeOperators.cql"),
                new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    void time() {
        ExpressionDef def = defs.get("TimeHour");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        Time t = (Time) def.getExpression();
        assertThat(t.getHour(), literalFor(0));

        def = defs.get("TimeMinute");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        t = (Time) def.getExpression();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));

        def = defs.get("TimeSecond");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        t = (Time) def.getExpression();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));
        assertThat(t.getSecond(), literalFor(0));

        def = defs.get("TimeMillisecond");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        t = (Time) def.getExpression();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));
        assertThat(t.getSecond(), literalFor(0));
        assertThat(t.getMillisecond(), literalFor(0));
    }

    @Test
    void timeOfDay() {
        ExpressionDef def = defs.get("TimeOfDayExpression");
        assertThat(def, hasTypeAndResult(TimeOfDay.class, "System.Time"));
    }
}
