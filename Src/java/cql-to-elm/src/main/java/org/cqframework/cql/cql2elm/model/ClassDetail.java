package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.TupleType;
import org.hl7.elm_modelinfo.r1.ClassInfo;

public class ClassDetail {
    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public TupleType getModelClass() {
        return modelClass;
    }

    public void setModelClass(TupleType modelClass) {
        this.modelClass = modelClass;
    }

    private ClassInfo classInfo;
    private TupleType modelClass;
}
