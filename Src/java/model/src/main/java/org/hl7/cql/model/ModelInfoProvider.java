package org.hl7.cql.model;

import org.hl7.elm_modelinfo.r1.ModelInfo;

public interface ModelInfoProvider {
    ModelInfo load(ModelIdentifier modelIdentifier);
}
