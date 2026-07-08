package org.cqframework.cql.cql2elm

import java.io.IOException
import java.math.BigDecimal
import javax.xml.namespace.QName
import kotlin.uuid.ExperimentalUuidApi
import org.cqframework.cql.cql2elm.matchers.LiteralFor.Companion.literalFor
import org.cqframework.cql.cql2elm.matchers.QdmDataType
import org.cqframework.cql.cql2elm.matchers.QuickDataType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.cql2elm.tracking.Trackable.trackerId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.hl7.elm.r1.Add
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.And
import org.hl7.elm.r1.As
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.ByColumn
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.DateTimePrecision
import org.hl7.elm.r1.DurationBetween
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.First
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.GreaterOrEqual
import org.hl7.elm.r1.In
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Instance
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.IsTrue
import org.hl7.elm.r1.Less
import org.hl7.elm.r1.LessOrEqual
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Modulo
import org.hl7.elm.r1.Multiply
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.Or
import org.hl7.elm.r1.OverlapsAfter
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Power
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.SameAs
import org.hl7.elm.r1.SameOrAfter
import org.hl7.elm.r1.SameOrBefore
import org.hl7.elm.r1.SortClause
import org.hl7.elm.r1.SortDirection
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.Subtract
import org.hl7.elm.r1.Tuple
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.With
import org.hl7.elm.r1.Xor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Suppress("LargeClass")
internal class Cql2ElmVisitorTest {
    @Test
    fun let() {
        val def = TestUtils.visitData("define b : true")
        assertThat(def!!.name, `is`("b"))
        assertTrackable(def)
    }

    @Test
    fun booleanLiteral() {
        var def = TestUtils.visitData("define b : true")
        assertThat(def!!.expression, literalFor(true))
        assertTrackable(def.expression)

        def = TestUtils.visitData("define b : false")
        assertThat(def!!.expression, literalFor(false))
    }

    @Test
    fun stringLiteral() {
        val def = TestUtils.visitData("define st : 'hey its a string'")
        assertThat(def!!.expression, literalFor("hey its a string"))
        assertTrackable(def.expression)
    }

    @Test
    fun nullLiteral() {
        val def = TestUtils.visitData("define st : null")
        assertThat(def!!.expression, Matchers.instanceOf(Null::class.java))
        assertTrackable(def.expression)
    }

    @Test
    fun quantityLiteral() {
        var def = TestUtils.visitData("define st : 1")
        assertThat(def!!.expression, literalFor(1))
        assertTrackable(def.expression)

        def = TestUtils.visitData("define st : 1.1")
        assertThat(def!!.expression, literalFor(1.1))

        def = TestUtils.visitData("define st : 1.1 'mm'")
        val quantity = def!!.expression as Quantity?
        assertThat(quantity!!.value, `is`(BigDecimal.valueOf(1.1)))
        assertThat(quantity.unit, `is`("mm"))
        assertTrackable(quantity)
    }

    @Test
    fun andExpressions() {
        val def = TestUtils.visitData("define st : true and false")
        val and = def!!.expression as And?
        val left: Expression = and!!.operand[0]
        val right: Expression = and.operand[1]

        assertThat(left, literalFor(true))
        assertThat(right, literalFor(false))

        assertTrackable(and)
        assertTrackable(left)
        assertTrackable(right)
    }

    @Test
    fun orExpressions() {
        var def = TestUtils.visitData("define st : true or false")
        val or = def!!.expression as Or?
        var left: Expression? = or!!.operand[0]
        var right: Expression? = or.operand[1]

        assertThat(left, literalFor(true))
        assertThat(right, literalFor(false))

        assertTrackable(or)
        assertTrackable(left)
        assertTrackable(right)

        def = TestUtils.visitData("define st : true xor false")
        val xor = def!!.expression as Xor?
        left = xor!!.operand[0]
        right = xor.operand[1]

        assertThat(left, literalFor(true))
        assertThat(right, literalFor(false))

        assertTrackable(or)
        assertTrackable(left)
        assertTrackable(right)
    }

    @Test
    fun comparisonExpressions() {
        val comparisons: MutableMap<String?, Class<*>?> =
            object : HashMap<String?, Class<*>?>() {
                init {
                    put("<", Less::class.java)
                    put("<=", LessOrEqual::class.java)
                    put("=", Equal::class.java)
                    put(">=", GreaterOrEqual::class.java)
                    put(">", Greater::class.java)
                }
            }

        for (e in comparisons.entries) {
            val def = TestUtils.visitData("define st : 1 " + e.key + " 2")
            val binary = def!!.expression as BinaryExpression?
            val left: Expression = binary!!.operand[0]
            val right: Expression = binary.operand[1]

            assertThat(binary, Matchers.instanceOf(e.value))
            assertThat(left, literalFor(1))
            assertThat(right, literalFor(2))

            assertTrackable(binary)
            assertTrackable(left)
            assertTrackable(right)
        }
    }

    @Test
    fun notEqualExpression() {
        val def = TestUtils.visitData("define st : 1 != 2")
        val not = def!!.expression as Not?
        val equal = not!!.operand as Equal?
        val left: Expression = equal!!.operand[0]
        val right: Expression = equal.operand[1]

        assertThat(left, literalFor(1))
        assertThat(right, literalFor(2))

        // assertTrackable(not);
        // assertTrackable(equal);
        assertTrackable(left)
        assertTrackable(right)
    }

    @Test
    fun isTrueExpressions() {
        val def = TestUtils.visitData("define X : true\ndefine st : X is true")
        val isTrue = def!!.expression as IsTrue?
        val left = isTrue!!.operand as ExpressionRef?

        assertThat(left!!.name, `is`("X"))

        assertTrackable(isTrue)
        assertTrackable(left)
    }

