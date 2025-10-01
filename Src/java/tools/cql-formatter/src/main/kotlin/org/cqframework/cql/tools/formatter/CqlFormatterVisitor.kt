package org.cqframework.cql.tools.formatter

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams.fromStream
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.RuleNode
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import org.antlr.v4.kotlinruntime.tree.TerminalNodeImpl
import org.cqframework.cql.gen.cqlBaseVisitor
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import org.cqframework.cql.gen.cqlParser.CaseExpressionItemContext
import org.cqframework.cql.gen.cqlParser.CaseExpressionTermContext
import org.cqframework.cql.gen.cqlParser.CodeDefinitionContext
import org.cqframework.cql.gen.cqlParser.CodesystemDefinitionContext
import org.cqframework.cql.gen.cqlParser.ConceptDefinitionContext
import org.cqframework.cql.gen.cqlParser.ContextDefinitionContext
import org.cqframework.cql.gen.cqlParser.FunctionDefinitionContext
import org.cqframework.cql.gen.cqlParser.InFixSetExpressionContext
import org.cqframework.cql.gen.cqlParser.IncludeDefinitionContext
import org.cqframework.cql.gen.cqlParser.LetClauseContext
import org.cqframework.cql.gen.cqlParser.LibraryContext
import org.cqframework.cql.gen.cqlParser.LibraryDefinitionContext
import org.cqframework.cql.gen.cqlParser.ParameterDefinitionContext
import org.cqframework.cql.gen.cqlParser.QueryInclusionClauseContext
import org.cqframework.cql.gen.cqlParser.RetrieveContext
import org.cqframework.cql.gen.cqlParser.ReturnClauseContext
import org.cqframework.cql.gen.cqlParser.SortClauseContext
import org.cqframework.cql.gen.cqlParser.SourceClauseContext
import org.cqframework.cql.gen.cqlParser.Tokens.EOF
import org.cqframework.cql.gen.cqlParser.TupleElementDefinitionContext
import org.cqframework.cql.gen.cqlParser.TupleElementSelectorContext
import org.cqframework.cql.gen.cqlParser.TupleSelectorContext
import org.cqframework.cql.gen.cqlParser.TupleTypeSpecifierContext
import org.cqframework.cql.gen.cqlParser.UsingDefinitionContext
import org.cqframework.cql.gen.cqlParser.ValuesetDefinitionContext
import org.cqframework.cql.gen.cqlParser.WhereClauseContext
import org.cqframework.cql.gen.cqlParser.WithClauseContext
import org.cqframework.cql.gen.cqlParser.WithoutClauseContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors

/**
 * Created by Bryn on 7/5/2017.
 */
class CqlFormatterVisitor : cqlBaseVisitor<Any?>() {
    val useSpaces: Boolean = true

    val indentSize: Int = 2

    private var output: StringBuilder? = null

    private val space = '\u0020'
    private val tab = '\t'
    private val newLine = "\r\n"

    private var currentLine = 0
    private var onNewLine = false
    private var needsWhitespace = false
    private var indentLevel = 0
    private var previousIndentLevel = 0

    private var isFirstTupleElement = false

    private var currentSection: String? = null
    private var sectionCount = 0

    private fun newSection(section: String) {
        if (hasSectionContent()) {
            resetIndentLevel()
            newLine()
        }
        sectionCount = 0
        currentSection = section
    }

    private fun needsSectionSeparator(section: String): Boolean {
        when (section) {
            "statement" -> return true
            else -> return false
        }
    }

    private fun ensureSectionSeparator() {
        if (needsSectionSeparator(currentSection!!) && hasSectionContent()) {
            resetIndentLevel()
            newLine()
        }
    }

    private fun addToSection(section: String) {
        if (section != currentSection) {
            newSection(section)
        }

        ensureSectionSeparator()

        sectionCount++
    }

    private fun hasSectionContent(): Boolean {
        return sectionCount > 0
    }

    private var typeSpecifierLevel = 0

    private fun enterTypeSpecifier() {
        typeSpecifierLevel++
    }

