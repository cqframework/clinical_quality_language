package org.cqframework.fhir.api.r4

import org.cqframework.fhir.api.FhirPlatform

/**
 * FhirPlatform defines the base interface for accessing Fhir API features including the data access
 * layer, capabilities, transactions, operations, and services.
 *
 * Initial design discussion for this interface is taking place here:
 * https://github.com/DBCG/cql-evaluator/discussions/90
 */
interface FhirPlatform : FhirPlatform {
    /**
     * Returns an implementation of the FhirDal interface appropriate to the environment.
     *
     * @return
     */
    override fun dal(): FhirDal?

    /**
     * Returns an implementation of the FhirCapabilities interface appropriate to the environment.
     *
     * @return
     */
    override fun capabilities(): FhirCapabilities?

    /**
     * Returns an implementation of the FhirTransactions interface appropriate to the environment.
     *
     * @return
     */
    override fun transactions(): FhirTransactions?
}
