package org.cqframework.fhir.api.stu3;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Library;
import org.hl7.fhir.dstu3.model.Resource;

/**
 * Defines the interactions for a publishable artifact repository, as specified in the PublishableArtifactRepository
 * capability statement: https://build.fhir.org/ig/HL7/cqf-measures/CapabilityStatement-publishable-measure-repository.html
 */
public interface FhirPublishableArtifactRepository<T extends Resource> extends FhirArtifactRepository {
    /**
     * Packages the given artifact as a Bundle, with components and dependencies included.
     * @param url
     * @param version
     * @return
     */
    Bundle packageArtifact(String url, String version);

    /**
     * Determines the effective data requirements of the given artifact, returning them as a module definition library.
     * @param url
     * @param version
     * @return
     */
    Library dataRequirements(String url, String version);
}
