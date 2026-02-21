package org.opencds.cqf.cql.engine.runtime

import kotlin.math.abs
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.ONE
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.InvalidDateTime
import org.opencds.cqf.cql.engine.util.Date
import org.opencds.cqf.cql.engine.util.OffsetDateTime
import org.opencds.cqf.cql.engine.util.ZoneOffset
import org.opencds.cqf.cql.engine.util.dateFrom
import org.opencds.cqf.cql.engine.util.dateTimeFormatterIsoOffsetDateTimeFormat
import org.opencds.cqf.cql.engine.util.offsetDateTimeParse
import org.opencds.cqf.cql.engine.util.toPaddedString
import org.opencds.cqf.cql.engine.util.zoneOffsetOfHoursMinutes

class DateTime : BaseTemporal {
    val zoneOffset: ZoneOffset

    var dateTime: OffsetDateTime? = null
        set(dateTime) {
            if (dateTime!!.getYear() < 1) {
                throw InvalidDateTime(
                    "The year: ${dateTime.getYear()} falls below the accepted bounds of 0001-9999."
                )
            }

            if (dateTime.getYear() > 9999) {
                throw InvalidDateTime(
                    "The year: ${dateTime.getYear()} falls above the accepted bounds of 0001-9999."
                )
            }
            field = dateTime
        }

    fun withPrecision(precision: Precision): DateTime {
        this.precision = precision
        return this
    }

    constructor(dateTime: OffsetDateTime?) {
        this.dateTime = (dateTime)
        this.precision = Precision.MILLISECOND
        zoneOffset = toZoneOffset(dateTime)
    }

    constructor(dateTime: OffsetDateTime, precision: Precision) {
        this.dateTime = (dateTime)
        this.precision = precision
        zoneOffset = toZoneOffset(dateTime)
    }

