package org.cqframework.cql.elm.serializing.jaxb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.utility.Visitors;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.OperatorExpression;
import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;

@Disabled(
        "Replaced with a direct count of signatures in CMS146SignatureTest.java. Keeping to use for serialization tests.")
class CMS146JsonTest {

    private static Object[][] sigFileAndSigLevel() {
        return new Object[][] {
            {"CMS146v2_Expected_SignatureLevel_None.json", SignatureLevel.None},
            {"CMS146v2_Expected_SignatureLevel_Differing.json", SignatureLevel.Differing},
            {"CMS146v2_Expected_SignatureLevel_Overloads.json", SignatureLevel.Overloads},
            {"CMS146v2_Expected_SignatureLevel_All.json", SignatureLevel.All}
        };
    }

    @ParameterizedTest
    @MethodSource("sigFileAndSigLevel")
    void cms146SignatureLevels(String fileName, SignatureLevel expectedSignatureLevel)
            throws IOException, JSONException {
        final String expectedJson = getJson(fileName);

        final File cms146 = getFile("CMS146v2_Test_CQM.cql");
        final ModelManager modelManager = new ModelManager();
        final CqlTranslator translator = CqlTranslator.fromFile(
                cms146,
                new LibraryManager(
                        modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, expectedSignatureLevel)));
        final String actualJson = translator.toJson();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    private static String getJson(String name) throws IOException {
        return new Scanner(getFile(name), StandardCharsets.UTF_8)
                .useDelimiter("\\Z")
                .next();
    }

    private static File getFile(String name) {
        final URL resource = CMS146JsonTest.class.getResource(name);

        if (resource == null) {
            throw new IllegalArgumentException("Cannot find file with name: " + name);
        }

        return new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
    }

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

        var visitor = Visitors.from(
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
}
