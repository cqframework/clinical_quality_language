@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass
import nl.adaptivity.xmlutil.QName
import org.cqframework.cql.cql2elm.model.*
import org.cqframework.cql.cql2elm.model.SystemLibraryHelper.load
import org.cqframework.cql.cql2elm.model.invocation.*
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.cql2elm.utils.Stack
import org.cqframework.cql.cql2elm.utils.getTranslatorVersion
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.elm.serializing.BigDecimal
import org.hl7.cql.model.*
import org.hl7.cql_annotations.r1.*
import org.hl7.cql_annotations.r1.ObjectFactory
import org.hl7.elm.r1.*

private const val FP_THIS = "\$this"

/** Created by Bryn on 12/29/2016. */
@Suppress("LargeClass", "TooManyFunctions", "ForbiddenComment", "ReturnCount", "MaxLineLength")
class LibraryBuilder(
    @JvmField
    val namespaceInfo: NamespaceInfo?, // Note: allowed to be null, implies global namespace
    val libraryManager: CommonLibraryManager,
    val objectFactory: IdObjectFactory
) {
    enum class SignatureLevel {
        /*
        Indicates signatures will never be included in operator invocations
         */
        None,

        /*
        Indicates signatures will only be included in invocations if the declared signature of the resolve operator is different from the invocation signature
         */
        Differing,

        /*
        Indicates signatures will only be included in invocations if the function has multiple overloads with the same number of arguments as the invocation
         */
        Overloads,

        /*
        Indicates signatures will always be included in invocations
         */
        All
    }

    constructor(
        libraryManager: CommonLibraryManager,
        objectFactory: IdObjectFactory
    ) : this(null, libraryManager, objectFactory)

    // Only exceptions of severity Error
    val errors: MutableList<CqlCompilerException> = ArrayList()

    // Only exceptions of severity Warning
    val warnings: MutableList<CqlCompilerException> = ArrayList()

    // Only exceptions of severity Info
    val messages: MutableList<CqlCompilerException> = ArrayList()

    // All exceptions
    val exceptions: MutableList<CqlCompilerException> = ArrayList()

    private val models: MutableMap<String, Model?> = LinkedHashMap()
    private val nameTypeSpecifiers:
        MutableMap<String, ResultWithPossibleError<NamedTypeSpecifier?>> =
        HashMap()
    private val libraries: MutableMap<String, CompiledLibrary> = LinkedHashMap()
    private val systemFunctionResolver: SystemFunctionResolver = SystemFunctionResolver(this)
    private val expressionContext = Stack<String>()
    private val expressionDefinitions = ExpressionDefinitionContextStack()
    private val functionDefs = Stack<FunctionDef>()
    private val globalIdentifiers = ArrayDeque<IdentifierContext>()
    private val localIdentifierStack = Stack<ArrayDeque<IdentifierContext>>()
    private var literalContext = 0
    private var typeSpecifierContext = 0
    private val modelManager: CommonModelManager = libraryManager.modelManager
    var defaultModel: Model? = null
        private set(model) {
            // The default model is the first model that is not System
            if (field == null && model?.modelInfo?.name != "System") {
                field = model
            }
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
    @JvmField var compiledLibrary = CompiledLibrary()
    @JvmField val conversionMap = ConversionMap()
    private val af = ObjectFactory()
    private var listTraversal = true
    private val options: CqlCompilerOptions = libraryManager.cqlCompilerOptions
    protected val cqlToElmInfo = af.createCqlToElmInfo()
    private val typeBuilder = TypeBuilder(objectFactory, modelManager)

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

    /*
    A "well-known" model name is one that is allowed to resolve without a namespace in a namespace-aware context
     */
    fun isWellKnownModelName(unqualifiedIdentifier: String?): Boolean {
        return if (namespaceInfo == null) {
            false
        } else modelManager.isWellKnownModelName(unqualifiedIdentifier)
    }

    /*
    A "well-known" library name is a library name that is allowed to resolve without a namespace in a namespace-aware context
     */
    fun isWellKnownLibraryName(unqualifiedIdentifier: String?): Boolean {
        return if (namespaceInfo == null) {
            false
        } else libraryManager.isWellKnownLibraryName(unqualifiedIdentifier)
    }

    private fun loadModel(modelIdentifier: ModelIdentifier): Model {
        val model = modelManager.resolveModel(modelIdentifier)
        loadConversionMap(model)
        return model
    }

    fun getModel(modelIdentifier: ModelIdentifier, localIdentifier: String): Model {
        var model: Model? = models[localIdentifier]
        if (model == null) {
            model = loadModel(modelIdentifier)
            defaultModel = model
            models[localIdentifier] = model
            // Add the model using def to the output
            buildUsingDef(modelIdentifier, model, localIdentifier)
        }
        require(
            !(modelIdentifier.version != null && modelIdentifier.version != model.modelInfo.version)
        ) {
            "Could not load model information for model ${modelIdentifier.id}, version ${modelIdentifier.version} because version ${model.modelInfo.version} is already loaded."
        }
        return model
    }

    fun getNamedTypeSpecifierResult(
        namedTypeSpecifierIdentifier: String
    ): ResultWithPossibleError<NamedTypeSpecifier?>? {
        return nameTypeSpecifiers[namedTypeSpecifierIdentifier]
    }

    fun addNamedTypeSpecifierResult(
        namedTypeSpecifierIdentifier: String,
        namedTypeSpecifierResult: ResultWithPossibleError<NamedTypeSpecifier?>
    ) {
        if (!nameTypeSpecifiers.containsKey(namedTypeSpecifierIdentifier)) {
            nameTypeSpecifiers[namedTypeSpecifierIdentifier] = namedTypeSpecifierResult
        }
    }

    private fun loadConversionMap(model: Model?) {
        for (conversion in model!!.getConversions()) {
            conversionMap.add(conversion)
        }
    }

    private fun buildUsingDef(
        modelIdentifier: ModelIdentifier,
        model: Model?,
        localIdentifier: String
    ): UsingDef {
        val usingDef =
            objectFactory
                .createUsingDef()
                .withLocalIdentifier(localIdentifier)
                .withVersion(modelIdentifier.version)
                .withUri(model!!.modelInfo.url)
        // TODO: Needs to write xmlns and schemalocation to the resulting ELM XML document...
        addUsing(usingDef)
        return usingDef
    }

    fun hasUsings(): Boolean {
        for (model in models.values) {
            if (model!!.modelInfo.name != "System") {
                return true
            }
        }
        return false
    }

    private fun addUsing(usingDef: UsingDef) {
        if (library.usings == null) {
            library.usings = objectFactory.createLibraryUsings()
        }
        library.usings!!.def.add(usingDef)
        compiledLibrary.add(usingDef)
    }

    @Suppress("NestedBlockDepth")
    private fun resolveLabel(modelName: String?, label: String): ClassType? {
        var result: ClassType? = null
        if (modelName == null || (modelName == "")) {
            for (model: Model? in models.values) {
                val modelResult: ClassType? = model!!.resolveLabel(label)
                if (modelResult != null) {
                    require(result == null) {
                        "Label $label is ambiguous between ${(result as ClassType).label} and ${modelResult.label}."
                    }
                    result = modelResult
                }
            }
        } else {
            result = getModel(modelName).resolveLabel(label)
        }
        return result
    }

    @Suppress("NestedBlockDepth")
    fun resolveContextName(modelName: String?, contextName: String): ModelContext? {
        // Attempt to resolve as a label first
        var result: ModelContext? = null
        if (modelName == null || (modelName == "")) {
            // Attempt to resolve in the default model if one is available
            if (defaultModel != null) {
                val modelResult: ModelContext? = defaultModel!!.resolveContextName(contextName)
                if (modelResult != null) {
                    return modelResult
                }
            }

            // Otherwise, resolve across all models and throw for ambiguous resolution
            for (model: Model? in models.values) {
                val modelResult: ModelContext? = model!!.resolveContextName(contextName)
                if (modelResult != null) {
                    require(result == null) {
                        "Context name $contextName is ambiguous between ${(result as ModelContext).name} and ${modelResult.name}."
                    }
                    result = modelResult
                }
            }
        } else {
            result = getModel(modelName).resolveContextName(contextName)
        }
        return result
    }

    fun resolveTypeName(typeName: String): DataType? {
        return resolveTypeName(null, typeName)
    }

    @Suppress("NestedBlockDepth")
    fun resolveTypeName(modelName: String?, typeName: String): DataType? {
        // Attempt to resolve as a label first
        var result: DataType? = resolveLabel(modelName, typeName)
        if (result == null) {
            if (modelName == null || (modelName == "")) {
                // Attempt to resolve in the default model if one is available
                if (defaultModel != null) {
                    val modelResult: DataType? = defaultModel!!.resolveTypeName(typeName)
                    if (modelResult != null) {
                        return modelResult
                    }
                }

                // Otherwise, resolve across all models and throw for ambiguous resolution
                for (model: Model? in models.values) {
                    val modelResult: DataType? = model!!.resolveTypeName(typeName)
                    if (modelResult != null) {
                        require(result == null) {
                            "Type name $typeName is ambiguous between ${(result as NamedType).name} and ${(modelResult as NamedType).name}."
                        }
                        result = modelResult
                    }
                }
            } else {
                result = getModel(modelName).resolveTypeName(typeName)
            }
        }

        // Types introduced in 1.5: Long, Vocabulary, ValueSet, CodeSystem
        if (result != null && result is NamedType) {
            when ((result as NamedType).name) {
                "System.Long",
                "System.Vocabulary",
                "System.CodeSystem",
                "System.ValueSet" -> // NOTE: This is a hack to allow the new ToValueSet operator in
                    // FHIRHelpers for
                    // backwards-compatibility
                    // The operator still cannot be used in 1.4, but the definition will compile.
                    // This really should be
                    // being done with preprocessor directives,
                    // but that's a whole other project in and of itself.
                    require(!(!isCompatibleWith("1.5") && !isFHIRHelpers(compiledLibrary))) {
                        "The type ${(result as NamedType).name} was introduced in CQL 1.5 and cannot be referenced at compatibility level $compatibilityLevel"
                    }
            }
        }
        return result
    }

    private fun isFHIRHelpers(library: CompiledLibrary?): Boolean {
        return (library != null) &&
            (library.identifier != null) &&
            (library.identifier!!.id != null) &&
            (library.identifier!!.id == "FHIRHelpers")
    }

    fun resolveTypeSpecifier(typeSpecifier: String?): DataType? {
        requireNotNull(typeSpecifier) { "typeSpecifier is null" }

        // typeSpecifier: simpleTypeSpecifier | intervalTypeSpecifier | listTypeSpecifier
        // simpleTypeSpecifier: (identifier '.')? identifier
        // intervalTypeSpecifier: 'interval' '<' typeSpecifier '>'
        // listTypeSpecifier: 'list' '<' typeSpecifier '>'
        return when {
            typeSpecifier.lowercase().startsWith("interval<") -> {
                val pointType =
                    resolveTypeSpecifier(
                        typeSpecifier.substring(
                            typeSpecifier.indexOf('<') + 1,
                            typeSpecifier.lastIndexOf('>')
                        )
                    )
                IntervalType(pointType!!)
            }
            else ->
                if (typeSpecifier.lowercase().startsWith("list<")) {
                    val elementType =
                        resolveTypeName(
                            typeSpecifier.substring(
                                typeSpecifier.indexOf('<') + 1,
                                typeSpecifier.lastIndexOf('>')
                            )
                        )
                    ListType(elementType!!)
                } else if (typeSpecifier.indexOf(".") >= 0) {
                    val modelName = typeSpecifier.substring(0, typeSpecifier.indexOf("."))
                    val typeName = typeSpecifier.substring(typeSpecifier.indexOf(".") + 1)
                    resolveTypeName(modelName, typeName)
                } else {
                    resolveTypeName(typeSpecifier)
                }
        }
    }

    fun resolveUsingRef(modelName: String): UsingDef? {
        return compiledLibrary.resolveUsingRef(modelName)
    }

    val systemModel: SystemModel
        get() = // TODO: Support loading different versions of the system library
        getModel(ModelIdentifier("System"), "System") as SystemModel

    fun getModel(modelName: String): Model {
        val usingDef = resolveUsingRef(modelName)
        if (usingDef == null && modelName == "FHIR") {
            // Special case for FHIR-derived models that include FHIR Helpers
            return modelManager.resolveModelByUri("http://hl7.org/fhir")
        }
        requireNotNull(usingDef) { "Could not resolve model name $modelName" }
        return getModel(usingDef)
    }

    private fun getModel(usingDef: UsingDef): Model {
        return getModel(
            ModelIdentifier(
                id = NamespaceManager.getNamePart(usingDef.uri)!!,
                system = NamespaceManager.getUriPart(usingDef.uri),
                version = usingDef.version
            ),
            usingDef.localIdentifier!!
        )
    }

    private fun loadSystemLibrary() {
        val systemLibrary = load(systemModel, typeBuilder)
        libraries[systemLibrary.identifier!!.id!!] = systemLibrary
        loadConversionMap(systemLibrary)
    }

    private fun loadConversionMap(library: CompiledLibrary) {
        for (conversion in library.getConversions()) {
            conversionMap.add(conversion)
        }
    }

    private val systemLibrary: CompiledLibrary
        get() = resolveLibrary("System")

    fun resolveLibrary(identifier: String?): CompiledLibrary {
        if (identifier != "System") {
            checkLiteralContext()
        }
        return libraries[identifier]
            ?: throw IllegalArgumentException("Could not resolve library name $identifier.")
    }

    fun resolveNamespaceUri(namespaceName: String, mustResolve: Boolean): String? {
        val namespaceUri = libraryManager.namespaceManager.resolveNamespaceUri(namespaceName)
        require(!(namespaceUri == null && mustResolve)) {
            "Could not resolve namespace name $namespaceName"
        }
        return namespaceUri
    }

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
            err.errorType =
                if (e is CqlSyntaxException) ErrorType.SYNTAX
                else (if (e is CqlSemanticException) ErrorType.SEMANTIC else ErrorType.INTERNAL)
            err.errorSeverity = toErrorSeverity(e.severity)
            if (e.locator != null) {
                if (e.locator.library != null) {
                    err.librarySystem = e.locator.library.system
                    err.libraryId = e.locator.library.id
                    err.libraryVersion = e.locator.library.version
                }
                err.startLine = e.locator.startLine
                err.endLine = e.locator.endLine
                err.startChar = e.locator.startChar
                err.endChar = e.locator.endChar
            }
            if (e.cause != null && e.cause is CqlIncludeException) {
                val incEx = e.cause as CqlIncludeException?
                err.targetIncludeLibrarySystem = incEx!!.librarySystem
                err.targetIncludeLibraryId = incEx.libraryId
                err.targetIncludeLibraryVersionId = incEx.versionId
                err.errorType = ErrorType.INCLUDE
            }
            library.annotation.add(err)
        }
    }

    fun beginTranslation() {
        loadSystemLibrary()
    }

    var libraryIdentifier: VersionedIdentifier?
        get() = library.identifier
        set(vid) {
            library.identifier = vid
            compiledLibrary.identifier = vid
        }

    fun endTranslation() {
        applyTargetModelMaps()
    }

    fun canResolveLibrary(includeDef: IncludeDef): Boolean {
        val libraryIdentifier =
            VersionedIdentifier()
                .withSystem(NamespaceManager.getUriPart(includeDef.path))
                .withId(NamespaceManager.getNamePart(includeDef.path))
                .withVersion(includeDef.version)
        return libraryManager.canResolveLibrary(libraryIdentifier)
    }

    fun addInclude(includeDef: IncludeDef) {
        require(!(library.identifier == null || library.identifier!!.id == null)) {
            "Unnamed libraries cannot reference other libraries."
        }
        if (library.includes == null) {
            library.includes = objectFactory.createLibraryIncludes()
        }
        library.includes!!.def.add(includeDef)
        compiledLibrary.add(includeDef)
        val libraryIdentifier =
            VersionedIdentifier()
                .withSystem(NamespaceManager.getUriPart(includeDef.path))
                .withId(NamespaceManager.getNamePart(includeDef.path))
                .withVersion(includeDef.version)
        val errors = ArrayList<CqlCompilerException>()
        val referencedLibrary = libraryManager.resolveLibrary(libraryIdentifier, errors)
        for (error in errors) {
            recordParsingException(error)
        }

        // Note that translation of a referenced library may result in implicit specification of the
        // namespace
        // In this case, the referencedLibrary will have a namespaceUri different from the currently
        // resolved
        // namespaceUri
        // of the IncludeDef.
        val currentNamespaceUri = NamespaceManager.getUriPart(includeDef.path)
        @Suppress("ComplexCondition")
        if (
            currentNamespaceUri == null && libraryIdentifier.system != null ||
                currentNamespaceUri != null && currentNamespaceUri != libraryIdentifier.system
        ) {
            includeDef.path =
                NamespaceManager.getPath(libraryIdentifier.system, libraryIdentifier.id!!)
        }
        libraries[includeDef.localIdentifier!!] = referencedLibrary
        loadConversionMap(referencedLibrary)
    }

    fun addParameter(paramDef: ParameterDef) {
        if (library.parameters == null) {
            library.parameters = objectFactory.createLibraryParameters()
        }
        library.parameters!!.def.add(paramDef)
        compiledLibrary.add(paramDef)
    }

    fun addCodeSystem(cs: CodeSystemDef) {
        if (library.codeSystems == null) {
            library.codeSystems = objectFactory.createLibraryCodeSystems()
        }
        library.codeSystems!!.def.add(cs)
        compiledLibrary.add(cs)
    }

    fun addValueSet(vs: ValueSetDef) {
        if (library.valueSets == null) {
            library.valueSets = objectFactory.createLibraryValueSets()
        }
        library.valueSets!!.def.add(vs)
        compiledLibrary.add(vs)
    }

    fun addCode(cd: CodeDef) {
        if (library.codes == null) {
            library.codes = objectFactory.createLibraryCodes()
        }
        library.codes!!.def.add(cd)
        compiledLibrary.add(cd)
    }

    fun addConcept(cd: ConceptDef) {
        if (library.concepts == null) {
            library.concepts = objectFactory.createLibraryConcepts()
        }
        library.concepts!!.def.add(cd)
        compiledLibrary.add(cd)
    }

    fun addContext(cd: ContextDef) {
        if (library.contexts == null) {
            library.contexts = objectFactory.createLibraryContexts()
        }
        library.contexts!!.def.add(cd)
    }

    fun addExpression(expDef: ExpressionDef) {
        if (library.statements == null) {
            library.statements = objectFactory.createLibraryStatements()
        }
        library.statements!!.def.add(expDef)
        compiledLibrary.add(expDef)
    }

    fun removeExpression(expDef: ExpressionDef) {
        if (library.statements != null) {
            library.statements!!.def.remove(expDef)
            compiledLibrary.remove(expDef)
        }
    }

    fun resolve(identifier: String): ResolvedIdentifierContext {
        return compiledLibrary.resolve(identifier)
    }

    fun resolveIncludeRef(identifier: String): IncludeDef? {
        return compiledLibrary.resolveIncludeRef(identifier)
    }

    private fun resolveIncludeAlias(libraryIdentifier: VersionedIdentifier): String? {
        return compiledLibrary.resolveIncludeAlias(libraryIdentifier)
    }

    fun resolveCodeSystemRef(identifier: String): CodeSystemDef? {
        return compiledLibrary.resolveCodeSystemRef(identifier)
    }

    fun resolveValueSetRef(identifier: String): ValueSetDef? {
        return compiledLibrary.resolveValueSetRef(identifier)
    }

    fun resolveCodeRef(identifier: String): CodeDef? {
        return compiledLibrary.resolveCodeRef(identifier)
    }

    fun resolveConceptRef(identifier: String): ConceptDef? {
        return compiledLibrary.resolveConceptRef(identifier)
    }

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
        allowPromotionAndDemotion: Boolean
    ): Conversion? {
        return conversionMap.findConversion(
            fromType,
            toType,
            implicit,
            allowPromotionAndDemotion,
            compiledLibrary.operatorMap
        )
    }

    fun resolveUnaryCall(
        libraryName: String?,
        operatorName: String,
        expression: UnaryExpression
    ): Expression? {
        return resolveCall(
            libraryName,
            operatorName,
            UnaryExpressionInvocation(expression),
            allowPromotionAndDemotion = false,
            allowFluent = false
        )
    }

    fun resolveBinaryCall(
        libraryName: String?,
        operatorName: String,
        expression: BinaryExpression
    ): Expression? {
        val invocation = resolveBinaryInvocation(libraryName, operatorName, expression)
        return invocation?.expression
    }

    @JvmOverloads
    fun resolveBinaryInvocation(
        libraryName: String?,
        operatorName: String,
        expression: BinaryExpression,
        mustResolve: Boolean = true,
        allowPromotionAndDemotion: Boolean = false
    ): Invocation? {
        return resolveInvocation(
            libraryName,
            operatorName,
            BinaryExpressionInvocation(expression),
            mustResolve,
            allowPromotionAndDemotion,
            false
        )
    }

    fun resolveBinaryCall(
        libraryName: String?,
        operatorName: String,
        expression: BinaryExpression,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean
    ): Expression? {
        val invocation =
            resolveBinaryInvocation(
                libraryName,
                operatorName,
                expression,
                mustResolve,
                allowPromotionAndDemotion
            )
        return invocation?.expression
    }

    fun resolveTernaryCall(
        libraryName: String?,
        operatorName: String,
        expression: TernaryExpression
    ): Expression? {
        return resolveCall(
            libraryName,
            operatorName,
            TernaryExpressionInvocation(expression),
            allowPromotionAndDemotion = false,
            allowFluent = false
        )
    }

    fun resolveNaryCall(
        libraryName: String?,
        operatorName: String,
        expression: NaryExpression?
    ): Expression? {
        return resolveCall(
            libraryName,
            operatorName,
            NaryExpressionInvocation(expression!!),
            allowPromotionAndDemotion = false,
            allowFluent = false
        )
    }

    fun resolveAggregateCall(
        libraryName: String?,
        operatorName: String,
        expression: AggregateExpression
    ): Expression? {
        return resolveCall(
            libraryName,
            operatorName,
            AggregateExpressionInvocation(expression),
            allowPromotionAndDemotion = false,
            allowFluent = false
        )
    }

    private inner class BinaryWrapper(var left: Expression, var right: Expression)

    @Suppress("NestedBlockDepth")
    private fun normalizeListTypes(left: Expression, right: Expression): BinaryWrapper {
        // for union of lists
        // collect list of types in either side
        // cast both operands to a choice type with all types

        // for intersect of lists
        // collect list of types in both sides
        // cast both operands to a choice type with all types
        // TODO: cast the result to a choice type with only types in both sides

        // for difference of lists
        // collect list of types in both sides
        // cast both operands to a choice type with all types
        // TODO: cast the result to the initial type of the left
        var left = left
        var right = right
        if (left.resultType is ListType && right.resultType is ListType) {
            val leftListType = left.resultType as ListType
            val rightListType = right.resultType as ListType
            @Suppress("ComplexCondition")
            if (
                !(leftListType.isSuperTypeOf(rightListType) ||
                    rightListType.isSuperTypeOf(leftListType)) &&
                    !(leftListType.isCompatibleWith(rightListType) ||
                        rightListType.isCompatibleWith(leftListType))
            ) {
                val elementTypes: MutableSet<DataType> = HashSet()
                if (leftListType.elementType is ChoiceType) {
                    for (choice in (leftListType.elementType as ChoiceType).types) {
                        elementTypes.add(choice)
                    }
                } else {
                    elementTypes.add(leftListType.elementType)
                }
                if (rightListType.elementType is ChoiceType) {
                    for (choice in (rightListType.elementType as ChoiceType).types) {
                        elementTypes.add(choice)
                    }
                } else {
                    elementTypes.add(rightListType.elementType)
                }
                if (elementTypes.size > 1) {
                    val targetType = ListType(ChoiceType(elementTypes))
                    left =
                        objectFactory
                            .createAs()
                            .withOperand(left)
                            .withAsTypeSpecifier(dataTypeToTypeSpecifier(targetType))
                    left.resultType = targetType
                    right =
                        objectFactory
                            .createAs()
                            .withOperand(right)
                            .withAsTypeSpecifier(dataTypeToTypeSpecifier(targetType))
                    right.resultType = targetType
                }
            }
        }
        return BinaryWrapper(left, right)
    }

    fun resolveUnion(left: Expression, right: Expression): Expression {
        // Create right-leaning bushy instead of left-deep
        var left = left
        var right = right
        if (left is Union) {
            val leftUnion = left
            val leftUnionLeft = leftUnion.operand[0]
            val leftUnionRight = leftUnion.operand[1]
            if (leftUnionLeft is Union && leftUnionRight !is Union) {
                left = leftUnionLeft
                right = resolveUnion(leftUnionRight, right)
            }
        }

        // TODO: Take advantage of nary unions
        val wrapper = normalizeListTypes(left, right)
        val union = objectFactory.createUnion().withOperand(wrapper.left, wrapper.right)
        resolveNaryCall("System", "Union", union)
        return union
    }

    fun resolveIntersect(left: Expression, right: Expression): Expression {
        // Create right-leaning bushy instead of left-deep
        var left = left
        var right = right
        if (left is Intersect) {
            val leftIntersect = left
            val leftIntersectLeft = leftIntersect.operand[0]
            val leftIntersectRight = leftIntersect.operand[1]
            if (leftIntersectLeft is Intersect && leftIntersectRight !is Intersect) {
                left = leftIntersectLeft
                right = resolveIntersect(leftIntersectRight, right)
            }
        }

        // TODO: Take advantage of nary intersect
        val wrapper = normalizeListTypes(left, right)
        val intersect = objectFactory.createIntersect().withOperand(wrapper.left, wrapper.right)
        resolveNaryCall("System", "Intersect", intersect)
        return intersect
    }

    fun resolveExcept(left: Expression, right: Expression): Expression {
        val wrapper = normalizeListTypes(left, right)
        val except = objectFactory.createExcept().withOperand(wrapper.left, wrapper.right)
        resolveNaryCall("System", "Except", except)
        return except
    }

    @Suppress("CyclomaticComplexMethod")
    fun resolveIn(left: Expression, right: Expression): Expression {
        @Suppress("ComplexCondition")
        if (
            right is ValueSetRef ||
                (isCompatibleWith("1.5") &&
                    right.resultType!!.isCompatibleWith(resolveTypeName("System", "ValueSet")!!) &&
                    right.resultType != resolveTypeName("System", "Any"))
        ) {
            if (left.resultType is ListType) {
                val anyIn =
                    objectFactory
                        .createAnyInValueSet()
                        .withCodes(left)
                        .withValueset(if (right is ValueSetRef) right else null)
                        .withValuesetExpression(if (right is ValueSetRef) null else right)
                resolveCall("System", "AnyInValueSet", AnyInValueSetInvocation(anyIn))
                return anyIn
            }
            val inValueSet =
                objectFactory
                    .createInValueSet()
                    .withCode(left)
                    .withValueset(if (right is ValueSetRef) right else null)
                    .withValuesetExpression(if (right is ValueSetRef) null else right)
            resolveCall("System", "InValueSet", InValueSetInvocation(inValueSet))
            return inValueSet
        }
        @Suppress("ComplexCondition")
        if (
            right is CodeSystemRef ||
                (isCompatibleWith("1.5") &&
                    right.resultType!!.isCompatibleWith(
                        resolveTypeName("System", "CodeSystem")!!
                    ) &&
                    right.resultType != resolveTypeName("System", "Any"))
        ) {
            if (left.resultType is ListType) {
                val anyIn =
                    objectFactory
                        .createAnyInCodeSystem()
                        .withCodes(left)
                        .withCodesystem(if (right is CodeSystemRef) right else null)
                        .withCodesystemExpression(if (right is CodeSystemRef) null else right)
                resolveCall("System", "AnyInCodeSystem", AnyInCodeSystemInvocation(anyIn))
                return anyIn
            }
            val inCodeSystem =
                objectFactory
                    .createInCodeSystem()
                    .withCode(left)
                    .withCodesystem(if (right is CodeSystemRef) right else null)
                    .withCodesystemExpression(if (right is CodeSystemRef) null else right)
            resolveCall("System", "InCodeSystem", InCodeSystemInvocation(inCodeSystem))
            return inCodeSystem
        }
        val inExpression = objectFactory.createIn().withOperand(left, right)
        resolveBinaryCall("System", "In", inExpression)
        return inExpression
    }

    fun resolveContains(left: Expression, right: Expression): Expression {
        // TODO: Add terminology overloads
        val contains = objectFactory.createContains().withOperand(left, right)
        resolveBinaryCall("System", "Contains", contains)
        return contains
    }

    fun resolveIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val result = resolveInInvocation(left, right, dateTimePrecision)
        return result?.expression
    }

    private fun resolveInInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Invocation? {
        val inExpression =
            objectFactory.createIn().withOperand(left, right).withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "In", inExpression)
    }

    fun resolveProperIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val result = resolveProperInInvocation(left, right, dateTimePrecision)
        return result?.expression
    }

    private fun resolveProperInInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Invocation? {
        val properIn =
            objectFactory.createProperIn().withOperand(left, right).withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "ProperIn", properIn)
    }

    fun resolveContains(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val result = resolveContainsInvocation(left, right, dateTimePrecision)
        return result?.expression
    }

    private fun resolveContainsInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Invocation? {
        val contains =
            objectFactory.createContains().withOperand(left, right).withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "Contains", contains)
    }

    fun resolveProperContains(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val result = resolveProperContainsInvocation(left, right, dateTimePrecision)
        return result?.expression
    }

    private fun resolveProperContainsInvocation(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Invocation? {
        val properContains =
            objectFactory
                .createProperContains()
                .withOperand(left, right)
                .withPrecision(dateTimePrecision)
        return resolveBinaryInvocation("System", "ProperContains", properContains)
    }

    private fun getTypeScore(resolution: OperatorResolution?): Int {
        var typeScore = ConversionMap.ConversionScore.ExactMatch.score
        for (operand in resolution!!.operator.signature.operandTypes) {
            typeScore += ConversionMap.getTypePrecedenceScore(operand)
        }
        return typeScore
    }

    @Suppress("NestedBlockDepth")
    private fun lowestScoringInvocation(primary: Invocation?, secondary: Invocation?): Expression? {
        if (primary != null) {
            if (secondary != null) {
                if (secondary.resolution!!.score < primary.resolution!!.score) {
                    return secondary.expression
                } else if (primary.resolution!!.score < secondary.resolution!!.score) {
                    return primary.expression
                }
                if (primary.resolution!!.score == secondary.resolution!!.score) {
                    val primaryTypeScore = getTypeScore(primary.resolution)
                    val secondaryTypeScore = getTypeScore(secondary.resolution)
                    return if (secondaryTypeScore < primaryTypeScore) {
                        secondary.expression
                    } else if (primaryTypeScore < secondaryTypeScore) {
                        primary.expression
                    } else {
                        // ERROR:
                        val message =
                            StringBuilder("Call to operator ")
                                .append(primary.resolution!!.operator.name)
                                .append("/")
                                .append(secondary.resolution!!.operator.name)
                                .append(" is ambiguous with: ")
                                .append("\n  - ")
                                .append(primary.resolution!!.operator.name)
                                .append(primary.resolution!!.operator.signature)
                                .append("\n  - ")
                                .append(secondary.resolution!!.operator.name)
                                .append(secondary.resolution!!.operator.signature)
                        throw IllegalArgumentException(message.toString())
                    }
                }
            }
            return primary.expression
        }
        return secondary?.expression
    }

    fun resolveIncludes(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val includes =
            objectFactory.createIncludes().withOperand(left, right).withPrecision(dateTimePrecision)
        val includesInvocation =
            resolveBinaryInvocation(
                "System",
                "Includes",
                includes,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        val contains =
            objectFactory.createContains().withOperand(left, right).withPrecision(dateTimePrecision)
        val containsInvocation =
            resolveBinaryInvocation(
                "System",
                "Contains",
                contains,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        return lowestScoringInvocation(includesInvocation, containsInvocation)
            ?: resolveBinaryCall("System", "Includes", includes)

        // Neither operator resolved, so force a resolve to throw
    }

    fun resolveProperIncludes(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val properIncludes =
            objectFactory
                .createProperIncludes()
                .withOperand(left, right)
                .withPrecision(dateTimePrecision)
        val properIncludesInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperIncludes",
                properIncludes,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        val properContains =
            objectFactory
                .createProperContains()
                .withOperand(left, right)
                .withPrecision(dateTimePrecision)
        val properContainsInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperContains",
                properContains,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        return lowestScoringInvocation(properIncludesInvocation, properContainsInvocation)
            ?: resolveBinaryCall("System", "ProperIncludes", properIncludes)

        // Neither operator resolved, so force a resolve to throw
    }

    fun resolveIncludedIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val includedIn =
            objectFactory
                .createIncludedIn()
                .withOperand(left, right)
                .withPrecision(dateTimePrecision)
        val includedInInvocation =
            resolveBinaryInvocation(
                "System",
                "IncludedIn",
                includedIn,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        val inExpression =
            objectFactory.createIn().withOperand(left, right).withPrecision(dateTimePrecision)
        val inInvocation =
            resolveBinaryInvocation(
                "System",
                "In",
                inExpression,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        return lowestScoringInvocation(includedInInvocation, inInvocation)
            ?: resolveBinaryCall("System", "IncludedIn", includedIn)

        // Neither operator resolved, so force a resolve to throw
    }

    fun resolveProperIncludedIn(
        left: Expression,
        right: Expression,
        dateTimePrecision: DateTimePrecision?
    ): Expression? {
        val properIncludedIn =
            objectFactory
                .createProperIncludedIn()
                .withOperand(left, right)
                .withPrecision(dateTimePrecision)
        val properIncludedInInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperIncludedIn",
                properIncludedIn,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        val properIn =
            objectFactory.createProperIn().withOperand(left, right).withPrecision(dateTimePrecision)
        val properInInvocation =
            resolveBinaryInvocation(
                "System",
                "ProperIn",
                properIn,
                mustResolve = false,
                allowPromotionAndDemotion = false
            )
        return lowestScoringInvocation(properIncludedInInvocation, properInInvocation)
            ?: resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn)

        // Neither operator resolved, so force a resolve to throw
    }

    fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation
    ): Expression? {
        return resolveCall(
            libraryName,
            operatorName,
            invocation,
            mustResolve = true,
            allowPromotionAndDemotion = false,
            allowFluent = false
        )
    }

    private fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean
    ): Expression? {
        return resolveCall(
            libraryName,
            operatorName,
            invocation,
            true,
            allowPromotionAndDemotion,
            allowFluent
        )
    }

    @Suppress("LongParameterList")
    private fun resolveCall(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean
    ): Expression? {
        val result =
            resolveInvocation(
                libraryName,
                operatorName,
                invocation,
                mustResolve,
                allowPromotionAndDemotion,
                allowFluent
            )
        return result?.expression
    }

    fun resolveInvocation(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean
    ): Invocation? {
        return resolveInvocation(
            libraryName,
            operatorName,
            invocation,
            true,
            allowPromotionAndDemotion,
            allowFluent
        )
    }

    @Suppress("LongParameterList")
    private fun buildCallContext(
        libraryName: String?,
        operatorName: String,
        operands: Iterable<Expression?>,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean
    ): CallContext {
        val dataTypes: MutableList<DataType> = ArrayList()
        for (operand in operands) {
            require(!(operand == null || operand.resultType == null)) {
                "Could not determine signature for invocation of operator ${if (libraryName == null) "" else "$libraryName."}$operatorName."
            }
            dataTypes.add(operand.resultType!!)
        }
        return CallContext(
            libraryName,
            operatorName,
            allowPromotionAndDemotion,
            allowFluent,
            mustResolve,
            dataTypes
        )
    }

    @Suppress("LongParameterList", "LongMethod")
    fun resolveInvocation(
        libraryName: String?,
        operatorName: String,
        invocation: Invocation,
        mustResolve: Boolean = true,
        allowPromotionAndDemotion: Boolean = false,
        allowFluent: Boolean = false
    ): Invocation? {
        val operands: Iterable<Expression> = invocation.operands
        val callContext =
            buildCallContext(
                libraryName,
                operatorName,
                operands,
                mustResolve,
                allowPromotionAndDemotion,
                allowFluent
            )
        val resolution = resolveCall(callContext)
        if (resolution == null && !mustResolve) {
            return null
        }
        checkOperator(callContext, resolution)
        val convertedOperands: MutableList<Expression> = ArrayList()
        val operandIterator = operands.iterator()
        val signatureTypes = resolution!!.operator.signature.operandTypes.iterator()
        val conversionIterator =
            if (resolution.hasConversions()) resolution.conversions.iterator() else null
        while (operandIterator.hasNext()) {
            var operand = operandIterator.next()
            val conversion = conversionIterator?.next()
            if (conversion != null) {
                operand = convertExpression(operand, conversion)
            }
            val signatureType = signatureTypes.next()
            operand = pruneChoices(operand, signatureType)
            convertedOperands.add(operand)
        }
        invocation.operands = convertedOperands
        @Suppress("ComplexCondition")
        if (
            options.signatureLevel == SignatureLevel.All ||
                (options.signatureLevel == SignatureLevel.Differing &&
                    resolution.operator.signature != callContext.signature) ||
                options.signatureLevel == SignatureLevel.Overloads &&
                    resolution.operatorHasOverloads
        ) {
            invocation.signature =
                dataTypesToTypeSpecifiers(resolution.operator.signature.operandTypes)
        } else if (resolution.operatorHasOverloads && resolution.operator.libraryName != "System") {
            // NOTE: Because system functions only deal with CQL system-defined types, and there is
            // one and only one
            // runtime representation of each system-defined type, there is no possibility of
            // ambiguous overload
            // resolution with system functions
            // WARNING:
            reportWarning(
                """
                    The function ${resolution.operator.libraryName}.${resolution.operator.name} has multiple overloads
                    and due to the SignatureLevel setting (${options.signatureLevel.name}),
                    the overload signature is not being included in the output.
                    This may result in ambiguous function resolution
                    at runtime, consider setting the SignatureLevel to Overloads or All
                    to ensure that the output includes sufficient
                    information to support correct overload selection at runtime.
                """
                    .trimIndent()
                    .replace("\n", " "),
                invocation.expression
            )
        }
        invocation.resultType = resolution.operator.resultType
        if (resolution.libraryIdentifier != null) {
            resolution.libraryName = resolveIncludeAlias(resolution.libraryIdentifier!!)
        }
        invocation.resolution = resolution
        return invocation
    }

    private fun pruneChoices(
        expression: Expression,
        @Suppress("UnusedParameter") targetType: DataType
    ): Expression {
        // TODO: In theory, we could collapse expressions that are unnecessarily broad, given the
        // targetType (type
        // leading)
        // This is a placeholder for where this functionality would be added in the future.
        return expression
    }

    fun resolveFunctionDefinition(fd: FunctionDef): Operator? {
        val libraryName = compiledLibrary.identifier!!.id
        val operatorName = fd.name
        val dataTypes: MutableList<DataType> = ArrayList()
        for (operand in fd.operand) {
            requireNotNull(operand.resultType) {
                "Could not determine signature for invocation of operator ${if (libraryName == null) "" else "$libraryName."}$operatorName."
            }
            dataTypes.add(operand.resultType!!)
        }
        val callContext =
            CallContext(
                compiledLibrary.identifier!!.id,
                fd.name!!,
                false,
                fd.fluent != null && fd.fluent!!,
                false,
                dataTypes
            )
        // Resolve exact, no conversion map
        return compiledLibrary.resolveCall(callContext, conversionMap)?.operator
    }

    @Suppress("NestedBlockDepth")
    private fun resolveCall(callContext: CallContext): OperatorResolution? {
        var result: OperatorResolution?
        if (callContext.libraryName.isNullOrEmpty()) {
            result = compiledLibrary.resolveCall(callContext, conversionMap)
            if (result == null) {
                result = systemLibrary.resolveCall(callContext, conversionMap)
                if (result == null && callContext.allowFluent) {
                    // attempt to resolve in each non-system included library, in order of
                    // inclusion, first resolution
                    // wins
                    for (lib in libraries.values) {
                        if (lib != systemLibrary) {
                            result = lib.resolveCall(callContext, conversionMap)
                            if (result != null) {
                                break
                            }
                        }
                    }
                }
                /*
                // Implicit resolution is only allowed for the system library functions.
                // Except for fluent functions, so consider whether we should have an ambiguous warnings for fluent function resolution?
                for (CompiledLibrary library : libraries.values()) {
                    OperatorResolution libraryResult = library.resolveCall(callContext, libraryBuilder.getConversionMap());
                    if (libraryResult != null) {
                        if (result != null) {
                            throw new IllegalArgumentException(String.format("Operator name %s is ambiguous between %s and %s.",
                                    callContext.getOperatorName(), result.getOperator().getName(), libraryResult.getOperator().getName()));
                        }

                        result = libraryResult;
                    }
                }
                */
            }
        } else {
            result = resolveLibrary(callContext.libraryName).resolveCall(callContext, conversionMap)
        }
        if (result != null) {
            checkAccessLevel(
                result.operator.libraryName,
                result.operator.name,
                result.operator.accessLevel
            )
        }
        return result
    }

    private fun isInterFunctionAccess(f1: String, f2: String?): Boolean {
        return if (f1.isNotBlank() && !f2.isNullOrBlank()) {
            !f1.equals(f2, ignoreCase = true)
        } else false
    }

    private fun checkOperator(callContext: CallContext, resolution: OperatorResolution?) {
        requireNotNull(resolution) {
            // ERROR:
            "Could not resolve call to operator ${callContext.operatorName} with signature ${callContext.signature}."
        }
        require(!(resolution.operator.fluent && !callContext.allowFluent)) {
            "Operator ${callContext.operatorName} with signature ${callContext.signature} is a fluent function and can only be invoked with fluent syntax."
        }
        require(
            !(callContext.allowFluent && !resolution.operator.fluent && !resolution.allowFluent)
        ) {
            "Invocation of operator ${callContext.operatorName} with signature ${callContext.signature} uses fluent syntax, but the operator is not defined as a fluent function."
        }
    }

    fun checkAccessLevel(
        libraryName: String?,
        objectName: String?,
        accessModifier: AccessModifier
    ) {
        if (
            accessModifier == AccessModifier.PRIVATE &&
                isInterFunctionAccess(library.identifier!!.id!!, libraryName)
        ) {
            // ERROR:
            throw CqlSemanticException(
                "Identifier $objectName in library $libraryName is marked private and cannot be referenced from another library."
            )
        }
    }

    fun resolveFunction(
        libraryName: String?,
        functionName: String,
        paramList: kotlin.collections.List<Expression>
    ): Expression? {
        return resolveFunction(
                libraryName,
                functionName,
                paramList,
                mustResolve = true,
                allowPromotionAndDemotion = false,
                allowFluent = false
            )
            ?.expression
    }

    private fun buildFunctionRef(
        libraryName: String?,
        functionName: String,
        paramList: Iterable<Expression>
    ): FunctionRef {
        val functionRef =
            objectFactory.createFunctionRef().withLibraryName(libraryName).withName(functionName)
        for (param in paramList) {
            functionRef.operand.add(param)
        }
        return functionRef
    }

    @Suppress("LongParameterList")
    fun resolveFunction(
        libraryName: String?,
        functionName: String,
        paramList: kotlin.collections.List<Expression>,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean
    ): Invocation? {
        var functionRef: FunctionRef? = buildFunctionRef(libraryName, functionName, paramList)

        // Attempt normal resolution, but don't require one
        var invocation: Invocation = FunctionRefInvocation(functionRef!!)
        functionRef =
            resolveCall(
                functionRef.libraryName,
                functionRef.name!!,
                invocation,
                false,
                allowPromotionAndDemotion,
                allowFluent
            )
                as FunctionRef?
        if (functionRef != null) {
            if ("System" == invocation.resolution!!.operator.libraryName) {
                val systemFun =
                    buildFunctionRef(
                        libraryName,
                        functionName,
                        paramList
                    ) // Rebuild the fun from the original arguments, otherwise it will resolve with
                // conversions in place
                val systemFunctionInvocation =
                    systemFunctionResolver.resolveSystemFunction(systemFun)
                if (systemFunctionInvocation != null) {
                    return systemFunctionInvocation
                }
            } else {
                // If the invocation is to a local function or a function in a non-system library,
                // check literal context
                if (mustResolve) {
                    checkLiteralContext()
                }
            }
        }

        // If it didn't resolve, there are two possibilities
        // 1. It is a special system function resolution that only resolves with the
        // systemFunctionResolver
        // 2. It is an error condition that needs to be reported
        if (functionRef == null) {
            functionRef = buildFunctionRef(libraryName, functionName, paramList)
            invocation = FunctionRefInvocation(functionRef)
            if (!allowFluent) {
                // Only attempt to resolve as a system function if
                // this is not a fluent call, or it is a required resolution
                val systemFunction = systemFunctionResolver.resolveSystemFunction(functionRef)
                if (systemFunction != null) {
                    return systemFunction
                }
                checkLiteralContext()
            }
            functionRef =
                resolveCall(
                    functionRef.libraryName,
                    functionRef.name!!,
                    invocation,
                    mustResolve,
                    allowPromotionAndDemotion,
                    allowFluent
                )
                    as FunctionRef?
            if (functionRef == null) {
                return null
            }
        }
        return invocation
    }

    fun verifyComparable(dataType: DataType) {
        val left = objectFactory.createLiteral().withResultType(dataType) as Expression
        val right = objectFactory.createLiteral().withResultType(dataType) as Expression
        val comparison: BinaryExpression = objectFactory.createLess().withOperand(left, right)
        resolveBinaryCall("System", "Less", comparison)
    }

    @JvmOverloads
    fun convertExpression(
        expression: Expression,
        targetType: DataType,
        implicit: Boolean = true
    ): Expression {
        val conversion = findConversion(expression.resultType!!, targetType, implicit, false)
        if (conversion != null) {
            return convertExpression(expression, conversion)
        }
        DataTypes.verifyType(expression.resultType, targetType)
        return expression
    }

    private fun convertListExpression(expression: Expression?, conversion: Conversion): Expression {
        val fromType: ListType = conversion.fromType as ListType
        val toType: ListType = conversion.toType as ListType
        return objectFactory
            .createQuery()
            .withSource(
                objectFactory
                    .createAliasedQuerySource()
                    .withAlias("X")
                    .withExpression(expression)
                    .withResultType(fromType)
            )
            .withReturn(
                objectFactory
                    .createReturnClause()
                    .withDistinct(false)
                    .withExpression(
                        convertExpression(
                            objectFactory
                                .createAliasRef()
                                .withName("X")
                                .withResultType(fromType.elementType),
                            conversion.conversion!!
                        )
                    )
                    .withResultType(toType)
            )
            .withResultType(toType)
    }

    private fun reportWarning(message: String, expression: Element?) {
        val trackback =
            if (expression != null && expression.trackbacks.isNotEmpty()) expression.trackbacks[0]
            else null
        val warning =
            CqlSemanticException(message, CqlCompilerException.ErrorSeverity.Warning, trackback)
        recordParsingException(warning)
    }

    private fun demoteListExpression(expression: Expression?, conversion: Conversion): Expression {
        val fromType = conversion.fromType as ListType
        val singletonFrom = objectFactory.createSingletonFrom().withOperand(expression)
        singletonFrom.resultType = fromType.elementType
        resolveUnaryCall("System", "SingletonFrom", singletonFrom)
        // WARNING:
        reportWarning("List-valued expression was demoted to a singleton.", expression)
        return if (conversion.conversion != null) {
            convertExpression(singletonFrom, conversion.conversion)
        } else {
            singletonFrom
        }
    }

    private fun promoteListExpression(expression: Expression, conversion: Conversion): Expression {
        var expression = expression
        if (conversion.conversion != null) {
            expression = convertExpression(expression, conversion.conversion)
        }
        if (expression.resultType == resolveTypeName("System", "Boolean")) {
            // WARNING:
            reportWarning("Boolean-valued expression was promoted to a list.", expression)
        }
        return resolveToList(expression)
    }

    fun resolveToList(expression: Expression?): Expression {
        // Use a ToList operator here to avoid duplicate evaluation of the operand.
        val toList = objectFactory.createToList().withOperand(expression)
        toList.resultType = ListType(expression!!.resultType!!)
        return toList
    }

    private fun demoteIntervalExpression(
        expression: Expression?,
        conversion: Conversion
    ): Expression {
        val fromType = conversion.fromType as IntervalType
        val pointFrom = objectFactory.createPointFrom().withOperand(expression)
        pointFrom.resultType = fromType.pointType
        resolveUnaryCall("System", "PointFrom", pointFrom)
        // WARNING:
        reportWarning("Interval-valued expression was demoted to a point.", expression)
        return if (conversion.conversion != null) {
            convertExpression(pointFrom, conversion.conversion)
        } else {
            pointFrom
        }
    }

    private fun promoteIntervalExpression(
        expression: Expression,
        conversion: Conversion
    ): Expression {
        var expression = expression
        if (conversion.conversion != null) {
            expression = convertExpression(expression, conversion.conversion)
        }
        return resolveToInterval(expression)
    }

    // When promoting a point to an interval, if the point is null, the result is null, rather than
    // constructing an
    // interval
    // with null boundaries
    private fun resolveToInterval(expression: Expression?): Expression {
        val condition = objectFactory.createIf()
        condition.condition = buildIsNull(expression)
        condition.then = buildNull(IntervalType(expression!!.resultType!!))
        val toInterval =
            objectFactory
                .createInterval()
                .withLow(expression)
                .withHigh(expression)
                .withLowClosed(true)
                .withHighClosed(true)
        toInterval.resultType = IntervalType(expression.resultType!!)
        condition.`else` = (toInterval)
        condition.resultType = resolveTypeName("System", "Boolean")
        return condition
    }

    private fun convertIntervalExpression(
        expression: Expression?,
        conversion: Conversion
    ): Expression {
        val fromType: IntervalType = conversion.fromType as IntervalType
        val toType: IntervalType = conversion.toType as IntervalType
        return objectFactory
            .createInterval()
            .withLow(
                convertExpression(
                    objectFactory
                        .createProperty()
                        .withSource(expression)
                        .withPath("low")
                        .withResultType(fromType.pointType),
                    conversion.conversion!!
                )
            )
            .withLowClosedExpression(
                objectFactory
                    .createProperty()
                    .withSource(expression)
                    .withPath("lowClosed")
                    .withResultType(resolveTypeName("System", "Boolean"))
            )
            .withHigh(
                convertExpression(
                    objectFactory
                        .createProperty()
                        .withSource(expression)
                        .withPath("high")
                        .withResultType(fromType.pointType),
                    conversion.conversion
                )
            )
            .withHighClosedExpression(
                objectFactory
                    .createProperty()
                    .withSource(expression)
                    .withPath("highClosed")
                    .withResultType(resolveTypeName("System", "Boolean"))
            )
            .withResultType(toType)
    }

    fun buildAs(expression: Expression?, asType: DataType?): As {
        val result = objectFactory.createAs().withOperand(expression).withResultType(asType)
        if (result.resultType is NamedType) {
            result.asType = dataTypeToQName(result.resultType)
        } else {
            result.asTypeSpecifier = dataTypeToTypeSpecifier(result.resultType)
        }
        return result
    }

    fun buildIs(expression: Expression?, isType: DataType?): Is {
        val result =
            objectFactory
                .createIs()
                .withOperand(expression)
                .withResultType(resolveTypeName("System", "Boolean"))
        if (isType is NamedType) {
            result.isType = dataTypeToQName(isType)
        } else {
            result.isTypeSpecifier = dataTypeToTypeSpecifier(isType)
        }
        return result
    }

    fun buildNull(nullType: DataType?): Null {
        val result = objectFactory.createNull().withResultType(nullType)
        if (nullType is NamedType) {
            result.resultTypeName = dataTypeToQName(nullType)
        } else {
            result.resultTypeSpecifier = dataTypeToTypeSpecifier(nullType)
        }
        return result
    }

    private fun buildIsNull(expression: Expression?): IsNull {
        val isNull = objectFactory.createIsNull().withOperand(expression)
        isNull.resultType = resolveTypeName("System", "Boolean")
        return isNull
    }

    private fun buildIsNotNull(expression: Expression?): Not {
        val isNull = buildIsNull(expression)
        val not = objectFactory.createNot().withOperand(isNull)
        not.resultType = resolveTypeName("System", "Boolean")
        return not
    }

    fun buildMinimum(dataType: DataType?): MinValue {
        val minimum = objectFactory.createMinValue()
        minimum.valueType = dataTypeToQName(dataType)
        minimum.resultType = dataType
        return minimum
    }

    fun buildMaximum(dataType: DataType?): MaxValue {
        val maximum = objectFactory.createMaxValue()
        maximum.valueType = dataTypeToQName(dataType)
        maximum.resultType = dataType
        return maximum
    }

    fun buildPredecessor(source: Expression?): Expression {
        val result = objectFactory.createPredecessor().withOperand(source)
        resolveUnaryCall("System", "Predecessor", result)
        return result
    }

    fun buildSuccessor(source: Expression?): Expression {
        val result = objectFactory.createSuccessor().withOperand(source)
        resolveUnaryCall("System", "Successor", result)
        return result
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth")
    fun convertExpression(expression: Expression, conversion: Conversion): Expression {
        return if (
            conversion.isCast &&
                (conversion.fromType.isSuperTypeOf(conversion.toType) ||
                    conversion.fromType.isCompatibleWith(conversion.toType))
        ) {
            // Otherwise, the choice is narrowing and a run-time As is required (to use only the
            // expected target
            // types)
            if (
                conversion.fromType is ChoiceType &&
                    conversion.toType is ChoiceType &&
                    (conversion.fromType).isSubSetOf(conversion.toType)
            ) {
                // conversion between compatible choice types requires no cast (i.e.
                // choice<Integer, String> can be
                // safely passed to choice<Integer, String, DateTime>
                return expression
            }
            val castedOperand = buildAs(expression, conversion.toType)
            collapseTypeCase(castedOperand)
        } else
            @Suppress("ComplexCondition")
            when {
                conversion.isCast &&
                    conversion.conversion != null &&
                    (conversion.fromType.isSuperTypeOf(conversion.conversion.fromType) ||
                        conversion.fromType.isCompatibleWith(conversion.conversion.fromType)) -> {
                    val castedOperand = buildAs(expression, conversion.conversion.fromType)
                    var result = convertExpression(castedOperand, conversion.conversion)
                    if (conversion.hasAlternativeConversions()) {
                        val caseResult = objectFactory.createCase()
                        caseResult.resultType = result.resultType
                        caseResult.caseItem.add(
                            objectFactory
                                .createCaseItem()
                                .withWhen(buildIs(expression, conversion.conversion.fromType))
                                .withThen(result)
                        )
                        for (alternative: Conversion in conversion.getAlternativeConversions()) {
                            caseResult.caseItem.add(
                                objectFactory
                                    .createCaseItem()
                                    .withWhen(buildIs(expression, alternative.fromType))
                                    .withThen(
                                        convertExpression(
                                            buildAs(expression, alternative.fromType),
                                            alternative
                                        )
                                    )
                            )
                        }
                        caseResult.withElse(buildNull(result.resultType))
                        result = caseResult
                    }
                    result
                }
                conversion.isListConversion -> {
                    convertListExpression(expression, conversion)
                }
                conversion.isListDemotion -> {
                    demoteListExpression(expression, conversion)
                }
                conversion.isListPromotion -> {
                    promoteListExpression(expression, conversion)
                }
                conversion.isIntervalConversion -> {
                    convertIntervalExpression(expression, conversion)
                }
                conversion.isIntervalDemotion -> {
                    demoteIntervalExpression(expression, conversion)
                }
                conversion.isIntervalPromotion -> {
                    promoteIntervalExpression(expression, conversion)
                }
                conversion.operator != null -> {
                    val functionRef =
                        objectFactory
                            .createFunctionRef()
                            .withLibraryName(conversion.operator.libraryName)
                            .withName(conversion.operator.name)
                            .withOperand(expression)
                    val systemFunctionInvocation =
                        systemFunctionResolver.resolveSystemFunction(functionRef)
                    if (systemFunctionInvocation != null) {
                        return systemFunctionInvocation.expression
                    }
                    resolveCall(
                        functionRef.libraryName,
                        functionRef.name!!,
                        FunctionRefInvocation(functionRef),
                        allowPromotionAndDemotion = false,
                        allowFluent = false
                    )
                    functionRef
                }
                else -> {
                    when (conversion.toType) {
                        resolveTypeName("System", "Boolean") -> {
                            objectFactory
                                .createToBoolean()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Integer") -> {
                            objectFactory
                                .createToInteger()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Long") -> {
                            objectFactory
                                .createToLong()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Decimal") -> {
                            objectFactory
                                .createToDecimal()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "String") -> {
                            objectFactory
                                .createToString()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Date") -> {
                            objectFactory
                                .createToDate()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "DateTime") -> {
                            objectFactory
                                .createToDateTime()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Time") -> {
                            objectFactory
                                .createToTime()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Quantity") -> {
                            objectFactory
                                .createToQuantity()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Ratio") -> {
                            objectFactory
                                .createToRatio()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        resolveTypeName("System", "Concept") -> {
                            objectFactory
                                .createToConcept()
                                .withOperand(expression)
                                .withResultType(conversion.toType)
                        }
                        else -> {
                            val convertedOperand =
                                objectFactory
                                    .createConvert()
                                    .withOperand(expression)
                                    .withResultType(conversion.toType)
                            if (convertedOperand.resultType is NamedType) {
                                convertedOperand.toType =
                                    dataTypeToQName(convertedOperand.resultType)
                            } else {
                                convertedOperand.toTypeSpecifier =
                                    dataTypeToTypeSpecifier(convertedOperand.resultType)
                            }
                            convertedOperand
                        }
                    }
                }
            }
    }

    /**
     * If the operand to an As is a "type case", meaning a case expression whose only cases have the
     * form: when X is T then X as T If one of the type cases is the same type as the As, the
     * operand of the As can be set to the operand of the type case with the same type, optimizing
     * the case as effectively a no-op
     *
     * @param asExpression
     * @return
     */
    @Suppress("NestedBlockDepth")
    private fun collapseTypeCase(asExpression: As): Expression {
        if (asExpression.operand is Case) {
            val c = asExpression.operand as Case
            if (isTypeCase(c)) {
                for (ci in c.caseItem) {
                    if (DataTypes.equal(asExpression.resultType, ci.then!!.resultType)) {
                        return ci.then!!
                    }
                }
            }
        }
        return asExpression
    }

    private fun isTypeCase(c: Case): Boolean {
        if (c.comparand != null) {
            return false
        }
        for (ci in c.caseItem) {
            if (ci.`when` !is Is) {
                return false
            }
            if (ci.then!!.resultType == null) {
                return false
            }
        }
        if (c.`else` !is Null) {
            return false
        }
        return c.resultType is ChoiceType
    }

    fun verifyType(actualType: DataType, expectedType: DataType) {
        if (expectedType.isSuperTypeOf(actualType) || actualType.isCompatibleWith(expectedType)) {
            return
        }
        val conversion =
            findConversion(
                actualType,
                expectedType,
                implicit = true,
                allowPromotionAndDemotion = false
            )
        if (conversion != null) {
            return
        }
        DataTypes.verifyType(actualType, expectedType)
    }

    fun findCompatibleType(first: DataType?, second: DataType?): DataType? {
        if (first == null || second == null) {
            return null
        }
        if (first == DataType.ANY) {
            return second
        }
        if (second == DataType.ANY) {
            return first
        }
        if (first.isSuperTypeOf(second) || second.isCompatibleWith(first)) {
            return first
        }
        if (second.isSuperTypeOf(first) || first.isCompatibleWith(second)) {
            return second
        }

        // If either side is a choice type, don't allow conversions because they will incorrectly
        // eliminate choices
        // based on convertibility
        if (!(first is ChoiceType || second is ChoiceType)) {
            var conversion =
                findConversion(second, first, implicit = true, allowPromotionAndDemotion = false)
            if (conversion != null) {
                return first
            }
            conversion =
                findConversion(first, second, implicit = true, allowPromotionAndDemotion = false)
            if (conversion != null) {
                return second
            }
        }
        return null
    }

    fun ensureCompatibleTypes(first: DataType?, second: DataType): DataType? {
        val compatibleType = findCompatibleType(first, second)
        if (compatibleType != null) {
            return compatibleType
        }
        if (first != null && !second.isSubTypeOf(first)) {
            return ChoiceType(first, second)
        }

        // The above construction of a choice type guarantees this will never be hit
        DataTypes.verifyType(second, first)
        return first
    }

    fun ensureCompatible(expression: Expression?, targetType: DataType?): Expression {
        if (targetType == null) {
            return objectFactory.createNull()
        }
        return if (!targetType.isSuperTypeOf(expression!!.resultType!!)) {
            convertExpression(expression, targetType, true)
        } else expression
    }

    fun enforceCompatible(expression: Expression?, targetType: DataType?): Expression {
        if (targetType == null) {
            return objectFactory.createNull()
        }
        return if (!targetType.isSuperTypeOf(expression!!.resultType!!)) {
            convertExpression(expression, targetType, false)
        } else expression
    }

    private fun createLiteral(value: String?, type: String): Literal {
        val resultType = resolveTypeName("System", type)
        val result =
            objectFactory
                .createLiteral()
                .withValue(value)
                .withValueType(dataTypeToQName(resultType))
        result.resultType = resultType
        return result
    }

    fun createLiteral(string: String?): Literal {
        return createLiteral(string, "String")
    }

    fun createLiteral(bool: Boolean): Literal {
        return createLiteral(bool.toString(), "Boolean")
    }

    fun createLiteral(integer: Int): Literal {
        return createLiteral(integer.toString(), "Integer")
    }

    fun createLiteral(value: Double): Literal {
        return createLiteral(value.toString(), "Decimal")
    }

    fun createNumberLiteral(value: String): Literal {
        val resultType =
            resolveTypeName("System", if (value.contains(".")) "Decimal" else "Integer")
        val result =
            objectFactory
                .createLiteral()
                .withValue(value)
                .withValueType(dataTypeToQName(resultType))
        result.resultType = resultType
        return result
    }

    fun createLongNumberLiteral(value: String?): Literal {
        val resultType = resolveTypeName("System", "Long")
        val result =
            objectFactory
                .createLiteral()
                .withValue(value)
                .withValueType(dataTypeToQName(resultType))
        result.resultType = resultType
        return result
    }

    private fun validateUnit(unit: String) {
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

    fun createQuantity(value: BigDecimal?, unit: String): Quantity {
        validateUnit(unit)
        val result = objectFactory.createQuantity().withValue(value).withUnit(unit)
        val resultType = resolveTypeName("System", "Quantity")
        result.resultType = resultType
        return result
    }

    fun createRatio(numerator: Quantity?, denominator: Quantity?): Ratio {
        val result =
            objectFactory.createRatio().withNumerator(numerator).withDenominator(denominator)
        val resultType = resolveTypeName("System", "Ratio")
        result.resultType = resultType
        return result
    }

    fun createInterval(
        low: Expression?,
        lowClosed: Boolean,
        high: Expression?,
        highClosed: Boolean
    ): Interval {
        val result: Interval =
            objectFactory
                .createInterval()
                .withLow(low)
                .withLowClosed(lowClosed)
                .withHigh(high)
                .withHighClosed(highClosed)
        val pointType: DataType? =
            ensureCompatibleTypes(result.low!!.resultType, result.high!!.resultType!!)
        result.resultType = IntervalType(pointType!!)
        result.low = ensureCompatible(result.low, pointType)
        result.high = ensureCompatible(result.high, pointType)
        return result
    }

    fun dataTypeToQName(type: DataType?): QName {
        return typeBuilder.dataTypeToQName(type)
    }

    private fun dataTypesToTypeSpecifiers(
        types: kotlin.collections.List<DataType>
    ): kotlin.collections.List<TypeSpecifier> {
        return typeBuilder.dataTypesToTypeSpecifiers(types)
    }

    fun dataTypeToTypeSpecifier(type: DataType?): TypeSpecifier {
        return typeBuilder.dataTypeToTypeSpecifier(type)
    }

    fun resolvePath(sourceType: DataType?, path: String): DataType? {
        // TODO: This is using a naive implementation for now... needs full path support (but not
        // full FluentPath
        // support...)
        var sourceType: DataType? = sourceType
        val identifiers: Array<String> =
            path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in identifiers.indices) {
            val resolution: PropertyResolution? = resolveProperty(sourceType, identifiers[i])
            sourceType = resolution!!.type
            // Actually, this doesn't matter for this call, we're just resolving the type...
            // if (!resolution.getTargetMap().equals(identifiers[i])) {
            //    throw new IllegalArgumentException(String.format("Identifier %s references an
            // element with a target
            // mapping defined and cannot be resolved as part of a path", identifiers[i]));
            // }
        }
        return sourceType
    }

    // TODO: Support case-insensitive models
    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ThrowsCount")
    @JvmOverloads
    fun resolveProperty(
        sourceType: DataType?,
        identifier: String,
        mustResolve: Boolean = true
    ): PropertyResolution? {
        var currentType: DataType? = sourceType
        while (currentType != null) {
            when {
                currentType is ClassType -> {
                    val classType: ClassType = currentType
                    if (identifier.startsWith("?") && isCompatibleWith("1.5")) {
                        val searchPath: String = identifier.substring(1)
                        for (s: SearchType in classType.getSearches()) {
                            if ((s.name == searchPath)) {
                                return PropertyResolution(s)
                            }
                        }
                    } else {
                        for (e: ClassTypeElement in classType.elements) {
                            if ((e.name == identifier)) {
                                require(!e.prohibited) {
                                    "Element ${e.name} cannot be referenced because it is marked prohibited in type ${(currentType as ClassType).name}."
                                }
                                return PropertyResolution(e)
                            }
                        }
                    }
                }
                currentType is TupleType -> {
                    for (e: TupleTypeElement in currentType.elements) {
                        if ((e.name == identifier)) {
                            return PropertyResolution(e)
                        }
                    }
                }
                currentType is IntervalType -> {
                    return when (identifier) {
                        "low",
                        "high" -> PropertyResolution(currentType.pointType, identifier)
                        "lowClosed",
                        "highClosed" ->
                            PropertyResolution((resolveTypeName("System", "Boolean"))!!, identifier)
                        else -> // ERROR:
                        throw IllegalArgumentException(
                                "Invalid interval property name $identifier."
                            )
                    }
                }
                currentType is ChoiceType -> {
                    // TODO: Issue a warning if the property does not resolve against every type in
                    // the
                    // choice

                    // Resolve the property against each type in the choice
                    val resultTypes: MutableSet<DataType> = HashSet()
                    val resultTargetMaps: MutableMap<DataType, String> = HashMap()
                    var name: String? = null
                    for (choice: DataType in currentType.types) {
                        val resolution: PropertyResolution? =
                            resolveProperty(choice, identifier, false)
                        if (resolution != null) {
                            resultTypes.add(resolution.type)
                            if (resolution.targetMap != null) {
                                if (resultTargetMaps.containsKey(resolution.type)) {
                                    require(
                                        resultTargetMaps[resolution.type] == resolution.targetMap
                                    ) {
                                        "Inconsistent target maps ${resultTargetMaps[resolution.type]} and ${resolution.targetMap} for choice type ${resolution.type}"
                                    }
                                } else {
                                    resultTargetMaps[resolution.type] = resolution.targetMap
                                }
                            }
                            if (name == null) {
                                name = resolution.name
                            } else
                                require(name == resolution.name) {
                                    "Inconsistent property resolution for choice type $choice (was $name, is ${resolution.name})"
                                }
                        }
                    }

                    // The result type is a choice of all the resolved types
                    if (resultTypes.size > 1) {
                        return PropertyResolution(ChoiceType(resultTypes), name!!, resultTargetMaps)
                    }
                    if (resultTypes.size == 1) {
                        return PropertyResolution(
                            resultTypes.iterator().next(),
                            name!!,
                            resultTargetMaps
                        )
                    }
                }
                currentType is ListType && listTraversal -> {
                    // NOTE: FHIRPath path traversal support
                    // Resolve property as a list of items of property of the element type
                    val resolution: PropertyResolution? =
                        resolveProperty(currentType.elementType, identifier)
                    return PropertyResolution(ListType(resolution!!.type), (resolution.targetMap)!!)
                }
            }
            if (currentType.baseType != DataType.ANY) {
                currentType = currentType.baseType
            } else {
                break
            }
        }
        require(!mustResolve) {
            // ERROR:
            "Member $identifier not found for type ${sourceType?.toLabel()}."
        }
        return null
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "ThrowsCount")
    fun resolveIdentifier(identifier: String, mustResolve: Boolean): Expression? {
        // An Identifier will always be:
        // 1: The name of an alias
        // 2: The name of a query define clause
        // 3: The name of an expression
        // 4: The name of a parameter
        // 5: The name of a valueset
        // 6: The name of a codesystem
        // 7: The name of a code
        // 8: The name of a concept
        // 9: The name of a library
        // 10: The name of a property on a specific context
        // 11: An unresolved identifier error is thrown

        // In a type specifier context, return the identifier as a Literal for resolution as a type
        // by the caller
        if (inTypeSpecifierContext()) {
            return this.createLiteral(identifier)
        }

        // In the sort clause of a plural query, names may be resolved based on the result type of
        // the query
        val resultElement: Expression? = resolveQueryResultElement(identifier)
        if (resultElement != null) {
            return resultElement
        }

        // In the case of a $this alias, names may be resolved as implicit property references
        val thisElement: Expression? = resolveQueryThisElement(identifier)
        if (thisElement != null) {
            return thisElement
        }
        if ((identifier == "\$index")) {
            val result: Iteration = objectFactory.createIteration()
            result.resultType = resolveTypeName("System", "Integer")
            return result
        }
        if ((identifier == "\$total")) {
            val result: Total = objectFactory.createTotal()
            result.resultType =
                resolveTypeName(
                    "System",
                    "Decimal"
                ) // TODO: This isn't right, but we don't set up a query for the Aggregate operator
            // right
            // now...
            return result
        }
        val alias: AliasedQuerySource? = resolveAlias(identifier)
        if (alias != null) {
            val result: AliasRef = objectFactory.createAliasRef().withName(identifier)
            if (alias.resultType is ListType) {
                result.resultType = (alias.resultType as ListType).elementType
            } else {
                result.resultType = alias.resultType
            }
            return result
        }
        val let: LetClause? = resolveQueryLet(identifier)
        if (let != null) {
            val result: QueryLetRef = objectFactory.createQueryLetRef().withName(identifier)
            result.resultType = let.resultType
            return result
        }
        val operandRef: OperandRef? = resolveOperandRef(identifier)
        if (operandRef != null) {
            return operandRef
        }
        val resolvedIdentifierContext: ResolvedIdentifierContext = resolve(identifier)
        val element = resolvedIdentifierContext.exactMatchElement
        if (element != null) {
            if (element is ExpressionDef) {
                checkLiteralContext()
                val expressionRef: ExpressionRef =
                    objectFactory.createExpressionRef().withName(element.name)
                expressionRef.resultType = getExpressionDefResultType(element)
                requireNotNull(expressionRef.resultType) {
                    // ERROR:
                    "Could not validate reference to expression ${expressionRef.name} because its definition contains errors."
                }
                return expressionRef
            }
            if (element is ParameterDef) {
                checkLiteralContext()
                val parameterRef: ParameterRef =
                    objectFactory.createParameterRef().withName(element.name)
                parameterRef.resultType = element.resultType
                requireNotNull(parameterRef.resultType) {
                    // ERROR:
                    "Could not validate reference to parameter ${parameterRef.name} because its definition contains errors."
                }
                return parameterRef
            }
            if (element is ValueSetDef) {
                checkLiteralContext()
                val valuesetRef: ValueSetRef =
                    objectFactory.createValueSetRef().withName(element.name)
                valuesetRef.resultType = element.resultType
                requireNotNull(valuesetRef.resultType) {
                    // ERROR:
                    "Could not validate reference to valueset ${valuesetRef.name} because its definition contains errors."
                }
                if (isCompatibleWith("1.5")) {
                    valuesetRef.preserve = true
                }
                return valuesetRef
            }
            if (element is CodeSystemDef) {
                checkLiteralContext()
                val codesystemRef: CodeSystemRef =
                    objectFactory.createCodeSystemRef().withName(element.name)
                codesystemRef.resultType = element.resultType
                requireNotNull(codesystemRef.resultType) {
                    // ERROR:
                    "Could not validate reference to codesystem ${codesystemRef.name} because its definition contains errors."
                }
                return codesystemRef
            }
            if (element is CodeDef) {
                checkLiteralContext()
                val codeRef: CodeRef = objectFactory.createCodeRef().withName(element.name)
                codeRef.resultType = element.resultType
                requireNotNull(codeRef.resultType) {
                    // ERROR:
                    "Could not validate reference to code ${codeRef.name} because its definition contains errors."
                }
                return codeRef
            }
            if (element is ConceptDef) {
                checkLiteralContext()
                val conceptRef: ConceptRef = objectFactory.createConceptRef().withName(element.name)
                conceptRef.resultType = element.resultType
                requireNotNull(conceptRef.resultType) {
                    // ERROR:
                    "Could not validate reference to concept ${conceptRef.name} because its definition contains error."
                }
                return conceptRef
            }
            if (element is IncludeDef) {
                checkLiteralContext()
                return LibraryRef(objectFactory.nextId(), element.localIdentifier)
            }
        }

        // If no other resolution occurs, and we are in a specific context, and there is a parameter
        // with the same name
        // as the context,
        // the identifier may be resolved as an implicit property reference on that context.
        val parameterRef: ParameterRef? = resolveImplicitContext()
        if (parameterRef != null) {
            val resolution: PropertyResolution? =
                resolveProperty(parameterRef.resultType, identifier, false)
            if (resolution != null) {
                var contextAccessor: Expression? =
                    buildProperty(
                        parameterRef,
                        resolution.name,
                        resolution.isSearch,
                        resolution.type
                    )
                contextAccessor = applyTargetMap(contextAccessor, resolution.targetMap)
                return contextAccessor
            }
        }
        if (mustResolve) {
            // ERROR:
            var message = resolvedIdentifierContext.warnCaseInsensitiveIfApplicable()
            if (message == null) {
                message = "Could not resolve identifier $identifier in the current library."
            }

            throw IllegalArgumentException(message)
        }
        return null
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
        if (!inLiteralContext() && inSpecificContext()) {
            val resolvedIdentifierContext: ResolvedIdentifierContext =
                resolve(currentExpressionContext())
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

    fun buildProperty(
        scope: String?,
        path: String?,
        isSearch: Boolean,
        resultType: DataType?
    ): Property {
        return if (isSearch) {
            val result = objectFactory.createSearch().withScope(scope).withPath(path)
            result.resultType = resultType
            result
        } else {
            val result = objectFactory.createProperty().withScope(scope).withPath(path)
            result.resultType = resultType
            result
        }
    }

    fun buildProperty(
        source: Expression?,
        path: String?,
        isSearch: Boolean,
        resultType: DataType?
    ): Property {
        return if (isSearch) {
            val result = objectFactory.createSearch().withSource(source).withPath(path)
            result.resultType = resultType
            result
        } else {
            val result = objectFactory.createProperty().withSource(source).withPath(path)
            result.resultType = resultType
            result
        }
    }

    @Suppress("NestedBlockDepth")
    private fun getModelMapping(sourceContext: Expression?): VersionedIdentifier? {
        var result: VersionedIdentifier? = null
        if (library.usings != null) {
            for (usingDef in library.usings!!.def) {
                val model = getModel(usingDef)
                if (model.modelInfo.targetUrl != null) {
                    if (result != null) {
                        reportWarning(
                            "Duplicate mapped model ${model.modelInfo.name}:${model.modelInfo.targetUrl}${
                                if (model.modelInfo.targetVersion != null)
                                    "|" + model.modelInfo.targetVersion
                                else ""
                            }",
                            sourceContext
                        )
                    }
                    result =
                        objectFactory
                            .createVersionedIdentifier()
                            .withId(model.modelInfo.name)
                            .withSystem(model.modelInfo.targetUrl)
                            .withVersion(model.modelInfo.targetVersion)
                }
            }
        }
        return result
    }

    private fun ensureLibraryIncluded(libraryName: String, sourceContext: Expression?) {
        var includeDef = compiledLibrary.resolveIncludeRef(libraryName)
        if (includeDef == null) {
            val modelMapping = getModelMapping(sourceContext)
            var path = libraryName
            if (namespaceInfo != null && modelMapping != null && modelMapping.system != null) {
                path = NamespaceManager.getPath(modelMapping.system, path)
            }
            includeDef =
                objectFactory.createIncludeDef().withLocalIdentifier(libraryName).withPath(path)
            if (modelMapping != null) {
                includeDef.version = modelMapping.version
            }
            compiledLibrary.add(includeDef)
        }
    }

    private fun applyTargetModelMaps() {
        if (library.usings != null) {
            for (usingDef in library.usings!!.def) {
                val model = getModel(usingDef)
                if (model.modelInfo.targetUrl != null) {
                    usingDef.uri = model.modelInfo.targetUrl
                    usingDef.version = model.modelInfo.targetVersion
                }
            }
        }
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ThrowsCount")
    fun applyTargetMap(source: Expression?, targetMap: String?): Expression? {
        var targetMap: String? = targetMap
        if (targetMap == null || (targetMap == "null")) {
            return source
        }

        // TODO: Consider whether the mapping should remove this in the ModelInfo...this is really a
        // FHIR-specific
        // hack...
        // Remove any "choice" paths...
        targetMap = targetMap.replace("[x]", "")

        // TODO: This only works for simple mappings, nested mappings will require the targetMap.g4
        // parser
        // Supported target mapping syntax:
        // %value.<property name>
        // Resolves as a property accessor with the given source and <property name> as the path
        // <qualified function name>(%value)
        // Resolves as a function ref with the given function name and the source as an operand
        // <type name>:<map>;<type name>:<map>...
        // Semi-colon delimited list of type names and associated maps
        // Resolves as a case with whens for each type, with target mapping applied per the target
        // map for that type
        // %parent.<qualified path>[<key path>=<key value>,<key path>=<key value>,...].<qualified
        // path>
        // Resolves as a replacement of the property on which it appears
        // Replaces the path of the property on which it appears with the given qualified path,
        // which then becomes the
        // source of a query with a where clause with criteria built for each comparison in the
        // indexer
        // If there is a trailing qualified path, the query is wrapped in a singletonFrom and a
        // property access
        // Any other target map results in an exception
        when {
            targetMap.contains(";") -> {
                val typeCases: Array<String> =
                    targetMap.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val c: Case = objectFactory.createCase()
                for (typeCase: String in typeCases) {
                    if (typeCase.isNotEmpty()) {
                        val splitIndex: Int = typeCase.indexOf(':')
                        require(splitIndex > 0) { "Malformed type case in targetMap $targetMap" }
                        val typeCaseElement: String = typeCase.substring(0, splitIndex)
                        val typeCaseType: DataType? = resolveTypeName(typeCaseElement)
                        val typeCaseMap: String = typeCase.substring(splitIndex + 1)
                        val ci: CaseItem =
                            objectFactory
                                .createCaseItem()
                                .withWhen(
                                    objectFactory
                                        .createIs()
                                        .withOperand(applyTargetMap(source, typeCaseMap))
                                        .withIsType(dataTypeToQName(typeCaseType))
                                )
                                .withThen(applyTargetMap(source, typeCaseMap))
                        ci.then!!.resultType = typeCaseType
                        c.caseItem.add(ci)
                    }
                }
                return when (c.caseItem.size) {
                    0 -> {
                        buildNull(source!!.resultType)
                    }
                    1 -> {
                        c.caseItem[0].then
                    }
                    else -> {
                        c.`else` = (buildNull(source!!.resultType))
                        c.resultType = source.resultType
                        c
                    }
                }
            }
            targetMap.contains("(") -> {
                val invocationStart: Int = targetMap.indexOf("(")
                val qualifiedFunctionName: String = targetMap.substring(0, invocationStart)
                val nameParts: Array<String> =
                    qualifiedFunctionName
                        .split("\\.".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                var libraryName: String? = null
                var functionName: String? = qualifiedFunctionName
                if (nameParts.size == 2) {
                    libraryName = nameParts[0]
                    functionName = nameParts[1]
                    ensureLibraryIncluded(libraryName, source)
                }
                val functionArgument: String =
                    targetMap.substring(invocationStart + 1, targetMap.lastIndexOf(')'))
                val argumentSource: Expression? =
                    if ((functionArgument == "%value")) source
                    else applyTargetMap(source, functionArgument)

                // NOTE: This is needed to work around the mapping for ToInterval
                // FHIRHelpers defines multiple overloads of ToInterval, but the type mapping
                // does not have the type of the source data type.
                // All the mappings for ToInterval use FHIR.Period, so this is safe to assume
                // In addition, no other FHIRHelpers functions use overloads (except ToString and
                // ToDateTime,
                // but those mappings expand the value element directly, rather than invoking the
                // FHIRHelpers function)
                var argumentSignature: TypeSpecifier? = null
                if (
                    options.signatureLevel != SignatureLevel.None &&
                        qualifiedFunctionName == "FHIRHelpers.ToInterval"
                ) {
                    // Force loading of the FHIR model, as it's an implicit
                    // dependency of the target mapping here.
                    var fhirVersion = "4.0.1"
                    val qiCoreModel = this.getModel("QICore")
                    val version = qiCoreModel.modelInfo.version
                    if (version == "3.3.0") {
                        fhirVersion = "4.0.0"
                    } else if (version!!.startsWith("3")) {
                        fhirVersion = "3.0.1"
                    }

                    // Force the FHIR model to be loaded.
                    modelManager.resolveModel("FHIR", fhirVersion)

                    val namedTypeSpecifier =
                        NamedTypeSpecifier()
                            .withName(dataTypeToQName(resolveTypeName("FHIR", "Period")))
                    argumentSignature = namedTypeSpecifier
                }

                when (argumentSource!!.resultType) {
                    is ListType -> {
                        val query: Query =
                            objectFactory
                                .createQuery()
                                .withSource(
                                    objectFactory
                                        .createAliasedQuerySource()
                                        .withExpression(argumentSource)
                                        .withAlias(FP_THIS)
                                )
                        val fr: FunctionRef =
                            objectFactory
                                .createFunctionRef()
                                .withLibraryName(libraryName)
                                .withName(functionName)
                                .withOperand(objectFactory.createAliasRef().withName(FP_THIS))

                        if (argumentSignature != null) {
                            fr.signature.add(argumentSignature)
                        }

                        // This doesn't quite work because the US.Core types aren't subtypes of FHIR
                        // types.
                        // resolveCall(libraryName, functionName, new FunctionRefInvocation(fr),
                        // false,
                        // false);
                        query.`return` =
                            (objectFactory
                                .createReturnClause()
                                .withDistinct(false)
                                .withExpression(fr))
                        query.resultType = source!!.resultType
                        return query
                    }
                    else -> {
                        val fr: FunctionRef =
                            objectFactory
                                .createFunctionRef()
                                .withLibraryName(libraryName)
                                .withName(functionName)
                                .withOperand(argumentSource)
                        fr.resultType = source!!.resultType

                        if (argumentSignature != null) {
                            fr.signature.add(argumentSignature)
                        }

                        return fr
                        // This doesn't quite work because the US.Core types aren't subtypes of FHIR
                        // types,
                        // or they are defined as System types and not FHIR types
                        // return resolveCall(libraryName, functionName, new
                        // FunctionRefInvocation(fr),
                        // false, false);
                    }
                }
            }
            targetMap.contains("[") -> {
                val indexerStart: Int = targetMap.indexOf("[")
                val indexerEnd: Int = targetMap.indexOf("]")
                val indexer: String = targetMap.substring(indexerStart + 1, indexerEnd)
                val indexerPath: String = targetMap.substring(0, indexerStart)
                var result: Expression? = null

                // Apply sourcePaths to get to the indexer
                val indexerPaths: Array<String> =
                    indexerPath.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (path: String in indexerPaths) {
                    if ((path == "%parent")) {
                        require(source is Property) {
                            "Cannot expand target map $targetMap for non-property-accessor type ${source!!::class.simpleName}"
                        }
                        val sourceProperty: Property = source
                        result =
                            sourceProperty.source
                                ?: sourceProperty.scope?.let {
                                    resolveIdentifier(sourceProperty.scope!!, true)
                                }
                        requireNotNull(result) {
                            "Cannot resolve %%parent reference in targetMap $targetMap"
                        }
                    } else {
                        val p: Property =
                            objectFactory.createProperty().withSource(result).withPath(path)
                        result = p
                    }
                }

                // Build a query with the current result as source and the indexer content as
                // criteria
                // in the where clause
                val querySource: AliasedQuerySource =
                    objectFactory
                        .createAliasedQuerySource()
                        .withExpression(result)
                        .withAlias(FP_THIS)
                var criteria: Expression? = null
                for (indexerItem: String in
                    indexer.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    val indexerItems: Array<String> =
                        indexerItem
                            .split("=".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    require(indexerItems.size == 2) {
                        "Invalid indexer item $indexerItem in targetMap $targetMap"
                    }
                    var left: Expression? = null
                    for (path: String in
                        indexerItems[0]
                            .split("\\.".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()) {
                        left =
                            if (left == null) {
                                objectFactory.createProperty().withScope(FP_THIS).withPath(path)
                            } else {
                                objectFactory.createProperty().withSource(left).withPath(path)
                            }

                        // HACK: Workaround the fact that we don't have type information for the
                        // mapping
                        // expansions...
                        if ((path == "coding")) {
                            left =
                                objectFactory
                                    .createFirst()
                                    .withSource(left)
                                    .withResultType(
                                        this.getModel("FHIR").resolveTypeName("FHIR.coding")
                                    )
                        }
                        if ((path == "url")) {
                            // HACK: This special cases FHIR model resolution
                            left.resultType = this.getModel("FHIR").resolveTypeName("FHIR.uri")
                            val ref: FunctionRef =
                                objectFactory
                                    .createFunctionRef()
                                    .withLibraryName("FHIRHelpers")
                                    .withName("ToString")
                                    .withOperand(left)
                            left =
                                resolveCall(
                                    ref.libraryName,
                                    ref.name!!,
                                    FunctionRefInvocation(ref),
                                    allowPromotionAndDemotion = false,
                                    allowFluent = false
                                )
                        }
                    }

                    // HACK: Workaround the fact that we don't have type information for the mapping
                    // expansions...
                    // These hacks will be removed when addressed by the model info
                    if ((indexerItems[0] == "code.coding.system")) {
                        // HACK: This special cases FHIR model resolution
                        left!!.resultType = this.getModel("FHIR").resolveTypeName("FHIR.uri")
                        val ref: FunctionRef =
                            objectFactory
                                .createFunctionRef()
                                .withLibraryName("FHIRHelpers")
                                .withName("ToString")
                                .withOperand(left)
                        left =
                            resolveCall(
                                ref.libraryName,
                                ref.name!!,
                                FunctionRefInvocation(ref),
                                allowPromotionAndDemotion = false,
                                allowFluent = false
                            )
                    }
                    if ((indexerItems[0] == "code.coding.code")) {
                        // HACK: This special cases FHIR model resolution
                        left!!.resultType = this.getModel("FHIR").resolveTypeName("FHIR.code")
                        val ref: FunctionRef =
                            objectFactory
                                .createFunctionRef()
                                .withLibraryName("FHIRHelpers")
                                .withName("ToString")
                                .withOperand(left)
                        left =
                            resolveCall(
                                ref.libraryName,
                                ref.name!!,
                                FunctionRefInvocation(ref),
                                allowPromotionAndDemotion = false,
                                allowFluent = false
                            )
                    }
                    val rightValue: String =
                        indexerItems[1].substring(1, indexerItems[1].length - 1)
                    val right: Expression =
                        this.createLiteral(StringEscapeUtils.unescapeCql(rightValue))
                    val criteriaItem: Expression =
                        objectFactory.createEqual().withOperand(left!!, right)
                    criteria =
                        if (criteria == null) {
                            criteriaItem
                        } else {
                            objectFactory.createAnd().withOperand(criteria, criteriaItem)
                        }
                }
                val query: Query =
                    objectFactory.createQuery().withSource(querySource).withWhere(criteria)
                result = query
                if (indexerEnd + 1 < targetMap.length) {
                    // There are additional paths following the indexer, apply them
                    var targetPath: String = targetMap.substring(indexerEnd + 1)
                    if (targetPath.startsWith(".")) {
                        targetPath = targetPath.substring(1)
                    }
                    if (targetPath.isNotEmpty()) {
                        query.`return` =
                            (objectFactory
                                .createReturnClause()
                                .withDistinct(false)
                                .withExpression(
                                    objectFactory
                                        .createProperty()
                                        .withSource(
                                            objectFactory.createAliasRef().withName(FP_THIS)
                                        )
                                        .withPath(targetPath)
                                ))
                    }

                    // The value reference should go inside the query, rather than being applied as
                    // a
                    // property outside of it
                    // for (String path : targetPath.split("\\.")) {
                    //    result = of.createProperty().withSource(result).withPath(path);
                    // }
                }
                if (source!!.resultType !is ListType) {
                    // Use a singleton from since the source of the query is a list
                    result = objectFactory.createSingletonFrom().withOperand(result)
                }
                result.resultType = source.resultType
                return result
            }
            targetMap.startsWith("%value.") -> {
                val propertyName: String = targetMap.substring(@Suppress("MagicNumber") 7)
                // If the source is a list, the mapping is expected to apply to every element in the
                // list
                // ((source $this return all $this.value)
                if (source!!.resultType is ListType) {
                    val s: AliasedQuerySource =
                        objectFactory
                            .createAliasedQuerySource()
                            .withExpression(source)
                            .withAlias(FP_THIS)
                    val p: Property =
                        objectFactory.createProperty().withScope(FP_THIS).withPath(propertyName)
                    p.resultType = (source.resultType as ListType).elementType
                    val r: ReturnClause =
                        objectFactory.createReturnClause().withDistinct(false).withExpression(p)
                    val q: Query = objectFactory.createQuery().withSource(s).withReturn(r)
                    q.resultType = source.resultType
                    return q
                } else {
                    val p: Property =
                        objectFactory.createProperty().withSource(source).withPath(propertyName)
                    p.resultType = source.resultType
                    return p
                }
            }
        }
        throw IllegalArgumentException("TargetMapping not implemented: $targetMap")
    }

    @Suppress("LongMethod", "NestedBlockDepth", "CyclomaticComplexMethod")
    fun resolveAccessor(left: Expression, memberIdentifier: String): Expression? {
        // if left is a LibraryRef
        // if right is an identifier
        // right may be an ExpressionRef, a CodeSystemRef, a ValueSetRef, a CodeRef, a ConceptRef,
        // or a ParameterRef --
        // need to resolve on the referenced library
        // if left is an ExpressionRef
        // if right is an identifier
        // return a Property with the ExpressionRef as source and identifier as Path
        // if left is a Property
        // if right is an identifier
        // modify the Property to append the identifier to the path
        // if left is an AliasRef
        // return a Property with a Path and no source, and Scope set to the Alias
        // if left is an Identifier
        // return a new Identifier with left as a qualifier
        // else
        // throws an error as an unresolved identifier
        when {
            left is LibraryRef -> {
                val libraryName: String? = left.libraryName
                val referencedLibrary: CompiledLibrary = resolveLibrary(libraryName)
                val resolvedIdentifierContext: ResolvedIdentifierContext =
                    referencedLibrary.resolve(memberIdentifier)
                val element = resolvedIdentifierContext.exactMatchElement
                if (element != null) {
                    if (element is ExpressionDef) {
                        checkAccessLevel(libraryName, memberIdentifier, element.accessLevel!!)
                        val result: Expression =
                            objectFactory
                                .createExpressionRef()
                                .withLibraryName(libraryName)
                                .withName(memberIdentifier)
                        result.resultType = getExpressionDefResultType(element)
                        return result
                    }
                    if (element is ParameterDef) {
                        checkAccessLevel(libraryName, memberIdentifier, element.accessLevel!!)
                        val result: Expression =
                            objectFactory
                                .createParameterRef()
                                .withLibraryName(libraryName)
                                .withName(memberIdentifier)
                        result.resultType = element.resultType
                        return result
                    }
                    if (element is ValueSetDef) {
                        checkAccessLevel(libraryName, memberIdentifier, element.accessLevel!!)
                        val result: ValueSetRef =
                            objectFactory
                                .createValueSetRef()
                                .withLibraryName(libraryName)
                                .withName(memberIdentifier)
                        result.resultType = element.resultType
                        if (isCompatibleWith("1.5")) {
                            result.preserve = true
                        }
                        return result
                    }
                    if (element is CodeSystemDef) {
                        checkAccessLevel(libraryName, memberIdentifier, element.accessLevel!!)
                        val result: CodeSystemRef =
                            objectFactory
                                .createCodeSystemRef()
                                .withLibraryName(libraryName)
                                .withName(memberIdentifier)
                        result.resultType = element.resultType
                        return result
                    }
                    if (element is CodeDef) {
                        checkAccessLevel(libraryName, memberIdentifier, element.accessLevel!!)
                        val result: CodeRef =
                            objectFactory
                                .createCodeRef()
                                .withLibraryName(libraryName)
                                .withName(memberIdentifier)
                        result.resultType = element.resultType
                        return result
                    }
                    if (element is ConceptDef) {
                        checkAccessLevel(libraryName, memberIdentifier, element.accessLevel!!)
                        val result: ConceptRef =
                            objectFactory
                                .createConceptRef()
                                .withLibraryName(libraryName)
                                .withName(memberIdentifier)
                        result.resultType = element.resultType
                        return result
                    }
                }

                // ERROR:
                throw IllegalArgumentException(
                    "Could not resolve identifier $memberIdentifier in library ${referencedLibrary.identifier!!.id}."
                )
            }
            left is AliasRef -> {
                val resolution: PropertyResolution? =
                    resolveProperty(left.resultType, memberIdentifier)
                val result: Expression =
                    buildProperty(
                        left.name,
                        resolution!!.name,
                        resolution.isSearch,
                        resolution.type
                    )
                return applyTargetMap(result, resolution.targetMap)
            }
            left.resultType is ListType && listTraversal -> {
                // NOTE: FHIRPath path traversal support
                // Resolve property access of a list of items as a query:
                // listValue.property ::= listValue X where X.property is not null return all
                // X.property
                val listType: ListType = left.resultType as ListType
                val resolution: PropertyResolution? =
                    resolveProperty(listType.elementType, memberIdentifier)
                var accessor: Expression? =
                    buildProperty(
                        objectFactory.createAliasRef().withName(FP_THIS),
                        resolution!!.name,
                        resolution.isSearch,
                        resolution.type
                    )
                accessor = applyTargetMap(accessor, resolution.targetMap)
                val not: Expression = buildIsNotNull(accessor)

                // Recreate property, it needs to be accessed twice
                accessor =
                    buildProperty(
                        objectFactory.createAliasRef().withName(FP_THIS),
                        resolution.name,
                        resolution.isSearch,
                        resolution.type
                    )
                accessor = applyTargetMap(accessor, resolution.targetMap)
                val source: AliasedQuerySource =
                    objectFactory.createAliasedQuerySource().withExpression(left).withAlias(FP_THIS)
                source.resultType = left.resultType
                val query: Query =
                    objectFactory
                        .createQuery()
                        .withSource(source)
                        .withWhere(not)
                        .withReturn(
                            objectFactory
                                .createReturnClause()
                                .withDistinct(false)
                                .withExpression(accessor)
                        )
                query.resultType = ListType(accessor!!.resultType!!)
                if (accessor.resultType is ListType) {
                    val result: Flatten = objectFactory.createFlatten().withOperand(query)
                    result.resultType = accessor.resultType
                    return result
                }
                return query
            }
            else -> {
                val resolution: PropertyResolution? =
                    resolveProperty(left.resultType, memberIdentifier)
                var result: Expression? =
                    buildProperty(left, resolution!!.name, resolution.isSearch, resolution.type)
                result = applyTargetMap(result, resolution.targetMap)
                return result
            }
        }
    }

    private fun resolveQueryResultElement(identifier: String): Expression? {
        if (inQueryContext()) {
            val query = peekQueryContext()
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
        if (inQueryContext()) {
            for (i in scope.queries.indices.reversed()) {
                val source = scope.queries.elementAt(i).resolveAlias(identifier)
                if (source != null) {
                    return source
                }
            }
        }
        return null
    }

    @Suppress("NestedBlockDepth")
    private fun resolveQueryThisElement(identifier: String): Expression? {
        if (inQueryContext()) {
            val query = peekQueryContext()
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
        if (inQueryContext()) {
            for (i in scope.queries.indices.reversed()) {
                val let = scope.queries.elementAt(i).resolveLet(identifier)
                if (let != null) {
                    return let
                }
            }
        }
        return null
    }

    private fun resolveOperandRef(identifier: String): OperandRef? {
        if (!functionDefs.empty()) {
            for (operand in functionDefs.peek().operand) {
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
        // expression def result
        // type.
        if ((currentExpressionContext() == expressionDef.context)) {
            return expressionDef.resultType
        }

        // If the current expression context is specific, a reference to an unfiltered context
        // expression will indicate
        // a full
        // evaluation of the population context expression, and the result type is the same.
        if (inSpecificContext()) {
            return expressionDef.resultType
        }

        // If the current expression context is unfiltered, a reference to a specific context
        // expression will need to be
        // performed for every context in the system, so the result type is promoted to a list (if
        // it is not already).
        if (inUnfilteredContext()) {
            // If we are in the source clause of a query, indicate that the source references
            // patient context
            if (inQueryContext() && scope.queries.peek().inSourceClause()) {
                scope.queries.peek().referencesSpecificContextValue = true
            }
            val resultType: DataType = expressionDef.resultType!!
            return if (resultType !is ListType) {
                ListType(resultType)
            } else {
                resultType
            }
        }
        throw IllegalArgumentException(
            "Invalid context reference from ${currentExpressionContext()} context to ${expressionDef.context} context."
        )
    }

    enum class IdentifierScope {
        GLOBAL,
        LOCAL
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
     * @param identifier The identifier belonging to the parameter, expression, function, alias,
     *   etc., to be evaluated.
     * @param element The construct trackable, for example [ExpressionRef].
     */
    @JvmOverloads
    fun pushIdentifier(
        identifier: String,
        element: Element?,
        scope: IdentifierScope = IdentifierScope.LOCAL
    ) {
        val localMatch =
            if (localIdentifierStack.isNotEmpty())
                findMatchingIdentifierContext(localIdentifierStack.peek(), identifier)
            else null
        val globalMatch = findMatchingIdentifierContext(globalIdentifiers, identifier)
        if (globalMatch != null || localMatch != null) {
            val matchedContext = if (globalMatch != null) globalMatch else localMatch!!
            val matchedOnFunctionOverloads =
                matchedContext.trackableSubclass == FunctionDef::class && element is FunctionDef
            if (!matchedOnFunctionOverloads) {
                reportWarning(
                    resolveWarningMessage(matchedContext.identifier, identifier, element),
                    element
                )
            }
        }
        if (shouldAddIdentifierContext(element)) {
            val trackableOrNull: KClass<out Element>? =
                if (element == null) null else element::class
            // Sometimes the underlying Trackable doesn't resolve in the calling code
            if (scope == IdentifierScope.GLOBAL) {
                globalIdentifiers.add(IdentifierContext(identifier, trackableOrNull))
            } else {
                localIdentifierStack.peek().add(IdentifierContext(identifier, trackableOrNull))
            }
        }
    }

    private fun findMatchingIdentifierContext(
        identifierContext: Collection<IdentifierContext>,
        identifier: String
    ): IdentifierContext? {
        return identifierContext
            .filter { innerContext: IdentifierContext -> (innerContext.identifier == identifier) }
            .firstOrNull()
    }

    /**
     * Pop the last resolved identifier off the deque. This is needed in case of a context in which
     * an identifier falls out of scope, for an example, an alias within an expression or function
     * body.
     */
    @JvmOverloads
    fun popIdentifier(scope: IdentifierScope = IdentifierScope.LOCAL) {
        if (scope == IdentifierScope.GLOBAL) {
            globalIdentifiers.removeLast()
        } else {
            localIdentifierStack.peek().removeLast()
        }
    }

    fun pushIdentifierScope() {
        localIdentifierStack.push(ArrayDeque())
    }

    fun popIdentifierScope() {
        localIdentifierStack.pop()
    }

    private fun shouldAddIdentifierContext(element: Element?): Boolean {
        return element !is Literal
    }

    private fun resolveWarningMessage(
        matchedIdentifier: String?,
        identifierParam: String,
        element: Element?
    ): String {
        val elementString = lookupElementWarning(element)
        return if (element is Literal) {
            "You used a string literal: [$identifierParam] here that matches an identifier in scope: [$matchedIdentifier]. Did you mean to use the identifier instead?"
        } else
            "$elementString identifier [$identifierParam] is hiding another identifier of the same name."
    }

    private inner class Scope {
        val targets = Stack<Expression>()
        val queries = Stack<QueryContext>()
    }

    private inner class ExpressionDefinitionContext(val identifier: String) {
        val scope: Scope = Scope()
        var rootCause: Exception? = null
    }

    private inner class ExpressionDefinitionContextStack : Stack<ExpressionDefinitionContext?>() {
        operator fun contains(identifier: String): Boolean {
            for (i in 0 until size()) {
                if (this.elementAt(i)?.identifier == identifier) {
                    return true
                }
            }
            return false
        }
    }

    fun determineRootCause(): Exception? {
        if (expressionDefinitions.isNotEmpty()) {
            val currentContext = expressionDefinitions.peek()
            if (currentContext != null) {
                return currentContext.rootCause
            }
        }
        return null
    }

    fun setRootCause(rootCause: Exception?) {
        if (expressionDefinitions.isNotEmpty()) {
            val currentContext = expressionDefinitions.peek()
            currentContext?.rootCause = rootCause
        }
    }

    fun pushExpressionDefinition(identifier: String) {
        require(!expressionDefinitions.contains(identifier)) {
            // ERROR:
            "Cannot resolve reference to expression or function $identifier because it results in a circular reference."
        }
        expressionDefinitions.push(ExpressionDefinitionContext(identifier))
    }

    fun popExpressionDefinition() {
        expressionDefinitions.pop()
    }

    private fun hasScope(): Boolean {
        return !expressionDefinitions.empty()
    }

    private val scope: Scope
        get() = expressionDefinitions.peek()?.scope!!

    fun pushExpressionContext(context: String?) {
        require(context != null) { "Expression context cannot be null" }
        expressionContext.push(context)
    }

    fun popExpressionContext() {
        check(!expressionContext.empty()) { "Expression context stack is empty." }
        expressionContext.pop()
    }

    private fun currentExpressionContext(): String {
        check(!expressionContext.empty()) { "Expression context stack is empty." }
        return expressionContext.peek()
    }

    private fun inSpecificContext(): Boolean {
        return !inUnfilteredContext()
    }

    private fun inUnfilteredContext(): Boolean {
        return currentExpressionContext() == "Unfiltered" ||
            isCompatibilityLevel3 && currentExpressionContext() == "Population"
    }

    private fun inQueryContext(): Boolean {
        return hasScope() && scope.queries.isNotEmpty()
    }

    fun pushQueryContext(context: QueryContext) {
        scope.queries.push(context)
    }

    fun popQueryContext(): QueryContext {
        return scope.queries.pop()
    }

    fun peekQueryContext(): QueryContext {
        return scope.queries.peek()
    }

    fun pushExpressionTarget(target: Expression) {
        scope.targets.push(target)
    }

    fun popExpressionTarget(): Expression {
        return scope.targets.pop()
    }

    fun hasExpressionTarget(): Boolean {
        return hasScope() && scope.targets.isNotEmpty()
    }

    fun beginFunctionDef(functionDef: FunctionDef) {
        functionDefs.push(functionDef)
    }

    fun endFunctionDef() {
        functionDefs.pop()
    }

    fun pushLiteralContext() {
        literalContext++
    }

    fun popLiteralContext() {
        check(inLiteralContext()) { "Not in literal context" }
        literalContext--
    }

    private fun inLiteralContext(): Boolean {
        return literalContext > 0
    }

    fun checkLiteralContext() {
        check(!inLiteralContext()) {
            "Expressions in this context must be able to be evaluated at compile-time."
        }
    }

    fun pushTypeSpecifierContext() {
        typeSpecifierContext++
    }

    fun popTypeSpecifierContext() {
        check(inTypeSpecifierContext()) { "Not in type specifier context" }
        typeSpecifierContext--
    }

    private fun inTypeSpecifierContext(): Boolean {
        return typeSpecifierContext > 0
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
