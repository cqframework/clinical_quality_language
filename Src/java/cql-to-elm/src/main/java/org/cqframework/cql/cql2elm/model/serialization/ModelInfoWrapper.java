package org.cqframework.cql.cql2elm.model.serialization;

import org.hl7.elm_modelinfo.r1.ModelInfo;

public class ModelInfoWrapper {

    private ModelInfo modelInfo;
    public ModelInfo getModelInfo() {
        return this.modelInfo;
    }

    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }
}
