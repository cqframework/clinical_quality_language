package org.cqframework.cql.cql2elm.qdm.v55;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class BaseTest {
    @Test
    public void testChoiceTypes() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("qdm/v55/TestChoiceTypes.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        ExpressionDef def = defs.get("TestIntegerChoice");
        assertThat(def.getExpression(), instanceOf(Query.class));
        Query query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(Equal.class));
        Equal equal = (Equal)query.getWhere();
        assertThat(equal.getOperand().get(0), instanceOf(As.class));
        As as = (As)equal.getOperand().get(0);
        assertThat(as.getAsType().getLocalPart(), is("Integer"));

        def = defs.get("TestDecimalChoice");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(Equal.class));
        equal = (Equal)query.getWhere();
        assertThat(equal.getOperand().get(0), instanceOf(As.class));
        as = (As)equal.getOperand().get(0);
        assertThat(as.getAsType().getLocalPart(), is("Decimal"));

        def = defs.get("TestQuantityChoice");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(Equal.class));
        equal = (Equal)query.getWhere();
        assertThat(equal.getOperand().get(0), instanceOf(As.class));
        as = (As)equal.getOperand().get(0);
        assertThat(as.getAsType().getLocalPart(), is("Quantity"));

        def = defs.get("TestRatioChoice");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(Equal.class));
        equal = (Equal)query.getWhere();
        assertThat(equal.getOperand().get(0), instanceOf(As.class));
        as = (As)equal.getOperand().get(0);
        assertThat(as.getAsType().getLocalPart(), is("Ratio"));

        def = defs.get("TestUnionChoices");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(IncludedIn.class));

        IncludedIn includedIn = (IncludedIn)query.getWhere();
        assertThat(includedIn.getOperand().get(0), instanceOf(Property.class));
        Property property = (Property)includedIn.getOperand().get(0);
        assertThat(property.getPath(), is("relevantPeriod"));
    }
}
