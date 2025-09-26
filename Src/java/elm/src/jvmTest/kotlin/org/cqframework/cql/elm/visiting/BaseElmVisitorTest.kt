package org.cqframework.cql.elm.visiting

import org.hl7.elm.r1.ByDirection
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Sort
import org.hl7.elm.r1.SortByItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class BaseElmVisitorTest {
    @Test
    fun sortByVisited() {
        // set up visitor that returns true if it visits a SortByItem
        val sortByFinder =
            object : BaseElmVisitor<Boolean, Void?>() {
                public override fun defaultResult(elm: Element, context: Void?): Boolean {
                    return elm is SortByItem
                }

                public override fun aggregateResult(
                    aggregate: Boolean,
                    nextResult: Boolean
                ): Boolean {
                    return aggregate || nextResult
                }
            }

        val sort = Sort()
        Assertions.assertFalse(sortByFinder.visitSort(sort, null))

        sort.by.add(ByDirection())
        Assertions.assertTrue(sortByFinder.visitSort(sort, null))
    }
}
