package org.cqframework.cql.elm.serializing.xmlutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
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
        var library = reader.read(xml);
        assertEquals(xml, writer.writeAsString(library));
    }
}
