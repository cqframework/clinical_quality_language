package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.DateTime;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Time;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

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

        def = defs.get("TimeZoneTimeLiteral");
        assertThat(def, hasTypeAndResult(Time.class, "System.Time"));
        Time time = (Time)def.getExpression();
        assertThat(time.getTimezoneOffset(), literalFor(-7.0));
    }
}
