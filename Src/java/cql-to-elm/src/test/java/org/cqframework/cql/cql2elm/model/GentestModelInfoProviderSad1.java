package org.cqframework.cql.cql2elm.model;

import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;
import java.io.InputStream;

public class GentestModelInfoProviderSad1 implements ModelInfoProvider {
    @Override
    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (modelIdentifier.equals("GENTEST")) {
            try { 
                InputStream is = GentestModelInfoProviderSad1.class.getResourceAsStream("/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-sad1.xml");
                return ModelInfoReaderFactory.getReader("application/xml").read(is);
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the GENTEST model.", localVersion));
            }
        }

        return null;
    }
}
