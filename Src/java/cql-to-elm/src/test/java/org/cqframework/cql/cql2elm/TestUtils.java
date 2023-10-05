package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.elm.r1.Library;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestUtils {

    private static ModelManager getModelManager() {
        return new ModelManager();
    }

    private static LibraryManager getLibraryManager() {
        final SignatureLevel sig = null;
        return getLibraryManager(sig);
    }

    private static LibraryManager getLibraryManager(SignatureLevel nullableSignatureLevel) {
        return getLibraryManager(new CqlCompilerOptions(ErrorSeverity.Warning,
                Objects.requireNonNullElse(nullableSignatureLevel, SignatureLevel.All)));

    }

    private static LibraryManager getLibraryManager(CqlCompilerOptions options) {
        var libraryManager = new LibraryManager(getModelManager(), options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());

        return libraryManager;
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
        return visitFile(fileName, null);
    }

    public static Object visitFile(String fileName, SignatureLevel nullableSignatureLevel) throws IOException {
        final File file = getFileOrThrow(fileName);
        CqlTranslator translator = CqlTranslator.fromFile(file, getLibraryManager(nullableSignatureLevel));
        ensureValid(translator);
        return translator.toObject();
    }

    public static CompiledLibrary visitFileLibrary(String fileName) throws IOException {
        return visitFileLibrary(fileName, null);
    }

    public static CompiledLibrary visitFileLibrary(String fileName, SignatureLevel nullableSignatureLevel) throws IOException {
        final File file = getFileOrThrow(fileName);
        CqlTranslator translator = CqlTranslator.fromFile(file, getLibraryManager(nullableSignatureLevel));
        ensureValid(translator);
        return translator.getTranslatedLibrary();
    }

    public static Object visitData(String cqlData) {
        CqlTranslator translator = CqlTranslator.fromText(cqlData,  getLibraryManager());
        ensureValid(translator);
        return translator.toObject();
    }

    public static Library visitLibrary(String cqlLibrary) {
        CqlTranslator translator = CqlTranslator.fromText(cqlLibrary, getLibraryManager());
        ensureValid(translator);
        return translator.toELM();
    }

    public static Object visitData(String cqlData, boolean enableAnnotations, boolean enableDateRangeOptimization) {
        var compilerOptions = new CqlCompilerOptions();
        if (enableAnnotations) {
           compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableAnnotations);
        }
        if (enableDateRangeOptimization) {
            compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableDateRangeOptimization);
        }

        CqlTranslator translator = CqlTranslator.fromText(cqlData,getLibraryManager(compilerOptions));
        ensureValid(translator);
        return translator.toObject();
    }

    private static void ensureValid(CqlTranslator translator) {
        StringBuilder builder = new StringBuilder();
        for (CqlCompilerException error : translator.getErrors()) {
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
        LibraryBuilder libraryBuilder = new LibraryBuilder(libraryManager);
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

    public static CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlCompilerOptions.Options... options) throws IOException {
        return runSemanticTest(null, testFileName, expectedErrors, options);
    }

    public static CqlTranslator runSemanticTest(String testFileName, int expectedErrors, CqlCompilerOptions options) throws IOException {
        return runSemanticTest(null, testFileName, expectedErrors, options);
    }

    public static CqlTranslator runSemanticTest(NamespaceInfo namespaceInfo, String testFileName, int expectedErrors, CqlCompilerOptions.Options... options) throws IOException {
        return runSemanticTest(namespaceInfo, testFileName, expectedErrors, new CqlCompilerOptions(options));
    }

    public static CqlTranslator runSemanticTest(NamespaceInfo namespaceInfo, String testFileName, int expectedErrors, CqlCompilerOptions options) throws IOException {
        CqlTranslator translator = TestUtils.createTranslator(namespaceInfo, testFileName, options);
        for (CqlCompilerException error : translator.getErrors()) {
            System.err.println(String.format("(%d,%d): %s",
                    error.getLocator().getStartLine(), error.getLocator().getStartChar(), error.getMessage()));
        }
        assertThat(translator.getErrors().size(), is(expectedErrors));
        return translator;
    }

    public static CqlTranslator createTranslatorFromText(String cqlText, CqlCompilerOptions.Options... options) {
        ModelManager modelManager = new ModelManager();
        var compilerOptions = new CqlCompilerOptions(options);
        LibraryManager libraryManager = new LibraryManager(modelManager, compilerOptions);
        CqlTranslator translator = CqlTranslator.fromText(cqlText,  libraryManager);
        return translator;
    }

    public static CqlTranslator createTranslatorFromStream(String testFileName, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, testFileName, null, options);
    }

    public static CqlTranslator createTranslatorFromStream(String testFileName, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, testFileName, signatureLevel, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, String testFileName, SignatureLevel nullableSignatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        InputStream inputStream = Cql2ElmVisitorTest.class.getResourceAsStream(testFileName);
        if (inputStream == null) {
            throw new FileNotFoundException("cannot find file with path: " + testFileName);
        }
        return createTranslatorFromStream(null, inputStream, nullableSignatureLevel, options);
    }

    public static CqlTranslator createTranslatorFromStream(InputStream inputStream, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, inputStream, null, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, InputStream inputStream, SignatureLevel nullableSignatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        ModelManager modelManager = new ModelManager();
        var compilerOptions = new CqlCompilerOptions(options);
        Optional.ofNullable(nullableSignatureLevel).ifPresent(compilerOptions::setSignatureLevel);
        LibraryManager libraryManager = new LibraryManager(modelManager, compilerOptions);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        CqlTranslator translator = CqlTranslator.fromStream(namespaceInfo, inputStream,  libraryManager);
        return translator;
    }

    public static CqlTranslator createTranslator(String testFileName, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslator(null, testFileName, new CqlCompilerOptions(options));
    }

    public static CqlTranslator createTranslator(String testFileName, CqlCompilerOptions options) throws IOException {
        return createTranslator(null, testFileName, options);
    }

    public static CqlTranslator createTranslator(NamespaceInfo namespaceInfo, String testFileName, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslator(namespaceInfo, testFileName, new CqlCompilerOptions(options));
    }

    public static CqlTranslator createTranslator(NamespaceInfo namespaceInfo, String testFileName, CqlCompilerOptions options) throws IOException {
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

        final File translationTestFile = getFileOrThrow(testFileName);
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(path == null ? new TestLibrarySourceProvider() : new TestLibrarySourceProvider(path));
        CqlTranslator translator = CqlTranslator.fromFile(namespaceInfo, translationTestFile,  libraryManager);
        return translator;
    }

    private static File getFileOrThrow(String theFileName) throws FileNotFoundException {
        final URL resource = Optional.ofNullable(Cql2ElmVisitorTest.class.getResource(theFileName))
                .orElseThrow(() -> new FileNotFoundException("cannot find file with path: " + theFileName));
        return new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
    }
}