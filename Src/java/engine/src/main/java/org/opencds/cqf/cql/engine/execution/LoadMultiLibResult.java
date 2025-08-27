package org.opencds.cqf.cql.engine.execution;

import static java.util.function.Predicate.not;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.exception.CqlException;

/**
 * Track results and exceptions for multiple libraries in a single load operation, to support partial
 * successes and partial failures across libraries.
 */
class LoadMultiLibResult {
    private final Map<VersionedIdentifier, Library> results;
    private final Map<VersionedIdentifier, RuntimeException> exceptions;
    private final Map<VersionedIdentifier, RuntimeException> warnings;

    private LoadMultiLibResult(Builder builder) {
        this.results = Collections.unmodifiableMap(builder.results);
        this.exceptions = Collections.unmodifiableMap(builder.exceptions);
        this.warnings = Collections.unmodifiableMap(builder.warnings);
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

    Map<VersionedIdentifier, RuntimeException> getExceptions() {
        return exceptions;
    }

    Map<VersionedIdentifier, RuntimeException> getWarnings() {
        return warnings;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LinkedHashMap<VersionedIdentifier, Library> results = new LinkedHashMap<>();
        private final LinkedHashMap<VersionedIdentifier, RuntimeException> exceptions = new LinkedHashMap<>();
        private final LinkedHashMap<VersionedIdentifier, RuntimeException> warnings = new LinkedHashMap<>();

        void addResult(VersionedIdentifier libraryId, Library library) {
            this.results.put(libraryId, library);
        }

        void addExceptionOrWarning(VersionedIdentifier libraryId, RuntimeException exception) {
            addExceptionsOrWarnings(libraryId, List.of(exception));
        }

        void addExceptionsOrWarnings(VersionedIdentifier libraryId, List<? extends RuntimeException> exceptions) {
            if (exceptions == null || exceptions.isEmpty()) {
                return;
            }

            exceptions.stream()
                    .filter(not(CqlCompilerException.class::isInstance))
                    .forEach(nonCompilerException -> this.exceptions.put(libraryId, nonCompilerException));

            var exceptionsBySeverity = exceptions.stream()
                    .filter(CqlCompilerException.class::isInstance)
                    .map(CqlCompilerException.class::cast)
                    .collect(Collectors.groupingBy(CqlCompilerException::getSeverity));

            for (CqlCompilerException.ErrorSeverity errorSeverity : exceptionsBySeverity.keySet()) {

                var wrappedExceptions = wrapExceptions(libraryId, exceptionsBySeverity.get(errorSeverity));

                if (errorSeverity == CqlCompilerException.ErrorSeverity.Error) {
                    this.exceptions.put(libraryId, wrappedExceptions);
                } else {
                    this.warnings.put(libraryId, wrappedExceptions);
                }
            }
        }

        LoadMultiLibResult build() {
            return new LoadMultiLibResult(this);
        }

        private static CqlException wrapExceptions(
                VersionedIdentifier libraryIdentifier, List<CqlCompilerException> exceptions) {
            return new CqlException("Library %s loaded, but had errors: %s"
                    .formatted(
                            libraryIdentifier.getId()
                                    + (libraryIdentifier.getVersion() != null
                                            ? "-" + libraryIdentifier.getVersion()
                                            : ""),
                            exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", "))));
        }
    }
}
