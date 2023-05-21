package org.opencds.cqf.cql.engine.execution;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.util.*;

public class CqlMainSuiteTest extends CqlTestBase {

    @Test
    public void test_all_portable_cql_engine_tests() {
        EvaluationResult evaluationResult = new EvaluationResult();

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlTestSuite"), null, null, null, null, ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId()));

        Object result;
        System.out.println(evaluationResult.expressionResults.size());
        for (Map.Entry entry : evaluationResult.expressionResults.entrySet()) {
            if(entry.getKey().toString().startsWith("test")) {
                System.out.println(((ExpressionResult)entry.getValue()).value());
                if(((ExpressionResult)entry.getValue()).value() != null) {
                Assert.assertEquals(
                        (String) ((ExpressionResult) entry.getValue()).value(),
                        entry.getKey().toString().replaceAll("test_", "") + " TEST PASSED"
                );
                }
            }
        }

    }

}
