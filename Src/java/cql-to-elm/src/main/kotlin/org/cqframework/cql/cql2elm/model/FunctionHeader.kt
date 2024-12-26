package org.cqframework.cql.cql2elm.model

import java.util.*
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier

/** POJO for the result of a pre compile operation (AKA: partial compile of function headers) */
data class FunctionHeader(val functionDef: FunctionDef, val resultType: TypeSpecifier?) {
    constructor(functionDef: FunctionDef) : this(functionDef, null)

    var isCompiled: Boolean = false

    val mangledName: String by lazy {
        val sb = StringBuilder()
        sb.append(functionDef.name)
        sb.append("_")
        for (od in functionDef.operand) {
            sb.append(
                when {
                    od.operandTypeSpecifier is NamedTypeSpecifier ->
                        (od.operandTypeSpecifier as NamedTypeSpecifier).name
                    else -> od.operandTypeSpecifier.toString()
                }
            )
        }
        sb.append("_")
        sb.toString()
    }

    override fun toString(): String {
        return StringJoiner(", ", FunctionHeader::class.java.simpleName + "[", "]")
            .add("functionDef=$functionDef")
            .add("resultType=$resultType")
            .toString()
    }
}
