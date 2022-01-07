package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.hl7.elm.r1.Library;

public class TestUtils {

    private static ModelManager modelManager;
    private static LibraryManager libraryManager;
    private static UcumService ucumService;

    private static void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        try {
            ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
        }
        catch (UcumException e) {
            e.printStackTrace();
        }
    }

    private static void tearDown() {
        ucumService = null;
        libraryManager = null;
        modelManager = null;
    }

    public static void reset() {
        tearDown();
    }

    private static ModelManager getModelManager() {
        if (modelManager == null) {
            setup();
        }

        return modelManager;
    }

    private static LibraryManager getLibraryManager() {
        if (libraryManager == null) {
            setup();
        }

        return libraryManager;
    }

    private static UcumService getUcumService() {
        if (ucumService == null) {
            setup();
        }

        return ucumService;
    }

    public static Cql2ElmVisitor visitFile(String fileName, boolean inClassPath) throws IOException {
        InputStream is = inClassPath ? TestUtils.class.getResourceAsStream(fileName) : new FileInputStream(fileName);
        TokenStream tokens = parseCharStream(CharStreams.fromStream(is));
        ParseTree tree = parseTokenStream(tokens);
        Cql2ElmVisitor visitor = createElmTranslatorVisitor(tokens, tree);
        visitor.visit(tree);
        return visitor;
    }

    public static Object visitFile(String fileName) throws IOException {
        File file = new File(URLDecoder.decode(TestUtils.class.getResource(fileName).getFile(), "UTF-8"));
        CqlTranslator translator = CqlTranslator.fromFile(file, getModelManager(), getLibraryManager(), getUcumService());
        ensureValid(translator);
        return translator.toObject();
    }

    public static TranslatedLibrary visitFileLibrary(String fileName) throws IOException {
        File file = new File(URLDecoder.decode(TestUtils.class.getResource(fileName).getFile(), "UTF-8"));
        CqlTranslator translator = CqlTranslator.fromFile(file, getModelManager(), getLibraryManager(), getUcumService());
        ensureValid(translator);
        return translator.getTranslatedLibrary();
    }

    public static Object visitData(String cqlData) {
        CqlTranslator translator = CqlTranslator.fromText(cqlData, getModelManager(), getLibraryManager(), getUcumService());
        ensureValid(translator);
        return translator.toObject();
    }

    public static Library visitLibrary(String cqlLibrary) {
        CqlTranslator translator = CqlTranslator.fromText(cqlLibrary, getModelManager(), getLibraryManager(), getUcumService());
        ensureValid(translator);
        return translator.toELM();
    }

    public static Object visitData(String cqlData, boolean enableAnnotations, boolean enableDateRangeOptimization) {
        List<CqlTranslator.Options> options = new ArrayList<>();
        if (enableAnnotations) {
            options.add(CqlTranslator.Options.EnableAnnotations);
        }
        if (enableDateRangeOptimization) {
            options.add(CqlTranslator.Options.EnableDateRangeOptimization);
        }
        CqlTranslator translator = CqlTranslator.fromText(cqlData, getModelManager(), getLibraryManager(), getUcumService(),
                options.toArray(new CqlTranslator.Options[options.size()]));
        ensureValid(translator);
        return translator.toObject();
    }

    private static void ensureValid(CqlTranslator translator) {
        StringBuilder builder = new StringBuilder();
        for (CqlTranslatorException error : translator.getErrors()) {
            builder.append(String.format("%s%n", error.getMessage()));
        }
        if (builder.length() > 0) {
            throw new IllegalStateException(builder.toString());
        }
    }

    private static Cql2ElmVisitor createElmTranslatorVisitor(TokenStream tokens, ParseTree tree) {
        CqlPreprocessorVisitor preprocessor = new CqlPreprocessorVisitor();
        preprocessor.visit(tree);
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        LibraryBuilder libraryBuilder = new LibraryBuilder(modelManager, libraryManager, ucumService);
        Cql2ElmVisitor visitor = new Cql2ElmVisitor(libraryBuilder);
        visitor.setTokenStream(tokens);
        visitor.setLibraryInfo(preprocessor.getLibraryInfo());
        return visitor;
    }

    private static ParseTree parseTokenStream(TokenStream tokens) {
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        return parser.library();
    }

    private static TokenStream parseCharStream(CharStream is) {
        cqlLexer lexer = new cqlLexer(is);
        return new CommonTokenStream(lexer);
    }

    public static CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlTranslator.Options... options) throws IOException {
        return runSemanticTest(null, testFileName, expectedErrors, options);
    }

    public static CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlTranslatorOptions options) throws IOException {
        return runSemanticTest(null, testFileName, expectedErrors, options);
    }

    public static CqlTranslator runSemanticTest(NamespaceInfo namespaceInfo, String testFileName, int expectedErrors, CqlTranslator.Options... options) throws IOException {
        return runSemanticTest(namespaceInfo, testFileName, expectedErrors, new CqlTranslatorOptions(options));
    }

    public static CqlTranslator runSemanticTest(NamespaceInfo namespaceInfo, String testFileName, int expectedErrors, CqlTranslatorOptions options) throws IOException {
        CqlTranslator translator = TestUtils.createTranslator(namespaceInfo, testFileName, options);
        for (CqlTranslatorException error : translator.getErrors()) {
            System.err.println(String.format("(%d,%d): %s",
                    error.getLocator().getStartLine(), error.getLocator().getStartChar(), error.getMessage()));
        }
        assertThat(translator.getErrors().size(), is(expectedErrors));
        return translator;
    }

    public static CqlTranslator createTranslatorFromText(String cqlText, CqlTranslator.Options... options) {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        CqlTranslator translator = CqlTranslator.fromText(cqlText, modelManager, libraryManager, getUcumService(), options);
        return translator;
    }

    public static CqlTranslator createTranslatorFromStream(String testFileName, CqlTranslator.Options... options) throws IOException {
        return createTranslatorFromStream(null, testFileName, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, String testFileName, CqlTranslator.Options... options) throws IOException {
        InputStream inputStream = Cql2ElmVisitorTest.class.getResourceAsStream(testFileName);
        return createTranslatorFromStream(null, inputStream, options);
    }

    public static CqlTranslator createTranslatorFromStream(InputStream inputStream, CqlTranslator.Options... options) throws IOException {
        return createTranslatorFromStream(null, inputStream, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, InputStream inputStream, CqlTranslator.Options... options) throws IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        CqlTranslator translator = CqlTranslator.fromStream(namespaceInfo, inputStream, modelManager, libraryManager, getUcumService(), options);
        return translator;
    }

    public static CqlTranslator createTranslator(String testFileName, CqlTranslator.Options... options) throws IOException {
        return createTranslator(null, testFileName, new CqlTranslatorOptions(options));
    }

    public static CqlTranslator createTranslator(String testFileName, CqlTranslatorOptions options) throws IOException {
        return createTranslator(null, testFileName, options);
    }

    public static CqlTranslator createTranslator(NamespaceInfo namespaceInfo, String testFileName, CqlTranslator.Options... options) throws IOException {
        return createTranslator(namespaceInfo, testFileName, new CqlTranslatorOptions(options));
    }

    public static CqlTranslator createTranslator(NamespaceInfo namespaceInfo, String testFileName, CqlTranslatorOptions options) throws IOException {
        String[] segments = testFileName.split("/");
        String path = null;
        if (segments.length > 1) {
            for (int i = 0; i < segments.length - 1; i++) {
                if (path == null) {
                    path = segments[i];
                }
                else {
                    path += "/" + segments[i];
                }
            }
        }
        String fileName = segments[segments.length - 1];

        File translationTestFile = new File(URLDecoder.decode(Cql2ElmVisitorTest.class.getResource(testFileName).getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider(path));
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        CqlTranslator translator = CqlTranslator.fromFile(namespaceInfo, translationTestFile, modelManager, libraryManager, getUcumService(), options);
        return translator;
    }
}