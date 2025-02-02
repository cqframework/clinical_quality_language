package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.Model

interface ModelResolver {
    fun getModel(modelName: String): Model
}
