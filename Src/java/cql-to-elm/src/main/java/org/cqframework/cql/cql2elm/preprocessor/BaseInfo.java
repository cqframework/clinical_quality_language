package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.kotlinruntime.misc.Interval;
import org.antlr.v4.kotlinruntime.tree.ParseTree;

@SuppressWarnings("checkstyle:abstractclassname")
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
