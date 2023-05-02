package org.opencds.cqf.cql.engine.elm.visiting.obfuscate;


public class RedactingPHIObfuscator implements PHIObfuscator {

    public static final String REDACTED_MESSAGE = "<redacted>";

    @Override
    public String obfuscate(Object source) {
        return REDACTED_MESSAGE;
    }

}
