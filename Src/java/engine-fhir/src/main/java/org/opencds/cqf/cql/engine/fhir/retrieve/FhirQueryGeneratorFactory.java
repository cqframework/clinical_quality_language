package org.opencds.cqf.cql.engine.fhir.retrieve;

import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;

import ca.uhn.fhir.context.FhirVersionEnum;

import static ca.uhn.fhir.context.FhirVersionEnum.DSTU3;
import static ca.uhn.fhir.context.FhirVersionEnum.R4;

public class FhirQueryGeneratorFactory {
    /**
     * Creates a FHIR version-specific FhirQueryGenerator
     * @param modelResolver is the model resolver for specific fhir version
     * @param searchParameterResolver the SearchParameterResolver instance the Generator should use
     * @param terminologyProvider the TerminologyProvider instance the Generator should use
     * @return a BaseFhirQueryGenerator
     * @throws IllegalArgumentException if the FHIR version specified is not supported
     */
    public static BaseFhirQueryGenerator create(ModelResolver modelResolver, SearchParameterResolver searchParameterResolver,
                                                TerminologyProvider terminologyProvider) throws FhirVersionMisMatchException {
        FhirVersionEnum fhirVersionEnum = searchParameterResolver.getFhirContext().getVersion().getVersion();
        if (fhirVersionEnum == DSTU3) {
            return new Dstu3FhirQueryGenerator(searchParameterResolver, terminologyProvider, modelResolver);
        } else if (fhirVersionEnum == R4) {
            return new R4FhirQueryGenerator(searchParameterResolver, terminologyProvider, modelResolver);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported FHIR version for FHIR Query Generation: %s", fhirVersionEnum));
        }
    }

    /**
     * Creates a FHIR version-specific FhirQueryGenerator
     * @param modelResolver is the model resolver for specific fhir version
     * @param searchParameterResolver the SearchParameterResolver instance the Generator should use
     * @param terminologyProvider the TerminologyProvider instance the Generator should use
     * @param shouldExpandValueSets configuration indicating whether or not ValueSets should be expanded for querying
     *                              via list of codes as opposed to using the :in modifier.
     * @param maxCodesPerQuery configuration indicating how many codes, at most, should be included on a query string
     * @param pageSize configuration indicating what the _count should be on the query
     * @return a BaseFhirQueryGenerator
     * @throws IllegalArgumentException if the FHIR version specified is not supported
     */
    public static BaseFhirQueryGenerator create(ModelResolver modelResolver, SearchParameterResolver searchParameterResolver,
                                         TerminologyProvider terminologyProvider, Boolean shouldExpandValueSets,
                                         Integer maxCodesPerQuery, Integer pageSize, Integer queryBatchThreshold) throws FhirVersionMisMatchException {
        BaseFhirQueryGenerator baseFhirQueryGenerator = create(modelResolver, searchParameterResolver, terminologyProvider);
        if (shouldExpandValueSets != null) {
            baseFhirQueryGenerator.setExpandValueSets(shouldExpandValueSets);
        }
        if (maxCodesPerQuery != null) {
            baseFhirQueryGenerator.setMaxCodesPerQuery(maxCodesPerQuery);
        }
        if (queryBatchThreshold != null) {
            baseFhirQueryGenerator.setQueryBatchThreshold(queryBatchThreshold);
        }
        if (pageSize != null) {
            baseFhirQueryGenerator.setPageSize(pageSize);
        }

        return baseFhirQueryGenerator;
    }
}
