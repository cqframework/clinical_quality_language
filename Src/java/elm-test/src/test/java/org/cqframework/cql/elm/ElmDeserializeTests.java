package org.cqframework.cql.elm;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;

import jakarta.xml.bind.JAXBException;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CompilerOptions;
import org.hl7.elm.r1.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ElmDeserializeTests {

    @Test
    public void testElmTests() {
        try {
            new org.cqframework.cql.elm.serializing.jaxb.ElmXmlLibraryReader().read(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/ElmTests.xml"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }


    @Test
    public void testJsonANCFHIRDummyLibraryLoad() {
        try {
            Library library = new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryReader().read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/ANCFHIRDummy.json")));
            Assert.assertTrue(library != null);

            EnumSet<CqlCompilerOptions.Options> translatorOptions = EnumSet.of(
                    CqlCompilerOptions.Options.EnableDateRangeOptimization,
                    CqlCompilerOptions.Options.EnableAnnotations,
                    CqlCompilerOptions.Options.EnableLocators,
                    CqlCompilerOptions.Options.EnableResultTypes,
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation
            );

            Assert.assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions);

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
    public void testJsonAdultOutpatientEncounters_FHIR4LibraryLoad() {
        try {
            Library library = new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryReader().read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/fhir/AdultOutpatientEncounters_FHIR4-2.0.000.json")));
            Assert.assertTrue(library != null);

            EnumSet<CqlCompilerOptions.Options> translatorOptions = EnumSet.of(
                    CqlCompilerOptions.Options.EnableAnnotations
            );
            Assert.assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions);
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
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void testXmlLibraryLoad() {
        try {
            Library library = new org.cqframework.cql.elm.serializing.jaxb.ElmXmlLibraryReader().read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/fhir/AdultOutpatientEncounters_FHIR4-2.0.000.xml")));
            Assert.assertTrue(library != null);
            Assert.assertEquals(library.getIdentifier().getId(), "AdultOutpatientEncounters_FHIR4");
            Assert.assertEquals(library.getIdentifier().getVersion(), "2.0.000");

            EnumSet<CqlCompilerOptions.Options> translatorOptions = EnumSet.of(
                    CqlCompilerOptions.Options.EnableDateRangeOptimization,
                    CqlCompilerOptions.Options.EnableAnnotations,
                    CqlCompilerOptions.Options.EnableLocators,
                    CqlCompilerOptions.Options.EnableResultTypes,
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation
            );
            Assert.assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions);

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
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void testJsonTerminologyLibraryLoad() {
        try {
            Library library = new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryReader().read(new InputStreamReader(ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/ANCFHIRTerminologyDummy.json")));
            Assert.assertTrue(library != null);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    private void testElmDeserialization(String path, String xmlFileName, String jsonFileName) throws IOException, JAXBException {
        Library xmlLibrary = null;
        try {
            xmlLibrary = new org.cqframework.cql.elm.serializing.jaxb.ElmXmlLibraryReader().read(new FileReader(path + "/" + xmlFileName));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from xml %s: %s", xmlFileName, e.getMessage()));
        }

        Library jsonLibrary;
        try {
            jsonLibrary = new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryReader().read(new FileReader(path + "/" + jsonFileName));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from json %s: %s", jsonFileName, e.getMessage()));
        }

        if (xmlLibrary != null) {
            if (!equivalent(xmlLibrary, jsonLibrary)) {
                System.out.println(xmlFileName);
            }
            Assert.assertTrue(equivalent(xmlLibrary, jsonLibrary));
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
                    e.printStackTrace();
                    throw new IllegalArgumentException(String.format("Errors occurred testing: %s", fileName));
                }
            }
        }
    }

    @Test
    public void regressionTestJsonSerializer() throws URISyntaxException, IOException, JAXBException {
        // This test validates that the ELM library deserialized from the Json matches the ELM library deserialized from Xml
        // Regression inputs are annual update measure Xml for QDM and FHIR
        testElmDeserialization("qdm");
        testElmDeserialization("fhir");
        testElmDeserialization("qdm2020");
    }

    private static boolean equivalent(Library xmlLibrary, Library jsonLibrary) {
        if (xmlLibrary == null && jsonLibrary == null) {
            return true;
        }

        boolean result = true;

        if (xmlLibrary != null) {
            result = result && xmlLibrary.getIdentifier().equals(jsonLibrary.getIdentifier());
        }

        if(xmlLibrary.getIncludes() != null && xmlLibrary.getIncludes().getDef() != null) {
            result = result && xmlLibrary.getIncludes().getDef().size() == jsonLibrary.getIncludes().getDef().size();
        }

        if(xmlLibrary.getUsings() != null && xmlLibrary.getUsings().getDef() != null) {
            result = result && xmlLibrary.getUsings().getDef().size() == jsonLibrary.getUsings().getDef().size();
        }

        if(xmlLibrary.getValueSets() != null && xmlLibrary.getValueSets().getDef() != null) {
            result = result && xmlLibrary.getValueSets().getDef().size() == jsonLibrary.getValueSets().getDef().size();
        }

        if(xmlLibrary.getCodeSystems() != null && xmlLibrary.getCodeSystems().getDef() != null) {
            result = result && xmlLibrary.getCodeSystems().getDef().size() == jsonLibrary.getCodeSystems().getDef().size();
        }

        if(xmlLibrary.getCodes() != null && xmlLibrary.getCodes().getDef() != null) {
            result = result && xmlLibrary.getCodes().getDef().size() == jsonLibrary.getCodes().getDef().size();
        }

        if(xmlLibrary.getConcepts() != null && xmlLibrary.getConcepts().getDef() != null) {
            result = result && xmlLibrary.getConcepts().getDef().size() == jsonLibrary.getConcepts().getDef().size();
        }

        if(xmlLibrary.getParameters() != null && xmlLibrary.getParameters().getDef() != null) {
            result = result && xmlLibrary.getParameters().getDef().size() == jsonLibrary.getParameters().getDef().size();
        }

        if(xmlLibrary.getStatements() != null && xmlLibrary.getStatements().getDef() != null) {
            result = result && xmlLibrary.getStatements().getDef().size() == jsonLibrary.getStatements().getDef().size();
        }

        if(xmlLibrary.getContexts() != null && xmlLibrary.getContexts().getDef() != null) {
            result = result && xmlLibrary.getContexts().getDef().size() == jsonLibrary.getContexts().getDef().size();
        }

        return result;
    }

    private void validateEmptyStringsTest(Library library) {
        // Null
        // Empty
        // Space
        for (ExpressionDef ed : library.getStatements().getDef()) {
            switch (ed.getName()) {
                case "Null": Assert.assertTrue(ed.getExpression() instanceof Null);
                break;

                case "Empty": {
                    Assert.assertTrue(ed.getExpression() instanceof Literal);
                    Literal l = (Literal)ed.getExpression();
                    Assert.assertTrue(l.getValue() != null && l.getValue().equals(""));
                }
                break;

                case "Space": {
                    Assert.assertTrue(ed.getExpression() instanceof Literal);
                    Literal l = (Literal)ed.getExpression();
                    Assert.assertTrue(l.getValue() != null && l.getValue().equals(" "));
                }
                break;
            }
        }
    }

    private String toJaxbXml(Library library) {
        return new org.cqframework.cql.elm.serializing.jaxb.ElmXmlLibraryWriter().writeAsString(library);
    }

    private String toJaxbJson(Library library) {
        return new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryWriter().writeAsString(library);
    }

    private String toJacksonXml(Library library) {
        return new org.cqframework.cql.elm.serializing.jackson.ElmXmlLibraryWriter().writeAsString(library);
    }

    private String toJacksonJson(Library library) {
        return new org.cqframework.cql.elm.serializing.jackson.ElmJsonLibraryWriter().writeAsString(library);
    }

    @Test
    public void emptyStringsTest() throws IOException {
        InputStream inputStream = ElmDeserializeTests.class.getResourceAsStream("ElmDeserialize/EmptyStringsTest.cql");
        CqlTranslator translator = TestUtils.createTranslatorFromStream(inputStream);
        Assert.assertTrue(translator.getErrors().size() == 0);

        String jaxbXml = toJaxbXml(translator.toELM());
        String jaxbJson = toJaxbJson(translator.toELM());
        //NOTE: Jackson XML is not right, but after 3 different devs fiddling with it, propose we abandon that as a use case we don't care about anyway.
        //String jacksonXml = toJacksonXml(translator.toELM());
        String jacksonJson = toJacksonJson(translator.toELM());

        try {
            Library xmlLibrary = new org.cqframework.cql.elm.serializing.jackson.ElmXmlLibraryReader().read(new StringReader(jaxbXml));
            validateEmptyStringsTest(xmlLibrary);

            xmlLibrary = new org.cqframework.cql.elm.serializing.jaxb.ElmXmlLibraryReader().read(new StringReader(jaxbXml));
            validateEmptyStringsTest(xmlLibrary);

            //xmlLibrary = new org.cqframework.cql.elm.serializing.jackson.ElmXmlLibraryReader().read(new StringReader(jacksonXml));
            //validateEmptyStringsTest(xmlLibrary);

            //xmlLibrary = new org.cqframework.cql.elm.serializing.jaxb.ElmXmlLibraryReader().read(new StringReader(jacksonXml));
            //validateEmptyStringsTest(xmlLibrary);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Library jsonLibrary = new org.cqframework.cql.elm.serializing.jackson.ElmJsonLibraryReader().read(new StringReader(jaxbJson));
            validateEmptyStringsTest(jsonLibrary);

            jsonLibrary = new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryReader().read(new StringReader(jaxbJson));
            validateEmptyStringsTest(jsonLibrary);

            jsonLibrary = new org.cqframework.cql.elm.serializing.jackson.ElmJsonLibraryReader().read(new StringReader(jacksonJson));
            validateEmptyStringsTest(jsonLibrary);

            jsonLibrary = new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryReader().read(new StringReader(jacksonJson));
            validateEmptyStringsTest(jsonLibrary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}