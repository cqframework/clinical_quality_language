package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.cql2elm.ModelInfoProvider;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.IOException;
import java.io.InputStream;

public class GentestModelInfoProvider implements ModelInfoProvider {
    @Override
    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("GENTEST")) {
            try {
                InputStream is = GentestModelInfoProvider.class.getResourceAsStream("/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-happy.xml");
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
