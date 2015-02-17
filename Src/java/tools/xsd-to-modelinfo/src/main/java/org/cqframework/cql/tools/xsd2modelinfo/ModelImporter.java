package org.cqframework.cql.tools.xsd2modelinfo;

import org.apache.ws.commons.schema.*;
import org.cqframework.cql.elm.tracking.*;
import org.hl7.elm_modelinfo.r1.*;

import javax.xml.namespace.QName;
import java.util.*;

public class ModelImporter {
    public static ModelInfo fromXsd(XmlSchema schema, String modelName) {
        ModelInfo result = new ModelInfo();

        result.setName(modelName);
        result.setTargetQualifier(new QName(modelName.toLowerCase()));
        result.setUrl(schema.getTargetNamespace());

        Map<String, DataType> typeCatalog = getSystemTypeCatalog();
        Collection<DataType> dataTypes = fromXsd(schema, modelName, typeCatalog);

        for (DataType dataType : dataTypes) {
            result.getTypeInfo().add(toTypeInfo(dataType));
        }

        return result;
    }

    private static Map<String, DataType> getSystemTypeCatalog() {
        Map<String, DataType> result = new HashMap<>();

        result.put("System.Boolean", new SimpleType("System.Boolean"));
        result.put("System.Integer", new SimpleType("System.Integer"));
        result.put("System.Decimal", new SimpleType("System.Decimal"));
        result.put("System.String", new SimpleType("System.String"));
        result.put("System.DateTime", new SimpleType("System.DateTime"));

        return result;
    }

    private static TypeInfo toTypeInfo(DataType dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType is null");
        }

        if (dataType instanceof SimpleType) {
            return toSimpleTypeInfo((SimpleType) dataType);
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
        result.setName(dataType.getName());
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

    private static String toIntervalTypeSpecifier(IntervalType dataType) {
        return String.format("interval<%s>", toTypeSpecifier(dataType.getPointType()));
    }

    private static String toListTypeSpecifier(ListType dataType) {
        return String.format("list<%s>", toTypeSpecifier(dataType.getElementType()));
    }

    private static String toTupleTypeSpecifier(TupleType dataType) {
        if (dataType.getName() == null) {
            throw new IllegalArgumentException("Anonymous tuple types cannot be used in type specifiers.");
        }

        return dataType.getName();
    }

    private static Collection<DataType> fromXsd(XmlSchema schema, String modelName, Map<String, DataType> typeCatalog) {
        Map<QName, XmlSchemaType> schemaTypes = schema.getSchemaTypes();
        Map<String, DataType> dataTypes = new HashMap<>();
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("http://www.w3.org/2001/XMLSchema", "System");
        namespaces.put(schema.getTargetNamespace(), modelName);

        //dataTypes.putAll(typeCatalog);

        for (XmlSchemaType schemaType : schemaTypes.values()) {
            resolveType(schema, schemaType, namespaces, dataTypes);
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

    private static DataType resolveType(XmlSchema schema, QName schemaTypeName, Map<String, String> namespaces, Map<String, DataType> dataTypes) {
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
            return resolveType(schema, schemaType, namespaces, dataTypes);
        }
    }

    private static DataType resolveType(XmlSchema schema, XmlSchemaType schemaType, Map<String, String> namespaces, Map<String, DataType> dataTypes) {
        if (schemaType instanceof XmlSchemaSimpleType) {
            return resolveSimpleType(schema, (XmlSchemaSimpleType)schemaType, namespaces, dataTypes);
        }
        else if (schemaType instanceof XmlSchemaComplexType) {
            return resolveComplexType(schema, (XmlSchemaComplexType)schemaType, namespaces, dataTypes);
        }

        return null;
    }

    private static DataType resolveSimpleType(XmlSchema schema, XmlSchemaSimpleType simpleType, Map<String, String> namespaces, Map<String, DataType> dataTypes) {
        if (simpleType.isAnonymous()) {
            return null;
        }
        String typeName = getTypeName(simpleType.getQName(), namespaces);
        DataType resultType = dataTypes.get(typeName);
        if (resultType == null) {
            resultType = new SimpleType(typeName);
            dataTypes.put(typeName, resultType);

            // TODO: Should we worry about this, or should we just use conversions wherever we need them...
//            if (simpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
//                XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
//                QName baseSchemaTypeName = restriction.getBaseTypeName();
//                if (baseSchemaTypeName != null) {
//                    DataType baseType = resolveType(schema, baseSchemaTypeName, namespaces, dataTypes);
//                    if (baseType != null) {
//                        resultType.setBaseType(baseType);
//                    }
//                }
//            }
        }

        return resultType;
    }

    private static DataType resolveComplexType(XmlSchema schema, XmlSchemaComplexType complexType, Map<String, String> namespaces, Map<String, DataType> dataTypes) {
        if (complexType.isAnonymous()) {
            return null;
        }
        String typeName = getTypeName(complexType.getQName(), namespaces);
        DataType resultType = dataTypes.get(typeName);
        if (resultType == null) {

            // Resolve the base type, if any
            DataType baseType = null;
            QName baseSchemaTypeName = complexType.getBaseSchemaTypeName();
            if (baseSchemaTypeName != null) {
                XmlSchemaType baseSchemaType = schema.getTypeByName(baseSchemaTypeName);
                baseType = resolveType(schema, baseSchemaType, namespaces, dataTypes);
            }

            // Create and register the type
            TupleType tupleType = new TupleType(typeName, baseType);
            dataTypes.put(typeName, tupleType);

            List<TupleTypeElement> elements = new ArrayList<>();

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
                resolveTupleTypeElements(schema, attribute, namespaces, dataTypes, elements);
            }

            XmlSchemaParticle particle = particleContent;
            if (particle instanceof XmlSchemaElement) {
                TupleTypeElement element = resolveTupleTypeElement(schema, (XmlSchemaElement)particle, namespaces, dataTypes);
                if (element != null) {
                    elements.add(element);
                }
            }
            else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
                for (XmlSchemaSequenceMember member : sequence.getItems()) {
                    if (member instanceof XmlSchemaElement) {
                        TupleTypeElement element = resolveTupleTypeElement(schema, (XmlSchemaElement)member, namespaces, dataTypes);
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
                        TupleTypeElement element = resolveTupleTypeElement(schema, (XmlSchemaElement)member, namespaces, dataTypes);
                        if (element != null) {
                            elements.add(element);
                        }
                    }
                }
            }

            tupleType.addElements(elements);
            resultType = tupleType;
        }

        return resultType;
    }

