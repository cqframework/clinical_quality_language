package org.opencds.cqf.cql.engine.execution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;

// LUKETODO: immutability, immutable copies of collections, etc
class LoadMultiLibResult {
    private final LinkedHashMap<VersionedIdentifier, Library> results;
    private final LinkedHashMap<VersionedIdentifier, RuntimeException> exceptions;

    private LoadMultiLibResult(Builder builder) {
        this.results = builder.results;
        this.exceptions = builder.exceptions;
    }

    int libraryCount() {
        return results.size();
    }

    List<VersionedIdentifier> getAllLibraryIds() {
        return List.copyOf(results.keySet());
    }

    List<Library> getAllLibraries() {
        return List.copyOf(results.values());
    }

    VersionedIdentifier getLibraryIdentifierAtIndex(int index) {
        if (index < 0 || index >= getAllLibraries().size()) {
            throw new IllegalStateException("Index out of bounds: " + index);
        }
        return getAllLibraryIds().get(index);
    }

    Library retrieveLibrary(VersionedIdentifier libraryIdentifier) {
        if (libraryIdentifier.getVersion() != null) {
            if (!results.containsKey(libraryIdentifier)) {
                throw new IllegalArgumentException(
                        String.format("libraryIdentifier '%s' does not exist.", libraryIdentifier));
            }

            return results.get(libraryIdentifier);
        }

        return results.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(libraryIdentifier.getId()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("library id %s not found.", libraryIdentifier.getId())));
    }

    LinkedHashMap<VersionedIdentifier, RuntimeException> getExceptions() {
        return exceptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LinkedHashMap<VersionedIdentifier, Library> results = new LinkedHashMap<>();
        private final LinkedHashMap<VersionedIdentifier, String> errors = new LinkedHashMap<>();
        private final LinkedHashMap<VersionedIdentifier, RuntimeException> exceptions = new LinkedHashMap<>();

        void addResult(VersionedIdentifier libraryId, Library library) {
            this.results.put(libraryId, library);
        }

        void addException(VersionedIdentifier libraryId, RuntimeException exception) {
            // LUKETODO: mutable list?
            this.exceptions.put(libraryId, exception);
        }

        LoadMultiLibResult build() {
            return new LoadMultiLibResult(this);
        }
    }
}
