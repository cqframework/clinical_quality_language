package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.math.BigDecimal
import java.text.ParseException
import java.util.*
import javax.xml.namespace.QName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.apache.commons.lang3.time.DateUtils
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Enumerations.DefinitionResourceType
import org.hl7.fhir.r4.model.Enumerations.EventResourceType
import org.hl7.fhir.r4.model.Enumerations.KnowledgeResourceType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.VisionPrescription
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class TestR4ModelResolver {
    @Test
    fun resolverThrowsExceptionForUnknownType() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))
        assertFailsWith<UnknownType> {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp")
        }
    }

    @Test
    fun resolveTypeTests() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

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
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        // This tests resolution of inner classes. They aren't registered directly.
        assertEquals(
            org.hl7.fhir.r4.model.TestScript.TestScriptRequestMethodCode::class.java,
            resolver.resolveType("TestScriptRequestMethodCode"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.Device.FHIRDeviceStatus::class.java,
            resolver.resolveType("FHIRDeviceStatus"),
        )

        // This tests the special case handling of "Codes".
        assertEquals(
            org.hl7.fhir.r4.model.Immunization.ImmunizationStatus::class.java,
            resolver.resolveType("ImmunizationStatusCodes"),
        )

        // These have different capitalization conventions
        assertEquals(
            org.hl7.fhir.r4.model.VerificationResult.Status::class.java,
            resolver.resolveType("status"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.MolecularSequence.OrientationType::class.java,
            resolver.resolveType("orientationType"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.MolecularSequence.StrandType::class.java,
            resolver.resolveType("strandType"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.MolecularSequence.SequenceType::class.java,
            resolver.resolveType("sequenceType"),
        )

        // These are oddballs requiring manual mapping. They may represent errors in the ModelInfo.
        assertEquals(
            org.hl7.fhir.r4.model.Composition.DocumentConfidentiality::class.java,
            resolver.resolveType("ConfidentialityClassification"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.Contract.ContractStatus::class.java,
            resolver.resolveType("ContractResourceStatusCodes"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.Procedure.ProcedureStatus::class.java,
            resolver.resolveType("EventStatus"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.ClaimResponse.ClaimResponseStatus::class.java,
            resolver.resolveType("FinancialResourceStatusCodes"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.StringType::class.java,
            resolver.resolveType("SampledDataDataType"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.ClaimResponse.RemittanceOutcome::class.java,
            resolver.resolveType("ClaimProcessingCodes"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.Contract.ContractPublicationStatus::class.java,
            resolver.resolveType("ContractResourcePublicationStatusCodes"),
        )

        // These are known glitches in the ModelInfo
        assertEquals(
            org.hl7.fhir.r4.model.Composition.DocumentConfidentiality::class.java,
            resolver.resolveType("vConfidentialityClassification"),
        )

        // This is a mapping for a value set that doesn't have a first-class enumeration
        assertEquals(
            org.hl7.fhir.r4.model.CodeType::class.java,
            resolver.resolveType("CurrencyCode"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.MessageDefinition.MessageheaderResponseRequest::class.java,
            resolver.resolveType("Messageheader_Response_Request"),
        )
        assertEquals(org.hl7.fhir.r4.model.CodeType::class.java, resolver.resolveType("MimeType"))

        // These were previously incorrectly mapped to CodeType
        assertEquals(
            org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus::class
                .java,
            resolver.resolveType("MedicationAdministrationStatus"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseStatus::class.java,
            resolver.resolveType("MedicationDispenseStatus"),
        )
        assertEquals(
            org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeStatus::class.java,
            resolver.resolveType("MedicationKnowledgeStatus"),
        )
    }

    @Test
    fun modelInfo400Tests() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))
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
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))
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
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        for (type in Enumerations.DataType.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                Enumerations.DataType.BACKBONEELEMENT,
                Enumerations.DataType.ELEMENT,
                Enumerations.DataType.NULL -> continue
                else -> {}
            }

            val instance = resolver.createHapiInstance(type.toCode())

            assertNotNull(instance)
        }

        for (type in Enumerations.ResourceType.entries) {
            // These are abstract types that should never be resolved directly.
            when (type) {
                Enumerations.ResourceType.DOMAINRESOURCE,
                Enumerations.ResourceType.RESOURCE,
                Enumerations.ResourceType.NULL -> continue
                else -> {}
            }

            val instance = resolver.createHapiInstance(type.toCode())

            assertNotNull(instance)
        }

        for (enumType in enums) {
            // For the enums we actually expect an Enumeration with a factory of the correct type to
            // be created.
            val instance = resolver.createHapiInstance(enumType.getSimpleName()) as Enumeration<*>?
            assertNotNull(instance)

            assertEquals(
                instance.getEnumFactory().javaClass.getSimpleName().replace("EnumFactory", ""),
                enumType.getSimpleName(),
            )
        }

        // These are some inner classes that don't appear in the enums above
        // This list is not exhaustive. It's meant as a spot check for the resolution code.
        var instance = resolver.createHapiInstance("TestScriptRequestMethodCode")
        assertNotNull(instance)

        instance = resolver.createHapiInstance("FHIRDeviceStatus")
        assertNotNull(instance)
    }

    @Test
    fun contextPathTests() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        var path = resolver.getContextPath("Patient", "Patient")
        assertEquals("id", path)

        path = resolver.getContextPath(null, "Encounter")
        assertNull(path)

        // TODO: Consider making this an exception on the resolver because
        // if this happens it means something went wrong in the context.
        path = resolver.getContextPath("Patient", null)
        assertNull(path)

        path = resolver.getContextPath("Patient", "Condition")
        assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Appointment")
        assertEquals("participant.actor", path)

        path = resolver.getContextPath("Patient", "Account")
        assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Encounter")
        assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "ValueSet")
        assertNull(path)

        path = resolver.getContextPath("Patient", "MedicationStatement")
        assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Task")
        assertEquals("for", path)

        path = resolver.getContextPath("Patient", "Coverage")
        assertEquals("beneficiary", path)

        path = resolver.getContextPath("Patient", "QuestionnaireResponse")
        assertEquals("subject", path)

        // Issue 527 - https://github.com/DBCG/cql_engine/issues/527
        path = resolver.getContextPath("Unfiltered", "MedicationStatement")
        assertNull(path)

        path = resolver.getContextPath("Unspecified", "MedicationStatement")
        assertNull(path)
    }

    @Test
    fun resolveMissingPropertyReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val p = Patient()

        val patientAsCqlValue = resolver.toCqlValue(p)
        assertIs<ClassInstance>(patientAsCqlValue)
        assertFalse(patientAsCqlValue.elements.containsKey("not-a-path"))
    }

    @Test
    fun resolveIdPropertyReturnsString() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val p = Patient()
        p.setId("5")

        val patientAsCqlValue = resolver.toCqlValue(p)
        assertIs<ClassInstance>(patientAsCqlValue)

        val id = patientAsCqlValue.elements["id"]
        assertIs<ClassInstance>(id)
        assertEquals(QName("http://hl7.org/fhir", "id"), id.type)

        assertEquals("5".toCqlString(), id.elements["value"])
    }

    @Test
    fun resolveDateTimeProviderReturnsDate() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val vp = VisionPrescription()
        val time = GregorianCalendar(1999, 3, 31).getTime()
        vp.setDateWritten(time)

        val value = resolver.toCqlValue(vp)
        assertIs<ClassInstance>(value)
        val dateWritten = value.elements["dateWritten"]
        assertIs<ClassInstance>(dateWritten)
        assertEquals(QName("http://hl7.org/fhir", "dateTime"), dateWritten.type)
    }

    @Test
    fun resolveNullEnumerationReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val q = Quantity()
        q.setValue(BigDecimal("10.0"))
        q.setUnit("1")
        val sq = resolver.castToSimpleQuantity(q)

        val value = resolver.toCqlValue(sq)
        assertIs<ClassInstance>(value)
        assertNull(value.elements["comparator"])
    }

    @Test
    fun resolveNullPrimitiveReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val dt = DateTimeType()

        val value = resolver.toCqlValue(dt)
        assertNull(value)
    }

    @Test
    fun resolveIdPatient() {
        val expectedId = "123"
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val patient = Patient()
        patient.setId(expectedId)

        assertEquals(expectedId, resolver.resolveId(resolver.toCqlValue(patient)))
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

        val patientAsCqlValue = resolver.toCqlValue(patient)
        assertIs<ClassInstance>(patientAsCqlValue)

        var result = patientAsCqlValue.elements["birthDate"]
        assertIs<ClassInstance>(result)
        assertEquals(QName("http://hl7.org/fhir", "date"), result.type)

        result = result.elements["extension"]
        assertIs<Iterable<*>>(result)
        assertEquals(1, result.count())

        val extension = result.first()
        assertIs<ClassInstance>(extension)
        assertEquals(QName("http://hl7.org/fhir", "Extension"), extension.type)
    }

    @Test
    fun resolveIdProcedure() {
        val expectedId = "456"
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        val procedure = Procedure()
        procedure.setId(expectedId)

        assertEquals(expectedId, resolver.resolveId(resolver.toCqlValue(procedure)))
    }

    @Test
    fun resolveIdStringReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        assertNull(resolver.resolveId(Date(2000)))
    }

    @Test
    fun resolveIdStringTypeReturnsNull() {
        val resolver = R4FhirModelResolver(FhirContext.forCached(FhirVersionEnum.R4))

        assertNull(resolver.resolveId(org.opencds.cqf.cql.engine.runtime.String.EMPTY_STRING))
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
