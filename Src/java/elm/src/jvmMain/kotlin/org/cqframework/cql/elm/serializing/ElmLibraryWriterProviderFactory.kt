@file:Suppress("PackageNaming")

package org.cqframework.cql.elm.serializing

import java.util.*
import kotlin.jvm.JvmStatic

object ElmLibraryWriterProviderFactory {
    fun providers(refresh: Boolean): Iterator<ElmLibraryWriterProvider> {
        val loader = ServiceLoader.load(ElmLibraryWriterProvider::class.java)
        if (refresh) {
            loader.reload()
        }

        return loader.iterator()
    }

    @JvmStatic
    @Suppress("TooGenericExceptionThrown")
    fun getReader(contentType: String): ElmLibraryWriter {
        val providers = providers(false)
        if (providers.hasNext()) {
            val p = providers.next()
            if (providers.hasNext()) {
                throw RuntimeException(
                    java.lang.String.join("Multiple ElmLibraryWriters found on the classpath.")
                )
            }

            return p.create(contentType)
        }

        throw RuntimeException(
            java.lang.String.join(
                " ",
                "No ElmLibraryWriterProviders found on the classpath.",
                "You need to add a dependency on the 'info.cqframework:serialization' package,",
                "or provide your own implementation."
            )
        )
    }
}
