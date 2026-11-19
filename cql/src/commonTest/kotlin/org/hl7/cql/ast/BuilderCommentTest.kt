package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** Comments and tags are kept on the declarations they belong to. */
class BuilderCommentTest {

    @Test
    fun givesEachCommentToTheDeclarationItBelongsTo() {
        val text =
            """
            // Before the header
            library L
            using FHIR // ends the using line

            /* Before A */
            define A:
              1 + /* within A */ 2
            // Before B, after a blank line

            define B: 2
            // At the end
            """
                .trimIndent()
        val library = parse(text)
        assertEquals(listOf("// Before the header"), library.comments.texts())
        assertEquals(
            listOf("// ends the using line"),
            library.definitions.single().comments.texts(),
        )
        val (a, b) = library.statements
        assertEquals(listOf("/* Before A */", "/* within A */"), a.comments.texts())
        assertEquals(listOf("// Before B, after a blank line", "// At the end"), b.comments.texts())
        for (comment in library.declarations().flatMap { it.comments }) {
            assertEquals(
                comment.text,
                text.substring(comment.locator.startIndex, comment.locator.stopIndex + 1),
            )
        }
    }

    @Test
    fun keepsTheStyleOfAComment() {
        val define = parse("// line\n/* block */\ndefine A: 1").statements.single()
        assertEquals(
            listOf(CommentStyle.LINE, CommentStyle.BLOCK),
            define.comments.map { it.style },
        )
    }

    @Test
    fun keepsTheCommentsOfALibraryThatDeclaresNothing() {
        assertEquals(listOf("// one", "/* two */"), parse("// one\n/* two */\n").comments.texts())
    }

    @Test
    fun commentsDoNotChangeTheDeclarations() {
        val plain = parse("library L\nusing FHIR\ndefine A: 1 + 2")
        val commented =
            parse("/* a */ library L // b\nusing /* c */ FHIR\n// d\ndefine A: 1 /* e */ + 2 // f")
        assertEquals(
            plain.declarations().map { it.withoutComments() },
            commented.declarations().map { it.withoutComments() },
        )
    }

    // The tag tests are the cases of cql-to-elm's TestTags.cql, with the tags CommentTests expects

    @Test
    fun readsTheTagsOfTheLibraryHeader() {
        val library =
            parse(
                """
                /*
                @author: Frederic Chopin
                @description: Test tags
                @allowFluent: true
                */
                library TestTags
                """
                    .trimIndent()
            )
        assertEquals(
            listOf(
                Tag("author", "Frederic Chopin"),
                Tag("description", "Test tags"),
                Tag("allowFluent", "true"),
            ),
            library.tags,
        )
    }

    @Test
    fun readsValuesOverSeveralLines() {
        assertEquals(
            listOf(
                Tag("author", "Frederic Chopin"),
                Tag("description", "This is a multi-line description that\n spans multiple lines."),
                Tag("following", "true"),
            ),
            tagsAfter(
                """
                /*
                @author: Frederic Chopin
                @description: This is a multi-line description that
                 spans multiple lines.
                @following: true
                 */
                """
            ),
        )
    }

    @Test
    fun readsATagWithoutAValue() {
        assertEquals(
            listOf(Tag("test"), Tag("pertinence", "strongly-positive")),
            tagsAfter("/*\n@test  @pertinence: strongly-positive\n */"),
        )
    }

    @Test
    fun readsDatesAndQuotedParameterNamesAsValues() {
        assertEquals(
            listOf(
                Tag("test", "@1980-12-01"),
                Tag("val", "val1"),
                Tag("asof", "@2020-10-01"),
                Tag("parameter", "\"Measurement Interval\" [@2019,@2020]"),
            ),
            tagsAfter(
                """
                /*
                @test: @1980-12-01 @val: val1
                @asof: @2020-10-01
                @parameter: "Measurement Interval" [@2019,@2020]
                */
                """
            ),
        )
        assertEquals(
            listOf(
                Tag("parameter", "\"abcd\" [1,10]"),
                Tag("test", "this is a\nmulti-line tag value"),
                Tag("pertinence", "weakly-negative"),
            ),
            tagsAfter(
                """
                /*
                @parameter: "abcd" [1,10]
                @test: this is a
                multi-line tag value
                @pertinence : weakly-negative
                */
                """
            ),
        )
    }

    @Test
    fun readsTagsFromADocumentationComment() {
        assertEquals(
            listOf(
                Tag("tagname", "tag value"),
                Tag("tagname2", "tag value2 this is\na long tag value"),
            ),
            tagsAfter(
                """
                /**
                 *  @tagname: tag value
                 *  @tagname2: tag value2 this is
                 *  a long tag value
                 */
                """
            ),
        )
    }

    @Test
    fun ignoresATagWithoutAName() {
        assertEquals(emptyList(), tagsAfter("/*@:@*/"))
    }

    @Test
    fun readsTheEdgesOfATag() {
        // A tag at the very end, with nothing after its colon
        assertEquals(listOf(Tag("a")), TagParser.parse("// @a:"))
        // A quoted value with no closing quote runs to the end
        assertEquals(listOf(Tag("p", "\"abc def")), TagParser.parse("// @p: \"abc def"))
        // Text before a date is the value, and the date is not a tag
        assertEquals(listOf(Tag("a", "x")), TagParser.parse("// @a: x @2020-01-01"))
        // A date value ends at the next space
        assertEquals(
            listOf(Tag("a", "@2020-01-01"), Tag("b", "c")),
            TagParser.parse("// @a: @2020-01-01 @b: c"),
        )
        // or at the end of its line
        assertEquals(
            listOf(Tag("a", "@2020-01-01"), Tag("b", "c")),
            TagParser.parse("// @a: @2020-01-01\n// @b: c"),
        )
    }

    @Test
    fun readsTagsFromEveryCommentBetweenDeclarations() {
        // As the translator does, a comment ending the previous declaration's line is included
        val define = parse("define A: 1 // @first\n// @second: 2\ndefine B: 2").statements.last()
        assertEquals(listOf(Tag("first"), Tag("second", "2")), define.tags)
    }

    /** The tags of a definition after [comment]. */
    private fun tagsAfter(comment: String): List<Tag> =
        parse(comment.trimIndent() + "\ndefine X: 1").statements.single().tags

    private fun parse(text: String): Library {
        val result = Builder().parseLibrary(text)
        assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })
        return result.library
    }

    private fun List<Comment>.texts(): List<String> = map { it.text }

    private fun Library.declarations(): List<Declaration> = definitions + statements

    /** The declaration's structure, without positions, comments or tags. */
    private fun Declaration.withoutComments(): JsonElement {
        val json =
            when (this) {
                is Definition -> Json.encodeToJsonElement(Definition.serializer(), this)
                is Statement -> Json.encodeToJsonElement(Statement.serializer(), this)
                else -> error("Unexpected declaration $this")
            }
        return JsonObject(
            (json.withoutSourceDetails() as JsonObject).filterKeys { it != "comments" }
        )
    }
}
