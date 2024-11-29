package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType

class OperatorMap {
    private val operators: MutableMap<String, OperatorEntry> = HashMap()

    fun containsOperator(operator: Operator): Boolean {
        val entry = getEntry(operator.name)
        return entry.containsOperator(operator)
    }

    fun addOperator(operator: Operator) {
        val entry = getEntry(operator.name)
        entry.addOperator(operator)
    }

    private fun getEntry(operatorName: String): OperatorEntry {
        return operators.computeIfAbsent(operatorName) { OperatorEntry(operatorName) }
    }

    private fun supportsOperator(
        libraryName: String?,
        operatorName: String,
        vararg signature: DataType
    ): Boolean {
        val call =
            CallContext(
                libraryName,
                operatorName,
                allowPromotionAndDemotion = false,
                allowFluent = false,
                mustResolve = false,
                operandTypes = signature
            )
        return resolveOperator(call, null) != null
    }

    // Returns true if the given type supports the operations necessary to be the point type of an
    // interval
    // (i.e. comparison, successor, and predecessor)
    fun isPointType(type: DataType): Boolean {
        return supportsOperator("System", "LessOrEqual", type, type) &&
            supportsOperator("System", "Successor", type)
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth")
    fun resolveOperator(
        callContext: CallContext,
        conversionMap: ConversionMap?
    ): OperatorResolution? {
        val entry = getEntry(callContext.operatorName)
        val results = entry.resolve(callContext, this, conversionMap)

        // Score each resolution and return the lowest score
        // Duplicate scores indicate ambiguous match
        var result: OperatorResolution? = null
        if (results != null) {
            var lowestScore = Int.MAX_VALUE
            var lowestScoringResults: MutableList<OperatorResolution> = ArrayList()
            for (resolution in results) {
                val operands = resolution.operator.signature.operandTypes
                val callOperands = callContext.signature.operandTypes
                val conversions = if (resolution.hasConversions()) resolution.conversions else null
                var score = ConversionMap.ConversionScore.ExactMatch.score
                for (i in operands.indices) {
                    val operand = operands[i]
                    val callOperand = callOperands[i]
                    val conversion = conversions?.get(i)
                    score += ConversionMap.getConversionScore(callOperand, operand, conversion)
                }

                resolution.score = score

                if (score < lowestScore) {
                    lowestScore = score
                    lowestScoringResults.clear()
                    lowestScoringResults.add(resolution)
                } else if (score == lowestScore) {
                    lowestScoringResults.add(resolution)
                }
            }

            if (lowestScoringResults.size > 1) {
                var lowestTypeScore = Int.MAX_VALUE
                val lowestTypeScoringResults: MutableList<OperatorResolution> = ArrayList()
                for (resolution in lowestScoringResults) {
                    var typeScore = ConversionMap.ConversionScore.ExactMatch.score
                    for (operand in resolution.operator.signature.operandTypes) {
                        typeScore += ConversionMap.getTypePrecedenceScore(operand)
                    }

                    if (typeScore < lowestTypeScore) {
                        lowestTypeScore = typeScore
                        lowestTypeScoringResults.clear()
                        lowestTypeScoringResults.add(resolution)
                    } else if (typeScore == lowestTypeScore) {
                        lowestTypeScoringResults.add(resolution)
                    }
                }

                lowestScoringResults = lowestTypeScoringResults
            }

            if (lowestScoringResults.size > 1) {
                if (callContext.mustResolve) {
                    // ERROR:
                    val message =
                        StringBuilder("Call to operator ")
                            .append(callContext.operatorName)
                            .append(callContext.signature)
                            .append(" is ambiguous with: ")
                    for ((operator) in lowestScoringResults) {
                        message.append("\n  - ").append(operator.name).append(operator.signature)
                    }
                    throw IllegalArgumentException(message.toString())
                } else {
                    return null
                }
            } else {
                result = lowestScoringResults[0]
            }
        }

        return result
    }
}
