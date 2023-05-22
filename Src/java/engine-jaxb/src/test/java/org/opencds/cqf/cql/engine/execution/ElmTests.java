package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.Library;
import org.opencds.cqf.cql.engine.serializing.jaxb.XmlCqlLibraryReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public class ElmTests {

    /**
     * {@link org.opencds.cqf.cql.engine.elm.visiting.FilterEvaluator}
     */
    @Test
    public void filterTest() throws JAXBException, IOException {
        Library library = new XmlCqlLibraryReader().read(ElmTests.class.getResourceAsStream("ElmTests.xml"));
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("TestFilter").getExpression().evaluate(context);

        Assert.assertTrue(((List<?>) result).size() == 2);
    }

}
