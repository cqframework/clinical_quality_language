package org.opencds.cqf.cql.engine.execution.external

import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

class MyMath {
    companion object {
        @JvmStatic
        fun MyPlus(x: Integer, y: Integer): Integer {
            return (x.value + y.value).toCqlInteger()
        }

        @JvmStatic
        fun MyMinus(x: Integer, y: Integer): Integer {
            return (x.value - y.value).toCqlInteger()
        }
    }
}
