package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class CMS146JsonTest {

    @DataProvider(name = "sigFileAndSigLevel")
    private static Object[][] sigFileAndSigLevel() {
        return new Object[][] {
                {"CMS146v2_Expected_SignatureLevel_None.json", SignatureLevel.None},
                {"CMS146v2_Expected_SignatureLevel_Differing.json", SignatureLevel.Differing},
                {"CMS146v2_Expected_SignatureLevel_Overloads.json", SignatureLevel.Overloads},
                {"CMS146v2_Expected_SignatureLevel_All.json", SignatureLevel.All}
        };
    }

    @Test(dataProvider = "sigFileAndSigLevel")
    public void testCms146_SignatureLevels(String fileName, SignatureLevel expectedSignatureLevel) throws IOException {
        final String expectedJson = getJson(fileName);

        final File cms146 = getFile("CMS146v2_Test_CQM.cql");
        final ModelManager modelManager = new ModelManager();
        final CqlTranslator translator = CqlTranslator.fromFile(cms146, new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, expectedSignatureLevel)));
        final String actualJson = translator.toJson();
        assertThat(actualJson, sameJSONAs(expectedJson));
    }

    private static String getJson(String name) throws IOException {
        return new Scanner(getFile(name), StandardCharsets.UTF_8)
                .useDelimiter("\\Z").next();
    }

    private static File getFile(String name) {
        final URL resource = CMS146JsonTest.class.getResource(name);

        if (resource == null) {
            throw new IllegalArgumentException("Cannot find file with name: " + name);
        }

        return new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
    }
}
