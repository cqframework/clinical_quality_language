package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.engine.exception.CqlException;

public class DefaultLibraryLoader implements LibraryLoader {
    @Override
    public Library load(VersionedIdentifier libraryIdentifier) {
        throw new CqlException("Library loader is not implemented.");
    }
}
