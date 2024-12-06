package org.cqframework.cql.cql2elm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.antlr.v4.kotlinruntime.CharStreams;
import org.antlr.v4.kotlinruntime.CommonTokenStream;
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Options;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessor;
import org.cqframework.cql.elm.IdObjectFactory;
import org.cqframework.cql.elm.utility.Visitors;
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.elm.r1.Element;
import org.junit.jupiter.api.Test;

// This test compiles a few example libraries and ensures
// local ids are assigned for all elements in the resulting ELM
class TestLocalId {

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
    void localIds() throws Exception {
        runTest("OperatorTests/CqlListOperators.cql");
        runTest("TranslationTests.cql");
        runTest("LibraryTests/TestMeasure.cql");
    }

    private void runTest(String cqlFileName) throws Exception {
        var lib = TestUtils.createTranslator(cqlFileName, Options.EnableLocators, Options.EnableAnnotations)
                .toELM();

        idChecker.visitLibrary(lib, cqlFileName);
    }

    @Test
    void noLocalIdThrowsException() throws Exception {

        // This is an intentionally broken IdObjectFactory that will not assign localIds
        var brokenFactory = new IdObjectFactory() {
            @Override
            public String nextId() {
                return null;
            }
        };

        // Bit longer setup because we're handling some deeper internals
        var modelManager = new ModelManager();
        var options = new CqlCompilerOptions(Options.EnableLocators, Options.EnableAnnotations);
        var libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        var libraryBuilder = new LibraryBuilder(libraryManager, brokenFactory);

        // Simplest possible library, just to trigger a missing id error.
        cqlLexer lexer = new cqlLexer(CharStreams.INSTANCE.fromString("library Test\ndefine \"One\": 1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        var tree = parser.library();
        CqlPreprocessor preprocessor = new CqlPreprocessor(libraryBuilder, tokens);
        preprocessor.visit(tree);
        Cql2ElmVisitor visitor = new Cql2ElmVisitor(libraryBuilder, tokens, preprocessor.getLibraryInfo());
        visitor.visit(tree);

        var exceptions = libraryBuilder.getExceptions();
        // Exceptions for the literal and the define, plus the library itself
        assertEquals(3, exceptions.size());
        var e = exceptions.get(0);
        assertTrue(e.getMessage().contains("localId"));
    }
}
