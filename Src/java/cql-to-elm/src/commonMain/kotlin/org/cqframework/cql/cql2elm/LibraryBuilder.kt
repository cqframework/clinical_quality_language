@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass
import org.cqframework.cql.cql2elm.model.*
import org.cqframework.cql.cql2elm.model.invocation.*
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.cql2elm.utils.getTranslatorVersion
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.hl7.cql.model.*
import org.hl7.cql_annotations.r1.*
import org.hl7.cql_annotations.r1.ObjectFactory
import org.hl7.elm.r1.*

/** Created by Bryn on 12/29/2016. */
@JsOnlyExport
@Suppress(
    "LargeClass",
    "TooManyFunctions",
    "ForbiddenComment",
    "ReturnCount",
    "MaxLineLength",
    "NON_EXPORTABLE_TYPE",
)
class LibraryBuilder(
    val namespaceInfo: NamespaceInfo?, // Note: allowed to be null, implies global namespace
    val libraryManager: LibraryManager,
    val objectFactory: IdObjectFactory,
) {
    enum class SignatureLevel {
        /** Indicates signatures will never be included in operator invocations */
        None,

        /**
         * Indicates signatures will only be included in invocations if the declared signature of
         * the resolve operator is different from the invocation signature
         */
        Differing,

        /**
         * Indicates signatures will only be included in invocations if the function has multiple
         * overloads with the same number of arguments as the invocation
         */
        Overloads,

        /** Indicates signatures will always be included in invocations */
        All,
    }

    @JsExport.Ignore
    constructor(
        libraryManager: LibraryManager,
        objectFactory: IdObjectFactory,
    ) : this(null, libraryManager, objectFactory)

    // Only exceptions of severity Error
    val errors: MutableList<CqlCompilerException> = ArrayList()

    // Only exceptions of severity Warning
    val warnings: MutableList<CqlCompilerException> = ArrayList()

    // Only exceptions of severity Info
    val messages: MutableList<CqlCompilerException> = ArrayList()

    // All exceptions
    val exceptions: MutableList<CqlCompilerException> = ArrayList()

    internal val models: MutableMap<String, Model?> = LinkedHashMap()
    internal val modelsInternal: MutableMap<String, Model?>
        get() = models

    internal val libraries: MutableMap<String, CompiledLibrary> = LinkedHashMap()
    private val systemFunctionResolver: SystemFunctionResolver = SystemFunctionResolver(this)
    internal val systemFunctionResolverInternal: SystemFunctionResolver
        get() = systemFunctionResolver

    internal val expressionFactoryInternal: ExpressionFactory
        get() = expressionFactory

    val scopeManager: ScopeManager = ScopeManager()
    private val propertyResolver: PropertyResolver by lazy { PropertyResolver(this, objectFactory) }
    private val symbolTable: SymbolTable by lazy { SymbolTable(this) }
    private val expressionFactory: ExpressionFactory by lazy {
        ExpressionFactory(this, objectFactory)
    }
    private val semanticAnalyzer: SemanticAnalyzer by lazy { SemanticAnalyzer(this, objectFactory) }
    private val typeResolver: TypeResolver by lazy { TypeResolver(this, objectFactory) }
    private val conversionEngine: ConversionEngine by lazy { ConversionEngine(this, objectFactory) }
    private val modelManager = libraryManager.modelManager
    var defaultModel: Model? = null
        internal set(model) {
            // The default model is the first model that is not System
            if (field == null && model?.modelInfo?.name != "System") {
                field = model
            }
        }

    internal var defaultModelInternal: Model?
        get() = defaultModel
        set(value) {
            defaultModel = value
        }

    var library: Library =
        objectFactory
            .createLibrary()
            .withSchemaIdentifier(
                objectFactory
                    .createVersionedIdentifier()
                    .withId("urn:hl7-org:elm") // TODO: Pull this from the ELM library namespace
                    .withVersion("r1")
            )
    var compiledLibrary = CompiledLibrary()
    val conversionMap = ConversionMap()
    private val af = ObjectFactory()
    internal var listTraversal = true
    private val options: CqlCompilerOptions = libraryManager.cqlCompilerOptions
    private val cqlToElmInfo = af.createCqlToElmInfo()
    internal val typeBuilder = TypeBuilder(objectFactory, modelManager)
    internal val typeBuilderInternal: TypeBuilder
        get() = typeBuilder

    fun enableListTraversal() {
        listTraversal = true
    }

    private fun setCompilerOptions(options: CqlCompilerOptions) {
        if (options.options.contains(CqlCompilerOptions.Options.DisableListTraversal)) {
            listTraversal = false
        }
        if (options.options.contains(CqlCompilerOptions.Options.DisableListDemotion)) {
            conversionMap.isListDemotionEnabled = false
        }
        if (options.options.contains(CqlCompilerOptions.Options.DisableListPromotion)) {
            conversionMap.isListPromotionEnabled = false
        }
        if (options.options.contains(CqlCompilerOptions.Options.EnableIntervalDemotion)) {
            conversionMap.isIntervalDemotionEnabled = true
        }
        if (options.options.contains(CqlCompilerOptions.Options.EnableIntervalPromotion)) {
            conversionMap.isIntervalPromotionEnabled = true
        }
        compatibilityLevel = options.compatibilityLevel
        cqlToElmInfo.translatorOptions = options.toString()
        cqlToElmInfo.signatureLevel = options.signatureLevel.name
    }

    var compatibilityLevel: String? = null
        set(compatibilityLevel) {
            field = compatibilityLevel
            if (compatibilityLevel != null) {
                compatibilityVersion = Version(compatibilityLevel)
            }
        }

    val isCompatibilityLevel3: Boolean
        get() = ("1.3" == compatibilityLevel)

    val isCompatibilityLevel4: Boolean
        get() = ("1.4" == compatibilityLevel)

    private var compatibilityVersion: Version? = null

    init {
        cqlToElmInfo.translatorVersion = getTranslatorVersion()
        library.annotation.add(cqlToElmInfo)
        setCompilerOptions(options)
        compiledLibrary.library = library
    }

    fun isCompatibleWith(sinceCompatibilityLevel: String?): Boolean {
        if (compatibilityVersion == null) {
            // No compatibility version is specified, assume latest functionality
            return true
        }
        require(!sinceCompatibilityLevel.isNullOrEmpty()) {
            "Internal Translator Error: compatibility level is required to perform a compatibility check"
        }
        val sinceVersion = Version(sinceCompatibilityLevel)
        return compatibilityVersion!!.compatibleWith(sinceVersion)
    }

    fun checkCompatibilityLevel(featureName: String?, sinceCompatibilityLevel: String?) {
        require(!featureName.isNullOrEmpty()) {
            "Internal Translator Error: feature name is required to perform a compatibility check"
        }
        require(isCompatibleWith(sinceCompatibilityLevel)) {
            "Feature $featureName was introduced in version $sinceCompatibilityLevel and so cannot be used at compatibility level $compatibilityLevel"
        }
    }

    /**
     * A "well-known" model name is one that is allowed to resolve without a namespace in a
     * namespace-aware context
     */
    fun isWellKnownModelName(unqualifiedIdentifier: String?): Boolean =
        symbolTable.isWellKnownModelName(unqualifiedIdentifier)

    /**
     * A "well-known" library name is a library name that is allowed to resolve without a namespace
     * in a namespace-aware context
     */
    fun isWellKnownLibraryName(unqualifiedIdentifier: String?): Boolean =
        symbolTable.isWellKnownLibraryName(unqualifiedIdentifier)

    @JsExport.Ignore
    fun getModel(modelIdentifier: ModelIdentifier, localIdentifier: String): Model =
        symbolTable.getModel(modelIdentifier, localIdentifier)

    fun getNamedTypeSpecifierResult(
        namedTypeSpecifierIdentifier: String
    ): ResultWithPossibleError<NamedTypeSpecifier?>? =
        typeResolver.getNamedTypeSpecifierResult(namedTypeSpecifierIdentifier)

    fun addNamedTypeSpecifierResult(
        namedTypeSpecifierIdentifier: String,
        namedTypeSpecifierResult: ResultWithPossibleError<NamedTypeSpecifier?>,
    ) =
        typeResolver.addNamedTypeSpecifierResult(
            namedTypeSpecifierIdentifier,
            namedTypeSpecifierResult,
        )

    fun hasUsings(): Boolean = symbolTable.hasUsings()

    private fun addUsing(usingDef: UsingDef) {
        symbolTable.addUsing(usingDef)
    }

    fun resolveContextName(modelName: String?, contextName: String): ModelContext? =
        typeResolver.resolveContextName(modelName, contextName)

    @JsExport.Ignore
    fun resolveTypeName(typeName: String): DataType? = typeResolver.resolveTypeName(typeName)

    @JsExport.Ignore
    fun resolveTypeName(modelName: String?, typeName: String): DataType? =
        typeResolver.resolveTypeName(modelName, typeName)

    fun resolveTypeSpecifier(typeSpecifier: String?): DataType? =
        typeResolver.resolveTypeSpecifier(typeSpecifier)

    fun resolveUsingRef(modelName: String): UsingDef? = symbolTable.resolveUsingRef(modelName)

    val systemModel: SystemModel
        get() = symbolTable.systemModel

    @JsExport.Ignore fun getModel(modelName: String): Model = symbolTable.getModel(modelName)

    internal fun loadConversionMap(library: CompiledLibrary) {
        symbolTable.loadConversionMap(library)
    }

    private val systemLibrary: CompiledLibrary
        get() = resolveLibrary("System")

    internal val systemLibraryInternal: CompiledLibrary
        get() = systemLibrary

    fun resolveLibrary(identifier: String?): CompiledLibrary =
        symbolTable.resolveLibrary(identifier)

    fun resolveNamespaceUri(namespaceName: String, mustResolve: Boolean): String? =
        symbolTable.resolveNamespaceUri(namespaceName, mustResolve)

    private fun toErrorSeverity(severity: CqlCompilerException.ErrorSeverity): ErrorSeverity {
        return when (severity) {
            CqlCompilerException.ErrorSeverity.Info -> {
                ErrorSeverity.INFO
            }
            CqlCompilerException.ErrorSeverity.Warning -> {
                ErrorSeverity.WARNING
            }
            CqlCompilerException.ErrorSeverity.Error -> {
                ErrorSeverity.ERROR
            }
        }
    }

    private fun addException(e: CqlCompilerException) {
        // Always add to the list of all exceptions
        exceptions.add(e)
        when (e.severity) {
            CqlCompilerException.ErrorSeverity.Error -> {
                errors.add(e)
            }
            CqlCompilerException.ErrorSeverity.Warning -> {
                warnings.add(e)
            }
            CqlCompilerException.ErrorSeverity.Info -> {
                messages.add(e)
            }
        }
    }

    private fun shouldReport(errorSeverity: CqlCompilerException.ErrorSeverity): Boolean {
        return when (options.errorLevel) {
            CqlCompilerException.ErrorSeverity.Info ->
                errorSeverity == CqlCompilerException.ErrorSeverity.Info ||
                    errorSeverity == CqlCompilerException.ErrorSeverity.Warning ||
                    errorSeverity == CqlCompilerException.ErrorSeverity.Error
            CqlCompilerException.ErrorSeverity.Warning ->
                (errorSeverity == CqlCompilerException.ErrorSeverity.Warning ||
                    errorSeverity == CqlCompilerException.ErrorSeverity.Error)
            CqlCompilerException.ErrorSeverity.Error ->
                errorSeverity == CqlCompilerException.ErrorSeverity.Error
            else -> throw IllegalArgumentException("Unknown error severity $errorSeverity")
        }
    }

    /**
     * Record any errors while parsing in both the list of errors but also in the library itself so
     * they can be processed easily by a remote client
     *
     * @param e the exception to record
     */
    fun recordParsingException(e: CqlCompilerException) {
        addException(e)
        if (shouldReport(e.severity)) {
            val err = af.createCqlToElmError()
            err.message = e.message
            err.errorSeverity = toErrorSeverity(e.severity)
            if (e.locator != null) {
                val loc = e.locator!!
                if (loc.library != null) {
                    val lib = loc.library
                    err.librarySystem = lib.system
                    err.libraryId = lib.id
                    err.libraryVersion = lib.version
                }
                err.startLine = loc.startLine
                err.endLine = loc.endLine
                err.startChar = loc.startChar
                err.endChar = loc.endChar
            }
            if (e is CqlIncludeException) {
                err.targetIncludeLibrarySystem = e.librarySystem
                err.targetIncludeLibraryId = e.libraryId
                err.targetIncludeLibraryVersionId = e.versionId
            }

            err.errorType =
                when (e) {
                    is CqlSyntaxException -> ErrorType.SYNTAX
                    is CqlIncludeException -> ErrorType.INCLUDE
                    is CqlSemanticException -> ErrorType.SEMANTIC
                    else -> ErrorType.INTERNAL
                }

            library.annotation.add(err)
        }
    }

    fun beginTranslation() {
        symbolTable.beginTranslation()
    }

    var libraryIdentifier: VersionedIdentifier?
        get() = library.identifier
        set(vid) {
            library.identifier = vid
            compiledLibrary.identifier = vid
        }

    fun endTranslation() {
        symbolTable.endTranslation()
    }

    fun canResolveLibrary(includeDef: IncludeDef): Boolean =
        symbolTable.canResolveLibrary(includeDef)

    fun addInclude(includeDef: IncludeDef) {
        symbolTable.addInclude(includeDef)
    }

    fun addParameter(paramDef: ParameterDef) {
        symbolTable.addParameter(paramDef)
    }

    fun addCodeSystem(cs: CodeSystemDef) {
        symbolTable.addCodeSystem(cs)
    }

    fun addValueSet(vs: ValueSetDef) {
        symbolTable.addValueSet(vs)
    }

    fun addCode(cd: CodeDef) {
        symbolTable.addCode(cd)
    }

    fun addConcept(cd: ConceptDef) {
        symbolTable.addConcept(cd)
    }

    fun addContext(cd: ContextDef) {
        symbolTable.addContext(cd)
    }

    fun addExpression(expDef: ExpressionDef) {
        symbolTable.addExpression(expDef)
    }

    fun removeExpression(expDef: ExpressionDef) {
        symbolTable.removeExpression(expDef)
    }

    fun resolve(identifier: String): ResolvedIdentifierContext = symbolTable.resolve(identifier)

    fun resolveIncludeRef(identifier: String): IncludeDef? =
        symbolTable.resolveIncludeRef(identifier)

    private fun resolveIncludeAlias(libraryIdentifier: VersionedIdentifier): String? {
        return compiledLibrary.resolveIncludeAlias(libraryIdentifier)
    }

    fun resolveCodeSystemRef(identifier: String): CodeSystemDef? =
        symbolTable.resolveCodeSystemRef(identifier)

    fun resolveValueSetRef(identifier: String): ValueSetDef? =
        symbolTable.resolveValueSetRef(identifier)

    fun resolveCodeRef(identifier: String): CodeDef? = symbolTable.resolveCodeRef(identifier)

    fun resolveConceptRef(identifier: String): ConceptDef? =
        symbolTable.resolveConceptRef(identifier)

    fun resolveParameterRef(identifier: String): ParameterDef? {
        checkLiteralContext()
        return compiledLibrary.resolveParameterRef(identifier)
    }

    fun resolveExpressionRef(identifier: String): ExpressionDef? {
        checkLiteralContext()
        return compiledLibrary.resolveExpressionRef(identifier)
    }

    fun findConversion(
        fromType: DataType,
        toType: DataType,
        implicit: Boolean,
        allowPromotionAndDemotion: Boolean,
    ): Conversion? =
        conversionEngine.findConversion(fromType, toType, implicit, allowPromotionAndDemotion)

    /**
     * Resolve an operator invocation, dispatching on the runtime type of [expression] to wrap it in
     * the appropriate [Invocation]. Replaces the family of arity-specific `resolve*Call` wrappers
     * (`resolveUnaryCall`, `resolveBinaryCall`, etc.).
     *
     * Set-membership operators (`In`, `IncludedIn`, `ProperIn`, `ProperIncludedIn`, etc.) carry
     * CQL-specific semantics beyond arity and stay on their own dedicated entry points.
     */
    @JsExport.Ignore
    fun resolveCall(
        libraryName: String?,
        operatorName: String,
        expression: Expression,
    ): Expression? = semanticAnalyzer.resolveCall(libraryName, operatorName, expression)

    @JvmOverloads
    fun resolveBinaryInvocation(
        libraryName: String?,
        operatorName: String,
        expression: BinaryExpression,
        mustResolve: Boolean = true,
        allowPromotionAndDemotion: Boolean = false,
    ): Invocation? =
        semanticAnalyzer.resolveBinaryInvocation(
            libraryName,
            operatorName,
            expression,
            mustResolve,
            allowPromotionAndDemotion,
        )

    /**
     * Variant of [resolveCall] that allows the caller to override resolution behavior for the
     * specific binary call. Kept as a dedicated entry point because it forwards `mustResolve` and
     * `allowPromotionAndDemotion`, which the unified helper does not.
     */
    @JsExport.Ignore
    fun resolveBinaryCall(
        libraryName: String?,
        operatorName: String,
        expression: BinaryExpression,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
    ): Expression? =
        semanticAnalyzer.resolveBinaryCall(
            libraryName,
            operatorName,
            expression,
            mustResolve,
            allowPromotionAndDemotion,
        )

    fun resolveUnion(left: Expression, right: Expression): Expression =
        semanticAnalyzer.resolveUnion(left, right)

    fun resolveIntersect(left: Expression, right: Expression): Expression =
        semanticAnalyzer.resolveIntersect(left, right)

    fun resolveExcept(left: Expression, right: Expression): Expression =
        semanticAnalyzer.resolveExcept(left, right)

    @JsExport.Ignore
    fun resolveIn(left: Expression, right: Expression): Expression =
        semanticAnalyzer.resolveIn(left, right)

    @JsExport.Ignore
    fun resolveContains(left: Expression, right: Expression): Expression =
        semanticAnalyzer.resolveContains(left, right)

    @JsExport.Ignore
    fun resolveIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveIn(left, right, dateTimePrecision)

    fun resolveProperIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveProperIn(left, right, dateTimePrecision)

    @JsExport.Ignore
    fun resolveContains(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveContains(left, right, dateTimePrecision)

    fun resolveProperContains(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveProperContains(left, right, dateTimePrecision)

    fun resolveIncludes(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveIncludes(left, right, dateTimePrecision)

    fun resolveProperIncludes(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveProperIncludes(left, right, dateTimePrecision)

    fun resolveIncludedIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveIncludedIn(left, right, dateTimePrecision)

    fun resolveProperIncludedIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?,
    ): Expression? = semanticAnalyzer.resolveProperIncludedIn(left, right, dateTimePrecision)

    fun checkAccessLevel(
        libraryName: String?,
        objectName: String?,
        accessModifier: AccessModifier,
    ) = semanticAnalyzer.checkAccessLevel(libraryName, objectName, accessModifier)

    @JsExport.Ignore
    fun resolveFunction(
        libraryName: String?,
        functionName: String,
        paramList: kotlin.collections.List<Expression>,
    ): Expression? = semanticAnalyzer.resolveFunction(libraryName, functionName, paramList)

    @JsExport.Ignore
    @Suppress("LongParameterList")
    fun resolveFunction(
        libraryName: String?,
        functionName: String,
        paramList: kotlin.collections.List<Expression>,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): Invocation? =
        semanticAnalyzer.resolveFunction(
            libraryName,
            functionName,
            paramList,
            mustResolve,
            allowPromotionAndDemotion,
            allowFluent,
        )

    fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
    ): Expression? = semanticAnalyzer.resolveCall(libraryName, operatorName, invocation)

    internal fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): Expression? =
        semanticAnalyzer.resolveCall(
            libraryName,
            operatorName,
            invocation,
            allowPromotionAndDemotion,
            allowFluent,
        )

    @Suppress("LongParameterList")
    private fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): Expression? =
        semanticAnalyzer.resolveCall(
            libraryName,
            operatorName,
            invocation,
            mustResolve,
            allowPromotionAndDemotion,
            allowFluent,
        )

    @JsExport.Ignore
    @JvmOverloads
    @Suppress("LongParameterList")
    fun resolveInvocation(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        mustResolve: Boolean = true,
        allowPromotionAndDemotion: Boolean = false,
        allowFluent: Boolean = false,
    ): Invocation? =
        semanticAnalyzer.resolveInvocation(
            libraryName,
            operatorName,
            invocation,
            mustResolve,
            allowPromotionAndDemotion,
            allowFluent,
        )

    fun resolveFunctionDefinition(fd: FunctionDef): Operator? =
        semanticAnalyzer.resolveFunctionDefinition(fd)

    fun verifyComparable(dataType: DataType) {
        val left = objectFactory.createLiteral().withResultType(dataType) as Expression
        val right = objectFactory.createLiteral().withResultType(dataType) as Expression
        val comparison: BinaryExpression =
            objectFactory.createLess().withOperand(listOf(left, right))
        resolveCall("System", "Less", comparison)
    }

    @JsExport.Ignore
    @JvmOverloads
    fun convertExpression(
        expression: Expression,
        targetType: DataType,
        implicit: Boolean = true,
    ): Expression = conversionEngine.convertExpression(expression, targetType, implicit)

    internal fun reportWarning(message: String, expression: Element?) {
        val trackback =
            if (expression != null && expression.trackbacks.isNotEmpty()) expression.trackbacks[0]
            else null
        val warning =
            CqlSemanticException(message, trackback, CqlCompilerException.ErrorSeverity.Warning)
        recordParsingException(warning)
    }

    fun resolveToList(expression: Expression?): Expression =
        conversionEngine.resolveToList(expression)

    fun buildAs(expression: Expression?, asType: DataType?): As =
        expressionFactory.buildAs(expression, asType)

    fun buildIs(expression: Expression?, isType: DataType?): Is =
        expressionFactory.buildIs(expression, isType)

    fun buildNull(nullType: DataType?): Null = expressionFactory.buildNull(nullType)

    internal fun buildIsNotNull(expression: Expression?): Not =
        expressionFactory.buildIsNotNull(expression)

    fun buildMinimum(dataType: DataType?): MinValue = expressionFactory.buildMinimum(dataType)

    fun buildMaximum(dataType: DataType?): MaxValue = expressionFactory.buildMaximum(dataType)

    fun buildPredecessor(source: Expression?): Expression =
        expressionFactory.buildPredecessor(source)

    fun buildSuccessor(source: Expression?): Expression = expressionFactory.buildSuccessor(source)

    @JsExport.Ignore
    fun convertExpression(expression: Expression, conversion: Conversion): Expression =
        conversionEngine.convertExpression(expression, conversion)

    fun verifyType(actualType: DataType, expectedType: DataType) =
        typeResolver.verifyType(actualType, expectedType)

    fun findCompatibleType(first: DataType?, second: DataType?): DataType? =
        typeResolver.findCompatibleType(first, second)

    fun ensureCompatibleTypes(first: DataType?, second: DataType): DataType? =
        typeResolver.ensureCompatibleTypes(first, second)

    fun ensureCompatible(expression: Expression?, targetType: DataType?): Expression =
        typeResolver.ensureCompatible(expression, targetType)

    fun enforceCompatible(expression: Expression?, targetType: DataType?): Expression =
        typeResolver.enforceCompatible(expression, targetType)

    @JsExport.Ignore
    fun createLiteral(string: String?): Literal = expressionFactory.createLiteral(string)

    @JsExport.Ignore
    fun createLiteral(bool: Boolean): Literal = expressionFactory.createLiteral(bool)

    @JsExport.Ignore
    fun createLiteral(integer: Int): Literal = expressionFactory.createLiteral(integer)

    @JsExport.Ignore
    fun createLiteral(value: Double): Literal = expressionFactory.createLiteral(value)

    fun createNumberLiteral(value: String): Literal = expressionFactory.createNumberLiteral(value)

    fun createLongNumberLiteral(value: String?): Literal =
        expressionFactory.createLongNumberLiteral(value)

    internal fun validateUnit(unit: String) {
        when (unit) {
            "year",
            "years",
            "month",
            "months",
            "week",
            "weeks",
            "day",
            "days",
            "hour",
            "hours",
            "minute",
            "minutes",
            "second",
            "seconds",
            "millisecond",
            "milliseconds" -> {}
            else -> validateUcumUnit(unit)
        }
    }

    fun ensureUcumUnit(unit: String): String {
        when (unit) {
            "year",
            "years" -> return "a"
            "month",
            "months" -> return "mo"
            "week",
            "weeks" -> return "wk"
            "day",
            "days" -> return "d"
            "hour",
            "hours" -> return "h"
            "minute",
            "minutes" -> return "min"
            "second",
            "seconds" -> return "s"
            "millisecond",
            "milliseconds" -> return "ms"
            else -> validateUcumUnit(unit)
        }
        return unit
    }

    private fun validateUcumUnit(unit: String) {
        val message = libraryManager.ucumService.validate(unit)
        require(message == null) { message!! }
    }

    fun createQuantity(value: BigDecimal?, unit: String): Quantity =
        expressionFactory.createQuantity(value, unit)

    fun createRatio(numerator: Quantity?, denominator: Quantity?): Ratio =
        expressionFactory.createRatio(numerator, denominator)

    fun createInterval(
        low: Expression?,
        lowClosed: Boolean,
        high: Expression?,
        highClosed: Boolean,
    ): Interval = expressionFactory.createInterval(low, lowClosed, high, highClosed)

    fun dataTypeToQName(type: DataType?): QName = typeResolver.dataTypeToQName(type)

    internal fun dataTypesToTypeSpecifiers(
        types: kotlin.collections.List<DataType>
    ): kotlin.collections.List<TypeSpecifier> = typeResolver.dataTypesToTypeSpecifiers(types)

    fun dataTypeToTypeSpecifier(type: DataType?): TypeSpecifier =
        typeResolver.dataTypeToTypeSpecifier(type)

    fun resolvePath(sourceType: DataType?, path: String): DataType? =
        typeResolver.resolvePath(sourceType, path)

    // TODO: Support case-insensitive models
    @JvmOverloads
    fun resolveProperty(
        sourceType: DataType?,
        identifier: String,
        mustResolve: Boolean = true,
    ): PropertyResolution? = propertyResolver.resolveProperty(sourceType, identifier, mustResolve)

    /**
     * Resolve an identifier through an ordered chain of focused resolvers. An identifier may be (in
     * priority order):
     * 1. A literal in a type-specifier context
     * 2. A reference to a column of the current sort-clause's result type
     * 3. An implicit property of a `$this` alias
     * 4. The `$index` / `$total` query iteration variables
     * 5. A query alias
     * 6. A query let-clause
     * 7. A function operand
     * 8. A definition (expression / parameter / valueset / codesystem / code / concept / include)
     * 9. An implicit property of a context parameter
     *
     * If none match and [mustResolve] is true, an exception is raised.
     */
    fun resolveIdentifier(identifier: String, mustResolve: Boolean): Expression? {
        for (resolver in identifierResolvers) {
            val result = resolver(identifier)
            if (result != null) return result
        }
        // The exact-match-element step also produces the diagnostic message used when we fail.
        val resolvedIdentifierContext: ResolvedIdentifierContext = resolve(identifier)
        val elementRef = resolvedIdentifierContext.exactMatchElement?.let { buildElementRef(it) }
        if (elementRef != null) return elementRef

        val implicitContextRef = resolveImplicitContextProperty(identifier)
        if (implicitContextRef != null) return implicitContextRef

        if (mustResolve) {
            val message =
                resolvedIdentifierContext.warnCaseInsensitiveIfApplicable()
                    ?: "Could not resolve identifier $identifier in the current library."
            throw IllegalArgumentException(message)
        }
        return null
    }

    private val identifierResolvers: kotlin.collections.List<(String) -> Expression?> =
        listOf(
            ::resolveAsTypeSpecifierLiteral,
            ::resolveQueryResultElement,
            ::resolveQueryThisElement,
            ::resolveIterationVariable,
            ::resolveAliasIdentifier,
            ::resolveLetIdentifier,
            ::resolveOperandIdentifier,
        )

    private fun resolveAsTypeSpecifierLiteral(identifier: String): Expression? =
        if (scopeManager.inTypeSpecifierContext()) createLiteral(identifier) else null

    private fun resolveIterationVariable(identifier: String): Expression? =
        when (identifier) {
            "\$index" -> {
                val iteration = objectFactory.createIteration()
                iteration.resultType = resolveTypeName("System", "Integer")
                iteration
            }
            "\$total" -> {
                // TODO: This isn't right, but we don't set up a query for the Aggregate operator
                // right now...
                val total = objectFactory.createTotal()
                total.resultType = resolveTypeName("System", "Decimal")
                total
            }
            else -> null
        }

    private fun resolveAliasIdentifier(identifier: String): Expression? {
        val alias = resolveAlias(identifier) ?: return null
        val ref = objectFactory.createAliasRef().withName(identifier)
        ref.resultType =
            if (alias.resultType is ListType) (alias.resultType as ListType).elementType
            else alias.resultType
        return ref
    }

    private fun resolveLetIdentifier(identifier: String): Expression? {
        val let = resolveQueryLet(identifier) ?: return null
        val ref = objectFactory.createQueryLetRef().withName(identifier)
        ref.resultType = let.resultType
        return ref
    }

    private fun resolveOperandIdentifier(identifier: String): Expression? =
        resolveOperandRef(identifier)

    private fun resolveImplicitContextProperty(identifier: String): Expression? {
        val parameterRef = resolveImplicitContext() ?: return null
        val resolution = resolveProperty(parameterRef.resultType, identifier, false) ?: return null
        val contextAccessor =
            buildProperty(parameterRef, resolution.name, resolution.isSearch, resolution.type)
        return applyTargetMap(contextAccessor, resolution.targetMap)
    }

    private fun buildElementRef(element: Element): Expression? {
        if (element is IncludeDef) {
            checkLiteralContext()
            return LibraryRef(objectFactory.nextId(), element.localIdentifier)
        }
        return buildDefinitionRef(element, libraryName = null, memberName = null)
    }

    /**
     * An implicit context is one where the context has the same name as a parameter. Implicit
     * contexts are used to allow FHIRPath expressions to resolve on the implicit context of the
     * expression
     *
     * For example, in a Patient context, with a parameter of type Patient, the expression
     * `birthDate` resolves to a property reference.
     *
     * @return A reference to the parameter providing the implicit context value
     */
    fun resolveImplicitContext(): ParameterRef? {
        if (!scopeManager.inLiteralContext() && inSpecificContext()) {
            val resolvedIdentifierContext: ResolvedIdentifierContext =
                resolve(scopeManager.currentExpressionContext())
            val optParameterDef = resolvedIdentifierContext.getElementOfType(ParameterDef::class)
            if (optParameterDef != null) {
                val contextParameter: ParameterDef = optParameterDef
                checkLiteralContext()
                val parameterRef: ParameterRef =
                    objectFactory.createParameterRef().withName(contextParameter.name)
                parameterRef.resultType = contextParameter.resultType
                requireNotNull(parameterRef.resultType) {
                    // ERROR:
                    "Could not validate reference to parameter ${parameterRef.name} because its definition contains errors."
                }
                return parameterRef
            }
        }
        return null
    }

    @JsExport.Ignore
    fun buildProperty(
        scope: String?,
        path: String?,
        isSearch: Boolean,
        resultType: DataType?,
    ): Property = propertyResolver.buildProperty(scope, path, isSearch, resultType)

    @JsExport.Ignore
    fun buildProperty(
        source: Expression?,
        path: String?,
        isSearch: Boolean,
        resultType: DataType?,
    ): Property = propertyResolver.buildProperty(source, path, isSearch, resultType)

    internal fun ensureLibraryIncluded(libraryName: String, sourceContext: Expression?) {
        symbolTable.ensureLibraryIncluded(libraryName, sourceContext)
    }

    fun resolveAccessor(left: Expression, memberIdentifier: String): Expression? =
        propertyResolver.resolveAccessor(left, memberIdentifier)

    fun applyTargetMap(source: Expression?, targetMap: String?): Expression? =
        propertyResolver.applyTargetMap(source, targetMap)

    internal fun resolveLibraryMemberAccessor(
        left: LibraryRef,
        memberIdentifier: String,
    ): Expression {
        val libraryName: String? = left.libraryName
        val referencedLibrary: CompiledLibrary = resolveLibrary(libraryName)
        val resolvedIdentifierContext: ResolvedIdentifierContext =
            referencedLibrary.resolve(memberIdentifier)
        val element = resolvedIdentifierContext.exactMatchElement
        val ref = element?.let { buildLibraryMemberRef(libraryName, memberIdentifier, it) }
        return ref
            ?: throw IllegalArgumentException(
                "Could not resolve identifier $memberIdentifier in library ${referencedLibrary.identifier!!.id}."
            )
    }

    private fun buildLibraryMemberRef(
        libraryName: String?,
        memberIdentifier: String,
        element: Element,
    ): Expression? = buildDefinitionRef(element, libraryName, memberIdentifier)

    /**
     * Build a `*Ref` ELM node for a resolved definition.
     *
     * When [libraryName] is non-null, [memberName] must be the member identifier used to address
     * the definition in the referenced library, and an access-level check runs. When [libraryName]
     * is null the ref is built for the current library; a literal-context check runs and the result
     * type is validated to have resolved (to catch upstream definition errors).
     *
     * [IncludeDef] is handled separately by the caller — it builds a [LibraryRef] rather than a
     * `*Ref` node and only applies in the local case.
     */
    @Suppress("LongMethod", "CyclomaticComplexMethod")
    private fun buildDefinitionRef(
        element: Element,
        libraryName: String?,
        memberName: String?,
    ): Expression? {
        val isLocal = libraryName == null
        val accessChecked: (AccessModifier?) -> Unit = { access ->
            if (isLocal) checkLiteralContext()
            else checkAccessLevel(libraryName, memberName!!, access!!)
        }
        val ref: Expression =
            when (element) {
                is ExpressionDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createExpressionRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = getExpressionDefResultType(element)
                    r
                }
                is ParameterDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createParameterRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                is ValueSetDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createValueSetRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    if (isCompatibleWith("1.5")) r.preserve = true
                    r
                }
                is CodeSystemDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createCodeSystemRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                is CodeDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createCodeRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                is ConceptDef -> {
                    accessChecked(element.accessLevel)
                    val r =
                        objectFactory
                            .createConceptRef()
                            .withLibraryName(libraryName)
                            .withName(memberName ?: element.name)
                    r.resultType = element.resultType
                    r
                }
                else -> return null
            }
        if (isLocal) {
            requireNotNull(ref.resultType) {
                val name = memberName ?: refName(ref)
                "Could not validate reference to ${definitionKindLabel(element)} $name because its definition contains errors."
            }
        }
        return ref
    }

    private fun refName(ref: Expression): String? =
        when (ref) {
            is ExpressionRef -> ref.name
            is ParameterRef -> ref.name
            is ValueSetRef -> ref.name
            is CodeSystemRef -> ref.name
            is CodeRef -> ref.name
            is ConceptRef -> ref.name
            else -> null
        }

    private fun definitionKindLabel(element: Element): String =
        when (element) {
            is ExpressionDef -> "expression"
            is ParameterDef -> "parameter"
            is ValueSetDef -> "valueset"
            is CodeSystemDef -> "codesystem"
            is CodeDef -> "code"
            is ConceptDef -> "concept"
            else -> element::class.simpleName ?: "definition"
        }

    private fun resolveQueryResultElement(identifier: String): Expression? {
        if (scopeManager.inQueryContext()) {
            val query = scopeManager.peekQueryContext()
            if (query.inSortClause() && !query.isSingular) {
                if (identifier == FP_THIS) {
                    val result = objectFactory.createIdentifierRef().withName(identifier)
                    result.resultType = query.resultElementType
                    return result
                }
                val resolution = resolveProperty(query.resultElementType, identifier, false)
                if (resolution != null) {
                    val result = objectFactory.createIdentifierRef().withName(resolution.name)
                    result.resultType = resolution.type
                    return applyTargetMap(result, resolution.targetMap)
                }
            }
        }
        return null
    }

    private fun resolveAlias(identifier: String): AliasedQuerySource? {
        // Need to use a for loop to go through backwards, iteration on a Stack is bottom up
        if (scopeManager.inQueryContext()) {
            val queries = scopeManager.currentScope.queries
            for (i in queries.indices.reversed()) {
                val source = queries.elementAt(i).resolveAlias(identifier)
                if (source != null) {
                    return source
                }
            }
        }
        return null
    }

    @Suppress("NestedBlockDepth")
    private fun resolveQueryThisElement(identifier: String): Expression? {
        if (scopeManager.inQueryContext()) {
            val query = scopeManager.peekQueryContext()
            if (query.isImplicit) {
                val source = resolveAlias(FP_THIS)
                if (source != null) {
                    val aliasRef = objectFactory.createAliasRef().withName(FP_THIS)
                    if (source.resultType is ListType) {
                        aliasRef.resultType = (source.resultType as ListType).elementType
                    } else {
                        aliasRef.resultType = source.resultType
                    }
                    val result = resolveProperty(aliasRef.resultType, identifier, false)
                    if (result != null) {
                        return resolveAccessor(aliasRef, identifier)
                    }
                }
            }
        }
        return null
    }

    private fun resolveQueryLet(identifier: String): LetClause? {
        // Need to use a for loop to go through backwards, iteration on a Stack is bottom up
        if (scopeManager.inQueryContext()) {
            val queries = scopeManager.currentScope.queries
            for (i in queries.indices.reversed()) {
                val let = queries.elementAt(i).resolveLet(identifier)
                if (let != null) {
                    return let
                }
            }
        }
        return null
    }

    private fun resolveOperandRef(identifier: String): OperandRef? {
        if (!scopeManager.functionDefs.empty()) {
            for (operand in scopeManager.functionDefs.peek().operand) {
                if (operand.name == identifier) {
                    return objectFactory
                        .createOperandRef()
                        .withName(identifier)
                        .withResultType(operand.resultType)
                }
            }
        }
        return null
    }

    private fun getExpressionDefResultType(expressionDef: ExpressionDef): DataType? {
        // If the current expression context is the same as the expression def context, return the
        // expression def result type.
        if ((scopeManager.currentExpressionContext() == expressionDef.context)) {
            return expressionDef.resultType
        }

        // If the current expression context is specific, a reference to an unfiltered context
        // expression will indicate a full evaluation of the population context expression, and the
        // result type is the same.
        if (inSpecificContext()) {
            return expressionDef.resultType
        }

        // If the current expression context is unfiltered, a reference to a specific context
        // expression will need to be performed for every context in the system, so the result type
        // is promoted to a list (if it is not already).
        if (inUnfilteredContext()) {
            // If we are in the source clause of a query, indicate that the source references
            // patient context
            if (
                scopeManager.inQueryContext() &&
                    scopeManager.currentScope.queries.peek().inSourceClause()
            ) {
                scopeManager.currentScope.queries.peek().referencesSpecificContextValue = true
            }
            val resultType: DataType = expressionDef.resultType!!
            return resultType as? ListType ?: ListType(resultType)
        }
        throw IllegalArgumentException(
            "Invalid context reference from ${scopeManager.currentExpressionContext()} context to ${expressionDef.context} context."
        )
    }

    enum class IdentifierScope {
        GLOBAL,
        LOCAL,
    }

    /**
     * Add an identifier to the deque to indicate that we are considering it for consideration for
     * identifier hiding and adding a compiler warning if this is the case.
     *
     * For example, if an alias within an expression body has the same name as a parameter,
     * execution would have added the parameter identifier and the next execution would consider an
     * alias with the same name, thus resulting in a warning.
     *
     * Exact case matching as well as case-insensitive matching are considered. If known, the type
     * of the structure in question will be considered in crafting the warning message, as per the
     * [Element] parameter.
     *
     * Also, special case function overloads so that only a single overloaded function name is taken
     * into account.
     *
     * Default scope is [IdentifierScope.LOCAL]
     *
     * @param identifierRef An identifierRef representing the identifier and a trackback to its
     *   definition
     * @param element The element identified by the identifier, for example [ExpressionRef].
     * @param scope The scope of the current identifier
     */
    @JvmOverloads
    fun pushIdentifier(
        identifierRef: IdentifierRef,
        element: Element?,
        scope: IdentifierScope = IdentifierScope.LOCAL,
    ) {
        val identifier = identifierRef.name!!
        val localStack = scopeManager.localIdentifierStack
        val localMatch =
            if (localStack.isNotEmpty())
                findMatchingIdentifierContext(localStack.peek(), identifier)
            else null
        val globalMatch = findMatchingIdentifierContext(scopeManager.globalIdentifiers, identifier)
        if (globalMatch != null || localMatch != null) {
            val matchedContext = globalMatch ?: localMatch!!
            val matchedOnFunctionOverloads =
                matchedContext.trackableSubclass == FunctionDef::class && element is FunctionDef
            if (!matchedOnFunctionOverloads) {
                reportWarning(
                    resolveWarningMessage(matchedContext.identifier, identifier, element),
                    identifierRef,
                )
            }
        }
        if (shouldAddIdentifierContext(element)) {
            val trackableOrNull: KClass<out Element>? =
                if (element == null) null else element::class
            // Sometimes the underlying Trackable doesn't resolve in the calling code
            if (scope == IdentifierScope.GLOBAL) {
                scopeManager.globalIdentifiers.add(
                    IdentifierContext(identifierRef, trackableOrNull)
                )
            } else {
                scopeManager.localIdentifierStack
                    .peek()
                    .add(IdentifierContext(identifierRef, trackableOrNull))
            }
        }
    }

    private fun findMatchingIdentifierContext(
        identifierContext: Collection<IdentifierContext>,
        identifier: String,
    ): IdentifierContext? {
        return identifierContext.firstOrNull { it.identifier == identifier }
    }

    /**
     * Pop the last resolved identifier off the deque. This is needed in case of a context in which
     * an identifier falls out of scope, for an example, an alias within an expression or function
     * body.
     */
    @JvmOverloads
    fun popIdentifier(scope: IdentifierScope = IdentifierScope.LOCAL) {
        if (scope == IdentifierScope.GLOBAL) {
            scopeManager.globalIdentifiers.removeLast()
        } else {
            scopeManager.localIdentifierStack.peek().removeLast()
        }
    }

    fun pushIdentifierScope() {
        scopeManager.pushIdentifierScope()
    }

    fun popIdentifierScope() {
        scopeManager.popIdentifierScope()
    }

    private fun shouldAddIdentifierContext(element: Element?): Boolean {
        return element !is Literal
    }

    private fun resolveWarningMessage(
        matchedIdentifier: String?,
        identifierParam: String,
        element: Element?,
    ): String {
        val elementString = lookupElementWarning(element)
        return if (element is Literal) {
            "String literal '$identifierParam' matches the identifier $matchedIdentifier. Consider whether the identifier was intended instead."
        } else
            "$elementString identifier $identifierParam is hiding another identifier of the same name."
    }

    fun checkLiteralContext() {
        check(!scopeManager.inLiteralContext()) {
            "Expressions in this context must be able to be evaluated at compile-time."
        }
    }

    private fun inSpecificContext(): Boolean {
        return !inUnfilteredContext()
    }

    private fun inUnfilteredContext(): Boolean {
        return scopeManager.currentExpressionContext() == "Unfiltered" ||
            isCompatibilityLevel3 && scopeManager.currentExpressionContext() == "Population"
    }

    companion object {
        private fun lookupElementWarning(element: Any?): String {
            return when (element) {
                is ExpressionDef -> "An expression"
                is ParameterDef -> "A parameter"
                is ValueSetDef -> "A valueset"
                is CodeSystemDef -> "A codesystem"
                is CodeDef -> "A code"
                is ConceptDef -> "A concept"
                is IncludeDef -> "An include"
                is AliasedQuerySource -> "An alias"
                is LetClause -> "A let"
                is OperandDef -> "An operand"
                is UsingDef -> "A using"
                is Literal -> "A literal"
                // default message if no match is made:
                else -> "An [unknown structure]"
            }
        }
    }
}
