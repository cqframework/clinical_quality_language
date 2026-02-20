package org.opencds.cqf.cql.engine.util

import org.apache.commons.lang3.StringUtils

actual fun stringUtilsSplit(string: String, separator: String): Array<String> {
    return StringUtils.split(string, separator)
}
