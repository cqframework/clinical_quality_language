package org.opencds.cqf.cql.engine.execution;

import java.util.Collections;

import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.CqlType;
import org.testng.annotations.Test;

public class IncludedConceptRefTest extends CqlExecutionTestBase {

    @Test
    public void testIncludedConceptRef() {
        Context ctx = new Context(library);
        ctx.registerLibraryLoader(new TestLibraryLoader(getLibraryManager()));

        Code code = new Code()
                .withCode("code-value")
                .withDisplay("code-display")
                .withSystem("http://system.org")
                .withVersion("1");
        Concept expected = new Concept()
                .withDisplay("concept-display")
                .withCodes(Collections.singletonList(code));

        CqlType actual = (CqlType)ctx.resolveExpressionRef("testIncludedConceptRef").getExpression().evaluate(ctx);

        CqlConceptTest.assertEqual(expected, actual);
    }

}
