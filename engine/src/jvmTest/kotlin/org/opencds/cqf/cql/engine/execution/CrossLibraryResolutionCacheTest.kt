package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/**
 * Guards the per-evaluation cross-library resolution cache in [State.enterLibrary]. The fixture
 * `IncludedSignatureOutputTests` makes seven calls into a single included library within one
 * evaluation, so each call re-enters that library and is served from the cache after the first.
 * This asserts the cache preserves resolution correctness under repetition — it does not instrument
 * cache hits, which would require a seam into [LibraryManager] the engine does not expose.
 */
internal class CrossLibraryResolutionCacheTest : CqlTestBase() {
    @Test
    fun repeatedCrossLibraryCallsResolveCorrectly() {
        val results = engine.evaluate { library("IncludedSignatureOutputTests") }.onlyResultOrThrow

        assertEquals(5.toCqlInteger(), results["TestOverload"]!!.value)
        assertEquals(Integer.ONE, results["TestOverloadOneInt"]!!.value)
        assertEquals(BigDecimal("2.0").toCqlDecimal(), results["TestOverloadOneDecimal"]!!.value)
        assertEquals(Integer.ONE, results["TestOverloadTwoInts"]!!.value)
        assertEquals(BigDecimal("2.0").toCqlDecimal(), results["TestOverloadTwoDecimals"]!!.value)
        assertEquals(
            BigDecimal("2.0").toCqlDecimal(),
            results["TestOverloadOneIntOneDecimal"]!!.value,
        )
        assertEquals(Integer.ONE, results["TestOverloadOneIntTwoDecimal"]!!.value)
    }
}
