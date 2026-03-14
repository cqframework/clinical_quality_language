package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.OperatorRegistry
import org.cqframework.cql.cql2elm.analysis.SymbolTable
import org.cqframework.cql.cql2elm.analysis.TypeTable
import org.cqframework.cql.cql2elm.model.OperatorResolution
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.AsExpression
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.CastExpression
import org.hl7.cql.ast.ConversionExpression
import org.hl7.cql.ast.Expression
import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.ast.IsExpression
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Literal as ElmLiteral

/**
 * Shared state and helpers used by all emission extension functions. Acts as the central hub for
 * recursive expression emission.
 */
class EmissionContext(
    val typeTable: TypeTable,
    val symbolTable: SymbolTable,
    val operatorRegistry: OperatorRegistry,
) {
    val typesNamespace = "urn:hl7-org:elm-types:r1"

    /**
     * Set resultType on an ELM element via the Trackable extension property. This sets the internal
     * resultType for downstream consumers but does NOT set resultTypeName or resultTypeSpecifier on
     * the serialized output, matching the legacy translator's default behavior.
     */
    fun decorate(element: Element, type: DataType) {
        element.resultType = type
    }

    fun createIntLiteral(value: Int): ElmLiteral {
        return ElmLiteral()
            .withValueType(QName(typesNamespace, "Integer"))
            .withValue(value.toString())
    }

    fun createDecimalLiteral(value: BigDecimal): ElmLiteral {
        return ElmLiteral()
            .withValueType(QName(typesNamespace, "Decimal"))
            .withValue(value.toString())
    }

    /** Look up the operator resolution for an AST expression from the TypeTable. */
    fun lookupResolution(expression: Expression): OperatorResolution? =
        typeTable.getOperatorResolution(expression)

    /** Wrap an expression in a conversion operator (e.g., ToDecimal, ToLong). */
    fun wrapConversion(expression: ElmExpression, conversionName: String): ElmExpression {
        return createConversionElm(conversionName, expression)
    }

    /** Wrap an expression in a Coalesce with an empty string fallback. */
    fun wrapCoalesce(expression: ElmExpression): ElmExpression {
        val emptyString = ElmLiteral().withValueType(QName(typesNamespace, "String")).withValue("")
        return org.hl7.elm.r1.Coalesce().apply { operand = mutableListOf(expression, emptyString) }
    }

    /**
     * Apply conversions from an [OperatorResolution]. Calls [handler] for each conversion with the
     * operand index and the conversion operator name.
     */
    inline fun applyConversions(resolution: OperatorResolution, handler: (Int, String) -> Unit) {
        if (resolution.hasConversions()) {
            resolution.conversions.forEachIndexed { index, conversion ->
                if (conversion != null) {
                    val convName = operatorRegistry.conversionOperatorName(conversion)
                    if (convName != null) {
                        handler(index, convName)
                    }
                }
            }
        }
    }

    /**
     * Recursively emit an AST [Expression] into an ELM expression. This is the main dispatch point
     * that all emission extension functions call for sub-expressions.
     */
    fun emitExpression(expression: Expression): ElmExpression {
        val elmExpr =
            when (expression) {
                is LiteralExpression -> emitLiteral(expression.literal)
                is OperatorBinaryExpression -> emitBinaryOperator(expression)
                is OperatorUnaryExpression -> emitUnaryOperator(expression)
                is BooleanTestExpression -> emitBooleanTest(expression)
                is IfExpression -> emitIfExpression(expression)
                is FunctionCallExpression -> emitFunctionCall(expression)
                is IndexExpression -> emitIndexExpression(expression)
                is IdentifierExpression -> emitIdentifierExpression(expression)
                is IsExpression -> emitIsExpression(expression)
                is AsExpression -> emitAsExpression(expression)
                is CastExpression -> emitCastExpression(expression)
                is ConversionExpression -> emitConversionExpression(expression)
                else ->
                    throw ElmEmitter.UnsupportedNodeException(
                        "Expression '${expression::class.simpleName}' is not supported yet."
                    )
            }

        // Set result type from the TypeTable
        val type = typeTable[expression]
        if (type != null) {
            decorate(elmExpr, type)
        }

        return elmExpr
    }
}
