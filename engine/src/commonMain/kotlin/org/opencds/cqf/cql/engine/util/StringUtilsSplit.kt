package org.opencds.cqf.cql.engine.util

/**
 * Splits [string] at every occurrence of any of the characters in [separator] and drops empty
 * parts, the same way as Apache Commons Lang `StringUtils.split(String, String)`. For example,
 * splitting `",a,,b,"` on `","` gives `["a", "b"]`, and splitting `"a<b>c"` on `"<>"` gives `["a",
 * "b", "c"]`.
 */
fun stringUtilsSplit(string: String, separator: String): Array<String> {
    val parts = mutableListOf<String>()
    val part = StringBuilder()
    for (c in string) {
        if (c in separator) {
            if (part.isNotEmpty()) {
                parts.add(part.toString())
                part.clear()
            }
        } else {
            part.append(c)
        }
    }
    if (part.isNotEmpty()) {
        parts.add(part.toString())
    }
    return parts.toTypedArray()
}
