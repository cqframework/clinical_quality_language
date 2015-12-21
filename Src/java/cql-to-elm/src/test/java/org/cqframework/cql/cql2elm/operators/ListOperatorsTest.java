package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.First;
import org.hl7.elm.r1.IndexOf;
import org.hl7.elm.r1.Last;
import org.hl7.elm.r1.Library;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ListOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        CqlTranslator translator = CqlTranslator.fromStream(ListOperatorsTest.class.getResourceAsStream("../OperatorTests/ListOperators.cql"));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testIndexOf() {
        ExpressionDef def = defs.get("ListIndexOf");
        assertThat(def, hasTypeAndResult(IndexOf.class, "System.Integer"));

        IndexOf indexOf = (IndexOf) def.getExpression();
        assertIntegerList(indexOf.getSource(), 1, 2, 3);
        assertThat(indexOf.getElement(), literalFor(2));
    }

    @Test
    public void testFirst() {
        ExpressionDef def = defs.get("ListFirst");
        assertThat(def, hasTypeAndResult(First.class, "System.Integer"));

        First first = (First) def.getExpression();
        assertIntegerList(first.getSource(), 1, 2, 3, 4, 5);
    }

    @Test
    public void testLast() {
        ExpressionDef def = defs.get("ListLast");
        assertThat(def, hasTypeAndResult(Last.class, "System.Integer"));

        Last last = (Last) def.getExpression();
        assertIntegerList(last.getSource(), 1, 2, 3);
    }

    private void assertIntegerList(Expression source, Integer... ints) {
        assertThat(source, instanceOf(org.hl7.elm.r1.List.class));

        List<Expression> args = ((org.hl7.elm.r1.List) source).getElement();
        assertThat(args, hasSize(ints.length));
        for (int i = 0; i < ints.length; i++) {
            assertThat(args.get(i), literalFor(ints[i]));
        }
    }
}
