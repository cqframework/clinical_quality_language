package org.opencds.cqf.cql.engine.runtime

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.*
import java.util.Date
import org.opencds.cqf.cql.engine.exception.InvalidDate
import org.opencds.cqf.cql.engine.execution.State

class Date : BaseTemporal {
    var date: LocalDate? = null
        set(date) {
            if (date!!.year < 1) {
                throw InvalidDate(
                    String.format(
                        Locale.US,
                        "The year: %d falls below the accepted bounds of 0001-9999.",
                        date.year,
                    )
                )
            }
            if (date.year > 9999) {
                throw InvalidDate(
                    String.format(
                        Locale.US,
                        "The year: %d falls above the accepted bounds of 0001-9999.",
                        date.year,
                    )
                )
            }
            if (this.precision == null) {
                this.precision = Precision.DAY
            }
            field = date
        }

    constructor(year: Int) {
        this.date = (LocalDate.of(year, 1, 1))
        this.precision = Precision.YEAR
    }

    constructor(year: Int, month: Int) {
        this.date = (LocalDate.of(year, month, 1))
        this.precision = Precision.MONTH
    }

    constructor(year: Int, month: Int, day: Int) {
        this.date = (LocalDate.of(year, month, day))
    }

    constructor(date: LocalDate, precision: Precision) {
        this.date = date
        this.precision = precision
    }

    constructor(dateString: String) {
        var dateString = dateString
        precision =
            Precision.fromDateIndex(
                dateString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size -
                    1
            )
        dateString = TemporalHelper.autoCompleteDateString(dateString, precision!!)
        this.date = (LocalDate.parse(dateString))
    }

    constructor(date: LocalDate) {
        this.date = (date)
    }

    fun expandPartialMinFromPrecision(
        precision: Precision
    ): org.opencds.cqf.cql.engine.runtime.Date {
        var ld = this.date!!.plusYears(0)
        for (i in precision.toDateIndex() + 1..2) {
            ld =
                ld.with(
                    Precision.fromDateIndex(i).toChronoField(),
                    ld.range(Precision.fromDateIndex(i).toChronoField()).minimum,
                )
        }
        return org.opencds.cqf.cql.engine.runtime
            .Date(ld.year, ld.monthValue, ld.dayOfMonth)
            .withPrecision(precision) as org.opencds.cqf.cql.engine.runtime.Date
    }

    private fun expandPartialMin(precision: Precision?): org.opencds.cqf.cql.engine.runtime.Date {
        val ld = this.date!!.plusYears(0)
        return org.opencds.cqf.cql.engine.runtime
            .Date(ld.year, ld.monthValue, ld.dayOfMonth)
            .withPrecision(precision) as org.opencds.cqf.cql.engine.runtime.Date
    }

    fun expandPartialMax(precision: Precision): org.opencds.cqf.cql.engine.runtime.Date {
        var ld = this.date!!.plusYears(0)
        for (i in this.precision!!.toDateIndex() + 1..2) {
            ld =
                if (i <= precision.toDateIndex()) {
                    ld.with(
                        Precision.fromDateIndex(i).toChronoField(),
                        ld.range(Precision.fromDateIndex(i).toChronoField()).maximum,
                    )
                } else {
                    ld.with(
                        Precision.fromDateIndex(i).toChronoField(),
                        ld.range(Precision.fromDateIndex(i).toChronoField()).minimum,
                    )
                }
        }
        return org.opencds.cqf.cql.engine.runtime
            .Date(ld.year, ld.monthValue, ld.dayOfMonth)
            .withPrecision(precision) as org.opencds.cqf.cql.engine.runtime.Date
    }

