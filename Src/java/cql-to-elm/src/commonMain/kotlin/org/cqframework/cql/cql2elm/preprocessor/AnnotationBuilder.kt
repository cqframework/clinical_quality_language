package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.model.Chunk
import org.cqframework.cql.cql2elm.utils.Stack
import org.cqframework.cql.gen.cqlParser.LibraryContext
import org.hl7.cql_annotations.r1.Annotation
import org.hl7.cql_annotations.r1.Narrative
import org.hl7.cql_annotations.r1.ObjectFactory
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef

/**
 * Owns the chunk-stack, narrative, and annotation building logic shared between [CqlPreprocessor]
 * and [org.cqframework.cql.cql2elm.Cql2ElmVisitor].
 *
 * Extracted from `CqlPreprocessorElmCommonVisitor` so changes to annotation handling for the
 * preprocessor no longer risk breaking the visitor through a shared inheritance chain.
 */
@Suppress(
    "TooManyFunctions",
    "CyclomaticComplexMethod",
    "NestedBlockDepth",
    "ReturnCount",
    "ComplexCondition",
)
class AnnotationBuilder(
    private val libraryBuilder: LibraryBuilder,
    private val tokenStream: TokenStream,
) {
    private val af = ObjectFactory()
    private val tagParser = TagParser()
    /**
     * The active chunk stack. Visitor callers may save/restore this when entering forward-
     * reference / function-header processing to avoid polluting the outer chunk tree.
     */
    var chunks: Stack<Chunk> = Stack()
    var libraryInfo: LibraryInfo = LibraryInfo()
    var enabled: Boolean = false

    fun pushChunk(tree: ParseTree): Boolean {
        if (!enabled) return false
        val sourceInterval = tree.sourceInterval
        if (sourceInterval.b < sourceInterval.a) return false
        chunks.push(Chunk(sourceInterval))
        return true
    }

    @Suppress("LongMethod", "NestedBlockDepth", "ComplexCondition")
    fun popChunk(tree: ParseTree, o: Any?, pushedChunk: Boolean) {
        if (!pushedChunk) return
        var chunk = chunks.pop()
        if (o is Element) {
            chunk.element = o
            if (tree !is LibraryContext) {
                if (isAnnotatable(o)) {
                    val a = getAnnotation(o)
                    if (a?.s == null) {
                        val definitionInfo = libraryInfo.resolveDefinition(tree)
                        if (definitionInfo?.headerInterval != null) {
                            val headerChunk = Chunk(definitionInfo.headerInterval!!, true)
                            val newChunk = Chunk(Interval(headerChunk.interval.a, chunk.interval.b))
                            newChunk.addChunk(headerChunk)
                            newChunk.element = chunk.element
                            for (c in chunk.getChunks()) {
                                newChunk.addChunk(c)
                            }
                            chunk = newChunk
                        }
                        a?.let { addNarrativeToAnnotation(it, chunk) }
                            ?: o.annotation.add(buildAnnotation(chunk))
                    }
                }
            } else {
                if (libraryInfo.definition != null && libraryInfo.headerInterval != null) {
                    val headerChunk = Chunk(libraryInfo.headerInterval!!, true)
                    val definitionChunk = Chunk(libraryInfo.definition?.sourceInterval!!)
                    val newChunk =
                        Chunk(Interval(headerChunk.interval.a, definitionChunk.interval.b))
                    newChunk.addChunk(headerChunk)
                    newChunk.addChunk(definitionChunk)
                    newChunk.element = chunk.element
                    chunk = newChunk
                    val a = getAnnotation(libraryBuilder.library)
                    a?.let { addNarrativeToAnnotation(it, chunk) }
                        ?: libraryBuilder.library.annotation.add(buildAnnotation(chunk))
                }
            }
        }
        if (chunks.isNotEmpty()) {
            chunks.peek().addChunk(chunk)
        }
    }

    fun processTags(tree: ParseTree, o: Any?) {
        if (!libraryBuilder.isCompatibleWith("1.5")) return
        if (o !is Element) return
        if (tree !is LibraryContext) {
            if (isAnnotatable(o)) {
                val tags = getTagsForTree(tree)
                if (tags.isNotEmpty()) {
                    var a = getAnnotation(o)
                    if (a == null) {
                        a = buildEmptyAnnotation()
                        o.annotation.add(a)
                    }
                    // If the definition was processed as a forward declaration the tag processing
                    // already happened — adding tags would duplicate them. Skip if any tags exist.
                    if (a.t.isEmpty()) {
                        a.t.addAll(tags)
                    }
                }
            }
        } else {
            if (libraryInfo.definition != null && libraryInfo.headerInterval != null) {
                val tags = tagParser.parseTags(tagParser.parseComments(libraryInfo.header))
                if (tags.isNotEmpty()) {
                    var a = getAnnotation(libraryBuilder.library)
                    if (a == null) {
                        a = buildEmptyAnnotation()
                        libraryBuilder.library.annotation.add(a)
                    }
                    a.t.addAll(tags)
                }
            }
        }
    }

    private fun isAnnotatable(o: Any?): Boolean =
        o is UsingDef ||
            o is IncludeDef ||
            o is CodeSystemDef ||
            o is ValueSetDef ||
            o is CodeDef ||
            o is ConceptDef ||
            o is ParameterDef ||
            o is ContextDef ||
            o is ExpressionDef

    private fun getTagsForTree(tree: ParseTree): List<org.hl7.cql_annotations.r1.Tag> {
        val bi = libraryInfo.resolveDefinition(tree) ?: return emptyList()
        return tagParser.parseTags(tagParser.parseComments(bi.header))
    }

    private fun buildAnnotation(chunk: Chunk): Annotation {
        val annotation = af.createAnnotation()
        annotation.s = buildNarrative(chunk)
        return annotation
    }

    private fun buildEmptyAnnotation(): Annotation = af.createAnnotation()

    private fun addNarrativeToAnnotation(annotation: Annotation, chunk: Chunk) {
        annotation.s = buildNarrative(chunk)
    }

    @Suppress("NestedBlockDepth")
    private fun buildNarrative(chunk: Chunk): Narrative {
        val narrative = af.createNarrative()
        if (chunk.element != null) {
            narrative.r = chunk.element!!.localId
        }
        if (chunk.hasChunks()) {
            var currentNarrative: Narrative? = null
            for (childChunk in chunk.getChunks()) {
                val chunkNarrative = buildNarrative(childChunk)
                if (hasChunks(chunkNarrative)) {
                    if (currentNarrative != null) {
                        narrative.content.add(wrapNarrative(currentNarrative))
                        currentNarrative = null
                    }
                    narrative.content.add(wrapNarrative(chunkNarrative))
                } else {
                    if (currentNarrative == null) {
                        currentNarrative = chunkNarrative
                    } else {
                        currentNarrative.content.addAll(chunkNarrative.content)
                        if (currentNarrative.r == null) {
                            currentNarrative.r = chunkNarrative.r
                        }
                    }
                }
            }
            if (currentNarrative != null) {
                narrative.content.add(wrapNarrative(currentNarrative))
            }
        } else {
            var chunkContent = tokenStream.getText(chunk.interval)
            if (chunk.isHeaderChunk) {
                chunkContent = chunkContent.trimStart()
            }
            chunkContent = chunkContent.replace("\r\n", "\n")
            narrative.content.add(chunkContent)
        }
        return narrative
    }

    private fun hasChunks(narrative: Narrative): Boolean {
        for (c in narrative.content) {
            if (c !is String) return true
        }
        return false
    }

    private fun getAnnotation(element: Element): Annotation? {
        for (o in element.annotation) {
            if (o is Annotation) return o
        }
        return null
    }

    companion object {
        @Suppress("ForbiddenComment") fun wrapNarrative(narrative: Narrative): Any = narrative
    }
}
