package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.CqlType;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.opencds.cqf.cql.engine.execution.ToConceptTest.assertEqual;

public class IncludedConceptRefTest extends CqlTestBase {

    @Test
    public void test_included_concept_ref() {

        Code code = new Code()
                .withCode("code-value")
                .withDisplay("code-display")
                .withSystem("http://system.org")
                .withVersion("1");
        Concept expected = new Concept()
                .withDisplay("concept-display")
                .withCodes(Collections.singletonList(code));

        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("IncludedConceptRefTest"), null, null, null, null, null);
        CqlType actual = (CqlType)evaluationResult.expressionResults.get("testIncludedConceptRef").value();

        assertEqual(expected, actual);
    }
}
