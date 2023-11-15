package org.cqframework.cql.cql2elm.qicore.v411;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public class BaseTest {
    @Test
    public void testAuthoringPatterns() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/AuthoringPatterns.cql", 0, LibraryBuilder.SignatureLevel.Overloads);

        assertThat(translator.getWarnings().toString(), translator.getWarnings().size(), is(1));

        final String first = "An alias identifier [Diabetes] is hiding another identifier of the same name. \n";

        assertThat(translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList()), contains(first));
    }

    @Test
    public void testQICore() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/TestQICore.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = null;
        Retrieve retrieve = null;
        Union union = null;
        Query query = null;

        def = defs.get("TestAge");
        assertThat(def.getExpression(), instanceOf(CalculateAge.class));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getOperand(), instanceOf(Property.class));
        Property p = (Property)age.getOperand();
        assertThat(p.getPath(), is("value"));
        assertThat(p.getSource(), instanceOf(Property.class));
        p = (Property)p.getSource();
        assertThat(p.getPath(), is("birthDate"));

        def = defs.get("TestAgeAt");
        assertThat(def.getExpression(), instanceOf(CalculateAgeAt.class));
        CalculateAgeAt ageAt = (CalculateAgeAt)def.getExpression();
        assertThat(ageAt.getOperand().size(), is(2));
        assertThat(ageAt.getOperand().get(0), instanceOf(Property.class));
        p = (Property)ageAt.getOperand().get(0);
        assertThat(p.getPath(), is("value"));
        assertThat(p.getSource(), instanceOf(Property.class));
        p = (Property)p.getSource();
        assertThat(p.getPath(), is("birthDate"));

        def = defs.get("TestAdverseEvent");
        assertThat(def.getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve)def.getExpression();
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-adverseevent"));

        def = defs.get("TestSpecificCommunicationNotDone");
        assertThat(def.getExpression(), instanceOf(Union.class));
        union = (Union)def.getExpression();
        assertThat(union.getOperand().size(), is(2));
        assertThat(union.getOperand().get(0), instanceOf(Retrieve.class));
        retrieve = (Retrieve)union.getOperand().get(0);
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(union.getOperand().get(1), instanceOf(Retrieve.class));
        retrieve = (Retrieve)union.getOperand().get(1);
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("contains"));

        def = defs.get("TestSpecificCommunicationNotDoneExplicit");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getSource().size(), is(1));
        assertThat(query.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve)query.getSource().get(0).getExpression();
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        Expression whereClause = query.getWhere();
        assertThat(whereClause, instanceOf(Or.class));
        Expression left = ((Or)whereClause).getOperand().get(0);
        Expression right = ((Or)whereClause).getOperand().get(1);
        assertThat(left, instanceOf(Equivalent.class));
        assertThat(right, instanceOf(InValueSet.class));

        def = defs.get("TestGeneralCommunicationNotDone");
        assertThat(def.getExpression(), instanceOf(Union.class));
        union = (Union)def.getExpression();
        assertThat(union.getOperand().size(), is(2));
        assertThat(union.getOperand().get(0), instanceOf(Retrieve.class));
        retrieve = (Retrieve)union.getOperand().get(0);
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("in"));
        assertThat(union.getOperand().get(1), instanceOf(Retrieve.class));
        retrieve = (Retrieve)union.getOperand().get(1);
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequested");
        assertThat(def.getExpression(), instanceOf(Union.class));
        union = (Union)def.getExpression();
        assertThat(union.getOperand().size(), is(2));
        assertThat(union.getOperand().get(0), instanceOf(Retrieve.class));
        retrieve = (Retrieve)union.getOperand().get(0);
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("in"));
        assertThat(union.getOperand().get(1), instanceOf(Retrieve.class));
        retrieve = (Retrieve)union.getOperand().get(1);
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequestedCode");
        assertThat(def.getExpression(),instanceOf(Retrieve.class));
        retrieve = (Retrieve)def.getExpression();
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("in"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequestedValueSet");
        assertThat(def.getExpression(),instanceOf(Retrieve.class));
        retrieve = (Retrieve)def.getExpression();
        assertThat(retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequestedCodeExplicit");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(InValueSet.class));
        InValueSet inValueSet = (InValueSet)query.getWhere();
        assertThat(inValueSet.getCode(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef)inValueSet.getCode();
        assertThat(fr.getLibraryName(), is("FHIRHelpers"));
        assertThat(fr.getName(), is("ToConcept"));

        def = defs.get("TestGeneralDeviceNotRequestedValueSetExplicit");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(Equivalent.class));
        Equivalent eq = (Equivalent)query.getWhere();
        assertThat(eq.getOperand().get(0), instanceOf(FunctionRef.class));
        fr = (FunctionRef)eq.getOperand().get(0);
        assertThat(fr.getLibraryName(), is("FHIRHelpers"));
        assertThat(fr.getName(), is("ToValueSet"));

        def = defs.get("TestEncounterDiagnosisPresentOnAdmission");
        assertThat(def.getExpression(), instanceOf(Exists.class));
        Exists e = (Exists)def.getExpression();
        assertThat(e.getOperand(), instanceOf(Query.class));
        query = (Query)e.getOperand();
        assertThat(query.getWhere(), instanceOf(Equivalent.class));
        eq = (Equivalent)query.getWhere();
        assertThat(eq.getOperand().get(0), instanceOf(FunctionRef.class));
        fr = (FunctionRef)eq.getOperand().get(0);
        assertThat(fr.getLibraryName(), is("FHIRHelpers"));
        assertThat(fr.getName(), is("ToConcept"));
    }

    @Test
    public void testEXM124() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/EXM124_QICore4-8.2.000.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Initial Population");
        assertThat(def, notNullValue());
    }

    @Test
    public void testEXM165() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/EXM165_QICore4-8.5.000.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Initial Population");
        assertThat(def, notNullValue());
    }

    @Test
    public void testAdultOutpatientEncounters() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/AdultOutpatientEncounters_QICore4-2.0.000.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
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

        ExpressionDef def = defs.get("Qualifying Encounters");
        assertThat(def, notNullValue());
        assertThat(def.getExpression(), instanceOf(Query.class));
        Query query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(And.class));
        And and = (And)query.getWhere();
        assertThat(and.getOperand().size(), equalTo(2));
        assertThat(and.getOperand().get(0), instanceOf(IncludedIn.class));
        IncludedIn includedIn = (IncludedIn)and.getOperand().get(0);
        assertThat(includedIn.getOperand().size(), equalTo(2));
        assertThat(includedIn.getOperand().get(0), instanceOf(FunctionRef.class));
        FunctionRef functionRef = (FunctionRef)includedIn.getOperand().get(0);
        assertThat(functionRef.getName(), equalTo("ToInterval"));
        assertThat(functionRef.getLibraryName(), equalTo("FHIRHelpers"));
    }

    @Test
    public void testPapTestWithResults() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/EXM124_QICore4-8.2.000.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Pap Test with Results");
        assertThat(def, notNullValue());

        /*
        ExpressionDef: Pap Test with Results
            Query
                Source
                Where: And
                    Operand[0]: Not
                        Operand[0]: IsNull
                            Operand[0]: FunctionRef: FHIRHelpers.ToValue
         */
        assertThat(def.getExpression(), instanceOf(Query.class));
        Query q = (Query)def.getExpression();
        assertThat(q.getWhere(), instanceOf(And.class));
        And a = (And)q.getWhere();
        assertThat(a.getOperand().get(0), instanceOf(Not.class));
        Not n = (Not)a.getOperand().get(0);
        assertThat(n.getOperand(), instanceOf(IsNull.class));
        IsNull i = (IsNull)n.getOperand();
        assertThat(i.getOperand(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef)i.getOperand();
        assertThat(fr.getLibraryName(), equalTo("FHIRHelpers"));
        assertThat(fr.getName(), equalTo("ToValue"));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        Property p = (Property)fr.getOperand().get(0);
        assertThat(p.getPath(), equalTo("value"));
        assertThat(p.getScope(), equalTo("PapTest"));
    }

    @Test
    public void TestMedicationRequest() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/TestMedicationRequest.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Antithrombotic Therapy at Discharge");
        assertThat(def, notNullValue());
        assertThat(def.getExpression(), instanceOf(Query.class));
        Query q = (Query)def.getExpression();
        assertThat(q.getSource().size(), is(1));
        assertThat(q.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        Retrieve r = (Retrieve)q.getSource().get(0).getExpression();
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"));
        assertThat(r.getCodeProperty(), is("medication"));
        assertThat(r.getCodeComparator(), is("in"));
        assertThat(r.getCodes(), instanceOf(ValueSetRef.class));
        ValueSetRef vsr = (ValueSetRef)r.getCodes();
        assertThat(vsr.getName(), is("Antithrombotic Therapy"));

        def = defs.get("Antithrombotic Therapy at Discharge (2)");
        assertThat(def, notNullValue());
        assertThat(def.getExpression(), instanceOf(Union.class));
        Union u = (Union)def.getExpression();
        assertThat(u.getOperand().size(), is(2));
        assertThat(u.getOperand().get(0), instanceOf(Retrieve.class));
        r = (Retrieve)u.getOperand().get(0);
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"));
        assertThat(r.getCodeProperty(), is("medication"));
        assertThat(r.getCodeComparator(), is("in"));
        assertThat(r.getCodes(), instanceOf(ValueSetRef.class));
        vsr = (ValueSetRef)r.getCodes();
        assertThat(vsr.getName(), is("Antithrombotic Therapy"));

        assertThat(u.getOperand().get(1), instanceOf(Query.class));
        q = (Query)u.getOperand().get(1);
        assertThat(q.getSource().size(), is(1));
        assertThat(q.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        r = (Retrieve)q.getSource().get(0).getExpression();
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"));
        assertThat(r.getCodeProperty() == null, is(true));
        assertThat(r.getCodes() == null, is(true));
        assertThat(q.getRelationship(), notNullValue());
        assertThat(q.getRelationship().size(), is(1));
        assertThat(q.getRelationship().get(0), instanceOf(With.class));
        With w = (With)q.getRelationship().get(0);
        assertThat(w.getExpression(), instanceOf(Retrieve.class));
        r = (Retrieve)w.getExpression();
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medication"));
        assertThat(r.getCodeProperty() == null, is(true));
        assertThat(r.getCodes() == null, is(true));
        assertThat(w.getSuchThat(), instanceOf(And.class));
        And a = (And)w.getSuchThat();
        assertThat(a.getOperand().get(0), instanceOf(Equal.class));
        assertThat(a.getOperand().get(1), instanceOf(InValueSet.class));
        InValueSet ivs = (InValueSet)a.getOperand().get(1);
        assertThat(ivs.getValueset().getName(), is("Antithrombotic Therapy"));
    }

    @Test
    public void TestChoiceUnion() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/TestChoiceUnion.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Union of Different Types");
        assertThat(def, notNullValue());
        assertThat(def.getExpression(), instanceOf(Query.class));
        Query q = (Query)def.getExpression();
        assertThat(q.getReturn(), notNullValue());
        assertThat(q.getReturn().getExpression(), instanceOf(Tuple.class));
        Tuple t = (Tuple)q.getReturn().getExpression();
        assertThat(t.getElement(), notNullValue());
        assertThat(t.getElement().size(), is(2));
        TupleElement t0 = t.getElement().get(0);
        TupleElement t1 = t.getElement().get(1);
        assertThat(t0.getName(), is("performed"));
        assertThat(t0.getValue(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef)t0.getValue();
        assertThat(fr.getName(), is("ToValue"));
        assertThat(fr.getOperand().size(), is(1));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        Property p = (Property)fr.getOperand().get(0);
        assertThat(p.getPath(), is("performed"));
        assertThat(p.getScope(), is("R"));
        assertThat(t1.getName(), is("authoredOn"));
        assertThat(t1.getValue(), instanceOf(Property.class));
        p = (Property)t1.getValue();
        assertThat(p.getPath(), is("value"));
        assertThat(p.getSource(), instanceOf(Property.class));
        p = (Property)p.getSource();
        assertThat(p.getPath(), is("authoredOn"));
        assertThat(p.getScope(), is("R"));
    }

    // TODO: Apparently (enabled=false) doesn't work on the CI server?
    // @Test(enabled = false, description = "Signature overloads not yet working for derived models")
    public void TestSignatureOnInterval() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v411/SupplementalDataElements_QICore4-2.0.0.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        var payer = defs.get("SDE Payer");

        assertNotNull(payer);

        var query = (Query)payer.getExpression();
        var t = (Tuple)query.getReturn().getExpression();
        var toInterval = (FunctionRef)t.getElement().get(1).getValue();

        assertNotNull(toInterval.getSignature());
        assertThat(toInterval.getSignature().size(), is(1));
    }
}
