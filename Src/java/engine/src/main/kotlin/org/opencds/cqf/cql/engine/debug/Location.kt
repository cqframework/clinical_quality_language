package org.opencds.cqf.cql.engine.debug

/*
Identifies a location in a source file
 */
class Location(val startLine: Int, val startChar: Int, val endLine: Int, val endChar: Int) {
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val other = o as Location

        if (endChar != other.endChar) {
            return false
        }
        if (endLine != other.endLine) {
            return false
        }
        if (startChar != other.startChar) {
            return false
        }
        if (startLine != other.startLine) {
            return false
        }

        return true
    }

    /*
    Returns true if this location includes the other location (i.e. starts on or before and ends on or after)
     */
    fun includes(other: Location): Boolean {
        requireNotNull(other) { "other required" }

        if (this.startLine > other.startLine) {
            return false
        }

        if (this.startLine == other.startLine && this.startChar > other.startChar) {
            return false
        }

        if (this.endLine < other.endLine) {
            return false
        }

        if (this.endLine == other.endLine && this.endChar < other.endChar) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = 13
        result = 31 * result + startLine
        result = 31 * result + startChar
        result = 31 * result + endLine
        result = 31 * result + endChar
        return result
    }

    override fun toString(): String {
        return ("Location{" +
            " startLine=" +
            startLine +
            ", startChar=" +
            startChar +
            ", endLine=" +
            endLine +
            ", endChar=" +
            endChar +
            '}')
    }

    fun toLocator(): String {
        return if (startLine == endLine && startChar == endChar)
            String.format("%s:%s", startLine, startChar)
        else String.format("%s:%s-%s:%s", startLine, startChar, endLine, endChar)
    }

    companion object {
        @JvmStatic
        fun fromLocator(locator: String): Location {
            require(!(locator == null || locator.trim { it <= ' ' }.isEmpty())) {
                "locator required"
            }

            var startLine = 0
            var startChar = 0
            var endLine = 0
            var endChar = 0
            val locations: Array<String?> =
                locator.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in locations.indices) {
                val ranges: Array<String?> =
                    locations[i]!!
                        .split(":".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                require(ranges.size == 2) { String.format("Invalid locator format: %s", locator) }
                if (i == 0) {
                    startLine = ranges[0]!!.toInt()
                    startChar = ranges[1]!!.toInt()
                } else {
                    endLine = ranges[0]!!.toInt()
                    endChar = ranges[1]!!.toInt()
                }
            }

            if (locations.size == 1) {
                endLine = startLine
                endChar = startChar
            }

            return Location(startLine, startChar, endLine, endChar)
        }
    }
}
