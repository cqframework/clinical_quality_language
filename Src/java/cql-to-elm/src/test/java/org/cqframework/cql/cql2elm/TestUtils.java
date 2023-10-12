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
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestUtils {

    private static ModelManager getModelManager() {
        return new ModelManager();
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
        return visitFile(fileName, SignatureLevel.Overloads);
    }

    public static Object visitFile(String fileName, SignatureLevel signatureLevel) throws IOException {
        final File file = getFileOrThrow(fileName);
        final CqlTranslator translator = CqlTranslator.fromFile(file, getLibraryManager(ErrorSeverity.Info, signatureLevel));
        ensureValid(translator);
        return translator.toObject();
    }

    public static CompiledLibrary visitFileLibrary(String fileName) throws IOException {
        return visitFileLibrary(fileName, SignatureLevel.Overloads);
    }

    public static CompiledLibrary visitFileLibrary(String fileName, SignatureLevel signatureLevel) throws IOException {
        final File file = getFileOrThrow(fileName);
        CqlTranslator translator = CqlTranslator.fromFile(file, getLibraryManager(ErrorSeverity.Info, signatureLevel));
        ensureValid(translator);
        return translator.getTranslatedLibrary();
    }

    public static Object visitData(String cqlData) {
        CqlTranslator translator = CqlTranslator.fromText(cqlData,  getLibraryManager(ErrorSeverity.Info, SignatureLevel.Overloads));
        ensureValid(translator);
        return translator.toObject();
    }

    public static Library visitLibrary(String cqlLibrary) {
        CqlTranslator translator = CqlTranslator.fromText(cqlLibrary, getLibraryManager(ErrorSeverity.Info, SignatureLevel.Overloads));
        ensureValid(translator);
        return translator.toELM();
    }

    public static Object visitData(String cqlData, boolean enableAnnotations, boolean enableDateRangeOptimization) {
        final HashSet<CqlCompilerOptions.Options> options = new HashSet<>();
        if (enableAnnotations) {
            options.add(CqlCompilerOptions.Options.EnableAnnotations);
        }
        if (enableDateRangeOptimization) {
            options.add(CqlCompilerOptions.Options.EnableDateRangeOptimization);
        }


        final CqlTranslator translator = CqlTranslator.fromText(cqlData,getLibraryManager(ErrorSeverity.Info, SignatureLevel.Overloads, options.toArray(new CqlCompilerOptions.Options[0])));
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
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = getLibraryManager(modelManager, null);
        LibraryBuilder libraryBuilder = new LibraryBuilder(libraryManager);
        CqlPreprocessorVisitor preprocessor = new CqlPreprocessorVisitor(libraryBuilder, tokens);
        preprocessor.visit(tree);
        Cql2ElmVisitor visitor = new Cql2ElmVisitor(libraryBuilder);
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

    public static CqlTranslator runSemanticTestNoAnnotations(String fileName) throws IOException {
        return runSemanticTestNoAnnotations(fileName, 0);
    }

    public static CqlTranslator runSemanticTestNoAnnotations(String fileName, int expectedErrors) throws IOException {
        final CqlCompilerOptions cqlCompilerOptions = new CqlCompilerOptions();
        final CqlTranslator translator = createTranslator(null, fileName, cqlCompilerOptions);

        for (CqlCompilerException error : translator.getErrors()) {
            System.err.printf("(%d,%d): %s%n",
                    error.getLocator().getStartLine(), error.getLocator().getStartChar(), error.getMessage());
        }
        assertThat(translator.getErrors().size(), is(expectedErrors));
        return translator;
    }

    public static CqlTranslator runSemanticTest(NamespaceInfo namespaceInfo, String testFileName, int expectedErrors, CqlCompilerOptions.Options... options) throws IOException {
        final CqlCompilerOptions cqlCompilerOptions = getCqlCompilerOptions(ErrorSeverity.Info, SignatureLevel.Overloads, options);
        return runSemanticTest(namespaceInfo, testFileName, expectedErrors, cqlCompilerOptions);
    }

    public static CqlTranslator runSemanticTest(NamespaceInfo namespaceInfo, String testFileName, int expectedErrors, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        final CqlCompilerOptions cqlCompilerOptions = getCqlCompilerOptions(ErrorSeverity.Info, signatureLevel, options);
        return runSemanticTest(namespaceInfo, testFileName, expectedErrors, cqlCompilerOptions);
    }

    public static CqlTranslator runSemanticTest(NamespaceInfo namespaceInfo, String testFileName, int expectedErrors, CqlCompilerOptions options) throws IOException {
        CqlTranslator translator = createTranslator(namespaceInfo, testFileName, options);
        for (CqlCompilerException error : translator.getErrors()) {
            System.err.printf("(%d,%d): %s%n",
                    error.getLocator().getStartLine(), error.getLocator().getStartChar(), error.getMessage());
        }
        assertThat(translator.getErrors().size(), is(expectedErrors));
        return translator;
    }

    public static CqlTranslator createTranslatorFromText(String cqlText, CqlCompilerOptions.Options... options) {
        final LibraryManager libraryManager = getLibraryManager(ErrorSeverity.Info, SignatureLevel.Overloads, options);
        return CqlTranslator.fromText(cqlText,  libraryManager);
    }

    public static CqlTranslator createTranslatorFromStream(String testFileName, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, testFileName, SignatureLevel.Overloads, options);
    }

    public static CqlTranslator createTranslatorFromStream(String testFileName, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslatorFromStream(null, testFileName, signatureLevel, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, String testFileName, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        InputStream inputStream = Cql2ElmVisitorTest.class.getResourceAsStream(testFileName);
        if (inputStream == null) {
            throw new FileNotFoundException("cannot find file with path: " + testFileName);
        }
        return createTranslatorFromStream(null, inputStream, signatureLevel, options);
    }

    public static CqlTranslator createTranslatorFromStream(NamespaceInfo namespaceInfo, InputStream inputStream, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        ModelManager modelManager = new ModelManager();
        var compilerOptions = getCqlCompilerOptions(ErrorSeverity.Info, signatureLevel, options);
        final LibraryManager libraryManager = getLibraryManager(modelManager, compilerOptions);
        return CqlTranslator.fromStream(namespaceInfo, inputStream,  libraryManager);
    }

    private static LibraryManager getLibraryManager(ModelManager theModelManager, CqlCompilerOptions theCompilerOptions) {
        LibraryManager libraryManager = new LibraryManager(theModelManager, theCompilerOptions);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        return libraryManager;
    }

    public static CqlTranslator createTranslator(String testFileName, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslator(null, testFileName, getCqlCompilerOptions(ErrorSeverity.Info, SignatureLevel.Overloads, options));
    }

    public static CqlTranslator createTranslatorNoAnnotations(String testFileName, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslator(null, testFileName, getCqlCompilerOptionsNoAnnotations(options));
    }

    public static CqlTranslator createTranslatorNoAnnotations(String testFileName, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) throws IOException {
        return createTranslator(null, testFileName, getCqlCompilerOptionsNoAnnotations(ErrorSeverity.Warning, signatureLevel, options));
    }

    public static CqlTranslator getTranslator(String cqlTestFile, String nullableLibrarySourceProvider, LibraryBuilder.SignatureLevel signatureLevel) throws IOException {
        final File testFile = getFileOrThrow(cqlTestFile);
        final ModelManager modelManager = new ModelManager();

        final CqlCompilerOptions compilerOptions = getCqlCompilerOptions(ErrorSeverity.Warning, signatureLevel);

        final LibraryManager libraryManager = getLibraryManager(compilerOptions, modelManager, nullableLibrarySourceProvider);
        return CqlTranslator.fromFile(testFile,  libraryManager);
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

        final File translationTestFile = getFileOrThrow(testFileName);
        ModelManager modelManager = new ModelManager();
        final LibraryManager libraryManager = getLibraryManager(options, modelManager, path);
        return CqlTranslator.fromFile(namespaceInfo, translationTestFile,  libraryManager);
    }

    public static File getFileOrThrow(String fileName) throws FileNotFoundException {
        final URL resource = Optional.ofNullable(Cql2ElmVisitorTest.class.getResource(fileName))
                .orElseThrow(() -> new FileNotFoundException("cannot find file with path: " + fileName));
        return new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
    }

    private static LibraryManager getLibraryManager(ErrorSeverity errorSeverity, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) {
        final ModelManager modelManager = new ModelManager();
        final CqlCompilerOptions compilerOptions = getCqlCompilerOptions(errorSeverity, signatureLevel, options);
        return getLibraryManager(compilerOptions, modelManager, null);
    }

    private static LibraryManager getLibraryManager(CqlCompilerOptions options, ModelManager modelManager, String path) {
        final LibraryManager libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(path == null ? new TestLibrarySourceProvider() : new TestLibrarySourceProvider(path));
        return libraryManager;
    }

    private static CqlCompilerOptions getCqlCompilerOptions(ErrorSeverity errorSeverity, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) {
        return new CqlCompilerOptions(errorSeverity, signatureLevel, withEnableAnnotations(options));
    }

    private static CqlCompilerOptions getCqlCompilerOptionsNoAnnotations(ErrorSeverity errorSeverity, SignatureLevel signatureLevel, CqlCompilerOptions.Options... options) {
        return new CqlCompilerOptions(errorSeverity, signatureLevel, options);
    }

    private static CqlCompilerOptions getCqlCompilerOptionsNoAnnotations(CqlCompilerOptions.Options... options) {
        return new CqlCompilerOptions(options);
    }

    private static CqlCompilerOptions.Options[] withEnableAnnotations(CqlCompilerOptions.Options... options) {
        final List<CqlCompilerOptions.Options> optionsList = new ArrayList<>(Arrays.asList(options));
        optionsList.add(CqlCompilerOptions.Options.EnableAnnotations);
        return optionsList.toArray(CqlCompilerOptions.Options[]::new);
    }
}