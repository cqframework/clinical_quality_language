package org.cqframework.cql.cql2elm.operators;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SortingTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromSource(
                buffered(asSource(QueryTest.class.getResourceAsStream("../OperatorTests/Sorting.cql"))), new LibraryManager(modelManager));

        // The alias test creates an error
        assertThat(translator.getErrors().size(), is(1));

        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    void simpleSort() {
        Query query = (Query) defs.get("TestSimpleSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.DESC));
    }

    @Test
    void descendingSort() {
        Query query = (Query) defs.get("TestDescendingSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.DESCENDING));
    }

    @Test
    void ascSort() {
        Query query = (Query) defs.get("TestAscSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.ASC));
    }

    @Test
    void ascendingSort() {
        Query query = (Query) defs.get("TestAscendingSort").getExpression();
        SortClause sort = query.getSort();
        assertThat(sort.getBy().size(), is(1));
        assertThat(sort.getBy().get(0).getDirection(), is(SortDirection.ASCENDING));
    }
}
