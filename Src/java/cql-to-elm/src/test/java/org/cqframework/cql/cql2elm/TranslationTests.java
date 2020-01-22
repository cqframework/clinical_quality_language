package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.elm.r1.As;
import org.hl7.elm.r1.Case;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.Property;
import org.hl7.elm.r1.Query;
import org.hl7.elm.r1.ReturnClause;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TranslationTests {
    // TODO: sameXMLAs? Couldn't find such a thing in hamcrest, but I don't want this to run on the JSON, I want it to verify the actual XML.
    @Test(enabled=false)
    public void testPatientPropertyAccess() throws IOException, JAXBException {
        File expectedXmlFile = new File(Cql2ElmVisitorTest.class.getResource("PropertyTest_ELM.xml").getFile());
        String expectedXml = new Scanner(expectedXmlFile, "UTF-8").useDelimiter("\\Z").next();

        File propertyTestFile = new File(Cql2ElmVisitorTest.class.getResource("PropertyTest.cql").getFile());
        ModelManager modelManager = new ModelManager();
        String actualXml = CqlTranslator.fromFile(propertyTestFile, modelManager, new LibraryManager(modelManager)).toXml();
        assertThat(actualXml, is(expectedXml));
    }

    @Test(enabled=false)
    public void testCMS146v2XML() throws IOException {
        String expectedXml = "";
        File cqlFile = new File(Cql2ElmVisitorTest.class.getResource("CMS146v2_Test_CQM.cql").getFile());
        ModelManager modelManager = new ModelManager();
        String actualXml = CqlTranslator.fromFile(cqlFile, modelManager, new LibraryManager(modelManager)).toXml();
        assertThat(actualXml, is(expectedXml));
    }

    @Test
    public void testIdentifierLocation() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("TranslatorTests/UnknownIdentifier.cql");
        assertEquals(1, translator.getErrors().size());

        CqlTranslatorException e = translator.getErrors().get(0);
        TrackBack tb = e.getLocator();

        assertEquals(6, tb.getStartLine());
        assertEquals(6, tb.getEndLine());

        assertEquals(5, tb.getStartChar());
        assertEquals(10, tb.getEndChar());
    }

    @Test
    public void testAnnotationsPresent() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("CMS146v2_Test_CQM.cql", CqlTranslator.Options.EnableAnnotations);
        assertEquals(0, translator.getErrors().size());
        List<ExpressionDef> defs = translator.getTranslatedLibrary().getLibrary().getStatements().getDef();
        assertNotNull(defs.get(1).getAnnotation());
        assertTrue(defs.get(1).getAnnotation().size() > 0);
    }

    @Test
    public void testAnnotationsAbsent() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("CMS146v2_Test_CQM.cql");
        assertEquals(0, translator.getErrors().size());
        List<ExpressionDef> defs = translator.getTranslatedLibrary().getLibrary().getStatements().getDef();
        assertTrue(defs.get(1).getAnnotation().size() == 0);
    }

    @Test
    public void testNoImplicitCasts() throws IOException {
        CqlTranslator translator = TestUtils.createTranslator("TestNoImplicitCast.cql");
        assertEquals(0, translator.getErrors().size());
        // Gets the "TooManyCasts" define
        Expression exp = translator.getTranslatedLibrary().getLibrary().getStatements().getDef().get(2).getExpression();
        assertThat(exp, is(instanceOf(Query.class)));

        Query query = (Query)exp;
        ReturnClause returnClause = query.getReturn();
        assertNotNull(returnClause);
        assertNotNull(returnClause.getExpression());
        assertThat(returnClause.getExpression(), is(instanceOf(FunctionRef.class)));

        FunctionRef functionRef = (FunctionRef)returnClause.getExpression();
        assertEquals(1, functionRef.getOperand().size());

        // The crux of the issue is that choice types that are compatible shouldn't create any conversion logic
        // It should be a direct property access.
        Expression operand = functionRef.getOperand().get(0);
        assertThat(operand, is(instanceOf(Property.class)));
    }
}
