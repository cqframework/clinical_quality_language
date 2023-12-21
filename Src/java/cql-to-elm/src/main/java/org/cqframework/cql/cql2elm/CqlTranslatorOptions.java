package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.EnumSet;
import java.util.Set;

public class CqlTranslatorOptions {

    public enum Format {
        XML,
        JSON,
        COFFEE
    }

    @JsonUnwrapped
    private CqlCompilerOptions cqlCompilerOptions;

    private Set<Format> formats;

    public static CqlTranslatorOptions defaultOptions() {
        return new CqlTranslatorOptions()
                .withCqlCompilerOptions(CqlCompilerOptions.defaultOptions())
                .withFormats(EnumSet.of(Format.XML));
    }

    public CqlCompilerOptions getCqlCompilerOptions() {
        return this.cqlCompilerOptions;
    }

    public void setCqlCompilerOptions(CqlCompilerOptions cqlCompilerOptions) {
        this.cqlCompilerOptions = cqlCompilerOptions;
    }

    public CqlTranslatorOptions withCqlCompilerOptions(CqlCompilerOptions cqlCompilerOptions) {
        this.setCqlCompilerOptions(cqlCompilerOptions);
        return this;
    }

    public Set<Format> getFormats() {
        return this.formats;
    }

    public void setFormats(Set<Format> formats) {
        this.formats = formats;
    }

    public CqlTranslatorOptions withFormats(Set<Format> formats) {
        this.setFormats(formats);
        return this;
    }
}
