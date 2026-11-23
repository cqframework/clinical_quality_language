package org.cqframework.cql.cql2elm.elm

import kotlin.test.Test
import kotlin.test.assertEquals
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library

class ElmEditorTest {
    private var editCount = 0
    private val edit = IElmEdit { _: Element -> editCount++ }
    private val editor = ElmEditor(listOf(edit))

    @Test
    fun edit() {
        editCount = 0
        editor.edit(Library())
        assertEquals(1, editCount)
    }

    @Test
    fun applyEdits2() {
        editCount = 0
        editor.applyEdits(ChoiceTypeSpecifier())
        assertEquals(1, editCount)
    }
}
