package org.cqframework.cql.cql2elm

import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.cqframework.cql.cql2elm.DataTypes.equal
import org.cqframework.cql.cql2elm.DataTypes.subTypeOf
import org.cqframework.cql.cql2elm.DataTypes.verifyCast
import org.cqframework.cql.cql2elm.DataTypes.verifyType
import org.cqframework.cql.cql2elm.LibraryBuilder.IdentifierScope
import org.cqframework.cql.cql2elm.model.*
import org.cqframework.cql.cql2elm.model.QueryContext
import org.cqframework.cql.cql2elm.model.invocation.*
import org.cqframework.cql.cql2elm.preprocessor.BaseInfo
import org.cqframework.cql.cql2elm.preprocessor.CodeDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.CodesystemDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.ConceptDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.ContextDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorElmCommonVisitor
import org.cqframework.cql.cql2elm.preprocessor.ExpressionDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.FunctionDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.IncludeDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.LibraryInfo
import org.cqframework.cql.cql2elm.preprocessor.ParameterDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.UsingDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.ValuesetDefinitionInfo
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.cqframework.cql.cql2elm.utils.Stack
import org.cqframework.cql.cql2elm.utils.logger
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.cqframework.cql.gen.cqlParser.*
import org.cqframework.cql.shared.BigDecimal
import org.hl7.cql.model.*
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.*
import org.hl7.elm_modelinfo.r1.ModelInfo

