package org.opencds.cqf.cql.engine.execution;

import java.math.BigDecimal;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Issue33 extends CqlTestBase {

    @Test
    public void testInterval() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var results = engine.evaluate(toElmIdentifier("Issue33"));
        Object value = results.forExpression("Issue33").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getStart(), new DateTime(bigDecimalZoneOffset, 2017, 12, 20, 11, 0, 0)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getEnd(), new DateTime(bigDecimalZoneOffset, 2017, 12, 20, 23, 59, 59, 999)));
    }
}
