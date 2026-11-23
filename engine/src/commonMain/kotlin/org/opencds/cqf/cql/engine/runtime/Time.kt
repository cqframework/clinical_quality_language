package org.opencds.cqf.cql.engine.runtime

import kotlin.js.JsExport
import kotlin.js.JsName
import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.exception.InvalidTime
import org.opencds.cqf.cql.engine.util.LocalTime
import org.opencds.cqf.cql.engine.util.localTimeParse
import org.opencds.cqf.cql.engine.util.toPaddedString

@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class Time(val time: LocalTime, override val precision: Precision) : BaseTemporal {
    override val type = timeTypeName

    @JsExport.Ignore
    fun withTime(time: LocalTime): Time {
        return Time(time, this.precision)
    }

    @JsExport.Ignore
    override fun withPrecision(precision: Precision): Time {
        return Time(time, precision)
    }

    private class TimeArgs(val time: LocalTime, val precision: Precision)

    private constructor(timeArgs: TimeArgs) : this(timeArgs.time, timeArgs.precision)

    @JsName("fromTimeString")
    constructor(
        timeString: kotlin.String
    ) : this(
        kotlin.run {
            var timeString = timeString
            var size = 0
            if (
                timeString.matches("^T[0-2]\\d$".toRegex()) ||
                    timeString.matches("^[0-2]\\d$".toRegex())
            ) {
                timeString += ":00"
                size = -1
            }
            timeString = timeString.replace("T", "")
            size += timeString.split(":").size
            if (timeString.contains(".")) {
                ++size
            }
            val precision = Precision.fromTimeIndex(size - 1)
            timeString = TemporalHelper.autoCompleteTimeString(timeString, precision)
            val time = localTimeParse(timeString)
            TimeArgs(time, precision)
        }
    )

    @JsName("fromTimeElements")
    constructor(
        vararg timeElements: Int
    ) : this(
        kotlin.run {
            if (timeElements.isEmpty()) {
                throw InvalidTime("Time must include an hour")
            }

            var timeString = StringBuilder()
            val stringElements = TemporalHelper.normalizeTimeElements(*timeElements)

            for (i in stringElements.indices) {
                if (i == 0) {
                    timeString.append(stringElements[i])
                    continue
                } else if (i < 3) {
                    timeString.append(":")
                } else if (i == 3) {
                    timeString.append(".")
                }
                timeString.append(stringElements[i])
            }

            val precision = Precision.fromTimeIndex(stringElements.size - 1)
            timeString =
                StringBuilder()
                    .append(
                        TemporalHelper.autoCompleteDateTimeString(timeString.toString(), precision)
                    )

            val time = localTimeParse(timeString.toString())
            TimeArgs(time, precision)
        }
    )

    @JsExport.Ignore
    fun expandPartialMinFromPrecision(precision: Precision): Time {
        var ot = this.time.plusHours(0)
        for (i in precision.toTimeIndex() + 1..3) {
            ot =
                ot.with(
                    Precision.fromTimeIndex(i).toChronoField(),
                    ot.range(Precision.fromTimeIndex(i).toChronoField()).getMinimum(),
                )
        }
        return Time(ot, this.precision)
    }

    @JsExport.Ignore
    fun expandPartialMin(precision: Precision?): Time {
        val ot = this.time.plusHours(0)
        return Time(ot, precision ?: Precision.MILLISECOND)
    }

    @JsExport.Ignore
    fun expandPartialMax(precision: Precision?): Time {
        var ot = this.time.plusHours(0)
        for (i in this.precision.toTimeIndex() + 1..3) {
            ot =
                if (i <= precision!!.toTimeIndex()) {
                    ot.with(
                        Precision.fromTimeIndex(i).toChronoField(),
                        ot.range(Precision.fromTimeIndex(i).toChronoField()).getMaximum(),
                    )
                } else {
                    ot.with(
                        Precision.fromTimeIndex(i).toChronoField(),
                        ot.range(Precision.fromTimeIndex(i).toChronoField()).getMinimum(),
                    )
                }
        }
        return Time(ot, precision ?: Precision.MILLISECOND)
    }

    @JsExport.Ignore
    override fun isUncertain(p: Precision): kotlin.Boolean {
        return this.precision.toTimeIndex() < p.toTimeIndex()
    }

    @JsExport.Ignore
    override fun getUncertaintyInterval(p: Precision): Interval {
        val start = expandPartialMin(p)
        val end = expandPartialMax(p).expandPartialMinFromPrecision(p)
        return Interval(start, true, end, true)
    }

    @JsExport.Ignore
    override fun roundToPrecision(precision: Precision, useCeiling: kotlin.Boolean): BaseTemporal? {
        val originalPrecision = this.precision
        val originalLocalTime = this.time.truncatedTo(originalPrecision.toChronoUnit())
        when (precision) {
            Precision.HOUR,
            Precision.MINUTE,
            Precision.SECOND,
            Precision.MILLISECOND ->
                return if (precision.toTimeIndex() < originalPrecision.toTimeIndex()) {
                    val floorLocalTime = originalLocalTime.truncatedTo(precision.toChronoUnit())
                    if (useCeiling && floorLocalTime != originalLocalTime) {
                        Time(floorLocalTime.plus(1, precision.toChronoUnit()), precision)
                    } else {
                        Time(floorLocalTime, precision)
                    }
                } else {
                    Time(originalLocalTime, originalPrecision)
                }
            else -> return null
        }
    }

    override fun compare(other: BaseTemporal, forSort: kotlin.Boolean): Int? {
        val differentPrecisions = this.precision != other.precision

        if (differentPrecisions) {
            val result =
                this.compareToPrecision(
                    other,
                    Precision.getHighestTimePrecision(this.precision, other.precision),
                )
            if (result == null && forSort) {
                return if (this.precision.toTimeIndex() > other.precision.toTimeIndex()) 1 else -1
            }
            return result
        } else {
            return compareToPrecision(other, this.precision)
        }
    }

    @JsExport.Ignore
    override fun compareToPrecision(other: BaseTemporal, p: Precision): Int? {
        var precision = p
        val leftMeetsPrecisionRequirements = this.precision.toTimeIndex() >= precision.toTimeIndex()
        val rightMeetsPrecisionRequirements =
            other.precision.toTimeIndex() >= precision.toTimeIndex()

        // adjust dates to evaluation offset
        val leftTime = this.time
        val rightTime = (other as Time).time

        if (!leftMeetsPrecisionRequirements || !rightMeetsPrecisionRequirements) {
            precision = Precision.getLowestTimePrecision(this.precision, other.precision)
        }

        for (i in 0..<precision.toTimeIndex() + 1) {
            val leftComp = leftTime.get(Precision.getTimeChronoFieldFromIndex(i))
            val rightComp = rightTime.get(Precision.getTimeChronoFieldFromIndex(i))
            if (leftComp > rightComp) {
                return 1
            } else if (leftComp < rightComp) {
                return -1
            }
        }

        if (leftMeetsPrecisionRequirements && rightMeetsPrecisionRequirements) {
            return 0
        }

        return null
    }

    override fun compareTo(other: BaseTemporal): Int {
        return this.compare(other, true)!!
    }

    private val hash: Int by lazy { listOf(time, precision).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Time && time == other.time && precision == other.precision)

    override fun toStringInner(): kotlin.String {
        return when (precision) {
            Precision.HOUR -> time.getHour().toPaddedString(2)
            Precision.MINUTE ->
                "${time.getHour().toPaddedString(2)}:${time.getMinute().toPaddedString(2)}"
            Precision.SECOND ->
                "${time.getHour().toPaddedString(2)}:${time.getMinute().toPaddedString(2)}:${
                time.getSecond().toPaddedString(2)
            }"
            else ->
                "${time.getHour().toPaddedString(2)}:${time.getMinute().toPaddedString(2)}:${
                time.getSecond().toPaddedString(2)
                }.${time.get(precision.toChronoField()).toPaddedString(3)}"
        }
    }

    override fun toString(): kotlin.String {
        return "@T${toStringInner()}"
    }
}
