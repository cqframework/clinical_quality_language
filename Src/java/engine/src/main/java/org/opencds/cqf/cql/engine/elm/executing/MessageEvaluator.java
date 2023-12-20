package org.opencds.cqf.cql.engine.elm.executing;

import java.util.Optional;
import java.util.function.Supplier;
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.Message;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageEvaluator {

    static final Logger logger = LoggerFactory.getLogger(MessageEvaluator.class);

    public static Object message(
            State state,
            SourceLocator sourceLocator,
            Object source,
            Boolean condition,
            String code,
            String severity,
            String message) {
        if (severity == null) {
            severity = "message";
        }

        if (condition != null && condition) {
            StringBuilder messageBuilder = new StringBuilder();
            if (code != null) {
                messageBuilder.append(code).append(": ");
            }
            switch (severity.toLowerCase()) {
                case "message": {
                    String finalMessage = messageBuilder.append(message).toString();
                    state.logDebugMessage(sourceLocator, finalMessage);
                    logger.info(finalMessage);
                    break;
                }
                case "warning": {
                    String finalMessage = messageBuilder.append(message).toString();
                    state.logDebugWarning(sourceLocator, finalMessage);
                    logger.warn(finalMessage);
                    break;
                }
                case "trace": {
                    String finalMessage = messageBuilder
                            .append(message)
                            .append(String.format("%n%s", stripPHI(state, source)))
                            .toString();
                    state.logDebugTrace(sourceLocator, finalMessage);
                    logger.debug(finalMessage);
                    break;
                }
                case "error": {
                    String finalMessage = messageBuilder
                            .append(message)
                            .append(String.format("%n%s", stripPHI(state, source)))
                            .toString();
                    // NOTE: debug logging happens through exception-handling
                    logger.error(finalMessage);
                    throw new CqlException(finalMessage);
                }
            }
        }
        return source;
    }

    private static String stripPHI(State state, Object source) {
        if (source == null) {
            return null;
        }

        Optional<DataProvider> dataProvider = Optional.ofNullable(state.getEnvironment()
                .resolveDataProvider(source.getClass().getPackage().getName(), false));

        return dataProvider
                .map(DataProvider::phiObfuscationSupplier)
                .map(Supplier::get)
                .map(obfuscator -> obfuscator.obfuscate(source))
                .orElse("");
    }

    public static Object internalEvaluate(Message elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        Object source = visitor.visitExpression(elm.getSource(), state);
        Boolean condition = (Boolean) visitor.visitExpression(elm.getCondition(), state);
        String code = (String) visitor.visitExpression(elm.getCode(), state);
        String severity = (String) visitor.visitExpression(elm.getSeverity(), state);
        String msg = (String) visitor.visitExpression(elm.getMessage(), state);

        return message(
                state, SourceLocator.fromNode(elm, state.getCurrentLibrary()), source, condition, code, severity, msg);
    }
}
