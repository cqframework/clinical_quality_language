package org.hl7.cql.ast

import kotlin.js.JsExport
import kotlinx.serialization.json.Json
import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ListTokenSource
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.cqframework.cql.gen.cqlParser
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.StringEscapeUtils
import org.hl7.cql.parsetree.createParser

@Suppress("TooManyFunctions", "LongMethod", "CyclomaticComplexMethod", "ReturnCount", "LargeClass")
class Builder(private val sourceId: String? = null) {

    private val problems = mutableListOf<Problem>()

    /**
     * Parses a library one top-level declaration at a time (see [StatementBoundaries]), so that a
     * syntax error in one cannot consume the others. For a library without errors the result is the
     * same as parsing it whole.
     */
    fun parseLibrary(text: String): LibraryResult {
        problems.clear()
        val lexed = StatementBoundaries.lex(text)
        val lexerProblems =
            lexed.errors.map {
                Problem(
                    message = it.message,
                    locator = Locator(sourceId, it.startIndex, it.stopIndex, it.line, it.column),
                )
            }
        problems += lexerProblems
        val runs = StatementBoundaries.split(lexed.tokens)
        val parts =
            runs
                .flatMapIndexed { index, run ->
                    // A run owns the text up to the next run, including any the lexer discarded,
                    // so it sees the lexer errors there.
                    val start = run.first().startIndex
                    val end = runs.getOrNull(index + 1)?.first()?.startIndex ?: text.length
                    parseDeclaration(
                        run,
                        lexerProblems.filter { it.locator.startIndex in start until end },
                    )
                }
                .map { parsed ->
                    problems += parsed.problems
                    buildLibrary(parsed.context)
                }
        reportOutOfOrderDeclarations(parts)
        val header = parts.firstOrNull { it.name != null }
        val library =
            Library(
                name = header?.name,
                version = header?.version,
                definitions = parts.flatMap { it.definitions },
                statements = parts.flatMap { it.statements },
                locator = libraryLocator(lexed.tokens),
            )
        return LibraryResult(library, problems.toList())
    }

    /**
     * The extent the grammar's `library` rule gives: from the first token on the default channel
     * (EOF for an empty library) to the last token of any channel before EOF.
     */
    private fun libraryLocator(tokens: List<Token>): Locator {
        val first = tokens.first { it.channel == Token.DEFAULT_CHANNEL }
        val last = tokens.lastOrNull { it.type != Token.EOF }
        return Locator(
            sourceId = sourceId,
            startIndex = first.startIndex,
            stopIndex = last?.stopIndex ?: -1,
            line = first.line,
            column = first.charPositionInLine,
        )
    }

    private class ParsedLibrary(val context: cqlParser.LibraryContext, val problems: List<Problem>)

