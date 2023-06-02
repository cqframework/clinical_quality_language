package org.opencds.cqf.cql.engine.execution;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.serializing.jaxb.JsonCqlLibraryReader;
import org.opencds.cqf.cql.engine.serializing.jaxb.XmlCqlLibraryReader;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class ElmRegressionTests implements ITest {
    private final String path;
    private final String fileName;

    @Factory(dataProvider = "dataMethod")
    public ElmRegressionTests(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    @DataProvider
    public static Object[][] dataMethod() throws URISyntaxException {
        List<String[]> filesToTest = new ArrayList<>();
        filesToTest.addAll(collectTestFilesFrom("qdm"));
        filesToTest.addAll(collectTestFilesFrom("fhir"));
        filesToTest.addAll(collectTestFilesFrom("qdm2020"));
        return filesToTest.toArray(new String[filesToTest.size()][]);
    }

    public static List<String[]> collectTestFilesFrom(String directoryName) throws URISyntaxException {
        List<String[]> filesToTest = new ArrayList<>();
        URL dirURL = ElmRegressionTests.class.getResource(String.format("ElmTests/Regression/%s/", directoryName));
        File file = new File(dirURL.toURI());
        for (String fileName : file.list()) {
            if (fileName.endsWith(".xml")) {
                String name = fileName.substring(0, fileName.length() - 4);
                filesToTest.add(new String[]{ file.getAbsolutePath(), name });
            }
        }
        return filesToTest;
    }

    public String getPathName() {
        return path.substring(path.lastIndexOf("/")+1).toUpperCase();
    }

    public String getTestName() {
        return "test" + getPathName() + fileName;
    }

    @Test
    private void testElmDeserialization() throws IOException, JAXBException {
        Library xmlLibrary = null;
        try {
            xmlLibrary = new XmlCqlLibraryReader().read(new FileReader(path + "/" + fileName + ".xml"));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from xml %s: %s", getPathName() + fileName, e.getMessage()));
        }

        Library jsonLibrary = null;
        try {
            jsonLibrary = new JsonCqlLibraryReader().read(new FileReader(path + "/" + fileName + ".json"));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from json %s: %s", getPathName() + fileName, e.getMessage()));
        }

        if (xmlLibrary != null && jsonLibrary != null) {
            Assert.assertTrue(equivalent(xmlLibrary, jsonLibrary));
        }
    }

    private static boolean equivalent(Library left, Library right) {
        if (left == null && right == null) {
            return true;
        }

        if (left != null) {
            return left.getIdentifier().equals(right.getIdentifier());
        }

        // TODO: validate ELM equivalence... big job...
        // Simplest would be to introduce on Executable...

        return false;
    }
}
