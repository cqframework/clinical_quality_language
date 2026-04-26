package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.computeWithConvertedUnits
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong
import org.opencds.cqf.cql.engine.runtime.toCqlString

@Suppress("LongMethod", "CyclomaticComplexMethod", "ReturnCount")
object AddEvaluator {
    @JvmStatic
    fun add(left: Value?, right: Value?, state: State?): Value? {
        if (left == null || right == null) {
            return null
        }

        if (left is Integer && right is Integer) {
            return (left.value + right.value).toCqlInteger()
        } else if (left is Long && right is Long) {
            return (left.value + right.value).toCqlLong()
        } else if (left is Decimal && right is Decimal) {
            return DecimalHelper.verifyPrecision(left.value.add(right.value), null).toCqlDecimal()
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
            return (left.value + right.value).toCqlString()
        }

        throw InvalidOperatorArgument(
            "Add(Integer, Integer), Add(Long, Long), Add(Decimal, Decimal), Add(Quantity, Quantity), Add(Date, Quantity), Add(DateTime, Quantity) or Add(Time, Quantity)",
            "Add(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
