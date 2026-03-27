package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirVersionEnum
import java.math.BigDecimal
import javax.xml.namespace.QName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.SimpleType
import org.hl7.cql.model.TupleType
import org.hl7.fhir.dstu2.model.HumanName as Dstu2HumanName
import org.hl7.fhir.dstu2.model.StringType as Dstu2StringType
import org.hl7.fhir.dstu3.model.HumanName as Dstu3HumanName
import org.hl7.fhir.dstu3.model.Patient as Dstu3Patient
import org.hl7.fhir.dstu3.model.StringType as Dstu3StringType
import org.hl7.fhir.r4.model.HumanName as R4HumanName
import org.hl7.fhir.r4.model.Patient as R4Patient
import org.hl7.fhir.r4.model.StringType as R4StringType
import org.hl7.fhir.r5.model.HumanName as R5HumanName
import org.hl7.fhir.r5.model.Patient as R5Patient
import org.hl7.fhir.r5.model.StringType as R5StringType
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver.Companion.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlClassInstance
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.Vocabulary
import org.opencds.cqf.cql.engine.runtime.anyTypeName

private val resolvers =
    mapOf(
        FhirVersionEnum.DSTU2 to CachedDstu2FhirModelResolver(),
        FhirVersionEnum.DSTU3 to CachedDstu3FhirModelResolver(),
        FhirVersionEnum.R4 to CachedR4FhirModelResolver(),
        FhirVersionEnum.R5 to CachedR5FhirModelResolver(),
    )

/** Collects all elements of a class type, including inherited elements. */
private fun ClassType.getAllElements(): Map<String, DataType> {
    return (if (baseType is ClassType) (baseType as ClassType).getAllElements() else emptyMap()) +
        elements.associate { it.name to it.type }
}

/** Validates that a CQL value conforms to the model used by the CQL compiler. */
private fun validateCqlValueAgainstModel(
    fhirVersion: FhirVersionEnum,
    expectedFhirTypeName: String,
    cqlValue: Any?,
) {
    val modelManager = ModelManager()

    fun validateCqlValueAgainstModelInner(cqlValue: Any?, modelType: DataType) {
        if (cqlValue == null) {
            return
        }

        when (modelType) {
            is IntervalType -> {
                assertIs<Interval>(cqlValue)
                validateCqlValueAgainstModelInner(cqlValue.low, modelType.pointType)
                validateCqlValueAgainstModelInner(cqlValue.high, modelType.pointType)
            }
            is ListType -> {
                assertIs<Iterable<*>>(cqlValue)
                for (element in cqlValue) {
                    validateCqlValueAgainstModelInner(element, modelType.elementType)
                }
            }
            is ChoiceType -> {
                for (type in modelType.types) {
                    try {
                        validateCqlValueAgainstModelInner(cqlValue, type)
                        return
                    } catch (e: AssertionError) {
                        // Try the next type
                    }
                }
                throw AssertionError(
                    "Value $cqlValue does not match any type in choice type $modelType"
                )
            }
            is TupleType -> {
                assertIs<Tuple>(cqlValue)
                assertEquals(modelType.elements.map { it.name }.toSet(), cqlValue.elements.keys)
                for (element in modelType.elements) {
                    validateCqlValueAgainstModelInner(cqlValue.elements[element.name], element.type)
                }
            }
            is ClassType -> {
                when (modelType.name) {
                    "System.Quantity" -> assertIs<Quantity>(cqlValue)
                    "System.Ratio" -> assertIs<Ratio>(cqlValue)
                    "System.Code" -> assertIs<Code>(cqlValue)
                    "System.Concept" -> assertIs<Concept>(cqlValue)
                    "System.Vocabulary" ->
                        assertIs<Vocabulary>(cqlValue) // value may be a ValueSet or CodeSystem
                    "System.ValueSet" -> assertIs<ValueSet>(cqlValue)
                    "System.CodeSystem" -> assertIs<CodeSystem>(cqlValue)
                    else -> {
                        assertIs<CqlClassInstance>(cqlValue)
                        val cqlValueType =
                            modelManager
                                .resolveModelByUri(cqlValue.type.namespaceURI)
                                .resolveTypeName(cqlValue.type.localPart) as ClassType
                        assertTrue(cqlValueType.isSubTypeOf(modelType))
                        val expectedElements = cqlValueType.getAllElements()
                        assertEquals(expectedElements.keys, cqlValue.elements.keys)
                        for ((elementName, elementType) in expectedElements) {
                            validateCqlValueAgainstModelInner(
                                cqlValue.elements[elementName],
                                elementType,
                            )
                        }
                    }
                }
            }
            is SimpleType -> {
                when (modelType.name) {
                    "System.Boolean" -> assertIs<Boolean>(cqlValue)
                    "System.Integer" -> assertIs<Int>(cqlValue)
                    "System.Long" -> assertIs<Long>(cqlValue)
                    "System.Decimal" -> assertIs<BigDecimal>(cqlValue)
                    "System.String" -> assertIs<String>(cqlValue)
                    "System.DateTime" -> assertIs<DateTime>(cqlValue)
                    "System.Date" -> assertIs<Date>(cqlValue)
                    "System.Time" -> assertIs<Time>(cqlValue)
                }
            }
        }
    }

    val fhirModelVersion =
        when (fhirVersion) {
            FhirVersionEnum.DSTU2 -> "1.0.2"
            FhirVersionEnum.DSTU3 -> "3.0.0"
            FhirVersionEnum.R4 -> "4.0.1"
            FhirVersionEnum.R5 -> "4.0.1" // Use "5.0.0" when it is available
            else -> throw IllegalArgumentException("Unsupported FHIR version: $fhirVersion")
        }
    val fhirModel = modelManager.resolveModel(ModelIdentifier("FHIR", null, fhirModelVersion))
    val expectedFhirModelType = fhirModel.resolveTypeName(expectedFhirTypeName)!!

    validateCqlValueAgainstModelInner(cqlValue, expectedFhirModelType)
}

