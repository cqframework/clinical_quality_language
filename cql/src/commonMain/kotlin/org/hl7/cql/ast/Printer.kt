package org.hl7.cql.ast

/**
 * Writes an AST as CQL. Parsing the result gives the same tree, apart from source positions: what
 * the AST keeps of how the source was written (identifier quoting, parentheses, comments, `asc` or
 * `ascending`) is written the same way, and the rest is laid out the same way every time.
 *
 * Where CQL has two spellings for one tree, it is written with one of them: `on or before` (not
 * `before or on`), `union` (not `|`), `Tuple { ... }` (with `Tuple`), `years between` (without
 * `duration in`), `aggregate` (without `all`), a calendar unit without quotes, and the optional
 * `from` of a query only when it has several sources.
 *
 * Parentheses are added only where the tree needs them, so a tree built in code prints correctly.
 * Comments are written in order, each before the code that followed it, so that they stay with
 * their declaration (see [Declaration.comments]).
 */
object Printer {

    fun print(library: Library): String = Writer().apply { library(library) }.toString()

    fun print(expression: Expression): String =
        Writer().apply { expression(expression, LOWEST, Follow.NOTHING) }.toString()
}

/** What follows an expression in the text, which decides if an open-ended one needs parentheses. */
private class Follow
private constructor(
    /** The precedence of the operator that follows, if one does. */
    val operator: Int?,
    /** The clause of the enclosing query that follows, if one does, which a query might take. */
    val clause: QueryClause?,
) {
    companion object {
        /** A closing delimiter, a keyword no expression can continue with, or the end. */
        val NOTHING = Follow(operator = null, clause = null)

        fun operator(precedence: Int) = Follow(operator = precedence, clause = null)

        fun clause(clause: QueryClause) = Follow(operator = null, clause = clause)
    }
}

/** The clauses of a query, in the order they must come. */
private enum class QueryClause {
    LET,
    /** `with` or `without`, of which there can be several. */
    INCLUSION,
    WHERE,
    /** `aggregate` or `return`. */
    RESULT,
    SORT,
}

// Precedence, from the order of the alternatives of the grammar's `expression` and
// `expressionTerm` rules: a higher number binds tighter.
private const val LOWEST = 0
private const val IMPLIES = 1
private const val OR = 2
private const val AND = 3
private const val MEMBERSHIP = 4
private const val EQUALITY = 5
private const val TIMING = 6
private const val INEQUALITY = 7
private const val SET = 8
private const val BETWEEN = 11
private const val EXISTS = 12
private const val NOT = 13
private const val TYPE_TEST = 15
private const val BOOLEAN_TEST = 16

/** The lowest precedence of an expression term, where an expression needs parentheses. */
private const val TERM = 18
private const val ADDITION = 25
private const val MULTIPLICATION = 26
private const val POWER = 27
private const val TERM_PREFIX = 30
private const val POLARITY = 38
private const val POSTFIX = 40

private val calendarUnits =
    setOf("year", "month", "week", "day", "hour", "minute", "second", "millisecond").let { units ->
        units + units.map { it + "s" }
    }

private val operatorTexts =
    mapOf(
        BinaryOperator.ADD to "+",
        BinaryOperator.SUBTRACT to "-",
        BinaryOperator.MULTIPLY to "*",
        BinaryOperator.DIVIDE to "/",
        BinaryOperator.TRUNCATED_DIVIDE to "div",
        BinaryOperator.MODULO to "mod",
        BinaryOperator.POWER to "^",
        BinaryOperator.CONCAT to "&",
        BinaryOperator.EQUALS to "=",
        BinaryOperator.NOT_EQUALS to "!=",
        BinaryOperator.EQUIVALENT to "~",
        BinaryOperator.NOT_EQUIVALENT to "!~",
        BinaryOperator.LT to "<",
        BinaryOperator.LTE to "<=",
        BinaryOperator.GT to ">",
        BinaryOperator.GTE to ">=",
        BinaryOperator.AND to "and",
        BinaryOperator.OR to "or",
        BinaryOperator.XOR to "xor",
        BinaryOperator.IMPLIES to "implies",
        BinaryOperator.UNION to "union",
        BinaryOperator.INTERSECT to "intersect",
        BinaryOperator.EXCEPT to "except",
    )

private val plainIdentifier = Regex("[A-Za-z_][A-Za-z0-9_]*")

private val specialIdentifiers = setOf("\$this", "\$index", "\$total")

@Suppress("TooManyFunctions", "LargeClass")
private class Writer {
    private val out = StringBuilder()
    private var indent = 0
    private var atLineStart = true

    /** Whether a space goes before the next text on this line, as after a block comment. */
    private var spaceBeforeText = false

    /** Comments within the declaration being written, not yet written. */
    private val pending = ArrayDeque<Comment>()

    override fun toString(): String = out.toString()

    // Layout

    private fun write(text: String) {
        if (text.isEmpty()) return
        if (atLineStart) {
            repeat(indent) { out.append("  ") }
            atLineStart = false
        } else if (spaceBeforeText && !text.startsWith(" ")) {
            out.append(' ')
        }
        spaceBeforeText = false
        out.append(text)
    }

    private fun newline() {
        out.append('\n')
        atLineStart = true
        spaceBeforeText = false
    }

    private inline fun indented(block: () -> Unit) {
        indent++
        block()
        indent--
    }

