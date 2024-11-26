package org.cqframework.cql.cql2elm.preprocessor

import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.ParseTree

open class BaseInfo(open val definition: ParseTree?) {
    var header: String? = null
    var headerInterval: Interval? = null
}
