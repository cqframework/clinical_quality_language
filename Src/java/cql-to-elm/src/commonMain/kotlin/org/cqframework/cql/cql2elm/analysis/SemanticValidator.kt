package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.AstWalker
import org.hl7.cql.ast.Library

/**
 * Walks the AST after type resolution and flags expressions with semantic errors. Codegen reads
 * these flags from the [SemanticModel] and emits `Null` for flagged expressions, matching the
 * translator's error recovery behavior.
 *
 * Detected errors:
 * - Unresolved identifiers (no resolution and no inferred type)
 * - Undeclared function calls (function name not found anywhere)
 * - Unmatched function signatures (name exists but arity doesn't match)
 * - Recursive function calls (matching arity but null type from circular reference)
 * - Function bodies with nested errors (entire body flagged)
 * - Invalid casts (e.g., list → scalar As expression)
 */
class SemanticValidator {
    fun validate(library: Library, symbolTable: SymbolTable, semanticModel: SemanticModel) {
        for ((_, exprDef) in symbolTable.expressionDefinitions) {
            validateExpression(exprDef.expression, symbolTable, semanticModel)
        }
        for ((_, funcDefs) in symbolTable.functionDefinitions) {
            for (funcDef in funcDefs) {
                val body = funcDef.body
                if (body is org.hl7.cql.ast.ExpressionFunctionBody) {
                    validateExpression(body.expression, symbolTable, semanticModel)
                    if (hasNestedError(body.expression, semanticModel)) {
                        semanticModel.addError(body.expression)
                    }
                }
            }
        }
        for ((_, paramDef) in symbolTable.parameterDefinitions) {
            paramDef.default?.let { validateExpression(it, symbolTable, semanticModel) }
        }
    }

    private fun hasNestedError(
        expression: org.hl7.cql.ast.Expression,
        semanticModel: SemanticModel,
    ): Boolean {
        if (semanticModel.hasError(expression)) return true
        var found = false
        val walker =
            object : AstWalker() {
                override fun visitExpression(expression: org.hl7.cql.ast.Expression) {
                    if (semanticModel.hasError(expression)) {
                        found = true
                        return
                    }
                    if (!found) super.visitExpression(expression)
                }
            }
        walker.visitExpression(expression)
        return found
    }

    private fun validateExpression(
        expression: org.hl7.cql.ast.Expression,
        symbolTable: SymbolTable,
        semanticModel: SemanticModel,
    ) {
        val walker =
            object : AstWalker() {
                override fun visitIdentifierExpression(
                    expression: org.hl7.cql.ast.IdentifierExpression
                ) {
                    if (semanticModel.getIdentifierResolution(expression) != null) return
                    if (semanticModel[expression] == null) {
                        semanticModel.addError(expression)
                    }
                }

                override fun visitFunctionCallExpression(
                    expression: org.hl7.cql.ast.FunctionCallExpression
                ) {
                    expression.target?.let { visitExpression(it) }
                    expression.arguments.forEach { visitExpression(it) }

                    if (semanticModel[expression] != null) return
                    if (semanticModel.getOperatorResolution(expression) != null) return

                    val name = expression.function.value
                    val userFuncs = symbolTable.resolveFunctions(name)
                    if (userFuncs.isNotEmpty()) {
                        val callArity = expression.arguments.size
                        val anyArityMatch = userFuncs.any { it.operands.size == callArity }
                        if (!anyArityMatch) {
                            semanticModel.addError(expression)
                        } else if (semanticModel[expression] == null) {
                            semanticModel.addError(expression)
                        }
                        return
                    }

                    val argTypes = expression.arguments.mapNotNull { semanticModel[it] }
                    if (argTypes.size == expression.arguments.size) {
                        semanticModel.addError(expression)
                    }
                }

                override fun visitAsExpression(expression: org.hl7.cql.ast.AsExpression) {
                    visitExpression(expression.operand)
                    val operandType = semanticModel[expression.operand]
                    val targetType = semanticModel[expression]
                    if (operandType != null && targetType != null) {
                        if (
                            operandType is org.hl7.cql.model.ListType &&
                                targetType !is org.hl7.cql.model.ListType
                        ) {
                            semanticModel.addError(expression)
                        }
                    }
                }

                override fun visitSortByItem(item: org.hl7.cql.ast.SortByItem) {
                    // Skip — sort items use a separate emission path (ByColumn/ByDirection)
                }
            }
        walker.visitExpression(expression)
    }
}
