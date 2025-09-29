package org.cqframework.cql.cql2elm

import java.io.IOException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.cql_annotations.r1.Annotation
import org.hl7.cql_annotations.r1.Narrative
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.junit.jupiter.api.Test

internal class CommentTests {
    @Test
    @Throws(IOException::class)
    fun comments() {
        val translator =
            TestUtils.runSemanticTest(
                "TestComments.cql",
                0,
                CqlCompilerOptions.Options.EnableAnnotations
            )
        val library = translator.translatedLibrary
        assertThat<Any?>(library!!.library!!.annotation, Matchers.notNullValue())

        val defs: MutableMap<String, ExpressionDef> = HashMap()

        if (library.library!!.statements != null) {
            for (def in library.library!!.statements!!.def) {
                defs[def.name!!] = def
            }
        }

        // Validate that boolIpp has appropriate comment value
        // Comment should be: "/* Multi-line works fine */\n// Single-line comment does not work\n"
        var def: ExpressionDef = defs["boolIpp"]!!
        assertThat<ExpressionDef?>(def, Matchers.notNullValue())
        assertThat<Any?>(def.annotation, Matchers.notNullValue())
        assertThat(def.annotation.size, `is`(1))
        assertThat(def.annotation[0], Matchers.instanceOf(Annotation::class.java))
        var a = def.annotation[0] as Annotation
        assertThat<Any?>(a.s!!.content, Matchers.notNullValue())
        assertThat(a.s!!.content.size, `is`(2))
        var e = a.s!!.content[0]
        assertThat(e, Matchers.notNullValue())
        assertThat(e, Matchers.instanceOf(Narrative::class.java))
        var n = e as Narrative
        assertThat(n.content, Matchers.notNullValue())
        assertThat(n.content.size, `is`(4))
        assertThat(n.content[0], Matchers.instanceOf(String::class.java))
        var s = n.content[0] as String?
        assertThat(s, `is`("/* Multi-line works fine */\n// Single-line comment does not work\n"))

        // Validate that singleLineCommentTest has appropriate comment value
        // Comment should be: "// Unmixed single-line comment works\n"
        def = defs["singleLineCommentTest"]!!
        assertThat<ExpressionDef?>(def, Matchers.notNullValue())
        assertThat<Any?>(def.annotation, Matchers.notNullValue())
        assertThat(def.annotation.size, `is`(1))
        assertThat(def.annotation[0], Matchers.instanceOf(Annotation::class.java))
        a = def.annotation[0] as Annotation
        assertThat(a.s!!.content, Matchers.notNullValue())
        assertThat(a.s!!.content.size, `is`(2))
        e = a.s!!.content[0]
        assertThat(e, Matchers.notNullValue())
        assertThat(e, Matchers.instanceOf(Narrative::class.java))
        n = e as Narrative
        assertThat(n.content, Matchers.notNullValue())
        assertThat(n.content.size, `is`(4))
        assertThat(n.content[0], Matchers.instanceOf(String::class.java))
        s = n.content[0] as String?
        assertThat(s, `is`("// Unmixed single-line comment works\n"))
    }

