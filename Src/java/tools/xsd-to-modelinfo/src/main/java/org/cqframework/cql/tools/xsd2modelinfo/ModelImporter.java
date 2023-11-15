package org.cqframework.cql.tools.xsd2modelinfo;

import org.apache.ws.commons.schema.*;
import org.hl7.cql.model.*;
import org.hl7.elm_modelinfo.r1.*;

import jakarta.xml.bind.JAXB;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.ChoiceTypePolicy.USE_CHOICE;

public class ModelImporter {
    private static final Map<String, DataType> SYSTEM_CATALOG = getSystemCatalog();
    private static Map<String, DataType> getSystemCatalog() {
        ModelInfo systemModelInfo = JAXB.unmarshal(
                ModelImporter.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"),
                ModelInfo.class);

        final Map<String, DataType> map = new HashMap<>();
        for (TypeInfo info : systemModelInfo.getTypeInfo()) {
            if (info instanceof SimpleTypeInfo) {
                SimpleTypeInfo sInfo = (SimpleTypeInfo) info;
                String qualifiedName = getQualifiedName(systemModelInfo.getName(), sInfo.getName());
                map.put(qualifiedName, new SimpleType(qualifiedName));
            } else if (info instanceof ClassInfo) {
                ClassInfo cInfo = (ClassInfo) info;
                String qualifiedName = getQualifiedName(systemModelInfo.getName(), cInfo.getName());
                map.put(qualifiedName, new ClassType(qualifiedName));
            }
        }
        return map;
    }

    private static String getQualifiedName(String modelName, String name) {
        if (name.startsWith(modelName + ".")) {
            return name;
        }

        return String.format("%s.%s", modelName, name);
    }

    private final XmlSchema schema;
    private final ModelInfo config;
    private final ModelImporterOptions options;
    private final Map<String, DataType> dataTypes;
    private final Map<String, String> namespaces;

    private ModelImporter(XmlSchema schema, ModelImporterOptions options, ModelInfo config) {
        this.schema = schema;
        this.config = config;
        this.dataTypes = new HashMap<>();
        this.namespaces = new HashMap<>();

        // Load default options first
        ModelImporterOptions tmpOptions;
        try (InputStream defaultPropertiesIS = getClass().getResourceAsStream("default_options.properties")) {
            tmpOptions = ModelImporterOptions.loadFromProperties(defaultPropertiesIS);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load default properties", e);
        }

        // If options were passed in, apply them on top of the default options
        if (options != null) {
            tmpOptions.applyProperties(options.exportProperties());
        }

        this.options = tmpOptions;
    }

    public static ModelInfo fromXsd(XmlSchema schema, ModelImporterOptions options, ModelInfo config) {
        ModelImporter importer = new ModelImporter(schema, options, config);
        return importer.importXsd();
    }

    public ModelInfo importXsd() {
        if (options.getModel() == null || options.getModel().isEmpty()) {
            throw new IllegalArgumentException("Model name is required.");
        }
        namespaces.put(schema.getTargetNamespace(), options.getModel());

        for (XmlSchemaType schemaType : schema.getSchemaTypes().values()) {
            resolveType(schemaType);
        }

        return new ModelInfo()
                .withName(options.getModel())
                .withTargetQualifier(options.getModel().toLowerCase())
                .withUrl(schema.getTargetNamespace())
                .withPatientClassName(config != null ? config.getPatientClassName() : null)
                .withPatientClassIdentifier(config != null ? config.getPatientClassIdentifier() : null)
                .withPatientBirthDatePropertyName(config != null ? config.getPatientBirthDatePropertyName() : null)
                .withTypeInfo(dataTypes.values().stream()
                        .map(this::toTypeInfo)
                        .collect(Collectors.toList()));
    }

    private TypeInfo toTypeInfo(DataType dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType is null");
        }

