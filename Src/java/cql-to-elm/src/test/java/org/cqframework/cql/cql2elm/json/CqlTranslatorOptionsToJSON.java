package org.cqframework.cql.cql2elm.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.reinert.jjschema.v1.JsonSchemaFactory;
import com.github.reinert.jjschema.v1.JsonSchemaV4Factory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class CqlTranslatorOptionsToJSON {
    private static final String separator = System.getProperty("file.separator");
    private static final String JSON_LOC = "src" + separator + "test" + separator +
            "resources" + separator + "org" + separator + "cqframework" + separator + "cql" + separator + "cql2elm" +
            separator + "json" + separator + "CqlTranslatorOptions.json";
    

    @Test
    public void CqlTranslatorOptionsToJsonSchema() {

        //delete file if exists:
        try {
            File jsonFile = new File(JSON_LOC);
            if (jsonFile.exists()) {
                jsonFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //verify output is valid:
        try {
            String expectedJson = "{\"type\":\"object\",\"description\":\"Translation options for Cql source files\",\"title\":\"CqlTranslatorOptions\",\"properties\":{\"analyzeDataRequirements\":{\"type\":\"boolean\"},\"collapseDataRequirements\":{\"type\":\"boolean\"},\"compatibilityLevel\":{\"type\":\"string\",\"description\":\"Indicates the compatibility level of this process.\"},\"errorLevel\":{\"enum\":[\"Info\",\"Warning\",\"Error\"],\"type\":\"string\"},\"formats\":{\"type\":\"array\",\"items\":{\"enum\":[\"XML\",\"JSON\",\"JXSON\",\"COFFEE\"],\"type\":\"string\"},\"description\":\"List of CqlTranslator.Format\",\"uniqueItems\":true,\"minItems\":1},\"options\":{\"type\":\"array\",\"items\":{\"enum\":[\"EnableDateRangeOptimization\",\"EnableAnnotations\",\"EnableLocators\",\"EnableResultTypes\",\"EnableDetailedErrors\",\"DisableListTraversal\",\"DisableListDemotion\",\"DisableListPromotion\",\"EnableIntervalDemotion\",\"EnableIntervalPromotion\",\"DisableMethodInvocation\",\"RequireFromKeyword\"],\"type\":\"string\"},\"description\":\"EnumSet of CqlTranslator.Options\"},\"signatureLevel\":{\"enum\":[\"None\",\"Differing\",\"Overloads\",\"All\"],\"type\":\"string\"},\"validateUnits\":{\"type\":\"boolean\",\"description\":\"Indicates units will be validated\"},\"verifyOnly\":{\"type\":\"boolean\",\"description\":\"Indicates override of operations and verification alone will process\"}},\"required\":[\"compatibilityLevel\",\"options\",\"validateUnits\",\"verifyOnly\"],\"$schema\":\"http://json-schema.org/draft-04/schema#\"}";
            JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();
            schemaFactory.setAutoPutDollarSchema(true);
            BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_LOC));
            JsonNode jNode = schemaFactory.createSchema(org.cqframework.cql.cql2elm.CqlTranslatorOptions.class);
            Assert.assertEquals(expectedJson, jNode.toString());
            writer.write(jNode.toPrettyString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
