@file:Suppress("TooManyFunctions", "MagicNumber")

package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.FunctionCallExpression
import org.hl7.cql.ast.IfExpression
import org.hl7.cql.ast.IndexExpression
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.DataType
import org.hl7.elm.r1.AllTrue
import org.hl7.elm.r1.AnyTrue
import org.hl7.elm.r1.Avg
import org.hl7.elm.r1.Coalesce
import org.hl7.elm.r1.Combine
import org.hl7.elm.r1.Concatenate
import org.hl7.elm.r1.Contains
import org.hl7.elm.r1.Count
import org.hl7.elm.r1.Distinct
import org.hl7.elm.r1.EndsWith
import org.hl7.elm.r1.Except
import org.hl7.elm.r1.Exists
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.First
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.GeometricMean
import org.hl7.elm.r1.If
import org.hl7.elm.r1.In
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Includes
import org.hl7.elm.r1.IndexOf
import org.hl7.elm.r1.Indexer
import org.hl7.elm.r1.Intersect
import org.hl7.elm.r1.Last
import org.hl7.elm.r1.LastPositionOf
import org.hl7.elm.r1.Length
import org.hl7.elm.r1.Lower
import org.hl7.elm.r1.Matches
import org.hl7.elm.r1.Max
import org.hl7.elm.r1.Median
import org.hl7.elm.r1.Min
import org.hl7.elm.r1.Mode
import org.hl7.elm.r1.PopulationStdDev
import org.hl7.elm.r1.PopulationVariance
import org.hl7.elm.r1.PositionOf
import org.hl7.elm.r1.Product
import org.hl7.elm.r1.ProperContains
import org.hl7.elm.r1.ProperIncludedIn
import org.hl7.elm.r1.ProperIncludes
import org.hl7.elm.r1.ReplaceMatches
import org.hl7.elm.r1.SingletonFrom
import org.hl7.elm.r1.Size
import org.hl7.elm.r1.Split
import org.hl7.elm.r1.SplitOnMatches
import org.hl7.elm.r1.StartsWith
import org.hl7.elm.r1.StdDev
import org.hl7.elm.r1.Substring
import org.hl7.elm.r1.Sum
import org.hl7.elm.r1.Union
import org.hl7.elm.r1.Upper
import org.hl7.elm.r1.Variance

/** Emit an if-then-else expression as an ELM If node. Children are pre-folded. */
internal fun EmissionContext.emitIfExpression(
    expression: IfExpression,
    conditionElm: ElmExpression,
    thenElm: ElmExpression,
    elseElm: ElmExpression,
): ElmExpression {
    var thenResult = thenElm
    var elseResult = elseElm

    // Check if branches need choice type wrapping
    val choiceType = computeChoiceType(expression.thenBranch, expression.elseBranch)
    if (choiceType != null) {
        thenResult = wrapInAs(thenResult, choiceType)
        elseResult = wrapInAs(elseResult, choiceType)
    } else {
        val thenType = semanticModel[expression.thenBranch]
        val elseType = semanticModel[expression.elseBranch]
        val anyType = operatorRegistry.type("Any")
        // Implicit type promotion: when branch types differ and there's an implicit conversion
        // (e.g., Integer→Decimal), apply the conversion to the narrower branch
        if (
            thenType != null &&
                elseType != null &&
                thenType != elseType &&
                thenType != anyType &&
                elseType != anyType
        ) {
            thenResult = applyImplicitConversion(thenResult, thenType, elseType)
            elseResult = applyImplicitConversion(elseResult, elseType, thenType)
        }
        // Null-As wrapping: when one branch is null and the other has a known type,
        // wrap the null in As(thatType) to match legacy behavior
        if (isNullBranch(expression.elseBranch) && thenType != null) {
            elseResult = wrapNullAs(elseResult, thenType)
        } else if (isNullBranch(expression.thenBranch) && elseType != null) {
            thenResult = wrapNullAs(thenResult, elseType)
        }
    }

    return If().apply {
        condition = conditionElm
        then = thenResult
        `else` = elseResult
    }
}

