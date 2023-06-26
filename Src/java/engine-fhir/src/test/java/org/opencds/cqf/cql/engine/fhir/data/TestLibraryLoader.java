package org.opencds.cqf.cql.engine.fhir.data;

import java.util.Map;

import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.execution.LibraryLoader;

public class TestLibraryLoader implements LibraryLoader {
    private Map<String, Library> libraries;

    public TestLibraryLoader(Map<String, Library> libraries) {
        this.libraries = libraries;
    }

    @Override
    public Library load(VersionedIdentifier libraryIdentifier) {
        Library result = libraries.get(libraryIdentifier.getId());
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not load ELM for library %s", libraryIdentifier.getId()));
        }

        return result;
    }
}
