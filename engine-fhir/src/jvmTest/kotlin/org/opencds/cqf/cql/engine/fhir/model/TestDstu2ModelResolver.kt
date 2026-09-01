package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.lang.reflect.Field
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.fhir.dstu2.model.EnumFactory
import org.hl7.fhir.dstu2.model.Enumeration
import org.hl7.fhir.dstu2.model.Enumerations
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType

class TestDstu2ModelResolver {
    @Test
    fun resolverThrowsExceptionForUnknownType() {
        val resolver = Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))
        assertFailsWith<UnknownType> {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp")
        }
    }

    @Test
    fun resolveModelInfoTests() {
        val resolver = Dstu2FhirModelResolver()
        val mm = ModelManager()
        val m = mm.resolveModel(ModelIdentifier("FHIR", null, "1.0.2"))

        val typeInfos: MutableList<TypeInfo> = m.modelInfo.typeInfo

        for (ti in typeInfos) {
            val ci = ti as ClassInfo?
            if (ci != null) {
                when (ci.baseType) {
                    "FHIR.Element" -> continue
                }

                when (ci.name) {
                    "FHIR.ResourceContainer" -> continue
                }

                resolver.resolveType(ci.name)
            }
        }
    }

    @Test
    fun resolveTypeTests() {
        val resolver = Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

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
    fun createInstanceTests() {
        val resolver = Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

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
            val instance = resolver.createHapiInstance(enumType.getSimpleName()) as Enumeration<*>?
            assertNotNull(instance)

            val enumFactory: Field?
            try {
                enumFactory = instance.javaClass.getDeclaredField("myEnumFactory")
                enumFactory.setAccessible(true)
                val factory = enumFactory.get(instance) as EnumFactory<*>

                assertEquals(
                    factory.javaClass.getSimpleName().replace("EnumFactory", ""),
                    enumType.getSimpleName(),
                )
            } catch (e: Exception) {
                throw AssertionError("error getting factory type. " + e.message)
            }
        }
    }

    @Test
    fun contextPathTests() {
        val resolver = Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

        var path = resolver.getContextPath("Patient", "Patient")
        assertEquals("id", path)

        path = resolver.getContextPath(null, "Encounter")
        assertNull(path)

        // TODO: Consider making this an exception on the resolver because
        // if this happens it means something went wrong in the context.
        path = resolver.getContextPath("Patient", null)
        assertNull(path)

        path = resolver.getContextPath("Patient", "Condition")
        assertEquals("patient", path)

        path = resolver.getContextPath("Patient", "Appointment")
        assertEquals("participant.actor", path)

        path = resolver.getContextPath("Patient", "Observation")
        assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Encounter")
        assertEquals("patient", path)

        path = resolver.getContextPath("Patient", "MedicationStatement")
        assertEquals("patient", path)

        path = resolver.getContextPath("Patient", "QuestionnaireResponse")
        assertEquals("subject", path)

        // Issue 527 - https://github.com/DBCG/cql_engine/issues/527
        path = resolver.getContextPath("Unfiltered", "MedicationStatement")
        assertNull(path)

        path = resolver.getContextPath("Unspecified", "MedicationStatement")
        assertNull(path)
    }

    companion object {
        // Couldn't find a way to automatically get the full list of enums.
        private val enums: MutableList<Class<*>> =
            object : ArrayList<Class<*>>() {
                init {
                    add(Enumerations.AdministrativeGender::class.java)
                    add(Enumerations.AgeUnits::class.java)
                    add(Enumerations.BindingStrength::class.java)
                    add(Enumerations.ConceptMapEquivalence::class.java)
                    add(Enumerations.DataAbsentReason::class.java)
                    add(Enumerations.DataType::class.java)
                    add(Enumerations.DocumentReferenceStatus::class.java)
                    add(Enumerations.FHIRDefinedType::class.java)
                    add(Enumerations.MessageEvent::class.java)
                    add(Enumerations.NoteType::class.java)
                    add(Enumerations.RemittanceOutcome::class.java)
                    add(Enumerations.ResourceType::class.java)
                    add(Enumerations.SearchParamType::class.java)
                    add(Enumerations.SpecialValues::class.java)
                }
            }
    }
}