/** Check if an AST expression is a null literal. */
private fun isNullBranch(expr: org.hl7.cql.ast.Expression): Boolean {
    return expr is org.hl7.cql.ast.LiteralExpression && expr.literal is org.hl7.cql.ast.NullLiteral
}

/**
 * Check if two branch expressions need choice type wrapping. Returns a [ChoiceType] if the branch
 * types are known, different, and neither is a subtype of the other.
 */
private fun EmissionContext.computeChoiceType(
    vararg branches: org.hl7.cql.ast.Expression
): ChoiceType? {
    val anyType = operatorRegistry.type("Any")
    val types = branches.mapNotNull { semanticModel[it] }.filter { it != anyType }
    if (types.size < 2) return null
    val distinct = types.distinct()
    if (distinct.size < 2) return null
    // Check if any pair is already sub/super type compatible
    if (distinct.all { t -> distinct.any { other -> other != t && other.isSuperTypeOf(t) } }) {
        return null
    }
    // Check if there exists a numeric promotion between types (e.g., Integer→Decimal)
    if (
        distinct.any { candidate ->
            distinct.all { t -> t == candidate || isNumericPromotion(t, candidate) }
        }
    ) {
        return null
    }
    // Check if there exists a common non-Any supertype
    val common = distinct.reduce { acc, type -> acc.getCommonSuperTypeOf(type) }
    if (common != anyType && common != DataType.ANY) return null
    return ChoiceType(distinct)
}

/** Wrap an emitted ELM expression in an As(ChoiceTypeSpecifier). */
private fun EmissionContext.wrapInAs(
    expression: ElmExpression,
    choiceType: ChoiceType,
): ElmExpression {
    return org.hl7.elm.r1.As().apply {
        operand = expression
        asTypeSpecifier = operatorRegistry.typeBuilder.dataTypeToTypeSpecifier(choiceType)
    }
}

/**
 * Check if [from] can be promoted to [to] via a numeric widening conversion (Integer→Long,
 * Integer→Decimal, Long→Decimal). This is distinct from arbitrary implicit conversions.
 */
private fun isNumericPromotion(from: DataType, to: DataType): Boolean {
    val fromName = from.toString()
    val toName = to.toString()
    return (fromName == "System.Integer" && toName == "System.Long") ||
        (fromName == "System.Integer" && toName == "System.Decimal") ||
        (fromName == "System.Long" && toName == "System.Decimal")
}

/** Emit an index expression (e.g., 'John'[1]) as an ELM Indexer node. Children are pre-folded. */
internal fun EmissionContext.emitIndexExpression(
    expression: IndexExpression,
    targetElm: ElmExpression,
    indexElm: ElmExpression,
): ElmExpression {
    return Indexer().apply { operand = mutableListOf(targetElm, indexElm) }
}

/**
 * Emit a function call expression. System functions are mapped to their dedicated ELM node types
 * rather than generic FunctionRef nodes. Children (target, arguments) are pre-folded.
 */
