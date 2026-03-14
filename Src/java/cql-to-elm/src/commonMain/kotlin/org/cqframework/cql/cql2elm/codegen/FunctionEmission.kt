@file:Suppress("TooManyFunctions", "MagicNumber")

package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Concatenate
import org.hl7.elm.r1.EndsWith
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.If
import org.hl7.elm.r1.Indexer
import org.hl7.elm.r1.LastPositionOf
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.Lower
import org.hl7.elm.r1.Matches
import org.hl7.elm.r1.PositionOf
import org.hl7.elm.r1.ReplaceMatches
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.SplitOnMatches
import org.hl7.elm.r1.StartsWith
import org.hl7.elm.r1.Substring
import org.hl7.elm.r1.Upper

/** Emit an if-then-else expression as an ELM If node. */
internal fun EmissionContext.emitIfExpression(expression: IfExpression): ElmExpression {
    return If().apply {
        condition = emitExpression(expression.condition)
        then = emitExpression(expression.thenBranch)
        `else` = emitExpression(expression.elseBranch)
    }
}

/** Emit an index expression (e.g., 'John'[1]) as an ELM Indexer node. */
internal fun EmissionContext.emitIndexExpression(expression: IndexExpression): ElmExpression {
    val targetElm = emitExpression(expression.target)
    val indexElm = emitExpression(expression.index)
    return Indexer().apply { operand = mutableListOf(targetElm, indexElm) }
}

/**
 * Emit a function call expression. System functions are mapped to their dedicated ELM node types
 * rather than generic FunctionRef nodes.
 */
@Suppress("CyclomaticComplexMethod")
internal fun EmissionContext.emitFunctionCall(expression: FunctionCallExpression): ElmExpression {
    val functionName = expression.function.value
    val args = expression.arguments.map { emitExpression(it) }

    return when (functionName) {
        // Nullological
        "Coalesce" -> emitCoalesce(args)

        // String unary operators
        "Length" -> emitUnaryFunction(args) { Length().apply { operand = it } }
        "Upper" -> emitUnaryFunction(args) { Upper().apply { operand = it } }
        "Lower" -> emitUnaryFunction(args) { Lower().apply { operand = it } }

        // String binary operators
        "StartsWith" ->
            emitBinaryFunction(args) { a, b ->
                StartsWith().apply { operand = mutableListOf(a, b) }
            }
        "EndsWith" ->
            emitBinaryFunction(args) { a, b -> EndsWith().apply { operand = mutableListOf(a, b) } }
        "Matches" ->
            emitBinaryFunction(args) { a, b -> Matches().apply { operand = mutableListOf(a, b) } }
        "Concatenate" -> Concatenate().apply { operand = args.toMutableList() }

        // String special-form operators
        "Combine" -> emitCombineFunction(args)
        "Split" -> emitSplitFunction(args)
        "SplitOnMatches" -> emitSplitOnMatchesFunction(args)
        "PositionOf" -> emitPositionOfFunction(args)
        "LastPositionOf" -> emitLastPositionOfFunction(args)
        "Substring" -> emitSubstringFunction(args)
        "ReplaceMatches" -> emitReplaceMatchesFunction(args)

        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "Function '$functionName' is not yet supported."
            )
    }
}

private fun emitUnaryFunction(
    args: List<ElmExpression>,
    factory: (ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 1) { "Expected 1 argument" }
    return factory(args[0])
}

private fun emitBinaryFunction(
    args: List<ElmExpression>,
    factory: (ElmExpression, ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments" }
    return factory(args[0], args[1])
}

private fun EmissionContext.emitCoalesce(args: List<ElmExpression>): ElmExpression {
    return Coalesce().apply { operand = args.toMutableList() }
}

private fun emitCombineFunction(args: List<ElmExpression>): ElmExpression {
    return Combine().apply {
        source = args[0]
        if (args.size > 1) {
            separator = args[1]
        }
    }
}

private fun emitSplitFunction(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for Split" }
    return Split().apply {
        stringToSplit = args[0]
        separator = args[1]
    }
}

private fun emitSplitOnMatchesFunction(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for SplitOnMatches" }
    return SplitOnMatches().apply {
        stringToSplit = args[0]
        separatorPattern = args[1]
    }
}

private fun emitPositionOfFunction(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for PositionOf" }
    return PositionOf().apply {
        pattern = args[0]
        string = args[1]
    }
}

private fun emitLastPositionOfFunction(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for LastPositionOf" }
    return LastPositionOf().apply {
        pattern = args[0]
        string = args[1]
    }
}

private fun emitSubstringFunction(args: List<ElmExpression>): ElmExpression {
    require(args.size in 2..3) { "Expected 2 or 3 arguments for Substring" }
    return Substring().apply {
        stringToSub = args[0]
        startIndex = args[1]
        if (args.size > 2) {
            length = args[2]
        }
    }
}

private fun emitReplaceMatchesFunction(args: List<ElmExpression>): ElmExpression {
    require(args.size == 3) { "Expected 3 arguments for ReplaceMatches" }
    return ReplaceMatches().apply { operand = args.toMutableList() }
}
