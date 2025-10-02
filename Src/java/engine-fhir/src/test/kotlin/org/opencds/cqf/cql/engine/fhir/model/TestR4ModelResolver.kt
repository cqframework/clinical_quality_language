package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.math.BigDecimal
import java.text.ParseException
import java.util.*
import org.apache.commons.lang3.time.DateUtils
import org.cqframework.cql.cql2elm.ModelManager
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Enumerations.DefinitionResourceType
import org.hl7.fhir.r4.model.Enumerations.EventResourceType
import org.hl7.fhir.r4.model.Enumerations.KnowledgeResourceType
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.VisionPrescription
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.model.ModelResolver

internal class TestR4ModelResolver {
    @Test
    fun resolverThrowsExceptionForUnknownType() {
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))
        Assertions.assertThrows(UnknownType::class.java) {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp")
        }
    }

    @Test
    fun resolveTypeTests() {
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        for (type in Enumerations.DataType.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                Enumerations.DataType.ELEMENT,
                Enumerations.DataType.NULL -> continue
                else -> {}
            }

            resolver.resolveType(type.toCode())
        }

        for (type in Enumerations.ResourceType.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                Enumerations.ResourceType.DOMAINRESOURCE,
                Enumerations.ResourceType.RESOURCE,
                Enumerations.ResourceType.NULL -> continue
                else -> {}
            }

            resolver.resolveType(type.toCode())
        }

        for (enumType in enums) {
            resolver.resolveType(enumType.getSimpleName())
        }
    }

    @Test
    fun modelInfoSpecialCaseTests() {
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        // This tests resolution of inner classes. They aren't registered directly.
        resolver.resolveType("TestScriptRequestMethodCode")
        resolver.resolveType("FHIRDeviceStatus")

        // This tests the special case handling of "Codes".
        resolver.resolveType("ImmunizationStatusCodes")

        // These have different capitalization conventions
        resolver.resolveType("status")
        resolver.resolveType("orientationType")
        resolver.resolveType("strandType")
        resolver.resolveType("sequenceType")

        // These are oddballs requiring manual mapping. They may represent errors in the ModelInfo.
        resolver.resolveType("ConfidentialityClassification")
        resolver.resolveType("ContractResourceStatusCodes")
        resolver.resolveType("EventStatus")
        resolver.resolveType("FinancialResourceStatusCodes")
        resolver.resolveType("SampledDataDataType")
        resolver.resolveType("ClaimProcessingCodes")
        resolver.resolveType("ContractResourcePublicationStatusCodes")

        // These are known glitches in the ModelInfo
        resolver.resolveType("vConfidentialityClassification")

        // This is a mapping for a value set that doesn't have a first-class enumeration
        resolver.resolveType("CurrencyCode")
        resolver.resolveType("MedicationAdministrationStatus")
        resolver.resolveType("MedicationDispenseStatus")
        resolver.resolveType("MedicationKnowledgeStatus")
        resolver.resolveType("Messageheader_Response_Request")
        resolver.resolveType("MimeType")
    }

    @Test
    fun modelInfo400Tests() {
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))
        val mm = ModelManager()
        val m = mm.resolveModel(ModelIdentifier("FHIR", null, "4.0.1"))

        val typeInfos: MutableList<TypeInfo> = m.modelInfo.typeInfo

        for (ti in typeInfos) {
            val ci = ti as ClassInfo?
            if (ci != null) {
                when (ci.baseType) {
                    "FHIR.Element" -> continue
                }

                when (ci.name) {
                    "ResourceContainer" -> continue
                }

                // TODO: The cause of failure for this is unknown.
                // Need to figure out if it's a gap in HAPI,
                // or if a manual mapping is required, or what.
                when (ci.name) {
                    "ItemInstance" -> continue
                }

                resolver.resolveType(ci.name)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun modelInfo401Tests() {
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))
        val mm = ModelManager()
        val m = mm.resolveModel(ModelIdentifier("FHIR", null, "4.0.1"))

        val typeInfos: MutableList<TypeInfo> = m.modelInfo.typeInfo

        for (ti in typeInfos) {
            val ci = ti as ClassInfo?
            if (ci != null) {
                when (ci.name) {
                    "ResourceContainer" -> continue
                    "DataElement constraint on ElementDefinition data type" -> continue
                    "question" -> continue
                    "allowedUnits" -> continue
                }

                // Also bugs in the 4.0.1 model info
                if (ci.baseType == null) {
                    continue
                }

                when (ci.baseType) {
                    "FHIR.Element" -> continue
                }

                // TODO: The cause of failure for this is unknown.
                // Need to figure out if it's a gap in HAPI,
                // or if a manual mapping is required, or what.
                when (ci.name) {
                    "ItemInstance" -> continue
                }

                resolver.resolveType(ci.name)
            }
        }
    }

    @Test
    fun createInstanceTests() {
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        for (type in Enumerations.DataType.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                Enumerations.DataType.BACKBONEELEMENT,
                Enumerations.DataType.ELEMENT,
                Enumerations.DataType.NULL -> continue
                else -> {}
            }

            val instance = resolver.createInstance(type.toCode())

            Assertions.assertNotNull(instance)
        }

        for (type in Enumerations.ResourceType.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                Enumerations.ResourceType.DOMAINRESOURCE,
                Enumerations.ResourceType.RESOURCE,
                Enumerations.ResourceType.NULL -> continue
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
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

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
        Assertions.assertEquals("participant.actor", path)

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
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val p = Patient()

        val value = resolver.resolvePath(p, "not-a-path")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveIdPropertyReturnsString() {
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

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
        val resolver: ModelResolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

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
        val resolver: FhirModelResolver<Base, *, *, SimpleQuantity, *, *, *, *> =
            R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val q = Quantity()
        q.setValue(BigDecimal("10.0"))
        q.setUnit("1")
        val sq = resolver.castToSimpleQuantity(q)

        val value = resolver.resolvePath(sq, "comparator")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveNullPrimitiveReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val dt = DateTimeType()

        val value = resolver.resolvePath(dt, "value")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveIdPatient() {
        val expectedId = "123"
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val patient = Patient()
        patient.setId(expectedId)

        Assertions.assertEquals(resolver.resolveId(patient), expectedId)
    }

    @Test
    @Throws(ParseException::class)
    fun resolveBirthDateExtensionPatient() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

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

    @Test
    fun resolveIdProcedure() {
        val expectedId = "456"
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val procedure = Procedure()
        procedure.setId(expectedId)

        Assertions.assertEquals(resolver.resolveId(procedure), expectedId)
    }

    @Test
    fun resolveIdStringReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        Assertions.assertNull(resolver.resolveId(Date()))
    }

    @Test
    fun resolveIdStringTypeReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        Assertions.assertNull(resolver.resolveId(StringType()))
    }

    companion object {
        // Couldn't find a way to automatically get the full list of enums.
        private val enums: MutableList<Class<*>> =
            object : ArrayList<Class<*>>() {
                init {
                    add(Enumerations.AbstractType::class.java)
                    add(Enumerations.AdministrativeGender::class.java)
                    add(Enumerations.AgeUnits::class.java)
                    add(Enumerations.BindingStrength::class.java)
                    add(Enumerations.ConceptMapEquivalence::class.java)
                    add(Enumerations.DataAbsentReason::class.java)
                    add(Enumerations.DataType::class.java)
                    add(DefinitionResourceType::class.java)
                    add(Enumerations.DocumentReferenceStatus::class.java)
                    add(EventResourceType::class.java)
                    add(Enumerations.FHIRAllTypes::class.java)
                    add(Enumerations.FHIRDefinedType::class.java)
                    add(Enumerations.FHIRVersion::class.java)
                    add(KnowledgeResourceType::class.java)
                    add(Enumerations.MessageEvent::class.java)
                    add(Enumerations.NoteType::class.java)
                    add(Enumerations.PublicationStatus::class.java)
                    add(Enumerations.RemittanceOutcome::class.java)
                    add(Enumerations.RequestResourceType::class.java)
                    add(Enumerations.ResourceType::class.java)
                    add(Enumerations.SearchParamType::class.java)
                    add(Enumerations.SpecialValues::class.java)
                }
            }
    }
}
