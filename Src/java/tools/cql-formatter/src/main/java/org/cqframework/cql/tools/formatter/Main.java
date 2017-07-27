package org.cqframework.cql.tools.formatter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        ANTLRInputStream input = new ANTLRInputStream(is);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        CommentListener listener = new CommentListener(tokens);
        listener.rewriteTokens();
        cqlParser parser = new cqlParser(listener.tokens);
        parser.setBuildParseTree(true);
        ParserRuleContext tree = parser.library();
        CqlFormatterVisitor formatter = new CqlFormatterVisitor();
        String output = (String)formatter.visit(tree);
        System.out.print(listener.refineOutput(output));
    }
}
