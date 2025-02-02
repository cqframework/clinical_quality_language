package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.Model
import org.hl7.cql.model.ModelIdentifier

interface CommonModelManager {
    fun resolveModel(modelIdentifier: ModelIdentifier): Model
    fun resolveModel(modelName: String): Model
    fun resolveModel(modelName: String, version: String? = null): Model
    fun resolveModelByUri(namespaceUri: String): Model

    /*
    A "well-known" model name is one that is allowed to resolve without a namespace in a namespace-aware context
     */
    fun isWellKnownModelName(unqualifiedIdentifier: String?): Boolean {
        return if (unqualifiedIdentifier == null) {
            false
        } else
            when (unqualifiedIdentifier) {
                "FHIR",
                "QDM",
                "USCore",
                "QICore",
                "QUICK" -> true
                else -> false
            }
    }
}
