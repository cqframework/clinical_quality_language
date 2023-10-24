package org.cqframework.cql.elm;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBException;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CompilerOptions;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ElmDeserializeTests {

    @Test
    public void testElmTests() {
        try {
            deserializeXmlLibrary("ElmDeserialize/ElmTests.xml");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }


    @Test
    public void testJsonANCFHIRDummyLibraryLoad() {
        try {
            final Library library = deserializeJsonLibrary("ElmDeserialize/ANCFHIRDummy.json");
            assertNotNull(library);

            EnumSet<CqlCompilerOptions.Options> translatorOptions = EnumSet.of(
                    CqlCompilerOptions.Options.EnableDateRangeOptimization,
                    CqlCompilerOptions.Options.EnableAnnotations,
                    CqlCompilerOptions.Options.EnableLocators,
                    CqlCompilerOptions.Options.EnableResultTypes,
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation
            );

            assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions);

            assertNotNull(library.getStatements());
            assertNotNull(library.getStatements().getDef());
            assertTrue(library.getStatements().getDef().size() >= 2);
            assertNotNull(library.getStatements().getDef().get(0));
            assertTrue(library.getStatements().getDef().get(0).getExpression() instanceof SingletonFrom);
            assertTrue(((SingletonFrom)library.getStatements().getDef().get(0).getExpression()).getOperand() instanceof Retrieve);
            assertNotNull(library.getStatements().getDef().get(1));
            assertTrue(library.getStatements().getDef().get(1).getExpression() instanceof Retrieve);

            verifySigLevels(library, LibraryBuilder.SignatureLevel.All);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void testJsonAdultOutpatientEncounters_FHIR4LibraryLoad() {
        try {
            final Library library = deserializeJsonLibrary("ElmDeserialize/fhir/AdultOutpatientEncounters_FHIR4-2.0.000.json");
            assertNotNull(library);

            EnumSet<CqlCompilerOptions.Options> translatorOptions = EnumSet.of(
                    CqlCompilerOptions.Options.EnableAnnotations
            );
            assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions);
            assertEquals(library.getIdentifier().getId(), "AdultOutpatientEncounters_FHIR4");
            assertEquals(library.getIdentifier().getVersion(), "2.0.000");
            assertNotNull(library.getUsings());
            assertNotNull(library.getUsings().getDef());
            assertTrue(library.getUsings().getDef().size() >= 2);
            assertNotNull(library.getStatements());
            assertNotNull(library.getStatements().getDef());
            assertNotNull(library.getStatements().getDef().get(0));
            assertTrue(library.getStatements().getDef().get(0).getExpression() instanceof SingletonFrom);
            assertTrue(((SingletonFrom)library.getStatements().getDef().get(0).getExpression()).getOperand() instanceof Retrieve);
            assertEquals(library.getStatements().getDef().get(1).getName(), "Qualifying Encounters");
            assertNotNull(library.getStatements().getDef().get(1));
            assertTrue(library.getStatements().getDef().get(1).getExpression() instanceof Query);

            verifySigLevels(library, LibraryBuilder.SignatureLevel.Differing);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void testXmlLibraryLoad() {
        try {
            final Library library = deserializeXmlLibrary("ElmDeserialize/fhir/AdultOutpatientEncounters_FHIR4-2.0.000.xml");
            assertNotNull(library);
            assertEquals(library.getIdentifier().getId(), "AdultOutpatientEncounters_FHIR4");
            assertEquals(library.getIdentifier().getVersion(), "2.0.000");

            EnumSet<CqlCompilerOptions.Options> translatorOptions = EnumSet.of(
                    CqlCompilerOptions.Options.EnableDateRangeOptimization,
                    CqlCompilerOptions.Options.EnableAnnotations,
                    CqlCompilerOptions.Options.EnableLocators,
                    CqlCompilerOptions.Options.EnableResultTypes,
                    CqlCompilerOptions.Options.DisableListDemotion,
                    CqlCompilerOptions.Options.DisableListPromotion,
                    CqlCompilerOptions.Options.DisableMethodInvocation
            );
            assertEquals(CompilerOptions.getCompilerOptions(library), translatorOptions);

            assertNotNull(library.getUsings());
            assertNotNull(library.getUsings().getDef());
            assertTrue(library.getUsings().getDef().size() >= 2);
            assertNotNull(library.getStatements());
            assertNotNull(library.getStatements().getDef());
            assertNotNull(library.getStatements().getDef().get(0));
            assertTrue(library.getStatements().getDef().get(0).getExpression() instanceof SingletonFrom);
            assertTrue(((SingletonFrom)library.getStatements().getDef().get(0).getExpression()).getOperand() instanceof Retrieve);
            assertEquals(library.getStatements().getDef().get(1).getName(), "Qualifying Encounters");
            assertNotNull(library.getStatements().getDef().get(1));
            assertTrue(library.getStatements().getDef().get(1).getExpression() instanceof Query);

            verifySigLevels(library, LibraryBuilder.SignatureLevel.Overloads);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void testJsonTerminologyLibraryLoad() {
        try {
            final Library library = deserializeJsonLibrary("ElmDeserialize/ANCFHIRTerminologyDummy.json");
            assertNotNull(library);

            verifySigLevels(library, LibraryBuilder.SignatureLevel.None);
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
            assertTrue(equivalent(xmlLibrary, jsonLibrary));
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
                case "Null": assertTrue(ed.getExpression() instanceof Null);
                break;

                case "Empty": {
                    assertTrue(ed.getExpression() instanceof Literal);
                    Literal l = (Literal)ed.getExpression();
                    assertTrue(l.getValue() != null && l.getValue().equals(""));
                }
                break;

                case "Space": {
                    assertTrue(ed.getExpression() instanceof Literal);
                    Literal l = (Literal)ed.getExpression();
                    assertTrue(l.getValue() != null && l.getValue().equals(" "));
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
        assertEquals(translator.getErrors().size(), 0);

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

    private static Library deserializeJsonLibrary(String filePath) throws IOException {
        final InputStream resourceAsStream = ElmDeserializeTests.class.getResourceAsStream(filePath);
        assertNotNull(resourceAsStream);
        return new org.cqframework.cql.elm.serializing.jaxb.ElmJsonLibraryReader().read(new InputStreamReader(resourceAsStream));
    }

    private static Library deserializeXmlLibrary(String filePath) throws IOException {
        final InputStream resourceAsStream = ElmDeserializeTests.class.getResourceAsStream(filePath);
        assertNotNull(resourceAsStream);
        return new org.cqframework.cql.elm.serializing.jaxb.ElmXmlLibraryReader().read(resourceAsStream);
    }

    private static void verifySigLevels(Library theLibrary, LibraryBuilder.SignatureLevel expectedSignatureLevel) {
        final List<String> sigLevels = theLibrary.getAnnotation().stream()
                .filter(CqlToElmInfo.class::isInstance)
                .map(CqlToElmInfo.class::cast)
                .map(CqlToElmInfo::getSignatureLevel)
                .collect(Collectors.toList());

        assertEquals(sigLevels.size(), 1);
        assertEquals(sigLevels.get(0), expectedSignatureLevel.name());
    }
}