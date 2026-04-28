package org.opencds.cqf.cql.engine.data

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class SystemDataProviderTest {
    @Test
    fun resolveIdAlwaysReturnsNull() {
        val provider = SystemDataProvider()

        Assertions.assertNull(provider.resolveId("someObject".toCqlString()))
        Assertions.assertNull(provider.resolveId(Date(2011)))
        Assertions.assertNull(provider.resolveId(Integer.ONE))
    }
}
