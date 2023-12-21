package org.opencds.cqf.cql.engine.fhir.retrieve;

import org.opencds.cqf.cql.engine.runtime.Code;

public class CodeFilter {
    public CodeFilter(String codePath, Iterable<Code> codes, String valueSet) {
        this.codePath = codePath;
        this.codes = codes;
        this.valueSet = valueSet;
    }

    private String codePath;

    public String getCodePath() {
        return codePath;
    }

    private Iterable<Code> codes;

    public Iterable<Code> getCodes() {
        return codes;
    }

    private String valueSet;

    public String getValueSet() {
        return valueSet;
    }
}
