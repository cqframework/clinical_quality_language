@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm.preprocessor

import jakarta.xml.bind.JAXBElement
import java.io.Serializable
import java.util.*
import javax.xml.namespace.QName
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.apache.commons.lang3.tuple.Pair
import org.cqframework.cql.cql2elm.*
import org.cqframework.cql.cql2elm.model.Chunk
import org.cqframework.cql.cql2elm.model.FunctionHeader
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.elm.tracking.TrackBack
import org.cqframework.cql.elm.tracking.Trackable
import org.cqframework.cql.gen.cqlBaseVisitor
import org.cqframework.cql.gen.cqlParser
import org.hl7.cql.model.*
import org.hl7.cql_annotations.r1.Annotation
import org.hl7.cql_annotations.r1.Narrative
import org.hl7.cql_annotations.r1.ObjectFactory
import org.hl7.cql_annotations.r1.Tag
import org.hl7.elm.r1.*

/** Common functionality used by [CqlPreprocessor] and [Cql2ElmVisitor] */
@Suppress(
    "LargeClass",
    "TooManyFunctions",
    "NestedBlockDepth",
    "ReturnCount",
    "CyclomaticComplexMethod",
    "ComplexCondition"
)
open class CqlPreprocessorElmCommonVisitor(
    libraryBuilder: LibraryBuilder,
    tokenStream: TokenStream
) : cqlBaseVisitor<Any?>() {
    @JvmField protected val of: IdObjectFactory
    protected val af = ObjectFactory()
    protected var implicitContextCreated = false
    @JvmField protected var currentContext = "Unfiltered"
    @JvmField protected var chunks = Stack<Chunk>()
    @JvmField protected val libraryBuilder: LibraryBuilder
    protected val tokenStream: TokenStream
    var libraryInfo = LibraryInfo()
        protected set

    var isAnnotationEnabled = false
        private set

    var isDetailedErrorsEnabled = false
        private set

    private var locate = false
    private var resultTypes = false
    var dateRangeOptimization = false
        private set

    var isMethodInvocationEnabled = true
        private set

    var isFromKeywordRequired = false
        private set

    private val expressions: MutableList<Expression> = ArrayList()
    @JvmField var includeDeprecatedElements = false

    init {
        this.libraryBuilder = Objects.requireNonNull(libraryBuilder, "libraryBuilder required")
        this.tokenStream = Objects.requireNonNull(tokenStream, "tokenStream required")
        of =
            Objects.requireNonNull(
                libraryBuilder.objectFactory,
                "libraryBuilder.objectFactory required"
            )

        // Don't talk to strangers. Except when you have to.
        setCompilerOptions(libraryBuilder.libraryManager.cqlCompilerOptions)
    }

    protected fun saveCurrentContext(currentContext: String): String {
        val saveContext = this.currentContext
        this.currentContext = currentContext
        return saveContext
    }

    override fun defaultResult(): Any? {
        return null
    }

    @Suppress("LongMethod")
    override fun visit(tree: ParseTree): Any? {
        Objects.requireNonNull(tree, "ParseTree required")
        val pushedChunk = pushChunk(tree)
        var o: Any? = null
        return try {
            // ERROR:
            try {
                o = super.visit(tree)
                if (o is Element) {
                    val element = o
                    if (element.localId == null) {
                        throw CqlInternalException(
                            @Suppress("ImplicitDefaultLocale")
                            String.format(
                                "Internal translator error. 'localId' was not assigned for Element \"%s\"",
                                element.javaClass.name
                            ),
                            getTrackBack(tree)
                        )
                    }
                }
            } catch (e: CqlIncludeException) {
                val translatorException = CqlCompilerException(e.message, getTrackBack(tree), e)
                if (translatorException.locator == null) {
                    throw translatorException
                }
                libraryBuilder.recordParsingException(translatorException)
            } catch (e: CqlCompilerException) {
                libraryBuilder.recordParsingException(e)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                var ex: CqlCompilerException? = null
                ex =
                    if (e.message == null) {
                        CqlInternalException("Internal translator error.", getTrackBack(tree), e)
                    } else {
                        CqlSemanticException(e.message, getTrackBack(tree), e)
                    }
                var rootCause = libraryBuilder.determineRootCause()
                if (rootCause == null) {
                    rootCause = ex
                    libraryBuilder.recordParsingException(ex)
                    libraryBuilder.setRootCause(rootCause)
                } else {
                    if (isDetailedErrorsEnabled) {
                        libraryBuilder.recordParsingException(ex)
                    }
                }
                o = of.createNull()
            }
            if (o is Trackable && tree !is cqlParser.LibraryContext) {
                track(o, tree)
            }
            if (o is Expression) {
                addExpression(o)
            }
            o
        } finally {
            popChunk(tree, o, pushedChunk)
            processTags(tree, o)
        }
    }

    override fun visitTupleElementDefinition(
        ctx: cqlParser.TupleElementDefinitionContext
    ): TupleElementDefinition {
        val result =
            of.createTupleElementDefinition()
                .withName(parseString(ctx.referentialIdentifier()))
                .withElementType(parseTypeSpecifier(ctx.typeSpecifier()))
        if (includeDeprecatedElements) {
            result.type = result.elementType
        }
        return result
    }

    override fun visitTupleTypeSpecifier(ctx: cqlParser.TupleTypeSpecifierContext): Any {
        val resultType = TupleType()
        val typeSpecifier = of.createTupleTypeSpecifier()
        for (definitionContext in ctx.tupleElementDefinition()) {
            val element = visit(definitionContext) as TupleElementDefinition
            resultType.addElement(TupleTypeElement(element.name, element.elementType.resultType))
            typeSpecifier.element.add(element)
        }
        typeSpecifier.resultType = resultType
        return typeSpecifier
    }

    override fun visitChoiceTypeSpecifier(
        ctx: cqlParser.ChoiceTypeSpecifierContext
    ): ChoiceTypeSpecifier {
        val typeSpecifiers = ArrayList<TypeSpecifier?>()
        val types = ArrayList<DataType>()
        for (typeSpecifierContext in ctx.typeSpecifier()) {
            val typeSpecifier = parseTypeSpecifier(typeSpecifierContext)
            typeSpecifiers.add(typeSpecifier)
            types.add(typeSpecifier!!.resultType)
        }
        val result = of.createChoiceTypeSpecifier().withChoice(typeSpecifiers)
        if (includeDeprecatedElements) {
            result.type.addAll(typeSpecifiers)
        }
        val choiceType = ChoiceType(types)
        result.resultType = choiceType
        return result
    }

    override fun visitIntervalTypeSpecifier(
        ctx: cqlParser.IntervalTypeSpecifierContext
    ): IntervalTypeSpecifier {
        val result =
            of.createIntervalTypeSpecifier().withPointType(parseTypeSpecifier(ctx.typeSpecifier()))
        val intervalType = IntervalType(result.pointType.resultType)
        result.resultType = intervalType
        return result
    }

    override fun visitListTypeSpecifier(
        ctx: cqlParser.ListTypeSpecifierContext
    ): ListTypeSpecifier {
        val result =
            of.createListTypeSpecifier().withElementType(parseTypeSpecifier(ctx.typeSpecifier()))
        val listType = ListType(result.elementType.resultType)
        result.resultType = listType
        return result
    }

    fun parseFunctionHeader(ctx: cqlParser.FunctionDefinitionContext): FunctionHeader {
        val functionDef =
            of.createFunctionDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifierOrFunctionIdentifier()))
                .withContext(currentContext)
        if (ctx.fluentModifier() != null) {
            libraryBuilder.checkCompatibilityLevel("Fluent functions", "1.5")
            functionDef.isFluent = true
        }
        if (ctx.operandDefinition() != null) {
            for (opdef in ctx.operandDefinition()) {
                val typeSpecifier = parseTypeSpecifier(opdef.typeSpecifier())
                functionDef.operand.add(
                    of.createOperandDef()
                        .withName(parseString(opdef.referentialIdentifier()))
                        .withOperandTypeSpecifier(typeSpecifier)
                        .withResultType(typeSpecifier!!.resultType) as OperandDef
                )
            }
        }
        val typeSpecifierContext = ctx.typeSpecifier()
        return if (typeSpecifierContext != null) {
            FunctionHeader.withReturnType(functionDef, parseTypeSpecifier(typeSpecifierContext))
        } else FunctionHeader.noReturnType(functionDef)
    }

    protected fun parseTypeSpecifier(pt: ParseTree?): TypeSpecifier? {
        return if (pt == null) null else visit(pt) as TypeSpecifier
    }

    protected fun parseAccessModifier(pt: ParseTree?): AccessModifier {
        return if (pt == null) AccessModifier.PUBLIC else (visit(pt) as AccessModifier)
    }

    protected fun parseQualifiers(ctx: cqlParser.NamedTypeSpecifierContext): List<String?> {
        val qualifiers: MutableList<String?> = ArrayList()
        if (ctx.qualifier() != null) {
            for (qualifierContext in ctx.qualifier()) {
                val qualifier = parseString(qualifierContext)
                qualifiers.add(qualifier)
            }
        }
        return qualifiers
    }

    protected open fun getModel(
        modelNamespace: NamespaceInfo?,
        modelName: String?,
        version: String?,
        localIdentifier: String?
    ): Model? {
        var modelName = modelName
        var version = version
        if (modelName == null) {
            val defaultUsing = libraryInfo.defaultUsingDefinition
            modelName = defaultUsing?.name
            version = defaultUsing?.version
        }
        val modelIdentifier = ModelIdentifier().withId(modelName).withVersion(version)
        if (modelNamespace != null) {
            modelIdentifier.system = modelNamespace.uri
        }
        return libraryBuilder.getModel(modelIdentifier, localIdentifier)
    }

    private fun pushChunk(tree: ParseTree): Boolean {
        if (!isAnnotationEnabled) {
            return false
        }
        val sourceInterval = tree.sourceInterval

        // An interval of i..i-1 indicates an empty interval at position i in the input stream,
        if (sourceInterval.b < sourceInterval.a) {
            return false
        }
        val chunk = Chunk().withInterval(sourceInterval)
        chunks.push(chunk)
        return true
    }

    @Suppress("LongMethod")
    private fun popChunk(tree: ParseTree, o: Any?, pushedChunk: Boolean) {
        if (!pushedChunk) {
            return
        }
        var chunk = chunks.pop()
        if (o is Element) {
            val element = o
            chunk.element = element
            if (tree !is cqlParser.LibraryContext) {
                if (
                    element is UsingDef ||
                        element is IncludeDef ||
                        element is CodeSystemDef ||
                        element is ValueSetDef ||
                        element is CodeDef ||
                        element is ConceptDef ||
                        element is ParameterDef ||
                        element is ContextDef ||
                        element is ExpressionDef
                ) {
                    val a = getAnnotation(element)
                    if (a == null || a.s == null) {
                        // Add header information (comments prior to the definition)
                        val definitionInfo = libraryInfo.resolveDefinition(tree)
                        if (definitionInfo != null && definitionInfo.headerInterval != null) {
                            val headerChunk =
                                Chunk()
                                    .withInterval(definitionInfo.headerInterval)
                                    .withIsHeaderChunk(true)
                            val newChunk =
                                Chunk()
                                    .withInterval(
                                        Interval(headerChunk.interval.a, chunk.interval.b)
                                    )
                            newChunk.addChunk(headerChunk)
                            newChunk.element = chunk.element
                            for (c in chunk.chunks) {
                                newChunk.addChunk(c)
                            }
                            chunk = newChunk
                        }
                        a?.let { addNarrativeToAnnotation(it, chunk) }
                            ?: element.annotation.add(buildAnnotation(chunk))
                    }
                }
            } else {
                if (libraryInfo.definition != null && libraryInfo.headerInterval != null) {
                    val headerChunk =
                        Chunk().withInterval(libraryInfo.headerInterval).withIsHeaderChunk(true)
                    val definitionChunk =
                        Chunk().withInterval(libraryInfo.definition!!.sourceInterval)
                    val newChunk =
                        Chunk()
                            .withInterval(
                                Interval(headerChunk.interval.a, definitionChunk.interval.b)
                            )
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
        if (!chunks.isEmpty()) {
            chunks.peek().addChunk(chunk)
        }
    }

    private fun processTags(tree: ParseTree, o: Any?) {
        if (!libraryBuilder.isCompatibleWith("1.5")) {
            return
        }
        if (o !is Element) {
            return
        }
        val element = o
        if (tree !is cqlParser.LibraryContext) {
            if (
                element is UsingDef ||
                    element is IncludeDef ||
                    element is CodeSystemDef ||
                    element is ValueSetDef ||
                    element is CodeDef ||
                    element is ConceptDef ||
                    element is ParameterDef ||
                    element is ContextDef ||
                    element is ExpressionDef
            ) {
                val tags = getTags(tree)
                if (!tags.isEmpty()) {
                    var a = getAnnotation(element)
                    if (a == null) {
                        a = buildAnnotation()
                        element.annotation.add(a)
                    }
                    // If the definition was processed as a forward declaration, the tag processing
                    // will already
                    // have occurred
                    // and just adding tags would duplicate them here. This doesn't account for the
                    // possibility
                    // that
                    // tags would be added for some other reason, but I didn't want the overhead of
                    // checking for
                    // existing
                    // tags, and there is currently nothing that would add tags other than being
                    // processed from
                    // comments
                    if (a.t.isEmpty()) {
                        a.t.addAll(tags)
                    }
                }
            }
        } else {
            if (libraryInfo.definition != null && libraryInfo.headerInterval != null) {
                val tags = getTags(libraryInfo.header)
                if (!tags.isEmpty()) {
                    var a = getAnnotation(libraryBuilder.library)
                    if (a == null) {
                        a = buildAnnotation()
                        libraryBuilder.library.annotation.add(a)
                    }
                    a.t.addAll(tags)
                }
            }
        }
    }

    private fun getTags(header: String?): List<Tag> {
        var header = header
        if (header != null) {
            header = parseComments(header)
            return parseTags(header)
        }
        return emptyList()
    }

    private fun getTags(tree: ParseTree): List<Tag> {
        val bi = libraryInfo.resolveDefinition(tree)
        return if (bi != null) {
            getTags(bi.header)
        } else emptyList()
    }

    private fun parseTags(header: String?): List<Tag> {
        val header =
            header!!
                .trim { it <= ' ' }
                .split("\n[ \t]*\\*[ \t\\*]*".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
                .joinToString("\n")
        val tags = ArrayList<Tag>()
        var startFrom = 0
        while (startFrom < header.length) {
            val tagNamePair = lookForTagName(header, startFrom)
            if (tagNamePair != null) {
                if (tagNamePair.left.length > 0 && isValidIdentifier(tagNamePair.left)) {
                    var t = af.createTag().withName(tagNamePair.left)
                    startFrom = tagNamePair.right
                    val tagValuePair = lookForTagValue(header, startFrom)
                    if (tagValuePair != null) {
                        if (tagValuePair.left.length > 0) {
                            t = t.withValue(tagValuePair.left)
                            startFrom = tagValuePair.right
                        }
                    }
                    tags.add(t)
                } else {
                    startFrom = tagNamePair.right
                }
            } else { // no name tag found, no need to traverse more
                break
            }
        }
        return tags
    }

    private fun parseComments(header: String): String {
        var header: String? = header
        val result: MutableList<String> = ArrayList()
        if (header != null) {
            header = header.replace("\r\n", "\n")
            val lines = header.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var inMultiline = false
            for (line in lines) {
                if (!inMultiline) {
                    var start = line.indexOf("/*")
                    if (start >= 0) {
                        if (line.endsWith("*/")) {
                            result.add(line.substring(start + 2, line.length - 2))
                        } else {
                            result.add(line.substring(start + 2))
                        }
                        inMultiline = true
                    } else start = line.indexOf("//")
                    if (start >= 0 && !inMultiline) {
                        result.add(line.substring(start + 2))
                    }
                } else {
                    val end = line.indexOf("*/")
                    if (end >= 0) {
                        inMultiline = false
                        if (end > 0) {
                            result.add(line.substring(0, end))
                        }
                    } else {
                        result.add(line)
                    }
                }
            }
        }
        return java.lang.String.join("\n", result)
    }

    fun enableAnnotations() {
        isAnnotationEnabled = true
    }

    fun disableAnnotations() {
        isAnnotationEnabled = false
    }

    private fun buildAnnotation(chunk: Chunk): Annotation {
        val annotation = af.createAnnotation()
        annotation.s = buildNarrative(chunk)
        return annotation
    }

    private fun buildAnnotation(): Annotation {
        return af.createAnnotation()
    }

    private fun addNarrativeToAnnotation(annotation: Annotation, chunk: Chunk) {
        annotation.s = buildNarrative(chunk)
    }

    private fun buildNarrative(chunk: Chunk): Narrative {
        val narrative = af.createNarrative()
        if (chunk.element != null) {
            narrative.r = chunk.element.localId
        }
        if (chunk.hasChunks()) {
            var currentNarrative: Narrative? = null
            for (childChunk in chunk.chunks) {
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
                chunkContent = stripLeading(chunkContent)
            }
            chunkContent = normalizeWhitespace(chunkContent)
            narrative.content.add(chunkContent)
        }
        return narrative
    }

    private fun hasChunks(narrative: Narrative): Boolean {
        for (c in narrative.content) {
            if (c !is String) {
                return true
            }
        }
        return false
    }

    private fun getTrackBack(tree: ParseTree): TrackBack? {
        if (tree is ParserRuleContext) {
            return getTrackBack(tree)
        }
        return if (tree is TerminalNode) {
            getTrackBack(tree)
        } else null
    }

    private fun getTrackBack(ctx: ParserRuleContext): TrackBack {
        return TrackBack(
            libraryBuilder.libraryIdentifier,
            ctx.start!!.line,
            ctx.start!!.charPositionInLine + 1, // 1-based instead of 0-based
            ctx.stop!!.line,
            ctx.stop!!.charPositionInLine + ctx.stop!!.text!!.length // 1-based instead of 0-based
        )
    }

    private fun track(trackable: Trackable, pt: ParseTree): TrackBack? {
        val tb = getTrackBack(pt)
        if (tb != null) {
            trackable.trackbacks.add(tb)
        }
        if (trackable is Element) {
            decorate(trackable, tb)
        }
        return tb
    }

    private fun decorate(element: Element, tb: TrackBack?) {
        if (locate && tb != null) {
            element.locator = tb.toLocator()
        }
        if (resultTypes && element.resultType != null) {
            if (element.resultType is NamedType) {
                element.resultTypeName = libraryBuilder.dataTypeToQName(element.resultType)
            } else {
                element.resultTypeSpecifier =
                    libraryBuilder.dataTypeToTypeSpecifier(element.resultType)
            }
        }
    }

    private fun lookForTagName(header: String?, startFrom: Int): Pair<String, Int>? {
        if (startFrom >= header!!.length) {
            return null
        }
        val start = header.indexOf("@", startFrom)
        if (start < 0) {
            return null
        }
        val nextTagStart = header.indexOf("@", start + 1)
        val nextColon = header.indexOf(":", start + 1)
        if (nextTagStart < 0) { // no next tag , no next colon
            if (nextColon < 0) {
                return Pair.of(
                    header.substring(start + 1, header.length).trim { it <= ' ' },
                    header.length
                )
            }
        } else {
            if (
                nextColon < 0 || nextColon > nextTagStart
            ) { // (has next tag and no colon) or (has next tag and next colon belongs to
                // next tag)
                return Pair.of(
                    header.substring(start + 1, nextTagStart).trim { it <= ' ' },
                    nextTagStart
                )
            }
        }
        return Pair.of(header.substring(start + 1, nextColon).trim { it <= ' ' }, nextColon + 1)
    }

    private fun addExpression(expression: Expression) {
        expressions.add(expression)
    }

    private fun getAnnotation(element: Element): Annotation? {
        for (o in element.annotation) {
            if (o is Annotation) {
                return o
            }
        }
        return null
    }

    protected fun parseString(pt: ParseTree?): String? {
        return StringEscapeUtils.unescapeCql(if (pt == null) null else visit(pt) as String)
    }

    fun enableLocators() {
        locate = true
    }

    fun locatorsEnabled(): Boolean {
        return locate
    }

    fun disableLocators() {
        locate = false
    }

    fun enableResultTypes() {
        resultTypes = true
    }

    fun disableResultTypes() {
        resultTypes = false
    }

    fun resultTypesEnabled(): Boolean {
        return resultTypes
    }

    fun enableDateRangeOptimization() {
        dateRangeOptimization = true
    }

    fun disableDateRangeOptimization() {
        dateRangeOptimization = false
    }

    fun enableDetailedErrors() {
        isDetailedErrorsEnabled = true
    }

    fun disableDetailedErrors() {
        isDetailedErrorsEnabled = false
    }

    fun enableMethodInvocation() {
        isMethodInvocationEnabled = true
    }

    fun disableMethodInvocation() {
        isMethodInvocationEnabled = false
    }

    fun enableFromKeywordRequired() {
        isFromKeywordRequired = true
    }

    fun disableFromKeywordRequired() {
        isFromKeywordRequired = false
    }

    private fun setCompilerOptions(options: CqlCompilerOptions) {
        if (options.options.contains(CqlCompilerOptions.Options.EnableDateRangeOptimization)) {
            enableDateRangeOptimization()
        }
        if (options.options.contains(CqlCompilerOptions.Options.EnableAnnotations)) {
            enableAnnotations()
        }
        if (options.options.contains(CqlCompilerOptions.Options.EnableLocators)) {
            enableLocators()
        }
        if (options.options.contains(CqlCompilerOptions.Options.EnableResultTypes)) {
            enableResultTypes()
        }
        if (options.options.contains(CqlCompilerOptions.Options.EnableDetailedErrors)) {
            enableDetailedErrors()
        }
        if (options.options.contains(CqlCompilerOptions.Options.DisableMethodInvocation)) {
            disableMethodInvocation()
        }
        if (options.options.contains(CqlCompilerOptions.Options.RequireFromKeyword)) {
            enableFromKeywordRequired()
        }
        libraryBuilder.compatibilityLevel = options.compatibilityLevel
    }

    companion object {
        // this method returns Pair<tag value, next tag name lookup index> starting from startFrom
        // can return null in cases.
        // for @1980-12-01, it will potentially check to be treated as value date
        // it looks for parameter in double quotes, e.g. @parameter: "Measurement Interval"
        // [@2019,@2020]
        fun lookForTagValue(header: String?, startFrom: Int): Pair<String, Int>? {
            if (startFrom >= header!!.length) {
                return null
            }
            val nextTag = header.indexOf('@', startFrom)
            val nextStartDoubleQuote = header.indexOf("\"", startFrom)
            if (
                (nextTag < 0 || nextTag > nextStartDoubleQuote) &&
                    nextStartDoubleQuote > 0 &&
                    header.length > nextStartDoubleQuote + 1
            ) {
                val nextEndDoubleQuote = header.indexOf("\"", nextStartDoubleQuote + 1)
                return if (nextEndDoubleQuote > 0) {
                    val parameterEnd = header.indexOf("\n", nextStartDoubleQuote + 1)
                    if (parameterEnd < 0) {
                        Pair.of(header.substring(nextStartDoubleQuote), header.length)
                    } else {
                        Pair.of(header.substring(nextStartDoubleQuote, parameterEnd), parameterEnd)
                    }
                } else { // branch where the 2nd double quote is missing
                    Pair.of(header.substring(nextStartDoubleQuote), header.length)
                }
            }
            if (
                nextTag == startFrom && !isStartingWithDigit(header, nextTag + 1)
            ) { // starts with `@` and not potential date value
                return Pair.of("", startFrom)
            } else if (nextTag > 0) { // has some text before tag
                val interimText = header.substring(startFrom, nextTag).trim { it <= ' ' }
                return if (isStartingWithDigit(header, nextTag + 1)) { // next `@` is a date value
                    if (
                        interimText.length > 0 && interimText != ":"
                    ) { // interim text has value, regards interim text
                        Pair.of(interimText, nextTag)
                    } else {
                        val nextSpace = header.indexOf(' ', nextTag)
                        val nextLine = header.indexOf("\n", nextTag)
                        val mul = nextSpace * nextLine
                        var nextDelimeterIndex = header.length
                        if (mul < 0) {
                            nextDelimeterIndex = Math.max(nextLine, nextSpace)
                        } else if (mul > 1) {
                            nextDelimeterIndex = Math.min(nextLine, nextSpace)
                        }
                        Pair.of(header.substring(nextTag, nextDelimeterIndex), nextDelimeterIndex)
                    }
                } else { // next `@` is not date
                    Pair.of(interimText, nextTag)
                }
            }
            return Pair.of(header.substring(startFrom).trim { it <= ' ' }, header.length)
        }

        fun wrapNarrative(narrative: Narrative): Serializable {
            @Suppress("ForbiddenComment")
            /*
            TODO: Should be able to collapse narrative if the span doesn't have an attribute
            That's what this code is doing, but it doesn't work and I don't have time to debug it
            if (narrative.getR() == null) {
                StringBuilder content = new StringBuilder();
                boolean onlyStrings = true;
                for (Serializable s : narrative.getContent()) {
                    if (s instanceof String) {
                        content.append((String)s);
                    }
                    else {
                        onlyStrings = false;
                    }
                }
                if (onlyStrings) {
                    return content.toString();
                }
            }
            */
            return JAXBElement(
                QName("urn:hl7-org:cql-annotations:r1", "s"),
                Narrative::class.java,
                narrative
            )
        }

        fun isValidIdentifier(tagName: String): Boolean {
            for (i in 0 until tagName.length) {
                if (tagName[i] == '_') {
                    continue
                }
                if (i == 0) {
                    if (!Character.isLetter(tagName[i])) {
                        return false
                    }
                } else {
                    if (!Character.isLetterOrDigit(tagName[i])) {
                        return false
                    }
                }
            }
            return true
        }

        fun getTypeIdentifier(qualifiers: List<String?>?, identifier: String?): String? {
            if (qualifiers!!.size > 1) {
                var result: String? = null
                for (i in 1 until qualifiers.size) {
                    result = if (result == null) qualifiers[i] else result + "." + qualifiers[i]
                }
                return "$result.$identifier"
            }
            return identifier
        }

        fun getModelIdentifier(qualifiers: List<String?>?): String? {
            return if (qualifiers!!.size > 0) qualifiers[0] else null
        }

        @Suppress("ForbiddenComment")
        // TODO: Should just use String.stripLeading() but that is only available in 11+
        fun stripLeading(s: String): String {
            var index = 0
            while (index < s.length) {
                if (!Character.isWhitespace(s[index])) {
                    break
                }
                index++
            }
            return if (index == s.length) {
                ""
            } else s.substring(index)
        }

        fun normalizeWhitespace(input: String): String {
            return input.replace("\r\n", "\n")
        }

        fun isStartingWithDigit(header: String?, index: Int): Boolean {
            return index < header!!.length && Character.isDigit(header[index])
        }
    }
}