    @Test
    fun isNotTrueExpressions() {
        val def = TestUtils.visitData("define X : true\ndefine st : X is not true")
        val not = def!!.expression as Not?
        val isTrue = not!!.operand as IsTrue?
        val left = isTrue!!.operand as ExpressionRef?

        assertThat(left!!.name, `is`("X"))

        assertTrackable(not)
        assertTrackable(left)
    }

    @Test
    fun isNullExpressions() {
        val def = TestUtils.visitData("define X : 1\ndefine st : X is null")
        val isNull = def!!.expression as IsNull?
        val id = isNull!!.operand as ExpressionRef?

        assertThat(id!!.name, `is`("X"))

        assertTrackable(isNull)
        assertTrackable(id)
    }

    @Test
    fun isNotNullExpressions() {
        val def = TestUtils.visitData("define X : 1\ndefine st : X is not null")
        val not = def!!.expression as Not?
        val isNull = not!!.operand as IsNull?
        val id = isNull!!.operand as ExpressionRef?

        assertThat(id!!.name, `is`("X"))

        assertTrackable(not)
        // assertTrackable(isNull);
        assertTrackable(id)
    }

    @Test
    fun expressionReference() {
        val cql = "using QUICK\n" + "define X : [Condition]\n" + "define st : X"
        val def = TestUtils.visitData(cql)
        val exp = def!!.expression as ExpressionRef?
        assertThat(exp!!.name, `is`("X"))
        assertThat(exp.libraryName, `is`(nullValue()))
    }

    @Test
    fun propertyReference() {
        val cql =
            "using QUICK\n" + "define X : First([Condition])\n" + "define st : X.onsetDateTime"
        val def = TestUtils.visitData(cql)
        val prop = def!!.expression as Property?
        val source = prop!!.source as ExpressionRef?
        assertThat(source!!.name, `is`("X"))
        assertThat(source.libraryName, Matchers.`is`(nullValue()))
        assertThat(prop.path, `is`("onsetDateTime"))
        assertThat(prop.scope, Matchers.`is`(nullValue()))
    }

    @Test
    fun valueSetReference() {
        val cql =
            ("valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define st : \"Acute Pharyngitis\"")
        val def = TestUtils.visitData(cql)
        val vs = def!!.expression as ValueSetRef?
        assertThat(vs!!.name, `is`("Acute Pharyngitis"))
        assertThat(vs.libraryName, Matchers.`is`(nullValue()))
    }

    @Test
    fun inValueSetExpression() {
        val cql =
            ("valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define m : 'Value' in \"Acute Pharyngitis\"")
        val def = TestUtils.visitData(cql)
        val ivs = def!!.expression as InValueSet?
        assertThat<ValueSetRef?>(
            ivs!!.valueset,
            Matchers.instanceOf<ValueSetRef?>(ValueSetRef::class.java),
        )
        val vsr = ivs.valueset
        assertThat(vsr!!.name, `is`("Acute Pharyngitis"))
    }

    @Test
    @Disabled("TODO: Fix when operator semantics are completed")
    fun functionReference() {
        val cql = "define function MyFunction() { return true }\n" + "define st : MyFunction()"
        val def = TestUtils.visitData(cql)
        val funRef = def!!.expression as FunctionRef?
        assertThat(funRef!!.name, `is`("MyFunction"))
        assertThat(funRef.libraryName, Matchers.`is`(nullValue()))
        assertThat(funRef.operand, Matchers.`is`(Matchers.empty()))
    }

    @Test
    @Disabled("TODO: Need to add operand resolution to the type inference...")
    fun functionReferenceWithArguments() {
        val cql =
            "define function MyFunction(arg String) { return arg }\n" +
                "define st : MyFunction('hello there')"
        val def = TestUtils.visitData(cql)
        val funRef = def!!.expression as FunctionRef?
        assertThat(funRef!!.name, `is`("MyFunction"))
        assertThat(funRef.libraryName, Matchers.`is`(nullValue()))
        assertThat(funRef.operand, Matchers.hasSize(1))
        assertThat(funRef.operand[0], literalFor("hello there"))
    }

    @Suppress("ForbiddenComment")
    // TODO: Tests for accessors to expressions, valuesets, functions from included libraries
    @Test
    fun arithmeticExpressions() {
        val comparisons: MutableMap<String?, Class<*>?> =
            object : HashMap<String?, Class<*>?>() {
                init {
                    put("+", Add::class.java)
                    put("-", Subtract::class.java)
                    put("*", Multiply::class.java)
                    // put("/", Divide.class); // This test fails because divide with integer
                    // arguments is not defined
                    // (relies on implicit conversion)
                    put("^", Power::class.java)
                    put("mod", Modulo::class.java)
                }
            }

        for (e in comparisons.entries) {
            val def = TestUtils.visitData("define st : 1 " + e.key + " 2")
            val binary = def!!.expression as BinaryExpression?
            val left: Expression = binary!!.operand[0]
            val right: Expression = binary.operand[1]

            assertThat(binary, Matchers.instanceOf(e.value))
            assertThat(left, literalFor(1))
            assertThat(right, literalFor(2))

            assertTrackable(binary)
            assertTrackable(left)
            assertTrackable(right)
        }
    }

    @Test
    fun basicValueSet() {
        val cql =
            "valueset \"Female Administrative Sex\" : '2.16.840.1.113883.3.560.100.2'\n" +
                "define X : 1"
        val l = TestUtils.visitLibrary(cql)
        val def = l!!.valueSets!!.def[0]
        assertThat(def.name, `is`("Female Administrative Sex"))
        assertThat(def.id, `is`("2.16.840.1.113883.3.560.100.2"))
        assertThat(def.version, Matchers.`is`(nullValue()))
        assertThat(def.codeSystem.size, `is`(0))
    }

