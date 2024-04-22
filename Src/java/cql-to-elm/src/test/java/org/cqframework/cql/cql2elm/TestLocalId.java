package org.cqframework.cql.cql2elm;

import static org.junit.Assert.assertNotNull;

import org.cqframework.cql.cql2elm.CqlCompilerOptions.Options;
import org.cqframework.cql.elm.utility.Visitors;
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor;
import org.hl7.elm.r1.Element;
import org.junit.Test;

// This test compiles a few example libraries and ensures
// local ids are assigned for all elements in the resulting ELM
public class TestLocalId {

    // This visitor checks that all nodes the graph have a localId
    static FunctionalElmVisitor<Void, String> idChecker = Visitors.from((node, libraryName) -> {
        if (node instanceof Element) {
            var locator = node.getTrackbacks().isEmpty()
                    ? "<unknown>"
                    : node.getTrackbacks().get(0).toLocator();
            assertNotNull(
                    String.format(
                            "node %s in library %s is missing localId at %s",
                            node.getClass().getName(), libraryName, locator),
                    ((Element) node).getLocalId());
        }

        return null;
    });

    @Test
    public void testLocalIds() throws Exception {
        runTest("OperatorTests/CqlListOperators.cql");
        runTest("TranslationTests.cql");
        runTest("LibraryTests/TestMeasure.cql");
    }

    private void runTest(String cqlFileName) throws Exception {
        var lib = TestUtils.createTranslator(cqlFileName, Options.EnableLocators, Options.EnableAnnotations)
                .toELM();

        idChecker.visitLibrary(lib, cqlFileName);
    }
}
