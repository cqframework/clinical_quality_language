package org.cqframework.cql.elm.serializing

import java.util.*

@Suppress("TooGenericExceptionThrown")
object ElmLibraryReaderFactory {
    fun providers(refresh: Boolean): Iterator<ElmLibraryReaderProvider> {
        val loader = ServiceLoader.load(ElmLibraryReaderProvider::class.java)
        if (refresh) {
            loader.reload()
        }

        return loader.iterator()
    }

    fun getReader(contentType: String): ElmLibraryReader {
        val providers = providers(false)
        if (providers.hasNext()) {
            val p = providers.next()
            if (providers.hasNext()) {
                throw RuntimeException(
                    """Multiple ElmLibraryReaderProviders found on the classpath.
                        You need to remove a reference to either the 'elm-jackson' or the 'elm-jaxb' package"""
                        .trimIndent()
                )
            }

            return p.create(contentType)
        }

        throw RuntimeException(
            """No ElmLibraryReaderProviders found on the classpath.,
               You need to add a reference to one of the 'elm-jackson' or 'elm-jaxb' packages,
               or provide your own implementation."""
                .trimIndent()
        )
    }
}
