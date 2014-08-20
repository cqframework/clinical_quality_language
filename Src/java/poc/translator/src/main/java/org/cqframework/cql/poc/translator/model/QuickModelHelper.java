package org.cqframework.cql.poc.translator.model;

import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;
import java.io.File;

/**
 * Created by Bryn on 8/20/2014.
 */
public class QuickModelHelper {
    public static ModelInfo load() {
        return JAXB.unmarshal(new File("quick-modelinfo.xml"), ModelInfo.class);
    }
}
