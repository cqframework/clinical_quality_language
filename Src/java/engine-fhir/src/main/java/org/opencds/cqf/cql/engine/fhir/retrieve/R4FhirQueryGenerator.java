package org.opencds.cqf.cql.engine.fhir.retrieve;

import java.net.URLDecoder;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DataRequirement;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Type;
import org.opencds.cqf.cql.engine.elm.execution.SubtractEvaluator;
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;


public class R4FhirQueryGenerator extends BaseFhirQueryGenerator {
    public R4FhirQueryGenerator(SearchParameterResolver searchParameterResolver, TerminologyProvider terminologyProvider,
                                ModelResolver modelResolver) throws FhirVersionMisMatchException {
        super(searchParameterResolver, terminologyProvider, modelResolver, searchParameterResolver.getFhirContext());
    }

    @Override
    public FhirVersionEnum getFhirVersion() {
        return FhirVersionEnum.R4;
    }

    @Override
    public List<String> generateFhirQueries(ICompositeType dreq, DateTime evaluationDateTime, Map<String, Object> contextValues, Map<String, Object> parameters, IBaseConformance capStatement) {
        if (!(dreq instanceof DataRequirement)) {
            throw new IllegalArgumentException("dataRequirement argument must be a DataRequirement");
        }
        if (capStatement != null && !(capStatement instanceof CapabilityStatement)) {
            throw new IllegalArgumentException("capabilityStatement argument must be a CapabilityStatement");
        }

        DataRequirement dataRequirement = (DataRequirement)dreq;

        List<String> queries = new ArrayList<>();

        List<CodeFilter> codeFilters = new ArrayList<CodeFilter>();
        if (dataRequirement.hasCodeFilter()) {
            for (DataRequirement.DataRequirementCodeFilterComponent codeFilterComponent : dataRequirement.getCodeFilter()) {
                if (!codeFilterComponent.hasPath()) continue;

                String codePath = null;
                List<Code> codes = null;
                String valueSet = null;

                codePath = codeFilterComponent.getPath();

                if (codeFilterComponent.hasValueSetElement()) {
                    valueSet = codeFilterComponent.getValueSet();
                }

                if (codeFilterComponent.hasCode()) {
                    codes = new ArrayList<Code>();

                    List<Coding> codeFilterValueCodings = codeFilterComponent.getCode();
                    for (Coding coding : codeFilterValueCodings) {
                        if (coding.hasCode()) {
                            Code code = new Code();
                            code.setSystem(coding.getSystem());
                            code.setCode(coding.getCode());
                            codes.add(code);
                        }
                    }
                }

                codeFilters.add(new CodeFilter(codePath, codes, valueSet));
            }
        }

        List<DateFilter> dateFilters = new ArrayList<DateFilter>();

        if (dataRequirement.hasDateFilter()) {
            for (DataRequirement.DataRequirementDateFilterComponent dateFilterComponent : dataRequirement.getDateFilter()) {
                String datePath = null;
                String dateLowPath = null;
                String dateHighPath = null;
                Interval dateRange = null;

                if (dateFilterComponent.hasPath() && dateFilterComponent.hasSearchParam()) {
                    throw new UnsupportedOperationException("Either a path or a searchParam must be provided, but not both");
                }

                if (dateFilterComponent.hasPath()) {
                    datePath = dateFilterComponent.getPath();
                } else if (dateFilterComponent.hasSearchParam()) {
                    datePath = dateFilterComponent.getSearchParam();
                }

                // TODO: Deal with the case that the value is expressed as an expression extension
                if (dateFilterComponent.hasValue()) {
                    Type dateFilterValue = dateFilterComponent.getValue();
                    if (dateFilterValue instanceof DateTimeType && dateFilterValue.hasPrimitiveValue()) {
                        dateLowPath = "valueDateTime";
                        dateHighPath = "valueDateTime";
                        String offsetDateTimeString = ((DateTimeType)dateFilterValue).getValueAsString();
                        DateTime dateTime = new DateTime(OffsetDateTime.parse(offsetDateTimeString));

                        dateRange = new Interval(dateTime, true, dateTime, true);
                    } else if (dateFilterValue instanceof Duration && ((Duration)dateFilterValue).hasValue()) {
                        // If a Duration is specified, the filter will return only those data items that fall within Duration before now.
                        Duration dateFilterAsDuration = (Duration)dateFilterValue;

                        org.opencds.cqf.cql.engine.runtime.Quantity dateFilterDurationAsCQLQuantity =
                            new org.opencds.cqf.cql.engine.runtime.Quantity().withValue(dateFilterAsDuration.getValue()).withUnit(dateFilterAsDuration.getUnit());

                        DateTime diff = ((DateTime)SubtractEvaluator.subtract(evaluationDateTime, dateFilterDurationAsCQLQuantity));

                        dateRange = new Interval(diff, true, evaluationDateTime, true);
                    } else if (dateFilterValue instanceof Period && ((Period)dateFilterValue).hasStart() && ((Period)dateFilterValue).hasEnd()) {
                        dateLowPath = "valueDateTime";
                        dateHighPath = "valueDateTime";
                        dateRange = new Interval(((Period)dateFilterValue).getStart(), true, ((Period)dateFilterValue).getEnd(), true);
                    }

                    dateFilters.add(new DateFilter(datePath, dateLowPath, dateHighPath, dateRange));
                }
            }
        }

        // Patient is the default context if one is not specified
        String contextType = "Patient";

        if (dataRequirement.hasSubjectReference()) {
            throw new IllegalArgumentException("Cannot process data requirements with subjects specified as references");
        }

        if (dataRequirement.hasSubjectCodeableConcept()) {
            Coding c = null;
            for (Coding coding : dataRequirement.getSubjectCodeableConcept().getCoding()) {
                if ("http://hl7.org/fhir/resource-types".equals(coding.getSystem())) {
                    c = coding;
                    break;
                }
            }
            if (c == null) {
                throw new IllegalArgumentException("Cannot process data requirements with subjects not specified using a code from http://hl7.org/fhir/resource-types");
            }
            contextType = c.getCode();
        }

        Object contextPath = modelResolver.getContextPath(contextType, dataRequirement.getType());
        Object contextValue = contextValues.get(contextType);
        String templateId = dataRequirement.getProfile() != null && !dataRequirement.getProfile().isEmpty()
            ? dataRequirement.getProfile().get(0).getValue()
            : null;

            List<SearchParameterMap> maps = setupQueries(contextType, (String)contextPath, contextValue, dataRequirement.getType(), templateId,
            codeFilters, dateFilters);

        for (SearchParameterMap map : maps) {
            String query = null;
            try {
                query = URLDecoder.decode(map.toNormalizedQueryString(fhirContext), "UTF-8");
            } catch (Exception ex) {
                query = map.toNormalizedQueryString(fhirContext);
            }
            queries.add(dataRequirement.getType() + query);
        }

        return queries;
    }
}