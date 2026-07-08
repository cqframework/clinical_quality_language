package org.cqframework.cql.cql2elm.qdm.v56

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.junit.jupiter.api.Test

internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun entities() {
        TestUtils.runSemanticTest("qdm/v56/TestEntities.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun choiceTypes() {
        val translator = TestUtils.runSemanticTest("qdm/v56/TestChoiceTypes.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        var def: ExpressionDef = defs["TestIntegerChoice"]!!
        assertThat(def.expression, Matchers.instanceOf(Query::class.java))
        var query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(Equal::class.java))
        var equal = query.where as Equal?
        assertThat(equal!!.operand[0], Matchers.instanceOf(As::class.java))
        var asDef = equal.operand[0] as As
        assertThat(asDef.asType!!.localPart, `is`("Integer"))

        def = defs["TestDecimalChoice"]!!
        assertThat(def.expression, Matchers.instanceOf(Query::class.java))
        query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(Equal::class.java))
        equal = query.where as Equal?
        assertThat(equal!!.operand[0], Matchers.instanceOf(As::class.java))
        asDef = equal.operand[0] as As
        assertThat(asDef.asType!!.localPart, `is`("Decimal"))

        def = defs["TestQuantityChoice"]!!
        assertThat(def.expression, Matchers.instanceOf(Query::class.java))
        query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(Equal::class.java))
        equal = query.where as Equal?
        assertThat(equal!!.operand[0], Matchers.instanceOf(As::class.java))
        asDef = equal.operand[0] as As
        assertThat(asDef.asType!!.localPart, `is`("Quantity"))

        def = defs["TestRatioChoice"]!!
        assertThat(def.expression, Matchers.instanceOf(Query::class.java))
        query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(Equal::class.java))
        equal = query.where as Equal?
        assertThat(equal!!.operand[0], Matchers.instanceOf(As::class.java))
        asDef = equal.operand[0] as As
        assertThat(asDef.asType!!.localPart, `is`("Ratio"))

        def = defs["TestUnionChoices"]!!
        assertThat(def.expression, Matchers.instanceOf(Query::class.java))
        query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(IncludedIn::class.java))

        val includedIn = query.where as IncludedIn?
        assertThat(includedIn!!.operand[0], Matchers.instanceOf(Property::class.java))
        val property: Property = includedIn.operand[0] as Property
        assertThat<String?>(property.path, `is`<String?>("relevantPeriod"))
    }
}
