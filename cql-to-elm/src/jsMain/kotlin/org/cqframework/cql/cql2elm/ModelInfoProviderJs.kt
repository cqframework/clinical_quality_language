package org.cqframework.cql.cql2elm

import org.hl7.cql.model.ModelInfoProvider

actual fun getModelInfoProviders(refresh: Boolean): Iterator<ModelInfoProvider> {
    // No-op implementation for platforms without ServiceLoader support
    return emptyList<ModelInfoProvider>().iterator()
}
