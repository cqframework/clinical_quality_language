package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.SignatureLevel;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.exception.CqlException;

@SuppressWarnings("removal")
class CqlListDistinguishedOverloadsTest extends CqlTestBase {

    private static final VersionedIdentifier library =
            new VersionedIdentifier().withId("CqlListDistinguishedOverloads");

    @Test
    void list_overload() {
        var compilerOptions = CqlCompilerOptions.defaultOptions();

        var engine1 = getEngine(compilerOptions.withSignatureLevel(SignatureLevel.Overloads));
        var value = engine1.expression(library, "Test").value();
        assertEquals("1, 2, 3, 4, 5", value);

        var engine2 = getEngine(compilerOptions.withSignatureLevel(SignatureLevel.None));
        var cqlException = assertThrows(CqlException.class, () -> engine2.expression(library, "Test"));
        assertEquals(
                "Ambiguous call to operator 'toString(java.util.List)' in library 'CqlListDistinguishedOverloads'.",
                cqlException.getMessage());
    }
}