    private fun comment(comment: Comment) {
        if (!atLineStart && out.lastOrNull()?.let { it == ' ' || it == '(' } == false) write(" ")
        write(comment.text)
        if (comment.style == CommentStyle.LINE) newline() else spaceBeforeText = true
    }

    /** Writes the pending comments that came before [locator]. */
    private fun flush(locator: Locator) {
        while (pending.isNotEmpty() && pending.first().locator.startIndex < locator.startIndex) {
            comment(pending.removeFirst())
        }
    }

    /** Writes the pending comments within [locator], then [text], which closes it. */
    private fun close(text: String, locator: Locator) {
        while (pending.isNotEmpty() && pending.first().locator.startIndex < locator.stopIndex) {
            comment(pending.removeFirst())
        }
        write(text)
    }

    // Declarations

    fun library(library: Library) {
        val header = library.name?.let { name -> HeaderDeclaration(library, name) }
        val declarations: List<Declaration> =
            listOfNotNull(header) + library.definitions + library.statements
        if (declarations.isEmpty()) {
            library.comments.forEach {
                comment(it)
                if (!atLineStart) newline()
            }
            return
        }
        var previous: Declaration? = null
        for ((index, declaration) in declarations.withIndex()) {
            if (previous != null && separatedByBlankLine(previous, declaration)) newline()
            declaration(declaration, isLast = index == declarations.lastIndex)
            previous = declaration
        }
    }

    /** Definitions of one kind are grouped; everything else is separated by a blank line. */
    private fun separatedByBlankLine(previous: Declaration, next: Declaration): Boolean =
        previous !is Definition || next !is Definition || previous::class != next::class

    private fun declaration(declaration: Declaration, isLast: Boolean) {
        val extent = declaration.locator
        val known = extent.startIndex >= 0
        val (before, rest) =
            declaration.comments.partition { !known || it.locator.startIndex < extent.startIndex }
        val (within, after) = rest.partition { it.locator.startIndex <= extent.stopIndex }
        before.forEach {
            comment(it)
            if (!atLineStart) newline()
        }
        pending.addAll(within)
        when (declaration) {
            is HeaderDeclaration -> header(declaration)
            is Definition -> definition(declaration)
            is Statement -> statement(declaration)
            else -> error("Unknown declaration $declaration")
        }
        while (pending.isNotEmpty()) comment(pending.removeFirst())
        // After the last declaration, a comment may have been on a later line; write each on its
        // own line, where it still belongs to that declaration.
        after.forEach {
            if (isLast && !atLineStart) newline()
            comment(it)
        }
        if (!atLineStart) newline()
    }

    /**
     * The library header, as a declaration with the library's own comments. The library does not
     * keep the extent of its header, so the name's stands in for it: comments before the name come
     * before the header, and comments after it follow the header on its line.
     */
    private class HeaderDeclaration(val library: Library, val name: QualifiedIdentifier) :
        Declaration {
        override val comments: List<Comment> = library.comments
        override val tags: List<Tag> = library.tags
        override val locator: Locator = name.locator
    }

    private fun header(header: HeaderDeclaration) {
        write("library ")
        qualifiedIdentifier(header.name)
        header.library.version?.let { version(it) }
    }

    private fun version(version: VersionSpecifier) {
        write(" version ")
        write(quote('\'', version.value))
    }

    private fun access(access: AccessModifier?) {
        when (access) {
            AccessModifier.PUBLIC -> write("public ")
            AccessModifier.PRIVATE -> write("private ")
            null -> {}
        }
    }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private fun definition(definition: Definition) {
        when (definition) {
            is UsingDefinition -> {
                write("using ")
                qualifiedIdentifier(definition.modelIdentifier)
                definition.version?.let { version(it) }
                definition.alias?.let {
                    write(" called ")
                    identifier(it)
                }
            }
            is IncludeDefinition -> {
                write("include ")
                qualifiedIdentifier(definition.libraryIdentifier)
                definition.version?.let { version(it) }
                definition.alias?.let {
                    write(" called ")
                    identifier(it)
                }
            }
            is CodeSystemDefinition -> {
                access(definition.access)
                write("codesystem ")
                identifier(definition.name)
                write(": ")
                write(quote('\'', definition.id))
                definition.version?.let { version(it) }
            }
            is ValueSetDefinition -> {
                access(definition.access)
                write("valueset ")
                identifier(definition.name)
                write(": ")
                write(quote('\'', definition.id))
                definition.version?.let { version(it) }
                if (definition.codesystems.isNotEmpty()) {
                    write(" codesystems { ")
                    commaSeparated(definition.codesystems) { terminologyReference(it) }
                    write(" }")
                }
            }
            is CodeDefinition -> {
                access(definition.access)
                write("code ")
                identifier(definition.name)
                write(": ")
                write(quote('\'', definition.id))
                write(" from ")
                terminologyReference(definition.system)
                definition.display?.let { display(it) }
            }
            is ConceptDefinition -> {
                access(definition.access)
                write("concept ")
                identifier(definition.name)
                write(": { ")
                commaSeparated(definition.codes) { terminologyReference(it) }
                write(" }")
                definition.display?.let { display(it) }
            }
            is ParameterDefinition -> {
                access(definition.access)
                write("parameter ")
                identifier(definition.name)
                definition.type?.let {
                    write(" ")
                    typeSpecifier(it)
                }
                definition.default?.let {
                    write(" default ")
                    expression(it, LOWEST, Follow.NOTHING)
                }
            }
            is UnsupportedDefinition ->
                throw IllegalArgumentException(
                    "Cannot print an incomplete ${definition.grammarRule}"
                )
        }
    }

