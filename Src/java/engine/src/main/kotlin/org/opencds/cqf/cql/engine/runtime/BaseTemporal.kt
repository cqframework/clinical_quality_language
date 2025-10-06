package org.opencds.cqf.cql.engine.runtime

abstract class BaseTemporal : CqlType, Comparable<BaseTemporal> {
    var precision: Precision? = null

    fun withPrecision(precision: Precision?): BaseTemporal {
        this.precision = precision
        return this
    }

    abstract fun compare(other: BaseTemporal, forSort: Boolean): Int?

    abstract fun compareToPrecision(other: BaseTemporal, p: Precision): Int?

    abstract fun isUncertain(p: Precision): Boolean

    abstract fun getUncertaintyInterval(p: Precision): Interval?

    /**
     * Returns a copy of this temporal value rounded to the specified precision if the value has a
     * greater precision.
     *
     * @param precision the precision to round to
     * @param useCeiling whether to return the ceiling or floor value when rounding
     * @return the rounded copy
     */
    abstract fun roundToPrecision(precision: Precision, useCeiling: Boolean): BaseTemporal?

    companion object {
        fun getHighestPrecision(vararg values: BaseTemporal?): String {
            var max = -1
            var isDateTime = true
            var isDate = false
            for (baseTemporal in values) {
                if (baseTemporal is DateTime) {
                    if (baseTemporal.precision!!.toDateTimeIndex() > max) {
                        max = baseTemporal.precision!!.toDateTimeIndex()
                    }
                } else if (baseTemporal is Date) {
                    isDateTime = false
                    isDate = true
                    if (baseTemporal.precision!!.toTimeIndex() > max) {
                        max = baseTemporal.precision!!.toDateIndex()
                    }
                } else if (baseTemporal is Time) {
                    isDateTime = false
                    if (baseTemporal.precision!!.toTimeIndex() > max) {
                        max = baseTemporal.precision!!.toTimeIndex()
                    }
                }
            }

            if (max == -1) {
                return Precision.MILLISECOND.toString()
            }

            return if (isDateTime) Precision.fromDateTimeIndex(max).toString()
            else if (isDate) Precision.fromDateIndex(max).toString()
            else Precision.fromTimeIndex(max).toString()
        }

        fun getLowestPrecision(vararg values: BaseTemporal?): String {
            var min = 99
            var isDateTime = true
            var isDate = false
            for (baseTemporal in values) {
                if (baseTemporal is DateTime) {
                    if (baseTemporal.precision!!.toDateTimeIndex() < min) {
                        min = baseTemporal.precision!!.toDateTimeIndex()
                    }
                } else if (baseTemporal is Date) {
                    isDateTime = false
                    isDate = true
                    if (baseTemporal.precision!!.toDateIndex() < min) {
                        min = baseTemporal.precision!!.toDateIndex()
                    }
                } else if (baseTemporal is Time) {
                    isDateTime = false
                    if (baseTemporal.precision!!.toTimeIndex() < min) {
                        min = baseTemporal.precision!!.toTimeIndex()
                    }
                }
            }

            if (min == 99) {
                return Precision.YEAR.toString()
            }

            return if (isDateTime) Precision.fromDateTimeIndex(min).toString()
            else if (isDate) Precision.fromDateIndex(min).toString()
            else Precision.fromTimeIndex(min).toString()
        }
    }
}
