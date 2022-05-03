package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.cql2elm.JacksonXML;
import org.cqframework.cql.cql2elm.ModelInfoProvider;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.io.IOException;
import java.io.InputStream;

public class GentestModelInfoProviderSad1 implements ModelInfoProvider {
    @Override
    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier.equals("GENTEST")) {
            try { 
                InputStream is = GentestModelInfoProviderSad1.class.getResourceAsStream("/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-sad1.xml");
                return JacksonXML.readValue(is, ModelInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the GENTEST model.", localVersion));
            }
        }

        return null;
    }
}
