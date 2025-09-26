package org.cqframework.cql.elm

import java.util.Locale
import kotlin.test.assertNotNull
import org.hl7.elm.r1.Element
import org.junit.jupiter.api.Test

internal class IdObjectFactoryTest {
    @Test
    fun ensureAllElementsHaveLocalId() {
        val factory = IdObjectFactory()
        val methods =
            IdObjectFactory::class.java.getMethods().filter {
                Element::class.java.isAssignableFrom(it.returnType)
            }
        methods.forEach { x ->
            val e = x.invoke(factory) as Element
            assertNotNull(
                e.localId,
                "%s returned null localId".format(Locale.US, e.javaClass.simpleName)
            )
        }
    }
}
