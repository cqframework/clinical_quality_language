package org.cqframework.cql.cql2elm.qicore.v410;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Retrieve;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class BaseTest {
    @Test
    public void testQICore() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v410/TestQICore.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("TestAdverseEvent");
        assertThat(def.getExpression(), instanceOf(Retrieve.class));
        Retrieve retrieve = (Retrieve)def.getExpression();
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-adverseevent"));
    }

    @Test
    public void testEXM124() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v410/EXM124_QICore4-8.2.000.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Initial Population");
    }

    @Test
    public void testEXM165() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v410/EXM165_QICore4-8.5.000.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Initial Population");
    }
}
