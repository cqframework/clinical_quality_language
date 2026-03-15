@file:Suppress("TooManyFunctions", "MagicNumber")

package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.elm.r1.AllTrue
import org.hl7.elm.r1.AnyTrue
import org.hl7.elm.r1.Avg
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Concatenate
import org.hl7.elm.r1.Count
import org.hl7.elm.r1.Distinct
import org.hl7.elm.r1.EndsWith
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.First
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.If
import org.hl7.elm.r1.IndexOf
import org.hl7.elm.r1.Indexer
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.LastPositionOf
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.Lower
import org.hl7.elm.r1.Matches
import org.hl7.elm.r1.Max
import org.hl7.elm.r1.Median
import org.hl7.elm.r1.Min
import org.hl7.elm.r1.Mode
import org.hl7.elm.r1.PositionOf
import org.hl7.elm.r1.ReplaceMatches
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.SplitOnMatches
import org.hl7.elm.r1.StartsWith
import org.hl7.elm.r1.Substring
import org.hl7.elm.r1.Sum
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
    if (expression.target != null) {
        throw ElmEmitter.UnsupportedNodeException(
            "Fluent function calls (target.${expression.function.value}()) are not yet supported."
        )
    }

    val functionName = expression.function.value
    val rawArgs = expression.arguments.map { emitExpression(it) }

    val args = rawArgs.toMutableList()
    val resolution = lookupResolution(expression)
    if (resolution != null) {
        applyConversions(resolution) { index, convName ->
            if (index in args.indices) {
                args[index] = wrapConversion(args[index], convName)
            }
        }
    }

    // Try system function emission (math, date/time, message)
    emitSystemFunction(functionName, args)?.let {
        return it
    }

    return when (functionName) {
        "Coalesce" -> Coalesce().apply { operand = args.toMutableList() }

        // String unary operators
        "Length" -> emitUnary(args) { Length().apply { operand = it } }
        "Upper" -> emitUnary(args) { Upper().apply { operand = it } }
        "Lower" -> emitUnary(args) { Lower().apply { operand = it } }

        // String binary operators
        "StartsWith" ->
            emitBinary(args) { a, b -> StartsWith().apply { operand = mutableListOf(a, b) } }
        "EndsWith" ->
            emitBinary(args) { a, b -> EndsWith().apply { operand = mutableListOf(a, b) } }
        "Matches" -> emitBinary(args) { a, b -> Matches().apply { operand = mutableListOf(a, b) } }
        "Concatenate" -> Concatenate().apply { operand = args.toMutableList() }

        // String special-form operators
        "Combine" -> emitCombine(args)
        "Split" -> emitSplit(args)
        "SplitOnMatches" -> emitSplitOnMatches(args)
        "PositionOf" -> emitPositionOf(args)
        "LastPositionOf" -> emitLastPositionOf(args)
        "Substring" -> emitSubstring(args)
        "ReplaceMatches" -> ReplaceMatches().apply { operand = args.toMutableList() }

        // Aggregate functions (source-based)
        "First" -> emitUnary(args) { First().apply { source = it } }
        "Last" -> emitUnary(args) { Last().apply { source = it } }
        "Count" -> emitUnary(args) { Count().apply { source = it } }
        "Sum" -> emitUnary(args) { Sum().apply { source = it } }
        "Min" -> emitUnary(args) { Min().apply { source = it } }
        "Max" -> emitUnary(args) { Max().apply { source = it } }
        "Avg" -> emitUnary(args) { Avg().apply { source = it } }
        "Median" -> emitUnary(args) { Median().apply { source = it } }
        "Mode" -> emitUnary(args) { Mode().apply { source = it } }
        "AllTrue" -> emitUnary(args) { AllTrue().apply { source = it } }
        "AnyTrue" -> emitUnary(args) { AnyTrue().apply { source = it } }
        "IndexOf" -> emitIndexOf(args)

        // List transform functions (operand-based)
        "Flatten" -> emitUnary(args) { Flatten().apply { operand = it } }
        "Distinct" -> emitUnary(args) { Distinct().apply { operand = it } }

        // Type conversion and ConvertsTo operators
        "ToString",
        "ToBoolean",
        "ToInteger",
        "ToLong",
        "ToDecimal",
        "ToDate",
        "ToDateTime",
        "ToTime",
        "ToQuantity",
        "ToRatio",
        "ToConcept",
        "ConvertsToString",
        "ConvertsToBoolean",
        "ConvertsToInteger",
        "ConvertsToLong",
        "ConvertsToDecimal",
        "ConvertsToDate",
        "ConvertsToDateTime",
        "ConvertsToTime",
        "ConvertsToQuantity",
        "ConvertsToRatio" -> emitUnary(args) { createConversionElm(functionName, it) }

        else ->
            FunctionRef().apply {
                name = functionName
                operand = args.toMutableList()
            }
    }
}

private fun emitUnary(
    args: List<ElmExpression>,
    factory: (ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 1) { "Expected 1 argument" }
    return factory(args[0])
}

private fun emitBinary(
    args: List<ElmExpression>,
    factory: (ElmExpression, ElmExpression) -> ElmExpression,
): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments" }
    return factory(args[0], args[1])
}

private fun emitCombine(args: List<ElmExpression>): ElmExpression {
    return Combine().apply {
        source = args[0]
        if (args.size > 1) separator = args[1]
    }
}

private fun emitSplit(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for Split" }
    return Split().apply {
        stringToSplit = args[0]
        separator = args[1]
    }
}

private fun emitSplitOnMatches(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for SplitOnMatches" }
    return SplitOnMatches().apply {
        stringToSplit = args[0]
        separatorPattern = args[1]
    }
}

private fun emitPositionOf(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for PositionOf" }
    return PositionOf().apply {
        pattern = args[0]
        string = args[1]
    }
}

private fun emitLastPositionOf(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for LastPositionOf" }
    return LastPositionOf().apply {
        pattern = args[0]
        string = args[1]
    }
}

private fun emitSubstring(args: List<ElmExpression>): ElmExpression {
    require(args.size in 2..3) { "Expected 2 or 3 arguments for Substring" }
    return Substring().apply {
        stringToSub = args[0]
        startIndex = args[1]
        if (args.size > 2) length = args[2]
    }
}

private fun emitIndexOf(args: List<ElmExpression>): ElmExpression {
    require(args.size == 2) { "Expected 2 arguments for IndexOf" }
    return IndexOf().apply {
        source = args[0]
        element = args[1]
    }
}
