package org.cqframework.cql.cql2elm.elm;

import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.elm.r1.Element;

public enum ElmEdit implements IElmEdit {
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
                    var x = element.getAnnotation().get(i);
                    if (x instanceof Annotation) {
                        var a = (Annotation) x;
                        // TODO: Remove narrative but _not_ tags
                        // Tags are necessary for `allowFluent` compiler resolution
                        // to work correctly
                        a.setS(null);
                        if (a.getT().isEmpty()) {
                            element.getAnnotation().remove(i);
                            i--;
                        }
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
}
