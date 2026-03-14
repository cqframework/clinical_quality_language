package org.cqframework.cql.cql2elm.ast

import org.cqframework.cql.shared.BigDecimal
import org.hl7.cql.ast.DateTimeLiteral
import org.hl7.cql.ast.TimeLiteral
import org.hl7.elm.r1.Date as ElmDate
import org.hl7.elm.r1.DateTime
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Time

@Suppress("MagicNumber", "MaxLineLength", "CyclomaticComplexMethod")
internal fun EmissionContext.emitDateTime(literal: DateTimeLiteral): ElmExpression {
    val input = literal.text
    val dateTimePattern =
        Regex(
            "(\\d{4})(((-(\\d{2}))(((-(\\d{2}))((T)((\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?)?)?)|(T))?)|(T))?((Z)|(([+-])(\\d{2})(:(\\d{2}))))?"
        )
    val matcher =
        dateTimePattern.matchEntire(input)
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Invalid date/time literal: ${literal.text}"
            )

    // Determine if this is a date-only literal (no T component)
    val isDateOnly = !input.contains('T')

    if (isDateOnly) {
        // Emit a Date node for date-only literals
        val result = ElmDate()
        result.year = createIntLiteral(matcher.groups[1]!!.value.toInt())
        if (matcher.groups[5] != null) {
            result.month = createIntLiteral(matcher.groups[5]!!.value.toInt())
        }
        if (matcher.groups[9] != null) {
            result.day = createIntLiteral(matcher.groups[9]!!.value.toInt())
        }
        return result
    }

    val result = DateTime()
    result.year = createIntLiteral(matcher.groups[1]!!.value.toInt())
    if (matcher.groups[5] != null) {
        result.month = createIntLiteral(matcher.groups[5]!!.value.toInt())
    }
    if (matcher.groups[9] != null) {
        result.day = createIntLiteral(matcher.groups[9]!!.value.toInt())
    }
    if (matcher.groups[13] != null) {
        result.hour = createIntLiteral(matcher.groups[13]!!.value.toInt())
    }
    if (matcher.groups[15] != null) {
        result.minute = createIntLiteral(matcher.groups[15]!!.value.toInt())
    }
    if (matcher.groups[17] != null) {
        result.second = createIntLiteral(matcher.groups[17]!!.value.toInt())
    }
    if (matcher.groups[19] != null) {
        result.millisecond = createIntLiteral(normalizeMilliseconds(matcher.groups[19]!!.value))
    }
    if (matcher.groups[23] != null) {
        result.timezoneOffset = createDecimalLiteral(BigDecimal("0"))
    } else if (matcher.groups[25] != null) {
        val polarity = if (matcher.groups[25]!!.value == "+") 1 else -1
        val offsetHour = matcher.groups[26]!!.value.toInt()
        val offsetMin = if (matcher.groups[28] != null) matcher.groups[28]!!.value.toInt() else 0
        val offset = polarity.toDouble() * (offsetHour + offsetMin.toDouble() / 60)
        result.timezoneOffset = createDecimalLiteral(BigDecimal(offset.toString()))
    }
    return result
}

@Suppress("MagicNumber")
internal fun EmissionContext.emitTime(literal: TimeLiteral): Time {
    val input = literal.text
    val timePattern = Regex("T(\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?")
    val matcher =
        timePattern.matchEntire(input)
            ?: throw ElmEmitter.UnsupportedNodeException("Invalid time literal: ${literal.text}")

    val result = Time()
    result.hour = createIntLiteral(matcher.groups[1]!!.value.toInt())
    if (matcher.groups[3] != null) {
        result.minute = createIntLiteral(matcher.groups[3]!!.value.toInt())
    }
    if (matcher.groups[5] != null) {
        result.second = createIntLiteral(matcher.groups[5]!!.value.toInt())
    }
    if (matcher.groups[7] != null) {
        result.millisecond = createIntLiteral(normalizeMilliseconds(matcher.groups[7]!!.value))
    }
    return result
}

/**
 * Normalize a millisecond string to an integer value. The string is padded or truncated to 3
 * digits: ".1" -> 100, ".12" -> 120, ".123" -> 123, ".1234" -> 123.
 */
@Suppress("MagicNumber")
internal fun normalizeMilliseconds(msString: String): Int {
    val padded = msString.take(3).padEnd(3, '0')
    return padded.toInt()
}
