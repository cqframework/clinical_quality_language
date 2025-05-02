package org.cqframework.cql.cql2elm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.OperatorExpression;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class CMS146SignatureTest {

    // This is a count of the number of expected
    // signatures for each SignatureLevel when CMS146v2_Test_CQM.cql
    // is compiled.
    private static Object[][] sigCounts() {
        return new Object[][] {
            {SignatureLevel.None, 0},
            {SignatureLevel.Differing, 3},
            {SignatureLevel.Overloads, 11},
            {SignatureLevel.All, 34}
        };
    }

    @ParameterizedTest
    @MethodSource("sigCounts")
    void cms146SignatureLevels(SignatureLevel signatureLevel, int expectedSignatures) throws IOException {
        final File cms146 = getFile("CMS146v2_Test_CQM.cql");
        final ModelManager modelManager = new ModelManager();
        final CqlTranslator translator = CqlTranslator.fromFile(
                cms146,
                new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, signatureLevel)));

        var visitor = FunctionalElmVisitor.Companion.from(
                (Element elm, Void context) -> {
                    if (elm instanceof OperatorExpression fd) {
                        return fd.getSignature().isEmpty() ? 0 : 1;
                    } else {
                        return 0;
                    }
                },
                Integer::sum);

        var sigCount = visitor.visitLibrary(translator.getTranslatedLibrary().getLibrary(), null);

        assertEquals(expectedSignatures, sigCount);
    }

    private static File getFile(String name) {
        final URL resource = CMS146SignatureTest.class.getResource(name);

        if (resource == null) {
            throw new IllegalArgumentException("Cannot find file with name: " + name);
        }

        return new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
    }
}
