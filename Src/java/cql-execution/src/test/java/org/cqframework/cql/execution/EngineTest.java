package org.cqframework.cql.execution;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class EngineTest {

    @Test
    public void testEngineWithAgeFile() throws UnsupportedEncodingException {
        // Configure the engine with test data
        Engine.setPatientSource(new TestPatientSource());

        // Run the sample age script
        File file = new File(URLDecoder.decode(EngineTest.class.getResource("age.cql").getFile(),"UTF-8"));
        assertTrue(file.exists(), "age.cql must exist for test to run correctly. ("+ file.toString() + ")");

        Results results = null;
        try {
            results = Engine.executeCql(file);
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
        assertThat(results, is (notNullValue()));

        // Parse the results
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree((String)results.results.get(0));
            JsonNode patientResults = node.get("patientResults");
            assertThat ( patientResults.size(), is (TestPatientSource.MAX_PATIENTS));
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test(enabled=false)
    public void testEngineWithCMS146File() {
        // Configure the engine with test data
        Engine.setPatientSource(new TestPatientSource());
        Engine.setCodeService(new TestCodeService());

        // Run the sample age script
        File file = new File(EngineTest.class.getResource("CMS146v2_Test_CQM.cql").getFile());
        Results results = null;
        try {
            results = Engine.executeCql(file);
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
        assertThat(results, is (notNullValue()));
        
        // Parse the results
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree((String)results.results.get(0));
            JsonNode patientResults = node.get("patientResults");
            assertThat ( patientResults.size(), is (TestPatientSource.MAX_PATIENTS));
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test(enabled = false)
    public void testEngineWithCql() {
        // TODO this needs to be fixed
        String cql = null;
        Results results = null;
        try {
            results = Engine.executeCql(cql);
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
        assertThat(results, is (notNullValue()));
    }

    @Test
    public void testEngineWithJson() {
        StringBuilder javascript = new StringBuilder();
        javascript.append("importPackage(org.cqframework.cql.execution);");
        javascript.append("\nvar source = Engine.getPatientSource();");
        javascript.append("\nvar patient = null;");
        javascript.append("\nwhile( (patient = source.shift()) != null) {");
        javascript.append("\n  Engine.add( JSON.stringify(patient) );");
        javascript.append("\n}");
        javascript.append("\n");

        String json = javascript.toString();
        Results results = null;
        try {
            Engine.setPatientSource(new TestPatientSource());
            results = Engine.executeJson(json);
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
        assertThat(results, is (notNullValue()));
        assertThat(results.results.size(), is (TestPatientSource.MAX_PATIENTS));
    }
}
