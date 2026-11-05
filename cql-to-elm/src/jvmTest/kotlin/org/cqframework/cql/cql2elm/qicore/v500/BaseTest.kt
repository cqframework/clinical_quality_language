package org.cqframework.cql.cql2elm.qicore.v500

import java.io.IOException
import java.util.stream.Collectors
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.TestUtils
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.And
import org.hl7.elm.r1.CalculateAge
import org.hl7.elm.r1.CalculateAgeAt
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.Or
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.ValueSetRef
import org.junit.jupiter.api.Test

@Suppress("LongMethod")
internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun qICoreCommon() {
        TestUtils.runSemanticTest("qicore/v500/QICoreCommon-2.0.000.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun cqmCommon() {
        TestUtils.runSemanticTest("qicore/v500/CQMCommon-2.0.000.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun authoringPatterns() {
        val translator =
            TestUtils.runSemanticTest(
                "qicore/v500/AuthoringPatterns.cql",
                0,
                LibraryBuilder.SignatureLevel.Overloads,
            )

        assertThat(translator.warnings.toString(), translator.warnings.size, `is`(1))

        val first = "An alias identifier Diabetes is hiding another identifier of the same name."

        assertThat(
            translator.warnings
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList()),
            Matchers.contains(first),
        )
    }

    @Test
    @Throws(IOException::class)
    fun qICore() {
        val translator = TestUtils.runSemanticTest("qicore/v500/TestQICore.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }
        var retrieve: Retrieve?
        var union: Union?
        var query: Query?

        var def = defs["TestAge"]
        assertThat(def!!.expression, Matchers.instanceOf(CalculateAge::class.java))
        val age = def.expression as CalculateAge?
        assertThat(age!!.operand, Matchers.instanceOf(Property::class.java))
        var p = age.operand as Property?
        assertThat(p!!.path, `is`("value"))
        assertThat(p.source, Matchers.instanceOf(Property::class.java))
        p = p.source as Property?
        assertThat(p!!.path, `is`("birthDate"))

        def = defs["TestAgeAt"]
        assertThat(def!!.expression, Matchers.instanceOf(CalculateAgeAt::class.java))
        val ageAt = def.expression as CalculateAgeAt?
        assertThat(ageAt!!.operand.size, `is`(2))
        assertThat(ageAt.operand[0], Matchers.instanceOf(Property::class.java))
        p = ageAt.operand[0] as Property?
        assertThat(p!!.path, `is`("value"))
        assertThat(p.source, Matchers.instanceOf(Property::class.java))
        p = p.source as Property?
        assertThat(p!!.path, `is`("birthDate"))

        def = defs["TestAdverseEvent"]
        assertThat(def!!.expression, Matchers.instanceOf(Retrieve::class.java))
        retrieve = def.expression as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-adverseevent"),
        )

        def = defs["TestSpecificCommunicationNotDone"]
        assertThat(def!!.expression, Matchers.instanceOf(Union::class.java))
        union = def.expression as Union?
        assertThat(union!!.operand.size, `is`(2))
        assertThat(union.operand[0], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[0] as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"),
        )
        assertThat(retrieve.codeComparator, `is`("~"))
        assertThat(union.operand[1], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[1] as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"),
        )
        assertThat(retrieve.codeComparator, `is`("contains"))

        def = defs["TestSpecificCommunicationNotDoneExplicit"]
        assertThat(def!!.expression, Matchers.instanceOf(Query::class.java))
        query = def.expression as Query?
        assertThat(query!!.source.size, `is`(1))
        assertThat(query.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        retrieve = query.source[0].expression as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"),
        )
        val whereClause = query.where
        assertThat(whereClause, Matchers.instanceOf(Or::class.java))
        val left: Expression = (whereClause as Or).operand[0]
        val right: Expression = whereClause.operand[1]
        assertThat(left, Matchers.instanceOf(Equivalent::class.java))
        assertThat(right, Matchers.instanceOf(InValueSet::class.java))

        def = defs["TestGeneralCommunicationNotDone"]
        assertThat(def!!.expression, Matchers.instanceOf(Union::class.java))
        union = def.expression as Union?
        assertThat(union!!.operand.size, `is`(2))
        assertThat(union.operand[0], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[0] as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"),
        )
        assertThat(retrieve.codeComparator, `is`("in"))
        assertThat(union.operand[1], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[1] as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"),
        )
        assertThat(retrieve.codeComparator, `is`("~"))
        assertThat(retrieve.codes, Matchers.instanceOf(ValueSetRef::class.java))

        def = defs["TestGeneralDeviceNotRequested"]
        assertThat(def!!.expression, Matchers.instanceOf(Union::class.java))
        union = def.expression as Union?
        assertThat(union!!.operand.size, `is`(2))
        assertThat(union.operand[0], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[0] as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"),
        )
        assertThat(retrieve.codeComparator, `is`("in"))
        assertThat(union.operand[1], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[1] as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"),
        )
        assertThat(retrieve.codeComparator, `is`("~"))
        assertThat(retrieve.codes, Matchers.instanceOf(ValueSetRef::class.java))

        def = defs["TestGeneralDeviceNotRequestedCode"]
        assertThat(def!!.expression, Matchers.instanceOf(Retrieve::class.java))
        retrieve = def.expression as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"),
        )
        assertThat(retrieve.codeComparator, `is`("in"))
        assertThat(retrieve.codes, Matchers.instanceOf(ValueSetRef::class.java))

        def = defs["TestGeneralDeviceNotRequestedValueSet"]
        assertThat(def!!.expression, Matchers.instanceOf(Retrieve::class.java))
        retrieve = def.expression as Retrieve?
        assertThat(
            retrieve!!.templateId,
            `is`("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"),
        )
        assertThat(retrieve.codeComparator, `is`("~"))
        assertThat(retrieve.codes, Matchers.instanceOf(ValueSetRef::class.java))

        def = defs["TestGeneralDeviceNotRequestedCodeExplicit"]
        assertThat(def!!.expression, Matchers.instanceOf(Query::class.java))
        query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(InValueSet::class.java))
        val inValueSet = query.where as InValueSet?
        assertThat(inValueSet!!.code, Matchers.instanceOf(FunctionRef::class.java))
        var fr = inValueSet.code as FunctionRef?
        assertThat(fr!!.libraryName, `is`("FHIRHelpers"))
        assertThat(fr.name, `is`("ToConcept"))

        def = defs["TestGeneralDeviceNotRequestedValueSetExplicit"]
        assertThat(def!!.expression, Matchers.instanceOf(Query::class.java))
        query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(Equivalent::class.java))
        val eq = query.where as Equivalent?
        assertThat(eq!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        fr = eq.operand[0] as FunctionRef?
        assertThat(fr!!.libraryName, `is`("FHIRHelpers"))
        assertThat(fr.name, `is`("ToValueSet"))
    }

    @Test
    @Throws(IOException::class)
    fun exm124() {
        val translator = TestUtils.runSemanticTest("qicore/v411/EXM124_QICore4-8.2.000.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def = defs["Initial Population"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
    }

    @Test
    @Throws(IOException::class)
    fun exm165() {
        val translator = TestUtils.runSemanticTest("qicore/v411/EXM165_QICore4-8.5.000.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def = defs["Initial Population"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
    }

    @Test
    @Throws(IOException::class)
    fun adultOutpatientEncounters() {
        val translator =
            TestUtils.runSemanticTest(
                "qicore/v411/AdultOutpatientEncounters_QICore4-2.0.000.cql",
                0,
            )
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        /*
        ExpressionDef
          expression: Query
            where: And
              operand[0]: IncludedIn
                operand[0]: FunctionRef
                  name: ToInterval
                  libraryName: FHIRHelpers

         */
        val def = defs["Qualifying Encounters"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
        assertThat(def!!.expression, Matchers.instanceOf(Query::class.java))
        val query = def.expression as Query?
        assertThat(query!!.where, Matchers.instanceOf(And::class.java))
        val and = query.where as And?
        assertThat(and!!.operand.size, CoreMatchers.equalTo(2))
        assertThat(and.operand[0], Matchers.instanceOf(IncludedIn::class.java))
        val includedIn = and.operand[0] as IncludedIn
        assertThat(includedIn.operand.size, CoreMatchers.equalTo(2))
        assertThat(includedIn.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = includedIn.operand[0] as FunctionRef
        assertThat(functionRef.name, CoreMatchers.equalTo("ToInterval"))
        assertThat(functionRef.libraryName, CoreMatchers.equalTo("FHIRHelpers"))
    }

    @Test
    @Throws(IOException::class)
    fun papTestWithResults() {
        val translator = TestUtils.runSemanticTest("qicore/v411/EXM124_QICore4-8.2.000.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def = defs["Pap Test with Results"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())

        /*
        ExpressionDef: Pap Test with Results
            Query
                Source
                Where: And
                    Operand[0]: Not
                        Operand[0]: IsNull
                            Operand[0]: FunctionRef: FHIRHelpers.ToValue
         */
        assertThat(def!!.expression, Matchers.instanceOf(Query::class.java))
        val q = def.expression as Query?
        assertThat(q!!.where, Matchers.instanceOf(And::class.java))
        val a = q.where as And?
        assertThat(a!!.operand[0], Matchers.instanceOf(Not::class.java))
        val n = a.operand[0] as Not
        assertThat(n.operand, Matchers.instanceOf(IsNull::class.java))
        val i = n.operand as IsNull?
        assertThat(i!!.operand, Matchers.instanceOf(FunctionRef::class.java))
        val fr = i.operand as FunctionRef?
        assertThat(fr!!.libraryName, CoreMatchers.equalTo("FHIRHelpers"))
        assertThat(fr.name, CoreMatchers.equalTo("ToValue"))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        val p: Property = fr.operand[0] as Property
        assertThat(p.path, CoreMatchers.equalTo("value"))
        assertThat(p.scope, CoreMatchers.equalTo("PapTest"))
    }
}
