package org.cqframework.cql.cql2elm.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Options;
import org.cqframework.cql.cql2elm.TestUtils;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.utility.Visitors;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Library;

public class LocalIdTests {

    private static class MissingIdDescription {
        private final Element element;

        public MissingIdDescription(Element element) {
            this.element = element;
        }

        public Element element() {
            return element;
        }

        public String description() {
            var description =
                    String.format("%s missing localId", element.getClass().getSimpleName());
            if (element.getTrackbacks() != null && !element.getTrackbacks().isEmpty()) {
                var tb = element.getTrackbacks().get(0);
                description = description
                        + String.format(
                                " at %s:[%s:%s-%s:%s]",
                                tb.getLibrary().getId(),
                                tb.getStartLine(),
                                tb.getStartChar(),
                                tb.getEndLine(),
                                tb.getEndChar());
            }

            return description;
        }
    }

    private static BiFunction<Trackable, List<MissingIdDescription>, List<MissingIdDescription>> missingIdChecker =
            (elm, context) -> {
                if (!(elm instanceof Element)) {
                    return context;
                }

                Element element = (Element) elm;
                if (element.getLocalId() == null) {
                    context.add(new MissingIdDescription(element));
                }

                return context;
            };

    protected Library compile(String cql) {
        return TestUtils.createTranslatorFromText(cql, Options.EnableAnnotations, Options.EnableLocators)
                .toELM();
    }

    // @Test
    public void simpleTest() {
        var lib = compile("library Test version '1.0.0'");
        var missingIds = Visitors.from(missingIdChecker).visitElement(lib, new ArrayList<>());

        for (var missingId : missingIds) {
            System.out.println(missingId.description());
        }

        assertTrue(missingIds.isEmpty());
    }

    // @Test
    public void equalityTest() {
        var lib = compile("library Test version '1.0.0'\n define foo: 1 = 1\n define bar: 1 != 1");
        var missingIds = Visitors.from(missingIdChecker).visitElement(lib, new ArrayList<>());

        for (var missingId : missingIds) {
            System.out.println(missingId.description());
        }

        assertTrue(missingIds.isEmpty());
    }
}
