package org.cqframework.cql.cql2js;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class Cql2Js {
    private static enum Format { JSON, COFFEESCRIPT }

    private static void writeJs(File inFile, PrintWriter pw, Format format) throws IOException {
        String lib = CqlLibrary.loadCql(inFile).asJson();
        if (format == Format.COFFEESCRIPT) {
            pw.print("module.exports = ");
        }
        pw.println(lib);
        pw.println();
        pw.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OptionParser parser = new OptionParser();
        OptionSpec<File> input = parser.accepts("input").withRequiredArg().ofType(File.class).required();
        OptionSpec<File> output = parser.accepts("output").withRequiredArg().ofType(File.class);
        OptionSpec coffee = parser.accepts("coffee");
        OptionSpec stdout = parser.accepts("stdout");

        OptionSet options = parser.parse(args);
        Format format = options.has(coffee) ? Format.COFFEESCRIPT : Format.JSON;
        File infile = input.value(options);
        PrintWriter pw;
        if (options.has(stdout)) {
            pw = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
        } else {
            File outfile;
            if (! options.has(output) || output.value(options).isDirectory()) {
                // Use input filename with ".json" or ".coffee" extension
                String name = infile.getName();
                if (name.lastIndexOf('.') != -1) {
                    name = name.substring(0, name.lastIndexOf('.'));
                }
                name = name + (format == Format.COFFEESCRIPT ? ".coffee" : ".json");
                String basePath = options.has(output) ? output.value(options).getAbsolutePath() : infile.getParent();
                outfile = new File(basePath + File.separator + name);
            } else {
                outfile = output.value(options);
            }
            if (outfile.equals(infile)) {
                throw new IllegalArgumentException("input and output file must be different!");
            }
            pw = new PrintWriter(outfile, "UTF-8");
        }

        writeJs(infile, pw, format);
    }
}
