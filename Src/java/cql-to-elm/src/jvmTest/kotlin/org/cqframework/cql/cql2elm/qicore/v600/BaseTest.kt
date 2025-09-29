package org.cqframework.cql.cql2elm.qicore.v600

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.And
import org.hl7.elm.r1.CalculateAge
import org.hl7.elm.r1.CalculateAgeAt
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.Or
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ReturnClause
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.Tuple
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.With
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("LongMethod", "LargeClass", "ForbiddenComment")
internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun qICore() {
        val translator = TestUtils.runSemanticTest("qicore/v600/TestQICore.cql", 0)

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
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(CalculateAge::class.java),
        )
        val age = def.expression as CalculateAge?
        assertThat<Expression?>(
            age!!.operand,
            Matchers.instanceOf<Expression?>(Property::class.java),
        )
        var p = age.operand as Property?
        assertThat<String?>(p!!.path, `is`<String?>("value"))
        assertThat<Expression?>(p.source, Matchers.instanceOf<Expression?>(Property::class.java))
        p = p.source as Property?
        assertThat<String?>(p!!.path, `is`<String?>("birthDate"))

        def = defs["TestAgeAt"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(CalculateAgeAt::class.java),
        )
        val ageAt = def.expression as CalculateAgeAt?
        assertThat(ageAt!!.operand.size, `is`(2))
        assertThat(ageAt.operand[0], Matchers.instanceOf(Property::class.java))
        p = ageAt.operand[0] as Property?
        assertThat<String?>(p!!.path, `is`<String?>("value"))
        assertThat<Expression?>(p.source, Matchers.instanceOf<Expression?>(Property::class.java))
        p = p.source as Property?
        assertThat<String?>(p!!.path, `is`<String?>("birthDate"))

        def = defs["TestAdverseEvent"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Retrieve::class.java),
        )
        retrieve = def.expression as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-adverseevent"),
        )

        def = defs["TestSpecificCommunicationNotDone"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Union::class.java),
        )
        union = def.expression as Union?
        assertThat(union!!.operand.size, `is`(2))
        assertThat(union.operand[0], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[0] as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("~"))
        assertThat(union.operand[1], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[1] as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("contains"))

        def = defs["TestSpecificCommunicationNotDoneExplicit"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        query = def.expression as Query?
        assertThat(query!!.source.size, `is`(1))
        assertThat(query.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        retrieve = query.source[0].expression as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"
            ),
        )
        val whereClause = query.where
        assertThat<Expression?>(whereClause, Matchers.instanceOf<Expression?>(Or::class.java))
        val left: Expression = (whereClause as Or).operand[0]
        val right: Expression = whereClause.operand[1]
        assertThat(left, Matchers.instanceOf(Equivalent::class.java))
        assertThat(right, Matchers.instanceOf(InValueSet::class.java))

        def = defs["TestGeneralCommunicationNotDone"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Union::class.java),
        )
        union = def.expression as Union?
        assertThat(union!!.operand.size, `is`(2))
        assertThat(union.operand[0], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[0] as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("in"))
        assertThat(union.operand[1], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[1] as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("~"))
        assertThat<Expression?>(
            retrieve.codes,
            Matchers.instanceOf<Expression?>(ValueSetRef::class.java),
        )

        def = defs["TestGeneralDeviceNotRequested"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Union::class.java),
        )
        union = def.expression as Union?
        assertThat(union!!.operand.size, `is`(2))
        assertThat(union.operand[0], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[0] as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("in"))
        assertThat(union.operand[1], Matchers.instanceOf(Retrieve::class.java))
        retrieve = union.operand[1] as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("~"))
        assertThat<Expression?>(
            retrieve.codes,
            Matchers.instanceOf<Expression?>(ValueSetRef::class.java),
        )

        def = defs["TestGeneralDeviceNotRequestedCode"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Retrieve::class.java),
        )
        retrieve = def.expression as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("in"))
        assertThat<Expression?>(
            retrieve.codes,
            Matchers.instanceOf<Expression?>(ValueSetRef::class.java),
        )

        def = defs["TestGeneralDeviceNotRequestedValueSet"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Retrieve::class.java),
        )
        retrieve = def.expression as Retrieve?
        assertThat<String?>(
            retrieve!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"
            ),
        )
        assertThat<String?>(retrieve.codeComparator, `is`<String?>("~"))
        assertThat<Expression?>(
            retrieve.codes,
            Matchers.instanceOf<Expression?>(ValueSetRef::class.java),
        )

        def = defs["TestGeneralDeviceNotRequestedCodeExplicit"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        query = def.expression as Query?
        assertThat<Expression?>(
            query!!.where,
            Matchers.instanceOf<Expression?>(InValueSet::class.java),
        )
        val inValueSet = query.where as InValueSet?
        assertThat<Expression?>(
            inValueSet!!.code,
            Matchers.instanceOf<Expression?>(FunctionRef::class.java),
        )
        var fr = inValueSet.code as FunctionRef?
        assertThat<String?>(fr!!.libraryName, `is`<String?>("FHIRHelpers"))
        assertThat<String?>(fr.name, `is`<String?>("ToConcept"))

        def = defs["TestGeneralDeviceNotRequestedValueSetExplicit"]
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        query = def.expression as Query?
        assertThat<Expression?>(
            query!!.where,
            Matchers.instanceOf<Expression?>(Equivalent::class.java),
        )
        val eq = query.where as Equivalent?
        assertThat(eq!!.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        fr = eq.operand[0] as FunctionRef?
        assertThat<String?>(fr!!.libraryName, `is`<String?>("FHIRHelpers"))
        assertThat<String?>(fr.name, `is`<String?>("ToValueSet"))
    }

    @Test
    @Throws(IOException::class)
    fun testPCSBMIScreenAndFollowUpFHIR() {
        val translator = TestUtils.runSemanticTest("qicore/v600/PCSBMIScreenAndFollowUpFHIR.cql", 0)

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
    fun testPCSDepressionScreenAdnFollowUpFHIR() {
        val translator =
            TestUtils.runSemanticTest("qicore/v600/PCSDepressionScreenAndFollowUpFHIR.cql", 0)

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
    fun testStatinsforthePreventionandTreatmentofCardiovascularDiseaseFHIR() {
        val translator =
            TestUtils.runSemanticTest(
                "qicore/v600/StatinsforthePreventionandTreatmentofCardiovascularDiseaseFHIR.cql",
                0,
            )

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def = defs["Initial Population 1"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
    }

    // TODO: This content isn't available from measure developers for QICOre 6 yet. Update when
    // available
    // @Test
    @Throws(IOException::class)
    fun adultOutpatientEncounters() {
        val translator =
            TestUtils.runSemanticTest(
                "qicore/v600/AdultOutpatientEncounters_QICore4-2.0.000.cql",
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
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        val query = def.expression as Query?
        assertThat<Expression?>(query!!.where, Matchers.instanceOf<Expression?>(And::class.java))
        val and = query.where as And?
        assertThat(and!!.operand.size, CoreMatchers.equalTo(2))
        assertThat(and.operand[0], Matchers.instanceOf(IncludedIn::class.java))
        val includedIn = and.operand[0] as IncludedIn
        assertThat(includedIn.operand.size, CoreMatchers.equalTo(2))
        assertThat(includedIn.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        val functionRef: FunctionRef = includedIn.operand[0] as FunctionRef
        assertThat(functionRef.name, CoreMatchers.equalTo<String?>("ToInterval"))
        assertThat<String?>(functionRef.libraryName, CoreMatchers.equalTo<String?>("FHIRHelpers"))
    }

    // TODO: Update when this content is available from measure developers for QICore 6
    // @Test
    @Throws(IOException::class)
    fun papTestWithResults() {
        val translator = TestUtils.runSemanticTest("qicore/v600/EXM124_QICore4-8.2.000.cql", 0)
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
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        val q = def.expression as Query?
        assertThat<Expression?>(q!!.where, Matchers.instanceOf<Expression?>(And::class.java))
        val a = q.where as And?
        assertThat(a!!.operand[0], Matchers.instanceOf(Not::class.java))
        val n = a.operand[0] as Not
        assertThat<Expression?>(n.operand, Matchers.instanceOf<Expression?>(IsNull::class.java))
        val i = n.operand as IsNull?
        assertThat<Expression?>(
            i!!.operand,
            Matchers.instanceOf<Expression?>(FunctionRef::class.java),
        )
        val fr = i.operand as FunctionRef?
        assertThat<String?>(fr!!.libraryName, CoreMatchers.equalTo<String?>("FHIRHelpers"))
        assertThat<String?>(fr.name, CoreMatchers.equalTo<String?>("ToValue"))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        val p: Property = fr.operand[0] as Property
        assertThat(p.path, CoreMatchers.equalTo<String?>("value"))
        assertThat<String?>(p.scope, CoreMatchers.equalTo<String?>("PapTest"))
    }

    @Test
    @Throws(IOException::class)
    fun medicationRequest() {
        val translator = TestUtils.runSemanticTest("qicore/v600/TestMedicationRequest.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        var def = defs["Antithrombotic Therapy at Discharge"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        var q = def.expression as Query?
        assertThat(q!!.source.size, `is`(1))
        assertThat(q.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        var r = q.source[0].expression as Retrieve?
        assertThat<String?>(
            r!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"
            ),
        )
        assertThat<String?>(r.codeProperty, `is`<String?>("medication"))
        assertThat<String?>(r.codeComparator, `is`<String?>("in"))
        assertThat<Expression?>(r.codes, Matchers.instanceOf<Expression?>(ValueSetRef::class.java))
        var vsr = r.codes as ValueSetRef?
        assertThat<String?>(vsr!!.name, `is`<String?>("Antithrombotic Therapy"))

        def = defs["Antithrombotic Therapy at Discharge (2)"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Union::class.java),
        )
        val u = def.expression as Union?
        assertThat(u!!.operand.size, `is`(2))
        assertThat(u.operand[0], Matchers.instanceOf(Retrieve::class.java))
        r = u.operand[0] as Retrieve?
        assertThat<String?>(
            r!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"
            ),
        )
        assertThat<String?>(r.codeProperty, `is`<String?>("medication"))
        assertThat<String?>(r.codeComparator, `is`<String?>("in"))
        assertThat<Expression?>(r.codes, Matchers.instanceOf<Expression?>(ValueSetRef::class.java))
        vsr = r.codes as ValueSetRef?
        assertThat<String?>(vsr!!.name, `is`<String?>("Antithrombotic Therapy"))

        assertThat(u.operand[1], Matchers.instanceOf(Query::class.java))
        q = u.operand[1] as Query?
        assertThat(q!!.source.size, `is`(1))
        assertThat(q.source[0].expression, Matchers.instanceOf(Retrieve::class.java))
        r = q.source[0].expression as Retrieve?
        assertThat<String?>(
            r!!.templateId,
            `is`<String?>(
                "http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"
            ),
        )
        assertThat(r.codeProperty == null, `is`(true))
        assertThat(r.codes == null, `is`(true))
        assertThat<Any?>(q.relationship, CoreMatchers.notNullValue())
        assertThat(q.relationship.size, `is`(1))
        assertThat(q.relationship[0], Matchers.instanceOf(With::class.java))
        val w = q.relationship[0] as With
        assertThat<Expression?>(
            w.expression,
            Matchers.instanceOf<Expression?>(Retrieve::class.java),
        )
        r = w.expression as Retrieve?
        assertThat<String?>(
            r!!.templateId,
            `is`<String?>("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medication"),
        )
        assertThat(r.codeProperty == null, `is`(true))
        assertThat(r.codes == null, `is`(true))
        assertThat<DataType?>(r.resultType, Matchers.instanceOf<DataType?>(ListType::class.java))
        assertThat(
            (r.resultType as ListType).elementType,
            Matchers.instanceOf(ClassType::class.java),
        )
        assertThat(
            ((r.resultType as ListType).elementType as ClassType).name,
            `is`("QICore.Medication"),
        )
        assertThat<Expression?>(w.suchThat, Matchers.instanceOf<Expression?>(And::class.java))
        val a = w.suchThat as And?
        assertThat(a!!.operand[0], Matchers.instanceOf(Equal::class.java))
        val eq = a.operand[0] as Equal
        assertThat(eq.operand[0], Matchers.instanceOf(Property::class.java))
        var p = eq.operand[0] as Property?
        assertThat<String?>(p!!.scope, `is`<String?>("M"))
        assertThat<String?>(p.path, `is`<String?>("id.value"))
        assertThat(eq.operand[1], Matchers.instanceOf(Last::class.java))
        val l = eq.operand[1] as Last
        assertThat<Expression?>(l.source, Matchers.instanceOf<Expression?>(Split::class.java))
        val s = l.source as Split?
        assertThat<Expression?>(
            s!!.stringToSplit,
            Matchers.instanceOf<Expression?>(Property::class.java),
        )
        p = s.stringToSplit as Property?
        assertThat<String?>(p!!.scope, `is`<String?>("MR"))
        assertThat<String?>(p.path, `is`<String?>("medication.reference.value"))
        // assertThat(s.getSeparator(), is("/"));
        assertThat(a.operand[1], Matchers.instanceOf(InValueSet::class.java))
        val ivs: InValueSet = a.operand[1] as InValueSet
        assertThat(ivs.valueset!!.name, `is`<String?>("Antithrombotic Therapy"))
        assertThat<Expression?>(ivs.code, Matchers.instanceOf<Expression?>(FunctionRef::class.java))
        val fr = ivs.code as FunctionRef?
        assertThat<String?>(fr!!.libraryName, `is`<String?>("FHIRHelpers"))
        assertThat<String?>(fr.name, `is`<String?>("ToConcept"))
        assertThat(fr.operand.size, `is`(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        p = fr.operand[0] as Property?
        assertThat<String?>(p!!.scope, `is`<String?>("M"))
        assertThat<String?>(p.path, `is`<String?>("code"))
    }

    @Test
    @Throws(IOException::class)
    fun choiceUnion() {
        val translator = TestUtils.runSemanticTest("qicore/v600/TestChoiceUnion.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val def = defs["Union of Different Types"]
        assertThat<ExpressionDef?>(def, CoreMatchers.notNullValue())
        assertThat<Expression?>(
            def!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        val q = def.expression as Query?
        assertThat<ReturnClause?>(q!!.`return`, CoreMatchers.notNullValue())
        assertThat<Expression?>(
            q.`return`!!.expression,
            Matchers.instanceOf<Expression?>(Tuple::class.java),
        )
        val t = q.`return`!!.expression as Tuple?
        assertThat<Any?>(t!!.element, CoreMatchers.notNullValue())
        assertThat(t.element.size, `is`(2))
        val t0 = t.element[0]
        val t1 = t.element[1]
        assertThat<String?>(t0.name, `is`<String?>("performed"))
        assertThat<Expression?>(t0.value, Matchers.instanceOf<Expression?>(FunctionRef::class.java))
        val fr = t0.value as FunctionRef?
        assertThat<String?>(fr!!.name, `is`<String?>("ToValue"))
        assertThat(fr.operand.size, `is`(1))
        assertThat(fr.operand[0], Matchers.instanceOf(Property::class.java))
        var p = fr.operand[0] as Property?
        assertThat<String?>(p!!.path, `is`<String?>("performed"))
        assertThat<String?>(p.scope, `is`<String?>("R"))
        assertThat<String?>(t1.name, `is`<String?>("authoredOn"))
        assertThat<Expression?>(t1.value, Matchers.instanceOf<Expression?>(Property::class.java))
        p = t1.value as Property?
        assertThat<String?>(p!!.path, `is`<String?>("value"))
        assertThat<Expression?>(p.source, Matchers.instanceOf<Expression?>(Property::class.java))
        p = p.source as Property?
        assertThat<String?>(p!!.path, `is`<String?>("authoredOn"))
        assertThat<String?>(p.scope, `is`<String?>("R"))
    }

    // TODO: Apparently (enabled=false) doesn't work on the CI server?
    // @Test(enabled = false, description = "Signature overloads not yet working for derived
    // models")
    @Throws(IOException::class)
    fun testSignatureOnInterval() {
        val translator =
            TestUtils.runSemanticTest("qicore/v600/SupplementalDataElements-4.0.0000.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val payer = defs["SDE Payer"]

        Assertions.assertNotNull(payer)

        val query = payer!!.expression as Query?
        val t = query!!.`return`!!.expression as Tuple?
        val toInterval = t!!.element[1].value as FunctionRef

        Assertions.assertNotNull(toInterval.signature)
        assertThat(toInterval.signature.size, `is`(1))
    }

    @Test
    @Throws(IOException::class)
    fun testMedicationNotRequested() {
        val translator = TestUtils.runSemanticTest("qicore/v600/FHIRHelpersToConceptError.cql", 0)

        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef?> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
            }
        }

        val minimalRepro = defs["Minimal Repro"]
        Assertions.assertNotNull(minimalRepro)
        assertThat<Expression?>(
            minimalRepro!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        var query = minimalRepro.expression as Query?
        var returnClause = query!!.`return`
        assertThat<Expression?>(
            returnClause!!.expression,
            Matchers.instanceOf<Expression?>(Query::class.java),
        )
        query = returnClause.expression as Query?
        assertThat(query!!.source.size, CoreMatchers.equalTo(1))
        val source = query.source[0]
        assertThat(source.expression, Matchers.instanceOf(Property::class.java))
        val property: Property = source.expression as Property
        assertThat(property.path, CoreMatchers.equalTo<String?>("reasonCode"))
        assertThat<String?>(property.scope, CoreMatchers.equalTo<String?>("NoStatinTherapyOrdered"))
        returnClause = query.`return`
        assertThat<Expression?>(
            returnClause!!.expression,
            Matchers.instanceOf<Expression?>(FunctionRef::class.java),
        )
        val functionRef = returnClause.expression as FunctionRef?
        assertThat<String?>(functionRef!!.libraryName, CoreMatchers.equalTo<String?>("FHIRHelpers"))
        assertThat<String?>(functionRef.name, CoreMatchers.equalTo<String?>("ToConcept"))
    }
}