    @Test
    @Throws(IOException::class)
    @Suppress("LongMethod", "CyclomaticComplexMethod")
    fun tags() {
        val translator =
            TestUtils.runSemanticTest(
                "TestTags.cql",
                0,
                CqlCompilerOptions.Options.EnableAnnotations
            )
        val library = translator.translatedLibrary
        assertThat<Any?>(library!!.library!!.annotation, Matchers.notNullValue())
        var a: Annotation? = null
        for (o in library.library!!.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(3))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("author"))
                    assertThat(t.value, Matchers.equalTo("Frederic Chopin"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("description"))
                    assertThat(t.value, Matchers.equalTo("Test tags"))
                }
                2 -> {
                    assertThat(t.name, Matchers.equalTo("allowFluent"))
                    assertThat(t.value, Matchers.equalTo("true"))
                }
            }
        }

        var d = library.resolveExpressionRef("TestExpression")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(2))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("author"))
                    assertThat(t.value, Matchers.equalTo("Frederic Chopin"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("description"))
                    assertThat(t.value, Matchers.equalTo("Test tags"))
                }
            }
        }

        for (e in library.library!!.statements!!.def) {
            if (e is FunctionDef) {
                d = e
                break
            }
        }
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(2))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("author"))
                    assertThat(t.value, Matchers.equalTo("Frederic Chopin"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("description"))
                    assertThat(t.value, Matchers.equalTo("Test tags"))
                }
            }
        }

        d = library.resolveExpressionRef("TestMultiline")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(3))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("author"))
                    assertThat(t.value, Matchers.equalTo("Frederic Chopin"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("description"))
                    assertThat(
                        t.value,
                        Matchers.equalTo(
                            "This is a multi-line description that\n spans multiple lines."
                        )
                    )
                }
                2 -> {
                    assertThat(t.name, Matchers.equalTo("following"))
                    assertThat(t.value, Matchers.equalTo("true"))
                }
            }
        }

        d = library.resolveExpressionRef("TestMultiTagInline")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(2))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("test"))
                    assertThat<Any?>(t.value, Matchers.nullValue())
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("pertinence"))
                    assertThat(t.value, Matchers.equalTo("strongly-positive"))
                }
            }
        }

        d = library.resolveExpressionRef("TestDateMultiTag")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(4))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("test"))
                    assertThat(t.value, Matchers.equalTo("@1980-12-01"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("val"))
                    assertThat(t.value, Matchers.equalTo("val1"))
                }
                2 -> {
                    assertThat(t.name, Matchers.equalTo("asof"))
                    assertThat(t.value, Matchers.equalTo("@2020-10-01"))
                }
                3 -> {
                    assertThat(t.name, Matchers.equalTo("parameter"))
                    assertThat(t.value, Matchers.equalTo("abcd"))
                }
            }
        }

        d = library.resolveExpressionRef("TestDateIntervalParameter")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(4))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("test"))
                    assertThat(t.value, Matchers.equalTo("@1980-12-01"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("val"))
                    assertThat(t.value, Matchers.equalTo("val1"))
                }
                2 -> {
                    assertThat(t.name, Matchers.equalTo("asof"))
                    assertThat(t.value, Matchers.equalTo("@2020-10-01"))
                }
                3 -> {
                    assertThat(t.name, Matchers.equalTo("parameter"))
                    assertThat(t.value, Matchers.equalTo("\"Measurement Interval\" [@2019,@2020]"))
                }
            }
        }

        d = library.resolveExpressionRef("TestMultilineValue")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(1))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("test"))
                    assertThat(t.value, Matchers.equalTo("this is a\n" + "multi-line tag value"))
                }
            }
        }

        d = library.resolveExpressionRef("TestParameterAtFirstLine")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(3))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("parameter"))
                    assertThat(t.value, Matchers.equalTo("\"abcd\" [1,10]"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("test"))
                    assertThat(t.value, Matchers.equalTo("this is a\n" + "multi-line tag value"))
                }
                2 -> {
                    assertThat(t.name, Matchers.equalTo("pertinence"))
                    assertThat(t.value, Matchers.equalTo("weakly-negative"))
                }
            }
        }

        d = library.resolveExpressionRef("AnotherTestCase")
        assertThat<Any?>(d!!.annotation, Matchers.notNullValue())
        for (o in d.annotation) {
            if (o is Annotation) {
                a = o
            }
        }
        assertThat<Annotation?>(a, Matchers.notNullValue())
        assertThat<Any?>(a!!.t, Matchers.notNullValue())
        assertThat(a.t.size, Matchers.equalTo(2))
        for (i in 0 ..< a.t.size) {
            val t = a.t[i]
            when (i) {
                0 -> {
                    assertThat(t.name, Matchers.equalTo("tagname"))
                    assertThat(t.value, Matchers.equalTo("tag value"))
                }
                1 -> {
                    assertThat(t.name, Matchers.equalTo("tagname2"))
                    assertThat(
                        t.value,
                        Matchers.equalTo("tag value2 this is\n" + "a long tag value")
                    )
                }
            }
        }

        val dInvalid = library.resolveExpressionRef("TestInvalid")
        assertThat<Any?>(dInvalid!!.annotation, Matchers.notNullValue())
        var aInvalid: Annotation? = null
        for (o in dInvalid.annotation) {
            if (o is Annotation) {
                aInvalid = o
            }
        }

        // Narrative still applies in the event of an invalid, it'll just
        // be a comment instead of a tag
        assertThat<Annotation?>(aInvalid, Matchers.notNullValue())
        assertThat<Narrative?>(aInvalid!!.s, Matchers.notNullValue())
        assertThat(aInvalid.t.size, Matchers.equalTo(0))
    }
}
