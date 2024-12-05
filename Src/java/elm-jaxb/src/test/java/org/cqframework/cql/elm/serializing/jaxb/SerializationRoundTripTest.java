package org.cqframework.cql.elm.serializing.jaxb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SerializationRoundTripTest {

    public static String[] dataMethod() {
        return new String[] {
            "OperatorTests/ArithmeticOperators.cql",
            "OperatorTests/ComparisonOperators.cql",
            "OperatorTests/ListOperators.cql",
        };
    }

    private static String pathForFile(String cqlFile) {
        return "../cql-to-elm/src/test/resources/org/cqframework/cql/cql2elm/" + cqlFile;
    }

    private static final ElmXmlLibraryReader reader = new ElmXmlLibraryReader();
    private static final ElmXmlLibraryWriter writer = new ElmXmlLibraryWriter();

    @ParameterizedTest
    @MethodSource("dataMethod")
    void roundTrip(String cqlFile) throws IOException {
        var translator = TestUtils.createTranslator(pathForFile(cqlFile));
        assertEquals(0, translator.getErrors().size());

        var xml = translator.toXml();
        var library = reader.read(new StringReader(xml));
        var stringWriter = new StringWriter();
        writer.write(library, stringWriter);
        assertEquals(xml, stringWriter.toString());
    }
}
