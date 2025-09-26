package org.cqframework.cql.cql2elm.elm

import kotlin.collections.listOf
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Library
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ElmEditorTest {
    private var editCount = 0
    private val edit: IElmEdit = IElmEdit { _: Element -> editCount++ }
    private val editor = ElmEditor(listOf(edit))

    @BeforeEach
    fun beforeEach() {
        editCount = 0
    }

    @Test
    fun edit() {
        editor.edit(Library())
        Assertions.assertEquals(1, editCount)
    }

    @Test
    fun applyEdits2() {
        editor.applyEdits(ChoiceTypeSpecifier())
        Assertions.assertEquals(1, editCount)
    }
}
