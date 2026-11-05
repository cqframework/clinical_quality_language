package org.cqframework.fhir.utilities

import org.hl7.fhir.r5.context.ILoggingService
import org.slf4j.Logger

class LoggerAdapter(private val innerLogger: Logger) : ILoggingService {
    override fun logMessage(s: String?) {
        innerLogger.info(s)
    }

    override fun logDebugMessage(logCategory: ILoggingService.LogCategory?, s: String?) {
        innerLogger.debug("{}: {}", logCategory, s)
    }

    @Deprecated("Deprecated in FHIR core")
    override fun isDebugLogging(): Boolean {
        return this.innerLogger.isDebugEnabled
    }
}
