package org.cqframework.cql.elm.serializing;

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
            "OperatorTests/Aggregate.cql",
            "SignatureTests/GenericOverloadsTests.cql",
        };
    }

    private static String pathForFile(String cqlFile) {
        return "../cql-to-elm/src/jvmTest/resources/org/cqframework/cql/cql2elm/" + cqlFile;
    }

    private static final ElmXmlLibraryReader xmlReader = new ElmXmlLibraryReader();
    private static final ElmXmlLibraryWriter xmlWriter = new ElmXmlLibraryWriter();
    private static final ElmJsonLibraryReader jsonReader = new ElmJsonLibraryReader();
    private static final ElmJsonLibraryWriter jsonWriter = new ElmJsonLibraryWriter();

    @ParameterizedTest
    @MethodSource("dataMethod")
    void roundTrip(String cqlFile) throws IOException {
        var translator = TestUtils.createTranslator(pathForFile(cqlFile));
        assertEquals(0, translator.getErrors().size());

        var xml = translator.toXml();
        var library = xmlReader.read(xml);
        assertEquals(xml, xmlWriter.writeAsString(library));

        var json = translator.toJson();
        library = jsonReader.read(json);
        assertEquals(json, jsonWriter.writeAsString(library));
    }
}
