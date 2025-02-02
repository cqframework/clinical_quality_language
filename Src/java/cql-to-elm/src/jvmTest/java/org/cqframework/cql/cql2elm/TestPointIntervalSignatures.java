package org.cqframework.cql.cql2elm;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.Test;

/**
 * Created by Bryn on 6/25/2018.
 */
class TestPointIntervalSignatures {

    private static Map<String, ExpressionDef> defs;

    @Test
    void resolvedSignatures() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestPointIntervalSignatures.cql", 0);
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("PointBeforeInterval");
        assertThat(def, hasTypeAndResult(Before.class, "System.Boolean"));
        BinaryExpression op = (BinaryExpression) def.getExpression();
        Expression left = op.getOperand().get(0);
        assertThat(left, instanceOf(If.class));
        assertThat(((If) left).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) left).getThen(), instanceOf(Null.class));
        assertThat(((If) left).getElse(), instanceOf(Interval.class));
        Expression right = op.getOperand().get(1);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointOnOrBeforeInterval");
        assertThat(def, hasTypeAndResult(SameOrBefore.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(If.class));
        assertThat(((If) left).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) left).getThen(), instanceOf(Null.class));
        assertThat(((If) left).getElse(), instanceOf(Interval.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointSameOrBeforeInterval");
        assertThat(def, hasTypeAndResult(SameOrBefore.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(If.class));
        assertThat(((If) left).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) left).getThen(), instanceOf(Null.class));
        assertThat(((If) left).getElse(), instanceOf(Interval.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("IntervalBeforePoint");
        assertThat(def, hasTypeAndResult(Before.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(If.class));
        assertThat(((If) right).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) right).getThen(), instanceOf(Null.class));
        assertThat(((If) right).getElse(), instanceOf(Interval.class));

        def = defs.get("IntervalOnOrBeforePoint");
        assertThat(def, hasTypeAndResult(SameOrBefore.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(If.class));
        assertThat(((If) right).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) right).getThen(), instanceOf(Null.class));
        assertThat(((If) right).getElse(), instanceOf(Interval.class));

        def = defs.get("IntervalSameOrBeforePoint");
        assertThat(def, hasTypeAndResult(SameOrBefore.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(If.class));
        assertThat(((If) right).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) right).getThen(), instanceOf(Null.class));
        assertThat(((If) right).getElse(), instanceOf(Interval.class));

        def = defs.get("PointAfterInterval");
        assertThat(def, hasTypeAndResult(After.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(If.class));
        assertThat(((If) left).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) left).getThen(), instanceOf(Null.class));
        assertThat(((If) left).getElse(), instanceOf(Interval.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointOnOrAfterInterval");
        assertThat(def, hasTypeAndResult(SameOrAfter.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(If.class));
        assertThat(((If) left).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) left).getThen(), instanceOf(Null.class));
        assertThat(((If) left).getElse(), instanceOf(Interval.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointSameOrAfterInterval");
        assertThat(def, hasTypeAndResult(SameOrAfter.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(If.class));
        assertThat(((If) left).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) left).getThen(), instanceOf(Null.class));
        assertThat(((If) left).getElse(), instanceOf(Interval.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("IntervalAfterPoint");
        assertThat(def, hasTypeAndResult(After.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(If.class));
        assertThat(((If) right).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) right).getThen(), instanceOf(Null.class));
        assertThat(((If) right).getElse(), instanceOf(Interval.class));

        def = defs.get("IntervalOnOrAfterPoint");
        assertThat(def, hasTypeAndResult(SameOrAfter.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(If.class));
        assertThat(((If) right).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) right).getThen(), instanceOf(Null.class));
        assertThat(((If) right).getElse(), instanceOf(Interval.class));

        def = defs.get("IntervalSameOrAfterPoint");
        assertThat(def, hasTypeAndResult(SameOrAfter.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(1);
        assertThat(right, instanceOf(If.class));
        assertThat(((If) right).getCondition(), instanceOf(IsNull.class));
        assertThat(((If) right).getThen(), instanceOf(Null.class));
        assertThat(((If) right).getElse(), instanceOf(Interval.class));

        def = defs.get("PointInInterval");
        assertThat(def, hasTypeAndResult(In.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointDuringInterval");
        assertThat(def, hasTypeAndResult(In.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointProperlyDuringInterval");
        assertThat(def, hasTypeAndResult(ProperIn.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointIncludedInInterval");
        assertThat(def, hasTypeAndResult(In.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("PointProperlyIncludedInInterval");
        assertThat(def, hasTypeAndResult(ProperIn.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("IntervalContainsPoint");
        assertThat(def, hasTypeAndResult(Contains.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("IntervalIncludesPoint");
        assertThat(def, hasTypeAndResult(Contains.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));

        def = defs.get("IntervalProperlyIncludesPoint");
        assertThat(def, hasTypeAndResult(ProperContains.class, "System.Boolean"));
        op = (BinaryExpression) def.getExpression();
        left = op.getOperand().get(0);
        assertThat(left, instanceOf(ExpressionRef.class));
        right = op.getOperand().get(0);
        assertThat(right, instanceOf(ExpressionRef.class));
    }
}
