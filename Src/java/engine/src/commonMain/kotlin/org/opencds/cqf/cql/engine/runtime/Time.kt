package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.exception.InvalidTime
import org.opencds.cqf.cql.engine.util.LocalTime
import org.opencds.cqf.cql.engine.util.localTimeParse

class Time : BaseTemporal {
    var time: LocalTime
        private set

    fun withTime(time: LocalTime): Time {
        this.time = time
        return this
    }

    fun withPrecision(precision: Precision): Time {
        this.precision = precision
        return this
    }

    constructor(time: LocalTime, precision: Precision) {
        this.time = time
        this.precision = precision
    }

    constructor(dateString: String) {
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

    fun expandPartialMin(precision: Precision?): Time {
        val ot = this.time.plusHours(0)
        return Time(ot, precision ?: Precision.MILLISECOND)
    }

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

    override fun isUncertain(p: Precision): Boolean {
        return this.precision!!.toTimeIndex() < p.toTimeIndex()
    }

    override fun getUncertaintyInterval(p: Precision): Interval {
        val start = expandPartialMin(p)
        val end = expandPartialMax(p).expandPartialMinFromPrecision(p)
        return Interval(start, true, end, true)
    }

    override fun roundToPrecision(precision: Precision, useCeiling: Boolean): BaseTemporal? {
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

    override fun compare(other: BaseTemporal, forSort: Boolean): Int? {
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

    override fun toString(): String {
        return when (precision) {
            Precision.HOUR -> time.getHour().toString().padStart(2, '0')
            Precision.MINUTE ->
                "${time.getHour().toString().padStart(2, '0')}:${time.getMinute().toString().padStart(2, '0')}"
            Precision.SECOND ->
                "${time.getHour().toString().padStart(2, '0')}:${time.getMinute().toString().padStart(2, '0')}:${
                time.getSecond().toString().padStart(2, '0')
            }"

            else ->
                "${time.getHour().toString().padStart(2, '0')}:${time.getMinute().toString().padStart(2, '0')}:${
                time.getSecond().toString().padStart(2, '0')
                }.${time.get(precision!!.toChronoField()).toString().padStart(3, '0')}"
        }
    }
}
