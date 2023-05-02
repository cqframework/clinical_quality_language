package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.serializing.jaxb.JsonCqlLibraryReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStreamReader;

public class ANCFHIRTests {
    @Test
    public void testJsonLibraryLoad() {
        try {
            Library library = new JsonCqlLibraryReader().read(new InputStreamReader(ANCFHIRTests.class.getResourceAsStream("ANCFHIRDummy.json")));
            Assert.assertTrue(library != null);
            Assert.assertTrue(library.getStatements() != null);
            Assert.assertTrue(library.getStatements().getDef() != null);
            Assert.assertTrue(library.getStatements().getDef().size() >= 2);
            Assert.assertTrue(library.getStatements().getDef().get(0) instanceof ExpressionDefEvaluator);
            Assert.assertTrue(library.getStatements().getDef().get(0).getExpression() instanceof SingletonFromEvaluator);
            Assert.assertTrue(((SingletonFromEvaluator)library.getStatements().getDef().get(0).getExpression()).getOperand() instanceof RetrieveEvaluator);
            Assert.assertTrue(library.getStatements().getDef().get(1) instanceof ExpressionDefEvaluator);
            Assert.assertTrue(library.getStatements().getDef().get(1).getExpression() instanceof RetrieveEvaluator);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }

    @Test
    public void testJsonTerminologyLibraryLoad() {
        try {
            Library library = new JsonCqlLibraryReader().read(new InputStreamReader(ANCFHIRTests.class.getResourceAsStream("ANCFHIRTerminologyDummy.json")));
            Assert.assertTrue(library != null);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading ELM: " + e.getMessage());
        }
    }
}
