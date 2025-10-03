package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.rest.api.SearchStyleEnum
import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.param.TokenParam
import java.util.function.BiFunction
import java.util.function.Consumer
import org.hl7.fhir.dstu3.model.Coding
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseCoding
import org.hl7.fhir.instance.model.api.IBaseResource
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.model.ModelResolver

class RestFhirRetrieveProvider(
    searchParameterResolver: SearchParameterResolver,
    modelResolver: ModelResolver,
    private val fhirClient: IGenericClient,
) : SearchParamFhirRetrieveProvider(searchParameterResolver, modelResolver) {
    var searchStyle: SearchStyleEnum = DEFAULT_SEARCH_STYLE

    override fun executeQueries(
        dataType: String?,
        queries: MutableList<SearchParameterMap>,
    ): Iterable<Any?> {
        if (queries.isEmpty()) {
            return mutableListOf()
        }

        val objects: MutableList<Any?> = ArrayList()
        val bundles: MutableList<IBaseBundle?> = ArrayList()
        for (map in queries) {
            val result = this.executeQuery(dataType, map)
            if (result is IBaseBundle) {
                bundles.add(result)
            } else {
                objects.add(result)
            }
        }

        // TODO: evaluate this lazily in case the engine only needs the first element
        for (b in bundles) {
            val cursor = FhirBundleCursor(fhirClient, b, dataType)
            cursor.forEach(Consumer { e: Any? -> objects.add(e) })
        }

        return objects
    }

    private fun executeQuery(dataType: String?, map: SearchParameterMap): IBaseResource? {
        if (map.containsKey("_id")) {
            return this.queryById(dataType, map)
        } else {
            var search = this.fhirClient.search<IBaseBundle?>().forResource(dataType)

            val flattenedMap: MutableMap<String, MutableList<IQueryParameterType>> = mutableMapOf()
            for (entry in map.entrySet()) {
                val name: String = entry.key

                val value: MutableList<MutableList<IQueryParameterType>> = entry.value
                if (value.isEmpty()) {
                    continue
                }

                val flattened: MutableList<IQueryParameterType> = ArrayList()

                for (subList in value) {
                    if (subList.isEmpty()) {
                        continue
                    }

                    if (subList.size == 1) {
                        flattened.add(subList[0])
                        continue
                    }

                    // Sublists are logical "Ors"
                    // The only "Or" supported from the engine are tokens at the moment.
                    // So this is a hack to add them to a "criterion", which is the
                    // only way the HAPI POST api supports passing them.
                    val first = subList[0]
                    if (first is TokenParam) {
                        val tcp = TokenClientParam(name)

                        val codings = this.toCodings(subList)

                        val criterion = tcp.exactly().codings(*codings)

                        search = search.where(criterion)
                    } else {
                        flattened.addAll(subList)
                    }
                }

                flattenedMap[name] = flattened
            }

            if (pageSize != null) {
                search.count(pageSize!!)
            }

            return search.where(flattenedMap).usingStyle(this.searchStyle).execute()
        }
    }

    private fun queryById(dataType: String?, map: SearchParameterMap): IBaseResource? {
        require(map.entrySet().size <= 1) {
            String.format(
                "Error querying %s. Queries by id must not have any other search criteria.",
                dataType,
            )
        }

        val tokenList: MutableList<IQueryParameterType> = map.get("_id")!![0]
        require(!tokenList.isEmpty()) {
            String.format(
                "Error querying %s. Attempted query by id but no id was specified.",
                dataType,
            )
        }

        require(tokenList.size <= 1) {
            String.format(
                "Error querying %s. Attempted query by id but multiple ids were specified.",
                dataType,
            )
        }

        val param = tokenList[0]
        require(param is TokenParam) {
            String.format(
                "Error querying %s. Attempted query by id but a non-token parameter was given.",
                dataType,
            )
        }

        val id = param.value

        requireNotNull(id) {
            String.format("Error querying %s. Attempted query by id but id was null.", dataType)
        }

        return queryById(dataType, id)
    }

    private fun queryById(dataType: String?, id: String?): IBaseResource? {
        return this.fhirClient.read().resource(dataType).withId(id).execute()
    }

    private fun toCodings(codingList: MutableList<IQueryParameterType>): Array<IBaseCoding> {
        val codings: MutableList<IBaseCoding> = ArrayList()

        val codingConverter =
            when (this.fhirClient.fhirContext.version.version) {
                FhirVersionEnum.DSTU3 ->
                    BiFunction { system: String?, code: String? -> Coding(system, code, null) }
                FhirVersionEnum.R4 ->
                    BiFunction { system: String?, code: String? ->
                        org.hl7.fhir.r4.model.Coding(system, code, null)
                    }
                else -> throw IllegalArgumentException("Unhandled FHIR version")
            }

        for (param in codingList) {
            val token = param as TokenParam

            val coding = codingConverter.apply(token.system, token.value)

            codings.add(coding)
        }

        return codings.toTypedArray<IBaseCoding>()
    }

    companion object {
        private val DEFAULT_SEARCH_STYLE = SearchStyleEnum.GET
    }
}
