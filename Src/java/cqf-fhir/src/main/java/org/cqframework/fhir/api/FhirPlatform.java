package org.cqframework.fhir.api;

/**
 * FhirPlatform defines the base interface for accessing Fhir API features including the data access layer, capabilities,
 * transactions, operations, and services.
 *
 * Initial design discussion for this interface is taking place here:
 * https://github.com/DBCG/cql-evaluator/discussions/90
 */
public interface FhirPlatform {
    FhirDal dal();
    FhirCapabilities capabilities();

    FhirTransactions transactions();

    /**
     * TODO: Define the FhirOperations interface
     * @return
     */
    // FhirOperations operations();

    /**
     * Supports accessing specific services through the platform API such as terminology services and knowledge repositories
     * @param <T>
     * @return
     */
    <T extends FhirService> T getService();
}
