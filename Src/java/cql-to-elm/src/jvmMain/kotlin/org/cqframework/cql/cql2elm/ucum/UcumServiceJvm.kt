package org.cqframework.cql.cql2elm.ucum

import java.util.*

actual val defaultLazyUcumService = lazy {
    ServiceLoader.load(UcumService::class.java).firstOrNull()
        ?: error(
            """
            No UCUM service implementation found. 
            Please ensure a UCUM service implementation is available on the classpath.
            The 'ucum' module is a reference implementation that can be used for this purpose.
            """
                .trimIndent()
        )
}
