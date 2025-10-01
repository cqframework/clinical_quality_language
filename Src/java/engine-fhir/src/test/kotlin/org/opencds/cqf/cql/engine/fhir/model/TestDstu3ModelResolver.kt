package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.math.BigDecimal
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.fhir.dstu3.model.Base
import org.hl7.fhir.dstu3.model.DateTimeType
import org.hl7.fhir.dstu3.model.Enumeration
import org.hl7.fhir.dstu3.model.Enumerations
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.dstu3.model.Quantity
import org.hl7.fhir.dstu3.model.SimpleQuantity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.model.ModelResolver

internal class TestDstu3ModelResolver {
    @Test
    fun resolverThrowsExceptionForUnknownType() {
        val resolver: ModelResolver =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))
        Assertions.assertThrows(UnknownType::class.java) {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp")
        }
    }

    // This tests all the top-level types HAPI knows about.
    @Test
    fun resolveTypeTests() {
        val resolver: ModelResolver =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

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

    // This tests all the types that are present in the ModelInfo.
    @Test
    fun resolveModelInfoTests() {
        val resolver: ModelResolver =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))
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
        val resolver: ModelResolver =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        // This tests resolution of inner classes. They aren't registered directly.
        resolver.resolveType("TestScriptRequestMethodCode")
        resolver.resolveType("FHIRDeviceStatus")

        // This tests the special case handling of "Codes".
        resolver.resolveType("ImmunizationStatusCodes")
        resolver.resolveType("ConditionClinicalStatusCodes")

        // These are oddballs requiring manual mapping.
        resolver.resolveType("ConfidentialityClassification")
        resolver.resolveType("ContractResourceStatusCodes")
        resolver.resolveType("EventStatus")
        resolver.resolveType("qualityType")
        resolver.resolveType("FinancialResourceStatusCodes")
        resolver.resolveType("repositoryType")
        resolver.resolveType("SampledDataDataType")
    }

    @Test
    fun createInstanceTests() {
        val resolver: ModelResolver =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

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
            // For the enums we actually expect an Enumeration with a factory of the correct
            // type to be created.
            val instance = resolver.createInstance(enumType.getSimpleName()) as Enumeration<*>?
            Assertions.assertNotNull(instance)

            Assertions.assertEquals(
                instance!!.getEnumFactory().javaClass.getSimpleName().replace("EnumFactory", ""),
                enumType.getSimpleName(),
            )
        }

        // These are some inner classes that don't appear in the enums above
        // This list is not exhaustive. It's meant as a spot check for the resolution
        // code.
        var instance = resolver.createInstance("TestScriptRequestMethodCode")
        Assertions.assertNotNull(instance)

        instance = resolver.createInstance("FHIRDeviceStatus")
        Assertions.assertNotNull(instance)
    }

    @Test
    fun contextPathTests() {
        val resolver: ModelResolver =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

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
        val resolver: ModelResolver =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val p = Patient()

        val value = resolver.resolvePath(p, "not-a-path")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveNullEnumerationReturnsNull() {
        val resolver: FhirModelResolver<Base, *, *, SimpleQuantity, *, *, *, *> =
            Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val q = Quantity()
        q.setValue(BigDecimal("10.0"))
        q.setUnit("1")
        val sq = resolver.castToSimpleQuantity(q)

        val value = resolver.resolvePath(sq, "comparator")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveNullPrimitiveReturnsNull() {
        val resolver = Dstu3FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU3))

        val dt = DateTimeType()

        val value = resolver.resolvePath(dt, "value")
        Assertions.assertNull(value)
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
