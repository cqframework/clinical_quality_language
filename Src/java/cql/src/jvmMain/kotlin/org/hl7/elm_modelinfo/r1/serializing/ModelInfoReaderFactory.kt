@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import java.util.*
import kotlin.jvm.JvmStatic

object ModelInfoReaderFactory {
    fun providers(refresh: Boolean): Iterator<ModelInfoReaderProvider> {
        val loader = ServiceLoader.load(ModelInfoReaderProvider::class.java)
        if (refresh) {
            loader.reload()
        }

        return loader.iterator()
    }

    @JvmStatic
    @Suppress("TooGenericExceptionThrown")
    fun getReader(contentType: String): ModelInfoReader {
        val providers = providers(false)
        if (providers.hasNext()) {
            val p = providers.next()
            if (providers.hasNext()) {
                throw RuntimeException(
                    java.lang.String.join(
                        "Multiple ModelInfoReaderProviders found on the classpath."
                    )
                )
            }

            return p.create(contentType)
        }

        throw RuntimeException(
            java.lang.String.join(
                " ",
                "No ModelInfoReaderProviders found on the classpath.",
                "You need to add a dependency on the 'info.cqframework:serialization' package,",
                "or provide your own implementation."
            )
        )
    }
}
