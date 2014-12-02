package org.cqframework.cql.execution;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EngineTest {
	
	@Test
	public void testEngineWithFile_Age() {
		// Configure the engine with test data
		Engine.setPatientSource(new TestPatientSource());
		
		// Run the sample age script
        File file = new File(EngineTest.class.getResource("age.cql").getFile());
		Results results = null;
		try {
			results = Engine.executeCql(file);
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		Assert.assertNotNull( results );
				
		// Parse the results
		ObjectMapper mapper = new ObjectMapper();		
		try {
			JsonNode node = mapper.readTree((String)results.results.get(0));
			JsonNode patientResults = node.get("patientResults");
			Assert.assertEquals( patientResults.size(), TestPatientSource.maxPatients);		
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
		Assert.assertTrue(true);
	}
	
	@Test(enabled=false)
	public void testEngineWithFile_CMS146() {
		// Configure the engine with test data
		Engine.setPatientSource(new TestPatientSource());
		
		// Run the sample age script
        File file = new File(EngineTest.class.getResource("CMS146v2_Test_CQM.cql").getFile());
		Results results = null;
		try {
			results = Engine.executeCql(file);
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		Assert.assertNotNull( results );
		
		// Parse the results
		ObjectMapper mapper = new ObjectMapper();		
		try {
			JsonNode node = mapper.readTree((String)results.results.get(0));
			JsonNode patientResults = node.get("patientResults");
			Assert.assertEquals( patientResults.size(), TestPatientSource.maxPatients);		
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
		Assert.assertTrue(true);
	}
	
	public void testEngineWithCql() {
		// TODO this needs to be fixed
		String cql = null;
		Results results = null;
		try {
			results = Engine.executeCql(cql);
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		Assert.assertNull(results);
	}
	
	public void testEngineWithJson() {
		// TODO this needs to be fixed
		
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
			results = Engine.executeJson(json);
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		Assert.assertNull(results);
	}
}
