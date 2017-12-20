package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.TestUtils.*;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.cqframework.cql.cql2elm.matchers.QuickDataType.quickDataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class Cql2ElmVisitorTest {
    @Test
    public void testLet(){
        ExpressionDef def = (ExpressionDef) visitData("define b : true");
        assertThat(def.getName(), is("b"));
        assertTrackable(def);
    }

    @Test
    public void testBooleanLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define b : true");
        assertThat(def.getExpression(), literalFor(true));
        assertTrackable(def.getExpression());

        def = (ExpressionDef) visitData("define b : false");
        assertThat(def.getExpression(), literalFor(false));
    }

    @Test
    public void testStringLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define st : 'hey its a string'");
        assertThat(def.getExpression(), literalFor("hey its a string"));
        assertTrackable(def.getExpression());
    }

    @Test
    public void testNullLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define st : null");
        assertThat(def.getExpression(), instanceOf(Null.class));
        assertTrackable(def.getExpression());
    }

    @Test
    public void testQuantityLiteral(){
        ExpressionDef def = (ExpressionDef) visitData("define st : 1");
        assertThat(def.getExpression(), literalFor(1));
        assertTrackable(def.getExpression());

        def = (ExpressionDef) visitData("define st : 1.1");
        assertThat(def.getExpression(), literalFor(1.1));

        def = (ExpressionDef) visitData("define st : 1.1 'mm'");
        Quantity quantity = (Quantity) def.getExpression();
        assertThat(quantity.getValue(), is(BigDecimal.valueOf(1.1)));
        assertThat(quantity.getUnit(), is("mm"));
        assertTrackable(quantity);
    }

    @Test
    public void testAndExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define st : true and false");
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
        ExpressionDef def = (ExpressionDef) visitData("define st : true or false");
        Or or = (Or) def.getExpression();
        Expression left = or.getOperand().get(0);
        Expression right = or.getOperand().get(1);

        assertThat(left, literalFor(true));
        assertThat(right, literalFor(false));

        assertTrackable(or);
        assertTrackable(left);
        assertTrackable(right);

        def = (ExpressionDef) visitData("define st : true xor false");
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
        }};

        for (Map.Entry<String, Class> e : comparisons.entrySet()) {
            ExpressionDef def = (ExpressionDef) visitData("define st : 1 " + e.getKey() + " 2");
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
    public void testNotEqualExpression() {
        ExpressionDef def = (ExpressionDef)visitData("define st : 1 != 2");
        Not not = (Not)def.getExpression();
        Equal equal = (Equal)not.getOperand();
        Expression left = equal.getOperand().get(0);
        Expression right = equal.getOperand().get(1);

        assertThat(left, literalFor(1));
        assertThat(right, literalFor(2));

        //assertTrackable(not);
        //assertTrackable(equal);
        assertTrackable(left);
        assertTrackable(right);
    }

    @Test
    public void testIsTrueExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define X : true\ndefine st : X is true");
        IsTrue isTrue = (IsTrue)def.getExpression();
        ExpressionRef left = (ExpressionRef) isTrue.getOperand();

        assertThat(left.getName(), is("X"));

        assertTrackable(isTrue);
        assertTrackable(left);
    }

    @Test
    public void testIsNotTrueExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define X : true\ndefine st : X is not true");
        Not not = (Not) def.getExpression();
        IsTrue isTrue = (IsTrue) not.getOperand();
        ExpressionRef left = (ExpressionRef) isTrue.getOperand();

        assertThat(left.getName(), is("X"));

        assertTrackable(not);
        assertTrackable(left);
    }

    @Test
    public void testIsNullExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define X : 1\ndefine st : X is null");
        IsNull isNull = (IsNull) def.getExpression();
        ExpressionRef id = (ExpressionRef) isNull.getOperand();

        assertThat(id.getName(), is("X"));

        assertTrackable(isNull);
        assertTrackable(id);
    }

    @Test
    public void testIsNotNullExpressions(){
        ExpressionDef def = (ExpressionDef) visitData("define X : 1\ndefine st : X is not null");
        Not not = (Not) def.getExpression();
        IsNull isNull = (IsNull) not.getOperand();
        ExpressionRef id = (ExpressionRef) isNull.getOperand();

        assertThat(id.getName(), is("X"));

        assertTrackable(not);
        //assertTrackable(isNull);
        assertTrackable(id);
    }

    @Test
    public void testExpressionReference() {
        String cql =
                "using QUICK\n" +
                "define X : [Condition]\n" +
                "define st : X";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ExpressionRef exp = (ExpressionRef) def.getExpression();
        assertThat(exp.getName(), is("X"));
        assertThat(exp.getLibraryName(), is(nullValue()));
    }

    @Test
    public void testPropertyReference() {
        String cql =
                "using QUICK\n" +
                "define X : First([Condition])\n" +
                "define st : X.onsetDateTime";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        Property prop = (Property) def.getExpression();
        ExpressionRef source = (ExpressionRef) prop.getSource();
        assertThat(source.getName(), is("X"));
        assertThat(source.getLibraryName(), is(nullValue()));
        assertThat(prop.getPath(), is("onsetDateTime"));
        assertThat(prop.getScope(), is(nullValue()));
    }

    @Test
    public void testValueSetReference() {
        String cql =
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define st : \"Acute Pharyngitis\"";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        ValueSetRef vs = (ValueSetRef) def.getExpression();
        assertThat(vs.getName(), is("Acute Pharyngitis"));
        assertThat(vs.getLibraryName(), is(nullValue()));
    }

    @Test
    public void testInValueSetExpression() {
        String cql =
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define m : 'Value' in \"Acute Pharyngitis\"";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        InValueSet ivs = (InValueSet)def.getExpression();
        assertThat(ivs.getValueset().getName(), is("Acute Pharyngitis"));
    }

    // TODO: Fix when operator semantics are completed
    @Test(enabled=false)
    public void testFunctionReference() {
        String cql =
                "define function MyFunction() { return true }\n" +
                "define st : MyFunction()";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        FunctionRef fun = (FunctionRef) def.getExpression();
        assertThat(fun.getName(), is("MyFunction"));
        assertThat(fun.getLibraryName(), is(nullValue()));
        assertThat(fun.getOperand(), is(empty()));
    }

    // TODO: Need to add operand resolution to the type inference...
    @Test(enabled=false)
    public void testFunctionReferenceWithArguments() {
        String cql =
                "define function MyFunction(arg String) { return arg }\n" +
                "define st : MyFunction('hello there')";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        FunctionRef fun = (FunctionRef) def.getExpression();
        assertThat(fun.getName(), is("MyFunction"));
        assertThat(fun.getLibraryName(), is(nullValue()));
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
            //put("/", Divide.class); // This test fails because divide with integer arguments is not defined (relies on implicit conversion)
            put("^", Power.class);
            put("mod", Modulo.class);
        }};

        for (Map.Entry<String, Class> e : comparisons.entrySet()) {
            ExpressionDef def = (ExpressionDef) visitData("define st : 1 " + e.getKey() + " 2");
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
    public void testBasicValueSet() {
        String cql = "valueset \"Female Administrative Sex\" : '2.16.840.1.113883.3.560.100.2'\n" +
                "define X : 1";
        Library l = visitLibrary(cql);
        ValueSetDef def = l.getValueSets().getDef().get(0);
        assertThat(def.getName(), is("Female Administrative Sex"));
        assertThat(def.getId(), is("2.16.840.1.113883.3.560.100.2"));
        assertThat(def.getVersion(), is(nullValue()));
        assertThat(def.getCodeSystem().size(), is(0));
    }

    @Test
    public void testVersionedValueSet() {
        String cql = "valueset \"Female Administrative Sex\" : '2.16.840.1.113883.3.560.100.2' version '1'\n" +
                "define X : 1";
        Library l = visitLibrary(cql);
        ValueSetDef def = l.getValueSets().getDef().get(0);
        assertThat(def.getName(), is("Female Administrative Sex"));
        assertThat(def.getId(), is("2.16.840.1.113883.3.560.100.2"));
        assertThat(def.getVersion(), is("1"));
        assertThat(def.getCodeSystem().size(), is(0));
    }

    @Test
    public void testStaticallyBoundValueSet() {
        String cql = "codesystem \"SNOMED-CT:2014\" : 'SNOMED-CT' version '2014'\n" +
                "codesystem \"ICD-9:2014\" : 'ICD-9' version '2014'\n" +
                "valueset \"Female Administrative Sex\" : '2.16.840.1.113883.3.560.100.2' version '1'\n" +
                "    codesystems { \"SNOMED-CT:2014\", \"ICD-9:2014\" }\n" +
                "define X : 1";
        Library l = visitLibrary(cql);
        ValueSetDef def = l.getValueSets().getDef().get(0);
        assertThat(def.getName(), is("Female Administrative Sex"));
        assertThat(def.getId(), is("2.16.840.1.113883.3.560.100.2"));
        assertThat(def.getVersion(), is("1"));
        assertThat(def.getCodeSystem(), is(not(nullValue())));
        assertThat(def.getCodeSystem().size(), is(2));
        CodeSystemRef r = def.getCodeSystem().get(0);
        assertThat(r.getName(), is("SNOMED-CT:2014"));
        assertThat(r.getLibraryName(), is(nullValue()));
        r = def.getCodeSystem().get(1);
        assertThat(r.getName(), is("ICD-9:2014"));
        assertThat(r.getLibraryName(), is(nullValue()));

        CodeSystemDef snomedCT = l.getCodeSystems().getDef().get(0);
        assertThat(snomedCT.getName(), is("SNOMED-CT:2014"));
        assertThat(snomedCT.getId(), is("SNOMED-CT"));
        assertThat(snomedCT.getVersion(), is("2014"));

        CodeSystemDef icd9 = l.getCodeSystems().getDef().get(1);
        assertThat(icd9.getName(), is("ICD-9:2014"));
        assertThat(icd9.getId(), is("ICD-9"));
        assertThat(icd9.getVersion(), is("2014"));
    }

    @Test
    public void testRetrieveTopic() {
        ExpressionDef def = (ExpressionDef) visitData("using QUICK define st : [Condition]");
        Retrieve request = (Retrieve) def.getExpression();
        assertThat(request.getDataType(), quickDataType("Condition"));
        assertThat(request.getCodeProperty(), is(nullValue()));
        assertThat(request.getCodes(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is("condition-qicore-qicore-condition"));
    }

    @Test
    public void testRetrieveTopicAndValueSet() {
        String cql =
                "using QUICK\n" +
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define st : [Condition: \"Acute Pharyngitis\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        Retrieve request = (Retrieve) def.getExpression();
        assertThat(request.getDataType(), quickDataType("Condition"));
        assertThat(request.getCodeProperty(), is("code"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Acute Pharyngitis"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is("condition-qicore-qicore-condition"));
    }

    @Test
    public void testRetrieveTopicAndSpecifiedCodeAttribute() {
        String cql =
                "using QUICK\n" +
                "valueset \"Moderate or Severe\" : '2.16.840.1.113883.3.526.3.1092'\n" +
                "define st : [Condition: severity in \"Moderate or Severe\"]";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        Retrieve request = (Retrieve) def.getExpression();
        assertThat(request.getDataType(), quickDataType("Condition"));
        assertThat(request.getCodeProperty(), is("severity"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Moderate or Severe"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is("condition-qicore-qicore-condition"));
    }

    @Test
    public void testDateRangeOptimizationForDateIntervalLiteral() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        Interval ivl = (Interval) request.getDateRange();
        assertTrue(ivl.isLowClosed());
        assertFalse(ivl.isHighClosed());
        DateTime ivlBegin = (DateTime) ivl.getLow();
        assertThat(ivlBegin.getYear(), literalFor(2013));
        assertThat(ivlBegin.getMonth(), literalFor(1));
        assertThat(ivlBegin.getDay(), literalFor(1));
        DateTime ivlEnd = (DateTime) ivl.getHigh();
        assertThat(ivlEnd.getYear(), literalFor(2014));
        assertThat(ivlEnd.getMonth(), literalFor(1));
        assertThat(ivlEnd.getDay(), literalFor(1));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDefaultedDateIntervalParameter() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForTypedDateIntervalParameter() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod Interval<DateTime>\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForDateIntervalExpressionReference() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define twentyThirteen : Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during twentyThirteen";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during twentyThirteen" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ExpressionRef ivl = (ExpressionRef) request.getDateRange();
        assertThat(ivl.getName(), is("twentyThirteen"));
        assertThat(ivl.getLibraryName(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    // This test is semantically invalid, you cannot use "during" with a date
    @Test(enabled = false)
    public void testDateRangeOptimizationForDateTimeLiteral() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during DateTime(2013, 6)";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during DateTime(2013, 6)" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        FunctionRef dtFun = (FunctionRef) request.getDateRange();
        assertThat(dtFun.getName(), is("DateTime"));
        assertThat(dtFun.getOperand(), hasSize(2));
        assertThat(dtFun.getOperand().get(0), literalFor(2013));
        assertThat(dtFun.getOperand().get(1), literalFor(6));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    // This test is semantically invalid, you cannot use "during" with a date
    @Test(enabled = false)
    public void testDateRangeOptimizationForDefaultedDateTimeParameter() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MyDate default DateTime(2013, 6)\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MyDate";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MyDate" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ParameterRef myDate = (ParameterRef) request.getDateRange();
        assertThat(myDate.getName(), is("MyDate"));
        assertThat(myDate.getLibraryName(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    // This test is semantically invalid, you cannot use "during" with a date
    @Test(enabled = false)
    public void testDateRangeOptimizationForTypedDateTimeParameter() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MyDate DateTime\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MyDate";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MyDate" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ParameterRef myDate = (ParameterRef) request.getDateRange();
        assertThat(myDate.getName(), is("MyDate"));
        assertThat(myDate.getLibraryName(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    // This test is semantically invalid, you cannot use "during" with a date
    @Test(enabled = false)
    public void testDateRangeOptimizationForDateTimeExpressionReference() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "define myDate : DateTime(2013, 6)\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during myDate";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during myDate" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ExpressionRef myDate = (ExpressionRef) request.getDateRange();
        assertThat(myDate.getName(), is("myDate"));
        assertThat(myDate.getLibraryName(), is(nullValue()));

        // "Where" should now be null!
        assertThat(query.getWhere(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationForAndedWhere() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.length > 2 days\n" +
                "    and E.period during MeasurementPeriod";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));

        // "Where" should now just be the greaterThanPhrase!
        Greater where = (Greater) query.getWhere();
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("length"));
        assertThat(lhs.getSource(), is(nullValue()));
        Quantity rhs = (Quantity) where.getOperand().get(1);
        assertThat(rhs.getValue(), is(BigDecimal.valueOf(2)));
        assertThat(rhs.getUnit(), is("days"));
    }

    @Test
    public void testDateRangeOptimizationForDeeplyAndedWhere() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.length > 2 days\n" +
                "    and E.length < 14 days\n" +
                "    and (First(E.location).location as QUICK.Location).name = 'The Good Hospital'\n" +
                "    and E.period during MeasurementPeriod";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));

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
        assertThat(eqLhs.getPath(), is("name"));
        As eqLhsAs = (As)eqLhs.getSource();
        assertThat(((NamedTypeSpecifier)eqLhsAs.getAsTypeSpecifier()).getName(), quickDataType("Location"));
        Property eqLhsAsSource = (Property)eqLhsAs.getOperand();
        assertThat(eqLhsAsSource.getPath(), is("location"));
        First eqLhsAsSourceFirst = (First)eqLhsAsSource.getSource();
        Property eqLhsAsSourceFirstSource = (Property)eqLhsAsSourceFirst.getSource();
        assertThat(eqLhsAsSourceFirstSource.getScope(), is("E"));
        assertThat(eqLhsAsSourceFirstSource.getPath(), is("location"));

//        assertThat(eqLhs.getScope(), is("E"));
//        assertThat(eqLhs.getPath(), is("location.location.name"));
//        assertThat(eqLhs.getSource(), is(nullValue()));
        assertThat(rhs.getOperand().get(1), literalFor("The Good Hospital"));
    }

    @Test
    public void testDateRangeOptimizationForMultipleQualifyingClauses() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod\n" +
                "    and E.period during MeasurementPeriod";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.getDateProperty(), is("period"));
        ParameterRef mp = (ParameterRef) request.getDateRange();
        assertThat(mp.getName(), is("MeasurementPeriod"));
        assertThat(mp.getLibraryName(), is(nullValue()));

        // "Where" should now just be the other IncludeIn phrase!
        IncludedIn where = (IncludedIn) query.getWhere();
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("period"));
        assertThat(lhs.getSource(), is(nullValue()));
        ParameterRef rhs = (ParameterRef) where.getOperand().get(1);
        assertThat(rhs.getName(), is("MeasurementPeriod"));
        assertThat(rhs.getLibraryName(), is(nullValue()));
    }

    @Test
    public void testDateRangeOptimizationNotDoneWhenDisabled() {
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during MeasurementPeriod";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql, false);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during MeasurementPeriod" migrated up!
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));

        // "Where" should now just be the other IncludeIn phrase!
        IncludedIn where = (IncludedIn) query.getWhere();
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("period"));
        assertThat(lhs.getSource(), is(nullValue()));
        ParameterRef rhs = (ParameterRef) where.getOperand().get(1);
        assertThat(rhs.getName(), is("MeasurementPeriod"));
        assertThat(rhs.getLibraryName(), is(nullValue()));
    }

    // This test is semantically invalid, you cannot use "during" with a list
    @Test(enabled = false)
    public void testDateRangeOptimizationNotDoneOnUnsupportedExpressions() {
        // NOTE: I'm not sure that the below statement is even valid without a "with" clause
        String cql =
                "using QUICK\n" +
                "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
                "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
                "define pharyngitis : [Condition: \"Acute Pharyngitis\"]\n" +
                "define st : [Encounter: \"Inpatient\"] E\n" +
                "    where E.period during pharyngitis";

        Query query = testEncounterPerformanceInpatientForDateRangeOptimization(cql);
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();

        // First check the source and ensure the "during pharnyngitis" didn't migrate up!
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));

        // "Where" should now be null!
        IncludedIn where = (IncludedIn) query.getWhere();
        Property lhs = (Property) where.getOperand().get(0);
        assertThat(lhs.getScope(), is("E"));
        assertThat(lhs.getPath(), is("period"));
        assertThat(lhs.getSource(), is(nullValue()));
        ExpressionRef rhs = (ExpressionRef) where.getOperand().get(1);
        assertThat(rhs.getName(), is("pharyngitis"));
        assertThat(rhs.getLibraryName(), is(nullValue()));
    }

    private Query testEncounterPerformanceInpatientForDateRangeOptimization(String cql) {
        return testEncounterPerformanceInpatientForDateRangeOptimization(cql, true);
    }

    private Query testEncounterPerformanceInpatientForDateRangeOptimization(String cql, boolean enableDateRangeOptimization) {
        ExpressionDef def = (ExpressionDef) visitData(cql, false, enableDateRangeOptimization);
        Query query = (Query) def.getExpression();

        AliasedQuerySource source = query.getSource().get(0);
        assertThat(source.getAlias(), is("E"));
        Retrieve request = (Retrieve) source.getExpression();
        assertThat(request.getDataType(), quickDataType("Encounter"));
        assertThat(request.getCodeProperty(), is("type"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is("encounter-qicore-qicore-encounter"));

        return query;
    }

    @Test
    public void testComplexQuery() {
        String cql =
            "using QUICK\n" +
            "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
            "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
            "parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))\n" +
            "define st : [Encounter: \"Inpatient\"] E\n" +
            "    with [Condition: \"Acute Pharyngitis\"] P\n" +
            "        such that Interval[P.onsetDateTime, P.abatementDate] overlaps after E.period\n" +
            "    where duration in days of E.period >= 120\n" +
            "    return Tuple { id: E.id, lengthOfStay: duration in days of E.period }\n" +
            "    sort by lengthOfStay desc";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        Query query = (Query) def.getExpression();

        // First check the source
        AliasedQuerySource source = query.getSource().get(0);
        assertThat(source.getAlias(), is("E"));
        Retrieve request = (Retrieve) source.getExpression();
        assertThat(request.getDataType(), quickDataType("Encounter"));
        assertThat(request.getCodeProperty(), is("type"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is("encounter-qicore-qicore-encounter"));

        // Then check the with statement
        assertThat(query.getRelationship(), hasSize(1));
        RelationshipClause relationship = query.getRelationship().get(0);
        assertThat(relationship, instanceOf(With.class));
        assertThat(relationship.getAlias(), is("P"));
        Retrieve withRequest = (Retrieve) relationship.getExpression();
        assertThat(withRequest.getDataType(), quickDataType("Condition"));
        assertThat(withRequest.getCodeProperty(), is("code"));
        ValueSetRef withCode = (ValueSetRef) withRequest.getCodes();
        assertThat(withCode.getName(), is("Acute Pharyngitis"));
        assertThat(withCode.getLibraryName(), is(nullValue()));
        assertThat(withRequest.getDateProperty(), is(nullValue()));
        assertThat(withRequest.getDateRange(), is(nullValue()));
        assertThat(withRequest.getScope(), is(nullValue()));
        assertThat(withRequest.getIdProperty(), is(nullValue()));
        assertThat(withRequest.getTemplateId(), is("condition-qicore-qicore-condition"));
        OverlapsAfter withWhere = (OverlapsAfter) relationship.getSuchThat();
        assertThat(withWhere.getOperand(), hasSize(2));
        Interval overlapsLHS = (Interval) withWhere.getOperand().get(0);
        assertThat(overlapsLHS.isLowClosed(), is(true));
        Property overlapsLHSLow = (Property) overlapsLHS.getLow();
        assertThat(overlapsLHSLow.getScope(), is("P"));
        assertThat(overlapsLHSLow.getPath(), is("onsetDateTime"));
        assertThat(overlapsLHSLow.getSource(), is(nullValue()));
        assertThat(overlapsLHS.isHighClosed(), is(true));
        Property overlapsLHSHigh = (Property) overlapsLHS.getHigh();
        assertThat(overlapsLHSHigh.getScope(), is("P"));
        assertThat(overlapsLHSHigh.getPath(), is("abatementDate"));
        assertThat(overlapsLHSHigh.getSource(), is(nullValue()));
        Property overlapsRHS = (Property) withWhere.getOperand().get(1);
        assertThat(overlapsRHS.getScope(), is("E"));
        assertThat(overlapsRHS.getPath(), is("period"));
        assertThat(overlapsRHS.getSource(), is(nullValue()));

        // Then check where statement
        GreaterOrEqual where = (GreaterOrEqual) query.getWhere();
        assertThat(where.getOperand(), hasSize(2));
        DurationBetween whereLHS = (DurationBetween) where.getOperand().get(0);
        assertThat(whereLHS.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(whereLHS.getOperand(), hasSize(2));
        Start whereLHSBegin = (Start) whereLHS.getOperand().get(0);
        Property whereLHSBeginProp = (Property) whereLHSBegin.getOperand();
        assertThat(whereLHSBeginProp.getScope(), is("E"));
        assertThat(whereLHSBeginProp.getPath(), is("period"));
        assertThat(whereLHSBeginProp.getSource(), is(nullValue()));
        End whereLHSEnd = (End) whereLHS.getOperand().get(1);
        Property whereLHSEndProp = (Property) whereLHSEnd.getOperand();
        assertThat(whereLHSEndProp.getScope(), is("E"));
        assertThat(whereLHSEndProp.getPath(), is("period"));
        assertThat(whereLHSEndProp.getSource(), is(nullValue()));
        assertThat(where.getOperand().get(1), literalFor(120));

        // Then check the return statement
        ReturnClause returnClause = query.getReturn();
        Tuple rtn = (Tuple) returnClause.getExpression();
        // TODO: Bug in translator!  Doesn't detect object type (intentional?)
        // assertThat(rtn.getObjectType(), is(notNullValue()));
        assertThat(rtn.getElement(), hasSize(2));
        TupleElement rtnP1 = rtn.getElement().get(0);
        assertThat(rtnP1.getName(), is("id"));
        Property rtnP1Val = (Property) rtnP1.getValue();
        assertThat(rtnP1Val.getScope(), is("E"));
        assertThat(rtnP1Val.getPath(), is("id"));
        assertThat(rtnP1Val.getSource(), is(nullValue()));
        TupleElement rtnP2 = rtn.getElement().get(1);
        assertThat(rtnP2.getName(), is("lengthOfStay"));
        DurationBetween rtnP2Val = (DurationBetween) rtnP2.getValue();
        assertThat(rtnP2Val.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(rtnP2Val.getOperand(), hasSize(2));
        Start rtnP2ValBegin = (Start) rtnP2Val.getOperand().get(0);
        Property rtnP2ValBeginProp = (Property) rtnP2ValBegin.getOperand();
        assertThat(rtnP2ValBeginProp.getScope(), is("E"));
        assertThat(rtnP2ValBeginProp.getPath(), is("period"));
        assertThat(rtnP2ValBeginProp.getSource(), is(nullValue()));
        End rtnP2ValEnd = (End) rtnP2Val.getOperand().get(1);
        Property rtnP2ValEndProp = (Property) rtnP2ValEnd.getOperand();
        assertThat(rtnP2ValEndProp.getScope(), is("E"));
        assertThat(rtnP2ValEndProp.getPath(), is("period"));
        assertThat(rtnP2ValEndProp.getSource(), is(nullValue()));

        // Finally test sort
        SortClause sort = query.getSort();
        assertThat(sort.getBy(), hasSize(1));
        ByColumn sortBy = (ByColumn)sort.getBy().get(0);
        assertThat(sortBy.getPath(), is("lengthOfStay"));
        assertThat(sortBy.getDirection(), is(SortDirection.DESC));
    }

    @Test
    public void testQueryThatReturnsLet() {
        String cql =
            "using QUICK\n" +
            "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
            "define st : [Encounter: \"Inpatient\"] E\n" +
            "    let a : 1\n" +
            "    return a";
        ExpressionDef def = (ExpressionDef) visitData(cql);
        Query query = (Query) def.getExpression();

        // First check the source
        AliasedQuerySource source = query.getSource().get(0);
        assertThat(source.getAlias(), is("E"));
        Retrieve request = (Retrieve) source.getExpression();
        assertThat(request.getDataType(), quickDataType("Encounter"));
        assertThat(request.getCodeProperty(), is("type"));
        ValueSetRef code = (ValueSetRef) request.getCodes();
        assertThat(code.getName(), is("Inpatient"));
        assertThat(code.getLibraryName(), is(nullValue()));
        assertThat(request.getDateProperty(), is(nullValue()));
        assertThat(request.getDateRange(), is(nullValue()));
        assertThat(request.getScope(), is(nullValue()));
        assertThat(request.getIdProperty(), is(nullValue()));
        assertThat(request.getTemplateId(), is("encounter-qicore-qicore-encounter"));

        // Then check the define
        assertThat(query.getLet(), hasSize(1));
        LetClause dfc = query.getLet().get(0);
        assertThat(dfc.getIdentifier(), is("a"));
        assertThat(dfc.getExpression(), literalFor(1));

        // Then check the return
        assertThat(query.getReturn().getExpression(), instanceOf(QueryLetRef.class));
        QueryLetRef qdr = (QueryLetRef) query.getReturn().getExpression();
        assertThat(qdr.getName(), is("a"));

        // Then check the rest
        assertThat(query.getRelationship(), is(empty()));
        assertThat(query.getWhere(), is(nullValue()));
        assertThat(query.getSort(), is(nullValue()));
    }

    @Test
    public void testChoiceDatatype() {
	    String cql =
			    "using FHIR \n" +
					    "define \"Med\": \n" +
					    "    MedicationRequest {\n" +
					    "        medication : CodeableConcept { \n" +
					    "            coding: Coding{ code: code{ value: 'test' } } \n" +
					    "        } \n" +
					    "  }";
	    ExpressionDef def = (ExpressionDef) visitData(cql);

	    assertThat( def.getExpression(), instanceOf( Instance.class ) );
	    Instance inst = ( Instance ) def.getExpression();
	    assertThat( inst.getElement().size(), is( 1 ) );
	    InstanceElement el = inst.getElement().get( 0 );
	    assertNotNull( el );

	    assertThat( el.getName(), is( "medication" ) );
	    assertThat( el.getValue(), instanceOf( Instance.class ) );
	    assertThat( ((Instance)el.getValue()).getClassType().getLocalPart(), is( "CodeableConcept" ) );
    }

    @Test
    public void testInnerTypes() {
    	String cql =
			    "using FHIR \n" +
					    "define \"Alert\":\n" +
					    "  CommunicationRequest {\n" +
					    "    payload: FHIR.CommunicationRequest.Payload {\n" +
					    "                      content: string{ value: 'aaa' }\n" +
					    "                      } \n" +
					    "  }";
	    ExpressionDef def = (ExpressionDef) visitData(cql);

	    assertThat( def.getExpression(), instanceOf( Instance.class ) );
	    Instance inst = ( Instance ) def.getExpression();
	    assertThat( inst.getElement().size(), is( 1 ) );
	    InstanceElement el = inst.getElement().get( 0 );
	    assertNotNull( el );

	    assertThat( el.getName(), is( "payload" ) );
	    assertThat( el.getValue(), instanceOf( ToList.class ) );
	    Expression op = ((ToList) el.getValue()).getOperand();
	    assertThat( op, instanceOf( Instance.class ) );
	    assertThat( ((Instance)op).getClassType().getLocalPart(), is( "CommunicationRequest.Payload" ) );
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameAs() {
        String where = "P same as E";
        SameAs sameAs = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameAs.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameAs.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameAs.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameYearAs() {
        String where = "P same year as E";
        SameAs sameYear = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameYear.getPrecision(), is(DateTimePrecision.YEAR));
        assertThat(sameYear.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameYear.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameYear.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameMonthAs() {
        String where = "P same month as E";
        SameAs sameMonth = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameMonth.getPrecision(), is(DateTimePrecision.MONTH));
        assertThat(sameMonth.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameMonth.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameMonth.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameDayAs() {
        String where = "P same day as E";
        SameAs sameDay = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameDay.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(sameDay.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameDay.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameDay.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameHourAs() {
        String where = "P same hour as E";
        SameAs sameHour = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameHour.getPrecision(), is(DateTimePrecision.HOUR));
        assertThat(sameHour.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameHour.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameHour.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameMinuteAs() {
        String where = "P same minute as E";
        SameAs sameMin = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameMin.getPrecision(), is(DateTimePrecision.MINUTE));
        assertThat(sameMin.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameMin.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameMin.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameSecondAs() {
        String where = "P same second as E";
        SameAs sameSec = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameSec.getPrecision(), is(DateTimePrecision.SECOND));
        assertThat(sameSec.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameSec.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameSec.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testSameMillisecondAs() {
        String where = "P same millisecond as E";
        SameAs sameMS = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameMS.getPrecision(), is(DateTimePrecision.MILLISECOND));
        assertThat(sameMS.getOperand(), hasSize(2));
        AliasRef lhs = (AliasRef) sameMS.getOperand().get(0);
        assertThat(lhs.getName(), is("P"));
        AliasRef rhs = (AliasRef) sameMS.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testStartsSameDayAs() {
        String where = "P starts same day as E";
        SameAs sameDay = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameDay.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(sameDay.getOperand(), hasSize(2));
        Start lhs = (Start) sameDay.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        AliasRef rhs = (AliasRef) sameDay.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testStartsSameDayAsStart() {
        String where = "P starts same day as start E";
        SameAs sameDay = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameDay.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(sameDay.getOperand(), hasSize(2));
        Start lhs = (Start) sameDay.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        Start rhs = (Start) sameDay.getOperand().get(1);
        assertThat(((AliasRef) rhs.getOperand()).getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testStartsSameDayAsEnd() {
        String where = "P starts same day as end E";
        SameAs sameDay = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameDay.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(sameDay.getOperand(), hasSize(2));
        Start lhs = (Start) sameDay.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        End rhs = (End) sameDay.getOperand().get(1);
        assertThat(((AliasRef) rhs.getOperand()).getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testStartsAtLeastSameDayAs() {
        String where = "P starts same day or after E";
        SameOrAfter sameOrAfter = (SameOrAfter) testInpatientWithPharyngitisWhere(where);
        assertThat(sameOrAfter.getOperand(), hasSize(2));
        assertThat(sameOrAfter.getPrecision(), is(DateTimePrecision.DAY));
        Start lhs = (Start) sameOrAfter.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        AliasRef rhs = (AliasRef) sameOrAfter.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testStartsAtMostSameDayAs() {
        String where = "P starts same day or before E";
        SameOrBefore sameOrBefore = (SameOrBefore) testInpatientWithPharyngitisWhere(where);
        assertThat(sameOrBefore.getPrecision(), is(DateTimePrecision.DAY));
        Start lhs = (Start) sameOrBefore.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        AliasRef rhs = (AliasRef) sameOrBefore.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testEndsSameDayAs() {
        String where = "P ends same day as E";
        SameAs sameDay = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameDay.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(sameDay.getOperand(), hasSize(2));
        End lhs = (End) sameDay.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        AliasRef rhs = (AliasRef) sameDay.getOperand().get(1);
        assertThat(rhs.getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testEndsSameDayAsEnd() {
        String where = "P ends same day as end E";
        SameAs sameDay = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameDay.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(sameDay.getOperand(), hasSize(2));
        End lhs = (End) sameDay.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        End rhs = (End) sameDay.getOperand().get(1);
        assertThat(((AliasRef) rhs.getOperand()).getName(), is("E"));
    }

    // TODO: This test needs to be repurposed, it won't work with the query as is.
    @Test(enabled = false)
    public void testEndsSameDayAsStart() {
        String where = "P ends same day as start E";
        SameAs sameDay = (SameAs) testInpatientWithPharyngitisWhere(where);
        assertThat(sameDay.getPrecision(), is(DateTimePrecision.DAY));
        assertThat(sameDay.getOperand(), hasSize(2));
        End lhs = (End) sameDay.getOperand().get(0);
        assertThat(((AliasRef) lhs.getOperand()).getName(), is("P"));
        Start rhs = (Start) sameDay.getOperand().get(1);
        assertThat(((AliasRef) rhs.getOperand()).getName(), is("E"));
    }

    private Expression testInpatientWithPharyngitisWhere(String withWhereClause) {
        String cql =
            "using QUICK\n" +
            "valueset \"Inpatient\" : '2.16.840.1.113883.3.666.5.307'\n" +
            "valueset \"Acute Pharyngitis\" : '2.16.840.1.113883.3.464.1003.102.12.1011'\n" +
            "define st : [Encounter: \"Inpatient\"] E\n" +
            "    with [Condition: \"Acute Pharyngitis\"] P\n" +
            "    such that " + withWhereClause;

        ExpressionDef def = (ExpressionDef) visitData(cql);
        Query query = (Query) def.getExpression();
        assertThat(query.getSource().get(0).getAlias(), is("E"));
        Retrieve request = (Retrieve) query.getSource().get(0).getExpression();
        assertThat(request.getDataType().getNamespaceURI(), is("http://org.hl7.fhir"));
        assertThat(request.getDataType().getLocalPart(), is("Encounter"));
        assertThat(request.getCodeProperty(), is("type"));
        ValueSetRef vs = (ValueSetRef) request.getCodes();
        assertThat(vs.getName(), is("Inpatient"));
        assertThat(vs.getLibraryName(), is(nullValue()));
        assertThat(query.getRelationship(), hasSize(1));
        With with = (With) query.getRelationship().get(0);
        assertThat(with.getAlias(), is("P"));
        Retrieve withRequest = (Retrieve) with.getExpression();
        assertThat(withRequest.getDataType().getNamespaceURI(), is("http://org.hl7.fhir"));
        assertThat(withRequest.getDataType().getLocalPart(), is("Condition"));
        assertThat(withRequest.getCodeProperty(), is("code"));
        ValueSetRef withVS = (ValueSetRef) withRequest.getCodes();
        assertThat(withVS.getName(), is("Acute Pharyngitis"));
        assertThat(withVS.getLibraryName(), is(nullValue()));
        return with.getSuchThat();
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
