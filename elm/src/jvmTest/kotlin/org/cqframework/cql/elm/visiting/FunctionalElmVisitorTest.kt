package org.cqframework.cql.elm.visiting

import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.Library
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class FunctionalElmVisitorTest {
    @Test
    fun countTest() {
        // set up visitor that counts all visited elements
        val trackableCounter = FunctionalElmVisitor<Int, Int>({ _, _ -> 1 }, { a, b -> a + b })

        val library = Library()
        library.statements = Library.Statements()
        library.statements!!.def.add(ExpressionDef())
        library.statements!!.def.add(ExpressionDef())
        library.statements!!.def.add(ExpressionDef())

        var result: Int = trackableCounter.visitLibrary(library, 1)
        Assertions.assertEquals(4, result) // ELM elements

        // set up visitor that counts all visited ELM elements
        val elmCounter = FunctionalElmVisitor<Int, Int>({ _, _ -> 1 }, { a, b -> a + b })

        result = elmCounter.visitLibrary(library, 1)
        Assertions.assertEquals(4, result)

        val maxThreeCounter =
            FunctionalElmVisitor<Int, Int>(
                { _, _ -> 1 },
                { aggregate, nextResult ->
                    if (aggregate >= 3) aggregate else aggregate + nextResult
                },
            )

        result = maxThreeCounter.visitLibrary(library, 1)
        Assertions.assertEquals(3, result)
    }

    @Test
    fun constructVisitorTest() {
        // set up visitor that counts all visited elements
        val trackableCounter =
            FunctionalElmVisitor.from({ _, _: Int -> 1 }, { a: Int, b: Int -> a + b })

        val library = Library()
        library.statements = Library.Statements()
        library.statements!!.def.add(ExpressionDef())
        library.statements!!.def.add(ExpressionDef())
        library.statements!!.def.add(ExpressionDef())

        val result = trackableCounter.visitLibrary(library, 1)
        Assertions.assertEquals(4, result) // ELM elements

        // This visitor returns the context object that's passed in
        val contextReturner = FunctionalElmVisitor.from<Any, Any> { _, c -> c }
        val context = Any()
        Assertions.assertEquals(context, contextReturner.visitLibrary(library, context))
    }
}
