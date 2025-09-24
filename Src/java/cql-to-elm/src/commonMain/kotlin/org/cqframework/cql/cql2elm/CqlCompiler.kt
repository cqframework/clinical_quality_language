package org.cqframework.cql.cql2elm

import kotlinx.io.IOException
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import org.antlr.v4.kotlinruntime.*
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.cql2elm.elm.ElmEdit
import org.cqframework.cql.cql2elm.elm.ElmEditor
import org.cqframework.cql.cql2elm.elm.IElmEdit
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessor
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.cqframework.cql.gen.cqlParser.LibraryContext
import org.hl7.cql.model.*
import org.hl7.elm.r1.*

class CqlCompiler(
    private val namespaceInfo: NamespaceInfo?,
    sourceInfo: VersionedIdentifier?,
    private val libraryManager: LibraryManager
) {
    var library: Library? = null
        private set

    var compiledLibrary: CompiledLibrary? = null
        private set

    private var visitResult: Any? = null
    private var retrieves: kotlin.collections.List<Retrieve>? = null
    val exceptions = ArrayList<CqlCompilerException>()
    val errors: kotlin.collections.List<CqlCompilerException>
        get() = exceptions.filter { it.severity == CqlCompilerException.ErrorSeverity.Error }

    val warnings: kotlin.collections.List<CqlCompilerException>
        get() = exceptions.filter { it.severity == CqlCompilerException.ErrorSeverity.Warning }

    val messages: kotlin.collections.List<CqlCompilerException>
        get() = exceptions.filter { it.severity == CqlCompilerException.ErrorSeverity.Info }

    private var sourceInfo =
        sourceInfo ?: VersionedIdentifier().withId("Anonymous").withSystem("text/cql")

    constructor(libraryManager: LibraryManager) : this(null, null, libraryManager)

    constructor(
        namespaceInfo: NamespaceInfo?,
        libraryManager: LibraryManager
    ) : this(namespaceInfo, null, libraryManager)

    init {
        if (namespaceInfo != null) {
            libraryManager.namespaceManager.ensureNamespaceRegistered(namespaceInfo)
        }
        if (
            libraryManager.namespaceManager.hasNamespaces() &&
                libraryManager.librarySourceLoader is NamespaceAware
        ) {
            libraryManager.librarySourceLoader.setNamespaceManager(libraryManager.namespaceManager)
        }
    }

    fun toObject(): Any? {
        return visitResult
    }

    fun toRetrieves(): kotlin.collections.List<Retrieve>? {
        return retrieves
    }

    val compiledLibraries: Map<VersionedIdentifier, CompiledLibrary>
        get() = libraryManager.compiledLibraries

    val libraries: Map<VersionedIdentifier, Library>
        get() {
            val result = HashMap<VersionedIdentifier, Library>()
            libraryManager.compiledLibraries.forEach { (id, compiledLibrary) ->
                result[id] = compiledLibrary.library!!
            }
            return result
        }

    private inner class CqlErrorListener(
        private val builder: LibraryBuilder,
        private val detailedErrors: Boolean
    ) : BaseErrorListener() {
        private fun extractLibraryIdentifier(parser: cqlParser): VersionedIdentifier? {
            var context: RuleContext? = parser.context
            while (context != null && context !is LibraryContext) {
                context = context.getParent()
            }
            if (context is LibraryContext) {
                val ldc = context.libraryDefinition()
                if (ldc?.qualifiedIdentifier() != null) {
                    var identifierText = ldc.qualifiedIdentifier().identifier().text
                    // The identifier may or may not be surrounded by double quotes. If it is,
                    // "unescape" and strip the delimiting double quotes.
                    if (identifierText.startsWith("\"")) {
                        val unescaped = StringEscapeUtils.unescapeCql(identifierText)
                        require(unescaped.startsWith("\"")) {
                            "Expected string to start with double quotes"
                        }
                        require(unescaped.endsWith("\"")) {
                            "Expected string to end with double quotes"
                        }
                        identifierText = unescaped.substring(1, unescaped.length - 1)
                    }
                    val vi = VersionedIdentifier().withId(identifierText)
                    if (ldc.versionSpecifier() != null) {
                        var version = StringEscapeUtils.unescapeCql(ldc.versionSpecifier()!!.text)
                        version = version.substring(1, version.length - 1)
                        vi.version = version
                    }

                    return vi
                }
            }
            return null
        }

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?
        ) {
            var libraryIdentifier = builder.libraryIdentifier
            if (libraryIdentifier == null) {
                // Attempt to extract a libraryIdentifier from the currently parsed content
                if (recognizer is cqlParser) {
                    libraryIdentifier = extractLibraryIdentifier(recognizer)
                }
                if (libraryIdentifier == null) {
                    libraryIdentifier = sourceInfo
                }
            }
            val trackback =
                TrackBack(libraryIdentifier, line, charPositionInLine, line, charPositionInLine)
            if (detailedErrors) {
                builder.recordParsingException(CqlSyntaxException(msg, trackback, e))
            } else {
                if (offendingSymbol is CommonToken) {
                    builder.recordParsingException(
                        CqlSyntaxException("Syntax error at ${offendingSymbol.text}", trackback, e)
                    )
                } else {
                    builder.recordParsingException(CqlSyntaxException("Syntax error", trackback, e))
                }
            }
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

    fun run(charStream: CharStream): Library {
        val options = libraryManager.cqlCompilerOptions.options
        val builder = LibraryBuilder(namespaceInfo, libraryManager, IdObjectFactory())
        val errorListener =
            CqlErrorListener(
                builder,
                options.contains(CqlCompilerOptions.Options.EnableDetailedErrors)
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
        visitResult = visitor.visit(tree)
        library = builder.library

        // Phase 5: ELM optimization/reduction (this is where result types, annotations, etc. are
        // removed and there will probably be a lot of other optimizations that happen here in the
        // future)
        val edits =
            allNonNull(
                (if (!options.contains(CqlCompilerOptions.Options.EnableAnnotations))
                    ElmEdit.REMOVE_ANNOTATION
                else null),
                (if (!options.contains(CqlCompilerOptions.Options.EnableResultTypes))
                    ElmEdit.REMOVE_RESULT_TYPE
                else null),
                (if (!options.contains(CqlCompilerOptions.Options.EnableLocators))
                    ElmEdit.REMOVE_LOCATOR
                else null)
            )
        ElmEditor(edits).edit(library!!)
        compiledLibrary = builder.compiledLibrary
        retrieves = visitor.retrieves
        exceptions.addAll(builder.exceptions)
        return library!!
    }

    private fun allNonNull(vararg ts: IElmEdit?): kotlin.collections.List<IElmEdit> {
        return ts.filterNotNull()
    }
}
