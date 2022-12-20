package org.opencds.cqf.cql.engine.fhir.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.model.Model;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.elm_modelinfo.r1.ClassInfo;
import org.hl7.elm_modelinfo.r1.TypeInfo;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.BaseDateTimeType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Enumeration;
import org.hl7.fhir.dstu3.model.Enumerations.AbstractType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Enumerations.AgeUnits;
import org.hl7.fhir.dstu3.model.Enumerations.BindingStrength;
import org.hl7.fhir.dstu3.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.dstu3.model.Enumerations.DataAbsentReason;
import org.hl7.fhir.dstu3.model.Enumerations.DataType;
import org.hl7.fhir.dstu3.model.Enumerations.DocumentReferenceStatus;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRDefinedType;
import org.hl7.fhir.dstu3.model.Enumerations.MessageEvent;
import org.hl7.fhir.dstu3.model.Enumerations.NoteType;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Enumerations.RemittanceOutcome;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu3.model.Enumerations.SearchParamType;
import org.hl7.fhir.dstu3.model.Enumerations.SpecialValues;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.testng.annotations.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class TestDstu3ModelResolver {

    // Couldn't find a way to automatically get the full list of enums.
    @SuppressWarnings("serial")
    private static List<Class<?>> enums = new ArrayList<Class<?>>() {
        {
            add(AbstractType.class);
            add(AdministrativeGender.class);
            add(AgeUnits.class);
            add(BindingStrength.class);
            add(ConceptMapEquivalence.class);
            add(DataAbsentReason.class);
            add(DataType.class);
            add(DocumentReferenceStatus.class);
            add(FHIRAllTypes.class);
            add(FHIRDefinedType.class);
            add(MessageEvent.class);
            add(NoteType.class);
            add(PublicationStatus.class);
            add(RemittanceOutcome.class);
            add(ResourceType.class);
            add(SearchParamType.class);
            add(SpecialValues.class);
        }
    };

    @Test(expectedExceptions = UnknownType.class)
    public void resolverThrowsExceptionForUnknownType() {
        ModelResolver resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));
        resolver.resolveType("ImpossibleTypeThatDoesntExistAndShouldBlowUp");
    }

    @Test
    // This tests all the top-level types HAPI knows about.
    public void resolveTypeTests() {
        ModelResolver resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        for (DataType type : DataType.values()) {
            // These are abstract types that should never be resolved directly.
            switch (type) {
                case ELEMENT:
                case NULL:
                    continue;
                default:
            }

            resolver.resolveType(type.toCode());
        }

        for (ResourceType type : ResourceType.values()) {
            // These are abstract types that should never be resolved directly.
            switch (type) {
                case DOMAINRESOURCE:
                case RESOURCE:
                case NULL:
                    continue;
                default:
            }

            resolver.resolveType(type.toCode());
        }

        for (Class<?> enumType : enums) {
            resolver.resolveType(enumType.getSimpleName());
        }
    }

    @Test
    // This tests all the types that are present in the ModelInfo.
    public void resolveModelInfoTests() {
        ModelResolver resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));
        ModelManager mm = new ModelManager();
        Model m = mm.resolveModel(new ModelIdentifier().withId("FHIR").withVersion("3.0.0"));

        List<TypeInfo> typeInfos = m.getModelInfo().getTypeInfo();

        for (TypeInfo ti : typeInfos) {
            ClassInfo ci = (ClassInfo) ti;
            if (ci != null) {
                switch (ci.getBaseType()) {
                    // Abstract classes
                    case "FHIR.Element":
                        continue;
                }

                switch (ci.getName()) {
                    // TODO: HAPI Doesn't have a ResourceContainer type for Dstu3
                    case "ResourceContainer":
                        continue;
                }

                resolver.resolveType(ci.getName());
            }
        }
    }

    // This tests special case logic in the Model Resolver.
    // Ideally, these would all disappear with either registering custom types
    // on the FhirContext or generalized logic, or fixed-up ModelInfos
    @Test
    public void modelInfoSpecialCaseTests() {
        ModelResolver resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        // This tests resolution of inner classes. They aren't registered directly.
        resolver.resolveType("TestScriptRequestMethodCode");
        resolver.resolveType("FHIRDeviceStatus");

        // This tests the special case handling of "Codes".
        resolver.resolveType("ImmunizationStatusCodes");
        resolver.resolveType("ConditionClinicalStatusCodes");

        // These are oddballs requiring manual mapping.
        resolver.resolveType("ConfidentialityClassification");
        resolver.resolveType("ContractResourceStatusCodes");
        resolver.resolveType("EventStatus");
        resolver.resolveType("qualityType");
        resolver.resolveType("FinancialResourceStatusCodes");
        resolver.resolveType("repositoryType");
        resolver.resolveType("SampledDataDataType");
    }

    @Test
    public void createInstanceTests() {
        ModelResolver resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        for (DataType type : DataType.values()) {
            // These are abstract types that should never be resolved directly.
            switch (type) {
                case BACKBONEELEMENT:
                case ELEMENT:
                case NULL:
                    continue;
                default:
            }

            Object instance = resolver.createInstance(type.toCode());

            assertNotNull(instance);
        }

        for (ResourceType type : ResourceType.values()) {
            // These are abstract types that should never be resolved directly.
            switch (type) {
                case DOMAINRESOURCE:
                case RESOURCE:
                case NULL:
                    continue;
                default:
            }

            Object instance = resolver.createInstance(type.toCode());

            assertNotNull(instance);
        }

        for (Class<?> enumType : enums) {
            // For the enums we actually expect an Enumeration with a factory of the correct
            // type to be created.
            Enumeration<?> instance = (Enumeration<?>) resolver.createInstance(enumType.getSimpleName());
            assertNotNull(instance);

            assertTrue(instance.getEnumFactory().getClass().getSimpleName().replace("EnumFactory", "")
                    .equals(enumType.getSimpleName()));
        }

        // These are some inner classes that don't appear in the enums above
        // This list is not exhaustive. It's meant as a spot check for the resolution
        // code.
        Object instance = resolver.createInstance("TestScriptRequestMethodCode");
        assertNotNull(instance);

        instance = resolver.createInstance("FHIRDeviceStatus");
        assertNotNull(instance);
    }

    @Test
    public void contextPathTests() {
        ModelResolver resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        String path = (String) resolver.getContextPath("Patient", "Patient");
        assertNotNull(path);
        assertEquals(path, "id");

        path = (String) resolver.getContextPath(null, "Encounter");
        assertNull(path);

        // TODO: Consider making this an exception on the resolver because
        // if this happens it means something went wrong in the context.
        path = (String) resolver.getContextPath("Patient", null);
        assertNull(path);

        path = (String) resolver.getContextPath("Patient", "Condition");
        assertNotNull(path);
        assertEquals(path, "subject");

        path = (String) resolver.getContextPath("Patient", "Appointment");
        assertNotNull(path);
        assertEquals(path, "participant.actor");

        path = (String) resolver.getContextPath("Patient", "Account");
        assertNotNull(path);
        assertEquals(path, "subject");

        path = (String) resolver.getContextPath("Patient", "Encounter");
        assertNotNull(path);
        assertEquals(path, "subject");

        path = (String) resolver.getContextPath("Patient", "MedicationStatement");
        assertEquals(path, "subject");

        path = (String) resolver.getContextPath("Patient", "Task");
        assertEquals(path, "for");

        path = (String)resolver.getContextPath("Patient", "Coverage");
        assertEquals(path, "beneficiary");

        // Issue 527 - https://github.com/DBCG/cql_engine/issues/527
        path = (String) resolver.getContextPath("Unfiltered", "MedicationStatement");
        assertNull(path);

        path = (String) resolver.getContextPath("Unspecified", "MedicationStatement");
        assertNull(path);
    }

    @Test
    public void resolveMissingPropertyReturnsNull() {
        ModelResolver resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        Patient p = new Patient();

        Object result = resolver.resolvePath(p, "not-a-path");
        assertNull(result);
    }

    @Test
    public void resolveNullEnumerationReturnsNull() {
        FhirModelResolver<Base,?,?,SimpleQuantity,?,?,?,?> resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        Quantity q = new Quantity();
        q.setValue(new BigDecimal("10.0"));
        q.setUnit("1");
        SimpleQuantity sq = resolver.castToSimpleQuantity(q);

        Object result = resolver.resolvePath(sq, "comparator");
        assertNull(result);
    }

    @Test
    public void resolveNullPrimitiveReturnsNull() {
        FhirModelResolver<Base,BaseDateTimeType,?,?,?,?,?,?> resolver = new Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3));

        DateTimeType dt = new DateTimeType();

        Object result = resolver.resolvePath(dt, "value");
        assertNull(result);
    }
}
