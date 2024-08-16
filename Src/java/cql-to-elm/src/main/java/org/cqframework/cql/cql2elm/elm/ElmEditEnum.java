package org.cqframework.cql.cql2elm.elm;

import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.elm.r1.ChoiceTypeSpecifier;
import org.hl7.elm.r1.Element;

public enum ElmEditEnum implements ElmEdit {
    REMOVE_LOCATOR {
        public void edit(Element element) {
            element.setLocator(null);
        }
    },
    REMOVE_ANNOTATION {
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
        public void edit(Element element) {
            element.setResultTypeName(null);
            element.setResultTypeSpecifier(null);
        }
    },
    REMOVE_CHOICE_TYPE_SPECIFIER_TYPE_IF_EMPTY {
        // The ChoiceTypeSpecifier ELM node has a deprecated `type` element which, if not null, clashes with the
        // `"type" : "ChoiceTypeSpecifier"` field in the JSON serialization of the node. This does not happen in the XML
        // serialization which nests <type> tags inside <ChoiceTypeSpecifier>.
        // Because the `type` element is deprecated, it is not normally populated by the compiler anymore and
        // stays null in the ChoiceTypeSpecifier instance. It is however set to an empty list if you just call
        // ChoiceTypeSpecifier.getType() (which we do during the ELM optimization stage in the compiler), so
        // this edit is needed to "protect" the downstream JSON serialization if it can be done without data loss.

        public void edit(Element element) {
            if (element instanceof ChoiceTypeSpecifier) {
                var choice = (ChoiceTypeSpecifier) element;
                if (choice.getType().isEmpty()) {
                    choice.setType(null);
                }
            }
        }
    };
}
