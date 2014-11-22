package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
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
import java.util.Arrays;
import java.util.List;

public class CqlTranslator {
    public static enum Options { EnableDateRangeOptimization, EnableAnnotations }
    private Library library = null;
    private Object visitResult = null;
    private List<Retrieve> retrieves = null;

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

    //public JsonNode toJsonNode() throws JAXBException, IOException {
    //    return new ObjectMapper().readTree(convertToJSON(library));
    //}

    public Library toELM() {
        return library;
    }

    public Object toObject() {
        return visitResult;
    }

    public List<Retrieve> toRetrieves() {
        return retrieves;
    }

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

    public static void main(String[] args) throws IOException, JAXBException {
        String inputFile = null;
        if (args.length > 0) {
            inputFile = args[0];
        }

        InputStream is = System.in;
        if (inputFile != null) {
            is = new FileInputStream(inputFile);
        }

        CqlTranslator translator = CqlTranslator.fromStream(is, Options.EnableAnnotations);
        System.out.println(translator.toXml());
    }
}
