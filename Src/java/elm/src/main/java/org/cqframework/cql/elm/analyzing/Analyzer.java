package org.cqframework.cql.elm.analyzing;

import org.hl7.elm.r1.Element;

public interface Analyzer {
    void analyze(Element element, VisitorContext context);
}