class FhirModelResolverTest {
    @Test
    fun `is`() {
        for (resolver in resolvers.values) {
            assertTrue(resolver.`is`("Patient", anyTypeName)!!)
            assertTrue(resolver.`is`("Patient", QName(fhirModelNamespaceUri, "Patient"))!!)
            assertFalse(resolver.`is`("Patient", QName("http://example.org", "ExampleClass"))!!)
            assertTrue(resolver.`is`("Age", QName(fhirModelNamespaceUri, "Quantity"))!!)
            assertFalse(resolver.`is`("Quantity", QName(fhirModelNamespaceUri, "Age"))!!)
        }
    }

    /**
     * TODO: Add DSTU2 test. At the moment,
     *
     *   FhirContext.forDstu2().getResourceDefinition(org.hl7.fhir.dstu2.model.Patient())
     *
     * throws
     *
     *     HAPI-1731: This context is for FHIR version "DSTU2" but the class "org.hl7.fhir.dstu2.model.Patient" is for version "DSTU2_HL7ORG"
     */
    @Test
    fun resolveId() {
        for ((fhirVersion, patient) in
            listOf(
                FhirVersionEnum.DSTU3 to Dstu3Patient(),
                FhirVersionEnum.R4 to R4Patient(),
                FhirVersionEnum.R5 to R5Patient(),
            )) {
            patient.id = "Patient/123"

            val resolver = resolvers[fhirVersion]!!
            val cqlValue = resolver.toCqlValue(patient)
            assertEquals("123", resolver.resolveId(cqlValue))
        }
    }

