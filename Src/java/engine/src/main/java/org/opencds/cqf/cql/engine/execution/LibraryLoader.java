package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.VersionedIdentifier;

public interface LibraryLoader {
    Library load(VersionedIdentifier libraryIdentifier);
}
