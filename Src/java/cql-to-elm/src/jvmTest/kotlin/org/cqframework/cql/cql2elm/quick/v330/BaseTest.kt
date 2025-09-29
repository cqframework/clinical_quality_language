package org.cqframework.cql.cql2elm.quick.v330

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils
import org.cqframework.cql.cql2elm.matchers.Quick2DataType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.instanceOf
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.In
import org.hl7.elm.r1.Is
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ToList
import org.junit.jupiter.api.Test

class BaseTest {
    // @Test
    // BTR -> The types in QUICK are collapsed so this doesn't result in a choice between equally
    // viable alternatives
    // The test is not valid for the QUICK Model
    @Throws(IOException::class)
    fun testChoiceWithAlternativeConversion() {
        val def = TestUtils.visitFile("quick/v330/TestChoiceTypes.cql") as ExpressionDef?
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        assertThat(source.alias, Matchers.`is`("Q"))
        val request = source.expression as Retrieve?
        assertThat(request!!.dataType, Quick2DataType.quick2DataType("QuestionnaireResponse"))

        // Then check that the suchThat of the with is a greater with a Case as the left operand
        val relationship = query.relationship[0]
        assertThat(relationship.suchThat, instanceOf(Greater::class.java))
        val suchThat = relationship.suchThat as Greater?
        assertThat(suchThat!!.operand[0], instanceOf(Case::class.java))
        val caseExpression = suchThat.operand[0] as Case
        assertThat<MutableCollection<*>?>(caseExpression.caseItem, Matchers.hasSize<Any?>(2))
        assertThat(caseExpression.caseItem[0].`when`, instanceOf(Is::class.java))
        assertThat(caseExpression.caseItem[0].then, instanceOf(FunctionRef::class.java))
        assertThat(caseExpression.caseItem[1].`when`, instanceOf(Is::class.java))
        assertThat(caseExpression.caseItem[1].then, instanceOf(FunctionRef::class.java))
    }

    // @Test
    // QUICK types render the conversion under test unnecessary
    @Throws(IOException::class)
    fun testURIConversion() {
        // If this translates without errors, the test is successful
        TestUtils.visitFile("quick/v330/TestURIConversion.cql") as ExpressionDef?
    }

    @Test
    @Throws(IOException::class)
    fun fhirTiming() {
        val def = TestUtils.visitFile("quick/v330/TestFHIRTiming.cql") as ExpressionDef?
        // Query->
        //  where->
        //      In->
        //          left->
        //              As(DateTime) ->
        //                  Property(P.performed)
        //          right-> MeasurementPeriod
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        assertThat(source.alias, Matchers.`is`("P"))
        val request = source.expression as Retrieve?
        assertThat(request!!.dataType, Quick2DataType.quick2DataType("Procedure"))

        // Then check that the where an IncludedIn with a Case as the left operand
        val where = query.where
        assertThat(where, instanceOf(In::class.java))
        val inDef = where as In?
        assertThat(inDef!!.operand[0], instanceOf(As::class.java))
        val asExpression = inDef.operand[0] as As
        assertThat(asExpression.asType!!.localPart, Matchers.equalTo("DateTime"))
        assertThat(asExpression.operand, instanceOf(Property::class.java))
        val property = asExpression.operand as Property?
        assertThat(property!!.scope, Matchers.`is`("P"))
        assertThat(property.path, Matchers.`is`("performed"))
    }

    @Test
    @Throws(IOException::class)
    fun equalityWithConversions() {
        val library = TestUtils.visitFileLibrary("quick/v330/EqualityWithConversions.cql")
        val getGender = library!!.resolveExpressionRef("GetGender")
        assertThat(getGender!!.expression, instanceOf(Equal::class.java))
        val equal = getGender.expression as Equal?
        assertThat(equal!!.operand[1], instanceOf(Literal::class.java))
        val literal: Literal = equal.operand[1] as Literal
        assertThat<String?>(literal.value, Matchers.`is`<String?>("female"))
    }

    @Test
    @Throws(IOException::class)
    fun doubleListPromotion() {
        val translator = TestUtils.runSemanticTest("quick/v330/TestDoubleListPromotion.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def: ExpressionDef = defs["Observations"]!!
        val retrieve = def.expression as Retrieve?
        val codes = retrieve!!.codes
        assertThat(codes, instanceOf(ToList::class.java))
        val toList = codes as ToList?
        assertThat(toList!!.operand, instanceOf(CodeRef::class.java))
        val codeRef = toList.operand as CodeRef?
        assertThat(codeRef!!.name, Matchers.`is`("T0"))
    }

    @Test
    @Throws(IOException::class)
    fun intervalImplicitConversion() {
        TestUtils.runSemanticTest("quick/v330/TestIntervalImplicitConversion.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun implicitFHIRHelpers() {
        TestUtils.runSemanticTest("quick/v330/TestImplicitFHIRHelpers.cql", 0)
    }
}
