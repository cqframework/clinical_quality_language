package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.hl7.elm.r1.Coalesce;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.IsNull;
import org.hl7.elm.r1.Library;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;

public class NullologicalOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        CqlTranslator translator = CqlTranslator.fromStream(NullologicalOperatorsTest.class.getResourceAsStream("../OperatorTests/NullologicalOperators.cql"));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testIsNull() {
        ExpressionDef def = defs.get("IsNullExpression");
        assertThat(def, hasTypeAndResult(IsNull.class, "System.Boolean"));

        IsNull isNull = (IsNull) def.getExpression();
        assertThat(isNull.getOperand(), literalFor(1));
    }

    @Test
    public void testCoalesce() {
        ExpressionDef def = defs.get("CoalesceList");
        assertThat(def, hasTypeAndResult(Coalesce.class, "System.Integer"));

        Coalesce coalesce = (Coalesce) def.getExpression();
        assertThat(coalesce.getOperand(), hasSize(1));
        assertThat(coalesce.getOperand().get(0), instanceOf(org.hl7.elm.r1.List.class));
        org.hl7.elm.r1.List args = (org.hl7.elm.r1.List) coalesce.getOperand().get(0);
        assertThat(args.getElement(), hasSize(5));
        int i = 1;
        for (Expression arg : args.getElement()) {
            assertThat(arg, literalFor(i++));
        }

        def = defs.get("CoalesceTwoArgument");
        assertThat(def, hasTypeAndResult(Coalesce.class, "System.Integer"));
        assertIntegerArgs((Coalesce) def.getExpression(), 1, 2);

        def = defs.get("CoalesceThreeArgument");
        assertThat(def, hasTypeAndResult(Coalesce.class, "System.Integer"));
        assertIntegerArgs((Coalesce) def.getExpression(), 1, 2, 3);

        def = defs.get("CoalesceFourArgument");
        assertThat(def, hasTypeAndResult(Coalesce.class, "System.Integer"));
        assertIntegerArgs((Coalesce) def.getExpression(), 1, 2, 3, 4);

        def = defs.get("CoalesceFiveArgument");
        assertThat(def, hasTypeAndResult(Coalesce.class, "System.Integer"));
        assertIntegerArgs((Coalesce) def.getExpression(), 1, 2, 3, 4, 5);
    }

    private void assertIntegerArgs(Coalesce coalesce, Integer... ints) {
        assertThat(coalesce.getOperand(), hasSize(ints.length));
        for (int i = 0; i < ints.length; i++) {
            assertThat(coalesce.getOperand().get(i), literalFor(ints[i]));
        }
    }
}
