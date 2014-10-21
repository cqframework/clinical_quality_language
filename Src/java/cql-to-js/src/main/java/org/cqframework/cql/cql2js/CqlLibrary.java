package org.cqframework.cql.cql2js;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.Cql2ElmVisitor;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class CqlLibrary {
    public static enum Options { EnableDateRangeOptimization, EnableAnnotations }
    private final String json;

    public static CqlLibrary loadCql(String cqlText, Options... options) {
        return loadANTLRInputStream(new ANTLRInputStream(cqlText), options);
    }

    public static CqlLibrary loadCql(File cqlFile, Options... options) throws IOException {
        return loadANTLRInputStream(new ANTLRInputStream(new FileInputStream(cqlFile)), options);
    }

    public static CqlLibrary loadElm(Library library) {
        try {
            return new CqlLibrary(library);
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Couldn't process ELM", e);
        }
    }

    private static CqlLibrary loadANTLRInputStream(ANTLRInputStream is, Options... options) {
        try {
            return new CqlLibrary(is, options);
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Couldn't process CQL", e);
        }
    }

    private CqlLibrary(ANTLRInputStream is, Options... options) throws JAXBException {
        this.json = convertToJSON(is, options);
    }

    private CqlLibrary(Library library, Options... options) throws JAXBException {
        this.json = convertToJSON(library);
    }

    public String asJson() {
        return json;
    }

    public JsonNode asJsonNode() throws IOException {
        return new ObjectMapper().readTree(json);
    }

    private String convertToJSON(ANTLRInputStream is, Options... options) throws JAXBException {
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
        visitor.visit(tree);

        return convertToJSON(visitor.getLibrary());
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
        if (args.length == 0) {
            System.err.println("Must provide file path as argument");
            System.exit(1);
        }

        CqlLibrary library = CqlLibrary.loadCql(new File(args[0]));
        System.out.println(library.asJson());
    }
}
