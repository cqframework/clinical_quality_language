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
Long	    (-)?#0L
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
        if (operand == null) {
            return null
        }

        if (operand is String) {
            return operand
        }

        if (operand is Integer) {
            return operand.value.toString().toCqlString()
        } else if (operand is Long) {
            return operand.value.toString().toCqlString()
        } else if (operand is Decimal) {
            return operand.value.toString().toCqlString()
        } else if (operand is Quantity) {
            return operand.toString().toCqlString()
        } else if (operand is Ratio) {
            return operand.toString().toCqlString()
        } else if (operand is Boolean) {
            return operand.value.toString().toCqlString()
        } else if (operand is Date) {
            return operand.toString().toCqlString()
        } else if (operand is DateTime) {
            return operand.toString().toCqlString()
        } else if (operand is Time) {
            return operand.toString().toCqlString()
        } else {
            return operand.toString().toCqlString()
        }
    }
}
