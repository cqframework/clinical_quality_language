package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass
import kotlin.reflect.cast
import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.TypeSpecifier
import org.opencds.cqf.cql.engine.exception.InvalidConversion
import org.opencds.cqf.cql.engine.execution.State

/*
convert to<T>(argument Any) T

The convert operator converts a value to a specific type.
The result of the operator is the value of the argument converted to the target type, if possible.
  If there is no valid conversion from the actual value to the target type, the result is null.

The following table lists the conversions supported in CQL:
From\To	  Boolean	  Integer	  Decimal	  Quantity  Ratio	  String   Date 	 Datetime	 Time	    Code	Concept  List<Code>
Boolean	    N/A	     Explicit	 Explicit	      -       -      Explicit	 -           -	       -	      -	      -          -
Integer	  Explicit      N/A	     Implicit	  Implicit	  -	     Explicit	 -           -	       -	      -       -          -
Decimal	  Explicit      -	       N/A	      Implicit	  - 	 Explicit 	 -           -	       -	      -       -          -
Quantity	 -	        -	        -	         N/A      -	     Explicit	 -           -	       -	      -       -          -
Ratio   	 -	        -	        -	          -      N/A     Explicit    -           -	       -	      -	      -	         -
String	  Explicit   Explicit	 Explicit	  Explicit Explicit    N/A	   Explicit  Explicit   Explicit      -	      -          -
Date    	 -	        -	        -	          -	      -      Explicit	N/A      Implicit      -          -	      -          -
Datetime	 -	        -	        -	          -	      -      Explicit  Explicit    N/A         -	      -	      -	         -
Time	     -	        -	        -	          -	      -      Explicit	 -           -	      N/A	      -	      -          -
Code	     -	        -	        -	          -	      -         -	     -           -	       -	     N/A   Implicit      -
Concept	     -	        -	        -	          -	      -         -	     -           -	       -	      -	     N/A      Explicit
List<Code>   -	        -	        -	          -	      -         -	     -           -	       -	      -	   Explicit     N/A

For conversions between date/time and string values, ISO-8601 standard format is used:
yyyy-MM-ddThh:mm:ss.fff(Z | +/- hh:mm)
For example, the following are valid string representations for date/time values:
'2014-01-01T14:30:00.0Z'      // January 1st, 2014, 2:30PM UTC
'2014-01-01T14:30:00.0-07:00' // January 1st, 2014, 2:30PM Mountain Standard (GMT-7:00)
'T14:30:00.0Z'                // 2:30PM UTC
'T14:30:00.0-07:00'           // 2:30PM Mountain Standard (GMT-7:00)
For specific semantics for each conversion, refer to the explicit conversion operator documentation.

*/
object ConvertEvaluator {
    private fun resolveType(
        toType: QName?,
        typeSpecifier: TypeSpecifier?,
        state: State?,
    ): KClass<*> {
        if (typeSpecifier != null) {
            return state!!.environment.resolveType(typeSpecifier)!!
        }
        return state!!.environment.resolveType(toType)!!
    }

    private fun convert(operand: Any?, type: KClass<*>): Any? {
        if (operand == null) {
            return null
        }

        try {
            if (type.isInstance(operand)) {
                return type.cast(operand)
            }
        } catch (e: Exception) {
            throw InvalidConversion("Error during conversion: " + e.message)
        }

        throw InvalidConversion(operand, type)
    }

    @JvmStatic
    fun internalEvaluate(
        operand: Any?,
        toType: QName?,
        typeSpecifier: TypeSpecifier?,
        state: State?,
    ): Any? {
        val type = resolveType(toType, typeSpecifier, state)
        return convert(operand, type)
    }
}
