package org.cqframework.cql.cql2elm.elm;

import org.hl7.elm.r1.Element;

public enum ElmEdit {

    REMOVE_LOCATOR {
        @Override
        public void edit(Element element) {
            element.setLocator(null);
        }
    },
    REMOVE_ANNOTATION {
        @Override
        public void edit(Element element) {
            element.setLocalId(null);
            if (element.getAnnotation() != null) {
                element.getAnnotation().clear();
            }
        }
    },
    REMOVE_RESULT_TYPE {
        @Override
        public void edit(Element element) {
            element.setResultType(null);
        }
    };

    public abstract void edit(Element element);
}