        if (dataType instanceof SimpleType) {
            return toSimpleTypeInfo((SimpleType) dataType);
        } else if (dataType instanceof ClassType) {
            return toClassInfo((ClassType) dataType);
        } else if (dataType instanceof IntervalType) {
            return toIntervalTypeInfo((IntervalType) dataType);
        } else if (dataType instanceof ListType) {
            return toListTypeInfo((ListType) dataType);
        } else if (dataType instanceof TupleType) {
            return toTupleTypeInfo((TupleType) dataType);
        } else {
            throw new IllegalArgumentException(String.format("Unknown data type class: %s", dataType.getClass().getName()));
        }
    }

    private String toTypeName(NamedTypeSpecifier typeSpecifier) {
        if (typeSpecifier.getModelName() != null) {
            return String.format("%s.%s", typeSpecifier.getModelName(), typeSpecifier.getName());
        }
        return typeSpecifier.getName();
    }

    private void setBaseType(TypeInfo typeInfo, DataType baseType) {
        TypeSpecifier baseTypeSpecifier = toTypeSpecifier(baseType);
        if (baseTypeSpecifier instanceof NamedTypeSpecifier) {
            typeInfo.setBaseType(toTypeName((NamedTypeSpecifier)baseTypeSpecifier));
        }
        else {
            typeInfo.setBaseTypeSpecifier(baseTypeSpecifier);
        }
    }

    private SimpleTypeInfo toSimpleTypeInfo(SimpleType dataType) {
        SimpleTypeInfo result = new SimpleTypeInfo();
        result.setName(dataType.getSimpleName());
        if (dataType.getBaseType() != null) {
            setBaseType(result, dataType.getBaseType());
        }

        return result;
    }

    private ClassInfo toClassInfo(ClassType dataType) {
        ClassInfo result = new ClassInfo();
        result.setName(dataType.getSimpleName());
        if (dataType.getBaseType() != null) {
            setBaseType(result, dataType.getBaseType());
        }
        if (dataType.getLabel() != null) {
            result.setLabel(dataType.getLabel());
        }
        else if (options.getNormalizePrefix() != null && dataType.getName().startsWith(options.getNormalizePrefix())) {
            result.setLabel(dataType.getName().substring(options.getNormalizePrefix().length()));
        }

        result.setIdentifier(dataType.getIdentifier());
        result.setRetrievable(dataType.isRetrievable());
        result.setPrimaryCodePath(dataType.getPrimaryCodePath());

        for(TypeParameter genericParameter : dataType.getGenericParameters()) {
            TypeParameterInfo parameterInfo = new TypeParameterInfo();
            parameterInfo.setName(genericParameter.getIdentifier());
            parameterInfo.setConstraint(genericParameter.getConstraint().name());
            parameterInfo.setConstraintType(getTypeName(genericParameter.getConstraintType()));
            result.getParameter().add(parameterInfo);
        }

        for (ClassTypeElement element : dataType.getElements()) {
            ClassInfoElement cie = new ClassInfoElement().withName(element.getName());
            TypeSpecifier elementTypeSpecifier = toTypeSpecifier(element.getType());
            if (elementTypeSpecifier instanceof NamedTypeSpecifier) {
                cie.setElementType(toTypeName((NamedTypeSpecifier)elementTypeSpecifier));
                if (options.getVersionPolicy() == ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED) {
                    cie.setType(toTypeName((NamedTypeSpecifier) elementTypeSpecifier));
                }
            }
            else {
                cie.setElementTypeSpecifier(elementTypeSpecifier);
                if (options.getVersionPolicy() == ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED) {
                    cie.setTypeSpecifier(elementTypeSpecifier);
                }
            }
            if (element.isProhibited()) {
                cie.setProhibited(true);
            }
            result.getElement().add(cie);
        }

        return result;
    }

    private IntervalTypeInfo toIntervalTypeInfo(IntervalType dataType) {
        IntervalTypeInfo result = new IntervalTypeInfo();
        TypeSpecifier pointTypeSpecifier = toTypeSpecifier(dataType.getPointType());
        if (pointTypeSpecifier instanceof NamedTypeSpecifier) {
            result.setPointType(toTypeName((NamedTypeSpecifier)pointTypeSpecifier));
        }
        else {
            result.setPointTypeSpecifier(pointTypeSpecifier);
        }
        return result;
    }

    private ListTypeInfo toListTypeInfo(ListType dataType) {
        ListTypeInfo result = new ListTypeInfo();
        TypeSpecifier elementTypeSpecifier = toTypeSpecifier(dataType.getElementType());
        if (elementTypeSpecifier instanceof NamedTypeSpecifier) {
            result.setElementType(toTypeName((NamedTypeSpecifier)elementTypeSpecifier));
        }
        else {
            result.setElementTypeSpecifier(elementTypeSpecifier);
        }
        return result;
    }

    private TupleTypeInfo toTupleTypeInfo(TupleType dataType) {
        TupleTypeInfo result = new TupleTypeInfo();
        if (dataType.getBaseType() != null) {
            setBaseType(result, dataType.getBaseType());
        }

        for (TupleTypeElement element : dataType.getElements()) {
            TupleTypeInfoElement infoElement = new TupleTypeInfoElement()
                    .withName(element.getName());

            TypeSpecifier elementTypeSpecifier = toTypeSpecifier(element.getType());
            if (elementTypeSpecifier instanceof NamedTypeSpecifier) {
                infoElement.setElementType(toTypeName((NamedTypeSpecifier) elementTypeSpecifier));
                if (options.getVersionPolicy() == ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED) {
                    infoElement.setType(toTypeName((NamedTypeSpecifier) elementTypeSpecifier));
                }
            }
            else {
                infoElement.setElementTypeSpecifier(elementTypeSpecifier);
                if (options.getVersionPolicy() == ModelImporterOptions.VersionPolicy.INCLUDE_DEPRECATED) {
                    infoElement.setTypeSpecifier(elementTypeSpecifier);
                }
            }

            result.getElement().add(infoElement);
        }

        return result;
    }

    private TypeSpecifier toTypeSpecifier(DataType dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType is null");
        }

        if (dataType instanceof SimpleType) {
            return toNamedTypeSpecifier((SimpleType) dataType);
        } else if (dataType instanceof ClassType) {
            return toNamedTypeSpecifier((ClassType) dataType);
        } else if (dataType instanceof IntervalType) {
            return toIntervalTypeSpecifier((IntervalType) dataType);
        } else if (dataType instanceof ListType) {
            return toListTypeSpecifier((ListType) dataType);
        } else if (dataType instanceof ChoiceType) {
            return toChoiceTypeSpecifier((ChoiceType) dataType);
        } else if (dataType instanceof TupleType) {
            throw new IllegalArgumentException("Tuple types cannot be used in type specifiers.");
        } else {
            throw new IllegalArgumentException(String.format("Unknown data type class: %s", dataType.getClass().getName()));
        }
    }

    private TypeSpecifier toNamedTypeSpecifier(NamedType dataType) {
        NamedTypeSpecifier namedTypeSpecifier = new NamedTypeSpecifier()
                .withModelName(dataType.getNamespace())
                .withName(dataType.getSimpleName());
        return namedTypeSpecifier;
    }

    private TypeSpecifier toIntervalTypeSpecifier(IntervalType dataType) {
        IntervalTypeSpecifier intervalTypeSpecifier = new IntervalTypeSpecifier();
        TypeSpecifier pointTypeSpecifier = toTypeSpecifier(dataType.getPointType());
        if (pointTypeSpecifier instanceof NamedTypeSpecifier) {
            intervalTypeSpecifier.setPointType(toTypeName((NamedTypeSpecifier)pointTypeSpecifier));
        }
        else {
            intervalTypeSpecifier.setPointTypeSpecifier(pointTypeSpecifier);
        }
        return intervalTypeSpecifier;
    }

    private TypeSpecifier toListTypeSpecifier(ListType dataType) {
        ListTypeSpecifier listTypeSpecifier = new ListTypeSpecifier();
        TypeSpecifier elementTypeSpecifier = toTypeSpecifier(dataType.getElementType());
        if (elementTypeSpecifier instanceof NamedTypeSpecifier) {
            listTypeSpecifier.setElementType(toTypeName((NamedTypeSpecifier)elementTypeSpecifier));
        }
        else {
            listTypeSpecifier.setElementTypeSpecifier(elementTypeSpecifier);
        }
        return listTypeSpecifier;
    }

    private TypeSpecifier toChoiceTypeSpecifier(ChoiceType dataType) {
        List<TypeSpecifier> choiceTypes = new ArrayList<>();
        for (DataType choice : dataType.getTypes()) {
            choiceTypes.add(toTypeSpecifier(choice));
        }
        ChoiceTypeSpecifier choiceTypeSpecifier = new ChoiceTypeSpecifier()
                .withChoice(choiceTypes);
        return choiceTypeSpecifier;
    }

    private String getTypeName(QName schemaTypeName, Map<String, String> namespaces) {
        if (schemaTypeName == null) {
            throw new IllegalArgumentException("schemaTypeName is null");
        }

        String modelName = namespaces.get(schemaTypeName.getNamespaceURI());
        if (modelName == null) {
            modelName = schemaTypeName.getPrefix(); // Doesn't always work, but should be okay for a fallback position...
            if (modelName != null && ! modelName.isEmpty()) {
                namespaces.put(schemaTypeName.getNamespaceURI(), modelName);
            }
        }

        if (modelName != null && ! modelName.isEmpty()) {
            return modelName + '.' + schemaTypeName.getLocalPart().replace('-', '_');
        }

        return schemaTypeName.getLocalPart();
    }

    private DataType resolveType(QName schemaTypeName) {
        if (schemaTypeName == null) {
            return null;
        }

        ModelImporterMapperValue mapping = options.getTypeMap().get(schemaTypeName);
        if (mapping != null && mapping.getRelationship() == ModelImporterMapperValue.Relationship.RETYPE) {
            return SYSTEM_CATALOG.get(mapping.getTargetSystemClass());
        }

        XmlSchemaType schemaType = schema.getTypeByName(schemaTypeName);
        if (schemaType == null) {
            String typeName = getTypeName(schemaTypeName, namespaces);
            DataType resultType = dataTypes.get(typeName);
            if (resultType == null) {
                if (mapping != null && mapping.getRelationship() == ModelImporterMapperValue.Relationship.EXTEND) {
                    resultType = new SimpleType(typeName, SYSTEM_CATALOG.get(mapping.getTargetSystemClass()));
                } else {
                    resultType = new SimpleType(typeName);
                }

                dataTypes.put(typeName, resultType);
            }

            return resultType;
        }
        else {
            return resolveType(schemaType);
        }
    }

    private DataType resolveType(XmlSchemaType schemaType) {
        if (schemaType instanceof XmlSchemaSimpleType) {
            return resolveSimpleType((XmlSchemaSimpleType)schemaType);
        }
        else if (schemaType instanceof XmlSchemaComplexType) {
            return resolveComplexType((XmlSchemaComplexType)schemaType);
        }

        return null;
    }

    private DataType resolveSimpleType(XmlSchemaSimpleType simpleType) {
        if (simpleType.isAnonymous()) {
            return null;
        }

        ModelImporterMapperValue mapping = options.getTypeMap().get(simpleType.getQName());
        if (mapping != null && mapping.getRelationship() == ModelImporterMapperValue.Relationship.RETYPE) {
            return SYSTEM_CATALOG.get(mapping.getTargetSystemClass());
        }

        String typeName = getTypeName(simpleType.getQName(), namespaces);
        DataType resultType = dataTypes.get(typeName);
        if (resultType == null) {
            DataType baseType = null;
            boolean retypeToBase = false;

            if (mapping != null && mapping.getRelationship() == ModelImporterMapperValue.Relationship.EXTEND) {
                baseType = SYSTEM_CATALOG.get(mapping.getTargetSystemClass());
            }
            else if (simpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
                baseType = resolveType(((XmlSchemaSimpleTypeRestriction) simpleType.getContent()).getBaseTypeName());
                switch (options.getSimpleTypeRestrictionPolicy()) {
                    case EXTEND_BASETYPE:
                        break;
                    case IGNORE:
                        baseType = null;
                        break;
                    case USE_BASETYPE:
                    default:
                        retypeToBase = true;
                }
            }

            if (retypeToBase) {
                resultType = baseType;
            }
            else {
                resultType = new SimpleType(typeName, baseType);
                dataTypes.put(typeName, resultType);
            }
        }

        return resultType;
    }

    private void applyConfig(ClassType classType) {
        if (config != null) {
            for (int i = 0; i < config.getTypeInfo().size(); i++) {
                TypeInfo typeConfig = config.getTypeInfo().get(i);
                if (typeConfig instanceof ClassInfo) {
                    ClassInfo classConfig = (ClassInfo)typeConfig;
                    if (classConfig.getName().equals(classType.getName())) {
                        classType.setIdentifier(classConfig.getIdentifier());
                        classType.setLabel(classConfig.getLabel());
                        classType.setRetrievable(classConfig.isRetrievable());
                        classType.setPrimaryCodePath(classConfig.getPrimaryCodePath());
                    }
                }
            }
        }
    }

    private DataType resolveComplexType(XmlSchemaComplexType complexType) {
        if (complexType.isAnonymous()) {
            return null;
        }

        ModelImporterMapperValue mapping = options.getTypeMap().get(complexType.getQName());
        if (mapping != null && mapping.getRelationship() == ModelImporterMapperValue.Relationship.RETYPE) {
            return SYSTEM_CATALOG.get(mapping.getTargetSystemClass());
        }

        String typeName = getTypeName(complexType.getQName(), namespaces);
        DataType resultType = dataTypes.get(typeName);
        if (resultType == null) {

            // Resolve the base type, if any
            DataType baseType = null;
            if (mapping != null && mapping.getRelationship() == ModelImporterMapperValue.Relationship.EXTEND) {
                baseType = SYSTEM_CATALOG.get(mapping.getTargetSystemClass());
            } else if (complexType.getBaseSchemaTypeName() != null) {
                baseType = resolveType(schema.getTypeByName(complexType.getBaseSchemaTypeName()));
            }

            // Create and register the type
            ClassType classType = new ClassType(typeName, baseType);
            dataTypes.put(typeName, classType);

            applyConfig(classType);

            List<ClassTypeElement> elements = new ArrayList<>();

            List<XmlSchemaAttributeOrGroupRef> attributeContent;
            XmlSchemaParticle particleContent;

            if (complexType.getContentModel() != null) {
                XmlSchemaContent content = complexType.getContentModel().getContent();
                if (content instanceof XmlSchemaComplexContentRestriction) {
                    XmlSchemaComplexContentRestriction restrictionContent = (XmlSchemaComplexContentRestriction)content;
                    attributeContent = restrictionContent.getAttributes();
                    particleContent = restrictionContent.getParticle();
                }
                else if (content instanceof XmlSchemaComplexContentExtension) {
                    XmlSchemaComplexContentExtension extensionContent = (XmlSchemaComplexContentExtension)content;
                    attributeContent = extensionContent.getAttributes();
                    particleContent = extensionContent.getParticle();
                }
                // For complex types with simple content, create a new class type with a value element for the content
                else if (content instanceof XmlSchemaSimpleContentRestriction) {
                    XmlSchemaSimpleContentRestriction restrictionContent = (XmlSchemaSimpleContentRestriction)content;

                    DataType valueType = resolveType(restrictionContent.getBaseTypeName());
                    ClassTypeElement valueElement = new ClassTypeElement("value", valueType, false, false, null);
                    elements.add(valueElement);

                    attributeContent = restrictionContent.getAttributes();
                    particleContent = null;
                }
                else if (content instanceof XmlSchemaSimpleContentExtension) {
                    XmlSchemaSimpleContentExtension extensionContent = (XmlSchemaSimpleContentExtension)content;
                    attributeContent = extensionContent.getAttributes();
                    particleContent = null;

                    DataType valueType = resolveType(extensionContent.getBaseTypeName());
                    ClassTypeElement valueElement = new ClassTypeElement("value", valueType, false, false, null);
                    elements.add(valueElement);
                }
                else {
                    throw new IllegalArgumentException("Unrecognized Schema Content: " + content.toString());
                }
            }
            else {
                attributeContent = complexType.getAttributes();
                particleContent = complexType.getParticle();
            }

            for (XmlSchemaAttributeOrGroupRef attribute : attributeContent) {
                resolveClassTypeElements(attribute, elements);
            }

            if (particleContent != null) {
                XmlSchemaParticle particle = particleContent;
                resolveClassTypeElements(particle, elements);
            }

            // TODO: Map elements to basetype if this or one of its parents is a configured extension of a CQL basetype.
            // This could get complicated...

            // Filter out elements already in the base class
            if (baseType instanceof ClassType) {
                ClassType cBase = (ClassType) baseType;
                elements.removeAll(cBase.getAllElements());
            }

            for (ClassTypeElement element : elements) {
                try {
                    classType.addElement(element);
                } catch (InvalidRedeclarationException e) {
                    switch (options.getElementRedeclarationPolicy()) {
                        case FAIL_INVALID_REDECLARATIONS:
                            System.err.println("Redeclaration failed.  Either fix the XSD or choose a different element-redeclaration-policy.");
                            throw e;
                        case DISCARD_INVALID_REDECLARATIONS:
                            System.err.printf("%s. Discarding element redeclaration.%n", e.getMessage());
                            break;
                        case RENAME_INVALID_REDECLARATIONS:
                        default:
                            String tName = getTypeName(element.getType());
                            StringBuilder name = new StringBuilder(element.getName()).append(Character.toUpperCase(tName.charAt(0)));
                            if (tName.length() > 1) {
                                name.append(tName.substring(1));
                            }
                            System.err.printf("%s. Renaming element to %s.%n", e.getMessage(), name.toString());
                            classType.addElement(new ClassTypeElement(name.toString(), element.getType(), element.isProhibited(), element.isOneBased(), null));
                    }
                }
            }
            resultType = classType;
        }

        return resultType;
    }

    private String getTypeName(DataType type) {
        String typeName;
        if (type instanceof ClassType) {
            typeName = ((ClassType) type).getSimpleName();
        } else if (type instanceof SimpleType) {
            typeName = ((SimpleType) type).getSimpleName();
        } else if (type instanceof ListType) {
            DataType elementType = ((ListType) type).getElementType();
            typeName = getTypeName(elementType) + "List";
        } else if (type instanceof IntervalType) {
            DataType pointType = ((IntervalType) type).getPointType();
            typeName = getTypeName(pointType) + "Interval";
        } else if (type instanceof TupleType) {
            typeName = "Tuple";
        } else if (type instanceof TypeParameter) {
            typeName = "Parameter";
        } else {
            typeName = "Type";
        }

        return typeName;
    }

    private int indexOfFirstDifference(String original, String comparison) {
        // Returns the index of the first difference between the two strings
        if (original == null) {
            throw new IllegalArgumentException("original is null");
        }

        if (comparison == null) {
            throw new IllegalArgumentException("comparison is null");
        }

        int result = -1;
        do {
            result++;

            if (result >= original.length() || result >= comparison.length() || original.charAt(result) != comparison.charAt(result)) {
                break;
            }
        } while (true);

        return result;
    }

    private void resolveClassTypeElements(XmlSchemaParticle particle, List<ClassTypeElement> elements) {
        if (particle instanceof XmlSchemaElement) {
            ClassTypeElement element = resolveClassTypeElement((XmlSchemaElement)particle);
            if (element != null) {
                elements.add(element);
            }
        }
        else if (particle instanceof XmlSchemaSequence) {
            XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
            for (XmlSchemaSequenceMember member : sequence.getItems()) {
                if (member instanceof XmlSchemaParticle) {
                    resolveClassTypeElements((XmlSchemaParticle) member, elements);
                }
            }
        }
        else if (particle instanceof XmlSchemaAll) {
            XmlSchemaAll all = (XmlSchemaAll)particle;
            for (XmlSchemaAllMember member : all.getItems()) {
                if (member instanceof XmlSchemaParticle) {
                    resolveClassTypeElements((XmlSchemaParticle) member, elements);
                }
            }
        }
        else if (particle instanceof XmlSchemaChoice) {
            XmlSchemaChoice choice = (XmlSchemaChoice)particle;
            boolean choiceCreated = false;
            if (options.getChoiceTypePolicy() == USE_CHOICE) {
                List<DataType> choices = new ArrayList<DataType>();
                String elementName = null;
                for (XmlSchemaChoiceMember member : choice.getItems()) {
                    ClassTypeElement choiceElement = resolveClassTypeElement((XmlSchemaElement) member);
                    if (choiceElement != null) {
                        if (elementName == null) {
                            elementName = choiceElement.getName();
                        }
                        else {
                            int firstDifference = indexOfFirstDifference(elementName, choiceElement.getName());
                            if (firstDifference < elementName.length()) {
                                elementName = elementName.substring(0, firstDifference);
                            }
                        }
                        choices.add(choiceElement.getType());
                    }
                }

                if (elementName != null && !elementName.isEmpty()) {
                    ChoiceType choiceType = new ChoiceType(choices);
                    ClassTypeElement element = new ClassTypeElement(elementName, choiceType, false, false, null);
                    elements.add(element);
                    choiceCreated = true;
                }
            }

            // Some choices don't have a prefix (e.g. FHIR.ResourceContainer)
            // In this case, create an expanded type
            if (!choiceCreated) {
                for (XmlSchemaChoiceMember member : choice.getItems()) {
                    if (member instanceof XmlSchemaElement) {
                        ClassTypeElement element = resolveClassTypeElement((XmlSchemaElement) member);
                        if (element != null) {
                            elements.add(element);
                        }
                    }
                }
            }
        }
        else if (particle instanceof XmlSchemaGroupRef) {
            XmlSchemaGroupRef ref = (XmlSchemaGroupRef)particle;
            resolveClassTypeElements(ref.getParticle(), elements);
        }
    }

    private ClassTypeElement resolveClassTypeElement(XmlSchemaElement element) {
        boolean isList = element.getMaxOccurs() > 1;

        if (element.isRef()) {
            element = element.getRef().getTarget();
        }

        DataType elementType = null;
        XmlSchemaType schemaType = element.getSchemaType();
        if (schemaType != null) {
            elementType = resolveType(schemaType);
        }
        else {
            QName schemaTypeName = element.getSchemaTypeName();
            if (schemaTypeName != null) {
                elementType = resolveType(schemaTypeName);
            }
        }

        if (elementType == null) {
            return null; // The type is anonymous and will not be represented within the imported model
            //throw new IllegalStateException(String.format("Unable to resolve type %s of element %s.", element.getSchemaType().getName(), element.getName()));
        }

        if (isList) {
            elementType = new ListType(elementType);
        }

        boolean isProhibited = element.getMinOccurs() == 0L && element.getMaxOccurs() == 0L;

        return new ClassTypeElement(element.getName(), elementType, isProhibited, false, null);
    }

    private ClassTypeElement resolveClassTypeElement(XmlSchemaAttribute attribute) {
        if (attribute.isRef()) {
            attribute = attribute.getRef().getTarget();
        }

        DataType elementType = null;
        XmlSchemaType schemaType = attribute.getSchemaType();
        if (schemaType != null) {
            elementType = resolveType(schemaType);
        }
        else {
            QName schemaTypeName = attribute.getSchemaTypeName();
            if (schemaTypeName != null) {
                elementType = resolveType(schemaTypeName);
            }
        }

        if (elementType == null) {
            return null; // The type is anonymous and will not be represented in the imported model
            //throw new IllegalStateException(String.format("Unable to resolve type %s of attribute %s.", attribute.getSchemaTypeName(), attribute.getName()));
        }

        return new ClassTypeElement(attribute.getName(), elementType, attribute.getUse() == XmlSchemaUse.PROHIBITED, false, null);
    }

    private void resolveClassTypeElements(XmlSchemaAttributeOrGroupRef attribute, List<ClassTypeElement> elements) {
        if (attribute instanceof XmlSchemaAttribute) {
            ClassTypeElement element = resolveClassTypeElement((XmlSchemaAttribute)attribute);
            if (element != null) {
                elements.add(element);
            }
        }
        else if (attribute instanceof XmlSchemaAttributeGroupRef) {
            resolveClassTypeElements(((XmlSchemaAttributeGroupRef)attribute).getRef().getTarget(), elements);
        }
    }

    private void resolveClassTypeElements(XmlSchemaAttributeGroup attributeGroup, List<ClassTypeElement> elements) {
        for (XmlSchemaAttributeGroupMember member : attributeGroup.getAttributes()) {
            if (member instanceof XmlSchemaAttribute) {
                ClassTypeElement element = resolveClassTypeElement((XmlSchemaAttribute)member);
                if (element != null) {
                    elements.add(element);
                }
            }
            else if (member instanceof XmlSchemaAttributeGroupRef) {
                resolveClassTypeElements(((XmlSchemaAttributeGroupRef)member).getRef().getTarget(), elements);
            }
            else if (member instanceof XmlSchemaAttributeGroup) {
                resolveClassTypeElements((XmlSchemaAttributeGroup)member, elements);
            }
        }
    }
}
