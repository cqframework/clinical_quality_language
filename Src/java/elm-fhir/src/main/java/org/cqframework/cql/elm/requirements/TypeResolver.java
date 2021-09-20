package org.cqframework.cql.elm.requirements;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.model.Model;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.IntervalType;
import org.hl7.cql.model.ListType;

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
