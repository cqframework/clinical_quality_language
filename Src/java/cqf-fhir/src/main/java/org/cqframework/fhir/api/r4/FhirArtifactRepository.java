package org.cqframework.fhir.api.r4;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

/**
 * This interface supports basic shareable artifact repository interactions, as defined by the ShareableArtifact
 * capability statement (https://build.fhir.org/ig/HL7/cqf-measures/CapabilityStatement-shareable-measure-repository.html)
 */
public interface FhirArtifactRepository<T extends Resource> extends FhirService {
    /**
     * Returns the set of resources matching the given canonical URL
     * @param url
     * @return
     */
    Bundle byCanonical(String url);

    /**
     * Returns the unique resource matching the given canonical and version. If more than one resource
     * matches the given canonical and version, an error is thrown.
     * @param urlWithVersion
     * @param <T>
     * @return
     */
    <T extends Resource> T byCanonicalWithVersion(String urlWithVersion);

    /**
     * Returns the unique resource matching the given canonical and version. If more than one resource
     * matches the given canonical and version, an error is thrown.
     * @param url
     * @param version
     * @param <T>
     * @return
     */
    <T extends Resource> T byCanonicalAndVersion(String url, String version);

    /**
     * Returns the "current" version of the given canonical, using the versioning semantics defined by the repository.
     * @param url
     * @param <T>
     * @return
     */
    <T extends Resource> T currentByCanonical(String url);

    /**
     * Returns the "current" version of the given canonical, including resources in draft status, using the
     * versioning semantics defined by the repository.
     * @param url
     * @param includeDraft
     * @param <T>
     * @return
     */
    <T extends Resource> T currentByCanonical(String url, boolean includeDraft);
}
