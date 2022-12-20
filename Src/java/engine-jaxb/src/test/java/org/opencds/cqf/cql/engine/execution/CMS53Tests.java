package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.serializing.jaxb.XmlCqlLibraryReader;
import org.testng.annotations.Test;

import java.io.IOException;

public class CMS53Tests {
    @Test
    public void testLibraryLoadXML() {
        try {
            new XmlCqlLibraryReader().read(CMS53Tests.class.getResourceAsStream("CMS53Draft/PrimaryPCIReceivedWithin90MinutesofHospitalArrival-7.0.001.xml"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

}
