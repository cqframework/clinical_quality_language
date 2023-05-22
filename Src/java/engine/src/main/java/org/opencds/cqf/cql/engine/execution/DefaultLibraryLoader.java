package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.exception.CqlException;

public class DefaultLibraryLoader implements LibraryLoader {
    @Override
    public Library load(VersionedIdentifier libraryIdentifier) {
        throw new CqlException("Library loader is not implemented.");
    }
}
