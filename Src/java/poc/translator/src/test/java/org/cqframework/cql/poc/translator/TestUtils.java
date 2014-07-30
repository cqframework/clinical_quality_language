package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestUtils {

    public static ParseTree parseFile(String fileName, boolean inClassPath)throws IOException {
        InputStream is = inClassPath ? TestUtils.class.getResourceAsStream(fileName) : new FileInputStream(fileName);
        return parseANTLRInputStream(new ANTLRInputStream(is));
    }

    public static ParseTree parseData(String cql_data){
        return parseANTLRInputStream(new ANTLRInputStream(cql_data));
    }

    private static ParseTree parseANTLRInputStream(ANTLRInputStream is) {
        cqlLexer lexer = new cqlLexer(is);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        return parser.logic();
    }
}
