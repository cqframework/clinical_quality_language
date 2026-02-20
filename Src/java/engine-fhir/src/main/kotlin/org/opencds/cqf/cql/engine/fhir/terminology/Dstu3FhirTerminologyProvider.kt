package org.opencds.cqf.cql.engine.fhir.terminology

import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import org.hl7.fhir.dstu3.model.BooleanType
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.CodeSystem
import org.hl7.fhir.dstu3.model.CodeType
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.Parameters
import org.hl7.fhir.dstu3.model.StringType
import org.hl7.fhir.dstu3.model.UriType
import org.hl7.fhir.dstu3.model.ValueSet
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.opencds.cqf.cql.engine.exception.TerminologyProviderException
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

class Dstu3FhirTerminologyProvider(private val fhirClient: IGenericClient) : TerminologyProvider {
    override fun `in`(code: Code, valueSet: ValueSetInfo): Boolean {
        try {
            val id = resolveValueSetId(valueSet)
            val respParam: Parameters
            if (code.system != null) {
                respParam =
                    fhirClient
                        .operation()
                        .onInstance(IdType(VALUE_SET, id)) // .onType(ValueSet.class)
                        .named("validate-code")
                        .withParameter(Parameters::class.java, "code", StringType(code.code))
                        .andParameter("system", StringType(code.system))
                        .useHttpGet()
                        .execute()
            } else {
                respParam =
                    fhirClient
                        .operation()
                        .onInstance(IdType(VALUE_SET, id)) // .onType(ValueSet.class)
                        .named("validate-code")
                        .withParameter(Parameters::class.java, "code", StringType(code.code))
                        .useHttpGet()
                        .execute()
            }
            return (respParam.getParameter()[0].getValue() as BooleanType).booleanValue()
        } catch (e: Exception) {
            throw TerminologyProviderException(
                "Error performing membership check of Code: $code in ValueSet: ${valueSet.id}",
                e,
            )
        }
    }

    override fun expand(valueSet: ValueSetInfo): Iterable<Code> {
        try {
            val id = resolveValueSetId(valueSet)
            val respParam =
                fhirClient
                    .operation()
                    .onInstance(IdType(VALUE_SET, id))
                    .named("expand")
                    .withNoParameters(Parameters::class.java)
                    .useHttpGet()
                    .execute()

            val expanded = respParam.getParameter()[0].getResource() as ValueSet
            val codes = mutableListOf<Code>()
            for (codeInfo in expanded.getExpansion().getContains()) {
                val nextCode =
                    Code()
                        .withCode(codeInfo.getCode())
                        .withSystem(codeInfo.getSystem())
                        .withVersion(codeInfo.getVersion())
                        .withDisplay(codeInfo.getDisplay())
                codes.add(nextCode)
            }
            return codes
        } catch (e: Exception) {
            throw TerminologyProviderException(
                "Error performing expansion of ValueSet: ${valueSet.id}",
                e,
            )
        }
    }

    override fun lookup(code: Code, codeSystem: CodeSystemInfo): Code {
        try {
            val respParam =
                fhirClient
                    .operation()
                    .onType(CodeSystem::class.java)
                    .named("lookup")
                    .withParameter(Parameters::class.java, "code", CodeType(code.code))
                    .andParameter("system", UriType(codeSystem.id))
                    .execute()

            val display =
                respParam
                    .getParameter()
                    .stream()
                    .filter { x: Parameters.ParametersParameterComponent? ->
                        x!!.getName() == "display"
                    }
                    .findFirst()
            if (display.isPresent) {
                code.withDisplay(display.get().getValue().toString())
            }

            return code.withSystem(codeSystem.id)
        } catch (e: Exception) {
            throw TerminologyProviderException(
                "Error performing lookup of Code: $code in CodeSystem: ${codeSystem.id}",
                e,
            )
        }
    }

    private fun searchByUrl(url: String?): Bundle {
        return fhirClient
            .search<IBaseBundle?>()
            .forResource(ValueSet::class.java)
            .where(ValueSet.URL.matches().value(url))
            .returnBundle(Bundle::class.java)
            .execute()
    }

    private fun searchByIdentifier(identifier: String?): Bundle {
        return fhirClient
            .search<IBaseBundle?>()
            .forResource(ValueSet::class.java)
            .where(ValueSet.IDENTIFIER.exactly().code(identifier))
            .returnBundle(Bundle::class.java)
            .execute()
    }

    private fun searchById(id: String): Bundle {
        var id = id
        if (id.startsWith(URN_OID)) {
            id = id.replace(URN_OID, "")
        } else if (id.startsWith(URN_UUID)) {
            id = id.replace(URN_UUID, "")
        }
        val searchResults = Bundle()
        // If we reached this point, and it looks like it might
        // be a FHIR resource ID, we will try to read it.
        // See https://www.hl7.org/fhir/datatypes.html#id
        if (id.matches("[A-Za-z0-9\\-\\.]{1,64}".toRegex())) {
            try {
                val vs = fhirClient.read().resource(ValueSet::class.java).withId(id).execute()
                searchResults.addEntry().setResource(vs)
            } catch (rnfe: ResourceNotFoundException) {
                // intentionally empty
            }
        }

        return searchResults
    }

    fun resolveValueSetId(valueSet: ValueSetInfo): String? {
        if (
            valueSet.version != null ||
                (valueSet.codeSystems != null && !valueSet.codeSystems!!.isEmpty())
        ) {
            throw UnsupportedOperationException(
                "Could not expand value set ${valueSet.id}; version and code system bindings are not supported at this time."
            )
        }

        // https://github.com/DBCG/cql_engine/pull/462 - Use a search path of URL,
        // identifier, and then resource id
        var searchResults = searchByUrl(valueSet.id)
        if (!searchResults.hasEntry()) {
            searchResults = searchByIdentifier(valueSet.id)
        }

        if (!searchResults.hasEntry()) {
            searchResults = searchById(valueSet.id!!)
        }

        require(!(!searchResults.hasEntry() || searchResults.getEntry().isEmpty())) {
            "Could not resolve value set ${valueSet.id}."
        }
        require(searchResults.getEntry().size <= 1) {
            "Found more than 1 ValueSet with url: " + valueSet.id
        }
        return searchResults.entryFirstRep.getResource().idElement.idPart
    }

    companion object {
        private const val URN_UUID = "urn:uuid:"
        private const val URN_OID = "urn:oid:"
        private const val VALUE_SET = "ValueSet"
    }
}
