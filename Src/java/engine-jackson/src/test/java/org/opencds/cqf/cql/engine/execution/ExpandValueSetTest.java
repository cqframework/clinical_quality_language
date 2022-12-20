package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;
import org.testng.annotations.Test;

public class ExpandValueSetTest extends CqlExecutionTestBase {

    @Test
    public void testExpandValueSetRef() {
        Code expected = new Code().withCode("M").withSystem("http://test.com/system");

        TerminologyProvider terminologyProvider = new TerminologyProvider() {
            public boolean in(Code code, ValueSetInfo valueSet) {
                return true;
            }

            public Iterable<Code> expand(ValueSetInfo valueSet) {
                return Collections.singletonList(expected);
            }

            public Code lookup(Code code, CodeSystemInfo codeSystem) {
                return null;
            }

        };
        Context ctx = new Context(library);
        ctx.registerLibraryLoader(new TestLibraryLoader(getLibraryManager()));
        ctx.registerTerminologyProvider(terminologyProvider);

        @SuppressWarnings("unchecked")
        List<Code> actual = (List<Code>)ctx.resolveExpressionRef("ExpandValueSet").getExpression().evaluate(ctx);
        assertNotNull(actual);
        assertEquals(actual.size(), 1);

        CqlConceptTest.assertEqual(expected, actual.get(0));
    }
}
