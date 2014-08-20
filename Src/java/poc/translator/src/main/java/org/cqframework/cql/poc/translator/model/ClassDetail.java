package org.cqframework.cql.poc.translator.model;

import com.sun.istack.internal.NotNull;
import org.hl7.elm_modelinfo.r1.ClassInfo;

/**
 * Created by Bryn on 8/20/2014.
 */
public class ClassDetail {
    public ClassInfo getClassInfo() {
        return _classInfo;
    }

    public void setClassInfo(@NotNull ClassInfo classInfo) {
        _classInfo = classInfo;
    }

    public Class getModelClass() {
        return _modelClass;
    }

    public void setModelClass(@NotNull Class modelClass) {
        _modelClass = modelClass;
    }

    private ClassInfo _classInfo;
    private Class _modelClass;
}
