package org.cqframework.cql.cql2elm.qicore.v500;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {
    @Test
    public void testQICoreCommon() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v500/QICoreCommon-2.0.000.cql", 0);
    }

    @Test
    public void testCQMCommon() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v500/CQMCommon-2.0.000.cql", 0);
    }

    @Test
    public void testAuthoringPatterns() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("qicore/v500/AuthoringPatterns.cql", 0, LibraryBuilder.SignatureLevel.Overloads);

        assertThat(translator.getWarnings().toString(), translator.getWarnings().size(), is(1));

        final String first = "An alias identifier [Diabetes] is hiding another identifier of the same name. \n";

        assertThat(translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList()), contains(first));
    }

    @Test
    public void testQICore() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qicore/v500/TestQICore.cql", 0);

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
}
