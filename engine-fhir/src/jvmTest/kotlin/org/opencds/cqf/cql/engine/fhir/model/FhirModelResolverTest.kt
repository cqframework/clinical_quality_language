package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.BaseRuntimeElementCompositeDefinition
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.model.api.annotation.Block
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
import org.hl7.fhir.dstu3.model.CodeType as Dstu3CodeType
import org.hl7.fhir.dstu3.model.HumanName as Dstu3HumanName
import org.hl7.fhir.dstu3.model.Patient as Dstu3Patient
import org.hl7.fhir.dstu3.model.StringType as Dstu3StringType
import org.hl7.fhir.r4.model.CodeType as R4CodeType
import org.hl7.fhir.r4.model.HumanName as R4HumanName
import org.hl7.fhir.r4.model.Patient as R4Patient
import org.hl7.fhir.r4.model.StringType as R4StringType
import org.hl7.fhir.r5.model.CodeType as R5CodeType
import org.hl7.fhir.r5.model.HumanName as R5HumanName
import org.hl7.fhir.r5.model.Patient as R5Patient
import org.hl7.fhir.r5.model.StringType as R5StringType
import org.opencds.cqf.cql.engine.fhir.fhirModelId
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.runtime.Vocabulary
import org.opencds.cqf.cql.engine.runtime.anyTypeName
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

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
                        assertIs<ClassInstance>(cqlValue)
                        val cqlValueType =
                            modelManager
                                .resolveModelByUri(cqlValue.type.namespaceURI)
                                .resolveTypeName(cqlValue.type.localPart) as ClassType
                        assertTrue(cqlValueType.isSubTypeOf(modelType))
                        val expectedElements = cqlValueType.getAllElements()
                        // Elements without a value are omitted from class instances
                        assertTrue(
                            expectedElements.keys.containsAll(cqlValue.elements.keys),
                            "Unexpected elements: ${cqlValue.elements.keys - expectedElements.keys}",
                        )
                        assertTrue(cqlValue.elements.values.none { it == null })
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
                    "System.Integer" -> assertIs<Integer>(cqlValue)
                    "System.Long" -> assertIs<Long>(cqlValue)
                    "System.Decimal" -> assertIs<Decimal>(cqlValue)
                    "System.String" -> assertIs<org.opencds.cqf.cql.engine.runtime.String>(cqlValue)
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
                ClassInstance(
                    QName(fhirModelNamespaceUri, "string", fhirModelId),
                    mutableMapOf(
                        "id" to null,
                        "extension" to
                            listOf(
                                    ClassInstance(
                                        QName(fhirModelNamespaceUri, "Extension", fhirModelId),
                                        mutableMapOf(
                                            "id" to null,
                                            "extension" to null,
                                            "value" to
                                                ClassInstance(
                                                    QName(
                                                        fhirModelNamespaceUri,
                                                        "string",
                                                        fhirModelId,
                                                    ),
                                                    mutableMapOf(
                                                        "id" to null,
                                                        "extension" to null,
                                                        "value" to extensionValue.toCqlString(),
                                                    ),
                                                ),
                                            "url" to
                                                ClassInstance(
                                                    QName(
                                                        fhirModelNamespaceUri,
                                                        "uri",
                                                        fhirModelId,
                                                    ),
                                                    mutableMapOf(
                                                        "id" to null,
                                                        "extension" to null,
                                                        "value" to extensionUrl.toCqlString(),
                                                    ),
                                                ),
                                        ),
                                    )
                                )
                                .toCqlList(),
                        "value" to stringValue.toCqlString(),
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
                ClassInstance(
                    QName(fhirModelNamespaceUri, "string", fhirModelId),
                    mutableMapOf(
                        "id" to stringId.toCqlString(),
                        "extension" to null,
                        "value" to null,
                    ),
                ),
                cqlValue,
            )
        }
    }

    /**
     * Any primitive may carry extensions without a value, e.g. a data-absent-reason. See the note
     * on [toCqlValueFhirPrimitiveWithValueAndExtension] for why DSTU2 is not covered.
     */
    @Test
    fun toCqlValueFhirPrimitiveWithExtensionAndWithoutValue() {
        val extensionUrl = "http://hl7.org/fhir/StructureDefinition/data-absent-reason"
        val reason = "unknown"

        for ((fhirVersion, fhirString) in
            listOf(
                FhirVersionEnum.DSTU3 to
                    Dstu3StringType().apply { addExtension(extensionUrl, Dstu3CodeType(reason)) },
                FhirVersionEnum.R4 to
                    R4StringType().apply { addExtension(extensionUrl, R4CodeType(reason)) },
                FhirVersionEnum.R5 to
                    R5StringType().apply { addExtension(extensionUrl, R5CodeType(reason)) },
            )) {
            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(fhirString)

            validateCqlValueAgainstModel(fhirVersion, "string", cqlValue)

            assertIs<ClassInstance>(cqlValue)
            assertEquals(QName(fhirModelNamespaceUri, "string", fhirModelId), cqlValue.type)
            assertNull(cqlValue.elements["value"])
            assertEquals(listOf(extensionUrl), extensionUrls(cqlValue.elements["extension"]))

            val extension = (cqlValue.elements["extension"] as Iterable<*>).single()
            assertIs<ClassInstance>(extension)
            assertEquals(
                ClassInstance(
                    QName(fhirModelNamespaceUri, "code", fhirModelId),
                    mutableMapOf("extension" to null, "id" to null, "value" to reason.toCqlString()),
                ),
                extension.elements["value"],
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
                ClassInstance(
                    QName(fhirModelNamespaceUri, "NameUse", fhirModelId),
                    mutableMapOf(
                        "id" to null,
                        "extension" to null,
                        "value" to "official".toCqlString(),
                    ),
                ),
                cqlValue,
            )
        }
    }

    @Test
    fun toCqlValueFhirEnumWithValueAndExtension() {
        val extensionUrl = "http://example.org"
        val extensionValue = "example extension value"

        for ((fhirVersion, useElement) in
            listOf(
                FhirVersionEnum.DSTU3 to
                    Dstu3HumanName().setUse(Dstu3HumanName.NameUse.OFFICIAL).useElement.apply {
                        addExtension(extensionUrl, Dstu3StringType(extensionValue))
                    },
                FhirVersionEnum.R4 to
                    R4HumanName().setUse(R4HumanName.NameUse.OFFICIAL).useElement.apply {
                        addExtension(extensionUrl, R4StringType(extensionValue))
                    },
                FhirVersionEnum.R5 to
                    R5HumanName().setUse(R5HumanName.NameUse.OFFICIAL).useElement.apply {
                        addExtension(extensionUrl, R5StringType(extensionValue))
                    },
            )) {
            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(useElement)

            validateCqlValueAgainstModel(fhirVersion, "NameUse", cqlValue)

            assertIs<ClassInstance>(cqlValue)
            assertEquals(QName(fhirModelNamespaceUri, "NameUse", fhirModelId), cqlValue.type)
            assertEquals("official".toCqlString(), cqlValue.elements["value"])
            assertEquals(listOf(extensionUrl), extensionUrls(cqlValue.elements["extension"]))
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
                ClassInstance(
                    QName(fhirModelNamespaceUri, "NameUse", fhirModelId),
                    mutableMapOf(
                        "id" to elementId.toCqlString(),
                        "extension" to null,
                        "value" to null,
                    ),
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

                assertIs<ClassInstance>(cqlValue)
                val containedResources = cqlValue.elements["contained"]
                assertIs<Iterable<*>>(containedResources)
                val containedPatient = containedResources.elementAt(0)
                assertIs<ClassInstance>(containedPatient)
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

            assertIs<ClassInstance>(cqlValue)
            val modifierExtensions = cqlValue.elements["modifierExtension"]
            assertIs<Iterable<*>>(modifierExtensions)
            val modifierExtension = modifierExtensions.elementAt(0)
            assertIs<ClassInstance>(modifierExtension)
            assertEquals(QName(fhirModelNamespaceUri, "Extension"), modifierExtension.type)
        }
    }

    /**
     * Extensions and modifier extensions are kept separate, on resources and on backbone elements.
     */
    @Test
    fun toCqlValueFhirResourceWithExtensionAndModifierExtension() {
        val extensionUrl = "http://example.org/extension"
        val modifierExtensionUrl = "http://example.org/modifierExtension"

        for ((fhirVersion, patient) in
            listOf(
                FhirVersionEnum.DSTU3 to
                    Dstu3Patient().apply {
                        addExtension().url = extensionUrl
                        addModifierExtension().url = modifierExtensionUrl
                    },
                FhirVersionEnum.R4 to
                    R4Patient().apply {
                        addExtension().url = extensionUrl
                        addModifierExtension().url = modifierExtensionUrl
                    },
                FhirVersionEnum.R5 to
                    R5Patient().apply {
                        addExtension().url = extensionUrl
                        addModifierExtension().url = modifierExtensionUrl
                    },
            )) {
            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(patient)

            validateCqlValueAgainstModel(fhirVersion, "Patient", cqlValue)

            assertIs<ClassInstance>(cqlValue)
            assertEquals(listOf(extensionUrl), extensionUrls(cqlValue.elements["extension"]))
            assertEquals(
                listOf(modifierExtensionUrl),
                extensionUrls(cqlValue.elements["modifierExtension"]),
            )
        }
    }

    /**
     * DSTU3 backbone elements keep the HAPI class name (e.g. ContactComponent), which isn't in the
     * DSTU3 model info, so only R4/R5 values are validated against the model.
     */
    @Test
    fun toCqlValueFhirBackboneElementWithExtensionAndModifierExtension() {
        val extensionUrl = "http://example.org/extension"
        val modifierExtensionUrl = "http://example.org/modifierExtension"

        for ((fhirVersion, contact) in
            listOf(
                FhirVersionEnum.DSTU3 to
                    Dstu3Patient().addContact().apply {
                        addExtension().url = extensionUrl
                        addModifierExtension().url = modifierExtensionUrl
                    },
                FhirVersionEnum.R4 to
                    R4Patient().addContact().apply {
                        addExtension().url = extensionUrl
                        addModifierExtension().url = modifierExtensionUrl
                    },
                FhirVersionEnum.R5 to
                    R5Patient().addContact().apply {
                        addExtension().url = extensionUrl
                        addModifierExtension().url = modifierExtensionUrl
                    },
            )) {
            val cqlValue = resolvers[fhirVersion]!!.toCqlValue(contact)

            if (fhirVersion != FhirVersionEnum.DSTU3) {
                validateCqlValueAgainstModel(fhirVersion, "Patient.Contact", cqlValue)
            }

            assertIs<ClassInstance>(cqlValue)
            assertEquals(listOf(extensionUrl), extensionUrls(cqlValue.elements["extension"]))
            assertEquals(
                listOf(modifierExtensionUrl),
                extensionUrls(cqlValue.elements["modifierExtension"]),
            )
        }
    }

    private fun extensionUrls(extensions: Any?): kotlin.collections.List<kotlin.String?> {
        assertIs<Iterable<*>>(extensions)
        return extensions.map {
            assertIs<ClassInstance>(it)
            val url = it.elements["url"]
            assertIs<ClassInstance>(url)
            (url.elements["value"] as org.opencds.cqf.cql.engine.runtime.String).value
        }
    }

    /**
     * Every HAPI backbone element class converts to a type defined in the model info (e.g.
     * Patient.Contact rather than ContactComponent), and is an instance of that type.
     */
    @Test
    fun toCqlValueFhirBackboneElementsHaveModelInfoTypeNames() {
        for ((fhirVersion, modelVersion) in listOf(FhirVersionEnum.R4 to "4.0.1")) {
            val resolver = resolvers[fhirVersion]!!
            val fhirContext = resolver.fhirContext
            val fhirModel = ModelManager().resolveModel(ModelIdentifier("FHIR", null, modelVersion))

            val definingClasses =
                fhirContext.resourceTypes.map {
                    fhirContext.getResourceDefinition(it).implementingClass
                } +
                    fhirContext.elementDefinitions
                        .filterIsInstance<BaseRuntimeElementCompositeDefinition<*>>()
                        .map { it.implementingClass }
            val backboneClasses =
                definingClasses
                    .flatMap { it.declaredClasses.toList() }
                    .filter { it.isAnnotationPresent(Block::class.java) }
                    .distinct()
            assertTrue(backboneClasses.size > 100, "Found ${backboneClasses.size} backbone classes")

            val unknownTypes =
                backboneClasses.mapNotNull { backboneClass ->
                    val cqlValue =
                        resolver.toCqlValue(backboneClass.getDeclaredConstructor().newInstance())
                    assertIs<ClassInstance>(cqlValue)
                    val typeName = cqlValue.type.localPart
                    if (fhirModel.resolveTypeName(typeName) == null) {
                        "${backboneClass.name} -> $typeName"
                    } else {
                        assertEquals(
                            true,
                            resolver.`is`(typeName, QName(fhirModelNamespaceUri, typeName)),
                            "${backboneClass.name} is not a $typeName",
                        )
                        null
                    }
                }
            assertEquals(emptyList(), unknownTypes, "$fhirVersion backbone types not in model info")
        }
    }
}
