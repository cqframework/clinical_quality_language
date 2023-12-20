package org.cqframework.cql.cql2elm;

import java.io.IOException;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

/**
 * Created by Bryn on 12/11/2016.
 */
public class TestFhirModelInfoProvider implements ModelInfoProvider {
    private Class clazz;

    public TestFhirModelInfoProvider(Class clazz) {
        this.clazz = clazz;
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("FHIR")) {
            try {
                return ModelInfoReaderFactory.getReader("application/xml")
                        .read(clazz.getResourceAsStream("fhir-modelinfo-1.8.xml"));
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the QDM model.",
                // localVersion));
            }
        }

        return null;
    }
}
