package org.opencds.cqf.cql.engine.execution.external

class MyMath2 {
    companion object {
        @JvmStatic
        fun MyTimes(x: Int, y: Int): Int {
            return x * y
        }

        @JvmStatic
        fun MyDividedBy(x: Int, y: Int): Int {
            return x / y
        }
    }
}
