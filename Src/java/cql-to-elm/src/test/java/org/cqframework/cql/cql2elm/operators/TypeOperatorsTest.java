package org.cqframework.cql.cql2elm.operators;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TypeOperatorsTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(
                TypeOperatorsTest.class.getResourceAsStream("../OperatorTests/TypeOperators.cql"),
                new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    void as() {
        ExpressionDef def = defs.get("AsExpression");
        assertThat(def, hasTypeAndResult(As.class, "System.Boolean"));
        As as = (As) def.getExpression();
        assertThat(as.getOperand(), instanceOf(Null.class));
        assertThat(as.getAsTypeSpecifier(), instanceOf(NamedTypeSpecifier.class));
        NamedTypeSpecifier spec = (NamedTypeSpecifier) as.getAsTypeSpecifier();
        assertThat(spec.getName(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        assertThat(spec.getResultType().toString(), is("System.Boolean"));
        // assertThat(as.getAsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    @Test
    void cast() {
        ExpressionDef def = defs.get("CastExpression");
        assertThat(def, hasTypeAndResult(As.class, "System.Boolean"));
        As as = (As) def.getExpression();
        assertThat(as.getOperand(), instanceOf(Null.class));
        assertThat(as.getAsTypeSpecifier(), instanceOf(NamedTypeSpecifier.class));
        NamedTypeSpecifier spec = (NamedTypeSpecifier) as.getAsTypeSpecifier();
        assertThat(spec.getName(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        assertThat(spec.getResultType().toString(), is("System.Boolean"));
        // assertThat(as.getAsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    @Test
    void isExpression() {
        ExpressionDef def = defs.get("IsExpression");
        assertThat(def, hasTypeAndResult(Is.class, "System.Boolean"));
        Is is = (Is) def.getExpression();
        assertThat(is.getOperand(), instanceOf(Null.class));
        assertThat(is.isTypeSpecifier(), instanceOf(NamedTypeSpecifier.class));
        NamedTypeSpecifier spec = (NamedTypeSpecifier) is.isTypeSpecifier();
        assertThat(spec.getName(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        assertThat(spec.getResultType().toString(), is("System.Boolean"));
        // assertThat(is.getIsType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
    }

    private static void validateTyping(Convert convert, QName typeName) {
        assertThat(convert.getToType(), is(typeName));
        assertTrue(convert.getToTypeSpecifier() != null);
        assertTrue(convert.getToTypeSpecifier() instanceof NamedTypeSpecifier);
        NamedTypeSpecifier nts = (NamedTypeSpecifier) convert.getToTypeSpecifier();
        assertThat(nts.getName(), is(typeName));
    }

    @Test
    void testToString() {
        ExpressionDef def = defs.get("BooleanToString");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        ToString convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(false));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("IntegerToString");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("DecimalToString");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3.0));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("QuantityToString");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Quantity.class));
        Quantity q = (Quantity) convert.getOperand();
        assertThat(q.getValue().doubleValue(), is(3.0));
        assertThat(q.getUnit(), is("m"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("RatioToString");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Ratio.class));
        Ratio r = (Ratio) convert.getOperand();
        assertThat(r.getDenominator().getValue().doubleValue(), is(180.0));
        assertThat(r.getDenominator().getUnit(), is("1"));
        assertThat(r.getNumerator().getValue().doubleValue(), is(1.0));
        assertThat(r.getNumerator().getUnit(), is("1"));

        def = defs.get("DateToString");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Date.class));
        Date d = (Date) convert.getOperand();
        assertThat(d.getYear(), literalFor(2014));
        assertThat(d.getMonth(), literalFor(1));
        assertThat(d.getDay(), literalFor(1));

        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
        def = defs.get("DateTimeToString");
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
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));

        def = defs.get("TimeToString");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Time.class));
        Time t = (Time) convert.getOperand();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));
        assertThat(t.getSecond(), literalFor(0));
        assertThat(t.getMillisecond(), literalFor(0));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "String"));
    }

    @Test
    void toStringFunction() {
        ExpressionDef def = defs.get("BooleanToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        ToString convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(false));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("DecimalToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3.0));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("QuantityToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Quantity.class));
        Quantity q = (Quantity) convert.getOperand();
        assertThat(q.getValue().doubleValue(), is(3.0));
        assertThat(q.getUnit(), is("m"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("RatioToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Ratio.class));
        Ratio r = (Ratio) convert.getOperand();
        assertThat(r.getDenominator().getValue().doubleValue(), is(180.0));
        assertThat(r.getDenominator().getUnit(), is("1"));
        assertThat(r.getNumerator().getValue().doubleValue(), is(1.0));
        assertThat(r.getNumerator().getUnit(), is("1"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("DateToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Date.class));
        Date d = (Date) convert.getOperand();
        assertThat(d.getYear(), literalFor(2014));
        assertThat(d.getMonth(), literalFor(1));
        assertThat(d.getDay(), literalFor(1));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

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
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("TimeToStringFun");
        assertThat(def, hasTypeAndResult(ToString.class, "System.String"));
        convert = (ToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Time.class));
        Time t = (Time) convert.getOperand();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));
        assertThat(t.getSecond(), literalFor(0));
        assertThat(t.getMillisecond(), literalFor(0));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void convertsToString() {
        ExpressionDef def = defs.get("BooleanConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        ConvertsToString convert = (ConvertsToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(false));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        convert = (ConvertsToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("DecimalConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        convert = (ConvertsToString) def.getExpression();
        assertThat(convert.getOperand(), literalFor(3.0));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("QuantityConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        convert = (ConvertsToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Quantity.class));
        Quantity q = (Quantity) convert.getOperand();
        assertThat(q.getValue().doubleValue(), is(3.0));
        assertThat(q.getUnit(), is("m"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("RatioConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        convert = (ConvertsToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Ratio.class));
        Ratio r = (Ratio) convert.getOperand();
        assertThat(r.getDenominator().getValue().doubleValue(), is(180.0));
        assertThat(r.getDenominator().getUnit(), is("1"));
        assertThat(r.getNumerator().getValue().doubleValue(), is(1.0));
        assertThat(r.getNumerator().getUnit(), is("1"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("DateConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        convert = (ConvertsToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Date.class));
        Date d = (Date) convert.getOperand();
        assertThat(d.getYear(), literalFor(2014));
        assertThat(d.getMonth(), literalFor(1));
        assertThat(d.getDay(), literalFor(1));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("DateTimeConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        convert = (ConvertsToString) def.getExpression();
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
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("TimeConvertsToString");
        assertThat(def, hasTypeAndResult(ConvertsToString.class, "System.Boolean"));
        convert = (ConvertsToString) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(Time.class));
        Time t = (Time) convert.getOperand();
        assertThat(t.getHour(), literalFor(0));
        assertThat(t.getMinute(), literalFor(0));
        assertThat(t.getSecond(), literalFor(0));
        assertThat(t.getMillisecond(), literalFor(0));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "String")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toBoolean() {
        ExpressionDef def = defs.get("StringToBoolean");
        assertThat(def, hasTypeAndResult(ToBoolean.class, "System.Boolean"));
        ToBoolean convert = (ToBoolean) def.getExpression();
        assertThat(convert.getOperand(), literalFor("false"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Boolean"));
    }

    @Test
    void toBooleanFunction() {
        ExpressionDef def = defs.get("StringToBooleanFun");
        assertThat(def, hasTypeAndResult(ToBoolean.class, "System.Boolean"));
        ToBoolean convert = (ToBoolean) def.getExpression();
        assertThat(convert.getOperand(), literalFor("false"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void convertsToBoolean() {
        ExpressionDef def = defs.get("StringConvertsToBoolean");
        assertThat(def, hasTypeAndResult(ConvertsToBoolean.class, "System.Boolean"));
        ConvertsToBoolean convert = (ConvertsToBoolean) def.getExpression();
        assertThat(convert.getOperand(), literalFor("false"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Boolean")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toInteger() {
        ExpressionDef def = defs.get("StringToInteger");
        assertThat(def, hasTypeAndResult(ToInteger.class, "System.Integer"));
        ToInteger convert = (ToInteger) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Integer"));
    }

    @Test
    void toIntegerFunction() {
        ExpressionDef def = defs.get("StringToIntegerFun");
        assertThat(def, hasTypeAndResult(ToInteger.class, "System.Integer"));
        ToInteger convert = (ToInteger) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Integer")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void convertsToInteger() {
        ExpressionDef def = defs.get("StringConvertsToInteger");
        assertThat(def, hasTypeAndResult(ConvertsToInteger.class, "System.Boolean"));
        ConvertsToInteger convert = (ConvertsToInteger) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Integer")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toLong() {
        ExpressionDef def = defs.get("StringToLong");
        assertThat(def, hasTypeAndResult(ToLong.class, "System.Long"));
        ToLong convert = (ToLong) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Long"));
    }

    @Test
    void convertsToLong() {
        ExpressionDef def = defs.get("StringConvertsToLong");
        assertThat(def, hasTypeAndResult(ConvertsToLong.class, "System.Boolean"));
        ConvertsToLong convert = (ConvertsToLong) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Long")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toDecimal() {
        ExpressionDef def = defs.get("StringToDecimal");
        assertThat(def, hasTypeAndResult(ToDecimal.class, "System.Decimal"));
        ToDecimal convert = (ToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));

        def = defs.get("IntegerToDecimal");
        assertThat(def, hasTypeAndResult(ToDecimal.class, "System.Decimal"));
        convert = (ToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    void toDecimalFunction() {
        ExpressionDef def = defs.get("StringToDecimalFun");
        assertThat(def, hasTypeAndResult(ToDecimal.class, "System.Decimal"));
        ToDecimal convert = (ToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Decimal")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerToDecimalFun");
        assertThat(def, hasTypeAndResult(ToDecimal.class, "System.Decimal"));
        convert = (ToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    void convertsToDecimal() {
        ExpressionDef def = defs.get("StringConvertsToDecimal");
        assertThat(def, hasTypeAndResult(ConvertsToDecimal.class, "System.Boolean"));
        ConvertsToDecimal convert = (ConvertsToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Decimal")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerConvertsToDecimal");
        assertThat(def, hasTypeAndResult(ConvertsToDecimal.class, "System.Boolean"));
        convert = (ConvertsToDecimal) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Decimal"));
    }

    @Test
    void toDate() {
        ExpressionDef def = defs.get("StringToDate");
        assertThat(def, hasTypeAndResult(ToDate.class, "System.Date"));
        ToDate convert = (ToDate) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Date"));
    }

    @Test
    void toDateFunction() {
        ExpressionDef def = defs.get("StringToDateFun");
        assertThat(def, hasTypeAndResult(ToDate.class, "System.Date"));
        ToDate convert = (ToDate) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Date")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void convertsToDate() {
        ExpressionDef def = defs.get("StringConvertsToDate");
        assertThat(def, hasTypeAndResult(ConvertsToDate.class, "System.Boolean"));
        ConvertsToDate convert = (ConvertsToDate) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Date")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toDateTime() {
        ExpressionDef def = defs.get("StringToDateTime");
        assertThat(def, hasTypeAndResult(ToDateTime.class, "System.DateTime"));
        ToDateTime convert = (ToDateTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01T00:00:00.0000+0700"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "DateTime"));
    }

    @Test
    void toDateTimeFunction() {
        ExpressionDef def = defs.get("StringToDateTimeFun");
        assertThat(def, hasTypeAndResult(ToDateTime.class, "System.DateTime"));
        ToDateTime convert = (ToDateTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01T00:00:00.0000+0700"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "DateTime")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void convertsToDateTime() {
        ExpressionDef def = defs.get("StringConvertsToDateTime");
        assertThat(def, hasTypeAndResult(ConvertsToDateTime.class, "System.Boolean"));
        ConvertsToDateTime convert = (ConvertsToDateTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("2014-01-01T00:00:00.0000+0700"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "DateTime")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toTime() {
        ExpressionDef def = defs.get("StringToTime");
        assertThat(def, hasTypeAndResult(ToTime.class, "System.Time"));
        ToTime convert = (ToTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("T00:00:00.0000+0700"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Time"));
    }

    @Test
    void toTimeFunction() {
        ExpressionDef def = defs.get("StringToTimeFun");
        assertThat(def, hasTypeAndResult(ToTime.class, "System.Time"));
        ToTime convert = (ToTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("T00:00:00.0000+0700"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Time")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void convertsToTime() {
        ExpressionDef def = defs.get("StringConvertsToTime");
        assertThat(def, hasTypeAndResult(ConvertsToTime.class, "System.Boolean"));
        ConvertsToTime convert = (ConvertsToTime) def.getExpression();
        assertThat(convert.getOperand(), literalFor("T00:00:00.0000+0700"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Time")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toQuantity() {
        ExpressionDef def = defs.get("StringToQuantity");
        assertThat(def, hasTypeAndResult(ToQuantity.class, "System.Quantity"));
        ToQuantity convert = (ToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0 'm'"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));

        def = defs.get("IntegerToQuantity");
        assertThat(def, hasTypeAndResult(ToQuantity.class, "System.Quantity"));
        convert = (ToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));

        def = defs.get("DecimalToQuantity");
        assertThat(def, hasTypeAndResult(ToQuantity.class, "System.Quantity"));
        convert = (ToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1.0));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
    }

    @Test
    void toQuantityFunction() {
        ExpressionDef def = defs.get("StringToQuantityFun");
        assertThat(def, hasTypeAndResult(ToQuantity.class, "System.Quantity"));
        ToQuantity convert = (ToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0 'm'"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Quantity")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerToQuantityFun");
        assertThat(def, hasTypeAndResult(ToQuantity.class, "System.Quantity"));
        convert = (ToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));

        def = defs.get("DecimalToQuantityFun");
        assertThat(def, hasTypeAndResult(ToQuantity.class, "System.Quantity"));
        convert = (ToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1.0));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
    }

    @Test
    void convertsToQuantity() {
        ExpressionDef def = defs.get("StringConvertsToQuantity");
        assertThat(def, hasTypeAndResult(ConvertsToQuantity.class, "System.Boolean"));
        ConvertsToQuantity convert = (ConvertsToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor("3.0 'm'"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Quantity")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("IntegerConvertsToQuantity");
        assertThat(def, hasTypeAndResult(ConvertsToQuantity.class, "System.Boolean"));
        convert = (ConvertsToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));

        def = defs.get("DecimalConvertsToQuantity");
        assertThat(def, hasTypeAndResult(ConvertsToQuantity.class, "System.Boolean"));
        convert = (ConvertsToQuantity) def.getExpression();
        assertThat(convert.getOperand(), literalFor(1.0));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Quantity"));
    }

    @Test
    void toRatio() {
        ExpressionDef def = defs.get("StringToRatio");
        assertThat(def, hasTypeAndResult(ToRatio.class, "System.Ratio"));
        ToRatio convert = (ToRatio) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1:180"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Ratio"));
    }

    @Test
    void toRatioFunction() {
        ExpressionDef def = defs.get("StringToRatioFun");
        assertThat(def, hasTypeAndResult(ToRatio.class, "System.Ratio"));
        ToRatio convert = (ToRatio) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1:180"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Ratio")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void convertsToRatio() {
        ExpressionDef def = defs.get("StringConvertsToRatio");
        assertThat(def, hasTypeAndResult(ConvertsToRatio.class, "System.Boolean"));
        ConvertsToRatio convert = (ConvertsToRatio) def.getExpression();
        assertThat(convert.getOperand(), literalFor("1:180"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Ratio")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void toConcept() {
        ExpressionDef def = defs.get("CodeToConcept");
        assertThat(def, hasTypeAndResult(ToConcept.class, "System.Concept"));
        ToConcept toConcept = (ToConcept) def.getExpression();
        assertThat(toConcept.getOperand(), instanceOf(ExpressionRef.class));
        ExpressionRef ref = (ExpressionRef) toConcept.getOperand();
        assertThat(ref.getName(), is("MyCode"));
        assertThat(ref.getResultType().toString(), is("System.Code"));
        // validateTyping(toConcept, new QName("urn:hl7-org:elm-types:r1", "Concept"));

        def = defs.get("CodesToConcept");
        assertThat(def, hasTypeAndResult(ToConcept.class, "System.Concept"));
        toConcept = (ToConcept) def.getExpression();
        assertThat(toConcept.getOperand(), instanceOf(ExpressionRef.class));
        ref = (ExpressionRef) toConcept.getOperand();
        assertThat(ref.getName(), is("MyCodes"));
        assertThat(ref.getResultType().toString(), is("list<System.Code>"));
        // validateTyping(convert, new QName("urn:hl7-org:elm-types:r1", "Concept"));
    }

    @Test
    void toConceptFunction() {
        ExpressionDef def = defs.get("CodeToConceptFun");
        assertThat(def, hasTypeAndResult(ToConcept.class, "System.Concept"));
        ToConcept convert = (ToConcept) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(ExpressionRef.class));
        ExpressionRef ref = (ExpressionRef) convert.getOperand();
        assertThat(ref.getName(), is("MyCode"));
        assertThat(ref.getResultType().toString(), is("System.Code"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Concept")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());

        def = defs.get("CodesToConceptFun");
        assertThat(def, hasTypeAndResult(ToConcept.class, "System.Concept"));
        convert = (ToConcept) def.getExpression();
        assertThat(convert.getOperand(), instanceOf(ExpressionRef.class));
        ref = (ExpressionRef) convert.getOperand();
        assertThat(ref.getName(), is("MyCodes"));
        assertThat(ref.getResultType().toString(), is("list<System.Code>"));
        // assertThat(convert.getToType(), is(new QName("urn:hl7-org:elm-types:r1", "Concept")));
        // assertThat(convert.getToTypeSpecifier(), nullValue());
    }

    @Test
    void minValue() {
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
    void maxValue() {
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
