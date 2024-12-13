package org.cqframework.cql.cql2elm.ucum

import java.util.*

@Suppress("TooGenericExceptionThrown")
object UcumServiceFactory {
    fun load(): UcumService {
        return ServiceLoader.load(UcumService::class.java).firstOrNull()
            ?: throw RuntimeException(
                """No UCUM service implementation found. 
                   Please ensure a UCUM service implementation is available on the classpath.
                   The 'ucum' module is a reference implementation that can be used for this purpose."""
            )
    }
}
