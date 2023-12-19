package org.cqframework.cql.cql2elm.model;

import java.io.IOException;
import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

public class GentestModelInfoProvider implements ModelInfoProvider {
    @Override
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("GENTEST")) {
            try {
                InputStream is = GentestModelInfoProvider.class.getResourceAsStream(
                        "/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-happy.xml");
                return ModelInfoReaderFactory.getReader("application/xml").read(is);
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the GENTEST model.",
                // localVersion));
            }
        }

        return null;
    }
}
