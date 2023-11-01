package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.nullValue;

public class CqlTypesTest2 extends CqlTestBase {

    @Test
    public void test_all_types() {
        final SoftAssert softAssert = new SoftAssert();

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlTypesTest2"));
        Object result;

        result = evaluationResult.forExpression("DateTimeMin").value();
        final DateTime actualDateTimeMin = (DateTime)result;
        final DateTime expectedDateTimeMin1 = new DateTime(null, 1, 1, 1, 0, 0, 0, 0);
        final DateTime expectedDateTimeMin = new DateTime(OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, OffsetDateTime.now().getOffset()));
        final boolean minEquivalent = EquivalentEvaluator.equivalent(result, expectedDateTimeMin);
        // LUKETODO:  this fails because we have a nonsensical timezone mismatch  do we care?  -05:17 vs -05:17:32
        // LUKETODO:  in case the code is unmodified from master, both timezones are -05:17:32 so they are considered equivalent
        // LUKETODO:  under the non-DST offsert, this is -04:00 vs. -05:17:32
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, expectedDateTimeMin), "DateTimeMin");

        result = evaluationResult.forExpression("DateTimeMax").value();
        final DateTime expectedDateTimeMax = new DateTime(null, 9999, 12, 31, 23, 59, 59, 999);
        final DateTime actualDateTimeMax = (DateTime)result;
        final boolean maxEquivalent = EquivalentEvaluator.equivalent(result, expectedDateTimeMax);
        // LUKETODO:  under the non-DST offsert, this is -04:00 vs. -05:00:00
        softAssert.assertTrue(EquivalentEvaluator.equivalent(result, expectedDateTimeMax),"DateTimeMax");

        softAssert.assertAll();
    }
}
