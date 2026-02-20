package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator.toString

class Tuple : CqlType {
    var elements = mutableMapOf<String, Any?>()
        set(value) {
            field = value.toMutableMap()
        }

    fun getElement(key: String?): Any? {
        return elements[key]
    }

    fun withElements(elements: MutableMap<String, Any?>): Tuple {
        this.elements = elements
        return this
    }

    override fun toString(): String {
        // Kick off recursion at indent level 0
        return toPrettyString(elements, 0)
    }

    /** Recursively builds a nicely-indented string representation of a Tuple's elements. */
    private fun toPrettyString(tupleElements: Map<String, Any?>, indentLevel: Int): String {
        if (tupleElements.isEmpty()) {
            return "Tuple {}"
        }
        val sb = StringBuilder()
        val currentIndent: String =
            indent(indentLevel) // indentation for "Tuple {" and closing brace
        val childIndent: String = indent(indentLevel + 1) // indentation for fields within the tuple

        sb.append("Tuple {\n")

        // We can iterate with an index to detect the last element or simply not add an extra
        // newline
        var i = 0
        val size = tupleElements.size
        for (entry in tupleElements.entries) {
            val fieldName = entry.key
            val fieldValue = entry.value

            // Print the field name, indented one level more than "Tuple {"
            sb.append(childIndent).append(fieldName).append(": ")

            // If the field value is itself a nested Tuple, recurse
            if (fieldValue is Tuple) {
                // Recursively build nested representation
                sb.append(fieldValue.toPrettyString(fieldValue.elements, indentLevel + 1))
            } else {
                // Otherwise, use a function that handles quoting, escaping, etc.
                sb.append(toString(entry.value))
            }

            // Add a newline for each field, except possibly the last
            if (++i < size) {
                sb.append("\n")
            }
        }

        // Close the Tuple with matching indentation
        sb.append("\n").append(currentIndent).append("}")

        return sb.toString()
    }

    companion object {
        /**
         * Helper method to produce indentation spaces. For each indent level, we add two spaces.
         */
        private fun indent(level: Int): String {
            // With Android API level 28 (Java 8), we cannot use Java 11's String::repeat.
            // After upgrading, this can be replaced with:
            // return "  ".repeat(Math.max(0, level));

            val sb = StringBuilder()
            for (i in 0..<level) {
                sb.append("  ")
            }
            return sb.toString()
        }
    }
}
