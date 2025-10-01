package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.math.BigDecimal
import java.text.ParseException
import java.util.*
import org.apache.commons.lang3.time.DateUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.fhir.r5.model.Base
import org.hl7.fhir.r5.model.BaseDateTimeType
import org.hl7.fhir.r5.model.DateTimeType
import org.hl7.fhir.r5.model.DateType
import org.hl7.fhir.r5.model.Enumeration
import org.hl7.fhir.r5.model.Enumerations
import org.hl7.fhir.r5.model.Enumerations.FHIRTypes
import org.hl7.fhir.r5.model.Extension
import org.hl7.fhir.r5.model.Patient
import org.hl7.fhir.r5.model.Quantity
import org.hl7.fhir.r5.model.SimpleQuantity
import org.hl7.fhir.r5.model.VisionPrescription
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.model.ModelResolver

internal class TestR5ModelResolver {
    @Test
    fun resolverThrowsExceptionForUnknownType() {
        val resolver: ModelResolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        Assertions.assertThrows(UnknownType::class.java) {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp")
        }
    }

    @Test
    fun resolveTypeTests() {
        val resolver: ModelResolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        for (type in FHIRTypes.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                FHIRTypes.DOMAINRESOURCE,
                FHIRTypes.RESOURCE,
                FHIRTypes.NULL,
                FHIRTypes.BASE -> continue
                else -> {}
            }

            resolver.resolveType(type.toCode())
        }

