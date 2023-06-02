package org.opencds.cqf.cql.engine.runtime;

import org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;

public class Code implements CqlType {

    private String code;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Code withCode(String code) {
        setCode(code);
        return this;
    }

    private String display;
    public String getDisplay() {
        return display;
    }
    public void setDisplay(String display) {
        this.display = display;
    }
    public Code withDisplay(String display) {
        setDisplay(display);
        return this;
    }

    private String system;
    public String getSystem() {
        return system;
    }
    public void setSystem(String system) {
        this.system = system;
    }
    public Code withSystem(String system) {
        setSystem(system);
        return this;
    }

    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public Code withVersion(String version) {
        setVersion(version);
        return this;
    }

    public Boolean equivalent(Object other) {
        return EquivalentEvaluator.equivalent(this.getCode(), ((Code) other).getCode())
                && EquivalentEvaluator.equivalent(this.getSystem(), ((Code) other).getSystem());
    }

    public Boolean equal(Object other) {
        Boolean codeIsEqual = EqualEvaluator.equal(this.getCode(), ((Code) other).getCode());
        Boolean systemIsEqual = EqualEvaluator.equal(this.getSystem(), ((Code) other).getSystem());
        Boolean versionIsEqual = EqualEvaluator.equal(this.getVersion(), ((Code) other).getVersion());
        Boolean displayIsEqual = EqualEvaluator.equal(this.getDisplay(), ((Code) other).getDisplay());
        if (codeIsEqual == null && this.code == null && ((Code) other).getCode() == null)
        {
            codeIsEqual = true;
        }
        if (systemIsEqual == null && this.system == null && ((Code) other).getSystem() == null)
        {
            systemIsEqual = true;
        }
        if (versionIsEqual == null && this.version == null && ((Code) other).getVersion() == null)
        {
            versionIsEqual = true;
        }
        if (displayIsEqual == null && this.display == null && ((Code) other).getDisplay() == null)
        {
            displayIsEqual = true;
        }
        return (codeIsEqual == null || systemIsEqual == null || versionIsEqual == null || displayIsEqual == null)
                ? null : codeIsEqual && systemIsEqual && versionIsEqual && displayIsEqual;
    }

    @Override
    public String toString() {
        return String.format(
                "Code { code: %s, system: %s, version: %s, display: %s }",
                getCode(), getSystem(), getVersion(), getDisplay()
        );
    }

}
