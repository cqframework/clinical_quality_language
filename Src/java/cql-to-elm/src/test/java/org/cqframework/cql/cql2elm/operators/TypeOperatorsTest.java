package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.assertTrue;

public class TypeOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        CqlTranslator translator = CqlTranslator.fromStream(TypeOperatorsTest.class.getResourceAsStream("../OperatorTests/TypeOperators.cql"), new LibraryManager());
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testAs() {
        ExpressionDef def = defs.get("AsExpression");
        assertThat(def, hasTypeAndResult(As.class, "System.Boolean"));
        As as = (As) def.getExpression();
        assertThat(as.getOperand(), instanceOf(Null.class));
        assertThat(as.getAsTypeSpecifier(), instanceOf(NamedTypeSpecifier.class));
        NamedTypeSpecifier spec = (NamedTypeSpecifier) as.getAsTypeSpecifier();
        assertThat(spec.getName(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        assertThat(spec.getResultType().toString(), is("System.Boolean"));
        //assertThat(as.getAsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    @Test
    public void testCast() {
        ExpressionDef def = defs.get("CastExpression");
        assertThat(def, hasTypeAndResult(As.class, "System.Boolean"));
        As as = (As) def.getExpression();
        assertThat(as.getOperand(), instanceOf(Null.class));
        assertThat(as.getAsTypeSpecifier(), instanceOf(NamedTypeSpecifier.class));
        NamedTypeSpecifier spec = (NamedTypeSpecifier) as.getAsTypeSpecifier();
        assertThat(spec.getName(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        assertThat(spec.getResultType().toString(), is("System.Boolean"));
        //assertThat(as.getAsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    @Test
    public void testIs() {
        ExpressionDef def = defs.get("IsExpression");
        assertThat(def, hasTypeAndResult(Is.class, "System.Boolean"));
        Is is = (Is) def.getExpression();
        assertThat(is.getOperand(), instanceOf(Null.class));
        assertThat(is.getIsTypeSpecifier(), instanceOf(NamedTypeSpecifier.class));
        NamedTypeSpecifier spec = (NamedTypeSpecifier) is.getIsTypeSpecifier();
        assertThat(spec.getName(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        assertThat(spec.getResultType().toString(), is("System.Boolean"));
        //assertThat(is.getIsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }
    
    private static void validateTyping(Convert convert, QName typeName) {
        assertThat(convert.getToType(), is(typeName));
        assertTrue(convert.getToTypeSpecifier() != null);
        assertTrue(convert.getToTypeSpecifier() instanceof NamedTypeSpecifier);
        NamedTypeSpecifier nts = (NamedTypeSpecifier)convert.getToTypeSpecifier();
        assertThat(nts.getName(), is(typeName));
    }

    @Test
    public void testToString() {
        ExpressionDef def = defs.get("BooleanToString");
        assertThat(def, hasTypeAndResult(Convert.class, "System.String"));
        Convert convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor(false));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("IntegerToString");
        assertThat(def, hasTypeAndResult(Convert.class, "System.String"));
        convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("DecimalToString");
        assertThat(def, hasTypeAndResult(Convert.class, "System.String"));
        convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3.0));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("QuantityToString");
        assertThat(def, hasTypeAndResult(Convert.class, "System.String"));
        convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Quantity.class));
        Quantity q = (Quantity) convert.getOperand();
        assertThat(q.getValue().doubleValue(), is(3.0));
        assertThat(q.getUnit(), is("m"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("DateTimeToString");
        assertThat(def, hasTypeAndResult(Convert.class, "System.String"));
        convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(DateTime.class));
        DateTime dt = (DateTime) convert.getOperand();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), literalFor(0));
        assertThat(dt.getMillisecond(), literalFor(0));
        assertThat(dt.getTimezoneOffset(), nullValue());
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("TimeToString");
        assertThat(def, hasTypeAndResult(Convert.class, "System.String"));
        convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Time.class));
        Time t = (Time) convert.getOperand();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));
        assertThat(t.getSecond(), literalFor(0));
        assertThat(t.getMillisecond(), literalFor(0));
        assertThat(t.getTimezoneOffset(), nullValue());
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
    }

    @Test
    public void testToStringFunction() {
        ExpressionDef def = defs.get("BooleanToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        ToString convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(false));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("DecimalToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3.0));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("QuantityToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Quantity.class));
        Quantity q = (Quantity) convert.getOperand();
        assertThat(q.getValue().doubleValue(), is(3.0));
        assertThat(q.getUnit(), is("m"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("DateTimeToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(DateTime.class));
        DateTime dt = (DateTime) convert.getOperand();
        assertThat(dt.getYear(), literalFor(2014));
        assertThat(dt.getMonth(), literalFor(1));
        assertThat(dt.getDay(), literalFor(1));
        assertThat(dt.getHour(), literalFor(0));
        assertThat(dt.getMinute(), literalFor(0));
        assertThat(dt.getSecond(), literalFor(0));
        assertThat(dt.getMillisecond(), literalFor(0));
        assertThat(dt.getTimezoneOffset(), nullValue());
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("TimeToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Time.class));
        Time t = (Time) convert.getOperand();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));
        assertThat(t.getSecond(), literalFor(0));
        assertThat(t.getMillisecond(), literalFor(0));
        assertThat(t.getTimezoneOffset(), nullValue());
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    public void testToBoolean() {
        ExpressionDef def = defs.get("StringToBoolean");
        assertThat(def, hasTypeAndResult(Convert.class, "System.Boolean"));
        Convert convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor("false"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Boolean"));
    }

    @Test
    public void testToBooleanFunction() {
        ExpressionDef def = defs.get("StringToBooleanFun");
        assertThat(def, hasTypeAndResult(ToBoolean.class, "System.Boolean"));
        ToBoolean convert = (ToBoolean) def.getExpression();
        assertThat(convert.getOperand(), literalFor("false"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    public void testToInteger() {
        ExpressionDef def = defs.get("StringToInteger");
        assertThat(def, hasTypeAndResult(Convert.class, "System.Integer"));
        Convert convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Integer"));
    }

    @Test
    public void testToIntegerFunction() {
        ExpressionDef def = defs.get("StringToIntegerFun");
        assertThat(def, hasTypeAndResult(ToInteger.class, "System.Integer"));
        ToInteger convert = (ToInteger) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Integer")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    public void testToDecimal() {
        ExpressionDef def = defs.get("StringToDecimal");
        assertThat(def, hasTypeAndResult(Convert.class, "System.Decimal"));
        Convert convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));

        def = defs.get("IntegerToDecimal");
        assertThat(def, hasTypeAndResult(Convert.class, "System.Decimal"));
        convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    public void testToDecimalFunction() {
        ExpressionDef def = defs.get("StringToDecimalFun");
        assertThat(def, hasTypeAndResult(ToDecimal.class, "System.Decimal"));
        ToDecimal convert = (ToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Decimal")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerToDecimalFun");
        assertThat(def, hasTypeAndResult(ToDecimal.class, "System.Decimal"));
        convert = (ToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        //validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    public void testToDateTime() {
        ExpressionDef def = defs.get("StringToDateTime");
        assertThat(def, hasTypeAndResult(Convert.class, "System.DateTime"));
        Convert convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01T00:00:00:00.0000+0700"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "DateTime"));
    }

    @Test
    public void testToDateTimeFunction() {
        ExpressionDef def = defs.get("StringToDateTimeFun");
        assertThat(def, hasTypeAndResult(ToDateTime.class, "System.DateTime"));
        ToDateTime convert = (ToDateTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01T00:00:00:00.0000+0700"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "DateTime")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    public void testToTime() {
        ExpressionDef def = defs.get("StringToTime");
        assertThat(def, hasTypeAndResult(Convert.class, "System.Time"));
        Convert convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), literalFor("T00:00:00:00.0000+0700"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Time"));
    }

    @Test
    public void testToTimeFunction() {
        ExpressionDef def = defs.get("StringToTimeFun");
        assertThat(def, hasTypeAndResult(ToTime.class, "System.Time"));
        ToTime convert = (ToTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("T00:00:00:00.0000+0700"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Time")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    public void testToConcept() {
        ExpressionDef def = defs.get("CodeToConcept");
        assertThat(def, hasTypeAndResult(Convert.class, "System.Concept"));
        Convert convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(ExpressionRef.class));
        ExpressionRef ref = (ExpressionRef) convert.getOperand();
        assertThat(ref.getName(), is("MyCode"));
        assertThat(ref.getResultType().toString(), is("System.Code"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Concept"));

        def = defs.get("CodesToConcept");
        assertThat(def, hasTypeAndResult(Convert.class, "System.Concept"));
        convert = (Convert) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(ExpressionRef.class));
        ref = (ExpressionRef) convert.getOperand();
        assertThat(ref.getName(), is("MyCodes"));
        assertThat(ref.getResultType().toString(), is("list<System.Code>"));
        validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Concept"));
    }

    @Test
    public void testToConceptFunction() {
        ExpressionDef def = defs.get("CodeToConceptFun");
        assertThat(def, hasTypeAndResult(ToConcept.class, "System.Concept"));
        ToConcept convert = (ToConcept) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(ExpressionRef.class));
        ExpressionRef ref = (ExpressionRef) convert.getOperand();
        assertThat(ref.getName(), is("MyCode"));
        assertThat(ref.getResultType().toString(), is("System.Code"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Concept")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("CodesToConceptFun");
        assertThat(def, hasTypeAndResult(ToConcept.class, "System.Concept"));
        convert = (ToConcept) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(ExpressionRef.class));
        ref = (ExpressionRef) convert.getOperand();
        assertThat(ref.getName(), is("MyCodes"));
        assertThat(ref.getResultType().toString(), is("list<System.Code>"));
        //assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Concept")));
        //assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    public void testMinValue() {
        ExpressionDef def = defs.get("MinimumInteger");
        assertThat(def, hasTypeAndResult(MinValue.class, "System.Integer"));
        MinValue minValue = (MinValue) def.getExpression();
        assertThat(minValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "Integer")));

        def = defs.get("MinimumDecimal");
        assertThat(def, hasTypeAndResult(MinValue.class, "System.Decimal"));
        minValue = (MinValue) def.getExpression();
        assertThat(minValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "Decimal")));

        def = defs.get("MinimumDateTime");
        assertThat(def, hasTypeAndResult(MinValue.class, "System.DateTime"));
        minValue = (MinValue) def.getExpression();
        assertThat(minValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "DateTime")));

        def = defs.get("MinimumTime");
        assertThat(def, hasTypeAndResult(MinValue.class, "System.Time"));
        minValue = (MinValue) def.getExpression();
        assertThat(minValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "Time")));
    }

    @Test
    public void testMaxValue() {
        ExpressionDef def = defs.get("MaximumInteger");
        assertThat(def, hasTypeAndResult(MaxValue.class, "System.Integer"));
        MaxValue maxValue = (MaxValue) def.getExpression();
        assertThat(maxValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "Integer")));

        def = defs.get("MaximumDecimal");
        assertThat(def, hasTypeAndResult(MaxValue.class, "System.Decimal"));
        maxValue = (MaxValue) def.getExpression();
        assertThat(maxValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "Decimal")));

        def = defs.get("MaximumDateTime");
        assertThat(def, hasTypeAndResult(MaxValue.class, "System.DateTime"));
        maxValue = (MaxValue) def.getExpression();
        assertThat(maxValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "DateTime")));

        def = defs.get("MaximumTime");
        assertThat(def, hasTypeAndResult(MaxValue.class, "System.Time"));
        maxValue = (MaxValue) def.getExpression();
        assertThat(maxValue.getValueType(), is(new QName("urn:hl7-org:elm-types:r1", "Time")));
    }
}
