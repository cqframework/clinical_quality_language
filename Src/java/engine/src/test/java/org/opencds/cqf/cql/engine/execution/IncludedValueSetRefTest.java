package org.opencds.cqf.cql.engine.execution;

import static org.opencds.cqf.cql.engine.execution.CqlConceptTest.assertEqual;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;
import org.testng.annotations.Test;

public class IncludedValueSetRefTest {

    @Test
    public void test_all_included_valueset() {
        LibraryManager libraryManager = new LibraryManager(new ModelManager());
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

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

        Environment environment = new Environment(libraryManager, null, terminologyProvider);

        CqlEngine engine = new CqlEngine(environment);

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(CqlTestBase.toElmIdentifier("IncludedValueSetRefTest"));

        @SuppressWarnings("unchecked")
        List<Code> actual =
                (List<Code>) evaluationResult.forExpression("IncludedValueSet").value();
        assertNotNull(actual);
        assertEquals(actual.size(), 1);

        assertEqual(expected, actual.get(0));
    }
}
