package org.cqframework.cql.elm.serializing

import java.util.*

object ElmLibraryReaderProviderFactory {
    fun providers(refresh: Boolean): Iterator<ElmLibraryReaderProvider> {
        val loader = ServiceLoader.load(ElmLibraryReaderProvider::class.java)
        if (refresh) {
            loader.reload()
        }

        return loader.iterator()
    }

    fun getProvider(): ElmLibraryReaderProvider {
        val providers = providers(false)
        require(providers.hasNext()) {
            @Suppress("MaxLineLength")
            "No ElmLibraryReaderProviders found on the classpath. You need to add a dependency on the 'info.cqframework:serialization' package, or provide your own implementation."
        }
        val p = providers.next()
        require(!providers.hasNext()) {
            "Multiple ElmLibraryReaderProviders found on the classpath."
        }
        return p
    }
}
