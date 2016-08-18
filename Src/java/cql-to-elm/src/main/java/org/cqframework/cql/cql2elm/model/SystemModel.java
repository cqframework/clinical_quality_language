package org.cqframework.cql.cql2elm.model;

import org.antlr.v4.runtime.misc.NotNull;
import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.SimpleType;
import org.hl7.elm_modelinfo.r1.ModelInfo;

public class SystemModel extends Model {
    public SystemModel(@NotNull ModelInfo modelInfo) throws ClassNotFoundException {
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

    public DataType getDecimal() {
        return this.resolveTypeName("Decimal");
    }

    public DataType getString() {
        return this.resolveTypeName("String");
    }

    public DataType getDateTime() {
        return this.resolveTypeName("DateTime");
    }

    public DataType getTime() {
        return this.resolveTypeName("Time");
    }

    public DataType getQuantity() { return this.resolveTypeName("Quantity"); }

    public DataType getCode() { return this.resolveTypeName("Code"); }

    public DataType getConcept() { return this.resolveTypeName("Concept"); }

    public DataType getVoid() { return new SimpleType("Void"); }
}
