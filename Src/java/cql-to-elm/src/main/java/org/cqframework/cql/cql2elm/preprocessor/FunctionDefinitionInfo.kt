package org.cqframework.cql.cql2elm.preprocessor

import java.util.*
import org.cqframework.cql.cql2elm.model.FunctionHeader
import org.cqframework.cql.gen.cqlParser

class FunctionDefinitionInfo : BaseInfo() {
    var name: String? = null
    @JvmField var context: String? = null
    var preCompileOutput: FunctionHeader? = null
    override var definition: cqlParser.FunctionDefinitionContext? = null

    fun withName(value: String?): FunctionDefinitionInfo {
        name = value
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as FunctionDefinitionInfo
        return name == that.name &&
            context == that.context &&
            preCompileOutput == that.preCompileOutput
    }

    override fun hashCode(): Int {
        return Objects.hash(name, context, preCompileOutput)
    }

    override fun toString(): String {
        return StringJoiner(", ", FunctionDefinitionInfo::class.java.simpleName + "[", "]")
            .add("name='$name'")
            .add("context='$context'")
            .add("preCompileOutput=" + preCompileOutput)
            .toString()
    }
}
