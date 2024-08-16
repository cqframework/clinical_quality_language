package org.cqframework.cql.cql2elm.elm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.hl7.elm.r1.ChoiceTypeSpecifier;
import org.hl7.elm.r1.Element;
import org.junit.jupiter.api.Test;

class ElmEditorTest {
    boolean editCalled = false;

    @Test
    void applyEdits() {
        editCalled = false;
        var editCounter = new ElmEdit() {
            public void edit(Element element) {
                editCalled = true;
            }
        };
        var edits = List.of((ElmEdit) editCounter);
        ElmEditor editor = new ElmEditor(edits);

        editCalled = false;
        editor.applyEdits(null, null);
        assertFalse(editCalled);

        editCalled = false;
        editor.applyEdits(null, new ChoiceTypeSpecifier());
        assertTrue(editCalled);
    }
}
