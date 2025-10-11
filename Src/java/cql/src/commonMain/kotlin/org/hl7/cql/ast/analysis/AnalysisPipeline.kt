package org.hl7.cql.ast.analysis

import org.hl7.cql.ast.AggregateClause
import org.hl7.cql.ast.AliasedQuerySource
import org.hl7.cql.ast.CodeDefinition
import org.hl7.cql.ast.CodeSystemDefinition
import org.hl7.cql.ast.ConceptDefinition
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.ExpressionQuerySource
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.Identifier
import org.hl7.cql.ast.IncludeDefinition
import org.hl7.cql.ast.LetClauseItem
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.Locator
import org.hl7.cql.ast.ParameterDefinition
import org.hl7.cql.ast.Problem
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.ReturnClause
import org.hl7.cql.ast.SortClause
import org.hl7.cql.ast.Statement
import org.hl7.cql.ast.UsingDefinition
import org.hl7.cql.ast.ValueSetDefinition
import org.hl7.cql.ast.AstWalker
import org.hl7.cql.ast.WithClause
import org.hl7.cql.ast.WithoutClause

class AnalysisPipeline(private val analyses: List<AstAnalysis>) : AstWalker() {

    private val scopeTracker = ScopeTracker()

    fun analyze(library: Library): List<Problem> {
        reset()
        analyses.forEach { it.beginLibrary(library) }
        pushScope()
        super.visitLibrary(library)
        popScope()
        analyses.forEach { it.endLibrary(library) }
        return collectProblems()
    }

    fun analyze(statement: Statement): List<Problem> {
        reset()
        pushScope()
        visitStatement(statement)
        popScope()
        return collectProblems()
    }

    fun analyze(expression: Expression): List<Problem> {
        reset()
        pushScope()
        visitExpression(expression)
        popScope()
        return collectProblems()
    }

    override fun visitUsingDefinition(definition: UsingDefinition) {
        definition.alias?.let { declare(it, definition.locator) }
    }

    override fun visitIncludeDefinition(definition: IncludeDefinition) {
        definition.alias?.let { declare(it, definition.locator) }
    }

    override fun visitExpressionDefinition(statement: ExpressionDefinition) {
        declare(statement.name, statement.locator)
        super.visitExpressionDefinition(statement)
    }

    override fun visitFunctionDefinition(statement: FunctionDefinition) {
        declare(statement.name, statement.locator)
        pushScope()
        super.visitFunctionDefinition(statement)
        popScope()
    }

    override fun visitExpressionFunctionBody(body: ExpressionFunctionBody) {
        super.visitExpressionFunctionBody(body)
    }

    override fun visitParameterDefinition(definition: ParameterDefinition) {
        declare(definition.name, definition.locator)
        super.visitParameterDefinition(definition)
    }

    override fun visitCodeSystemDefinition(definition: CodeSystemDefinition) {
        declare(definition.name, definition.locator)
    }

    override fun visitValueSetDefinition(definition: ValueSetDefinition) {
        declare(definition.name, definition.locator)
    }

    override fun visitCodeDefinition(definition: CodeDefinition) {
        declare(definition.name, definition.locator)
    }

    override fun visitConceptDefinition(definition: ConceptDefinition) {
        declare(definition.name, definition.locator)
    }

    override fun visitQueryExpression(expression: QueryExpression) {
        pushScope()
        super.visitQueryExpression(expression)
        popScope()
    }

    override fun visitAliasedQuerySource(source: AliasedQuerySource) {
        super.visitAliasedQuerySource(source)
        declare(source.alias, source.locator)
    }

    override fun visitExpressionQuerySource(source: ExpressionQuerySource) {
        visitExpression(source.expression)
    }

    override fun visitLetClauseItem(item: LetClauseItem) {
        visitExpression(item.expression)
        declare(item.identifier, item.locator)
    }

    override fun visitAggregateClause(clause: AggregateClause) {
        pushScope()
        declare(clause.identifier, clause.locator)
        super.visitAggregateClause(clause)
        popScope()
    }

    override fun visitWithClause(clause: WithClause) {
        pushScope()
        super.visitWithClause(clause)
        popScope()
    }

    override fun visitWithoutClause(clause: WithoutClause) {
        pushScope()
        super.visitWithoutClause(clause)
        popScope()
    }

    override fun visitReturnClause(clause: ReturnClause) {
        super.visitReturnClause(clause)
    }

    override fun visitSortClause(clause: SortClause) {
        super.visitSortClause(clause)
    }

    override fun visitOperandDefinition(definition: org.hl7.cql.ast.OperandDefinition) {
        declare(definition.name, definition.locator)
    }

    private fun reset() {
        scopeTracker.reset()
        analyses.forEach { it.reset() }
    }

    private fun pushScope() {
        scopeTracker.push()
        analyses.forEach { it.enterScope() }
    }

    private fun popScope() {
        analyses.asReversed().forEach { it.exitScope() }
        scopeTracker.pop()
    }

    private fun declare(identifier: Identifier, locator: Locator) {
        val previous = scopeTracker.declare(identifier, locator)
        analyses.forEach { it.onDeclaration(identifier, locator, previous) }
    }

    private fun collectProblems(): List<Problem> =
        analyses.flatMap { it.collectProblems() }
}