    private fun statement(statement: Statement) {
        when (statement) {
            is ContextDefinition -> {
                write("context ")
                statement.model?.let {
                    identifier(it)
                    write(".")
                }
                identifier(statement.context)
            }
            is ExpressionDefinition -> {
                write("define ")
                access(statement.access)
                identifier(statement.name)
                write(":")
                body(statement.expression)
            }
            is FunctionDefinition -> function(statement)
            is UnsupportedStatement ->
                throw IllegalArgumentException(
                    "Cannot print an incomplete ${statement.grammarRule}"
                )
        }
    }

    private fun function(function: FunctionDefinition) {
        write("define ")
        access(function.access)
        if (function.fluent) write("fluent ")
        write("function ")
        identifier(function.name)
        write("(")
        commaSeparated(function.operands) {
            identifier(it.name)
            write(" ")
            typeSpecifier(it.type)
        }
        write(")")
        function.returnType?.let {
            write(" returns ")
            typeSpecifier(it)
        }
        write(":")
        when (val body = function.body) {
            is ExpressionFunctionBody -> body(body.expression)
            is ExternalFunctionBody -> write(" external")
        }
    }

    /** The expression of a definition, on the lines after it. */
    private fun body(expression: Expression) {
        newline()
        indented { expression(expression, LOWEST, Follow.NOTHING) }
    }

    private fun display(display: String) {
        write(" display ")
        write(quote('\'', display))
    }

    private inline fun <T> commaSeparated(items: List<T>, write: (T) -> Unit) {
        items.forEachIndexed { index, item ->
            if (index > 0) write(", ")
            write(item)
        }
    }

    // Names and types

    private fun identifier(identifier: Identifier) {
        flush(identifier.locator)
        write(identifier.written())
    }

    private fun qualifiedIdentifier(name: QualifiedIdentifier) {
        name.parts.forEachIndexed { index, part ->
            if (index > 0) write(".")
            identifier(part)
        }
    }

    private fun terminologyReference(reference: TerminologyReference) {
        reference.libraryName?.let {
            identifier(it)
            write(".")
        }
        identifier(reference.identifier)
    }

    private fun typeSpecifier(type: TypeSpecifier) {
        flush(type.locator)
        when (type) {
            is NamedTypeSpecifier -> qualifiedIdentifier(type.name)
            is ListTypeSpecifier -> {
                write("List<")
                typeSpecifier(type.elementType)
                write(">")
            }
            is IntervalTypeSpecifier -> {
                write("Interval<")
                typeSpecifier(type.pointType)
                write(">")
            }
            is TupleTypeSpecifier -> {
                write("Tuple { ")
                commaSeparated(type.elements) {
                    identifier(it.name)
                    write(" ")
                    typeSpecifier(it.type)
                }
                write(" }")
            }
            is ChoiceTypeSpecifier -> {
                write("Choice<")
                commaSeparated(type.choices) { typeSpecifier(it) }
                write(">")
            }
        }
    }

    // Expressions

    /**
     * Writes [expression] where an operator must have a precedence of at least [minimum] and
     * [follow] comes after it, in parentheses if it would otherwise be read differently.
     */
    fun expression(expression: Expression, minimum: Int, follow: Follow) {
        if (!startsWithOperand(expression)) flush(expression.locator)
        if (needsParentheses(expression, minimum, follow)) {
            write("(")
            unparenthesized(expression, Follow.NOTHING)
            write(")")
        } else {
            unparenthesized(expression, follow)
        }
    }

    /**
     * An operator that starts with its left operand needs parentheses where a lower precedence is
     * not allowed. Any other form is read whole wherever an expression starts (as the parser reads
     * a primary), except that a form that is not an expression term needs them inside one. Either
     * kind needs them if it ends with an operand that would take what follows as its own.
     */
    private fun needsParentheses(expression: Expression, minimum: Int, follow: Follow): Boolean {
        val misplaced =
            if (startsWithOperand(expression)) operatorPrecedence(expression) < minimum
            else minimum >= TERM && !isTerm(expression)
        val absorbed =
            (follow.operator != null &&
                absorbsFrom(expression)?.let { follow.operator >= it } == true) ||
                (expression is QueryExpression &&
                    follow.clause?.let { takes(expression, it) } == true)
        return misplaced || absorbed
    }

    /** Whether [expression] is written starting with one of its operands, rather than a token. */
    private fun startsWithOperand(expression: Expression): Boolean =
        when (expression) {
            is BinaryExpression,
            is BetweenExpression,
            is IsExpression,
            is AsExpression,
            is BooleanTestExpression,
            is PropertyAccessExpression,
            is IndexExpression -> true
            is FunctionCallExpression -> expression.target != null
            else -> false
        }

    /** Whether [query] would take a following [clause] of an enclosing query as its own. */
    private fun takes(query: QueryExpression, clause: QueryClause): Boolean {
        val last = clausesOf(query).lastOrNull() ?: return true
        return clause > last || (clause == QueryClause.INCLUSION && last == QueryClause.INCLUSION)
    }

