package org.cqframework.cql.cql2elm.qicore.v600;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

public class BaseTest {
    @Test
    public void testQICore() throws IOException {
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
}
