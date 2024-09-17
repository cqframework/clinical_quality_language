package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.ValueSet;

class IncludedValueSetRefTest {

    @Test
    void all_included_valueset() {
        LibraryManager libraryManager = new LibraryManager(new ModelManager());
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        Environment environment = new Environment(libraryManager);

        CqlEngine engine = new CqlEngine(environment);

        var results = engine.evaluate(CqlTestBase.toElmIdentifier("IncludedValueSetRefTest"));

        var actual = (ValueSet) results.forExpression("IncludedValueSet").value();

        assertNotNull(actual);
        assertEquals("http://test/common", actual.getId());
    }
}
