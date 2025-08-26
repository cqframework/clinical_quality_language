package org.cqframework.cql.cql2elm.preprocessor

import kotlin.collections.ArrayList
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.ResultWithPossibleError
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser.*
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.*

@Suppress("TooManyFunctions")
class CqlPreprocessor(libraryBuilder: LibraryBuilder, tokenStream: TokenStream) :
    CqlPreprocessorElmCommonVisitor(libraryBuilder, tokenStream) {
    private var lastSourceIndex = -1

    private fun processHeader(ctx: ParseTree, info: BaseInfo) {
        val sourceInterval = ctx.sourceInterval
        val beforeDefinition = sourceInterval.a - 1
        if (beforeDefinition >= lastSourceIndex) {
            val header = Interval(lastSourceIndex + 1, sourceInterval.a - 1)
            lastSourceIndex = sourceInterval.b
            info.headerInterval = header
            info.header = tokenStream.getText(header)
        }
    }

    override fun visitLibrary(ctx: LibraryContext): Any? {
        var lastResult: Any? = null
        // NOTE: Need to set the library identifier here so the builder can begin the translation
        // appropriately
        val identifier =
            VersionedIdentifier().withId(libraryInfo.libraryName).withVersion(libraryInfo.version)
        if (libraryInfo.namespaceName != null) {
            identifier.system =
                libraryBuilder.resolveNamespaceUri(libraryInfo.namespaceName!!, true)
        } else if (libraryBuilder.namespaceInfo != null) {
            identifier.system = libraryBuilder.namespaceInfo.uri
        }
        libraryBuilder.libraryIdentifier = identifier
        libraryBuilder.beginTranslation()
        return try {
            // Loop through and call visit on each child (to ensure they are tracked)
            for (i in 0 until ctx.childCount) {
                val tree = ctx.getChild(i)
                val terminalNode = tree as? TerminalNode
                if (terminalNode != null && terminalNode.symbol.type == Recognizer.EOF) {
                    continue
                }
                val childResult = visit(tree!!)
                // Only set the last result if we received something useful
                if (childResult != null) {
                    lastResult = childResult
                }
            }

            // Return last result (consistent with super implementation and helps w/ testing)
            lastResult
        } finally {
            libraryBuilder.endTranslation()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitLibraryDefinition(ctx: LibraryDefinitionContext): Any? {
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        val libraryName = identifiers.removeAt(identifiers.size - 1)
        val namespaceName = if (identifiers.isNotEmpty()) identifiers.joinToString(".") else null
        val version =
            if (ctx.versionSpecifier() != null) visit(ctx.versionSpecifier()!!) as String else null
        val newLibraryInfo = LibraryInfo(namespaceName, libraryName, version, ctx)
        libraryInfo = newLibraryInfo
        processHeader(ctx, newLibraryInfo)
        return super.visitLibraryDefinition(ctx)
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitIncludeDefinition(ctx: IncludeDefinitionContext): Any {
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        val name = identifiers.removeAt(identifiers.size - 1)
        val namespaceName = if (identifiers.isNotEmpty()) identifiers.joinToString(".") else null
        val version =
            if (ctx.versionSpecifier() != null) visit(ctx.versionSpecifier()!!) as String else null
        val localName =
            if (ctx.localIdentifier() != null) parseString(ctx.localIdentifier())!! else name
        val includeDefinition = IncludeDefinitionInfo(namespaceName, name, version, localName, ctx)
        processHeader(ctx, includeDefinition)
        libraryInfo.addIncludeDefinition(includeDefinition)
        return includeDefinition
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitUsingDefinition(ctx: UsingDefinitionContext): Any {
        val identifiers = visit(ctx.qualifiedIdentifier()) as MutableList<String>
        val unqualifiedIdentifier = identifiers.removeAt(identifiers.size - 1)
        val namespaceNameForUsingDefinition =
            if (identifiers.isNotEmpty()) identifiers.joinToString(".") else null
        val version =
            if (ctx.versionSpecifier() != null) visit(ctx.versionSpecifier()!!) as String else null
        val localName =
            if (ctx.localIdentifier() != null) parseString(ctx.localIdentifier())!!
            else unqualifiedIdentifier
        val usingDefinition =
            UsingDefinitionInfo(
                namespaceNameForUsingDefinition,
                unqualifiedIdentifier,
                version,
                localName,
                ctx
            )
        processHeader(ctx, usingDefinition)
        libraryInfo.addUsingDefinition(usingDefinition)
        val namespaceName =
            when {
                identifiers.isNotEmpty() -> identifiers.joinToString(".")
                libraryBuilder.isWellKnownModelName(unqualifiedIdentifier) -> null
                libraryBuilder.namespaceInfo != null -> libraryBuilder.namespaceInfo.name
                else -> null
            }
        var modelNamespace: NamespaceInfo? = null
        if (namespaceName != null) {
            val namespaceUri = libraryBuilder.resolveNamespaceUri(namespaceName, true)
            modelNamespace = NamespaceInfo(namespaceName, namespaceUri!!)
        }
        val localIdentifier =
            if (ctx.localIdentifier() == null) unqualifiedIdentifier
            else parseString(ctx.localIdentifier())!!
        require(localIdentifier == unqualifiedIdentifier) {
            """Local identifiers for models must be the same as the name of the model 
                in this release of the translator (Model $unqualifiedIdentifier, Called $localIdentifier)"""
        }

        // This has the side effect of initializing
        // the model in the ModelManager
        getModel(
            modelNamespace,
            unqualifiedIdentifier,
            parseString(ctx.versionSpecifier()),
            localIdentifier
        )

        return usingDefinition
    }

    override fun visitCodesystemDefinition(ctx: CodesystemDefinitionContext): Any {
        val codesystemDefinition = CodesystemDefinitionInfo(parseString(ctx.identifier())!!, ctx)
        processHeader(ctx, codesystemDefinition)
        libraryInfo.addCodesystemDefinition(codesystemDefinition)
        return codesystemDefinition
    }

    override fun visitValuesetDefinition(ctx: ValuesetDefinitionContext): Any {
        val valuesetDefinition = ValuesetDefinitionInfo(parseString(ctx.identifier())!!, ctx)
        processHeader(ctx, valuesetDefinition)
        libraryInfo.addValuesetDefinition(valuesetDefinition)
        return valuesetDefinition
    }

    override fun visitCodeDefinition(ctx: CodeDefinitionContext): Any {
        val codeDefinition = CodeDefinitionInfo(parseString(ctx.identifier())!!, ctx)
        processHeader(ctx, codeDefinition)
        libraryInfo.addCodeDefinition(codeDefinition)
        return codeDefinition
    }

    override fun visitConceptDefinition(ctx: ConceptDefinitionContext): Any {
        val conceptDefinition = ConceptDefinitionInfo(parseString(ctx.identifier())!!, ctx)
        processHeader(ctx, conceptDefinition)
        libraryInfo.addConceptDefinition(conceptDefinition)
        return conceptDefinition
    }

    override fun visitParameterDefinition(ctx: ParameterDefinitionContext): Any {
        val parameterDefinition = ParameterDefinitionInfo(parseString(ctx.identifier())!!, ctx)
        processHeader(ctx, parameterDefinition)
        libraryInfo.addParameterDefinition(parameterDefinition)
        return parameterDefinition
    }

    override fun visitContextDefinition(ctx: ContextDefinitionContext): Any {
        val modelIdentifier =
            if (ctx.modelIdentifier() != null) parseString(ctx.modelIdentifier()) else null
        val unqualifiedContext = parseString(ctx.identifier())!!
        currentContext =
            if (!modelIdentifier.isNullOrEmpty()) {
                "$modelIdentifier.$unqualifiedContext"
            } else {
                unqualifiedContext
            }
        val contextDefinition = ContextDefinitionInfo(ctx)
        processHeader(ctx, contextDefinition)
        libraryInfo.addContextDefinition(contextDefinition)
        if (!implicitContextCreated && unqualifiedContext != "Unfiltered") {
            val expressionDefinition =
                ExpressionDefinitionInfo(unqualifiedContext, currentContext, null)
            libraryInfo.addExpressionDefinition(expressionDefinition)
            implicitContextCreated = true
        }
        return currentContext
    }

    override fun visitExpressionDefinition(ctx: ExpressionDefinitionContext): Any {
        val expressionDefinition =
            ExpressionDefinitionInfo(parseString(ctx.identifier())!!, currentContext, ctx)
        processHeader(ctx, expressionDefinition)
        libraryInfo.addExpressionDefinition(expressionDefinition)
        return expressionDefinition
    }

    override fun visitFunctionDefinition(ctx: FunctionDefinitionContext): Any {
        val functionDefinition =
            FunctionDefinitionInfo(
                parseString(ctx.identifierOrFunctionIdentifier())!!,
                currentContext,
                ctx
            )
        processHeader(ctx, functionDefinition)
        libraryInfo.addFunctionDefinition(functionDefinition)
        return functionDefinition
    }

    override fun visitNamedTypeSpecifier(ctx: NamedTypeSpecifierContext): NamedTypeSpecifier {
        val qualifiers = parseQualifiers(ctx)
        val modelIdentifier = getModelIdentifier(qualifiers)
        val identifier =
            getTypeIdentifier(qualifiers, parseString(ctx.referentialOrTypeNameIdentifier())!!)
        val typeSpecifierKey = "$modelIdentifier:$identifier"
        val resultType = libraryBuilder.resolveTypeName(modelIdentifier, identifier)
        if (null == resultType) {
            libraryBuilder.addNamedTypeSpecifierResult(
                typeSpecifierKey,
                ResultWithPossibleError.withError()
            )
            throw CqlCompilerException(
                "Could not find type for model: $modelIdentifier and name: $identifier"
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

    override fun visitQualifiedIdentifier(ctx: QualifiedIdentifierContext): Any {
        // Return the list of qualified identifiers for resolution by the containing element
        val identifiers = ArrayList<String>()
        for (qualifierContext in ctx.qualifier()) {
            val qualifier = visit(qualifierContext) as String
            identifiers.add(qualifier)
        }
        val identifier = parseString(ctx.identifier())!!
        identifiers.add(identifier)
        return identifiers
    }

    override fun visitQualifiedIdentifierExpression(
        ctx: QualifiedIdentifierExpressionContext
    ): Any {
        // Return the list of qualified identifiers for resolution by the containing element
        val identifiers = ArrayList<String>()
        for (qualifierContext in ctx.qualifierExpression()) {
            val qualifier = visit(qualifierContext) as String
            identifiers.add(qualifier)
        }
        val identifier = parseString(ctx.referentialIdentifier())!!
        identifiers.add(identifier)
        return identifiers
    }

    override fun defaultResult(): Any? {
        return null
    }
}
