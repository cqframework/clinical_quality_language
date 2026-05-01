package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.exception.InvalidTime
import org.opencds.cqf.cql.engine.util.LocalTime
import org.opencds.cqf.cql.engine.util.localTimeParse
import org.opencds.cqf.cql.engine.util.toPaddedString

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class Time : BaseTemporal {
    override val type = timeTypeName

    @JsExport.Ignore
    var time: LocalTime
        private set

    @JsExport.Ignore
    fun withTime(time: LocalTime): Time {
        this.time = time
        return this
    }

    @JsExport.Ignore
    override fun withPrecision(precision: Precision?): Time {
        this.precision = precision
        return this
    }

    @JsExport.Ignore
    constructor(time: LocalTime, precision: Precision) {
        this.time = time
        this.precision = precision
    }

    @JsName("fromDateString")
    constructor(dateString: kotlin.String) {
        var dateString = dateString
        var size = 0
        if (
            dateString.matches("^T[0-2]\\d$".toRegex()) ||
                dateString.matches("^[0-2]\\d$".toRegex())
        ) {
            dateString += ":00"
            size = -1
        }
        dateString = dateString.replace("T", "")
        size += dateString.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
        if (dateString.contains(".")) {
            ++size
        }
        precision = Precision.fromTimeIndex(size - 1)
        dateString = TemporalHelper.autoCompleteTimeString(dateString, precision!!)
        time = localTimeParse(dateString)
    }

    @JsName("fromTimeElements")
    constructor(vararg timeElements: Int) {
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

        precision = Precision.fromTimeIndex(stringElements.size - 1)
        timeString =
            StringBuilder()
                .append(
                    TemporalHelper.autoCompleteDateTimeString(timeString.toString(), precision!!)
                )

        time = localTimeParse(timeString.toString())
    }

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
        return Time(ot, this.precision!!)
    }

    @JsExport.Ignore
    fun expandPartialMin(precision: Precision?): Time {
        val ot = this.time.plusHours(0)
        return Time(ot, precision ?: Precision.MILLISECOND)
    }

    @JsExport.Ignore
    fun expandPartialMax(precision: Precision?): Time {
        var ot = this.time.plusHours(0)
        for (i in this.precision!!.toTimeIndex() + 1..3) {
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
        return this.precision!!.toTimeIndex() < p.toTimeIndex()
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
        val originalLocalTime = this.time.truncatedTo(originalPrecision!!.toChronoUnit())
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
                    Precision.getHighestTimePrecision(this.precision!!, other.precision!!),
                )
            if (result == null && forSort) {
                return if (this.precision!!.toTimeIndex() > other.precision!!.toTimeIndex()) 1
                else -1
            }
            return result
        } else {
            return compareToPrecision(other, this.precision!!)
        }
    }

    @JsExport.Ignore
    override fun compareToPrecision(other: BaseTemporal, p: Precision): Int? {
        var precision = p
        val leftMeetsPrecisionRequirements =
            this.precision!!.toTimeIndex() >= precision.toTimeIndex()
        val rightMeetsPrecisionRequirements =
            other.precision!!.toTimeIndex() >= precision.toTimeIndex()

        // adjust dates to evaluation offset
        val leftTime = this.time
        val rightTime = (other as Time).time

        if (!leftMeetsPrecisionRequirements || !rightMeetsPrecisionRequirements) {
            precision = Precision.getLowestTimePrecision(this.precision!!, other.precision!!)
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

    override fun toString(): kotlin.String {
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
                }.${time.get(precision!!.toChronoField()).toPaddedString(3)}"
        }
    }
}
