package org.opencds.cqf.cql.engine.elm.execution.obfuscate;



public class NoOpPHIObfuscator implements PHIObfuscator {

    @Override
    public String obfuscate(Object source) {
        return String.valueOf(source);
    }

}