    /**
     * TODO: Add DSTU2 test. At the moment,
     *
     *   FhirContext.forDstu2().getElementDefinition(org.hl7.fhir.dstu2.model.Extension().javaClass)
     *
     * throws
     *
     *     HAPI-1690: Unknown profileOf value: class org.hl7.fhir.dstu2.model.StringType in type org.hl7.fhir.dstu2.model.IdType ...
     */
    @Test
    fun toCqlValueFhirPrimitiveWithValueAndExtension() {
        val stringValue = "example string value"

        val extensionUrl = "http://example.org"
        val extensionValue = "example extension value"

        for ((fhirVersion, fhirString) in
            listOf(
                FhirVersionEnum.DSTU3 to
                    Dstu3StringType(stringValue).apply {
                        addExtension(extensionUrl, Dstu3StringType(extensionValue))
                    },
                FhirVersionEnum.R4 to
                    R4StringType(stringValue).apply {
                        addExtension(extensionUrl, R4StringType(extensionValue))
                    },
                FhirVersionEnum.R5 to
                    R5StringType(stringValue).apply {
                        addExtension(extensionUrl, R5StringType(extensionValue))
                    },
            )) {

            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(fhirString)

            validateCqlValueAgainstModel(fhirVersion, "string", cqlValue)

            assertEquals(
                CqlClassInstance(
                    QName(fhirModelNamespaceUri, "string"),
                    mutableMapOf(
                        "id" to null,
                        "extension" to
                            listOf(
                                CqlClassInstance(
                                    QName(fhirModelNamespaceUri, "Extension"),
                                    mutableMapOf(
                                        "id" to null,
                                        "extension" to null,
                                        "value" to
                                            CqlClassInstance(
                                                QName(fhirModelNamespaceUri, "string"),
                                                mutableMapOf(
                                                    "id" to null,
                                                    "extension" to null,
                                                    "value" to extensionValue,
                                                ),
                                            ),
                                        "url" to
                                            CqlClassInstance(
                                                QName(fhirModelNamespaceUri, "uri"),
                                                mutableMapOf(
                                                    "id" to null,
                                                    "extension" to null,
                                                    "value" to extensionUrl,
                                                ),
                                            ),
                                    ),
                                )
                            ),
                        "value" to stringValue,
                    ),
                ),
                cqlValue,
            )
        }
    }

    @Test
    fun toCqlValueFhirPrimitiveWithoutValueOrIdOrExtensions() {
        for ((fhirVersion, fhirString) in
            listOf(
                FhirVersionEnum.DSTU2 to Dstu2StringType(),
                FhirVersionEnum.DSTU3 to Dstu3StringType(),
                FhirVersionEnum.R4 to R4StringType(),
                FhirVersionEnum.R5 to R5StringType(),
            )) {

            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(fhirString)

            validateCqlValueAgainstModel(fhirVersion, "string", cqlValue)

            assertNull(cqlValue)
        }
    }

    @Test
    fun toCqlValueFhirPrimitiveWithIdAndWithoutValueOrExtensions() {
        for ((fhirVersion, fhirString) in
            listOf(
                FhirVersionEnum.DSTU2 to Dstu2StringType(),
                FhirVersionEnum.DSTU3 to Dstu3StringType(),
                FhirVersionEnum.R4 to R4StringType(),
                FhirVersionEnum.R5 to R5StringType(),
            )) {
            val stringId = "exampleId"

            fhirString.id = stringId

            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(fhirString)

            validateCqlValueAgainstModel(fhirVersion, "string", cqlValue)

            assertEquals(
                CqlClassInstance(
                    QName(fhirModelNamespaceUri, "string"),
                    mutableMapOf("id" to stringId, "extension" to null, "value" to null),
                ),
                cqlValue,
            )
        }
    }

    /**
     * TODO: Add DSTU2 test. At the moment, it throws
     *
     *   Could not resolve type NameUseEnumFactory. Primary package(s) for this resolver are
     *   ca.uhn.fhir.model.dstu2,org.hl7.fhir.dstu2.model,ca.uhn.fhir.model.primitive
     */
    @Test
    fun toCqlValueFhirEnumWithValue() {
        for ((fhirVersion, useElement) in
            listOf(
                FhirVersionEnum.DSTU3 to
                    Dstu3HumanName().setUse(Dstu3HumanName.NameUse.OFFICIAL).useElement,
                FhirVersionEnum.R4 to R4HumanName().setUse(R4HumanName.NameUse.OFFICIAL).useElement,
                FhirVersionEnum.R5 to R5HumanName().setUse(R5HumanName.NameUse.OFFICIAL).useElement,
            )) {
            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(useElement)

            validateCqlValueAgainstModel(fhirVersion, "NameUse", cqlValue)

            assertEquals(
                CqlClassInstance(
                    QName(fhirModelNamespaceUri, "NameUse"),
                    mutableMapOf("id" to null, "extension" to null, "value" to "official"),
                ),
                cqlValue,
            )
        }
    }

