package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlString

/*

ToString(argument Boolean) String
ToString(argument Integer) String
ToString(argument Long) String
ToString(argument Decimal) String
ToString(argument Quantity) String
ToString(argument Ratio) String
ToString(argument Date) String
ToString(argument DateTime) String
ToString(argument Time) String

The ToString operator converts the value of its argument to a String value.
The operator uses the following string representations for each type:
Boolean	true|false
Integer	    (-)?#0
Long	    (-)?#0
Decimal	    (-)?#0.0#
Quantity    (-)?#0.0# '<unit>'
Ratio       <quantity>:<quantity>
Date        YYYY-MM-DD
DateTime	YYYY-MM-DDThh:mm:ss.fff(+|-)hh:mm
Time	    Thh:mm:ss.fff(+|-)hh:mm
If the argument is null, the result is null.

*/
object ToStringEvaluator {
    @JvmStatic
    fun toString(operand: Value?): String? {
        return when (operand) {
            null -> null
            is Boolean -> operand.value.toString().toCqlString()
            is Integer -> operand.value.toString().toCqlString()
            is Long -> operand.value.toString().toCqlString()
            is Decimal -> operand.value.toPlainString().toCqlString()
            is Quantity,
            is Ratio -> operand.toString().toCqlString()
            is Date,
            is DateTime,
            is Time -> operand.toStringInner().toCqlString()
            is String -> operand
            else -> operand.toString().toCqlString()
        }
    }
}
