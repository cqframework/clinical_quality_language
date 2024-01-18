package org.opencds.cqf.cql.engine.fhir.retrieve;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import java.util.List;
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

@SuppressWarnings("checkstyle:abstractclassname")
public abstract class SearchParamFhirRetrieveProvider extends TerminologyAwareRetrieveProvider {
    protected FhirContext fhirContext;
    protected SearchParameterResolver searchParameterResolver;
    protected Integer pageSize;
    protected Integer maxCodesPerQuery;
    protected Integer queryBatchThreshold;
    private BaseFhirQueryGenerator fhirQueryGenerator;
    private ModelResolver modelResolver;

    protected SearchParamFhirRetrieveProvider(SearchParameterResolver searchParameterResolver) {
        this.searchParameterResolver = searchParameterResolver;
        this.fhirContext = searchParameterResolver.getFhirContext();
    }

    protected SearchParamFhirRetrieveProvider(
            SearchParameterResolver searchParameterResolver, ModelResolver modelResolver) {
        this(searchParameterResolver);
        this.modelResolver = modelResolver;
    }

    public void setPageSize(Integer value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("value must be a non-null integer > 0");
        }

        this.pageSize = value;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setFhirQueryGenerator(BaseFhirQueryGenerator fhirQueryGenerator) {
        this.fhirQueryGenerator = fhirQueryGenerator;
    }

    public BaseFhirQueryGenerator getFhirQueryGenerator() {
        return fhirQueryGenerator;
    }

    public void setModelResolver(ModelResolver modelResolver) {
        this.modelResolver = modelResolver;
    }

    public ModelResolver getModelResolver() {
        return modelResolver;
    }

    public void setMaxCodesPerQuery(Integer value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("value must be a non-null integer > 0");
        }

        this.maxCodesPerQuery = value;
    }

    public Integer getMaxCodesPerQuery() {
        return this.maxCodesPerQuery;
    }

    public void setQueryBatchThreshold(Integer value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("value must be a non-null integer > 0");
        }

        this.queryBatchThreshold = value;
    }

    public Integer getQueryBatchThreshold() {
        return this.queryBatchThreshold;
    }

    protected abstract Iterable<Object> executeQueries(String dataType, List<SearchParameterMap> queries);

    @Override
    public Iterable<Object> retrieve(
            String context,
            String contextPath,
            Object contextValue,
            String dataType,
            String templateId,
            String codePath,
            Iterable<Code> codes,
            String valueSet,
            String datePath,
            String dateLowPath,
            String dateHighPath,
            Interval dateRange) {

        List<SearchParameterMap> queries = null;

        if (this.fhirContext != null && modelResolver != null) {
            try {
                if (this.fhirContext.getVersion().getVersion().equals(FhirVersionEnum.DSTU3)) {
                    fhirQueryGenerator = new Dstu3FhirQueryGenerator(
                            searchParameterResolver, terminologyProvider, this.modelResolver);
                } else if (this.fhirContext.getVersion().getVersion().equals(FhirVersionEnum.R4)) {
                    fhirQueryGenerator =
                            new R4FhirQueryGenerator(searchParameterResolver, terminologyProvider, this.modelResolver);
                }
            } catch (FhirVersionMisMatchException exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
        if (fhirQueryGenerator != null) {
            fhirQueryGenerator.setExpandValueSets(isExpandValueSets());
            if (getMaxCodesPerQuery() != null && getMaxCodesPerQuery() > 0) {
                fhirQueryGenerator.setMaxCodesPerQuery(getMaxCodesPerQuery());
            }
            if (getQueryBatchThreshold() != null && getQueryBatchThreshold() > 0) {
                fhirQueryGenerator.setQueryBatchThreshold(getQueryBatchThreshold());
            }
            if (getPageSize() != null && getPageSize() > 0) {
                fhirQueryGenerator.setPageSize(getPageSize());
            }

            queries = fhirQueryGenerator.setupQueries(
                    context,
                    contextPath,
                    contextValue,
                    dataType,
                    templateId,
                    codePath,
                    codes,
                    valueSet,
                    datePath,
                    dateLowPath,
                    dateHighPath,
                    dateRange);
        }

        return this.executeQueries(dataType, queries);
    }
}
