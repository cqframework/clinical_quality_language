package org.cqframework.cql.cql2elm.model

import java.util.regex.Pattern
import kotlin.math.max

/** Created by Bryn on 3/2/2017. */
/**
 * Implements a comparable version for use in comparing CQL artifact versions. Supports versions
 * specified in filename strings according to the following pattern:
 * [v]{{major}}(.|-){{minor}}(.|-){{patch}}(.|-){{build}} where major, minor, and patch are all
 * required to be unsigned integers, and build is any string
 *
 * Examples: 1.0.0 -&gt; major: 1, minor: 0, patch: 0 v1-0-0 -&gt; major: 1, minor: 0, patch: 0
 * v1-0-0-SNAPSHOT -&gt; major: 1, minor: 0, patch: 0, build: snapshot
 *
 * NOTE: Deliberately not using Apache ComparableVersion to a) avoid dependencies on Maven and b)
 * allow for more flexible version strings used by MAT file naming conventions.
 */
data class Version(private val version: String) : Comparable<Version?> {
    init {
        initVersion()
    }

    var majorVersion: Int? = null
        private set

    var minorVersion: Int? = null
        private set

    var patchVersion: Int? = null
        private set

    var buildVersion: String? = null
        private set

    private fun initVersion() {
        val parts = this.version.split("[.\\-]".toRegex()).dropLastWhile { it.isEmpty() }
        for (i in 0 until max(parts.size, 4)) {
            var part = if (i < parts.size) parts[i] else ""
            if (part.startsWith("v")) {
                part = part.substring(1)
            }

            when (i) {
                0 -> majorVersion = part.toUnsignedIntOrNull()
                1 -> minorVersion = part.toUnsignedIntOrNull()
                2 -> patchVersion = part.toUnsignedIntOrNull()
                3 -> buildVersion = part
                else -> buildVersion += "-$part"
            }
        }
    }

    private fun compareTo(that: Version?, level: Int): Int {
        if (that == null) return 1
        require(this.isComparable && that.isComparable) { "The versions are not comparable" }

        for (i in 0 until max(level, 4)) {

            val comparison =
                when (i) {
                    0 -> majorVersion.compareToNullable(that.majorVersion)
                    1 -> minorVersion.compareToNullable(that.minorVersion)
                    2 -> patchVersion.compareToNullable(that.patchVersion)
                    3 -> buildVersion.compareToNullable(that.buildVersion)
                    else -> 0
                }

            if (comparison != 0) return comparison
        }
        return 0
    }

    private fun String?.compareToNullable(that: String?): Int {
        return when {
            this == null && that == null -> 0
            this == null -> -1
            that == null -> 1
            else -> this.compareTo(that, ignoreCase = true)
        }
    }

    private fun Int?.compareToNullable(that: Int?): Int {
        return when {
            this == null && that == null -> 0
            this == null -> -1
            that == null -> 1
            else -> this.compareTo(that)
        }
    }

    override fun compareTo(other: Version?): Int {
        return compareTo(other, 4)
    }

    fun compatibleWith(that: Version?): Boolean {
        if (that == null) return false

        if (!isComparable || !that.isComparable) {
            return matchStrictly(that)
        }

        return compareTo(that, 2) >= 0
    }

    fun matchStrictly(that: Version): Boolean {
        return this.version == that.version
    }

    private fun String.toUnsignedIntOrNull(): Int? {
        return if (isUnsignedInteger.matcher(this).matches()) this.toInt() else null
    }

    private fun isComparable(level: Int): Boolean {
        return when (level) {
            0 -> majorVersion != null
            1 -> majorVersion != null && minorVersion != null
            2 -> majorVersion != null && minorVersion != null && patchVersion != null
            3 ->
                majorVersion != null &&
                    minorVersion != null &&
                    patchVersion != null &&
                    buildVersion != null
            else -> false
        }
    }

    val isComparable: Boolean
        get() = this.isComparable(2)

    override fun toString(): String {
        return version
    }

    companion object {
        private val isUnsignedInteger: Pattern = Pattern.compile("[0-9]+")
    }
}
