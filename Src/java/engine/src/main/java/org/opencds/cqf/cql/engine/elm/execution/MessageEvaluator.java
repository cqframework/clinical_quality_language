package org.opencds.cqf.cql.engine.elm.execution;

import java.util.Optional;
import java.util.function.Supplier;

import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageEvaluator extends org.cqframework.cql.elm.execution.Message {

    static final Logger logger = LoggerFactory.getLogger(MessageEvaluator.class);

    public Object message(Context context, SourceLocator sourceLocator, Object source, Boolean condition, String code, String severity, String message) {
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
                    context.logDebugMessage(sourceLocator, finalMessage);
                    logger.info(finalMessage); break;
                }
                case "warning": {
                    String finalMessage = messageBuilder.append(message).toString();
                    context.logDebugWarning(sourceLocator, finalMessage);
                    logger.warn(finalMessage); break;
                }
                case "trace": {
                    String finalMessage = messageBuilder.append(message).append(String.format("%n%s", stripPHI(context, source))).toString();
                    context.logDebugTrace(sourceLocator, finalMessage);
                    logger.debug(finalMessage); break;
                }
                case "error": {
                    String finalMessage = messageBuilder.append(message).append(String.format("%n%s", stripPHI(context, source))).toString();
                    // NOTE: debug logging happens through exception-handling
                    logger.error(finalMessage);
                    throw new CqlException(finalMessage);
                }
            }
        }
        return source;
    }

    private String stripPHI(Context context, Object source) {
        if (source == null) {
            return null;
        }

        Optional<DataProvider> dataProvider = Optional.ofNullable(context.resolveDataProvider(source.getClass().getPackage().getName(), false));

        return dataProvider.map(DataProvider::phiObfuscationSupplier).map(Supplier::get)
                .map(obfuscator -> obfuscator.obfuscate(source))
                .orElse("");
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        Boolean condition = (Boolean) getCondition().evaluate(context);
        String code = (String) getCode().evaluate(context);
        String severity = (String) getSeverity().evaluate(context);
        String message = (String) getMessage().evaluate(context);

        return message(context, SourceLocator.fromNode(this, context.getCurrentLibrary()),
                source, condition, code, severity, message
        );
    }
}
