package org.cqframework.cql.cql2elm.model;

import org.apache.ws.commons.schema.*;
import org.cqframework.cql.elm.tracking.*;
import org.hl7.elm_modelinfo.r1.*;

import javax.xml.namespace.QName;
import java.util.*;

public class ModelImporter {

    private ModelInfo modelInfo;
    private Map<String, TypeInfo> typeInfoIndex;
    private Map<String, DataType> resolvedTypes;
    private List<DataType> dataTypes;

    public ModelImporter(ModelInfo modelInfo, Iterable<DataType> systemTypes) {
        if (modelInfo == null) {
            throw new IllegalArgumentException("modelInfo is null");
        }

        this.modelInfo = modelInfo;
        this.typeInfoIndex = new HashMap<>();
        this.resolvedTypes = new HashMap<>();
        this.dataTypes = new ArrayList<>();

        // Import system types
        if (systemTypes != null) {
            for (DataType systemType : systemTypes) {
                if (systemType instanceof NamedType) {
                    NamedType namedSystemType = (NamedType)systemType;
                    this.resolvedTypes.put(namedSystemType.getName(), systemType);
                }
            }
        }

        for (TypeInfo t : this.modelInfo.getTypeInfo()) {
            if (t instanceof SimpleTypeInfo) {
                typeInfoIndex.put(((SimpleTypeInfo)t).getName(), t);
            }
            else if (t instanceof TupleTypeInfo) {
                TupleTypeInfo tupleTypeInfo = (TupleTypeInfo)t;
                if (tupleTypeInfo.getName() != null) {
                    typeInfoIndex.put(tupleTypeInfo.getName(), tupleTypeInfo);
                }
            }
        }

        for (TypeInfo t: this.modelInfo.getTypeInfo()) {
            dataTypes.add(resolveTypeInfo(t));
        }

        if (systemTypes != null) {
            for (DataType systemType : systemTypes) {
                if (systemType instanceof NamedType) {
                    NamedType namedSystemType = (NamedType)systemType;
                    this.resolvedTypes.remove(namedSystemType.getName());
                }
            }
        }
    }

    public Map<String, DataType> getTypes() { return resolvedTypes; }

    private DataType resolveTypeInfo(TypeInfo t) {
        if (t instanceof SimpleTypeInfo) {
            return resolveSimpleType((SimpleTypeInfo)t);
        }
        else if (t instanceof ClassInfo) {
            return resolveClassType((ClassInfo)t);
        }
        else if (t instanceof TupleTypeInfo) {
            return resolveTupleType((TupleTypeInfo)t);
        }
        else if (t instanceof IntervalTypeInfo) {
            return resolveIntervalType((IntervalTypeInfo)t);
        }
        else if (t instanceof ListTypeInfo) {
            return resolveListType((ListTypeInfo)t);
        }

        return null;
    }

    private DataType resolveTypeSpecifier(String typeSpecifier) {
        if ((typeSpecifier == null) || typeSpecifier.equals("")) {
            return null;
        }

        // typeSpecifier: simpleTypeSpecifier | intervalTypeSpecifier | listTypeSpecifier;
        // simpleTypeSpecifier: (identifier '.')? identifier
        // intervalTypeSpecifier: 'interval' '<' typeSpecifier '>'
        // listTypeSpecifier: 'list' '<' typeSpecifier '>'
        if (typeSpecifier.startsWith("interval")) {
            DataType pointType = resolveTypeSpecifier(typeSpecifier.substring(typeSpecifier.indexOf('<') + 1, typeSpecifier.lastIndexOf('>')));
            return new IntervalType(pointType);
        }
        else if (typeSpecifier.startsWith("list")) {
            DataType elementType = resolveTypeSpecifier(typeSpecifier.substring(typeSpecifier.indexOf('<') + 1, typeSpecifier.lastIndexOf('>')));
            return new ListType(elementType);
        }
        else {
            return resolveTypeName(typeSpecifier);
        }
    }

    private DataType resolveTypeName(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        DataType result = lookupType(typeName);
        if (result == null) {
            TypeInfo typeInfo = lookupTypeInfo(typeName);
            if (typeInfo == null) {
                throw new IllegalArgumentException(String.format("Could not resolve type info for type name %s.", typeName));
            }

            result = resolveTypeInfo(typeInfo);
        }

        return result;
    }

    private DataType lookupType(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        return resolvedTypes.get(typeName);
    }

    private TypeInfo lookupTypeInfo(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        return typeInfoIndex.get(typeName);
    }

    private SimpleType resolveSimpleType(SimpleTypeInfo t) {
        SimpleType result = (SimpleType)lookupType(t.getName());
        if (result == null) {
            result = new SimpleType(t.getName(), resolveTypeSpecifier(t.getBaseType()));
            resolvedTypes.put(result.getName(), result);
        }

        return result;
    }

    private Collection<TupleTypeElement> resolveTupleTypeElements(Collection<TupleTypeInfoElement> infoElements) {
        List<TupleTypeElement> elements = new ArrayList();
        for (TupleTypeInfoElement e : infoElements) {
            elements.add(new TupleTypeElement(e.getName(), resolveTypeSpecifier(e.getType())));
        }
        return elements;
    }

    private TupleType resolveTupleType(TupleTypeInfo t) {
        if (t.getName() != null) {
            TupleType result = (TupleType)lookupType(t.getName());
            if (result == null) {
                result = new TupleType(t.getName(), resolveTypeSpecifier(t.getBaseType()));
                resolvedTypes.put(result.getName(), result);
                result.addElements(resolveTupleTypeElements(t.getElement()));
            }

            return result;
        }
        else {
            TupleType result = new TupleType(null, null, resolveTupleTypeElements(t.getElement()));
            return result;
        }
    }

    private ClassType resolveClassType(ClassInfo t) {
        if (t.getName() == null) {
            throw new IllegalArgumentException("Class definition must have a name.");
        }

        ClassType result = (ClassType)lookupType(t.getName());
        if (result == null) {
            result = new ClassType(t.getName(), resolveTypeSpecifier(t.getBaseType()));
            result.addElements(resolveTupleTypeElements(t.getElement()));
            result.setIdentifier(t.getIdentifier());
            result.setTopic(t.getTopic());
            result.setPrimaryCodePath(t.getPrimaryCodePath());
            resolvedTypes.put(result.getName(), result);
        }

        return result;
    }

    private IntervalType resolveIntervalType(IntervalTypeInfo t) {
        IntervalType result = new IntervalType(resolveTypeSpecifier(t.getPointType()));
        return result;
    }

    private ListType resolveListType(ListTypeInfo t) {
        ListType result = new ListType(resolveTypeSpecifier(t.getElementType()));
        return result;
    }
}
