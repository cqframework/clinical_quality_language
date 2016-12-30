package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.hl7.elm.r1.Library;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static Cql2ElmVisitor visitFile(String fileName, boolean inClassPath) throws IOException {
        InputStream is = inClassPath ? TestUtils.class.getResourceAsStream(fileName) : new FileInputStream(fileName);
        TokenStream tokens = parseANTLRInputStream(new ANTLRInputStream(is));
        ParseTree tree = parseTokenStream(tokens);
        Cql2ElmVisitor visitor = createElmTranslatorVisitor(tokens, tree);
        visitor.visit(tree);
        return visitor;
    }

    public static Object visitData(String cqlData) {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromText(cqlData, modelManager, new LibraryManager(modelManager));
        ensureValid(translator);
        return translator.toObject();
    }

    public static Library visitLibrary(String cqlLibrary) {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromText(cqlLibrary, modelManager, new LibraryManager(modelManager));
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
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromText(cqlData, modelManager, new LibraryManager(modelManager), options.toArray(new CqlTranslator.Options[options.size()]));
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
        LibraryBuilder libraryBuilder = new LibraryBuilder(modelManager, libraryManager);
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
}