    private fun exitTypeSpecifier() {
        typeSpecifierLevel--
    }

    private fun inTypeSpecifier(): Boolean {
        return typeSpecifierLevel > 0
    }

    private var functionDefinitionLevel = 0

    private fun enterFunctionDefinition() {
        functionDefinitionLevel++
    }

    private fun exitFunctionDefinition() {
        functionDefinitionLevel--
    }

    private fun inFunctionDefinition(): Boolean {
        return functionDefinitionLevel > 0
    }

    private var functionInvocationLevel = 0

    private fun enterFunctionInvocation() {
        functionInvocationLevel++
    }

    private fun exitFunctionInvocation() {
        functionInvocationLevel--
    }

    private fun inFunctionInvocation(): Boolean {
        return functionInvocationLevel > 0
    }

    private var retrieveLevel = 0

    private fun enterRetrieve() {
        retrieveLevel++
    }

    private fun exitRetrieve() {
        retrieveLevel--
    }

    private fun inRetrieve(): Boolean {
        return retrieveLevel > 0
    }

    private fun enterClause() {
        increaseIndentLevel()
        newLine()
    }

    private fun exitClause() {
        decreaseIndentLevel()
    }

    private var groups: Stack<Int>? = null

    private fun enterGroup() {
        increaseIndentLevel()
        groups!!.push(currentLine)
    }

    private fun exitGroup() {
        val groupStartLine = groups!!.pop()
        decreaseIndentLevel()
        if (currentLine != groupStartLine) {
            newLine()
        }
    }

    private fun needsWhitespaceBefore(terminal: String): Boolean {
        if (terminal.trim { it <= ' ' }.isEmpty() || terminal.startsWith("//") || terminal.startsWith("/*")) {
            return false
        }

        when (terminal) {
            ":" -> return false
            "." -> return false
            "," -> return false
            "<" -> return !inTypeSpecifier()
            ">" -> return !inTypeSpecifier()
            "(" -> return !inFunctionDefinition() && !inFunctionInvocation()
            ")" -> return !inFunctionDefinition() && !inFunctionInvocation()
            "[" -> return inRetrieve()
            "]" -> return false
            "starts" -> return !inFunctionDefinition() || !inFunctionInvocation()
            else -> return true
        }
    }

    private fun needsWhitespaceAfter(terminal: String): Boolean {
        when (terminal) {
            "." -> return false
            "<" -> return !inTypeSpecifier()
            ">" -> return !inTypeSpecifier()
            "(" -> return !inFunctionDefinition() && !inFunctionInvocation()
            ")" -> return !inFunctionDefinition() || !inFunctionInvocation()
            "[" -> return false
            "]" -> return inRetrieve()
            else -> return true
        }
    }

    private fun appendComment(token: CommentToken) {
        // get the whitespace at the end of output
        val out = output.toString()
        val whitespace = out.substring(out.replace("\\s+$".toRegex(), "").length)
        if (whitespace != token.whitespaceBefore) {
            val whitespaceBefore = token.whitespaceBefore
            output = StringBuilder()
                .append(out.substring(0, out.length - whitespace.length))
                .append(whitespaceBefore)
        }
        output!!.append(token.token.text).append(whitespace)
        newLine()
    }

    private fun appendTerminal(terminal: String) {
        if (needsWhitespaceBefore(terminal)) {
            ensureWhitespace()
        }
        if (terminal == "else") {
            increaseIndentLevel()
            newLine()
            decreaseIndentLevel()
        }
        if (terminal == "end" && (inFunctionInvocation() || inFunctionDefinition())) {
            newLine()
        }
        output!!.append(terminal)
        onNewLine = false
        needsWhitespace = needsWhitespaceAfter(terminal)
    }

    private fun increaseIndentLevel() {
        previousIndentLevel = indentLevel
        indentLevel = previousIndentLevel + 1
    }

    private fun decreaseIndentLevel() {
        previousIndentLevel = indentLevel
        indentLevel = previousIndentLevel - 1
    }

    private fun resetIndentLevel() {
        indentLevel = 0
        previousIndentLevel = 0
    }

