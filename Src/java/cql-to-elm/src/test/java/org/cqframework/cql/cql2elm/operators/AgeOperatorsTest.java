package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;


import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AgeOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(AgeOperatorsTest.class.getResourceAsStream("../OperatorTests/AgeOperators.cql"), modelManager, new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }
    }

    @Test
    public void testAgeInYears() {
        ExpressionDef def = defs.get("TestAgeInYears");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand(), instanceOf(ToDate.class));
    }

    @Test
    public void testAgeInMonths() {
        ExpressionDef def = defs.get("TestAgeInMonths");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand(), instanceOf(ToDate.class));
    }

    @Test
    public void testAgeInWeeks() {
        ExpressionDef def = defs.get("TestAgeInWeeks");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    public void testAgeInDays() {
        ExpressionDef def = defs.get("TestAgeInDays");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    public void testAgeInHours() {
        ExpressionDef def = defs.get("TestAgeInHours");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.HOUR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    public void testAgeInMinutes() {
        ExpressionDef def = defs.get("TestAgeInMinutes");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MINUTE));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    public void testAgeInSeconds() {
        ExpressionDef def = defs.get("TestAgeInSeconds");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.SECOND));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    public void testAgeInYearsAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInYearsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    public void testAgeInMonthsAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInMonthsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    public void testAgeInWeeksAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInWeeksAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    public void testAgeInDaysAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInDaysAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    public void testAgeInYearsAtDate() {
        ExpressionDef def = defs.get("TestAgeInYearsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    public void testAgeInMonthsAtDate() {
        ExpressionDef def = defs.get("TestAgeInMonthsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    public void testAgeInWeeksAtDate() {
        ExpressionDef def = defs.get("TestAgeInWeeksAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    public void testAgeInDaysAtDate() {
        ExpressionDef def = defs.get("TestAgeInDaysAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    public void testAgeInHoursAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInHoursAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.HOUR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    public void testAgeInMinutesAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInMinutesAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MINUTE));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    public void testAgeInSecondsAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInSecondsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.SECOND));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }


    @Test
    public void testCalculateAgeInYearsAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInYearsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInMonthsAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInMonthsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInWeeksAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInWeeksAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInDaysAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInDaysAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInYearsAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInYearsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInMonthsAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInMonthsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInWeeksAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInWeeksAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInDaysAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInDaysAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInHoursAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInHoursAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.HOUR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInMinutesAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInMinutesAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MINUTE));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    public void testCalculateAgeInSecondsAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInSecondsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt)def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.SECOND));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }
}
