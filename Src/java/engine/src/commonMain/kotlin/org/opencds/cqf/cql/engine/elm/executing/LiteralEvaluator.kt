package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.InvalidLiteral
import org.opencds.cqf.cql.engine.execution.State

object LiteralEvaluator {
    @JvmStatic
    fun internalEvaluate(valueT: QName?, value: String?, state: State?): Any? {
        val valueType = state!!.environment.fixupQName(valueT!!)
        when (valueType.getLocalPart()) {
            "Boolean" -> return value.toBoolean()
            "Integer" -> {
                val intValue: Int
                try {
                    intValue = value!!.toInt()
                } catch (e: NumberFormatException) {
                    throw CqlException("Bad format for Integer literal")
                }
                return intValue
            }
            "Long" -> {
                val longValue: Long
                try {
                    longValue = value!!.toLong()
                } catch (e: NumberFormatException) {
                    throw CqlException("Bad format for Long literal")
                }
                return longValue
            }
            "Decimal" -> {
                val bigDecimalValue: BigDecimal?

                try {
                    bigDecimalValue = BigDecimal(value!!)
                } catch (nfe: NumberFormatException) {
                    throw CqlException(nfe.message)
                }
                return bigDecimalValue
            }
            "String" -> return value
            else ->
                throw InvalidLiteral(
                    "Cannot construct literal value for type '${valueType.toString()}'."
                )
        }
    }
}
