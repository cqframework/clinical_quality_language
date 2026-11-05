package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Test

internal class Issue213 : CqlTestBase() {
    @Test
    fun interval() {
        //
        //
        //        results = engine.evaluate(toElmIdentifier("Issue213"), null, null, null, null,
        // null);
        //        Object value = results["Collapsed Treatment Intervals"].value;
        //        Assertions.assertTrue(EquivalentEvaluator.equivalent(((Interval) ((List)
        // value).get(0)).getStart(),
        // new
        // DateTime(null, 2018, 1, 1)));
        //        Assertions.assertTrue(EquivalentEvaluator.equivalent(((Interval) ((List)
        // value).get(0)).getEnd(), new
        // DateTime(null, 2018, 8, 28)));
        //        Assertions.assertTrue(EquivalentEvaluator.equivalent(((Interval) ((List)
        // value).get(1)).getStart(),
        // new
        // DateTime(null, 2018, 8, 30)));
        //        Assertions.assertTrue(EquivalentEvaluator.equivalent(((Interval) ((List)
        // value).get(1)).getEnd(), new
        // DateTime(null, 2018, 10, 15)));
        //        assertTrue(((List) value).size() == 2);
    }
}