    private static TupleTypeElement resolveTupleTypeElement(XmlSchema schema, XmlSchemaElement element, Map<String, String> namespaces, Map<String, DataType> dataTypes) {
        boolean isList = element.getMaxOccurs() > 1;

        if (element.isRef()) {
            element = element.getRef().getTarget();
        }

        DataType elementType = null;
        XmlSchemaType schemaType = element.getSchemaType();
        if (schemaType != null) {
            elementType = resolveType(schema, schemaType, namespaces, dataTypes);
        }
        else {
            QName schemaTypeName = element.getSchemaTypeName();
            if (schemaTypeName != null) {
                elementType = resolveType(schema, schemaTypeName, namespaces, dataTypes);
            }
        }

        if (elementType == null) {
            return null; // The type is anonymous and will not be represented within the imported model
            //throw new IllegalStateException(String.format("Unable to resolve type %s of element %s.", element.getSchemaType().getName(), element.getName()));
        }

        if (isList) {
            elementType = new ListType(elementType);
        }

        return new TupleTypeElement(element.getName(), elementType);
    }

    private static TupleTypeElement resolveTupleTypeElement(XmlSchema schema, XmlSchemaAttribute attribute, Map<String, String> namespaces, Map<String, DataType> dataTypes) {
        if (attribute.isRef()) {
            attribute = attribute.getRef().getTarget();
        }

        DataType elementType = null;
        XmlSchemaType schemaType = attribute.getSchemaType();
        if (schemaType != null) {
            elementType = resolveType(schema, schemaType, namespaces, dataTypes);
        }
        else {
            QName schemaTypeName = attribute.getSchemaTypeName();
            if (schemaTypeName != null) {
                elementType = resolveType(schema, schemaTypeName, namespaces, dataTypes);
            }
        }

        if (elementType == null) {
            return null; // The type is anonymous and will not be represented in the imported model
            //throw new IllegalStateException(String.format("Unable to resolve type %s of attribute %s.", attribute.getSchemaTypeName(), attribute.getName()));
        }

        return new TupleTypeElement(attribute.getName(), elementType);
    }

    private static void resolveTupleTypeElements(XmlSchema schema, XmlSchemaAttributeOrGroupRef attribute, Map<String, String> namespaces, Map<String, DataType> dataTypes, List<TupleTypeElement> elements) {
        if (attribute instanceof XmlSchemaAttribute) {
            TupleTypeElement element = resolveTupleTypeElement(schema, (XmlSchemaAttribute)attribute, namespaces, dataTypes);
            if (element != null) {
                elements.add(element);
            }
        }
        else if (attribute instanceof XmlSchemaAttributeGroupRef) {
            resolveTupleTypeElements(schema, ((XmlSchemaAttributeGroupRef)attribute).getRef().getTarget(), namespaces, dataTypes, elements);
        }
    }

    private static void resolveTupleTypeElements(XmlSchema schema, XmlSchemaAttributeGroup attributeGroup, Map<String, String> namespaces, Map<String, DataType> dataTypes, List<TupleTypeElement> elements) {
        for (XmlSchemaAttributeGroupMember member : attributeGroup.getAttributes()) {
            if (member instanceof XmlSchemaAttribute) {
                TupleTypeElement element = resolveTupleTypeElement(schema, (XmlSchemaAttribute)member, namespaces, dataTypes);
                if (element != null) {
                    elements.add(element);
                }
            }
            else if (member instanceof XmlSchemaAttributeGroupRef) {
                resolveTupleTypeElements(schema, ((XmlSchemaAttributeGroupRef)member).getRef().getTarget(), namespaces, dataTypes, elements);
            }
            else if (member instanceof XmlSchemaAttributeGroup) {
                resolveTupleTypeElements(schema, (XmlSchemaAttributeGroup)member, namespaces, dataTypes, elements);
            }
        }
    }
}
