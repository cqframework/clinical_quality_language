package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.util.javaClassName

@Suppress("LongMethod", "CyclomaticComplexMethod", "ReturnCount")
object AddEvaluator {
    @JvmStatic
    fun add(left: Any?, right: Any?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Int && right is Int) {
            return left + right
        } else if (left is Long && right is Long) {
            return left + right
        } else if (left is BigDecimal && right is BigDecimal) {
            return Value.verifyPrecision(left.add(right), null)
        } else if (left is Quantity && right is Quantity) {
            return computeWithConvertedUnits(
                left,
                right,
                { commonUnit, leftValue, rightValue ->
                    Quantity().withUnit(commonUnit).withValue(leftValue.add(rightValue))
                },
                state!!,
            )
        } else if (left is BaseTemporal && right is Quantity) {
            var valueToAddPrecision = Precision.fromString(right.unit!!)
            var precision = Precision.fromString(BaseTemporal.getLowestPrecision(left))
            var valueToAdd = right.value!!.toInt()

            if (left is DateTime || left is Date) {
                if (valueToAddPrecision == Precision.WEEK) {
                    valueToAdd = TemporalHelper.weeksToDays(valueToAdd)
                    valueToAddPrecision = Precision.DAY
                }
            }

            if (left is DateTime || left is Date) {
                if (precision == Precision.WEEK) {
                    valueToAdd = TemporalHelper.weeksToDays(valueToAdd)
                    precision = Precision.DAY
                }
            }
            var convertedValueToAdd = valueToAdd.toLong()
            if (precision.toDateTimeIndex() < valueToAddPrecision.toDateTimeIndex()) {
                convertedValueToAdd =
                    TemporalHelper.truncateValueToTargetPrecision(
                        valueToAdd.toLong(),
                        valueToAddPrecision,
                        precision,
                    )
                valueToAddPrecision = precision
            }

            if (left is DateTime) {
                return DateTime(
                    left.dateTime!!.plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit()),
                    precision,
                )
            } else if (left is Date) {
                return Date(
                        left.date!!.plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit())
                    )
                    .withPrecision(precision)
            } else {
                return Time(
                    (left as Time)
                        .time
                        .plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit()),
                    precision,
                )
            }
        } else if (left is Interval && right is Interval) {
            return Interval(
                add(left.start, right.start, state),
                true,
                add(left.end, right.end, state),
                true,
            )
        } else if (left is String && right is String) {
            return left + right
        }

        throw InvalidOperatorArgument(
            "Add(Integer, Integer), Add(Long, Long), Add(Decimal, Decimal), Add(Quantity, Quantity), Add(Date, Quantity), Add(DateTime, Quantity) or Add(Time, Quantity)",
            "Add(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
