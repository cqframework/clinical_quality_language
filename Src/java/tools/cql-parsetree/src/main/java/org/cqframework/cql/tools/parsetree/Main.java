package org.cqframework.cql.tools.parsetree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

/**
 * A simple wrapper around the ANTLR4 testrig.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String inputFile = null;
        if (args.length > 0) {
            inputFile = args[0];
        }
        InputStream is = System.in;
        if (inputFile != null) {
            is = new FileInputStream(inputFile);
        }
        CharStream input = CharStreams.fromStream(is);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        ParserRuleContext tree = parser.library();
        Trees.inspect(tree, parser);
    }
}
