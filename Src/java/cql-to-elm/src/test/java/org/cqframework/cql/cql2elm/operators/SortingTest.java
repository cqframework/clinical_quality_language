package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SortingTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(QueryTest.class.getResourceAsStream("../OperatorTests/Sorting.cql"), new LibraryManager(modelManager));

        // The alias test creates an error
        assertThat(translator.getErrors().size(), is(1));

        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testSimpleSort() {
        Query query = (Query)defs.get("TestSimpleSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.DESC));
    }

    @Test
    public void testDescendingSort() {
        Query query = (Query)defs.get("TestDescendingSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.DESCENDING));
    }

    @Test
    public void testAscSort() {
        Query query = (Query)defs.get("TestAscSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.ASC));
    }

    @Test
    public void testAscendingSort() {
        Query query = (Query)defs.get("TestAscendingSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.ASCENDING));
    }
}