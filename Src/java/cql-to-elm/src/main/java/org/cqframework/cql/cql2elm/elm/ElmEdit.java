package org.cqframework.cql.cql2elm.elm;

import org.hl7.cql_annotations.r1.Annotation;
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
                for (int i = 0; i < element.getAnnotation().size(); i++) {
                    var a = element.getAnnotation().get(i);
                    if (a instanceof Annotation) {
                        element.getAnnotation().remove(i);
                        i--;
                    }
                }
            }
        }
    },
    REMOVE_RESULT_TYPE {
        @Override
        public void edit(Element element) {
            element.setResultTypeName(null);
            element.setResultTypeSpecifier(null);
        }
    };

    public abstract void edit(Element element);
}