    private fun parseLibrary(tokens: List<Token>): ParsedLibrary {
        val parser = cqlParser(CommonTokenStream(ListTokenSource(tokens)))
        val listener = ProblemCollector()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)
        return ParsedLibrary(parser.library(), listener.problems)
    }

    /**
     * Parses the tokens of one declaration. If they have errors, including [lexerProblems] within
     * them, but a part ending at a line break before the first error parses cleanly, that part is
     * the declaration and the rest is parsed on its own. A damaged declaration keyword (`defi`,
     * `de"fine`) leaves no boundary to split at, so this keeps its remains from breaking the
     * declaration before it.
     */
    private fun parseDeclaration(
        run: List<Token>,
        lexerProblems: List<Problem>,
    ): List<ParsedLibrary> {
        val parsed = parseLibrary(run)
        val errors = parsed.problems + lexerProblems
        if (errors.isEmpty()) return listOf(parsed)
        // A problem without a position is taken to be at the end, so the prefixes are still tried.
        val firstError =
            errors.map { it.locator.startIndex }.filter { it >= 0 }.minOrNull() ?: Int.MAX_VALUE
        StatementBoundaries.lineStarts(run)
            .filter { run[it].startIndex <= firstError }
            .takeLast(MAX_PREFIX_ATTEMPTS)
            .asReversed()
            .forEach { end ->
                val prefix = parseLibrary(run.subList(0, end))
                if (prefix.problems.isEmpty() && prefix.context.declaresAnything()) {
                    return listOf(prefix, parseLibrary(run.subList(end, run.size)))
                }
            }
        return listOf(parsed)
    }

    private fun cqlParser.LibraryContext.declaresAnything(): Boolean =
        libraryDefinition() != null || definition().isNotEmpty() || statement().isNotEmpty()

    /**
     * Parsing declarations separately loses the grammar's ordering rule (`library`, then `using`,
     * `include`, terminology and parameters, then `context` and `define`), so it is checked here.
     */
    private fun reportOutOfOrderDeclarations(parts: List<Library>) {
        val nonEmpty =
            parts.filter {
                it.name != null || it.definitions.isNotEmpty() || it.statements.isNotEmpty()
            }
        nonEmpty
            .drop(1)
            .filter { it.name != null }
            .forEach { problems += Problem("The library declaration must come first", it.locator) }
        val firstStatement =
            nonEmpty.flatMap { it.statements }.minOfOrNull { it.locator.startIndex } ?: return
        nonEmpty
            .flatMap { it.definitions }
            .filter { it.locator.startIndex > firstStatement }
            .forEach {
                problems +=
                    Problem(
                        "using, include, terminology and parameter definitions must come " +
                            "before context and define statements",
                        it.locator,
                    )
            }
    }

    fun parseExpression(text: String): ExpressionResult {
        problems.clear()
        val parser = createParser(text)
        val listener = ProblemCollector()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)
        val expressionContext = parser.expression()
        problems.addAll(listener.problems)
        val expression = buildExpression(expressionContext)
        val trailing = parser.currentToken
        if (trailing != null && trailing.type != Token.EOF) {
            problems +=
                Problem(
                    message = "Unexpected input after expression: '${trailing.text}'",
                    locator = trailing.toLocator(),
                )
        }
        return ExpressionResult(expression, problems.toList())
    }

    private fun buildLibrary(ctx: cqlParser.LibraryContext): Library {
        val libraryDefinition = ctx.libraryDefinition()
        // An incomplete header leaves the library unnamed rather than discarding its contents.
        val header =
            libraryDefinition?.let { header ->
                recover(header, { null }) {
                    header.requireComplete()
                    header.qualifiedIdentifier().toQualifiedIdentifier() to
                        header.versionSpecifier()?.toVersionSpecifier()
                }
            }
        val libraryName = header?.first
        val libraryVersion = header?.second
        val definitions = ctx.definition().map { buildDefinition(it) }
        val statements = ctx.statement().map { buildStatement(it) }
        return Library(
            name = libraryName,
            version = libraryVersion,
            definitions = definitions,
            statements = statements,
            locator = ctx.toLocator(),
        )
    }

    private fun buildDefinition(ctx: cqlParser.DefinitionContext): Definition =
        recover(ctx, ::incompleteDefinition) {
            ctx.requireComplete()
            when {
                ctx.usingDefinition() != null -> buildUsingDefinition(ctx.usingDefinition()!!)
                ctx.includeDefinition() != null -> buildIncludeDefinition(ctx.includeDefinition()!!)
                ctx.parameterDefinition() != null ->
                    buildParameterDefinition(ctx.parameterDefinition()!!)

                ctx.codesystemDefinition() != null ->
                    buildCodeSystemDefinition(ctx.codesystemDefinition()!!)

                ctx.valuesetDefinition() != null ->
                    buildValueSetDefinition(ctx.valuesetDefinition()!!)
                ctx.codeDefinition() != null -> buildCodeDefinition(ctx.codeDefinition()!!)
                ctx.conceptDefinition() != null -> buildConceptDefinition(ctx.conceptDefinition()!!)
                else -> unsupportedDefinition("definition", ctx)
            }
        }

    private fun buildUsingDefinition(ctx: cqlParser.UsingDefinitionContext): Definition {
        ctx.requireComplete()
        return UsingDefinition(
            modelIdentifier = ctx.qualifiedIdentifier().toQualifiedIdentifier(),
            version = ctx.versionSpecifier()?.toVersionSpecifier(),
            alias = ctx.localIdentifier()?.requireComplete()?.identifier()?.toIdentifier(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildIncludeDefinition(ctx: cqlParser.IncludeDefinitionContext): Definition {
        ctx.requireComplete()
        return IncludeDefinition(
            libraryIdentifier = ctx.qualifiedIdentifier().toQualifiedIdentifier(),
            version = ctx.versionSpecifier()?.toVersionSpecifier(),
            alias = ctx.localIdentifier()?.requireComplete()?.identifier()?.toIdentifier(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildCodeSystemDefinition(ctx: cqlParser.CodesystemDefinitionContext): Definition {
        ctx.requireComplete()
        return CodeSystemDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            id = ctx.codesystemId().text.unquote(),
            version = ctx.versionSpecifier()?.toVersionSpecifier(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildValueSetDefinition(ctx: cqlParser.ValuesetDefinitionContext): Definition {
        ctx.requireComplete()
        val codesystems =
            ctx.codesystems()?.codesystemIdentifier().orEmpty().map { it.toTerminologyReference() }
        return ValueSetDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            id = ctx.valuesetId().text.unquote(),
            version = ctx.versionSpecifier()?.toVersionSpecifier(),
            codesystems = codesystems,
            locator = ctx.toLocator(),
        )
    }

    private fun buildCodeDefinition(ctx: cqlParser.CodeDefinitionContext): Definition {
        ctx.requireComplete()
        return CodeDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            id = ctx.codeId().text.unquote(),
            system = ctx.codesystemIdentifier().toTerminologyReference(),
            display = ctx.displayClause()?.toDisplay(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildConceptDefinition(ctx: cqlParser.ConceptDefinitionContext): Definition {
        ctx.requireComplete()
        val codes =
            ctx.codeIdentifier().map {
                it.requireComplete()
                TerminologyReference(
                    identifier = it.identifier().toIdentifier(),
                    libraryName =
                        it.libraryIdentifier()?.requireComplete()?.identifier()?.toIdentifier(),
                    locator = it.toLocator(),
                )
            }
        return ConceptDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            codes = codes,
            display = ctx.displayClause()?.toDisplay(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildParameterDefinition(ctx: cqlParser.ParameterDefinitionContext): Definition {
        ctx.requireComplete()
        return ParameterDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            type = ctx.typeSpecifier()?.let { buildTypeSpecifier(it) },
            default = ctx.expression()?.let { buildExpression(it) },
            locator = ctx.toLocator(),
        )
    }

    private fun buildStatement(ctx: cqlParser.StatementContext): Statement =
        recover(ctx, ::incompleteStatement) {
            ctx.requireComplete()
            when {
                ctx.contextDefinition() != null -> buildContextDefinition(ctx.contextDefinition()!!)
                ctx.expressionDefinition() != null ->
                    buildExpressionDefinition(ctx.expressionDefinition()!!)

                ctx.functionDefinition() != null ->
                    buildFunctionDefinition(ctx.functionDefinition()!!)
                else ->
                    UnsupportedStatement(grammarRule = "statement", locator = ctx.toLocator())
                        .also {
                            problems +=
                                Problem(
                                    message = "Unsupported statement: ${ctx.text}",
                                    locator = ctx.toLocator(),
                                )
                        }
            }
        }

    private fun buildContextDefinition(ctx: cqlParser.ContextDefinitionContext): Statement {
        ctx.requireComplete()
        return ContextDefinition(
            model = ctx.modelIdentifier()?.requireComplete()?.identifier()?.toIdentifier(),
            context = ctx.identifier().toIdentifier(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildExpressionDefinition(ctx: cqlParser.ExpressionDefinitionContext): Statement {
        ctx.requireComplete()
        return ExpressionDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            expression = buildExpression(ctx.expression()),
            locator = ctx.toLocator(),
        )
    }

    private fun buildFunctionDefinition(ctx: cqlParser.FunctionDefinitionContext): Statement {
        ctx.requireComplete()
        val operands =
            ctx.operandDefinition().map {
                it.requireComplete()
                OperandDefinition(
                    name = it.referentialIdentifier().toIdentifier(),
                    type = buildTypeSpecifier(it.typeSpecifier()),
                    locator = it.toLocator(),
                )
            }
        val functionBody = ctx.functionBody()
        val body =
            when {
                functionBody == null -> ExternalFunctionBody(ctx.toLocator())
                else ->
                    ExpressionFunctionBody(
                        expression = buildExpression(functionBody.requireComplete().expression()),
                        locator = functionBody.toLocator(),
                    )
            }
        return FunctionDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            fluent = ctx.fluentModifier() != null,
            name = ctx.identifierOrFunctionIdentifier().toIdentifier(),
            operands = operands,
            returnType = ctx.typeSpecifier()?.let { buildTypeSpecifier(it) },
            body = body,
            locator = ctx.toLocator(),
        )
    }

    private fun buildTypeSpecifier(ctx: cqlParser.TypeSpecifierContext): TypeSpecifier {
        ctx.requireComplete()
        return when {
            ctx.namedTypeSpecifier() != null ->
                NamedTypeSpecifier(
                    name = ctx.namedTypeSpecifier()!!.toQualifiedIdentifier(),
                    locator = ctx.namedTypeSpecifier()!!.toLocator(),
                )

            ctx.listTypeSpecifier() != null ->
                ListTypeSpecifier(
                    elementType =
                        buildTypeSpecifier(
                            ctx.listTypeSpecifier()!!.requireComplete().typeSpecifier()
                        ),
                    locator = ctx.listTypeSpecifier()!!.toLocator(),
                )

            ctx.intervalTypeSpecifier() != null ->
                IntervalTypeSpecifier(
                    pointType =
                        buildTypeSpecifier(
                            ctx.intervalTypeSpecifier()!!.requireComplete().typeSpecifier()
                        ),
                    locator = ctx.intervalTypeSpecifier()!!.toLocator(),
                )

            ctx.tupleTypeSpecifier() != null -> {
                val tuple = ctx.tupleTypeSpecifier()!!
                val elements =
                    tuple.tupleElementDefinition().map { element ->
                        element.requireComplete()
                        TupleElement(
                            name = element.referentialIdentifier().toIdentifier(),
                            type = buildTypeSpecifier(element.typeSpecifier()),
                            locator = element.toLocator(),
                        )
                    }
                TupleTypeSpecifier(elements = elements, locator = tuple.toLocator())
            }

            ctx.choiceTypeSpecifier() != null -> {
                val choice = ctx.choiceTypeSpecifier()!!
                val choices = choice.typeSpecifier().map { spec -> buildTypeSpecifier(spec) }
                ChoiceTypeSpecifier(choices = choices, locator = choice.toLocator())
            }

            else -> {
                problems += Problem("Unsupported type specifier: ${ctx.text}", ctx.toLocator())
                NamedTypeSpecifier(
                    name = QualifiedIdentifier(listOf(Identifier("Any"))),
                    locator = ctx.toLocator(),
                )
            }
        }
    }

    private fun buildExpression(ctx: cqlParser.ExpressionContext): Expression =
        recover(ctx, ::incompleteExpression) {
            ctx.requireComplete()
            when (ctx) {
                is cqlParser.TermExpressionContext -> buildExpressionTerm(ctx.expressionTerm())
                is cqlParser.RetrieveExpressionContext -> buildRetrieve(ctx.retrieve())
                is cqlParser.QueryExpressionContext -> buildQuery(ctx.query())
                is cqlParser.BooleanExpressionContext -> buildBooleanExpression(ctx)
                is cqlParser.EqualityExpressionContext -> buildEqualityExpression(ctx)

                is cqlParser.InequalityExpressionContext -> buildInequalityExpression(ctx)
                is cqlParser.TimingExpressionContext -> buildTimingExpression(ctx)
                is cqlParser.InFixSetExpressionContext -> buildInfixSetExpression(ctx)
                is cqlParser.MembershipExpressionContext -> buildMembershipExpression(ctx)
                is cqlParser.BetweenExpressionContext -> buildBetweenExpression(ctx)
                is cqlParser.DurationBetweenExpressionContext -> buildDurationBetweenExpression(ctx)
                is cqlParser.DifferenceBetweenExpressionContext ->
                    buildDifferenceBetweenExpression(ctx)
                is cqlParser.ExistenceExpressionContext ->
                    ExistsExpression(buildExpression(ctx.expression()), ctx.toLocator())

                is cqlParser.AndExpressionContext ->
                    buildBinaryExpression(
                        BinaryOperator.AND,
                        ctx.expression(0),
                        ctx.expression(1),
                        ctx,
                    )

                is cqlParser.OrExpressionContext -> buildOrExpression(ctx)

                is cqlParser.ImpliesExpressionContext ->
                    buildBinaryExpression(
                        BinaryOperator.IMPLIES,
                        ctx.expression(0),
                        ctx.expression(1),
                        ctx,
                    )

                is cqlParser.CastExpressionContext ->
                    CastExpression(
                        operand = buildExpression(ctx.expression()),
                        type = buildTypeSpecifier(ctx.typeSpecifier()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.TypeExpressionContext -> buildTypeExpression(ctx)
                is cqlParser.NotExpressionContext ->
                    OperatorUnaryExpression(
                        operator = UnaryOperator.NOT,
                        operand = buildExpression(ctx.expression()),
                        locator = ctx.toLocator(),
                    )

                else -> unsupportedExpression("expression", ctx)
            }
        }

    private fun buildBooleanExpression(ctx: cqlParser.BooleanExpressionContext): Expression {
        ctx.requireComplete()
        val left = buildExpression(ctx.expression())
        // `expression 'is' 'not'? ('null' | 'true' | 'false')`: the keywords are this rule's own
        // tokens, so they are never confused with an operand that is itself `true`, etc.
        val keywords = ctx.children.orEmpty().filterIsInstance<TerminalNode>().map { it.text }
        val kind =
            when (keywords.lastOrNull()) {
                "null" -> BooleanTestKind.NULL
                "true" -> BooleanTestKind.TRUE
                "false" -> BooleanTestKind.FALSE
                else -> return unsupportedExpression("booleanExpression", ctx)
            }
        return BooleanTestExpression(
            operand = left,
            booleanTestKind = kind,
            negated = "not" in keywords,
            locator = ctx.toLocator(),
        )
    }

    private fun buildEqualityExpression(ctx: cqlParser.EqualityExpressionContext): Expression {
        ctx.requireComplete()
        val operatorToken =
            ctx.children
                ?.map { it.text }
                ?.firstOrNull { it == "=" || it == "!=" || it == "~" || it == "!~" }
        val operator =
            when (operatorToken) {
                "=" -> BinaryOperator.EQUALS
                "!=" -> BinaryOperator.NOT_EQUALS
                "~" -> BinaryOperator.EQUIVALENT
                "!~" -> BinaryOperator.NOT_EQUIVALENT
                else -> BinaryOperator.EQUALS
            }
        return buildBinaryExpression(operator, ctx.expression(0), ctx.expression(1), ctx)
    }

    private fun buildInequalityExpression(ctx: cqlParser.InequalityExpressionContext): Expression {
        ctx.requireComplete()
        val operatorToken = ctx.getChild(1)?.text
        val operator =
            when (operatorToken) {
                "<" -> BinaryOperator.LT
                "<=" -> BinaryOperator.LTE
                ">" -> BinaryOperator.GT
                ">=" -> BinaryOperator.GTE
                else -> BinaryOperator.LT
            }
        return buildBinaryExpression(operator, ctx.expression(0), ctx.expression(1), ctx)
    }

    private fun buildOrExpression(ctx: cqlParser.OrExpressionContext): Expression {
        ctx.requireComplete()
        val operatorToken =
            ctx.children?.map { it.text.lowercase() }?.firstOrNull { it == "or" || it == "xor" }
        val operator =
            when (operatorToken) {
                "xor" -> BinaryOperator.XOR
                else -> BinaryOperator.OR
            }
        return buildBinaryExpression(operator, ctx.expression(0), ctx.expression(1), ctx)
    }

    private fun buildBinaryExpression(
        operator: BinaryOperator,
        leftCtx: cqlParser.ExpressionContext?,
        rightCtx: cqlParser.ExpressionContext?,
        parent: ParserRuleContext,
    ): Expression {
        val left =
            leftCtx?.let { buildExpression(it) } ?: unsupportedExpression("expression", parent)
        val right =
            rightCtx?.let { buildExpression(it) } ?: unsupportedExpression("expression", parent)
        return OperatorBinaryExpression(
            operator = operator,
            left = left,
            right = right,
            locator = parent.toLocator(),
        )
    }

    private fun buildExpressionTerm(ctx: cqlParser.ExpressionTermContext): Expression =
        recover(ctx, ::incompleteExpression) {
            ctx.requireComplete()
            when (ctx) {
                is cqlParser.TermExpressionTermContext -> buildTerm(ctx.term())
                is cqlParser.InvocationExpressionTermContext -> buildInvocationExpression(ctx)
                is cqlParser.IndexedExpressionTermContext ->
                    IndexExpression(
                        target = buildExpressionTerm(ctx.expressionTerm()),
                        index = buildExpression(ctx.expression()),
                        locator = ctx.toLocator(),
                    )
                is cqlParser.PolarityExpressionTermContext -> {
                    val symbol = ctx.getChild(0)?.text
                    val operator =
                        if (symbol == "-") UnaryOperator.NEGATE else UnaryOperator.POSITIVE
                    if (operator == UnaryOperator.NEGATE) {
                        negativeNumberLiteral(ctx)?.let {
                            return it
                        }
                    }
                    OperatorUnaryExpression(
                        operator = operator,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.AdditionExpressionTermContext -> {
                    val operator =
                        when (ctx.getChild(1)?.text?.lowercase()) {
                            "+" -> BinaryOperator.ADD
                            "-" -> BinaryOperator.SUBTRACT
                            "&" -> BinaryOperator.CONCAT
                            else -> return unsupportedExpression("additionExpressionTerm", ctx)
                        }
                    OperatorBinaryExpression(
                        operator = operator,
                        left = buildExpressionTerm(ctx.expressionTerm(0)!!),
                        right = buildExpressionTerm(ctx.expressionTerm(1)!!),
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.MultiplicationExpressionTermContext -> {
                    val opText = ctx.getChild(1)?.text?.lowercase()
                    val operator =
                        when (opText) {
                            "*" -> BinaryOperator.MULTIPLY
                            "/" -> BinaryOperator.DIVIDE
                            "div" -> BinaryOperator.TRUNCATED_DIVIDE
                            "mod" -> BinaryOperator.MODULO
                            else ->
                                return unsupportedExpression("multiplicationExpressionTerm", ctx)
                        }
                    OperatorBinaryExpression(
                        operator = operator,
                        left = buildExpressionTerm(ctx.expressionTerm(0)!!),
                        right = buildExpressionTerm(ctx.expressionTerm(1)!!),
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.PowerExpressionTermContext ->
                    OperatorBinaryExpression(
                        operator = BinaryOperator.POWER,
                        left = buildExpressionTerm(ctx.expressionTerm(0)!!),
                        right = buildExpressionTerm(ctx.expressionTerm(1)!!),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.DurationExpressionTermContext ->
                    DurationOfExpression(
                        precision = ctx.pluralDateTimePrecision().text,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.DifferenceExpressionTermContext ->
                    DifferenceOfExpression(
                        precision = ctx.pluralDateTimePrecision().text,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.WidthExpressionTermContext ->
                    WidthExpression(
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.SuccessorExpressionTermContext ->
                    OperatorUnaryExpression(
                        operator = UnaryOperator.SUCCESSOR,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.PredecessorExpressionTermContext ->
                    OperatorUnaryExpression(
                        operator = UnaryOperator.PREDECESSOR,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.ElementExtractorExpressionTermContext -> {
                    ElementExtractorExpression(
                        elementExtractorKind = ElementExtractorKind.SINGLETON,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.PointExtractorExpressionTermContext ->
                    ElementExtractorExpression(
                        elementExtractorKind = ElementExtractorKind.POINT,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.TypeExtentExpressionTermContext -> {
                    val typeExtentKind =
                        when (ctx.getChild(0)?.text?.lowercase()) {
                            "minimum" -> TypeExtentKind.MINIMUM
                            "maximum" -> TypeExtentKind.MAXIMUM
                            else -> return unsupportedExpression("typeExtentExpressionTerm", ctx)
                        }
                    val typeContext = ctx.namedTypeSpecifier()
                    val namedType =
                        NamedTypeSpecifier(
                            name = typeContext.toQualifiedIdentifier(),
                            locator = typeContext.toLocator(),
                        )
                    TypeExtentExpression(
                        typeExtentKind = typeExtentKind,
                        type = namedType,
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.ConversionExpressionTermContext -> {
                    val operand = buildExpression(ctx.expression())
                    val type = ctx.typeSpecifier()?.let { buildTypeSpecifier(it) }
                    val unit = ctx.unit()?.toUnitString()
                    ConversionExpression(
                        operand = operand,
                        destinationType = type,
                        destinationUnit = unit,
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.TimeBoundaryExpressionTermContext -> {
                    val timeBoundaryKind =
                        when (ctx.getChild(0)?.text?.lowercase()) {
                            "start" -> TimeBoundaryKind.START
                            "end" -> TimeBoundaryKind.END
                            else -> return unsupportedExpression("timeBoundaryExpressionTerm", ctx)
                        }
                    TimeBoundaryExpression(
                        timeBoundaryKind = timeBoundaryKind,
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )
                }
                is cqlParser.TimeUnitExpressionTermContext ->
                    DateTimeComponentExpression(
                        component = ctx.dateTimeComponent().toDateTimeComponent(),
                        operand = buildExpressionTerm(ctx.expressionTerm()),
                        locator = ctx.toLocator(),
                    )

                is cqlParser.IfThenElseExpressionTermContext -> {
                    val condition = buildExpression(ctx.expression(0)!!)
                    val thenExpr = buildExpression(ctx.expression(1)!!)
                    val elseExpr = buildExpression(ctx.expression(2)!!)
                    IfExpression(
                        condition = condition,
                        thenBranch = thenExpr,
                        elseBranch = elseExpr,
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.CaseExpressionTermContext -> buildCaseExpression(ctx)

                is cqlParser.AggregateExpressionTermContext -> {
                    val keyword = ctx.getChild(0)?.text?.lowercase()
                    val listTransformKind =
                        when (keyword) {
                            "distinct" -> ListTransformKind.DISTINCT
                            "flatten" -> ListTransformKind.FLATTEN
                            else -> return unsupportedExpression("aggregateExpressionTerm", ctx)
                        }
                    ListTransformExpression(
                        listTransformKind = listTransformKind,
                        operand = buildExpression(ctx.expression()),
                        locator = ctx.toLocator(),
                    )
                }

                is cqlParser.SetAggregateExpressionTermContext -> {
                    val keyword = ctx.getChild(0)?.text?.lowercase()
                    val expandCollapseKind =
                        when (keyword) {
                            "expand" -> ExpandCollapseKind.EXPAND
                            "collapse" -> ExpandCollapseKind.COLLAPSE
                            else -> return unsupportedExpression("setAggregateExpressionTerm", ctx)
                        }
                    val precision = ctx.dateTimePrecision()?.text
                    val perExpression =
                        ctx.expression().drop(1).firstOrNull()?.let { buildExpression(it) }
                    ExpandCollapseExpression(
                        expandCollapseKind = expandCollapseKind,
                        operand = buildExpression(ctx.expression(0)!!),
                        perPrecision = precision,
                        perExpression = perExpression,
                        locator = ctx.toLocator(),
                    )
                }

                else -> unsupportedExpression("expressionTerm", ctx)
            }
        }

    private fun buildInvocationExpression(
        ctx: cqlParser.InvocationExpressionTermContext
    ): Expression {
        ctx.requireComplete()
        val target = buildExpressionTerm(ctx.expressionTerm())
        return when (val invocation = ctx.qualifiedInvocation()) {
            is cqlParser.QualifiedMemberInvocationContext ->
                PropertyAccessExpression(
                    target = target,
                    property = invocation.referentialIdentifier().toIdentifier(),
                    locator = invocation.toLocator(),
                )

            is cqlParser.QualifiedFunctionInvocationContext -> {
                val function = invocation.qualifiedFunction()
                val name = function.identifierOrFunctionIdentifier().toIdentifier()
                val arguments =
                    function.paramList()?.expression()?.map { buildExpression(it) }.orEmpty()
                FunctionCallExpression(
                    target = target,
                    function = name,
                    arguments = arguments,
                    locator = invocation.toLocator(),
                )
            }

            else -> unsupportedExpression("qualifiedInvocation", ctx)
        }
    }

    private fun buildTerm(ctx: cqlParser.TermContext): Expression {
        ctx.requireComplete()
        return when (ctx) {
            is cqlParser.LiteralTermContext -> buildLiteral(ctx.literal())
            is cqlParser.InvocationTermContext -> buildInvocation(ctx.invocation())
            is cqlParser.ParenthesizedTermContext ->
                ParenthesizedExpression(
                    expression = buildExpression(ctx.expression()),
                    locator = ctx.toLocator(),
                )
            is cqlParser.ListSelectorTermContext ->
                LiteralExpression(buildListLiteral(ctx.listSelector()), ctx.toLocator())

            is cqlParser.TupleSelectorTermContext ->
                LiteralExpression(buildTupleLiteral(ctx.tupleSelector()), ctx.toLocator())

            is cqlParser.InstanceSelectorTermContext ->
                LiteralExpression(buildInstanceLiteral(ctx.instanceSelector()), ctx.toLocator())

            is cqlParser.CodeSelectorTermContext ->
                LiteralExpression(buildCodeLiteral(ctx.codeSelector()), ctx.toLocator())

            is cqlParser.ConceptSelectorTermContext ->
                LiteralExpression(buildConceptLiteral(ctx.conceptSelector()), ctx.toLocator())

            is cqlParser.IntervalSelectorTermContext ->
                LiteralExpression(buildIntervalLiteral(ctx.intervalSelector()), ctx.toLocator())

            is cqlParser.ExternalConstantTermContext ->
                buildExternalConstant(ctx.externalConstant())

            else -> unsupportedExpression("term", ctx)
        }
    }

    private fun buildInvocation(ctx: cqlParser.InvocationContext): Expression {
        ctx.requireComplete()
        return when (ctx) {
            is cqlParser.FunctionInvocationContext -> {
                val arguments =
                    ctx.function().paramList()?.expression()?.map { buildExpression(it) }.orEmpty()
                FunctionCallExpression(
                    target = null,
                    function = ctx.function().referentialIdentifier().toIdentifier(),
                    arguments = arguments,
                    locator = ctx.toLocator(),
                )
            }

            is cqlParser.MemberInvocationContext ->
                IdentifierExpression(
                    name = ctx.referentialIdentifier().toQualifiedIdentifier(),
                    locator = ctx.toLocator(),
                )

            is cqlParser.ThisInvocationContext ->
                IdentifierExpression(name = ctx.toSpecialIdentifier(), locator = ctx.toLocator())

            is cqlParser.IndexInvocationContext ->
                IdentifierExpression(name = ctx.toSpecialIdentifier(), locator = ctx.toLocator())

            is cqlParser.TotalInvocationContext ->
                IdentifierExpression(name = ctx.toSpecialIdentifier(), locator = ctx.toLocator())

            else -> unsupportedExpression("invocation", ctx)
        }
    }

    private fun buildLiteral(ctx: cqlParser.LiteralContext): Expression {
        ctx.requireComplete()
        return when (ctx) {
            is cqlParser.NumberLiteralContext -> numberLiteral(ctx.NUMBER().text, ctx)

            is cqlParser.LongNumberLiteralContext -> longNumberLiteral(ctx.LONGNUMBER().text, ctx)

            is cqlParser.StringLiteralContext ->
                LiteralExpression(
                    literal = StringLiteral(ctx.STRING().text.unquote(), ctx.toLocator()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.BooleanLiteralContext ->
                LiteralExpression(
                    literal =
                        BooleanLiteral(ctx.text.equals("true", ignoreCase = true), ctx.toLocator()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.NullLiteralContext ->
                LiteralExpression(literal = NullLiteral(ctx.toLocator()), locator = ctx.toLocator())

            is cqlParser.DateLiteralContext ->
                LiteralExpression(
                    literal = DateTimeLiteral(ctx.DATE().text, ctx.toLocator()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.DateTimeLiteralContext ->
                LiteralExpression(
                    literal = DateTimeLiteral(ctx.DATETIME().text, ctx.toLocator()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.TimeLiteralContext ->
                LiteralExpression(
                    literal = TimeLiteral(ctx.TIME().text, ctx.toLocator()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.QuantityLiteralContext ->
                LiteralExpression(
                    literal = ctx.quantity().toQuantityLiteral(),
                    locator = ctx.toLocator(),
                )

            is cqlParser.RatioLiteralContext ->
                LiteralExpression(
                    literal = buildRatioLiteral(ctx.ratio()),
                    locator = ctx.toLocator(),
                )

            else -> unsupportedExpression("literal", ctx)
        }
    }

    private fun buildExternalConstant(ctx: cqlParser.ExternalConstantContext): Expression {
        ctx.requireComplete()
        val identifier = ctx.identifier()
        val keyword = ctx.keywordIdentifier()
        val stringToken = ctx.STRING()
        val name =
            when {
                identifier != null -> identifier.toIdentifier().value
                keyword != null -> keyword.text
                stringToken != null -> stringToken.text.unquote()
                else -> ctx.text.removePrefix("%")
            }
        return ExternalConstantExpression(name = name, locator = ctx.toLocator())
    }

    private fun cqlParser.VersionSpecifierContext.toVersionSpecifier(): VersionSpecifier =
        VersionSpecifier(this.text.unquote())

    private fun cqlParser.AccessModifierContext.toAccessModifier(): AccessModifier =
        when (text.lowercase()) {
            "public" -> AccessModifier.PUBLIC
            "private" -> AccessModifier.PRIVATE
            else -> AccessModifier.PUBLIC
        }

    private fun cqlParser.IdentifierContext.toIdentifier(): Identifier = toIdentifierAsWritten()

    /**
     * The identifier this rule's text spells: a plain name, keyword or function name as is, or a
     * quoted or delimited name without its quotes and with its escapes resolved.
     */
    private fun ParserRuleContext.toIdentifierAsWritten(): Identifier {
        val written = text
        val style =
            when {
                written.length < 2 -> IdentifierStyle.PLAIN
                written.startsWith('"') && written.endsWith('"') -> IdentifierStyle.QUOTED
                written.startsWith('`') && written.endsWith('`') -> IdentifierStyle.DELIMITED
                else -> IdentifierStyle.PLAIN
            }
        val value =
            if (style == IdentifierStyle.PLAIN) written
            else StringEscapeUtils.unescapeCql(written.substring(1, written.length - 1))
        return Identifier(value = value, style = style, locator = toLocator())
    }

    /** `$this`, `$index` or `$total`. */
    private fun ParserRuleContext.toSpecialIdentifier(): QualifiedIdentifier =
        QualifiedIdentifier(parts = listOf(toIdentifierAsWritten()), locator = toLocator())

    private fun cqlParser.QuantityContext.toQuantityLiteral(): QuantityLiteral =
        requireComplete().run {
            QuantityLiteral(
                value = NUMBER().text,
                unit = unit()?.toUnitString(),
                locator = toLocator(),
            )
        }

    private fun cqlParser.DisplayClauseContext.toDisplay(): String =
        requireComplete().STRING().text.unquote()

    private fun cqlParser.DateTimePrecisionSpecifierContext.toPrecision(): String =
        requireComplete().dateTimePrecision().text.lowercase()

    /** A UCUM unit string is unquoted; a calendar duration keyword is kept as written. */
    private fun cqlParser.UnitContext.toUnitString(): String = STRING()?.text?.unquote() ?: text

    private fun cqlParser.QualifiedIdentifierContext.toQualifiedIdentifier(): QualifiedIdentifier =
        requireComplete().run {
            QualifiedIdentifier(
                parts =
                    qualifier().map { it.toIdentifierAsWritten() } + identifier().toIdentifier(),
                locator = toLocator(),
            )
        }

    private fun cqlParser.DateTimeComponentContext.toDateTimeComponent(): DateTimeComponent =
        when (text.lowercase()) {
            "year" -> DateTimeComponent.YEAR
            "month" -> DateTimeComponent.MONTH
            "week" -> DateTimeComponent.WEEK
            "day" -> DateTimeComponent.DAY
            "hour" -> DateTimeComponent.HOUR
            "minute" -> DateTimeComponent.MINUTE
            "second" -> DateTimeComponent.SECOND
            "millisecond" -> DateTimeComponent.MILLISECOND
            "date" -> DateTimeComponent.DATE
            "time" -> DateTimeComponent.TIME
            "timezone" -> DateTimeComponent.TIMEZONE
            "timezoneoffset" -> DateTimeComponent.TIMEZONE_OFFSET
            else -> {
                problems += Problem("Unsupported dateTimeComponent: ${text}", toLocator())
                DateTimeComponent.DATE
            }
        }

    private fun cqlParser.NamedTypeSpecifierContext.toQualifiedIdentifier(): QualifiedIdentifier =
        requireComplete().run {
            QualifiedIdentifier(
                parts =
                    qualifier().map { it.toIdentifierAsWritten() } +
                        referentialOrTypeNameIdentifier().toIdentifierAsWritten(),
                locator = toLocator(),
            )
        }

    private fun cqlParser.ReferentialIdentifierContext.toIdentifier(): Identifier =
        toIdentifierAsWritten()

    private fun cqlParser.ReferentialIdentifierContext.toQualifiedIdentifier():
        QualifiedIdentifier =
        QualifiedIdentifier(parts = listOf(toIdentifier()), locator = toLocator())

    private fun cqlParser.IdentifierOrFunctionIdentifierContext.toIdentifier(): Identifier =
        toIdentifierAsWritten()

    private fun buildInfixSetExpression(ctx: cqlParser.InFixSetExpressionContext): Expression {
        ctx.requireComplete()
        val operator =
            when (ctx.getChild(1)?.text?.lowercase()) {
                "|",
                "union" -> BinaryOperator.UNION
                "intersect" -> BinaryOperator.INTERSECT
                "except" -> BinaryOperator.EXCEPT
                else -> return unsupportedExpression("inFixSetExpression", ctx)
            }
        return OperatorBinaryExpression(
            operator = operator,
            left = buildExpression(ctx.expression(0)!!),
            right = buildExpression(ctx.expression(1)!!),
            locator = ctx.toLocator(),
        )
    }

    private fun buildMembershipExpression(ctx: cqlParser.MembershipExpressionContext): Expression {
        ctx.requireComplete()
        val operatorToken =
            ctx.children
                ?.map { it.text.lowercase() }
                ?.firstOrNull { it == "in" || it == "contains" }
                ?: return unsupportedExpression("membershipExpression", ctx)
        val operator =
            when (operatorToken) {
                "in" -> MembershipOperator.IN
                "contains" -> MembershipOperator.CONTAINS
                else -> MembershipOperator.IN
            }
        val precision = ctx.dateTimePrecisionSpecifier()?.text?.trim()
        return MembershipExpression(
            operator = operator,
            precision = precision?.ifBlank { null },
            left = buildExpression(ctx.expression(0)!!),
            right = buildExpression(ctx.expression(1)!!),
            locator = ctx.toLocator(),
        )
    }

    private fun buildBetweenExpression(ctx: cqlParser.BetweenExpressionContext): Expression {
        ctx.requireComplete()
        return BetweenExpression(
            input = buildExpression(ctx.expression()),
            lower = buildExpressionTerm(ctx.expressionTerm(0)!!),
            upper = buildExpressionTerm(ctx.expressionTerm(1)!!),
            properly = ctx.children?.any { it.text.equals("properly", true) } == true,
            locator = ctx.toLocator(),
        )
    }

    private fun buildDurationBetweenExpression(
        ctx: cqlParser.DurationBetweenExpressionContext
    ): Expression {
        ctx.requireComplete()
        return DurationBetweenExpression(
            precision = ctx.pluralDateTimePrecision().text,
            lower = buildExpressionTerm(ctx.expressionTerm(0)!!),
            upper = buildExpressionTerm(ctx.expressionTerm(1)!!),
            locator = ctx.toLocator(),
        )
    }

    private fun buildDifferenceBetweenExpression(
        ctx: cqlParser.DifferenceBetweenExpressionContext
    ): Expression {
        ctx.requireComplete()
        return DifferenceBetweenExpression(
            precision = ctx.pluralDateTimePrecision().text,
            lower = buildExpressionTerm(ctx.expressionTerm(0)!!),
            upper = buildExpressionTerm(ctx.expressionTerm(1)!!),
            locator = ctx.toLocator(),
        )
    }

    private fun buildTimingExpression(ctx: cqlParser.TimingExpressionContext): Expression {
        ctx.requireComplete()
        return IntervalRelationExpression(
            left = buildExpression(ctx.expression(0)!!),
            phrase = buildIntervalOperatorPhrase(ctx.intervalOperatorPhrase()),
            right = buildExpression(ctx.expression(1)!!),
            locator = ctx.toLocator(),
        )
    }

    private fun buildTypeExpression(ctx: cqlParser.TypeExpressionContext): Expression {
        ctx.requireComplete()
        val keyword = ctx.getChild(1)?.text?.lowercase()
        return when (keyword) {
            "is" ->
                IsExpression(
                    operand = buildExpression(ctx.expression()),
                    type = buildTypeSpecifier(ctx.typeSpecifier()),
                    negated = ctx.children?.any { it.text.equals("not", true) } == true,
                    locator = ctx.toLocator(),
                )

            "as" ->
                AsExpression(
                    operand = buildExpression(ctx.expression()),
                    type = buildTypeSpecifier(ctx.typeSpecifier()),
                    locator = ctx.toLocator(),
                )

            else -> unsupportedExpression("typeExpression", ctx)
        }
    }

    private fun buildCaseExpression(ctx: cqlParser.CaseExpressionTermContext): Expression {
        ctx.requireComplete()
        val expressionList = ctx.expression()
        val items = ctx.caseExpressionItem()
        val comparand =
            if (expressionList.size > 1) buildExpression(expressionList.first()) else null
        val caseItems =
            items.map { item ->
                item.requireComplete()
                CaseItem(
                    condition = buildExpression(item.expression(0)!!),
                    result = buildExpression(item.expression(1)!!),
                    locator = item.toLocator(),
                )
            }
        val elseExpression = buildExpression(expressionList.last())
        return CaseExpression(
            comparand = comparand,
            cases = caseItems,
            elseResult = elseExpression,
            locator = ctx.toLocator(),
        )
    }

    private fun buildListLiteral(ctx: cqlParser.ListSelectorContext): ListLiteral {
        ctx.requireComplete()
        return ListLiteral(
            elements = ctx.expression().map { buildExpression(it) },
            elementType = ctx.typeSpecifier()?.let { buildTypeSpecifier(it) },
            locator = ctx.toLocator(),
        )
    }

    private fun buildTupleLiteral(ctx: cqlParser.TupleSelectorContext): TupleLiteral {
        ctx.requireComplete()
        val elements =
            ctx.tupleElementSelector().map {
                it.requireComplete()
                TupleElementValue(
                    name = it.referentialIdentifier().toIdentifier(),
                    expression = buildExpression(it.expression()),
                    locator = it.toLocator(),
                )
            }
        return TupleLiteral(elements = elements, locator = ctx.toLocator())
    }

    private fun buildInstanceLiteral(ctx: cqlParser.InstanceSelectorContext): InstanceLiteral {
        ctx.requireComplete()
        val typeContext = ctx.namedTypeSpecifier()
        val namedType =
            NamedTypeSpecifier(
                name = typeContext.toQualifiedIdentifier(),
                locator = typeContext.toLocator(),
            )
        val elements =
            ctx.instanceElementSelector().map {
                it.requireComplete()
                TupleElementValue(
                    name = it.referentialIdentifier().toIdentifier(),
                    expression = buildExpression(it.expression()),
                    locator = it.toLocator(),
                )
            }
        return InstanceLiteral(type = namedType, elements = elements, locator = ctx.toLocator())
    }

    private fun buildCodeLiteral(ctx: cqlParser.CodeSelectorContext): CodeLiteral {
        ctx.requireComplete()
        val code = ctx.STRING().text.unquote()
        val system = ctx.codesystemIdentifier().toTerminologyReference()
        val display = ctx.displayClause()?.toDisplay()
        return CodeLiteral(
            code = code,
            system = system,
            display = display,
            locator = ctx.toLocator(),
        )
    }

    private fun buildConceptLiteral(ctx: cqlParser.ConceptSelectorContext): ConceptLiteral {
        ctx.requireComplete()
        val codes = ctx.codeSelector().map { buildCodeLiteral(it) }
        val display = ctx.displayClause()?.toDisplay()
        return ConceptLiteral(codes = codes, display = display, locator = ctx.toLocator())
    }

    private fun buildIntervalLiteral(ctx: cqlParser.IntervalSelectorContext): IntervalLiteral {
        ctx.requireComplete()
        val lowerClosed = ctx.getChild(1)?.text == "["
        val upperClosed = ctx.getChild(ctx.childCount - 1)?.text == "]"
        val expressions = ctx.expression()
        val lower = buildExpression(expressions[0])
        val upper = buildExpression(expressions[1])
        return IntervalLiteral(
            lower = lower,
            upper = upper,
            lowerClosed = lowerClosed,
            upperClosed = upperClosed,
            locator = ctx.toLocator(),
        )
    }

    private fun buildRatioLiteral(ctx: cqlParser.RatioContext): RatioLiteral {
        ctx.requireComplete()
        return RatioLiteral(
            numerator = ctx.quantity(0)!!.toQuantityLiteral(),
            denominator = ctx.quantity(1)!!.toQuantityLiteral(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildRetrieve(ctx: cqlParser.RetrieveContext): RetrieveExpression {
        ctx.requireComplete()
        val contextIdentifier =
            ctx.contextIdentifier()
                ?.requireComplete()
                ?.qualifiedIdentifierExpression()
                ?.toQualifiedIdentifier()
        val typeSpecifier =
            NamedTypeSpecifier(
                name = ctx.namedTypeSpecifier().toQualifiedIdentifier(),
                locator = ctx.namedTypeSpecifier().toLocator(),
            )
        val codePath = ctx.codePath()?.requireComplete()?.simplePath()?.toQualifiedIdentifier()
        val comparator =
            ctx.codeComparator()?.text?.lowercase()?.let {
                when (it) {
                    "in" -> TerminologyComparator.IN
                    "=" -> TerminologyComparator.EQUALS
                    "~" -> TerminologyComparator.EQUIVALENT
                    else -> null
                }
            }
        val terminologyRestriction = ctx.terminology()?.let { buildTerminologyRestriction(it) }
        return RetrieveExpression(
            typeSpecifier = typeSpecifier,
            terminology = terminologyRestriction,
            context = contextIdentifier,
            codePath = codePath,
            comparator = comparator,
            locator = ctx.toLocator(),
        )
    }

    private fun buildQuery(ctx: cqlParser.QueryContext): QueryExpression {
        ctx.requireComplete()
        val sources = ctx.sourceClause().aliasedQuerySource().map { buildAliasedQuerySource(it) }
        val lets = ctx.letClause()?.letClauseItem()?.map { buildLetClauseItem(it) }.orEmpty()
        val inclusions = ctx.queryInclusionClause().map { buildInclusionClause(it) }
        val where = ctx.whereClause()?.requireComplete()?.expression()?.let { buildExpression(it) }
        val aggregate = ctx.aggregateClause()?.let { buildAggregateClauseNode(it) }
        val result = ctx.returnClause()?.let { buildReturnClause(it) }
        val sort = ctx.sortClause()?.let { buildSortClause(it) }
        return QueryExpression(
            sources = sources,
            lets = lets,
            inclusions = inclusions,
            where = where,
            aggregate = aggregate,
            result = result,
            sort = sort,
            locator = ctx.toLocator(),
        )
    }

    private fun buildAliasedQuerySource(
        ctx: cqlParser.AliasedQuerySourceContext
    ): AliasedQuerySource {
        ctx.requireComplete()
        return AliasedQuerySource(
            source = buildQuerySource(ctx.querySource()),
            alias = ctx.alias().identifier().toIdentifier(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildQuerySource(ctx: cqlParser.QuerySourceContext): QuerySource {
        ctx.requireComplete()
        ctx.retrieve()?.let {
            return buildRetrieve(it)
        }
        ctx.qualifiedIdentifierExpression()?.let {
            return ExpressionQuerySource(
                expression =
                    IdentifierExpression(
                        name = it.toQualifiedIdentifier(),
                        locator = it.toLocator(),
                    ),
                locator = ctx.toLocator(),
            )
        }
        ctx.expression()?.let {
            return ExpressionQuerySource(
                expression = buildExpression(it),
                locator = ctx.toLocator(),
            )
        }
        return ExpressionQuerySource(
            expression =
                LiteralExpression(
                    literal = NullLiteral(ctx.toLocator()),
                    locator = ctx.toLocator(),
                ),
            locator = ctx.toLocator(),
        )
    }

    private fun buildLetClauseItem(ctx: cqlParser.LetClauseItemContext): LetClauseItem {
        ctx.requireComplete()
        return LetClauseItem(
            identifier = ctx.identifier().toIdentifier(),
            expression = buildExpression(ctx.expression()),
            locator = ctx.toLocator(),
        )
    }

    private fun buildInclusionClause(
        ctx: cqlParser.QueryInclusionClauseContext
    ): QueryInclusionClause {
        ctx.requireComplete()
        return when {
            ctx.withClause() != null -> {
                val withClause = ctx.withClause()!!.requireComplete()
                WithClause(
                    source = buildAliasedQuerySource(withClause.aliasedQuerySource()),
                    condition = buildExpression(withClause.expression()),
                    locator = ctx.toLocator(),
                )
            }

            ctx.withoutClause() != null -> {
                val withoutClause = ctx.withoutClause()!!.requireComplete()
                WithoutClause(
                    source = buildAliasedQuerySource(withoutClause.aliasedQuerySource()),
                    condition = buildExpression(withoutClause.expression()),
                    locator = ctx.toLocator(),
                )
            }

            else -> unsupportedQueryInclusion(ctx)
        }
    }

    private fun unsupportedQueryInclusion(ctx: ParserRuleContext): QueryInclusionClause =
        WithoutClause(
            source =
                AliasedQuerySource(
                    source =
                        ExpressionQuerySource(
                            expression =
                                LiteralExpression(
                                    literal = NullLiteral(ctx.toLocator()),
                                    locator = ctx.toLocator(),
                                ),
                            locator = ctx.toLocator(),
                        ),
                    alias = Identifier("it"),
                    locator = ctx.toLocator(),
                ),
            condition =
                LiteralExpression(
                    literal = BooleanLiteral(true, ctx.toLocator()),
                    locator = ctx.toLocator(),
                ),
            locator = ctx.toLocator(),
        )

    private fun buildAggregateClauseNode(ctx: cqlParser.AggregateClauseContext): AggregateClause {
        ctx.requireComplete()
        val distinct = ctx.children?.any { it.text.equals("distinct", true) } == true
        val identifier = ctx.identifier().toIdentifier()
        val starting = ctx.startingClause()?.let { buildStartingExpression(it) }
        val expression = buildExpression(ctx.expression())
        return AggregateClause(
            distinct = distinct,
            identifier = identifier,
            starting = starting,
            expression = expression,
            locator = ctx.toLocator(),
        )
    }

    private fun buildStartingExpression(ctx: cqlParser.StartingClauseContext): Expression {
        ctx.requireComplete()
        return ctx.simpleLiteral()?.let { buildSimpleLiteralExpression(it) }
            ?: ctx.quantity()?.let {
                LiteralExpression(literal = it.toQuantityLiteral(), locator = it.toLocator())
            }
            ?: ctx.expression()?.let { buildExpression(it) }
            ?: LiteralExpression(literal = NullLiteral(ctx.toLocator()), locator = ctx.toLocator())
    }

    private fun buildReturnClause(ctx: cqlParser.ReturnClauseContext): ReturnClause {
        ctx.requireComplete()
        val allFlag = ctx.children?.any { it.text.equals("all", true) } == true
        val distinctFlag = ctx.children?.any { it.text.equals("distinct", true) } == true
        return ReturnClause(
            all = allFlag,
            distinct = distinctFlag,
            expression = buildExpression(ctx.expression()),
            locator = ctx.toLocator(),
        )
    }

    private fun buildSortClause(ctx: cqlParser.SortClauseContext): SortClause {
        ctx.requireComplete()
        return SortClause(
            items = ctx.sortByItem().map { buildSortByItem(it) },
            direction = ctx.sortDirection()?.let { buildSortDirection(it) },
            locator = ctx.toLocator(),
        )
    }

    private fun buildSortByItem(ctx: cqlParser.SortByItemContext): SortByItem {
        ctx.requireComplete()
        return SortByItem(
            expression = buildExpressionTerm(ctx.expressionTerm()),
            direction = ctx.sortDirection()?.let { buildSortDirection(it) },
            locator = ctx.toLocator(),
        )
    }

    private fun buildSortDirection(ctx: cqlParser.SortDirectionContext): SortDirection {
        ctx.requireComplete()
        return when (ctx.text) {
            "asc" -> SortDirection.ASC
            "ascending" -> SortDirection.ASCENDING
            "desc" -> SortDirection.DESC
            else -> SortDirection.DESCENDING
        }
    }

    private fun buildIntervalOperatorPhrase(
        ctx: cqlParser.IntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        return when (ctx) {
            is cqlParser.ConcurrentWithIntervalOperatorPhraseContext ->
                buildConcurrentIntervalPhrase(ctx)

            is cqlParser.IncludesIntervalOperatorPhraseContext -> buildIncludesIntervalPhrase(ctx)

            is cqlParser.IncludedInIntervalOperatorPhraseContext ->
                buildIncludedInIntervalPhrase(ctx)

            is cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ->
                buildBeforeOrAfterIntervalPhrase(ctx)

            is cqlParser.WithinIntervalOperatorPhraseContext -> buildWithinIntervalPhrase(ctx)
            is cqlParser.MeetsIntervalOperatorPhraseContext -> buildMeetsIntervalPhrase(ctx)
            is cqlParser.OverlapsIntervalOperatorPhraseContext -> buildOverlapsIntervalPhrase(ctx)
            is cqlParser.StartsIntervalOperatorPhraseContext -> buildStartsIntervalPhrase(ctx)
            is cqlParser.EndsIntervalOperatorPhraseContext -> buildEndsIntervalPhrase(ctx)
            else -> {
                problems +=
                    Problem("Unsupported interval operator phrase: ${ctx.text}", ctx.toLocator())
                UnsupportedIntervalPhrase(ctx.text, ctx.toLocator())
            }
        }
    }

    private fun buildConcurrentIntervalPhrase(
        ctx: cqlParser.ConcurrentWithIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        val leftBoundary = ctx.findLeadingBoundary()
        val precision = ctx.dateTimePrecision()?.text?.lowercase()
        val qualifier =
            ctx.relativeQualifier()?.text?.lowercase()?.let {
                when (it) {
                    "or before" -> ConcurrentQualifier.OR_BEFORE
                    "or after" -> ConcurrentQualifier.OR_AFTER
                    else -> ConcurrentQualifier.AS
                }
            } ?: ConcurrentQualifier.AS
        val rightBoundary = ctx.findTrailingBoundary()
        return ConcurrentIntervalPhrase(
            leftBoundary = leftBoundary,
            precision = precision,
            qualifier = qualifier,
            rightBoundary = rightBoundary,
            locator = ctx.toLocator(),
        )
    }

    private fun buildIncludesIntervalPhrase(
        ctx: cqlParser.IncludesIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        val tokens = ctx.children?.map { it.text.lowercase() }.orEmpty()
        val precision = ctx.dateTimePrecisionSpecifier()?.toPrecision()
        val rightBoundary = ctx.findTrailingBoundary()
        val proper = tokens.contains("properly")
        return IncludesIntervalPhrase(
            proper = proper,
            precision = precision,
            rightBoundary = rightBoundary,
            locator = ctx.toLocator(),
        )
    }

    private fun buildIncludedInIntervalPhrase(
        ctx: cqlParser.IncludedInIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        val tokens = ctx.children?.map { it.text.lowercase() }.orEmpty()
        val leftBoundary = ctx.findLeadingBoundary()
        val proper = tokens.contains("properly")
        val variant =
            when {
                tokens.contains("during") -> InclusionVariant.DURING
                else -> InclusionVariant.INCLUDED_IN
            }
        val precision = ctx.dateTimePrecisionSpecifier()?.toPrecision()
        return IncludedInIntervalPhrase(
            leftBoundary = leftBoundary,
            proper = proper,
            variant = variant,
            precision = precision,
            locator = ctx.toLocator(),
        )
    }

    private fun buildBeforeOrAfterIntervalPhrase(
        ctx: cqlParser.BeforeOrAfterIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        val leftBoundary = ctx.findLeadingBoundary()
        val offset = ctx.quantityOffset()?.let { buildQuantityOffset(it) }
        val relationship = buildTemporalRelationship(ctx.temporalRelationship())
        val precision = ctx.dateTimePrecisionSpecifier()?.toPrecision()
        val rightBoundary = ctx.findTrailingBoundary()
        return BeforeOrAfterIntervalPhrase(
            leftBoundary = leftBoundary,
            offset = offset,
            relationship = relationship,
            precision = precision,
            rightBoundary = rightBoundary,
            locator = ctx.toLocator(),
        )
    }

    private fun buildWithinIntervalPhrase(
        ctx: cqlParser.WithinIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        val tokens = ctx.children?.map { it.text.lowercase() }.orEmpty()
        val leftBoundary = ctx.findLeadingBoundary()
        val proper = tokens.contains("properly")
        val quantity = ctx.quantity().toQuantityLiteral()
        val rightBoundary = ctx.findTrailingBoundary()
        return WithinIntervalPhrase(
            leftBoundary = leftBoundary,
            proper = proper,
            quantity = quantity,
            rightBoundary = rightBoundary,
            locator = ctx.toLocator(),
        )
    }

    private fun buildMeetsIntervalPhrase(
        ctx: cqlParser.MeetsIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        val tokens = ctx.children?.map { it.text.lowercase() }.orEmpty()
        val direction =
            tokens.firstOrNull { it == "before" || it == "after" }?.toTemporalDirection()
        val precision = ctx.dateTimePrecisionSpecifier()?.toPrecision()
        return MeetsIntervalPhrase(
            direction = direction,
            precision = precision,
            locator = ctx.toLocator(),
        )
    }

    private fun buildOverlapsIntervalPhrase(
        ctx: cqlParser.OverlapsIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        val tokens = ctx.children?.map { it.text.lowercase() }.orEmpty()
        val direction =
            tokens.firstOrNull { it == "before" || it == "after" }?.toTemporalDirection()
        val precision = ctx.dateTimePrecisionSpecifier()?.toPrecision()
        return OverlapsIntervalPhrase(
            direction = direction,
            precision = precision,
            locator = ctx.toLocator(),
        )
    }

    private fun buildStartsIntervalPhrase(
        ctx: cqlParser.StartsIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        return StartsIntervalPhrase(
            precision = ctx.dateTimePrecisionSpecifier()?.toPrecision(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildEndsIntervalPhrase(
        ctx: cqlParser.EndsIntervalOperatorPhraseContext
    ): IntervalOperatorPhrase {
        ctx.requireComplete()
        return EndsIntervalPhrase(
            precision = ctx.dateTimePrecisionSpecifier()?.toPrecision(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildQuantityOffset(ctx: cqlParser.QuantityOffsetContext): QuantityOffset {
        ctx.requireComplete()
        val quantity =
            ctx.quantity()?.toQuantityLiteral() ?: QuantityLiteral(ctx.text, null, ctx.toLocator())
        val offsetQualifier =
            ctx.offsetRelativeQualifier()?.text?.lowercase()?.let {
                when (it) {
                    "or more" -> OffsetRelativeQualifier.OR_MORE
                    "or less" -> OffsetRelativeQualifier.OR_LESS
                    else -> null
                }
            }
        val exclusiveQualifier =
            ctx.exclusiveRelativeQualifier()?.text?.lowercase()?.let {
                when (it) {
                    "less than" -> ExclusiveRelativeQualifier.LESS_THAN
                    "more than" -> ExclusiveRelativeQualifier.MORE_THAN
                    else -> null
                }
            }
        return QuantityOffset(
            quantity = quantity,
            offsetQualifier = offsetQualifier,
            exclusiveQualifier = exclusiveQualifier,
            locator = ctx.toLocator(),
        )
    }

    private fun buildTemporalRelationship(
        ctx: cqlParser.TemporalRelationshipContext
    ): TemporalRelationshipPhrase {
        ctx.requireComplete()
        val tokens = ctx.children?.map { it.text.lowercase() }.orEmpty()
        val direction =
            tokens.firstOrNull { it == "before" || it == "after" }?.toTemporalDirection()
                ?: TemporalRelationshipDirection.BEFORE
        val inclusive = tokens.any { it == "on or" || it == "or on" }
        val qualifier =
            tokens
                .firstOrNull { it == "more than" || it == "less than" }
                ?.let {
                    when (it) {
                        "more than" -> ExclusiveRelativeQualifier.MORE_THAN
                        "less than" -> ExclusiveRelativeQualifier.LESS_THAN
                        else -> null
                    }
                }
        return TemporalRelationshipPhrase(
            direction = direction,
            inclusive = inclusive,
            leadingQualifier = qualifier,
            locator = ctx.toLocator(),
        )
    }

    private fun ParserRuleContext.findLeadingBoundary(): IntervalBoundarySelector? {
        val tokens = children?.map { it.text.lowercase() }.orEmpty()
        val keyword = tokens.firstOrNull { it == "starts" || it == "ends" || it == "occurs" }
        return keyword?.toIntervalBoundarySelector()
    }

    private fun ParserRuleContext.findTrailingBoundary(): IntervalBoundarySelector? {
        val tokens = children?.map { it.text.lowercase() }.orEmpty()
        val keyword = tokens.asReversed().firstOrNull { it == "start" || it == "end" }
        return keyword?.toIntervalBoundarySelector()
    }

    private fun String.toIntervalBoundarySelector(): IntervalBoundarySelector? =
        when (this.lowercase()) {
            "starts",
            "start" -> IntervalBoundarySelector.START
            "ends",
            "end" -> IntervalBoundarySelector.END
            "occurs" -> IntervalBoundarySelector.OCCURS
            else -> null
        }

    private fun String.toTemporalDirection(): TemporalRelationshipDirection? =
        when (this.lowercase()) {
            "before" -> TemporalRelationshipDirection.BEFORE
            "after" -> TemporalRelationshipDirection.AFTER
            else -> null
        }

    /**
     * A minus sign applied directly to a numeric literal is part of the literal, so that `-5` is
     * `IntLiteral(-5)` rather than `Negate(IntLiteral(5))`. This is what makes the minimum Integer
     * (`-2147483648`) and Long values representable, since their magnitudes are out of range.
     */
    private fun negativeNumberLiteral(ctx: cqlParser.PolarityExpressionTermContext): Expression? {
        val term = (ctx.expressionTerm() as? cqlParser.TermExpressionTermContext)?.term()
        return when (val literal = (term as? cqlParser.LiteralTermContext)?.literal()) {
            is cqlParser.NumberLiteralContext -> numberLiteral("-" + literal.NUMBER().text, ctx)
            is cqlParser.LongNumberLiteralContext ->
                longNumberLiteral("-" + literal.LONGNUMBER().text, ctx)
            else -> null
        }
    }

    private fun numberLiteral(text: String, ctx: ParserRuleContext): Expression {
        val literal =
            if (text.contains('.')) {
                DecimalLiteral(BigDecimal(text), ctx.toLocator())
            } else {
                IntLiteral(
                    text.toIntOrNull() ?: return outOfRange("Integer", text, ctx),
                    ctx.toLocator(),
                )
            }
        return LiteralExpression(literal = literal, locator = ctx.toLocator())
    }

    private fun longNumberLiteral(text: String, ctx: ParserRuleContext): Expression {
        val value = text.removeSuffix("L").toLongOrNull() ?: return outOfRange("Long", text, ctx)
        return LiteralExpression(LongLiteral(value, ctx.toLocator()), ctx.toLocator())
    }

    private fun outOfRange(type: String, text: String, ctx: ParserRuleContext): Expression {
        problems += Problem("$type literal out of range: $text", ctx.toLocator())
        return UnsupportedExpression(description = text, locator = ctx.toLocator())
    }

    private fun buildSimpleLiteralExpression(ctx: cqlParser.SimpleLiteralContext): Expression {
        ctx.requireComplete()
        return when (ctx) {
            is cqlParser.SimpleStringLiteralContext ->
                LiteralExpression(
                    literal = StringLiteral(ctx.STRING().text.unquote(), ctx.toLocator()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.SimpleNumberLiteralContext -> numberLiteral(ctx.NUMBER().text, ctx)

            else ->
                LiteralExpression(literal = NullLiteral(ctx.toLocator()), locator = ctx.toLocator())
        }
    }

    private fun buildTerminologyRestriction(
        ctx: cqlParser.TerminologyContext
    ): TerminologyRestriction {
        ctx.requireComplete()
        ctx.qualifiedIdentifierExpression()?.let { qualified ->
            val identifierExpression =
                IdentifierExpression(
                    name = qualified.toQualifiedIdentifier(),
                    locator = qualified.toLocator(),
                )
            return TerminologyRestriction(
                terminology = identifierExpression,
                locator = ctx.toLocator(),
            )
        }

        ctx.expression()?.let { expressionContext ->
            return TerminologyRestriction(
                terminology = buildExpression(expressionContext),
                locator = ctx.toLocator(),
            )
        }

        return TerminologyRestriction(
            terminology =
                LiteralExpression(
                    literal = NullLiteral(ctx.toLocator()),
                    locator = ctx.toLocator(),
                ),
            locator = ctx.toLocator(),
        )
    }

    private fun cqlParser.QualifiedIdentifierExpressionContext.toQualifiedIdentifier():
        QualifiedIdentifier =
        requireComplete().run {
            QualifiedIdentifier(
                parts =
                    qualifierExpression().map { it.referentialIdentifier().toIdentifier() } +
                        referentialIdentifier().toIdentifier(),
                locator = toLocator(),
            )
        }

    private fun cqlParser.SimplePathContext.toQualifiedIdentifier(): QualifiedIdentifier {
        requireComplete()
        return when (this) {
            is cqlParser.SimplePathReferentialIdentifierContext ->
                QualifiedIdentifier(
                    parts = listOf(referentialIdentifier().toIdentifier()),
                    locator = toLocator(),
                )

            is cqlParser.SimplePathQualifiedIdentifierContext ->
                QualifiedIdentifier(
                    parts =
                        simplePath().toQualifiedIdentifier().parts +
                            referentialIdentifier().toIdentifier(),
                    locator = toLocator(),
                )

            is cqlParser.SimplePathIndexerContext ->
                QualifiedIdentifier(
                    parts =
                        simplePath().toQualifiedIdentifier().parts +
                            Identifier(
                                value = simpleLiteral().text.unquote(),
                                locator = simpleLiteral().toLocator(),
                            ),
                    locator = toLocator(),
                )

            else ->
                QualifiedIdentifier(parts = listOf(toIdentifierAsWritten()), locator = toLocator())
        }
    }

    private fun cqlParser.CodesystemIdentifierContext.toTerminologyReference():
        TerminologyReference =
        requireComplete().run {
            TerminologyReference(
                identifier = identifier().toIdentifier(),
                libraryName = libraryIdentifier()?.requireComplete()?.identifier()?.toIdentifier(),
                locator = toLocator(),
            )
        }

    private fun ParserRuleContext.toLocator(): Locator =
        Locator(
            sourceId = sourceId,
            startIndex = start?.startIndex ?: -1,
            stopIndex = stop?.stopIndex ?: -1,
            line = start?.line,
            column = start?.charPositionInLine,
        )

    private fun Token.toLocator(): Locator =
        Locator(
            sourceId = sourceId,
            startIndex = this.startIndex,
            stopIndex = this.stopIndex,
            line = this.line,
            column = this.charPositionInLine,
        )

    /** Strips the delimiters from a `'string'` or `"identifier"` and processes its escapes. */
    private fun String.unquote(): String =
        when {
            length >= 2 && startsWith("'") && endsWith("'") ->
                StringEscapeUtils.unescapeCql(substring(1, length - 1))
            length >= 2 && startsWith("\"") && endsWith("\"") ->
                StringEscapeUtils.unescapeCql(substring(1, length - 1))
            else -> this
        }

    /**
     * When the parser cannot complete a rule it records the error on that rule's context
     * ([ParserRuleContext.exception]) and leaves the remaining children out. The generated
     * accessors for required children assert that they are present, so the Builder checks a context
     * before reading it and abandons the enclosing node if it is incomplete.
     */
    private fun <C : ParserRuleContext> C.requireComplete(): C {
        if (exception != null) throw IncompleteSyntax()
        return this
    }

    /**
     * Builds a node, replacing it with [fallback] if any part of it is incomplete. Expressions,
     * definitions and statements are the recovery points, so a syntax error costs only the smallest
     * enclosing one of those.
     */
    private inline fun <C : ParserRuleContext, T> recover(
        ctx: C,
        fallback: (C) -> T,
        build: () -> T,
    ): T =
        try {
            build()
        } catch (@Suppress("SwallowedException") _: IncompleteSyntax) {
            fallback(ctx)
        }

    // The parser has already reported the syntax error, so these add no Problem of their own.

    private fun incompleteExpression(ctx: ParserRuleContext): Expression =
        UnsupportedExpression(description = ctx.text, locator = ctx.toLocator())

    private fun incompleteDefinition(ctx: ParserRuleContext): Definition =
        UnsupportedDefinition(grammarRule = "definition", locator = ctx.toLocator())

    private fun incompleteStatement(ctx: ParserRuleContext): Statement =
        UnsupportedStatement(grammarRule = "statement", locator = ctx.toLocator())

    private fun unsupportedExpression(rule: String, ctx: ParserRuleContext): Expression {
        problems += Problem("Unsupported $rule: ${ctx.text}", ctx.toLocator())
        return UnsupportedExpression(description = ctx.text, locator = ctx.toLocator())
    }

    private fun unsupportedDefinition(rule: String, ctx: ParserRuleContext): Definition {
        problems += Problem("Unsupported $rule: ${ctx.text}", ctx.toLocator())
        return UnsupportedDefinition(grammarRule = rule, locator = ctx.toLocator())
    }

    private inner class ProblemCollector : BaseErrorListener() {
        val problems = mutableListOf<Problem>()

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?,
        ) {
            problems +=
                Problem(
                    message = msg,
                    locator =
                        Locator(
                            sourceId = sourceId,
                            startIndex = (offendingSymbol as? Token)?.startIndex ?: -1,
                            stopIndex = (offendingSymbol as? Token)?.stopIndex ?: -1,
                            line = line,
                            column = charPositionInLine,
                        ),
                )
        }
    }
}

/** How many line breaks [Builder] tries when looking for the clean part of a declaration. */
private const val MAX_PREFIX_ATTEMPTS = 8

/** Signals that the Builder reached a part of the parse tree the parser could not complete. */
private class IncompleteSyntax : RuntimeException()

/** Generates the AST for the given CQL in the JSON format. */
@JsExport
fun inspectCqlAst(text: String): String {
    val builder = Builder()
    val libraryResult = builder.parseLibrary(text)
    return Json.encodeToString(libraryResult)
}
