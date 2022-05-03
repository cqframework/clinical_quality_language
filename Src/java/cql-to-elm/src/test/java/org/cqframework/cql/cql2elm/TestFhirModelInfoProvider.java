package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.io.IOException;

/**
 * Created by Bryn on 12/11/2016.
 */
public class TestFhirModelInfoProvider implements ModelInfoProvider {
    private Class clazz;

    public TestFhirModelInfoProvider(Class clazz) {
        this.clazz = clazz;
    }

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("FHIR")) {
            try {
                return JacksonXML.readValue(clazz.getResourceAsStream("fhir-modelinfo-1.8.xml"),
                ModelInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the QDM model.", localVersion));
            }

        }

        return null;
    }
}
