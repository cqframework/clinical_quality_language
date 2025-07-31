package org.opencds.cqf.cql.engine.execution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.exception.CqlException;

// LUKETODO: builder, immutability, immutable copies of collections, etc
// LUKETODO: record?
// LUKETODO: javadoc
public class EvaluationResultsForMultiLib {
    private final Map<VersionedIdentifier, EvaluationResult> results;
    private final Map<VersionedIdentifier, List<Exception>> exceptions;
    // LUKETODO:  single or multiple errors per library??
    private final Map<VersionedIdentifier, String> errors;

    private EvaluationResultsForMultiLib(Builder builder) {
        this.results = builder.results;
        this.exceptions = builder.exceptions;
        this.errors = builder.errors;
    }

    // Visible for testing
    // LUKETODO:  don't expose the maps directly, but rather provide methods to access the results
    Map<VersionedIdentifier, EvaluationResult> getResults() {
        return results;
    }

    // Visible for testing
    // LUKETODO:  don't expose the maps directly, but rather provide methods to access the results
    Map<VersionedIdentifier, String> getErrors() {
        return errors;
    }

    // Visible for testing
    // LUKETODO:  don't expose the maps directly, but rather provide methods to access the results
    Map<VersionedIdentifier, List<Exception>> getExceptions() {
        return exceptions;
    }

    public boolean containsResultsFor(VersionedIdentifier libraryIdentifier) {
        return results.containsKey(libraryIdentifier);
    }

    public boolean containsErrorsOrExceptionsFor(VersionedIdentifier libraryIdentifier) {
        return errors.containsKey(libraryIdentifier) || exceptions.containsKey(libraryIdentifier);
    }

    // LUKETODO:  do we keep this?
    public EvaluationResult getResultFor(VersionedIdentifier libraryIdentifier) {
        if (results.containsKey(libraryIdentifier)) {
            return results.get(libraryIdentifier);
        }

        if (libraryIdentifier.getVersion() == null
                || libraryIdentifier.getVersion().isEmpty()) {
            // If the version is not specified, try to match by ID only
            return results.entrySet().stream()
                    .filter(entry -> matchIdentifiers(libraryIdentifier, entry))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(null);
        }

        return null;
    }

    public EvaluationResult getSingleResultOrThrow() {
        if (results.size() > 1 || errors.size() > 1) {
            throw new IllegalStateException("Expected exactly one result or error, but found results: %s errors: %s: "
                    .formatted(results.size(), errors.size()));
        }

        if (!errors.isEmpty()) {
            throw new CqlException(this.errors.values().iterator().next());
        }

        return this.getFirstResult();
    }

    // LUKETODO:  test all scenarios here
    public EvaluationResult getOnlyResultOrThrow() {
        if (results.size() > 1 || errors.size() > 1 || exceptions.size() > 1) {
            throw new IllegalStateException("Expected exactly one result or error, but found results: %s errors: %s: "
                    .formatted(results.size(), errors.size()));
        }

        var firstError = getFirstError();
        var firstListOfExceptions = getFirstListOfExceptions();

        if (firstError != null && firstListOfExceptions.isEmpty()) {
            // LUKETODO: prepend the error message?
            throw new CqlException(firstError);
        }

        if (firstError == null && !firstListOfExceptions.isEmpty()) {
            if (firstListOfExceptions.size() == 1) {
                var firstException = firstListOfExceptions.get(0);
                throw new CqlException(firstException.getMessage(), firstException);
            }

            var exceptionMessages =
                    firstListOfExceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", "));

            throw new CqlException(exceptionMessages);
        }

        if (firstError != null) {
            var exceptionMessages =
                    firstListOfExceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", "));

            throw new CqlException(firstError + ", " + exceptionMessages);
        }

        return this.getFirstResult();
    }

    public String getErrorFor(VersionedIdentifier libraryIdentifier) {
        return errors.getOrDefault(libraryIdentifier, null);
    }

    public List<Exception> getExceptionsFor(VersionedIdentifier libraryIdentifier) {
        return exceptions.getOrDefault(libraryIdentifier, List.of());
    }

    private boolean matchIdentifiers(
            VersionedIdentifier libraryIdentifier, Map.Entry<VersionedIdentifier, EvaluationResult> entry) {
        return entry.getKey().getId().equals(libraryIdentifier.getId());
    }

    // LUKETODO:  validate this isn't empty or Optional or something
    private EvaluationResult getFirstResult() {
        return results.entrySet().iterator().next().getValue();
    }

    private String getFirstError() {
        if (errors.isEmpty()) {
            return null;
        }
        return errors.values().iterator().next();
    }

    private List<Exception> getFirstListOfExceptions() {
        if (exceptions.isEmpty()) {
            return List.of();
        }
        return exceptions.values().iterator().next();
    }

    static Builder builder(LoadMultiLibResult loadMultiLibResult) {
        return new Builder(loadMultiLibResult);
    }

    static class Builder {
        private final LinkedHashMap<VersionedIdentifier, EvaluationResult> results = new LinkedHashMap<>();
        private final LinkedHashMap<VersionedIdentifier, List<Exception>> exceptions = new LinkedHashMap<>();
        private final LinkedHashMap<VersionedIdentifier, String> errors = new LinkedHashMap<>();

        Builder(LoadMultiLibResult loadMultiLibResult) {
            exceptions.putAll(loadMultiLibResult.getExceptions());
            errors.putAll(loadMultiLibResult.getErrors());
        }

        Builder addResult(VersionedIdentifier libraryId, EvaluationResult evaluationResult) {
            results.put(withIdOnly(libraryId), evaluationResult);
            return this;
        }

        Builder addException(VersionedIdentifier libraryId, Exception exception) {
            exceptions
                    .computeIfAbsent(withIdOnly(libraryId), k -> new java.util.ArrayList<>())
                    .add(exception);
            return this;
        }

        EvaluationResultsForMultiLib build() {
            return new EvaluationResultsForMultiLib(this);
        }

        private VersionedIdentifier withIdOnly(VersionedIdentifier libraryId) {
            return new VersionedIdentifier().withId(libraryId.getId());
        }
    }
}
