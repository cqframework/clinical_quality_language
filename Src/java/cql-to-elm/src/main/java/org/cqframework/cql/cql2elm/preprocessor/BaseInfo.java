package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

public class BaseInfo {
    private String header;
    private Interval headerInterval;
    private ParseTree definition;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Interval getHeaderInterval() {
        return headerInterval;
    }

    public void setHeaderInterval(Interval headerInterval) {
        this.headerInterval = headerInterval;
    }

    public ParseTree getDefinition() {
        return definition;
    }

    public void setDefinition(ParseTree definition) {
        this.definition = definition;
    }
}
