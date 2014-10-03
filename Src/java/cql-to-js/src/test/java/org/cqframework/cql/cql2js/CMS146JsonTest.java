package org.cqframework.cql.cql2js;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class CMS146JsonTest {

    @Test
    public void testCms146() throws IOException {
        File expectedJsonFile = new File(CMS146JsonTest.class.getResource("CMS146v2_Expected.json").getFile());
        String expectedJson = new Scanner(expectedJsonFile, "UTF-8").useDelimiter("\\Z").next();

        File cms146 = new File(CMS146JsonTest.class.getResource("CMS146v2_Test_CQM.cql").getFile());
        String actualJson = CqlLibrary.loadCql(cms146).asJson();
        assertThat(actualJson, sameJSONAs(expectedJson));
    }
}