    private fun clausesOf(query: QueryExpression): List<QueryClause> =
        listOfNotNull(
            QueryClause.LET.takeIf { query.lets.isNotEmpty() },
            QueryClause.INCLUSION.takeIf { query.inclusions.isNotEmpty() },
            QueryClause.WHERE.takeIf { query.where != null },
            QueryClause.RESULT.takeIf { query.aggregate != null || query.result != null },
            QueryClause.SORT.takeIf { query.sort != null },
        )

    /** Whether [expression] is an expression term, rather than one of the forms of `expression`. */
    private fun isTerm(expression: Expression): Boolean =
        when (expression) {
            is OperatorUnaryExpression -> expression.operator != UnaryOperator.NOT
            is ExistsExpression,
            is CastExpression,
            is QueryExpression,
            is RetrieveExpression,
            is DurationBetweenExpression,
            is DifferenceBetweenExpression -> false
            else -> !startsWithOperand(expression) || operatorPrecedence(expression) >= TERM
        }

    /**
     * The lowest precedence of a following operator that [expression] would take into its last
     * operand, or `null` if it takes none. A prefix operator's operand takes the operators that
     * bind at least as tightly as it does, and the last expression of an `if`, `distinct`, `expand`
     * or query clause takes any.
     */
    private fun absorbsFrom(expression: Expression): Int? =
        prefixPrecedence(expression)
            ?: when (expression) {
                is IfExpression,
                is ListTransformExpression -> LOWEST
                is ExpandCollapseExpression -> if (expression.perPrecision == null) LOWEST else null
                is QueryExpression -> if (endsWithExpression(expression)) LOWEST else null
                else -> null
            }

    /** The precedence of a prefix operator, which a negative number is written as. */
    private fun prefixPrecedence(expression: Expression): Int? =
        when (expression) {
            is OperatorUnaryExpression ->
                when (expression.operator) {
                    UnaryOperator.NOT -> NOT
                    UnaryOperator.NEGATE,
                    UnaryOperator.POSITIVE -> POLARITY
                    UnaryOperator.SUCCESSOR,
                    UnaryOperator.PREDECESSOR -> TERM_PREFIX
                }
            is ExistsExpression -> EXISTS
            is TimeBoundaryExpression,
            is DateTimeComponentExpression,
            is DurationOfExpression,
            is DifferenceOfExpression,
            is WidthExpression,
            is ElementExtractorExpression -> TERM_PREFIX
            is LiteralExpression -> if (expression.literal.isNegative()) POLARITY else null
            else -> null
        }

    /** Whether a query's last clause ends with an expression, rather than an alias or `sort`. */
    private fun endsWithExpression(query: QueryExpression): Boolean =
        clausesOf(query).lastOrNull().let { it != null && it != QueryClause.SORT }

    /** The precedence of an operator that starts with its left operand. */
    private fun operatorPrecedence(expression: Expression): Int =
        when (expression) {
            is OperatorBinaryExpression -> precedence(expression.operator)
            is MembershipExpression -> MEMBERSHIP
            is IntervalRelationExpression -> TIMING
            is BetweenExpression -> BETWEEN
            is IsExpression,
            is AsExpression -> TYPE_TEST
            is BooleanTestExpression -> BOOLEAN_TEST
            else -> POSTFIX
        }

    private fun precedence(operator: BinaryOperator): Int =
        when (operator) {
            BinaryOperator.IMPLIES -> IMPLIES
            BinaryOperator.OR,
            BinaryOperator.XOR -> OR
            BinaryOperator.AND -> AND
            BinaryOperator.EQUALS,
            BinaryOperator.NOT_EQUALS,
            BinaryOperator.EQUIVALENT,
            BinaryOperator.NOT_EQUIVALENT -> EQUALITY
            BinaryOperator.LT,
            BinaryOperator.LTE,
            BinaryOperator.GT,
            BinaryOperator.GTE -> INEQUALITY
            BinaryOperator.UNION,
            BinaryOperator.INTERSECT,
            BinaryOperator.EXCEPT -> SET
            BinaryOperator.ADD,
            BinaryOperator.SUBTRACT,
            BinaryOperator.CONCAT -> ADDITION
            BinaryOperator.MULTIPLY,
            BinaryOperator.DIVIDE,
            BinaryOperator.TRUNCATED_DIVIDE,
            BinaryOperator.MODULO -> MULTIPLICATION
            BinaryOperator.POWER -> POWER
        }

