package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.DataType;
import org.hl7.cql.model.SimpleType;
import org.hl7.elm_modelinfo.r1.ModelInfo;

public class SystemModel extends Model {
    public SystemModel(ModelInfo modelInfo) throws ClassNotFoundException {
        super(modelInfo, null);
    }

    public DataType getAny() {
        return this.resolveTypeName("Any");
    }

    public DataType getBoolean() {
        return this.resolveTypeName("Boolean");
    }

    public DataType getInteger() {
        return this.resolveTypeName("Integer");
    }

    public DataType getLong() {
        return this.resolveTypeName("Long");
    }

    public DataType getDecimal() {
        return this.resolveTypeName("Decimal");
    }

    public DataType getString() {
        return this.resolveTypeName("String");
    }

    public DataType getDateTime() {
        return this.resolveTypeName("DateTime");
    }

    public DataType getDate() {
        return this.resolveTypeName("Date");
    }

    public DataType getTime() {
        return this.resolveTypeName("Time");
    }

    public DataType getQuantity() {
        return this.resolveTypeName("Quantity");
    }

    public DataType getRatio() {
        return this.resolveTypeName("Ratio");
    }

    public DataType getCode() {
        return this.resolveTypeName("Code");
    }

    public DataType getConcept() {
        return this.resolveTypeName("Concept");
    }

    public DataType getVocabulary() {
        return this.resolveTypeName("Vocabulary");
    }

    public DataType getCodeSystem() {
        return this.resolveTypeName("CodeSystem");
    }

    public DataType getValueSet() {
        return this.resolveTypeName("ValueSet");
    }

    public DataType getVoid() {
        return new SimpleType("Void");
    }
}
