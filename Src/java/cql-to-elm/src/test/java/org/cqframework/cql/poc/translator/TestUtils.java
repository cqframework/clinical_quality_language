package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.translator.preprocessor.CqlPreprocessorVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestUtils {

    public static ElmTranslatorVisitor visitFile(String fileName, boolean inClassPath) throws IOException {
        InputStream is = inClassPath ? TestUtils.class.getResourceAsStream(fileName) : new FileInputStream(fileName);
        TokenStream tokens = parseANTLRInputStream(new ANTLRInputStream(is));
        ParseTree tree = parseTokenStream(tokens);
        ElmTranslatorVisitor visitor = createElmTranslatorVisitor(tokens, tree);
        visitor.visit(tree);
        return visitor;
    }

    public static Object visitData(String cqlData) {
        TokenStream tokens = parseANTLRInputStream(new ANTLRInputStream(cqlData));
        ParseTree tree = parseTokenStream(tokens);
        return createElmTranslatorVisitor(tokens, tree).visit(tree);
    }

    public static Object visitData(String cqlData, boolean enableAnnotations, boolean enableDateRangeOptimization) {
        TokenStream tokens = parseANTLRInputStream(new ANTLRInputStream(cqlData));
        ParseTree tree = parseTokenStream(tokens);
        ElmTranslatorVisitor visitor = createElmTranslatorVisitor(tokens, tree);
        if (enableAnnotations) {
            visitor.enableAnnotations();
        }
        if (enableDateRangeOptimization) {
            visitor.enableDateRangeOptimization();
        }
        return visitor.visit(tree);
    }

    private static ElmTranslatorVisitor createElmTranslatorVisitor(TokenStream tokens, ParseTree tree) {
        CqlPreprocessorVisitor preprocessor = new CqlPreprocessorVisitor();
        preprocessor.visit(tree);
        ElmTranslatorVisitor visitor = new ElmTranslatorVisitor();
        visitor.setTokenStream(tokens);
        visitor.setLibraryInfo(preprocessor.getLibraryInfo());
        return visitor;
    }

    private static ParseTree parseTokenStream(TokenStream tokens) {
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        return parser.logic();
    }

    private static TokenStream parseANTLRInputStream(ANTLRInputStream is) {
        cqlLexer lexer = new cqlLexer(is);
        return new CommonTokenStream(lexer);
    }
}
