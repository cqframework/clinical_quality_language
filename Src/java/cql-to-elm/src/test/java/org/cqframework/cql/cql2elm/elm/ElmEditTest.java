package org.cqframework.cql.cql2elm.elm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.hl7.elm.r1.ChoiceTypeSpecifier;
import org.hl7.elm.r1.NamedTypeSpecifier;
import org.hl7.elm.r1.TypeSpecifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ElmEditTest {

    @Test
    @Disabled("TODO: Fix ExtChoiceTypeSpecifier logic")
    void removeChoiceTypeSpecifierTypeIfEmpty() {
        var extChoiceTypeSpecifier = new ExtChoiceTypeSpecifier();

        extChoiceTypeSpecifier.setType(List.of());
        ElmEdit.REMOVE_CHOICE_TYPE_SPECIFIER_TYPE_IF_EMPTY.edit(extChoiceTypeSpecifier);
        assertNull(extChoiceTypeSpecifier.getType());

        var typeSpecifiers = List.of((TypeSpecifier) new NamedTypeSpecifier());
        extChoiceTypeSpecifier.setType(typeSpecifiers);
        ElmEdit.REMOVE_CHOICE_TYPE_SPECIFIER_TYPE_IF_EMPTY.edit(extChoiceTypeSpecifier);
        assertSame(typeSpecifiers, extChoiceTypeSpecifier.getType());
    }

    private static class ExtChoiceTypeSpecifier extends ChoiceTypeSpecifier {
        //            public List<TypeSpecifier> getType() {
        //                return type;
        //            }
    }
}
