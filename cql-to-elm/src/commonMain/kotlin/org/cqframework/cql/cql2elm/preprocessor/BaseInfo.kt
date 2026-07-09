package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree

open class BaseInfo(open val definition: ParseTree?) {
    var header: String? = null
    var headerInterval: Interval? = null
}
