package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType
import org.hl7.cql.model.SimpleType
import org.hl7.elm_modelinfo.r1.ModelInfo

class SystemModel(modelInfo: ModelInfo) : Model(modelInfo, null) {
    val any: DataType
        get() = this.resolveTypeName("Any")!!

    val boolean: DataType
        get() = this.resolveTypeName("Boolean")!!

    val integer: DataType
        get() = this.resolveTypeName("Integer")!!

    val long: DataType
        get() = this.resolveTypeName("Long")!!

    val decimal: DataType
        get() = this.resolveTypeName("Decimal")!!

    val string: DataType
        get() = this.resolveTypeName("String")!!

    val dateTime: DataType
        get() = this.resolveTypeName("DateTime")!!

    val date: DataType
        get() = this.resolveTypeName("Date")!!

    val time: DataType
        get() = this.resolveTypeName("Time")!!

    val quantity: DataType
        get() = this.resolveTypeName("Quantity")!!

    val ratio: DataType
        get() = this.resolveTypeName("Ratio")!!

    val code: DataType
        get() = this.resolveTypeName("Code")!!

    val concept: DataType
        get() = this.resolveTypeName("Concept")!!

    val vocabulary: DataType
        get() = this.resolveTypeName("Vocabulary")!!

    val codeSystem: DataType
        get() = this.resolveTypeName("CodeSystem")!!

    val valueSet: DataType
        get() = this.resolveTypeName("ValueSet")!!

    val void: DataType
        get() = SimpleType("Void")
}
