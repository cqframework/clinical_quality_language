package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Time;

class CqlNulLogicalOperatorsTest extends CqlTestBase {

    @Test
    void all_null_logical_operators() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();
        var results = engine.evaluate(toElmIdentifier("CqlNullologicalOperatorsTest"));
        var value = results.forExpression("CoalesceANull").value();
        assertThat(value, is("a"));

        value = results.forExpression("CoalesceNullA").value();
        assertThat(value, is("a"));

        value = results.forExpression("CoalesceEmptyList").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("CoalesceListFirstA").value();
        assertThat(value, is("a"));

        value = results.forExpression("CoalesceListLastA").value();
        assertThat(value, is("a"));

        value = results.forExpression("CoalesceFirstList").value();
        assertThat(value, is(Collections.singletonList("a")));

        value = results.forExpression("CoalesceLastList").value();
        assertThat(value, is(Collections.singletonList("a")));

        value = results.forExpression("DateTimeCoalesce").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 5, 18)));

        value = results.forExpression("DateTimeListCoalesce").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 5, 18)));

        value = results.forExpression("TimeCoalesce").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(5, 15, 33, 556)));

        value = results.forExpression("TimeListCoalesce").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(5, 15, 33, 556)));

        value = results.forExpression("IsNullTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IsNullFalseEmptyString").value();
        assertThat(value, is(false));

        value = results.forExpression("IsNullAlsoFalseAbcString").value();
        assertThat(value, is(false));

        value = results.forExpression("IsNullAlsoFalseNumber1").value();
        assertThat(value, is(false));

        value = results.forExpression("IsNullAlsoFalseNumberZero").value();
        assertThat(value, is(false));

        value = results.forExpression("IsFalseFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("IsFalseTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("IsFalseNull").value();
        assertThat(value, is(false));

        value = results.forExpression("IsTrueTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IsTrueFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IsTrueNull").value();
        assertThat(value, is(false));
    }
}
