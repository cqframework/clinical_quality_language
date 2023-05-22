package org.opencds.cqf.cql.engine.execution;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.cqframework.cql.elm.serializing.ElmLibraryWriterFactory;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;

public class TestLibraryLoader implements LibraryLoader {

    private CqlTranslatorOptions options = CqlTranslatorOptions.defaultOptions();

    public TestLibraryLoader(LibraryManager libraryManager, CqlTranslatorOptions options) {
        this(libraryManager);
        this.options = options;
    }

    public TestLibraryLoader(LibraryManager libraryManager) {
        if (libraryManager == null) {
            throw new IllegalArgumentException("libraryManager is null");
        }

        this.libraryManager = libraryManager;
    }

    private LibraryManager libraryManager;

    private Map<String, Library> libraries = new HashMap<>();

    private Library resolveLibrary(VersionedIdentifier libraryIdentifier) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("Library identifier is null.");
        }

        if (libraryIdentifier.getId() == null) {
            throw new IllegalArgumentException("Library identifier id is null.");
        }

        Library library = libraries.get(libraryIdentifier.getId());
        if (library != null && libraryIdentifier.getVersion() != null && !libraryIdentifier.getVersion().equals(library.getIdentifier().getVersion())) {
            throw new IllegalArgumentException(String.format("Could not load library %s, version %s because version %s is already loaded.",
                    libraryIdentifier.getId(), libraryIdentifier.getVersion(), library.getIdentifier().getVersion()));
        }
        else {
            library = loadLibrary(libraryIdentifier);
            libraries.put(libraryIdentifier.getId(), library);
        }

        return library;
    }

    private Library loadLibrary(VersionedIdentifier libraryIdentifier) {
        List<CqlCompilerException> errors = new ArrayList<>();
        org.hl7.elm.r1.VersionedIdentifier identifier = new org.hl7.elm.r1.VersionedIdentifier()
                .withId(libraryIdentifier.getId())
                .withSystem(libraryIdentifier.getSystem())
                .withVersion(libraryIdentifier.getVersion());

        CompiledLibrary compiledLibrary = libraryManager.resolveLibrary(identifier, this.options, errors);

        String json;
        try {
            StringWriter writer = new StringWriter();
            ElmLibraryWriterFactory.getWriter("application/elm+json").write(compiledLibrary.getLibrary(), writer);
            json = writer.getBuffer().toString();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Errors encountered while loading library %s: %s", libraryIdentifier.getId(), e.getMessage()));
        }

        Library library = null;
        try {
            library = new JsonCqlLibraryReader().read(json);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Errors encountered while loading library %s: %s", libraryIdentifier.getId(), e.getMessage()));
        }

        return library;
    }

    @Override
    public Library load(VersionedIdentifier libraryIdentifier) {
        return resolveLibrary(libraryIdentifier);
    }
}
