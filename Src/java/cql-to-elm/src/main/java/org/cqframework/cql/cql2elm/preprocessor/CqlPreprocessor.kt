package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.ResultWithPossibleError
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.VersionedIdentifier
import org.slf4j.LoggerFactory

@Suppress("detekt:all")
class CqlPreprocessor(libraryBuilder: LibraryBuilder, tokenStream: TokenStream) :
    CqlPreprocessorElmCommonVisitor(libraryBuilder, tokenStream) {
    private var lastSourceIndex = -1

    private fun processHeader(ctx: ParseTree, info: BaseInfo) {
        var header: Interval? = null
        val sourceInterval = ctx.sourceInterval
        val beforeDefinition = sourceInterval.a - 1
        if (beforeDefinition >= lastSourceIndex) {
            header = Interval(lastSourceIndex + 1, sourceInterval.a - 1)
            lastSourceIndex = sourceInterval.b
            info.headerInterval = header
            info.header = tokenStream.getText(header)
        }
    }

    override fun visitLibrary(ctx: cqlParser.LibraryContext): Any {
        var lastResult: Any? = null
        // NOTE: Need to set the library identifier here so the builder can begin the translation
        // appropriately
        val identifier =
            VersionedIdentifier().withId(libraryInfo.libraryName).withVersion(libraryInfo.version)
        if (libraryInfo.namespaceName != null) {
            identifier.system = libraryBuilder.resolveNamespaceUri(libraryInfo.namespaceName, true)
        } else if (libraryBuilder.namespaceInfo != null) {
            identifier.system = libraryBuilder.namespaceInfo.uri
        }
        libraryBuilder.libraryIdentifier = identifier
        libraryBuilder.beginTranslation()
        return try {
            // Loop through and call visit on each child (to ensure they are tracked)
            for (i in 0 until ctx.childCount) {
                val tree = ctx.getChild(i)
                val terminalNode = if (tree is TerminalNode) tree else null
                if (terminalNode != null && terminalNode.symbol.type == cqlParser.Tokens.EOF) {
                    continue
                }
                val childResult = visit(tree!!)
                // Only set the last result if we received something useful
                if (childResult != null) {
                    lastResult = childResult
                }
            }

            // Return last result (consistent with super implementation and helps w/ testing)
            lastResult!!
        } finally {
            libraryBuilder.endTranslation()
        }
    }

    override fun visitLibraryDefinition(ctx: cqlParser.LibraryDefinitionContext): Any {
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        libraryInfo.libraryName = identifiers.removeAt(identifiers.size - 1)
        if (identifiers.size > 0) {
            libraryInfo.namespaceName = java.lang.String.join(".", identifiers)
        }
        if (ctx.versionSpecifier() != null) {
            libraryInfo.version = visit(ctx.versionSpecifier()!!) as String
        }
        libraryInfo.definition = ctx
        processHeader(ctx, libraryInfo)
        return super.visitLibraryDefinition(ctx)!!
    }

    override fun visitIncludeDefinition(ctx: cqlParser.IncludeDefinitionContext): Any {
        val includeDefinition = IncludeDefinitionInfo()
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        includeDefinition.name = identifiers.removeAt(identifiers.size - 1)
        if (identifiers.size > 0) {
            includeDefinition.namespaceName = java.lang.String.join(".", identifiers)
        }
        if (ctx.versionSpecifier() != null) {
            includeDefinition.version = visit(ctx.versionSpecifier()!!) as String
        }
        if (ctx.localIdentifier() != null) {
            includeDefinition.localName = parseString(ctx.localIdentifier())
        } else {
            includeDefinition.localName = includeDefinition.name
        }
        includeDefinition.definition = ctx
        processHeader(ctx, includeDefinition)
        libraryInfo.addIncludeDefinition(includeDefinition)
        return includeDefinition
    }

    override fun visitUsingDefinition(ctx: cqlParser.UsingDefinitionContext): Any {
        val usingDefinition = UsingDefinitionInfo()
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        val unqualifiedIdentifier: String = identifiers.removeAt(identifiers.size - 1)
        usingDefinition.name = unqualifiedIdentifier
        if (identifiers.size > 0) {
            usingDefinition.namespaceName = java.lang.String.join(".", identifiers)
        }
        if (ctx.versionSpecifier() != null) {
            usingDefinition.version = visit(ctx.versionSpecifier()!!) as String
        }
        if (ctx.localIdentifier() != null) {
            usingDefinition.localName = parseString(ctx.localIdentifier())
        } else {
            usingDefinition.localName = usingDefinition.name
        }
        usingDefinition.definition = ctx
        processHeader(ctx, usingDefinition)
        libraryInfo.addUsingDefinition(usingDefinition)
        val namespaceName =
            if (!identifiers.isEmpty()) java.lang.String.join(".", identifiers)
            else if (libraryBuilder.isWellKnownModelName(unqualifiedIdentifier)) null
            else if (libraryBuilder.namespaceInfo != null) libraryBuilder.namespaceInfo.name
            else null
        var modelNamespace: NamespaceInfo? = null
        if (namespaceName != null) {
            val namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true)
            modelNamespace = NamespaceInfo(namespaceName, namespaceUri)
        }
        val localIdentifier =
            if (ctx.localIdentifier() == null) unqualifiedIdentifier
            else parseString(ctx.localIdentifier())
        require(localIdentifier == unqualifiedIdentifier) {
            String.format(
                "Local identifiers for models must be the same as the name of the model in this release of the translator (Model %s, Called %s)",
                unqualifiedIdentifier,
                localIdentifier
            )
        }

        // This should only be called once, from this class, and not from Cql2ElmVisitor otherwise
        // there will be
        // duplicate errors sometimes
        val model =
            getModel(
                modelNamespace,
                unqualifiedIdentifier,
                parseString(ctx.versionSpecifier()),
                localIdentifier
            )
        return usingDefinition
    }

    override fun visitCodesystemDefinition(ctx: cqlParser.CodesystemDefinitionContext): Any {
        val codesystemDefinition = CodesystemDefinitionInfo()
        codesystemDefinition.name = parseString(ctx.identifier())
        codesystemDefinition.definition = ctx
        processHeader(ctx, codesystemDefinition)
        libraryInfo.addCodesystemDefinition(codesystemDefinition)
        return codesystemDefinition
    }

    override fun visitValuesetDefinition(ctx: cqlParser.ValuesetDefinitionContext): Any {
        val valuesetDefinition = ValuesetDefinitionInfo()
        valuesetDefinition.name = parseString(ctx.identifier())
        valuesetDefinition.definition = ctx
        processHeader(ctx, valuesetDefinition)
        libraryInfo.addValuesetDefinition(valuesetDefinition)
        return valuesetDefinition
    }

    override fun visitCodeDefinition(ctx: cqlParser.CodeDefinitionContext): Any {
        val codeDefinition = CodeDefinitionInfo()
        codeDefinition.name = parseString(ctx.identifier())
        codeDefinition.definition = ctx
        processHeader(ctx, codeDefinition)
        libraryInfo.addCodeDefinition(codeDefinition)
        return codeDefinition
    }

    override fun visitConceptDefinition(ctx: cqlParser.ConceptDefinitionContext): Any {
        val conceptDefinition = ConceptDefinitionInfo()
        conceptDefinition.name = parseString(ctx.identifier())
        conceptDefinition.definition = ctx
        processHeader(ctx, conceptDefinition)
        libraryInfo.addConceptDefinition(conceptDefinition)
        return conceptDefinition
    }

    override fun visitParameterDefinition(ctx: cqlParser.ParameterDefinitionContext): Any {
        val parameterDefinition = ParameterDefinitionInfo()
        parameterDefinition.name = parseString(ctx.identifier())
        parameterDefinition.definition = ctx
        processHeader(ctx, parameterDefinition)
        libraryInfo.addParameterDefinition(parameterDefinition)
        return parameterDefinition
    }

    override fun visitContextDefinition(ctx: cqlParser.ContextDefinitionContext): Any {
        val modelIdentifier =
            if (ctx.modelIdentifier() != null) parseString(ctx.modelIdentifier()) else null
        val unqualifiedContext = parseString(ctx.identifier())
        currentContext =
            if (modelIdentifier != null && modelIdentifier != "") {
                "$modelIdentifier.$unqualifiedContext"
            } else {
                unqualifiedContext
            }
        val contextDefinition = ContextDefinitionInfo()
        contextDefinition.definition = ctx
        processHeader(ctx, contextDefinition)
        libraryInfo.addContextDefinition(contextDefinition)
        if (!implicitContextCreated && unqualifiedContext != "Unfiltered") {
            val expressionDefinition = ExpressionDefinitionInfo()
            expressionDefinition.name = unqualifiedContext
            expressionDefinition.context = currentContext
            libraryInfo.addExpressionDefinition(expressionDefinition)
            implicitContextCreated = true
        }
        return currentContext
    }

    override fun visitExpressionDefinition(ctx: cqlParser.ExpressionDefinitionContext): Any {
        val expressionDefinition = ExpressionDefinitionInfo()
        expressionDefinition.name = parseString(ctx.identifier())
        expressionDefinition.context = currentContext
        expressionDefinition.definition = ctx
        processHeader(ctx, expressionDefinition)
        libraryInfo.addExpressionDefinition(expressionDefinition)
        return expressionDefinition
    }

    override fun visitFunctionDefinition(ctx: cqlParser.FunctionDefinitionContext): Any {
        val functionDefinition = FunctionDefinitionInfo()
        functionDefinition.name = parseString(ctx.identifierOrFunctionIdentifier())
        functionDefinition.context = currentContext
        functionDefinition.definition = ctx
        processHeader(ctx, functionDefinition)
        libraryInfo.addFunctionDefinition(functionDefinition)
        return functionDefinition
    }

    override fun visitNamedTypeSpecifier(
        ctx: cqlParser.NamedTypeSpecifierContext
    ): NamedTypeSpecifier {
        val qualifiers = parseQualifiers(ctx)
        val modelIdentifier: String? =
            CqlPreprocessorElmCommonVisitor.Companion.getModelIdentifier(qualifiers)
        val identifier: String? =
            CqlPreprocessorElmCommonVisitor.Companion.getTypeIdentifier(
                qualifiers,
                parseString(ctx.referentialOrTypeNameIdentifier())
            )
        val typeSpecifierKey = String.format("%s:%s", modelIdentifier, identifier)
        val resultType = libraryBuilder.resolveTypeName(modelIdentifier, identifier)
        if (null == resultType) {
            libraryBuilder.addNamedTypeSpecifierResult(
                typeSpecifierKey,
                ResultWithPossibleError.withError()
            )
            throw CqlCompilerException(
                String.format(
                    "Could not find type for model: %s and name: %s",
                    modelIdentifier,
                    identifier
                )
            )
        }
        val result =
            of.createNamedTypeSpecifier().withName(libraryBuilder.dataTypeToQName(resultType))

        // Fluent API would be nice here, but resultType isn't part of the model so...
        result.resultType = resultType
        libraryBuilder.addNamedTypeSpecifierResult(
            typeSpecifierKey,
            ResultWithPossibleError.withTypeSpecifier(result)
        )
        return result
    }

    override fun visitTerminal(node: TerminalNode): Any {
        var text = node.text
        val tokenType = node.symbol.type
        if (cqlLexer.Tokens.STRING == tokenType || cqlLexer.Tokens.QUOTEDIDENTIFIER == tokenType) {
            // chop off leading and trailing ' or "
            text = text.substring(1, text.length - 1)
        }
        return text
    }

    override fun visitQualifiedIdentifier(ctx: cqlParser.QualifiedIdentifierContext): Any {
        // Return the list of qualified identifiers for resolution by the containing element
        val identifiers: MutableList<String?> = ArrayList()
        for (qualifierContext in ctx.qualifier()) {
            val qualifier = visit(qualifierContext) as String
            identifiers.add(qualifier)
        }
        val identifier = parseString(ctx.identifier())
        identifiers.add(identifier)
        return identifiers
    }

    override fun visitQualifiedIdentifierExpression(
        ctx: cqlParser.QualifiedIdentifierExpressionContext
    ): Any {
        // Return the list of qualified identifiers for resolution by the containing element
        val identifiers: MutableList<String?> = ArrayList()
        for (qualifierContext in ctx.qualifierExpression()) {
            val qualifier = visit(qualifierContext) as String
            identifiers.add(qualifier)
        }
        val identifier = parseString(ctx.referentialIdentifier())
        identifiers.add(identifier)
        return identifiers
    }

    companion object {
        val logger = LoggerFactory.getLogger(CqlPreprocessor::class.java)
    }
}