    @Test
    fun versionedValueSet() {
        val cql =
            ("valueset \"Female Administrative Sex\" : '2.16.840.1.113883.3.560.100.2' version '1'\n" +
                "define X : 1")
        val l = TestUtils.visitLibrary(cql)
        val def = l!!.valueSets!!.def[0]
        assertThat(def.name, `is`("Female Administrative Sex"))
        assertThat(def.id, `is`("2.16.840.1.113883.3.560.100.2"))
        assertThat(def.version, `is`("1"))
        assertThat(def.codeSystem.size, `is`(0))
    }

    @Test
    fun staticallyBoundValueSet() {
        val cql =
            ("codesystem \"SNOMED-CT:2014\" : 'SNOMED-CT' version '2014'\n" +
                "codesystem \"ICD-9:2014\" : 'ICD-9' version '2014'\n" +
                "valueset \"Female Administrative Sex\" : '2.16.840.1.113883.3.560.100.2' version '1'\n" +
                "    codesystems { \"SNOMED-CT:2014\", \"ICD-9:2014\" }\n" +
                "define X : 1")
        val l = TestUtils.visitLibrary(cql)
        val def = l!!.valueSets!!.def[0]
        assertThat(def.name, `is`("Female Administrative Sex"))
        assertThat(def.id, `is`("2.16.840.1.113883.3.560.100.2"))
        assertThat(def.version, `is`("1"))
        assertThat(def.codeSystem, Matchers.`is`(Matchers.not(nullValue())))
        assertThat(def.codeSystem.size, `is`(2))
        var r = def.codeSystem[0]
        assertThat(r.name, `is`("SNOMED-CT:2014"))
        assertThat(r.libraryName, Matchers.`is`(nullValue()))
        r = def.codeSystem[1]
        assertThat(r.name, `is`("ICD-9:2014"))
        assertThat(r.libraryName, Matchers.`is`(nullValue()))

        val snomedCT = l.codeSystems!!.def[0]
        assertThat(snomedCT.name, `is`("SNOMED-CT:2014"))
        assertThat(snomedCT.id, `is`("SNOMED-CT"))
        assertThat(snomedCT.version, `is`("2014"))

        val icd9 = l.codeSystems!!.def[1]
        assertThat(icd9.name, `is`("ICD-9:2014"))
        assertThat(icd9.id, `is`("ICD-9"))
        assertThat(icd9.version, `is`("2014"))
    }

    @Test
    fun retrieveTopic() {
        val def = TestUtils.visitData("using QUICK define st : [Condition]")
        val request = def!!.expression as Retrieve?
        assertThat<QName>(request!!.dataType, QuickDataType.quickDataType("Condition"))
        assertThat(request.codeProperty, Matchers.`is`(nullValue()))
        assertThat(request.codes, Matchers.`is`(nullValue()))
        assertThat(request.dateProperty, Matchers.`is`(nullValue()))
        assertThat(request.dateRange, Matchers.`is`(nullValue()))
        assertThat(request.idProperty, Matchers.`is`(nullValue()))
        assertThat(request.templateId, `is`("condition-qicore-qicore-condition"))
    }

