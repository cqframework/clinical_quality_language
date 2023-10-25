package org.opencds.cqf.cql.engine.fhir.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.model.Model;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.elm_modelinfo.r1.ClassInfo;
import org.hl7.elm_modelinfo.r1.TypeInfo;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Enumerations.AbstractType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Enumerations.AgeUnits;
import org.hl7.fhir.r4.model.Enumerations.BindingStrength;
import org.hl7.fhir.r4.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.r4.model.Enumerations.DataAbsentReason;
import org.hl7.fhir.r4.model.Enumerations.DataType;
import org.hl7.fhir.r4.model.Enumerations.DefinitionResourceType;
import org.hl7.fhir.r4.model.Enumerations.DocumentReferenceStatus;
import org.hl7.fhir.r4.model.Enumerations.EventResourceType;
import org.hl7.fhir.r4.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.r4.model.Enumerations.FHIRDefinedType;
import org.hl7.fhir.r4.model.Enumerations.FHIRVersion;
import org.hl7.fhir.r4.model.Enumerations.KnowledgeResourceType;
import org.hl7.fhir.r4.model.Enumerations.MessageEvent;
import org.hl7.fhir.r4.model.Enumerations.NoteType;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Enumerations.RemittanceOutcome;
import org.hl7.fhir.r4.model.Enumerations.RequestResourceType;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Enumerations.SearchParamType;
import org.hl7.fhir.r4.model.Enumerations.SpecialValues;
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.testng.annotations.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;