    constructor(dateString: String, offset: ZoneOffset?) {
        if (offset == null) {
            throw CqlException("Cannot pass a null offset")
        }

        var dateString = dateString

        zoneOffset = offset

        // Handles case when Tz is not complete (T02:04:59.123+01)
        if (dateString.matches("T[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d{3}(\\+|-)\\d{2}$".toRegex())) {
            dateString += ":00"
        }
        var size = 0
        if (dateString.contains("T")) {
            val datetimeSplit: Array<String?> =
                dateString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            size +=
                datetimeSplit[0]!!
                    .split("-".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                    .size
            val tzSplit: Array<String?> =
                if (dateString.contains("Z"))
                    dateString.split("Z".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                else
                    datetimeSplit[1]!!
                        .split("[+-]".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
            size +=
                tzSplit[0]!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
            if (tzSplit[0]!!.contains(".")) {
                ++size
            }
            precision = Precision.fromDateTimeIndex(size - 1)
            if (tzSplit.size == 1 && !dateString.contains("Z")) {
                dateString = TemporalHelper.autoCompleteDateTimeString(dateString, precision!!)
                dateString += offset.getId()
            }
        } else {
            size +=
                dateString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
            precision = Precision.fromDateTimeIndex(size - 1)
            dateString = TemporalHelper.autoCompleteDateTimeString(dateString, precision!!)
            dateString += offset.getId()
        }

        this.dateTime = (offsetDateTimeParse(dateString))
    }

    constructor(offset: BigDecimal?, vararg dateElements: Int) {
        if (offset == null) {
            throw CqlException("BigDecimal offset must be non-null")
        }

        if (dateElements.isEmpty()) {
            throw InvalidDateTime("DateTime must include a year")
        }

        zoneOffset = toZoneOffset(offset)

        var dateString = StringBuilder()
        val stringElements = TemporalHelper.normalizeDateTimeElements(*dateElements)

        for (i in stringElements.indices) {
            if (i == 0) {
                dateString.append(stringElements[i])
                continue
            } else if (i < 3) {
                dateString.append("-")
            } else if (i == 3) {
                dateString.append("T")
            } else if (i < 6) {
                dateString.append(":")
            } else if (i == 6) {
                dateString.append(".")
            }
            dateString.append(stringElements[i])
        }

        precision = Precision.fromDateTimeIndex(stringElements.size - 1)
        dateString =
            StringBuilder()
                .append(
                    TemporalHelper.autoCompleteDateTimeString(dateString.toString(), precision!!)
                )
        dateString.append(zoneOffset.getId())
        this.dateTime = (offsetDateTimeParse(dateString.toString()))
    }

    fun expandPartialMinFromPrecision(precision: Precision): DateTime {
        var odt = this.dateTime!!.plusYears(0)
        for (i in precision.toDateTimeIndex() + 1..6) {
            odt =
                odt.with(
                    Precision.fromDateTimeIndex(i).toChronoField(),
                    odt.range(Precision.fromDateTimeIndex(i).toChronoField()).getMinimum(),
                )
        }
        return DateTime(odt, this.precision!!)
    }

    fun expandPartialMin(precision: Precision?): DateTime {
        val odt = this.dateTime!!.plusYears(0)
        return DateTime(odt, if (precision == null) Precision.MILLISECOND else precision)
    }

    fun expandPartialMax(precision: Precision?): DateTime {
        var odt = this.dateTime!!.plusYears(0)
        for (i in this.precision!!.toDateTimeIndex() + 1..6) {
            if (i <= precision!!.toDateTimeIndex()) {
                odt =
                    odt.with(
                        Precision.fromDateTimeIndex(i).toChronoField(),
                        odt.range(Precision.fromDateTimeIndex(i).toChronoField()).getMaximum(),
                    )
            } else {
                odt =
                    odt.with(
                        Precision.fromDateTimeIndex(i).toChronoField(),
                        odt.range(Precision.fromDateTimeIndex(i).toChronoField()).getMinimum(),
                    )
            }
        }
        return DateTime(odt, if (precision == null) Precision.MILLISECOND else precision)
    }

    override fun isUncertain(p: Precision): Boolean {
        var precision = p
        if (precision == Precision.WEEK) {
            precision = Precision.DAY
        }

        return this.precision!!.toDateTimeIndex() < precision.toDateTimeIndex()
    }

    override fun getUncertaintyInterval(p: Precision): Interval {
        val start = expandPartialMin(p)
        val end = expandPartialMax(p).expandPartialMinFromPrecision(p)
        return Interval(start, true, end, true)
    }

    override fun roundToPrecision(precision: Precision, useCeiling: Boolean): BaseTemporal {
        var precision = precision
        val originalPrecision = this.precision
        val originalOffsetDateTime =
            TemporalHelper.truncateToPrecision(this.dateTime!!, originalPrecision!!)
        precision =
            precision.weekAsDay() // Precision.WEEK is treated as Precision.DAY for the purposes of
        // rounding
        if (precision.toDateTimeIndex() < originalPrecision.toDateTimeIndex()) {
            val floorOffsetDateTime =
                TemporalHelper.truncateToPrecision(originalOffsetDateTime, precision)
            if (useCeiling && floorOffsetDateTime != originalOffsetDateTime) {
                val ceilingOffsetDateTime = floorOffsetDateTime.plus(1, precision.toChronoUnit())
                return DateTime(ceilingOffsetDateTime, precision)
            } else {
                return DateTime(floorOffsetDateTime, precision)
            }
        } else {
            return DateTime(originalOffsetDateTime, originalPrecision)
        }
    }

    override fun compare(other: BaseTemporal, forSort: Boolean): Int? {
        val differentPrecisions = this.precision != other.precision

        if (differentPrecisions) {
            val result =
                this.compareToPrecision(
                    other,
                    Precision.getHighestDateTimePrecision(this.precision!!, other.precision!!),
                )
            if (result == null && forSort) {
                return if (this.precision!!.toDateTimeIndex() > other.precision!!.toDateTimeIndex())
                    1
                else -1
            }
            return result
        } else {
            return compareToPrecision(other, this.precision!!)
        }
    }

    fun getNormalized(precision: Precision): OffsetDateTime {
        return getNormalized(precision, zoneOffset)
    }

    fun getNormalized(precision: Precision, nullableZoneOffset: ZoneOffset?): OffsetDateTime {
        if (precision.toDateTimeIndex() > Precision.DAY.toDateTimeIndex()) {
            if (nullableZoneOffset != null) {
                return dateTime!!.withOffsetSameInstant(nullableZoneOffset)
            }

            throw IllegalStateException("There must be a non-null offset!")
        }

        return dateTime!!
    }

    override fun compareToPrecision(other: BaseTemporal, p: Precision): Int? {
        var precision = p
        val leftMeetsPrecisionRequirements =
            this.precision!!.toDateTimeIndex() >= precision.toDateTimeIndex()
        val rightMeetsPrecisionRequirements =
            other.precision!!.toDateTimeIndex() >= precision.toDateTimeIndex()

        // adjust dates to evaluation offset
        val leftDateTime = this.getNormalized(precision)
        val rightDateTime = (other as DateTime).getNormalized(precision, this.zoneOffset)

        if (!leftMeetsPrecisionRequirements || !rightMeetsPrecisionRequirements) {
            precision = Precision.getLowestDateTimePrecision(this.precision!!, other.precision!!)
        }

        for (i in 0..<precision.toDateTimeIndex() + 1) {
            val leftComp = leftDateTime.get(Precision.getDateTimeChronoFieldFromIndex(i))
            val rightComp = rightDateTime.get(Precision.getDateTimeChronoFieldFromIndex(i))
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

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || this::class != other::class) {
            return false
        }

        val otherDateTime = other as DateTime

        return zoneOffset == otherDateTime.zoneOffset &&
            precision == otherDateTime.precision &&
            dateTime == otherDateTime.dateTime
    }

    override fun hashCode(): Int {
        //        return Objects.hash(zoneOffset, dateTime)
        var result = zoneOffset.hashCode()
        result = 31 * result + (dateTime?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        when (precision) {
            Precision.YEAR -> return dateTime!!.getYear().toPaddedString(4)
            Precision.MONTH ->
                return "${
                    dateTime!!.getYear().toPaddedString(4)
                }-${
                    dateTime!!.getMonthValue().toPaddedString(2)
                }"
            Precision.DAY ->
                return "${
                    dateTime!!.getYear().toPaddedString(4)
                }-${
                    dateTime!!.getMonthValue().toPaddedString(2)
                }-${
                    dateTime!!.getDayOfMonth().toPaddedString(2)
                }"
            Precision.HOUR ->
                return "${
                dateTime!!.getYear().toPaddedString(4)
            }-${
                dateTime!!.getMonthValue().toPaddedString(2)
            }-${
                dateTime!!.getDayOfMonth().toPaddedString(2)
            }T${
                dateTime!!.getHour().toPaddedString(2)
            }"
            Precision.MINUTE ->
                return "${
                    dateTime!!.getYear().toPaddedString(4)
                }-${
                    dateTime!!.getMonthValue().toPaddedString(2)
                }-${
                    dateTime!!.getDayOfMonth().toPaddedString(2)
                }T${
                    dateTime!!.getHour().toPaddedString(2)
                }:${
                    dateTime!!.getMinute().toPaddedString(2)
                }"
            Precision.SECOND ->
                return "${
                    dateTime!!.getYear().toPaddedString(4)
                }-${
                    dateTime!!.getMonthValue().toPaddedString(2)
                }-${
                    dateTime!!.getDayOfMonth().toPaddedString(2)
                }T${
                    dateTime!!.getHour().toPaddedString(2)
                }:${
                    dateTime!!.getMinute().toPaddedString(2)
                }:${
                    dateTime!!.getSecond().toPaddedString(2)
                }"
            else -> {
                val offsetSeconds = this.zoneOffset.getTotalSeconds()
                return "${
                    dateTime!!.getYear().toPaddedString(4)
                }-${
                    dateTime!!.getMonthValue().toPaddedString(2)
                }-${
                    dateTime!!.getDayOfMonth().toPaddedString(2)
                }T${
                    dateTime!!.getHour().toPaddedString(2)
                }:${
                    dateTime!!.getMinute().toPaddedString(2)
                }:${
                    dateTime!!.getSecond().toPaddedString(2)
                }.${
                    dateTime!!.get(precision!!.toChronoField()).toPaddedString(3)
                }${if (offsetSeconds >= 0) "+" else "-"}${
                    (abs(offsetSeconds) / 3600).toPaddedString(2)
                }:${
                    ((abs(offsetSeconds) % 3600) / 60).toPaddedString(2)
                }"
            }
        }
    }

    // conversion functions
    fun toJavaDate(): Date {
        return dateFrom(dateTime!!.toInstant())
    }

    fun toDateString(): String {
        return dateTimeFormatterIsoOffsetDateTimeFormat(dateTime!!)
    }

    private fun toZoneOffset(offsetDateTime: OffsetDateTime?): ZoneOffset {
        return offsetDateTime!!.getOffset()
    }

    private fun toZoneOffset(offsetAsBigDecimal: BigDecimal): ZoneOffset {
        return zoneOffsetOfHoursMinutes(
            offsetAsBigDecimal.toInt(),
            BigDecimal(60).multiply(offsetAsBigDecimal.remainder(ONE)).toInt(),
        )
    }
}
