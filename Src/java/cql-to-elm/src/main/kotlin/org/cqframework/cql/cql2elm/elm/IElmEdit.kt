package org.cqframework.cql.cql2elm.elm

import org.hl7.elm.r1.Element

fun interface IElmEdit {
    fun edit(element: Element)
}