@Suppress(
    "LongMethod",
    "LargeClass",
    "CyclomaticComplexMethod",
    "NestedBlockDepth",
    "TooManyFunctions",
    "ComplexCondition",
    "TooGenericExceptionCaught",
    "ReturnCount",
    "ThrowsCount",
    "MaxLineLength",
    "ForbiddenComment",
    "LoopWithTooManyJumpStatements",
    "MagicNumber",
)
class Cql2ElmVisitor(libraryBuilder: LibraryBuilder, tokenStream: TokenStream) :
    CqlPreprocessorElmCommonVisitor(libraryBuilder, tokenStream) {
    private val systemMethodResolver = SystemMethodResolver(this, libraryBuilder)
    private val dateTimeLiteralParser = DateTimeLiteralParser(libraryBuilder, of)
    private val retrieveBuilder = RetrieveBuilder(libraryBuilder, of) { ctx -> getTrackBack(ctx) }
    private val timing =
        TimingOperatorDispatcher(
            libraryBuilder,
            of,
            track = { element, pt -> track(element, pt) },
            trackFromElement = { element, from -> track(element, from) },
        )

    /**
     * Resolve a System-library operator call against an already-constructed ELM node and return the
     * node. Collapses the repeated `of.createX().withOperand(...);
     * libraryBuilder.resolveCall("System", name, it); return it` pattern that dominates the simple
     * expression visitors.
     */
    private inline fun <E : Expression> systemCall(operatorName: String, result: E): E {
        libraryBuilder.resolveCall("System", operatorName, result)
        return result
    }

    private val definedExpressionDefinitions: MutableSet<String> = HashSet()
    private val forwards = Stack<ExpressionDefinitionInfo>()
    private val functionHeaders: MutableMap<FunctionDefinitionContext, FunctionHeader> = HashMap()

    // IdentityHashMaps are used here instead of HashMaps because the keys are mutated after
    // insertion
    private val functionHeadersByDef = IdentityHashMap<FunctionDef, FunctionHeader>()
    private val functionDefinitions = IdentityHashMap<FunctionHeader, FunctionDefinitionContext>()

    private val timingOperators = Stack<TimingOperatorContext>()
    val expressions: kotlin.collections.List<Expression> = ArrayList()
    private val contextDefinitions: MutableMap<String, Element?> = HashMap()

    override fun defaultResult(): Any? {
        return null
    }

    private inline fun <reified T> Any?.cast(): T {
        return this as T
    }

    override fun visitLibrary(ctx: LibraryContext): Any? {
        // Initialize the compiled library's identifier even when the source has no explicit
        // `library` declaration — downstream resolution paths read
        // compiledLibrary.identifier!!.id and must not NPE. preprocessLibrary below and the
        // main visitor's visitLibraryDefinition both overwrite this with the real identifier
        // when a library declaration is present.
        libraryBuilder.libraryIdentifier = org.hl7.elm.r1.VersionedIdentifier()

        // beginTranslation() loads the System library and adds its UsingDef. Must run before
        // preprocessLibrary so that subsequent `using X` definitions append AFTER the System
        // entry — preserving the historical order [System, ...userUsings].
        libraryBuilder.beginTranslation()

        // Pre-pass: walk top-level children once, populate libraryInfo with definition-index
        // entries (and trigger model loading / library-identifier assignment) so that forward
        // references resolve during the main visit. Replaces the previously separate
        // CqlPreprocessor walk.
        //
        // Chunk tracking is disabled for the duration of the pre-pass. The pre-pass calls
        // visit() on a handful of sub-trees (qualifiedIdentifier, versionSpecifier, identifier)
        // to extract strings; if annotations are enabled those visits would push chunks into
        // the same stack the main pass will populate, producing duplicate entries and
        // "Unable to find target chunk for insertion" errors. The main pass re-enables
        // chunk tracking (if it was on) after the pre-pass completes.
        val wasAnnotationEnabled = isAnnotationEnabled
        if (wasAnnotationEnabled) disableAnnotations()
        try {
            preprocessLibrary(ctx)
        } finally {
            if (wasAnnotationEnabled) enableAnnotations()
        }

        try {
            var lastResult: Any? = null

            // Main pass: call visit() on each top-level child so that chunk/tag processing
            // fires and ELM is emitted.
            for (i in 0 until ctx.childCount) {
                val tree = ctx.getChild(i)
                val terminalNode = tree as? TerminalNode
                if (terminalNode != null && terminalNode.symbol.type == Token.EOF) {
                    continue
                }
                val childResult = visit(tree!!)
                // Only set the last result if we received something useful
                if (childResult != null) {
                    lastResult = childResult
                }
            }

            // Return last result (consistent with super implementation and helps w/ testing)
            return lastResult
        } finally {
            libraryBuilder.endTranslation()
        }
    }

    override fun visitLibraryDefinition(ctx: LibraryDefinitionContext): VersionedIdentifier {
        val identifiers: MutableList<String> = visit(ctx.qualifiedIdentifier()).cast()
        val vid =
            of.createVersionedIdentifier()
                .withId(identifiers.removeAt(identifiers.size - 1))
                .withVersion(parseString(ctx.versionSpecifier()))
        if (identifiers.isNotEmpty()) {
            vid.system = libraryBuilder.resolveNamespaceUri(identifiers.joinToString("."), true)
        } else if (libraryBuilder.namespaceInfo != null) {
            vid.system = libraryBuilder.namespaceInfo.uri
        }
        libraryBuilder.libraryIdentifier = vid
        return vid
    }

    override fun visitUsingDefinition(ctx: UsingDefinitionContext): UsingDef? {
        val identifiers: MutableList<String> = visit(ctx.qualifiedIdentifier()).cast()
        val unqualifiedIdentifier: String = identifiers.removeAt(identifiers.size - 1)
        val namespaceName =
            when {
                identifiers.isNotEmpty() -> identifiers.joinToString(".")
                libraryBuilder.isWellKnownModelName(unqualifiedIdentifier) -> null
                libraryBuilder.namespaceInfo != null -> libraryBuilder.namespaceInfo.name
                else -> null
            }
        var path: String? = null
        var modelNamespace: NamespaceInfo? = null
        if (namespaceName != null) {
            val namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true)
            path = NamespaceManager.getPath(namespaceUri, unqualifiedIdentifier)
            modelNamespace = NamespaceInfo(namespaceName, namespaceUri!!)
        } else {
            path = unqualifiedIdentifier
        }
        val localIdentifier =
            if (ctx.localIdentifier() == null) unqualifiedIdentifier
            else parseString(ctx.localIdentifier())!!
        require(localIdentifier == unqualifiedIdentifier) {
            "Local identifiers for models must be the same as the name of the model in this release of the translator (Model $unqualifiedIdentifier, Called $localIdentifier)"
        }

        // The model was already calculated by CqlPreprocessorVisitor
        val usingDef = libraryBuilder.resolveUsingRef(localIdentifier)

        val ir = of.createIdentifierRef().withName(localIdentifier)
        track(
            ir,
            (if (ctx.localIdentifier() == null) ctx.qualifiedIdentifier()
            else ctx.localIdentifier())!!,
        )
        libraryBuilder.pushIdentifier(ir, usingDef, IdentifierScope.GLOBAL)

        return usingDef
    }

    override fun getModel(
        modelNamespace: NamespaceInfo?,
        modelName: String?,
        version: String?,
        localIdentifier: String,
    ): Model {
        var modelName = modelName
        var version = version
        if (modelName == null) {
            val defaultUsing = this.libraryInfo.defaultUsingDefinition!!
            modelName = defaultUsing.name
            version = defaultUsing.version
        }
        val modelIdentifier =
            ModelIdentifier(id = modelName, version = version, system = modelNamespace?.uri)
        return libraryBuilder.getModel(modelIdentifier, localIdentifier)
    }

    private fun getLibraryPath(namespaceName: String?, unqualifiedIdentifier: String): String {
        if (namespaceName != null) {
            val namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true)
            return NamespaceManager.getPath(namespaceUri, unqualifiedIdentifier)
        }
        return unqualifiedIdentifier
    }

    override fun visitIncludeDefinition(ctx: IncludeDefinitionContext): IncludeDef {
        val identifiers: MutableList<String> = visit(ctx.qualifiedIdentifier()).cast()
        val unqualifiedIdentifier: String = identifiers.removeAt(identifiers.size - 1)
        var namespaceName =
            if (identifiers.isNotEmpty()) identifiers.joinToString(".")
            else if (libraryBuilder.namespaceInfo != null) libraryBuilder.namespaceInfo.name
            else null
        var path = getLibraryPath(namespaceName, unqualifiedIdentifier)
        var library =
            of.createIncludeDef()
                .withLocalIdentifier(
                    if (ctx.localIdentifier() == null) unqualifiedIdentifier
                    else parseString(ctx.localIdentifier())
                )
                .withPath(path)
                .withVersion(parseString(ctx.versionSpecifier()))

        // TODO: This isn't great because it complicates the loading process (and results in the
        // source being loaded twice in the general case) But the full fix is to introduce source
        // resolution/caching to enable this layer to determine whether the library identifier
        // resolved with the namespace
        if (!libraryBuilder.canResolveLibrary(library)) {
            namespaceName =
                when {
                    identifiers.isNotEmpty() -> identifiers.joinToString(".")
                    libraryBuilder.isWellKnownLibraryName(unqualifiedIdentifier) -> null
                    libraryBuilder.namespaceInfo != null -> libraryBuilder.namespaceInfo.name
                    else -> null
                }
            path = getLibraryPath(namespaceName, unqualifiedIdentifier)
            library =
                of.createIncludeDef()
                    .withLocalIdentifier(
                        if (ctx.localIdentifier() == null) unqualifiedIdentifier
                        else parseString(ctx.localIdentifier())
                    )
                    .withPath(path)
                    .withVersion(parseString(ctx.versionSpecifier()))
        }
        libraryBuilder.addInclude(library)

        val ir = of.createIdentifierRef().withName(library.localIdentifier)
        track(
            ir,
            (if (ctx.localIdentifier() == null) ctx.qualifiedIdentifier()
            else ctx.localIdentifier())!!,
        )
        libraryBuilder.pushIdentifier(ir, library, IdentifierScope.GLOBAL)

        return library
    }

    override fun visitParameterDefinition(ctx: ParameterDefinitionContext): ParameterDef {
        val param =
            of.createParameterDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withDefault(parseLiteralExpression(ctx.expression()))
                .withParameterTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
        var paramType: DataType? = null
        if (param.parameterTypeSpecifier != null) {
            paramType = param.parameterTypeSpecifier!!.resultType
        }
        if (param.default != null) {
            if (paramType != null) {
                libraryBuilder.verifyType(param.default!!.resultType!!, paramType)
            } else {
                paramType = param.default!!.resultType
            }
        }
        requireNotNull(paramType) {
            "Could not determine parameter type for parameter ${param.name}."
        }
        param.resultType = paramType
        if (param.default != null) {
            param.default = libraryBuilder.ensureCompatible(param.default, paramType)
        }
        libraryBuilder.addParameter(param)

        val ir = of.createIdentifierRef().withName(param.name)
        track(ir, ctx.identifier())
        libraryBuilder.pushIdentifier(ir, param, IdentifierScope.GLOBAL)

        return param
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

    override fun visitAccessModifier(ctx: AccessModifierContext): AccessModifier {
        return when (ctx.text.lowercase()) {
            "public" -> AccessModifier.PUBLIC
            "private" -> AccessModifier.PRIVATE
            else ->
                throw IllegalArgumentException("Unknown access modifier ${ctx.text.lowercase()}.")
        }
    }

    override fun visitCodesystemDefinition(ctx: CodesystemDefinitionContext): CodeSystemDef {
        val cs =
            of.createCodeSystemDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.codesystemId()))
                .withVersion(parseString(ctx.versionSpecifier()))
        if (libraryBuilder.isCompatibleWith("1.5")) {
            cs.resultType = libraryBuilder.resolveTypeName("System", "CodeSystem")
        } else {
            cs.resultType = ListType(libraryBuilder.resolveTypeName("System", "Code")!!)
        }
        libraryBuilder.addCodeSystem(cs)

        val ir = of.createIdentifierRef().withName(cs.name)
        track(ir, ctx.identifier())
        libraryBuilder.pushIdentifier(ir, cs, IdentifierScope.GLOBAL)

        return cs
    }

    override fun visitCodesystemIdentifier(ctx: CodesystemIdentifierContext): CodeSystemRef {
        val libraryName = parseString(ctx.libraryIdentifier())
        val name = parseString(ctx.identifier())!!
        val def: CodeSystemDef?
        if (libraryName != null) {
            def = libraryBuilder.resolveLibrary(libraryName).resolveCodeSystemRef(name)
            if (def != null) {
                libraryBuilder.checkAccessLevel(libraryName, name, def.accessLevel!!)
            }
        } else {
            def = libraryBuilder.resolveCodeSystemRef(name)
        }
        requireNotNull(def) { "Could not resolve reference to code system $name." }
        return of.createCodeSystemRef()
            .withLibraryName(libraryName)
            .withName(name)
            .withResultType(def.resultType)
    }

    override fun visitCodeIdentifier(ctx: CodeIdentifierContext): CodeRef {
        val libraryName = parseString(ctx.libraryIdentifier())
        val name = parseString(ctx.identifier())!!
        val def: CodeDef?
        if (libraryName != null) {
            def = libraryBuilder.resolveLibrary(libraryName).resolveCodeRef(name)
            if (def != null) {
                libraryBuilder.checkAccessLevel(libraryName, name, def.accessLevel!!)
            }
        } else {
            def = libraryBuilder.resolveCodeRef(name)
        }
        requireNotNull(def) {
            // ERROR:
            "Could not resolve reference to code $name."
        }
        return of.createCodeRef()
            .withLibraryName(libraryName)
            .withName(name)
            .withResultType(def.resultType)
    }

    override fun visitValuesetDefinition(ctx: ValuesetDefinitionContext): ValueSetDef {
        val vs =
            of.createValueSetDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.valuesetId()))
                .withVersion(parseString(ctx.versionSpecifier()))
        if (ctx.codesystems() != null) {
            for (codesystem in ctx.codesystems()!!.codesystemIdentifier()) {
                val cs =
                    visit(codesystem) as CodeSystemRef?
                        ?: throw IllegalArgumentException(
                            "Could not resolve reference to code system ${codesystem.text}."
                        )
                vs.codeSystem.add(cs)
            }
        }
        if (libraryBuilder.isCompatibleWith("1.5")) {
            vs.resultType = libraryBuilder.resolveTypeName("System", "ValueSet")
        } else {
            vs.resultType = ListType(libraryBuilder.resolveTypeName("System", "Code")!!)
        }
        libraryBuilder.addValueSet(vs)

        val ir = of.createIdentifierRef().withName(vs.name)
        track(ir, ctx.identifier())
        libraryBuilder.pushIdentifier(ir, vs, IdentifierScope.GLOBAL)

        return vs
    }

    override fun visitCodeDefinition(ctx: CodeDefinitionContext): CodeDef {
        val cd =
            of.createCodeDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.codeId()))

        val cs = visit(ctx.codesystemIdentifier())
        if (cs is CodeSystemRef) {
            cd.codeSystem = cs
        }

        if (ctx.displayClause() != null) {
            cd.display = parseString(ctx.displayClause()!!.STRING())
        }
        cd.resultType = libraryBuilder.resolveTypeName("Code")
        libraryBuilder.addCode(cd)

        val ir = of.createIdentifierRef().withName(cd.name)
        track(ir, ctx.identifier())
        libraryBuilder.pushIdentifier(ir, cd, IdentifierScope.GLOBAL)

        return cd
    }

    override fun visitConceptDefinition(ctx: ConceptDefinitionContext): ConceptDef {
        val cd =
            of.createConceptDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
        for (ci in ctx.codeIdentifier()) {
            cd.code.add(visit(ci) as CodeRef)
        }
        if (ctx.displayClause() != null) {
            cd.display = parseString(ctx.displayClause()!!.STRING())
        }
        cd.resultType = libraryBuilder.resolveTypeName("Concept")
        libraryBuilder.addConcept(cd)
        return cd
    }

    override fun visitNamedTypeSpecifier(ctx: NamedTypeSpecifierContext): NamedTypeSpecifier? {
        val qualifiers = parseQualifiers(ctx)
        val modelIdentifier = getModelIdentifier(qualifiers)
        val identifier =
            getTypeIdentifier(qualifiers, parseString(ctx.referentialOrTypeNameIdentifier())!!)
        val retrievedResult =
            libraryBuilder.getNamedTypeSpecifierResult("$modelIdentifier:$identifier")
        if (retrievedResult != null) {
            return if (retrievedResult.hasError()) {
                null
            } else retrievedResult.underlyingResultIfExists
        }
        val resultType =
            libraryBuilder.resolveTypeName(modelIdentifier, identifier)
                ?: throw CqlSemanticException(
                    "Could not find type for model: $modelIdentifier and name: $identifier",
                    getTrackBack(ctx),
                )
        val result =
            of.createNamedTypeSpecifier().withName(libraryBuilder.dataTypeToQName(resultType))

        // Fluent API would be nice here, but resultType isn't part of the model so...
        result.resultType = resultType
        return result
    }

    private fun isUnfilteredContext(contextName: String?): Boolean {
        return contextName == "Unfiltered" ||
            libraryBuilder.isCompatibilityLevel3 && contextName == "Population"
    }

    override fun visitContextDefinition(ctx: ContextDefinitionContext): Any {
        val modelIdentifier: String? = parseString(ctx.modelIdentifier())
        val unqualifiedIdentifier = parseString(ctx.identifier())!!
        this.currentContext =
            if (modelIdentifier != null) "$modelIdentifier.$unqualifiedIdentifier"
            else (unqualifiedIdentifier)
        if (!isUnfilteredContext(unqualifiedIdentifier)) {
            val modelContext: ModelContext? =
                libraryBuilder.resolveContextName(modelIdentifier, unqualifiedIdentifier)

            // If this is the first time a context definition is encountered, construct a context
            // definition:
            // define <Context> = element of [<Context model type>]
            var modelContextDefinition: Element? = this.contextDefinitions[modelContext!!.name]
            if (modelContextDefinition == null) {
                if (libraryBuilder.hasUsings()) {
                    val modelInfo: ModelInfo =
                        if (modelIdentifier == null)
                            libraryBuilder.getModel(this.libraryInfo.defaultModelName).modelInfo
                        else libraryBuilder.getModel(modelIdentifier).modelInfo
                    // String contextTypeName = modelContext.getName();
                    // DataType contextType = libraryBuilder.resolveTypeName(modelInfo.getName(),
                    // contextTypeName);
                    val contextType: DataType = modelContext.type
                    modelContextDefinition = libraryBuilder.resolveParameterRef(modelContext.name)
                    if (modelContextDefinition != null) {
                        this.contextDefinitions[modelContext.name] = modelContextDefinition
                    } else {
                        val contextRetrieve: Retrieve =
                            of.createRetrieve()
                                .withDataType(libraryBuilder.dataTypeToQName(contextType))
                        track(contextRetrieve, ctx)
                        contextRetrieve.resultType = ListType(contextType)
                        val contextClassIdentifier: String? = (contextType as ClassType).identifier
                        if (contextClassIdentifier != null) {
                            contextRetrieve.templateId = contextClassIdentifier
                        }
                        modelContextDefinition =
                            of.createExpressionDef()
                                .withName(unqualifiedIdentifier)
                                .withContext(this.currentContext)
                                .withExpression(
                                    of.createSingletonFrom().withOperand(contextRetrieve)
                                )
                        track(modelContextDefinition, ctx)
                        modelContextDefinition.expression!!.resultType = contextType
                        modelContextDefinition.resultType = contextType
                        libraryBuilder.addExpression(modelContextDefinition)
                        this.contextDefinitions[modelContext.name] = modelContextDefinition
                    }
                } else {
                    modelContextDefinition =
                        of.createExpressionDef()
                            .withName(unqualifiedIdentifier)
                            .withContext(this.currentContext)
                            .withExpression(of.createNull())
                    track(modelContextDefinition, ctx)
                    modelContextDefinition.expression!!.resultType =
                        libraryBuilder.resolveTypeName("System", "Any")
                    modelContextDefinition.resultType =
                        modelContextDefinition.expression!!.resultType
                    libraryBuilder.addExpression(modelContextDefinition)
                    this.contextDefinitions[modelContext.name] = modelContextDefinition
                }
            }
        }
        val contextDef: ContextDef = of.createContextDef().withName(this.currentContext)
        track(contextDef, ctx)
        if (libraryBuilder.isCompatibleWith("1.5")) {
            libraryBuilder.addContext(contextDef)
        }
        return this.currentContext
    }

    private fun isImplicitContextExpressionDef(def: ExpressionDef): Boolean {
        for (e in this.contextDefinitions.values) {
            if (def === e) {
                return true
            }
        }
        return false
    }

    private fun removeImplicitContextExpressionDef(def: ExpressionDef) {
        for ((key, value) in this.contextDefinitions) {
            if (def === value) {
                this.contextDefinitions.remove(key)
                break
            }
        }
    }

    private fun internalVisitExpressionDefinition(
        ctx: ExpressionDefinitionContext
    ): ExpressionDef? {
        val identifier = parseString(ctx.identifier())!!
        var def = libraryBuilder.resolveExpressionRef(identifier)

        // First time visiting this expression definition, create a lightweight ExpressionDef to be
        // used to output a hiding warning message
        //
        // If it's the second time around, we'll be able to resolve it, and we can assume it's
        // already on the hiding stack.
        if (def == null) {
            val hollowExpressionDef =
                of.createExpressionDef().withName(identifier).withContext(this.currentContext)

            val ir = of.createIdentifierRef().withName(identifier)
            track(ir, ctx.identifier())

            libraryBuilder.pushIdentifier(ir, hollowExpressionDef, IdentifierScope.GLOBAL)
        }
        if (def == null || isImplicitContextExpressionDef(def)) {
            if (def != null && isImplicitContextExpressionDef(def)) {
                libraryBuilder.removeExpression(def)
                removeImplicitContextExpressionDef(def)
            }
            libraryBuilder.scopeManager.withExpressionContext(this.currentContext) {
                libraryBuilder.scopeManager.withExpressionDefinition(identifier) {
                    val newDef =
                        of.createExpressionDef()
                            .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                            .withName(identifier)
                            .withContext(this.currentContext)
                            .withExpression(visit(ctx.expression()) as Expression?)
                    if (newDef.expression != null) {
                        newDef.resultType = newDef.expression!!.resultType
                    }
                    libraryBuilder.addExpression(newDef)
                    def = newDef
                }
            }
        }
        return def
    }

    override fun visitExpressionDefinition(ctx: ExpressionDefinitionContext): ExpressionDef? {
        return libraryBuilder.scopeManager.withIdentifierScope {
            val expressionDef = internalVisitExpressionDefinition(ctx)
            if (this.forwards.isEmpty() || this.forwards.peek().name != expressionDef!!.name) {
                require(!this.definedExpressionDefinitions.contains(expressionDef!!.name)) {
                    // ERROR:
                    "Identifier ${expressionDef.name} is already in use in this library."
                }

                // Track defined expression definitions locally, otherwise duplicate expression
                // definitions will be missed because they are overwritten by name when they are
                // encountered by the preprocessor.
                this.definedExpressionDefinitions.add(expressionDef.name!!)
            }
            expressionDef
        }
    }

    override fun visitStringLiteral(ctx: StringLiteralContext): Literal {
        val stringLiteral = libraryBuilder.createLiteral(parseString(ctx.STRING()))
        // Literals are never actually pushed to the stack. This just emits a warning if the literal
        // is hiding something

        val ir = of.createIdentifierRef().withName(stringLiteral.value)
        track(ir, ctx.STRING())
        libraryBuilder.pushIdentifier(ir, stringLiteral)

        return stringLiteral
    }

    override fun visitSimpleStringLiteral(ctx: SimpleStringLiteralContext): Literal {
        return libraryBuilder.createLiteral(parseString(ctx.STRING()))
    }

    override fun visitBooleanLiteral(ctx: BooleanLiteralContext): Literal {
        return libraryBuilder.createLiteral(ctx.text.toBoolean())
    }

    override fun visitIntervalSelector(ctx: IntervalSelectorContext): Any {
        return libraryBuilder.createInterval(
            parseExpression(ctx.expression(0)),
            ctx.getChild(1)!!.text == "[",
            parseExpression(ctx.expression(1)),
            ctx.getChild(5)!!.text == "]",
        )
    }

    override fun visitTupleElementSelector(ctx: TupleElementSelectorContext): TupleElement {
        val result =
            of.createTupleElement()
                .withName(parseString(ctx.referentialIdentifier()))
                .withValue(parseExpression(ctx.expression()))
        return result
    }

    override fun visitTupleSelector(ctx: TupleSelectorContext): Tuple {
        val tuple = of.createTuple()
        val elements = mutableListOf<TupleTypeElement>()
        for (elementContext in ctx.tupleElementSelector()) {
            val element = visit(elementContext) as TupleElement
            elements.add(TupleTypeElement(element.name!!, element.value!!.resultType!!))
            tuple.element.add(element)
        }
        tuple.resultType = TupleType(elements)
        return tuple
    }

    override fun visitInstanceElementSelector(
        ctx: InstanceElementSelectorContext
    ): InstanceElement {
        val name = parseString(ctx.referentialIdentifier())
        val exp = parseExpression(ctx.expression())
        val result = of.createInstanceElement().withName(name).withValue(exp)
        return result
    }

    override fun visitInstanceSelector(ctx: InstanceSelectorContext): Instance {
        val instance: Instance = of.createInstance()
        val classTypeSpecifier = visitNamedTypeSpecifier(ctx.namedTypeSpecifier())!!
        instance.classType = classTypeSpecifier.name
        instance.resultType = classTypeSpecifier.resultType
        for (elementContext in ctx.instanceElementSelector()) {
            val element = visit(elementContext) as InstanceElement
            val resolution: PropertyResolution? =
                libraryBuilder.resolveProperty(classTypeSpecifier.resultType, element.name!!)
            element.value = libraryBuilder.ensureCompatible(element.value, resolution!!.type)
            element.name = resolution.name
            require(resolution.targetMap == null) {
                "Target Mapping in instance selectors not yet supported"
            }
            instance.element.add(element)
        }
        return instance
    }

    override fun visitCodeSelector(ctx: CodeSelectorContext): Code {
        val code = of.createCode()
        code.code = parseString(ctx.STRING())
        code.system = visit(ctx.codesystemIdentifier()) as CodeSystemRef?
        if (ctx.displayClause() != null) {
            code.display = parseString(ctx.displayClause()!!.STRING())
        }
        code.resultType = libraryBuilder.resolveTypeName("System", "Code")
        return code
    }

    override fun visitConceptSelector(ctx: ConceptSelectorContext): Concept {
        val concept = of.createConcept()
        if (ctx.displayClause() != null) {
            concept.display = parseString(ctx.displayClause()!!.STRING())
        }
        for (codeContext in ctx.codeSelector()) {
            concept.code.add(visit(codeContext) as Code)
        }
        concept.resultType = libraryBuilder.resolveTypeName("System", "Concept")
        return concept
    }

    override fun visitListSelector(ctx: ListSelectorContext): org.hl7.elm.r1.List {
        val elementTypeSpecifier = parseTypeSpecifier(ctx.typeSpecifier())
        val list = of.createList()
        var listType: ListType? = null
        if (elementTypeSpecifier != null) {
            val listTypeSpecifier =
                of.createListTypeSpecifier().withElementType(elementTypeSpecifier)
            track(listTypeSpecifier, ctx.typeSpecifier()!!)
            listType = ListType(elementTypeSpecifier.resultType!!)
            listTypeSpecifier.resultType = listType
        }
        var elementType = elementTypeSpecifier?.resultType
        var inferredElementType: DataType? = null
        var initialInferredElementType: DataType? = null
        val elements: MutableList<Expression> = ArrayList()
        for (elementContext in ctx.expression()) {
            val element =
                parseExpression(elementContext)
                    ?: @Suppress("TooGenericExceptionThrown")
                    throw RuntimeException("Element failed to parse")
            if (elementType != null) {
                libraryBuilder.verifyType(element.resultType!!, elementType)
            } else {
                if (initialInferredElementType == null) {
                    initialInferredElementType = element.resultType
                    inferredElementType = initialInferredElementType
                } else {
                    // Once a list type is inferred as Any, keep it that way
                    // The only potential exception to this is if the element responsible for the
                    // inferred type of Any is a null
                    val compatibleType =
                        libraryBuilder.findCompatibleType(inferredElementType, element.resultType)
                    inferredElementType =
                        if (
                            compatibleType != null &&
                                (inferredElementType !=
                                    libraryBuilder.resolveTypeName("System", "Any") ||
                                    initialInferredElementType ==
                                        libraryBuilder.resolveTypeName("System", "Any"))
                        ) {
                            compatibleType
                        } else {
                            libraryBuilder.resolveTypeName("System", "Any")
                        }
                }
            }
            elements.add(element)
        }
        if (elementType == null) {
            elementType = inferredElementType ?: libraryBuilder.resolveTypeName("System", "Any")
        }
        for (element in elements) {
            if (!elementType!!.isSuperTypeOf(element.resultType!!)) {
                val conversion =
                    libraryBuilder.findConversion(
                        element.resultType!!,
                        elementType,
                        implicit = true,
                        allowPromotionAndDemotion = false,
                    )
                if (conversion != null) {
                    list.element.add(libraryBuilder.convertExpression(element, conversion))
                } else {
                    list.element.add(element)
                }
            } else {
                list.element.add(element)
            }
        }
        if (listType == null) {
            listType = ListType(elementType!!)
        }
        list.resultType = listType
        return list
    }

    override fun visitTimeLiteral(ctx: TimeLiteralContext): Time {
        var input = ctx.text
        if (input.startsWith("@")) {
            input = input.substring(1)
        }
        return dateTimeLiteralParser.parseTime(input)
    }

    override fun visitDateLiteral(ctx: DateLiteralContext): Any {
        var input = ctx.text
        if (input.startsWith("@")) {
            input = input.substring(1)
        }
        return dateTimeLiteralParser.parseDateTime(input)
    }

    override fun visitDateTimeLiteral(ctx: DateTimeLiteralContext): Any {
        var input = ctx.text
        if (input.startsWith("@")) {
            input = input.substring(1)
        }
        return dateTimeLiteralParser.parseDateTime(input)
    }

    override fun visitNullLiteral(ctx: NullLiteralContext): Null {
        val result = of.createNull()
        result.resultType = libraryBuilder.resolveTypeName("System", "Any")
        return result
    }

    override fun visitNumberLiteral(ctx: NumberLiteralContext): Expression {
        return libraryBuilder.createNumberLiteral(ctx.NUMBER().text)
    }

    override fun visitSimpleNumberLiteral(ctx: SimpleNumberLiteralContext): Expression {
        return libraryBuilder.createNumberLiteral(ctx.NUMBER().text)
    }

    override fun visitLongNumberLiteral(ctx: LongNumberLiteralContext): Literal {
        var input = ctx.LONGNUMBER().text
        if (input.endsWith("L")) {
            input = input.substring(0, input.length - 1)
        }
        return libraryBuilder.createLongNumberLiteral(input)
    }

    private fun parseDecimal(value: String): BigDecimal {
        return try {
            BigDecimal(value)
        } catch (@Suppress("SwallowedException") e: Exception) {
            throw IllegalArgumentException("Could not parse number literal: $value")
        }
    }

    override fun visitQuantity(ctx: QuantityContext): Expression {
        return if (ctx.unit() != null) {
            libraryBuilder.createQuantity(
                parseDecimal(ctx.NUMBER().text),
                (parseString(ctx.unit()))!!,
            )
        } else {
            libraryBuilder.createNumberLiteral(ctx.NUMBER().text)
        }
    }

    private fun getQuantity(source: Expression?): Quantity {
        if (source is Literal) {
            return libraryBuilder.createQuantity(parseDecimal(source.value!!), "1")
        } else if (source is Quantity) {
            return source
        }
        throw IllegalArgumentException("Could not create quantity from source expression.")
    }

    override fun visitRatio(ctx: RatioContext): Ratio {
        val numerator = getQuantity(visit(ctx.quantity(0)!!) as Expression?)
        val denominator = getQuantity(visit(ctx.quantity(1)!!) as Expression?)
        return libraryBuilder.createRatio(numerator, denominator)
    }

    override fun visitNotExpression(ctx: NotExpressionContext): Not {
        val result = of.createNot().withOperand(parseExpression(ctx.expression()))
        return systemCall("Not", result)
    }

    override fun visitExistenceExpression(ctx: ExistenceExpressionContext): Exists {
        val result = of.createExists().withOperand(parseExpression(ctx.expression()))
        return systemCall("Exists", result)
    }

    override fun visitMultiplicationExpressionTerm(
        ctx: MultiplicationExpressionTermContext
    ): BinaryExpression {
        val exp: BinaryExpression?
        val operatorName: String?
        when (ctx.getChild(1)!!.text) {
            "*" -> {
                exp = of.createMultiply()
                operatorName = "Multiply"
            }
            "/" -> {
                exp = of.createDivide()
                operatorName = "Divide"
            }
            "div" -> {
                exp = of.createTruncatedDivide()
                operatorName = "TruncatedDivide"
            }
            "mod" -> {
                exp = of.createModulo()
                operatorName = "Modulo"
            }
            else ->
                throw IllegalArgumentException("Unsupported operator: ${ctx.getChild(1)!!.text}.")
        }
        exp.withOperand(
            listOf(
                parseExpression(ctx.expressionTerm(0))!!,
                parseExpression(ctx.expressionTerm(1))!!,
            )
        )
        libraryBuilder.resolveCall("System", operatorName, (exp))
        return exp
    }

    override fun visitPowerExpressionTerm(ctx: PowerExpressionTermContext): Power {
        val power =
            of.createPower()
                .withOperand(
                    listOf(
                        parseExpression(ctx.expressionTerm(0))!!,
                        parseExpression(ctx.expressionTerm(1))!!,
                    )
                )
        return systemCall("Power", power)
    }

    override fun visitPolarityExpressionTerm(ctx: PolarityExpressionTermContext): Any? {
        if (ctx.getChild(0)!!.text == "+") {
            return visit(ctx.expressionTerm())
        }
        val result = of.createNegate().withOperand(parseExpression(ctx.expressionTerm()))
        return systemCall("Negate", result)
    }

    override fun visitAdditionExpressionTerm(ctx: AdditionExpressionTermContext): Expression {
        var exp: Expression?
        val operatorName: String?
        when (ctx.getChild(1)!!.text) {
            "+" -> {
                exp = of.createAdd()
                operatorName = "Add"
            }
            "-" -> {
                exp = of.createSubtract()
                operatorName = "Subtract"
            }
            "&" -> {
                exp = of.createConcatenate()
                operatorName = "Concatenate"
            }
            else ->
                throw IllegalArgumentException("Unsupported operator: ${ctx.getChild(1)!!.text}.")
        }
        if (exp is BinaryExpression) {
            exp.withOperand(
                listOf(
                    parseExpression(ctx.expressionTerm(0))!!,
                    parseExpression(ctx.expressionTerm(1))!!,
                )
            )
            libraryBuilder.resolveCall("System", operatorName, (exp as BinaryExpression?)!!)
            if (exp.resultType === libraryBuilder.resolveTypeName("System", "String")) {
                val concatenate: Concatenate = of.createConcatenate()
                concatenate.operand.addAll(exp.operand)
                concatenate.resultType = exp.resultType
                exp = concatenate
            }
        } else {
            val concatenate = exp as Concatenate?
            concatenate!!.withOperand(
                listOf(
                    parseExpression(ctx.expressionTerm(0))!!,
                    parseExpression(ctx.expressionTerm(1))!!,
                )
            )
            for (i in concatenate.operand.indices) {
                val operand: Expression = concatenate.operand[i]
                val empty: Literal = libraryBuilder.createLiteral("")
                val params: ArrayList<Expression> = ArrayList()
                params.add(operand)
                params.add(empty)
                val coalesce = libraryBuilder.resolveFunction("System", "Coalesce", params)!!
                concatenate.operand[i] = coalesce
            }
            libraryBuilder.resolveCall("System", operatorName, concatenate)
        }
        return exp
    }

    override fun visitPredecessorExpressionTerm(ctx: PredecessorExpressionTermContext): Any {
        return libraryBuilder.buildPredecessor(parseExpression(ctx.expressionTerm()))
    }

    override fun visitSuccessorExpressionTerm(ctx: SuccessorExpressionTermContext): Any {
        return libraryBuilder.buildSuccessor(parseExpression(ctx.expressionTerm()))
    }

    override fun visitElementExtractorExpressionTerm(
        ctx: ElementExtractorExpressionTermContext
    ): SingletonFrom {
        val result = of.createSingletonFrom().withOperand(parseExpression(ctx.expressionTerm()))
        return systemCall("SingletonFrom", result)
    }

    override fun visitPointExtractorExpressionTerm(
        ctx: PointExtractorExpressionTermContext
    ): PointFrom {
        val result = of.createPointFrom().withOperand(parseExpression(ctx.expressionTerm()))
        return systemCall("PointFrom", result)
    }

    override fun visitTypeExtentExpressionTerm(ctx: TypeExtentExpressionTermContext): Any {
        val extent = parseString(ctx.getChild(0))
        val targetType = parseTypeSpecifier(ctx.namedTypeSpecifier())
        return when (extent) {
            "minimum" -> {
                libraryBuilder.buildMinimum(targetType!!.resultType)
            }
            "maximum" -> {
                libraryBuilder.buildMaximum(targetType!!.resultType)
            }
            else -> throw IllegalArgumentException("Unknown extent: $extent")
        }
    }

    override fun visitTimeBoundaryExpressionTerm(ctx: TimeBoundaryExpressionTermContext): Any {
        val result: UnaryExpression?
        val operatorName: String?
        if (ctx.getChild(0)!!.text == "start") {
            result = of.createStart().withOperand(parseExpression(ctx.expressionTerm()))
            operatorName = "Start"
        } else {
            result = of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
            operatorName = "End"
        }
        libraryBuilder.resolveCall("System", operatorName, result)
        return result
    }

    private fun parseComparableDateTimePrecision(dateTimePrecision: String): DateTimePrecision? {
        return parseDateTimePrecision(
            dateTimePrecision,
            precisionRequired = true,
            allowWeeks = false,
        )
    }

    private fun parseComparableDateTimePrecision(
        dateTimePrecision: String?,
        precisionRequired: Boolean,
    ): DateTimePrecision? {
        return parseDateTimePrecision(dateTimePrecision, precisionRequired, false)
    }

    private fun parseDateTimePrecision(
        dateTimePrecision: String?,
        precisionRequired: Boolean = true,
        allowWeeks: Boolean = true,
    ): DateTimePrecision? {
        if (dateTimePrecision == null) {
            require(!precisionRequired) { "dateTimePrecision is null" }
            return null
        }
        return when (dateTimePrecision) {
            "a",
            "year",
            "years" -> DateTimePrecision.YEAR
            "mo",
            "month",
            "months" -> DateTimePrecision.MONTH
            "wk",
            "week",
            "weeks" -> {
                require(allowWeeks) { "Week precision cannot be used for comparisons." }
                DateTimePrecision.WEEK
            }
            "d",
            "day",
            "days" -> DateTimePrecision.DAY
            "h",
            "hour",
            "hours" -> DateTimePrecision.HOUR
            "min",
            "minute",
            "minutes" -> DateTimePrecision.MINUTE
            "s",
            "second",
            "seconds" -> DateTimePrecision.SECOND
            "ms",
            "millisecond",
            "milliseconds" -> DateTimePrecision.MILLISECOND
            else -> throw IllegalArgumentException("Unknown precision '$dateTimePrecision'.")
        }
    }

    override fun visitTimeUnitExpressionTerm(ctx: TimeUnitExpressionTermContext): Any {
        val component = ctx.dateTimeComponent().text
        val result: UnaryExpression?
        val operatorName: String?
        when (component) {
            "date" -> {
                result = of.createDateFrom().withOperand(parseExpression(ctx.expressionTerm()))
                operatorName = "DateFrom"
            }
            "time" -> {
                result = of.createTimeFrom().withOperand(parseExpression(ctx.expressionTerm()))
                operatorName = "TimeFrom"
            }
            "timezone" -> {
                require(libraryBuilder.isCompatibilityLevel3) {
                    "Timezone keyword is only valid in 1.3 or lower"
                }
                result = of.createTimezoneFrom().withOperand(parseExpression(ctx.expressionTerm()))
                operatorName = "TimezoneFrom"
            }
            "timezoneoffset" -> {
                result =
                    of.createTimezoneOffsetFrom().withOperand(parseExpression(ctx.expressionTerm()))
                operatorName = "TimezoneOffsetFrom"
            }
            "year",
            "month",
            "day",
            "hour",
            "minute",
            "second",
            "millisecond" -> {
                result =
                    of.createDateTimeComponentFrom()
                        .withOperand(parseExpression(ctx.expressionTerm()))
                        .withPrecision(parseDateTimePrecision(component))
                operatorName = "DateTimeComponentFrom"
            }
            "week" ->
                throw IllegalArgumentException("Date/time values do not have a week component.")
            else -> throw IllegalArgumentException("Unknown precision '$component'.")
        }
        libraryBuilder.resolveCall("System", operatorName, result)
        return result
    }

    override fun visitDurationExpressionTerm(ctx: DurationExpressionTermContext): DurationBetween {
        // duration in days of X <=> days between start of X and end of X
        val operand = parseExpression(ctx.expressionTerm())
        val start = of.createStart().withOperand(operand)
        libraryBuilder.resolveCall("System", "Start", start)
        val end = of.createEnd().withOperand(operand)
        libraryBuilder.resolveCall("System", "End", end)
        val result =
            of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(listOf(start, end))
        return systemCall("DurationBetween", result)
    }

    override fun visitDifferenceExpressionTerm(
        ctx: DifferenceExpressionTermContext
    ): DifferenceBetween {
        // difference in days of X <=> difference in days between start of X and end of X
        val operand = parseExpression(ctx.expressionTerm())
        val start = of.createStart().withOperand(operand)
        libraryBuilder.resolveCall("System", "Start", start)
        val end = of.createEnd().withOperand(operand)
        libraryBuilder.resolveCall("System", "End", end)
        val result =
            of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(listOf(start, end))
        return systemCall("DifferenceBetween", result)
    }

    override fun visitBetweenExpression(ctx: BetweenExpressionContext): Expression {
        // X properly? between Y and Z
        val first = parseExpression(ctx.expression())!!
        val second = parseExpression(ctx.expressionTerm(0))!!
        val third = parseExpression(ctx.expressionTerm(1))
        val isProper = ctx.getChild(1)!!.text == "properly"
        return if (first.resultType is IntervalType) {
            val result =
                if (isProper) of.createProperIncludedIn()
                else
                    of.createIncludedIn()
                        .withOperand(
                            listOf(first, libraryBuilder.createInterval(second, true, third, true))
                        )
            libraryBuilder.resolveCall(
                "System",
                if (isProper) "ProperIncludedIn" else "IncludedIn",
                result,
            )
            result
        } else {
            val result: BinaryExpression =
                of.createAnd()
                    .withOperand(
                        listOf(
                            (if (isProper) of.createGreater() else of.createGreaterOrEqual())
                                .withOperand(listOf(first, second)),
                            (if (isProper) of.createLess() else of.createLessOrEqual()).withOperand(
                                listOf(first, third!!)
                            ),
                        )
                    )
            libraryBuilder.resolveCall(
                "System",
                if (isProper) "Greater" else "GreaterOrEqual",
                (result.operand[0] as BinaryExpression),
            )
            libraryBuilder.resolveCall(
                "System",
                if (isProper) "Less" else "LessOrEqual",
                (result.operand[1] as BinaryExpression),
            )
            libraryBuilder.resolveCall("System", "And", result)
            result
        }
    }

    override fun visitDurationBetweenExpression(ctx: DurationBetweenExpressionContext): Any {
        val result: BinaryExpression =
            of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(
                    listOf(
                        parseExpression(ctx.expressionTerm(0))!!,
                        parseExpression(ctx.expressionTerm(1))!!,
                    )
                )
        return systemCall("DurationBetween", result)
    }

    override fun visitDifferenceBetweenExpression(ctx: DifferenceBetweenExpressionContext): Any {
        val result: BinaryExpression =
            of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(
                    listOf(
                        parseExpression(ctx.expressionTerm(0))!!,
                        parseExpression(ctx.expressionTerm(1))!!,
                    )
                )
        return systemCall("DifferenceBetween", result)
    }

    override fun visitWidthExpressionTerm(ctx: WidthExpressionTermContext): Any {
        val result: UnaryExpression =
            of.createWidth().withOperand(parseExpression(ctx.expressionTerm()))
        return systemCall("Width", result)
    }

    override fun visitParenthesizedTerm(ctx: ParenthesizedTermContext): Expression? {
        return parseExpression(ctx.expression())
    }

    override fun visitMembershipExpression(ctx: MembershipExpressionContext): Any {
        val operator: String = ctx.getChild(1)!!.text
        when (operator) {
            "in" ->
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    val inExpression: In =
                        of.createIn()
                            .withPrecision(
                                parseComparableDateTimePrecision(
                                    ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
                                )
                            )
                            .withOperand(
                                listOf(
                                    parseExpression(ctx.expression(0))!!,
                                    parseExpression(ctx.expression(1))!!,
                                )
                            )
                    return systemCall("In", inExpression)
                } else {
                    val left: Expression? = parseExpression(ctx.expression(0))
                    val right: Expression? = parseExpression(ctx.expression(1))
                    return libraryBuilder.resolveIn((left)!!, (right)!!)
                }
            "contains" ->
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    val contains: Contains =
                        of.createContains()
                            .withPrecision(
                                parseComparableDateTimePrecision(
                                    ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
                                )
                            )
                            .withOperand(
                                listOf(
                                    parseExpression(ctx.expression(0))!!,
                                    parseExpression(ctx.expression(1))!!,
                                )
                            )
                    return systemCall("Contains", contains)
                } else {
                    val left = parseExpression(ctx.expression(0))!!
                    val right = parseExpression(ctx.expression(1))!!
                    if (left is ValueSetRef) {
                        val inValueSet: InValueSet =
                            of.createInValueSet()
                                .withCode(right)
                                .withValueset(left as ValueSetRef?)
                                .withValuesetExpression(left)
                        libraryBuilder.resolveCall(
                            "System",
                            "InValueSet",
                            InValueSetInvocation(inValueSet),
                        )
                        return inValueSet
                    }
                    if (left is CodeSystemRef) {
                        val inCodeSystem: InCodeSystem =
                            of.createInCodeSystem()
                                .withCode(right)
                                .withCodesystem(left as CodeSystemRef?)
                                .withCodesystemExpression(left)
                        libraryBuilder.resolveCall(
                            "System",
                            "InCodeSystem",
                            InCodeSystemInvocation(inCodeSystem),
                        )
                        return inCodeSystem
                    }
                    val contains: Contains = of.createContains().withOperand(listOf(left, right))
                    return systemCall("Contains", contains)
                }
        }
        throw IllegalArgumentException("Unknown operator: $operator")
    }

    override fun visitAndExpression(ctx: AndExpressionContext): And {
        val and =
            of.createAnd()
                .withOperand(
                    listOf(
                        parseExpression(ctx.expression(0))!!,
                        parseExpression(ctx.expression(1))!!,
                    )
                )
        return systemCall("And", and)
    }

    override fun visitOrExpression(ctx: OrExpressionContext): Expression {
        return if (ctx.getChild(1)!!.text == "xor") {
            val xor =
                of.createXor()
                    .withOperand(
                        listOf(
                            parseExpression(ctx.expression(0))!!,
                            parseExpression(ctx.expression(1))!!,
                        )
                    )
            libraryBuilder.resolveCall("System", "Xor", xor)
            xor
        } else {
            val or =
                of.createOr()
                    .withOperand(
                        listOf(
                            parseExpression(ctx.expression(0))!!,
                            parseExpression(ctx.expression(1))!!,
                        )
                    )
            libraryBuilder.resolveCall("System", "Or", or)
            or
        }
    }

    override fun visitImpliesExpression(ctx: ImpliesExpressionContext): Implies {
        val implies =
            of.createImplies()
                .withOperand(
                    listOf(
                        parseExpression(ctx.expression(0))!!,
                        parseExpression(ctx.expression(1))!!,
                    )
                )
        return systemCall("Implies", implies)
    }

    override fun visitInFixSetExpression(ctx: InFixSetExpressionContext): Expression {
        val operator = ctx.getChild(1)!!.text
        val left = parseExpression(ctx.expression(0))
        val right = parseExpression(ctx.expression(1))
        when (operator) {
            "|",
            "union" -> return libraryBuilder.resolveUnion(left!!, right!!)
            "intersect" -> return libraryBuilder.resolveIntersect(left!!, right!!)
            "except" -> return libraryBuilder.resolveExcept(left!!, right!!)
        }
        return of.createNull()
    }

    override fun visitEqualityExpression(ctx: EqualityExpressionContext): Expression {
        val operator = parseString(ctx.getChild(1))
        return if (operator == "~" || operator == "!~") {
            val equivalent =
                of.createEquivalent()
                    .withOperand(
                        listOf(
                            parseExpression(ctx.expression(0))!!,
                            parseExpression(ctx.expression(1))!!,
                        )
                    )
            libraryBuilder.resolveCall("System", "Equivalent", equivalent)
            if ("~" != parseString(ctx.getChild(1))) {
                track(equivalent, ctx)
                val not = of.createNot().withOperand(equivalent)
                return systemCall("Not", not)
            }
            equivalent
        } else {
            val equal =
                of.createEqual()
                    .withOperand(
                        listOf(
                            parseExpression(ctx.expression(0))!!,
                            parseExpression(ctx.expression(1))!!,
                        )
                    )
            libraryBuilder.resolveCall("System", "Equal", equal)
            if ("=" != parseString(ctx.getChild(1))) {
                track(equal, ctx)
                val not = of.createNot().withOperand(equal)
                return systemCall("Not", not)
            }
            equal
        }
    }

    override fun visitInequalityExpression(ctx: InequalityExpressionContext): BinaryExpression {
        val exp: BinaryExpression
        val operatorName: String
        when (parseString(ctx.getChild(1))) {
            "<=" -> {
                operatorName = "LessOrEqual"
                exp = of.createLessOrEqual()
            }
            "<" -> {
                operatorName = "Less"
                exp = of.createLess()
            }
            ">" -> {
                operatorName = "Greater"
                exp = of.createGreater()
            }
            ">=" -> {
                operatorName = "GreaterOrEqual"
                exp = of.createGreaterOrEqual()
            }
            else -> throw IllegalArgumentException("Unknown operator: ${ctx.getChild(1)!!.text}")
        }
        exp.withOperand(
            listOf(parseExpression(ctx.expression(0))!!, parseExpression(ctx.expression(1))!!)
        )
        libraryBuilder.resolveCall("System", operatorName, exp)
        return exp
    }

    override fun visitQualifiedIdentifier(
        ctx: QualifiedIdentifierContext
    ): kotlin.collections.List<String?> {
        // Return the list of qualified identifiers for resolution by the containing element
        val identifiers: MutableList<String?> = ArrayList()
        for (qualifierContext in ctx.qualifier()) {
            val qualifier = parseString(qualifierContext)
            identifiers.add(qualifier)
        }
        val identifier = parseString(ctx.identifier())
        identifiers.add(identifier)
        return identifiers
    }

    override fun visitQualifiedIdentifierExpression(
        ctx: QualifiedIdentifierExpressionContext
    ): kotlin.collections.List<String?> {
        // Return the list of qualified identifiers for resolution by the containing element
        val identifiers: MutableList<String?> = ArrayList()
        for (qualifierContext in ctx.qualifierExpression()) {
            val qualifier = parseString(qualifierContext)
            identifiers.add(qualifier)
        }
        val identifier = parseString(ctx.referentialIdentifier())
        identifiers.add(identifier)
        return identifiers
    }

    override fun visitSimplePathReferentialIdentifier(
        ctx: SimplePathReferentialIdentifierContext
    ): String? {
        return visit(ctx.referentialIdentifier()) as String?
    }

    override fun visitSimplePathQualifiedIdentifier(
        ctx: SimplePathQualifiedIdentifierContext
    ): String {
        return visit(ctx.simplePath()).toString() + "." + visit(ctx.referentialIdentifier())
    }

    override fun visitSimplePathIndexer(ctx: SimplePathIndexerContext): String {
        return visit(ctx.simplePath()).toString() + "[" + visit(ctx.simpleLiteral()) + "]"
    }

    override fun visitTermExpression(ctx: TermExpressionContext): Any? {
        val result = super.visitTermExpression(ctx)
        require(result !is LibraryRef) {
            "Identifier ${(result as LibraryRef).libraryName} is a library and cannot be used as an expression."
        }
        return result
    }

    override fun visitTerminal(node: TerminalNode): Any? {
        var text = node.text
        val tokenType = node.symbol.type
        if (Token.EOF == tokenType) {
            return null
        }
        if (
            cqlLexer.Tokens.STRING == tokenType ||
                cqlLexer.Tokens.QUOTEDIDENTIFIER == tokenType ||
                cqlLexer.Tokens.DELIMITEDIDENTIFIER == tokenType
        ) {
            // chop off leading and trailing ', ", or `
            text = text.substring(1, text.length - 1)

            // This is an alternate style of escaping that was removed when we switched to
            // industry-standard escape sequences
            // if (cqlLexer.STRING == tokenType) {
            //     text = text.replace("''", "'");
            // }
            // else {
            //     text = text.replace("\"\"", "\"");
            // }
        }
        return text
    }

    override fun visitConversionExpressionTerm(ctx: ConversionExpressionTermContext): Any? {
        if (ctx.typeSpecifier() != null) {
            val targetType: TypeSpecifier? = parseTypeSpecifier(ctx.typeSpecifier())
            val operand: Expression? = parseExpression(ctx.expression())
            if (!equal(operand!!.resultType, targetType!!.resultType)) {
                val conversion: Conversion =
                    libraryBuilder.findConversion(
                        operand.resultType!!,
                        targetType.resultType!!,
                        implicit = false,
                        allowPromotionAndDemotion = true,
                    )
                        ?: // ERROR:
                        throw IllegalArgumentException(
                            "Could not resolve conversion from type ${operand.resultType} to type ${targetType.resultType}."
                        )
                return libraryBuilder.convertExpression((operand), conversion)
            }
            return operand
        } else {
            var targetUnit: String? = parseString(ctx.unit())
            targetUnit = libraryBuilder.ensureUcumUnit((targetUnit)!!)
            val operand = parseExpression(ctx.expression())!!
            val unitOperand: Expression = libraryBuilder.createLiteral(targetUnit)
            track(unitOperand, ctx.unit()!!)
            val convertQuantity: ConvertQuantity =
                of.createConvertQuantity().withOperand(listOf(operand, unitOperand))
            track(convertQuantity, ctx)
            return libraryBuilder.resolveCall("System", "ConvertQuantity", convertQuantity)
        }
    }

    override fun visitTypeExpression(ctx: TypeExpressionContext): Expression {
        // NOTE: These don't use the buildIs or buildAs because those start with a DataType, rather
        // than a TypeSpecifier
        if (ctx.getChild(1)!!.text == "is") {
            val isExpression =
                of.createIs()
                    .withOperand(parseExpression(ctx.expression()))
                    .withIsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
            isExpression.resultType = libraryBuilder.resolveTypeName("System", "Boolean")
            return isExpression
        }
        val asExpression =
            of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(false)
        val targetType = asExpression.asTypeSpecifier!!.resultType
        verifyCast(targetType, asExpression.operand!!.resultType)
        asExpression.resultType = targetType
        return asExpression
    }

    override fun visitCastExpression(ctx: CastExpressionContext): As {
        // NOTE: This doesn't use buildAs because it starts with a DataType, rather than a
        // TypeSpecifier
        val asExpression =
            of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(true)
        val targetType = asExpression.asTypeSpecifier!!.resultType
        verifyCast(targetType, asExpression.operand!!.resultType)
        asExpression.resultType = targetType
        return asExpression
    }

    override fun visitBooleanExpression(ctx: BooleanExpressionContext): Expression {
        var exp: UnaryExpression?
        val left = visit(ctx.expression()) as Expression?
        val lastChild = ctx.getChild(ctx.childCount - 1)!!.text
        val nextToLast = ctx.getChild(ctx.childCount - 2)!!.text
        when (lastChild) {
            "null" -> {
                exp = of.createIsNull().withOperand(left)
                libraryBuilder.resolveCall("System", "IsNull", exp)
            }
            "true" -> {
                exp = of.createIsTrue().withOperand(left)
                libraryBuilder.resolveCall("System", "IsTrue", exp)
            }
            "false" -> {
                exp = of.createIsFalse().withOperand(left)
                libraryBuilder.resolveCall("System", "IsFalse", exp)
            }
            else -> throw IllegalArgumentException("Unknown boolean test predicate $lastChild.")
        }
        if ("not" == nextToLast) {
            track(exp, ctx)
            exp = of.createNot().withOperand(exp)
            libraryBuilder.resolveCall("System", "Not", exp)
        }

        return exp
    }

    override fun visitTimingExpression(ctx: TimingExpressionContext): Any? {
        val left = parseExpression(ctx.expression(0))
        val right = parseExpression(ctx.expression(1))
        requireNotNull(left) { "left expression of timing operator can not be null" }
        requireNotNull(right) { "right expression of timing operator can not be null" }
        val timingOperatorContext = TimingOperatorContext(left, right)
        this.timingOperators.push(timingOperatorContext)
        return try {
            visit(ctx.intervalOperatorPhrase())
        } finally {
            this.timingOperators.pop()
        }
    }

    override fun visitConcurrentWithIntervalOperatorPhrase(
        ctx: ConcurrentWithIntervalOperatorPhraseContext
    ): BinaryExpression {
        // ('starts' | 'ends' | 'occurs')? 'same' dateTimePrecision? (relativeQualifier | 'as')
        // ('start' | 'end')?
        val timingOperator: TimingOperatorContext = this.timingOperators.peek()
        val firstChild: ParseTree = ctx.getChild(0)!!
        when (firstChild.text) {
            "starts" -> timingOperator.left = timing.takeStart(timingOperator.left, firstChild)
            "ends" -> timingOperator.left = timing.takeEnd(timingOperator.left, firstChild)
        }
        val lastChild: ParseTree = ctx.getChild(ctx.childCount - 1)!!
        when (lastChild.text) {
            "start" -> timingOperator.right = timing.takeStart(timingOperator.right, lastChild)
            "end" -> timingOperator.right = timing.takeEnd(timingOperator.right, lastChild)
        }
        val (operatorName, allowPromotion) =
            when (ctx.relativeQualifier()?.text) {
                null -> "SameAs" to false
                "or after" -> "SameOrAfter" to true
                "or before" -> "SameOrBefore" to true
                else ->
                    throw IllegalArgumentException(
                        "Unknown relative qualifier: '${ctx.relativeQualifier()!!.text}'."
                    )
            }
        val precision = ctx.dateTimePrecision()?.let { parseComparableDateTimePrecision(it.text) }
        return timing.buildTemporal(
            operatorName,
            timingOperator.left,
            timingOperator.right,
            precision,
            allowPromotion,
        )
    }

    override fun visitIncludesIntervalOperatorPhrase(
        ctx: IncludesIntervalOperatorPhraseContext
    ): Any? {
        // 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?
        var isProper = false
        var isRightPoint = false
        val timingOperator = this.timingOperators.peek()
        for (pt in ctx.children!!) {
            when (pt.text) {
                "properly" -> isProper = true
                "start" -> {
                    timingOperator.right = timing.takeStart(timingOperator.right, pt)
                    isRightPoint = true
                }
                "end" -> {
                    timingOperator.right = timing.takeEnd(timingOperator.right, pt)
                    isRightPoint = true
                }
            }
        }
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
            else null

        // If the right is not convertible to an interval or list
        // if (!isRightPoint &&
        //     !(timingOperator.getRight().getResultType() instanceof IntervalType
        //     || timingOperator.getRight().getResultType() instanceof ListType)) {
        //     isRightPoint = true;
        // }
        if (isRightPoint) {
            return if (isProper) {
                libraryBuilder.resolveProperContains(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false),
                )
            } else
                libraryBuilder.resolveContains(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false),
                )
        }
        return if (isProper) {
            libraryBuilder.resolveProperIncludes(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false),
            )
        } else
            libraryBuilder.resolveIncludes(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false),
            )
    }

    override fun visitIncludedInIntervalOperatorPhrase(
        ctx: IncludedInIntervalOperatorPhraseContext
    ): Any? {
        // ('starts' | 'ends' | 'occurs')? 'properly'? ('during' | 'included in')
        // dateTimePrecisionSpecifier?
        var isProper = false
        var isLeftPoint = false
        val timingOperator = this.timingOperators.peek()
        for (pt in ctx.children!!) {
            when (pt.text) {
                "starts" -> {
                    timingOperator.left = timing.takeStart(timingOperator.left, pt)
                    isLeftPoint = true
                }
                "ends" -> {
                    timingOperator.left = timing.takeEnd(timingOperator.left, pt)
                    isLeftPoint = true
                }
                "properly" -> isProper = true
            }
        }
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
            else null

        // If the left is not convertible to an interval or list
        // if (!isLeftPoint &&
        //     !(timingOperator.getLeft().getResultType() instanceof IntervalType
        //     || timingOperator.getLeft().getResultType() instanceof ListType)) {
        //     isLeftPoint = true;
        // }
        if (isLeftPoint) {
            return if (isProper) {
                libraryBuilder.resolveProperIn(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false),
                )
            } else
                libraryBuilder.resolveIn(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false),
                )
        }
        return if (isProper) {
            libraryBuilder.resolveProperIncludedIn(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false),
            )
        } else
            libraryBuilder.resolveIncludedIn(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false),
            )
    }

    override fun visitBeforeOrAfterIntervalOperatorPhrase(
        ctx: BeforeOrAfterIntervalOperatorPhraseContext
    ): Expression {
        // ('starts' | 'ends' | 'occurs')? quantityOffset? ('before' | 'after')
        // dateTimePrecisionSpecifier? ('start' | 'end')?

        // duration before/after
        // A starts 3 days before start B
        // * start of A same day as start of B - 3 days
        // A starts 3 days after start B
        // * start of A same day as start of B + 3 days

        // or more/less duration before/after
        // A starts 3 days or more before start B
        // * start of A <= start of B - 3 days
        // A starts 3 days or more after start B
        // * start of A >= start of B + 3 days
        // A starts 3 days or less before start B
        // * start of A in [start of B - 3 days, start of B) and B is not null
        // A starts 3 days or less after start B
        // * start of A in (start of B, start of B + 3 days] and B is not null

        // less/more than duration before/after
        // A starts more than 3 days before start B
        // * start of A < start of B - 3 days
        // A starts more than 3 days after start B
        // * start of A > start of B + 3 days
        // A starts less than 3 days before start B
        // * start of A in (start of B - 3 days, start of B)
        // A starts less than 3 days after start B
        // * start of A in (start of B, start of B + 3 days)
        val timingOperator = this.timingOperators.peek()
        var isBefore = false
        var isInclusive = false
        for (child in ctx.children!!) {
            when (child.text) {
                "starts" -> timingOperator.left = timing.takeStart(timingOperator.left, child)
                "ends" -> timingOperator.left = timing.takeEnd(timingOperator.left, child)
                "start" -> timingOperator.right = timing.takeStart(timingOperator.right, child)
                "end" -> timingOperator.right = timing.takeEnd(timingOperator.right, child)
            }
        }
        for (child in ctx.temporalRelationship().children!!) {
            when (child.text) {
                "before" -> isBefore = true
                "on or",
                "or on" -> isInclusive = true
            }
        }
        val dateTimePrecision =
            ctx.dateTimePrecisionSpecifier()?.dateTimePrecision()?.text?.let {
                parseComparableDateTimePrecision(it)
            }
        if (ctx.quantityOffset() == null) {
            val op =
                when {
                    isInclusive && isBefore -> "SameOrBefore"
                    isInclusive -> "SameOrAfter"
                    isBefore -> "Before"
                    else -> "After"
                }
            return timing.buildTemporal(
                op,
                timingOperator.left,
                timingOperator.right,
                dateTimePrecision,
                allowPromotionAndDemotion = true,
            )
        } else {
            val quantity = visit(ctx.quantityOffset()!!.quantity()!!) as Quantity
            if (timingOperator.left.resultType is IntervalType) {
                timingOperator.left =
                    if (isBefore) timing.takeEnd(timingOperator.left, timingOperator.left)
                    else timing.takeStart(timingOperator.left, timingOperator.left)
            }
            if (timingOperator.right.resultType is IntervalType) {
                timingOperator.right =
                    if (isBefore) timing.takeStart(timingOperator.right, timingOperator.right)
                    else timing.takeEnd(timingOperator.right, timingOperator.right)
            }
            if (
                ctx.quantityOffset()!!.offsetRelativeQualifier() == null &&
                    ctx.quantityOffset()!!.exclusiveRelativeQualifier() == null
            ) {
                // SameAs with right shifted by +/- quantity.
                timingOperator.right = shiftOperand(timingOperator.right, quantity, isBefore)
                return timing.buildTemporal(
                    "SameAs",
                    timingOperator.left,
                    timingOperator.right,
                    dateTimePrecision,
                )
            } else {
                val isOffsetInclusive = ctx.quantityOffset()!!.offsetRelativeQualifier() != null
                val qualifier =
                    if (ctx.quantityOffset()!!.offsetRelativeQualifier() != null)
                        ctx.quantityOffset()!!.offsetRelativeQualifier()!!.text
                    else ctx.quantityOffset()!!.exclusiveRelativeQualifier()!!.text
                when (qualifier) {
                    "more than",
                    "or more" -> {
                        // Shift right by +/- quantity, then Before/After (or SameOr-variant if
                        // inclusive).
                        timingOperator.right =
                            shiftOperand(timingOperator.right, quantity, isBefore)
                        val op =
                            when {
                                isBefore && isOffsetInclusive -> "SameOrBefore"
                                isBefore -> "Before"
                                isOffsetInclusive -> "SameOrAfter"
                                else -> "After"
                            }
                        return timing.buildTemporal(
                            op,
                            timingOperator.left,
                            timingOperator.right,
                            dateTimePrecision,
                            allowPromotionAndDemotion = true,
                        )
                    }
                    "less than",
                    "or less" -> {
                        // For Less Than/Or Less, use an In
                        // For Before, construct an interval from right - quantity to right
                        // For After, construct an interval from right to right + quantity
                        val lowerBound: Expression?
                        val upperBound: Expression?
                        val right = timingOperator.right
                        if (isBefore) {
                            lowerBound = of.createSubtract().withOperand(listOf(right, quantity))
                            track(lowerBound, right)
                            libraryBuilder.resolveCall(
                                "System",
                                "Subtract",
                                (lowerBound as BinaryExpression?)!!,
                            )
                            upperBound = right
                        } else {
                            lowerBound = right
                            upperBound = of.createAdd().withOperand(listOf(right, quantity))
                            track(upperBound, right)
                            libraryBuilder.resolveCall(
                                "System",
                                "Add",
                                (upperBound as BinaryExpression?)!!,
                            )
                        }

                        // 3 days or less before -> [B - 3 days, B)
                        // less than 3 days before -> (B - 3 days, B)
                        // 3 days or less after -> (B, B + 3 days]
                        // less than 3 days after -> (B, B + 3 days)
                        val interval =
                            if (isBefore)
                                libraryBuilder.createInterval(
                                    lowerBound,
                                    isOffsetInclusive,
                                    upperBound,
                                    isInclusive,
                                )
                            else
                                libraryBuilder.createInterval(
                                    lowerBound,
                                    isInclusive,
                                    upperBound,
                                    isOffsetInclusive,
                                )
                        track(interval, ctx.quantityOffset()!!)
                        val inExpression =
                            of.createIn().withOperand(listOf(timingOperator.left, interval))
                        if (dateTimePrecision != null) {
                            inExpression.precision = dateTimePrecision
                        }
                        track(inExpression, ctx.quantityOffset()!!)
                        libraryBuilder.resolveCall("System", "In", inExpression)

                        // if the offset or comparison is inclusive, add a null check for B to
                        // ensure correct interpretation
                        if (isOffsetInclusive || isInclusive) {
                            val nullTest = of.createIsNull().withOperand(right)
                            track(nullTest, ctx.quantityOffset()!!)
                            libraryBuilder.resolveCall("System", "IsNull", nullTest)
                            val notNullTest = of.createNot().withOperand(nullTest)
                            track(notNullTest, ctx.quantityOffset()!!)
                            libraryBuilder.resolveCall("System", "Not", notNullTest)
                            val and = of.createAnd().withOperand(listOf(inExpression, notNullTest))
                            track(and, ctx.quantityOffset()!!)
                            return systemCall("And", and)
                        }

                        // Otherwise, return the constructed in
                        return inExpression
                    }
                }
            }
        }
        throw IllegalArgumentException("Unable to resolve interval operator phrase.")
    }

    /**
     * Return `operand +/- quantity` depending on direction: subtract when [subtract] is true,
     * otherwise add. Tracks the new node against [trackSource] (an [Element] to copy trackbacks
     * from, or a [ParseTree] to resolve a new trackback from) and resolves the call.
     */
    private fun shiftOperand(
        operand: Expression,
        quantity: Quantity,
        subtract: Boolean,
        trackSource: Any = operand,
    ): BinaryExpression {
        val shifted: BinaryExpression =
            if (subtract) of.createSubtract().withOperand(listOf(operand, quantity))
            else of.createAdd().withOperand(listOf(operand, quantity))
        when (trackSource) {
            is Element -> track(shifted, trackSource)
            is ParseTree -> track(shifted, trackSource)
        }
        libraryBuilder.resolveCall("System", if (subtract) "Subtract" else "Add", shifted)
        return shifted
    }

    override fun visitWithinIntervalOperatorPhrase(
        ctx: WithinIntervalOperatorPhraseContext
    ): Expression {
        // ('starts' | 'ends' | 'occurs')? 'properly'? 'within' quantityLiteral 'of' ('start' |
        // 'end')?
        // A starts within 3 days of start B
        // * start of A in [start of B - 3 days, start of B + 3 days] and start B is not null
        // A starts within 3 days of B
        // * start of A in [start of B - 3 days, end of B + 3 days]
        val timingOperator = this.timingOperators.peek()
        var isProper = false
        for (child in ctx.children!!) {
            when (child.text) {
                "starts" -> timingOperator.left = timing.takeStart(timingOperator.left, child)
                "ends" -> timingOperator.left = timing.takeEnd(timingOperator.left, child)
                "start" -> timingOperator.right = timing.takeStart(timingOperator.right, child)
                "end" -> timingOperator.right = timing.takeEnd(timingOperator.right, child)
                "properly" -> isProper = true
            }
        }
        val quantity = visit(ctx.quantity()) as Quantity
        var lowerBound: Expression
        var upperBound: Expression
        var initialBound: Expression? = null
        if (timingOperator.right.resultType is IntervalType) {
            lowerBound = timing.takeStart(timingOperator.right, ctx.quantity())
            upperBound = timing.takeEnd(timingOperator.right, ctx.quantity())
        } else {
            lowerBound = timingOperator.right
            upperBound = timingOperator.right
            initialBound = lowerBound
        }
        lowerBound =
            shiftOperand(lowerBound, quantity, subtract = true, trackSource = ctx.quantity())
        upperBound =
            shiftOperand(upperBound, quantity, subtract = false, trackSource = ctx.quantity())
        val interval = libraryBuilder.createInterval(lowerBound, !isProper, upperBound, !isProper)
        track(interval, ctx.quantity())
        val inExpression = of.createIn().withOperand(listOf(timingOperator.left, interval))
        libraryBuilder.resolveCall("System", "In", inExpression)

        // if the within is not proper and the interval is being constructed from a single point,
        // add a null check for that point to ensure correct interpretation
        if (!isProper && initialBound != null) {
            val nullTest = of.createIsNull().withOperand(initialBound)
            track(nullTest, ctx.quantity())
            libraryBuilder.resolveCall("System", "IsNull", nullTest)
            val notNullTest = of.createNot().withOperand(nullTest)
            track(notNullTest, ctx.quantity())
            libraryBuilder.resolveCall("System", "Not", notNullTest)
            val and = of.createAnd().withOperand(listOf(inExpression, notNullTest))
            track(and, ctx.quantity())
            return systemCall("And", and)
        }

        // Otherwise, return the constructed in
        return inExpression
    }

    override fun visitMeetsIntervalOperatorPhrase(ctx: MeetsIntervalOperatorPhraseContext): Any {
        val precision = parsePrecisionSpecifier(ctx.dateTimePrecisionSpecifier())
        val operatorName =
            if (ctx.childCount == 1 + if (precision == null) 0 else 1) "Meets"
            else if ("before" == ctx.getChild(1)!!.text) "MeetsBefore" else "MeetsAfter"
        val t = this.timingOperators.peek()
        return timing.buildTemporal(operatorName, t.left, t.right, precision)
    }

    override fun visitOverlapsIntervalOperatorPhrase(
        ctx: OverlapsIntervalOperatorPhraseContext
    ): Any {
        val precision = parsePrecisionSpecifier(ctx.dateTimePrecisionSpecifier())
        val operatorName =
            if (ctx.childCount == 1 + if (precision == null) 0 else 1) "Overlaps"
            else if ("before" == ctx.getChild(1)!!.text) "OverlapsBefore" else "OverlapsAfter"
        val t = this.timingOperators.peek()
        return timing.buildTemporal(operatorName, t.left, t.right, precision)
    }

    override fun visitStartsIntervalOperatorPhrase(
        ctx: StartsIntervalOperatorPhraseContext
    ): Starts {
        val precision = parsePrecisionSpecifier(ctx.dateTimePrecisionSpecifier())
        val t = this.timingOperators.peek()
        return timing.buildTemporal("Starts", t.left, t.right, precision) as Starts
    }

    override fun visitEndsIntervalOperatorPhrase(ctx: EndsIntervalOperatorPhraseContext): Ends {
        val precision = parsePrecisionSpecifier(ctx.dateTimePrecisionSpecifier())
        val t = this.timingOperators.peek()
        return timing.buildTemporal("Ends", t.left, t.right, precision) as Ends
    }

    private fun parsePrecisionSpecifier(
        specCtx: DateTimePrecisionSpecifierContext?
    ): DateTimePrecision? =
        specCtx?.dateTimePrecision()?.text?.let { parseComparableDateTimePrecision(it) }

    fun resolveIfThenElse(ifObject: If): Expression {
        ifObject.condition =
            libraryBuilder.ensureCompatible(
                ifObject.condition,
                libraryBuilder.resolveTypeName("System", "Boolean"),
            )
        val resultType: DataType? =
            libraryBuilder.ensureCompatibleTypes(
                ifObject.then!!.resultType,
                ifObject.`else`!!.resultType!!,
            )
        ifObject.resultType = resultType
        ifObject.then = libraryBuilder.ensureCompatible(ifObject.then, resultType)
        ifObject.`else` = (libraryBuilder.ensureCompatible(ifObject.`else`, resultType))
        return ifObject
    }

    override fun visitIfThenElseExpressionTerm(ctx: IfThenElseExpressionTermContext): Any {
        val ifObject =
            of.createIf()
                .withCondition(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)))
                .withElse(parseExpression(ctx.expression(2)))
        return resolveIfThenElse(ifObject)
    }

    override fun visitCaseExpressionTerm(ctx: CaseExpressionTermContext): Any {
        val result: Case = of.createCase()
        var hitElse = false
        var resultType: DataType? = null
        for (pt: ParseTree in ctx.children!!) {
            if (("else" == pt.text)) {
                hitElse = true
                continue
            }
            if (pt is ExpressionContext) {
                if (hitElse) {
                    result.`else` = (parseExpression(pt))
                    resultType =
                        libraryBuilder.ensureCompatibleTypes(
                            resultType,
                            result.`else`!!.resultType!!,
                        )
                } else {
                    result.comparand = parseExpression(pt)
                }
            }
            if (pt is CaseExpressionItemContext) {
                val caseItem = visit(pt) as CaseItem
                if (result.comparand != null) {
                    libraryBuilder.verifyType(
                        caseItem.`when`!!.resultType!!,
                        result.comparand!!.resultType!!,
                    )
                } else {
                    verifyType(
                        caseItem.`when`!!.resultType,
                        libraryBuilder.resolveTypeName("System", "Boolean"),
                    )
                }
                resultType =
                    if (resultType == null) {
                        caseItem.then!!.resultType
                    } else {
                        libraryBuilder.ensureCompatibleTypes(
                            resultType,
                            caseItem.then!!.resultType!!,
                        )
                    }
                result.caseItem.add(caseItem)
            }
        }
        for (caseItem: CaseItem? in result.caseItem) {
            if (result.comparand != null) {
                caseItem!!.`when` =
                    (libraryBuilder.ensureCompatible(
                        caseItem.`when`,
                        result.comparand!!.resultType,
                    ))
            }
            caseItem!!.then = libraryBuilder.ensureCompatible(caseItem.then, resultType)
        }
        result.`else` = (libraryBuilder.ensureCompatible(result.`else`, resultType))
        result.resultType = resultType
        return result
    }

    override fun visitCaseExpressionItem(ctx: CaseExpressionItemContext): CaseItem {
        return of.createCaseItem()
            .withWhen(parseExpression(ctx.expression(0)))
            .withThen(parseExpression(ctx.expression(1)))
    }

    override fun visitAggregateExpressionTerm(ctx: AggregateExpressionTermContext): Expression {
        when (ctx.getChild(0)!!.text) {
            "distinct" -> {
                val distinct = of.createDistinct().withOperand(parseExpression(ctx.expression()))
                return systemCall("Distinct", distinct)
            }
            "flatten" -> {
                val flatten = of.createFlatten().withOperand(parseExpression(ctx.expression()))
                return systemCall("Flatten", flatten)
            }
        }
        throw IllegalArgumentException("Unknown aggregate operator ${ctx.getChild(0)!!.text}.")
    }

    override fun visitSetAggregateExpressionTerm(ctx: SetAggregateExpressionTermContext): Any {
        val source = parseExpression(ctx.expression(0))!!

        // If `per` is not set, it will remain `null as System.Quantity`.
        var per: Expression? =
            libraryBuilder.buildNull(libraryBuilder.resolveTypeName("System", "Quantity"))
        if (ctx.dateTimePrecision() != null) {
            per =
                libraryBuilder.createQuantity(
                    BigDecimal("1.0"), // Always use BigDecimal(String) for exact precision
                    (parseString(ctx.dateTimePrecision()))!!,
                )
        } else if (ctx.expression().size > 1) {
            val perExpression = parseExpression(ctx.expression(1))
            // Implicitly convert a literal to a quantity here
            // Note that although this is declared as a conversion, the translator doesn't pick it
            // up because it won't instantiate the generic signature because generic signature
            // instantiation logic is not considering implicit conversions to class types.
            if (perExpression is Literal) {
                per = libraryBuilder.createQuantity(BigDecimal(perExpression.value!!), "1")
            } else {
                per = perExpression
            }
        } else {
            // Determine per quantity based on point type of the intervals involved
            if (source.resultType is ListType) {
                val listType: ListType = source.resultType as ListType
                if (listType.elementType is IntervalType) {
                    val intervalType: IntervalType = listType.elementType as IntervalType
                    val pointType: DataType = intervalType.pointType

                    // TODO: Test this...
                    // // Successor(MinValue<T>) - MinValue<T>
                    // MinValue minimum = libraryBuilder.buildMinimum(pointType);
                    // track(minimum, ctx);
                    //
                    // Expression successor = libraryBuilder.buildSuccessor(minimum);
                    // track(successor, ctx);
                    //
                    // minimum = libraryBuilder.buildMinimum(pointType);
                    // track(minimum, ctx);
                    //
                    // Subtract subtract = of.createSubtract().withOperand(successor, minimum);
                    // libraryBuilder.resolveCall("System", "Subtract", subtract);
                    // per = subtract;
                }
            }
        }
        when (ctx.getChild(0)!!.text) {
            "expand" -> {
                val expand: Expand = of.createExpand().withOperand(listOf(source, per!!))
                return systemCall("Expand", expand)
            }
            "collapse" -> {
                val collapse: Collapse = of.createCollapse().withOperand(listOf(source, per!!))
                return systemCall("Collapse", collapse)
            }
        }
        throw IllegalArgumentException("Unknown aggregate set operator ${ctx.getChild(0)!!.text}.")
    }

    override fun visitRetrieve(ctx: RetrieveContext): Expression? {
        libraryBuilder.checkLiteralContext()
        val qualifiers: kotlin.collections.List<String> = parseQualifiers(ctx.namedTypeSpecifier())
        val model: String? = getModelIdentifier(qualifiers)
        val label: String =
            getTypeIdentifier(
                qualifiers,
                (parseString(ctx.namedTypeSpecifier().referentialOrTypeNameIdentifier()))!!,
            )
        val dataType = libraryBuilder.resolveTypeName(model, label)

        requireNotNull(dataType) { "Could not resolve type name $label." }
        require(dataType is ClassType && dataType.isRetrievable) {
            "Specified data type $label does not support retrieval."
        }

        val classType: ClassType = dataType
        // BTR -> The original intent of this code was to have the retrieve return the base type,
        // and use the "templateId" element of the retrieve to communicate the "positive" or
        // "negative" profile to the data access layer. However, because this notion of carrying the
        // "profile" through a type is not general, it causes inconsistencies when using retrieve
        // results with functions defined in terms of the same type (see GitHub Issue #131). Based
        // on the discussion there, the retrieve will now return the declared type, whether it is a
        // profile or not.
        // ProfileType profileType = dataType instanceof ProfileType ? (ProfileType)dataType : null;
        // NamedType namedType = profileType == null ? classType :
        // (NamedType)classType.getBaseType();
        val namedType: NamedType = classType
        val modelInfo: ModelInfo = libraryBuilder.getModel(namedType.namespace).modelInfo
        val useStrictRetrieveTyping: Boolean =
            modelInfo.isStrictRetrieveTyping() != null && modelInfo.isStrictRetrieveTyping()!!
        var codePath: String? = null
        var property: Property? = null
        var propertyException: CqlCompilerException? = null
        var terminology: Expression? = null
        var codeComparator: String? = null
        if (ctx.terminology() != null) {
            if (ctx.codePath() != null) {
                val identifiers: String? = visit(ctx.codePath()!!) as String?
                codePath = identifiers
            } else if (classType.primaryCodePath != null) {
                codePath = classType.primaryCodePath
            }
            if (codePath == null) {
                // ERROR:
                // WARNING:
                propertyException =
                    CqlSemanticException(
                        "Retrieve has a terminology target but does not specify a code path and the type of the retrieve does not have a primary code path defined.",
                        getTrackBack(ctx),
                        if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                        else CqlCompilerException.ErrorSeverity.Warning,
                    )
                libraryBuilder.recordParsingException(propertyException)
            } else {
                try {
                    val codeType: DataType? =
                        libraryBuilder.resolvePath(namedType as DataType, codePath)
                    property = of.createProperty().withPath(codePath)
                    property.resultType = codeType
                } catch (e: Exception) {
                    // ERROR:
                    // WARNING:
                    propertyException =
                        CqlSemanticException(
                            "Could not resolve code path $codePath for the type of the retrieve ${namedType.name}.",
                            getTrackBack(ctx),
                            if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                            else CqlCompilerException.ErrorSeverity.Warning,
                            e,
                        )
                    libraryBuilder.recordParsingException(propertyException)
                }
            }
            if (ctx.terminology()!!.qualifiedIdentifierExpression() != null) {
                val identifiers: kotlin.collections.List<String> = visit(ctx.terminology()!!).cast()
                terminology = resolveQualifiedIdentifier(identifiers)
                track(terminology, ctx.terminology()!!.qualifiedIdentifierExpression()!!)
            } else {
                terminology = parseExpression(ctx.terminology()!!.expression())
            }
            codeComparator =
                if (ctx.codeComparator() != null) visit(ctx.codeComparator()!!) as String? else null
        }
        var result: Expression? = null

        // Resolve the contextIdentifier once for use in any retrieves built below.
        val contextExpression: Expression? =
            ctx.contextIdentifier()?.let {
                val identifiers: kotlin.collections.List<String> = visit(it).cast()
                resolveQualifiedIdentifier(identifiers)
            }

        // Only expand a choice-valued code path if no comparator is specified
        // Otherwise, a code comparator will always choose a specific representation
        val hasFHIRHelpers: Boolean = this.libraryInfo.resolveLibraryName("FHIRHelpers") != null
        if ((property != null) && property.resultType is ChoiceType && (codeComparator == null)) {
            for (propertyType: DataType? in (property.resultType as ChoiceType).types) {
                if (
                    (hasFHIRHelpers &&
                        propertyType is NamedType &&
                        ((propertyType as NamedType).simpleName == "Reference") &&
                        (namedType.simpleName == "MedicationRequest" ||
                            namedType.simpleName == "MedicationAdministration" ||
                            namedType.simpleName == "MedicationDispense" ||
                            namedType.simpleName == "MedicationStatement"))
                ) {
                    // TODO: This is a model-specific special case to support QICore
                    // This functionality needs to be generalized to a retrieve mapping in the model
                    // info
                    // But that requires a model info change (to represent references, right now the
                    // model info only includes context relationships)
                    // The reference expands to
                    //   [MedicationRequest] MR
                    //     with [Medication] M
                    //       such that M.id = Last(Split(MR.medication.reference, '/'))
                    //         and M.code in <valueset>
                    val mrRetrieve: Retrieve =
                        retrieveBuilder.build(
                            ctx,
                            useStrictRetrieveTyping,
                            namedType,
                            classType,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            contextExpression,
                        )
                    mrRetrieve.resultType = ListType(namedType as DataType)
                    val mDataType: DataType? = libraryBuilder.resolveTypeName(model, "Medication")
                    val mClassType = mDataType as ClassType
                    val mNamedType: NamedType = mClassType
                    val mRetrieve: Retrieve =
                        retrieveBuilder.build(
                            ctx,
                            useStrictRetrieveTyping,
                            mNamedType,
                            mClassType,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            contextExpression,
                        )
                    mRetrieve.resultType = ListType(mDataType)
                    val q: Query = of.createQuery()
                    val aqs: AliasedQuerySource =
                        of.createAliasedQuerySource().withExpression(mrRetrieve).withAlias("MR")
                    track(aqs, ctx)
                    aqs.resultType = aqs.expression!!.resultType
                    q.source.add(aqs)
                    track(q, ctx)
                    q.resultType = aqs.resultType
                    val w: With = of.createWith().withExpression(mRetrieve).withAlias("M")
                    track(w, ctx)
                    w.resultType = w.expression!!.resultType
                    q.relationship.add(w)
                    val idPath = "id"
                    val idType: DataType? = libraryBuilder.resolvePath(mDataType, idPath)
                    val idProperty: Property =
                        libraryBuilder.buildProperty("M", idPath, false, idType)
                    val refPath = "medication.reference"
                    val refType: DataType? = libraryBuilder.resolvePath(dataType, refPath)
                    val refProperty: Property =
                        libraryBuilder.buildProperty("MR", refPath, false, refType)
                    val split: Split =
                        of.createSplit()
                            .withStringToSplit(refProperty)
                            .withSeparator(libraryBuilder.createLiteral("/"))
                    libraryBuilder.resolveCall("System", "Split", SplitInvocation(split))
                    val last: Last = of.createLast().withSource(split)
                    libraryBuilder.resolveCall("System", "Last", LastInvocation(last))
                    val e: Equal = of.createEqual().withOperand(listOf(idProperty, last))
                    libraryBuilder.resolveCall("System", "Equal", e)

                    // Apply target mapping if this is a profile-informed model info
                    if (equal(idType, libraryBuilder.resolveTypeName("System", "String"))) {
                        idProperty.path = "id.value"
                    }
                    if (equal(refType, libraryBuilder.resolveTypeName("System", "String"))) {
                        refProperty.path = "medication.reference.value"
                    }
                    val mCodeType: DataType? =
                        libraryBuilder.resolvePath(mNamedType as DataType?, "code")
                    val mProperty = libraryBuilder.buildProperty("M", "code", false, mCodeType)
                    var mCodeProperty: Expression = mProperty

                    // Apply target mapping if this is a profile-informed model info
                    if (equal(mCodeType, libraryBuilder.resolveTypeName("System", "Concept"))) {
                        val toConcept =
                            of.createFunctionRef()
                                .withLibraryName("FHIRHelpers")
                                .withName("ToConcept")
                                .withOperand(listOf(mCodeProperty))
                        toConcept.resultType = mCodeType
                        mCodeProperty = toConcept
                    }

                    var mCodeComparator = "~"
                    if (terminology!!.resultType is ListType) {
                        mCodeComparator = "in"
                    } else if (libraryBuilder.isCompatibleWith("1.5")) {
                        mCodeComparator =
                            if (
                                terminology.resultType!!.isSubTypeOf(
                                    libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                                )
                            )
                                "in"
                            else "~"
                    }
                    val terminologyComparison: Expression =
                        if ((mCodeComparator == "in")) {
                            libraryBuilder.resolveIn(mCodeProperty, (terminology))
                        } else {
                            val equivalent: BinaryExpression =
                                of.createEquivalent()
                                    .withOperand(listOf(mCodeProperty, terminology))
                            libraryBuilder.resolveCall("System", "Equivalent", equivalent)
                            equivalent
                        }
                    val a: And = of.createAnd().withOperand(listOf(e, terminologyComparison))
                    libraryBuilder.resolveCall("System", "And", a)
                    w.withSuchThat(a)
                    result =
                        if (result == null) {
                            q
                        } else {
                            track(q, ctx)
                            libraryBuilder.resolveUnion(result, q)
                        }
                } else {
                    val retrieve: Retrieve =
                        retrieveBuilder.build(
                            ctx,
                            useStrictRetrieveTyping,
                            namedType,
                            classType,
                            codePath,
                            codeComparator,
                            property,
                            propertyType,
                            propertyException,
                            terminology,
                            contextExpression,
                        )
                    retrieve.resultType = ListType(namedType as DataType)
                    result =
                        if (result == null) {
                            retrieve
                        } else {
                            // Should only include the result if it resolved appropriately with the
                            // codeComparator
                            // Allowing it to go through for now
                            // if (retrieve.getCodeProperty() != null &&
                            //     retrieve.getCodeComparator() != null &&
                            //     retrieve.getCodes() != null) {
                            track(retrieve, ctx)
                            libraryBuilder.resolveUnion(result, retrieve)
                            // }
                        }
                }
            }
        } else {
            val retrieve: Retrieve =
                retrieveBuilder.build(
                    ctx,
                    useStrictRetrieveTyping,
                    namedType,
                    classType,
                    codePath,
                    codeComparator,
                    property,
                    property?.resultType,
                    propertyException,
                    terminology,
                    contextExpression,
                )
            retrieve.resultType = ListType(namedType as DataType)
            result = retrieve
        }
        return result
    }

    override fun visitSourceClause(ctx: SourceClauseContext): Any {
        val hasFrom = "from" == ctx.getChild(0)!!.text
        require(hasFrom || !this.isFromKeywordRequired) {
            "The from keyword is required for queries."
        }
        val sources: MutableList<AliasedQuerySource?> = ArrayList()
        for (source in ctx.aliasedQuerySource()) {
            require(sources.isEmpty() || hasFrom) {
                "The from keyword is required for multi-source queries."
            }
            sources.add(visit(source) as AliasedQuerySource?)
        }
        return sources
    }

    override fun visitQuery(ctx: cqlParser.QueryContext): Query {
        val queryContext = QueryContext()
        libraryBuilder.pushQueryContext(queryContext)
        var sources: kotlin.collections.List<AliasedQuerySource>? = null
        return try {
            queryContext.enterSourceClause()
            try {
                sources = visit(ctx.sourceClause()).cast()
            } finally {
                queryContext.exitSourceClause()
            }
            queryContext.addPrimaryQuerySources(sources!!)
            for (source: AliasedQuerySource in sources) {
                val ir = of.createIdentifierRef().withName(source.alias)
                track(ir, source)
                libraryBuilder.pushIdentifier(ir, source)
            }

            // If we are evaluating a population-level query whose source ranges over any
            // patient-context expressions, then references to patient context expressions within
            // the iteration clauses of the query can be accessed at the patient, rather than the
            // population, context.
            val expressionContextPushed = false
            /*
             * TODO: Address the issue of referencing multiple context expressions within a
             * query (or even expression in general)
             * if (libraryBuilder.inUnfilteredContext() &&
             * queryContext.referencesSpecificContext()) {
             * libraryBuilder.pushExpressionContext("Patient");
             * expressionContextPushed = true;
             * }
             */
            var dfcx: kotlin.collections.List<LetClause> = emptyList()
            try {
                dfcx = if (ctx.letClause() != null) visit(ctx.letClause()!!).cast() else dfcx
                for (letClause: LetClause in dfcx) {
                    val ir = of.createIdentifierRef().withName(letClause.identifier)
                    track(ir, letClause)
                    libraryBuilder.pushIdentifier(ir, letClause)
                }
                val qicx: MutableList<RelationshipClause> = ArrayList()
                for (queryInclusionClauseContext in ctx.queryInclusionClause()) {
                    qicx.add(visit(queryInclusionClauseContext) as RelationshipClause)
                }
                val where =
                    if (ctx.whereClause() != null) visit(ctx.whereClause()!!) as Expression?
                    else null
                // Date-range promotion now runs as a post-visit ElmPass; see DateRangeOptimizer.
                var ret =
                    if (ctx.returnClause() != null) visit(ctx.returnClause()!!) as ReturnClause?
                    else null
                val agg =
                    if (ctx.aggregateClause() != null)
                        visit(ctx.aggregateClause()!!) as AggregateClause?
                    else null
                if (agg == null && ret == null && sources.size > 1) {
                    ret = of.createReturnClause().withDistinct(true)
                    val returnExpression = of.createTuple()
                    val elements = mutableListOf<TupleTypeElement>()
                    for (aqs: AliasedQuerySource in sources) {
                        val element =
                            of.createTupleElement()
                                .withName(aqs.alias)
                                .withValue(of.createAliasRef().withName(aqs.alias))
                        val sourceType =
                            if (aqs.resultType is ListType) (aqs.resultType as ListType).elementType
                            else aqs.resultType
                        element.value!!.resultType =
                            sourceType // Doesn't use the fluent API to avoid casting
                        elements.add(TupleTypeElement(element.name!!, element.value!!.resultType!!))
                        returnExpression.element.add(element)
                    }
                    val returnType = TupleType(elements)
                    returnExpression.resultType =
                        if (queryContext.isSingular) returnType else ListType(returnType)
                    ret.expression = returnExpression
                    ret.resultType = returnExpression.resultType
                }
                queryContext.removeQuerySources(sources)
                queryContext.removeLetClauses(dfcx)
                val queryResultType: DataType? =
                    if (agg != null) {
                        agg.resultType
                    } else if (ret != null) {
                        ret.resultType
                    } else {
                        sources[0].resultType
                    }
                var sort: SortClause? = null
                if (agg == null) {
                    queryContext.resultElementType =
                        if (queryContext.isSingular) null
                        else (queryResultType as ListType?)!!.elementType
                    if (ctx.sortClause() != null) {
                        require(!queryContext.isSingular) {
                            "Sort clause cannot be used in a singular query."
                        }
                        queryContext.enterSortClause()
                        try {
                            sort = visit(ctx.sortClause()!!) as SortClause
                            // Validate that the sort can be performed based on the existence of
                            // comparison operators for all types involved
                            for (sortByItem: SortByItem? in sort.by) {
                                if (sortByItem is ByDirection) {
                                    // validate that there is a comparison operator defined for the
                                    // result element type of the query context
                                    libraryBuilder.verifyComparable(
                                        queryContext.resultElementType!!
                                    )
                                } else {
                                    libraryBuilder.verifyComparable(sortByItem!!.resultType!!)
                                }
                            }
                        } finally {
                            queryContext.exitSortClause()
                        }
                    }
                } else {
                    require(ctx.sortClause() == null) {
                        "Sort clause cannot be used in an aggregate query."
                    }
                }
                val query =
                    of.createQuery()
                        .withSource(sources)
                        .withLet(dfcx)
                        .withRelationship(qicx)
                        .withWhere(where)
                        .withReturn(ret)
                        .withAggregate(agg)
                        .withSort(sort)
                query.resultType = queryResultType
                query
            } finally {
                if (expressionContextPushed) {
                    libraryBuilder.popExpressionContext()
                }
                for (letClause in dfcx) {
                    libraryBuilder.popIdentifier()
                }
            }
        } finally {
            libraryBuilder.popQueryContext()
            if (sources != null) {
                for (source in sources) {
                    libraryBuilder.popIdentifier()
                }
            }
        }
    }

    override fun visitLetClause(ctx: LetClauseContext): Any {
        val letClauseItems: MutableList<LetClause?> = ArrayList()
        for (letClauseItem in ctx.letClauseItem()) {
            letClauseItems.add(visit(letClauseItem) as LetClause?)
        }
        return letClauseItems
    }

    override fun visitLetClauseItem(ctx: LetClauseItemContext): LetClause {
        val letClause =
            of.createLetClause()
                .withExpression(parseExpression(ctx.expression()))
                .withIdentifier(parseString(ctx.identifier()))
        letClause.resultType = letClause.expression!!.resultType
        libraryBuilder.peekQueryContext().addLetClause(letClause)
        return letClause
    }

    override fun visitAliasedQuerySource(ctx: AliasedQuerySourceContext): AliasedQuerySource {
        val source =
            of.createAliasedQuerySource()
                .withExpression(parseExpression(ctx.querySource()))
                .withAlias(parseString(ctx.alias()))
        source.resultType = source.expression!!.resultType
        return source
    }

    override fun visitWithClause(ctx: WithClauseContext): Any {
        val aqs = visit(ctx.aliasedQuerySource()) as AliasedQuerySource
        libraryBuilder.peekQueryContext().addRelatedQuerySource(aqs)
        return try {
            val expression = visit(ctx.expression()) as Expression
            verifyType(expression.resultType, libraryBuilder.resolveTypeName("System", "Boolean"))
            val result: RelationshipClause = of.createWith()
            result.withExpression(aqs.expression).withAlias(aqs.alias).withSuchThat(expression)
            result.resultType = aqs.resultType
            result
        } finally {
            libraryBuilder.peekQueryContext().removeQuerySource(aqs)
        }
    }

    override fun visitWithoutClause(ctx: WithoutClauseContext): Any {
        val aqs = visit(ctx.aliasedQuerySource()) as AliasedQuerySource
        libraryBuilder.peekQueryContext().addRelatedQuerySource(aqs)
        return try {
            val expression = visit(ctx.expression()) as Expression
            verifyType(expression.resultType, libraryBuilder.resolveTypeName("System", "Boolean"))
            val result: RelationshipClause = of.createWithout()
            result.withExpression(aqs.expression).withAlias(aqs.alias).withSuchThat(expression)
            result.resultType = aqs.resultType
            result
        } finally {
            libraryBuilder.peekQueryContext().removeQuerySource(aqs)
        }
    }

    override fun visitWhereClause(ctx: WhereClauseContext): Any {
        val result = visit(ctx.expression()) as Expression
        verifyType(result.resultType, libraryBuilder.resolveTypeName("System", "Boolean"))
        return result
    }

    override fun visitReturnClause(ctx: ReturnClauseContext): ReturnClause {
        val returnClause = of.createReturnClause()
        if (ctx.getChild(1) is TerminalNode) {
            when (ctx.getChild(1)!!.text) {
                "all" -> returnClause.distinct = false
                "distinct" -> returnClause.distinct = true
            }
        }
        returnClause.expression = parseExpression(ctx.expression())
        returnClause.resultType =
            if (libraryBuilder.peekQueryContext().isSingular) returnClause.expression!!.resultType
            else ListType(returnClause.expression!!.resultType!!)
        return returnClause
    }

    override fun visitStartingClause(ctx: StartingClauseContext): Any? {
        if (ctx.simpleLiteral() != null) {
            return visit(ctx.simpleLiteral()!!)
        }
        if (ctx.quantity() != null) {
            return visit(ctx.quantity()!!)
        }
        return if (ctx.expression() != null) {
            visit(ctx.expression()!!)
        } else null
    }

    override fun visitAggregateClause(ctx: AggregateClauseContext): AggregateClause {
        libraryBuilder.checkCompatibilityLevel("Aggregate query clause", "1.5")
        val aggregateClause = of.createAggregateClause()
        if (ctx.getChild(1) is TerminalNode) {
            when (ctx.getChild(1)!!.text) {
                "all" -> aggregateClause.distinct = false
                "distinct" -> aggregateClause.distinct = true
            }
        }
        if (ctx.startingClause() != null) {
            aggregateClause.starting = parseExpression(ctx.startingClause())
        }

        // If there is a starting, that's the type of the var
        // If there's not a starting, push an Any and then attempt to evaluate (might need a type
        // hint here)
        aggregateClause.identifier = parseString(ctx.identifier())
        val accumulator: Expression =
            if (aggregateClause.starting != null) {
                libraryBuilder.buildNull(aggregateClause.starting!!.resultType)
            } else {
                libraryBuilder.buildNull(libraryBuilder.resolveTypeName("System", "Any"))
            }
        val letClause =
            of.createLetClause()
                .withExpression(accumulator)
                .withIdentifier(aggregateClause.identifier)
        letClause.resultType = letClause.expression!!.resultType
        libraryBuilder.peekQueryContext().addLetClause(letClause)
        aggregateClause.expression = parseExpression(ctx.expression())
        aggregateClause.resultType = aggregateClause.expression!!.resultType
        if (aggregateClause.starting == null) {
            accumulator.resultType = aggregateClause.resultType
            aggregateClause.starting = accumulator
        }
        return aggregateClause
    }

    override fun visitSortDirection(ctx: SortDirectionContext): SortDirection {
        return SortDirection.fromValue(ctx.text)
    }

    private fun parseSortDirection(ctx: SortDirectionContext?): SortDirection {
        return if (ctx != null) {
            visitSortDirection(ctx)
        } else SortDirection.ASC
    }

    override fun visitSortByItem(ctx: SortByItemContext): SortByItem {
        val sortExpression = parseExpression(ctx.expressionTerm())!!
        return if (sortExpression is IdentifierRef) {
            of.createByColumn()
                .withPath(sortExpression.name)
                .withDirection(parseSortDirection(ctx.sortDirection()))
                .withResultType(sortExpression.resultType)
        } else
            of.createByExpression()
                .withExpression(sortExpression)
                .withDirection(parseSortDirection(ctx.sortDirection()))
                .withResultType(sortExpression.resultType)
    }

    override fun visitSortClause(ctx: SortClauseContext): SortClause {
        if (ctx.sortDirection() != null) {
            return of.createSortClause()
                .withBy(
                    listOf(
                        of.createByDirection()
                            .withDirection(parseSortDirection(ctx.sortDirection()))
                    )
                )
        }
        val sortItems: MutableList<SortByItem> = ArrayList()
        for (sortByItemContext in ctx.sortByItem()) {
            sortItems.add(visit(sortByItemContext) as SortByItem)
        }
        return of.createSortClause().withBy(sortItems)
    }

    override fun visitQuerySource(ctx: QuerySourceContext): Any? {
        return if (ctx.expression() != null) {
            visit(ctx.expression()!!)
        } else if (ctx.retrieve() != null) {
            visit(ctx.retrieve()!!)
        } else {
            val identifiers: kotlin.collections.List<String> =
                visit(ctx.qualifiedIdentifierExpression()!!).cast()
            resolveQualifiedIdentifier(identifiers)
        }
    }

    override fun visitIndexedExpressionTerm(ctx: IndexedExpressionTermContext): Indexer {
        val indexer =
            of.createIndexer()
                .withOperand(
                    listOf(
                        parseExpression(ctx.expressionTerm())!!,
                        parseExpression(ctx.expression())!!,
                    )
                )

        // TODO: Support zero-based indexers as defined by the isZeroBased attribute
        return systemCall("Indexer", indexer)
    }

    override fun visitInvocationExpressionTerm(ctx: InvocationExpressionTermContext): Expression? {
        val left = parseExpression(ctx.expressionTerm())!!
        return libraryBuilder.scopeManager.withExpressionTarget(left) {
            visit(ctx.qualifiedInvocation()) as Expression?
        }
    }

    override fun visitExternalConstant(ctx: ExternalConstantContext): Expression? {
        return libraryBuilder.resolveIdentifier(ctx.text, true)
    }

    override fun visitThisInvocation(ctx: ThisInvocationContext): Expression? {
        return libraryBuilder.resolveIdentifier(ctx.text, true)
    }

    override fun visitMemberInvocation(ctx: MemberInvocationContext): Expression? {
        val identifier = parseString(ctx.referentialIdentifier())!!
        return resolveMemberIdentifier(identifier)
    }

    override fun visitQualifiedMemberInvocation(
        ctx: QualifiedMemberInvocationContext
    ): Expression? {
        val identifier = parseString(ctx.referentialIdentifier())!!
        return resolveMemberIdentifier(identifier)
    }

    private fun resolveQualifiedIdentifier(
        identifiers: kotlin.collections.List<String>
    ): Expression? {
        var current: Expression? = null
        for (identifier in identifiers) {
            current =
                if (current == null) {
                    resolveIdentifier(identifier)
                } else {
                    libraryBuilder.resolveAccessor(current, identifier)
                }
        }
        return current
    }

    private fun resolveMemberIdentifier(identifier: String): Expression? {
        if (libraryBuilder.hasExpressionTarget()) {
            val target = libraryBuilder.popExpressionTarget()
            return try {
                libraryBuilder.resolveAccessor(target, identifier)
            } finally {
                libraryBuilder.pushExpressionTarget(target)
            }
        }
        return resolveIdentifier(identifier)
    }

    private fun resolveIdentifier(identifier: String): Expression? {
        // If the identifier cannot be resolved in the library builder, check for forward
        // declarations for expressions and parameters
        var result = libraryBuilder.resolveIdentifier(identifier, false)
        if (result == null) {
            val expressionInfo = this.libraryInfo.resolveExpressionReference(identifier)
            if (expressionInfo != null) {
                val saveContext = saveCurrentContext(expressionInfo.context)
                try {
                    val saveChunks = this.chunks
                    this.chunks = Stack()
                    this.forwards.push(expressionInfo)
                    try {
                        requireNotNull(expressionInfo.definition) {
                            // ERROR:
                            "Could not validate reference to expression ${expressionInfo.name} because its definition contains errors."
                        }

                        // Have to call the visit to get the outer processing to occur
                        visit(expressionInfo.definition)
                    } finally {
                        this.chunks = saveChunks
                        this.forwards.pop()
                    }
                } finally {
                    this.currentContext = saveContext
                }
            }
            val parameterInfo = this.libraryInfo.resolveParameterReference(identifier)
            if (parameterInfo != null) {
                visitParameterDefinition(parameterInfo.definition)
            }
            result = libraryBuilder.resolveIdentifier(identifier, true)
        }
        return result
    }

    private fun ensureSystemFunctionName(libraryName: String?, functionName: String): String {
        if (libraryName == null || libraryName == "System") {
            // Because these functions can be both a keyword and the name of a method, they can be
            // resolved by the parser as a function, instead of as the keyword-based parser rule. In
            // this case, the function name needs to be translated to the System function name in
            // order to resolve.
            return when (functionName) {
                "contains" -> "Contains"
                "distinct" -> "Distinct"
                "exists" -> "Exists"
                "in" -> "In"
                "not" -> "Not"
                else -> functionName
            }
        }
        return functionName
    }

    private fun resolveFunction(
        libraryName: String?,
        functionName: String,
        paramList: ParamListContext?,
    ): Expression? {
        val expressions: MutableList<Expression> = ArrayList()
        if (paramList?.expression() != null) {
            for (expressionContext in paramList.expression()) {
                expressions.add(visit(expressionContext) as Expression)
            }
        }
        return resolveFunction(
            libraryName,
            functionName,
            expressions,
            mustResolve = true,
            allowPromotionAndDemotion = false,
            allowFluent = false,
        )
    }

    @Suppress("LongParameterList")
    fun resolveFunction(
        libraryName: String?,
        functionName: String,
        expressions: kotlin.collections.List<Expression>,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean,
    ): Expression? {
        var name = functionName
        if (allowFluent) {
            libraryBuilder.checkCompatibilityLevel("Fluent functions", "1.5")
        }
        name = ensureSystemFunctionName(libraryName, name)

        // 1. Ensure all overloads of the function are registered with the operator map
        // 2. Resolve the function, allowing for the case that operator map is a skeleton
        // 3. If the resolution from the operator map is a skeleton, compile the function body to
        // determine the result type

        // Find all functionDefinitionInfo instances with the given name register each
        // functionDefinitionInfo
        if (
            libraryName == null || libraryName == "" || libraryName == this.libraryInfo.libraryName
        ) {
            val fdis = this.libraryInfo.resolveFunctionReference(name)
            if (fdis != null) {
                for ((_, context, definition) in fdis) {
                    val saveContext = saveCurrentContext(context)
                    try {
                        registerFunctionDefinition(definition)
                    } finally {
                        this.currentContext = saveContext
                    }
                }
            }
        }
        val result =
            libraryBuilder.resolveFunction(
                libraryName,
                name,
                expressions,
                mustResolve,
                allowPromotionAndDemotion,
                allowFluent,
            )
        if (
            result is FunctionRefInvocation &&
                result.resolution != null &&
                (result.resolution!!.operator.libraryName == null ||
                    (result.resolution!!.operator.libraryName ==
                        libraryBuilder.compiledLibrary.identifier!!.id))
        ) {
            val op = result.resolution!!.operator
            val fh = getFunctionHeader(op)
            if (!fh.isCompiled) {
                val ctx = getFunctionDefinitionContext(fh)
                val saveContext = saveCurrentContext(fh.functionDef.context!!)
                val saveChunks = this.chunks
                this.chunks = Stack()
                try {
                    val fd = compileFunctionDefinition(ctx)
                    op.resultType = fd.resultType
                    result.resultType = op.resultType
                } finally {
                    this.currentContext = saveContext
                    this.chunks = saveChunks
                }
            }
        }
        if (mustResolve) {
            // Extra internal error handling, these should never be hit if the two-phase operator
            // compile is working as expected
            requireNotNull(result) { "Internal error: could not resolve function" }
            requireNotNull(result.expression.resultType) {
                "Internal error: could not determine result type"
            }
        }
        return result?.expression
    }

    private fun resolveFunctionOrQualifiedFunction(
        identifier: String,
        paramListCtx: ParamListContext?,
    ): Expression? {
        if (libraryBuilder.hasExpressionTarget()) {
            val target: Expression = libraryBuilder.popExpressionTarget()
            try {
                // If the target is a library reference, resolve as a standard qualified call
                if (target is LibraryRef) {
                    return resolveFunction(target.libraryName, identifier, paramListCtx)
                }

                // NOTE: FHIRPath method invocation
                // If the target is an expression, resolve as a method invocation
                if (this.isMethodInvocationEnabled) {
                    return this.systemMethodResolver.resolveMethod(
                        target,
                        identifier,
                        paramListCtx,
                        true,
                    )
                }
                if (!this.isMethodInvocationEnabled) {
                    throw CqlSemanticException(
                        "The identifier $identifier could not be resolved as an invocation because method-style invocation is disabled."
                    )
                }
                throw IllegalArgumentException(
                    "Invalid invocation target: ${target::class.simpleName}"
                )
            } finally {
                libraryBuilder.pushExpressionTarget(target)
            }
        }

        // If we are in an implicit $this context, the function may be resolved as a method
        // invocation
        val thisRef: Expression? = libraryBuilder.resolveIdentifier("\$this", false)
        if (thisRef != null) {
            val result: Expression? =
                this.systemMethodResolver.resolveMethod(thisRef, identifier, paramListCtx, false)
            if (result != null) {
                return result
            }
        }

        // If we are in an implicit context (i.e. a context named the same as a parameter), the
        // function may be resolved as a method invocation
        val parameterRef: ParameterRef? = libraryBuilder.resolveImplicitContext()
        if (parameterRef != null) {
            val result: Expression? =
                this.systemMethodResolver.resolveMethod(
                    parameterRef,
                    identifier,
                    paramListCtx,
                    false,
                )
            if (result != null) {
                return result
            }
        }

        // If there is no target, resolve as a system function
        return resolveFunction(null, identifier, paramListCtx)
    }

    override fun visitFunction(ctx: FunctionContext): Expression? {
        return resolveFunctionOrQualifiedFunction(
            parseString(ctx.referentialIdentifier())!!,
            ctx.paramList(),
        )
    }

    override fun visitQualifiedFunction(ctx: QualifiedFunctionContext): Expression? {
        return resolveFunctionOrQualifiedFunction(
            parseString(ctx.identifierOrFunctionIdentifier())!!,
            ctx.paramList(),
        )
    }

    override fun visitFunctionBody(ctx: FunctionBodyContext): Any? {
        return visit(ctx.expression())
    }

    private fun getFunctionHeader(ctx: FunctionDefinitionContext): FunctionHeader {
        var fh = this.functionHeaders[ctx]
        if (fh == null) {
            val saveChunks = this.chunks
            this.chunks = Stack()
            fh =
                try {
                    // Have to call the visit to allow the outer processing to occur
                    parseFunctionHeader(ctx)
                } finally {
                    this.chunks = saveChunks
                }
            this.functionHeaders[ctx] = fh
            this.functionDefinitions[fh] = ctx
            this.functionHeadersByDef[fh.functionDef] = fh
        }
        return fh
    }

    private fun getFunctionDef(op: Operator): FunctionDef? {
        var target: FunctionDef? = null
        val st: MutableList<DataType> = ArrayList()
        for (dt in op.signature.operandTypes) {
            st.add(dt)
        }
        val fds = libraryBuilder.compiledLibrary.resolveFunctionRef(op.name, st)
        for (fd in fds) {
            if (fd.operand.size == op.signature.size) {
                val signatureTypes = op.signature.operandTypes.iterator()
                var signaturesMatch = true
                for (i in fd.operand.indices) {
                    if (!equal(fd.operand[i].resultType, signatureTypes.next())) {
                        signaturesMatch = false
                    }
                }
                if (signaturesMatch) {
                    check(target == null) {
                        "Internal error attempting to resolve function header for ${op.name}"
                    }
                    target = fd
                }
            }
        }
        return target
    }

    private fun getFunctionHeaderByDef(fd: FunctionDef): FunctionHeader? {
        return this.functionHeadersByDef[fd]
    }

    private fun getFunctionHeader(op: Operator): FunctionHeader {
        val fd =
            getFunctionDef(op)
                ?: throw IllegalArgumentException(
                    "Could not resolve function header for operator ${op.name}"
                )
        return getFunctionHeaderByDef(fd)
            ?: throw IllegalArgumentException(
                "Could not resolve function header for operator ${op.name}"
            )
    }

    private fun getFunctionDefinitionContext(fh: FunctionHeader): FunctionDefinitionContext {
        return this.functionDefinitions[fh]
            ?: throw IllegalArgumentException(
                "Could not resolve function definition context for function header ${fh.functionDef.name}"
            )
    }

    private fun registerFunctionDefinition(ctx: FunctionDefinitionContext) {
        val fh = getFunctionHeader(ctx)
        if (!libraryBuilder.compiledLibrary.contains(fh.functionDef)) {
            libraryBuilder.addExpression(fh.functionDef)
        }
    }

    private fun compileFunctionDefinition(ctx: FunctionDefinitionContext): FunctionDef {
        val fh: FunctionHeader = getFunctionHeader(ctx)
        val functionDef: FunctionDef = fh.functionDef
        val resultType: TypeSpecifier? = fh.resultType
        val op: Operator =
            libraryBuilder.resolveFunctionDefinition(fh.functionDef)
                ?: throw IllegalArgumentException(
                    "Internal error: Could not resolve operator map entry for function header ${fh.mangledName}"
                )

        val ir = of.createIdentifierRef().withName(functionDef.name)
        track(ir, ctx.identifierOrFunctionIdentifier())
        libraryBuilder.pushIdentifier(ir, functionDef, IdentifierScope.GLOBAL)

        val operand = op.functionDef!!.operand as kotlin.collections.List<OperandDef>
        for (operandDef: OperandDef in operand) {
            val oir = of.createIdentifierRef().withName(operandDef.name)
            track(oir, operandDef)
            libraryBuilder.pushIdentifier(oir, operandDef)
        }
        try {
            if (ctx.functionBody() != null) {
                libraryBuilder.scopeManager.withFunctionDef(functionDef) {
                    libraryBuilder.scopeManager.withExpressionContext(this.currentContext) {
                        libraryBuilder.scopeManager.withExpressionDefinition(fh.mangledName) {
                            functionDef.expression = parseExpression(ctx.functionBody())
                        }
                    }
                }
                if (resultType != null && functionDef.expression?.resultType != null) {
                    require(subTypeOf(functionDef.expression!!.resultType, resultType.resultType)) {
                        // ERROR:
                        "Function ${functionDef.name} has declared return type ${resultType.resultType} but the function body returns incompatible type ${functionDef.expression!!.resultType}."
                    }
                }
                functionDef.resultType = functionDef.expression!!.resultType
                op.resultType = functionDef.resultType
            } else {
                functionDef.external = true
                requireNotNull(resultType) {
                    // ERROR:
                    "Function ${functionDef.name} is marked external but does not declare a return type."
                }
                functionDef.resultType = resultType.resultType
                op.resultType = functionDef.resultType
            }
            functionDef.context = this.currentContext
            fh.isCompiled = true
            return functionDef
        } finally {
            for (operandDef: OperandDef? in operand) {
                try {
                    libraryBuilder.popIdentifier()
                } catch (e: Exception) {
                    logger.warn(e) { "Error popping identifier" }
                }
            }
            // Intentionally do _not_ pop the function name, it needs to remain in global scope!
        }
    }

    override fun visitFunctionDefinition(ctx: FunctionDefinitionContext): Any {
        return libraryBuilder.scopeManager.withIdentifierScope {
            registerFunctionDefinition(ctx)
            compileFunctionDefinition(ctx)
        }
    }

    private fun parseLiteralExpression(pt: ParseTree?): Expression? {
        return libraryBuilder.scopeManager.withLiteralContext { parseExpression(pt) }
    }

    private fun parseExpression(pt: ParseTree?): Expression? {
        return if (pt == null) null else visit(pt) as Expression?
    }

    private fun getTrackBack(tree: ParseTree): TrackBack? {
        if (tree is ParserRuleContext) {
            return getTrackBack(tree)
        }
        return if (tree is TerminalNode) {
            getTrackBack(tree)
        } else null
    }

    private fun getTrackBack(node: TerminalNode): TrackBack {
        return TrackBack(
            libraryBuilder.libraryIdentifier,
            node.symbol.line,
            node.symbol.charPositionInLine + 1, // 1-based instead of 0-based
            node.symbol.line,
            node.symbol.charPositionInLine + node.symbol.text!!.length,
        )
    }

    private fun getTrackBack(ctx: ParserRuleContext): TrackBack {
        return TrackBack(
            libraryBuilder.libraryIdentifier,
            ctx.start?.line ?: 0,
            ctx.start?.charPositionInLine?.inc() ?: 0, // 1-based instead of 0-based
            ctx.stop?.line ?: 0,
            (ctx.stop?.charPositionInLine ?: 0) +
                (ctx.stop?.text?.length ?: 0), // 1-based instead of 0-based
        )
    }

    private fun decorate(element: Element, tb: TrackBack?) {
        if (locatorsEnabled() && tb != null) {
            element.locator = tb.toLocator()
        }
        if (resultTypesEnabled() && element.resultType != null) {
            if (element.resultType is NamedType) {
                element.resultTypeName = libraryBuilder.dataTypeToQName(element.resultType)
            } else {
                element.resultTypeSpecifier =
                    libraryBuilder.dataTypeToTypeSpecifier(element.resultType)
            }
        }
    }

    private fun track(trackable: Element?, pt: ParseTree): TrackBack? {
        val tb = getTrackBack(pt)
        if (tb != null) {
            trackable!!.trackbacks.add(tb)
        }
        if (trackable is Element) {
            decorate(trackable, tb)
        }
        return tb
    }

    private fun track(element: Element?, from: Element): TrackBack? {
        val tb = if (from.trackbacks.isNotEmpty()) from.trackbacks[0] else null
        if (tb != null) {
            element!!.trackbacks.add(tb)
        }
        if (element is Element) {
            decorate(element, tb)
        }
        return tb
    }

    // ========================================================================
    // Library preprocessing: scan top-level children to populate libraryInfo
    // with name → definition-context entries so forward references resolve
    // during the main visit. Formerly a separate CqlPreprocessor walk.
    // ========================================================================

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth")
    private fun preprocessLibrary(ctx: LibraryContext) {
        var ppContext = "Unfiltered"
        var implicitContext = false
        var lastSourceIndex = -1

        fun processHeader(child: ParseTree, info: BaseInfo) {
            val sourceInterval = child.sourceInterval
            val beforeDefinition = sourceInterval.a - 1
            if (beforeDefinition >= lastSourceIndex) {
                val header = Interval(lastSourceIndex + 1, sourceInterval.a - 1)
                lastSourceIndex = sourceInterval.b
                info.headerInterval = header
                info.header = tokenStream.getText(header)
            }
        }

        for (i in 0 until ctx.childCount) {
            val raw = ctx.getChild(i) ?: continue
            @Suppress("TooGenericExceptionCaught")
            try {
                // LibraryContext's grammar wraps non-library children in DefinitionContext and
                // StatementContext. Unwrap to the concrete *DefinitionContext so the `when` below
                // can match on concrete definition types.
                val child =
                    when (raw) {
                        is DefinitionContext ->
                            raw.usingDefinition()
                                ?: raw.includeDefinition()
                                ?: raw.codesystemDefinition()
                                ?: raw.valuesetDefinition()
                                ?: raw.codeDefinition()
                                ?: raw.conceptDefinition()
                                ?: raw.parameterDefinition()
                                ?: continue
                        is StatementContext ->
                            raw.contextDefinition()
                                ?: raw.expressionDefinition()
                                ?: raw.functionDefinition()
                                ?: continue
                        else -> raw
                    }
                when (child) {
                    is LibraryDefinitionContext -> {
                        @Suppress("UNCHECKED_CAST")
                        val identifiers = visit(child.qualifiedIdentifier()) as MutableList<String>
                        val libraryName = identifiers.removeAt(identifiers.size - 1)
                        val namespaceName =
                            if (identifiers.isNotEmpty()) identifiers.joinToString(".") else null
                        val version =
                            if (child.versionSpecifier() != null)
                                visit(child.versionSpecifier()!!) as String
                            else null
                        libraryInfo = LibraryInfo(namespaceName, libraryName, version, child)
                        processHeader(child, libraryInfo)

                        // Set the compiled library identifier so subsequent translation has the
                        // right namespace/name/version context.
                        val vid =
                            org.hl7.elm.r1
                                .VersionedIdentifier()
                                .withId(libraryName)
                                .withVersion(version)
                        vid.system =
                            when {
                                namespaceName != null ->
                                    libraryBuilder.resolveNamespaceUri(namespaceName, true)
                                libraryBuilder.namespaceInfo != null ->
                                    libraryBuilder.namespaceInfo.uri
                                else -> null
                            }
                        libraryBuilder.libraryIdentifier = vid
                    }
                    is IncludeDefinitionContext -> {
                        @Suppress("UNCHECKED_CAST")
                        val identifiers = visit(child.qualifiedIdentifier()) as MutableList<String>
                        val name = identifiers.removeAt(identifiers.size - 1)
                        val namespaceName =
                            if (identifiers.isNotEmpty()) identifiers.joinToString(".") else null
                        val version =
                            if (child.versionSpecifier() != null)
                                visit(child.versionSpecifier()!!) as String
                            else null
                        val localName =
                            if (child.localIdentifier() != null)
                                parseString(child.localIdentifier())!!
                            else name
                        val info =
                            IncludeDefinitionInfo(namespaceName, name, version, localName, child)
                        processHeader(child, info)
                        libraryInfo.addIncludeDefinition(info)
                    }
                    is UsingDefinitionContext -> {
                        @Suppress("UNCHECKED_CAST")
                        val identifiers = visit(child.qualifiedIdentifier()) as MutableList<String>
                        val unqualified = identifiers.removeAt(identifiers.size - 1)
                        val namespaceForUsing =
                            if (identifiers.isNotEmpty()) identifiers.joinToString(".") else null
                        val version =
                            if (child.versionSpecifier() != null)
                                visit(child.versionSpecifier()!!) as String
                            else null
                        val localName =
                            if (child.localIdentifier() != null)
                                parseString(child.localIdentifier())!!
                            else unqualified
                        val info =
                            UsingDefinitionInfo(
                                namespaceForUsing,
                                unqualified,
                                version,
                                localName,
                                child,
                            )
                        processHeader(child, info)
                        libraryInfo.addUsingDefinition(info)

                        // Side effect: load the model into the ModelManager. Later type-name
                        // resolution (NamedTypeSpecifier visits, parameter defaults, retrieves)
                        // depends on the model being present by the time the main pass begins.
                        val modelNamespace: NamespaceInfo? =
                            when {
                                identifiers.isNotEmpty() -> {
                                    val nsName = identifiers.joinToString(".")
                                    val uri = libraryBuilder.resolveNamespaceUri(nsName, true)
                                    NamespaceInfo(nsName, uri!!)
                                }
                                libraryBuilder.isWellKnownModelName(unqualified) -> null
                                libraryBuilder.namespaceInfo != null -> libraryBuilder.namespaceInfo
                                else -> null
                            }
                        getModel(modelNamespace, unqualified, version, localName)
                    }
                    is CodesystemDefinitionContext -> {
                        val info =
                            CodesystemDefinitionInfo(parseString(child.identifier())!!, child)
                        processHeader(child, info)
                        libraryInfo.addCodesystemDefinition(info)
                    }
                    is ValuesetDefinitionContext -> {
                        val info = ValuesetDefinitionInfo(parseString(child.identifier())!!, child)
                        processHeader(child, info)
                        libraryInfo.addValuesetDefinition(info)
                    }
                    is CodeDefinitionContext -> {
                        val info = CodeDefinitionInfo(parseString(child.identifier())!!, child)
                        processHeader(child, info)
                        libraryInfo.addCodeDefinition(info)
                    }
                    is ConceptDefinitionContext -> {
                        val info = ConceptDefinitionInfo(parseString(child.identifier())!!, child)
                        processHeader(child, info)
                        libraryInfo.addConceptDefinition(info)
                    }
                    is ParameterDefinitionContext -> {
                        val info = ParameterDefinitionInfo(parseString(child.identifier())!!, child)
                        processHeader(child, info)
                        libraryInfo.addParameterDefinition(info)
                    }
                    is ContextDefinitionContext -> {
                        val modelIdentifier =
                            if (child.modelIdentifier() != null)
                                parseString(child.modelIdentifier())
                            else null
                        val unqualified = parseString(child.identifier())!!
                        ppContext =
                            if (!modelIdentifier.isNullOrEmpty()) "$modelIdentifier.$unqualified"
                            else unqualified
                        val info = ContextDefinitionInfo(child)
                        processHeader(child, info)
                        libraryInfo.addContextDefinition(info)
                        if (!implicitContext && unqualified != "Unfiltered") {
                            libraryInfo.addExpressionDefinition(
                                ExpressionDefinitionInfo(unqualified, ppContext, null)
                            )
                            implicitContext = true
                        }
                    }
                    is ExpressionDefinitionContext -> {
                        val info =
                            ExpressionDefinitionInfo(
                                parseString(child.identifier())!!,
                                ppContext,
                                child,
                            )
                        processHeader(child, info)
                        libraryInfo.addExpressionDefinition(info)
                    }
                    is FunctionDefinitionContext -> {
                        val info =
                            FunctionDefinitionInfo(
                                parseString(child.identifierOrFunctionIdentifier())!!,
                                ppContext,
                                child,
                            )
                        processHeader(child, info)
                        libraryInfo.addFunctionDefinition(info)
                    }
                }
            } catch (e: CqlCompilerException) {
                libraryBuilder.recordParsingException(e)
            } catch (e: Exception) {
                libraryBuilder.recordParsingException(
                    CqlSemanticException(
                        e.message ?: "Internal error processing top-level definition",
                        getTrackBack(raw),
                        CqlCompilerException.ErrorSeverity.Error,
                        e,
                    )
                )
            }
        }
    }
}
