package org.cqframework.cql.poc.translator;

import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.poc.translator.model.Identifier;
import org.hl7.elm.r1.Add;
import org.hl7.elm.r1.AliasedQuerySource;
import org.hl7.elm.r1.And;
import org.hl7.elm.r1.Begin;
import org.hl7.elm.r1.BinaryExpression;
import org.hl7.elm.r1.ByExpression;
import org.hl7.elm.r1.ClinicalRequest;
import org.hl7.elm.r1.DaysBetween;
import org.hl7.elm.r1.Divide;
import org.hl7.elm.r1.End;
import org.hl7.elm.r1.Equal;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.ExpressionRef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.Greater;
import org.hl7.elm.r1.GreaterOrEqual;
import org.hl7.elm.r1.IncludedIn;
import org.hl7.elm.r1.Interval;
import org.hl7.elm.r1.IsNull;
import org.hl7.elm.r1.Less;
import org.hl7.elm.r1.LessOrEqual;
import org.hl7.elm.r1.Modulo;
import org.hl7.elm.r1.Multiply;
import org.hl7.elm.r1.Not;
import org.hl7.elm.r1.NotEqual;
import org.hl7.elm.r1.Null;
import org.hl7.elm.r1.ObjectExpression;
import org.hl7.elm.r1.Or;
import org.hl7.elm.r1.OverlapsAfter;
import org.hl7.elm.r1.ParameterRef;
import org.hl7.elm.r1.Power;
import org.hl7.elm.r1.Property;
import org.hl7.elm.r1.PropertyExpression;
import org.hl7.elm.r1.Quantity;
import org.hl7.elm.r1.Query;
import org.hl7.elm.r1.RelationshipClause;
import org.hl7.elm.r1.RequestCardinality;
import org.hl7.elm.r1.SortClause;
import org.hl7.elm.r1.SortDirection;
import org.hl7.elm.r1.Subtract;
import org.hl7.elm.r1.ValueSetRef;
import org.hl7.elm.r1.With;
import org.hl7.elm.r1.Xor;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.poc.translator.TestUtils.*;
import static org.cqframework.cql.poc.translator.matchers.LiteralFor.literalFor;
import static org.cqframework.cql.poc.translator.matchers.QuickDataType.quickDataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ElmTranslatorTest {
    @Test
    public void testLet(){
        ExpressionDef def = (ExpressionDef) visitData("define b = true");
        assertThat(def.getName(), is("b"));
        assertTrackable(def);
    }

    @Test
    public void testBooleanLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define b = true");
        assertThat(def.getExpression(), literalFor(true));
        assertTrackable(def.getExpression());

        def = (ExpressionDef) visitData("define b = false");
        assertThat(def.getExpression(), literalFor(false));
    }

    @Test
    public void testStringLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define st = 'hey its a string'");
        assertThat(def.getExpression(), literalFor("hey its a string"));
        assertTrackable(def.getExpression());
    }

    @Test
    public void testNullLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define st = null");
        assertThat(def.getExpression(), instanceOf(Null.class));
        assertTrackable(def.getExpression());
    }

    @Test
    public void testQuantityLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define st = 1");
        assertThat(def.getExpression(), literalFor(1));
        assertTrackable(def.getExpression());

        def = (ExpressionDef) visitData("define st = 1.1");
        assertThat(def.getExpression(), literalFor(1.1));

        def = (ExpressionDef) visitData("define st = 1.1 u'mm'");
        Quantity quantity = (Quantity) def.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(1.1)));
        assertThat(quantity.getUnit(), is("u'mm'"));
        assertTrackable(quantity);

        def = (ExpressionDef) visitData("define st = 1.1 weeks");
        quantity = (Quantity) def.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(1.1)));
        assertThat(quantity.getUnit(), is("weeks"));
    }

    @Test
    public void testAndExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define st = true and false");
        And and = (And) def.getExpression();
        Expression left = and.getOperand().get(0);
        Expression right = and.getOperand().get(1);

        assertThat(left, literalFor(true));
        assertThat(right, literalFor(false));

        assertTrackable(and);
        assertTrackable(left);
        assertTrackable(right);
    }

    @Test
    public void testOrExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define st = true or false");
        Or or = (Or) def.getExpression();
        Expression left = or.getOperand().get(0);
        Expression right = or.getOperand().get(1);

        assertThat(left, literalFor(true));
        assertThat(right, literalFor(false));

        assertTrackable(or);
        assertTrackable(left);
        assertTrackable(right);

        def = (ExpressionDef) visitData("define st = true xor false");
        Xor xor = (Xor) def.getExpression();
        left = xor.getOperand().get(0);
        right = xor.getOperand().get(1);

        assertThat(left, literalFor(true));
        assertThat(right, literalFor(false));

        assertTrackable(or);
        assertTrackable(left);
        assertTrackable(right);
    }

    @Test
    public void testComparisonExpressions() {
        Map<String, Class> comparisons = new HashMap<String, Class>() {{
            put("<", Less.class);
            put("<=", LessOrEqual.class);
            put("=", Equal.class);
            put(">=", GreaterOrEqual.class);
            put(">", Greater.class);
            put("<>", NotEqual.class);
        }};

        for (Map.Entry<String, Class> e : comparisons.entrySet()) {
            ExpressionDef def = (ExpressionDef) visitData("define st = 1 " + e.getKey() + " 2");
            BinaryExpression binary = (BinaryExpression) def.getExpression();
            Expression left = binary.getOperand().get(0);
            Expression right = binary.getOperand().get(1);

            assertThat(binary, instanceOf(e.getValue()));
            assertThat(left, literalFor(1));
            assertThat(right, literalFor(2));

            assertTrackable(binary);
            assertTrackable(left);
            assertTrackable(right);
        }
    }

    @Test
    public void testIsTrueExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define st = X is true");
        Equal equal = (Equal) def.getExpression();
        Identifier left = (Identifier) equal.getOperand().get(0);
        Expression right = equal.getOperand().get(1);

        assertThat(left.getIdentifier(), is("X"));
        assertThat(right, literalFor(true));

        assertTrackable(equal);
        assertTrackable(left);
        //assertTrackable(right);
    }

    @Test
    public void testIsNotTrueExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define st = X is not true");
        Not not = (Not) def.getExpression();
        Equal equal = (Equal) not.getOperand();
        Identifier left = (Identifier) equal.getOperand().get(0);
        Expression right = equal.getOperand().get(1);

        assertThat(left.getIdentifier(), is("X"));
        assertThat(right, literalFor(true));

        assertTrackable(not);
        //assertTrackable(equal);
        assertTrackable(left);
        //assertTrackable(right);
    }

    @Test
    public void testIsNullExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define st = X is null");
        IsNull isNull = (IsNull) def.getExpression();
        Identifier id = (Identifier) isNull.getOperand();

        assertThat(id.getIdentifier(), is("X"));

        assertTrackable(isNull);
        assertTrackable(id);
    }

    @Test
    public void testIsNotNullExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define st = X is not null");
        Not not = (Not) def.getExpression();
        IsNull isNull = (IsNull) not.getOperand();
        Identifier id = (Identifier) isNull.getOperand();

        assertThat(id.getIdentifier(), is("X"));

        assertTrackable(not);
        //assertTrackable(isNull);
        assertTrackable(id);
    }

    @Test
    public void testExpressionReference() {
        String cql =
                "define X = [Condition]\n" +
                "define st = X";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ExpressionRef exp = (ExpressionRef) def.getExpression();
        assertThat(exp.getName(), is("X"));
        assertThat(exp.getLibraryName(), is(nullValue()));
        assertThat(exp.getDescription(), is(nullValue()));
    }

    @Test
    public void testPropertyReference() {
        String cql =
                "define X = [Condition]\n" +
                "define st = X.effectiveTime";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        Property prop = (Property) def.getExpression();
        ExpressionRef source = (ExpressionRef) prop.getSource();
        assertThat(source.getName(), is("X"));
        assertThat(source.getLibraryName(), is(nullValue()));
        assertThat(source.getDescription(), is(nullValue()));
        assertThat(prop.getPath(), is("effectiveTime"));
        assertThat(prop.getScope(), is(nullValue()));
        assertThat(prop.getDescription(), is(nullValue()));
    }

    @Test
    public void testValueSetReference() {
        String cql =
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "define st = \"Acute Pharyngitis\"";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ValueSetRef vs = (ValueSetRef) def.getExpression();
        assertThat(vs.getName(), is("Acute Pharyngitis"));
        assertThat(vs.getLibraryName(), is(nullValue()));
        assertThat(vs.getDescription(), is(nullValue()));
    }

    @Test
    public void testFunctionReference() {
        String cql =
                "define function MyFunction() { return true }\n" +
                "define st = MyFunction()";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        FunctionRef fun = (FunctionRef) def.getExpression();
        assertThat(fun.getName(), is("MyFunction"));
        assertThat(fun.getLibraryName(), is(nullValue()));
        assertThat(fun.getDescription(), is(nullValue()));
        assertThat(fun.getOperand(), is(empty()));
    }

    @Test
    public void testFunctionReferenceWithArguments() {
        String cql =
                "define function MyFunction(arg: String) { return arg }\n" +
                "define st = MyFunction('hello there')";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        FunctionRef fun = (FunctionRef) def.getExpression();
        assertThat(fun.getName(), is("MyFunction"));
        assertThat(fun.getLibraryName(), is(nullValue()));
        assertThat(fun.getDescription(), is(nullValue()));
        assertThat(fun.getOperand(), hasSize(1));
        assertThat(fun.getOperand().get(0), literalFor("hello there"));
    }

    // TODO: Tests for accessors to expressions, valuesets, functions from included libraries

    @Test
    public void testArithmeticExpressions() {
        Map<String, Class> comparisons = new HashMap<String, Class>() {{
            put("+", Add.class);
            put("-", Subtract.class);
            put("*", Multiply.class);
            put("/", Divide.class);
            put("^", Power.class);
            put("mod", Modulo.class);
        }};

        for (Map.Entry<String, Class> e : comparisons.entrySet()) {
            ExpressionDef def = (ExpressionDef) visitData("define st = 1 " + e.getKey() + " 2");
            BinaryExpression binary = (BinaryExpression) def.getExpression();
            Expression left = binary.getOperand().get(0);
            Expression right = binary.getOperand().get(1);

            assertThat(binary, instanceOf(e.getValue()));
            assertThat(left, literalFor(1));
            assertThat(right, literalFor(2));

            assertTrackable(binary);
            assertTrackable(left);
            assertTrackable(right);
        }
    }

    @Test
    public void testRetrieveTopic() {
        ExpressionDef def = (ExpressionDef) visitData("define st = [Condition]");
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("ConditionOccurrence"));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getCodeProperty(), is(nullValue()));
        assertThat(request.getCodes(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        // TODO: What is templateId for?
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test(enabled=false)
    public void testRetrieveNonOccurrenceOfTopic() {
        ExpressionDef def = (ExpressionDef) visitData("define st = [NonOccurrence of Condition]");
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("ConditionOccurrence"));
        assertThat(request.getCodeProperty(), is(nullValue()));
        assertThat(request.getCodes(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getCardinality(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testRetrieveTopicAndModality() {
        ExpressionDef def = (ExpressionDef) visitData("define st = [Encounter, Performance]");
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getCodeProperty(), is(nullValue()));
        assertThat(request.getCodes(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test(enabled=false)
    public void testRetrieveNonOccurrenceOfTopicAndModality() {
        ExpressionDef def = (ExpressionDef) visitData("define st = [NonOccurrence of Encounter, Performance]");
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceNonOccurrence"));
        assertThat(request.getCodeProperty(), is(nullValue()));
        assertThat(request.getCodes(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getCardinality(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testRetrieveTopicAndModalityAndOccurrence() {
        ExpressionDef def = (ExpressionDef) visitData("define st = [Occurrence of Encounter, Performance]");
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getCodeProperty(), is(nullValue()));
        assertThat(request.getCodes(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testRetrieveTopicAndValueSet() {
        String cql =
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "define st = [Condition: \"Acute Pharyngitis\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("ConditionOccurrence"));
        assertThat(request.getCodeProperty(), is("code"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Acute Pharyngitis"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testRetrieveTopicAndModalityAndValueSet() {
        String cql =
                "valueset \"Ambulatory/ED Visit\" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')\n" +
                "define st = [Encounter, Performance: \"Ambulatory/ED Visit\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Ambulatory/ED Visit"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test(enabled = false)
    public void testRetrieveNonOccurrenceOfTopicAndModalityAndValueSet() {
        String cql =
                "valueset \"Ambulatory/ED Visit\" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')\n" +
                "define st = [NonOccurrence of Encounter, Performance: \"Ambulatory/ED Visit\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceNonOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Ambulatory/ED Visit"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getCardinality(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testRetrieveTopicAndModalityAndOccurrenceAndValueSet() {
        String cql =
                "valueset \"Ambulatory/ED Visit\" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')\n" +
                "define st = [Occurrence of Encounter, Performance: \"Ambulatory/ED Visit\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Ambulatory/ED Visit"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testRetrieveTopicAndSpecifiedCodeAttribute() {
        String cql =
                "valueset \"Moderate or Severe\" = ValueSet('2.16.840.1.113883.3.526.3.1092')\n" +
                "define st = [Condition: severity in \"Moderate or Severe\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("ConditionOccurrence"));
        assertThat(request.getCodeProperty(), is("severity"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Moderate or Severe"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testRetrieveTopicAndModalityAndSpecifiedCodeAttribute() {
        String cql =
                "valueset \"CABG Surgeries\" = ValueSet('2.16.840.1.113883.3.666.5.694')\n" +
                "define st = [Encounter, Performance: actionPerformed in \"CABG Surgeries\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ClinicalRequest request = (ClinicalRequest) def.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("actionPerformed"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("CABG Surgeries"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDateIntervalLiteral() {
        String cql =
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.effectiveTime during interval[Date(2013, 1, 1), Date(2014, 1, 1))";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during interval[Date(2013, 1, 1), Date(2014, 1, 1))" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        Interval ivl = (Interval) request.getDateRange();
        assertFalse(ivl.isBeginOpen());
        assertTrue(ivl.isEndOpen());
        FunctionRef ivlBegin = (FunctionRef) ivl.getBegin();
        assertThat(ivlBegin.getName(), is("Date"));
        assertThat(ivlBegin.getOperand(), hasSize(3));
        assertThat(ivlBegin.getOperand().get(0), literalFor(2013));
        assertThat(ivlBegin.getOperand().get(1), literalFor(1));
        assertThat(ivlBegin.getOperand().get(2), literalFor(1));
        FunctionRef ivlEnd = (FunctionRef) ivl.getEnd();
        assertThat(ivlEnd.getName(), is("Date"));
        assertThat(ivlEnd.getOperand(), hasSize(3));
        assertThat(ivlEnd.getOperand().get(0), literalFor(2014));
        assertThat(ivlEnd.getOperand().get(1), literalFor(1));
        assertThat(ivlEnd.getOperand().get(2), literalFor(1));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDateIntervalLiteralAndImpliedDateRangeProperty() {
        String cql =
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                        "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                        "    where E during interval[Date(2013, 1, 1), Date(2014, 1, 1))";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during interval[Date(2013, 1, 1), Date(2014, 1, 1))" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("performanceTime"));
        Interval ivl = (Interval) request.getDateRange();
        assertFalse(ivl.isBeginOpen());
        assertTrue(ivl.isEndOpen());
        FunctionRef ivlBegin = (FunctionRef) ivl.getBegin();
        assertThat(ivlBegin.getName(), is("Date"));
        assertThat(ivlBegin.getOperand(), hasSize(3));
        assertThat(ivlBegin.getOperand().get(0), literalFor(2013));
        assertThat(ivlBegin.getOperand().get(1), literalFor(1));
        assertThat(ivlBegin.getOperand().get(2), literalFor(1));
        FunctionRef ivlEnd = (FunctionRef) ivl.getEnd();
        assertThat(ivlEnd.getName(), is("Date"));
        assertThat(ivlEnd.getOperand(), hasSize(3));
        assertThat(ivlEnd.getOperand().get(0), literalFor(2014));
        assertThat(ivlEnd.getOperand().get(1), literalFor(1));
        assertThat(ivlEnd.getOperand().get(2), literalFor(1));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDefaultedDateIntervalParameter() {
        String cql =
            "parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))\n" +
            "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
            "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
            "    where E.effectiveTime during MeasurementPeriod";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));
        assertThat(mp.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForTypedDateIntervalParameter() {
        String cql =
                "parameter MeasurementPeriod : interval<DateTime>\n" +
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.effectiveTime during MeasurementPeriod";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));
        assertThat(mp.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDateIntervalExpressionReference() {
        String cql =
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "define twentyThirteen = interval[Date(2013, 1, 1), Date(2014, 1, 1))\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.effectiveTime during twentyThirteen";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during twentyThirteen" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ExpressionRef ivl = (ExpressionRef) request.getDateRange();
        assertThat(ivl.getName(), is("twentyThirteen"));
        assertThat(ivl.getLibraryName(), is(nullValue()));
        assertThat(ivl.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDateTimeLiteral() {
        String cql =
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                        "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                        "    where E.effectiveTime during Date(2013, 6)";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during Date(2013, 6)" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        FunctionRef dtFun = (FunctionRef) request.getDateRange();
        assertThat(dtFun.getName(), is("Date"));
        assertThat(dtFun.getOperand(), hasSize(2));
        assertThat(dtFun.getOperand().get(0), literalFor(2013));
        assertThat(dtFun.getOperand().get(1), literalFor(6));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDefaultedDateTimeParameter() {
        String cql =
                "parameter MyDate default Date(2013, 6)\n" +
                        "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                        "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                        "    where E.effectiveTime during MyDate";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MyDate" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ParameterRef myDate = (ParameterRef) request.getDateRange();
        assertThat(myDate.getName(), is("MyDate"));
        assertThat(myDate.getLibraryName(), is(nullValue()));
        assertThat(myDate.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForTypedDateTimeParameter() {
        String cql =
                "parameter MyDate : DateTime\n" +
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.effectiveTime during MyDate";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MyDate" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ParameterRef myDate = (ParameterRef) request.getDateRange();
        assertThat(myDate.getName(), is("MyDate"));
        assertThat(myDate.getLibraryName(), is(nullValue()));
        assertThat(myDate.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDateTimeExpressionReference() {
        String cql =
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "define myDate = Date(2013, 6)\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.effectiveTime during myDate";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during myDate" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ExpressionRef myDate = (ExpressionRef) request.getDateRange();
        assertThat(myDate.getName(), is("myDate"));
        assertThat(myDate.getLibraryName(), is(nullValue()));
        assertThat(myDate.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForAndedWhere() {
        String cql =
                "parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))\n" +
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.length > 2 days\n" +
                "    and E.effectiveTime during MeasurementPeriod";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));
        assertThat(mp.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now just be the greaterThanPhrase!
        Greater where = (Greater) query.getWhere();
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("length"));
        assertThat(lhs.getSource(), is(nullValue()));
        Quantity rhs = (Quantity) where.getOperand().get(1);
        assertThat(rhs.getValue(), is(BigDecimal.valueOf(2)));
        assertThat(rhs.getUnit(), is("days"));
        assertThat(where.getDescription(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDeeplyAndedWhere() {
        String cql =
                "parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))\n" +
                        "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                        "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                        "    where E.length > 2 days\n" +
                        "    and E.length < 14 days\n" +
                        "    and E.location.name = 'The Good Hospital'\n" +
                        "    and E.effectiveTime during MeasurementPeriod";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("effectiveTime"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));
        assertThat(mp.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be the And clauses without the during!
        And where = (And) query.getWhere();
        And lhs = (And) where.getOperand().get(0);
        Greater innerLhs = (Greater) lhs.getOperand().get(0);
        Property gtLhs = (Property) innerLhs.getOperand().get(0);
        assertThat(gtLhs.getScope(), is("E"));
        assertThat(gtLhs.getPath(), is("length"));
        assertThat(gtLhs.getSource(), is(nullValue()));
        Quantity gtRhs = (Quantity) innerLhs.getOperand().get(1);
        assertThat(gtRhs.getValue(), is(BigDecimal.valueOf(2)));
        assertThat(gtRhs.getUnit(), is("days"));
        Less innerRhs = (Less) lhs.getOperand().get(1);
        Property ltLhs = (Property) innerRhs.getOperand().get(0);
        assertThat(ltLhs.getScope(), is("E"));
        assertThat(ltLhs.getPath(), is("length"));
        assertThat(ltLhs.getSource(), is(nullValue()));
        Quantity ltRhs = (Quantity) innerRhs.getOperand().get(1);
        assertThat(ltRhs.getValue(), is(BigDecimal.valueOf(14)));
        assertThat(ltRhs.getUnit(), is("days"));
        Equal rhs = (Equal) where.getOperand().get(1);
        Property eqLhs = (Property) rhs.getOperand().get(0);
        assertThat(eqLhs.getScope(), is("E"));
        assertThat(eqLhs.getPath(), is("location.name"));
        assertThat(eqLhs.getSource(), is(nullValue()));
        assertThat(rhs.getOperand().get(1), literalFor("The Good Hospital"));
        assertThat(where.getDescription(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForMultipleQualifyingClauses() {
        String cql =
                "parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))\n" +
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.performanceTime during MeasurementPeriod\n" +
                "    and E.effectiveTime during MeasurementPeriod";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is("performanceTime"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));
        assertThat(mp.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now just be the other IncludeIn phrase!
        IncludedIn where = (IncludedIn) query.getWhere();
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("effectiveTime"));
        assertThat(lhs.getSource(), is(nullValue()));
        ParameterRef rhs = (ParameterRef) where.getOperand().get(1);
        assertThat(rhs.getName(), is("MeasurementPeriod"));
        assertThat(rhs.getLibraryName(), is(nullValue()));
        assertThat(rhs.getDescription(), is(nullValue()));
        assertThat(where.getDescription(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationNotDoneWhenDisabled() {
        String cql =
                "parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))\n" +
                        "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                        "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                        "    where E.effectiveTime during MeasurementPeriod";

        ExpressionDef def = (ExpressionDef) visitData(cql);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now just be the other IncludeIn phrase!
        IncludedIn where = (IncludedIn) query.getWhere();
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("effectiveTime"));
        assertThat(lhs.getSource(), is(nullValue()));
        ParameterRef rhs = (ParameterRef) where.getOperand().get(1);
        assertThat(rhs.getName(), is("MeasurementPeriod"));
        assertThat(rhs.getLibraryName(), is(nullValue()));
        assertThat(rhs.getDescription(), is(nullValue()));
        assertThat(where.getDescription(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationNotDoneOnUnsupportedExpressions() {
        // NOTE: I'm not sure that the below statement is even valid without a "with" clause
        String cql =
                "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "define pharyngitis = [Condition: \"Acute Pharyngitis\"]\n" +
                "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
                "    where E.effectiveTime during pharyngitis";

        ExpressionDef def = (ExpressionDef) visitData(cql, false, true);
        Query query = (Query) def.getExpression();

        // First check the source and ensure the "during pharnyngitis" didn't migrate up!
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // "Where" should now be null!
        IncludedIn where = (IncludedIn) query.getWhere();
        assertThat(where.getDescription(), is(nullValue()));
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("effectiveTime"));
        assertThat(lhs.getSource(), is(nullValue()));
        ExpressionRef rhs = (ExpressionRef) where.getOperand().get(1);
        assertThat(rhs.getName(), is("pharyngitis"));
        assertThat(rhs.getLibraryName(), is(nullValue()));
        assertThat(rhs.getDescription(), is(nullValue()));
    }

    @Test
    public void testComplexQuery() {
        String cql =
            "parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))\n" +
            "valueset \"Inpatient\" = ValueSet('2.16.840.1.113883.3.666.5.307')\n" +
            "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
            "define st = [Encounter, Performance: \"Inpatient\"] E\n" +
            "    with [Condition: \"Acute Pharyngitis\"] P\n" +
            "        where P.effectiveTime overlaps after E.performanceTime\n" +
            "    where duration in days of E.performanceTime >= 120\n" +
            "    return tuple { id: E.id, lengthOfStay: duration in days of E.performanceTime }\n" +
            "    sort by lengthOfStay desc";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        Query query = (Query) def.getExpression();

        // First check the source
        AliasedQuerySource source = query.getSource();
        assertThat(source.getAlias(), is("E"));
        ClinicalRequest request = (ClinicalRequest) source.getExpression();
        assertThat(request.getDataType(), quickDataType("EncounterPerformanceOccurrence"));
        assertThat(request.getCodeProperty(), is("class"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(code.getDescription(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getDescription(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getSubjectProperty(), is(nullValue()));
        assertThat(request.getSubject(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is(nullValue()));

        // Then check the with statement
        assertThat(query.getRelationship(), hasSize(1));
        RelationshipClause relationship = query.getRelationship().get(0);
        assertThat(relationship, instanceOf(With.class));
        assertThat(relationship.getAlias(), is("P"));
        ClinicalRequest withRequest = (ClinicalRequest) relationship.getExpression();
        assertThat(withRequest.getDataType(), quickDataType("ConditionOccurrence"));
        assertThat(withRequest.getCodeProperty(), is("code"));
        ValueSetRef withCode = (ValueSetRef) withRequest.getCodes();
        assertThat(withCode.getName(), is("Acute Pharyngitis"));
        assertThat(request.getCardinality(), is(RequestCardinality.MULTIPLE));
        assertThat(withCode.getLibraryName(), is(nullValue()));
        assertThat(withCode.getDescription(), is(nullValue()));
        assertThat(withRequest.getDateProperty(), is(nullValue()));
        assertThat(withRequest.getDateRange(), is(nullValue()));
        assertThat(withRequest.getDescription(), is(nullValue()));
        assertThat(withRequest.getScope(), is(nullValue()));
        assertThat(withRequest.getSubjectProperty(), is(nullValue()));
        assertThat(withRequest.getSubject(), is(nullValue()));
        assertThat(withRequest.getIdProperty(), is(nullValue()));
        assertThat(withRequest.getTemplateId(), is(nullValue()));
        OverlapsAfter withWhere = (OverlapsAfter) relationship.getWhere();
        assertThat(withWhere.getDescription(), is(nullValue()));
        assertThat(withWhere.getOperand(), hasSize(2));
        Property overlapsLHS = (Property) withWhere.getOperand().get(0);
        assertThat(overlapsLHS.getScope(), is("P"));
        assertThat(overlapsLHS.getPath(), is("effectiveTime"));
        assertThat(overlapsLHS.getSource(), is(nullValue()));
        assertThat(overlapsLHS.getDescription(), is(nullValue()));
        Property overlapsRHS = (Property) withWhere.getOperand().get(1);
        assertThat(overlapsRHS.getScope(), is("E"));
        assertThat(overlapsRHS.getPath(), is("performanceTime"));
        assertThat(overlapsRHS.getSource(), is(nullValue()));
        assertThat(overlapsRHS.getDescription(), is(nullValue()));

        // Then check where statement
        GreaterOrEqual where = (GreaterOrEqual) query.getWhere();
        assertThat(where.getDescription(), is(nullValue()));
        assertThat(where.getOperand(), hasSize(2));
        DaysBetween whereLHS = (DaysBetween) where.getOperand().get(0);
        assertThat(whereLHS.getDescription(), is(nullValue()));
        assertThat(whereLHS.getOperand(), hasSize(2));
        Begin whereLHSBegin = (Begin) whereLHS.getOperand().get(0);
        assertThat(whereLHSBegin.getDescription(), is(nullValue()));
        Property whereLHSBeginProp = (Property) whereLHSBegin.getOperand();
        assertThat(whereLHSBeginProp.getScope(), is("E"));
        assertThat(whereLHSBeginProp.getPath(), is("performanceTime"));
        assertThat(whereLHSBeginProp.getSource(), is(nullValue()));
        assertThat(whereLHSBeginProp.getDescription(), is(nullValue()));
        End whereLHSEnd = (End) whereLHS.getOperand().get(1);
        assertThat(whereLHSEnd.getDescription(), is(nullValue()));
        Property whereLHSEndProp = (Property) whereLHSEnd.getOperand();
        assertThat(whereLHSEndProp.getScope(), is("E"));
        assertThat(whereLHSEndProp.getPath(), is("performanceTime"));
        assertThat(whereLHSEndProp.getSource(), is(nullValue()));
        assertThat(whereLHSEndProp.getDescription(), is(nullValue()));
        assertThat(where.getOperand().get(1), literalFor(120));

        // Then check the return statement
        ObjectExpression rtn = (ObjectExpression) query.getReturn();
        assertThat(rtn.getDescription(), is(nullValue()));
        // TODO: Bug in translator!  Doesn't detect object type (intentional?)
        // assertThat(rtn.getObjectType(), is(notNullValue()));
        assertThat(rtn.getProperty(), hasSize(2));
        PropertyExpression rtnP1 = rtn.getProperty().get(0);
        assertThat(rtnP1.getName(), is("id"));
        Property rtnP1Val = (Property) rtnP1.getValue();
        assertThat(rtnP1Val.getScope(), is("E"));
        assertThat(rtnP1Val.getPath(), is("id"));
        assertThat(rtnP1Val.getSource(), is(nullValue()));
        assertThat(rtnP1Val.getDescription(), is(nullValue()));
        PropertyExpression rtnP2 = rtn.getProperty().get(1);
        assertThat(rtnP2.getName(), is("lengthOfStay"));
        DaysBetween rtnP2Val = (DaysBetween) rtnP2.getValue();
        assertThat(rtnP2Val.getDescription(), is(nullValue()));
        assertThat(rtnP2Val.getOperand(), hasSize(2));
        Begin rtnP2ValBegin = (Begin) rtnP2Val.getOperand().get(0);
        assertThat(rtnP2ValBegin.getDescription(), is(nullValue()));
        Property rtnP2ValBeginProp = (Property) rtnP2ValBegin.getOperand();
        assertThat(rtnP2ValBeginProp.getScope(), is("E"));
        assertThat(rtnP2ValBeginProp.getPath(), is("performanceTime"));
        assertThat(rtnP2ValBeginProp.getSource(), is(nullValue()));
        assertThat(rtnP2ValBeginProp.getDescription(), is(nullValue()));
        End rtnP2ValEnd = (End) rtnP2Val.getOperand().get(1);
        assertThat(rtnP2ValEnd.getDescription(), is(nullValue()));
        Property rtnP2ValEndProp = (Property) rtnP2ValEnd.getOperand();
        assertThat(rtnP2ValEndProp.getScope(), is("E"));
        assertThat(rtnP2ValEndProp.getPath(), is("performanceTime"));
        assertThat(rtnP2ValEndProp.getSource(), is(nullValue()));
        assertThat(rtnP2ValEndProp.getDescription(), is(nullValue()));

        // Finally test sort
        SortClause sort = query.getSort();
        assertThat(sort.getBy(), hasSize(1));
        // TODO: Confirm this should not be ByColumn
        ByExpression sortBy = (ByExpression) sort.getBy().get(0);
        // TODO: Should this really be using Identifier class here?
        Identifier id = (Identifier) sortBy.getExpression();
        assertThat(id.getIdentifier(), is("lengthOfStay"));
        assertThat(id.getLibraryName(), is(nullValue()));
        assertThat(sortBy.getDirection(), is(SortDirection.DESC));
    }

    private void assertTrackable(Trackable t){
        if(t == null){
            return;
        }
        assertThat(t.getTrackbacks(), not(empty()));
        assertThat(t.getTrackbacks().get(0), notNullValue());
        assertThat(t.getTrackerId(), notNullValue());
    }
}
