package internal.rewrite

import java.util.UUID
import java.util.regex.Pattern
import org.openrewrite.ExecutionContext
import org.openrewrite.Recipe
import org.openrewrite.TreeVisitor
import org.openrewrite.java.JavaIsoVisitor
import org.openrewrite.java.MethodMatcher
import org.openrewrite.java.tree.J
import org.openrewrite.java.tree.JavaType
import org.openrewrite.marker.Markers

class StringFormatToInterpolationRecipe : Recipe() {
    override fun getDisplayName() = "Convert String.format to Kotlin string interpolation"

    override fun getDescription() =
        "Converts String.format calls to Kotlin string interpolation using template expressions"

    override fun getVisitor(): TreeVisitor<*, ExecutionContext> {
        return StringFormatToInterpolationVisitor()
    }

    private class StringFormatToInterpolationVisitor : JavaIsoVisitor<ExecutionContext>() {

        private val stringFormatMatcher = MethodMatcher("java.lang.String format(String, ..)")

        override fun visitMethodInvocation(
            method: J.MethodInvocation,
            ctx: ExecutionContext,
        ): J.MethodInvocation {
            val m = super.visitMethodInvocation(method, ctx)

            // Check if this is a String.format call
            if (stringFormatMatcher.matches(m) || isStringFormatCall(m)) {
                val replacement = createInterpolatedStringLiteral(m)
                if (replacement != null) {
                    // Return the replacement literal wrapped as a method invocation
                    // This is a workaround to return the correct type
                    return m.withSelect(null)
                        .withName(m.name.withSimpleName(""))
                        .withArguments(listOf(replacement))
                }
            }

            return m
        }

        private fun isStringFormatCall(method: J.MethodInvocation): Boolean {
            val select = method.select
            val methodName = method.name.simpleName

            if (methodName != "format") return false

            // Check for String.format pattern
            when (select) {
                is J.Identifier -> return select.simpleName == "String"
                is J.FieldAccess -> {
                    val target = select.target
                    if (target is J.Identifier && target.simpleName == "kotlin") {
                        return select.name.simpleName == "String"
                    }
                    return false
                }
                else -> return false
            }
        }

        private fun createInterpolatedStringLiteral(method: J.MethodInvocation): J.Literal? {
            val arguments = method.arguments
            if (arguments.isEmpty()) return null

            val formatString = arguments[0]
            if (formatString !is J.Literal || formatString.type != JavaType.Primitive.String)
                return null

            val formatValue = formatString.value as? String ?: return null
            val formatArgs = arguments.drop(1)

            return try {
                val interpolatedString = convertFormatStringToInterpolation(formatValue, formatArgs)

                // Create a new string literal with the interpolated content
                J.Literal(
                    UUID.randomUUID(),
                    method.prefix,
                    Markers.EMPTY,
                    interpolatedString,
                    "\"$interpolatedString\"",
                    null,
                    JavaType.Primitive.String,
                )
            } catch (_: Exception) {
                null
            }
        }

        private fun convertFormatStringToInterpolation(format: String, args: List<J>): String {
            var argIndex = 0

            // Pattern to match format specifiers like %s, %d, %f, %02x, etc.
            val formatPattern =
                Pattern.compile(
                    """%([-#+ 0,(]*)?(\*|\d+)?(\.\*|\.\d+)?[bBhHsScCdoxXeEfFgGaAtT%n]"""
                )
            val matcher = formatPattern.matcher(format)
            val sb = StringBuilder()
            var lastEnd = 0

            while (matcher.find()) {
                val specifier = matcher.group()
                sb.append(format, lastEnd, matcher.start())

                when {
                    specifier == "%%" -> sb.append("%")
                    specifier == "%n" -> sb.append("\\n")
                    argIndex < args.size -> {
                        val arg = args[argIndex]
                        argIndex++

                        // Extract the source text of the argument
                        val argText = extractArgumentText(arg)

                        // Handle special formatting cases
                        when {
                            specifier.contains("02x") ->
                                sb.append("\${$argText.toString(16).padStart(2, '0')}")
                            specifier.contains("x") -> sb.append("\${$argText.toString(16)}")
                            specifier.contains("X") ->
                                sb.append("\${$argText.toString(16).uppercase()}")
                            specifier.contains("d") -> sb.append("\${$argText}")
                            specifier.contains("f") -> sb.append("\${$argText}")
                            specifier.contains("s") || specifier.contains("S") ->
                                sb.append("\${$argText}")
                            specifier.contains("g") || specifier.contains("G") ->
                                sb.append("\${$argText}")
                            specifier.contains("e") || specifier.contains("E") ->
                                sb.append("\${$argText}")
                            else -> sb.append("\${$argText}")
                        }
                    }
                    else -> sb.append(specifier) // Keep original if no more args
                }

                lastEnd = matcher.end()
            }

            sb.append(format, lastEnd, format.length)
            return sb.toString()
        }

        private fun extractArgumentText(arg: J): String {
            return when (arg) {
                is J.Identifier -> arg.simpleName
                is J.Literal -> {
                    // For literals, we want the variable name that was passed, not the literal
                    // value
                    // This is tricky since we need the source code representation
                    arg.valueSource?.removeSurrounding("\"") ?: arg.value?.toString() ?: "null"
                }
                is J.FieldAccess -> "${extractArgumentText(arg.target)}.${arg.name.simpleName}"
                is J.MethodInvocation -> {
                    val select = arg.select?.let { "${extractArgumentText(it)}." } ?: ""
                    val args = arg.arguments.joinToString(", ") { extractArgumentText(it) }
                    "$select${arg.name.simpleName}($args)"
                }
                else -> arg.toString().trim()
            }
        }
    }
}
