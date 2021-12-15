package org.cqframework.cql.elm.requirements;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.model.Model;
import org.hl7.cql.model.*;
import org.hl7.elm.r1.*;

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

    public boolean isListType(DataType dataType) {
        return dataType instanceof ListType;
    }

    public boolean isIntervalType(DataType dataType) {
        return dataType instanceof IntervalType;
    }
}
