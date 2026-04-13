package org.cqframework.cql.cql2elm.preprocessor

import kotlin.jvm.JvmField
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.cqframework.cql.cql2elm.*
import org.cqframework.cql.cql2elm.model.Chunk
import org.cqframework.cql.cql2elm.model.FunctionHeader
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.cql2elm.utils.Stack
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.gen.cqlBaseVisitor
import org.cqframework.cql.gen.cqlParser.*
import org.hl7.cql.model.*
import org.hl7.elm.r1.*

/** Common functionality used by [CqlPreprocessor] and [Cql2ElmVisitor] */
@Suppress(
    "LargeClass",
    "CyclomaticComplexMethod",
    "NestedBlockDepth",
    "TooManyFunctions",
    "ComplexCondition",
    "ReturnCount",
)
abstract class CqlPreprocessorElmCommonVisitor(
    @JvmField protected val libraryBuilder: LibraryBuilder,
    protected val tokenStream: TokenStream,
) : cqlBaseVisitor<Any?>() {
    @JvmField protected val of: IdObjectFactory = libraryBuilder.objectFactory
    @JvmField
    protected val annotationBuilder: AnnotationBuilder =
        AnnotationBuilder(libraryBuilder, tokenStream)
    protected var implicitContextCreated = false
    protected var currentContext = "Unfiltered"

    /** Active chunk stack, owned by the [annotationBuilder]. */
    @Suppress("VariableNaming")
    protected var chunks: Stack<Chunk>
        get() = annotationBuilder.chunks
        set(value) {
            annotationBuilder.chunks = value
        }

    var libraryInfo: LibraryInfo
        get() = annotationBuilder.libraryInfo
        protected set(value) {
            annotationBuilder.libraryInfo = value
        }

    var isAnnotationEnabled: Boolean
        get() = annotationBuilder.enabled
        private set(value) {
            annotationBuilder.enabled = value
        }

    var isDetailedErrorsEnabled = false
        private set

    private var locate = false
    private var resultTypes = false

    var isMethodInvocationEnabled = true
        private set

    var isFromKeywordRequired = false
        private set

    init {
        // Don't talk to strangers. Except when you have to.
        setCompilerOptions(libraryBuilder.libraryManager.cqlCompilerOptions)
    }

    protected fun saveCurrentContext(currentContext: String): String {
        val saveContext = this.currentContext
        this.currentContext = currentContext
        return saveContext
    }

    override fun visit(tree: ParseTree): Any? {
        val pushedChunk = pushChunk(tree)
        var o: Any? = null
        return try {
            // ERROR:
            try {
                o = super.visit(tree)
                if (o is Element && o.localId.isNullOrEmpty()) {
                    throw CqlInternalException(
                        "Internal translator error. 'localId' was not assigned for Element \"${o::class.simpleName}\"",
                        getTrackBack(tree),
                    )
                }
            } catch (e: CqlCompilerException) {
                e.locator = e.locator ?: getTrackBack(tree)
                libraryBuilder.recordParsingException(e)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                val ex =
                    if (e.message == null) {
                        CqlInternalException("Internal translator error.", getTrackBack(tree), e)
                    } else {
                        CqlSemanticException(
                            e.message!!,
                            getTrackBack(tree),
                            CqlCompilerException.ErrorSeverity.Error,
                            e,
                        )
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
            if (o is Element && tree !is LibraryContext) {
                track(o, tree)
            }
            o
        } finally {
            popChunk(tree, o, pushedChunk)
            processTags(tree, o)
        }
    }

    override fun visitTupleElementDefinition(
        ctx: TupleElementDefinitionContext
    ): TupleElementDefinition {
        val result =
            of.createTupleElementDefinition()
                .withName(parseString(ctx.referentialIdentifier()))
                .withElementType(parseTypeSpecifier(ctx.typeSpecifier()))

        return result
    }

    override fun visitTupleTypeSpecifier(ctx: TupleTypeSpecifierContext): Any {
        val elements = mutableListOf<TupleTypeElement>()
        val typeSpecifier = of.createTupleTypeSpecifier()
        for (definitionContext in ctx.tupleElementDefinition()) {
            val element = visit(definitionContext) as TupleElementDefinition
            elements.add(TupleTypeElement(element.name!!, element.elementType!!.resultType!!))
            typeSpecifier.element.add(element)
        }
        typeSpecifier.resultType = TupleType(elements)
        return typeSpecifier
    }

    override fun visitChoiceTypeSpecifier(ctx: ChoiceTypeSpecifierContext): ChoiceTypeSpecifier {
        val specifiersByType = LinkedHashMap<DataType, TypeSpecifier>()
        for (typeSpecifierContext in ctx.typeSpecifier()) {
            val typeSpecifier = parseTypeSpecifier(typeSpecifierContext)!!
            specifiersByType[typeSpecifier.resultType!!] = typeSpecifier
        }
        val choiceType = ChoiceType(specifiersByType.keys)
        // Build the specifier list in the same order as choiceType.types (sorted) so that
        // ChoiceTypeSpecifier ordering is always consistent with ChoiceType ordering.
        val sortedSpecifiers = choiceType.types.map { checkNotNull(specifiersByType[it]) }
        val result = of.createChoiceTypeSpecifier().withChoice(sortedSpecifiers)
        result.resultType = choiceType
        return result
    }

    override fun visitIntervalTypeSpecifier(
        ctx: IntervalTypeSpecifierContext
    ): IntervalTypeSpecifier {
        val result =
            of.createIntervalTypeSpecifier().withPointType(parseTypeSpecifier(ctx.typeSpecifier()))
        val intervalType = IntervalType(result.pointType!!.resultType!!)
        result.resultType = intervalType
        return result
    }

    override fun visitListTypeSpecifier(ctx: ListTypeSpecifierContext): ListTypeSpecifier {
        val result =
            of.createListTypeSpecifier().withElementType(parseTypeSpecifier(ctx.typeSpecifier()))
        val listType = ListType(result.elementType!!.resultType!!)
        result.resultType = listType
        return result
    }

    fun parseFunctionHeader(ctx: FunctionDefinitionContext): FunctionHeader {
        val functionDef =
            of.createFunctionDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifierOrFunctionIdentifier()))
                .withContext(currentContext)
        if (ctx.fluentModifier() != null) {
            libraryBuilder.checkCompatibilityLevel("Fluent functions", "1.5")
            functionDef.fluent = true
        }

        for (opdef in ctx.operandDefinition()) {
            val typeSpecifier = parseTypeSpecifier(opdef.typeSpecifier())!!
            functionDef.operand.add(
                of.createOperandDef()
                    .withName(parseString(opdef.referentialIdentifier()))
                    .withOperandTypeSpecifier(typeSpecifier)
                    .withResultType(typeSpecifier.resultType)
            )
        }
        val typeSpecifierContext = ctx.typeSpecifier()
        return if (typeSpecifierContext == null) {
            FunctionHeader(functionDef)
        } else {
            FunctionHeader(functionDef, parseTypeSpecifier(typeSpecifierContext))
        }
    }

    protected fun parseTypeSpecifier(pt: ParseTree?): TypeSpecifier? {
        return if (pt == null) null else visit(pt) as TypeSpecifier
    }

    protected fun parseAccessModifier(pt: ParseTree?): AccessModifier {
        return if (pt == null) AccessModifier.PUBLIC else (visit(pt) as AccessModifier)
    }

    protected fun parseQualifiers(ctx: NamedTypeSpecifierContext): kotlin.collections.List<String> {
        val qualifiers = ArrayList<String>()
        for (qualifierContext in ctx.qualifier()) {
            val qualifier = parseString(qualifierContext)!!
            qualifiers.add(qualifier)
        }
        return qualifiers
    }

    protected open fun getModel(
        modelNamespace: NamespaceInfo?,
        modelName: String?,
        version: String?,
        localIdentifier: String,
    ): Model {
        val modelId = modelName ?: libraryInfo.defaultUsingDefinition?.name
        val modelVersion = version ?: libraryInfo.defaultUsingDefinition?.version
        val modelIdentifier =
            ModelIdentifier(id = modelId!!, version = modelVersion, system = modelNamespace?.uri)
        return libraryBuilder.getModel(modelIdentifier, localIdentifier)
    }

    private fun pushChunk(tree: ParseTree): Boolean = annotationBuilder.pushChunk(tree)

    private fun popChunk(tree: ParseTree, o: Any?, pushedChunk: Boolean) {
        annotationBuilder.popChunk(tree, o, pushedChunk)
    }

    private fun processTags(tree: ParseTree, o: Any?) {
        annotationBuilder.processTags(tree, o)
    }

    fun enableAnnotations() {
        isAnnotationEnabled = true
    }

    fun disableAnnotations() {
        isAnnotationEnabled = false
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
            ctx.start?.line ?: 0,
            (ctx.start?.charPositionInLine ?: 0) + 1, // 1-based instead of 0-based
            ctx.stop?.line ?: 0,
            (ctx.stop?.charPositionInLine ?: 0) +
                (ctx.stop?.text?.length ?: 0), // 1-based instead of 0-based
        )
    }

    private fun track(element: Element, pt: ParseTree): TrackBack? {
        val tb = getTrackBack(pt)
        if (tb != null) {
            element.trackbacks.add(tb)
        }

        decorate(element, tb)
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

    protected fun parseString(pt: ParseTree?): String? {
        return if (pt == null) null else StringEscapeUtils.unescapeCql(visit(pt) as String)
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
        // EnableDateRangeOptimization is consumed by the post-visit pipeline; see CqlCompiler.
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
        fun getTypeIdentifier(
            qualifiers: kotlin.collections.List<String>,
            identifier: String,
        ): String {
            if (qualifiers.size > 1) {
                var result: String? = null
                for (i in 1 until qualifiers.size) {
                    result = if (result == null) qualifiers[i] else result + "." + qualifiers[i]
                }
                return "$result.$identifier"
            }
            return identifier
        }

        fun getModelIdentifier(qualifiers: kotlin.collections.List<String>): String? {
            return if (qualifiers.isNotEmpty()) qualifiers[0] else null
        }
    }
}
