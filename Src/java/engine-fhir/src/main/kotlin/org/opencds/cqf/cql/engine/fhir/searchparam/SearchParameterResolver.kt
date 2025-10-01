package org.opencds.cqf.cql.engine.fhir.searchparam

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.RuntimeSearchParam
import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.rest.api.RestSearchParameterTypeEnum
import ca.uhn.fhir.rest.param.NumberParam
import ca.uhn.fhir.rest.param.QuantityParam
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.param.UriParam
import kotlin.text.isEmpty
import kotlin.text.split
import kotlin.text.startsWith
import kotlin.text.toRegex
import kotlin.text.trim
import org.apache.commons.lang3.tuple.Pair

class SearchParameterResolver(val fhirContext: FhirContext) {
    fun getSearchParameterDefinition(dataType: String?, path: String?): RuntimeSearchParam? {
        return this.getSearchParameterDefinition(
            dataType,
            path,
            null as RestSearchParameterTypeEnum?,
        )
    }

    fun getSearchParameterDefinition(
        dataType: String?,
        path: String?,
        paramType: RestSearchParameterTypeEnum?,
    ): RuntimeSearchParam? {
        var path = path
        if (dataType == null || path == null) {
            return null
        }

        // Special case for system params. They need to be resolved by name.
        // TODO: All the others like "_language"
        var name: String? = null
        if (path == "id") {
            name = "_id"
            path = ""
        }

        val params = this.fhirContext.getResourceDefinition(dataType).searchParams

        for (param in params) {
            // If name matches, it's the one we want.
            if (name != null && param.name == name) {
                return param
            }

            // Filter out parameters that don't match our requested type.
            if (paramType != null && param.paramType != paramType) {
                continue
            }

            val normalizedPath = normalizePath(param.path)
            if (path == normalizedPath || path.equals(param.name, ignoreCase = true)) {
                return param
            }
        }

        return null
    }

    fun createSearchParameter(
        context: String?,
        dataType: String?,
        path: String?,
        value: String?,
    ): Pair<String, IQueryParameterType>? {
        val searchParam = this.getSearchParameterDefinition(dataType, path) ?: return null

        val name = searchParam.name

        when (searchParam.paramType) {
            RestSearchParameterTypeEnum.TOKEN ->
                return Pair.of<String, IQueryParameterType>(name, TokenParam(value))
            RestSearchParameterTypeEnum.REFERENCE ->
                return Pair.of<String, IQueryParameterType>(
                    name,
                    ReferenceParam(context, null, value),
                )

            RestSearchParameterTypeEnum.QUANTITY ->
                return Pair.of<String, IQueryParameterType>(name, QuantityParam(value))

            RestSearchParameterTypeEnum.STRING ->
                return Pair.of<String, IQueryParameterType>(name, StringParam(value))

            RestSearchParameterTypeEnum.NUMBER ->
                return Pair.of<String, IQueryParameterType>(name, NumberParam(value))

            RestSearchParameterTypeEnum.URI ->
                return Pair.of<String, IQueryParameterType>(name, UriParam(value))

            RestSearchParameterTypeEnum.DATE,
            RestSearchParameterTypeEnum.HAS,
            RestSearchParameterTypeEnum.COMPOSITE,
            RestSearchParameterTypeEnum.SPECIAL -> {}
        }

        return null
    }

    // This is actually a lot of processing. We should cache search parameter resolutions.
    private fun normalizePath(path: String): String? {
        // TODO: What we really need is FhirPath parsing to just get the path
        // MedicationAdministration.medication.as(CodeableConcept)
        // MedicationAdministration.medication.as(Reference)
        // (MedicationAdministration.medication as CodeableConcept)
        // (MedicationAdministration.medication as Reference)
        // Condition.onset.as(Age) | Condition.onset.as(Range)
        // Observation.code | Observation.component.code

        // Trim off outer parens

        var path = path
        if (path == "(") {
            path = path.substring(1, path.length - 1)
        }

        val normalizedParts: MutableSet<String?> = HashSet()
        val orParts = path.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (part in orParts) {
            path = part.trim { it <= ' ' }

            // Trim off DataType
            path = path.substring(path.indexOf(".") + 1, path.length)

            // Split into components
            val pathSplit =
                path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val newPathParts: MutableList<String> = ArrayList()

            for (p in pathSplit) {
                // Skip the "as(X)" part.
                if (p.startsWith("as(")) {
                    continue
                }

                // Skip the "[x]" part.
                if (p.startsWith("[x]")) {
                    continue
                }

                // Filter out spaces and everything after "medication as Reference"
                val ps = p.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (ps.isNotEmpty()) {
                    newPathParts.add(ps[0])
                }
            }

            path = newPathParts.joinToString(".")
            normalizedParts.add(path)
        }

        // This handles cases such as /Condition?onset-age and /Condition?onset-date
        // where there are multiple underlying representations of the same property
        // (e.g. Condition.onset.as(Age) | Condition.onset.as(Range)), but
        // will punt on something like /Observation?combo-code where the underlying
        // representation maps to multiple places in a nested hierarchy (e.g.
        // Observation.code | Observation.component.code ).
        return if (normalizedParts.size == 1) {
            normalizedParts.iterator().next()
        } else {
            null
        }
    }
}
