package org.cqframework.cql.tools.xsd2modelinfo;

import org.apache.ws.commons.schema.*;
import org.cqframework.cql.elm.tracking.*;
import org.hl7.elm_modelinfo.r1.*;

import static org.apache.ws.commons.schema.constants.Constants.*;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;
import java.util.*;
import java.util.stream.Collectors;

public class ModelImporter {
    private static final Map<String, DataType> SYSTEM_CATALOG = getSystemCatalog();
    private static final Map<QName, DataType> SYSTEM_TYPE_MAP = new HashMap<QName, DataType>(){{
        put(XSD_ANY,                SYSTEM_CATALOG.get("System.Any"));
        put(XSD_ANYSIMPLETYPE,      SYSTEM_CATALOG.get("System.Any"));
        put(XSD_ANYTYPE,            SYSTEM_CATALOG.get("System.Any"));
        put(XSD_ANYURI,             SYSTEM_CATALOG.get("System.String"));
//      put(XSD_BASE64,             SYSTEM_CATALOG.get("System.String"));
        put(XSD_BOOLEAN,            SYSTEM_CATALOG.get("System.Boolean"));
        put(XSD_BYTE,               SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_DATE,               SYSTEM_CATALOG.get("System.DateTime"));
        put(XSD_DATETIME,           SYSTEM_CATALOG.get("System.DateTime"));
//      put(XSD_DAY,                SYSTEM_CATALOG.get("System.DateTime"));
        put(XSD_DECIMAL,            SYSTEM_CATALOG.get("System.Decimal"));
        put(XSD_DOUBLE,             SYSTEM_CATALOG.get("System.Boolean"));
        put(XSD_DURATION,           SYSTEM_CATALOG.get("System.Quantity"));
        put(XSD_ENTITIES,           SYSTEM_CATALOG.get("list<System.String>"));
        put(XSD_ENTITY,             SYSTEM_CATALOG.get("System.String"));
        put(XSD_FLOAT,              SYSTEM_CATALOG.get("System.Decimal"));
//      put(XSD_HEXBIN,             SYSTEM_CATALOG.get("System.String"));
        put(XSD_ID,                 SYSTEM_CATALOG.get("System.String"));
        put(XSD_IDREF,              SYSTEM_CATALOG.get("System.String"));
        put(XSD_IDREFS,             SYSTEM_CATALOG.get("list<System.String>"));
        put(XSD_INT,                SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_INTEGER,            SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_LANGUAGE,           SYSTEM_CATALOG.get("System.String"));
        put(XSD_LONG,               SYSTEM_CATALOG.get("System.Integer"));
//      put(XSD_MONTH,              SYSTEM_CATALOG.get("System.DateTime"));
//      put(XSD_MONTHDAY,           SYSTEM_CATALOG.get("System.DateTime"));
        put(XSD_NAME,               SYSTEM_CATALOG.get("System.String"));
        put(XSD_NCNAME,             SYSTEM_CATALOG.get("System.String"));
        put(XSD_NEGATIVEINTEGER,    SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_NMTOKEN,            SYSTEM_CATALOG.get("System.String"));
        put(XSD_NMTOKENS,           SYSTEM_CATALOG.get("list<System.String>"));
        put(XSD_NONNEGATIVEINTEGER, SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_NONPOSITIVEINTEGER, SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_NORMALIZEDSTRING,   SYSTEM_CATALOG.get("System.String"));
        put(XSD_POSITIVEINTEGER,    SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_QNAME,              SYSTEM_CATALOG.get("System.String"));
        put(XSD_SHORT,              SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_STRING,             SYSTEM_CATALOG.get("System.String"));
        put(XSD_TIME,               SYSTEM_CATALOG.get("System.Time"));
        put(XSD_TOKEN,              SYSTEM_CATALOG.get("System.String"));
        put(XSD_UNSIGNEDBYTE,       SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_UNSIGNEDINT,        SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_UNSIGNEDLONG,       SYSTEM_CATALOG.get("System.Integer"));
        put(XSD_UNSIGNEDSHORT,      SYSTEM_CATALOG.get("System.Integer"));
//      put(XSD_YEAR,               SYSTEM_CATALOG.get("System.DateTime"));
//      put(XSD_YEARMONTH,          SYSTEM_CATALOG.get("System.DateTime"));
    }};
    private static Map<String, DataType> getSystemCatalog() {
        ModelInfo systemModelInfo = JAXB.unmarshal(
                ModelImporter.class.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml"),
                ModelInfo.class);

        final Map<String, DataType> map = new HashMap<>();
        for (TypeInfo info : systemModelInfo.getTypeInfo()) {
            if (info instanceof SimpleTypeInfo) {
                SimpleTypeInfo sInfo = (SimpleTypeInfo) info;
                map.put(sInfo.getName(), new SimpleType(sInfo.getName()));
            } else if (info instanceof ClassInfo) {
                ClassInfo cInfo = (ClassInfo) info;
                map.put(cInfo.getName(), new ClassType(cInfo.getName()));
            }
        }
        return map;
    }

