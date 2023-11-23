package org.opencds.cqf.cql.engine.fhir.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Age;
import org.hl7.fhir.r4.model.AnnotatedUuidType;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.Count;
import org.hl7.fhir.r4.model.Distance;
import org.hl7.fhir.r4.model.Duration;
import org.hl7.fhir.r4.model.EnumFactory;
import org.hl7.fhir.r4.model.Enumeration;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.MoneyQuantity;
import org.hl7.fhir.r4.model.OidType;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.UnsignedIntType;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.UuidType;
import org.opencds.cqf.cql.engine.exception.InvalidCast;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class R4FhirModelResolver extends FhirModelResolver<Base, BaseDateTimeType, TimeType, SimpleQuantity, IdType, Resource, Enumeration<?>, EnumFactory<?>> {

	public R4FhirModelResolver() {
        // This ModelResolver makes specific alterations to the FhirContext,
        // so it's unable to use a cached version.
        this(FhirContext.forR4());
	}

    protected R4FhirModelResolver(FhirContext fhirContext) {
        super(fhirContext);
        this.setPackageNames(Arrays.asList("org.hl7.fhir.r4.model"));
        if (fhirContext.getVersion().getVersion() != FhirVersionEnum.R4) {
            throw new IllegalArgumentException("The supplied context is not configured for R4");
        }
    }

    @SuppressWarnings("unchecked")
    protected void initialize() {
        // HAPI has some bugs where it's missing annotations on certain types. This patches that.
        this.fhirContext.registerCustomType(AnnotatedUuidType.class);

        // The context loads Resources on demand which can cause resolution to fail in certain cases
        // This forces all Resource types to be loaded.

        // force calling of validateInitialized();
        this.fhirContext.getResourceDefinition(Enumerations.ResourceType.ACCOUNT.toCode());

        Map<String, Class<? extends IBaseResource>> myNameToResourceType;
        try {
            Field f = this.fhirContext.getClass().getDeclaredField("myNameToResourceType");
            f.setAccessible(true);
            myNameToResourceType = (Map<String, Class<? extends IBaseResource>>) f.get(this.fhirContext);

            List<Class<? extends IBaseResource>> toLoad = new ArrayList<Class<? extends IBaseResource>>(myNameToResourceType.size());

            for (Enumerations.ResourceType type : Enumerations.ResourceType.values()) {
                // These are abstract types that should never be resolved directly.
                switch (type) {
                    case DOMAINRESOURCE:
                    case RESOURCE:
                    case NULL:
                        continue;
                    default:
                }
                if (myNameToResourceType.containsKey(type.toCode().toLowerCase()))
                    toLoad.add(myNameToResourceType.get(type.toCode().toLowerCase()));
            }

            // Sends a list of all classes to be loaded in bulk.
            Method m = this.fhirContext.getClass().getDeclaredMethod("scanResourceTypes", Collection.class);
            m.setAccessible(true);
            m.invoke(this.fhirContext, toLoad);
        } catch (Exception e) {
            // intentionally ignored
        }
    }

    @Override
    protected Object resolveProperty(Object target, String path) {
        // This is kind of a hack to get around contained resources - HAPI doesn't have ResourceContainer type for STU3
        if (target instanceof Resource && ((Resource) target).fhirType().equals(path))
        {
            return target;
        }

        return super.resolveProperty(target, path);
    }

    protected Boolean equalsDeep(Base left, Base right) {
        return left.equalsDeep(right);
    }

    protected SimpleQuantity castToSimpleQuantity(Base base) {
        return base.castToSimpleQuantity(base);
    }

    protected Calendar getCalendar(BaseDateTimeType dateTime) {
        return dateTime.getValueAsCalendar();
    }

    protected Integer getCalendarConstant(BaseDateTimeType dateTime) {
        return dateTime.getPrecision().getCalendarConstant();
    }

    protected void setCalendarConstant(BaseDateTimeType dateTime, BaseTemporal temporal) {
        dateTime.setPrecision(toTemporalPrecisionEnum(temporal.getPrecision()));
    }

    protected String timeToString(TimeType time) {
        return time.getValue();
    }

    protected String idToString(IdType id) {
        return id.getIdPart();
    }

    protected String getResourceType(Resource resource) {
        return resource.fhirType();
    }

    protected Enumeration<?> enumConstructor(EnumFactory<?> factory) {
        return new Enumeration<>(factory);
    }

    protected Boolean enumChecker(Object object) {
        return object instanceof Enumeration;
    }

    protected Class<?> enumFactoryTypeGetter(Enumeration<?> enumeration) {
        return enumeration.getEnumFactory().getClass();
    }

    @Override
    public Class<?> resolveType(String typeName) {

        // TODO: Might be able to patch some of these by registering custom types in HAPI.
        switch(typeName) {
            case "ConfidentialityClassification": typeName = "Composition$DocumentConfidentiality"; break;
            case "ContractResourceStatusCodes": typeName = "Contract$ContractStatus"; break;
            case "EventStatus": typeName = "Procedure$ProcedureStatus"; break;
            case "FinancialResourceStatusCodes": typeName = "ClaimResponse$ClaimResponseStatus"; break;
            case "SampledDataDataType": typeName = "StringType"; break;
            case "ClaimProcessingCodes": typeName = "ClaimResponse$RemittanceOutcome"; break;
            case "vConfidentialityClassification": typeName = "Composition$DocumentConfidentiality"; break;
            case "ContractResourcePublicationStatusCodes": typeName = "Contract$ContractPublicationStatus"; break;
            // CodeTypes - Bug in HAPI 4.2
            case "CurrencyCode" : typeName = "CodeType"; break;
            case "MedicationAdministrationStatus": typeName = "CodeType"; break;
            case "MedicationDispenseStatus": typeName = "CodeType"; break;
            case "MedicationKnowledgeStatus": typeName = "CodeType"; break;
            case "Messageheader_Response_Request": typeName = "CodeType"; break;
            case "MimeType": typeName = "CodeType"; break;
            default: break;
        }


        return super.resolveType(typeName);
    }

    /*
    Casting of derived primitives:
    Datatypes that derive from datatypes other than Element are actually profiles
    // Types that exhibit this behavior are:
    // url: uri
    // canonical: uri
    // uuid: uri
    // oid: uri
    // positiveInt: integer
    // unsignedInt: integer
    // code: string
    // markdown: string
    // id: string

     */

    @Override
    public Boolean is(Object value, Class<?> type) {
        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return true;
        }

        // TODO: These should really be using profile validation
        if (value instanceof UriType) {
            switch (type.getSimpleName()) {
                case "UrlType": return true;
                case "CanonicalType": return true;
                case "AnnotatedUuidType":
                case "UuidType": return true;
                case "OidType": return true;
                default: break;
            }
        }

        if (value instanceof IntegerType) {
            switch (type.getSimpleName()) {
                case "PositiveIntType": return true;
                case "UnsignedIntType": return true;
                default: break;
            }
        }

        if (value instanceof StringType) {
            switch (type.getSimpleName()) {
                case "CodeType": return true;
                case "MarkdownType": return true;
                case "IdType": return true;
                default: break;
            }
        }

        if (value instanceof Quantity) {
            switch (type.getSimpleName()) {
                case "Age":
                case "Distance":
                case "Duration":
                case "Count":
                case "SimpleQuantity":
                case "MoneyQuantity": return true;
                default: break;
            }
        }

        return false;
    }

    @Override
    public Object as(Object value, Class<?> type, boolean isStrict) {
        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (value instanceof UriType) {
            UriType uriType = (UriType)value;
            switch (type.getSimpleName()) {
                case "UrlType": return uriType.castToUrl(uriType);
                case "CanonicalType": return uriType.castToCanonical(uriType);
                case "AnnotatedUuidType":
                case "UuidType": return uriType.hasPrimitiveValue() && uriType.getValue().startsWith("urn:uuid:") ? new UuidType(uriType.primitiveValue()) : null;
                case "OidType": return uriType.hasPrimitiveValue() && uriType.getValue().startsWith("urn:oid:") ? new OidType(uriType.primitiveValue()) : null; // castToOid(uriType); Throws an exception, not implemented
                default: break;
            }
        }

        if (value instanceof IntegerType) {
            IntegerType integerType = (IntegerType)value;
            switch (type.getSimpleName()) {
                case "PositiveIntType": return integerType.hasPrimitiveValue() && integerType.getValue() > 0 ? new PositiveIntType(integerType.primitiveValue()) : null; // integerType.castToPositiveInt(integerType); Throws an exception, not implemented
                case "UnsignedIntType": return integerType.hasPrimitiveValue() && integerType.getValue() >= 0 ? new UnsignedIntType(integerType.primitiveValue()) : null; // castToUnsignedInt(integerType); Throws an exception, not implemented
                default: break;
            }
        }

        if (value instanceof StringType) {
            StringType stringType = (StringType)value;
            switch (type.getSimpleName()) {
                case "CodeType": return stringType.castToCode(stringType);
                case "MarkdownType": return stringType.castToMarkdown(stringType);
                case "IdType": return stringType.hasPrimitiveValue() ? new IdType(stringType.primitiveValue()) : null; // stringType.castToId(stringType); Throws an exception, not implemented
                default: break;
            }
        }

        if (value instanceof Quantity) {
            Quantity quantity = (Quantity)value;
            switch (type.getSimpleName()) {
                case "Age":
                    Age age = new Age();
                    age.setValue(quantity.getValue());
                    age.setCode(quantity.getCode());
                    // TODO: Ensure age constraints are met, else return null
                    return age;
                case "Distance":
                    Distance distance = new Distance();
                    distance.setValue(quantity.getValue());
                    distance.setCode(quantity.getCode());
                    // TODO: Ensure distance constraints are met, else return null
                    return distance;
                case "Duration":
                    Duration duration = new Duration();
                    duration.setValue(quantity.getValue());
                    duration.setCode(quantity.getCode());
                    // TODO: Ensure duration constraints are met, else return null
                    return duration;
                case "Count":
                    Count count = new Count();
                    count.setValue(quantity.getValue());
                    count.setCode(quantity.getCode());
                    // TODO: Ensure count constraints are met, else return null
                    return count;
                case "SimpleQuantity": return quantity.castToSimpleQuantity(quantity); // NOTE: This is wrong in that it is copying the comparator, it should be ensuring comparator is not set...
                case "MoneyQuantity":
                    MoneyQuantity moneyQuantity = new MoneyQuantity();
                    moneyQuantity.setValue(quantity.getValue());
                    moneyQuantity.setCode(quantity.getCode());
                    // TODO: Ensure money constraints are met, else return null
                    return moneyQuantity;
                default: break;
            }
        }

        if (isStrict) {
            throw new InvalidCast(String.format("Cannot cast a value of type %s as %s.", value.getClass().getName(), type.getName()));
        }

        return null;
    }

    @Override
    public Object getContextPath(String contextType, String targetType) {
        if (targetType == null || contextType == null ) {
            return null;
        }

        if (contextType.equals("Patient") && targetType.equals("MedicationStatement")) {
            return "subject";
        }

        if (contextType.equals("Patient") && targetType.equals("Task")) {
            return "for";
        }

        if (contextType.equals("Patient") && targetType.equals("Coverage")) {
            return "beneficiary";
        }


        return super.getContextPath(contextType, targetType);
    }
}