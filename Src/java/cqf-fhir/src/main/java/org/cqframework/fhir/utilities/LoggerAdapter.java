package org.cqframework.fhir.utilities;

import org.hl7.fhir.r5.context.ILoggingService;
import org.slf4j.Logger;

public class LoggerAdapter implements ILoggingService {
    private Logger innerLogger;

    public LoggerAdapter(Logger innerLogger) {
        this.innerLogger = innerLogger;
    }

    @Override
    public void logMessage(String s) {
        innerLogger.info(s);
    }

    @Override
    public void logDebugMessage(LogCategory logCategory, String s) {
        innerLogger.debug("{}: {}", logCategory, s);
    }

    @Override
    public boolean isDebugLogging() {
        return this.innerLogger.isDebugEnabled();
    }
}
