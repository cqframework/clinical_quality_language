package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.CodeSystem
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo

/*
in(code String, codesystem CodeSystemRef) Boolean
in(code Code, codesystem CodeSystemRef) Boolean
in(concept Concept, codesystem CodeSystemRef) Boolean

The in (Codesystem) operators determine whether or not a given code is in a particular codesystem.
For the String overload, if the given code system contains a code with an equivalent code element, the result is true.
For the Code overload, if the given code system contains an equivalent code, the result is true.
For the Concept overload, if the given code system contains a code equivalent to any code in the given concept, the result is true.
If the code argument is null, the result is null.
*/
object InCodeSystemEvaluator {
    @JvmStatic
    fun inCodeSystem(code: Value?, codeSystem: Value?, state: State?): Boolean? {
        if (code == null || codeSystem == null) {
            return null
        }

        if (codeSystem is CodeSystem) {
            val csi = CodeSystemInfo.fromCodeSystem(codeSystem)

            val provider = state!!.environment.terminologyProvider

            if (code is String) {
                return (provider!!.lookup(Code().withCode(code.value), csi) != null).toCqlBoolean()
            } else if (code is Code) {
                return (provider!!.lookup(code, csi) != null).toCqlBoolean()
            } else if (code is Concept) {
                for (codes in code.codes!!) {
                    if (provider!!.lookup(codes!!, csi) != null) {
                        return Boolean.TRUE
                    }
                }
                return Boolean.FALSE
            }
        }

        throw InvalidOperatorArgument(
            "In(String, CodeSystemRef), In(Code, CodeSystemRef) or In(Concept, CodeSystemRef)",
            "In(${code.typeAsString}, ${codeSystem.typeAsString})",
        )
    }
}
