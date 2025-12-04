package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

internal class ExpandValueSetTest {
    @Test
    fun all_expand_valueset() {
        val libraryManager = LibraryManager(ModelManager())
        libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())

        val expected = Code().withCode("M").withSystem("http://test.com/system")
        val terminologyProvider: TerminologyProvider =
            object : TerminologyProvider {
                override fun `in`(code: Code, valueSet: ValueSetInfo): Boolean {
                    return true
                }

                override fun expand(valueSet: ValueSetInfo): Iterable<Code> {
                    return mutableListOf(expected)
                }

                override fun lookup(code: Code, codeSystem: CodeSystemInfo): Code {
                    error("Not implemented")
                }
            }

        val environment = Environment(libraryManager, null, terminologyProvider)

        val engine = CqlEngine(environment)
        val results = engine.evaluate { library("ExpandValueSetTest") }.onlyResultOrThrow

        val actual = results["ExpandValueSet"]!!.value as MutableList<*>
        Assertions.assertEquals(1, actual.size)

        CqlConceptTest.assertEqual(expected, actual[0] as CqlType)
    }
}
