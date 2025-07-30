package org.opencds.cqf.cql.engine.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;

// LUKETODO: builder, immutability, immutable copies of collections, etc
// LUKETODO: record?
class LoadMultiLibResult {
    private final LinkedHashMap<VersionedIdentifier, Library> results;
    private final LinkedHashMap<VersionedIdentifier, List<Exception>> exceptions;
    private final LinkedHashMap<VersionedIdentifier, String> errors;

    public static LoadMultiLibResult resultsAndErrors(
            LinkedHashMap<VersionedIdentifier, Library> results,
            LinkedHashMap<VersionedIdentifier, List<Exception>> exceptions) {
        return new LoadMultiLibResult(results, exceptions, new LinkedHashMap<>());
    }

    public static LoadMultiLibResult errorsOnly(LinkedHashMap<VersionedIdentifier, String> errors) {
        return new LoadMultiLibResult(new LinkedHashMap<>(), new LinkedHashMap<>(), errors);
    }

    private LoadMultiLibResult(
            LinkedHashMap<VersionedIdentifier, Library> results,
            LinkedHashMap<VersionedIdentifier, List<Exception>> exceptions,
            LinkedHashMap<VersionedIdentifier, String> errors) {
        this.results = results;
        this.exceptions = exceptions;
        this.errors = errors;
    }

    private LoadMultiLibResult(Builder builder) {
        this.results = builder.results;
        this.exceptions = builder.exceptions;
        this.errors = builder.errors;
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

    // LUKETODO:  encapsulate the maps, don't expose them directly
    LinkedHashMap<VersionedIdentifier, Library> getResults() {
        return results;
    }

    public LinkedHashMap<VersionedIdentifier, List<Exception>> getExceptions() {
        return exceptions;
    }

    LinkedHashMap<VersionedIdentifier, String> getErrors() {
        return errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LinkedHashMap<VersionedIdentifier, Library> results = new LinkedHashMap<>();
        private LinkedHashMap<VersionedIdentifier, String> errors = new LinkedHashMap<>();
        private LinkedHashMap<VersionedIdentifier, List<Exception>> exceptions = new LinkedHashMap<>();

        Builder addResult(VersionedIdentifier libraryId, Library library) {
            this.results.put(libraryId, library);
            return this;
        }

        Builder addException(VersionedIdentifier libraryId, Exception exception) {
            // LUKETODO: mutable list?
            this.exceptions.computeIfAbsent(libraryId, k -> new ArrayList<>()).add(exception);
            return this;
        }

        Builder addExceptions(VersionedIdentifier libraryId, Collection<? extends Exception> exceptions) {
            this.exceptions.put(libraryId, List.copyOf(exceptions));
            return this;
        }

        LoadMultiLibResult build() {
            return new LoadMultiLibResult(this);
        }
    }
}
