package org.cqframework.cql.cql2elm;

import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.elm.r1.IncludeDef;
import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

public class NamespaceTests {
    ModelManager modelManager;
    LibraryManager libraryManager;
    NamespaceInfo defaultNamespaceInfo;
    NamespaceInfo coreNamespaceInfo;
    NamespaceInfo sharedNamespaceInfo;
    NamespaceInfo contentNamespaceInfo;

    public class NamespaceTestsLibrarySourceProvider implements LibrarySourceProvider {
        @Override
        public LibraryContentMeta getLibrarySource(VersionedIdentifier libraryIdentifier) {
            String namespacePath = "NamespaceTests/";
            if (libraryIdentifier.getSystem() != null) {
                NamespaceInfo namespaceInfo = libraryManager.getNamespaceManager().getNamespaceInfoFromUri(libraryIdentifier.getSystem());
                if (namespaceInfo != null && !namespaceInfo.getName().equals("Public")) {
                    namespacePath += namespaceInfo.getName() + "/";

                }
            }
            String libraryFileName = String.format("%s%s%s.cql",
                    namespacePath,
                    libraryIdentifier.getId(),
                    libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
            return new LibraryContentMeta(LibraryContentType.CQL).withSource(
                    org.cqframework.cql.cql2elm.NamespaceTests.class.getResourceAsStream(libraryFileName));
        }
    }

    @BeforeClass
    public void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new NamespaceTestsLibrarySourceProvider());
        defaultNamespaceInfo = new NamespaceInfo("Public", "http://cql.hl7.org/public");
        coreNamespaceInfo = new NamespaceInfo("Core", "http://cql.hl7.org/core");
        sharedNamespaceInfo = new NamespaceInfo("Shared", "http://cql.hl7.org/shared");
        contentNamespaceInfo = new NamespaceInfo("Content", "http://cql.hl7.org/content");
        libraryManager.getNamespaceManager().addNamespace(defaultNamespaceInfo);
        libraryManager.getNamespaceManager().addNamespace(coreNamespaceInfo);
        libraryManager.getNamespaceManager().addNamespace(sharedNamespaceInfo);
        libraryManager.getNamespaceManager().addNamespace(contentNamespaceInfo);
    }

    /* Unit test namespace functionality */

    @Test
    public void testNamespacePath() {
        assertThat(NamespaceManager.getPath(defaultNamespaceInfo.getUri(), "Main"), is("http://cql.hl7.org/public/Main"));
    }

    @Test
    public void testNamespaceNamePart() {
        assertThat(NamespaceManager.getNamePart("http://cql.hl7.org/public/Main"), is("Main"));
    }

    @Test
    public void testNamespaceUriPart() {
        assertThat(NamespaceManager.getUriPart("http://cql.hl7.org/public/Main"), is("http://cql.hl7.org/public"));
    }

    /* Ensure base functionality with a defaulted namespace uri */

    @Test
    public void testLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(defaultNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/ReferencingLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(defaultNamespaceInfo.getUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(defaultNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/InvalidReferencingLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidLibraryReference() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(defaultNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/InvalidLibraryReference.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidBaseLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(defaultNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/ReferencingInvalidBaseLibrary.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(1));
            assertThat(translator.getErrors().get(0), instanceOf(CqlTranslatorException.class));
            assertThat(translator.getErrors().get(0).getLocator(), notNullValue());
            assertThat(translator.getErrors().get(0).getLocator().getLibrary(), notNullValue());
            assertThat(translator.getErrors().get(0).getLocator().getLibrary().getSystem(), is(defaultNamespaceInfo.getUri()));
            assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), is("InvalidBaseLibrary"));

            assertThat(translator.toELM(), notNullValue());
            assertThat(translator.toELM().getAnnotation(), notNullValue());
            assertThat(translator.toELM().getAnnotation().size(), greaterThan(0));
            CqlToElmError invalidBaseLibraryError = null;
            for (Object o : translator.toELM().getAnnotation()) {
                if (o instanceof CqlToElmError) {
                    invalidBaseLibraryError = (CqlToElmError)o;
                    break;
                }
            }
            assertThat(invalidBaseLibraryError, notNullValue());
            assertThat(invalidBaseLibraryError.getLibrarySystem(), is(defaultNamespaceInfo.getUri()));
            assertThat(invalidBaseLibraryError.getLibraryId(), is("InvalidBaseLibrary"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(defaultNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/Main.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("Main"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(defaultNamespaceInfo.getUri()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReferencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(defaultNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/ReferencingMain.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("ReferencingMain"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(defaultNamespaceInfo.getUri()));
            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/public/Main"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCoreMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(coreNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/Core/Main.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("Main"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(coreNamespaceInfo.getUri()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCoreReferencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(coreNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/Core/ReferencingMain.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("ReferencingMain"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(coreNamespaceInfo.getUri()));
            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/core/Main"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSharedMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(sharedNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/Shared/Main.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("Main"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(sharedNamespaceInfo.getUri()));

            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/core/Main"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSharedReferencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(sharedNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/Shared/ReferencingMain.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("ReferencingMain"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(sharedNamespaceInfo.getUri()));
            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/shared/Main"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testContentMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(contentNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/Content/Main.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("Main"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(contentNamespaceInfo.getUri()));

            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(2));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("CoreMain"));
            assertThat(i.getPath(), is("http://cql.hl7.org/core/Main"));

            i = translator.toELM().getIncludes().getDef().get(1);
            assertThat(i.getLocalIdentifier(), is("SharedMain"));
            assertThat(i.getPath(), is("http://cql.hl7.org/shared/Main"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testContentReferencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(contentNamespaceInfo, NamespaceTests.class.getResourceAsStream("NamespaceTests/Content/ReferencingMain.cql"), modelManager, libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("ReferencingMain"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(contentNamespaceInfo.getUri()));
            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(3));

            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/shared/Main"));

            i = translator.toELM().getIncludes().getDef().get(1);
            assertThat(i.getLocalIdentifier(), is("ReferencingMain"));
            assertThat(i.getPath(), is("http://cql.hl7.org/core/ReferencingMain"));

            i = translator.toELM().getIncludes().getDef().get(2);
            assertThat(i.getLocalIdentifier(), is("SharedReferencingMain"));
            assertThat(i.getPath(), is("http://cql.hl7.org/shared/ReferencingMain"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
