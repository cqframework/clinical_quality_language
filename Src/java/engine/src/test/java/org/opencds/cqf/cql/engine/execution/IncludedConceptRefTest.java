package org.opencds.cqf.cql.engine.execution;

import static org.opencds.cqf.cql.engine.execution.CqlConceptTest.assertEqual;

import java.util.Collections;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.CqlType;
import org.testng.annotations.Test;

public class IncludedConceptRefTest extends CqlTestBase {

    @Test
    public void test_included_concept_ref() {

        Code code = new Code()
                .withCode("code-value")
                .withDisplay("code-display")
                .withSystem("http://system.org")
                .withVersion("1");
        Concept expected = new Concept().withDisplay("concept-display").withCodes(Collections.singletonList(code));

        var evaluationResult = engine.evaluate(toIdentifier("IncludedConceptRefTest"));
        CqlType actual = (CqlType)
                evaluationResult.forExpression("testIncludedConceptRef").value();

        assertEqual(expected, actual);
    }
}
