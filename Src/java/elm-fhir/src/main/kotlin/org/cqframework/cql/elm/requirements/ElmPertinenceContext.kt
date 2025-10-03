package org.cqframework.cql.elm.requirements

import org.hl7.cql_annotations.r1.Annotation
import org.hl7.elm.r1.ExpressionDef

class ElmPertinenceContext(val expressionDef: ExpressionDef) {

    var pertinenceValue: String? = null
        private set

    fun checkPertinenceTag(): Boolean {
        var pertinenceFound = false
        var a: Annotation? = null
        for (o in expressionDef.annotation) {
            if (o is Annotation) {
                a = o
            }
            for (i in 0..<a!!.t.size) {
                val t = a.t[i]
                if (t.name != null && t.name.equals("pertinence")) {
                    pertinenceFound = true
                    pertinenceValue = t.value
                }
            }
        }
        return pertinenceFound
    }
}
