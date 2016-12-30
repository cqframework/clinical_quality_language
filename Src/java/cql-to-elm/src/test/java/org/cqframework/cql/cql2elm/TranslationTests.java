package org.cqframework.cql.cql2elm;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TranslationTests {
    // TODO: sameXMLAs? Couldn't find such a thing in hamcrest, but I don't want this to run on the JSON, I want it to verify the actual XML.
    @Test(enabled=false)
    public void testPatientPropertyAccess() throws IOException, JAXBException {
        File expectedXmlFile = new File(Cql2ElmVisitorTest.class.getResource("PropertyTest_ELM.xml").getFile());
        String expectedXml = new Scanner(expectedXmlFile, "UTF-8").useDelimiter("\\Z").next();

        File propertyTestFile = new File(Cql2ElmVisitorTest.class.getResource("PropertyTest.cql").getFile());
        ModelManager modelManager = new ModelManager();
        String actualXml = CqlTranslator.fromFile(propertyTestFile, modelManager, new LibraryManager(modelManager)).toXml();
        assertThat(actualXml, is(expectedXml));
    }

    @Test(enabled=false)
    public void testCMS146v2XML() throws IOException {
        String expectedXml = "";
        File cqlFile = new File(Cql2ElmVisitorTest.class.getResource("CMS146v2_Test_CQM.cql").getFile());
        ModelManager modelManager = new ModelManager();
        String actualXml = CqlTranslator.fromFile(cqlFile, modelManager, new LibraryManager(modelManager)).toXml();
        assertThat(actualXml, is(expectedXml));
    }
}
