package org.opencds.cqf.cql.engine.serializing;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;

import org.cqframework.cql.cql2elm.LibraryContentType;
import org.hl7.elm.r1.IntervalTypeSpecifier;
import org.hl7.elm.r1.NamedTypeSpecifier;
import org.testng.annotations.Test;

public class QNameIssueJaxbTest {
    @Test
    void loaderIsAvailable() throws IOException {
        var lib =
            CqlLibraryReaderFactory.getReader(LibraryContentType.XML.mimeType())
                .read(QNameIssueJaxbTest.class.getResourceAsStream("QNameIssue.xml"));

        var actual = ((NamedTypeSpecifier)
            ((IntervalTypeSpecifier) lib.getParameters().getDef().get(0).getParameterTypeSpecifier()).getPointType()
        ).getName();

        assertEquals("{urn:hl7-org:elm-types:r1}DateTime", actual.toString());
    }

}
