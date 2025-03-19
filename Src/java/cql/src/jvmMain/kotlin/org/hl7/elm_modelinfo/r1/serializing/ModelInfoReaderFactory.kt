@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import java.util.*

object ModelInfoReaderFactory {
    fun providers(refresh: Boolean): Iterator<ModelInfoReaderProvider> {
        val loader = ServiceLoader.load(ModelInfoReaderProvider::class.java)
        if (refresh) {
            loader.reload()
        }

        return loader.iterator()
    }

    @Suppress("TooGenericExceptionThrown")
    fun getReader(contentType: String): ModelInfoReader {
        val providers = providers(false)
        if (providers.hasNext()) {
            val p = providers.next()
            if (providers.hasNext()) {
                throw RuntimeException(
                    java.lang.String.join(
                        " ",
                        "Multiple ModelInfoReaderProviders found on the classpath.",
                        "You need to remove a reference to either the 'model-jackson' or the 'model-jaxb' package"
                    )
                )
            }

            return p.create(contentType)
        }

        throw RuntimeException(
            java.lang.String.join(
                " ",
                "No ModelInfoReaderProviders found on the classpath.",
                "You need to add a reference to one of the 'model-jackson' or 'model-jaxb' packages,",
                "or provide your own implementation."
            )
        )
    }
}
