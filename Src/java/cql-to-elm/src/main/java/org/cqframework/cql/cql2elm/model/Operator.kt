package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType
import org.hl7.elm.r1.AccessModifier
import org.hl7.elm.r1.FunctionDef

@Suppress("LongParameterList")
open class Operator(
    val name: String,
    var signature: Signature,
    var resultType: DataType?,
    var functionDef: FunctionDef?,
    var accessLevel: AccessModifier = AccessModifier.PUBLIC,
    var fluent: Boolean = false,
    var external: Boolean = false
) {
    constructor(
        name: String,
        signature: Signature,
        resultType: DataType?
    ) : this(name, signature, resultType, null)

    constructor(
        functionDef: FunctionDef
    ) : this(
        functionDef.name!!,
        Signature(functionDef.operand!!.map { it!!.resultType!! }),
        functionDef.resultType,
        functionDef,
        functionDef.accessLevel!!,
        functionDef.isFluent() ?: false,
        functionDef.isExternal() ?: false
    )

    init {
        require(name.isNotEmpty()) { "name is null or empty" }
    }

    var libraryName: String? = null
}
