package org.hl7.cql.ast

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser

@Suppress("TooManyFunctions")
class CqlAstBuilder(private val sourceId: String? = null) {

    private val problems = mutableListOf<AstProblem>()

    fun parseLibrary(text: String): AstResult {
        val parser = createParser(text)
        val listener = ProblemCollector()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)
        val libraryContext = parser.library()
        problems.addAll(listener.problems)
        val library = buildLibrary(libraryContext)
        return AstResult(library, problems.toList())
    }

    private fun createParser(text: String): cqlParser {
        val input = CharStreams.fromString(text)
        val lexer = cqlLexer(input)
        val tokens = CommonTokenStream(lexer)
        return cqlParser(tokens)
    }

    private fun buildLibrary(ctx: cqlParser.LibraryContext): Library {
        val libraryDefinition = ctx.libraryDefinition()
        val libraryName = libraryDefinition?.qualifiedIdentifier()?.toQualifiedIdentifier()
        val libraryVersion = libraryDefinition?.versionSpecifier()?.toVersionSpecifier()
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
        when {
            ctx.usingDefinition() != null -> buildUsingDefinition(ctx.usingDefinition()!!)
            ctx.includeDefinition() != null -> buildIncludeDefinition(ctx.includeDefinition()!!)
            ctx.parameterDefinition() != null ->
                buildParameterDefinition(ctx.parameterDefinition()!!)

            ctx.codesystemDefinition() != null ->
                buildCodeSystemDefinition(ctx.codesystemDefinition()!!)

            ctx.valuesetDefinition() != null -> buildValueSetDefinition(ctx.valuesetDefinition()!!)
            ctx.codeDefinition() != null -> buildCodeDefinition(ctx.codeDefinition()!!)
            ctx.conceptDefinition() != null -> buildConceptDefinition(ctx.conceptDefinition()!!)
            else -> unsupportedDefinition("definition", ctx)
        }

    private fun buildUsingDefinition(ctx: cqlParser.UsingDefinitionContext): Definition =
        UsingDefinition(
            modelIdentifier = ctx.qualifiedIdentifier().toQualifiedIdentifier(),
            version = ctx.versionSpecifier()?.toVersionSpecifier(),
            alias = ctx.localIdentifier()?.identifier()?.toIdentifier(),
            locator = ctx.toLocator(),
        )

    private fun buildIncludeDefinition(ctx: cqlParser.IncludeDefinitionContext): Definition =
        IncludeDefinition(
            libraryIdentifier = ctx.qualifiedIdentifier().toQualifiedIdentifier(),
            version = ctx.versionSpecifier()?.toVersionSpecifier(),
            alias = ctx.localIdentifier()?.identifier()?.toIdentifier(),
            locator = ctx.toLocator(),
        )

    private fun buildCodeSystemDefinition(ctx: cqlParser.CodesystemDefinitionContext): Definition =
        CodeSystemDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            id = ctx.codesystemId().text.unquote(),
            version = ctx.versionSpecifier()?.toVersionSpecifier(),
            locator = ctx.toLocator(),
        )

    private fun buildValueSetDefinition(ctx: cqlParser.ValuesetDefinitionContext): Definition {
        val codesystems =
            ctx.codesystems()?.codesystemIdentifier().orEmpty().map {
                TerminologyReference(
                    identifier = it.identifier().toIdentifier(),
                    libraryName = it.libraryIdentifier()?.identifier()?.toIdentifier(),
                    locator = it.toLocator(),
                )
            }
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
        val terminology = ctx.codesystemIdentifier()
        val reference =
            TerminologyReference(
                identifier = terminology.identifier().toIdentifier(),
                libraryName = terminology.libraryIdentifier()?.identifier()?.toIdentifier(),
                locator = terminology.toLocator(),
            )
        return CodeDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            id = ctx.codeId().text.unquote(),
            system = reference,
            display = ctx.displayClause()?.STRING()?.text?.unquote(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildConceptDefinition(ctx: cqlParser.ConceptDefinitionContext): Definition {
        val codes =
            ctx.codeIdentifier().map {
                TerminologyReference(
                    identifier = it.identifier().toIdentifier(),
                    libraryName = it.libraryIdentifier()?.identifier()?.toIdentifier(),
                    locator = it.toLocator(),
                )
            }
        return ConceptDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            codes = codes,
            display = ctx.displayClause()?.STRING()?.text?.unquote(),
            locator = ctx.toLocator(),
        )
    }

    private fun buildParameterDefinition(ctx: cqlParser.ParameterDefinitionContext): Definition =
        ParameterDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            type = ctx.typeSpecifier()?.let { buildTypeSpecifier(it) },
            default = ctx.expression()?.let { buildExpression(it) },
            locator = ctx.toLocator(),
        )

    private fun buildStatement(ctx: cqlParser.StatementContext): Statement =
        when {
            ctx.contextDefinition() != null -> buildContextDefinition(ctx.contextDefinition()!!)
            ctx.expressionDefinition() != null ->
                buildExpressionDefinition(ctx.expressionDefinition()!!)

            ctx.functionDefinition() != null -> buildFunctionDefinition(ctx.functionDefinition()!!)
            else ->
                UnsupportedStatement(grammarRule = "statement", locator = ctx.toLocator()).also {
                    problems +=
                        AstProblem(
                            message = "Unsupported statement: ${ctx.text}",
                            locator = ctx.toLocator(),
                        )
                }
        }

    private fun buildContextDefinition(ctx: cqlParser.ContextDefinitionContext): Statement =
        ContextDefinition(
            model = ctx.modelIdentifier()?.identifier()?.toIdentifier(),
            context = ctx.identifier().toIdentifier(),
            locator = ctx.toLocator(),
        )

    private fun buildExpressionDefinition(ctx: cqlParser.ExpressionDefinitionContext): Statement =
        ExpressionDefinition(
            access = ctx.accessModifier()?.toAccessModifier(),
            name = ctx.identifier().toIdentifier(),
            expression = buildExpression(ctx.expression()),
            locator = ctx.toLocator(),
        )

    private fun buildFunctionDefinition(ctx: cqlParser.FunctionDefinitionContext): Statement {
        val operands =
            ctx.operandDefinition().map {
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
                        expression = buildExpression(functionBody.expression()),
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
        return when {
            ctx.namedTypeSpecifier() != null ->
                NamedTypeSpecifier(
                    name = ctx.namedTypeSpecifier()!!.toQualifiedIdentifier(),
                    locator = ctx.namedTypeSpecifier()!!.toLocator(),
                )

            ctx.listTypeSpecifier() != null ->
                ListTypeSpecifier(
                    elementType = buildTypeSpecifier(ctx.listTypeSpecifier()!!.typeSpecifier()),
                    locator = ctx.listTypeSpecifier()!!.toLocator(),
                )

            ctx.intervalTypeSpecifier() != null ->
                IntervalTypeSpecifier(
                    pointType = buildTypeSpecifier(ctx.intervalTypeSpecifier()!!.typeSpecifier()),
                    locator = ctx.intervalTypeSpecifier()!!.toLocator(),
                )

            ctx.tupleTypeSpecifier() != null -> {
                val tuple = ctx.tupleTypeSpecifier()!!
                val elements =
                    tuple.tupleElementDefinition().map { element ->
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
                problems += AstProblem("Unsupported type specifier: ${ctx.text}", ctx.toLocator())
                NamedTypeSpecifier(
                    name = QualifiedIdentifier(listOf("Any")),
                    locator = ctx.toLocator(),
                )
            }
        }
    }

    private fun buildExpression(ctx: cqlParser.ExpressionContext): Expression =
        when (ctx) {
            is cqlParser.TermExpressionContext -> buildExpressionTerm(ctx.expressionTerm())
            is cqlParser.RetrieveExpressionContext -> buildRetrieve(ctx.retrieve())
            is cqlParser.QueryExpressionContext -> buildQuery(ctx.query())
            is cqlParser.BooleanExpressionContext -> buildBooleanExpression(ctx)
            is cqlParser.EqualityExpressionContext ->
                buildBinaryExpression(
                    BinaryOperator.EQUALS,
                    ctx.expression(0),
                    ctx.expression(1),
                    ctx,
                )

            is cqlParser.InequalityExpressionContext -> buildInequalityExpression(ctx)
            is cqlParser.InFixSetExpressionContext -> buildInfixSetExpression(ctx)
            is cqlParser.MembershipExpressionContext -> buildMembershipExpression(ctx)
            is cqlParser.BetweenExpressionContext -> buildBetweenExpression(ctx)
            is cqlParser.DurationBetweenExpressionContext -> buildDurationBetweenExpression(ctx)
            is cqlParser.DifferenceBetweenExpressionContext -> buildDifferenceBetweenExpression(ctx)
            is cqlParser.ExistenceExpressionContext ->
                ExistsExpression(buildExpression(ctx.expression()), ctx.toLocator())

            is cqlParser.AndExpressionContext ->
                buildBinaryExpression(BinaryOperator.AND, ctx.expression(0), ctx.expression(1), ctx)

            is cqlParser.OrExpressionContext ->
                buildBinaryExpression(BinaryOperator.OR, ctx.expression(0), ctx.expression(1), ctx)

            is cqlParser.ImpliesExpressionContext ->
                buildBinaryExpression(
                    BinaryOperator.IMPLIES,
                    ctx.expression(0),
                    ctx.expression(1),
                    ctx,
                )

            is cqlParser.CastExpressionContext -> CastExpression(
                operand = buildExpression(ctx.expression()),
                type = buildTypeSpecifier(ctx.typeSpecifier()),
                locator = ctx.toLocator(),
            )

            is cqlParser.TypeExpressionContext -> buildTypeExpression(ctx)
            is cqlParser.NotExpressionContext ->
                UnaryExpression(
                    operator = UnaryOperator.NOT,
                    operand = buildExpression(ctx.expression()),
                    locator = ctx.toLocator(),
                )

            else -> unsupportedExpression("expression", ctx)
        }

    private fun buildBooleanExpression(ctx: cqlParser.BooleanExpressionContext): Expression {
        val leftCtx = ctx.expression()
        val left = buildExpression(leftCtx)
        val keyword =
            ctx.children
                ?.map { it.text }
                ?.firstOrNull {
                    it.equals("null", true) || it.equals("true", true) || it.equals("false", true)
                }
        val literal =
            when (keyword?.lowercase()) {
                "null" -> NullLiteral(ctx.toLocator())
                "true" -> BooleanLiteral(true, ctx.toLocator())
                "false" -> BooleanLiteral(false, ctx.toLocator())
                else -> return unsupportedExpression("booleanExpression", ctx)
            }
        val op =
            if (ctx.text.contains("is not", ignoreCase = true)) BinaryOperator.NOT_EQUALS
            else BinaryOperator.EQUALS
        val right = LiteralExpression(literal, ctx.toLocator())
        return BinaryExpression(
            operator = op,
            left = left,
            right = right,
            locator = ctx.toLocator(),
        )
    }

    private fun buildInequalityExpression(ctx: cqlParser.InequalityExpressionContext): Expression {
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
        return BinaryExpression(
            operator = operator,
            left = left,
            right = right,
            locator = parent.toLocator(),
        )
    }

    private fun buildExpressionTerm(ctx: cqlParser.ExpressionTermContext): Expression =
        when (ctx) {
            is cqlParser.TermExpressionTermContext -> buildTerm(ctx.term())
            is cqlParser.InvocationExpressionTermContext -> buildInvocationExpression(ctx)
            is cqlParser.PolarityExpressionTermContext -> {
                val symbol = ctx.getChild(0)?.text
                val operator = if (symbol == "-") UnaryOperator.NEGATE else UnaryOperator.POSITIVE
                UnaryExpression(
                    operator = operator,
                    operand = buildExpressionTerm(ctx.expressionTerm()),
                    locator = ctx.toLocator(),
                )
            }

            is cqlParser.AdditionExpressionTermContext -> {
                val operator = when (ctx.getChild(1)?.text?.lowercase()) {
                    "+" -> BinaryOperator.ADD
                    "-" -> BinaryOperator.SUBTRACT
                    "&" -> BinaryOperator.CONCAT
                    else -> return unsupportedExpression("additionExpressionTerm", ctx)
                }
                BinaryExpression(
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
                        "div" -> BinaryOperator.DIVIDE
                        "mod" -> BinaryOperator.MODULO
                        else -> return unsupportedExpression("multiplicationExpressionTerm", ctx)
                    }
                BinaryExpression(
                    operator = operator,
                    left = buildExpressionTerm(ctx.expressionTerm(0)!!),
                    right = buildExpressionTerm(ctx.expressionTerm(1)!!),
                    locator = ctx.toLocator(),
                )
            }

            is cqlParser.PowerExpressionTermContext ->
                BinaryExpression(
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
                UnaryExpression(
                    operator = UnaryOperator.SUCCESSOR,
                    operand = buildExpressionTerm(ctx.expressionTerm()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.PredecessorExpressionTermContext ->
                UnaryExpression(
                    operator = UnaryOperator.PREDECESSOR,
                    operand = buildExpressionTerm(ctx.expressionTerm()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.ElementExtractorExpressionTermContext -> {
                val kind =
                    when (ctx.getChild(0)?.text?.lowercase()) {
                        "singleton" -> ElementExtractorKind.SINGLETON
                        "point" -> ElementExtractorKind.POINT
                        else -> return unsupportedExpression("elementExtractorExpressionTerm", ctx)
                    }
                ElementExtractorExpression(
                    kind = kind,
                    operand = buildExpressionTerm(ctx.expressionTerm()),
                    locator = ctx.toLocator(),
                )
            }

            is cqlParser.TypeExtentExpressionTermContext -> {
                val kind =
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
                    kind = kind,
                    type = namedType,
                    locator = ctx.toLocator(),
                )
            }

            is cqlParser.ConversionExpressionTermContext -> {
                val operand = buildExpression(ctx.expression())
                val type =
                    ctx.typeSpecifier()?.let { buildTypeSpecifier(it) }
                val unit = ctx.unit()?.text?.trim('\'')
                ConversionExpression(
                    operand = operand,
                    destinationType = type,
                    destinationUnit = unit,
                    locator = ctx.toLocator(),
                )
            }

            is cqlParser.TimeBoundaryExpressionTermContext -> {
                val kind =
                    when (ctx.getChild(0)?.text?.lowercase()) {
                        "start" -> TimeBoundaryKind.START
                        "end" -> TimeBoundaryKind.END
                        else -> return unsupportedExpression("timeBoundaryExpressionTerm", ctx)
                    }
                TimeBoundaryExpression(
                    kind = kind,
                    operand = buildExpressionTerm(ctx.expressionTerm()),
                    locator = ctx.toLocator(),
                )
            }

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
                val kind =
                    when (keyword) {
                        "distinct" -> ListTransformKind.DISTINCT
                        "flatten" -> ListTransformKind.FLATTEN
                        else -> return unsupportedExpression("aggregateExpressionTerm", ctx)
                    }
                ListTransformExpression(
                    kind = kind,
                    operand = buildExpression(ctx.expression()),
                    locator = ctx.toLocator(),
                )
            }

            is cqlParser.SetAggregateExpressionTermContext -> {
                val keyword = ctx.getChild(0)?.text?.lowercase()
                val kind =
                    when (keyword) {
                        "expand" -> ExpandCollapseKind.EXPAND
                        "collapse" -> ExpandCollapseKind.COLLAPSE
                        else -> return unsupportedExpression("setAggregateExpressionTerm", ctx)
                    }
                val precision = ctx.dateTimePrecision()?.text
                val perExpression =
                    ctx.expression().drop(1).firstOrNull()?.let { buildExpression(it) }
                ExpandCollapseExpression(
                    kind = kind,
                    operand = buildExpression(ctx.expression(0)!!),
                    perPrecision = precision,
                    perExpression = perExpression,
                    locator = ctx.toLocator(),
                )
            }

            else -> unsupportedExpression("expressionTerm", ctx)
        }

    private fun buildInvocationExpression(
        ctx: cqlParser.InvocationExpressionTermContext
    ): Expression {
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

    private fun buildTerm(ctx: cqlParser.TermContext): Expression =
        when (ctx) {
            is cqlParser.LiteralTermContext -> buildLiteral(ctx.literal())
            is cqlParser.InvocationTermContext -> buildInvocation(ctx.invocation())
            is cqlParser.ParenthesizedTermContext -> buildExpression(ctx.expression())
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

            else -> unsupportedExpression("term", ctx)
        }

    private fun buildInvocation(ctx: cqlParser.InvocationContext): Expression =
        when (ctx) {
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
                IdentifierExpression(
                    name = QualifiedIdentifier(listOf("\$this")),
                    locator = ctx.toLocator(),
                )

            is cqlParser.IndexInvocationContext ->
                IdentifierExpression(
                    name = QualifiedIdentifier(listOf("\$index")),
                    locator = ctx.toLocator(),
                )

            is cqlParser.TotalInvocationContext ->
                IdentifierExpression(
                    name = QualifiedIdentifier(listOf("\$total")),
                    locator = ctx.toLocator(),
                )

            else -> unsupportedExpression("invocation", ctx)
        }

    private fun buildLiteral(ctx: cqlParser.LiteralContext): Expression =
        when (ctx) {
            is cqlParser.NumberLiteralContext ->
                LiteralExpression(
                    literal =
                        NumberLiteral(
                            ctx.NUMBER().text,
                            isDecimal =
                                ctx.NUMBER().text.contains('.') ||
                                    ctx.NUMBER().text.contains('e', true),
                            locator = ctx.toLocator(),
                        ),
                    locator = ctx.toLocator(),
                )

            is cqlParser.LongNumberLiteralContext ->
                LiteralExpression(
                    literal =
                        NumberLiteral(
                            ctx.LONGNUMBER().text,
                            isDecimal = false,
                            locator = ctx.toLocator(),
                        ),
                    locator = ctx.toLocator(),
                )

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

    private fun cqlParser.VersionSpecifierContext.toVersionSpecifier(): VersionSpecifier =
        VersionSpecifier(this.text.unquote())

    private fun cqlParser.AccessModifierContext.toAccessModifier(): AccessModifier =
        when (text.lowercase()) {
            "public" -> AccessModifier.PUBLIC
            "private" -> AccessModifier.PRIVATE
            else -> AccessModifier.PUBLIC
        }

    private fun cqlParser.IdentifierContext.toIdentifier(): Identifier =
        Identifier(this.text.removeTickEscapes())

    private fun cqlParser.QuantityContext.toQuantityLiteral(): QuantityLiteral {
        val raw = text
        val match = QUANTITY_REGEX.matchEntire(raw)
        return if (match != null) {
            val value = match.groupValues[1]
            val unit = match.groupValues[2].ifEmpty { null }
            QuantityLiteral(value, unit, toLocator())
        } else {
            QuantityLiteral(raw, null, toLocator())
        }
    }

    private fun cqlParser.QualifiedIdentifierContext.toQualifiedIdentifier(): QualifiedIdentifier =
        QualifiedIdentifier(
            parts =
                buildList {
                    addAll(qualifier().map { it.text.removeTickEscapes() })
                    add(identifier().text.removeTickEscapes())
                }
        )

    private fun cqlParser.NamedTypeSpecifierContext.toQualifiedIdentifier(): QualifiedIdentifier =
        QualifiedIdentifier(
            parts =
                buildList {
                    addAll(qualifier().map { it.text.removeTickEscapes() })
                    add(referentialOrTypeNameIdentifier().text.removeTickEscapes())
                }
        )

    private fun cqlParser.ReferentialIdentifierContext.toIdentifier(): Identifier =
        when {
            identifier() != null -> identifier()!!.toIdentifier()
            keywordIdentifier() != null -> Identifier(keywordIdentifier()!!.text)
            else -> Identifier(text.removeTickEscapes())
        }

    private fun cqlParser.ReferentialIdentifierContext.toQualifiedIdentifier():
        QualifiedIdentifier = QualifiedIdentifier(listOf(toIdentifier().value))

    private fun cqlParser.IdentifierOrFunctionIdentifierContext.toIdentifier(): Identifier =
        when {
            identifier() != null -> identifier()!!.toIdentifier()
            functionIdentifier() != null -> Identifier(functionIdentifier()!!.text)
            else -> Identifier(text.removeTickEscapes())
        }

    private fun buildInfixSetExpression(ctx: cqlParser.InFixSetExpressionContext): Expression {
        val operator =
            when (ctx.getChild(1)?.text?.lowercase()) {
                "|", "union" -> BinaryOperator.UNION
                "intersect" -> BinaryOperator.INTERSECT
                "except" -> BinaryOperator.EXCEPT
                else -> return unsupportedExpression("inFixSetExpression", ctx)
            }
        return BinaryExpression(
            operator = operator,
            left = buildExpression(ctx.expression(0)!!),
            right = buildExpression(ctx.expression(1)!!),
            locator = ctx.toLocator(),
        )
    }

    private fun buildMembershipExpression(
        ctx: cqlParser.MembershipExpressionContext,
    ): Expression {
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

    private fun buildBetweenExpression(ctx: cqlParser.BetweenExpressionContext): Expression =
        BetweenExpression(
            input = buildExpression(ctx.expression()),
            lower = buildExpressionTerm(ctx.expressionTerm(0)!!),
            upper = buildExpressionTerm(ctx.expressionTerm(1)!!),
            properly = ctx.children?.any { it.text.equals("properly", true) } == true,
            locator = ctx.toLocator(),
        )

    private fun buildDurationBetweenExpression(
        ctx: cqlParser.DurationBetweenExpressionContext,
    ): Expression =
        DurationBetweenExpression(
            precision = ctx.pluralDateTimePrecision().text,
            lower = buildExpressionTerm(ctx.expressionTerm(0)!!),
            upper = buildExpressionTerm(ctx.expressionTerm(1)!!),
            locator = ctx.toLocator(),
        )

    private fun buildDifferenceBetweenExpression(
        ctx: cqlParser.DifferenceBetweenExpressionContext,
    ): Expression =
        DifferenceBetweenExpression(
            precision = ctx.pluralDateTimePrecision().text,
            lower = buildExpressionTerm(ctx.expressionTerm(0)!!),
            upper = buildExpressionTerm(ctx.expressionTerm(1)!!),
            locator = ctx.toLocator(),
        )

    private fun buildTypeExpression(ctx: cqlParser.TypeExpressionContext): Expression {
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
        val expressionList = ctx.expression()
        val items = ctx.caseExpressionItem()
        val comparand = if (expressionList.size > 1) buildExpression(expressionList.first()) else null
        val caseItems =
            items.map { item ->
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

    private fun buildListLiteral(ctx: cqlParser.ListSelectorContext): ListLiteral =
        ListLiteral(
            elements = ctx.expression().map { buildExpression(it) },
            elementType = ctx.typeSpecifier()?.let { buildTypeSpecifier(it) },
            locator = ctx.toLocator(),
        )

    private fun buildTupleLiteral(ctx: cqlParser.TupleSelectorContext): TupleLiteral {
        val elements =
            ctx.tupleElementSelector().map {
                TupleElementValue(
                    name = it.referentialIdentifier().toIdentifier(),
                    expression = buildExpression(it.expression()),
                    locator = it.toLocator(),
                )
            }
        return TupleLiteral(elements = elements, locator = ctx.toLocator())
    }

    private fun buildInstanceLiteral(ctx: cqlParser.InstanceSelectorContext): InstanceLiteral {
        val typeContext = ctx.namedTypeSpecifier()
        val namedType =
            NamedTypeSpecifier(
                name = typeContext.toQualifiedIdentifier(),
                locator = typeContext.toLocator(),
            )
        val elements =
            ctx.instanceElementSelector().map {
                TupleElementValue(
                    name = it.referentialIdentifier().toIdentifier(),
                    expression = buildExpression(it.expression()),
                    locator = it.toLocator(),
                )
            }
        return InstanceLiteral(type = namedType, elements = elements, locator = ctx.toLocator())
    }

    private fun buildCodeLiteral(ctx: cqlParser.CodeSelectorContext): CodeLiteral {
        val code = ctx.STRING().text.unquote()
        val system = ctx.codesystemIdentifier().toTerminologyReference()
        val display = ctx.displayClause()?.STRING()?.text?.unquote()
        return CodeLiteral(code = code, system = system, display = display, locator = ctx.toLocator())
    }

    private fun buildConceptLiteral(ctx: cqlParser.ConceptSelectorContext): ConceptLiteral {
        val codes = ctx.codeSelector().map { buildCodeLiteral(it) }
        val display = ctx.displayClause()?.STRING()?.text?.unquote()
        return ConceptLiteral(codes = codes, display = display, locator = ctx.toLocator())
    }

    private fun buildIntervalLiteral(ctx: cqlParser.IntervalSelectorContext): IntervalLiteral {
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

    private fun buildRatioLiteral(ctx: cqlParser.RatioContext): RatioLiteral =
        RatioLiteral(
            numerator = ctx.quantity(0)!!.toQuantityLiteral(),
            denominator = ctx.quantity(1)!!.toQuantityLiteral(),
            locator = ctx.toLocator(),
        )

    private fun buildRetrieve(ctx: cqlParser.RetrieveContext): RetrieveExpression {
        val contextIdentifier =
            ctx.contextIdentifier()?.qualifiedIdentifierExpression()?.toQualifiedIdentifier()
        val typeSpecifier =
            NamedTypeSpecifier(
                name = ctx.namedTypeSpecifier().toQualifiedIdentifier(),
                locator = ctx.namedTypeSpecifier().toLocator(),
            )
        val codePath =
            ctx.codePath()?.simplePath()?.toQualifiedIdentifier()
        val comparator =
            ctx.codeComparator()?.text?.lowercase()?.let {
                when (it) {
                    "in" -> TerminologyComparator.IN
                    "=" -> TerminologyComparator.EQUALS
                    "~" -> TerminologyComparator.EQUIVALENT
                    else -> null
                }
            }
        val terminologyRestriction =
            ctx.terminology()?.let { buildTerminologyRestriction(it) }
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
        val sources = ctx.sourceClause().aliasedQuerySource().map { buildAliasedQuerySource(it) }
        val lets =
            ctx.letClause()?.letClauseItem()?.map { buildLetClauseItem(it) }.orEmpty()
        val inclusions = ctx.queryInclusionClause().map { buildInclusionClause(it) }
        val where = ctx.whereClause()?.expression()?.let { buildExpression(it) }
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
        ctx: cqlParser.AliasedQuerySourceContext,
    ): AliasedQuerySource =
        AliasedQuerySource(
            source = buildQuerySource(ctx.querySource()),
            alias = ctx.alias().identifier().toIdentifier(),
            locator = ctx.toLocator(),
        )

    private fun buildQuerySource(ctx: cqlParser.QuerySourceContext): QuerySource {
        ctx.retrieve()?.let { return buildRetrieve(it) }
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

    private fun buildLetClauseItem(ctx: cqlParser.LetClauseItemContext): LetClauseItem =
        LetClauseItem(
            identifier = ctx.identifier().toIdentifier(),
            expression = buildExpression(ctx.expression()),
            locator = ctx.toLocator(),
        )

    private fun buildInclusionClause(
        ctx: cqlParser.QueryInclusionClauseContext,
    ): QueryInclusionClause {
        return when {

            ctx.withClause() != null -> {
                val withClause = ctx.withClause()!!
                WithClause(
                    source = buildAliasedQuerySource(withClause.aliasedQuerySource()),
                    condition = buildExpression(withClause.expression()),
                    locator = ctx.toLocator(),
                )
            }

            ctx.withoutClause() != null -> {
                val withoutClause = ctx.withoutClause()!!
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

    private fun buildAggregateClauseNode(
        ctx: cqlParser.AggregateClauseContext,
    ): AggregateClause {
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

    private fun buildStartingExpression(ctx: cqlParser.StartingClauseContext): Expression =
        ctx.simpleLiteral()?.let { buildSimpleLiteralExpression(it) }
            ?: ctx.quantity()?.let {
                LiteralExpression(
                    literal = it.toQuantityLiteral(),
                    locator = it.toLocator(),
                )
            }
            ?: ctx.expression()?.let { buildExpression(it) }
            ?: LiteralExpression(literal = NullLiteral(ctx.toLocator()), locator = ctx.toLocator())

    private fun buildReturnClause(ctx: cqlParser.ReturnClauseContext): ReturnClause {
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
        val items =
            if (ctx.sortByItem().isNotEmpty()) {
                ctx.sortByItem().map { buildSortByItem(it) }
            } else {
                emptyList()
            }
        return SortClause(items = items, locator = ctx.toLocator())
    }

    private fun buildSortByItem(ctx: cqlParser.SortByItemContext): SortByItem =
        SortByItem(
            expression = buildExpressionTerm(ctx.expressionTerm()),
            direction =
                ctx.sortDirection()?.let { buildSortDirection(it) } ?: SortDirection.ASCENDING,
            locator = ctx.toLocator(),
        )

    private fun buildSortDirection(ctx: cqlParser.SortDirectionContext): SortDirection =
        when (ctx.text.lowercase()) {
            "desc", "descending" -> SortDirection.DESCENDING
            else -> SortDirection.ASCENDING
        }

    private fun buildSimpleLiteralExpression(
        ctx: cqlParser.SimpleLiteralContext,
    ): Expression =
        when (ctx) {
            is cqlParser.SimpleStringLiteralContext ->
                LiteralExpression(
                    literal = StringLiteral(ctx.STRING().text.unquote(), ctx.toLocator()),
                    locator = ctx.toLocator(),
                )

            is cqlParser.SimpleNumberLiteralContext -> {
                val value = ctx.NUMBER().text
                LiteralExpression(
                    literal =
                        NumberLiteral(
                            value = value,
                            isDecimal = value.contains('.') || value.contains('e', true),
                            locator = ctx.toLocator(),
                        ),
                    locator = ctx.toLocator(),
                )
            }

            else ->
                LiteralExpression(literal = NullLiteral(ctx.toLocator()), locator = ctx.toLocator())
        }

    private fun buildTerminologyRestriction(
        ctx: cqlParser.TerminologyContext,
    ): TerminologyRestriction {
        ctx.qualifiedIdentifierExpression()?.let { qualified ->
            val identifierExpression =
                IdentifierExpression(
                    name = qualified.toQualifiedIdentifier(),
                    locator = qualified.toLocator(),
                )
            return TerminologyRestriction(terminology = identifierExpression, locator = ctx.toLocator())
        }

        ctx.expression()?.let { expressionContext ->
            return TerminologyRestriction(
                terminology = buildExpression(expressionContext),
                locator = ctx.toLocator(),
            )
        }

        return TerminologyRestriction(
            terminology =
                LiteralExpression(literal = NullLiteral(ctx.toLocator()), locator = ctx.toLocator()),
            locator = ctx.toLocator(),
        )
    }

    private fun cqlParser.QualifiedIdentifierExpressionContext.toQualifiedIdentifier():
        QualifiedIdentifier =
        QualifiedIdentifier(
            parts =
                buildList {
                    addAll(qualifierExpression().map { it.referentialIdentifier().toIdentifier().value })
                    add(referentialIdentifier().toIdentifier().value)
                }
        )

    private fun cqlParser.SimplePathContext.toQualifiedIdentifier(): QualifiedIdentifier =
        when (this) {
            is cqlParser.SimplePathReferentialIdentifierContext ->
                QualifiedIdentifier(listOf(referentialIdentifier().toIdentifier().value))

            is cqlParser.SimplePathQualifiedIdentifierContext -> {
                val base = simplePath().toQualifiedIdentifier().parts.toMutableList()
                base += referentialIdentifier().toIdentifier().value
                QualifiedIdentifier(base)
            }

            is cqlParser.SimplePathIndexerContext -> {
                val base = simplePath().toQualifiedIdentifier().parts.toMutableList()
                base += simpleLiteral().text.unquote()
                QualifiedIdentifier(base)
            }

            else -> QualifiedIdentifier(listOf(text))
        }

    private fun cqlParser.CodesystemIdentifierContext.toTerminologyReference(): TerminologyReference =
        TerminologyReference(
            identifier = identifier().toIdentifier(),
            libraryName = libraryIdentifier()?.identifier()?.toIdentifier(),
            locator = toLocator(),
        )

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

    private fun String.unquote(): String =
        when {
            length >= 2 && startsWith("'") && endsWith("'") -> substring(1, length - 1)
            length >= 2 && startsWith("\"") && endsWith("\"") -> substring(1, length - 1)
            else -> this
        }

    private fun String.removeTickEscapes(): String =
        when {
            length >= 2 && startsWith("`") && endsWith("`") -> substring(1, length - 1)
            length >= 2 && startsWith("\"") && endsWith("\"") -> substring(1, length - 1)
            else -> this
        }

    private fun unsupportedExpression(rule: String, ctx: ParserRuleContext): Expression {
        problems += AstProblem("Unsupported $rule: ${ctx.text}", ctx.toLocator())
        return UnsupportedExpression(description = ctx.text, locator = ctx.toLocator())
    }

    private fun unsupportedDefinition(rule: String, ctx: ParserRuleContext): Definition {
        problems += AstProblem("Unsupported $rule: ${ctx.text}", ctx.toLocator())
        return UnsupportedDefinition(grammarRule = rule, locator = ctx.toLocator())
    }

    private inner class ProblemCollector : BaseErrorListener() {
        val problems = mutableListOf<AstProblem>()

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?,
        ) {
            problems +=
                AstProblem(
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

    companion object {
        private val QUANTITY_REGEX = Regex("^(-?[0-9]+(?:\\.[0-9]+)?)\\s*(.*)$")
    }
}
