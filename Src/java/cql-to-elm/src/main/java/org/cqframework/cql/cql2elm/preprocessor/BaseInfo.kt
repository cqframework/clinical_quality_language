package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ParseTree

open class BaseInfo {
    var header: String? = null
    var headerInterval: Interval? = null
    open val definition: ParseTree? = null
}