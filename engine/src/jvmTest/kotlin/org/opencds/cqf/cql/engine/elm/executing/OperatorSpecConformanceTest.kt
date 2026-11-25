package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.assertEquals
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.execution.MapLibrarySourceProvider

/** Operator behavior the CQL specification requires and the engine previously got wrong. */
class OperatorSpecConformanceTest : CqlTestBase() {
    private fun evaluate(cql: String): String {
        val manager = libraryManager
        manager.librarySourceLoader.registerProvider(
            MapLibrarySourceProvider(
                mutableMapOf(VersionedIdentifier().withId("T") to "library T\ndefine X: $cql")
            )
        )
        return CqlEngine(Environment(manager))
            .evaluate { library("T") { expressions("X") } }
            .onlyResultOrThrow["X"]!!
            .value
            .toString()
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value =
            [
                // Arithmetic that cannot be represented is null rather than overflowed
                "(maximum Integer) + 1 | null",
                "(minimum Integer) - 1 | null",
                "(maximum Integer) * 2 | null",
                "-(minimum Integer) | null",
                "Abs(minimum Integer) | null",
                "(maximum Long) + 1L | null",
                "(minimum Long) - 1L | null",
                "(maximum Long) * 2L | null",
                "(minimum Long) * -1L | null",
                "-(minimum Long) | null",
                "Abs(minimum Long) | null",
                "(maximum Integer) - 1 + 1 | 2147483647",
                "(minimum Long) + 1L - 1L | -9223372036854775808L",
                "-(maximum Long) | -9223372036854775807L",
                "Product({ 65536, 65536 }) | null",
                // ToInteger and ToLong accept only (+|-)?#0
                "ToInteger('1.0') | null",
                "ToInteger('+12') | 12",
                "ToInteger('2147483648') | null",
                "ConvertsToInteger('1.0') | false",
                "ToLong('1.0') | null",
                "ToLong('-12') | -12L",
                "ConvertsToLong('1.0') | false",
                // ToInteger(Long) is null when the value is out of range
                "ToInteger(12L) | 12",
                "ToInteger(2147483648L) | null",
                "ConvertsToInteger(2147483648L) | false",
                "ConvertsToInteger(12L) | true",
                // IndexOf uses equality, not equivalence
                "IndexOf({ 'a', 'B' }, 'b') | -1",
                "IndexOf({ 1 'm', 1.1 'm' }, 1.1 'm') | 1",
                "IndexOf({ 1, null }, null) | null",
                // An empty pattern is last found at the end of the string
                "LastPositionOf('', 'Say hi to Ohio!') | 15",
                "LastPositionOf('hi', 'Say hi to Ohio!') | 11",
                // Product ignores null elements, as the other aggregates do
                "Product({ 5, 4, null }) | 20",
                "Product({ null as Integer, null }) | null",
            ],
    )
    fun conformsToSpec(cql: String, expected: String) {
        assertEquals(expected, evaluate(cql), cql)
    }
}
