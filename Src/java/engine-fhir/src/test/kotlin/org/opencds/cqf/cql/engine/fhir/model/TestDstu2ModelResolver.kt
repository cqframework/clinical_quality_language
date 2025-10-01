package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.lang.reflect.Field
import java.math.BigDecimal
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.fhir.dstu2.model.Base
import org.hl7.fhir.dstu2.model.BaseDateTimeType
import org.hl7.fhir.dstu2.model.DateTimeType
import org.hl7.fhir.dstu2.model.EnumFactory
import org.hl7.fhir.dstu2.model.Enumeration
import org.hl7.fhir.dstu2.model.Enumerations
import org.hl7.fhir.dstu2.model.Patient
import org.hl7.fhir.dstu2.model.Quantity
import org.hl7.fhir.dstu2.model.SimpleQuantity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.model.ModelResolver

class TestDstu2ModelResolver {
    @Test
    fun resolverThrowsExceptionForUnknownType() {
        val resolver: ModelResolver =
            Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))
        Assertions.assertThrows(UnknownType::class.java) {
            resolver.resolveType("ImpossibleTypeThatDoesn'tExistAndShouldBlowUp")
        }
    }

    @Test
    fun resolveModelInfoTests() {
        val resolver: ModelResolver = Dstu2FhirModelResolver()
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
        val resolver: ModelResolver =
            Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

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
        val resolver: ModelResolver =
            Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

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

            val enumFactory: Field?
            try {
                enumFactory = instance!!.javaClass.getDeclaredField("myEnumFactory")
                enumFactory.setAccessible(true)
                val factory = enumFactory.get(instance) as EnumFactory<*>

                Assertions.assertEquals(
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
        val resolver: ModelResolver =
            Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

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
        Assertions.assertEquals("patient", path)

        path = resolver.getContextPath("Patient", "Appointment") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("participant.actor", path)

        path = resolver.getContextPath("Patient", "Observation") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("subject", path)

        path = resolver.getContextPath("Patient", "Encounter") as String?
        Assertions.assertNotNull(path)
        Assertions.assertEquals("patient", path)

        path = resolver.getContextPath("Patient", "MedicationStatement") as String?
        Assertions.assertEquals("patient", path)

        path = resolver.getContextPath("Patient", "QuestionnaireResponse") as String?
        Assertions.assertEquals("subject", path)

        // Issue 527 - https://github.com/DBCG/cql_engine/issues/527
        path = resolver.getContextPath("Unfiltered", "MedicationStatement") as String?
        Assertions.assertNull(path)

        path = resolver.getContextPath("Unspecified", "MedicationStatement") as String?
        Assertions.assertNull(path)
    }

    // This is a serious failure that needs to be addressed. There's some sort of
    // mixup
    // between the dstu2 and hl7org dstu2 objects.
    // @Test
    fun resolveMissingPropertyReturnsNull() {
        val resolver: ModelResolver =
            Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

        val p = Patient()

        val value = resolver.resolvePath(p, "not-a-path")
        Assertions.assertNull(value)
    }

    // @Test
    fun resolveNullEnumerationReturnsNull() {
        val resolver: FhirModelResolver<Base?, *, *, SimpleQuantity?, *, *, *, *> =
            Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

        val q = Quantity()
        q.setValue(BigDecimal("10.0"))
        q.setUnit("1")
        val sq = resolver.castToSimpleQuantity(q)

        val value = resolver.resolvePath(sq, "comparator")
        Assertions.assertNull(value)
    }

    // @Test
    fun resolveNullPrimitiveReturnsNull() {
        val resolver: FhirModelResolver<Base?, BaseDateTimeType?, *, *, *, *, *, *> =
            Dstu2FhirModelResolver(FhirContext.forCached(FhirVersionEnum.DSTU2))

        val dt = DateTimeType()

        val value = resolver.resolvePath(dt, "value")
        Assertions.assertNull(value)
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
