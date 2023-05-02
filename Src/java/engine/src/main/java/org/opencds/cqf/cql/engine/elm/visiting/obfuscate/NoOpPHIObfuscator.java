package org.opencds.cqf.cql.engine.elm.visiting.obfuscate;


public class NoOpPHIObfuscator implements PHIObfuscator {

    @Override
    public String obfuscate(Object source) {
        return String.valueOf(source);
    }

}