    private final XmlSchema schema;
    private final String modelName;
    private final ModelImporterOptions options;
    private final Map<String, DataType> dataTypes;
    private final Map<String, String> namespaces;

    private ModelImporter(XmlSchema schema, String modelName, ModelImporterOptions options) {
        this.schema = schema;
        this.modelName = modelName;
        this.options = options != null ? options : new ModelImporterOptions();
        this.dataTypes = new HashMap<>();
        this.namespaces = new HashMap<>();
    }

    public static ModelInfo fromXsd(XmlSchema schema, String modelName, ModelImporterOptions options) {
        ModelImporter importer = new ModelImporter(schema, modelName, options);
        return importer.importXsd();
    }

    public ModelInfo importXsd() {
        namespaces.put(schema.getTargetNamespace(), modelName);

        for (XmlSchemaType schemaType : schema.getSchemaTypes().values()) {
            resolveType(schemaType);
        }

        return new ModelInfo()
                .withName(modelName)
                .withTargetQualifier(new QName(modelName.toLowerCase()))
                .withUrl(schema.getTargetNamespace())
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

    private SimpleTypeInfo toSimpleTypeInfo(SimpleType dataType) {
        SimpleTypeInfo result = new SimpleTypeInfo();
        result.setName(dataType.getName());
        if (dataType.getBaseType() != null) {
            result.setBaseType(toTypeSpecifier(dataType.getBaseType()));
        }

        return result;
    }

    private ClassInfo toClassInfo(ClassType dataType) {
        ClassInfo result = new ClassInfo();
        result.setName(dataType.getName());
        if (dataType.getBaseType() != null) {
            result.setBaseType(toTypeSpecifier(dataType.getBaseType()));
        }
        if (options.getNormalizePrefix() != null && dataType.getName().startsWith(options.getNormalizePrefix())) {
            result.setLabel(dataType.getName().substring(options.getNormalizePrefix().length()));
        }

        for (ClassTypeElement element : dataType.getElements()) {
            result.getElement().add(new ClassInfoElement()
                    .withName(element.getName())
                    .withType(toTypeSpecifier(element.getType())));
        }

        return result;
    }

    private IntervalTypeInfo toIntervalTypeInfo(IntervalType dataType) {
        IntervalTypeInfo result = new IntervalTypeInfo();
        result.setPointType(toTypeSpecifier(dataType.getPointType()));
        return result;
    }

    private ListTypeInfo toListTypeInfo(ListType dataType) {
        ListTypeInfo result = new ListTypeInfo();
        result.setElementType(toTypeSpecifier(dataType.getElementType()));
        return result;
    }

    private TupleTypeInfo toTupleTypeInfo(TupleType dataType) {
        TupleTypeInfo result = new TupleTypeInfo();
        if (dataType.getBaseType() != null) {
            result.setBaseType(toTypeSpecifier(dataType.getBaseType()));
        }

        for (TupleTypeElement element : dataType.getElements()) {
            result.getElement().add(new TupleTypeInfoElement()
                    .withName(element.getName())
                    .withType(toTypeSpecifier(element.getType())));
        }

        return result;
    }

    private String toTypeSpecifier(DataType dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType is null");
        }

        if (dataType instanceof SimpleType) {
            return toSimpleTypeSpecifier((SimpleType) dataType);
        } else if (dataType instanceof ClassType) {
            return toClassTypeSpecifier((ClassType) dataType);
        } else if (dataType instanceof IntervalType) {
            return toIntervalTypeSpecifier((IntervalType) dataType);
        } else if (dataType instanceof ListType) {
            return toListTypeSpecifier((ListType) dataType);
        } else if (dataType instanceof TupleType) {
            throw new IllegalArgumentException("Tuple types cannot be used in type specifiers.");
        } else {
            throw new IllegalArgumentException(String.format("Unknown data type class: %s", dataType.getClass().getName()));
        }
    }

    private String toSimpleTypeSpecifier(SimpleType dataType) {
        return dataType.getName();
    }

    private String toClassTypeSpecifier(ClassType dataType) {
        return dataType.getName();
    }

