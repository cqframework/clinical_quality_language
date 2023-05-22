package org.opencds.cqf.cql.engine.serializing;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;
import org.opencds.cqf.cql.engine.serializing.jackson.XmlCqlLibraryReader;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class CqlCompileTranslateTest implements ITest {
    private final String path;
    private final String fileName;

    @Factory(dataProvider = "dataMethod")
    public CqlCompileTranslateTest(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    @DataProvider
    public static Object[][] dataMethod() throws URISyntaxException {
        List<String[]> filesToTest = new ArrayList<>();
        filesToTest.addAll(collectTestFiles());
        return filesToTest.toArray(new String[filesToTest.size()][]);
    }

    public static List<String[]> collectTestFiles() throws URISyntaxException {
        List<String[]> filesToTest = new ArrayList<>();
        URL dirURL = org.opencds.cqf.cql.engine.execution.TestLibrarySourceProvider.class.getResource(".");
        File file = new File(dirURL.toURI());
        for (String fileName : file.list()) {
            if (fileName.endsWith(".cql")) {
                filesToTest.add(new String[]{ file.getAbsolutePath(), fileName });
            }
        }
        return filesToTest;
    }

    public String getTestName() {
        return "test" + fileName.replaceAll(".cql","");
    }

    @Test
    private void testCompileTranscode() throws IOException, JAXBException, UcumException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new LibrarySourceProvider() {
            @Override
            public InputStream getLibrarySource(VersionedIdentifier versionedIdentifier) {
                String libraryFileName = String.format("%s%s.cql",
                    versionedIdentifier.getId(), versionedIdentifier.getVersion() != null ? ("-" + versionedIdentifier.getVersion()) : "");
                return org.opencds.cqf.cql.engine.execution.TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
            }
        });

        UcumService ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));

        File cqlFile = new File(path + "/" + fileName);

        CqlTranslator translator = CqlTranslator.fromFile(cqlFile, modelManager, libraryManager, ucumService);

        if (translator.getErrors().size() > 0) {
            System.err.println("Translation failed due to errors:");
            ArrayList<String> errors = new ArrayList<>();
            for (CqlCompilerException error : translator.getErrors()) {
                TrackBack tb = error.getLocator();
                String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
                    tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                System.err.printf("%s %s%n", lines, error.getMessage());
                errors.add(lines + error.getMessage());
            }
            throw new IllegalArgumentException(errors.toString());
        }

        assertThat(translator.getErrors().size(), is(0));

        Library jsonLibrary = null;
        try {
            jsonLibrary = new JsonCqlLibraryReader().read(translator.toJson());
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from json %s: %s", fileName, e.getMessage()));
        }

        Library xmlLibrary = null;

        try {
            xmlLibrary = new XmlCqlLibraryReader().read(translator.toXml());
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Errors occurred reading ELM from xml %s: %s", fileName, e.getMessage()));
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

