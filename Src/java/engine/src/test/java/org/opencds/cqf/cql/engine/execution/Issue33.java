package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;


public class Issue33 extends CqlTestBase {

    @Test
    public void testInterval() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("Issue33"));
        Object result = evaluationResult.forExpression("Issue33").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(bigDecimalZoneOffset, 2017, 12, 20, 11, 0, 0)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(bigDecimalZoneOffset, 2017, 12, 20, 23, 59, 59, 999)));

    }
}