    private fun Literal.isNegative(): Boolean =
        when (this) {
            is IntLiteral -> value < 0
            is LongLiteral -> value < 0
            is DecimalLiteral -> value.toPlainString().startsWith("-")
            else -> false
        }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private fun unparenthesized(expression: Expression, follow: Follow) {
        when (expression) {
            is ParenthesizedExpression -> {
                write("(")
                expression(expression.expression, LOWEST, Follow.NOTHING)
                close(")", expression.locator)
            }
            is LiteralExpression -> literal(expression.literal)
            is IdentifierExpression -> qualifiedIdentifier(expression.name)
            is ExternalConstantExpression -> {
                write("%")
                write(Identifier(expression.name).written())
            }
            is OperatorBinaryExpression -> {
                val level = precedence(expression.operator)
                expression(expression.left, level, Follow.operator(level))
                write(" ${operatorTexts.getValue(expression.operator)} ")
                expression(expression.right, level + 1, follow)
            }
            is OperatorUnaryExpression -> unary(expression, follow)
            is ExistsExpression -> {
                write("exists ")
                expression(expression.operand, EXISTS, follow)
            }
            is MembershipExpression -> {
                expression(expression.left, MEMBERSHIP, Follow.operator(MEMBERSHIP))
                write(if (expression.operator == MembershipOperator.IN) " in " else " contains ")
                expression.precision?.let { write("$it of ") }
                expression(expression.right, MEMBERSHIP + 1, follow)
            }
            is IntervalRelationExpression -> {
                expression(expression.left, TIMING, Follow.operator(TIMING))
                write(" ")
                intervalPhrase(expression.phrase)
                write(" ")
                expression(expression.right, TIMING + 1, follow)
            }
            is BetweenExpression -> {
                expression(expression.input, BETWEEN, Follow.operator(BETWEEN))
                write(if (expression.properly) " properly between " else " between ")
                expression(expression.lower, TERM, Follow.operator(AND))
                write(" and ")
                expression(expression.upper, TERM, follow)
            }
            is DurationBetweenExpression -> {
                write("${expression.precision} between ")
                expression(expression.lower, TERM, Follow.operator(AND))
                write(" and ")
                expression(expression.upper, TERM, follow)
            }
            is DifferenceBetweenExpression -> {
                write("difference in ${expression.precision} between ")
                expression(expression.lower, TERM, Follow.operator(AND))
                write(" and ")
                expression(expression.upper, TERM, follow)
            }
            is DurationOfExpression ->
                prefix("duration in ${expression.precision} of", expression.operand, follow)
            is DifferenceOfExpression ->
                prefix("difference in ${expression.precision} of", expression.operand, follow)
            is WidthExpression -> prefix("width of", expression.operand, follow)
            is ElementExtractorExpression ->
                prefix(
                    when (expression.elementExtractorKind) {
                        ElementExtractorKind.SINGLETON -> "singleton from"
                        ElementExtractorKind.POINT -> "point from"
                    },
                    expression.operand,
                    follow,
                )
            is TimeBoundaryExpression ->
                prefix(
                    when (expression.timeBoundaryKind) {
                        TimeBoundaryKind.START -> "start of"
                        TimeBoundaryKind.END -> "end of"
                    },
                    expression.operand,
                    follow,
                )
            is DateTimeComponentExpression ->
                prefix("${componentText(expression.component)} from", expression.operand, follow)
            is TypeExtentExpression -> {
                write(
                    when (expression.typeExtentKind) {
                        TypeExtentKind.MINIMUM -> "minimum "
                        TypeExtentKind.MAXIMUM -> "maximum "
                    }
                )
                typeSpecifier(expression.type)
            }
            is ConversionExpression -> {
                write("convert ")
                expression(expression.operand, LOWEST, Follow.NOTHING)
                write(" to ")
                expression.destinationType?.let { typeSpecifier(it) }
                expression.destinationUnit?.let { write(unitText(it)) }
            }
            is IsExpression -> typeTest(expression.operand, "is", expression.type)
            is AsExpression -> typeTest(expression.operand, "as", expression.type)
            is CastExpression -> {
                write("cast ")
                expression(expression.operand, LOWEST, Follow.operator(TYPE_TEST))
                write(" as ")
                typeSpecifier(expression.type)
            }
            is BooleanTestExpression -> {
                expression(expression.operand, TYPE_TEST, Follow.operator(BOOLEAN_TEST))
                write(if (expression.negated) " is not " else " is ")
                write(
                    when (expression.booleanTestKind) {
                        BooleanTestKind.NULL -> "null"
                        BooleanTestKind.TRUE -> "true"
                        BooleanTestKind.FALSE -> "false"
                    }
                )
            }
            is FunctionCallExpression -> {
                expression.target?.let {
                    expression(it, POSTFIX, Follow.operator(POSTFIX))
                    write(".")
                }
                identifier(expression.function)
                write("(")
                commaSeparated(expression.arguments) { expression(it, LOWEST, Follow.NOTHING) }
                close(")", expression.locator)
            }
            is PropertyAccessExpression -> {
                expression(expression.target, POSTFIX, Follow.operator(POSTFIX))
                write(".")
                identifier(expression.property)
            }
            is IndexExpression -> {
                expression(expression.target, POSTFIX, Follow.operator(POSTFIX))
                write("[")
                expression(expression.index, LOWEST, Follow.NOTHING)
                close("]", expression.locator)
            }
            is IfExpression -> {
                write("if ")
                expression(expression.condition, LOWEST, Follow.NOTHING)
                write(" then ")
                expression(expression.thenBranch, LOWEST, Follow.NOTHING)
                write(" else ")
                expression(expression.elseBranch, LOWEST, follow)
            }
            is CaseExpression -> case(expression)
            is ListTransformExpression -> {
                write(
                    when (expression.listTransformKind) {
                        ListTransformKind.DISTINCT -> "distinct "
                        ListTransformKind.FLATTEN -> "flatten "
                    }
                )
                expression(expression.operand, LOWEST, follow)
            }
            is ExpandCollapseExpression -> expandCollapse(expression, follow)
            is RetrieveExpression -> retrieve(expression)
            is QueryExpression -> query(expression)
            is UnsupportedExpression ->
                throw IllegalArgumentException("Cannot print unsupported ${expression.description}")
        }
    }

