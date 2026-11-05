package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Query
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class SortingTest {
    @Test
    fun simpleSort() {
        val query = defs!!["TestSimpleSort"]!!.expression as Query?
        val sort = query!!.sort
        assertThat(sort!!.by.size, `is`(1))
        assertThat(sort.by[0].direction, `is`(org.hl7.elm.r1.SortDirection.DESC))
    }

    @Test
    fun descendingSort() {
        val query = defs!!["TestDescendingSort"]!!.expression as Query?
        val sort = query!!.sort
        assertThat(sort!!.by.size, `is`(1))
        assertThat(sort.by[0].direction, `is`(org.hl7.elm.r1.SortDirection.DESCENDING))
    }

    @Test
    fun ascSort() {
        val query = defs!!["TestAscSort"]!!.expression as Query?
        val sort = query!!.sort
        assertThat(sort!!.by.size, `is`(1))
        assertThat(sort.by[0].direction, `is`(org.hl7.elm.r1.SortDirection.ASC))
    }

    @Test
    fun ascendingSort() {
        val query = defs!!["TestAscendingSort"]!!.expression as Query?
        val sort = query!!.sort
        assertThat(sort!!.by.size, `is`(1))
        assertThat(sort.by[0].direction, `is`(org.hl7.elm.r1.SortDirection.ASCENDING))
    }

    companion object {
        private var defs: MutableMap<String?, ExpressionDef?>? = null

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun setup() {
            val modelManager = ModelManager()
            val translator =
                fromSource(
                    QueryTest::class
                        .java
                        .getResourceAsStream("../OperatorTests/Sorting.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(modelManager),
                )

            // The alias test creates an error
            assertThat(translator.errors.size, `is`(1))

            val library = translator.toELM()
            defs = HashMap()
            for (def in library!!.statements!!.def) {
                defs!![def.name] = def
            }
        }
    }
}
