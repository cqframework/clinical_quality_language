package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.elm.tracking.*;
import org.hl7.elm_modelinfo.r1.*;

import java.util.*;

public class ModelImporter {

    private ModelInfo modelInfo;
    private Map<String, TypeInfo> typeInfoIndex;
    private Map<String, DataType> resolvedTypes;
    private List<DataType> dataTypes;
    private List<Conversion> conversions;

    public ModelImporter(ModelInfo modelInfo, Iterable<DataType> systemTypes) {
        if (modelInfo == null) {
            throw new IllegalArgumentException("modelInfo is null");
        }

        this.modelInfo = modelInfo;
        this.typeInfoIndex = new HashMap<>();
        this.resolvedTypes = new HashMap<>();
        this.dataTypes = new ArrayList<>();
        this.conversions = new ArrayList<>();

        // Import system types
        if (systemTypes != null) {
            for (DataType systemType : systemTypes) {
                if (systemType instanceof NamedType) {
                    NamedType namedSystemType = (NamedType)systemType;
                    this.resolvedTypes.put(namedSystemType.getName(), systemType);
                }
            }
        }

        // Import model types
        for (TypeInfo t : this.modelInfo.getTypeInfo()) {
            if (t instanceof SimpleTypeInfo) {
                typeInfoIndex.put(((SimpleTypeInfo)t).getName(), t);
            }
            else if (t instanceof ClassInfo) {
                ClassInfo classInfo = (ClassInfo)t;
                if (classInfo.getName() != null) {
                    typeInfoIndex.put(classInfo.getName(), classInfo);
                }
            }
        }

        // Import model conversions
        for (ConversionInfo c : this.modelInfo.getConversionInfo()) {
            DataType fromType = resolveTypeSpecifier(c.getFromType());
            DataType toType = resolveTypeSpecifier(c.getToType());
            int qualifierIndex = c.getFunctionName().indexOf('.');
            String libraryName = qualifierIndex >= 0 ? c.getFunctionName().substring(0, qualifierIndex) : null;
            String functionName = qualifierIndex >= 0 ? c.getFunctionName().substring(qualifierIndex + 1) : null;
            Operator operator = new Operator(functionName, new Signature(fromType), toType);
            if (libraryName != null) {
                operator.setLibraryName(libraryName);
            }

            // All conversions loaded as part of a model are implicit
            Conversion conversion = new Conversion(operator, true);
            conversions.add(conversion);
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
    public Iterable<Conversion> getConversions() { return conversions; }

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
        else if (t instanceof ChoiceTypeInfo) {
            return resolveChoiceType((ChoiceTypeInfo)t);
        }

        return null;
    }

    private DataType resolveTypeSpecifier(String typeSpecifier) {
        if ((typeSpecifier == null) || typeSpecifier.equals("")) {
            return null;
        }

        // typeSpecifier: simpleTypeSpecifier | intervalTypeSpecifier | listTypeSpecifier | choiceTypeSpecifier;
        // simpleTypeSpecifier: (identifier '.')? identifier
        // intervalTypeSpecifier: 'interval' '<' typeSpecifier '>'
        // listTypeSpecifier: 'list' '<' typeSpecifier '>'
        // choiceTypeSpecifier: 'choice' '<' typeSpecifier (',' typeSpecifier)* '>'
        if (typeSpecifier.toLowerCase().startsWith("interval")) {
            DataType pointType = resolveTypeSpecifier(typeSpecifier.substring(typeSpecifier.indexOf('<') + 1, typeSpecifier.lastIndexOf('>')));
            return new IntervalType(pointType);
        }
        else if (typeSpecifier.toLowerCase().startsWith("list")) {
            DataType elementType = resolveTypeSpecifier(typeSpecifier.substring(typeSpecifier.indexOf('<') + 1, typeSpecifier.lastIndexOf('>')));
            return new ListType(elementType);
        }
        // TODO: Need a type specifier parser at this point, the type specifier grammar is now beyond simple parsing
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
            if (t.getName().equals(DataType.ANY.getName())) {
                result = DataType.ANY;
            }
            else {
                result = new SimpleType(t.getName(), resolveTypeSpecifier(t.getBaseType()));
            }
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
        TupleType result = new TupleType(resolveTupleTypeElements(t.getElement()));
        return result;
    }

    private Collection<ClassTypeElement> resolveClassTypeElements(Collection<ClassInfoElement> infoElements) {
        List<ClassTypeElement> elements = new ArrayList();
        for (ClassInfoElement e : infoElements) {
            elements.add(new ClassTypeElement(e.getName(), resolveTypeSpecifier(e.getType())));
        }
        return elements;
    }

    private ClassType resolveClassType(ClassInfo t) {
        if (t.getName() == null) {
            throw new IllegalArgumentException("Class definition must have a name.");
        }

        ClassType result = (ClassType)lookupType(t.getName());
        if (result == null) {
            if (t instanceof ProfileInfo) {
                result = new ProfileType(t.getName(), resolveTypeSpecifier(t.getBaseType()));
            }
            else {
                result = new ClassType(t.getName(), resolveTypeSpecifier(t.getBaseType()));
            }
            resolvedTypes.put(result.getName(), result);
            result.addElements(resolveClassTypeElements(t.getElement()));
            result.setIdentifier(t.getIdentifier());
            result.setLabel(t.getLabel());
            result.setRetrievable(t.isRetrievable());
            result.setPrimaryCodePath(t.getPrimaryCodePath());
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

    private ChoiceType resolveChoiceType(ChoiceTypeInfo t) {
        ArrayList<DataType> types = new ArrayList<DataType>();
        for (String typeInfo : t.getType()) {
            types.add(resolveTypeSpecifier(typeInfo));
        }
        return new ChoiceType(types);
    }
}
