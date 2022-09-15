package org.cqframework.cql.elm;

import java.io.IOException;
import java.io.InputStream;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.fhir.ucum.UcumEssenceService;
import org.hl7.cql.model.NamespaceInfo;

public class TestUtils {

    private static UcumEssenceService getUcumEssenceService() {
        try {
            return new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
        }
        catch(Exception e) {
            return null;
        }
    }

    public static CqlTranslator createTranslatorFromStream(String testFileName, CqlTranslatorOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, testFileName, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, String testFileName, CqlTranslatorOptions.Options... options) throws IOException {
        InputStream inputStream = TestUtils.class.getResourceAsStream(testFileName);
        return createTranslatorFromStream(null, inputStream, options);
    }

    public static CqlTranslator createTranslatorFromStream(InputStream inputStream, CqlTranslatorOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, inputStream, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, InputStream inputStream, CqlTranslatorOptions.Options... options) throws IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        CqlTranslator translator = CqlTranslator.fromStream(namespaceInfo, inputStream, modelManager, libraryManager, getUcumEssenceService(), options);
        return translator;
    }
}