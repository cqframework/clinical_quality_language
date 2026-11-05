package org.opencds.cqf.cql.engine.execution.external

import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

class MyMath2 {
    companion object {
        @JvmStatic
        fun MyTimes(x: Integer, y: Integer): Integer {
            return (x.value * y.value).toCqlInteger()
        }

        @JvmStatic
        fun MyDividedBy(x: Integer, y: Integer): Integer {
            return (x.value / y.value).toCqlInteger()
        }
    }
}
