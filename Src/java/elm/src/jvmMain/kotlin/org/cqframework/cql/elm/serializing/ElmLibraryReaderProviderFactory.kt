@file:Suppress("PackageNaming")

package org.cqframework.cql.elm.serializing

import java.util.*
import kotlin.jvm.JvmStatic

object ElmLibraryReaderProviderFactory {
    fun providers(refresh: Boolean): Iterator<ElmLibraryReaderProvider> {
        val loader = ServiceLoader.load(ElmLibraryReaderProvider::class.java)
        if (refresh) {
            loader.reload()
        }

        return loader.iterator()
    }

    @JvmStatic
    @Suppress("TooGenericExceptionThrown")
    fun getReader(contentType: String): ElmLibraryReader {
        val providers = providers(false)
        if (providers.hasNext()) {
            val p = providers.next()
            if (providers.hasNext()) {
                throw RuntimeException(
                    java.lang.String.join("Multiple ElmLibraryReaders found on the classpath.")
                )
            }

            return p.create(contentType)
        }

        throw RuntimeException(
            java.lang.String.join(
                " ",
                "No ElmLibraryReaderProviders found on the classpath.",
                "You need to add a dependency on the 'info.cqframework:serialization' package,",
                "or provide your own implementation."
            )
        )
    }
}
