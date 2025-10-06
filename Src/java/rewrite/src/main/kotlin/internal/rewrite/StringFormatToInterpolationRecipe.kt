package internal.rewrite

import java.util.regex.Pattern
import org.openrewrite.ExecutionContext
import org.openrewrite.Recipe
import org.openrewrite.Tree.randomId
import org.openrewrite.TreeVisitor
import org.openrewrite.java.tree.J
import org.openrewrite.kotlin.KotlinVisitor
import org.openrewrite.kotlin.tree.K

class StringFormatToInterpolationRecipe : Recipe() {
    override fun getDisplayName() = "Convert String.format to Kotlin string interpolation"

    override fun getDescription() =
        "Converts String.format calls to Kotlin string interpolation using template expressions."

    override fun getVisitor(): TreeVisitor<*, ExecutionContext> {
        return StringFormatToInterpolationVisitor()
    }

    private class StringFormatToInterpolationVisitor : KotlinVisitor<ExecutionContext>() {

        override fun visitMethodInvocation(method: J.MethodInvocation, ctx: ExecutionContext): J {
            val call = super.visitMethodInvocation(method, ctx) as J.MethodInvocation

            // Check if this is a String.format method invocation
            if (isStringFormatCall(call)) {
                val interpolatedString = createInterpolatedString(call)
                if (interpolatedString != null) {
                    // Mark this node for replacement using the cursor
                    val literal =
                        J.Literal(
                            call.id,
                            call.prefix,
                            call.markers,
                            interpolatedString,
                            "\"$interpolatedString\"",
                            null,
                            org.openrewrite.java.tree.JavaType.Primitive.String,
                        )

                    cursor.putMessage("replaceWithLiteral", true)
                    return K.ExpressionStatement(randomId(), literal)
                }
            }

            return call
        }

        private fun isStringFormatCall(method: J.MethodInvocation): Boolean {
            val select = method.select
            val methodName = method.simpleName
            val arg = method.typeParameters?.firstOrNull()

            return methodName == "format" &&
                (((select is J.Identifier && select.simpleName == "String") ||
                    (select is J.FieldAccess &&
                        select.target is J.Identifier &&
                        (select.target as J.Identifier).simpleName == "String"))) &&
                !(arg.let { (it as? J.FieldAccess)?.type?.javaClass?.simpleName == "Locale" })
        }

        private fun createInterpolatedString(method: J.MethodInvocation): String? {
            val arguments = method.arguments
            if (arguments.isEmpty()) return null

            val formatExpression = arguments[0]
            if (formatExpression !is J.Literal) return null

            val formatValue = formatExpression.value as? String ?: return null
            val formatArgs = arguments.drop(1)

            return try {
                convertFormatStringToInterpolation(formatValue, formatArgs)
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
                            specifier.contains("02d") ->
                                sb.append($$"${($$argText).toString().padStart(2, '0')}")
                            specifier.contains("03d") ->
                                sb.append($$"${($$argText).toString().padStart(3, '0')}")
                            specifier.contains("04d") ->
                                sb.append($$"${($$argText).toString().padStart(4, '0')}")
                            specifier.contains("02x") ->
                                sb.append($$"${($$argText).toString(16).padStart(2, '0')}")
                            specifier.contains("x") -> sb.append($$"${($$argText).toString(16)}")
                            specifier.contains("X") ->
                                sb.append($$"${$$(argText).toString(16).uppercase()}")
                            else -> {
                                // For string literals, embed them directly without interpolation
                                if (arg is J.Literal && arg.value is String) {
                                    sb.append(argText)
                                } else {
                                    // Complex expressions use ${expression} syntax
                                    sb.append($$"${$$argText}")
                                }
                            }
                        }
                    }
                    else -> sb.append(specifier) // Keep original if no more args
                }

                lastEnd = matcher.end()
            }

            sb.append(format, lastEnd, format.length)
            return sb.toString()
        }

        private fun isSimpleIdentifier(text: String): Boolean {
            // Check if the text is just a simple identifier (no dots, parentheses, brackets, etc.)
            return text.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))
        }

        private fun extractArgumentText(arg: J): String {
            return when (arg) {
                is J.Identifier -> arg.simpleName
                is J.Literal -> {
                    // For string literals, return the actual value, not the quoted version
                    when (val value = arg.value) {
                        is String -> value
                        else -> value?.toString() ?: "null"
                    }
                }
                else -> arg.printTrimmed(cursor)
            }
        }
    }
}
