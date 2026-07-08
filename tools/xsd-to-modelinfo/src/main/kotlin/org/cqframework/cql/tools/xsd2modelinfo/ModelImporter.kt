package org.cqframework.cql.tools.xsd2modelinfo

import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import javax.xml.namespace.QName
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.apache.ws.commons.schema.XmlSchema
import org.apache.ws.commons.schema.XmlSchemaAll
import org.apache.ws.commons.schema.XmlSchemaAttribute
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef
import org.apache.ws.commons.schema.XmlSchemaAttributeOrGroupRef
import org.apache.ws.commons.schema.XmlSchemaChoice
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction
import org.apache.ws.commons.schema.XmlSchemaComplexType
import org.apache.ws.commons.schema.XmlSchemaElement
import org.apache.ws.commons.schema.XmlSchemaGroupRef
import org.apache.ws.commons.schema.XmlSchemaParticle
import org.apache.ws.commons.schema.XmlSchemaSequence
import org.apache.ws.commons.schema.XmlSchemaSimpleContentExtension
import org.apache.ws.commons.schema.XmlSchemaSimpleContentRestriction
import org.apache.ws.commons.schema.XmlSchemaSimpleType
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction
import org.apache.ws.commons.schema.XmlSchemaType
import org.apache.ws.commons.schema.XmlSchemaUse
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.ChoiceTypePolicy
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.Companion.loadFromProperties
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.ElementRedeclarationPolicy
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.SimpleTypeRestrictionPolicy
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.ClassTypeElement
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.InvalidRedeclarationException
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamedType
import org.hl7.cql.model.SimpleType
import org.hl7.cql.model.TupleType
import org.hl7.cql.model.TypeParameter
import org.hl7.elm_modelinfo.r1.ChoiceTypeSpecifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.ClassInfoElement
import org.hl7.elm_modelinfo.r1.IntervalTypeInfo
import org.hl7.elm_modelinfo.r1.IntervalTypeSpecifier
import org.hl7.elm_modelinfo.r1.ListTypeInfo
import org.hl7.elm_modelinfo.r1.ListTypeSpecifier
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.NamedTypeSpecifier
import org.hl7.elm_modelinfo.r1.SimpleTypeInfo
import org.hl7.elm_modelinfo.r1.TupleTypeInfo
import org.hl7.elm_modelinfo.r1.TupleTypeInfoElement
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.elm_modelinfo.r1.TypeParameterInfo
import org.hl7.elm_modelinfo.r1.TypeSpecifier
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

