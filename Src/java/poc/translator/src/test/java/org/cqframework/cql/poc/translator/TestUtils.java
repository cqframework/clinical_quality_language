package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.translator.preprocessor.CqlPreprocessorVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestUtils {

    public static ParseTree parseFile(String fileName, boolean inClassPath)throws IOException {
        InputStream is = inClassPath ? TestUtils.class.getResourceAsStream(fileName) : new FileInputStream(fileName);
        return parseANTLRInputStream(new ANTLRInputStream(is));
    }

    public static ElmTranslatorVisitor visitFile(String fileName, boolean inClassPath) throws IOException {
        ParseTree tree = parseFile(fileName, inClassPath);
        ElmTranslatorVisitor visitor = createElmTranslatorVisitor(tree);
        visitor.visit(tree);
        return visitor;
    }


    public static ParseTree parseData(String cqlData){
        return parseANTLRInputStream(new ANTLRInputStream(cqlData));
    }

    public static Object visitData(String cqlData) {
        ParseTree tree = parseData(cqlData);
        return createElmTranslatorVisitor(tree).visit(tree);
    }

    private static ElmTranslatorVisitor createElmTranslatorVisitor(ParseTree tree) {
        CqlPreprocessorVisitor preprocessor = new CqlPreprocessorVisitor();
        preprocessor.visit(tree);
        ElmTranslatorVisitor visitor = new ElmTranslatorVisitor();
        visitor.setLibraryInfo(preprocessor.getLibraryInfo());
        return visitor;
    }

    private static ParseTree parseANTLRInputStream(ANTLRInputStream is) {
        cqlLexer lexer = new cqlLexer(is);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        return parser.logic();
    }
}
