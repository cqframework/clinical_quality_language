package org.cqframework.cql.cql2elm

import kotlinx.io.IOException
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import org.antlr.v4.kotlinruntime.*
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.cql2elm.StringEscapeUtils.unescapeCql
import org.cqframework.cql.cql2elm.elm.ElmEdit
import org.cqframework.cql.cql2elm.elm.ElmEditor
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessor
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.cqframework.cql.gen.cqlParser.LibraryContext
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Compiles CQL source into the corresponding ELM tree representation. Records and exposes
 * compile-time exceptions and the resulting library.
 */
class CqlCompiler(
    private val namespaceInfo: NamespaceInfo? = null,
    private val sourceInfo: VersionedIdentifier? = null,
    val libraryManager: LibraryManager,
) {

    var library: Library? = null
        private set

    var root: Any? = null
        private set

    var compiledLibrary: CompiledLibrary? = null
        private set

    val exceptions = mutableListOf<CqlCompilerException>()

    private inner class CqlErrorListener(
        private val builder: LibraryBuilder,
        private val detailedErrors: Boolean,
    ) : BaseErrorListener() {
        private fun extractLibraryIdentifier(parser: cqlParser): VersionedIdentifier? {
            var context: RuleContext? = parser.context
            while (context != null && context !is LibraryContext) {
                context = context.getParent()
            }

            val ldc = context?.libraryDefinition() ?: return null
            var identifierText = ldc.qualifiedIdentifier().identifier().text
            // The identifier may or may not be surrounded by double quotes. If it is,
            // "unescape" and strip the delimiting double quotes.
            if (identifierText.startsWith("\"")) {
                val unescaped = unescapeCql(identifierText)
                require(unescaped.startsWith("\"")) {
                    "Expected string to start with double quotes"
                }
                require(unescaped.endsWith("\"")) { "Expected string to end with double quotes" }
                identifierText = unescaped.substring(1, unescaped.length - 1)
            }
            var versionText: String? = null
            if (ldc.versionSpecifier() != null) {
                versionText = unescapeCql(ldc.versionSpecifier()!!.text)
                versionText = versionText.substring(1, versionText.length - 1)
            }

            return VersionedIdentifier().apply {
                id = identifierText
                version = versionText
            }
        }

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?,
        ) {

            val libraryIdentifier =
                builder.libraryIdentifier
                    ?: (recognizer as? cqlParser)?.let { extractLibraryIdentifier(it) }
                    ?: sourceInfo
                    ?: VersionedIdentifier().apply {
                        id = "Anonymous"
                        system = "text/cql"
                    }

            val trackback =
                TrackBack(libraryIdentifier, line, charPositionInLine, line, charPositionInLine)

            val syntaxError =
                when {
                    detailedErrors -> CqlSyntaxException(msg, trackback, e)
                    offendingSymbol is CommonToken ->
                        CqlSyntaxException("Syntax error at ${offendingSymbol.text}", trackback, e)
                    else -> CqlSyntaxException("Syntax error", trackback, e)
                }

            builder.recordParsingException(syntaxError)
        }
    }

    @Throws(IOException::class)
    fun run(file: Path): Library {
        return SystemFileSystem.source(file).buffered().use { run(it) }
    }

    fun run(cqlText: String): Library {
        return run(CharStreams.fromString(cqlText))
    }

    @Throws(IOException::class)
    fun run(source: Source): Library {
        return run(source.readString())
    }

    /** Compiles CQL source from the provided [CharStream] into an ELM [Library]. */
    fun run(charStream: CharStream): Library {
        val options = libraryManager.cqlCompilerOptions.options
        val builder = LibraryBuilder(namespaceInfo, libraryManager, IdObjectFactory())
        val errorListener =
            CqlErrorListener(
                builder,
                options.contains(CqlCompilerOptions.Options.EnableDetailedErrors),
            )

        // Phase 1: Lexing
        val lexer = cqlLexer(charStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)
        val tokens = CommonTokenStream(lexer)

        // Phase 2: Parsing (the lexer is actually streaming, so Phase 1 and 2 happen together)
        val parser = cqlParser(tokens)
        parser.buildParseTree = true
        parser.removeErrorListeners() // Clear the default console listener
        parser.addErrorListener(errorListener)
        val tree: ParseTree = parser.library()

        // Phase 3: preprocess the parse tree (generates the LibraryInfo with
        // header information for definitions)
        val preprocessor = CqlPreprocessor(builder, tokens)
        preprocessor.visit(tree)

        // Phase 4: generate the ELM (the ELM is generated with full type information that can be
        // used for validation, optimization, rewriting, debugging, etc.)
        val visitor = Cql2ElmVisitor(builder, tokens, preprocessor.libraryInfo)
        root = visitor.visit(tree)
        library = builder.library

        // Phase 5: ELM optimization/reduction (this is where result types, annotations, etc. are
        // removed and there will probably be a lot of other optimizations that happen here in the
        // future)
        val optionToEdit =
            mapOf(
                CqlCompilerOptions.Options.EnableAnnotations to ElmEdit.REMOVE_ANNOTATION,
                CqlCompilerOptions.Options.EnableResultTypes to ElmEdit.REMOVE_RESULT_TYPE,
                CqlCompilerOptions.Options.EnableLocators to ElmEdit.REMOVE_LOCATOR,
            )

        // The edits are applied if the corresponding option is NOT present
        val edits = optionToEdit.filterKeys { it !in options }.values.toList()

        ElmEditor(edits).edit(library!!)
        compiledLibrary = builder.compiledLibrary
        exceptions.addAll(builder.exceptions)
        return library!!
    }
}
