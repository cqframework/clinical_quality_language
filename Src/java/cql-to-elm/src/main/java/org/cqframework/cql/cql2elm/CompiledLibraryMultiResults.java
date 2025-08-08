package org.cqframework.cql.cql2elm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;

/**
 * Represents the result of compiling a CQL library, including the compiled library and any compilation errors, in
 * order to support a partially successful compile with errors
 */
// LUKETODO:  record?
public class CompiledLibraryMultiResults {
    private final List<CompiledLibraryResult> results;

    public static CompiledLibraryMultiResults from(List<CompiledLibraryResult> results) {
        return new CompiledLibraryMultiResults(results);
    }

    private CompiledLibraryMultiResults(List<CompiledLibraryResult> results) {
        this.results = Collections.unmodifiableList(results);
    }

    public boolean hasErrors() {
        return results.stream().anyMatch(res -> !res.errors().isEmpty());
    }

    public List<CompiledLibraryResult> allResults() {
        return results;
    }

    public List<CompiledLibrary> allCompiledLibraries() {
        return results.stream().map(CompiledLibraryResult::compiledLibrary).toList();
    }

    public CompiledLibraryResult getOnlyResult() {
        if (results.size() != 1) {
            throw new IllegalStateException("Expected exactly one result, but found " + results.size());
        }
        return results.get(0);
    }

    public List<CqlCompilerException> getErrorsFor(VersionedIdentifier libraryIdentifier) {
        return results.stream()
                .filter(res -> libraryIdentifier.equals(res.compiledLibrary().getIdentifier()))
                .map(CompiledLibraryResult::errors)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Library> allLibrariesWithoutErrorSeverity() {
        return results.stream()
                .filter(res -> !CqlCompilerException.hasErrors(res.errors()))
                .map(CompiledLibraryResult::compiledLibrary)
                .map(CompiledLibrary::getLibrary)
                .toList();
    }

    public List<CqlCompilerException> allErrors() {
        return results.stream()
                .map(CompiledLibraryResult::errors)
                .flatMap(Collection::stream)
                .toList();
    }
}
