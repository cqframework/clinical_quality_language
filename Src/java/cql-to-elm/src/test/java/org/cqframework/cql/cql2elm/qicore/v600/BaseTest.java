package org.cqframework.cql.cql2elm.qicore.v600;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.cql.model.ClassType;
import org.hl7.cql.model.ListType;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.Test;

class BaseTest {
    @Test
    void qICore() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v600/TestQICore.cql", 0);

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
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getOperand(), instanceOf(Property.class));
        Property p = (Property) age.getOperand();
        assertThat(p.getPath(), is("value"));
        assertThat(p.getSource(), instanceOf(Property.class));
        p = (Property) p.getSource();
        assertThat(p.getPath(), is("birthDate"));

        def = defs.get("TestAgeAt");
        assertThat(def.getExpression(), instanceOf(CalculateAgeAt.class));
        CalculateAgeAt ageAt = (CalculateAgeAt) def.getExpression();
        assertThat(ageAt.getOperand().size(), is(2));
        assertThat(ageAt.getOperand().get(0), instanceOf(Property.class));
        p = (Property) ageAt.getOperand().get(0);
        assertThat(p.getPath(), is("value"));
        assertThat(p.getSource(), instanceOf(Property.class));
        p = (Property) p.getSource();
        assertThat(p.getPath(), is("birthDate"));

        def = defs.get("TestAdverseEvent");
        assertThat(def.getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve) def.getExpression();
        assertThat(
                retrieve.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-adverseevent"));

        def = defs.get("TestSpecificCommunicationNotDone");
        assertThat(def.getExpression(), instanceOf(Union.class));
        union = (Union) def.getExpression();
        assertThat(union.getOperand().size(), is(2));
        assertThat(union.getOperand().get(0), instanceOf(Retrieve.class));
        retrieve = (Retrieve) union.getOperand().get(0);
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(union.getOperand().get(1), instanceOf(Retrieve.class));
        retrieve = (Retrieve) union.getOperand().get(1);
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("contains"));

        def = defs.get("TestSpecificCommunicationNotDoneExplicit");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query) def.getExpression();
        assertThat(query.getSource().size(), is(1));
        assertThat(query.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve) query.getSource().get(0).getExpression();
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        Expression whereClause = query.getWhere();
        assertThat(whereClause, instanceOf(Or.class));
        Expression left = ((Or) whereClause).getOperand().get(0);
        Expression right = ((Or) whereClause).getOperand().get(1);
        assertThat(left, instanceOf(Equivalent.class));
        assertThat(right, instanceOf(InValueSet.class));

        def = defs.get("TestGeneralCommunicationNotDone");
        assertThat(def.getExpression(), instanceOf(Union.class));
        union = (Union) def.getExpression();
        assertThat(union.getOperand().size(), is(2));
        assertThat(union.getOperand().get(0), instanceOf(Retrieve.class));
        retrieve = (Retrieve) union.getOperand().get(0);
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("in"));
        assertThat(union.getOperand().get(1), instanceOf(Retrieve.class));
        retrieve = (Retrieve) union.getOperand().get(1);
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-communicationnotdone"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequested");
        assertThat(def.getExpression(), instanceOf(Union.class));
        union = (Union) def.getExpression();
        assertThat(union.getOperand().size(), is(2));
        assertThat(union.getOperand().get(0), instanceOf(Retrieve.class));
        retrieve = (Retrieve) union.getOperand().get(0);
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("in"));
        assertThat(union.getOperand().get(1), instanceOf(Retrieve.class));
        retrieve = (Retrieve) union.getOperand().get(1);
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequestedCode");
        assertThat(def.getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve) def.getExpression();
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("in"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequestedValueSet");
        assertThat(def.getExpression(), instanceOf(Retrieve.class));
        retrieve = (Retrieve) def.getExpression();
        assertThat(
                retrieve.getTemplateId(),
                is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-devicenotrequested"));
        assertThat(retrieve.getCodeComparator(), is("~"));
        assertThat(retrieve.getCodes(), instanceOf(ValueSetRef.class));

        def = defs.get("TestGeneralDeviceNotRequestedCodeExplicit");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query) def.getExpression();
        assertThat(query.getWhere(), instanceOf(InValueSet.class));
        InValueSet inValueSet = (InValueSet) query.getWhere();
        assertThat(inValueSet.getCode(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef) inValueSet.getCode();
        assertThat(fr.getLibraryName(), is("FHIRHelpers"));
        assertThat(fr.getName(), is("ToConcept"));

        def = defs.get("TestGeneralDeviceNotRequestedValueSetExplicit");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query) def.getExpression();
        assertThat(query.getWhere(), instanceOf(Equivalent.class));
        Equivalent eq = (Equivalent) query.getWhere();
        assertThat(eq.getOperand().get(0), instanceOf(FunctionRef.class));
        fr = (FunctionRef) eq.getOperand().get(0);
        assertThat(fr.getLibraryName(), is("FHIRHelpers"));
        assertThat(fr.getName(), is("ToValueSet"));
    }

    @Test
    void testPCSBMIScreenAndFollowUpFHIR() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v600/PCSBMIScreenAndFollowupFHIR.cql", 0);

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
    void testPCSDepressionScreenAdnFollowUpFHIR() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v600/PCSDepressionScreenAndFollowUPFHIR.cql", 0);

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
    void testStatinsforthePreventionandTreatmentofCardiovascularDiseaseFHIR() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v600/StatinsforthePreventionandTreatmentofCardiovascularDiseaseFHIR.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("Initial Population 1");
        assertThat(def, notNullValue());
    }

    //TODO: This content isn't available from measure developers for QICOre 6 yet. Update when available
    //@Test
    void adultOutpatientEncounters() throws IOException {
        CqlTranslator translator =
                TestUtils.runSemanticTest("qicore/v600/AdultOutpatientEncounters_QICore4-2.0.000.cql", 0);
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
        Query query = (Query) def.getExpression();
        assertThat(query.getWhere(), instanceOf(And.class));
        And and = (And) query.getWhere();
        assertThat(and.getOperand().size(), equalTo(2));
        assertThat(and.getOperand().get(0), instanceOf(IncludedIn.class));
        IncludedIn includedIn = (IncludedIn) and.getOperand().get(0);
        assertThat(includedIn.getOperand().size(), equalTo(2));
        assertThat(includedIn.getOperand().get(0), instanceOf(FunctionRef.class));
        FunctionRef functionRef = (FunctionRef) includedIn.getOperand().get(0);
        assertThat(functionRef.getName(), equalTo("ToInterval"));
        assertThat(functionRef.getLibraryName(), equalTo("FHIRHelpers"));
    }

    // TODO: Update when this content is available from measure developers for QICore 6
    //@Test
    void papTestWithResults() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v600/EXM124_QICore4-8.2.000.cql", 0);
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
        Query q = (Query) def.getExpression();
        assertThat(q.getWhere(), instanceOf(And.class));
        And a = (And) q.getWhere();
        assertThat(a.getOperand().get(0), instanceOf(Not.class));
        Not n = (Not) a.getOperand().get(0);
        assertThat(n.getOperand(), instanceOf(IsNull.class));
        IsNull i = (IsNull) n.getOperand();
        assertThat(i.getOperand(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef) i.getOperand();
        assertThat(fr.getLibraryName(), equalTo("FHIRHelpers"));
        assertThat(fr.getName(), equalTo("ToValue"));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        Property p = (Property) fr.getOperand().get(0);
        assertThat(p.getPath(), equalTo("value"));
        assertThat(p.getScope(), equalTo("PapTest"));
    }

    @Test
    void medicationRequest() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v600/TestMedicationRequest.cql", 0);
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
        Query q = (Query) def.getExpression();
        assertThat(q.getSource().size(), is(1));
        assertThat(q.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        Retrieve r = (Retrieve) q.getSource().get(0).getExpression();
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"));
        assertThat(r.getCodeProperty(), is("medication"));
        assertThat(r.getCodeComparator(), is("in"));
        assertThat(r.getCodes(), instanceOf(ValueSetRef.class));
        ValueSetRef vsr = (ValueSetRef) r.getCodes();
        assertThat(vsr.getName(), is("Antithrombotic Therapy"));

        def = defs.get("Antithrombotic Therapy at Discharge (2)");
        assertThat(def, notNullValue());
        assertThat(def.getExpression(), instanceOf(Union.class));
        Union u = (Union) def.getExpression();
        assertThat(u.getOperand().size(), is(2));
        assertThat(u.getOperand().get(0), instanceOf(Retrieve.class));
        r = (Retrieve) u.getOperand().get(0);
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"));
        assertThat(r.getCodeProperty(), is("medication"));
        assertThat(r.getCodeComparator(), is("in"));
        assertThat(r.getCodes(), instanceOf(ValueSetRef.class));
        vsr = (ValueSetRef) r.getCodes();
        assertThat(vsr.getName(), is("Antithrombotic Therapy"));

        assertThat(u.getOperand().get(1), instanceOf(Query.class));
        q = (Query) u.getOperand().get(1);
        assertThat(q.getSource().size(), is(1));
        assertThat(q.getSource().get(0).getExpression(), instanceOf(Retrieve.class));
        r = (Retrieve) q.getSource().get(0).getExpression();
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medicationrequest"));
        assertThat(r.getCodeProperty() == null, is(true));
        assertThat(r.getCodes() == null, is(true));
        assertThat(q.getRelationship(), notNullValue());
        assertThat(q.getRelationship().size(), is(1));
        assertThat(q.getRelationship().get(0), instanceOf(With.class));
        With w = (With) q.getRelationship().get(0);
        assertThat(w.getExpression(), instanceOf(Retrieve.class));
        r = (Retrieve) w.getExpression();
        assertThat(r.getTemplateId(), is("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-medication"));
        assertThat(r.getCodeProperty() == null, is(true));
        assertThat(r.getCodes() == null, is(true));
        assertThat(r.getResultType(), instanceOf(ListType.class));
        assertThat(((ListType) r.getResultType()).getElementType(), instanceOf(ClassType.class));
        assertThat(((ClassType) ((ListType) r.getResultType()).getElementType()).getName(), is("QICore.Medication"));
        assertThat(w.getSuchThat(), instanceOf(And.class));
        And a = (And) w.getSuchThat();
        assertThat(a.getOperand().get(0), instanceOf(Equal.class));
        Equal eq = (Equal) a.getOperand().get(0);
        assertThat(eq.getOperand().get(0), instanceOf(Property.class));
        Property p = (Property) eq.getOperand().get(0);
        assertThat(p.getScope(), is("M"));
        assertThat(p.getPath(), is("id.value"));
        assertThat(eq.getOperand().get(1), instanceOf(Last.class));
        Last l = (Last) eq.getOperand().get(1);
        assertThat(l.getSource(), instanceOf(Split.class));
        Split s = (Split) l.getSource();
        assertThat(s.getStringToSplit(), instanceOf(Property.class));
        p = (Property) s.getStringToSplit();
        assertThat(p.getScope(), is("MR"));
        assertThat(p.getPath(), is("medication.reference.value"));
        // assertThat(s.getSeparator(), is("/"));
        assertThat(a.getOperand().get(1), instanceOf(InValueSet.class));
        InValueSet ivs = (InValueSet) a.getOperand().get(1);
        assertThat(ivs.getValueset().getName(), is("Antithrombotic Therapy"));
        assertThat(ivs.getCode(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef) ivs.getCode();
        assertThat(fr.getLibraryName(), is("FHIRHelpers"));
        assertThat(fr.getName(), is("ToConcept"));
        assertThat(fr.getOperand().size(), is(1));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        p = (Property) fr.getOperand().get(0);
        assertThat(p.getScope(), is("M"));
        assertThat(p.getPath(), is("code"));
    }

    @Test
    void choiceUnion() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v600/TestChoiceUnion.cql", 0);
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
        Query q = (Query) def.getExpression();
        assertThat(q.getReturn(), notNullValue());
        assertThat(q.getReturn().getExpression(), instanceOf(Tuple.class));
        Tuple t = (Tuple) q.getReturn().getExpression();
        assertThat(t.getElement(), notNullValue());
        assertThat(t.getElement().size(), is(2));
        TupleElement t0 = t.getElement().get(0);
        TupleElement t1 = t.getElement().get(1);
        assertThat(t0.getName(), is("performed"));
        assertThat(t0.getValue(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef) t0.getValue();
        assertThat(fr.getName(), is("ToValue"));
        assertThat(fr.getOperand().size(), is(1));
        assertThat(fr.getOperand().get(0), instanceOf(Property.class));
        Property p = (Property) fr.getOperand().get(0);
        assertThat(p.getPath(), is("performed"));
        assertThat(p.getScope(), is("R"));
        assertThat(t1.getName(), is("authoredOn"));
        assertThat(t1.getValue(), instanceOf(Property.class));
        p = (Property) t1.getValue();
        assertThat(p.getPath(), is("value"));
        assertThat(p.getSource(), instanceOf(Property.class));
        p = (Property) p.getSource();
        assertThat(p.getPath(), is("authoredOn"));
        assertThat(p.getScope(), is("R"));
    }

    // TODO: Apparently (enabled=false) doesn't work on the CI server?
    // @Test(enabled = false, description = "Signature overloads not yet working for derived models")
    public void testSignatureOnInterval() throws IOException {
        CqlTranslator translator =
                TestUtils.runSemanticTest("qicore/v600/SupplementalDataElements-4.0.0000.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        var payer = defs.get("SDE Payer");

        assertNotNull(payer);

        var query = (Query) payer.getExpression();
        var t = (Tuple) query.getReturn().getExpression();
        var toInterval = (FunctionRef) t.getElement().get(1).getValue();

        assertNotNull(toInterval.getSignature());
        assertThat(toInterval.getSignature().size(), is(1));
    }

    @Test
    public void testMedicationNotRequested() throws IOException {
        CqlTranslator translator =
                TestUtils.runSemanticTest("qicore/v600/FHIRHelpersToConceptError.cql", 0);

        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        var minimalRepro = defs.get("Minimal Repro");
        assertNotNull(minimalRepro);
        assertThat(minimalRepro.getExpression(), instanceOf(Query.class));
        var query = (Query)minimalRepro.getExpression();
        var returnClause = query.getReturn();
        assertThat(returnClause.getExpression(), instanceOf(Query.class));
        query = (Query)returnClause.getExpression();
        assertThat(query.getSource().size(), equalTo(1));
        var source = query.getSource().get(0);
        assertThat(source.getExpression(), instanceOf(Property.class));
        var property = (Property)source.getExpression();
        assertThat(property.getPath(), equalTo("reasonCode"));
        assertThat(property.getScope(), equalTo("NoStatinTherapyOrdered"));
        returnClause = query.getReturn();
        assertThat(returnClause.getExpression(), instanceOf(FunctionRef.class));
        var functionRef = (FunctionRef)returnClause.getExpression();
        assertThat(functionRef.getLibraryName(), equalTo("FHIRHelpers"));
        assertThat(functionRef.getName(), equalTo("ToConcept"));
    }
}
