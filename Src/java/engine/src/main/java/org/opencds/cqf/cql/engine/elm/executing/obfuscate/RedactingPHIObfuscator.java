package org.opencds.cqf.cql.engine.elm.executing.obfuscate;


public class RedactingPHIObfuscator implements PHIObfuscator {

    public static final String REDACTED_MESSAGE = "<redacted>";

    @Override
    public String obfuscate(Object source) {
        return REDACTED_MESSAGE;
    }

}
