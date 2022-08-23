package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.Model;

public interface ModelResolver {
    Model getModel(String modelName);
}
