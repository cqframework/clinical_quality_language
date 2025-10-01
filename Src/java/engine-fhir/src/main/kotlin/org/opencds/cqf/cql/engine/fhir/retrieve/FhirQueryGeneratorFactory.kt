package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.context.FhirVersionEnum
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

object FhirQueryGeneratorFactory {
    /**
     * Creates a FHIR version-specific FhirQueryGenerator
     *
     * @param modelResolver is the model resolver for specific fhir version
     * @param searchParameterResolver the SearchParameterResolver instance the Generator should use
     * @param terminologyProvider the TerminologyProvider instance the Generator should use
     * @return a BaseFhirQueryGenerator
     * @throws IllegalArgumentException if the FHIR version specified is not supported
     */
    @Throws(FhirVersionMisMatchException::class)
    fun create(
        modelResolver: ModelResolver,
        searchParameterResolver: SearchParameterResolver,
        terminologyProvider: TerminologyProvider?,
    ): BaseFhirQueryGenerator {
        return when (val fhirVersionEnum = searchParameterResolver.fhirContext.version.version) {
            FhirVersionEnum.DSTU3 -> {
                Dstu3FhirQueryGenerator(searchParameterResolver, terminologyProvider, modelResolver)
            }
            FhirVersionEnum.R4 -> {
                R4FhirQueryGenerator(searchParameterResolver, terminologyProvider, modelResolver)
            }
            else -> {
                throw IllegalArgumentException(
                    String.format(
                        "Unsupported FHIR version for FHIR Query Generation: %s",
                        fhirVersionEnum,
                    )
                )
            }
        }
    }

    /**
     * Creates a FHIR version-specific FhirQueryGenerator
     *
     * @param modelResolver is the model resolver for specific fhir version
     * @param searchParameterResolver the SearchParameterResolver instance the Generator should use
     * @param terminologyProvider the TerminologyProvider instance the Generator should use
     * @param shouldExpandValueSets configuration indicating whether or not ValueSets should be
     *   expanded for querying via list of codes as opposed to using the :in modifier.
     * @param maxCodesPerQuery configuration indicating how many codes, at most, should be included
     *   on a query string
     * @param pageSize configuration indicating what the _count should be on the query
     * @return a BaseFhirQueryGenerator
     * @throws IllegalArgumentException if the FHIR version specified is not supported
     */
    @Throws(FhirVersionMisMatchException::class)
    fun create(
        modelResolver: ModelResolver,
        searchParameterResolver: SearchParameterResolver,
        terminologyProvider: TerminologyProvider?,
        shouldExpandValueSets: Boolean?,
        maxCodesPerQuery: Int?,
        pageSize: Int?,
        queryBatchThreshold: Int?,
    ): BaseFhirQueryGenerator {
        val baseFhirQueryGenerator =
            create(modelResolver, searchParameterResolver, terminologyProvider)
        if (shouldExpandValueSets != null) {
            baseFhirQueryGenerator.isExpandValueSets = shouldExpandValueSets
        }
        if (maxCodesPerQuery != null) {
            baseFhirQueryGenerator.maxCodesPerQuery = maxCodesPerQuery
        }
        if (queryBatchThreshold != null) {
            baseFhirQueryGenerator.queryBatchThreshold = queryBatchThreshold
        }
        if (pageSize != null) {
            baseFhirQueryGenerator.pageSize = pageSize
        }

        return baseFhirQueryGenerator
    }
}