        for (enumType in enums) {
            resolver.resolveType(enumType.getSimpleName())
        }
    }

    // TODO: Replace with R5 special cases once that's available
    // @Test
    // public void modelInfoSpecialCaseTests() {
    //     ModelResolver resolver = new
    // R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5));
    //     // This tests resolution of inner classes. They aren't registered directly.
    //     resolver.resolveType("TestScriptRequestMethodCode");
    //     resolver.resolveType("FHIRDeviceStatus");
    //     // This tests the special case handling of "Codes".
    //     resolver.resolveType("ImmunizationStatusCodes");
    //     // These have different capitalization conventions
    //     resolver.resolveType("status");
    //     resolver.resolveType("orientationType");
    //     resolver.resolveType("strandType");
    //     resolver.resolveType("sequenceType");
    //     // These are oddballs requiring manual mapping. They may represent errors in the
    // ModelInfo.
    //     resolver.resolveType("ConfidentialityClassification");
    //     resolver.resolveType("ContractResourceStatusCodes");
    //     resolver.resolveType("EventStatus");
    //     resolver.resolveType("FinancialResourceStatusCodes");
    //     resolver.resolveType("SampledDataDataType");
    //     resolver.resolveType("ClaimProcessingCodes");
    //     resolver.resolveType("ContractResourcePublicationStatusCodes");
    //     // These are known glitches in the ModelInfo
    //     resolver.resolveType("vConfidentialityClassification");
    //     // This is a mapping for a value set that doesn't have a first-class enumeration
    //     resolver.resolveType("CurrencyCode");
    //     resolver.resolveType("MedicationAdministrationStatus");
    //     resolver.resolveType("MedicationDispenseStatus");
    //     resolver.resolveType("MedicationKnowledgeStatus");
    //     resolver.resolveType("Messageheader_Response_Request");
    //     resolver.resolveType("MimeType");
    // }
    // TODO: Replace with 5.0.0 model info once that's available
    // @Test
    // public void modelInfo401Tests() throws Exception {
    //     ModelResolver resolver = new
    // R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5));
    //     ModelManager mm = new ModelManager();
    //     Model m = mm.resolveModel(new ModelIdentifier().withId("FHIR").withVersion("4.0.1"));
    //     List<TypeInfo> typeInfos = m.getModelInfo().getTypeInfo();
    //     for (TypeInfo ti : typeInfos) {
    //         ClassInfo ci = (ClassInfo)ti;
    //         if (ci != null) {
    //             switch (ci.getName()) {
    //                 // TODO: HAPI Doesn't have a ResourceContainer type
    //                 case "ResourceContainer": continue;
    //                 // Bugs in 4.0.1 model info
    //                 case "DataElement constraint on ElementDefinition data type": continue;
    //                 case "question": continue;
    //                 case "allowedUnits": continue;
    //             }
    //             // Also bugs in the 4.0.1 model info
    //             if (ci.getBaseType() == null) {
    //                 continue;
    //             }
    //             switch (ci.getBaseType()) {
    //                 // Abstract classes
    //                 case "FHIR.Element": continue;
    //             }
    //             // TODO: The cause of failure for this is unknown.
    //             // Need to figure out if it's a gap in HAPI,
    //             // or if a manual mapping is required, or what.
    //             switch(ci.getName()) {
    //                 case "ItemInstance" : continue;
    //             }
    //             resolver.resolveType(ci.getName());
    //         }
    //     }
    // }
    @Test
    fun createInstanceTests() {
        val resolver: ModelResolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        for (type in FHIRTypes.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                FHIRTypes.DOMAINRESOURCE,
                FHIRTypes.RESOURCE,
                FHIRTypes.NULL,
                FHIRTypes.BASE,
                FHIRTypes.ELEMENT,
                FHIRTypes.BACKBONEELEMENT,
                FHIRTypes.DATATYPE,
                FHIRTypes.BACKBONETYPE,
                FHIRTypes.PRIMITIVETYPE,
                FHIRTypes.CANONICALRESOURCE,
                FHIRTypes.METADATARESOURCE -> continue
                else -> {}
            }

            val instance = resolver.createInstance(type.toCode())

            Assertions.assertNotNull(instance)
        }

        for (enumType in enums) {
            // For the enums we actually expect an Enumeration with a factory of the correct type to
            // be created.
            val instance = resolver.createInstance(enumType.getSimpleName()) as Enumeration<*>?
            Assertions.assertNotNull(instance)

            Assertions.assertEquals(
                instance!!.getEnumFactory().javaClass.getSimpleName().replace("EnumFactory", ""),
                enumType.getSimpleName(),
            )
        }

        // These are some inner classes that don't appear in the enums above
        // This list is not exhaustive. It's meant as a spot check for the resolution code.
        var instance = resolver.createInstance("TestScriptRequestMethodCode")
        Assertions.assertNotNull(instance)

        instance = resolver.createInstance("FHIRDeviceStatus")
        Assertions.assertNotNull(instance)
    }

    @Test
    fun contextPathTests() {
        val resolver: ModelResolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        var path = resolver.getContextPath("Patient", "Patient") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("id", path)

        path = resolver.getContextPath(null, "Encounter") as String?
        Assertions.assertNull(path)

        // TODO: Consider making this an exception on the resolver because
        // if this happens it means something went wrong in the context.
        path = resolver.getContextPath("Patient", null) as String?
        Assertions.assertNull(path)

        path = resolver.getContextPath("Patient", "Condition") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Appointment") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Account") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Encounter") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "ValueSet") as String?
        Assertions.assertNull(path)

        path = resolver.getContextPath("Patient", "MedicationStatement") as String?
        Assertions.assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Task") as String?
        Assertions.assertEquals("for", path)

        path = resolver.getContextPath("Patient", "Coverage") as String?
        Assertions.assertEquals("beneficiary", path)

        path = resolver.getContextPath("Patient", "QuestionnaireResponse") as String?
        Assertions.assertEquals("subject", path)

        // Issue 527 - https://github.com/DBCG/cql_engine/issues/527
        path = resolver.getContextPath("Unfiltered", "MedicationStatement") as String?
        Assertions.assertNull(path)

        path = resolver.getContextPath("Unspecified", "MedicationStatement") as String?
        Assertions.assertNull(path)
    }

    @Test
    fun resolveMissingPropertyReturnsNull() {
        val resolver: ModelResolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        val p = Patient()

        val value = resolver.resolvePath(p, "not-a-path")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveIdPropertyReturnsString() {
        val resolver: ModelResolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        val p = Patient()
        p.setId("5")
        val idType = p.idElement

        var value = resolver.resolvePath(p, "id")
        Assertions.assertNotNull(value)
        MatcherAssert.assertThat(value, Matchers.`is`(idType))

        value = resolver.resolvePath(p, "id.value")
        Assertions.assertNotNull(value)
        MatcherAssert.assertThat(value, Matchers.`is`("5"))
    }

    @Test
    fun resolveDateTimeProviderReturnsDate() {
        val resolver: ModelResolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        val vp = VisionPrescription()
        val time = GregorianCalendar(1999, 3, 31).getTime()
        vp.setDateWritten(time)

        val dateTimeType = vp.dateWrittenElement

        val value = resolver.resolvePath(vp, "dateWritten")
        Assertions.assertNotNull(value)
        MatcherAssert.assertThat(value, Matchers.`is`(dateTimeType))
    }

    @Test
    fun resolveNullEnumerationReturnsNull() {
        val resolver: FhirModelResolver<Base?, *, *, SimpleQuantity?, *, *, *, *> =
            R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        val q = Quantity()
        q.setValue(BigDecimal("10.0"))
        q.setUnit("1")
        val sq = resolver.castToSimpleQuantity(q)

        val value = resolver.resolvePath(sq, "comparator")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveNullPrimitiveReturnsNull() {
        val resolver: FhirModelResolver<Base?, BaseDateTimeType?, *, *, *, *, *, *> =
            R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        val dt = DateTimeType()

        val value = resolver.resolvePath(dt, "value")
        Assertions.assertNull(value)
    }

    @Test
    @Throws(ParseException::class)
    fun resolveBirthDateExtensionPatient() {
        val resolver = R5FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R5))

        val patient = Patient()
        val birthDate = DateUtils.parseDate("1974-12-25", "yyyy-dd-MM")
        patient.setBirthDate(birthDate)
        patient.birthDateElement.addExtension(
            "http://hl7.org/fhir/StructureDefinition/patient-birthTime",
            DateTimeType("1974-12-25T14:35:45-05:00"),
        )
        var result = resolver.resolvePath(patient, "birthDate")
        Assertions.assertInstanceOf(DateType::class.java, result)
        result = resolver.resolvePath(patient, "birthDate.extension")
        Assertions.assertInstanceOf(MutableList::class.java, result)
        Assertions.assertEquals(1, (result as MutableList<*>).size)
        Assertions.assertInstanceOf(Extension::class.java, result[0])
    }

    companion object {
        // Couldn't find a way to automatically get the full list of enums.
        private val enums: MutableList<Class<*>> =
            object : ArrayList<Class<*>>() {
                init {
                    // TODO: Add the remainder of the FHIR R5 enums.
                    add(Enumerations.AdministrativeGender::class.java)
                    add(Enumerations.BindingStrength::class.java)
                    add(Enumerations.FHIRVersion::class.java)
                    add(Enumerations.PublicationStatus::class.java)
                    add(Enumerations.SearchParamType::class.java)
                }
            }
    }
}