    private fun unary(expression: OperatorUnaryExpression, follow: Follow) {
        when (expression.operator) {
            UnaryOperator.NOT -> {
                write("not ")
                expression(expression.operand, NOT, follow)
            }
            UnaryOperator.NEGATE,
            UnaryOperator.POSITIVE -> {
                write(if (expression.operator == UnaryOperator.NEGATE) "-" else "+")
                // `- -1`, not `--1`
                if (absorbsFrom(expression.operand) == POLARITY) write(" ")
                expression(expression.operand, POLARITY, follow)
            }
            UnaryOperator.SUCCESSOR -> prefix("successor of", expression.operand, follow)
            UnaryOperator.PREDECESSOR -> prefix("predecessor of", expression.operand, follow)
        }
    }

    /** A keyword phrase followed by an expression term, such as `start of X`. */
    private fun prefix(keywords: String, operand: Expression, follow: Follow) {
        write("$keywords ")
        expression(operand, TERM_PREFIX, follow)
    }

    private fun typeTest(operand: Expression, keyword: String, type: TypeSpecifier) {
        expression(operand, TYPE_TEST, Follow.operator(TYPE_TEST))
        write(" $keyword ")
        typeSpecifier(type)
    }

    private fun case(expression: CaseExpression) {
        write("case")
        expression.comparand?.let {
            write(" ")
            expression(it, LOWEST, Follow.NOTHING)
        }
        indented {
            for (item in expression.cases) {
                newline()
                flush(item.locator)
                write("when ")
                expression(item.condition, LOWEST, Follow.NOTHING)
                write(" then ")
                expression(item.result, LOWEST, Follow.NOTHING)
            }
            newline()
            write("else ")
            expression(expression.elseResult, LOWEST, Follow.NOTHING)
        }
        newline()
        close("end", expression.locator)
    }

    private fun expandCollapse(expression: ExpandCollapseExpression, follow: Follow) {
        write(
            when (expression.expandCollapseKind) {
                ExpandCollapseKind.EXPAND -> "expand "
                ExpandCollapseKind.COLLAPSE -> "collapse "
            }
        )
        val per = expression.perPrecision != null || expression.perExpression != null
        expression(expression.operand, LOWEST, if (per) Follow.NOTHING else follow)
        expression.perPrecision?.let { write(" per $it") }
        expression.perExpression?.let {
            write(" per ")
            expression(it, LOWEST, follow)
        }
    }

    private fun componentText(component: DateTimeComponent): String =
        when (component) {
            DateTimeComponent.TIMEZONE_OFFSET -> "timezoneoffset"
            else -> component.name.lowercase()
        }

    // Literals

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private fun literal(literal: Literal) {
        flush(literal.locator)
        when (literal) {
            is StringLiteral -> write(quote('\'', literal.value))
            is IntLiteral -> write(literal.value.toString())
            is LongLiteral -> write("${literal.value}L")
            is DecimalLiteral -> write(literal.value.toPlainString())
            is BooleanLiteral -> write(literal.value.toString())
            is NullLiteral -> write("null")
            is QuantityLiteral -> quantity(literal)
            is DateTimeLiteral -> write(literal.text)
            is TimeLiteral -> write(literal.text)
            is RatioLiteral -> {
                quantity(literal.numerator)
                write(":")
                quantity(literal.denominator)
            }
            is TupleLiteral -> {
                write("Tuple ")
                elements(literal.elements, literal.locator)
            }
            is InstanceLiteral -> {
                literal.type?.let {
                    typeSpecifier(it)
                    write(" ")
                } ?: write("Tuple ")
                elements(literal.elements, literal.locator)
            }
            is IntervalLiteral -> {
                write(if (literal.lowerClosed) "Interval[" else "Interval(")
                expression(literal.lower, LOWEST, Follow.NOTHING)
                write(", ")
                expression(literal.upper, LOWEST, Follow.NOTHING)
                close(if (literal.upperClosed) "]" else ")", literal.locator)
            }
            is ListLiteral -> {
                literal.elementType?.let {
                    write("List<")
                    typeSpecifier(it)
                    write("> ")
                }
                if (literal.elements.isEmpty()) {
                    write("{ }")
                } else {
                    write("{ ")
                    commaSeparated(literal.elements) { expression(it, LOWEST, Follow.NOTHING) }
                    close(" }", literal.locator)
                }
            }
            is CodeLiteral -> code(literal)
            is ConceptLiteral -> {
                write("Concept { ")
                commaSeparated(literal.codes) { code(it) }
                close(" }", literal.locator)
                literal.display?.let { display(it) }
            }
        }
    }

    private fun quantity(quantity: QuantityLiteral) {
        flush(quantity.locator)
        write(quantity.value)
        quantity.unit?.let { write(" " + unitText(it)) }
    }

    /** A calendar unit as a keyword, any other unit as a UCUM string. */
    private fun unitText(unit: String): String =
        if (unit in calendarUnits) unit else quote('\'', unit)

    private fun code(code: CodeLiteral) {
        flush(code.locator)
        write("Code ")
        write(quote('\'', code.code))
        write(" from ")
        terminologyReference(code.system)
        code.display?.let { display(it) }
    }

    private fun elements(elements: List<TupleElementValue>, locator: Locator) {
        if (elements.isEmpty()) {
            close("{ : }", locator)
            return
        }
        write("{ ")
        commaSeparated(elements) {
            identifier(it.name)
            write(": ")
            expression(it.expression, LOWEST, Follow.NOTHING)
        }
        close(" }", locator)
    }

    // Queries