public class TestR4ModelResolver {

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
            add(DefinitionResourceType.class);
            add(DocumentReferenceStatus.class);
            add(EventResourceType.class);
            add(FHIRAllTypes.class);
            add(FHIRDefinedType.class);
            add(FHIRVersion.class);
            add(KnowledgeResourceType.class);
            add(MessageEvent.class);
            add(NoteType.class);
            add(PublicationStatus.class);
            add(RemittanceOutcome.class);
            add(RequestResourceType.class);
            add(ResourceType.class);
            add(SearchParamType.class);
            add(SpecialValues.class);
        }
    };

    @Test(expectedExceptions = UnknownType.class)
    public void resolverThrowsExceptionForUnknownType()
    {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));
        resolver.resolveType("ImpossibleTypeThatDoesntExistAndShouldBlowUp");
    }

    @Test
    public void resolveTypeTests() {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

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

            resolver.resolveType(type.toCode());;
        }

        for (Class<?> enumType : enums) {
            resolver.resolveType(enumType.getSimpleName());
        }
    }

    @Test
    public void modelInfoSpecialCaseTests() {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        // This tests resolution of inner classes. They aren't registered directly.
        resolver.resolveType("TestScriptRequestMethodCode");
        resolver.resolveType("FHIRDeviceStatus");


        // This tests the special case handling of "Codes".
        resolver.resolveType("ImmunizationStatusCodes");

        // These have different capitalization conventions
        resolver.resolveType("status");
        resolver.resolveType("orientationType");
        resolver.resolveType("strandType");
        resolver.resolveType("sequenceType");


        // These are oddballs requiring manual mapping. They may represent errors in the ModelInfo.
        resolver.resolveType("ConfidentialityClassification");
        resolver.resolveType("ContractResourceStatusCodes");
        resolver.resolveType("EventStatus");
        resolver.resolveType("FinancialResourceStatusCodes");
        resolver.resolveType("SampledDataDataType");
        resolver.resolveType("ClaimProcessingCodes");
        resolver.resolveType("ContractResourcePublicationStatusCodes");


        // These are known glitches in the ModelInfo
        resolver.resolveType("vConfidentialityClassification");

        // This is a mapping for a value set that doesn't have a first-class enumeration
        resolver.resolveType("CurrencyCode");
        resolver.resolveType("MedicationAdministrationStatus");
        resolver.resolveType("MedicationDispenseStatus");
        resolver.resolveType("MedicationKnowledgeStatus");
        resolver.resolveType("Messageheader_Response_Request");
        resolver.resolveType("MimeType");
    }

     @Test
    public void modelInfo400Tests() {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));
        ModelManager mm = new ModelManager();
        Model m = mm.resolveModel(new ModelIdentifier().withId("FHIR").withVersion("4.0.1"));

        List<TypeInfo> typeInfos = m.getModelInfo().getTypeInfo();

        for (TypeInfo ti : typeInfos) {
            ClassInfo ci = (ClassInfo)ti;
            if (ci != null) {
                switch (ci.getBaseType()) {
                    // Abstract classes
                    case "FHIR.Element": continue;
                }

                switch (ci.getName()) {
                    // TODO: HAPI Doesn't have a ResourceContainer type
                    case "ResourceContainer": continue;
                }


                // TODO: The cause of failure for this is unknown.
                // Need to figure out if it's a gap in HAPI,
                // or if a manual mapping is required, or what.
                switch(ci.getName()) {
                    case "ItemInstance" : continue;
                }

                resolver.resolveType(ci.getName());
            }
        }
    }

    @Test
    public void modelInfo401Tests() throws Exception {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));
        ModelManager mm = new ModelManager();
        Model m = mm.resolveModel(new ModelIdentifier().withId("FHIR").withVersion("4.0.1"));

        List<TypeInfo> typeInfos = m.getModelInfo().getTypeInfo();

        for (TypeInfo ti : typeInfos) {
            ClassInfo ci = (ClassInfo)ti;
            if (ci != null) {

                switch (ci.getName()) {
                    // TODO: HAPI Doesn't have a ResourceContainer type
                    case "ResourceContainer": continue;
                    // Bugs in 4.0.1 model info
                    case "DataElement constraint on ElementDefinition data type": continue;
                    case "question": continue;
                    case "allowedUnits": continue;
                }

                // Also bugs in the 4.0.1 model info
                if (ci.getBaseType() == null) {
                    continue;
                }


                switch (ci.getBaseType()) {
                    // Abstract classes
                    case "FHIR.Element": continue;
                }

                // TODO: The cause of failure for this is unknown.
                // Need to figure out if it's a gap in HAPI,
                // or if a manual mapping is required, or what.
                switch(ci.getName()) {
                    case "ItemInstance" : continue;
                }

                resolver.resolveType(ci.getName());
            }
        }
    }

    @Test
    public void createInstanceTests() {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

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
            // For the enums we actually expect an Enumeration with a factory of the correct type to be created.
            Enumeration<?> instance = (Enumeration<?>)resolver.createInstance(enumType.getSimpleName());
            assertNotNull(instance);

            assertTrue(instance.getEnumFactory().getClass().getSimpleName().replace("EnumFactory", "").equals(enumType.getSimpleName()));
        }

        // These are some inner classes that don't appear in the enums above
        // This list is not exhaustive. It's meant as a spot check for the resolution code.
        Object instance = resolver.createInstance("TestScriptRequestMethodCode");
        assertNotNull(instance);

        instance = resolver.createInstance("FHIRDeviceStatus");
        assertNotNull(instance);
    }


    @Test
    public void contextPathTests() {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        String path = (String)resolver.getContextPath("Patient", "Patient");
        assertNotNull(path);
        assertTrue(path.equals("id"));

        path = (String)resolver.getContextPath(null, "Encounter");
        assertNull(path);

        // TODO: Consider making this an exception on the resolver because
        // if this happens it means something went wrong in the context.
        path = (String)resolver.getContextPath("Patient", null);
        assertNull(path);

        path = (String)resolver.getContextPath("Patient", "Condition");
        assertNotNull(path);
        assertEquals(path, "subject");

        path = (String)resolver.getContextPath("Patient", "Appointment");
        assertNotNull(path);
        assertEquals(path, "participant.actor");

        path = (String)resolver.getContextPath("Patient", "Account");
        assertNotNull(path);
        assertEquals(path, "subject");

        path = (String)resolver.getContextPath("Patient", "Encounter");
        assertNotNull(path);
        assertEquals(path, "subject");

        path = (String)resolver.getContextPath("Patient", "ValueSet");
        assertNull(path);

        path = (String)resolver.getContextPath("Patient", "MedicationStatement");
        assertEquals(path, "subject");

        path = (String)resolver.getContextPath("Patient", "Task");
        assertEquals(path, "for");


        path = (String)resolver.getContextPath("Patient", "Coverage");
        assertEquals(path, "beneficiary");

        // Issue 527 - https://github.com/DBCG/cql_engine/issues/527
        path = (String)resolver.getContextPath("Unfiltered", "MedicationStatement");
        assertNull(path);

        path = (String) resolver.getContextPath("Unspecified", "MedicationStatement");
        assertNull(path);
    }

    @Test
    public void resolveMissingPropertyReturnsNull() {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        Patient p = new Patient();

        Object result = resolver.resolvePath(p, "not-a-path");
        assertNull(result);
    }

    @Test
    public void resolveIdPropertyReturnsString() {
        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        Patient p = new Patient();
        p.setId("5");
        IdType idType = p.getIdElement();

        Object result = resolver.resolvePath(p, "id");
        assertNotNull(result);
        assertThat(result, is(idType));

        result = resolver.resolvePath(p, "id.value");
        assertNotNull(result);
        assertThat(result, is("5"));
    }

    @Test
    public void resolveDateTimeProviderReturnsDate() {

        ModelResolver resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        VisionPrescription vp = new VisionPrescription();
        Date time = new GregorianCalendar(1999, 3, 31).getTime();
        vp.setDateWritten(time);

        DateTimeType dateTimeType = vp.getDateWrittenElement();

        Object result = resolver.resolvePath(vp, "dateWritten");
        assertNotNull(result);
        assertThat(result, is(dateTimeType));
    }

    @Test
    public void resolveNullEnumerationReturnsNull() {
        FhirModelResolver<Base,?,?,SimpleQuantity,?,?,?,?> resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        Quantity q = new Quantity();
        q.setValue(new BigDecimal("10.0"));
        q.setUnit("1");
        SimpleQuantity sq = resolver.castToSimpleQuantity(q);

        Object result = resolver.resolvePath(sq, "comparator");
        assertNull(result);
    }

    @Test
    public void resolveNullPrimitiveReturnsNull() {
        FhirModelResolver<Base,BaseDateTimeType,?,?,?,?,?,?> resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        DateTimeType dt = new DateTimeType();

        Object result = resolver.resolvePath(dt, "value");
        assertNull(result);
    }

    @Test
    public void resolveIdPatient() {
        final String expectedId = "123";
        final FhirModelResolver<Base,BaseDateTimeType,?,?,?,?,?,?> resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        final Patient patient = new Patient();
        patient.setId(expectedId);

        assertEquals(resolver.resolveId(patient), expectedId);
    }

    @Test
    public void resolveIdProcedure() {
        final String expectedId = "456";
        final FhirModelResolver<Base,BaseDateTimeType,?,?,?,?,?,?> resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        final Procedure procedure = new Procedure();
        procedure.setId(expectedId);

        assertEquals(resolver.resolveId(procedure), expectedId);
    }

    @Test
    public void resolveIdStringReturnsNull() {
        final FhirModelResolver<Base,BaseDateTimeType,?,?,?,?,?,?> resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        assertNull(resolver.resolveId(new Date()));
    }

    @Test
    public void resolveIdStringTypeReturnsNull() {
        final FhirModelResolver<Base,BaseDateTimeType,?,?,?,?,?,?> resolver = new R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4));

        assertNull(resolver.resolveId(new StringType()));
    }
}
