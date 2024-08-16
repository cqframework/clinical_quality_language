package org.cqframework.cql.cql2elm.elm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.hl7.elm.r1.ChoiceTypeSpecifier;
import org.hl7.elm.r1.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ElmEditorTest {
    private int editCount = 0;
    private final ElmEditor editor = new ElmEditor(List.of(element -> editCount++));

    @BeforeEach
    void beforeEach() {
        editCount = 0;
    }

    @Test
    void edit() {
        editor.edit(new Library());
        assertEquals(editCount, 1);
    }

    @Test
    void applyEdits1() {
        editor.applyEdits(null);
        assertEquals(editCount, 0);
    }

    @Test
    void applyEdits2() {
        editor.applyEdits(new ChoiceTypeSpecifier());
        assertEquals(editCount, 1);
    }
}