    @Test
    fun retrieveTopicAndValueSet() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define st : [Condition: \"Acute Pharyngitis\"]")
        val def = TestUtils.visitData(cql)
        val request = def!!.expression as Retrieve?
        assertThat<QName>(request!!.dataType, QuickDataType.quickDataType("Condition"))
        assertThat(request.codeProperty, `is`("code"))
        val code = request.codes as ValueSetRef?
        assertThat(code!!.name, `is`("Acute Pharyngitis"))
        assertThat(code.libraryName, Matchers.`is`(nullValue()))
        assertThat(request.dateProperty, Matchers.`is`(nullValue()))
        assertThat(request.dateRange, Matchers.`is`(nullValue()))
        assertThat(request.idProperty, Matchers.`is`(nullValue()))
        assertThat(request.templateId, `is`("condition-qicore-qicore-condition"))
    }

    @Test
    fun retrieveTopicAndSpecifiedCodeAttribute() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Moderate or Severe\" : '2.16.840.1.113883.3.526.3.1092'\n" +
                "define st : [Condition: severity in \"Moderate or Severe\"]")
        val def = TestUtils.visitData(cql)
        val request = def!!.expression as Retrieve?
        assertThat<QName>(request!!.dataType, QuickDataType.quickDataType("Condition"))
        assertThat(request.codeProperty, `is`("severity"))
        val code = request.codes as ValueSetRef?
        assertThat(code!!.name, `is`("Moderate or Severe"))
        assertThat(code.libraryName, Matchers.`is`(nullValue()))
        assertThat(request.dateProperty, Matchers.`is`(nullValue()))
        assertThat(request.dateRange, Matchers.`is`(nullValue()))
        assertThat(request.idProperty, Matchers.`is`(nullValue()))
        assertThat(request.templateId, `is`("condition-qicore-qicore-condition"))
    }

    @Test
    fun dateRangeOptimizationForDateIntervalLiteral() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during Interval[DateTime(2013, 1, 1),
        // DateTime(2014, 1, 1))" migrated
        // up!
        assertThat(request.dateProperty, `is`("period"))
        val ivl = request.dateRange as Interval?
        Assertions.assertTrue(ivl!!.isLowClosed() == true)
        Assertions.assertFalse(ivl.isHighClosed() == true)
        val ivlBegin = ivl.low as DateTime?
        assertThat(ivlBegin!!.year, literalFor(2013))
        assertThat(ivlBegin.month, literalFor(1))
        assertThat(ivlBegin.day, literalFor(1))
        val ivlEnd = ivl.high as DateTime?
        assertThat(ivlEnd!!.year, literalFor(2014))
        assertThat(ivlEnd.month, literalFor(1))
        assertThat(ivlEnd.day, literalFor(1))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    fun dateRangeOptimizationForDefaultedDateIntervalParameter() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val mp = request.dateRange as ParameterRef?
        assertThat(mp!!.name, `is`("MeasurementPeriod"))
        assertThat(mp.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    fun dateRangeOptimizationForTypedDateIntervalParameter() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod Interval<DateTime>\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val mp = request.dateRange as ParameterRef?
        assertThat(mp!!.name, `is`("MeasurementPeriod"))
        assertThat(mp.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    fun dateRangeOptimizationForDateIntervalExpressionReference() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define twentyThirteen : Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during twentyThirteen")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during twentyThirteen" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val ivl = request.dateRange as ExpressionRef?
        assertThat(ivl!!.name, `is`("twentyThirteen"))
        assertThat(ivl.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    @Disabled("TODO: This test is semantically invalid, you cannot use \"during\" with a date")
    fun dateRangeOptimizationForDateTimeLiteral() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during DateTime(2013, 6)")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during DateTime(2013, 6)" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val dtFun = request.dateRange as FunctionRef?
        assertThat(dtFun!!.name, `is`("DateTime"))
        assertThat(dtFun.operand, Matchers.hasSize(2))
        assertThat(dtFun.operand[0], literalFor(2013))
        assertThat(dtFun.operand[1], literalFor(6))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    @Disabled("TODO: test is semantically invalid, you cannot use \"during\" with a date")
    fun dateRangeOptimizationForDefaultedDateTimeParameter() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MyDate default DateTime(2013, 6)\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MyDate")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MyDate" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val myDate = request.dateRange as ParameterRef?
        assertThat(myDate!!.name, `is`("MyDate"))
        assertThat(myDate.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    @Disabled("TODO: This test is semantically invalid, you cannot use \"during\" with a date")
    fun dateRangeOptimizationForTypedDateTimeParameter() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MyDate DateTime\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MyDate")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MyDate" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val myDate = request.dateRange as ParameterRef?
        assertThat(myDate!!.name, `is`("MyDate"))
        assertThat(myDate.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    @Disabled("TODO: This test is semantically invalid, you cannot use \"during\" with a date")
    fun dateRangeOptimizationForDateTimeExpressionReference() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define myDate : DateTime(2013, 6)\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during myDate")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during myDate" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val myDate = request.dateRange as ExpressionRef?
        assertThat(myDate!!.name, `is`("myDate"))
        assertThat(myDate.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now be null!
        assertThat(query.where, Matchers.`is`(nullValue()))
    }

    @Test
    fun dateRangeOptimizationForAndedWhere() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.length > 2 days\n" +
                "    and E.period during MeasurementPeriod")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val mp = request.dateRange as ParameterRef?
        assertThat(mp!!.name, `is`("MeasurementPeriod"))
        assertThat(mp.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now just be the greaterThanPhrase!
        val where = query.where as Greater?
        val lhs: Property = where!!.operand[0] as Property
        assertThat(lhs.scope, `is`("E"))
        assertThat(lhs.path, `is`("length"))
        assertThat(lhs.source, Matchers.`is`(nullValue()))
        val rhs: Quantity = where.operand[1] as Quantity
        assertThat(rhs.value, `is`(BigDecimal.valueOf(2)))
        assertThat(rhs.unit, `is`("days"))
    }

    @Test
    fun dateRangeOptimizationForDeeplyAndedWhere() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.length > 2 days\n" +
                "    and E.length < 14 days\n" +
                "    and (First(E.location).location as QUICK.Location).name = 'The Good Hospital'\n" +
                "    and E.period during MeasurementPeriod")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val mp = request.dateRange as ParameterRef?
        assertThat(mp!!.name, `is`("MeasurementPeriod"))
        assertThat(mp.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now be the And clauses without the during!
        val where = query.where as And?
        val lhs = where!!.operand[0] as And
        val innerLhs = lhs.operand[0] as Greater
        val gtLhs: Property = innerLhs.operand[0] as Property
        assertThat(gtLhs.scope, `is`("E"))
        assertThat(gtLhs.path, `is`("length"))
        assertThat(gtLhs.source, Matchers.`is`(nullValue()))
        val gtRhs: Quantity = innerLhs.operand[1] as Quantity
        assertThat(gtRhs.value, `is`(BigDecimal.valueOf(2)))
        assertThat(gtRhs.unit, `is`("days"))
        val innerRhs = lhs.operand[1] as Less
        val ltLhs: Property = innerRhs.operand[0] as Property
        assertThat(ltLhs.scope, `is`("E"))
        assertThat(ltLhs.path, `is`("length"))
        assertThat(ltLhs.source, Matchers.`is`(nullValue()))
        val ltRhs: Quantity = innerRhs.operand[1] as Quantity
        assertThat(ltRhs.value, `is`(BigDecimal.valueOf(14)))
        assertThat(ltRhs.unit, `is`("days"))
        val rhs = where.operand[1] as Equal
        val eqLhs: Property = rhs.operand[0] as Property
        assertThat(eqLhs.path, `is`("name"))
        val eqLhsAs = eqLhs.source as As?
        assertThat<QName>(
            (eqLhsAs!!.asTypeSpecifier as NamedTypeSpecifier).name,
            QuickDataType.quickDataType("Location"),
        )
        val eqLhsAsSource = eqLhsAs.operand as Property?
        assertThat(eqLhsAsSource!!.path, `is`("location"))
        val eqLhsAsSourceFirst = eqLhsAsSource.source as First?
        val eqLhsAsSourceFirstSource = eqLhsAsSourceFirst!!.source as Property?
        assertThat(eqLhsAsSourceFirstSource!!.scope, `is`("E"))
        assertThat(eqLhsAsSourceFirstSource.path, `is`("location"))

        //        assertThat(eqLhs.getScope(), is("E"));
        //        assertThat(eqLhs.getPath(), is("location.location.name"));
        //        assertThat(eqLhs.getSource(), is(nullValue()));
        assertThat(rhs.operand[1], literalFor("The Good Hospital"))
    }

    @Test
    fun dateRangeOptimizationForMultipleQualifyingClauses() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod\n" +
                "    and E.period during MeasurementPeriod")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request: Retrieve = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.dateProperty, `is`("period"))
        val mp = request.dateRange as ParameterRef?
        assertThat(mp!!.name, `is`("MeasurementPeriod"))
        assertThat(mp.libraryName, Matchers.`is`(nullValue()))

        // "Where" should now just be the other IncludeIn phrase!
        val where = query.where as IncludedIn?
        val lhs: Property = where!!.operand[0] as Property
        assertThat(lhs.scope, `is`("E"))
        assertThat(lhs.path, `is`("period"))
        assertThat(lhs.source, Matchers.`is`(nullValue()))
        val rhs: ParameterRef = where.operand[1] as ParameterRef
        assertThat(rhs.name, `is`("MeasurementPeriod"))
        assertThat(rhs.libraryName, Matchers.`is`(nullValue()))
    }

    @Test
    fun dateRangeOptimizationNotDoneWhenDisabled() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql, false)
        val request = query.source[0].expression as Retrieve

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.dateProperty, Matchers.`is`(nullValue()))
        assertThat(request.dateRange, Matchers.`is`(nullValue()))

        // "Where" should now just be the other IncludeIn phrase!
        val where = query.where as IncludedIn?
        val lhs: Property = where!!.operand[0] as Property
        assertThat(lhs.scope, `is`("E"))
        assertThat(lhs.path, `is`("period"))
        assertThat(lhs.source, Matchers.`is`(nullValue()))
        val rhs: ParameterRef = where.operand[1] as ParameterRef
        assertThat(rhs.name, `is`("MeasurementPeriod"))
        assertThat(rhs.libraryName, Matchers.`is`(nullValue()))
    }

    @Test
    @Disabled("TODO: This test is semantically invalid, you cannot use \"during\" with a list")
    fun dateRangeOptimizationNotDoneOnUnsupportedExpressions() {
        // NOTE: I'm not sure that the below statement is even valid without a "with" clause
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define pharyngitis : [Condition: \"Acute Pharyngitis\"]\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during pharyngitis")

        val query = testEncounterPerformanceInpatientForDateRangeOptimization(cql)
        val request = query.source[0].expression as Retrieve

        // First check the source and ensure the "during pharyngitis" didn't migrate up!
        assertThat(request.dateProperty, Matchers.`is`(nullValue()))
        assertThat(request.dateRange, Matchers.`is`(nullValue()))

        // "Where" should now be null!
        val where = query.where as IncludedIn?
        val lhs: Property = where!!.operand[0] as Property
        assertThat(lhs.scope, `is`("E"))
        assertThat(lhs.path, `is`("period"))
        assertThat(lhs.source, Matchers.`is`(nullValue()))
        val rhs: ExpressionRef = where.operand[1] as ExpressionRef
        assertThat(rhs.name, `is`("pharyngitis"))
        assertThat(rhs.libraryName, Matchers.`is`(nullValue()))
    }

    private fun testEncounterPerformanceInpatientForDateRangeOptimization(
        cql: String,
        enableDateRangeOptimization: Boolean = true,
    ): Query {
        val def = TestUtils.visitData(cql, false, enableDateRangeOptimization)
        val query = def!!.expression as Query

        val source = query.source[0]
        assertThat(source.alias, `is`("E"))
        val request = source.expression as Retrieve?
        assertThat<QName>(request!!.dataType, QuickDataType.quickDataType("Encounter"))
        assertThat(request.codeProperty, `is`("type"))
        val code = request.codes as ValueSetRef?
        assertThat(code!!.name, `is`("Inpatient"))
        assertThat(code.libraryName, Matchers.`is`(nullValue()))
        assertThat(request.idProperty, Matchers.`is`(nullValue()))
        assertThat(request.templateId, `is`("encounter-qicore-qicore-encounter"))

        return query
    }

    @Test
    @Suppress("LongMethod")
    fun complexQuery() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    with [Condition: \"Acute Pharyngitis\"] P\n" +
                "        such that Interval[P.onsetDateTime, P.abatementDate] overlaps after E.period\n" +
                "    where duration in days of E.period >= 120\n" +
                "    return Tuple { id: E.id, lengthOfStay: duration in days of E.period }\n" +
                "    sort by lengthOfStay desc")
        val def = TestUtils.visitData(cql)
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        assertThat(source.alias, `is`("E"))
        val request = source.expression as Retrieve?
        assertThat<QName>(request!!.dataType, QuickDataType.quickDataType("Encounter"))
        assertThat(request.codeProperty, `is`("type"))
        val code = request.codes as ValueSetRef?
        assertThat(code!!.name, `is`("Inpatient"))
        assertThat(code.libraryName, Matchers.`is`(nullValue()))
        assertThat(request.dateProperty, Matchers.`is`(nullValue()))
        assertThat(request.dateRange, Matchers.`is`(nullValue()))
        assertThat(request.idProperty, Matchers.`is`(nullValue()))
        assertThat(request.templateId, `is`("encounter-qicore-qicore-encounter"))

        // Then check the with statement
        assertThat(query.relationship, Matchers.hasSize(1))
        val relationship = query.relationship[0]
        assertThat(relationship, Matchers.instanceOf(With::class.java))
        assertThat(relationship.alias, `is`("P"))
        val withRequest = relationship.expression as Retrieve?
        assertThat<QName>(withRequest!!.dataType, QuickDataType.quickDataType("Condition"))
        assertThat(withRequest.codeProperty, `is`("code"))
        val withCode = withRequest.codes as ValueSetRef?
        assertThat(withCode!!.name, `is`("Acute Pharyngitis"))
        assertThat(withCode.libraryName, Matchers.`is`(nullValue()))
        assertThat(withRequest.dateProperty, Matchers.`is`(nullValue()))
        assertThat(withRequest.dateRange, Matchers.`is`(nullValue()))
        assertThat(withRequest.idProperty, Matchers.`is`(nullValue()))
        assertThat(withRequest.templateId, `is`("condition-qicore-qicore-condition"))
        val withWhere = relationship.suchThat as OverlapsAfter?
        assertThat(withWhere!!.operand, Matchers.hasSize(2))
        val overlapsLHS: Interval = withWhere.operand[0] as Interval
        assertThat(overlapsLHS.isLowClosed(), `is`<Boolean?>(true))
        val overlapsLHSLow = overlapsLHS.low as Property?
        assertThat(overlapsLHSLow!!.scope, `is`("P"))
        assertThat(overlapsLHSLow.path, `is`("onsetDateTime"))
        assertThat(overlapsLHSLow.source, Matchers.`is`(nullValue()))
        assertThat<Boolean?>(overlapsLHS.isHighClosed(), `is`(true))
        val overlapsLHSHigh = overlapsLHS.high as Property?
        assertThat(overlapsLHSHigh!!.scope, `is`("P"))
        assertThat(overlapsLHSHigh.path, `is`("abatementDate"))
        assertThat(overlapsLHSHigh.source, Matchers.`is`(nullValue()))
        val overlapsRHS: Property = withWhere.operand[1] as Property
        assertThat(overlapsRHS.scope, `is`("E"))
        assertThat(overlapsRHS.path, `is`("period"))
        assertThat(overlapsRHS.source, Matchers.`is`(nullValue()))

        // Then check where statement
        val where = query.where as GreaterOrEqual?
        assertThat(where!!.operand, Matchers.hasSize(2))
        val whereLHS = where.operand[0] as DurationBetween
        assertThat<DateTimePrecision?>(
            whereLHS.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(whereLHS.operand, Matchers.hasSize(2))
        val whereLHSBegin = whereLHS.operand[0] as Start
        val whereLHSBeginProp = whereLHSBegin.operand as Property?
        assertThat(whereLHSBeginProp!!.scope, `is`("E"))
        assertThat(whereLHSBeginProp.path, `is`("period"))
        assertThat(whereLHSBeginProp.source, Matchers.`is`(nullValue()))
        val whereLHSEnd = whereLHS.operand[1] as End
        val whereLHSEndProp = whereLHSEnd.operand as Property?
        assertThat(whereLHSEndProp!!.scope, `is`("E"))
        assertThat(whereLHSEndProp.path, `is`("period"))
        assertThat(whereLHSEndProp.source, Matchers.`is`(nullValue()))
        assertThat(where.operand[1], literalFor(120))

        // Then check the return statement
        val returnClause = query.`return`
        val rtn = returnClause!!.expression as Tuple?
        @Suppress("ForbiddenComment")
        // TODO: Bug in translator!  Doesn't detect object type (intentional?)
        // assertThat(rtn.getObjectType(), is(notNullValue()));
        assertThat(rtn!!.element, Matchers.hasSize(2))
        val rtnP1 = rtn.element[0]
        assertThat(rtnP1.name, `is`("id"))
        val rtnP1Val = rtnP1.value as Property?
        assertThat(rtnP1Val!!.scope, `is`("E"))
        assertThat(rtnP1Val.path, `is`("id"))
        assertThat(rtnP1Val.source, Matchers.`is`(nullValue()))
        val rtnP2 = rtn.element[1]
        assertThat(rtnP2.name, `is`("lengthOfStay"))
        val rtnP2Val = rtnP2.value as DurationBetween?
        assertThat<DateTimePrecision?>(
            rtnP2Val!!.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(rtnP2Val.operand, Matchers.hasSize(2))
        val rtnP2ValBegin = rtnP2Val.operand[0] as Start
        val rtnP2ValBeginProp = rtnP2ValBegin.operand as Property?
        assertThat(rtnP2ValBeginProp!!.scope, `is`("E"))
        assertThat(rtnP2ValBeginProp.path, `is`("period"))
        assertThat(rtnP2ValBeginProp.source, Matchers.`is`(nullValue()))
        val rtnP2ValEnd = rtnP2Val.operand[1] as End
        val rtnP2ValEndProp = rtnP2ValEnd.operand as Property?
        assertThat(rtnP2ValEndProp!!.scope, `is`("E"))
        assertThat(rtnP2ValEndProp.path, `is`("period"))
        assertThat(rtnP2ValEndProp.source, Matchers.`is`(nullValue()))

        // Finally test sort
        val sort = query.sort
        assertThat(sort!!.by, Matchers.hasSize(1))
        val sortBy: ByColumn = sort.by[0] as ByColumn
        assertThat(sortBy.path, `is`("lengthOfStay"))
        assertThat<SortDirection?>(sortBy.direction, `is`<SortDirection>(SortDirection.DESC))
    }

    @Test
    fun queryThatReturnsLet() {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    let a : 1\n" +
                "    return a")
        val def = TestUtils.visitData(cql)
        val query = def!!.expression as Query?

        // First check the source
        val source = query!!.source[0]
        assertThat(source.alias, `is`("E"))
        val request = source.expression as Retrieve?
        assertThat<QName>(request!!.dataType, QuickDataType.quickDataType("Encounter"))
        assertThat(request.codeProperty, `is`("type"))
        val code = request.codes as ValueSetRef?
        assertThat(code!!.name, `is`("Inpatient"))
        assertThat(code.libraryName, Matchers.`is`(nullValue()))
        assertThat(request.dateProperty, Matchers.`is`(nullValue()))
        assertThat(request.dateRange, Matchers.`is`(nullValue()))
        assertThat(request.idProperty, Matchers.`is`(nullValue()))
        assertThat(request.templateId, `is`("encounter-qicore-qicore-encounter"))

        // Then check the define
        assertThat(query.let, Matchers.hasSize(1))
        val dfc = query.let[0]
        assertThat(dfc.identifier, `is`("a"))
        assertThat(dfc.expression, literalFor(1))

        // Then check the return
        assertThat(query.`return`!!.expression, Matchers.instanceOf(QueryLetRef::class.java))
        val qdr = query.`return`!!.expression as QueryLetRef?
        assertThat(qdr!!.name, `is`("a"))

        // Then check the rest
        assertThat(query.relationship, Matchers.`is`(Matchers.empty()))
        assertThat(query.where, Matchers.`is`(nullValue()))
        assertThat<SortClause?>(query.sort, Matchers.`is`(nullValue()))
    }

    @Test
    @Throws(IOException::class)
    fun choiceAssignment() {
        val def = TestUtils.visitFile("TestChoiceAssignment.cql")
        val instance = def!!.expression as Instance?

        assertThat<QName?>(
            instance!!.classType,
            QdmDataType.qdmDataType("PositiveAssessmentPerformed"),
        )
    }

    @Test
    @Throws(IOException::class)
    fun localFunctionResolution() {
        val def = TestUtils.visitFile("LocalFunctionResolutionTest.cql")
        assertThat(def!!.expression, Matchers.instanceOf(FunctionRef::class.java))
        val functionRef = def.expression as FunctionRef?
        assertThat(functionRef!!.name, `is`("ToDate"))
    }

    @Test
    @Throws(IOException::class)
    fun union() {
        val def = TestUtils.visitFile("TestUnion.cql")

        // Union(Union(Union(Union(A, B), Union(C,D)), Union(E,F)), Union(G,H))
        val union1 = def!!.expression as Union?
        val union2 = union1!!.operand[0] as Union
        val union3 = union2.operand[0] as Union
        val union4 = union3.operand[0] as Union
        val union5 = union3.operand[1] as Union
        val union6 = union2.operand[1] as Union
        val union7 = union1.operand[1] as Union
        val a = union4.operand[0] as ExpressionRef
        val b = union4.operand[1] as ExpressionRef
        val c = union5.operand[0] as ExpressionRef
        val d = union5.operand[1] as ExpressionRef
        val e = union6.operand[0] as ExpressionRef
        val f = union6.operand[1] as ExpressionRef
        val g = union7.operand[0] as ExpressionRef
        val h: ExpressionRef = union7.operand[1] as ExpressionRef

        assertThat(a.name, `is`("A"))
        assertThat(b.name, `is`("B"))
        assertThat(c.name, `is`("C"))
        assertThat(d.name, `is`("D"))
        assertThat(e.name, `is`("E"))
        assertThat(f.name, `is`("F"))
        assertThat(g.name, `is`("G"))
        assertThat(h.name, `is`("H"))
    }

    @Test
    @Throws(IOException::class)
    fun includedIn() {
        val def = TestUtils.visitFile("TestIncludedIn.cql")
        // Query->
        //   where->
        //      In ->
        //        left -> Property
        //        right -> ParameterRef
        val query = def!!.expression as Query?
        val where = query!!.where
        assertThat(where, Matchers.instanceOf(In::class.java))
        val inExpression = where as In?
        assertThat(inExpression!!.operand[0], Matchers.instanceOf(Property::class.java))
        assertThat(inExpression.operand[1], Matchers.instanceOf(ParameterRef::class.java))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameAs() {
        val where = "P same as E"
        val sameAs = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat(sameAs.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameAs.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameAs.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameYearAs() {
        val where = "P same year as E"
        val sameYear = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameYear.precision,
            `is`<DateTimePrecision>(DateTimePrecision.YEAR),
        )
        assertThat(sameYear.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameYear.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameYear.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameMonthAs() {
        val where = "P same month as E"
        val sameMonth = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameMonth.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MONTH),
        )
        assertThat(sameMonth.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameMonth.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameMonth.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameDayAs() {
        val where = "P same day as E"
        val sameDay = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameDay.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(sameDay.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameDay.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameDay.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameHourAs() {
        val where = "P same hour as E"
        val sameHour = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameHour.precision,
            `is`<DateTimePrecision>(DateTimePrecision.HOUR),
        )
        assertThat(sameHour.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameHour.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameHour.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameMinuteAs() {
        val where = "P same minute as E"
        val sameMin = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameMin.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MINUTE),
        )
        assertThat(sameMin.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameMin.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameMin.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameSecondAs() {
        val where = "P same second as E"
        val sameSec = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameSec.precision,
            `is`<DateTimePrecision>(DateTimePrecision.SECOND),
        )
        assertThat(sameSec.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameSec.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameSec.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun sameMillisecondAs() {
        val where = "P same millisecond as E"
        val sameMS = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameMS.precision,
            `is`<DateTimePrecision>(DateTimePrecision.MILLISECOND),
        )
        assertThat(sameMS.operand, Matchers.hasSize(2))
        val lhs: AliasRef = sameMS.operand[0] as AliasRef
        assertThat(lhs.name, `is`("P"))
        val rhs: AliasRef = sameMS.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun startsSameDayAs() {
        val where = "P starts same day as E"
        val sameDay = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameDay.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(sameDay.operand, Matchers.hasSize(2))
        val lhs: Start = sameDay.operand[0] as Start
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: AliasRef = sameDay.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun startsSameDayAsStart() {
        val where = "P starts same day as start E"
        val sameDay = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameDay.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(sameDay.operand, Matchers.hasSize(2))
        val lhs: Start = sameDay.operand[0] as Start
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: Start = sameDay.operand[1] as Start
        assertThat((rhs.operand as AliasRef).name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun startsSameDayAsEnd() {
        val where = "P starts same day as end E"
        val sameDay = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameDay.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(sameDay.operand, Matchers.hasSize(2))
        val lhs: Start = sameDay.operand[0] as Start
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: End = sameDay.operand[1] as End
        assertThat((rhs.operand as AliasRef).name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun startsAtLeastSameDayAs() {
        val where = "P starts same day or after E"
        val sameOrAfter = testInpatientWithPharyngitisWhere(where) as SameOrAfter
        assertThat(sameOrAfter.operand, Matchers.hasSize(2))
        assertThat<DateTimePrecision?>(
            sameOrAfter.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        val lhs: Start = sameOrAfter.operand[0] as Start
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: AliasRef = sameOrAfter.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun startsAtMostSameDayAs() {
        val where = "P starts same day or before E"
        val sameOrBefore = testInpatientWithPharyngitisWhere(where) as SameOrBefore
        assertThat<DateTimePrecision?>(
            sameOrBefore.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        val lhs: Start = sameOrBefore.operand[0] as Start
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: AliasRef = sameOrBefore.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun endsSameDayAs() {
        val where = "P ends same day as E"
        val sameDay = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameDay.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(sameDay.operand, Matchers.hasSize(2))
        val lhs: End = sameDay.operand[0] as End
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: AliasRef = sameDay.operand[1] as AliasRef
        assertThat(rhs.name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun endsSameDayAsEnd() {
        val where = "P ends same day as end E"
        val sameDay = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameDay.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(sameDay.operand, Matchers.hasSize(2))
        val lhs: End = sameDay.operand[0] as End
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: End = sameDay.operand[1] as End
        assertThat((rhs.operand as AliasRef).name, `is`("E"))
    }

    @Test
    @Disabled("TODO: This test needs to be repurposed, it won't work with the query as is.")
    fun endsSameDayAsStart() {
        val where = "P ends same day as start E"
        val sameDay = testInpatientWithPharyngitisWhere(where) as SameAs
        assertThat<DateTimePrecision?>(
            sameDay.precision,
            `is`<DateTimePrecision>(DateTimePrecision.DAY),
        )
        assertThat(sameDay.operand, Matchers.hasSize(2))
        val lhs: End = sameDay.operand[0] as End
        assertThat((lhs.operand as AliasRef).name, `is`("P"))
        val rhs: Start = sameDay.operand[1] as Start
        assertThat((rhs.operand as AliasRef).name, `is`("E"))
    }

    private fun testInpatientWithPharyngitisWhere(withWhereClause: String?): Expression? {
        val cql =
            ("using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    with [Condition: \"Acute Pharyngitis\"] P\n" +
                "    such that " +
                withWhereClause)

        val def = TestUtils.visitData(cql)
        val query = def!!.expression as Query?
        assertThat(query!!.source[0].alias, `is`("E"))
        val request = query.source[0].expression as Retrieve
        assertThat(request.dataType!!.namespaceURI, `is`("http://org.hl7.fhir"))
        assertThat(request.dataType!!.localPart, `is`("Encounter"))
        assertThat(request.codeProperty, `is`("type"))
        val vs = request.codes as ValueSetRef?
        assertThat(vs!!.name, `is`("Inpatient"))
        assertThat(vs.libraryName, Matchers.`is`(nullValue()))
        assertThat(query.relationship, Matchers.hasSize(1))
        val with: With = query.relationship[0] as With
        assertThat(with.alias, `is`("P"))
        val withRequest = with.expression as Retrieve?
        assertThat(withRequest!!.dataType!!.namespaceURI, `is`("http://org.hl7.fhir"))
        assertThat(withRequest.dataType!!.localPart, `is`("Condition"))
        assertThat(withRequest.codeProperty, `is`("code"))
        val withVS = withRequest.codes as ValueSetRef?
        assertThat(withVS!!.name, `is`("Acute Pharyngitis"))
        assertThat(withVS.libraryName, Matchers.`is`(nullValue()))
        return with.suchThat
    }

    @Test
    @Throws(IOException::class)
    fun patientContext() {
        val library = TestUtils.visitFileLibrary("TestPatientContext.cql")
        val patient = library!!.resolveExpressionRef("Patient")
        assertThat(patient!!.expression, Matchers.instanceOf(Literal::class.java))
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun assertTrackable(t: Element?) {
        if (t == null) {
            return
        }

        val trackbacks = t.trackbacks
        val trackerId = t.trackerId
        assertThat(trackbacks, Matchers.not(Matchers.empty()))
        assertThat(trackbacks[0], Matchers.notNullValue())
        assertThat(trackerId, Matchers.notNullValue())
    }
}