    private fun retrieve(retrieve: RetrieveExpression) {
        write("[")
        retrieve.context?.let {
            qualifiedIdentifier(it)
            write(" -> ")
        }
        typeSpecifier(retrieve.typeSpecifier)
        retrieve.terminology?.let { restriction ->
            write(": ")
            retrieve.codePath?.let { path ->
                qualifiedIdentifier(path)
                write(
                    when (retrieve.comparator) {
                        TerminologyComparator.EQUALS -> " = "
                        TerminologyComparator.EQUIVALENT -> " ~ "
                        TerminologyComparator.IN,
                        null -> " in "
                    }
                )
            }
            when (val terminology = restriction.terminology) {
                is IdentifierExpression -> qualifiedIdentifier(terminology.name)
                else -> expression(terminology, LOWEST, Follow.NOTHING)
            }
        }
        close("]", retrieve.locator)
    }

    private fun query(query: QueryExpression) {
        if (query.sources.size > 1) write("from ")
        commaSeparated(query.sources) { aliasedSource(it) }
        val clauses = clauses(query)
        indented {
            clauses.forEachIndexed { index, (_, write) ->
                newline()
                write(
                    clauses.getOrNull(index + 1)?.let { Follow.clause(it.first) } ?: Follow.NOTHING
                )
            }
        }
    }

    /** The clauses of [query], each with a writer given what follows its last expression. */
    private fun clauses(query: QueryExpression): List<Pair<QueryClause, (Follow) -> Unit>> =
        buildList {
            if (query.lets.isNotEmpty()) {
                add(
                    QueryClause.LET to
                        { follow: Follow ->
                            write("let ")
                            commaSeparated(query.lets) { item ->
                                identifier(item.identifier)
                                write(": ")
                                val last = item === query.lets.last()
                                expression(
                                    item.expression,
                                    LOWEST,
                                    if (last) follow else Follow.NOTHING,
                                )
                            }
                        }
                )
            }
            for (inclusion in query.inclusions) {
                add(QueryClause.INCLUSION to { follow: Follow -> inclusion(inclusion, follow) })
            }
            query.where?.let { where ->
                add(
                    QueryClause.WHERE to
                        { follow: Follow ->
                            write("where ")
                            expression(where, LOWEST, follow)
                        }
                )
            }
            query.aggregate?.let { aggregate ->
                add(QueryClause.RESULT to { follow: Follow -> aggregate(aggregate, follow) })
            }
            query.result?.let { result ->
                add(
                    QueryClause.RESULT to
                        { follow: Follow ->
                            write("return ")
                            if (result.all) write("all ")
                            if (result.distinct) write("distinct ")
                            expression(result.expression, LOWEST, follow)
                        }
                )
            }
            query.sort?.let { sort -> add(QueryClause.SORT to { _: Follow -> sort(sort) }) }
        }

    private fun aliasedSource(source: AliasedQuerySource) {
        flush(source.locator)
        when (val querySource = source.source) {
            is RetrieveExpression -> retrieve(querySource)
            is ExpressionQuerySource ->
                when (val expression = querySource.expression) {
                    is IdentifierExpression -> qualifiedIdentifier(expression.name)
                    else -> {
                        write("(")
                        expression(expression, LOWEST, Follow.NOTHING)
                        close(")", querySource.locator)
                    }
                }
        }
        write(" ")
        identifier(source.alias)
    }

    private fun inclusion(inclusion: QueryInclusionClause, follow: Follow) {
        flush(inclusion.locator)
        val (keyword, source, condition) =
            when (inclusion) {
                is WithClause -> Triple("with ", inclusion.source, inclusion.condition)
                is WithoutClause -> Triple("without ", inclusion.source, inclusion.condition)
            }
        write(keyword)
        aliasedSource(source)
        write(" such that ")
        expression(condition, LOWEST, follow)
    }

    private fun aggregate(aggregate: AggregateClause, follow: Follow) {
        flush(aggregate.locator)
        write("aggregate ")
        if (aggregate.distinct) write("distinct ")
        identifier(aggregate.identifier)
        aggregate.starting?.let {
            write(" starting ")
            if (isSimpleStartingValue(it)) {
                expression(it, LOWEST, Follow.NOTHING)
            } else {
                write("(")
                expression(it, LOWEST, Follow.NOTHING)
                write(")")
            }
        }
        write(": ")
        expression(aggregate.expression, LOWEST, follow)
    }

    /** A string, number or quantity, which `starting` takes without parentheses. */
    private fun isSimpleStartingValue(expression: Expression): Boolean {
        val literal = (expression as? LiteralExpression)?.literal
        return literal is StringLiteral ||
            literal is QuantityLiteral ||
            ((literal is IntLiteral || literal is DecimalLiteral) && !literal.isNegative())
    }

    private fun sort(sort: SortClause) {
        flush(sort.locator)
        write("sort ")
        sort.direction?.let {
            write(directionText(it))
            return
        }
        write("by ")
        commaSeparated(sort.items) { item ->
            expression(item.expression, TERM, Follow.NOTHING)
            item.direction?.let { write(" " + directionText(it)) }
        }
    }

    private fun directionText(direction: SortDirection): String =
        when (direction) {
            SortDirection.ASC -> "asc"
            SortDirection.ASCENDING -> "ascending"
            SortDirection.DESC -> "desc"
            SortDirection.DESCENDING -> "descending"
        }

    // Interval operator phrases

