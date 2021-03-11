package org.cqframework.cql.tools.formatter;

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

        System.out.print(CqlFormatterVisitor.getFormattedOutput(is));
    }
}
