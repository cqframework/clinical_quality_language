package org.opencds.cqf.cql.engine.fhir.model;

import static org.junit.jupiter.api.Assertions.*;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.model.Model;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.elm_modelinfo.r1.ClassInfo;
import org.hl7.elm_modelinfo.r1.TypeInfo;
import org.hl7.fhir.dstu2.model.*;
import org.hl7.fhir.dstu2.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu2.model.Enumerations.AgeUnits;
import org.hl7.fhir.dstu2.model.Enumerations.BindingStrength;
import org.hl7.fhir.dstu2.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.dstu2.model.Enumerations.DataAbsentReason;
import org.hl7.fhir.dstu2.model.Enumerations.DataType;
import org.hl7.fhir.dstu2.model.Enumerations.DocumentReferenceStatus;
import org.hl7.fhir.dstu2.model.Enumerations.FHIRDefinedType;
import org.hl7.fhir.dstu2.model.Enumerations.MessageEvent;
import org.hl7.fhir.dstu2.model.Enumerations.NoteType;
import org.hl7.fhir.dstu2.model.Enumerations.RemittanceOutcome;
import org.hl7.fhir.dstu2.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu2.model.Enumerations.SearchParamType;
import org.hl7.fhir.dstu2.model.Enumerations.SpecialValues;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType;
import org.opencds.cqf.cql.engine.model.ModelResolver;

public class TestDstu2ModelResolver {

    // Couldn't find a way to automatically get the full list of enums.
    @SuppressWarnings("serial")
    private static List<Class<?>> enums = new ArrayList<Class<?>>() {
        {
            add(AdministrativeGender.class);
            add(AgeUnits.class);
            add(BindingStrength.class);
            add(ConceptMapEquivalence.class);
            add(DataAbsentReason.class);
            add(DataType.class);
            add(DocumentReferenceStatus.class);
            add(FHIRDefinedType.class);
            add(MessageEvent.class);
            add(NoteType.class);
            add(RemittanceOutcome.class);
            add(ResourceType.class);
            add(SearchParamType.class);
            add(SpecialValues.class);
        }
    };

    @Test
    void resolverThrowsExceptionForUnknownType() {
        ModelResolver resolver = new Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2));
        assertThrows(UnknownType.class, () -> {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp");
        });
    }

    @Test
    void resolveModelInfoTests() {
        ModelResolver resolver = new Dstu2FhirModelResolver();
        ModelManager mm = new ModelManager();
        Model m = mm.resolveModel(new ModelIdentifier("FHIR", null, "1.0.2"));

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
                        // TODO: HAPI Doesn't have a ResourceContainer type
                    case "FHIR.ResourceContainer":
                        continue;
                }

                resolver.resolveType(ci.getName());
            }
        }
    }

    @Test
    void resolveTypeTests() {
        ModelResolver resolver = new Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2));

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
    void createInstanceTests() {
        ModelResolver resolver = new Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2));

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

            Field enumFactory;
            try {
                enumFactory = instance.getClass().getDeclaredField("myEnumFactory");
                enumFactory.setAccessible(true);
                EnumFactory<?> factory = (EnumFactory<?>) enumFactory.get(instance);

                assertEquals(factory.getClass().getSimpleName().replace("EnumFactory", ""), enumType.getSimpleName());
            } catch (Exception e) {
                throw new AssertionError("error getting factory type. " + e.getMessage());
            }
        }
    }

    @Test
    void contextPathTests() {
        ModelResolver resolver = new Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2));

        String path = (String) resolver.getContextPath("Patient", "Patient");
        assertNotNull(path);
        assertEquals("id", path);

        path = (String) resolver.getContextPath(null, "Encounter");
        assertNull(path);

        // TODO: Consider making this an exception on the resolver because
        // if this happens it means something went wrong in the context.
        path = (String) resolver.getContextPath("Patient", null);
        assertNull(path);

        path = (String) resolver.getContextPath("Patient", "Condition");
        assertNotNull(path);
        assertEquals("patient", path);

        path = (String) resolver.getContextPath("Patient", "Appointment");
        assertNotNull(path);
        assertEquals("participant.actor", path);

        path = (String) resolver.getContextPath("Patient", "Observation");
        assertNotNull(path);
        assertEquals("subject", path);

        path = (String) resolver.getContextPath("Patient", "Encounter");
        assertNotNull(path);
        assertEquals("patient", path);

        path = (String) resolver.getContextPath("Patient", "MedicationStatement");
        assertEquals("patient", path);

        path = (String) resolver.getContextPath("Patient", "QuestionnaireResponse");
        assertEquals("subject", path);

        // Issue 527 - https://github.com/DBCG/cql_engine/issues/527
        path = (String) resolver.getContextPath("Unfiltered", "MedicationStatement");
        assertNull(path);

        path = (String) resolver.getContextPath("Unspecified", "MedicationStatement");
        assertNull(path);
    }

    // This is a serious failure that needs to be addressed. There's some sort of
    // mixup
    // between the dstu2 and hl7org dstu2 objects.
    // @Test
    public void resolveMissingPropertyReturnsNull() {
        ModelResolver resolver = new Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2));

        Patient p = new Patient();

        var value = resolver.resolvePath(p, "not-a-path");
        assertNull(value);
    }

    // @Test
    public void resolveNullEnumerationReturnsNull() {
        FhirModelResolver<Base, ?, ?, SimpleQuantity, ?, ?, ?, ?> resolver =
                new Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2));

        Quantity q = new Quantity();
        q.setValue(new BigDecimal("10.0"));
        q.setUnit("1");
        SimpleQuantity sq = resolver.castToSimpleQuantity(q);

        var value = resolver.resolvePath(sq, "comparator");
        assertNull(value);
    }

    // @Test
    public void resolveNullPrimitiveReturnsNull() {
        FhirModelResolver<Base, BaseDateTimeType, ?, ?, ?, ?, ?, ?> resolver =
                new Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2));

        DateTimeType dt = new DateTimeType();

        var value = resolver.resolvePath(dt, "value");
        assertNull(value);
    }
}
