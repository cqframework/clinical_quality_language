package org.cqframework.fhir.api.r4

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Resource

/**
 * Defines the interactions for a publishable artifact repository, as specified in the
 * PublishableArtifactRepository capability statement:
 * https://build.fhir.org/ig/HL7/cqf-measures/CapabilityStatement-publishable-measure-repository.html
 */
interface FhirPublishableArtifactRepository<T : Resource?> : FhirArtifactRepository<T> {
    /**
     * Packages the given artifact as a Bundle, with components and dependencies included.
     *
     * @param url
     * @param version
     * @return
     */
    fun packageArtifact(url: String?, version: String?): Bundle?

    /**
     * Determines the effective data requirements of the given artifact, returning them as a module
     * definition library.
     *
     * @param url
     * @param version
     * @return
     */
    fun dataRequirements(url: String?, version: String?): Library?
}
