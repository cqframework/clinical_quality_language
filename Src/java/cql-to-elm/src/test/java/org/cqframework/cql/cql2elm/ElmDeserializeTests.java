package org.cqframework.cql.cql2elm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Retrieve;
import org.hl7.elm.r1.SingletonFrom;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ElmDeserializeTests {

    private Library library;

    @BeforeMethod
    public void setup() {
        try {
            this.library = CqlLibraryReader.read(ElmDeserializeTests.class.getResourceAsStream("ElmTests.xml"));
        } catch (IOException | JAXBException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }


    @Test
    public void TestJsonLibraryLoad() {
        try {
            Library library = CqlJsonLibraryReader.read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ANCFHIRDummy.json")));
            System.out.println("LIB:"+ library);
            Assert.assertTrue(library != null);
            Assert.assertTrue(library.getStatements() != null);
            Assert.assertTrue(library.getStatements().getDef() != null);
            Assert.assertTrue(library.getStatements().getDef().size() >= 2);
            Assert.assertTrue(library.getStatements().getDef().get(0) instanceof ExpressionDef);
            Assert.assertTrue(library.getStatements().getDef().get(0).getExpression() instanceof SingletonFrom);
            Assert.assertTrue(((SingletonFrom)library.getStatements().getDef().get(0).getExpression()).getOperand() instanceof Retrieve);
            Assert.assertTrue(library.getStatements().getDef().get(1) instanceof ExpressionDef);
            Assert.assertTrue(library.getStatements().getDef().get(1).getExpression() instanceof Retrieve);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void TestJsonTerminologyLibraryLoad() {
        try {
            Library library = CqlJsonLibraryReader.read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ANCFHIRTerminologyDummy.json")));
            Assert.assertTrue(library != null);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    private void testElmDeserialization(String path, String xmlFileName, String jsonFileName) throws IOException, JAXBException {
        Library xmlLibrary = null;
        try {
            xmlLibrary = CqlLibraryReader.read(new FileReader(path + "/" + xmlFileName));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from xml %s: %s", xmlFileName, e.getMessage()));
        }

        Library jsonLibrary = null;
        try {
            jsonLibrary = CqlJsonLibraryReader.read(new FileReader(path + "/" + jsonFileName));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from json %s: %s", jsonFileName, e.getMessage()));
        }

        if (xmlLibrary != null && jsonLibrary != null) {
            Assert.assertTrue(equivalent(xmlLibrary, jsonLibrary));
        }
    }

    private void testElmDeserialization(String directoryName) throws URISyntaxException, IOException, JAXBException {
        URL dirURL = ElmDeserializeTests.class.getResource(String.format("EmlDeserializeRegression/%s/", directoryName));
        File file = new File(dirURL.toURI());
        for (String fileName : file.list()) {
            if (fileName.endsWith(".xml")) {
                try {
                    testElmDeserialization(file.getAbsolutePath(), fileName, fileName.substring(0, fileName.length() - 4) + ".json");
                }
                catch (Exception e) {
                    throw new IllegalArgumentException(String.format("Errors occurred testing: %s", fileName));
                }
            }
        }
    }

    @Test
    public void RegressionTestJsonSerializer() throws URISyntaxException, IOException, JAXBException {
        // This test validates that the ELM library deserialized from the Json matches the ELM library deserialized from Xml
        // Regression inputs are annual update measure Xml for QDM and FHIR
        testElmDeserialization("qdm");
        testElmDeserialization("fhir");
        testElmDeserialization("qdm2020");
    }

    private static boolean equivalent(Library left, Library right) {
        if (left == null && right == null) {
            return true;
        }

        boolean result = true;

        if (left != null) {
            result = result && left.getIdentifier().equals(right.getIdentifier());
        }

        if(left.getIncludes() != null && left.getIncludes().getDef() != null) {
            result = result && left.getIncludes().getDef().size() == right.getIncludes().getDef().size();
        }

        if(left.getUsings() != null && left.getUsings().getDef() != null) {
            result = result && left.getUsings().getDef().size() == right.getUsings().getDef().size();
        }

        if(left.getValueSets() != null && left.getValueSets().getDef() != null) {
            result = result && left.getValueSets().getDef().size() == right.getValueSets().getDef().size();
        }

        if(left.getCodeSystems() != null && left.getCodeSystems().getDef() != null) {
            result = result && left.getCodeSystems().getDef().size() == right.getCodeSystems().getDef().size();
        }

        if(left.getCodes() != null && left.getCodes().getDef() != null) {
            result = result && left.getCodes().getDef().size() == right.getCodes().getDef().size();
        }

        if(left.getConcepts() != null && left.getConcepts().getDef() != null) {
            result = result && left.getConcepts().getDef().size() == right.getConcepts().getDef().size();
        }

        if(left.getParameters() != null && left.getParameters().getDef() != null) {
            result = result && left.getParameters().getDef().size() == right.getParameters().getDef().size();
        }

        if(left.getStatements() != null && left.getStatements().getDef() != null) {
            result = result && left.getStatements().getDef().size() == right.getStatements().getDef().size();
        }

        if(left.getContexts() != null && left.getContexts().getDef() != null) {
            result = result && left.getContexts().getDef().size() == right.getContexts().getDef().size();
        }

        return result;
    }
}