package org.cqframework.fhir.api

import ca.uhn.fhir.model.api.IQueryParameterType
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType

/**
 * This interface is a minimal Fhir CRUD API. It's based on the
 * [FHIR HTTP API](https://www.hl7.org/fhir/http.html), but constrained to provide only the
 * operations necessary for the cql-evaluator modules to function.
 *
 * @see [https://www.hl7.org/fhir/http.html](https://www.hl7.org/fhir/http.html)
 */
interface FhirDal {
    /**
     * Fetches an `IBaseResource` by `id`. The `IIdType` must have the resourceType defined.
     *
     * Returns null if no resource is found.
     *
     * @param id the id of the resource
     * @return the resource
     */
    fun read(id: IIdType?): IBaseResource?

    /**
     * Creates the `IBaseResource`.
     *
     * Default behavior is to overwrite the resource if it already exists.
     *
     * @param resource the resource
     */
    fun create(resource: IBaseResource?)

    /**
     * Updates the `IBaseResource`.
     *
     * Default behavior is to create the resource if it does not exist.
     *
     * @param resource the resource
     */
    fun update(resource: IBaseResource?)

    /**
     * Deletes an `IBaseResource` by `id`. The `IIdType` must have the resourceType defined.
     *
     * Default behavior is no-op if the resource does not exist.
     *
     * @param id the id of the resource
     */
    fun delete(id: IIdType?)

    /**
     * Returns an `Iterable<IBaseResource>` of all the resources of type `resourceType`.
     *
     * Default behavior is to return null if no resources are found.
     *
     * @param resourceType the type of resources to return.
     * @return the resources
     */
    fun search(
        resourceType: String?,
        searchParameters: MutableMap<String?, MutableList<MutableList<IQueryParameterType?>?>?>?,
    ): IBaseBundle?
}
