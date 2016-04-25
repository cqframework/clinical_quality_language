package org.cqframework.cql.cql2elm;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class CMS146JsonTest {

    @Test
    public void testCms146() throws IOException {
        File expectedJsonFile = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Expected.json").getFile(), "UTF-8"));
        String expectedJson = new Scanner(expectedJsonFile, "UTF-8").useDelimiter("\\Z").next();

        File cms146 = new File(URLDecoder.decode(CMS146JsonTest.class.getResource("CMS146v2_Test_CQM.cql").getFile(), "UTF-8"));
        String actualJson = CqlTranslator.fromFile(cms146, new LibraryManager()).toJson();
        assertThat(actualJson, sameJSONAs(expectedJson));
    }
}
