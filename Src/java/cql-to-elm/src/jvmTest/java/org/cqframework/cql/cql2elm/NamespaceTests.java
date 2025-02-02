package org.cqframework.cql.cql2elm;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import kotlinx.io.Source;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.elm.r1.IncludeDef;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NamespaceTests {
    private static ModelManager modelManager;
    private static LibraryManager libraryManager;
    private static NamespaceInfo defaultNamespaceInfo;
    private static NamespaceInfo coreNamespaceInfo;
    private static NamespaceInfo sharedNamespaceInfo;
    private static NamespaceInfo contentNamespaceInfo;

    static class NamespaceTestsLibrarySourceProvider implements LibrarySourceProvider {
        @Override
        public Source getLibrarySource(VersionedIdentifier libraryIdentifier) {
            String namespacePath = "NamespaceTests/";
            if (libraryIdentifier.getSystem() != null) {
                NamespaceInfo namespaceInfo =
                        libraryManager.getNamespaceManager().getNamespaceInfoFromUri(libraryIdentifier.getSystem());
                if (namespaceInfo != null && !namespaceInfo.getName().equals("Public")) {
                    namespacePath += namespaceInfo.getName() + "/";
                }
            }
            String libraryFileName = String.format(
                    "%s%s%s.cql",
                    namespacePath,
                    libraryIdentifier.getId(),
                    libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
            var inputStream = org.cqframework.cql.cql2elm.NamespaceTests.class.getResourceAsStream(libraryFileName);
            if (inputStream == null) {
                return null;
            }
            return buffered(asSource(inputStream));
        }

        @Override
        public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
            if (LibraryContentType.CQL == type) {
                return getLibrarySource(libraryIdentifier);
            }

            return null;
        }
    }

    @BeforeAll
    static void setup() {
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
    void namespacePath() {
        assertThat(
                NamespaceManager.Companion.getPath(defaultNamespaceInfo.getUri(), "Main"),
                is("http://cql.hl7.org/public/Main"));
    }

    @Test
    void namespaceNamePart() {
        assertThat(NamespaceManager.Companion.getNamePart("http://cql.hl7.org/public/Main"), is("Main"));
    }

    @Test
    void namespaceUriPart() {
        assertThat(
                NamespaceManager.Companion.getUriPart("http://cql.hl7.org/public/Main"),
                is("http://cql.hl7.org/public"));
    }

    /* Ensure base functionality with a defaulted namespace uri */

    @Test
    void libraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    defaultNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/ReferencingLibrary.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(defaultNamespaceInfo.getUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void invalidLibraryReferences() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    defaultNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/InvalidReferencingLibrary.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void invalidLibraryReference() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    defaultNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/InvalidLibraryReference.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(not(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void invalidBaseLibrary() {
        CqlTranslator translator = null;
        try {
            translator = CqlTranslator.fromStream(
                    defaultNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/ReferencingInvalidBaseLibrary.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(1));
            assertThat(translator.getErrors().get(0), instanceOf(CqlCompilerException.class));
            assertThat(translator.getErrors().get(0).getLocator(), notNullValue());
            assertThat(translator.getErrors().get(0).getLocator().getLibrary(), notNullValue());
            assertThat(
                    translator.getErrors().get(0).getLocator().getLibrary().getSystem(),
                    is(defaultNamespaceInfo.getUri()));
            assertThat(translator.getErrors().get(0).getLocator().getLibrary().getId(), is("InvalidBaseLibrary"));

            assertThat(translator.toELM(), notNullValue());
            assertThat(translator.toELM().getAnnotation(), notNullValue());
            assertThat(translator.toELM().getAnnotation().size(), greaterThan(0));
            CqlToElmError invalidBaseLibraryError = null;
            for (Object o : translator.toELM().getAnnotation()) {
                if (o instanceof CqlToElmError) {
                    invalidBaseLibraryError = (CqlToElmError) o;
                    break;
                }
            }
            assertThat(invalidBaseLibraryError, notNullValue());
            assertThat(invalidBaseLibraryError.getLibrarySystem(), is(defaultNamespaceInfo.getUri()));
            assertThat(invalidBaseLibraryError.getLibraryId(), is("InvalidBaseLibrary"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void main() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    defaultNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/Main.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("Main"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(defaultNamespaceInfo.getUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void referencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    defaultNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/ReferencingMain.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("ReferencingMain"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(defaultNamespaceInfo.getUri()));
            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/public/Main"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void coreMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    coreNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/Core/Main.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("Main"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(coreNamespaceInfo.getUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void coreReferencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    coreNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/Core/ReferencingMain.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("ReferencingMain"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(coreNamespaceInfo.getUri()));
            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/core/Main"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sharedMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    sharedNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/Shared/Main.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("Main"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(sharedNamespaceInfo.getUri()));

            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/core/Main"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sharedReferencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    sharedNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/Shared/ReferencingMain.cql"),
                    libraryManager);
            assertThat(translator.getErrors().size(), is(0));
            assertThat(translator.toELM().getIdentifier().getId(), is("ReferencingMain"));
            assertThat(translator.toELM().getIdentifier().getSystem(), is(sharedNamespaceInfo.getUri()));
            assertThat(translator.toELM().getIncludes(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef(), notNullValue());
            assertThat(translator.toELM().getIncludes().getDef().size(), greaterThanOrEqualTo(1));
            IncludeDef i = translator.toELM().getIncludes().getDef().get(0);
            assertThat(i.getLocalIdentifier(), is("Main"));
            assertThat(i.getPath(), is("http://cql.hl7.org/shared/Main"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void contentMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    contentNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/Content/Main.cql"),
                    libraryManager);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void contentReferencingMain() {
        try {
            CqlTranslator translator = CqlTranslator.fromStream(
                    contentNamespaceInfo,
                    NamespaceTests.class.getResourceAsStream("NamespaceTests/Content/ReferencingMain.cql"),
                    libraryManager);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
