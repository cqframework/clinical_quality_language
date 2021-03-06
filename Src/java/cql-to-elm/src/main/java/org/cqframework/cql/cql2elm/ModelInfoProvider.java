package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

public interface ModelInfoProvider {
    ModelInfo load(VersionedIdentifier modelIdentifier);
}
