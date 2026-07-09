package org.cqframework.cql.cql2elm.elm

import org.hl7.cql_annotations.r1.Annotation
import org.hl7.cql_annotations.r1.CqlToElmBase
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
            removeAnnotations(element.annotation)
        }

        private fun removeAnnotations(annotations: MutableList<CqlToElmBase>) {
            for (i in annotations.indices.reversed()) {
                val x = annotations[i]
                if (x is Annotation) {
                    x.s = null
                    // Remove narrative but _not_ tags.
                    // Tags are necessary for `allowFluent` compiler resolution
                    // to work correctly
                    if (x.t.isEmpty()) {
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
}
