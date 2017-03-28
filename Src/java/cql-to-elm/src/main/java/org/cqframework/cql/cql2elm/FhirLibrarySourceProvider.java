package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;

/**
 * Created by Bryn on 3/28/2017.
 */
public class FhirLibrarySourceProvider implements LibrarySourceProvider {
    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return FhirLibrarySourceProvider.class.getResourceAsStream(String.format("/org/hl7/fhir/%s-%s.cql", libraryIdentifier.getId(),
                libraryIdentifier.getVersion()));
    }
}
