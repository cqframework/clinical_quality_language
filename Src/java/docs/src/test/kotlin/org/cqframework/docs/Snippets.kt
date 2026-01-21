package org.cqframework.docs

import kotlin.test.Test
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager

/**
 * Code snippets for documentation. These are compiled as part of the build to ensure they stay
 * fresh.
 */
class Snippets {

    @Test
    fun basicTranslation() {
        // BEGIN:basic-translation
        val modelManager = ModelManager()
        val libraryManager = LibraryManager(modelManager)
        val translator =
            CqlTranslator.fromText(
                """
                library Example version '1.0'
                define X: 1 + 1
                """
                    .trimIndent(),
                libraryManager,
            )
        val library = translator.toELM()
        // END:basic-translation
    }
}
