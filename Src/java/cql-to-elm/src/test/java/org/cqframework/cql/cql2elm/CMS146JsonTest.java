package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class CMS146JsonTest {

    private static final String SIG_LEVEL_TOKEN = "{SIGNATURE_LEVEL_TOKEN}";

    @Test
    public void testCms146_SignatureLevel_None() throws IOException {
        File expectedJsonFile = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Expected.json").getFile(), "UTF-8"));
        String expectedJson = new Scanner(expectedJsonFile, "UTF-8").useDelimiter("\\Z").next().replace(SIG_LEVEL_TOKEN, SignatureLevel.None.name());

        File cms146 = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Test_CQM.cql").getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromFile(cms146, new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.None)));
        String actualJson = translator.toJson();
        assertThat(actualJson, sameJSONAs(expectedJson));
    }

    // LUKETODO:
    /*
        but: library.statements.def[4].expression.relationship[1].suchThat.operand[1].operand
        Unexpected: signature
         ; library.statements.def[6].expression.operand.relationship[0].suchThat.operand[1].operand
        Unexpected: signature
         ; library.statements.def[11].expression.operand.where.operand[1].operand
        Unexpected: signatur
     */
    @Test
    public void testCms146_SignatureLevel_Differing() throws IOException {
        File expectedJsonFile = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Expected.json").getFile(), "UTF-8"));
        String expectedJson = new Scanner(expectedJsonFile, "UTF-8").useDelimiter("\\Z").next().replace(SIG_LEVEL_TOKEN, SignatureLevel.Differing.name());

        File cms146 = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Test_CQM.cql").getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromFile(cms146, new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.Differing)));
        String actualJson = translator.toJson();
        assertThat(actualJson, sameJSONAs(expectedJson));
    }

    @Test
    public void testCms146_SignatureLevel_Overloads() throws IOException {
        File expectedJsonFile = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Expected.json").getFile(), "UTF-8"));
        String expectedJson = new Scanner(expectedJsonFile, "UTF-8").useDelimiter("\\Z").next().replace(SIG_LEVEL_TOKEN, SignatureLevel.Overloads.name());

        File cms146 = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Test_CQM.cql").getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromFile(cms146, new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.Overloads)));
        String actualJson = translator.toJson();
        assertThat(actualJson, sameJSONAs(expectedJson));
    }

    @Test
    public void testCms146_SignatureLevel_All() throws IOException {
        File expectedJsonFile = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Expected.json").getFile(), "UTF-8"));
        String expectedJson = new Scanner(expectedJsonFile, "UTF-8").useDelimiter("\\Z").next().replace(SIG_LEVEL_TOKEN, SignatureLevel.All.name());

        File cms146 = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Test_CQM.cql").getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromFile(cms146, new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.All)));
        String actualJson = translator.toJson();
        assertThat(actualJson, sameJSONAs(expectedJson));
    }
}
