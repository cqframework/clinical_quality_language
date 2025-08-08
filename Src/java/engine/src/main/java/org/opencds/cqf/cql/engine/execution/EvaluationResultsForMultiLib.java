package org.opencds.cqf.cql.engine.execution;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hl7.elm.r1.VersionedIdentifier;

/**
 * Track evaluation results and exceptions for multiple libraries in a single evaluation, to support partial
 * successes and partial failures across libraries.
 */
public class EvaluationResultsForMultiLib {
    private final Map<VersionedIdentifier, EvaluationResult> results;
    private final Map<VersionedIdentifier, RuntimeException> exceptions;

    private EvaluationResultsForMultiLib(Builder builder) {
        this.results = Collections.unmodifiableMap(builder.results);
        this.exceptions = Collections.unmodifiableMap(builder.exceptions);
    }

    // Visible for testing
    Map<VersionedIdentifier, EvaluationResult> getResults() {
        return results;
    }

    // Visible for testing
    Map<VersionedIdentifier, RuntimeException> getExceptions() {
        return exceptions;
    }

    public boolean containsResultsFor(VersionedIdentifier libraryIdentifier) {
        return getResultFor(libraryIdentifier) != null;
    }

    public boolean containsExceptionsFor(VersionedIdentifier libraryIdentifier) {
        return exceptions.containsKey(libraryIdentifier);
    }

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

    public EvaluationResult getOnlyResultOrThrow() {
        if (results.size() > 1 || exceptions.size() > 1) {
            throw new IllegalStateException(
                    "Did you run an evaluation for multiple libraries?  Expected exactly one result or error, but found results: %s errors: %s: "
                            .formatted(results.size(), exceptions.size()));
        }

        var firstException = getFirstException();

        if (firstException != null) {
            throw firstException;
        }

        return this.getFirstResult();
    }

    public RuntimeException getExceptionFor(VersionedIdentifier libraryIdentifier) {
        return exceptions.getOrDefault(libraryIdentifier, null);
    }

    private boolean matchIdentifiers(
            VersionedIdentifier libraryIdentifier, Map.Entry<VersionedIdentifier, EvaluationResult> entry) {
        return entry.getKey().getId().equals(libraryIdentifier.getId());
    }

    private EvaluationResult getFirstResult() {
        var allEvaluationResults = results.values();

        if (allEvaluationResults.size() > 1) {
            throw new IllegalStateException(
                    "Did you run an evaluation for multiple libraries?  Expected 0-1 results, but found: %s"
                            .formatted(results.size()));
        }

        return allEvaluationResults.stream().findFirst().orElse(null);
    }

    private RuntimeException getFirstException() {
        if (exceptions.isEmpty()) {
            return null;
        }

        return exceptions.values().iterator().next();
    }

    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }

    static Builder builder(LoadMultiLibResult loadMultiLibResult) {
        return new Builder(loadMultiLibResult);
    }

    static class Builder {
        private final LinkedHashMap<VersionedIdentifier, EvaluationResult> results = new LinkedHashMap<>();
        private final LinkedHashMap<VersionedIdentifier, RuntimeException> exceptions = new LinkedHashMap<>();

        Builder(LoadMultiLibResult loadMultiLibResult) {
            exceptions.putAll(loadMultiLibResult.getExceptions());
        }

        void addResult(VersionedIdentifier libraryId, EvaluationResult evaluationResult) {
            results.put(libraryId, evaluationResult);
        }

        void addException(VersionedIdentifier libraryId, RuntimeException exception) {
            exceptions.put(withIdOnly(libraryId), exception);
        }

        EvaluationResultsForMultiLib build() {
            return new EvaluationResultsForMultiLib(this);
        }

        private VersionedIdentifier withIdOnly(VersionedIdentifier libraryId) {
            return new VersionedIdentifier().withId(libraryId.getId());
        }
    }
}
