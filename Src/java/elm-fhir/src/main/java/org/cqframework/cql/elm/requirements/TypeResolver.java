package org.cqframework.cql.elm.requirements;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.model.Model;
import org.hl7.cql.model.*;
import org.hl7.elm.r1.*;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.namespace.QName;
import java.util.ArrayList;

public class TypeResolver {
    public TypeResolver(LibraryManager libraryManager) {
        if (libraryManager == null) {
            throw new IllegalArgumentException("libraryManager is required");
        }
        this.libraryManager = libraryManager;
    }

    private LibraryManager libraryManager;
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    public String getTypeUri(DataType type) {
        if (type instanceof ListType) {
            return getTypeUri(((ListType)type).getElementType());
        }
        if (type instanceof ClassType) {
            ClassType classType = (ClassType)type;
            if (classType.getIdentifier() != null) {
                return classType.getIdentifier();
            }
        }

        if (type instanceof NamedType) {
            return dataTypeToQName(type).getLocalPart();
        }

        return null;
    }

    public QName dataTypeToProfileQName(DataType type) {
        if (type instanceof ClassType) {
            ClassType classType = (ClassType)type;
            if (classType.getIdentifier() != null) {
                int tailIndex = classType.getIdentifier().lastIndexOf('/');
                if (tailIndex > 0) {
                    String tail = classType.getIdentifier().substring(tailIndex + 1);
                    String namespace = classType.getIdentifier().substring(0, tailIndex);
                    return new QName(namespace, tail);
                }
            }
        }

        if (type instanceof NamedType) {
            return dataTypeToQName(type);
        }

        return null;
    }
    /**
     * Return the QName for the given type (without target mapping)
     * This is to preserve data requirements reporting for profiled types when
     * reported against unbound data requirements. This will only work when
     * the ELM tree has type references (which typically means it came
     * straight from the translator, although type resolution could be
     * performed by a visitor on an ELM tree).
     * @param type The data type to determine a QName for
     * @return The QName for the given type (without target mapping)
     */
    public QName dataTypeToQName(DataType type) {
        if (type instanceof NamedType) {
            NamedType namedType = (NamedType)type;
            ModelInfo modelInfo = libraryManager.getModelManager().resolveModel(namedType.getNamespace()).getModelInfo();
            return new QName(modelInfo.getUrl(), namedType.getSimpleName());
        }

        // ERROR:
        throw new IllegalArgumentException("A named type is required in this context.");
    }

    public DataType resolveTypeName(QName typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is required");
        }

        // NOTE: This resolution path is ignoring prefix, namespace is required
        if (typeName.getNamespaceURI() == null || typeName.getNamespaceURI().equals("")) {
            throw new IllegalArgumentException("namespaceURI is required");
        }

