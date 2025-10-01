package org.cqframework.fhir.api.stu3;

/**
 * FhirPlatform defines the base interface for accessing Fhir API features including the data access layer, capabilities,
 * transactions, operations, and services.
 *
 * Initial design discussion for this interface is taking place here:
 * https://github.com/DBCG/cql-evaluator/discussions/90
 */
public interface FhirPlatform extends org.cqframework.fhir.api.FhirPlatform {
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
}
