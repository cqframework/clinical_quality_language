package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.cql2elm.JacksonXML;
import org.cqframework.cql.cql2elm.ModelInfoProvider;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.InputStream;

public class GentestModelInfoProvider implements ModelInfoProvider {
    @Override
    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        if (modelIdentifier.getId().equals("GENTEST")) {
            try {
                InputStream is = GentestModelInfoProvider.class.getResourceAsStream("/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-happy.xml");
                return JacksonXML.readValue(is, ModelInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
                // Do not throw, allow other providers to resolve
                //    throw new IllegalArgumentException(String.format("Unknown version %s of the QDM model.", localVersion));
            }
        }

        return null;
    }
}
