package org.cqframework.cql.runtime;

/**
 * Created by Bryn on 4/15/2016.
 */
public class Code {

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

    @Override
    public boolean equals(Object other) {
        if (other instanceof Code) {
            Code otherCode = (Code)other;
            return this.getCode().equals(otherCode.getCode())
                    && ((this.getSystem() == null && otherCode.getSystem() == null)
                        || (this.getSystem() != null && this.getSystem().equals(otherCode.getSystem())))
                    && ((this.getVersion() == null && otherCode.getVersion() == null)
                        || (this.getVersion() != null && this.getVersion().equals(otherCode.getVersion())));
        }

        return false;
    }
}
