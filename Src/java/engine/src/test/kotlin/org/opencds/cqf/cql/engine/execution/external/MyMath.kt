package org.opencds.cqf.cql.engine.execution.external

class MyMath {
    companion object {
        @JvmStatic
        fun MyPlus(x: Int, y: Int): Int {
            return x + y
        }

        @JvmStatic
        fun MyMinus(x: Int, y: Int): Int {
            return x - y
        }
    }
}
