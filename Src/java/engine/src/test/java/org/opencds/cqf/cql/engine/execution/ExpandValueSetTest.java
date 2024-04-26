package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.opencds.cqf.cql.engine.execution.CqlConceptTest.assertEqual;

import java.util.Collections;
import java.util.List;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

class ExpandValueSetTest {

    @Test
    void all_expand_valueset() {
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
        var results = engine.evaluate(CqlTestBase.toElmIdentifier("ExpandValueSetTest"));

        @SuppressWarnings("unchecked")
        List<Code> actual = (List<Code>) results.forExpression("ExpandValueSet").value();
        assertNotNull(actual);
        assertEquals(1, actual.size());

        assertEqual(expected, actual.get(0));
    }
}
