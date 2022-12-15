package org.opencds.cqf.cql.engine.elm.execution.obfuscate;



public class RedactingPHIObfuscator implements PHIObfuscator {

    public static final String REDACTED_MESSAGE = "<redacted>";

    @Override
    public String obfuscate(Object source) {
        return REDACTED_MESSAGE;
    }

}
