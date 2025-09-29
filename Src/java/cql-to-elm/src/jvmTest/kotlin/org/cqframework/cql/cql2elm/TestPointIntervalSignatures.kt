package org.cqframework.cql.cql2elm

import java.io.IOException
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hl7.elm.r1.After
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.Contains
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.If
import org.hl7.elm.r1.In
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.ProperContains
import org.hl7.elm.r1.ProperIn
import org.hl7.elm.r1.SameOrAfter
import org.hl7.elm.r1.SameOrBefore
import org.junit.jupiter.api.Test

@Suppress("LongMethod")
/** Created by Bryn on 6/25/2018. */
internal class TestPointIntervalSignatures {
    @Test
    @Throws(IOException::class)
    fun resolvedSignatures() {
        val translator = TestUtils.runSemanticTest("TestPointIntervalSignatures.cql", 0)
        val library = translator.toELM()
        defs = HashMap()
        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs!![def.name] = def
            }
        }

        var def: ExpressionDef = defs!!["PointBeforeInterval"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Before::class.java, "System.Boolean"))
        var op = def.expression as BinaryExpression?
        var left = op!!.operand[0]
        assertThat(left, instanceOf(If::class.java))
        assertThat((left as If).condition, instanceOf(IsNull::class.java))
        assertThat(left.then, instanceOf(Null::class.java))
        assertThat(left.`else`, instanceOf(Interval::class.java))
        var right = op.operand[1]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointOnOrBeforeInterval"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrBefore::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(If::class.java))
        assertThat((left as If).condition, instanceOf(IsNull::class.java))
        assertThat(left.then, instanceOf(Null::class.java))
        assertThat(left.`else`, instanceOf(Interval::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointSameOrBeforeInterval"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrBefore::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(If::class.java))
        assertThat((left as If).condition, instanceOf(IsNull::class.java))
        assertThat(left.then, instanceOf(Null::class.java))
        assertThat(left.`else`, instanceOf(Interval::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["IntervalBeforePoint"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Before::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(If::class.java))
        assertThat((right as If).condition, instanceOf(IsNull::class.java))
        assertThat(right.then, instanceOf(Null::class.java))
        assertThat(right.`else`, instanceOf(Interval::class.java))

        def = defs!!["IntervalOnOrBeforePoint"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrBefore::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(If::class.java))
        assertThat((right as If).condition, instanceOf(IsNull::class.java))
        assertThat(right.then, instanceOf(Null::class.java))
        assertThat(right.`else`, instanceOf(Interval::class.java))

        def = defs!!["IntervalSameOrBeforePoint"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrBefore::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(If::class.java))
        assertThat((right as If).condition, instanceOf(IsNull::class.java))
        assertThat(right.then, instanceOf(Null::class.java))
        assertThat(right.`else`, instanceOf(Interval::class.java))

        def = defs!!["PointAfterInterval"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(After::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(If::class.java))
        assertThat((left as If).condition, instanceOf(IsNull::class.java))
        assertThat(left.then, instanceOf(Null::class.java))
        assertThat(left.`else`, instanceOf(Interval::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointOnOrAfterInterval"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrAfter::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(If::class.java))
        assertThat((left as If).condition, instanceOf(IsNull::class.java))
        assertThat(left.then, instanceOf(Null::class.java))
        assertThat(left.`else`, instanceOf(Interval::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointSameOrAfterInterval"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrAfter::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(If::class.java))
        assertThat((left as If).condition, instanceOf(IsNull::class.java))
        assertThat(left.then, instanceOf(Null::class.java))
        assertThat(left.`else`, instanceOf(Interval::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["IntervalAfterPoint"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(After::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(If::class.java))
        assertThat((right as If).condition, instanceOf(IsNull::class.java))
        assertThat(right.then, instanceOf(Null::class.java))
        assertThat(right.`else`, instanceOf(Interval::class.java))

        def = defs!!["IntervalOnOrAfterPoint"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrAfter::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(If::class.java))
        assertThat((right as If).condition, instanceOf(IsNull::class.java))
        assertThat(right.then, instanceOf(Null::class.java))
        assertThat(right.`else`, instanceOf(Interval::class.java))

        def = defs!!["IntervalSameOrAfterPoint"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(SameOrAfter::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[1]
        assertThat(right, instanceOf(If::class.java))
        assertThat((right as If).condition, instanceOf(IsNull::class.java))
        assertThat(right.then, instanceOf(Null::class.java))
        assertThat(right.`else`, instanceOf(Interval::class.java))

        def = defs!!["PointInInterval"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(In::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointDuringInterval"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(In::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointProperlyDuringInterval"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(ProperIn::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointIncludedInInterval"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(In::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["PointProperlyIncludedInInterval"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(ProperIn::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["IntervalContainsPoint"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Contains::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["IntervalIncludesPoint"]!!
        assertThat(def, HasTypeAndResult.hasTypeAndResult(Contains::class.java, "System.Boolean"))
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))

        def = defs!!["IntervalProperlyIncludesPoint"]!!
        assertThat(
            def,
            HasTypeAndResult.hasTypeAndResult(ProperContains::class.java, "System.Boolean")
        )
        op = def.expression as BinaryExpression?
        left = op!!.operand[0]
        assertThat(left, instanceOf(ExpressionRef::class.java))
        right = op.operand[0]
        assertThat(right, instanceOf(ExpressionRef::class.java))
    }

    companion object {
        private var defs: MutableMap<String?, ExpressionDef>? = null
    }
}