@Suppress("LargeClass", "TooManyFunctions", "ForbiddenComment")
class ModelImporter
private constructor(
    private val schema: XmlSchema,
    options: ModelImporterOptions?,
    private val config: ModelInfo?,
) {
    private val options: ModelImporterOptions
    private val dataTypes: MutableMap<String?, DataType?>
    private val namespaces: MutableMap<String?, String?>

    init {
        this.dataTypes = HashMap<String?, DataType?>()
        this.namespaces = HashMap<String?, String?>()

        // Load default options first
        val tmpOptions: ModelImporterOptions
        try {
            javaClass.getResourceAsStream("default_options.properties").use { defaultPropertiesIS ->
                tmpOptions = loadFromProperties(defaultPropertiesIS)
            }
        } catch (e: IOException) {
            throw IllegalStateException("Could not load default properties", e)
        }

        // If options were passed in, apply them on top of the default options
        if (options != null) {
            tmpOptions.applyProperties(options.exportProperties())
        }

        this.options = tmpOptions
    }

    fun importXsd(): ModelInfo {
        require(!(options.model == null || options.model!!.isEmpty())) { "Model name is required." }
        namespaces[schema.targetNamespace] = options.model

        for (schemaType in schema.schemaTypes.values) {
            resolveType(schemaType)
        }

        return ModelInfo()
            .withName(options.model)
            .withTargetQualifier(options.model!!.lowercase(Locale.getDefault()))
            .withUrl(schema.targetNamespace)
            .withPatientClassName(if (config != null) config.patientClassName else null)
            .withPatientClassIdentifier(if (config != null) config.patientClassIdentifier else null)
            .withPatientBirthDatePropertyName(
                if (config != null) config.patientBirthDatePropertyName else null
            )
            .withTypeInfo(
                dataTypes.values
                    .stream()
                    .map<TypeInfo?> { dataType: DataType? -> this.toTypeInfo(dataType!!) }
                    .collect(Collectors.toList())
            )
    }

    private fun toTypeInfo(dataType: DataType): TypeInfo {
        return when (dataType) {
            is SimpleType -> {
                toSimpleTypeInfo(dataType)
            }

            is ClassType -> {
                toClassInfo(dataType)
            }

            is IntervalType -> {
                toIntervalTypeInfo(dataType)
            }

            is ListType -> {
                toListTypeInfo(dataType)
            }

            is TupleType -> {
                toTupleTypeInfo(dataType)
            }

            else -> {
                throw IllegalArgumentException(
                    java.lang.String.format("Unknown data type class: %s", dataType.javaClass.name)
                )
            }
        }
    }

    private fun toTypeName(typeSpecifier: NamedTypeSpecifier): String? {
        if (typeSpecifier.modelName != null) {
            return "${typeSpecifier.modelName}, ${typeSpecifier.name}"
        }
        return typeSpecifier.name
    }

    private fun setBaseType(typeInfo: TypeInfo, baseType: DataType) {
        val baseTypeSpecifier = toTypeSpecifier(baseType)
        if (baseTypeSpecifier is NamedTypeSpecifier) {
            typeInfo.baseType = toTypeName(baseTypeSpecifier)
        } else {
            typeInfo.baseTypeSpecifier = baseTypeSpecifier
        }
    }

    private fun toSimpleTypeInfo(dataType: SimpleType): SimpleTypeInfo {
        val result = SimpleTypeInfo()
        result.name = dataType.simpleName
        setBaseType(result, dataType.baseType)

        return result
    }

    private fun toClassInfo(dataType: ClassType): ClassInfo {
        val result = ClassInfo()
        result.name = dataType.simpleName
        setBaseType(result, dataType.baseType)
        if (dataType.label != null) {
            result.label = dataType.label
        } else if (
            options.normalizePrefix != null && dataType.name.startsWith(options.normalizePrefix!!)
        ) {
            result.label = dataType.name.substring(options.normalizePrefix!!.length)
        }

        result.identifier = dataType.identifier
        result.retrievable = dataType.isRetrievable
        result.primaryCodePath = dataType.primaryCodePath

        for (genericParameter in dataType.genericParameters) {
            val parameterInfo = TypeParameterInfo()
            parameterInfo.name = genericParameter.identifier
            parameterInfo.constraint = genericParameter.constraint.name
            parameterInfo.constraintType = getTypeName(genericParameter.constraintType)
            result.parameter.add(parameterInfo)
        }

        for (element in dataType.elements) {
            val cie = ClassInfoElement().withName(element.name)
            val elementTypeSpecifier = toTypeSpecifier(element.type)
            if (elementTypeSpecifier is NamedTypeSpecifier) {
                cie.elementType = toTypeName(elementTypeSpecifier)
                if (
                    options.getVersionPolicy() ==
                        ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED
                ) {
                    cie.type = toTypeName(elementTypeSpecifier)
                }
            } else {
                cie.elementTypeSpecifier = elementTypeSpecifier
                if (
                    options.getVersionPolicy() ==
                        ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED
                ) {
                    cie.typeSpecifier = elementTypeSpecifier
                }
            }
            if (element.prohibited) {
                cie.prohibited = true
            }
            result.element.add(cie)
        }

        return result
    }

    private fun toIntervalTypeInfo(dataType: IntervalType): IntervalTypeInfo {
        val result = IntervalTypeInfo()
        val pointTypeSpecifier = toTypeSpecifier(dataType.pointType)
        if (pointTypeSpecifier is NamedTypeSpecifier) {
            result.pointType = toTypeName(pointTypeSpecifier)
        } else {
            result.pointTypeSpecifier = pointTypeSpecifier
        }
        return result
    }

    private fun toListTypeInfo(dataType: ListType): ListTypeInfo {
        val result = ListTypeInfo()
        val elementTypeSpecifier = toTypeSpecifier(dataType.elementType)
        if (elementTypeSpecifier is NamedTypeSpecifier) {
            result.elementType = toTypeName(elementTypeSpecifier)
        } else {
            result.elementTypeSpecifier = elementTypeSpecifier
        }
        return result
    }

    private fun toTupleTypeInfo(dataType: TupleType): TupleTypeInfo {
        val result = TupleTypeInfo()
        setBaseType(result, dataType.baseType)

        for (element in dataType.elements) {
            val infoElement = TupleTypeInfoElement().withName(element.name)

            val elementTypeSpecifier = toTypeSpecifier(element.type)
            if (elementTypeSpecifier is NamedTypeSpecifier) {
                infoElement.elementType = toTypeName(elementTypeSpecifier)
                if (
                    options.getVersionPolicy() ==
                        ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED
                ) {
                    infoElement.type = toTypeName(elementTypeSpecifier)
                }
            } else {
                infoElement.elementTypeSpecifier = elementTypeSpecifier
                if (
                    options.getVersionPolicy() ==
                        ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED
                ) {
                    infoElement.typeSpecifier = elementTypeSpecifier
                }
            }

            result.element.add(infoElement)
        }

        return result
    }

    private fun toTypeSpecifier(dataType: DataType): TypeSpecifier {
        require(dataType !is TupleType) { "Tuple types cannot be used in type specifiers." }
        return when (dataType) {
            is SimpleType -> toNamedTypeSpecifier(dataType)
            is ClassType -> toNamedTypeSpecifier(dataType)
            is IntervalType -> toIntervalTypeSpecifier(dataType)
            is ListType -> toListTypeSpecifier(dataType)
            is ChoiceType -> toChoiceTypeSpecifier(dataType)
            else ->
                throw IllegalArgumentException(
                    "Unknown data type class: ${dataType.javaClass.name}"
                )
        }
    }

    private fun toNamedTypeSpecifier(dataType: NamedType): TypeSpecifier {
        val namedTypeSpecifier =
            NamedTypeSpecifier().withModelName(dataType.namespace).withName(dataType.simpleName)
        return namedTypeSpecifier
    }

    private fun toIntervalTypeSpecifier(dataType: IntervalType): TypeSpecifier {
        val intervalTypeSpecifier = IntervalTypeSpecifier()
        val pointTypeSpecifier = toTypeSpecifier(dataType.pointType)
        if (pointTypeSpecifier is NamedTypeSpecifier) {
            intervalTypeSpecifier.pointType = toTypeName(pointTypeSpecifier)
        } else {
            intervalTypeSpecifier.pointTypeSpecifier = pointTypeSpecifier
        }
        return intervalTypeSpecifier
    }

    private fun toListTypeSpecifier(dataType: ListType): TypeSpecifier {
        val listTypeSpecifier = ListTypeSpecifier()
        val elementTypeSpecifier = toTypeSpecifier(dataType.elementType)
        if (elementTypeSpecifier is NamedTypeSpecifier) {
            listTypeSpecifier.elementType = toTypeName(elementTypeSpecifier)
        } else {
            listTypeSpecifier.elementTypeSpecifier = elementTypeSpecifier
        }
        return listTypeSpecifier
    }

    private fun toChoiceTypeSpecifier(dataType: ChoiceType): TypeSpecifier {
        val choiceTypes: MutableList<TypeSpecifier> = ArrayList<TypeSpecifier>()
        for (choice in dataType.types) {
            choiceTypes.add(toTypeSpecifier(choice))
        }
        val choiceTypeSpecifier = ChoiceTypeSpecifier().withChoice(choiceTypes)
        return choiceTypeSpecifier
    }

    private fun getTypeName(
        schemaTypeName: QName,
        namespaces: MutableMap<String?, String?>,
    ): String {

        var modelName = namespaces[schemaTypeName.namespaceURI]
        if (modelName == null) {
            modelName =
                schemaTypeName
                    .prefix // Doesn't always work, but should be okay for a fallback position...
            if (modelName != null && !modelName.isEmpty()) {
                namespaces[schemaTypeName.namespaceURI] = modelName
            }
        }

        if (modelName != null && !modelName.isEmpty()) {
            return modelName + '.' + schemaTypeName.localPart.replace('-', '_')
        }

        return schemaTypeName.localPart
    }

    @Suppress("ReturnCount")
    private fun resolveType(schemaTypeName: QName?): DataType? {
        if (schemaTypeName == null) {
            return null
        }

        val mapping = options.typeMap[schemaTypeName]
        if (
            mapping != null && mapping.relationship == ModelImporterMapperValue.Relationship.RETYPE
        ) {
            return SYSTEM_CATALOG[mapping.targetSystemClass]!!
        }

        val schemaType = schema.getTypeByName(schemaTypeName)
        if (schemaType == null) {
            val typeName = getTypeName(schemaTypeName, namespaces)
            var resultType = dataTypes[typeName]
            if (resultType == null) {
                if (
                    mapping != null &&
                        mapping.relationship == ModelImporterMapperValue.Relationship.EXTEND
                ) {
                    resultType =
                        SimpleType(typeName, SYSTEM_CATALOG[mapping.targetSystemClass], null)
                } else {
                    resultType = SimpleType(typeName)
                }

                dataTypes[typeName] = resultType
            }

            return resultType
        } else {
            return resolveType(schemaType)
        }
    }

    @Suppress("ReturnCount")
    private fun resolveType(schemaType: XmlSchemaType?): DataType? {
        if (schemaType is XmlSchemaSimpleType) {
            return resolveSimpleType(schemaType)
        } else if (schemaType is XmlSchemaComplexType) {
            return resolveComplexType(schemaType)
        }

        return null
    }

    @Suppress("ReturnCount")
    private fun resolveSimpleType(simpleType: XmlSchemaSimpleType): DataType? {
        if (simpleType.isAnonymous) {
            return null
        }

        val mapping = options.typeMap[simpleType.qName]
        if (
            mapping != null && mapping.relationship == ModelImporterMapperValue.Relationship.RETYPE
        ) {
            return SYSTEM_CATALOG[mapping.targetSystemClass]
        }

        val typeName = getTypeName(simpleType.qName, namespaces)
        var resultType = dataTypes[typeName]
        if (resultType == null) {
            var baseType: DataType? = null
            var retypeToBase = false

            if (
                mapping != null &&
                    mapping.relationship == ModelImporterMapperValue.Relationship.EXTEND
            ) {
                baseType = SYSTEM_CATALOG[mapping.targetSystemClass]
            } else if (simpleType.getContent() is XmlSchemaSimpleTypeRestriction) {
                baseType =
                    resolveType(
                        (simpleType.getContent() as XmlSchemaSimpleTypeRestriction).baseTypeName
                    )
                when (options.getSimpleTypeRestrictionPolicy()) {
                    SimpleTypeRestrictionPolicy.EXTEND_BASETYPE -> {}
                    SimpleTypeRestrictionPolicy.IGNORE -> baseType = null
                    SimpleTypeRestrictionPolicy.USE_BASETYPE -> retypeToBase = true
                }
            }

            if (retypeToBase) {
                resultType = baseType
            } else {
                resultType = SimpleType(typeName)
                dataTypes[typeName] = resultType
            }
        }

        return resultType
    }

    @Suppress("NestedBlockDepth")
    private fun applyConfig(classType: ClassType) {
        if (config != null) {
            for (i in 0..<config.typeInfo.size) {
                val typeConfig: TypeInfo = config.typeInfo[i]
                if (typeConfig is ClassInfo) {
                    val classConfig = typeConfig
                    if (classConfig.name.equals(classType.name)) {
                        classType.identifier = classConfig.identifier
                        classType.label = classConfig.label
                        classType.isRetrievable = classConfig.isRetrievable()!!
                        classType.primaryCodePath = classConfig.primaryCodePath
                    }
                }
            }
        }
    }

    @Suppress(
        "LongMethod",
        "CyclomaticComplexMethod",
        "NestedBlockDepth",
        "ReturnCount",
        "MaxLineLength",
    )
    private fun resolveComplexType(complexType: XmlSchemaComplexType): DataType? {
        if (complexType.isAnonymous) {
            return null
        }

        val mapping = options.typeMap[complexType.qName]
        if (
            mapping != null && mapping.relationship == ModelImporterMapperValue.Relationship.RETYPE
        ) {
            return SYSTEM_CATALOG[mapping.targetSystemClass]
        }

        val typeName = getTypeName(complexType.qName, namespaces)
        var resultType = dataTypes[typeName]
        if (resultType == null) {
            // Resolve the base type, if any

            var baseType: DataType? = null
            if (
                mapping != null &&
                    mapping.relationship == ModelImporterMapperValue.Relationship.EXTEND
            ) {
                baseType = SYSTEM_CATALOG[mapping.targetSystemClass]
            } else if (complexType.getBaseSchemaTypeName() != null) {
                baseType = resolveType(schema.getTypeByName(complexType.getBaseSchemaTypeName()))
            }

            // Create and register the type
            val classType = ClassType(typeName)
            dataTypes[typeName] = classType

            applyConfig(classType)

            val elements: MutableList<ClassTypeElement> = ArrayList<ClassTypeElement>()

            val attributeContent: MutableList<XmlSchemaAttributeOrGroupRef?>
            val particleContent: XmlSchemaParticle?

            if (complexType.contentModel != null) {
                val content = complexType.contentModel.content
                if (content is XmlSchemaComplexContentRestriction) {
                    val restrictionContent = content
                    attributeContent = restrictionContent.attributes
                    particleContent = restrictionContent.particle
                } else if (content is XmlSchemaComplexContentExtension) {
                    val extensionContent = content
                    attributeContent = extensionContent.attributes
                    particleContent = extensionContent.particle
                } else if (content is XmlSchemaSimpleContentRestriction) {
                    val restrictionContent = content

                    val valueType = resolveType(restrictionContent.baseTypeName)!!
                    val valueElement = ClassTypeElement("value", valueType, false, false, null)
                    elements.add(valueElement)

                    attributeContent = restrictionContent.attributes
                    particleContent = null
                } else if (content is XmlSchemaSimpleContentExtension) {
                    val extensionContent = content
                    attributeContent = extensionContent.attributes
                    particleContent = null

                    val valueType = resolveType(extensionContent.baseTypeName)!!
                    val valueElement = ClassTypeElement("value", valueType, false, false, null)
                    elements.add(valueElement)
                } else {
                    throw IllegalArgumentException(
                        "Unrecognized Schema Content: " + content.toString()
                    )
                }
            } else {
                attributeContent = complexType.attributes
                particleContent = complexType.particle
            }

            for (attribute in attributeContent) {
                resolveClassTypeElements(attribute, elements)
            }

            if (particleContent != null) {
                val particle: XmlSchemaParticle? = particleContent
                resolveClassTypeElements(particle, elements)
            }

            // TODO: Map elements to basetype if this or one of its parents is a configured
            // extension of a CQL basetype.
            // This could get complicated...

            // Filter out elements already in the base class
            if (baseType is ClassType) {
                val cBase = baseType
                elements.removeAll(cBase.allElements)
            }

            for (element in elements) {
                try {
                    classType.addElement(element)
                } catch (e: InvalidRedeclarationException) {
                    when (options.getElementRedeclarationPolicy()) {
                        ElementRedeclarationPolicy.FAIL_INVALID_REDECLARATIONS -> {
                            System.err.println(
                                "Redeclaration failed.  Either fix the XSD or choose a different element-redeclaration-policy."
                            )
                            throw e
                        }

                        ElementRedeclarationPolicy.DISCARD_INVALID_REDECLARATIONS ->
                            System.err.printf("%s. Discarding element redeclaration.%n", e.message)

                        ElementRedeclarationPolicy.RENAME_INVALID_REDECLARATIONS -> {
                            val tName = getTypeName(element.type)
                            val name = StringBuilder(element.name).append(tName[0].uppercaseChar())
                            if (tName.length > 1) {
                                name.append(tName.substring(1))
                            }
                            System.err.printf(
                                "%s. Renaming element to %s.%n",
                                e.message,
                                name.toString(),
                            )
                            classType.addElement(
                                ClassTypeElement(
                                    name.toString(),
                                    element.type,
                                    element.prohibited,
                                    element.oneBased,
                                    null,
                                )
                            )
                        }
                    }
                }
            }
            resultType = classType
        }

        return resultType
    }

    private fun getTypeName(type: DataType?): String {
        val typeName: String
        if (type is ClassType) {
            typeName = type.simpleName
        } else if (type is SimpleType) {
            typeName = type.simpleName
        } else if (type is ListType) {
            val elementType = type.elementType
            typeName = getTypeName(elementType) + "List"
        } else if (type is IntervalType) {
            val pointType = type.pointType
            typeName = getTypeName(pointType) + "Interval"
        } else if (type is TupleType) {
            typeName = "Tuple"
        } else if (type is TypeParameter) {
            typeName = "Parameter"
        } else {
            typeName = "Type"
        }

        return typeName
    }

    private fun indexOfFirstDifference(original: String, comparison: String): Int {
        // Returns the index of the first difference between the two strings

        var result = -1
        do {
            result++

            if (
                result >= original.length ||
                    result >= comparison.length ||
                    original[result] != comparison[result]
            ) {
                break
            }
        } while (true)

        return result
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth")
    private fun resolveClassTypeElements(
        particle: XmlSchemaParticle?,
        elements: MutableList<ClassTypeElement>,
    ) {
        if (particle is XmlSchemaElement) {
            val element = resolveClassTypeElement(particle)
            if (element != null) {
                elements.add(element)
            }
        } else if (particle is XmlSchemaSequence) {
            val sequence = particle
            for (member in sequence.items) {
                if (member is XmlSchemaParticle) {
                    resolveClassTypeElements(member as XmlSchemaParticle, elements)
                }
            }
        } else if (particle is XmlSchemaAll) {
            val all = particle
            for (member in all.items) {
                if (member is XmlSchemaParticle) {
                    resolveClassTypeElements(member as XmlSchemaParticle, elements)
                }
            }
        } else if (particle is XmlSchemaChoice) {
            val choice = particle
            var choiceCreated = false
            if (options.getChoiceTypePolicy() == ChoiceTypePolicy.USE_CHOICE) {
                val choices: MutableList<DataType> = ArrayList<DataType>()
                var elementName: String? = null
                for (member in choice.items) {
                    val choiceElement = resolveClassTypeElement((member as XmlSchemaElement?)!!)
                    if (choiceElement != null) {
                        if (elementName == null) {
                            elementName = choiceElement.name
                        } else {
                            val firstDifference =
                                indexOfFirstDifference(elementName, choiceElement.name)
                            if (firstDifference < elementName.length) {
                                elementName = elementName.substring(0, firstDifference)
                            }
                        }
                        choices.add(choiceElement.type)
                    }
                }

                if (elementName != null && !elementName.isEmpty()) {
                    val choiceType = ChoiceType(choices)
                    val element = ClassTypeElement(elementName, choiceType, false, false, null)
                    elements.add(element)
                    choiceCreated = true
                }
            }

            // Some choices don't have a prefix (e.g. FHIR.ResourceContainer)
            // In this case, create an expanded type
            if (!choiceCreated) {
                for (member in choice.items) {
                    if (member is XmlSchemaElement) {
                        val element = resolveClassTypeElement(member)
                        if (element != null) {
                            elements.add(element)
                        }
                    }
                }
            }
        } else if (particle is XmlSchemaGroupRef) {
            val ref = particle
            resolveClassTypeElements(ref.particle, elements)
        }
    }

    private fun resolveClassTypeElement(element: XmlSchemaElement): ClassTypeElement? {
        var element = element
        val isList = element.maxOccurs > 1

        if (element.isRef) {
            element = element.ref.getTarget()
        }

        var elementType: DataType? = null
        val schemaType = element.schemaType
        if (schemaType != null) {
            elementType = resolveType(schemaType)
        } else {
            val schemaTypeName = element.schemaTypeName
            if (schemaTypeName != null) {
                elementType = resolveType(schemaTypeName)
            }
        }

        if (elementType == null) {
            return null // The type is anonymous and will not be represented within the imported
            // model
            // throw new IllegalStateException(String.format("Unable to resolve type %s of element
            // %s.",
            // element.getSchemaType().getName(), element.getName()));
        }

        if (isList) {
            elementType = ListType(elementType)
        }

        val isProhibited = element.minOccurs == 0L && element.maxOccurs == 0L

        return ClassTypeElement(element.name, elementType, isProhibited, false, null)
    }

    private fun resolveClassTypeElement(attribute: XmlSchemaAttribute): ClassTypeElement? {
        var attribute = attribute
        if (attribute.isRef) {
            attribute = attribute.ref.getTarget()
        }

        var elementType: DataType? = null
        val schemaType: XmlSchemaType? = attribute.schemaType
        if (schemaType != null) {
            elementType = resolveType(schemaType)
        } else {
            val schemaTypeName = attribute.schemaTypeName
            if (schemaTypeName != null) {
                elementType = resolveType(schemaTypeName)
            }
        }

        if (elementType == null) {
            return null // The type is anonymous and will not be represented in the imported model
            // throw new IllegalStateException(String.format("Unable to resolve type %s of attribute
            // %s.",
            // attribute.getSchemaTypeName(), attribute.getName()));
        }

        return ClassTypeElement(
            attribute.name,
            elementType,
            attribute.use == XmlSchemaUse.PROHIBITED,
            false,
            null,
        )
    }

    private fun resolveClassTypeElements(
        attribute: XmlSchemaAttributeOrGroupRef?,
        elements: MutableList<ClassTypeElement>,
    ) {
        if (attribute is XmlSchemaAttribute) {
            val element = resolveClassTypeElement(attribute)
            if (element != null) {
                elements.add(element)
            }
        } else if (attribute is XmlSchemaAttributeGroupRef) {
            resolveClassTypeElements(attribute.ref.getTarget(), elements)
        }
    }

    private fun resolveClassTypeElements(
        attributeGroup: XmlSchemaAttributeGroup,
        elements: MutableList<ClassTypeElement>,
    ) {
        for (member in attributeGroup.attributes) {
            if (member is XmlSchemaAttribute) {
                val element = resolveClassTypeElement(member)
                if (element != null) {
                    elements.add(element)
                }
            } else if (member is XmlSchemaAttributeGroupRef) {
                resolveClassTypeElements(member.ref.getTarget(), elements)
            } else if (member is XmlSchemaAttributeGroup) {
                resolveClassTypeElements(member, elements)
            }
        }
    }

    companion object {
        private val SYSTEM_CATALOG: MutableMap<String?, DataType> = systemCatalog

        private val systemCatalog: MutableMap<String?, DataType>
            get() {
                val source =
                    ModelImporter::class
                        .java
                        .getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml")!!
                        .asSource()
                        .buffered()
                val systemModelInfo: ModelInfo = parseModelInfoXml(source)
                val map: MutableMap<String?, DataType> = HashMap<String?, DataType>()
                for (info in systemModelInfo.typeInfo) {
                    if (info is SimpleTypeInfo) {
                        val sInfo = info
                        val qualifiedName: String =
                            getQualifiedName(systemModelInfo.name, sInfo.name!!)
                        map[qualifiedName] = SimpleType(qualifiedName)
                    } else if (info is ClassInfo) {
                        val cInfo = info
                        val qualifiedName: String =
                            getQualifiedName(systemModelInfo.name, cInfo.name!!)
                        map[qualifiedName] = ClassType(qualifiedName)
                    }
                }
                return map
            }

        private fun getQualifiedName(modelName: String?, name: String): String {
            if (name.startsWith(modelName + ".")) {
                return name
            }

            return "$modelName.$name"
        }

        @JvmStatic
        fun fromXsd(
            schema: XmlSchema,
            options: ModelImporterOptions?,
            config: ModelInfo?,
        ): ModelInfo {
            val importer = ModelImporter(schema, options, config)
            return importer.importXsd()
        }
    }
}
