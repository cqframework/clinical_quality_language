package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.exception.InvalidLiteral
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong
import org.opencds.cqf.cql.engine.runtime.toCqlString

object LiteralEvaluator {
    @JvmStatic
    fun internalEvaluate(valueT: QName?, value: kotlin.String?, state: State?): Value? {
        val valueType = state!!.environment.fixupQName(valueT!!)
        when (valueType.getLocalPart()) {
            "Boolean" -> return value.toBoolean().toCqlBoolean()
            "Integer" -> {
                val intValue: Int
                try {
                    intValue = value!!.toInt()
                } catch (e: NumberFormatException) {
                    throw CqlException("Bad format for Integer literal")
                }
                return intValue.toCqlInteger()
            }
            "Long" -> {
                val longValue: Long
                try {
                    longValue = value!!.toLong()
                } catch (e: NumberFormatException) {
                    throw CqlException("Bad format for Long literal")
                }
                return longValue.toCqlLong()
            }
            "Decimal" -> {
                val bigDecimalValue: BigDecimal?

                try {
                    bigDecimalValue = BigDecimal(value!!)
                } catch (nfe: NumberFormatException) {
                    throw CqlException(nfe.message)
                }
                return bigDecimalValue.toCqlDecimal()
            }
            "String" -> return value?.toCqlString()
            else ->
                throw InvalidLiteral(
                    "Cannot construct literal value for type '${valueType.toString()}'."
                )
        }
    }
}
