package org.cqframework.cql.cql2elm;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ObjectFactory;
import org.hl7.elm.r1.Retrieve;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.cqframework.cql.cql2elm.CqlTranslator.fromFile;

public class CqlTranslator {
    public static enum Options { EnableDateRangeOptimization, EnableAnnotations }
    private Library library = null;
    private Object visitResult = null;
    private List<Retrieve> retrieves = null;
    private List<CqlTranslatorException> errors = null;

    public static CqlTranslator fromText(String cqlText, Options... options) {
        return new CqlTranslator(new ANTLRInputStream(cqlText), options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, Options... options) throws IOException {
        return new CqlTranslator(new ANTLRInputStream(cqlStream), options);
    }

    public static CqlTranslator fromFile(String cqlFileName, Options... options) throws IOException {
        return new CqlTranslator(new ANTLRInputStream(new FileInputStream(cqlFileName)), options);
    }

    public static CqlTranslator fromFile(File cqlFile, Options... options) throws IOException {
        return new CqlTranslator(new ANTLRInputStream(new FileInputStream(cqlFile)), options);
    }

    private CqlTranslator(ANTLRInputStream is, Options... options) {
        translateToELM(is, options);
    }

    public String toXml() {
        try {
            return convertToXML(library);
        }
        catch (JAXBException e) {
            throw new IllegalArgumentException("Could not convert library to XML.", e);
        }
    }

    public String toJson() {
        try {
            return convertToJSON(library);
        }
        catch (JAXBException e) {
            throw new IllegalArgumentException("Could not convert library to JSON.", e);
        }
    }

    public Library toELM() {
        return library;
    }

    public Object toObject() {
        return visitResult;
    }

    public List<Retrieve> toRetrieves() {
        return retrieves;
    }

    public List<CqlTranslatorException> getErrors() { return errors; }

    private void translateToELM(ANTLRInputStream is, Options... options) {
        cqlLexer lexer = new cqlLexer(is);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.logic();

        CqlPreprocessorVisitor preprocessor = new CqlPreprocessorVisitor();
        preprocessor.visit(tree);

        Cql2ElmVisitor visitor = new Cql2ElmVisitor();
        visitor.setLibraryInfo(preprocessor.getLibraryInfo());
        visitor.setTokenStream(tokens);

        List<Options> optionList = Arrays.asList(options);
        if (optionList.contains(Options.EnableDateRangeOptimization)) {
            visitor.enableDateRangeOptimization();
        }
        if (optionList.contains(Options.EnableAnnotations)) {
            visitor.enableAnnotations();
        }
        visitResult = visitor.visit(tree);
        library = visitor.getLibrary();
        retrieves = visitor.getRetrieves();
        errors = visitor.getErrors();
    }

    private String convertToXML(Library library) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Library.class, Annotation.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(new ObjectFactory().createLibrary(library), writer);
        return writer.getBuffer().toString();
    }

    private String convertToJSON(Library library) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Library.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("eclipselink.media-type", "application/json");

        StringWriter writer = new StringWriter();
        marshaller.marshal(new ObjectFactory().createLibrary(library), writer);
        return writer.getBuffer().toString();
    }

    private static enum Format { XML, JSON, COFFEE }

    private static void writeELM(File inFile, PrintWriter pw, Format format, boolean dateRangeOptimizations, boolean annotations) throws IOException {
        ArrayList<Options> options = new ArrayList<>();
        if (dateRangeOptimizations) {
            options.add(Options.EnableDateRangeOptimization);
        }
        if (annotations) {
            options.add(Options.EnableAnnotations);
        }
        CqlTranslator translator = fromFile(inFile, options.toArray(new Options[options.size()]));

        if (translator.getErrors().size() > 0) {
            System.err.println("Translation failed due to errors:");
            for (CqlTranslatorException error : translator.getErrors()) {
                TrackBack tb = error.getLocator();
                String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
                        tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                System.err.printf("%s %s%n", lines, error.getMessage());
            }
        } else {
            switch (format) {
                case COFFEE:
                    pw.print("module.exports = ");
                    pw.println(translator.toJson());
                    break;
                case JSON:
                    pw.println(translator.toJson());
                    break;
                case XML:
                default:
                    pw.println(translator.toXml());
            }
        }

        pw.println();
        pw.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OptionParser parser = new OptionParser();
        OptionSpec<File> input = parser.accepts("input").withRequiredArg().ofType(File.class).required();
        OptionSpec<File> output = parser.accepts("output").withRequiredArg().ofType(File.class);
        OptionSpec<String> format = parser.accepts("format").withRequiredArg().ofType(String.class);
        OptionSpec optimization = parser.accepts("date-range-optimization");
        OptionSpec annotations = parser.accepts("annotations");
        OptionSpec stdout = parser.accepts("stdout");

        OptionSet options = parser.parse(args);
        File infile = input.value(options);
        Format outputFormat = options.has(format) ? Format.valueOf(format.value(options).toUpperCase()) : Format.XML;
        PrintWriter pw;
        if (options.has(stdout)) {
            pw = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
        } else {
            File outfile;
            if (! options.has(output) || output.value(options).isDirectory()) {
                // Use input filename with ".xml", ".json", or ".coffee" extension
                String name = infile.getName();
                if (name.lastIndexOf('.') != -1) {
                    name = name.substring(0, name.lastIndexOf('.'));
                }
                switch (outputFormat) {
                    case JSON:
                        name += ".json";
                        break;
                    case COFFEE:
                        name += ".coffee";
                        break;
                    case XML:
                    default:
                        name += ".xml";
                        break;

                }
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

        writeELM(infile, pw, outputFormat, options.has(optimization), options.has(annotations));
    }
}
