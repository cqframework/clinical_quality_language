package org.cqframework.cql.cql2elm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Query;
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
            this.library = ElmXmlLibraryReader.read(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/ElmTests.xml"));
        } catch (IOException | JAXBException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }


    @Test
    public void TestJxsonLibraryLoad() {
        try {
            Library library = ElmJxsonLibraryReader.read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/ANCFHIRDummy.json")));
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
    public void TestJsonLibraryLoad() {
        try {
            Library library = ElmJsonLibraryReader.read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/fhir/json/AdultOutpatientEncounters_FHIR4-2.0.000.json")));
            Assert.assertTrue(library != null);
            Assert.assertEquals(library.getIdentifier().getId(), "AdultOutpatientEncounters_FHIR4");
            Assert.assertEquals(library.getIdentifier().getVersion(), "2.0.000");
            Assert.assertTrue(library.getUsings() != null);
            Assert.assertTrue(library.getUsings().getDef() != null);
            Assert.assertTrue(library.getUsings().getDef().size() >= 2);
            Assert.assertTrue(library.getStatements() != null);
            Assert.assertTrue(library.getStatements().getDef() != null);
            Assert.assertTrue(library.getStatements().getDef().get(0) instanceof ExpressionDef);
            Assert.assertTrue(library.getStatements().getDef().get(0).getExpression() instanceof SingletonFrom);
            Assert.assertTrue(((SingletonFrom)library.getStatements().getDef().get(0).getExpression()).getOperand() instanceof Retrieve);
            Assert.assertEquals(library.getStatements().getDef().get(1).getName(), "Qualifying Encounters");
            Assert.assertTrue(library.getStatements().getDef().get(1) instanceof ExpressionDef);
            Assert.assertTrue(library.getStatements().getDef().get(1).getExpression() instanceof Query);
        } catch (IOException | JAXBException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void TestXmlLibraryLoad() {
        try {
            Library library = ElmXmlLibraryReader.read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/fhir/AdultOutpatientEncounters_FHIR4-2.0.000.xml")));
            Assert.assertTrue(library != null);
            Assert.assertEquals(library.getIdentifier().getId(), "AdultOutpatientEncounters_FHIR4");
            Assert.assertEquals(library.getIdentifier().getVersion(), "2.0.000");
            String translatorOptions = "EnableDateRangeOptimization,EnableAnnotations,EnableLocators,EnableResultTypes,DisableListDemotion,DisableListPromotion,DisableMethodInvocation";
            Assert.assertEquals(((CqlToElmInfo) library.getAnnotation().get(0)).getTranslatorOptions(), translatorOptions);
            Assert.assertTrue(library.getUsings() != null);
            Assert.assertTrue(library.getUsings().getDef() != null);
            Assert.assertTrue(library.getUsings().getDef().size() >= 2);
            Assert.assertTrue(library.getStatements() != null);
            Assert.assertTrue(library.getStatements().getDef() != null);
            Assert.assertTrue(library.getStatements().getDef().get(0) instanceof ExpressionDef);
            Assert.assertTrue(library.getStatements().getDef().get(0).getExpression() instanceof SingletonFrom);
            Assert.assertTrue(((SingletonFrom)library.getStatements().getDef().get(0).getExpression()).getOperand() instanceof Retrieve);
            Assert.assertEquals(library.getStatements().getDef().get(1).getName(), "Qualifying Encounters");
            Assert.assertTrue(library.getStatements().getDef().get(1) instanceof ExpressionDef);
            Assert.assertTrue(library.getStatements().getDef().get(1).getExpression() instanceof Query);
        } catch (IOException | JAXBException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void TestJxsonTerminologyLibraryLoad() {
        try {
            Library library = ElmJxsonLibraryReader.read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/ANCFHIRTerminologyDummy.json")));
            Assert.assertTrue(library != null);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    private void testElmDeserialization(String path, String xmlFileName, String jsonFileName) throws IOException, JAXBException {
        Library xmlLibrary = null;
        try {
            xmlLibrary = ElmXmlLibraryReader.read(new FileReader(path + "/" + xmlFileName));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from xml %s: %s", xmlFileName, e.getMessage()));
        }

        Library jxsonLibrary = null;
        try {
            jxsonLibrary = ElmJxsonLibraryReader.read(new FileReader(path + "/jxson/" + jsonFileName));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from json %s: %s", jsonFileName, e.getMessage()));
        }

        Library jsonLibrary = null;
        try {
            jsonLibrary = ElmJsonLibraryReader.read(new FileReader(path + "/json/" + jsonFileName));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from json %s: %s", jsonFileName, e.getMessage()));
        }

        if (xmlLibrary != null && jxsonLibrary != null) {
            Assert.assertTrue(equivalent(xmlLibrary, jxsonLibrary, jsonLibrary));
        }
    }

    private void testElmDeserialization(String directoryName) throws URISyntaxException, IOException, JAXBException {
        URL dirURL = ElmDeserializeTests.class.getResource(String.format("ElmDeserialize/%s/", directoryName));
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

    private static boolean equivalent(Library xmlLibrary, Library jxsonLibrary, Library jsonLibrary) {
        if (xmlLibrary == null && jxsonLibrary == null && jsonLibrary == null) {
            return true;
        }

        boolean result = true;

        if (xmlLibrary != null) {
            result = result && xmlLibrary.getIdentifier().equals(jxsonLibrary.getIdentifier());
            result = result && xmlLibrary.getIdentifier().equals(jsonLibrary.getIdentifier());
        }

        if(xmlLibrary.getIncludes() != null && xmlLibrary.getIncludes().getDef() != null) {
            result = result && xmlLibrary.getIncludes().getDef().size() == jxsonLibrary.getIncludes().getDef().size();
            result = result && xmlLibrary.getIncludes().getDef().size() == jsonLibrary.getIncludes().getDef().size();
        }

        if(xmlLibrary.getUsings() != null && xmlLibrary.getUsings().getDef() != null) {
            result = result && xmlLibrary.getUsings().getDef().size() == jxsonLibrary.getUsings().getDef().size();
            result = result && xmlLibrary.getUsings().getDef().size() == jsonLibrary.getUsings().getDef().size();
        }

        if(xmlLibrary.getValueSets() != null && xmlLibrary.getValueSets().getDef() != null) {
            result = result && xmlLibrary.getValueSets().getDef().size() == jxsonLibrary.getValueSets().getDef().size();
            result = result && xmlLibrary.getValueSets().getDef().size() == jsonLibrary.getValueSets().getDef().size();
        }

        if(xmlLibrary.getCodeSystems() != null && xmlLibrary.getCodeSystems().getDef() != null) {
            result = result && xmlLibrary.getCodeSystems().getDef().size() == jxsonLibrary.getCodeSystems().getDef().size();
            result = result && xmlLibrary.getCodeSystems().getDef().size() == jsonLibrary.getCodeSystems().getDef().size();
        }

        if(xmlLibrary.getCodes() != null && xmlLibrary.getCodes().getDef() != null) {
            result = result && xmlLibrary.getCodes().getDef().size() == jxsonLibrary.getCodes().getDef().size();
            result = result && xmlLibrary.getCodes().getDef().size() == jsonLibrary.getCodes().getDef().size();
        }

        if(xmlLibrary.getConcepts() != null && xmlLibrary.getConcepts().getDef() != null) {
            result = result && xmlLibrary.getConcepts().getDef().size() == jxsonLibrary.getConcepts().getDef().size();
            result = result && xmlLibrary.getConcepts().getDef().size() == jsonLibrary.getConcepts().getDef().size();
        }

        if(xmlLibrary.getParameters() != null && xmlLibrary.getParameters().getDef() != null) {
            result = result && xmlLibrary.getParameters().getDef().size() == jxsonLibrary.getParameters().getDef().size();
            result = result && xmlLibrary.getParameters().getDef().size() == jsonLibrary.getParameters().getDef().size();
        }

        if(xmlLibrary.getStatements() != null && xmlLibrary.getStatements().getDef() != null) {
            result = result && xmlLibrary.getStatements().getDef().size() == jxsonLibrary.getStatements().getDef().size();
            result = result && xmlLibrary.getStatements().getDef().size() == jsonLibrary.getStatements().getDef().size();
        }

        if(xmlLibrary.getContexts() != null && xmlLibrary.getContexts().getDef() != null) {
            result = result && xmlLibrary.getContexts().getDef().size() == jxsonLibrary.getContexts().getDef().size();
            result = result && xmlLibrary.getContexts().getDef().size() == jsonLibrary.getContexts().getDef().size();
        }

        return result;
    }
}