@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.math.BigDecimal
import java.util.*
import java.util.regex.Pattern
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
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
import org.cqframework.cql.elm.tracking.TrackBack
import org.cqframework.cql.elm.tracking.Trackable
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.cqframework.cql.gen.cqlParser.*
import org.hl7.cql.model.*
import org.hl7.elm.r1.*
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.slf4j.LoggerFactory

@Suppress(
    "LongMethod",
    "LargeClass",
    "CyclomaticComplexMethod",
    "NestedBlockDepth",
    "TooManyFunctions",
    "ComplexCondition",
    "TooGenericExceptionCaught",
    "ImplicitDefaultLocale",
    "ReturnCount",
    "ThrowsCount",
    "MaxLineLength",
    "ForbiddenComment",
    "LoopWithTooManyJumpStatements",
    "MagicNumber"
)
class Cql2ElmVisitor(
    libraryBuilder: LibraryBuilder,
    tokenStream: TokenStream,
    libraryInfo: LibraryInfo
) : CqlPreprocessorElmCommonVisitor(libraryBuilder, tokenStream) {
    private val systemMethodResolver = SystemMethodResolver(this, libraryBuilder)
    private val definedExpressionDefinitions: MutableSet<String> = HashSet()
    private val forwards = Stack<ExpressionDefinitionInfo>()
    private val functionHeaders: MutableMap<FunctionDefinitionContext, FunctionHeader?> = HashMap()
    private val functionHeadersByDef: MutableMap<FunctionDef, FunctionHeader?> = HashMap()
    private val functionDefinitions: MutableMap<FunctionHeader?, FunctionDefinitionContext> =
        HashMap()
    private val timingOperators = Stack<TimingOperatorContext>()
    val retrieves: MutableList<Retrieve> = ArrayList()
    val expressions: List<Expression> = ArrayList()
    private val contextDefinitions: MutableMap<String, Element?> = HashMap()

    init {
        this.libraryInfo = libraryInfo
    }

    override fun visitLibrary(ctx: LibraryContext): Any? {
        var lastResult: Any? = null

        // Loop through and call visit on each child (to ensure they are tracked)
        for (i in 0 until ctx.childCount) {
            val tree = ctx.getChild(i)
            val terminalNode = tree as? TerminalNode
            if (terminalNode != null && terminalNode.symbol.type == cqlLexer.EOF) {
                continue
            }
            val childResult = visit(tree)
            // Only set the last result if we received something useful
            if (childResult != null) {
                lastResult = childResult
            }
        }

        // Return last result (consistent with super implementation and helps w/
        // testing)
        return lastResult
    }

    override fun visitLibraryDefinition(ctx: LibraryDefinitionContext): VersionedIdentifier {
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        val vid =
            of.createVersionedIdentifier()
                .withId(identifiers.removeAt(identifiers.size - 1))
                .withVersion(parseString(ctx.versionSpecifier()))
        if (identifiers.isNotEmpty()) {
            vid.system =
                libraryBuilder.resolveNamespaceUri(java.lang.String.join(".", identifiers), true)
        } else if (libraryBuilder.namespaceInfo != null) {
            vid.system = libraryBuilder.namespaceInfo.uri
        }
        libraryBuilder.libraryIdentifier = vid
        return vid
    }

    override fun visitUsingDefinition(ctx: UsingDefinitionContext): UsingDef? {
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        val unqualifiedIdentifier: String = identifiers.removeAt(identifiers.size - 1)
        val namespaceName =
            if (identifiers.isNotEmpty()) java.lang.String.join(".", identifiers)
            else if (libraryBuilder.isWellKnownModelName(unqualifiedIdentifier)) null
            else if (libraryBuilder.namespaceInfo != null) libraryBuilder.namespaceInfo.name
            else null
        var path: String? = null
        var modelNamespace: NamespaceInfo? = null
        if (namespaceName != null) {
            val namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true)
            path = NamespaceManager.getPath(namespaceUri, unqualifiedIdentifier)
            modelNamespace = NamespaceInfo(namespaceName, namespaceUri)
        } else {
            path = unqualifiedIdentifier
        }
        val localIdentifier =
            if (ctx.localIdentifier() == null) unqualifiedIdentifier
            else parseString(ctx.localIdentifier())!!
        require(localIdentifier == unqualifiedIdentifier) {
            String.format(
                "Local identifiers for models must be the same as the name of the model in this release of the translator (Model %s, Called %s)",
                unqualifiedIdentifier,
                localIdentifier
            )
        }

        // The model was already calculated by CqlPreprocessorVisitor
        val usingDef = libraryBuilder.resolveUsingRef(localIdentifier)
        libraryBuilder.pushIdentifier(localIdentifier, usingDef, IdentifierScope.GLOBAL)
        return usingDef
    }

    public override fun getModel(
        modelNamespace: NamespaceInfo?,
        modelName: String?,
        version: String?,
        localIdentifier: String
    ): Model {
        Objects.requireNonNull(modelName, "modelName")
        val modelIdentifier = ModelIdentifier().withId(modelName).withVersion(version)
        if (modelNamespace != null) {
            modelIdentifier.system = modelNamespace.uri
        }
        return libraryBuilder.getModel(modelIdentifier, localIdentifier)
    }

    private fun getLibraryPath(namespaceName: String?, unqualifiedIdentifier: String): String {
        if (namespaceName != null) {
            val namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true)
            return NamespaceManager.getPath(namespaceUri, unqualifiedIdentifier)
        }
        return unqualifiedIdentifier
    }

    override fun visitIncludeDefinition(ctx: IncludeDefinitionContext): Any? {
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        val unqualifiedIdentifier: String = identifiers.removeAt(identifiers.size - 1)
        var namespaceName =
            if (identifiers.isNotEmpty()) java.lang.String.join(".", identifiers)
            else if (libraryBuilder.namespaceInfo != null) libraryBuilder.namespaceInfo.name
            else null
        var path: String = getLibraryPath(namespaceName, unqualifiedIdentifier)
        var library =
            of.createIncludeDef()
                .withLocalIdentifier(
                    if (ctx.localIdentifier() == null) unqualifiedIdentifier
                    else parseString(ctx.localIdentifier())
                )
                .withPath(path)
                .withVersion(parseString(ctx.versionSpecifier()))

        // TODO: This isn't great because it complicates the loading process (and
        // results in the source being loaded
        // twice in the general case)
        // But the full fix is to introduce source resolution/caching to enable this
        // layer to determine whether the
        // library identifier resolved
        // with the namespace
        if (!libraryBuilder.canResolveLibrary(library)) {
            namespaceName =
                if (identifiers.size > 0) java.lang.String.join(".", identifiers)
                else if (libraryBuilder.isWellKnownLibraryName(unqualifiedIdentifier)) null
                else if (libraryBuilder.namespaceInfo != null) libraryBuilder.namespaceInfo.name
                else null
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
        libraryBuilder.pushIdentifier(library.localIdentifier, library, IdentifierScope.GLOBAL)
        return library
    }

    override fun visitParameterDefinition(ctx: ParameterDefinitionContext): ParameterDef? {
        val param =
            of.createParameterDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withDefault(parseLiteralExpression(ctx.expression()))
                .withParameterTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
        var paramType: DataType? = null
        if (param.parameterTypeSpecifier != null) {
            paramType = param.parameterTypeSpecifier.resultType
        }
        if (param.default != null) {
            if (paramType != null) {
                libraryBuilder.verifyType(param.default.resultType, paramType)
            } else {
                paramType = param.default.resultType
            }
        }
        requireNotNull(paramType) {
            String.format("Could not determine parameter type for parameter %s.", param.name)
        }
        param.resultType = paramType
        if (param.default != null) {
            param.default = libraryBuilder.ensureCompatible(param.default, paramType)
        }
        libraryBuilder.addParameter(param)
        libraryBuilder.pushIdentifier(param.name, param, IdentifierScope.GLOBAL)
        return param
    }

    override fun visitTupleElementDefinition(
        ctx: TupleElementDefinitionContext
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

    override fun visitAccessModifier(ctx: AccessModifierContext): AccessModifier {
        return when (ctx.text.lowercase(Locale.getDefault())) {
            "public" -> AccessModifier.PUBLIC
            "private" -> AccessModifier.PRIVATE
            else ->
                throw IllegalArgumentException(
                    String.format(
                        "Unknown access modifier %s.",
                        ctx.text.lowercase(Locale.getDefault())
                    )
                )
        }
    }

    override fun visitCodesystemDefinition(ctx: CodesystemDefinitionContext): CodeSystemDef {
        val cs =
            of.createCodeSystemDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.codesystemId()))
                .withVersion(parseString(ctx.versionSpecifier())) as CodeSystemDef
        if (libraryBuilder.isCompatibleWith("1.5")) {
            cs.resultType = libraryBuilder.resolveTypeName("System", "CodeSystem")
        } else {
            cs.resultType = ListType(libraryBuilder.resolveTypeName("System", "Code"))
        }
        libraryBuilder.addCodeSystem(cs)
        libraryBuilder.pushIdentifier(cs.name, cs, IdentifierScope.GLOBAL)
        return cs
    }

    override fun visitCodesystemIdentifier(ctx: CodesystemIdentifierContext): CodeSystemRef {
        val libraryName = parseString(ctx.libraryIdentifier())
        val name = parseString(ctx.identifier())
        val def: CodeSystemDef?
        if (libraryName != null) {
            def = libraryBuilder.resolveLibrary(libraryName).resolveCodeSystemRef(name)
            libraryBuilder.checkAccessLevel(libraryName, name, def.accessLevel)
        } else {
            def = libraryBuilder.resolveCodeSystemRef(name)
        }
        requireNotNull(def) {
            // ERROR:
            String.format("Could not resolve reference to code system %s.", name)
        }
        return of.createCodeSystemRef()
            .withLibraryName(libraryName)
            .withName(name)
            .withResultType(def.resultType) as CodeSystemRef
    }

    override fun visitCodeIdentifier(ctx: CodeIdentifierContext): CodeRef {
        val libraryName = parseString(ctx.libraryIdentifier())
        val name = parseString(ctx.identifier())
        val def: CodeDef?
        if (libraryName != null) {
            def = libraryBuilder.resolveLibrary(libraryName).resolveCodeRef(name)
            libraryBuilder.checkAccessLevel(libraryName, name, def.accessLevel)
        } else {
            def = libraryBuilder.resolveCodeRef(name)
        }
        if (def == null) {
            // ERROR:
            throw IllegalArgumentException(
                String.format("Could not resolve reference to code %s.", name)
            )
        }
        return of.createCodeRef()
            .withLibraryName(libraryName)
            .withName(name)
            .withResultType(def.resultType) as CodeRef
    }

    override fun visitValuesetDefinition(ctx: ValuesetDefinitionContext): ValueSetDef? {
        val vs =
            of.createValueSetDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.valuesetId()))
                .withVersion(parseString(ctx.versionSpecifier()))
        if (ctx.codesystems() != null) {
            for (codesystem in ctx.codesystems().codesystemIdentifier()) {
                val cs =
                    visit(codesystem) as CodeSystemRef?
                        ?: throw IllegalArgumentException(
                            String.format(
                                "Could not resolve reference to code system %s.",
                                codesystem.text
                            )
                        )
                vs.codeSystem.add(cs)
            }
        }
        if (libraryBuilder.isCompatibleWith("1.5")) {
            vs.resultType = libraryBuilder.resolveTypeName("System", "ValueSet")
        } else {
            vs.resultType = ListType(libraryBuilder.resolveTypeName("System", "Code"))
        }
        libraryBuilder.addValueSet(vs)
        libraryBuilder.pushIdentifier(vs.name, vs, IdentifierScope.GLOBAL)
        return vs
    }

    override fun visitCodeDefinition(ctx: CodeDefinitionContext): CodeDef? {
        val cd =
            of.createCodeDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.codeId()))
        if (ctx.codesystemIdentifier() != null) {
            cd.codeSystem = visit(ctx.codesystemIdentifier()) as CodeSystemRef?
        }
        if (ctx.displayClause() != null) {
            cd.display = parseString(ctx.displayClause().STRING())
        }
        cd.resultType = libraryBuilder.resolveTypeName("Code")
        libraryBuilder.addCode(cd)
        libraryBuilder.pushIdentifier(cd.name, cd, IdentifierScope.GLOBAL)
        return cd
    }

    override fun visitConceptDefinition(ctx: ConceptDefinitionContext): ConceptDef? {
        val cd =
            of.createConceptDef()
                .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                .withName(parseString(ctx.identifier()))
        if (ctx.codeIdentifier() != null) {
            for (ci in ctx.codeIdentifier()) {
                cd.code.add(visit(ci) as CodeRef?)
            }
        }
        if (ctx.displayClause() != null) {
            cd.display = parseString(ctx.displayClause().STRING())
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
            libraryBuilder.getNamedTypeSpecifierResult(
                String.format("%s:%s", modelIdentifier, identifier)
            )
        if (retrievedResult != null) {
            return if (retrievedResult.hasError()) {
                null
            } else retrievedResult.underlyingResultIfExists
        }
        val resultType =
            libraryBuilder.resolveTypeName(modelIdentifier, identifier)
                ?: throw CqlCompilerException(
                    String.format(
                        "Could not find type for model: %s and name: %s",
                        modelIdentifier,
                        identifier
                    ),
                    getTrackBack(ctx)
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
        val unqualifiedIdentifier: String? = parseString(ctx.identifier())
        currentContext =
            if (modelIdentifier != null) "$modelIdentifier.$unqualifiedIdentifier"
            else (unqualifiedIdentifier)!!
        if (!isUnfilteredContext(unqualifiedIdentifier)) {
            val modelContext: ModelContext? =
                libraryBuilder.resolveContextName(modelIdentifier, unqualifiedIdentifier)

            // If this is the first time a context definition is encountered, construct a
            // context definition:
            // define <Context> = element of [<Context model type>]
            var modelContextDefinition: Element? = contextDefinitions[modelContext!!.name]
            if (modelContextDefinition == null) {
                if (libraryBuilder.hasUsings()) {
                    val modelInfo: ModelInfo =
                        if (modelIdentifier == null)
                            libraryBuilder.getModel(libraryInfo.defaultModelName).modelInfo
                        else libraryBuilder.getModel(modelIdentifier).modelInfo
                    // String contextTypeName = modelContext.getName();
                    // DataType contextType = libraryBuilder.resolveTypeName(modelInfo.getName(),
                    // contextTypeName);
                    val contextType: DataType = modelContext.type
                    modelContextDefinition = libraryBuilder.resolveParameterRef(modelContext.name)
                    if (modelContextDefinition != null) {
                        contextDefinitions[modelContext.name] = modelContextDefinition
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
                                .withContext(currentContext)
                                .withExpression(
                                    of.createSingletonFrom().withOperand(contextRetrieve)
                                )
                        track(modelContextDefinition, ctx)
                        modelContextDefinition.expression.resultType = contextType
                        modelContextDefinition.setResultType(contextType)
                        libraryBuilder.addExpression(modelContextDefinition)
                        contextDefinitions[modelContext.name] = modelContextDefinition
                    }
                } else {
                    modelContextDefinition =
                        of.createExpressionDef()
                            .withName(unqualifiedIdentifier)
                            .withContext(currentContext)
                            .withExpression(of.createNull())
                    track(modelContextDefinition, ctx)
                    modelContextDefinition.expression.resultType =
                        libraryBuilder.resolveTypeName("System", "Any")
                    modelContextDefinition.setResultType(
                        modelContextDefinition.expression.resultType
                    )
                    libraryBuilder.addExpression(modelContextDefinition)
                    contextDefinitions[modelContext.name] = modelContextDefinition
                }
            }
        }
        val contextDef: ContextDef = of.createContextDef().withName(currentContext)
        track(contextDef, ctx)
        if (libraryBuilder.isCompatibleWith("1.5")) {
            libraryBuilder.addContext(contextDef)
        }
        return currentContext
    }

    private fun isImplicitContextExpressionDef(def: ExpressionDef): Boolean {
        for (e in contextDefinitions.values) {
            if (def === e) {
                return true
            }
        }
        return false
    }

    private fun removeImplicitContextExpressionDef(def: ExpressionDef) {
        for ((key, value) in contextDefinitions) {
            if (def === value) {
                contextDefinitions.remove(key)
                break
            }
        }
    }

    fun internalVisitExpressionDefinition(ctx: ExpressionDefinitionContext): ExpressionDef? {
        val identifier = parseString(ctx.identifier())!!
        var def = libraryBuilder.resolveExpressionRef(identifier)

        // First time visiting this expression definition, create a lightweight ExpressionDef to be
        // used to output a
        // hiding warning message
        // If it's the second time around, we'll be able to resolve it and we can assume it's
        // already on the
        // hiding stack.
        if (def == null) {
            val hollowExpressionDef =
                of.createExpressionDef().withName(identifier).withContext(currentContext)
            libraryBuilder.pushIdentifier(identifier, hollowExpressionDef, IdentifierScope.GLOBAL)
        }
        if (def == null || isImplicitContextExpressionDef(def)) {
            if (def != null && isImplicitContextExpressionDef(def)) {
                libraryBuilder.removeExpression(def)
                removeImplicitContextExpressionDef(def)
                def = null
            }
            libraryBuilder.pushExpressionContext(currentContext)
            try {
                libraryBuilder.pushExpressionDefinition(identifier)
                try {
                    def =
                        of.createExpressionDef()
                            .withAccessLevel(parseAccessModifier(ctx.accessModifier()))
                            .withName(identifier)
                            .withContext(currentContext)
                            .withExpression(visit(ctx.expression()) as Expression?)
                    if (def.expression != null) {
                        def.resultType = def.expression.resultType
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
            if (forwards.isEmpty() || forwards.peek().name != expressionDef!!.name) {
                if (definedExpressionDefinitions.contains(expressionDef!!.name)) {
                    // ERROR:
                    throw IllegalArgumentException(
                        String.format(
                            "Identifier %s is already in use in this library.",
                            expressionDef.name
                        )
                    )
                }

                // Track defined expression definitions locally, otherwise duplicate expression
                // definitions will be missed
                // because they are
                // overwritten by name when they are encountered by the preprocessor.
                definedExpressionDefinitions.add(expressionDef.name)
            }
            expressionDef
        } finally {
            libraryBuilder.popIdentifierScope()
        }
    }

    override fun visitStringLiteral(ctx: StringLiteralContext): Literal {
        val stringLiteral = libraryBuilder.createLiteral(parseString(ctx.STRING()))
        // Literals are never actually pushed to the stack. This just emits a warning if
        // the literal is hiding something
        libraryBuilder.pushIdentifier(stringLiteral.value, stringLiteral)
        return stringLiteral
    }

    override fun visitSimpleStringLiteral(ctx: SimpleStringLiteralContext): Literal {
        return libraryBuilder.createLiteral(parseString(ctx.STRING()))
    }

    override fun visitBooleanLiteral(ctx: BooleanLiteralContext): Literal {
        return libraryBuilder.createLiteral(java.lang.Boolean.valueOf(ctx.text))
    }

    override fun visitIntervalSelector(ctx: IntervalSelectorContext): Any {
        return libraryBuilder.createInterval(
            parseExpression(ctx.expression(0)),
            ctx.getChild(1).text == "[",
            parseExpression(ctx.expression(1)),
            ctx.getChild(5).text == "]"
        )
    }

    override fun visitTupleElementSelector(ctx: TupleElementSelectorContext): Any? {
        val result =
            of.createTupleElement()
                .withName(parseString(ctx.referentialIdentifier()))
                .withValue(parseExpression(ctx.expression()))
        result.resultType = result.value.resultType
        return result
    }

    override fun visitTupleSelector(ctx: TupleSelectorContext): Any? {
        val tuple = of.createTuple()
        val tupleType = TupleType()
        for (elementContext in ctx.tupleElementSelector()) {
            val element = visit(elementContext) as TupleElement
            tupleType.addElement(TupleTypeElement(element.name, element.resultType))
            tuple.element.add(element)
        }
        tuple.resultType = tupleType
        return tuple
    }

    override fun visitInstanceElementSelector(ctx: InstanceElementSelectorContext): Any? {
        val result =
            of.createInstanceElement()
                .withName(parseString(ctx.referentialIdentifier()))
                .withValue(parseExpression(ctx.expression()))
        result.resultType = result.value.resultType
        return result
    }

    override fun visitInstanceSelector(ctx: InstanceSelectorContext): Any {
        val instance: Instance = of.createInstance()
        val classTypeSpecifier = visitNamedTypeSpecifier(ctx.namedTypeSpecifier())!!
        instance.classType = classTypeSpecifier.name
        instance.resultType = classTypeSpecifier.resultType
        for (elementContext in ctx.instanceElementSelector()) {
            val element = visit(elementContext) as InstanceElement
            val resolution: PropertyResolution? =
                libraryBuilder.resolveProperty(classTypeSpecifier.resultType, element.name)
            element.value = libraryBuilder.ensureCompatible(element.value, resolution!!.type)
            element.name = resolution.name
            require(resolution.targetMap == null) {
                "Target Mapping in instance selectors not yet supported"
            }
            instance.element.add(element)
        }
        return instance
    }

    override fun visitCodeSelector(ctx: CodeSelectorContext): Any? {
        val code = of.createCode()
        code.code = parseString(ctx.STRING())
        code.system = visit(ctx.codesystemIdentifier()) as CodeSystemRef?
        if (ctx.displayClause() != null) {
            code.display = parseString(ctx.displayClause().STRING())
        }
        code.resultType = libraryBuilder.resolveTypeName("System", "Code")
        return code
    }

    override fun visitConceptSelector(ctx: ConceptSelectorContext): Any? {
        val concept = of.createConcept()
        if (ctx.displayClause() != null) {
            concept.display = parseString(ctx.displayClause().STRING())
        }
        for (codeContext in ctx.codeSelector()) {
            concept.code.add(visit(codeContext) as Code?)
        }
        concept.resultType = libraryBuilder.resolveTypeName("System", "Concept")
        return concept
    }

    override fun visitListSelector(ctx: ListSelectorContext): Any? {
        val elementTypeSpecifier = parseTypeSpecifier(ctx.typeSpecifier())
        val list = of.createList()
        var listType: ListType? = null
        if (elementTypeSpecifier != null) {
            val listTypeSpecifier =
                of.createListTypeSpecifier().withElementType(elementTypeSpecifier)
            track(listTypeSpecifier, ctx.typeSpecifier())
            listType = ListType(elementTypeSpecifier.resultType)
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
                libraryBuilder.verifyType(element.resultType, elementType)
            } else {
                if (initialInferredElementType == null) {
                    initialInferredElementType = element.resultType
                    inferredElementType = initialInferredElementType
                } else {
                    // Once a list type is inferred as Any, keep it that way
                    // The only potential exception to this is if the element responsible for the
                    // inferred type of Any
                    // is a null
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
            if (!elementType!!.isSuperTypeOf(element.resultType)) {
                val conversion =
                    libraryBuilder.findConversion(element.resultType, elementType, true, false)
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
            listType = ListType(elementType)
        }
        list.resultType = listType
        return list
    }

    override fun visitTimeLiteral(ctx: TimeLiteralContext): Any? {
        var input = ctx.text
        if (input.startsWith("@")) {
            input = input.substring(1)
        }
        val timePattern = Pattern.compile("T(\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?")
        // -1-------2---3-------4---5-------6---7-----------
        val matcher = timePattern.matcher(input)
        return if (matcher.matches()) {
            try {
                val result = of.createTime()
                val hour = matcher.group(1).toInt()
                var minute = -1
                var second = -1
                var millisecond = -1
                if (hour < 0 || hour > 24) {
                    throw IllegalArgumentException(
                        String.format("Invalid hour in time literal (%s).", input)
                    )
                }
                result.hour = libraryBuilder.createLiteral(hour)
                if (matcher.group(3) != null) {
                    minute = matcher.group(3).toInt()
                    if ((minute < 0) || (minute >= 60) || (hour == 24 && minute > 0)) {
                        throw IllegalArgumentException(
                            String.format("Invalid minute in time literal (%s).", input)
                        )
                    }
                    result.minute = libraryBuilder.createLiteral(minute)
                }
                if (matcher.group(5) != null) {
                    second = matcher.group(5).toInt()
                    if ((second < 0) || (second >= 60) || (hour == 24 && second > 0)) {
                        throw IllegalArgumentException(
                            String.format("Invalid second in time literal (%s).", input)
                        )
                    }
                    result.second = libraryBuilder.createLiteral(second)
                }
                if (matcher.group(7) != null) {
                    millisecond = matcher.group(7).toInt()
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw IllegalArgumentException(
                            String.format("Invalid millisecond in time literal (%s).", input)
                        )
                    }
                    result.millisecond = libraryBuilder.createLiteral(millisecond)
                }
                result.resultType = libraryBuilder.resolveTypeName("System", "Time")
                result
            } catch (e: RuntimeException) {
                throw IllegalArgumentException(
                    String.format(
                        "Invalid time input (%s). Use ISO 8601 time representation (hh:mm:ss.fff).",
                        input
                    ),
                    e
                )
            }
        } else {
            throw IllegalArgumentException(
                String.format(
                    "Invalid time input (%s). Use ISO 8601 time representation (hh:mm:ss.fff).",
                    input
                )
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
            Pattern.compile(
                "(\\d{4})(((-(\\d{2}))(((-(\\d{2}))((T)((\\d{2})(\\:(\\d{2})(\\:(\\d{2})(\\.(\\d+))?)?)?)?)?)|(T))?)|(T))?((Z)|(([+-])(\\d{2})(\\:(\\d{2}))))?"
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
        val matcher = dateTimePattern.matcher(input)
        return if (matcher.matches()) {
            try {
                val calendar = GregorianCalendar.getInstance() as GregorianCalendar
                val result = of.createDateTime()
                val year = matcher.group(1).toInt()
                var month = -1
                var day = -1
                var hour = -1
                var minute = -1
                var second = -1
                var millisecond = -1
                result.year = libraryBuilder.createLiteral(year)
                if (matcher.group(5) != null) {
                    month = matcher.group(5).toInt()
                    if (month < 0 || month > 12) {
                        throw IllegalArgumentException(
                            String.format("Invalid month in date/time literal (%s).", input)
                        )
                    }
                    result.month = libraryBuilder.createLiteral(month)
                }
                if (matcher.group(9) != null) {
                    day = matcher.group(9).toInt()
                    var maxDay = 31
                    when (month) {
                        2 -> maxDay = if (calendar.isLeapYear(year)) 29 else 28
                        4,
                        6,
                        9,
                        11 -> maxDay = 30
                        else -> {}
                    }
                    if (day < 0 || day > maxDay) {
                        throw IllegalArgumentException(
                            String.format("Invalid day in date/time literal (%s).", input)
                        )
                    }
                    result.day = libraryBuilder.createLiteral(day)
                }
                if (matcher.group(13) != null) {
                    hour = matcher.group(13).toInt()
                    if (hour < 0 || hour > 24) {
                        throw IllegalArgumentException(
                            String.format("Invalid hour in date/time literal (%s).", input)
                        )
                    }
                    result.hour = libraryBuilder.createLiteral(hour)
                }
                if (matcher.group(15) != null) {
                    minute = matcher.group(15).toInt()
                    if ((minute < 0) || (minute >= 60) || (hour == 24 && minute > 0)) {
                        throw IllegalArgumentException(
                            String.format("Invalid minute in date/time literal (%s).", input)
                        )
                    }
                    result.minute = libraryBuilder.createLiteral(minute)
                }
                if (matcher.group(17) != null) {
                    second = matcher.group(17).toInt()
                    if ((second < 0) || (second >= 60) || (hour == 24 && second > 0)) {
                        throw IllegalArgumentException(
                            String.format("Invalid second in date/time literal (%s).", input)
                        )
                    }
                    result.second = libraryBuilder.createLiteral(second)
                }
                if (matcher.group(19) != null) {
                    millisecond = matcher.group(19).toInt()
                    if (millisecond < 0 || (hour == 24 && millisecond > 0)) {
                        throw IllegalArgumentException(
                            String.format("Invalid millisecond in date/time literal (%s).", input)
                        )
                    }
                    result.millisecond = libraryBuilder.createLiteral(millisecond)
                }
                if (matcher.group(23) != null && (matcher.group(23) == "Z")) {
                    result.timezoneOffset = libraryBuilder.createLiteral(0.0)
                }
                if (matcher.group(25) != null) {
                    val offsetPolarity = if ((matcher.group(25) == "+")) 1 else -1
                    if (matcher.group(28) != null) {
                        val hourOffset = matcher.group(26).toInt()
                        if (hourOffset < 0 || hourOffset > 14) {
                            throw IllegalArgumentException(
                                String.format(
                                    "Timezone hour offset is out of range in date/time literal (%s).",
                                    input
                                )
                            )
                        }
                        val minuteOffset = matcher.group(28).toInt()
                        if (
                            (minuteOffset < 0) ||
                                (minuteOffset >= 60) ||
                                (hourOffset == 14 && minuteOffset > 0)
                        ) {
                            throw IllegalArgumentException(
                                String.format(
                                    "Timezone minute offset is out of range in date/time literal (%s).",
                                    input
                                )
                            )
                        }
                        result.timezoneOffset =
                            libraryBuilder.createLiteral(
                                (hourOffset + (minuteOffset.toDouble() / 60)) * offsetPolarity
                            )
                    } else {
                        if (matcher.group(26) != null) {
                            val hourOffset = matcher.group(26).toInt()
                            if (hourOffset < 0 || hourOffset > 14) {
                                throw IllegalArgumentException(
                                    String.format(
                                        "Timezone hour offset is out of range in date/time literal (%s).",
                                        input
                                    )
                                )
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
                        (matcher.group(11) == null) &&
                        (matcher.group(20) == null) &&
                        (matcher.group(21) == null)
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
                    String.format(
                        "Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|(+/-hh:mm)).",
                        input
                    ),
                    e
                )
            }
        } else {
            throw IllegalArgumentException(
                String.format(
                    "Invalid date-time input (%s). Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|+/-hh:mm)).",
                    input
                )
            )
        }
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

    override fun visitNullLiteral(ctx: NullLiteralContext): Null? {
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
            throw IllegalArgumentException(
                String.format("Could not parse number literal: %s", value)
            )
        }
    }

    override fun visitQuantity(ctx: QuantityContext): Expression {
        return if (ctx.unit() != null) {
            libraryBuilder.createQuantity(
                parseDecimal(ctx.NUMBER().text),
                (parseString(ctx.unit()))!!
            )
        } else {
            libraryBuilder.createNumberLiteral(ctx.NUMBER().text)
        }
    }

    private fun getQuantity(source: Expression?): Quantity {
        if (source is Literal) {
            return libraryBuilder.createQuantity(parseDecimal(source.value), "1")
        } else if (source is Quantity) {
            return source
        }
        throw IllegalArgumentException("Could not create quantity from source expression.")
    }

    override fun visitRatio(ctx: RatioContext): Expression {
        val numerator = getQuantity(visit(ctx.quantity(0)) as Expression?)
        val denominator = getQuantity(visit(ctx.quantity(1)) as Expression?)
        return libraryBuilder.createRatio(numerator, denominator)
    }

    override fun visitNotExpression(ctx: NotExpressionContext): Not? {
        val result = of.createNot().withOperand(parseExpression(ctx.expression()))
        libraryBuilder.resolveUnaryCall("System", "Not", result)
        return result
    }

    override fun visitExistenceExpression(ctx: ExistenceExpressionContext): Exists? {
        val result = of.createExists().withOperand(parseExpression(ctx.expression()))
        libraryBuilder.resolveUnaryCall("System", "Exists", result)
        return result
    }

    override fun visitMultiplicationExpressionTerm(
        ctx: MultiplicationExpressionTermContext
    ): BinaryExpression {
        val exp: BinaryExpression?
        val operatorName: String?
        when (ctx.getChild(1).text) {
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
                throw IllegalArgumentException(
                    String.format("Unsupported operator: %s.", ctx.getChild(1).text)
                )
        }
        exp!!.withOperand(
            parseExpression(ctx.expressionTerm(0)),
            parseExpression(ctx.expressionTerm(1))
        )
        libraryBuilder.resolveBinaryCall("System", operatorName, (exp))
        return exp
    }

    override fun visitPowerExpressionTerm(ctx: PowerExpressionTermContext): Power? {
        val power =
            of.createPower()
                .withOperand(
                    parseExpression(ctx.expressionTerm(0)),
                    parseExpression(ctx.expressionTerm(1))
                )
        libraryBuilder.resolveBinaryCall("System", "Power", power)
        return power
    }

    override fun visitPolarityExpressionTerm(ctx: PolarityExpressionTermContext): Any? {
        if (ctx.getChild(0).text == "+") {
            return visit(ctx.expressionTerm())
        }
        val result = of.createNegate().withOperand(parseExpression(ctx.expressionTerm()))
        libraryBuilder.resolveUnaryCall("System", "Negate", result)
        return result
    }

    override fun visitAdditionExpressionTerm(ctx: AdditionExpressionTermContext): Expression? {
        var exp: Expression?
        val operatorName: String?
        when (ctx.getChild(1).text) {
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
                throw IllegalArgumentException(
                    String.format("Unsupported operator: %s.", ctx.getChild(1).text)
                )
        }
        if (exp is BinaryExpression) {
            exp.withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1))
            )
            libraryBuilder.resolveBinaryCall("System", operatorName, (exp as BinaryExpression?)!!)
            if (exp.getResultType() === libraryBuilder.resolveTypeName("System", "String")) {
                val concatenate: Concatenate = of.createConcatenate()
                concatenate.operand.addAll(exp.operand)
                concatenate.resultType = exp.getResultType()
                exp = concatenate
            }
        } else {
            val concatenate: Concatenate? = exp as Concatenate?
            concatenate!!.withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1))
            )
            for (i in concatenate.operand.indices) {
                val operand: Expression = concatenate.operand[i]
                val empty: Literal = libraryBuilder.createLiteral("")
                val params: ArrayList<Expression?> = ArrayList()
                params.add(operand)
                params.add(empty)
                val coalesce: Expression? =
                    libraryBuilder.resolveFunction("System", "Coalesce", params)
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
    ): Any? {
        val result = of.createSingletonFrom().withOperand(parseExpression(ctx.expressionTerm()))
        libraryBuilder.resolveUnaryCall("System", "SingletonFrom", result)
        return result
    }

    override fun visitPointExtractorExpressionTerm(ctx: PointExtractorExpressionTermContext): Any? {
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
            else -> throw IllegalArgumentException(String.format("Unknown extent: %s", extent))
        }
    }

    override fun visitTimeBoundaryExpressionTerm(ctx: TimeBoundaryExpressionTermContext): Any {
        val result: UnaryExpression?
        val operatorName: String?
        if (ctx.getChild(0).text == "start") {
            result = of.createStart().withOperand(parseExpression(ctx.expressionTerm()))
            operatorName = "Start"
        } else {
            result = of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
            operatorName = "End"
        }
        libraryBuilder.resolveUnaryCall("System", operatorName, result!!)
        return result
    }

    private fun parseComparableDateTimePrecision(dateTimePrecision: String): DateTimePrecision? {
        return parseDateTimePrecision(dateTimePrecision, true, false)
    }

    private fun parseComparableDateTimePrecision(
        dateTimePrecision: String?,
        precisionRequired: Boolean
    ): DateTimePrecision? {
        return parseDateTimePrecision(dateTimePrecision, precisionRequired, false)
    }

    private fun parseDateTimePrecision(
        dateTimePrecision: String?,
        precisionRequired: Boolean = true,
        allowWeeks: Boolean = true
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
            else ->
                throw IllegalArgumentException(
                    String.format("Unknown precision '%s'.", dateTimePrecision)
                )
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
            else ->
                throw IllegalArgumentException(String.format("Unknown precision '%s'.", component))
        }
        libraryBuilder.resolveUnaryCall("System", operatorName, result!!)
        return result
    }

    override fun visitDurationExpressionTerm(ctx: DurationExpressionTermContext): Any? {
        // duration in days of X <=> days between start of X and end of X
        val operand = parseExpression(ctx.expressionTerm())
        val start = of.createStart().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "Start", start)
        val end = of.createEnd().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "End", end)
        val result =
            of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(start, end)
        libraryBuilder.resolveBinaryCall("System", "DurationBetween", result)
        return result
    }

    override fun visitDifferenceExpressionTerm(ctx: DifferenceExpressionTermContext): Any? {
        // difference in days of X <=> difference in days between start of X and end of
        // X
        val operand = parseExpression(ctx.expressionTerm())
        val start = of.createStart().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "Start", start)
        val end = of.createEnd().withOperand(operand)
        libraryBuilder.resolveUnaryCall("System", "End", end)
        val result =
            of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(start, end)
        libraryBuilder.resolveBinaryCall("System", "DifferenceBetween", result)
        return result
    }

    override fun visitBetweenExpression(ctx: BetweenExpressionContext): Any? {
        // X properly? between Y and Z
        val first = parseExpression(ctx.expression())
        val second = parseExpression(ctx.expressionTerm(0))
        val third = parseExpression(ctx.expressionTerm(1))
        val isProper = ctx.getChild(0).text == "properly"
        return if (first!!.resultType is IntervalType) {
            val result =
                if (isProper) of.createProperIncludedIn()
                else
                    of.createIncludedIn()
                        .withOperand(
                            first,
                            libraryBuilder.createInterval(second, true, third, true)
                        )
            libraryBuilder.resolveBinaryCall(
                "System",
                if (isProper) "ProperIncludedIn" else "IncludedIn",
                result
            )
            result
        } else {
            val result: BinaryExpression =
                of.createAnd()
                    .withOperand(
                        (if (isProper) of.createGreater() else of.createGreaterOrEqual())
                            .withOperand(first, second),
                        (if (isProper) of.createLess() else of.createLessOrEqual()).withOperand(
                            first,
                            third
                        )
                    )
            libraryBuilder.resolveBinaryCall(
                "System",
                if (isProper) "Greater" else "GreaterOrEqual",
                (result.operand[0] as BinaryExpression)
            )
            libraryBuilder.resolveBinaryCall(
                "System",
                if (isProper) "Less" else "LessOrEqual",
                (result.operand[1] as BinaryExpression)
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
                    parseExpression(ctx.expressionTerm(0)),
                    parseExpression(ctx.expressionTerm(1))
                )
        libraryBuilder.resolveBinaryCall("System", "DurationBetween", result)
        return result
    }

    override fun visitDifferenceBetweenExpression(ctx: DifferenceBetweenExpressionContext): Any {
        val result: BinaryExpression =
            of.createDifferenceBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().text))
                .withOperand(
                    parseExpression(ctx.expressionTerm(0)),
                    parseExpression(ctx.expressionTerm(1))
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
        val operator: String = ctx.getChild(1).text
        when (operator) {
            "in" ->
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    val inExpression: In =
                        of.createIn()
                            .withPrecision(
                                parseComparableDateTimePrecision(
                                    ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
                                )
                            )
                            .withOperand(
                                parseExpression(ctx.expression(0)),
                                parseExpression(ctx.expression(1))
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
                                    ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
                                )
                            )
                            .withOperand(
                                parseExpression(ctx.expression(0)),
                                parseExpression(ctx.expression(1))
                            )
                    libraryBuilder.resolveBinaryCall("System", "Contains", contains)
                    return contains
                } else {
                    val left: Expression? = parseExpression(ctx.expression(0))
                    val right: Expression? = parseExpression(ctx.expression(1))
                    if (left is ValueSetRef) {
                        val inValueSet: InValueSet =
                            of.createInValueSet()
                                .withCode(right)
                                .withValueset(left as ValueSetRef?)
                                .withValuesetExpression(left)
                        libraryBuilder.resolveCall(
                            "System",
                            "InValueSet",
                            InValueSetInvocation(inValueSet)
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
                            InCodeSystemInvocation(inCodeSystem)
                        )
                        return inCodeSystem
                    }
                    val contains: Contains = of.createContains().withOperand(left, right)
                    libraryBuilder.resolveBinaryCall("System", "Contains", contains)
                    return contains
                }
        }
        throw IllegalArgumentException(String.format("Unknown operator: %s", operator))
    }

    override fun visitAndExpression(ctx: AndExpressionContext): And? {
        val and =
            of.createAnd()
                .withOperand(parseExpression(ctx.expression(0)), parseExpression(ctx.expression(1)))
        libraryBuilder.resolveBinaryCall("System", "And", and)
        return and
    }

    override fun visitOrExpression(ctx: OrExpressionContext): Expression? {
        return if (ctx.getChild(1).text == "xor") {
            val xor =
                of.createXor()
                    .withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                    )
            libraryBuilder.resolveBinaryCall("System", "Xor", xor)
            xor
        } else {
            val or =
                of.createOr()
                    .withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                    )
            libraryBuilder.resolveBinaryCall("System", "Or", or)
            or
        }
    }

    override fun visitImpliesExpression(ctx: ImpliesExpressionContext): Expression? {
        val implies =
            of.createImplies()
                .withOperand(parseExpression(ctx.expression(0)), parseExpression(ctx.expression(1)))
        libraryBuilder.resolveBinaryCall("System", "Implies", implies)
        return implies
    }

    override fun visitInFixSetExpression(ctx: InFixSetExpressionContext): Any? {
        val operator = ctx.getChild(1).text
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

    override fun visitEqualityExpression(ctx: EqualityExpressionContext): Expression? {
        val operator = parseString(ctx.getChild(1))
        return if (operator == "~" || operator == "!~") {
            val equivalent: BinaryExpression =
                of.createEquivalent()
                    .withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
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
            val equal: BinaryExpression =
                of.createEqual()
                    .withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
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
            else ->
                throw IllegalArgumentException(
                    String.format("Unknown operator: %s", ctx.getChild(1).text)
                )
        }
        exp.withOperand(parseExpression(ctx.expression(0)), parseExpression(ctx.expression(1)))
        libraryBuilder.resolveBinaryCall("System", operatorName, exp)
        return exp
    }

    override fun visitQualifiedIdentifier(ctx: QualifiedIdentifierContext): List<String?> {
        // Return the list of qualified identifiers for resolution by the containing
        // element
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
    ): List<String?> {
        // Return the list of qualified identifiers for resolution by the containing
        // element
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
        if (result is LibraryRef) {
            // ERROR:
            throw IllegalArgumentException(
                String.format(
                    "Identifier %s is a library and cannot be used as an expression.",
                    result.libraryName
                )
            )
        }
        return result
    }

    override fun visitTerminal(node: TerminalNode): Any? {
        var text = node.text
        val tokenType = node.symbol.type
        if (cqlLexer.EOF == tokenType) {
            return null
        }
        if (
            cqlLexer.STRING == tokenType ||
                cqlLexer.QUOTEDIDENTIFIER == tokenType ||
                cqlLexer.DELIMITEDIDENTIFIER == tokenType
        ) {
            // chop off leading and trailing ', ", or `
            text = text.substring(1, text.length - 1)

            // This is an alternate style of escaping that was removed when we switched to
            // industry-standard escape
            // sequences
            // if (cqlLexer.STRING == tokenType) {
            // text = text.replace("''", "'");
            // }
            // else {
            // text = text.replace("\"\"", "\"");
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
                        operand.resultType,
                        targetType.resultType,
                        false,
                        true
                    )
                        ?: // ERROR:
                        throw IllegalArgumentException(
                            String.format(
                                "Could not resolve conversion from type %s to type %s.",
                                operand.resultType,
                                targetType.resultType
                            )
                        )
                return libraryBuilder.convertExpression((operand), conversion)
            }
            return operand
        } else {
            var targetUnit: String? = parseString(ctx.unit())
            targetUnit = libraryBuilder.ensureUcumUnit((targetUnit)!!)
            val operand: Expression? = parseExpression(ctx.expression())
            val unitOperand: Expression = libraryBuilder.createLiteral(targetUnit)
            track(unitOperand, ctx.unit())
            val convertQuantity: ConvertQuantity =
                of.createConvertQuantity().withOperand(operand, unitOperand)
            track(convertQuantity, ctx)
            return libraryBuilder.resolveBinaryCall("System", "ConvertQuantity", convertQuantity)
        }
    }

    override fun visitTypeExpression(ctx: TypeExpressionContext): Any? {
        // NOTE: These don't use the buildIs or buildAs because those start with a
        // DataType, rather than a TypeSpecifier
        if (ctx.getChild(1).text == "is") {
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
        val targetType = asExpression.asTypeSpecifier.resultType
        verifyCast(targetType, asExpression.operand.resultType)
        asExpression.resultType = targetType
        return asExpression
    }

    override fun visitCastExpression(ctx: CastExpressionContext): Any? {
        // NOTE: This doesn't use buildAs because it starts with a DataType, rather than
        // a TypeSpecifier
        val asExpression =
            of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(true)
        val targetType = asExpression.asTypeSpecifier.resultType
        verifyCast(targetType, asExpression.operand.resultType)
        asExpression.resultType = targetType
        return asExpression
    }

    override fun visitBooleanExpression(ctx: BooleanExpressionContext): Expression? {
        var exp: UnaryExpression?
        val left = visit(ctx.expression()) as Expression?
        val lastChild = ctx.getChild(ctx.childCount - 1).text
        val nextToLast = ctx.getChild(ctx.childCount - 2).text
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
            else ->
                throw IllegalArgumentException(
                    String.format("Unknown boolean test predicate %s.", lastChild)
                )
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
        Objects.requireNonNull(left, "left expression of timing operator can not be null")
        Objects.requireNonNull(right, "right expression of timing operator can not be null")
        val timingOperatorContext = TimingOperatorContext(left!!, right!!)
        timingOperators.push(timingOperatorContext)
        return try {
            visit(ctx.intervalOperatorPhrase())
        } finally {
            timingOperators.pop()
        }
    }

    override fun visitConcurrentWithIntervalOperatorPhrase(
        ctx: ConcurrentWithIntervalOperatorPhraseContext
    ): Any? {
        // ('starts' | 'ends' | 'occurs')? 'same' dateTimePrecision? (relativeQualifier
        // | 'as') ('start' | 'end')?
        val timingOperator: TimingOperatorContext = timingOperators.peek()
        val firstChild: ParseTree = ctx.getChild(0)
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
        val lastChild: ParseTree = ctx.getChild(ctx.childCount - 1)
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
                            parseComparableDateTimePrecision(ctx.dateTimePrecision().text)
                        )
                } else {
                    of.createSameAs()
                }
            operatorName = "SameAs"
        } else {
            when (ctx.relativeQualifier().text) {
                "or after" -> {
                    operator =
                        if (ctx.dateTimePrecision() != null) {
                            of.createSameOrAfter()
                                .withPrecision(
                                    parseComparableDateTimePrecision(ctx.dateTimePrecision().text)
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
                                    parseComparableDateTimePrecision(ctx.dateTimePrecision().text)
                                )
                        } else {
                            of.createSameOrBefore()
                        }
                    operatorName = "SameOrBefore"
                    allowPromotionAndDemotion = true
                }
                else ->
                    throw IllegalArgumentException(
                        String.format(
                            "Unknown relative qualifier: '%s'.",
                            ctx.relativeQualifier().text
                        )
                    )
            }
        }
        operator = operator!!.withOperand(timingOperator.left, timingOperator.right)
        libraryBuilder.resolveBinaryCall(
            "System",
            operatorName,
            operator,
            true,
            allowPromotionAndDemotion
        )
        return operator
    }

    override fun visitIncludesIntervalOperatorPhrase(
        ctx: IncludesIntervalOperatorPhraseContext
    ): Any? {
        // 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?
        var isProper = false
        var isRightPoint = false
        val timingOperator = timingOperators.peek()
        for (pt in ctx.children) {
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
                ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
            else null

        // If the right is not convertible to an interval or list
        // if (!isRightPoint &&
        // !(timingOperator.getRight().getResultType() instanceof IntervalType
        // || timingOperator.getRight().getResultType() instanceof ListType)) {
        // isRightPoint = true;
        // }
        if (isRightPoint) {
            return if (isProper) {
                libraryBuilder.resolveProperContains(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false)
                )
            } else
                libraryBuilder.resolveContains(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false)
                )
        }
        return if (isProper) {
            libraryBuilder.resolveProperIncludes(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false)
            )
        } else
            libraryBuilder.resolveIncludes(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false)
            )
    }

    override fun visitIncludedInIntervalOperatorPhrase(
        ctx: IncludedInIntervalOperatorPhraseContext
    ): Any? {
        // ('starts' | 'ends' | 'occurs')? 'properly'? ('during' | 'included in')
        // dateTimePrecisionSpecifier?
        var isProper = false
        var isLeftPoint = false
        val timingOperator = timingOperators.peek()
        for (pt in ctx.children) {
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
                ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
            else null

        // If the left is not convertible to an interval or list
        // if (!isLeftPoint &&
        // !(timingOperator.getLeft().getResultType() instanceof IntervalType
        // || timingOperator.getLeft().getResultType() instanceof ListType)) {
        // isLeftPoint = true;
        // }
        if (isLeftPoint) {
            return if (isProper) {
                libraryBuilder.resolveProperIn(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false)
                )
            } else
                libraryBuilder.resolveIn(
                    timingOperator.left,
                    timingOperator.right,
                    parseComparableDateTimePrecision(dateTimePrecision, false)
                )
        }
        return if (isProper) {
            libraryBuilder.resolveProperIncludedIn(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false)
            )
        } else
            libraryBuilder.resolveIncludedIn(
                timingOperator.left,
                timingOperator.right,
                parseComparableDateTimePrecision(dateTimePrecision, false)
            )
    }

    override fun visitBeforeOrAfterIntervalOperatorPhrase(
        ctx: BeforeOrAfterIntervalOperatorPhraseContext
    ): Any? {
        // ('starts' | 'ends' | 'occurs')? quantityOffset? ('before' | 'after')
        // dateTimePrecisionSpecifier? ('start' |
        // 'end')?

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
        val timingOperator = timingOperators.peek()
        var isBefore = false
        var isInclusive = false
        for (child in ctx.children) {
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
        for (child in ctx.temporalRelationship().children) {
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
                ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
            else null
        if (ctx.quantityOffset() == null) {
            return if (isInclusive) {
                if (isBefore) {
                    val sameOrBefore =
                        of.createSameOrBefore()
                            .withOperand(timingOperator.left, timingOperator.right)
                    if (dateTimePrecision != null) {
                        sameOrBefore.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall(
                        "System",
                        "SameOrBefore",
                        sameOrBefore,
                        true,
                        true
                    )
                    sameOrBefore
                } else {
                    val sameOrAfter =
                        of.createSameOrAfter()
                            .withOperand(timingOperator.left, timingOperator.right)
                    if (dateTimePrecision != null) {
                        sameOrAfter.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall(
                        "System",
                        "SameOrAfter",
                        sameOrAfter,
                        true,
                        true
                    )
                    sameOrAfter
                }
            } else {
                if (isBefore) {
                    val before =
                        of.createBefore().withOperand(timingOperator.left, timingOperator.right)
                    if (dateTimePrecision != null) {
                        before.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall("System", "Before", before, true, true)
                    before
                } else {
                    val after =
                        of.createAfter().withOperand(timingOperator.left, timingOperator.right)
                    if (dateTimePrecision != null) {
                        after.precision = parseComparableDateTimePrecision(dateTimePrecision)
                    }
                    libraryBuilder.resolveBinaryCall("System", "After", after, true, true)
                    after
                }
            }
        } else {
            val quantity = visit(ctx.quantityOffset().quantity()) as Quantity?
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
                ctx.quantityOffset().offsetRelativeQualifier() == null &&
                    ctx.quantityOffset().exclusiveRelativeQualifier() == null
            ) {
                // Use a SameAs
                // For a Before, subtract the quantity from the right operand
                // For an After, add the quantity to the right operand
                if (isBefore) {
                    val subtract = of.createSubtract().withOperand(timingOperator.right, quantity)
                    track(subtract, timingOperator.right)
                    libraryBuilder.resolveBinaryCall("System", "Subtract", subtract)
                    timingOperator.right = subtract
                } else {
                    val add = of.createAdd().withOperand(timingOperator.right, quantity)
                    track(add, timingOperator.right)
                    libraryBuilder.resolveBinaryCall("System", "Add", add)
                    timingOperator.right = add
                }
                val sameAs =
                    of.createSameAs().withOperand(timingOperator.left, timingOperator.right)
                if (dateTimePrecision != null) {
                    sameAs.precision = parseComparableDateTimePrecision(dateTimePrecision)
                }
                libraryBuilder.resolveBinaryCall("System", "SameAs", sameAs)
                return sameAs
            } else {
                val isOffsetInclusive = ctx.quantityOffset().offsetRelativeQualifier() != null
                val qualifier =
                    if (ctx.quantityOffset().offsetRelativeQualifier() != null)
                        ctx.quantityOffset().offsetRelativeQualifier().text
                    else ctx.quantityOffset().exclusiveRelativeQualifier().text
                when (qualifier) {
                    "more than",
                    "or more" -> // For More Than/Or More, Use a
                        // Before/After/SameOrBefore/SameOrAfter
                        // For a Before, subtract the quantity from the right operand
                        // For an After, add the quantity to the right operand
                        return if (isBefore) {
                            val subtract =
                                of.createSubtract().withOperand(timingOperator.right, quantity)
                            track(subtract, timingOperator.right)
                            libraryBuilder.resolveBinaryCall("System", "Subtract", subtract)
                            timingOperator.right = subtract
                            if (!isOffsetInclusive) {
                                val before =
                                    of.createBefore()
                                        .withOperand(timingOperator.left, timingOperator.right)
                                if (dateTimePrecision != null) {
                                    before.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "Before",
                                    before,
                                    true,
                                    true
                                )
                                before
                            } else {
                                val sameOrBefore =
                                    of.createSameOrBefore()
                                        .withOperand(timingOperator.left, timingOperator.right)
                                if (dateTimePrecision != null) {
                                    sameOrBefore.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "SameOrBefore",
                                    sameOrBefore,
                                    true,
                                    true
                                )
                                sameOrBefore
                            }
                        } else {
                            val add = of.createAdd().withOperand(timingOperator.right, quantity)
                            track(add, timingOperator.right)
                            libraryBuilder.resolveBinaryCall("System", "Add", add)
                            timingOperator.right = add
                            if (!isOffsetInclusive) {
                                val after =
                                    of.createAfter()
                                        .withOperand(timingOperator.left, timingOperator.right)
                                if (dateTimePrecision != null) {
                                    after.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "After",
                                    after,
                                    true,
                                    true
                                )
                                after
                            } else {
                                val sameOrAfter =
                                    of.createSameOrAfter()
                                        .withOperand(timingOperator.left, timingOperator.right)
                                if (dateTimePrecision != null) {
                                    sameOrAfter.precision =
                                        parseComparableDateTimePrecision(dateTimePrecision)
                                }
                                libraryBuilder.resolveBinaryCall(
                                    "System",
                                    "SameOrAfter",
                                    sameOrAfter,
                                    true,
                                    true
                                )
                                sameOrAfter
                            }
                        }
                    "less than",
                    "or less" -> {
                        // For Less Than/Or Less, Use an In
                        // For Before, construct an interval from right - quantity to right
                        // For After, construct an interval from right to right + quantity
                        val lowerBound: Expression?
                        val upperBound: Expression?
                        val right = timingOperator.right
                        if (isBefore) {
                            lowerBound = of.createSubtract().withOperand(right, quantity)
                            track(lowerBound, right)
                            libraryBuilder.resolveBinaryCall(
                                "System",
                                "Subtract",
                                (lowerBound as BinaryExpression?)!!
                            )
                            upperBound = right
                        } else {
                            lowerBound = right
                            upperBound = of.createAdd().withOperand(right, quantity)
                            track(upperBound, right)
                            libraryBuilder.resolveBinaryCall(
                                "System",
                                "Add",
                                (upperBound as BinaryExpression?)!!
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
                                    isInclusive
                                )
                            else
                                libraryBuilder.createInterval(
                                    lowerBound,
                                    isInclusive,
                                    upperBound,
                                    isOffsetInclusive
                                )
                        track(interval, ctx.quantityOffset())
                        val inExpression = of.createIn().withOperand(timingOperator.left, interval)
                        if (dateTimePrecision != null) {
                            inExpression.precision =
                                parseComparableDateTimePrecision(dateTimePrecision)
                        }
                        track(inExpression, ctx.quantityOffset())
                        libraryBuilder.resolveBinaryCall("System", "In", inExpression)

                        // if the offset or comparison is inclusive, add a null check for B to
                        // ensure
                        // correct
                        // interpretation
                        if (isOffsetInclusive || isInclusive) {
                            val nullTest = of.createIsNull().withOperand(right)
                            track(nullTest, ctx.quantityOffset())
                            libraryBuilder.resolveUnaryCall("System", "IsNull", nullTest)
                            val notNullTest = of.createNot().withOperand(nullTest)
                            track(notNullTest, ctx.quantityOffset())
                            libraryBuilder.resolveUnaryCall("System", "Not", notNullTest)
                            val and = of.createAnd().withOperand(inExpression, notNullTest)
                            track(and, ctx.quantityOffset())
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

    @Suppress("UnusedPrivateMember")
    private fun resolveBetweenOperator(
        unit: String?,
        left: Expression,
        right: Expression
    ): BinaryExpression? {
        if (unit != null) {
            val between =
                of.createDurationBetween()
                    .withPrecision(parseDateTimePrecision(unit))
                    .withOperand(left, right)
            libraryBuilder.resolveBinaryCall("System", "DurationBetween", between)
            return between
        }
        return null
    }

    override fun visitWithinIntervalOperatorPhrase(ctx: WithinIntervalOperatorPhraseContext): Any? {
        // ('starts' | 'ends' | 'occurs')? 'properly'? 'within' quantityLiteral 'of'
        // ('start' | 'end')?
        // A starts within 3 days of start B
        // * start of A in [start of B - 3 days, start of B + 3 days] and start B is not
        // null
        // A starts within 3 days of B
        // * start of A in [start of B - 3 days, end of B + 3 days]
        val timingOperator = timingOperators.peek()
        var isProper = false
        for (child in ctx.children) {
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
        val quantity = visit(ctx.quantity()) as Quantity?
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
        lowerBound = of.createSubtract().withOperand(lowerBound, quantity)
        track(lowerBound, ctx.quantity())
        libraryBuilder.resolveBinaryCall("System", "Subtract", (lowerBound as BinaryExpression?)!!)
        upperBound = of.createAdd().withOperand(upperBound, quantity)
        track(upperBound, ctx.quantity())
        libraryBuilder.resolveBinaryCall("System", "Add", (upperBound as BinaryExpression?)!!)
        val interval = libraryBuilder.createInterval(lowerBound, !isProper, upperBound, !isProper)
        track(interval, ctx.quantity())
        val inExpression = of.createIn().withOperand(timingOperator.left, interval)
        libraryBuilder.resolveBinaryCall("System", "In", inExpression)

        // if the within is not proper and the interval is being constructed from a
        // single point, add a null check for
        // that point to ensure correct interpretation
        if (!isProper && initialBound != null) {
            val nullTest = of.createIsNull().withOperand(initialBound)
            track(nullTest, ctx.quantity())
            libraryBuilder.resolveUnaryCall("System", "IsNull", nullTest)
            val notNullTest = of.createNot().withOperand(nullTest)
            track(notNullTest, ctx.quantity())
            libraryBuilder.resolveUnaryCall("System", "Not", notNullTest)
            val and = of.createAnd().withOperand(inExpression, notNullTest)
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
                ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
            else null
        if (ctx.childCount == 1 + if (dateTimePrecision == null) 0 else 1) {
            operator =
                if (dateTimePrecision != null)
                    of.createMeets()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createMeets()
            operatorName = "Meets"
        } else {
            if ("before" == ctx.getChild(1).text) {
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
        operator.withOperand(timingOperators.peek().left, timingOperators.peek().right)
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
                ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
            else null
        if (ctx.childCount == 1 + if (dateTimePrecision == null) 0 else 1) {
            operator =
                if (dateTimePrecision != null)
                    of.createOverlaps()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createOverlaps()
            operatorName = "Overlaps"
        } else {
            if ("before" == ctx.getChild(1).text) {
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
        operator.withOperand(timingOperators.peek().left, timingOperators.peek().right)
        libraryBuilder.resolveBinaryCall("System", operatorName, operator)
        return operator
    }

    override fun visitStartsIntervalOperatorPhrase(ctx: StartsIntervalOperatorPhraseContext): Any? {
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
            else null
        val starts =
            (if (dateTimePrecision != null)
                    of.createStarts()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createStarts())
                .withOperand(timingOperators.peek().left, timingOperators.peek().right)
        libraryBuilder.resolveBinaryCall("System", "Starts", starts)
        return starts
    }

    override fun visitEndsIntervalOperatorPhrase(ctx: EndsIntervalOperatorPhraseContext): Any? {
        val dateTimePrecision =
            if (ctx.dateTimePrecisionSpecifier() != null)
                ctx.dateTimePrecisionSpecifier().dateTimePrecision().text
            else null
        val ends =
            (if (dateTimePrecision != null)
                    of.createEnds()
                        .withPrecision(parseComparableDateTimePrecision(dateTimePrecision))
                else of.createEnds())
                .withOperand(timingOperators.peek().left, timingOperators.peek().right)
        libraryBuilder.resolveBinaryCall("System", "Ends", ends)
        return ends
    }

    fun resolveIfThenElse(ifObject: If): Expression {
        ifObject.condition =
            libraryBuilder.ensureCompatible(
                ifObject.condition,
                libraryBuilder.resolveTypeName("System", "Boolean")
            )
        val resultType: DataType? =
            libraryBuilder.ensureCompatibleTypes(
                ifObject.then.resultType,
                ifObject.getElse().resultType
            )
        ifObject.resultType = resultType
        ifObject.then = libraryBuilder.ensureCompatible(ifObject.then, resultType)
        ifObject.setElse(libraryBuilder.ensureCompatible(ifObject.getElse(), resultType))
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
        for (pt: ParseTree in ctx.children) {
            if (("else" == pt.text)) {
                hitElse = true
                continue
            }
            if (pt is ExpressionContext) {
                if (hitElse) {
                    result.setElse(parseExpression(pt))
                    resultType =
                        libraryBuilder.ensureCompatibleTypes(
                            resultType,
                            result.getElse().resultType
                        )
                } else {
                    result.comparand = parseExpression(pt)
                }
            }
            if (pt is CaseExpressionItemContext) {
                val caseItem = visit(pt) as CaseItem
                if (result.comparand != null) {
                    libraryBuilder.verifyType(
                        caseItem.getWhen().resultType,
                        result.comparand.resultType
                    )
                } else {
                    verifyType(
                        caseItem.getWhen().resultType,
                        libraryBuilder.resolveTypeName("System", "Boolean")
                    )
                }
                resultType =
                    if (resultType == null) {
                        caseItem.then.resultType
                    } else {
                        libraryBuilder.ensureCompatibleTypes(resultType, caseItem.then.resultType)
                    }
                result.caseItem.add(caseItem)
            }
        }
        for (caseItem: CaseItem in result.caseItem) {
            if (result.comparand != null) {
                caseItem.setWhen(
                    libraryBuilder.ensureCompatible(caseItem.getWhen(), result.comparand.resultType)
                )
            }
            caseItem.then = libraryBuilder.ensureCompatible(caseItem.then, resultType)
        }
        result.setElse(libraryBuilder.ensureCompatible(result.getElse(), resultType))
        result.resultType = resultType
        return result
    }

    override fun visitCaseExpressionItem(ctx: CaseExpressionItemContext): Any? {
        return of.createCaseItem()
            .withWhen(parseExpression(ctx.expression(0)))
            .withThen(parseExpression(ctx.expression(1)))
    }

    override fun visitAggregateExpressionTerm(ctx: AggregateExpressionTermContext): Any? {
        when (ctx.getChild(0).text) {
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
        throw IllegalArgumentException(
            String.format("Unknown aggregate operator %s.", ctx.getChild(0).text)
        )
    }

    override fun visitSetAggregateExpressionTerm(ctx: SetAggregateExpressionTermContext): Any {
        val source: Expression? = parseExpression(ctx.expression(0))

        // If `per` is not set, it will remain `null as System.Quantity`.
        var per: Expression? =
            libraryBuilder.buildNull(libraryBuilder.resolveTypeName("System", "Quantity"))
        if (ctx.dateTimePrecision() != null) {
            per =
                libraryBuilder.createQuantity(
                    BigDecimal.valueOf(1.0),
                    (parseString(ctx.dateTimePrecision()))!!
                )
        } else if (ctx.expression().size > 1) {
            per = parseExpression(ctx.expression(1))
        } else {
            // Determine per quantity based on point type of the intervals involved
            if (source!!.resultType is ListType) {
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
        when (ctx.getChild(0).text) {
            "expand" -> {
                val expand: Expand = of.createExpand().withOperand(source, per)
                libraryBuilder.resolveBinaryCall("System", "Expand", expand)
                return expand
            }
            "collapse" -> {
                val collapse: Collapse = of.createCollapse().withOperand(source, per)
                libraryBuilder.resolveBinaryCall("System", "Collapse", collapse)
                return collapse
            }
        }
        throw IllegalArgumentException(
            String.format("Unknown aggregate set operator %s.", ctx.getChild(0).text)
        )
    }

    override fun visitRetrieve(ctx: RetrieveContext): Expression? {
        libraryBuilder.checkLiteralContext()
        val qualifiers: List<String> = parseQualifiers(ctx.namedTypeSpecifier())
        val model: String? = getModelIdentifier(qualifiers)
        val label: String =
            getTypeIdentifier(
                qualifiers,
                (parseString(ctx.namedTypeSpecifier().referentialOrTypeNameIdentifier()))!!
            )
        val dataType: DataType =
            libraryBuilder.resolveTypeName(model, label)
                ?: // ERROR:
                throw IllegalArgumentException(
                    String.format("Could not resolve type name %s.", label)
                )
        if (dataType !is ClassType || !dataType.isRetrievable) {
            // ERROR:
            throw IllegalArgumentException(
                String.format("Specified data type %s does not support retrieval.", label)
            )
        }
        val classType: ClassType = dataType
        // BTR -> The original intent of this code was to have the retrieve return the
        // base type, and use the
        // "templateId"
        // element of the retrieve to communicate the "positive" or "negative" profile
        // to the data access layer.
        // However, because this notion of carrying the "profile" through a type is not
        // general, it causes
        // inconsistencies
        // when using retrieve results with functions defined in terms of the same type
        // (see GitHub Issue #131).
        // Based on the discussion there, the retrieve will now return the declared
        // type, whether it is a profile or
        // not.
        // ProfileType profileType = dataType instanceof ProfileType ?
        // (ProfileType)dataType : null;
        // NamedType namedType = profileType == null ? classType :
        // (NamedType)classType.getBaseType();
        val namedType: NamedType = classType
        val modelInfo: ModelInfo = libraryBuilder.getModel(namedType.namespace).modelInfo
        val useStrictRetrieveTyping: Boolean =
            modelInfo.isStrictRetrieveTyping != null && modelInfo.isStrictRetrieveTyping
        var codePath: String? = null
        var property: Property? = null
        var propertyException: CqlCompilerException? = null
        var terminology: Expression? = null
        var codeComparator: String? = null
        if (ctx.terminology() != null) {
            if (ctx.codePath() != null) {
                val identifiers: String? = visit(ctx.codePath()) as String?
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
                        if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                        else CqlCompilerException.ErrorSeverity.Warning,
                        getTrackBack(ctx)
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
                            String.format(
                                "Could not resolve code path %s for the type of the retrieve %s.",
                                codePath,
                                namedType.name
                            ),
                            if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                            else CqlCompilerException.ErrorSeverity.Warning,
                            getTrackBack(ctx),
                            e
                        )
                    libraryBuilder.recordParsingException(propertyException)
                }
            }
            if (ctx.terminology().qualifiedIdentifierExpression() != null) {
                val identifiers = visit(ctx.terminology()) as List<String>
                terminology = resolveQualifiedIdentifier(identifiers)
                track(terminology, ctx.terminology().qualifiedIdentifierExpression())
            } else {
                terminology = parseExpression(ctx.terminology().expression())
            }
            codeComparator =
                if (ctx.codeComparator() != null) visit(ctx.codeComparator()) as String? else null
        }
        var result: Expression? = null

        // Only expand a choice-valued code path if no comparator is specified
        // Otherwise, a code comparator will always choose a specific representation
        val hasFHIRHelpers: Boolean = libraryInfo.resolveLibraryName("FHIRHelpers") != null
        if ((property != null) && property.resultType is ChoiceType && (codeComparator == null)) {
            for (propertyType: DataType? in (property.resultType as ChoiceType).types) {
                if (
                    (hasFHIRHelpers &&
                        propertyType is NamedType &&
                        ((propertyType as NamedType).simpleName == "Reference") &&
                        (namedType.simpleName == "MedicationRequest"))
                ) {
                    // TODO: This is a model-specific special case to support QICore
                    // This functionality needs to be generalized to a retrieve mapping in the model
                    // info
                    // But that requires a model info change (to represent references, right now the
                    // model info only
                    // includes context relationships)
                    // The reference expands to [MedicationRequest] MR with [Medication] M such that
                    // M.id =
                    // Last(Split(MR.medication.reference, '/')) and M.code in <valueset>
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
                            null
                        )
                    retrieves.add(mrRetrieve)
                    mrRetrieve.resultType = ListType(namedType as DataType?)
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
                            null
                        )
                    retrieves.add(mRetrieve)
                    mRetrieve.resultType = ListType(namedType as DataType?)
                    val q: Query = of.createQuery()
                    val aqs: AliasedQuerySource =
                        of.createAliasedQuerySource().withExpression(mrRetrieve).withAlias("MR")
                    track(aqs, ctx)
                    aqs.resultType = aqs.expression.resultType
                    q.source.add(aqs)
                    track(q, ctx)
                    q.resultType = aqs.resultType
                    val w: With = of.createWith().withExpression(mRetrieve).withAlias("M")
                    track(w, ctx)
                    w.resultType = w.expression.resultType
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
                    val e: Equal = of.createEqual().withOperand(idProperty, last)
                    libraryBuilder.resolveBinaryCall("System", "Equal", e)
                    val mCodeType: DataType? =
                        libraryBuilder.resolvePath(mNamedType as DataType?, "code")
                    val mProperty: Property = of.createProperty().withPath("code")
                    mProperty.resultType = mCodeType
                    var mCodeComparator = "~"
                    if (terminology!!.resultType is ListType) {
                        mCodeComparator = "in"
                    } else if (libraryBuilder.isCompatibleWith("1.5")) {
                        mCodeComparator =
                            if (
                                terminology.resultType.isSubTypeOf(
                                    libraryBuilder.resolveTypeName("System", "Vocabulary")
                                )
                            )
                                "in"
                            else "~"
                    }
                    val terminologyComparison: Expression =
                        if ((mCodeComparator == "in")) {
                            libraryBuilder.resolveIn(mProperty, (terminology))
                        } else {
                            val equivalent: BinaryExpression =
                                of.createEquivalent().withOperand(mProperty, terminology)
                            libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent)
                            equivalent
                        }
                    val a: And = of.createAnd().withOperand(e, terminologyComparison)
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
                            terminology
                        )
                    retrieves.add(retrieve)
                    retrieve.resultType = ListType(namedType as DataType?)
                    result =
                        if (result == null) {
                            retrieve
                        } else {
                            // Should only include the result if it resolved appropriately with the
                            // codeComparator
                            // Allowing it to go through for now
                            // if (retrieve.getCodeProperty() != null &&
                            // retrieve.getCodeComparator() !=
                            // null &&
                            // retrieve.getCodes() != null) {
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
                    terminology
                )
            retrieves.add(retrieve)
            retrieve.resultType = ListType(namedType as DataType?)
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
        terminology: Expression?
    ): Retrieve {
        var codeComparator: String? = codeComparator
        val retrieve: Retrieve =
            of.createRetrieve()
                .withDataType(libraryBuilder.dataTypeToQName(namedType as DataType?))
                .withTemplateId(classType.identifier)
                .withCodeProperty(codePath)
        if (ctx.contextIdentifier() != null) {
            val identifiers = visit(ctx.contextIdentifier()) as List<String>
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
                                        libraryBuilder.resolveTypeName("System", "Vocabulary")
                                    ))
                            ) {
                                if (
                                    terminology.resultType.isSubTypeOf(
                                        libraryBuilder.resolveTypeName("System", "Vocabulary")
                                    )
                                )
                                    "~"
                                else "contains"
                            } else {
                                if (
                                    terminology.resultType.isSubTypeOf(
                                        libraryBuilder.resolveTypeName("System", "Vocabulary")
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
                                        String.format(
                                            "Unexpected membership operator %s in retrieve",
                                            inExpression.javaClass.simpleName
                                        ),
                                        if (useStrictRetrieveTyping)
                                            CqlCompilerException.ErrorSeverity.Error
                                        else CqlCompilerException.ErrorSeverity.Warning,
                                        getTrackBack(ctx)
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
                        // to
                        // support with a
                        // retrieve (direct-reference code negation)
                        // ERROR:
                        libraryBuilder.recordParsingException(
                            CqlSemanticException(
                                "Terminology resolution using contains is not supported at this time. Use a where clause with an in operator instead.",
                                if (useStrictRetrieveTyping)
                                    CqlCompilerException.ErrorSeverity.Error
                                else CqlCompilerException.ErrorSeverity.Warning,
                                getTrackBack(ctx)
                            )
                        )
                    }
                    "~" -> {

                        // Resolve with equivalent to verify the type of the target
                        val equivalent: BinaryExpression =
                            of.createEquivalent().withOperand(property, terminology)
                        libraryBuilder.resolveBinaryCall("System", "Equivalent", equivalent)

                        // Automatically promote to a list for use in the retrieve target
                        if (
                            !((equivalent.operand[1].resultType is ListType ||
                                (libraryBuilder.isCompatibleWith("1.5") &&
                                    equivalent.operand[1]
                                        .resultType
                                        .isSubTypeOf(
                                            libraryBuilder.resolveTypeName("System", "Vocabulary")
                                        ))))
                        ) {
                            retrieve.codes = libraryBuilder.resolveToList(equivalent.operand[1])
                        } else {
                            retrieve.codes = equivalent.operand[1]
                        }
                    }
                    "=" -> {

                        // Resolve with equality to verify the type of the source and target
                        val equal: BinaryExpression =
                            of.createEqual().withOperand(property, terminology)
                        libraryBuilder.resolveBinaryCall("System", "Equal", equal)

                        // Automatically promote to a list for use in the retrieve target
                        if (
                            !((equal.operand[1].resultType is ListType ||
                                (libraryBuilder.isCompatibleWith("1.5") &&
                                    equal.operand[1]
                                        .resultType
                                        .isSubTypeOf(
                                            libraryBuilder.resolveTypeName("System", "Vocabulary")
                                        ))))
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
                                String.format(
                                    "Unknown code comparator %s in retrieve",
                                    codeComparator
                                ),
                                if (useStrictRetrieveTyping)
                                    CqlCompilerException.ErrorSeverity.Error
                                else CqlCompilerException.ErrorSeverity.Warning,
                                getTrackBack(ctx.codeComparator())
                            )
                        )
                }
                retrieve.codeComparator = codeComparator

                // Verify that the type of the terminology target is a List<Code>
                // Due to implicit conversion defined by specific models, the resolution path
                // above may result in a
                // List<Concept>
                // In that case, convert to a list of code (Union the Code elements of the
                // Concepts in the list)
                if (
                    ((retrieve.codes != null) &&
                        (retrieve.codes.resultType != null) &&
                        retrieve.codes.resultType is ListType &&
                        ((retrieve.codes.resultType as ListType).elementType ==
                            libraryBuilder.resolveTypeName("System", "Concept")))
                ) {
                    if (retrieve.codes is ToList) {
                        // ToList will always have a single argument
                        val toList: ToList = retrieve.codes as ToList
                        // If that argument is a ToConcept, replace the ToList argument with the
                        // code
                        // (skip the implicit
                        // conversion, the data access layer is responsible for it)
                        if (toList.operand is ToConcept) {
                            toList.operand = (toList.operand as ToConcept).operand
                        } else {
                            // Otherwise, access the codes property of the resulting Concept
                            val codesAccessor: Expression =
                                libraryBuilder.buildProperty(
                                    toList.operand,
                                    "codes",
                                    false,
                                    toList.operand.resultType
                                )
                            retrieve.codes = codesAccessor
                        }
                    } else {
                        // WARNING:
                        libraryBuilder.recordParsingException(
                            CqlSemanticException(
                                "Terminology target is a list of concepts, but expects a list of codes",
                                CqlCompilerException.ErrorSeverity.Warning,
                                getTrackBack(ctx)
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // If something goes wrong attempting to resolve, just set to the expression and
                // report it as a warning,
                // it shouldn't prevent translation unless the modelinfo indicates strict
                // retrieve typing
                if (
                    ((libraryBuilder.isCompatibleWith("1.5") &&
                        !(terminology.resultType.isSubTypeOf(
                            libraryBuilder.resolveTypeName("System", "Vocabulary")
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
                        if (useStrictRetrieveTyping) CqlCompilerException.ErrorSeverity.Error
                        else CqlCompilerException.ErrorSeverity.Warning,
                        getTrackBack(ctx),
                        e
                    )
                )
            }
        }
        return retrieve
    }

    override fun visitSourceClause(ctx: SourceClauseContext): Any {
        val hasFrom = "from" == ctx.getChild(0).text
        require(!(!hasFrom && isFromKeywordRequired)) {
            "The from keyword is required for queries."
        }
        val sources: MutableList<AliasedQuerySource?> = ArrayList()
        for (source in ctx.aliasedQuerySource()) {
            require(!(sources.size > 0 && !hasFrom)) {
                "The from keyword is required for multi-source queries."
            }
            sources.add(visit(source) as AliasedQuerySource?)
        }
        return sources
    }

    override fun visitQuery(ctx: cqlParser.QueryContext): Any? {
        val queryContext = QueryContext()
        libraryBuilder.pushQueryContext(queryContext)
        var sources: List<AliasedQuerySource>? = null
        return try {
            queryContext.enterSourceClause()
            try {
                sources = visit(ctx.sourceClause()) as List<AliasedQuerySource>?
            } finally {
                queryContext.exitSourceClause()
            }
            queryContext.addPrimaryQuerySources(sources!!)
            for (source: AliasedQuerySource in sources) {
                libraryBuilder.pushIdentifier(source.alias, source)
            }

            // If we are evaluating a population-level query whose source ranges over any
            // patient-context expressions,
            // then references to patient context expressions within the iteration clauses
            // of the query can be accessed
            // at the patient, rather than the population, context.
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
            var dfcx: List<LetClause>? = null
            try {
                dfcx =
                    if (ctx.letClause() != null) visit(ctx.letClause()) as List<LetClause>?
                    else null
                if (dfcx != null) {
                    for (letClause: LetClause in dfcx) {
                        libraryBuilder.pushIdentifier(letClause.identifier, letClause)
                    }
                }
                val qicx: MutableList<RelationshipClause?> = ArrayList()
                if (ctx.queryInclusionClause() != null) {
                    for (queryInclusionClauseContext in ctx.queryInclusionClause()) {
                        qicx.add(visit(queryInclusionClauseContext) as RelationshipClause?)
                    }
                }
                var where =
                    if (ctx.whereClause() != null) visit(ctx.whereClause()) as Expression? else null
                if (dateRangeOptimization && where != null) {
                    for (aqs: AliasedQuerySource in sources) {
                        where = optimizeDateRangeInQuery(where, aqs)
                    }
                }
                var ret =
                    if (ctx.returnClause() != null) visit(ctx.returnClause()) as ReturnClause?
                    else null
                val agg =
                    if (ctx.aggregateClause() != null)
                        visit(ctx.aggregateClause()) as AggregateClause?
                    else null
                if (agg == null && ret == null && sources.size > 1) {
                    ret = of.createReturnClause().withDistinct(true)
                    val returnExpression = of.createTuple()
                    val returnType = TupleType()
                    for (aqs: AliasedQuerySource in sources) {
                        val element =
                            of.createTupleElement()
                                .withName(aqs.alias)
                                .withValue(of.createAliasRef().withName(aqs.alias))
                        val sourceType =
                            if (aqs.resultType is ListType) (aqs.resultType as ListType).elementType
                            else aqs.resultType
                        element.value.resultType =
                            sourceType // Doesn't use the fluent API to avoid casting
                        element.resultType = element.value.resultType
                        returnType.addElement(TupleTypeElement(element.name, element.resultType))
                        returnExpression.element.add(element)
                    }
                    returnExpression.resultType =
                        if (queryContext.isSingular) returnType else ListType(returnType)
                    ret.expression = returnExpression
                    ret.resultType = returnExpression.resultType
                }
                queryContext.removeQuerySources(sources)
                if (dfcx != null) {
                    queryContext.removeLetClauses(dfcx)
                }
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
                            sort = visit(ctx.sortClause()) as SortClause
                            // Validate that the sort can be performed based on the existence of
                            // comparison
                            // operators
                            // for all types involved
                            for (sortByItem: SortByItem in sort.by) {
                                if (sortByItem is ByDirection) {
                                    // validate that there is a comparison operator defined for the
                                    // result element
                                    // type
                                    // of the query context
                                    libraryBuilder.verifyComparable(queryContext.resultElementType)
                                } else {
                                    libraryBuilder.verifyComparable(sortByItem.resultType)
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
                if (dfcx != null) {
                    for (letClause: LetClause? in dfcx) {
                        libraryBuilder.popIdentifier()
                    }
                }
            }
        } finally {
            libraryBuilder.popQueryContext()
            if (sources != null) {
                for (source: AliasedQuerySource? in sources) {
                    libraryBuilder.popIdentifier()
                }
            }
        }
    }
    // TODO: Expand this optimization to work the DateLow/DateHigh property
    // attributes
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
                    attemptDateRangeOptimization(where as BinaryExpression, retrieve, alias)
            ) {
                where = null
            } else if (where is And && attemptDateRangeOptimization(where, retrieve, alias)) {
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
        alias: String
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
        var reference = reference
        reference = getConversionReference(reference)
        reference = getChoiceSelection(reference)
        if (reference is Property) {
            val property = reference
            if (alias == property.scope) {
                return property.path
            } else if (property.source != null) {
                val subPath = getPropertyPath(property.source, alias)
                if (subPath != null) {
                    return String.format("%s.%s", subPath, property.path)
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
                        functionRef.operand[0].resultType,
                        functionRef.resultType
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
            if (reference.operand != null && reference.operand.resultType is ChoiceType) {
                return reference.operand
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
                    attemptDateRangeOptimization(operand as BinaryExpression, retrieve, alias)
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
        if (isBooleanLiteral(lhs, true)) {
            result = rhs
        } else if (isBooleanLiteral(rhs, true)) {
            result = lhs
        } else if (lhs is And) {
            and.operand[0] = consolidateAnd(lhs)
        } else if (rhs is And) {
            and.operand[1] = consolidateAnd(rhs)
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
        return (rhs.resultType.isSubTypeOf(libraryBuilder.resolveTypeName("System", "DateTime")) ||
            rhs.resultType.isSubTypeOf(
                IntervalType(libraryBuilder.resolveTypeName("System", "DateTime"))
            ))

        // BTR: The only requirement for the optimization is that the expression be of
        // type DateTime or
        // Interval<DateTime>
        // Whether or not the expression can be statically evaluated (literal, in the
        // loose sense of the word) is really
        // a function of the engine in determining the "initial" data requirements,
        // versus subsequent data requirements
        // Element targetElement = rhs;
        // if (rhs instanceof ParameterRef) {
        // String paramName = ((ParameterRef) rhs).getName();
        // for (ParameterDef def : getLibrary().getParameters().getDef()) {
        // if (paramName.equals(def.getName())) {
        // targetElement = def.getParameterTypeSpecifier();
        // if (targetElement == null) {
        // targetElement = def.getDefault();
        // }
        // break;
        // }
        // }
        // } else if (rhs instanceof ExpressionRef && !(rhs instanceof FunctionRef)) {
        // // TODO: Support forward declaration, if necessary
        // String expName = ((ExpressionRef) rhs).getName();
        // for (ExpressionDef def : getLibrary().getStatements().getDef()) {
        // if (expName.equals(def.getName())) {
        // targetElement = def.getExpression();
        // }
        // }
        // }
        //
        // boolean isEligible = false;
        // if (targetElement instanceof DateTime) {
        // isEligible = true;
        // } else if (targetElement instanceof Interval) {
        // Interval ivl = (Interval) targetElement;
        // isEligible = (ivl.getLow() != null && ivl.getLow() instanceof DateTime) ||
        // (ivl.getHigh() != null
        // && ivl.getHigh() instanceof DateTime);
        // } else if (targetElement instanceof IntervalTypeSpecifier) {
        // IntervalTypeSpecifier spec = (IntervalTypeSpecifier) targetElement;
        // isEligible = isDateTimeTypeSpecifier(spec.getPointType());
        // } else if (targetElement instanceof NamedTypeSpecifier) {
        // isEligible = isDateTimeTypeSpecifier(targetElement);
        // }
        // return isEligible;
    }

    @Suppress("UnusedPrivateMember")
    private fun isDateTimeTypeSpecifier(e: Element): Boolean {
        return e.resultType == libraryBuilder.resolveTypeName("System", "DateTime")
    }

    override fun visitLetClause(ctx: LetClauseContext): Any {
        val letClauseItems: MutableList<LetClause?> = ArrayList()
        for (letClauseItem in ctx.letClauseItem()) {
            letClauseItems.add(visit(letClauseItem) as LetClause?)
        }
        return letClauseItems
    }

    override fun visitLetClauseItem(ctx: LetClauseItemContext): Any? {
        val letClause =
            of.createLetClause()
                .withExpression(parseExpression(ctx.expression()))
                .withIdentifier(parseString(ctx.identifier()))
        letClause.resultType = letClause.expression.resultType
        libraryBuilder.peekQueryContext().addLetClause(letClause)
        return letClause
    }

    override fun visitAliasedQuerySource(ctx: AliasedQuerySourceContext): Any? {
        val source =
            of.createAliasedQuerySource()
                .withExpression(parseExpression(ctx.querySource()))
                .withAlias(parseString(ctx.alias()))
        source.resultType = source.expression.resultType
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

    override fun visitReturnClause(ctx: ReturnClauseContext): Any? {
        val returnClause = of.createReturnClause()
        if (ctx.getChild(1) is TerminalNode) {
            when (ctx.getChild(1).text) {
                "all" -> returnClause.isDistinct = false
                "distinct" -> returnClause.isDistinct = true
                else -> {}
            }
        }
        returnClause.expression = parseExpression(ctx.expression())
        returnClause.resultType =
            if (libraryBuilder.peekQueryContext().isSingular) returnClause.expression.resultType
            else ListType(returnClause.expression.resultType)
        return returnClause
    }

    override fun visitStartingClause(ctx: StartingClauseContext): Any? {
        if (ctx.simpleLiteral() != null) {
            return visit(ctx.simpleLiteral())
        }
        if (ctx.quantity() != null) {
            return visit(ctx.quantity())
        }
        return if (ctx.expression() != null) {
            visit(ctx.expression())
        } else null
    }

    override fun visitAggregateClause(ctx: AggregateClauseContext): Any? {
        libraryBuilder.checkCompatibilityLevel("Aggregate query clause", "1.5")
        val aggregateClause = of.createAggregateClause()
        if (ctx.getChild(1) is TerminalNode) {
            when (ctx.getChild(1).text) {
                "all" -> aggregateClause.isDistinct = false
                "distinct" -> aggregateClause.isDistinct = true
                else -> {}
            }
        }
        if (ctx.startingClause() != null) {
            aggregateClause.starting = parseExpression(ctx.startingClause())
        }

        // If there is a starting, that's the type of the var
        // If there's not a starting, push an Any and then attempt to evaluate (might
        // need a type hint here)
        aggregateClause.identifier = parseString(ctx.identifier())
        val accumulator: Expression =
            if (aggregateClause.starting != null) {
                libraryBuilder.buildNull(aggregateClause.starting.resultType)
            } else {
                libraryBuilder.buildNull(libraryBuilder.resolveTypeName("System", "Any"))
            }
        val letClause =
            of.createLetClause()
                .withExpression(accumulator)
                .withIdentifier(aggregateClause.identifier)
        letClause.resultType = letClause.expression.resultType
        libraryBuilder.peekQueryContext().addLetClause(letClause)
        aggregateClause.expression = parseExpression(ctx.expression())
        aggregateClause.resultType = aggregateClause.expression.resultType
        if (aggregateClause.starting == null) {
            accumulator.setResultType(aggregateClause.resultType)
            aggregateClause.starting = accumulator
        }
        return aggregateClause
    }

    override fun visitSortDirection(ctx: SortDirectionContext): SortDirection? {
        return SortDirection.fromValue(ctx.text)
    }

    private fun parseSortDirection(ctx: SortDirectionContext?): SortDirection? {
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
                .withResultType(sortExpression.getResultType()) as SortByItem
        } else
            of.createByExpression()
                .withExpression(sortExpression)
                .withDirection(parseSortDirection(ctx.sortDirection()))
                .withResultType(sortExpression.resultType) as SortByItem
    }

    override fun visitSortClause(ctx: SortClauseContext): Any? {
        if (ctx.sortDirection() != null) {
            return of.createSortClause()
                .withBy(
                    of.createByDirection().withDirection(parseSortDirection(ctx.sortDirection()))
                )
        }
        val sortItems: MutableList<SortByItem?> = ArrayList()
        if (ctx.sortByItem() != null) {
            for (sortByItemContext in ctx.sortByItem()) {
                sortItems.add(visit(sortByItemContext) as SortByItem?)
            }
        }
        return of.createSortClause().withBy(sortItems)
    }

    override fun visitQuerySource(ctx: QuerySourceContext): Any? {
        return if (ctx.expression() != null) {
            visit(ctx.expression())
        } else if (ctx.retrieve() != null) {
            visit(ctx.retrieve())
        } else {
            val identifiers = visit(ctx.qualifiedIdentifierExpression()) as List<String>
            resolveQualifiedIdentifier(identifiers)
        }
    }

    override fun visitIndexedExpressionTerm(ctx: IndexedExpressionTermContext): Any? {
        val indexer =
            of.createIndexer()
                .withOperand(parseExpression(ctx.expressionTerm()))
                .withOperand(parseExpression(ctx.expression()))

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

    fun resolveQualifiedIdentifier(identifiers: List<String>): Expression? {
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

    fun resolveMemberIdentifier(identifier: String): Expression? {
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
        // If the identifier cannot be resolved in the library builder, check for
        // forward declarations for expressions
        // and parameters
        var result = libraryBuilder.resolveIdentifier(identifier, false)
        if (result == null) {
            val expressionInfo = libraryInfo.resolveExpressionReference(identifier)
            if (expressionInfo != null) {
                val saveContext = saveCurrentContext(expressionInfo.context)
                try {
                    val saveChunks = chunks
                    chunks = Stack()
                    forwards.push(expressionInfo)
                    try {
                        if (expressionInfo.definition == null) {
                            // ERROR:
                            throw IllegalArgumentException(
                                String.format(
                                    "Could not validate reference to expression %s because its definition contains errors.",
                                    expressionInfo.name
                                )
                            )
                        }

                        // Have to call the visit to get the outer processing to occur
                        visit(expressionInfo.definition)
                    } finally {
                        chunks = saveChunks
                        forwards.pop()
                    }
                } finally {
                    currentContext = saveContext
                }
            }
            val parameterInfo = libraryInfo.resolveParameterReference(identifier)
            if (parameterInfo != null) {
                visitParameterDefinition(parameterInfo.definition)
            }
            result = libraryBuilder.resolveIdentifier(identifier, true)
        }
        return result
    }

    private fun ensureSystemFunctionName(libraryName: String?, functionName: String?): String? {
        var functionName = functionName
        if (libraryName == null || libraryName == "System") {
            // Because these functions can be both a keyword and the name of a method, they
            // can be resolved by the
            // parser as a function, instead of as the keyword-based parser rule. In this
            // case, the function
            // name needs to be translated to the System function name in order to resolve.
            when (functionName) {
                "contains" -> functionName = "Contains"
                "distinct" -> functionName = "Distinct"
                "exists" -> functionName = "Exists"
                "in" -> functionName = "In"
                "not" -> functionName = "Not"
            }
        }
        return functionName
    }

    private fun resolveFunction(
        libraryName: String?,
        functionName: String?,
        paramList: ParamListContext?
    ): Expression? {
        val expressions: MutableList<Expression?> = ArrayList()
        if (paramList?.expression() != null) {
            for (expressionContext in paramList.expression()) {
                expressions.add(visit(expressionContext) as Expression?)
            }
        }
        return resolveFunction(libraryName, functionName, expressions, true, false, false)
    }

    @Suppress("LongParameterList")
    fun resolveFunction(
        libraryName: String?,
        functionName: String?,
        expressions: List<Expression?>?,
        mustResolve: Boolean,
        allowPromotionAndDemotion: Boolean,
        allowFluent: Boolean
    ): Expression? {
        var functionName = functionName
        if (allowFluent) {
            libraryBuilder.checkCompatibilityLevel("Fluent functions", "1.5")
        }
        functionName = ensureSystemFunctionName(libraryName, functionName)

        // 1. Ensure all overloads of the function are registered with the operator map
        // 2. Resolve the function, allowing for the case that operator map is a
        // skeleton
        // 3. If the resolution from the operator map is a skeleton, compile the
        // function body to determine the result
        // type

        // Find all functionDefinitionInfo instances with the given name
        // register each functionDefinitionInfo
        if (libraryName == null || libraryName == "" || libraryName == libraryInfo.libraryName) {
            val fdis = libraryInfo.resolveFunctionReference(functionName!!)
            if (fdis != null) {
                for ((_, context, definition) in fdis) {
                    val saveContext = saveCurrentContext(context)
                    try {
                        registerFunctionDefinition(definition)
                    } finally {
                        currentContext = saveContext
                    }
                }
            }
        }
        val result =
            libraryBuilder.resolveFunction(
                libraryName,
                functionName,
                expressions!!,
                mustResolve,
                allowPromotionAndDemotion,
                allowFluent
            )
        if (result is FunctionRefInvocation) {
            if (
                result.resolution != null &&
                    result.resolution!!.operator != null &&
                    (result.resolution!!.operator.libraryName == null ||
                        (result.resolution!!.operator.libraryName ==
                            libraryBuilder.compiledLibrary.identifier.id))
            ) {
                val op = result.resolution!!.operator
                val fh = getFunctionHeader(op)
                if (!fh.isCompiled) {
                    val ctx = getFunctionDefinitionContext(fh)
                    val saveContext = saveCurrentContext(fh.functionDef.context)
                    val saveChunks = chunks
                    chunks = Stack()
                    try {
                        val fd = compileFunctionDefinition(ctx)
                        op.resultType = fd.resultType
                        result.resultType = op.resultType
                    } finally {
                        currentContext = saveContext
                        chunks = saveChunks
                    }
                }
            }
        }
        if (mustResolve) {
            // Extra internal error handling, these should never be hit if the two-phase
            // operator compile is working as
            // expected
            require(result != null) { "Internal error: could not resolve function" }
            require(result.expression != null) {
                "Internal error: could not resolve invocation expression"
            }
            require(result.expression.resultType != null) {
                "Internal error: could not determine result type"
            }
        }
        return result?.expression
    }

    fun resolveFunctionOrQualifiedFunction(
        identifier: String?,
        paramListCtx: ParamListContext?
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
                if (target is Expression && isMethodInvocationEnabled) {
                    return systemMethodResolver.resolveMethod(
                        target,
                        (identifier)!!,
                        paramListCtx,
                        true
                    )
                }
                if (!isMethodInvocationEnabled) {
                    throw CqlCompilerException(
                        String.format(
                            "The identifier %s could not be resolved as an invocation because method-style invocation is disabled.",
                            identifier
                        ),
                        CqlCompilerException.ErrorSeverity.Error
                    )
                }
                throw IllegalArgumentException(
                    String.format("Invalid invocation target: %s", target.javaClass.name)
                )
            } finally {
                libraryBuilder.pushExpressionTarget(target)
            }
        }

        // If we are in an implicit $this context, the function may be resolved as a
        // method invocation
        val thisRef: Expression? = libraryBuilder.resolveIdentifier("\$this", false)
        if (thisRef != null) {
            val result: Expression? =
                systemMethodResolver.resolveMethod(thisRef, (identifier)!!, paramListCtx, false)
            if (result != null) {
                return result
            }
        }

        // If we are in an implicit context (i.e. a context named the same as a
        // parameter), the function may be resolved
        // as a method invocation
        val parameterRef: ParameterRef? = libraryBuilder.resolveImplicitContext()
        if (parameterRef != null) {
            val result: Expression? =
                systemMethodResolver.resolveMethod(
                    parameterRef,
                    (identifier)!!,
                    paramListCtx,
                    false
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
            parseString(ctx.referentialIdentifier()),
            ctx.paramList()
        )
    }

    override fun visitQualifiedFunction(ctx: QualifiedFunctionContext): Expression? {
        return resolveFunctionOrQualifiedFunction(
            parseString(ctx.identifierOrFunctionIdentifier()),
            ctx.paramList()
        )
    }

    override fun visitFunctionBody(ctx: FunctionBodyContext): Any? {
        return visit(ctx.expression())
    }

    private fun getFunctionHeader(ctx: FunctionDefinitionContext): FunctionHeader {
        var fh = functionHeaders[ctx]
        if (fh == null) {
            val saveChunks = chunks
            chunks = Stack()
            fh =
                try {
                    // Have to call the visit to allow the outer processing to occur
                    parseFunctionHeader(ctx)
                } finally {
                    chunks = saveChunks
                }
            functionHeaders[ctx] = fh
            functionDefinitions[fh] = ctx
            functionHeadersByDef[fh!!.functionDef] = fh
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
                    target =
                        if (target == null) {
                            fd
                        } else {
                            throw IllegalArgumentException(
                                String.format(
                                    "Internal error attempting to resolve function header for %s",
                                    op.name
                                )
                            )
                        }
                }
            }
        }
        return target
    }

    private fun getFunctionHeaderByDef(fd: FunctionDef): FunctionHeader? {
        // Shouldn't need to do this, something about the hashCode implementation of
        // FunctionDef is throwing this off,
        // Don't have time to investigate right now, this should work fine, could
        // potentially be improved
        for ((key, value) in functionHeadersByDef) {
            if (key === fd) {
                return value
            }
        }
        return null
    }

    private fun getFunctionHeader(op: Operator): FunctionHeader {
        val fd =
            getFunctionDef(op)
                ?: throw IllegalArgumentException(
                    String.format("Could not resolve function header for operator %s", op.name)
                )
        return getFunctionHeaderByDef(fd)
            ?: throw IllegalArgumentException(
                String.format("Could not resolve function header for operator %s", op.name)
            )
    }

    private fun getFunctionDefinitionContext(fh: FunctionHeader): FunctionDefinitionContext {
        return functionDefinitions[fh]
            ?: throw IllegalArgumentException(
                String.format(
                    "Could not resolve function definition context for function header %s",
                    fh.functionDef.name
                )
            )
    }

    fun registerFunctionDefinition(ctx: FunctionDefinitionContext) {
        val fh = getFunctionHeader(ctx)
        if (!libraryBuilder.compiledLibrary.contains(fh.functionDef)) {
            libraryBuilder.addExpression(fh.functionDef)
        }
    }

    fun compileFunctionDefinition(ctx: FunctionDefinitionContext): FunctionDef {
        val fh: FunctionHeader = getFunctionHeader(ctx)
        val functionDef: FunctionDef = fh.functionDef
        val resultType: TypeSpecifier? = fh.resultType
        val op: Operator =
            libraryBuilder.resolveFunctionDefinition(fh.functionDef)
                ?: throw IllegalArgumentException(
                    String.format(
                        "Internal error: Could not resolve operator map entry for function header %s",
                        fh.mangledName
                    )
                )
        libraryBuilder.pushIdentifier(functionDef.name, functionDef, IdentifierScope.GLOBAL)
        val operand: List<OperandDef> = op.functionDef!!.operand
        for (operandDef: OperandDef in operand) {
            libraryBuilder.pushIdentifier(operandDef.name, operandDef)
        }
        try {
            if (ctx.functionBody() != null) {
                libraryBuilder.beginFunctionDef(functionDef)
                try {
                    libraryBuilder.pushExpressionContext(currentContext)
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
                if (
                    (resultType != null) &&
                        (functionDef.expression != null) &&
                        (functionDef.expression.resultType != null)
                ) {
                    if (!subTypeOf(functionDef.expression.resultType, resultType.resultType)) {
                        // ERROR:
                        throw IllegalArgumentException(
                            String.format(
                                "Function %s has declared return type %s but the function body returns incompatible type %s.",
                                functionDef.name,
                                resultType.resultType,
                                functionDef.expression.resultType
                            )
                        )
                    }
                }
                functionDef.resultType = functionDef.expression.resultType
                op.resultType = functionDef.resultType
            } else {
                functionDef.isExternal = true
                if (resultType == null) {
                    // ERROR:
                    throw IllegalArgumentException(
                        String.format(
                            "Function %s is marked external but does not declare a return type.",
                            functionDef.name
                        )
                    )
                }
                functionDef.resultType = resultType.resultType
                op.resultType = functionDef.resultType
            }
            functionDef.context = currentContext
            fh.isCompiled = true
            return functionDef
        } finally {
            for (operandDef: OperandDef? in operand) {
                try {
                    libraryBuilder.popIdentifier()
                } catch (e: Exception) {
                    log.info("Error popping identifier: {}", e.message)
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
                ret = bool == java.lang.Boolean.valueOf(expression.value)
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
            node.symbol.charPositionInLine + node.symbol.text.length
        )
    }

    private fun getTrackBack(ctx: ParserRuleContext): TrackBack {
        return TrackBack(
            libraryBuilder.libraryIdentifier,
            ctx.getStart().line,
            ctx.getStart().charPositionInLine + 1, // 1-based instead of 0-based
            ctx.getStop().line,
            ctx.getStop().charPositionInLine +
                ctx.getStop().text.length // 1-based instead of 0-based
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

    private fun track(trackable: Trackable?, pt: ParseTree): TrackBack? {
        val tb = getTrackBack(pt)
        if (tb != null) {
            trackable!!.trackbacks.add(tb)
        }
        if (trackable is Element) {
            decorate(trackable, tb)
        }
        return tb
    }

    private fun track(trackable: Trackable?, from: Element): TrackBack? {
        val tb = if (from.trackbacks.size > 0) from.trackbacks[0] else null
        if (tb != null) {
            trackable!!.trackbacks.add(tb)
        }
        if (trackable is Element) {
            decorate(trackable, tb)
        }
        return tb
    }

    companion object {
        private val log = LoggerFactory.getLogger(Cql2ElmVisitor::class.java)
    }
}