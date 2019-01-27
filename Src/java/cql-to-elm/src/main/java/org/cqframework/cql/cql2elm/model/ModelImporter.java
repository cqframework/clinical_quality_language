package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.*;
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
                typeInfoIndex.put(ensureUnqualified(((SimpleTypeInfo)t).getName()), t);
            }
            else if (t instanceof ClassInfo) {
                ClassInfo classInfo = (ClassInfo)t;
                if (classInfo.getName() != null) {
                    typeInfoIndex.put(ensureUnqualified(classInfo.getName()), classInfo);
                }
            }
        }

        // Import model conversions
        for (ConversionInfo c : this.modelInfo.getConversionInfo()) {
            DataType fromType = resolveTypeNameOrSpecifier(c.getFromType(), c.getFromTypeSpecifier());
            DataType toType = resolveTypeNameOrSpecifier(c.getToType(), c.getToTypeSpecifier());
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

    private String casify(String typeName) {
        return casify(typeName, this.modelInfo.isCaseSensitive() != null ? this.modelInfo.isCaseSensitive() : false);
    }

    private String casify(String typeName, boolean caseSensitive) {
        return caseSensitive ? typeName.toLowerCase() : typeName;
    }

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

    private DataType resolveTypeSpecifier(TypeSpecifier typeSpecifier) {
        if (typeSpecifier == null) {
            return null;
        }

        if (typeSpecifier instanceof NamedTypeSpecifier) {
            NamedTypeSpecifier namedTypeSpecifier = (NamedTypeSpecifier)typeSpecifier;
            String qualifier = namedTypeSpecifier.getModelName();
            if (qualifier == null || qualifier.isEmpty()) {
                qualifier = this.modelInfo.getName();
            }

            String qualifiedTypeName = String.format("%s.%s", qualifier, namedTypeSpecifier.getName());
            return resolveTypeName(qualifiedTypeName);
        }

        if (typeSpecifier instanceof IntervalTypeSpecifier) {
            IntervalTypeSpecifier intervalTypeSpecifier = (IntervalTypeSpecifier)typeSpecifier;
            DataType pointType = resolveTypeNameOrSpecifier(intervalTypeSpecifier.getPointType(), intervalTypeSpecifier.getPointTypeSpecifier());
            return new IntervalType(pointType);
        }

        if (typeSpecifier instanceof ListTypeSpecifier) {
            ListTypeSpecifier listTypeSpecifier = (ListTypeSpecifier)typeSpecifier;
            DataType elementType = resolveTypeNameOrSpecifier(listTypeSpecifier.getElementType(), listTypeSpecifier.getElementTypeSpecifier());
            if (elementType != null) {
                return new ListType(elementType);
            }
        }

        if (typeSpecifier instanceof ChoiceTypeSpecifier) {
            ChoiceTypeSpecifier choiceTypeSpecifier = (ChoiceTypeSpecifier)typeSpecifier;
            List<DataType> choices = new ArrayList<>();
            for (TypeSpecifier choice : choiceTypeSpecifier.getChoice()) {
                DataType choiceType = resolveTypeSpecifier(choice);
                choices.add(choiceType);
            }
            return new ChoiceType(choices);
        }

        return null;
    }

    private DataType resolveTypeName(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        // NOTE: Preserving the ability to parse string type specifiers for backwards loading compatibility
        // typeSpecifier: simpleTypeSpecifier | intervalTypeSpecifier | listTypeSpecifier
        // simpleTypeSpecifier: (identifier '.')? identifier
        // intervalTypeSpecifier: 'interval' '<' typeSpecifier '>'
        // listTypeSpecifier: 'list' '<' typeSpecifier '>'
        if (typeName.toLowerCase().startsWith("interval<")) {
            DataType pointType = resolveTypeName(typeName.substring(typeName.indexOf('<') + 1, typeName.lastIndexOf('>')));
            return new IntervalType(pointType);
        }
        else if (typeName.toLowerCase().startsWith("list<")) {
            DataType elementType = resolveTypeName(typeName.substring(typeName.indexOf('<') + 1, typeName.lastIndexOf('>')));
            return new ListType(elementType);
        }

        DataType result = lookupType(typeName);
        if (result == null) {
            TypeInfo typeInfo = lookupTypeInfo(ensureUnqualified(typeName));
            if (typeInfo == null) {
                throw new IllegalArgumentException(String.format("Could not resolve type info for type name %s.", typeName));
            }

            result = resolveTypeInfo(typeInfo);
        }

        return result;
    }

    private DataType resolveTypeNameOrSpecifier(String typeName, TypeSpecifier typeSpecifier) {
        if ((typeName == null || typeName.isEmpty()) && typeSpecifier == null) {
            return null;
        }

        if (typeSpecifier != null) {
            return resolveTypeSpecifier(typeSpecifier);
        }

        return resolveTypeName(typeName);
    }

    private DataType lookupType(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        return resolvedTypes.get(casify(typeName));
    }

    private TypeInfo lookupTypeInfo(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        }

        return typeInfoIndex.get(typeName);
    }

    // This method is used to ensure backwards compatible loading, type names in model info may be qualified with the model name
    private String ensureQualified(String name) {
        String qualifier = String.format("%s.", this.modelInfo.getName());
        if (!name.startsWith(qualifier)) {
            return String.format("%s%s", qualifier, name);
        }

        return name;
    }

    // This method is used to ensure backwards compatible loading, type names in model info may be qualified with the model name
    private String ensureUnqualified(String name) {
        if (name.startsWith(String.format("%s.", this.modelInfo.getName()))) {
            return name.substring(name.indexOf('.') + 1);
        }

        return name;
    }

    private SimpleType resolveSimpleType(SimpleTypeInfo t) {
        String qualifiedTypeName = ensureQualified(t.getName());
        DataType lookupType = lookupType(qualifiedTypeName);
        if (lookupType instanceof ClassType) {
            throw new IllegalArgumentException("Expected instance of SimpleType but found instance of ClassType instead.");
        }
        SimpleType result = (SimpleType)lookupType(qualifiedTypeName);
        if (result == null) {
            if (qualifiedTypeName.equals(DataType.ANY.getName())) {
                result = DataType.ANY;
            }
            else {
                result = new SimpleType(qualifiedTypeName, resolveTypeNameOrSpecifier(t.getBaseType(), t.getBaseTypeSpecifier()));
            }
            resolvedTypes.put(casify(result.getName()), result);
        }

        return result;
    }

    private DataType resolveTypeNameOrSpecifier(TupleTypeInfoElement element) {
        DataType result = resolveTypeNameOrSpecifier(element.getElementType(), element.getElementTypeSpecifier());
        if (result == null) {
            result = resolveTypeNameOrSpecifier(element.getType(), element.getTypeSpecifier());
        }

        return result;
    }

    private Collection<TupleTypeElement> resolveTupleTypeElements(Collection<TupleTypeInfoElement> infoElements) {
        List<TupleTypeElement> elements = new ArrayList();
        for (TupleTypeInfoElement e : infoElements) {
            elements.add(new TupleTypeElement(e.getName(), resolveTypeNameOrSpecifier(e)));
        }
        return elements;
    }

    private TupleType resolveTupleType(TupleTypeInfo t) {
        TupleType result = new TupleType(resolveTupleTypeElements(t.getElement()));
        return result;
    }

    private DataType resolveTypeNameOrSpecifier(ClassInfoElement element) {
        DataType result = resolveTypeNameOrSpecifier(element.getElementType(), element.getElementTypeSpecifier());
        if (result == null) {
            result = resolveTypeNameOrSpecifier(element.getType(), element.getTypeSpecifier());
        }

        return result;
    }

    private Collection<ClassTypeElement> resolveClassTypeElements(Collection<ClassInfoElement> infoElements) {
        List<ClassTypeElement> elements = new ArrayList();
        for (ClassInfoElement e : infoElements) {
            DataType elementType = resolveTypeNameOrSpecifier(e);
            if (elementType == null) {
                elementType = resolveTypeName("System.Any");
            }
            elements.add(new ClassTypeElement(e.getName(), elementType, e.isProhibited(), e.isOneBased()));
        }
        return elements;
    }

    private ClassType resolveClassType(ClassInfo t) {
        if (t.getName() == null) {
            throw new IllegalArgumentException("Class definition must have a name.");
        }

        String qualifiedName = ensureQualified(t.getName());
        ClassType result = (ClassType)lookupType(qualifiedName);
        if (result == null) {
            if (t instanceof ProfileInfo) {
                result = new ProfileType(qualifiedName, resolveTypeNameOrSpecifier(t.getBaseType(), t.getBaseTypeSpecifier()));
            }
            else {
                result = new ClassType(qualifiedName, resolveTypeNameOrSpecifier(t.getBaseType(), t.getBaseTypeSpecifier()));
            }
            resolvedTypes.put(casify(result.getName()), result);
            result.addElements(resolveClassTypeElements(t.getElement()));
            result.setIdentifier(t.getIdentifier());
            result.setLabel(t.getLabel());
            result.setRetrievable(t.isRetrievable());
            result.setPrimaryCodePath(t.getPrimaryCodePath());
        }

        return result;
    }

    private IntervalType resolveIntervalType(IntervalTypeInfo t) {
        IntervalType result = new IntervalType(resolveTypeNameOrSpecifier(t.getPointType(), t.getPointTypeSpecifier()));
        return result;
    }

    private ListType resolveListType(ListTypeInfo t) {
        ListType result = new ListType(resolveTypeNameOrSpecifier(t.getElementType(), t.getElementTypeSpecifier()));
        return result;
    }

    private ChoiceType resolveChoiceType(ChoiceTypeInfo t) {
        ArrayList<DataType> types = new ArrayList<DataType>();
        if (t.getChoice() != null && t.getChoice().size() > 0) {
            for (TypeSpecifier typeSpecifier : t.getChoice()) {
                types.add(resolveTypeSpecifier(typeSpecifier));
            }
        }
        else {
            for (TypeSpecifier typeSpecifier : t.getType()) {
                types.add(resolveTypeSpecifier(typeSpecifier));
            }
        }
        return new ChoiceType(types);
    }
}
