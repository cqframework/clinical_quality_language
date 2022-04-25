package org.cqframework.fhir.api;

/**
 * FhirPlatform defines the base interface for accessing Fhir API features including the data access layer, capabilities,
 * transactions, operations, and services.
 *
 * Initial design discussion for this interface is taking place here:
 * https://github.com/DBCG/cql-evaluator/discussions/90
 */
public interface FhirPlatform {
    /**
     * Returns an implementation of the FhirDal interface appropriate to the environment.
     * @return
     */
    FhirDal dal();

    /**
     * Returns an implementation of the FhirCapabilities interface appropriate to the environment.
     * @return
     */
    FhirCapabilities capabilities();

    /**
     * Returns an implementation of the FhirTransactions interface appropriate to the environment.
     * @return
     */
    FhirTransactions transactions();

    /**
     * TODO: Define the FhirOperations interface
     * @return
     */
    // FhirOperations operations();

    /**
     * Supports accessing specific services through the platform API such as terminology services and knowledge repositories
     * @param <T> The type of FhirService being requested
     * @return An implementation of the requested interface appropriate to the environment
     */
    <T extends FhirService> T getService();
}
