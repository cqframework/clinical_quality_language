package org.opencds.cqf.cql.engine.fhir.retrieve;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.RuntimeSearchParam;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.RestSearchParameterTypeEnum;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.TokenParamModifier;
import java.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo;

public abstract class BaseFhirQueryGenerator implements FhirVersionIntegrityChecker {
    protected static final boolean DEFAULT_SHOULD_EXPAND_VALUESETS = false;

    protected FhirContext fhirContext;

    protected TerminologyProvider terminologyProvider;
    protected SearchParameterResolver searchParameterResolver;
    protected ModelResolver modelResolver;

    private Integer pageSize;

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("value must be a non-null integer > 0");
        }
        this.pageSize = value;
    }

    private Integer maxCodesPerQuery;

    public Integer getMaxCodesPerQuery() {
        return this.maxCodesPerQuery;
    }

    public void setMaxCodesPerQuery(Integer value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("value must be non-null integer > 0");
        }
        this.maxCodesPerQuery = value;
    }

    private Integer queryBatchThreshold;

    public Integer getQueryBatchThreshold() {
        return this.queryBatchThreshold;
    }

    public void setQueryBatchThreshold(Integer value) {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("value must be non-null integer > 0");
        }
        this.queryBatchThreshold = value;
    }

    // TODO: Think about how to best handle the decision to expand value sets... Should it be part of the
    // terminology provider if it detects support for "code:in"? How does that feed back to the retriever?
    private boolean expandValueSets;

    public boolean isExpandValueSets() {
        return this.expandValueSets;
    }

    public void setExpandValueSets(boolean expandValueSets) {
        this.expandValueSets = expandValueSets;
    }

    protected BaseFhirQueryGenerator(
            SearchParameterResolver searchParameterResolver,
            TerminologyProvider terminologyProvider,
            ModelResolver modelResolver,
            FhirContext fhirContext)
            throws FhirVersionMisMatchException {
        this.searchParameterResolver = searchParameterResolver;
        this.terminologyProvider = terminologyProvider;
        this.modelResolver = modelResolver;
        this.setExpandValueSets(DEFAULT_SHOULD_EXPAND_VALUESETS);

        this.fhirContext = fhirContext;
        validateFhirVersionIntegrity(fetchFhirVersionEnum(this.fhirContext));
    }

    @Override
    public void validateFhirVersionIntegrity(FhirVersionEnum fhirVersionEnum) throws FhirVersionMisMatchException {
        if (this.searchParameterResolver != null
                && fetchFhirVersionEnum(this.searchParameterResolver.getFhirContext()) != fhirVersionEnum) {
            throw new IllegalArgumentException("Components have different");
        }
    }

    public FhirVersionEnum fetchFhirVersionEnum(FhirContext fhirContext) {
        if (fhirContext == null) {
            throw new NullPointerException("The provided argument is null");
        }
        return fhirContext.getVersion().getVersion();
    }

    public abstract List<String> generateFhirQueries(
            ICompositeType dataRequirement,
            DateTime evaluationDateTime,
            Map<String, Object> contextValues,
            Map<String, Object> parameters,
            IBaseConformance capabilityStatement);

    public abstract FhirVersionEnum getFhirVersion();

    protected Pair<String, IQueryParameterType> getTemplateParam(String dataType, String templateId) {
        if (templateId == null || templateId.equals("")) {
            return null;
        }

        // Do something?
        return null;
    }

    protected Pair<String, DateRangeParam> getDateRangeParam(
            String dataType, String datePath, String dateLowPath, String dateHighPath, Interval dateRange) {
        if (dateRange == null) {
            return null;
        }

        DateParam low = null;
        DateParam high = null;
        if (dateRange.getLow() != null) {
            if (dateRange.getLow() instanceof Date) {
                low = new DateParam(
                        ParamPrefixEnum.GREATERTHAN_OR_EQUALS, Date.from(((Date) dateRange.getLow()).toInstant()));
            } else if (dateRange.getLow() instanceof DateTime) {
                low = new DateParam(
                        ParamPrefixEnum.GREATERTHAN_OR_EQUALS,
                        Date.from(((DateTime) dateRange.getLow()).getDateTime().toInstant()));
            }
        }

        if (dateRange.getHigh() != null) {
            if (dateRange.getLow() instanceof Date) {
                high = new DateParam(
                        ParamPrefixEnum.LESSTHAN_OR_EQUALS, Date.from(((Date) dateRange.getHigh()).toInstant()));
            } else if (dateRange.getLow() instanceof DateTime) {
                high = new DateParam(
                        ParamPrefixEnum.LESSTHAN_OR_EQUALS,
                        Date.from(((DateTime) dateRange.getHigh()).getDateTime().toInstant()));
            }
        }

        DateRangeParam rangeParam;
        if (low == null && high != null) {
            rangeParam = new DateRangeParam(high);
        } else if (high == null && low != null) {
            rangeParam = new DateRangeParam(low);
        } else {
            rangeParam = new DateRangeParam(low, high);
        }

        RuntimeSearchParam dateParam = this.searchParameterResolver.getSearchParameterDefinition(
                dataType, datePath, RestSearchParameterTypeEnum.DATE);

        if (dateParam == null) {
            throw new UnsupportedOperationException(String.format(
                    "Could not resolve a search parameter with date type for %s.%s ", dataType, datePath));
        }

        return Pair.of(dateParam.getName(), rangeParam);
    }

    protected Pair<String, IQueryParameterType> getContextParam(
            String dataType, String context, String contextPath, Object contextValue) {
        if (context != null && context.equals("Patient") && contextValue != null && contextPath != null) {
            return this.searchParameterResolver.createSearchParameter(
                    context, dataType, contextPath, (String) contextValue);
        }

        return null;
    }

    protected Pair<String, List<TokenOrListParam>> getCodeParams(
            String dataType, String codePath, Iterable<Code> codes, String valueSet) {
        if (valueSet != null && valueSet.startsWith("urn:oid:")) {
            valueSet = valueSet.replace("urn:oid:", "");
        }

        if (codePath == null && (codes != null || valueSet != null)) {
            throw new IllegalArgumentException("A code path must be provided when filtering on codes or a valueset.");
        }

        if (codePath == null || codePath.isEmpty()) {
            return null;
        }

        // TODO: This assumes the code path will always be a token param.
        List<TokenOrListParam> codeParamLists = this.getCodeParams(codes, valueSet);
        if (codeParamLists == null || codeParamLists.isEmpty()) {
            return null;
        }

        RuntimeSearchParam codeParam = this.searchParameterResolver.getSearchParameterDefinition(
                dataType, codePath, RestSearchParameterTypeEnum.TOKEN);

        if (codeParam == null) {
            return null;
        }

        return Pair.of(codeParam.getName(), codeParamLists);
    }

    // The code params will be either the literal set of codes in the event the data server doesn't have the referenced
    // ValueSet (or doesn't support pulling and caching a ValueSet). If the target server DOES support that then it's
    // "dataType.codePath in ValueSet"
    protected List<TokenOrListParam> getCodeParams(Iterable<Code> codes, String valueSet) {
        if (valueSet != null) {
            if (!isExpandValueSets()) {
                return Collections.singletonList(
                        new TokenOrListParam().addOr(new TokenParam(valueSet).setModifier(TokenParamModifier.IN)));
            } else {
                if (terminologyProvider == null) {
                    throw new IllegalArgumentException(
                            "Expand value sets cannot be used without a terminology provider and no terminology provider is set.");
                }
                ValueSetInfo valueSetInfo = new ValueSetInfo().withId(valueSet);
                codes = terminologyProvider.expand(valueSetInfo);
            }
        }

        if (codes == null) {
            return Collections.emptyList();
        }

        // If the number of queries generated as a result of the code list size and maxCodesPerQuery constraint would
        // result in a number of queries that is greater than the queryBatchThreshold value, then codeParams will
        // be omitted altogether.
        if (getMaxCodesPerQuery() != null && getQueryBatchThreshold() != null) {
            if ((getIterableSize(codes) / (float) getMaxCodesPerQuery()) > getQueryBatchThreshold()) {
                return Collections.emptyList();
            }
        }

        List<TokenOrListParam> codeParamsList = new ArrayList<>();
        TokenOrListParam codeParams = null;
        int codeCount = 0;
        for (Object code : codes) {
            if (getMaxCodesPerQuery() == null) {
                if (codeCount == 0 && codeParams == null) {
                    codeParams = new TokenOrListParam();
                }
            } else if (codeCount % getMaxCodesPerQuery() == 0) {
                if (codeParams != null) {
                    codeParamsList.add(codeParams);
                }

                codeParams = new TokenOrListParam();
            }

            codeCount++;
            if (code instanceof Code) {
                Code c = (Code) code;
                codeParams.addOr(new TokenParam(c.getSystem(), c.getCode()));
            } else if (code instanceof String) {
                String s = (String) code;
                codeParams.addOr(new TokenParam(s));
            }
        }

        if (codeParams != null) {
            codeParamsList.add(codeParams);
        }

        return codeParamsList;
    }

    protected List<SearchParameterMap> setupQueries(
            String context,
            String contextPath,
            Object contextValue,
            String dataType,
            String templateId,
            List<CodeFilter> codeFilters,
            List<DateFilter> dateFilters) {
        Pair<String, IQueryParameterType> templateParam = this.getTemplateParam(dataType, templateId);

        Pair<String, IQueryParameterType> contextParam =
                this.getContextParam(dataType, context, contextPath, contextValue);

        List<Pair<String, DateRangeParam>> dateRangeParams = new ArrayList<Pair<String, DateRangeParam>>();
        if (dateFilters != null) {
            for (DateFilter df : dateFilters) {
                Pair<String, DateRangeParam> dateRangeParam = this.getDateRangeParam(
                        dataType, df.getDatePath(), df.getDateLowPath(), df.getDateHighPath(), df.getDateRange());
                if (dateRangeParam != null) {
                    dateRangeParams.add(dateRangeParam);
                }
            }
        }

        List<Pair<String, List<TokenOrListParam>>> codeParamList =
                new ArrayList<Pair<String, List<TokenOrListParam>>>();
        if (codeFilters != null) {
            for (CodeFilter cf : codeFilters) {
                Pair<String, List<TokenOrListParam>> codeParams =
                        this.getCodeParams(dataType, cf.getCodePath(), cf.getCodes(), cf.getValueSet());

                if (codeParams != null) {
                    codeParamList.add(codeParams);
                }
            }
        }

        return this.innerSetupQueries(templateParam, contextParam, dateRangeParams, codeParamList);
    }

    protected List<SearchParameterMap> setupQueries(
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
        return setupQueries(
                context,
                contextPath,
                contextValue,
                dataType,
                templateId,
                codePath != null ? Arrays.asList(new CodeFilter(codePath, codes, valueSet)) : null,
                datePath != null || dateLowPath != null || dateHighPath != null
                        ? Arrays.asList(new DateFilter(datePath, dateLowPath, dateHighPath, dateRange))
                        : null);
    }

    protected List<SearchParameterMap> innerSetupQueries(
            Pair<String, IQueryParameterType> templateParam,
            Pair<String, IQueryParameterType> contextParam,
            List<Pair<String, DateRangeParam>> dateRangeParams,
            List<Pair<String, List<TokenOrListParam>>> codeParams) {

        if (codeParams == null || codeParams.isEmpty()) {
            return Collections.singletonList(this.getBaseMap(templateParam, contextParam, dateRangeParams, codeParams));
        }

        Pair<String, List<TokenOrListParam>> chunkedCodeParam = null;
        for (Pair<String, List<TokenOrListParam>> cp : codeParams) {
            if (cp.getValue() != null && cp.getValue().size() > 1) {
                if (chunkedCodeParam != null) {
                    throw new IllegalArgumentException(String.format(
                            "Cannot evaluate multiple chunked code filters on %s and %s",
                            chunkedCodeParam.getKey(), cp.getKey()));
                }
                chunkedCodeParam = cp;
            }
        }

        if (chunkedCodeParam == null) {
            return Collections.singletonList(this.getBaseMap(templateParam, contextParam, dateRangeParams, codeParams));
        }

        List<SearchParameterMap> maps = new ArrayList<>();
        for (TokenOrListParam tolp : chunkedCodeParam.getValue()) {
            SearchParameterMap base = this.getBaseMap(templateParam, contextParam, dateRangeParams, codeParams);
            base.add(chunkedCodeParam.getKey(), tolp);
            maps.add(base);
        }

        return maps;
    }

    protected SearchParameterMap getBaseMap(
            Pair<String, IQueryParameterType> templateParam,
            Pair<String, IQueryParameterType> contextParam,
            List<Pair<String, DateRangeParam>> dateRangeParams,
            List<Pair<String, List<TokenOrListParam>>> codeParams) {
        SearchParameterMap baseMap = new SearchParameterMap();
        baseMap.setLastUpdated(new DateRangeParam());

        if (this.getPageSize() != null) {
            baseMap.setCount(pageSize);
        }

        if (templateParam != null) {
            baseMap.add(templateParam.getKey(), templateParam.getValue());
        }

        if (dateRangeParams != null) {
            for (Pair<String, DateRangeParam> drp : dateRangeParams) {
                baseMap.add(drp.getKey(), drp.getValue());
            }
        }

        if (codeParams != null) {
            for (Pair<String, List<TokenOrListParam>> cp : codeParams) {
                if (cp.getValue() == null
                        || cp.getValue().isEmpty()
                        || cp.getValue().size() > 1) {
                    // NOTE: Ignores "chunked" code parameters so they don't have to be removed
                    continue;
                }
                baseMap.add(cp.getKey(), cp.getValue().get(0));
            }
        }

        if (contextParam != null) {
            baseMap.add(contextParam.getKey(), contextParam.getValue());
        }

        return baseMap;
    }

    private static int getIterableSize(Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).size();
        }

        int counter = 0;
        for (Object i : iterable) {
            counter++;
        }
        return counter;
    }
}