    @Suppress("CyclomaticComplexMethod")
    private fun intervalPhrase(phrase: IntervalOperatorPhrase) {
        flush(phrase.locator)
        val words =
            when (phrase) {
                is ConcurrentIntervalPhrase -> concurrentWords(phrase)
                is IncludesIntervalPhrase ->
                    listOfNotNull(
                        "properly".takeIf { phrase.proper },
                        "includes",
                        phrase.precision?.let { "$it of" },
                        phrase.rightBoundary?.let(::rightBoundaryText),
                    )
                is IncludedInIntervalPhrase ->
                    listOfNotNull(
                        phrase.leftBoundary?.let(::leftBoundaryText),
                        "properly".takeIf { phrase.proper },
                        when (phrase.variant) {
                            InclusionVariant.DURING -> "during"
                            InclusionVariant.INCLUDED_IN -> "included in"
                        },
                        phrase.precision?.let { "$it of" },
                    )
                is BeforeOrAfterIntervalPhrase -> beforeOrAfterWords(phrase)
                is WithinIntervalPhrase -> withinWords(phrase)
                is MeetsIntervalPhrase ->
                    listOfNotNull(
                        "meets",
                        phrase.direction?.let(::directionText),
                        phrase.precision?.let { "$it of" },
                    )
                is OverlapsIntervalPhrase ->
                    listOfNotNull(
                        "overlaps",
                        phrase.direction?.let(::directionText),
                        phrase.precision?.let { "$it of" },
                    )
                is StartsIntervalPhrase ->
                    listOfNotNull("starts", phrase.precision?.let { "$it of" })
                is EndsIntervalPhrase -> listOfNotNull("ends", phrase.precision?.let { "$it of" })
            }
        write(words.joinToString(" "))
    }

    private fun concurrentWords(phrase: ConcurrentIntervalPhrase): List<String> =
        listOfNotNull(
            phrase.leftBoundary?.let(::leftBoundaryText),
            "same",
            phrase.precision,
            when (phrase.qualifier) {
                ConcurrentQualifier.AS -> "as"
                ConcurrentQualifier.OR_BEFORE -> "or before"
                ConcurrentQualifier.OR_AFTER -> "or after"
            },
            phrase.rightBoundary?.let(::rightBoundaryText),
        )

    private fun beforeOrAfterWords(phrase: BeforeOrAfterIntervalPhrase): List<String> =
        listOfNotNull(
            phrase.leftBoundary?.let(::leftBoundaryText),
            phrase.offset?.let(::offsetText),
            relationshipText(phrase.relationship),
            phrase.precision?.let { "$it of" },
            phrase.rightBoundary?.let(::rightBoundaryText),
        )

    private fun withinWords(phrase: WithinIntervalPhrase): List<String> =
        listOfNotNull(
            phrase.leftBoundary?.let(::leftBoundaryText),
            "properly".takeIf { phrase.proper },
            "within",
            quantityText(phrase.quantity),
            "of",
            phrase.rightBoundary?.let(::rightBoundaryText),
        )

    private fun leftBoundaryText(boundary: IntervalBoundarySelector): String =
        when (boundary) {
            IntervalBoundarySelector.START -> "starts"
            IntervalBoundarySelector.END -> "ends"
            IntervalBoundarySelector.OCCURS -> "occurs"
        }

    private fun rightBoundaryText(boundary: IntervalBoundarySelector): String =
        when (boundary) {
            IntervalBoundarySelector.START -> "start"
            IntervalBoundarySelector.END -> "end"
            IntervalBoundarySelector.OCCURS -> "occurs"
        }

    private fun directionText(direction: TemporalRelationshipDirection): String =
        when (direction) {
            TemporalRelationshipDirection.BEFORE -> "before"
            TemporalRelationshipDirection.AFTER -> "after"
        }

    private fun relationshipText(relationship: TemporalRelationshipPhrase): String =
        (if (relationship.inclusive) "on or " else "") + directionText(relationship.direction)

    private fun offsetText(offset: QuantityOffset): String =
        when {
            offset.exclusiveQualifier != null ->
                when (offset.exclusiveQualifier) {
                    ExclusiveRelativeQualifier.LESS_THAN -> "less than "
                    ExclusiveRelativeQualifier.MORE_THAN -> "more than "
                } + quantityText(offset.quantity)
            else ->
                quantityText(offset.quantity) +
                    when (offset.offsetQualifier) {
                        OffsetRelativeQualifier.OR_MORE -> " or more"
                        OffsetRelativeQualifier.OR_LESS -> " or less"
                        null -> ""
                    }
        }

    private fun quantityText(quantity: QuantityLiteral): String =
        quantity.value + (quantity.unit?.let { " " + unitText(it) } ?: "")
}

/** How [this] is written: as it was in the source, or quoted if it cannot be written plain. */
private fun Identifier.written(): String =
    when (style) {
        IdentifierStyle.PLAIN ->
            if (plainIdentifier.matches(value) || value in specialIdentifiers) value
            else quote('"', value)
        IdentifierStyle.QUOTED -> quote('"', value)
        IdentifierStyle.DELIMITED -> quote('`', value)
    }

/** [value] between [delimiter]s, escaping what would end or change it. */
private fun quote(delimiter: Char, value: String): String = buildString {
    append(delimiter)
    for (c in value) {
        when (c) {
            delimiter,
            '\\' -> append('\\').append(c)
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            '\u000c' -> append("\\f")
            else -> append(c)
        }
    }
    append(delimiter)
}
