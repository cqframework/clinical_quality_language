package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType
import org.hl7.elm.r1.AccessModifier
import org.hl7.elm.r1.FunctionDef

open class Operator(
    functionDef: FunctionDef?,
    name: String?,
    signature: Signature?,
    resultType: DataType?
) {
    constructor(
        name: String?,
        signature: Signature?,
        resultType: DataType?
    ) : this(null, name, signature, resultType)

    var libraryName: String? = null
        set(libraryName) {
            require(!(libraryName == null || libraryName == "")) { "libraryName is null." }

            field = libraryName
        }

    @JvmField var accessLevel: AccessModifier = AccessModifier.PUBLIC

    fun withAccessLevel(accessLevel: AccessModifier): Operator {
        this.accessLevel = accessLevel
        return this
    }

    var fluent: Boolean = false

    fun withFluent(isFluent: Boolean): Operator {
        fluent = isFluent
        return this
    }

    var external: Boolean = false

    fun withExternal(isExternal: Boolean): Operator {
        external = isExternal
        return this
    }

    @JvmField var functionDef: FunctionDef?

    fun withFunctionDef(functionDef: FunctionDef?): Operator {
        this.functionDef = functionDef
        return this
    }

    @JvmField val name: String

    @JvmField val signature: Signature

    @JvmField var resultType: DataType?

    init {
        require(!(name == null || name == "")) { "name is null or empty" }

        requireNotNull(signature) { "signature is null" }

        this.functionDef = functionDef
        this.name = name
        this.signature = signature
        this.resultType = resultType
    }

    companion object {
        @JvmStatic
        fun fromFunctionDef(functionDef: FunctionDef): Operator {
            val operandTypes: MutableList<DataType> = ArrayList()
            for (operand in functionDef.operand) {
                operandTypes.add(operand.resultType)
            }
            return Operator(
                    functionDef,
                    functionDef.name,
                    Signature(operandTypes),
                    functionDef.resultType
                )
                .withAccessLevel(functionDef.accessLevel)
                .withFluent(if (functionDef.isFluent != null) functionDef.isFluent else false)
                .withExternal(if (functionDef.isExternal != null) functionDef.isExternal else false)
        }
    }
}
