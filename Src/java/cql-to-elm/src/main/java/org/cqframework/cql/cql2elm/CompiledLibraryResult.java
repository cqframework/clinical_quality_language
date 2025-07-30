package org.cqframework.cql.cql2elm;

import java.util.List;
import java.util.StringJoiner;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;

// LUKETODO: javadoc
record CompiledLibraryResult(CompiledLibrary compiledLibrary, List<CqlCompilerException> errors) {
    @Override
    public String toString() {
        return new StringJoiner(", ", CompiledLibraryResult.class.getSimpleName() + "[", "]")
                .add("compiledLibrary=" + compiledLibrary.getIdentifier())
                .add("errors=" + errors)
                .toString();
    }
}
