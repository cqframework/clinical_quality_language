package org.opencds.cqf.cql.engine.execution;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.serializing.ElmLibraryWriterFactory;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;

public class TranslatingTestBase {

    public LibraryManager toLibraryManager(Map<org.hl7.elm.r1.VersionedIdentifier, String> libraryText) {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new InMemoryLibrarySourceProvider(libraryText));
        return libraryManager;
    }

    public Library toLibrary(String text) throws IOException  {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);

        return this.toLibrary(text, modelManager, libraryManager);
    }

    public Library toLibrary(String text, ModelManager modelManager, LibraryManager libraryManager) throws IOException {
        CqlTranslator translator = CqlTranslator.fromText(text, modelManager, libraryManager);
        return this.readJson(translator.toJson());
    }

    public Library readJson(String json) throws IOException {
        return new JsonCqlLibraryReader().read(new StringReader(json));
    }

    public org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

    public String convertToJson(org.hl7.elm.r1.Library library) throws IOException {
        StringWriter writer = new StringWriter();
        ElmLibraryWriterFactory.getWriter("application/elm+json").write(library, writer);
        return writer.getBuffer().toString();
    }
}
