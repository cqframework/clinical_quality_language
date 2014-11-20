package org.cqframework.cql.execution;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EngineTest {
	
	
	public void testEngineWithFile() {
		// TODO this needs to be fixed
        File file = new File(EngineTest.class.getResource("CMS146v2_Test_CQM.cql").getFile());
		Results results = null;
		try {
			results = Engine.executeCql(file);
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		Assert.assertNull(results);
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
		String json = null;
		Results results = null;
		try {
			results = Engine.executeJson(json);
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
		Assert.assertNull(results);
	}
}
