package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.ValueSet
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo
import org.opencds.cqf.cql.engine.util.javaClassName

/*
in(code String, valueset ValueSetRef) Boolean
in(code Code, valueset ValueSetRef) Boolean
in(concept Concept, valueset ValueSetRef) Boolean

The in (Valueset) operators determine whether or not a given code is in a particular valueset.
For the String overload, if the given valueset contains a code with an equivalent code element, the result is true.
For the Code overload, if the given valueset contains an equivalent code, the result is true.
For the Concept overload, if the given valueset contains a code equivalent to any code in the given concept, the result is true.
If the code argument is null, the result is null.
*/
object InValueSetEvaluator {
    @JvmStatic
    fun inValueSet(code: Any?, valueset: Any?, state: State?): Any? {
        if (code == null) {
            return false
        }
        if (valueset == null) {
            return null
        }

        if (valueset is ValueSet) {
            val vsi = ValueSetInfo.fromValueSet(valueset)
            val provider = state!!.environment.terminologyProvider

            // perform operation
            if (code is String) {
                if (provider!!.`in`(Code().withCode(code), vsi) == true) {
                    return true
                }
                return false
            } else if (code is Code) {
                if (provider!!.`in`(code, vsi) == true) {
                    return true
                }
                return false
            } else if (code is Concept) {
                for (codes in code.codes!!) {
                    if (codes == null) return null
                    if (provider!!.`in`(codes, vsi) == true) return true
                }
                return false
            }
        }

        throw InvalidOperatorArgument(
            "In(String, ValueSetRef), In(Code, ValueSetRef) or In(Concept, ValueSetRef)",
            "In(${code.javaClassName}, ${valueset.javaClassName})",
        )
    }
}