    @Test
    fun toCqlValueFhirEnumWithoutValueOrIdOrExtensions() {
        for ((fhirVersion, useElement) in
            listOf(
                FhirVersionEnum.DSTU2 to Dstu2HumanName().useElement,
                FhirVersionEnum.DSTU3 to Dstu3HumanName().useElement,
                FhirVersionEnum.R4 to R4HumanName().useElement,
                FhirVersionEnum.R5 to R5HumanName().useElement,
            )) {
            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(useElement)

            validateCqlValueAgainstModel(fhirVersion, "NameUse", cqlValue)

            assertNull(cqlValue)
        }
    }

    @Test
    fun toCqlValueFhirEnumWithIdAndWithoutValueOrExtensions() {
        for ((fhirVersion, useElement) in
            listOf(
                FhirVersionEnum.DSTU2 to Dstu2HumanName().useElement,
                FhirVersionEnum.DSTU3 to Dstu3HumanName().useElement,
                FhirVersionEnum.R4 to R4HumanName().useElement,
                FhirVersionEnum.R5 to R5HumanName().useElement,
            )) {
            val elementId = "exampleId"

            useElement.id = elementId

            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(useElement)

            validateCqlValueAgainstModel(fhirVersion, "NameUse", cqlValue)

            assertEquals(
                CqlClassInstance(
                    QName(fhirModelNamespaceUri, "NameUse"),
                    mutableMapOf("id" to elementId, "extension" to null, "value" to null),
                ),
                cqlValue,
            )
        }
    }

    @Test
    fun toCqlValueFhirResourceWithContainedResource() {
        // TODO: Add DSTU2 test
        for ((fhirVersion, patient) in
            listOf(
                FhirVersionEnum.DSTU3 to Dstu3Patient().apply { addContained(Dstu3Patient()) },
                FhirVersionEnum.R4 to R4Patient().apply { addContained(R4Patient()) },
                FhirVersionEnum.R5 to R5Patient().apply { addContained(R5Patient()) },
            )) {

            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(patient)

            // TODO: enable this for DSTU2 and DSTU3 models which use FHIR.ResourceContainer for
            // contained resources
            if (fhirVersion == FhirVersionEnum.R4 || fhirVersion == FhirVersionEnum.R5) {
                validateCqlValueAgainstModel(fhirVersion, "Patient", cqlValue)

                assertIs<CqlClassInstance>(cqlValue)
                val containedResources = cqlValue.elements["contained"]
                assertIs<Iterable<*>>(containedResources)
                val containedPatient = containedResources.elementAt(0)
                assertIs<CqlClassInstance>(containedPatient)
                assertEquals(QName(fhirModelNamespaceUri, "Patient"), containedPatient.type)
            }
        }
    }

    @Test
    fun toCqlValueFhirResourceWithModifierExtension() {
        // TODO: Add DSTU2 test
        for ((fhirVersion, patient) in
            listOf(
                FhirVersionEnum.DSTU3 to Dstu3Patient(),
                FhirVersionEnum.R4 to R4Patient(),
                FhirVersionEnum.R5 to R5Patient(),
            )) {
            patient.addModifierExtension()

            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(patient)

            validateCqlValueAgainstModel(fhirVersion, "Patient", cqlValue)

            assertIs<CqlClassInstance>(cqlValue)
            val modifierExtensions = cqlValue.elements["modifierExtension"]
            assertIs<Iterable<*>>(modifierExtensions)
            val modifierExtension = modifierExtensions.elementAt(0)
            assertIs<CqlClassInstance>(modifierExtension)
            assertEquals(QName(fhirModelNamespaceUri, "Extension"), modifierExtension.type)
        }
    }
}
