package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.hl7.elm.r1.Library;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestUtils {

    private static ModelManager modelManager;
    private static LibraryManager libraryManager;
    private static UcumService ucumService;

    private static void setup() {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        try {
            ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));

        } catch (UcumException e) {
            e.printStackTrace();
        }
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
        TokenStream tokens = parseANTLRInputStream(new ANTLRInputStream(is));
        ParseTree tree = parseTokenStream(tokens);
        Cql2ElmVisitor visitor = createElmTranslatorVisitor(tokens, tree);
        visitor.visit(tree);
        return visitor;
    }

    public static Object visitFile(String fileName) throws IOException {
        File file = new File(URLDecoder.decode(Cql2ElmVisitorTest.class.getResource(fileName).getFile(), "UTF-8"));
        CqlTranslator translator = CqlTranslator.fromFile(file, getModelManager(), getLibraryManager(), getUcumService());
        ensureValid(translator);
        return translator.toObject();
    }

    public static TranslatedLibrary visitFileLibrary(String fileName) throws IOException {
        File file = new File(URLDecoder.decode(Cql2ElmVisitorTest.class.getResource(fileName).getFile(), "UTF-8"));
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

    private static TokenStream parseANTLRInputStream(ANTLRInputStream is) {
        cqlLexer lexer = new cqlLexer(is);
        return new CommonTokenStream(lexer);
    }

    public static CqlTranslator runSemanticTest(String testFileName, int expectedErrors) throws IOException {
        File translationTestFile = new File(URLDecoder.decode(Cql2ElmVisitorTest.class.getResource(testFileName).getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromFile(translationTestFile, modelManager, new LibraryManager(modelManager), getUcumService());
        for (CqlTranslatorException error : translator.getErrors()) {
            System.err.println(String.format("(%d,%d): %s",
                    error.getLocator().getStartLine(), error.getLocator().getStartChar(), error.getMessage()));
        }
        assertThat(translator.getErrors().size(), is(expectedErrors));
        return translator;
    }
}