    private String toIntervalTypeSpecifier(IntervalType dataType) {
        return String.format("interval<%s>", toTypeSpecifier(dataType.getPointType()));
    }

    private String toListTypeSpecifier(ListType dataType) {
        return String.format("list<%s>", toTypeSpecifier(dataType.getElementType()));
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
        if (options.getTypeMap().containsKey(schemaTypeName)) {
            return SYSTEM_CATALOG.get(options.getTypeMap().get(schemaTypeName));
        }
        else if (SYSTEM_TYPE_MAP.containsKey(schemaTypeName)) {
            return SYSTEM_TYPE_MAP.get(schemaTypeName);
        }

        XmlSchemaType schemaType = schema.getTypeByName(schemaTypeName);
        if (schemaType == null) {
            String typeName = getTypeName(schemaTypeName, namespaces);
            DataType resultType = dataTypes.get(typeName);
            if (resultType == null) {
                resultType = new SimpleType(typeName);
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
        else if (options.getTypeMap().containsKey(simpleType.getQName())) {
            return SYSTEM_CATALOG.get(options.getTypeMap().get(simpleType.getQName()));
        }
        else if (SYSTEM_TYPE_MAP.containsKey(simpleType.getQName())) {
            return SYSTEM_TYPE_MAP.get(simpleType.getQName());
        }

        String typeName = getTypeName(simpleType.getQName(), namespaces);
        DataType resultType = dataTypes.get(typeName);
        if (resultType == null) {
            DataType baseType = null;
            if (simpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
                XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
                QName baseSchemaTypeName = restriction.getBaseTypeName();
                if (baseSchemaTypeName != null) {
                    baseType = resolveType(baseSchemaTypeName);
                }
            }

            switch (options.getSimpleTypeRestrictionPolicy()) {
                case IGNORE:
                    resultType = new SimpleType(typeName);
                    dataTypes.put(typeName, resultType);
                    break;
                case EXTEND_BASETYPE:
                    resultType = new SimpleType(typeName, baseType);
                    dataTypes.put(typeName, resultType);
                    break;
                case USE_BASETYPE:
                default:
                    resultType = baseType;
                    break;
            }


        }

        return resultType;
    }

    private DataType resolveComplexType(XmlSchemaComplexType complexType) {
        if (complexType.isAnonymous()) {
            return null;
        }
        else if (options.getTypeMap().containsKey(complexType.getQName())) {
            return SYSTEM_CATALOG.get(options.getTypeMap().get(complexType.getQName()));
        }
        else if (SYSTEM_TYPE_MAP.containsKey(complexType.getQName())) {
            return SYSTEM_TYPE_MAP.get(complexType.getQName());
        }

        String typeName = getTypeName(complexType.getQName(), namespaces);
        DataType resultType = dataTypes.get(typeName);
        if (resultType == null) {

            // Resolve the base type, if any
            DataType baseType = null;
            QName baseSchemaTypeName = complexType.getBaseSchemaTypeName();
            if (baseSchemaTypeName != null) {
                XmlSchemaType baseSchemaType = schema.getTypeByName(baseSchemaTypeName);
                baseType = resolveType(baseSchemaType);
            }

            // Create and register the type
            ClassType classType = new ClassType(typeName, baseType);
            dataTypes.put(typeName, classType);

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

            XmlSchemaParticle particle = particleContent;
            if (particle instanceof XmlSchemaElement) {
                ClassTypeElement element = resolveClassTypeElement((XmlSchemaElement)particle);
                if (element != null) {
                    elements.add(element);
                }
            }
            else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
                for (XmlSchemaSequenceMember member : sequence.getItems()) {
                    if (member instanceof XmlSchemaElement) {
                        ClassTypeElement element = resolveClassTypeElement((XmlSchemaElement)member);
                        if (element != null) {
                            elements.add(element);
                        }
                    }
                }
            }
            else if (particle instanceof XmlSchemaChoice) {
                XmlSchemaChoice choice = (XmlSchemaChoice)particle;
                for (XmlSchemaChoiceMember member : choice.getItems()) {
                    if (member instanceof XmlSchemaElement) {
                        ClassTypeElement element = resolveClassTypeElement((XmlSchemaElement)member);
                        if (element != null) {
                            elements.add(element);
                        }
                    }
                }
            }

            classType.addElements(elements);
            resultType = classType;
        }

        return resultType;
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

        return new ClassTypeElement(element.getName(), elementType);
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

        return new ClassTypeElement(attribute.getName(), elementType);
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
