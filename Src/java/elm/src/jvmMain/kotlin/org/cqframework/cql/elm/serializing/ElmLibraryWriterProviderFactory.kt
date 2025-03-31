package org.cqframework.cql.elm.serializing

import java.util.*

object ElmLibraryWriterProviderFactory {
    fun providers(refresh: Boolean): Iterator<ElmLibraryWriterProvider> {
        val loader = ServiceLoader.load(ElmLibraryWriterProvider::class.java)
        if (refresh) {
            loader.reload()
        }

        return loader.iterator()
    }

    fun getProvider(): ElmLibraryWriterProvider {
        val providers = providers(false)
        require(providers.hasNext()) {
            @Suppress("MaxLineLength")
            "No ElmLibraryWriterProviders found on the classpath. You need to add a dependency on the 'info.cqframework:serialization' package, or provide your own implementation."
        }
        val p = providers.next()
        require(!providers.hasNext()) {
            "Multiple ElmLibraryWriterProviders found on the classpath."
        }
        return p
    }
}
