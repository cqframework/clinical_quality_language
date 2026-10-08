package org.opencds.cqf.cql.engine.data

import kotlin.test.Test
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlString

class SystemDataProviderTest {
    @Test
    fun resolveIdAlwaysReturnsNull() {
        val provider = SystemDataProvider()

        assertNull(provider.resolveId("someObject".toCqlString()))
        assertNull(provider.resolveId(Date(2011)))
        assertNull(provider.resolveId(Integer.ONE))
    }
}
