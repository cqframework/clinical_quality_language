package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

public class Main {
    public static void main(String[] args) {
        String logic = "let InDemographic = AgeAt(start of MeasurementPeriod) >= 16";
        ANTLRInputStream input = new ANTLRInputStream(logic);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.logic();

        ParseTreeWalker walker = new ParseTreeWalker();
        CqlTranslatorListener listener = new CqlTranslatorListener();
        walker.walk(listener, tree);
    }
}
