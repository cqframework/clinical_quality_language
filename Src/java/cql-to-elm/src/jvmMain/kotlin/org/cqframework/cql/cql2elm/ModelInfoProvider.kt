package org.cqframework.cql.cql2elm

import java.util.*
import org.hl7.cql.model.ModelInfoProvider

fun getModelInfoProviders(refresh: Boolean): Iterator<ModelInfoProvider> {
    val loader = ServiceLoader.load(ModelInfoProvider::class.java)
    if (refresh) {
        loader.reload()
    }
    return loader.iterator()
}
