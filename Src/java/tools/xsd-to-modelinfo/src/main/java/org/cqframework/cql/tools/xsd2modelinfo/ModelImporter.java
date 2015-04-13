package org.cqframework.cql.tools.xsd2modelinfo;

import org.apache.ws.commons.schema.*;
import org.cqframework.cql.elm.tracking.*;
import org.hl7.elm_modelinfo.r1.*;

import static org.apache.ws.commons.schema.constants.Constants.*;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;
import java.util.*;

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

    public static Map<String, DataType> getSystemCatalog() {
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

    public static ModelInfo fromXsd(XmlSchema schema, String modelName, ModelImporterOptions options) {
        ModelInfo result = new ModelInfo();

        result.setName(modelName);
        result.setTargetQualifier(new QName(modelName.toLowerCase()));
        result.setUrl(schema.getTargetNamespace());

        if (options == null) {
            options = new ModelImporterOptions();
        }

        Collection<DataType> dataTypes = getDatatypesFromXsd(schema, modelName, options);

        for (DataType dataType : dataTypes) {
            result.getTypeInfo().add(toTypeInfo(dataType));
        }

        return result;
    }

    private static TypeInfo toTypeInfo(DataType dataType) {
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

    private static SimpleTypeInfo toSimpleTypeInfo(SimpleType dataType) {
        SimpleTypeInfo result = new SimpleTypeInfo();
        result.setName(dataType.getName());
        if (dataType.getBaseType() != null) {
            result.setBaseType(toTypeSpecifier(dataType.getBaseType()));
        }

        return result;
    }

    private static ClassInfo toClassInfo(ClassType dataType) {
        ClassInfo result = new ClassInfo();
        result.setName(dataType.getName());
        if (dataType.getBaseType() != null) {
            result.setBaseType(toTypeSpecifier(dataType.getBaseType()));
        }

        for (ClassTypeElement element : dataType.getElements()) {
            result.getElement().add(new ClassInfoElement()
                    .withName(element.getName())
                    .withType(toTypeSpecifier(element.getType())));
        }

        return result;
    }

    private static IntervalTypeInfo toIntervalTypeInfo(IntervalType dataType) {
        IntervalTypeInfo result = new IntervalTypeInfo();
        result.setPointType(toTypeSpecifier(dataType.getPointType()));
        return result;
    }

    private static ListTypeInfo toListTypeInfo(ListType dataType) {
        ListTypeInfo result = new ListTypeInfo();
        result.setElementType(toTypeSpecifier(dataType.getElementType()));
        return result;
    }

    private static TupleTypeInfo toTupleTypeInfo(TupleType dataType) {
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

    private static String toTypeSpecifier(DataType dataType) {
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
            return toTupleTypeSpecifier((TupleType) dataType);
        } else {
            throw new IllegalArgumentException(String.format("Unknown data type class: %s", dataType.getClass().getName()));
        }
    }

    private static String toSimpleTypeSpecifier(SimpleType dataType) {
        return dataType.getName();
    }

    private static String toClassTypeSpecifier(ClassType dataType) {
        return dataType.getName();
    }

    private static String toIntervalTypeSpecifier(IntervalType dataType) {
        return String.format("interval<%s>", toTypeSpecifier(dataType.getPointType()));
    }

    private static String toListTypeSpecifier(ListType dataType) {
        return String.format("list<%s>", toTypeSpecifier(dataType.getElementType()));
    }

    private static String toTupleTypeSpecifier(TupleType dataType) {
        throw new IllegalArgumentException("Tuple types cannot be used in type specifiers.");
    }

    private static Collection<DataType> getDatatypesFromXsd(XmlSchema schema, String modelName, ModelImporterOptions options) {
        Map<QName, XmlSchemaType> schemaTypes = schema.getSchemaTypes();
        Map<String, DataType> dataTypes = new HashMap<>();
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("http://www.w3.org/2001/XMLSchema", "XS");
        namespaces.put(schema.getTargetNamespace(), modelName);

        for (XmlSchemaType schemaType : schemaTypes.values()) {
            resolveType(schema, schemaType, namespaces, dataTypes, options);
        }

        return dataTypes.values();
    }

    private static String getTypeName(QName schemaTypeName, Map<String, String> namespaces) {
        if (schemaTypeName == null) {
            throw new IllegalArgumentException("schemaTypeName is null");
        }
        String modelName = namespaces.get(schemaTypeName.getNamespaceURI());
        if (modelName == null) {
            modelName = schemaTypeName.getPrefix(); // Doesn't always work, but should be okay for a fallback position...
            if (modelName != null) {
                namespaces.put(schemaTypeName.getNamespaceURI(), modelName);
            }
        }

        if (modelName != null) {
            return modelName + '.' + schemaTypeName.getLocalPart().replace('-', '_');
        }

        return schemaTypeName.getLocalPart();
    }

    private static DataType resolveType(XmlSchema schema, QName schemaTypeName, Map<String, String> namespaces, Map<String, DataType> dataTypes, ModelImporterOptions options) {
        if (SYSTEM_TYPE_MAP.containsKey(schemaTypeName)) {
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
            return resolveType(schema, schemaType, namespaces, dataTypes, options);
        }
    }

    private static DataType resolveType(XmlSchema schema, XmlSchemaType schemaType, Map<String, String> namespaces, Map<String, DataType> dataTypes, ModelImporterOptions options) {
        if (schemaType instanceof XmlSchemaSimpleType) {
            return resolveSimpleType(schema, (XmlSchemaSimpleType)schemaType, namespaces, dataTypes, options);
        }
        else if (schemaType instanceof XmlSchemaComplexType) {
            return resolveComplexType(schema, (XmlSchemaComplexType)schemaType, namespaces, dataTypes, options);
        }

        return null;
    }

    private static DataType resolveSimpleType(XmlSchema schema, XmlSchemaSimpleType simpleType, Map<String, String> namespaces, Map<String, DataType> dataTypes, ModelImporterOptions options) {
        if (simpleType.isAnonymous()) {
            return null;
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
                    baseType = resolveType(schema, baseSchemaTypeName, namespaces, dataTypes, options);
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

    private static DataType resolveComplexType(XmlSchema schema, XmlSchemaComplexType complexType, Map<String, String> namespaces, Map<String, DataType> dataTypes, ModelImporterOptions options) {
        if (complexType.isAnonymous()) {
            return null;
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
                baseType = resolveType(schema, baseSchemaType, namespaces, dataTypes, options);
            }

            // Create and register the type
            ClassType classType = new ClassType(typeName, baseType);
            dataTypes.put(typeName, classType);

            List<ClassTypeElement> elements = new ArrayList<>();

            List<XmlSchemaAttributeOrGroupRef> attributeContent = null;
            XmlSchemaParticle particleContent = null;

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
            }
            else {
                attributeContent = complexType.getAttributes();
                particleContent = complexType.getParticle();
            }

            for (XmlSchemaAttributeOrGroupRef attribute : attributeContent) {
                resolveClassTypeElements(schema, attribute, namespaces, dataTypes, elements, options);
            }

            XmlSchemaParticle particle = particleContent;
            if (particle instanceof XmlSchemaElement) {
                ClassTypeElement element = resolveClassTypeElement(schema, (XmlSchemaElement)particle, namespaces, dataTypes, options);
                if (element != null) {
                    elements.add(element);
                }
            }
            else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
                for (XmlSchemaSequenceMember member : sequence.getItems()) {
                    if (member instanceof XmlSchemaElement) {
                        ClassTypeElement element = resolveClassTypeElement(schema, (XmlSchemaElement)member, namespaces, dataTypes, options);
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
                        ClassTypeElement element = resolveClassTypeElement(schema, (XmlSchemaElement)member, namespaces, dataTypes, options);
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

    private static ClassTypeElement resolveClassTypeElement(XmlSchema schema, XmlSchemaElement element, Map<String, String> namespaces, Map<String, DataType> dataTypes, ModelImporterOptions options) {
        boolean isList = element.getMaxOccurs() > 1;

        if (element.isRef()) {
            element = element.getRef().getTarget();
        }

        DataType elementType = null;
        XmlSchemaType schemaType = element.getSchemaType();
        if (schemaType != null) {
            elementType = resolveType(schema, schemaType, namespaces, dataTypes, options);
        }
        else {
            QName schemaTypeName = element.getSchemaTypeName();
            if (schemaTypeName != null) {
                elementType = resolveType(schema, schemaTypeName, namespaces, dataTypes, options);
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

    private static ClassTypeElement resolveClassTypeElement(XmlSchema schema, XmlSchemaAttribute attribute, Map<String, String> namespaces, Map<String, DataType> dataTypes, ModelImporterOptions options) {
        if (attribute.isRef()) {
            attribute = attribute.getRef().getTarget();
        }

        DataType elementType = null;
        XmlSchemaType schemaType = attribute.getSchemaType();
        if (schemaType != null) {
            elementType = resolveType(schema, schemaType, namespaces, dataTypes, options);
        }
        else {
            QName schemaTypeName = attribute.getSchemaTypeName();
            if (schemaTypeName != null) {
                elementType = resolveType(schema, schemaTypeName, namespaces, dataTypes, options);
            }
        }

        if (elementType == null) {
            return null; // The type is anonymous and will not be represented in the imported model
            //throw new IllegalStateException(String.format("Unable to resolve type %s of attribute %s.", attribute.getSchemaTypeName(), attribute.getName()));
        }

        return new ClassTypeElement(attribute.getName(), elementType);
    }

    private static void resolveClassTypeElements(XmlSchema schema, XmlSchemaAttributeOrGroupRef attribute, Map<String, String> namespaces, Map<String, DataType> dataTypes, List<ClassTypeElement> elements, ModelImporterOptions options) {
        if (attribute instanceof XmlSchemaAttribute) {
            ClassTypeElement element = resolveClassTypeElement(schema, (XmlSchemaAttribute)attribute, namespaces, dataTypes, options);
            if (element != null) {
                elements.add(element);
            }
        }
        else if (attribute instanceof XmlSchemaAttributeGroupRef) {
            resolveClassTypeElements(schema, ((XmlSchemaAttributeGroupRef)attribute).getRef().getTarget(), namespaces, dataTypes, elements, options);
        }
    }

    private static void resolveClassTypeElements(XmlSchema schema, XmlSchemaAttributeGroup attributeGroup, Map<String, String> namespaces, Map<String, DataType> dataTypes, List<ClassTypeElement> elements, ModelImporterOptions options) {
        for (XmlSchemaAttributeGroupMember member : attributeGroup.getAttributes()) {
            if (member instanceof XmlSchemaAttribute) {
                ClassTypeElement element = resolveClassTypeElement(schema, (XmlSchemaAttribute)member, namespaces, dataTypes, options);
                if (element != null) {
                    elements.add(element);
                }
            }
            else if (member instanceof XmlSchemaAttributeGroupRef) {
                resolveClassTypeElements(schema, ((XmlSchemaAttributeGroupRef)member).getRef().getTarget(), namespaces, dataTypes, elements, options);
            }
            else if (member instanceof XmlSchemaAttributeGroup) {
                resolveClassTypeElements(schema, (XmlSchemaAttributeGroup)member, namespaces, dataTypes, elements, options);
            }
        }
    }
}
