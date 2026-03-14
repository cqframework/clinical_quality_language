package org.opencds.cqf.cql.engine.data

import java.time.Month
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Date

internal class SystemDataProviderTest {
    @Test
    fun resolveMissingPropertyReturnsNull() {
        val provider = SystemDataProvider()

        val date = Date(2019, Month.JANUARY.value, 1)

        val value = provider.resolvePath(date, "notapath")
        Assertions.assertNull(value)
    }

    @Test
    fun resolveIdAlwaysReturnsNull() {
        val provider = SystemDataProvider()

        Assertions.assertNull(provider.resolveId("someObject"))
        Assertions.assertNull(provider.resolveId(java.util.Date()))
        Assertions.assertNull(provider.resolveId(1))
    }
}