    private fun indent() {
        val indent = indentLevel * (if (useSpaces) indentSize else 1)
        for (i in 0..<indent) {
            output!!.append(if (useSpaces) space else tab)
        }
    }

    private fun newLine() {
        output!!.append(newLine)
        currentLine++
        indent()
        onNewLine = true
    }

    private fun newConstruct(section: String) {
        resetIndentLevel()
        newLine()
        addToSection(section)
    }

    private fun ensureWhitespace() {
        if (!onNewLine && needsWhitespace) {
            output!!.append(space)
        }
    }

    private fun reset() {
        resetIndentLevel()
        currentLine = 1
        onNewLine = true
        output = StringBuilder()
        groups = Stack<Int>()
    }

    override fun visitLibrary(ctx: LibraryContext): Any {
        reset()
        super.visitLibrary(ctx)
        resetIndentLevel()
        //        newLine();
        return output.toString()
    }

    override fun visitChildren(node: RuleNode): Any? {
        var result = defaultResult()
        val n = node.childCount
        for (i in 0..<n) {
            if (!shouldVisitNextChild(node, result)) {
                break
            }

            val c = node.getChild(i)

            if ((node is TupleSelectorContext || node is TupleTypeSpecifierContext)
                && c is TerminalNodeImpl
            ) {
                if (c.symbol.text == "}") {
                    decreaseIndentLevel()
                    newLine()
                }
            }

            val childResult: Any? = c!!.accept(this)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    override fun visitLibraryDefinition(ctx: LibraryDefinitionContext): Any? {
        addToSection("library")
        return super.visitLibraryDefinition(ctx)
    }

    override fun visitUsingDefinition(ctx: UsingDefinitionContext): Any? {
        newConstruct("using")
        return super.visitUsingDefinition(ctx)
    }

    override fun visitIncludeDefinition(ctx: IncludeDefinitionContext): Any? {
        newConstruct("include")
        return super.visitIncludeDefinition(ctx)
    }

    override fun visitParameterDefinition(ctx: ParameterDefinitionContext): Any? {
        newConstruct("parameter")
        return super.visitParameterDefinition(ctx)
    }

    override fun visitCodesystemDefinition(ctx: CodesystemDefinitionContext): Any? {
        newConstruct("codesystem")
        return super.visitCodesystemDefinition(ctx)
    }

    override fun visitValuesetDefinition(ctx: ValuesetDefinitionContext): Any? {
        newConstruct("valueset")
        return super.visitValuesetDefinition(ctx)
    }

    override fun visitCodeDefinition(ctx: CodeDefinitionContext): Any? {
        newConstruct("code")
        return super.visitCodeDefinition(ctx)
    }

    override fun visitConceptDefinition(ctx: ConceptDefinitionContext): Any? {
        newConstruct("concept")
        return super.visitConceptDefinition(ctx)
    }

    override fun visitTypeSpecifier(ctx: cqlParser.TypeSpecifierContext): Any? {
        enterTypeSpecifier()
        try {
            return super.visitTypeSpecifier(ctx)
        } finally {
            exitTypeSpecifier()
        }
    }

    override fun visitTupleTypeSpecifier(ctx: TupleTypeSpecifierContext): Any? {
        isFirstTupleElement = true
        return super.visitTupleTypeSpecifier(ctx)
    }

    override fun visitTupleElementDefinition(ctx: TupleElementDefinitionContext): Any? {
        if (isFirstTupleElement) {
            increaseIndentLevel()
            isFirstTupleElement = false
        }
        newLine()
        return super.visitTupleElementDefinition(ctx)
    }

    override fun visitExpressionDefinition(ctx: cqlParser.ExpressionDefinitionContext): Any? {
        newConstruct("statement")
        var result = defaultResult()
        val n: Int = ctx.childCount
        for (i in 0..<n) {
            if (!shouldVisitNextChild(ctx, result)) {
                break
            }

            val c: ParseTree = ctx.getChild(i)!!
            if (c === ctx.expression()) {
                enterClause()
            }
            try {
                val childResult: Any? = c.accept(this)
                result = aggregateResult(result, childResult)
            } finally {
                if (c === ctx.expression()) {
                    exitClause()
                }
            }
        }

        return result
    }

    override fun visitContextDefinition(ctx: ContextDefinitionContext): Any? {
        newConstruct("statement")
        return super.visitContextDefinition(ctx)
    }

    override fun visitFunctionDefinition(ctx: FunctionDefinitionContext): Any? {
        newConstruct("statement")

        var result = defaultResult()
        val n: Int = ctx.childCount
        var clauseEntered = false
        try {
            for (i in 0..<n) {
                if (!shouldVisitNextChild(ctx, result)) {
                    break
                }

                val c: ParseTree = ctx.getChild(i)!!

                if (c.text == "(") {
                    enterFunctionDefinition()
                }

                val childResult: Any? = c.accept(this)
                result = aggregateResult(result, childResult)

                if (c.text == ")") {
                    exitFunctionDefinition()
                }

                if (c.text == ":") {
                    enterClause()
                    clauseEntered = true
                }
            }
        } finally {
            if (clauseEntered) {
                exitClause()
            }
        }

        return result
    }

    override fun visitQueryInclusionClause(ctx: QueryInclusionClauseContext): Any? {
        enterClause()
        try {
            return super.visitQueryInclusionClause(ctx)
        } finally {
            exitClause()
        }
    }

    private fun visitWithOrWithoutClause(ctx: ParserRuleContext): Any? {
        var result = defaultResult()
        val n = ctx.childCount
        var clauseEntered = false
        try {
            for (i in 0..<n) {
                if (!shouldVisitNextChild(ctx, result)) {
                    break
                }

                val c = ctx.getChild(i)
                if (c!!.text == "such that") {
                    enterClause()
                    clauseEntered = true
                }
                val childResult: Any? = c.accept(this)
                result = aggregateResult(result, childResult)
            }
        } finally {
            if (clauseEntered) {
                exitClause()
            }
        }

        return result
    }

    override fun visitWithClause(ctx: WithClauseContext): Any? {
        return visitWithOrWithoutClause(ctx)
    }

    override fun visitWithoutClause(ctx: WithoutClauseContext): Any? {
        return visitWithOrWithoutClause(ctx)
    }

    override fun visitRetrieve(ctx: RetrieveContext): Any? {
        enterRetrieve()
        try {
            return super.visitRetrieve(ctx)
        } finally {
            exitRetrieve()
        }
    }

    override fun visitSourceClause(ctx: SourceClauseContext): Any? {
        var result = defaultResult()
        val n: Int = ctx.childCount
        var clauseEntered = false
        try {
            for (i in 0..<n) {
                if (!shouldVisitNextChild(ctx, result)) {
                    break
                }

                val c: ParseTree = ctx.getChild(i)!!

                if (i == 1) {
                    enterClause()
                    clauseEntered = true
                }

                if (i > 1 && c.text != ",") {
                    newLine()
                }

                val childResult: Any? = c.accept(this)
                result = aggregateResult(result, childResult)
            }
            return result
        } finally {
            if (clauseEntered) {
                exitClause()
            }
        }
    }

    //    @Override
    //    public Object visitSingleSourceClause(cqlParser.SingleSourceClauseContext ctx) {
    //        return super.visitSingleSourceClause(ctx);
    //    }
    //
    //    @Override
    //    public Object visitMultipleSourceClause(cqlParser.MultipleSourceClauseContext ctx) {
    //        Object result = defaultResult();
    //        int n = ctx.childCount;
    //        boolean clauseEntered = false;
    //        try {
    //            for (int i = 0; i < n; i++) {
    //                if (!shouldVisitNextChild(ctx, result)) {
    //                    break;
    //                }
    //
    //                ParseTree c = ctx.getChild(i);
    //
    //                if (i == 1) {
    //                    enterClause();
    //                    clauseEntered = true;
    //                }
    //
    //                if (i > 1 && !c.getText().equals(",")) {
    //                    newLine();
    //                }
    //
    //                Object childResult = c.accept(this);
    //                result = aggregateResult(result, childResult);
    //            }
    //            return result;
    //        }
    //        finally {
    //            if (clauseEntered) {
    //                exitClause();
    //            }
    //        }
    //    }
    //
    override fun visitLetClause(ctx: LetClauseContext): Any? {
        enterClause()
        try {
            var result = defaultResult()
            val n: Int = ctx.childCount
            for (i in 0..<n) {
                if (!shouldVisitNextChild(ctx, result)) {
                    break
                }

                val c: ParseTree = ctx.getChild(i)!!

                if (i > 1 && c.text != ",") {
                    newLine()
                }

                val childResult: Any? = c.accept(this)
                result = aggregateResult(result, childResult)
            }
            return result
        } finally {
            exitClause()
        }
    }

    override fun visitWhereClause(ctx: WhereClauseContext): Any? {
        enterClause()
        try {
            return super.visitWhereClause(ctx)
        } finally {
            exitClause()
        }
    }

    override fun visitReturnClause(ctx: ReturnClauseContext): Any? {
        enterClause()
        try {
            return super.visitReturnClause(ctx)
        } finally {
            exitClause()
        }
    }

    override fun visitSortClause(ctx: SortClauseContext): Any? {
        enterClause()
        try {
            return super.visitSortClause(ctx)
        } finally {
            exitClause()
        }
    }

    override fun visitInFixSetExpression(ctx: InFixSetExpressionContext): Any? {
        return visitBinaryClausedExpression(ctx)
    }

    override fun visitOrExpression(ctx: cqlParser.OrExpressionContext): Any? {
        return visitBinaryClausedExpression(ctx)
    }

    private fun visitBinaryClausedExpression(ctx: ParserRuleContext): Any? {
        var result = defaultResult()
        val n = ctx.childCount
        var clauseEntered = false
        try {
            for (i in 0..<n) {
                if (!shouldVisitNextChild(ctx, result)) {
                    break
                }

                val c = ctx.getChild(i)

                if (i == 1) {
                    enterClause()
                    clauseEntered = true
                }

                val childResult: Any? = c!!.accept(this)
                result = aggregateResult(result, childResult)
            }
            return result
        } finally {
            if (clauseEntered) {
                exitClause()
            }
        }
    }

    override fun visitAndExpression(ctx: cqlParser.AndExpressionContext): Any? {
        return visitBinaryClausedExpression(ctx)
    }

    private fun hasNeighborOnLine(ctx: ParserRuleContext): Boolean {
        var context = ctx.getParent()
        while (context != null) {
            if (context.start!!.startIndex < ctx.start!!.startIndex) {
                return context.start!!.line == ctx.start!!.line
            }
            context = context.getParent()
        }
        return false
    }

    override fun visitCaseExpressionTerm(ctx: CaseExpressionTermContext): Any? {
        if (hasNeighborOnLine(ctx)) {
            newLine()
            if (previousIndentLevel == indentLevel) {
                increaseIndentLevel()
            }
        }

        return super.visitCaseExpressionTerm(ctx)
    }

    override fun visitCaseExpressionItem(ctx: CaseExpressionItemContext): Any? {
        try {
            enterClause()
            return super.visitCaseExpressionItem(ctx)
        } finally {
            exitClause()
        }
    }

    override fun visitParenthesizedTerm(ctx: cqlParser.ParenthesizedTermContext): Any? {
        var result = defaultResult()
        val n: Int = ctx.childCount
        for (i in 0..<n) {
            if (!shouldVisitNextChild(ctx, result)) {
                break
            }

            val c: ParseTree = ctx.getChild(i)!!

            if (c === ctx.expression()) {
                enterGroup()
            }
            try {
                val childResult: Any? = c.accept(this)
                result = aggregateResult(result, childResult)
            } finally {
                if (c === ctx.expression()) {
                    exitGroup()
                }
            }
        }
        return result
    }

    override fun visitTupleSelector(ctx: TupleSelectorContext): Any? {
        isFirstTupleElement = true
        return super.visitTupleSelector(ctx)
    }

    override fun visitTupleElementSelector(ctx: TupleElementSelectorContext): Any? {
        if (isFirstTupleElement) {
            increaseIndentLevel()
            isFirstTupleElement = false
        }
        newLine()
        return super.visitTupleElementSelector(ctx)
    }

    override fun visitFunctionInvocation(ctx: cqlParser.FunctionInvocationContext): Any? {
        enterFunctionInvocation()
        try {
            return super.visitFunctionInvocation(ctx)
        } finally {
            exitFunctionInvocation()
        }
    }

    override fun visitFunction(ctx: cqlParser.FunctionContext): Any? {
        var result = defaultResult()
        val n: Int = ctx.childCount
        for (i in 0..<n) {
            if (!shouldVisitNextChild(ctx, result)) {
                break
            }

            val c: ParseTree = ctx.getChild(i)!!

            if (c === ctx.paramList()) {
                enterGroup()
            }
            try {
                val childResult: Any? = c.accept(this)
                result = aggregateResult(result, childResult)
            } finally {
                if (c === ctx.paramList()) {
                    exitGroup()
                }
            }
        }
        return result
    }

    override fun visitTerminal(node: TerminalNode): Any? {
        checkForComment(node)
        if (node.symbol.type != EOF) {
            appendTerminal(node.text)
        }
        return super.visitTerminal(node)
    }

    private fun checkForComment(node: TerminalNode) {
        var numComments = 0
        for (token in comments) {
            if (token.token.tokenIndex < node.symbol.tokenIndex) {
                appendComment(token)
                ++numComments
            }
        }
        while (numComments > 0) {
            comments.removeAt(--numComments)
        }
    }

    override fun defaultResult(): Any? {
        return null
    }

    private class CommentToken(val token: Token, val whitespaceBefore: String?)

    private class SyntaxErrorListener : BaseErrorListener() {
        val errors: MutableList<Exception> = ArrayList<Exception>()

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?
        ) {
            if (!(offendingSymbol as Token).text!!.trim { it <= ' ' }.isEmpty()) {
                errors.add(Exception(String.format("[%d:%d]: %s", line, charPositionInLine, msg)))
            }
        }
    }

    class FormatResult(var errors: List<Exception>, var output: String)
    companion object {
        private val comments: MutableList<CommentToken> = ArrayList<CommentToken>()

        @Throws(IOException::class)
        fun getFormattedOutput(`is`: InputStream): FormatResult {
            val `in` = fromStream(`is`)
            val lexer = cqlLexer(`in`)
            val tokens = CommonTokenStream(lexer)
            tokens.fill()
            populateComments(tokens)
            val parser = cqlParser(tokens)
            parser.addErrorListener(SyntaxErrorListener())
            parser.buildParseTree = true
            val tree: ParserRuleContext = parser.library()

            if ((parser.errorListeners[1] as SyntaxErrorListener).errors.size > 0) {
                return FormatResult(
                    (parser.errorListeners[1] as SyntaxErrorListener).errors, `in`.toString()
                )
            }

            val formatter = CqlFormatterVisitor()
            var output = formatter.visit(tree) as String

            if (comments.size > 0) {
                val eofComments = StringBuilder()
                for (comment in comments) {
                    eofComments.append(comment.whitespaceBefore).append(comment.token.text)
                }
                comments.clear()
                output += eofComments.toString()
            }

            return FormatResult(ArrayList<Exception>(), output)
        }

        fun getInputStreamAsString(`is`: InputStream): String {
            return BufferedReader(InputStreamReader(`is`)).lines().collect(Collectors.joining("\n"))
        }

        fun populateComments(tokens: CommonTokenStream) {
            for (token in tokens.tokens) {
                if (token.text!!.startsWith("//") || token.text!!.startsWith("/*")) {
                    val whitespace = if (token.tokenIndex < 1)
                        ""
                    else
                        tokens[token.tokenIndex - 1].text
                    comments.add(CommentToken(token, if (whitespace!!.matches("\\s+".toRegex())) whitespace else ""))
                }
            }
        }
    }
}
