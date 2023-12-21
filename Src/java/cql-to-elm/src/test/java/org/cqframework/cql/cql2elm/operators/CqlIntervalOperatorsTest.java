package org.cqframework.cql.cql2elm.operators;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by Bryn on 12/30/2016.
 */
public class CqlIntervalOperatorsTest {

    // NOTE: The CQL for this test is taken from an engine testing suite that produced a particular issue with generic
    // instantiations.
    // This library will not translate successfully without the proper fix in place.
    // So this test only needs to validate that the library translates successfully.

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(
                CqlIntervalOperatorsTest.class.getResourceAsStream("../OperatorTests/CqlIntervalOperators.cql"),
                new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }
    }

    // Ignored, see comment in test source
    //    @Test
    //    public void testAfter() {
    //        ExpressionDef def = defs.get("TestAfterNull");
    //        assertThat(def, hasTypeAndResult(After.class, "System.Boolean"));
    //    }

    @Test
    public void testOverlapsDay() {
        ExpressionDef def = defs.get("TestOverlapsDay");
        assertThat(def, hasTypeAndResult(Overlaps.class, "System.Boolean"));
    }

    @Test
    public void testOverlapsDayBefore() {
        ExpressionDef def = defs.get("TestOverlapsDayBefore");
        assertThat(def, hasTypeAndResult(OverlapsBefore.class, "System.Boolean"));
    }

    @Test
    public void testOverlapsDayAfter() {
        ExpressionDef def = defs.get("TestOverlapsDayAfter");
        assertThat(def, hasTypeAndResult(OverlapsAfter.class, "System.Boolean"));
    }
}
