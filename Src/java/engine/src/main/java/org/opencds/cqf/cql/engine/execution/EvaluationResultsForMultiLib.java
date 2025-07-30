package org.opencds.cqf.cql.engine.execution;

import java.util.List;
import java.util.Map;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.exception.CqlException;

// LUKETODO: builder, immutability, immutable copies of collections, etc
// LUKETODO: record?
// LUKETODO: javadoc
public class EvaluationResultsForMultiLib {
    private final Map<SearchableLibraryIdentifier, EvaluationResult> results;
    private final Map<SearchableLibraryIdentifier, List<Exception>> exceptions;
    // LUKETODO:  single or multiple errors per library??
    private final Map<SearchableLibraryIdentifier, String> errors;

    public EvaluationResultsForMultiLib(
            Map<SearchableLibraryIdentifier, EvaluationResult> results,
            Map<SearchableLibraryIdentifier, List<Exception>> exceptions,
            Map<SearchableLibraryIdentifier, String> errors) {
        this.results = results;
        this.exceptions = exceptions;
        this.errors = errors;
    }

    // LUKETODO:  don't expose the maps directly, but rather provide methods to access the results
    public Map<SearchableLibraryIdentifier, EvaluationResult> getResults() {
        return results;
    }

    public Map<SearchableLibraryIdentifier, List<Exception>> getExceptions() {
        return exceptions;
    }

    // LUKETODO:  validate this isn't empty or Optional or something
    public EvaluationResult getFirstResult() {
        return results.entrySet().iterator().next().getValue();
    }

    // LUKETODO:  don't expose the maps directly, but rather provide methods to access the results
    public Map<SearchableLibraryIdentifier, String> getErrors() {
        return errors;
    }

    public EvaluationResult getResultFor(VersionedIdentifier libraryIdentifier) {
        return results.get(SearchableLibraryIdentifier.fromIdentifier(libraryIdentifier));
    }

    public EvaluationResult getResultFor(SearchableLibraryIdentifier libraryIdentifier) {
        return results.get(libraryIdentifier);
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
}
