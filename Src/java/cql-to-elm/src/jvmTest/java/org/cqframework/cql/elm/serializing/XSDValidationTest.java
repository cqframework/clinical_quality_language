package org.cqframework.cql.elm.serializing;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

class XSDValidationTest {
    private static final Logger logger = LoggerFactory.getLogger(XSDValidationTest.class);

    public static String[] dataMethod() {
        return new String[] {
            "OperatorTests/ArithmeticOperators.cql",
            "OperatorTests/ComparisonOperators.cql",
            "OperatorTests/ListOperators.cql",
        };
    }

    private static final Validator validator = createValidator();

    private static Validator createValidator() {
        try {
            var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = schemaFactory.newSchema(
                    Path.of("../../cql-lm/schema/elm/library.xsd").toFile());
            return schema.newValidator();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static String pathForFile(String cqlFile) {
        return "../cql-to-elm/src/jvmTest/resources/org/cqframework/cql/cql2elm/" + cqlFile;
    }

    private static boolean validateXMLAgainstXSD(String cqlFile, String xml) {
        try {
            validator.validate(new StreamSource(new StringReader(xml)));
            return true;
        } catch (IOException | SAXException e) {
            logger.error("error validating XML against XSD for file {}", cqlFile, e);
        }
        return false;
    }

    @Test
    void ensureValidatorFailsForBadXml() {
        var xml = "<library xmlns=\"urn:hl7-org:elm:r1\"></library>";
        var source = new StreamSource(new StringReader(xml));
        assertThrows(SAXException.class, () -> validator.validate(source));
    }

    @ParameterizedTest
    @MethodSource("dataMethod")
    void validateXML(String cqlFile) throws IOException {
        var path = pathForFile(cqlFile);
        var t = TestUtils.createTranslator(path);
        assertTrue(t.getErrors().isEmpty());

        var xml = t.convertToXml(t.toELM());
        assertTrue(validateXMLAgainstXSD(cqlFile, xml));
    }
}
