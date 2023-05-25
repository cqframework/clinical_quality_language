package org.opencds.cqf.cql.engine.fhir.retrieve;

import java.net.URLDecoder;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.hl7.fhir.dstu3.model.CapabilityStatement;
import org.hl7.fhir.dstu3.model.CodeType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.opencds.cqf.cql.engine.elm.visiting.SubtractEvaluator;
import org.opencds.cqf.cql.engine.fhir.exception.FhirVersionMisMatchException;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterMap;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;


public class Dstu3FhirQueryGenerator extends BaseFhirQueryGenerator {
    public Dstu3FhirQueryGenerator(SearchParameterResolver searchParameterResolver, TerminologyProvider terminologyProvider, ModelResolver modelResolver) throws FhirVersionMisMatchException {
        super(searchParameterResolver, terminologyProvider, modelResolver, searchParameterResolver.getFhirContext());
    }

    @Override
    public FhirVersionEnum getFhirVersion() {
        return FhirVersionEnum.DSTU3;
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
            for (org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent codeFilterComponent : dataRequirement.getCodeFilter()) {
                if (!codeFilterComponent.hasPath()) {
                    continue;
                }
                String codePath = null;
                List<Code> codes = null;
                String valueSet = null;


                codePath = codeFilterComponent.getPath();

                // TODO: What to do if/when System is not provided...
                if (codeFilterComponent.hasValueCode()) {
                    codes = new ArrayList<>();
                    List<org.hl7.fhir.dstu3.model.CodeType> codeFilterValueCode = codeFilterComponent.getValueCode();
                    for (CodeType codeType : codeFilterValueCode) {
                        Code code = new Code();
                        code.setCode(codeType.asStringValue());
                        codes.add(code);
                    }
                }
                if (codeFilterComponent.hasValueCoding()) {
                    codes = new ArrayList<>();

                    List<org.hl7.fhir.dstu3.model.Coding> codeFilterValueCodings = codeFilterComponent.getValueCoding();
                    for (Coding coding : codeFilterValueCodings) {
                        if (coding.hasCode()) {
                            Code code = new Code();
                            code.setSystem(coding.getSystem());
                            code.setCode(coding.getCode());
                            codes.add(code);
                        }
                    }
                }
                if (codeFilterComponent.hasValueCodeableConcept()) {
                    List<org.hl7.fhir.dstu3.model.CodeableConcept> codeFilterValueCodeableConcepts = codeFilterComponent.getValueCodeableConcept();
                    for (CodeableConcept codeableConcept : codeFilterValueCodeableConcepts) {
                        List<org.hl7.fhir.dstu3.model.Coding> codeFilterValueCodeableConceptCodings = codeableConcept.getCoding();
                        for (Coding coding : codeFilterValueCodeableConceptCodings) {
                            if (coding.hasCode()) {
                                Code code = new Code();
                                code.setSystem(coding.getSystem());
                                code.setCode(coding.getCode());
                                codes.add(code);
                            }
                        }
                    }
                }
                if (codeFilterComponent.hasValueSet()) {
                    if (codeFilterComponent.getValueSetReference().getReference() instanceof String) {
                        valueSet = ((Reference)codeFilterComponent.getValueSet()).getReference();
                    } else if (codeFilterComponent.getValueSetReference() instanceof Reference) {
                        valueSet = codeFilterComponent.getValueSetReference().getReference();
                    }
                }

                codeFilters.add(new CodeFilter(codePath, codes, valueSet));
            }
        }

        List<DateFilter> dateFilters = new ArrayList<DateFilter>();

        if (dataRequirement.hasDateFilter()) {
            for (org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementDateFilterComponent dateFilterComponent : dataRequirement.getDateFilter()) {
                if (!dateFilterComponent.hasPath()) {
                    throw new UnsupportedOperationException("A path must be provided");
                }

                String datePath = null;
                String dateLowPath = null;
                String dateHighPath = null;
                Interval dateRange = null;

                datePath = dateFilterComponent.getPath();

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

                        DateTime diff = ((DateTime) SubtractEvaluator.subtract(evaluationDateTime, dateFilterDurationAsCQLQuantity));

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

        // STU3 only supported Patient context
        String contextType = "Patient";
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