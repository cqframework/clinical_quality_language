package org.opencds.cqf.cql.engine.runtime

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Date
import kotlin.math.abs
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.InvalidDateTime

class DateTime : BaseTemporal {
    val zoneOffset: ZoneOffset

    var dateTime: OffsetDateTime? = null
        set(dateTime) {
            if (dateTime!!.year < 1) {
                throw InvalidDateTime(
                    String.format(
                        "The year: %d falls below the accepted bounds of 0001-9999.",
                        dateTime.year,
                    )
                )
            }

            if (dateTime.year > 9999) {
                throw InvalidDateTime(
                    String.format(
                        "The year: %d falls above the accepted bounds of 0001-9999.",
                        dateTime.year,
                    )
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
                dateString += offset.id
            }
        } else {
            size +=
                dateString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
            precision = Precision.fromDateTimeIndex(size - 1)
            dateString = TemporalHelper.autoCompleteDateTimeString(dateString, precision!!)
            dateString += offset.id
        }

        this.dateTime = (OffsetDateTime.parse(dateString))
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
        dateString.append(zoneOffset.id)
        this.dateTime = (OffsetDateTime.parse(dateString.toString()))
    }

    fun expandPartialMinFromPrecision(precision: Precision): DateTime {
        var odt = this.dateTime!!.plusYears(0)
        for (i in precision.toDateTimeIndex() + 1..6) {
            odt =
                odt.with(
                    Precision.fromDateTimeIndex(i).toChronoField(),
                    odt.range(Precision.fromDateTimeIndex(i).toChronoField()).minimum,
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
                        odt.range(Precision.fromDateTimeIndex(i).toChronoField()).maximum,
                    )
            } else {
                odt =
                    odt.with(
                        Precision.fromDateTimeIndex(i).toChronoField(),
                        odt.range(Precision.fromDateTimeIndex(i).toChronoField()).minimum,
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

    override fun equivalent(other: Any?): Boolean? {
        val comparison = compare((other as BaseTemporal?)!!, false)
        return comparison != null && comparison == 0
    }

    override fun equal(other: Any?): Boolean? {
        val comparison = compare((other as BaseTemporal?)!!, false)
        return if (comparison == null) null else comparison == 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val otherDateTime = other as DateTime

        return zoneOffset == otherDateTime.zoneOffset &&
            precision == otherDateTime.precision &&
            dateTime == otherDateTime.dateTime
    }

    override fun hashCode(): Int {
        return Objects.hash(zoneOffset, dateTime)
    }

    override fun toString(): String {
        when (precision) {
            Precision.YEAR -> return String.format("%04d", dateTime!!.year)
            Precision.MONTH ->
                return String.format("%04d-%02d", dateTime!!.year, dateTime!!.monthValue)
            Precision.DAY ->
                return String.format(
                    "%04d-%02d-%02d",
                    dateTime!!.year,
                    dateTime!!.monthValue,
                    dateTime!!.dayOfMonth,
                )
            Precision.HOUR ->
                return String.format(
                    "%04d-%02d-%02dT%02d",
                    dateTime!!.year,
                    dateTime!!.monthValue,
                    dateTime!!.dayOfMonth,
                    dateTime!!.hour,
                )
            Precision.MINUTE ->
                return String.format(
                    "%04d-%02d-%02dT%02d:%02d",
                    dateTime!!.year,
                    dateTime!!.monthValue,
                    dateTime!!.dayOfMonth,
                    dateTime!!.hour,
                    dateTime!!.minute,
                )
            Precision.SECOND ->
                return String.format(
                    "%04d-%02d-%02dT%02d:%02d:%02d",
                    dateTime!!.year,
                    dateTime!!.monthValue,
                    dateTime!!.dayOfMonth,
                    dateTime!!.hour,
                    dateTime!!.minute,
                    dateTime!!.second,
                )
            else -> {
                val offsetSeconds = this.zoneOffset.totalSeconds
                return String.format(
                    "%04d-%02d-%02dT%02d:%02d:%02d.%03d%s%02d:%02d",
                    dateTime!!.year,
                    dateTime!!.monthValue,
                    dateTime!!.dayOfMonth,
                    dateTime!!.hour,
                    dateTime!!.minute,
                    dateTime!!.second,
                    dateTime!!.get(precision!!.toChronoField()),
                    if (offsetSeconds >= 0) "+" else "-",
                    abs(offsetSeconds) / 3600,
                    (abs(offsetSeconds) % 3600) / 60,
                )
            }
        }
    }

    // conversion functions
    fun toJavaDate(): Date {
        return Date.from(dateTime!!.toInstant())
    }

    fun toDateString(): String {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime)
    }

    private fun toZoneOffset(offsetDateTime: OffsetDateTime?): ZoneOffset {
        return offsetDateTime!!.offset
    }

    private fun toZoneOffset(offsetAsBigDecimal: BigDecimal): ZoneOffset {
        return ZoneOffset.ofHoursMinutes(
            offsetAsBigDecimal.toInt(),
            BigDecimal(60).multiply(offsetAsBigDecimal.remainder(BigDecimal.ONE)).toInt(),
        )
    }
}
