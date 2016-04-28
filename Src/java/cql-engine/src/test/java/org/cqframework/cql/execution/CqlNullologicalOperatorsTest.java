package org.cqframework.cql.execution;

import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlNullologicalOperatorsTest {
    @Test
    public void testIsNull() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(getClass().getResourceAsStream("NullologicalOperators.xml"), Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsNullTrue").getExpression().evaluate(context);
        assertThat(result, is(true));
    }
}

//    define IsNullTrue                   : is null null
//        define IsNullFalseEmptyStrin        : is null ''
//        define IsNullAlsoFalseAbcString     : is null 'abc'
//        define IsNullAlsoFalseNumber1       : is null 1
//        define IsNullAlsoFalseNumberZero    : is null 0
//        define IsNullAlsoFalseNumberSpace   : is null