        Model model = libraryManager.getModelManager().resolveModelByUri(typeName.getNamespaceURI());
        DataType result = model.resolveTypeName(typeName.getLocalPart());
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve type %s", typeName.toString()));
        }
        return result;
    }

    public DataType resolveTypeSpecifier(TypeSpecifier typeSpecifier) {
        if (typeSpecifier == null) {
            throw new IllegalArgumentException("typeSpecifier is required");
        }

        // If the typeSpecifier already has a type, use it
        if (typeSpecifier.getResultType() != null) {
            return typeSpecifier.getResultType();
        }

        if (typeSpecifier instanceof NamedTypeSpecifier) {
            return resolveNamedTypeSpecifier((NamedTypeSpecifier)typeSpecifier);
        }
        else if (typeSpecifier instanceof TupleTypeSpecifier) {
            return resolveTupleTypeSpecifier((TupleTypeSpecifier)typeSpecifier);
        }
        else if (typeSpecifier instanceof IntervalTypeSpecifier) {
            return resolveIntervalTypeSpecifier((IntervalTypeSpecifier)typeSpecifier);
        }
        else if (typeSpecifier instanceof ListTypeSpecifier) {
            return resolveListTypeSpecifier((ListTypeSpecifier)typeSpecifier);
        }
        else if (typeSpecifier instanceof ChoiceTypeSpecifier) {
            return resolveChoiceTypeSpecifier((ChoiceTypeSpecifier)typeSpecifier);
        }
        else {
            throw new IllegalArgumentException(String.format("Unknown type specifier category: %s", typeSpecifier.getClass().getSimpleName()));
        }
    }

    private DataType resolveNamedTypeSpecifier(NamedTypeSpecifier typeSpecifier) {
        return resolveTypeName(typeSpecifier.getName());
    }

    private DataType resolveTupleTypeSpecifier(TupleTypeSpecifier typeSpecifier) {
        TupleType tupleType = new TupleType();
        for (TupleElementDefinition element : typeSpecifier.getElement()) {
            TupleTypeElement tupleElement = new TupleTypeElement(element.getName(), resolveTypeSpecifier(element.getElementType()));
            tupleType.addElement(tupleElement);
        }
        return tupleType;
    }

    private DataType resolveIntervalTypeSpecifier(IntervalTypeSpecifier typeSpecifier) {
        return new IntervalType(resolveTypeSpecifier(typeSpecifier.getPointType()));
    }

    private DataType resolveListTypeSpecifier(ListTypeSpecifier typeSpecifier) {
        return new ListType(resolveTypeSpecifier(typeSpecifier.getElementType()));
    }

    private DataType resolveChoiceTypeSpecifier(ChoiceTypeSpecifier typeSpecifier) {
        ArrayList<DataType> choiceTypes = new ArrayList<DataType>();
        for (TypeSpecifier choiceType : typeSpecifier.getChoice()) {
            choiceTypes.add(resolveTypeSpecifier(choiceType));
        }
        return new ChoiceType(choiceTypes);
    }

    public DataType resolveTypeName(String modelName, String typeName) {
        if (modelName == null || modelName.equals("")) {
            throw new IllegalArgumentException("Unqualified type name cannot be resolved");
        }

        // NOTE: Assumption here is that the appropriate version of the given model has already been resolved in this context
        Model model = libraryManager.getModelManager().resolveModel(modelName);
        DataType result = model.resolveTypeName(typeName);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not resolve type %s.%s", modelName, typeName));
        }
        return result;
    }

    private DataType stringType;
    public DataType getStringType() {
        if (stringType == null) {
            stringType = resolveTypeName("System", "String");
        }
        return stringType;
    }

    private DataType codeType;
    public DataType getCodeType() {
        if (codeType == null) {
            codeType = resolveTypeName("System", "Code");
        }
        return codeType;
    }

    private DataType conceptType;
    public DataType getConceptType() {
        if (conceptType == null) {
            conceptType = resolveTypeName("System", "Concept");
        }
        return conceptType;
    }

    private DataType valueSetType;
    public DataType getValueSetType() {
        if (valueSetType == null) {
            valueSetType = resolveTypeName("System", "ValueSet");
        }
        return valueSetType;
    }

    private DataType codeSystemType;
    public DataType getCodeSystemType() {
        if (codeSystemType == null) {
            codeSystemType = resolveTypeName("System", "CodeSystem");
        }
        return codeSystemType;
    }

    private DataType dateType;
    public DataType getDateType() {
        if (dateType == null) {
            dateType = resolveTypeName("System", "Date");
        }
        return dateType;
    }

    private DataType dateTimeType;
    public DataType getDateTimeType() {
        if (dateTimeType == null) {
            dateTimeType = resolveTypeName("System", "DateTime");
        }
        return dateTimeType;
    }

    private DataType timeType;
    public DataType getTimeType() {
        if (timeType == null) {
            timeType = resolveTypeName("System", "Time");
        }
        return timeType;
    }

    private DataType booleanType;
    public DataType getBooleanType() {
        if (booleanType == null) {
            booleanType = resolveTypeName("System", "Boolean");
        }
        return booleanType;
    }

    private DataType integerType;
    public DataType getIntegerType() {
        if (integerType == null) {
            integerType = resolveTypeName("System", "Integer");
        }
        return integerType;
    }

    private DataType decimalType;
    public DataType getDecimalType() {
        if (decimalType == null) {
            decimalType = resolveTypeName("System", "Decimal");
        }
        return decimalType;
    }

    private DataType quantityType;
    public DataType getQuantityType() {
        if (quantityType == null) {
            quantityType = resolveTypeName("System", "Quantity");
        }
        return quantityType;
    }

    public boolean isTerminologyType(DataType dataType) {
        if (dataType != null) {
            return
                dataType.isSubTypeOf(getCodeType())
                    || dataType.isSubTypeOf(getConceptType())
                    || dataType.isSubTypeOf(getValueSetType())
                    || dataType.isSubTypeOf(getCodeSystemType())
                    || dataType.isSubTypeOf(getStringType())
                    || (dataType instanceof ListType && (
                            ((ListType)dataType).getElementType().isSubTypeOf(getCodeType())
                                || ((ListType)dataType).getElementType().isSubTypeOf(getConceptType())
                                || ((ListType)dataType).getElementType().isSubTypeOf(getStringType())));
        }

        return false;
    }

    public boolean isDateType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getDateType()) || dataType.isSubTypeOf(getDateTimeType())
                    || (dataType instanceof IntervalType && (
                            ((IntervalType)dataType).getPointType().isSubTypeOf(getDateType())
                                    || ((IntervalType)dataType).getPointType().isSubTypeOf(getDateTimeType())));
        }

        return false;
    }

    public boolean isDateTimeType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getDateTimeType())
                    || (dataType instanceof IntervalType && ((IntervalType)dataType).getPointType().isSubTypeOf(getDateTimeType()));
        }

        return false;
    }

    public boolean isTimeType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getTimeType());
        }

        return false;
    }

    public boolean isIntegerType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getIntegerType());
        }

        return false;
    }

    public boolean isDecimalType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getDecimalType());
        }

        return false;
    }

    public boolean isQuantityType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getQuantityType());
        }

        return false;
    }

    public boolean isBooleanType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getBooleanType());
        }

        return false;
    }

    public boolean isStringType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getStringType());
        }

        return false;
    }

    public boolean isListType(DataType dataType) {
        return dataType instanceof ListType;
    }

    public boolean isIntervalType(DataType dataType) {
        return dataType instanceof IntervalType;
    }

    public boolean isCodeType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getCodeType());
        }

        return false;
    }

    public boolean isConceptType(DataType dataType) {
        if (dataType != null) {
            return dataType.isSubTypeOf(getConceptType());
        }

        return false;
    }
}
