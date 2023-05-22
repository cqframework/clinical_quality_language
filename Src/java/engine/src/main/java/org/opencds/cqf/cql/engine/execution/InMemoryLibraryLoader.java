package org.opencds.cqf.cql.engine.execution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;

public class InMemoryLibraryLoader implements LibraryLoader {

    private Map<String, Library> libraries = new HashMap<>();

    public InMemoryLibraryLoader(Collection<Library> libraries) {

        for (Library library : libraries) {
            String id = library.getIdentifier().getId();
            if (this.libraries.containsKey(id)) {
                throw new IllegalArgumentException(String.format("Found multiple versions / instances of library %s.", id));
            }

            this.libraries.put(library.getIdentifier().getId(), library);
        }
    }

    public Library load(VersionedIdentifier libraryIdentifier) {
        Library library = this.libraries.get(libraryIdentifier.getId());
        if (library == null) {
            throw new IllegalArgumentException(String.format("Library %s not found.", libraryIdentifier.getId()));
        }

        return library;
    }
}