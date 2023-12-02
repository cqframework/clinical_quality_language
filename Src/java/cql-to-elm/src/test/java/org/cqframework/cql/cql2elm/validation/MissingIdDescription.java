package org.cqframework.cql.cql2elm.validation;

import org.hl7.elm.r1.Element;

public class MissingIdDescription {
    private final Element element;

    public MissingIdDescription(Element element) {
        this.element = element;
    }

    public Element element() {
        return element;
    }

    public String description() {
        var description = String.format("%s missing localId", element.getClass().getSimpleName());
        if (element.getTrackbacks() != null && !element.getTrackbacks().isEmpty()) {
            var tb = element.getTrackbacks().get(0);
            description = description + String.format(" at %s:[%s:%s-%s:%s]", tb.getLibrary().getId(), tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
        }

        return description;
    }
}