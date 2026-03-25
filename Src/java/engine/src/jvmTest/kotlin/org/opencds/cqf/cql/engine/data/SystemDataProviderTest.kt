package org.opencds.cqf.cql.engine.data

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SystemDataProviderTest {
    @Test
    fun resolveIdAlwaysReturnsNull() {
        val provider = SystemDataProvider()

        Assertions.assertNull(provider.resolveId("someObject"))
        Assertions.assertNull(provider.resolveId(java.util.Date()))
        Assertions.assertNull(provider.resolveId(1))
    }
}
