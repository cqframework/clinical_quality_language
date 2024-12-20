package org.cqframework.cql.cql2elm.elm

import org.hl7.cql_annotations.r1.Annotation
import org.hl7.cql_annotations.r1.CqlToElmBase
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Element

enum class ElmEdit : IElmEdit {
    REMOVE_LOCATOR {
        override fun edit(element: Element) {
            element.locator = null
        }
    },
    REMOVE_ANNOTATION {
        override fun edit(element: Element) {
            element.localId = null
            element.annotation?.let { removeAnnotations(it as MutableList<CqlToElmBase>) }
        }

        private fun removeAnnotations(annotations: MutableList<CqlToElmBase>) {
            for (i in annotations.indices.reversed()) {
                val x = annotations[i]
                if (x is Annotation) {
                    x.s = null
                    // Remove narrative but _not_ tags
                    // Tags are necessary for `allowFluent` compiler resolution
                    // to work correctly
                    if (x.t!!.isEmpty()) {
                        annotations.removeAt(i)
                    }
                }
            }
        }
    },
    REMOVE_RESULT_TYPE {
        override fun edit(element: Element) {
            element.resultTypeName = null
            element.resultTypeSpecifier = null
        }
    },
    REMOVE_CHOICE_TYPE_SPECIFIER_TYPE_IF_EMPTY {
        // The ChoiceTypeSpecifier ELM node has a deprecated `type` element which, if not null,
        // clashes with the
        // `"type" : "ChoiceTypeSpecifier"` field in the JSON serialization of the node. This does
        // not happen in the XML
        // serialization which nests <type> tags inside <ChoiceTypeSpecifier>.
        // Because the `type` element is deprecated, it is not normally populated by the compiler
        // anymore and
        // stays null in the ChoiceTypeSpecifier instance. It is however set to an empty list if you
        // just call
        // ChoiceTypeSpecifier.getType() (which we do during the ELM optimization stage in the
        // compiler), so
        // this edit is needed to "protect" the downstream JSON serialization if it can be done
        // without data loss.
        override fun edit(element: Element) {
            if (element is ChoiceTypeSpecifier && element.type!!.isEmpty()) {
                element.type = null
            }
        }
    }
}
