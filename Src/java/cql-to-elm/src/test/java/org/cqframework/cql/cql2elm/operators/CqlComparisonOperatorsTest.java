package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Bryn on 12/30/2016.
 */
public class CqlComparisonOperatorsTest {

    // NOTE: The CQL for this test is taken from an engine testing suite that produced a particular issue with the operatorMap. This library will not translate successfully without the proper fix in place.
    // So this test only needs to validate that the library translates successfully.

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(CqlComparisonOperatorsTest.class.getResourceAsStream("../OperatorTests/CqlComparisonOperators.cql"), modelManager, new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }
    }

    //@Test
    //public void testEqual() {
    //    ExpressionDef def = defs.get("SimpleEqNullNull");
    //    assertThat(def, hasTypeAndResult(Equal.class, "System.Boolean"));
    //}
}
