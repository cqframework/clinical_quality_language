package org.cqframework.cql.cql2elm.operators

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromSource
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.Companion.hasTypeAndResult
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.List
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Tuple
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class QueryTest {
    @Test
    fun singularSource() {
        val def: ExpressionDef? = defs!!["Singular Source"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(Tuple::class.java, "tuple{id:System.Integer,name:System.String}"),
        )
    }

    @Test
    fun pluralSource() {
        val def: ExpressionDef? = defs!!["Plural Source"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(List::class.java, "list<tuple{id:System.Integer,name:System.String}>"),
        )
    }

    @Test
    fun singularSourceQuery() {
        val def: ExpressionDef? = defs!!["Singular Source Query"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(Query::class.java, "tuple{id:System.Integer,name:System.String}"),
        )
    }

    @Test
    fun singularMultipleSourceQuery() {
        val def: ExpressionDef? = defs!!["Singular Multiple Source Query"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(
                Query::class.java,
                "tuple{S1:tuple{id:System.Integer,name:System.String},S2:tuple{id:System.Integer,name:System.String}}",
            ),
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun pluralMultipleSourceQuery() {
        val def: ExpressionDef? = defs!!["Plural Multiple Source Query"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(
                Query::class.java,
                "list<tuple{P1:tuple{id:System.Integer,name:System.String},P2:tuple{id:System.Integer,name:System.String}}>",
            ),
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun mixedMultipleSourceQuery() {
        val def: ExpressionDef? = defs!!["Mixed Multiple Source Query"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(
                Query::class.java,
                "list<tuple{S:tuple{id:System.Integer,name:System.String},P:tuple{id:System.Integer,name:System.String}}>",
            ),
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun mixedMultipleSourceQuery2() {
        val def: ExpressionDef? = defs!!["Mixed Multiple Source Query 2"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(
                Query::class.java,
                "list<tuple{P:tuple{id:System.Integer,name:System.String},S:tuple{id:System.Integer,name:System.String}}>",
            ),
        )
    }

    @Test
    fun mixedMultipleSourceQueryWithReturn() {
        val def: ExpressionDef? = defs!!["Mixed Multiple Source Query With Return"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(Query::class.java, "list<tuple{id:System.Integer,name:System.String}>"),
        )
    }

    @Test
    fun singularSourceWithPluralRelationship() {
        val def: ExpressionDef? = defs!!["Singular Source With Plural Relationship"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(Query::class.java, "tuple{id:System.Integer,name:System.String}"),
        )
    }

    @Test
    fun pluralSourceWithSingularRelationship() {
        val def: ExpressionDef? = defs!!["Plural Source With Singular Relationship"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(Query::class.java, "list<tuple{id:System.Integer,name:System.String}>"),
        )
    }

    @Test
    fun singularSourceWithPluralRelationshipAndReturn() {
        val def: ExpressionDef? = defs!!["Singular Source With Plural Relationship And Return"]
        assertThat<ExpressionDef?>(
            def,
            hasTypeAndResult(Query::class.java, "tuple{id:System.Integer,name:System.String}"),
        )
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
                        .getResourceAsStream("../OperatorTests/Query.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(modelManager),
                )
            assertThat(translator.errors.size, `is`(0))
            val library = translator.toELM()
            defs = HashMap()
            for (def in library!!.statements!!.def) {
                defs!![def.name] = def
            }
        }
    }
}
