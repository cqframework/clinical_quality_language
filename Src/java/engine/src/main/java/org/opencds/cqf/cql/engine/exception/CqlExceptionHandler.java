package org.opencds.cqf.cql.engine.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class CqlExceptionHandler implements Thread.UncaughtExceptionHandler
{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Throwable rootCause = ExceptionUtils.getRootCause(e);
        rootCause.printStackTrace(System.err);
        throw new CqlException("Unexpected exception caught during execution: " + e.getClass().getName() + "\nWith trace:\n" + ExceptionUtils.getStackTrace(e));
    }
}
