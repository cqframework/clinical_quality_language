package org.cqframework.cql.cql2elm.json;

import java.io.File;
import org.junit.jupiter.api.Test;

class CqlTranslatorOptionsToJsonSchema {
    private static final String separator = System.getProperty("file.separator");
    private static final String JSON_LOC = "src" + separator + "test" + separator + "resources"
            + separator + "org" + separator + "cqframework" + separator + "cql" + separator + "cql2elm" + separator
            + "json" + separator + "CqlTranslatorOptions.json";

    @Test
    void BuildJsonSchemaFromCqlTranslatorOptions() {
        // delete file if exists:
        try {
            File jsonFile = new File(JSON_LOC);
            if (jsonFile.exists()) {
                jsonFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // verify output is valid:

        // TODO: Jackson is replaced with XmlUtil. Reimplement this using XmlUtil.
        //    try {
        //        JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();
        //        schemaFactory.setAutoPutDollarSchema(true);
        //        BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_LOC));
        //        JsonNode jNode = schemaFactory.createSchema(CqlCompilerOptions.class);
        //        writer.write(jNode.toPrettyString());
        //        writer.close();
        //    } catch (Exception e) {
        //        e.printStackTrace();
        //        fail();
        //    }
    }
}
