package org.opencds.cqf.cql.engine.execution;

import static org.opencds.cqf.cql.engine.execution.CqlConceptTest.assertEqual;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.CqlType;

class IncludedConceptRefTest extends CqlTestBase {

    @Test
    void included_concept_ref() {

        Code code = new Code()
                .withCode("code-value")
                .withDisplay("code-display")
                .withSystem("http://system.org")
                .withVersion("1");
        Concept expected = new Concept().withDisplay("concept-display").withCodes(Collections.singletonList(code));

        var results = engine.evaluate(toElmIdentifier("IncludedConceptRefTest"));
        CqlType actual =
                (CqlType) results.forExpression("testIncludedConceptRef").value();

        assertEqual(expected, actual);
    }
}
