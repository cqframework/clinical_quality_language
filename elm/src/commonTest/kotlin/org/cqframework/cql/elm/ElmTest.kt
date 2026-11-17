package org.cqframework.cql.elm

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.With

class ElmTest {

    /** Two instances of the same class with same properties should be equal. */
    @Test
    fun equalsTrue() {
        val abs1 = Literal().withValue("abc")
        val abs2 = Literal().withValue("abc")
        assertTrue(abs1.equals(abs2))
    }

    /**
     * An instance of a class and an instance of a subclass should not be equal, even if they have
     * the same properties.
     */
    @Test
    fun equalsFalse() {
        val aliasedQuerySource = AliasedQuerySource().withAlias("alias")
        val with = With().withAlias("alias")
        assertFalse(with.equals(aliasedQuerySource))
        assertFalse(aliasedQuerySource.equals(with))
    }
}
