package org.cqframework.cql.cql2elm.model;

import org.hl7.elm_modelinfo.r1.ModelInfo;

public interface ModelInfoProvider {
    ModelInfo load();
}
