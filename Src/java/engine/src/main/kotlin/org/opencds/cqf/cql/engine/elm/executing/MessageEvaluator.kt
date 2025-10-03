package org.opencds.cqf.cql.engine.elm.executing

import java.util.Locale
import java.util.Optional
import java.util.function.Function
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Message
import org.opencds.cqf.cql.engine.data.DataProvider
import org.opencds.cqf.cql.engine.debug.SourceLocator
import org.opencds.cqf.cql.engine.debug.SourceLocator.Companion.fromNode
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.State
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MessageEvaluator {
    val logger: Logger = LoggerFactory.getLogger(MessageEvaluator::class.java)

    fun message(
        state: State?,
        sourceLocator: SourceLocator?,
        source: Any?,
        condition: Boolean?,
        code: String?,
        severity: String?,
        message: String?,
    ): Any? {
        var severity = severity
        if (severity == null) {
            severity = "message"
        }

        if (condition != null && condition) {
            val messageBuilder = StringBuilder()
            if (code != null) {
                messageBuilder.append(code).append(": ")
            }
            when (severity.lowercase(Locale.getDefault())) {
                "message" -> {
                    val finalMessage = messageBuilder.append(message).toString()
                    state!!.logDebugMessage(sourceLocator, finalMessage)
                    logger.info(finalMessage)
                }
                "warning" -> {
                    val finalMessage = messageBuilder.append(message).toString()
                    state!!.logDebugWarning(sourceLocator, finalMessage)
                    logger.warn(finalMessage)
                }
                "trace" -> {
                    val finalMessage =
                        messageBuilder
                            .append(message)
                            .append(String.format("%n%s", stripPHI(state, source)))
                            .toString()
                    state!!.logDebugTrace(sourceLocator, finalMessage)
                    logger.debug(finalMessage)
                }
                "error" -> {
                    val finalMessage =
                        messageBuilder
                            .append(message)
                            .append(String.format("%n%s", stripPHI(state, source)))
                            .toString()
                    // NOTE: debug logging happens through exception-handling
                    logger.error(finalMessage)
                    throw CqlException(finalMessage)
                }
            }
        }
        return source
    }

    private fun stripPHI(state: State?, source: Any?): String? {
        if (source == null) {
            return null
        }

        val dataProvider: Optional<DataProvider> =
            Optional.ofNullable(
                state!!
                    .environment
                    .resolveDataProvider(source.javaClass.getPackage().getName(), false)
            )

        return dataProvider
            .map(Function { obj -> obj.phiObfuscationSupplier() })
            .map(Function { obj -> obj.get() })
            .map(Function { obfuscator -> obfuscator!!.obfuscate(source) })
            .orElse("")
    }

    fun internalEvaluate(
        elm: Message?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val source = visitor.visitExpression(elm!!.source!!, state)
        val condition = visitor.visitExpression(elm.condition!!, state) as Boolean?
        val code = visitor.visitExpression(elm.code!!, state) as String?
        val severity = visitor.visitExpression(elm.severity!!, state) as String?
        val msg = visitor.visitExpression(elm.message!!, state) as String?

        return message(
            state,
            fromNode(elm, state!!.getCurrentLibrary()),
            source,
            condition,
            code,
            severity,
            msg,
        )
    }
}
