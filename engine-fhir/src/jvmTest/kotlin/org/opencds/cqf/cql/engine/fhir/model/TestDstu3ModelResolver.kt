package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.fhir.dstu3.model.DateTimeType
import org.hl7.fhir.dstu3.model.Enumeration
import org.hl7.fhir.dstu3.model.Enumerations
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.dstu3.model.Quantity
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.runtime.ClassInstance

internal class TestDstu3ModelResolver {
    @Test
    fun resolverThrowsExceptionForUnknownType() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))
        assertFailsWith<UnknownType> {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp")
        }
    }

    // This tests all the top-level types HAPI knows about.
    @Test
    fun resolveTypeTests() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

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
            resolver.resolveType(enumType.simpleName)
        }
    }

    // This tests all the types that are present in the ModelInfo.
    @Test
    fun resolveModelInfoTests() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))
        val mm = ModelManager()
        val m = mm.resolveModel(ModelIdentifier("FHIR", null, "3.0.0"))

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

                resolver.resolveType(ci.name)
            }
        }
    }

    // This tests special case logic in the Model Resolver.
    // Ideally, these would all disappear with either registering custom types
    // on the FhirContext or generalized logic, or fixed-up ModelInfos
    @Test
    fun modelInfoSpecialCaseTests() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        // This tests resolution of inner classes. They aren't registered directly.
        assertEquals(
            org.hl7.fhir.dstu3.model.TestScript.TestScriptRequestMethodCode::class.java,
            resolver.resolveType("TestScriptRequestMethodCode"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.Device.FHIRDeviceStatus::class.java,
            resolver.resolveType("FHIRDeviceStatus"),
        )

        // This tests the special case handling of "Codes".
        assertEquals(
            org.hl7.fhir.dstu3.model.Immunization.ImmunizationStatus::class.java,
            resolver.resolveType("ImmunizationStatusCodes"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus::class.java,
            resolver.resolveType("ConditionClinicalStatusCodes"),
        )

        // These are oddballs requiring manual mapping.
        assertEquals(
            org.hl7.fhir.dstu3.model.Composition.DocumentConfidentiality::class.java,
            resolver.resolveType("ConfidentialityClassification"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.Contract.ContractStatus::class.java,
            resolver.resolveType("ContractResourceStatusCodes"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.Procedure.ProcedureStatus::class.java,
            resolver.resolveType("EventStatus"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.Sequence.QualityType::class.java,
            resolver.resolveType("qualityType"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.ClaimResponse.ClaimResponseStatus::class.java,
            resolver.resolveType("FinancialResourceStatusCodes"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.Sequence.RepositoryType::class.java,
            resolver.resolveType("repositoryType"),
        )
        assertEquals(
            org.hl7.fhir.dstu3.model.StringType::class.java,
            resolver.resolveType("SampledDataDataType"),
        )
    }

    @Test
    fun createInstanceTests() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

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
            // For the enums we actually expect an Enumeration with a factory of the correct
            // type to be created.
            val instance = resolver.createHapiInstance(enumType.simpleName) as Enumeration<*>?
            assertNotNull(instance)

            assertEquals(
                instance.getEnumFactory().javaClass.simpleName.replace("EnumFactory", ""),
                enumType.simpleName,
            )
        }

        // These are some inner classes that don't appear in the enums above
        // This list is not exhaustive. It's meant as a spot check for the resolution
        // code.
        var instance = resolver.createHapiInstance("TestScriptRequestMethodCode")
        assertNotNull(instance)

        instance = resolver.createHapiInstance("FHIRDeviceStatus")
        assertNotNull(instance)
    }

    @Test
    fun contextPathTests() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        var path = resolver.getContextPath("Patient", "Patient")
        assertEquals("id", path)

        path = resolver.getContextPath(null, "Encounter")
        assertNull(path)

        // TODO: Consider making this an exception on the resolver because
        // if this happens it means something went wrong in the context.
        path = resolver.getContextPath("Patient", null)
        assertNull(path)

        path = resolver.getContextPath("Patient", "Condition")
        assertNotNull(path)
        assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Appointment")
        assertNotNull(path)
        assertEquals("participant.actor", path)

        path = resolver.getContextPath("Patient", "Account")
        assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Encounter")
        assertEquals("subject", path)

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
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val p = Patient()

        val patientAsCqlValue = resolver.toCqlValue(p)
        assertIs<ClassInstance>(patientAsCqlValue)
        assertFalse(patientAsCqlValue.elements.containsKey("not-a-path"))
    }

    @Test
    fun resolveNullEnumerationReturnsNull() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

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
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val dt = DateTimeType()

        val value = resolver.toCqlValue(dt)
        assertNull(value)
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
                    add(Enumerations.DocumentReferenceStatus::class.java)
                    add(Enumerations.FHIRAllTypes::class.java)
                    add(Enumerations.FHIRDefinedType::class.java)
                    add(Enumerations.MessageEvent::class.java)
                    add(Enumerations.NoteType::class.java)
                    add(Enumerations.PublicationStatus::class.java)
                    add(Enumerations.RemittanceOutcome::class.java)
                    add(Enumerations.ResourceType::class.java)
                    add(Enumerations.SearchParamType::class.java)
                    add(Enumerations.SpecialValues::class.java)
                }
            }
    }
}
