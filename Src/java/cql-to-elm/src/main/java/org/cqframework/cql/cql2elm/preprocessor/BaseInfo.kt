package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.ParseTree

open class BaseInfo {
    var header: String? = null
    var headerInterval: Interval? = null
    open val definition: ParseTree? = null
}