@Suppress("CyclomaticComplexMethod")
internal fun EmissionContext.emitFunctionCall(
    expression: FunctionCallExpression,
    targetElm: ElmExpression?,
    rawArgs: List<ElmExpression>,
): ElmExpression {
    if (targetElm != null) {
        throw ElmEmitter.UnsupportedNodeException(
            "Fluent function calls (target.${expression.function.value}()) are not yet supported."
        )
    }

    val functionName = expression.function.value

    val args = rawArgs.toMutableList()
    // Operator-based conversions (ToDecimal, ToLong, etc.) are inserted at the AST level by
    // ConversionInserter and emitted via onConversion handlers. Only apply remaining (cast,
    // list, interval) conversions here.
    val resolution = lookupResolution(expression)
    if (resolution != null) {
        applyRemainingConversions(resolution, args)
    }

    // Try system function emission (math, date/time, message)
    emitSystemFunction(functionName, args)?.let {
        return it
    }

    return when (functionName) {
        "Coalesce" -> Coalesce().apply { operand = args.toMutableList() }

        // String unary operators
        "Length" -> emitUnaryArg(args) { Length().apply { operand = it } }
        "Upper" -> emitUnaryArg(args) { Upper().apply { operand = it } }
        "Lower" -> emitUnaryArg(args) { Lower().apply { operand = it } }

        // String binary operators
        "StartsWith" ->
            emitBinaryArgs(args) { a, b -> StartsWith().apply { operand = mutableListOf(a, b) } }
        "EndsWith" ->
            emitBinaryArgs(args) { a, b -> EndsWith().apply { operand = mutableListOf(a, b) } }
        "Matches" ->
            emitBinaryArgs(args) { a, b -> Matches().apply { operand = mutableListOf(a, b) } }
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
        "First" -> emitUnaryArg(args) { First().apply { source = it } }
        "Last" -> emitUnaryArg(args) { Last().apply { source = it } }
        "Count" -> emitUnaryArg(args) { Count().apply { source = it } }
        "Sum" -> emitUnaryArg(args) { Sum().apply { source = it } }
        "Min" -> emitUnaryArg(args) { Min().apply { source = it } }
        "Max" -> emitUnaryArg(args) { Max().apply { source = it } }
        "Avg" -> emitUnaryArg(args) { Avg().apply { source = it } }
        "Median" -> emitUnaryArg(args) { Median().apply { source = it } }
        "Mode" -> emitUnaryArg(args) { Mode().apply { source = it } }
        "AllTrue" -> emitUnaryArg(args) { AllTrue().apply { source = it } }
        "AnyTrue" -> emitUnaryArg(args) { AnyTrue().apply { source = it } }
        "StdDev" -> emitUnaryArg(args) { StdDev().apply { source = it } }
        "PopulationStdDev" -> emitUnaryArg(args) { PopulationStdDev().apply { source = it } }
        "Variance" -> emitUnaryArg(args) { Variance().apply { source = it } }
        "PopulationVariance" -> emitUnaryArg(args) { PopulationVariance().apply { source = it } }
        "GeometricMean" -> emitUnaryArg(args) { GeometricMean().apply { source = it } }
        "Product" -> emitUnaryArg(args) { Product().apply { source = it } }
        "IndexOf" -> emitIndexOf(args)

        // List transform functions (operand-based)
        "Flatten" -> emitUnaryArg(args) { Flatten().apply { operand = it } }
        "Distinct" -> emitUnaryArg(args) { Distinct().apply { operand = it } }

        // Set operations (function-call form)
        "Except" ->
            emitBinaryArgs(args) { a, b -> Except().apply { operand = mutableListOf(a, b) } }
        "Union" -> emitBinaryArgs(args) { a, b -> Union().apply { operand = mutableListOf(a, b) } }
        "Intersect" ->
            emitBinaryArgs(args) { a, b -> Intersect().apply { operand = mutableListOf(a, b) } }

        // List/collection query functions (function-call form)
        "Exists" -> emitUnaryArg(args) { Exists().apply { operand = it } }
        "Contains" ->
            emitBinaryArgs(args) { a, b -> Contains().apply { operand = mutableListOf(a, b) } }
        "In" -> emitBinaryArgs(args) { a, b -> In().apply { operand = mutableListOf(a, b) } }
        "Includes" ->
            emitBinaryArgs(args) { a, b -> Includes().apply { operand = mutableListOf(a, b) } }
        "IncludedIn" ->
            emitBinaryArgs(args) { a, b -> IncludedIn().apply { operand = mutableListOf(a, b) } }
        "ProperContains" ->
            emitBinaryArgs(args) { a, b ->
                ProperContains().apply { operand = mutableListOf(a, b) }
            }
        "ProperIncludes" ->
            emitBinaryArgs(args) { a, b ->
                ProperIncludes().apply { operand = mutableListOf(a, b) }
            }
        "ProperIncludedIn" ->
            emitBinaryArgs(args) { a, b ->
                ProperIncludedIn().apply { operand = mutableListOf(a, b) }
            }
        "SingletonFrom" -> emitUnaryArg(args) { SingletonFrom().apply { operand = it } }

        // Interval functions
        "Size" -> emitUnaryArg(args) { Size().apply { operand = it } }

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
        "ConvertsToRatio" -> emitUnaryArg(args) { createConversionElm(functionName, it) }

        else ->
            FunctionRef().apply {
                name = functionName
                operand = args.toMutableList()
            }
    }
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