    override fun compare(other: BaseTemporal, forSort: Boolean): Int? {
        val differentPrecisions = this.precision != other.precision

        if (differentPrecisions) {
            val result =
                this.compareToPrecision(
                    other,
                    Precision.getHighestDatePrecision(this.precision!!, other.precision!!),
                )
            if (result == null && forSort) {
                return if (this.precision!!.toDateIndex() > other.precision!!.toDateIndex()) 1
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
            this.precision!!.toDateIndex() >= precision.toDateIndex()
        val rightMeetsPrecisionRequirements =
            other.precision!!.toDateIndex() >= precision.toDateIndex()

        if (!leftMeetsPrecisionRequirements || !rightMeetsPrecisionRequirements) {
            precision = Precision.getLowestDatePrecision(this.precision!!, other.precision!!)
        }

        for (i in 0..<precision.toDateIndex() + 1) {
            val leftComp = this.date!!.get(Precision.getDateChronoFieldFromIndex(i))
            val rightComp =
                (other as org.opencds.cqf.cql.engine.runtime.Date)
                    .date!!
                    .get(Precision.getDateChronoFieldFromIndex(i))
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

    override fun isUncertain(p: Precision): Boolean {
        var precision = p
        if (precision == Precision.WEEK) {
            precision = Precision.DAY
        }

        return this.precision!!.toDateIndex() < precision.toDateIndex()
    }

    override fun getUncertaintyInterval(p: Precision): Interval {
        val start = expandPartialMin(p)
        val end = expandPartialMax(p).expandPartialMinFromPrecision(p)
        return Interval(start, true, end, true)
    }

    override fun roundToPrecision(precision: Precision, useCeiling: Boolean): BaseTemporal {
        var precision = precision
        val originalPrecision = this.precision
        val originalLocalDate = TemporalHelper.truncateToPrecision(this.date!!, originalPrecision!!)
        precision =
            precision.weekAsDay() // Precision.WEEK is treated as Precision.DAY for the purposes of
        // rounding
        when (precision) {
            Precision.YEAR,
            Precision.MONTH,
            Precision.DAY ->
                if (precision.toDateIndex() < originalPrecision.toDateIndex()) {
                    val floorLocalDate =
                        TemporalHelper.truncateToPrecision(originalLocalDate, precision)
                    if (useCeiling && floorLocalDate != originalLocalDate) {
                        val ceilingLocalDate = floorLocalDate.plus(1, precision.toChronoUnit())
                        return Date(ceilingLocalDate, precision)
                    } else {
                        return Date(floorLocalDate, precision)
                    }
                } else {
                    return Date(originalLocalDate, originalPrecision)
                }
            else -> return Date(originalLocalDate, originalPrecision)
        }
    }

    override fun compareTo(other: BaseTemporal): Int {
        return this.compare(other, true)!!
    }

    override fun equivalent(other: Any?): Boolean {
        val comparison = compare((other as BaseTemporal?)!!, false)
        return comparison != null && comparison == 0
    }

    override fun equal(other: Any?): Boolean? {
        val comparison = compare((other as BaseTemporal?)!!, false)
        return if (comparison == null) null else comparison == 0
    }

    override fun toString(): String {
        return when (precision) {
            Precision.YEAR -> String.format(Locale.US, "%04d", date!!.year)
            Precision.MONTH -> String.format(Locale.US, "%04d-%02d", date!!.year, date!!.monthValue)

            else ->
                String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    date!!.year,
                    date!!.monthValue,
                    date!!.dayOfMonth,
                )
        }
    }

    @JvmOverloads
    fun toJavaDate(c: State? = null): Date {
        var zonedDateTime: ZonedDateTime? = null
        zonedDateTime =
            if (c != null) {
                date!!.atStartOfDay(c.evaluationZonedDateTime!!.zone)
            } else {
                date!!.atStartOfDay(TimeZone.getDefault().toZoneId())
            }
        val instant = zonedDateTime.toInstant()
        val date = Date.from(instant)
        return date
    }

    companion object {
        fun fromJavaDate(date: Date): DateTime {
            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            return DateTime(
                OffsetDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()),
                Precision.MILLISECOND,
            )
        }
    }
}
