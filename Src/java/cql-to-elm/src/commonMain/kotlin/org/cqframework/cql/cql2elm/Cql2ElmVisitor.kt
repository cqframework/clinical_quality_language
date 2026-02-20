package org.cqframework.cql.cql2elm

import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.TokenStream
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
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorElmCommonVisitor
import org.cqframework.cql.cql2elm.preprocessor.ExpressionDefinitionInfo
import org.cqframework.cql.cql2elm.preprocessor.LibraryInfo
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.cql2elm.utils.IdentityHashMap
import org.cqframework.cql.cql2elm.utils.Stack
import org.cqframework.cql.cql2elm.utils.isLeapYear
import org.cqframework.cql.cql2elm.utils.logger
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.cqframework.cql.gen.cqlParser.*
import org.cqframework.cql.shared.BigDecimal
import org.hl7.cql.model.*
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
class Cql2ElmVisitor(
    libraryBuilder: LibraryBuilder,
    tokenStream: TokenStream,
    libraryInfo: LibraryInfo,
) : CqlPreprocessorElmCommonVisitor(libraryBuilder, tokenStream) {
    private val systemMethodResolver = SystemMethodResolver(this, libraryBuilder)
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

    init {
        this.libraryInfo = libraryInfo
    }

    override fun defaultResult(): Any? {
        return null
    }

    private inline fun <reified T> Any?.cast(): T {
        return this as T
    }

    override fun visitLibrary(ctx: LibraryContext): Any? {
        var lastResult: Any? = null

        // Loop through and call visit on each child (to ensure they are tracked)
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
            libraryBuilder.pushExpressionContext(this.currentContext)
            try {
                libraryBuilder.pushExpressionDefinition(identifier)
                try {
                    def =
                        of.createExpressionDef()
                            .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                            .withName(identifier)
                            .withContext(this.currentContext)
                            .withExpression(visit(ctx.expression()) as Expression?)
                    if (def.expression != null) {
                        def.resultType = def.expression!!.resultType
                    }
                    libraryBuilder.addExpression(def)
                } finally {
                    libraryBuilder.popExpressionDefinition()
                }
            } finally {
                libraryBuilder.popExpressionContext()
            }
        }
        return def
    }

    override fun visitExpressionDefinition(ctx: ExpressionDefinitionContext): ExpressionDef? {
        libraryBuilder.pushIdentifierScope()
        return try {
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
        } finally {
            libraryBuilder.popIdentifierScope()
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
        val timePattern = Regex("T(\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?")
        // -1-------2---3-------4---5-------6---7-----------
        val matcher = timePattern.matchEntire(input)
        return if (matcher != null) {
            try {
                val result = of.createTime()
                val hour = matcher.groups[1]!!.value.toInt()
                var minute = -1
                var second = -1
                var millisecond = -1
                require(hour in 0..24) { "Invalid hour in time literal ($hour)." }
                result.hour = libraryBuilder.createLiteral(hour)
                if (matcher.groups[3] != null) {
                    minute = matcher.groups[3]!!.value.toInt()
                    require(!((minute < 0) || (minute >= 60) || (hour == 24 && minute > 0))) {
                        "Invalid minute in time literal ($minute)."
                    }
                    result.minute = libraryBuilder.createLiteral(minute)
                }
                if (matcher.groups[5] != null) {
                    second = matcher.groups[5]!!.value.toInt()
                    require(!((second < 0) || (second >= 60) || (hour == 24 && second > 0))) {
                        "Invalid second in time literal ($second)."
                    }
                    result.second = libraryBuilder.createLiteral(second)
                }
                if (matcher.groups[7] != null) {
                    millisecond = matcher.groups[7]!!.value.toInt()
                    require(hour == 24 && millisecond == 0 || millisecond >= 0) {
                        "Invalid millisecond in time literal ($millisecond)."
                    }
                    result.millisecond = libraryBuilder.createLiteral(millisecond)
                }
                result.resultType = libraryBuilder.resolveTypeName("System", "Time")
                result
            } catch (e: RuntimeException) {
                throw IllegalArgumentException(
                    "Invalid time input ($input). Use ISO 8601 time representation (hh:mm:ss.fff).",
                    e,
                )
            }
        } else {
            @Suppress("UseRequire")
            throw IllegalArgumentException(
                "Invalid time input ($input). Use ISO 8601 time representation (hh:mm:ss.fff)."
            )
        }
    }

    private fun parseDateTimeLiteral(input: String): Expression {
        /*
         * DATETIME
         * : '@'
         * [0-9][0-9][0-9][0-9] // year
         * (
         * (
         * '-'[0-9][0-9] // month
         * (
         * (
         * '-'[0-9][0-9] // day
         * ('T' TIMEFORMAT?)?
         * )
         * | 'T'
         * )?
         * )
         * | 'T'
         * )?
         * ('Z' | ('+' | '-') [0-9][0-9]':'[0-9][0-9])? // timezone offset
         * ;
         */
        val dateTimePattern =
            Regex(
                "(\\d{4})(((-(\\d{2}))(((-(\\d{2}))((T)((\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?)?)?)|(T))?)|(T))?((Z)|(([+-])(\\d{2})(:(\\d{2}))))?"
            )
        // 1-------234-5--------678-9--------11--11-------1---1-------1---1-------1---1-----------------2------2----22---22-----2-------2---2-----------
        // ----------------------------------01--23-------4---5-------6---7-------8---9-----------------0------1----23---45-----6-------7---8-----------

        /*
         * year - group 1
         * month - group 5
         * day - group 9
         * day dateTime indicator - group 11
         * hour - group 13
         * minute - group 15
         * second - group 17
         * millisecond - group 19
         * month dateTime indicator - group 20
         * year dateTime indicator - group 21
         * utc indicator - group 23
         * timezone offset polarity - group 25
         * timezone offset hour - group 26
         * timezone offset minute - group 28
         */

        /*
         * Pattern dateTimePattern =
         * Pattern.compile(
         * "(\\d{4})(-(\\d{2}))?(-(\\d{2}))?((Z)|(T((\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?)?((Z)|(([+-])(\\d{2})(\\:?(\\d{2}))?))?))?"
         * );
         * //1-------2-3---------4-5---------67---8-91-------1---1-------1---1-------1--
         * -1-------------11---12-----2-------2----2---------------
         * //----------------------------------------0-------1---2-------3---4-------5--
         * -6-------------78---90-----1-------2----3---------------
         */
        val matcher = dateTimePattern.matchEntire(input)
        return if (matcher != null) {
            try {
                val result = of.createDateTime()
                val year = matcher.groups[1]!!.value.toInt()
                var month = -1
                var day = -1
                var hour = -1
                var minute = -1
                var second = -1
                var millisecond = -1
                result.year = libraryBuilder.createLiteral(year)
                if (matcher.groups[5] != null) {
                    month = matcher.groups[5]!!.value.toInt()
                    require(month in 0..12) { "Invalid month in date/time literal ($input)." }
                    result.month = libraryBuilder.createLiteral(month)
                }
                if (matcher.groups[9] != null) {
                    day = matcher.groups[9]!!.value.toInt()
                    var maxDay = 31
                    when (month) {
                        2 -> maxDay = if (isLeapYear(year)) 29 else 28
                        4,
                        6,
                        9,
                        11 -> maxDay = 30
                    }
                    require(day in 0..maxDay) { "Invalid day in date/time literal ($input)." }
                    result.day = libraryBuilder.createLiteral(day)
                }
                if (matcher.groups[13] != null) {
                    hour = matcher.groups[13]!!.value.toInt()
                    require(hour in 0..24) { "Invalid hour in date/time literal ($input)." }
                    result.hour = libraryBuilder.createLiteral(hour)
                }
                if (matcher.groups[15] != null) {
                    minute = matcher.groups[15]!!.value.toInt()
                    require(minute in 0..60 && !(hour == 24 && minute > 0)) {
                        "Invalid minute in date/time literal ($input)."
                    }
                    result.minute = libraryBuilder.createLiteral(minute)
                }
                if (matcher.groups[17] != null) {
                    second = matcher.groups[17]!!.value.toInt()
                    require(second in 0..60 && !(hour == 24 && second > 0)) {
                        "Invalid second in date/time literal ($input)."
                    }
                    result.second = libraryBuilder.createLiteral(second)
                }
                if (matcher.groups[19] != null) {
                    millisecond = matcher.groups[19]!!.value.toInt()
                    require(millisecond >= 0 && !(hour == 24 && millisecond > 0)) {
                        "Invalid millisecond in date/time literal ($input)."
                    }
                    result.millisecond = libraryBuilder.createLiteral(millisecond)
                }
                if (matcher.groups[23] != null && (matcher.groups[23]!!.value == "Z")) {
                    result.timezoneOffset = libraryBuilder.createLiteral(0.0)
                }
                if (matcher.groups[25] != null) {
                    val offsetPolarity = if ((matcher.groups[25]!!.value == "+")) 1 else -1
                    if (matcher.groups[28] != null) {
                        val hourOffset = matcher.groups[26]!!.value.toInt()
                        require(hourOffset in 0..14) {
                            "Timezone hour offset is out of range in date/time literal ($input)."
                        }
                        val minuteOffset = matcher.groups[28]!!.value.toInt()
                        require(minuteOffset in 0..60 && !(hourOffset == 14 && minuteOffset > 0)) {
                            "Timezone minute offset is out of range in date/time literal ($input)."
                        }
                        result.timezoneOffset =
                            libraryBuilder.createLiteral(
                                (hourOffset + (minuteOffset.toDouble() / 60)) * offsetPolarity
                            )
                    } else {
                        if (matcher.groups[26] != null) {
                            val hourOffset = matcher.groups[26]!!.value.toInt()
                            require(hourOffset in 0..14) {
                                "Timezone hour offset is out of range in date/time literal ($input)."
                            }
                            result.timezoneOffset =
                                libraryBuilder.createLiteral(
                                    (hourOffset * offsetPolarity).toDouble()
                                )
                        }
                    }
                }
                if (
                    (result.hour == null) &&
                        (matcher.groups[11] == null) &&
                        (matcher.groups[20] == null) &&
                        (matcher.groups[21] == null)
                ) {
                    val date = of.createDate()
                    date.year = result.year
                    date.month = result.month
                    date.day = result.day
                    date.resultType = libraryBuilder.resolveTypeName("System", "Date")
                    return date
                }
                result.resultType = libraryBuilder.resolveTypeName("System", "DateTime")
                result
            } catch (e: RuntimeException) {
                throw IllegalArgumentException(
                    "Invalid date-time input ($input)." +
                        " Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|(+/-hh:mm)).",
                    e,
                )
            }
        } else
            throw IllegalArgumentException(
                "Invalid date-time input ($input)." +
                    " Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|+/-hh:mm))."
            )
    }

    override fun visitDateLiteral(ctx: DateLiteralContext): Any {
        var input = ctx.text
        if (input.startsWith("@")) {
            input = input.substring(1)
        }
        return parseDateTimeLiteral(input)
    }

    override fun visitDateTimeLiteral(ctx: DateTimeLiteralContext): Any {
        var input = ctx.text
        if (input.startsWith("@")) {
            input = input.substring(1)
        }
        return parseDateTimeLiteral(input)
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
        libraryBuilder.resolveUnaryCall("System", "Not", result)
        return result
    }

    override fun visitExistenceExpression(ctx: ExistenceExpressionContext): Exists {
        val result = of.createExists().withOperand(parseExpression(ctx.expression()))
        libraryBuilder.resolveUnaryCall("System", "Exists", result)
        return result
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
        libraryBuilder.resolveBinaryCall("System", operatorName, (exp))
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
        libraryBuilder.resolveBinaryCall("System", "Power", power)
        return power
    }

    override fun visitPolarityExpressionTerm(ctx: PolarityExpressionTermContext): Any? {
        if (ctx.getChild(0)!!.text == "+") {
            return visit(ctx.expressionTerm())
        }
        val result = of.createNegate().withOperand(parseExpression(ctx.expressionTerm()))
        libraryBuilder.resolveUnaryCall("System", "Negate", result)
        return result
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
            libraryBuilder.resolveBinaryCall("System", operatorName, (exp as BinaryExpression?)!!)
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
            libraryBuilder.resolveNaryCall("System", operatorName, concatenate)
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
        libraryBuilder.resolveUnaryCall("System", "SingletonFrom", result)
        return result
    }

    override fun visitPointExtractorExpressionTerm(
        ctx: PointExtractorExpressionTermContext
    ): PointFrom {
        val result = of.createPointFrom().withOperand(parseExpression(ctx.expressionTerm()))
        libraryBuilder.resolveUnaryCall("System", "PointFrom", result)
        return result
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
        libraryBuilder.resolveUnaryCall("System", operatorName, result)
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
        libraryBuilder.resolveUnaryCall("System", operatorName, result)
        return result
    }

    override fun visitDurationExpressionTerm(ctx: DurationExpressionTermContext): DurationBetween {
        // duration in days of X <=> days between start of X and end of X
        val operand = parseExpression(ctx.expressionTerm())
        val start = of.createStart().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "Start", start)
        val end = of.createEnd().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "End", end)
        val result =
            of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(listOf(start, end))
        libraryBuilder.resolveBinaryCall("System", "DurationBetween", result)
        return result
    }

    override fun visitDifferenceExpressionTerm(
        ctx: DifferenceExpressionTermContext
    ): DifferenceBetween {
        // difference in days of X <=> difference in days between start of X and end of X
        val operand = parseExpression(ctx.expressionTerm())
        val start = of.createStart().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "Start", start)
        val end = of.createEnd().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "End", end)
        val result =
            of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(listOf(start, end))
        libraryBuilder.resolveBinaryCall("System", "DifferenceBetween", result)
        return result
    }

    override fun visitBetweenExpression(ctx: BetweenExpressionContext): Expression {
        // X properly? between Y and Z
        val first = parseExpression(ctx.expression())!!
        val second = parseExpression(ctx.expressionTerm(0))!!
        val third = parseExpression(ctx.expressionTerm(1))
        val isProper = ctx.getChild(0)!!.text == "properly"
        return if (first.resultType is IntervalType) {
            val result =
                if (isProper) of.createProperIncludedIn()
                else
                    of.createIncludedIn()
                        .withOperand(
                            listOf(first, libraryBuilder.createInterval(second, true, third, true))
                        )
            libraryBuilder.resolveBinaryCall(
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
            libraryBuilder.resolveBinaryCall(
                "System",
                if (isProper) "Greater" else "GreaterOrEqual",
                (result.operand[0] as BinaryExpression),
            )
            libraryBuilder.resolveBinaryCall(
                "System",
                if (isProper) "Less" else "LessOrEqual",
                (result.operand[1] as BinaryExpression),
            )
            libraryBuilder.resolveBinaryCall("System", "And", result)
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
        libraryBuilder.resolveBinaryCall("System", "DurationBetween", result)
        return result
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
        libraryBuilder.resolveBinaryCall("System", "DifferenceBetween", result)
        return result
    }

    override fun visitWidthExpressionTerm(ctx: WidthExpressionTermContext): Any {
        val result: UnaryExpression =
            of.createWidth().withOperand(parseExpression(ctx.expressionTerm()))
        libraryBuilder.resolveUnaryCall("System", "Width", result)
        return result
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
                    libraryBuilder.resolveBinaryCall("System", "In", inExpression)
                    return inExpression
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
                    libraryBuilder.resolveBinaryCall("System", "Contains", contains)
                    return contains
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
                    libraryBuilder.resolveBinaryCall("System", "Contains", contains)
                    return contains
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
        libraryBuilder.resolveBinaryCall("System", "And", and)
        return and
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
            libraryBuilder.resolveBinaryCall("System", "Xor", xor)
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
            libraryBuilder.resolveBinaryCall("System", "Or", or)
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
        libraryBuilder.resolveBinaryCall("System", "Implies", implies)
        return implies
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
            libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent)
            if ("~" != parseString(ctx.getChild(1))) {
                track(equivalent, ctx)
                val not = of.createNot().withOperand(equivalent)
                libraryBuilder.resolveUnaryCall("System", "Not", not)
                return not
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
            libraryBuilder.resolveBinaryCall("System", "Equal", equal)
            if ("=" != parseString(ctx.getChild(1))) {
                track(equal, ctx)
                val not = of.createNot().withOperand(equal)
                libraryBuilder.resolveUnaryCall("System", "Not", not)
                return not
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
        libraryBuilder.resolveBinaryCall("System", operatorName, exp)
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
            return libraryBuilder.resolveBinaryCall("System", "ConvertQuantity", convertQuantity)
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
                libraryBuilder.resolveUnaryCall("System", "IsNull", exp)
            }
            "true" -> {
                exp = of.createIsTrue().withOperand(left)
                libraryBuilder.resolveUnaryCall("System", "IsTrue", exp)
            }
            "false" -> {
                exp = of.createIsFalse().withOperand(left)
                libraryBuilder.resolveUnaryCall("System", "IsFalse", exp)
            }
            else -> throw IllegalArgumentException("Unknown boolean test predicate $lastChild.")
        }
        if ("not" == nextToLast) {
            track(exp, ctx)
            exp = of.createNot().withOperand(exp)
            libraryBuilder.resolveUnaryCall("System", "Not", exp)
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
        if (("starts" == firstChild.text)) {
            val start: Start = of.createStart().withOperand(timingOperator.left)
            track(start, firstChild)
            libraryBuilder.resolveUnaryCall("System", "Start", start)
            timingOperator.left = start
        }
        if (("ends" == firstChild.text)) {
            val end: End = of.createEnd().withOperand(timingOperator.left)
            track(end, firstChild)
            libraryBuilder.resolveUnaryCall("System", "End", end)
            timingOperator.left = end
        }
        val lastChild: ParseTree = ctx.getChild(ctx.childCount - 1)!!
        if (("start" == lastChild.text)) {
            val start: Start = of.createStart().withOperand(timingOperator.right)
            track(start, lastChild)
            libraryBuilder.resolveUnaryCall("System", "Start", start)
            timingOperator.right = start
        }
        if (("end" == lastChild.text)) {
            val end: End = of.createEnd().withOperand(timingOperator.right)
            track(end, lastChild)
            libraryBuilder.resolveUnaryCall("System", "End", end)
            timingOperator.right = end
        }
        val operatorName: String?
        var operator: BinaryExpression?
        var allowPromotionAndDemotion = false
        if (ctx.relativeQualifier() == null) {
            operator =
                if (ctx.dateTimePrecision() != null) {
                    of.createSameAs()
                        .withPrecision(
                            parseComparableDateTimePrecision(ctx.dateTimePrecision()!!.text)
                        )
                } else {
                    of.createSameAs()
                }
            operatorName = "SameAs"
        } else {
            when (ctx.relativeQualifier()!!.text) {
                "or after" -> {
                    operator =
                        if (ctx.dateTimePrecision() != null) {
                            of.createSameOrAfter()
                                .withPrecision(
                                    parseComparableDateTimePrecision(ctx.dateTimePrecision()!!.text)
                                )
                        } else {
                            of.createSameOrAfter()
                        }
                    operatorName = "SameOrAfter"
                    allowPromotionAndDemotion = true
                }
                "or before" -> {
                    operator =
                        if (ctx.dateTimePrecision() != null) {
                            of.createSameOrBefore()
                                .withPrecision(
                                    parseComparableDateTimePrecision(ctx.dateTimePrecision()!!.text)
                                )
                        } else {
                            of.createSameOrBefore()
                        }
                    operatorName = "SameOrBefore"
                    allowPromotionAndDemotion = true
                }
                else ->
                    throw IllegalArgumentException(
                        "Unknown relative qualifier: '${ctx.relativeQualifier()!!.text}'."
                    )
            }
        }
        operator = operator.withOperand(listOf(timingOperator.left, timingOperator.right))
        libraryBuilder.resolveBinaryCall(
            "System",
            operatorName,
            operator,
            true,
            allowPromotionAndDemotion,
        )
        return operator
    }

    override fun visitIncludesIntervalOperatorPhrase(
        ctx: IncludesIntervalOperatorPhraseContext
    ): Any? {
        // 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?
        var isProper = false
        var isRightPoint = false
        val timingOperator = this.timingOperators.peek()
        for (pt in ctx.children!!) {
            if ("properly" == pt.text) {
                isProper = true
                continue
            }
            if ("start" == pt.text) {
                val start = of.createStart().withOperand(timingOperator.right)
                track(start, pt)
                libraryBuilder.resolveUnaryCall("System", "Start", start)
                timingOperator.right = start
                isRightPoint = true
                continue
            }
            if ("end" == pt.text) {
                val end = of.createEnd().withOperand(timingOperator.right)
                track(end, pt)
                libraryBuilder.resolveUnaryCall("System", "End", end)
                timingOperator.right = end
                isRightPoint = true
                continue
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
            if ("starts" == pt.text) {
                val start = of.createStart().withOperand(timingOperator.left)
                track(start, pt)
                libraryBuilder.resolveUnaryCall("System", "Start", start)
                timingOperator.left = start
                isLeftPoint = true
                continue
            }
            if ("ends" == pt.text) {
                val end = of.createEnd().withOperand(timingOperator.left)
                track(end, pt)
                libraryBuilder.resolveUnaryCall("System", "End", end)
                timingOperator.left = end
                isLeftPoint = true
                continue
            }
            if ("properly" == pt.text) {
                isProper = true
                continue
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
            if ("starts" == child.text) {
                val start = of.createStart().withOperand(timingOperator.left)
                track(start, child)
                libraryBuilder.resolveUnaryCall("System", "Start", start)
                timingOperator.left = start
                continue
            }
            if ("ends" == child.text) {
                val end = of.createEnd().withOperand(timingOperator.left)
                track(end, child)
                libraryBuilder.resolveUnaryCall("System", "End", end)
                timingOperator.left = end
                continue
            }
            if ("start" == child.text) {
                val start = of.createStart().withOperand(timingOperator.right)
                track(start, child)
                libraryBuilder.resolveUnaryCall("System", "Start", start)
                timingOperator.right = start
                continue
            }
            if ("end" == child.text) {
                val end = of.createEnd().withOperand(timingOperator.right)
                track(end, child)
                libraryBuilder.resolveUnaryCall("System", "End", end)
                timingOperator.right = end
                continue
            }
        }
        for (child in ctx.temporalRelationship().children!!) {
            if ("before" == child.text) {
                isBefore = true
                continue
            }
            if ("on or" == child.text || "or on" == child.text) {
                isInclusive = true
                continue
            }
        }
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
            else null
        if (ctx.quantityOffset() == null) {
            return if (isInclusive) {
                if (isBefore) {
                    val sameOrBefore =
                        of.createSameOrBefore()
                            .withOperand(listOf(timingOperator.left, timingOperator.right))
                    if (dateTimePrecision != null) {
                        sameOrBefore.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall(
                        "System",
                        "SameOrBefore",
                        sameOrBefore,
                        mustResolve = true,
                        allowPromotionAndDemotion = true,
                    )
                    sameOrBefore
                } else {
                    val sameOrAfter =
                        of.createSameOrAfter()
                            .withOperand(listOf(timingOperator.left, timingOperator.right))
                    if (dateTimePrecision != null) {
                        sameOrAfter.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall(
                        "System",
                        "SameOrAfter",
                        sameOrAfter,
                        mustResolve = true,
                        allowPromotionAndDemotion = true,
                    )
                    sameOrAfter
                }
            } else {
                if (isBefore) {
                    val before =
                        of.createBefore()
                            .withOperand(listOf(timingOperator.left, timingOperator.right))
                    if (dateTimePrecision != null) {
                        before.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall(
                        "System",
                        "Before",
                        before,
                        mustResolve = true,
                        allowPromotionAndDemotion = true,
                    )
                    before
                } else {
                    val after =
                        of.createAfter()
                            .withOperand(listOf(timingOperator.left, timingOperator.right))
                    if (dateTimePrecision != null) {
                        after.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall(
                        "System",
                        "After",
                        after,
                        mustResolve = true,
                        allowPromotionAndDemotion = true,
                    )
                    after
                }
            }
        } else {
            val quantity = visit(ctx.quantityOffset()!!.quantity()!!) as Quantity
            if (timingOperator.left.resultType is IntervalType) {
                if (isBefore) {
                    val end = of.createEnd().withOperand(timingOperator.left)
                    track(end, timingOperator.left)
                    libraryBuilder.resolveUnaryCall("System", "End", end)
                    timingOperator.left = end
                } else {
                    val start = of.createStart().withOperand(timingOperator.left)
                    track(start, timingOperator.left)
                    libraryBuilder.resolveUnaryCall("System", "Start", start)
                    timingOperator.left = start
                }
            }
            if (timingOperator.right.resultType is IntervalType) {
                if (isBefore) {
                    val start = of.createStart().withOperand(timingOperator.right)
                    track(start, timingOperator.right)
                    libraryBuilder.resolveUnaryCall("System", "Start", start)
                    timingOperator.right = start
                } else {
                    val end = of.createEnd().withOperand(timingOperator.right)
                    track(end, timingOperator.right)
                    libraryBuilder.resolveUnaryCall("System", "End", end)
                    timingOperator.right = end
                }
            }
            if (
                ctx.quantityOffset()!!.offsetRelativeQualifier() == null &&
                    ctx.quantityOffset()!!.exclusiveRelativeQualifier() == null
            ) {
                // Use a SameAs
                // For a Before, subtract the quantity from the right operand
                // For an After, add the quantity to the right operand
                if (isBefore) {
                    val subtract =
                        of.createSubtract().withOperand(listOf(timingOperator.right, quantity))
                    track(subtract, timingOperator.right)
                    libraryBuilder.resolveBinaryCall("System", "Subtract", subtract)
                    timingOperator.right = subtract
                } else {
                    val add = of.createAdd().withOperand(listOf(timingOperator.right, quantity))
                    track(add, timingOperator.right)
                    libraryBuilder.resolveBinaryCall("System", "Add", add)
                    timingOperator.right = add
                }
                val sameAs =
                    of.createSameAs().withOperand(listOf(timingOperator.left, timingOperator.right))
                if (dateTimePrecision != null) {
                    sameAs.precision = parseComparableDateTimePrecision(dateTimePrecision)
                }
                libraryBuilder.resolveBinaryCall("System", "SameAs", sameAs)
                return sameAs
            } else {
                val isOffsetInclusive = ctx.quantityOffset()!!.offsetRelativeQualifier() != null
                val qualifier =
                    if (ctx.quantityOffset()!!.offsetRelativeQualifier() != null)
                        ctx.quantityOffset()!!.offsetRelativeQualifier()!!.text
                    else ctx.quantityOffset()!!.exclusiveRelativeQualifier()!!.text
                when (qualifier) {
                    "more than",
                    "or more" -> // For More Than/Or More, use a
                        // Before/After/SameOrBefore/SameOrAfter
                        // For a Before, subtract the quantity from the right operand
                        // For an After, add the quantity to the right operand
                        return if (isBefore) {
                            val subtract =
                                of.createSubtract()
                                    .withOperand(listOf(timingOperator.right, quantity))
                            track(subtract, timingOperator.right)
                            libraryBuilder.resolveBinaryCall("System", "Subtract", subtract)
                            timingOperator.right = subtract
                            if (!isOffsetInclusive) {
                                val before =
                                    of.createBefore()
                                        .withOperand(
                                            listOf(timingOperator.left, timingOperator.right)
                                        )
                                if (dateTimePrecision != null) {
                                    before.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "Before",
                                    before,
                                    mustResolve = true,
                                    allowPromotionAndDemotion = true,
                                )
                                before
                            } else {
                                val sameOrBefore =
                                    of.createSameOrBefore()
                                        .withOperand(
                                            listOf(timingOperator.left, timingOperator.right)
                                        )
                                if (dateTimePrecision != null) {
                                    sameOrBefore.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "SameOrBefore",
                                    sameOrBefore,
                                    mustResolve = true,
                                    allowPromotionAndDemotion = true,
                                )
                                sameOrBefore
                            }
                        } else {
                            val add =
                                of.createAdd().withOperand(listOf(timingOperator.right, quantity))
                            track(add, timingOperator.right)
                            libraryBuilder.resolveBinaryCall("System", "Add", add)
                            timingOperator.right = add
                            if (!isOffsetInclusive) {
                                val after =
                                    of.createAfter()
                                        .withOperand(
                                            listOf(timingOperator.left, timingOperator.right)
                                        )
                                if (dateTimePrecision != null) {
                                    after.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "After",
                                    after,
                                    mustResolve = true,
                                    allowPromotionAndDemotion = true,
                                )
                                after
                            } else {
                                val sameOrAfter =
                                    of.createSameOrAfter()
                                        .withOperand(
                                            listOf(timingOperator.left, timingOperator.right)
                                        )
                                if (dateTimePrecision != null) {
                                    sameOrAfter.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "SameOrAfter",
                                    sameOrAfter,
                                    mustResolve = true,
                                    allowPromotionAndDemotion = true,
                                )
                                sameOrAfter
                            }
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
                            libraryBuilder.resolveBinaryCall(
                                "System",
                                "Subtract",
                                (lowerBound as BinaryExpression?)!!,
                            )
                            upperBound = right
                        } else {
                            lowerBound = right
                            upperBound = of.createAdd().withOperand(listOf(right, quantity))
                            track(upperBound, right)
                            libraryBuilder.resolveBinaryCall(
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
                            inExpression.precision =
                                parseComparableDateTimePrecision(dateTimePrecision)
                        }
                        track(inExpression, ctx.quantityOffset()!!)
                        libraryBuilder.resolveBinaryCall("System", "In", inExpression)

                        // if the offset or comparison is inclusive, add a null check for B to
                        // ensure correct interpretation
                        if (isOffsetInclusive || isInclusive) {
                            val nullTest = of.createIsNull().withOperand(right)
                            track(nullTest, ctx.quantityOffset()!!)
                            libraryBuilder.resolveUnaryCall("System", "IsNull", nullTest)
                            val notNullTest = of.createNot().withOperand(nullTest)
                            track(notNullTest, ctx.quantityOffset()!!)
                            libraryBuilder.resolveUnaryCall("System", "Not", notNullTest)
                            val and = of.createAnd().withOperand(listOf(inExpression, notNullTest))
                            track(and, ctx.quantityOffset()!!)
                            libraryBuilder.resolveBinaryCall("System", "And", and)
                            return and
                        }

                        // Otherwise, return the constructed in
                        return inExpression
                    }
                }
            }
        }
        throw IllegalArgumentException("Unable to resolve interval operator phrase.")
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
            if ("starts" == child.text) {
                val start = of.createStart().withOperand(timingOperator.left)
                track(start, child)
                libraryBuilder.resolveUnaryCall("System", "Start", start)
                timingOperator.left = start
                continue
            }
            if ("ends" == child.text) {
                val end = of.createEnd().withOperand(timingOperator.left)
                track(end, child)
                libraryBuilder.resolveUnaryCall("System", "End", end)
                timingOperator.left = end
                continue
            }
            if ("start" == child.text) {
                val start = of.createStart().withOperand(timingOperator.right)
                track(start, child)
                libraryBuilder.resolveUnaryCall("System", "Start", start)
                timingOperator.right = start
                continue
            }
            if ("end" == child.text) {
                val end = of.createEnd().withOperand(timingOperator.right)
                track(end, child)
                libraryBuilder.resolveUnaryCall("System", "End", end)
                timingOperator.right = end
                continue
            }
            if ("properly" == child.text) {
                isProper = true
                continue
            }
        }
        val quantity = visit(ctx.quantity()) as Quantity
        var lowerBound: Expression?
        var upperBound: Expression?
        var initialBound: Expression? = null
        if (timingOperator.right.resultType is IntervalType) {
            lowerBound = of.createStart().withOperand(timingOperator.right)
            track(lowerBound, ctx.quantity())
            libraryBuilder.resolveUnaryCall("System", "Start", lowerBound)
            upperBound = of.createEnd().withOperand(timingOperator.right)
            track(upperBound, ctx.quantity())
            libraryBuilder.resolveUnaryCall("System", "End", upperBound)
        } else {
            lowerBound = timingOperator.right
            upperBound = timingOperator.right
            initialBound = lowerBound
        }
        lowerBound = of.createSubtract().withOperand(listOf(lowerBound, quantity))
        track(lowerBound, ctx.quantity())
        libraryBuilder.resolveBinaryCall("System", "Subtract", (lowerBound as BinaryExpression?)!!)
        upperBound = of.createAdd().withOperand(listOf(upperBound, quantity))
        track(upperBound, ctx.quantity())
        libraryBuilder.resolveBinaryCall("System", "Add", (upperBound as BinaryExpression?)!!)
        val interval = libraryBuilder.createInterval(lowerBound, !isProper, upperBound, !isProper)
        track(interval, ctx.quantity())
        val inExpression = of.createIn().withOperand(listOf(timingOperator.left, interval))
        libraryBuilder.resolveBinaryCall("System", "In", inExpression)

        // if the within is not proper and the interval is being constructed from a single point,
        // add a null check for that point to ensure correct interpretation
        if (!isProper && initialBound != null) {
            val nullTest = of.createIsNull().withOperand(initialBound)
            track(nullTest, ctx.quantity())
            libraryBuilder.resolveUnaryCall("System", "IsNull", nullTest)
            val notNullTest = of.createNot().withOperand(nullTest)
            track(notNullTest, ctx.quantity())
            libraryBuilder.resolveUnaryCall("System", "Not", notNullTest)
            val and = of.createAnd().withOperand(listOf(inExpression, notNullTest))
            track(and, ctx.quantity())
            libraryBuilder.resolveBinaryCall("System", "And", and)
            return and
        }

        // Otherwise, return the constructed in
        return inExpression
    }

    override fun visitMeetsIntervalOperatorPhrase(ctx: MeetsIntervalOperatorPhraseContext): Any {
        val operatorName: String?
        val operator: BinaryExpression
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
            else null
        if (ctx.childCount == 1 + if (dateTimePrecision == null) 0 else 1) {
            operator =
                if (dateTimePrecision != null)
                    of.createMeets()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createMeets()
            operatorName = "Meets"
        } else {
            if ("before" == ctx.getChild(1)!!.text) {
                operator =
                    if (dateTimePrecision != null)
                        of.createMeetsBefore()
                            .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                    else of.createMeetsBefore()
                operatorName = "MeetsBefore"
            } else {
                operator =
                    if (dateTimePrecision != null)
                        of.createMeetsAfter()
                            .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                    else of.createMeetsAfter()
                operatorName = "MeetsAfter"
            }
        }
        operator.withOperand(
            listOf(this.timingOperators.peek().left, this.timingOperators.peek().right)
        )
        libraryBuilder.resolveBinaryCall("System", operatorName, operator)
        return operator
    }

    override fun visitOverlapsIntervalOperatorPhrase(
        ctx: OverlapsIntervalOperatorPhraseContext
    ): Any {
        val operatorName: String?
        val operator: BinaryExpression
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
            else null
        if (ctx.childCount == 1 + if (dateTimePrecision == null) 0 else 1) {
            operator =
                if (dateTimePrecision != null)
                    of.createOverlaps()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createOverlaps()
            operatorName = "Overlaps"
        } else {
            if ("before" == ctx.getChild(1)!!.text) {
                operator =
                    if (dateTimePrecision != null)
                        of.createOverlapsBefore()
                            .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                    else of.createOverlapsBefore()
                operatorName = "OverlapsBefore"
            } else {
                operator =
                    if (dateTimePrecision != null)
                        of.createOverlapsAfter()
                            .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                    else of.createOverlapsAfter()
                operatorName = "OverlapsAfter"
            }
        }
        operator.withOperand(
            listOf(this.timingOperators.peek().left, this.timingOperators.peek().right)
        )
        libraryBuilder.resolveBinaryCall("System", operatorName, operator)
        return operator
    }

    override fun visitStartsIntervalOperatorPhrase(
        ctx: StartsIntervalOperatorPhraseContext
    ): Starts {
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
            else null
        val starts =
            (if (dateTimePrecision != null)
                    of.createStarts()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createStarts())
                .withOperand(
                    listOf(this.timingOperators.peek().left, this.timingOperators.peek().right)
                )
        libraryBuilder.resolveBinaryCall("System", "Starts", starts)
        return starts
    }

    override fun visitEndsIntervalOperatorPhrase(ctx: EndsIntervalOperatorPhraseContext): Ends {
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier()!!.dateTimePrecision().text
            else null
        val ends =
            (if (dateTimePrecision != null)
                    of.createEnds()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createEnds())
                .withOperand(
                    listOf(this.timingOperators.peek().left, this.timingOperators.peek().right)
                )
        libraryBuilder.resolveBinaryCall("System", "Ends", ends)
        return ends
    }

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
                libraryBuilder.resolveUnaryCall("System", "Distinct", distinct)
                return distinct
            }
            "flatten" -> {
                val flatten = of.createFlatten().withOperand(parseExpression(ctx.expression()))
                libraryBuilder.resolveUnaryCall("System", "Flatten", flatten)
                return flatten
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
                    // libraryBuilder.resolveBinaryCall("System", "Subtract", subtract);
                    // per = subtract;
                }
            }
        }
        when (ctx.getChild(0)!!.text) {
            "expand" -> {
                val expand: Expand = of.createExpand().withOperand(listOf(source, per!!))
                libraryBuilder.resolveBinaryCall("System", "Expand", expand)
                return expand
            }
            "collapse" -> {
                val collapse: Collapse = of.createCollapse().withOperand(listOf(source, per!!))
                libraryBuilder.resolveBinaryCall("System", "Collapse", collapse)
                return collapse
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
                        buildRetrieve(
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
                        )
                    mrRetrieve.resultType = ListType(namedType as DataType)
                    val mDataType: DataType? = libraryBuilder.resolveTypeName(model, "Medication")
                    val mClassType = mDataType as ClassType
                    val mNamedType: NamedType = mClassType
                    val mRetrieve: Retrieve =
                        buildRetrieve(
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
                    libraryBuilder.resolveBinaryCall("System", "Equal", e)

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
                            libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent)
                            equivalent
                        }
                    val a: And = of.createAnd().withOperand(listOf(e, terminologyComparison))
                    libraryBuilder.resolveBinaryCall("System", "And", a)
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
                        buildRetrieve(
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
                buildRetrieve(
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
                )
            retrieve.resultType = ListType(namedType as DataType)
            result = retrieve
        }
        return result
    }

    @Suppress("LongParameterList")
    private fun buildRetrieve(
        ctx: RetrieveContext,
        useStrictRetrieveTyping: Boolean,
        namedType: NamedType?,
        classType: ClassType,
        codePath: String?,
        codeComparator: String?,
        property: Property?,
        propertyType: DataType?,
        propertyException: Exception?,
        terminology: Expression?,
    ): Retrieve {
        var codeComparator: String? = codeComparator
        val retrieve: Retrieve =
            of.createRetrieve()
                .withDataType(libraryBuilder.dataTypeToQName(namedType as DataType?))
                .withTemplateId(classType.identifier)
                .withCodeProperty(codePath)
        if (ctx.contextIdentifier() != null) {
            val identifiers: kotlin.collections.List<String> =
                visit(ctx.contextIdentifier()!!).cast()
            val contextExpression: Expression? = resolveQualifiedIdentifier(identifiers)
            retrieve.context = contextExpression
        }
        if (terminology != null) {
            // Resolve the terminology target using an in or ~ operator
            try {
                if (codeComparator == null) {
                    codeComparator = "~"
                    if (terminology.resultType is ListType) {
                        codeComparator = "in"
                    } else if (libraryBuilder.isCompatibleWith("1.5")) {
                        codeComparator =
                            if (
                                (propertyType != null &&
                                    propertyType.isSubTypeOf(
                                        libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                                    ))
                            ) {
                                if (
                                    terminology.resultType!!.isSubTypeOf(
                                        libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                                    )
                                )
                                    "~"
                                else "contains"
                            } else {
                                if (
                                    terminology.resultType!!.isSubTypeOf(
                                        libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                                    )
                                )
                                    "in"
                                else "~"
                            }
                    }
                }
                if (property == null) {
                    throw (propertyException)!!
                }
                when (codeComparator) {
                    "in" -> {
                        when (
                            val inExpression: Expression =
                                libraryBuilder.resolveIn(property, terminology)
                        ) {
                            is In -> {
                                retrieve.codes = inExpression.operand[1]
                            }
                            is InValueSet -> {
                                retrieve.codes = inExpression.valueset
                            }
                            is InCodeSystem -> {
                                retrieve.codes = inExpression.codesystem
                            }
                            is AnyInValueSet -> {
                                retrieve.codes = inExpression.valueset
                            }
                            is AnyInCodeSystem -> {
                                retrieve.codes = inExpression.codesystem
                            }
                            else -> {
                                // ERROR:
                                // WARNING:
                                libraryBuilder.recordParsingException(
                                    CqlSemanticException(
                                        "Unexpected membership operator ${inExpression::class.simpleName} in retrieve",
                                        getTrackBack(ctx),
                                        if (useStrictRetrieveTyping)
                                            CqlCompilerException.ErrorSeverity.Error
                                        else CqlCompilerException.ErrorSeverity.Warning,
                                    )
                                )
                            }
                        }
                    }
                    "contains" -> {
                        val contains: Expression =
                            libraryBuilder.resolveContains(property, terminology)
                        if (contains is Contains) {
                            retrieve.codes = contains.operand[1]
                        }
                        // TODO: Introduce support for the contains operator to make this possible
                        // to support with a retrieve (direct-reference code negation)
                        // ERROR:
                        libraryBuilder.recordParsingException(
                            CqlSemanticException(
                                "Terminology resolution using contains is not supported at this time. Use a where clause with an in operator instead.",
                                getTrackBack(ctx),
                                if (useStrictRetrieveTyping)
                                    CqlCompilerException.ErrorSeverity.Error
                                else CqlCompilerException.ErrorSeverity.Warning,
                            )
                        )
                    }
                    "~" -> {

                        // Resolve with equivalent to verify the type of the target
                        val equivalent: BinaryExpression =
                            of.createEquivalent().withOperand(listOf(property, terminology))
                        libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent)

                        // Automatically promote to a list for use in the retrieve target
                        if (
                            !(equivalent.operand[1].resultType is ListType ||
                                (libraryBuilder.isCompatibleWith("1.5") &&
                                    equivalent.operand[1]
                                        .resultType!!
                                        .isSubTypeOf(
                                            libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                                        )))
                        ) {
                            retrieve.codes = libraryBuilder.resolveToList(equivalent.operand[1])
                        } else {
                            retrieve.codes = equivalent.operand[1]
                        }
                    }
                    "=" -> {

                        // Resolve with equality to verify the type of the source and target
                        val equal: BinaryExpression =
                            of.createEqual().withOperand(listOf(property, terminology))
                        libraryBuilder.resolveBinaryCall("System", "Equal", equal)

                        // Automatically promote to a list for use in the retrieve target
                        if (
                            !(equal.operand[1].resultType is ListType ||
                                (libraryBuilder.isCompatibleWith("1.5") &&
                                    equal.operand[1]
                                        .resultType!!
                                        .isSubTypeOf(
                                            libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                                        )))
                        ) {
                            retrieve.codes = libraryBuilder.resolveToList(equal.operand[1])
                        } else {
                            retrieve.codes = equal.operand[1]
                        }
                    }
                    else -> // ERROR:
                        // WARNING:
                        libraryBuilder.recordParsingException(
                            CqlSemanticException(
                                "Unknown code comparator $codeComparator in retrieve",
                                getTrackBack(ctx.codeComparator()!!),
                                if (useStrictRetrieveTyping)
                                    CqlCompilerException.ErrorSeverity.Error
                                else CqlCompilerException.ErrorSeverity.Warning,
                            )
                        )
                }
                retrieve.codeComparator = codeComparator

                // Verify that the type of the terminology target is a List<Code>
                // Due to implicit conversion defined by specific models, the resolution path above
                // may result in a List<Concept>
                // In that case, convert to a list of code (Union the Code elements of the Concepts
                // in the list)
                if (
                    ((retrieve.codes != null) &&
                        (retrieve.codes!!.resultType != null) &&
                        retrieve.codes!!.resultType is ListType &&
                        ((retrieve.codes!!.resultType as ListType).elementType ==
                            libraryBuilder.resolveTypeName("System", "Concept")))
                ) {
                    if (retrieve.codes is ToList) {
                        // ToList will always have a single argument
                        val toList: ToList = retrieve.codes as ToList
                        // If that argument is a ToConcept, replace the ToList argument with the
                        // code (skip the implicit conversion, the data access layer is responsible
                        // for it)
                        if (toList.operand is ToConcept) {
                            toList.operand = (toList.operand as ToConcept).operand
                        } else {
                            // Otherwise, access the codes property of the resulting Concept
                            val codesAccessor: Expression =
                                libraryBuilder.buildProperty(
                                    toList.operand,
                                    "codes",
                                    false,
                                    toList.operand!!.resultType,
                                )
                            retrieve.codes = codesAccessor
                        }
                    } else {
                        // WARNING:
                        libraryBuilder.recordParsingException(
                            CqlSemanticException(
                                "Terminology target is a list of concepts, but expects a list of codes",
                                getTrackBack(ctx),
                                CqlCompilerException.ErrorSeverity.Warning,
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // If something goes wrong attempting to resolve, just set to the expression and
                // report it as a warning, it shouldn't prevent translation unless the modelinfo
                // indicates strict retrieve typing
                if (
                    ((libraryBuilder.isCompatibleWith("1.5") &&
                        !(terminology.resultType!!.isSubTypeOf(
                            libraryBuilder.resolveTypeName("System", "Vocabulary")!!
                        ))) ||
                        (!libraryBuilder.isCompatibleWith("1.5") &&
                            terminology.resultType !is ListType))
                ) {
                    retrieve.codes = libraryBuilder.resolveToList(terminology)
                } else {
                    retrieve.codes = terminology
                }
                retrieve.codeComparator = codeComparator
                // ERROR:
                // WARNING:
                libraryBuilder.recordParsingException(
                    CqlSemanticException(
                        "Could not resolve membership operator for terminology target of the retrieve.",
                        getTrackBack(ctx),
                        if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                        else CqlCompilerException.ErrorSeverity.Warning,
                        e,
                    )
                )
            }
        }
        return retrieve
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
                var where =
                    if (ctx.whereClause() != null) visit(ctx.whereClause()!!) as Expression?
                    else null
                if (this.dateRangeOptimization && where != null) {
                    for (aqs: AliasedQuerySource in sources) {
                        where = optimizeDateRangeInQuery(where, aqs)
                    }
                }
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

    // TODO: Expand this optimization to work the DateLow/DateHigh property attributes
    /**
     * Some systems may wish to optimize performance by restricting retrieves with available date
     * ranges. Specifying date ranges in a retrieve was removed from the CQL grammar, but it is
     * still possible to extract date ranges from the where clause and put them in the Retrieve in
     * ELM. The `optimizeDateRangeInQuery` method attempts to do this automatically. If optimization
     * is possible, it will remove the corresponding "during" from the where clause and insert the
     * date range into the Retrieve.
     *
     * @param aqs the AliasedQuerySource containing the ClinicalRequest to possibly refactor a date
     *   range into.
     * @param where the Where clause to search for potential date range optimizations
     * @return the where clause with optimized "durings" removed, or `null` if there is no longer a
     *   Where clause after optimization.
     */
    fun optimizeDateRangeInQuery(where: Expression?, aqs: AliasedQuerySource): Expression? {
        var where = where
        if (aqs.expression is Retrieve) {
            val retrieve = aqs.expression as Retrieve
            val alias = aqs.alias
            if (
                (where is IncludedIn || where is In) &&
                    attemptDateRangeOptimization(where, retrieve, alias!!)
            ) {
                where = null
            } else if (where is And && attemptDateRangeOptimization(where, retrieve, alias!!)) {
                // Now optimize out the trues from the Ands
                where = consolidateAnd(where)
            }
        }
        return where
    }

    /**
     * Test a `BinaryExpression` expression and determine if it is suitable to be refactored into
     * the `Retrieve` as a date range restriction. If so, adjust the `Retrieve` accordingly and
     * return `true`.
     *
     * @param during the `BinaryExpression` expression to potentially refactor into the `Retrieve`
     * @param retrieve the `Retrieve` to add qualifying date ranges to (if applicable)
     * @param alias the alias of the `Retrieve` in the query.
     * @return `true` if the date range was set in the `Retrieve`; `false` otherwise.
     */
    private fun attemptDateRangeOptimization(
        during: BinaryExpression,
        retrieve: Retrieve,
        alias: String,
    ): Boolean {
        if (retrieve.dateProperty != null || retrieve.dateRange != null) {
            return false
        }
        val left = during.operand[0]
        val right = during.operand[1]
        val propertyPath = getPropertyPath(left, alias)
        if (propertyPath != null && isRHSEligibleForDateRangeOptimization(right)) {
            retrieve.dateProperty = propertyPath
            retrieve.dateRange = right
            return true
        }
        return false
    }

    /**
     * Collapse a property path expression back to it's qualified form for use as the path attribute
     * of the retrieve.
     *
     * @param reference the `Expression` to collapse
     * @param alias the alias of the `Retrieve` in the query.
     * @return The collapsed path operands (or sub-operands) were modified; `false` otherwise.
     */
    private fun getPropertyPath(reference: Expression, alias: String): String? {
        var ref = reference
        ref = getConversionReference(ref)
        ref = getChoiceSelection(ref)
        if (ref is Property) {
            val property = ref
            if (alias == property.scope) {
                return property.path
            } else if (property.source != null) {
                val subPath = getPropertyPath(property.source!!, alias)
                if (subPath != null) {
                    return "$subPath.${property.path}"
                }
            }
        }
        return null
    }

    /**
     * If this is a conversion operator, return the argument of the conversion, on the grounds that
     * the date range optimization should apply through a conversion (i.e. it is an order-preserving
     * conversion)
     *
     * @param reference the `Expression` to examine
     * @return The argument to the conversion operator if there was one, otherwise, the given
     *   `reference`
     */
    private fun getConversionReference(reference: Expression): Expression {
        if (reference is FunctionRef) {
            val functionRef: FunctionRef = reference
            if (
                (functionRef.operand.size == 1) &&
                    (functionRef.resultType != null) &&
                    (functionRef.operand[0].resultType != null)
            ) {
                val o: Operator? =
                    libraryBuilder.conversionMap.getConversionOperator(
                        functionRef.operand[0].resultType!!,
                        functionRef.resultType!!,
                    )
                if (
                    ((o != null) &&
                        (o.libraryName != null) &&
                        (o.libraryName == functionRef.libraryName) &&
                        (o.name == functionRef.name))
                ) {
                    return functionRef.operand[0]
                }
            }
        }
        return reference
    }

    /**
     * If this is a choice selection, return the argument of the choice selection, on the grounds
     * that the date range optimization should apply through the cast (i.e. it is an
     * order-preserving cast)
     *
     * @param reference the `Expression` to examine
     * @return The argument to the choice selection (i.e. As) if there was one, otherwise, the given
     *   `reference`
     */
    private fun getChoiceSelection(reference: Expression): Expression {
        if (reference is As) {
            if (reference.operand != null && reference.operand!!.resultType is ChoiceType) {
                return reference.operand!!
            }
        }
        return reference
    }

    /**
     * Test an `And` expression and determine if it contains any operands (first-level or nested
     * deeper) than are `IncludedIn` expressions that can be refactored into a `Retrieve`. If so,
     * adjust the `Retrieve` accordingly and reset the corresponding operand to a literal `true`.
     * This `and` branch containing a `true` can be further consolidated later.
     *
     * @param and the `And` expression containing operands to potentially refactor into the
     *   `Retrieve`
     * @param retrieve the `Retrieve` to add qualifying date ranges to (if applicable)
     * @param alias the alias of the `Retrieve` in the query.
     * @return `true` if the date range was set in the `Retrieve` and the `And` operands (or
     *   sub-operands) were modified; `false` otherwise.
     */
    private fun attemptDateRangeOptimization(and: And, retrieve: Retrieve, alias: String): Boolean {
        if (retrieve.dateProperty != null || retrieve.dateRange != null) {
            return false
        }
        for (i in and.operand.indices) {
            val operand = and.operand[i]
            if (
                (operand is IncludedIn || operand is In) &&
                    attemptDateRangeOptimization(operand, retrieve, alias)
            ) {
                // Replace optimized part in And with true -- to be optimized out later
                and.operand[i] = libraryBuilder.createLiteral(true)
                return true
            } else if (operand is And && attemptDateRangeOptimization(operand, retrieve, alias)) {
                return true
            }
        }
        return false
    }

    /**
     * If any branches in the `And` tree contain a `true`, refactor it out.
     *
     * @param and the `And` tree to attempt to consolidate
     * @return the potentially consolidated `And`
     */
    private fun consolidateAnd(and: And): Expression {
        var result: Expression = and
        val lhs = and.operand[0]
        val rhs = and.operand[1]
        when {
            isBooleanLiteral(lhs, true) -> result = rhs
            isBooleanLiteral(rhs, true) -> result = lhs
            lhs is And -> and.operand[0] = consolidateAnd(lhs)
            rhs is And -> and.operand[1] = consolidateAnd(rhs)
        }
        return result
    }

    /**
     * Determine if the right-hand side of an `IncludedIn` expression can be refactored into the
     * date range of a `Retrieve`. Currently, refactoring is only supported when the RHS is a
     * literal DateTime interval, a literal DateTime, a parameter representing a DateTime interval
     * or a DateTime, or an expression reference representing a DateTime interval or a DateTime.
     *
     * @param rhs the right-hand side of the `IncludedIn` to test for potential optimization
     * @return `true` if the RHS supports refactoring to a `Retrieve`, `false` otherwise.
     */
    private fun isRHSEligibleForDateRangeOptimization(rhs: Expression): Boolean {
        return (rhs.resultType!!.isSubTypeOf(
            libraryBuilder.resolveTypeName("System", "DateTime")!!
        ) ||
            rhs.resultType!!.isSubTypeOf(
                IntervalType(libraryBuilder.resolveTypeName("System", "DateTime")!!)
            ))

        // BTR: The only requirement for the optimization is that the expression be of type DateTime
        // or Interval<DateTime>
        // Whether or not the expression can be statically evaluated (literal, in the loose sense of
        // the word) is really a function of the engine in determining the "initial" data
        // requirements, versus subsequent data requirements
        // Element targetElement = rhs;
        // if (rhs instanceof ParameterRef) {
        //     String paramName = ((ParameterRef) rhs).getName();
        //     for (ParameterDef def : getLibrary().getParameters().getDef()) {
        //         if (paramName.equals(def.getName())) {
        //             targetElement = def.getParameterTypeSpecifier();
        //             if (targetElement == null) {
        //                 targetElement = def.getDefault();
        //             }
        //             break;
        //         }
        //     }
        // } else if (rhs instanceof ExpressionRef && !(rhs instanceof FunctionRef)) {
        //     // TODO: Support forward declaration, if necessary
        //     String expName = ((ExpressionRef) rhs).getName();
        //     for (ExpressionDef def : getLibrary().getStatements().getDef()) {
        //         if (expName.equals(def.getName())) {
        //             targetElement = def.getExpression();
        //         }
        //     }
        // }
        //
        // boolean isEligible = false;
        // if (targetElement instanceof DateTime) {
        //     isEligible = true;
        // } else if (targetElement instanceof Interval) {
        //     Interval ivl = (Interval) targetElement;
        //     isEligible = (ivl.getLow() != null && ivl.getLow() instanceof DateTime) ||
        //         (ivl.getHigh() != null && ivl.getHigh() instanceof DateTime);
        // } else if (targetElement instanceof IntervalTypeSpecifier) {
        //     IntervalTypeSpecifier spec = (IntervalTypeSpecifier) targetElement;
        //     isEligible = isDateTimeTypeSpecifier(spec.getPointType());
        // } else if (targetElement instanceof NamedTypeSpecifier) {
        //     isEligible = isDateTimeTypeSpecifier(targetElement);
        // }
        // return isEligible;
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
        libraryBuilder.resolveBinaryCall("System", "Indexer", indexer)
        return indexer
    }

    override fun visitInvocationExpressionTerm(ctx: InvocationExpressionTermContext): Expression? {
        val left = parseExpression(ctx.expressionTerm())!!
        libraryBuilder.pushExpressionTarget(left)
        return try {
            visit(ctx.qualifiedInvocation()) as Expression?
        } finally {
            libraryBuilder.popExpressionTarget()
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
                libraryBuilder.beginFunctionDef(functionDef)
                try {
                    libraryBuilder.pushExpressionContext(this.currentContext)
                    try {
                        libraryBuilder.pushExpressionDefinition(fh.mangledName)
                        try {
                            functionDef.expression = parseExpression(ctx.functionBody())
                        } finally {
                            libraryBuilder.popExpressionDefinition()
                        }
                    } finally {
                        libraryBuilder.popExpressionContext()
                    }
                } finally {
                    libraryBuilder.endFunctionDef()
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
        libraryBuilder.pushIdentifierScope()
        return try {
            registerFunctionDefinition(ctx)
            compileFunctionDefinition(ctx)
        } finally {
            libraryBuilder.popIdentifierScope()
        }
    }

    private fun parseLiteralExpression(pt: ParseTree?): Expression? {
        libraryBuilder.pushLiteralContext()
        return try {
            parseExpression(pt)
        } finally {
            libraryBuilder.popLiteralContext()
        }
    }

    private fun parseExpression(pt: ParseTree?): Expression? {
        return if (pt == null) null else visit(pt) as Expression?
    }

    private fun isBooleanLiteral(expression: Expression, bool: Boolean?): Boolean {
        var ret = false
        if (expression is Literal) {
            ret =
                (expression.valueType ==
                    libraryBuilder.dataTypeToQName(
                        libraryBuilder.resolveTypeName("System", "Boolean")
                    ))
            if (ret && bool != null) {
                ret = bool == expression.value.toBoolean()
            }
        }
        return ret
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
}
