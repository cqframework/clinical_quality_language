package org.cqframework.cql.cql2elm.operators;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AgeOperatorsTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        var modelManager = new ModelManager();
        var translator = CqlTranslator.fromSource(
                buffered(asSource(AgeOperatorsTest.class.getResourceAsStream("../OperatorTests/AgeOperators.cql"))),
                new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        var library = translator.toELM();
        defs = new HashMap<>();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }
    }

    @Test
    void ageInYears() {
        ExpressionDef def = defs.get("TestAgeInYears");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand(), instanceOf(ToDate.class));
    }

    @Test
    void ageInMonths() {
        ExpressionDef def = defs.get("TestAgeInMonths");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand(), instanceOf(ToDate.class));
    }

    @Test
    void ageInWeeks() {
        ExpressionDef def = defs.get("TestAgeInWeeks");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    void ageInDays() {
        ExpressionDef def = defs.get("TestAgeInDays");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    void ageInHours() {
        ExpressionDef def = defs.get("TestAgeInHours");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.HOUR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    void ageInMinutes() {
        ExpressionDef def = defs.get("TestAgeInMinutes");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MINUTE));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    void ageInSeconds() {
        ExpressionDef def = defs.get("TestAgeInSeconds");
        assertThat(def, hasTypeAndResult(CalculateAge.class, "System.Integer"));
        CalculateAge age = (CalculateAge) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.SECOND));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand(), instanceOf(Property.class));
    }

    @Test
    void ageInYearsAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInYearsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    void ageInMonthsAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInMonthsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    void ageInWeeksAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInWeeksAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    void ageInDaysAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInDaysAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    void ageInYearsAtDate() {
        ExpressionDef def = defs.get("TestAgeInYearsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    void ageInMonthsAtDate() {
        ExpressionDef def = defs.get("TestAgeInMonthsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    void ageInWeeksAtDate() {
        ExpressionDef def = defs.get("TestAgeInWeeksAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    void ageInDaysAtDate() {
        ExpressionDef def = defs.get("TestAgeInDaysAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ToDate.class));
    }

    @Test
    void ageInHoursAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInHoursAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.HOUR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    void ageInMinutesAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInMinutesAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MINUTE));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    void ageInSecondsAtDateTime() {
        ExpressionDef def = defs.get("TestAgeInSecondsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.SECOND));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(Property.class));
    }

    @Test
    void calculateAgeInYearsAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInYearsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInMonthsAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInMonthsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInWeeksAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInWeeksAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInDaysAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInDaysAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInYearsAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInYearsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.YEAR));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInMonthsAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInMonthsAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MONTH));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInWeeksAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInWeeksAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.WEEK));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInDaysAtDate() {
        ExpressionDef def = defs.get("TestCalculateAgeInDaysAtDate");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.DAY));
        // Verify the date is _not_ being converted to a datetime
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInHoursAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInHoursAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.HOUR));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInMinutesAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInMinutesAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.MINUTE));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }

    @Test
    void calculateAgeInSecondsAtDateTime() {
        ExpressionDef def = defs.get("TestCalculateAgeInSecondsAtDateTime");
        assertThat(def, hasTypeAndResult(CalculateAgeAt.class, "System.Integer"));
        CalculateAgeAt age = (CalculateAgeAt) def.getExpression();
        assertThat(age.getPrecision(), is(DateTimePrecision.SECOND));
        // Verify the datetime is _not_ being converted to a date
        assertThat(age.getOperand().get(0), instanceOf(ExpressionRef.class));
    }
}
