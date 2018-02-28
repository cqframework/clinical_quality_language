package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.cql.model.ListType;
import org.hl7.cql.model.TupleType;
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

public class QueryTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(QueryTest.class.getResourceAsStream("../OperatorTests/Query.cql"), modelManager, new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testSingularSource() {
        ExpressionDef def = defs.get("Singular Source");
        assertThat(def, hasTypeAndResult(Tuple.class, "tuple{id:System.Integer,name:System.String}"));
    }

    @Test
    public void testPluralSource() {
        ExpressionDef def = defs.get("Plural Source");
        assertThat(def, hasTypeAndResult(List.class, "list<tuple{id:System.Integer,name:System.String}>"));
    }

    @Test
    public void testSingularSourceQuery() {
        ExpressionDef def = defs.get("Singular Source Query");
        assertThat(def, hasTypeAndResult(Query.class, "tuple{id:System.Integer,name:System.String}"));
    }

    @Test
    public void testSingularMultipleSourceQuery() {
        ExpressionDef def = defs.get("Singular Multiple Source Query");
        assertThat(def, hasTypeAndResult(Query.class, "tuple{S1:tuple{id:System.Integer,name:System.String},S2:tuple{id:System.Integer,name:System.String}}"));
    }

    @Test
    public void testPluralMultipleSourceQuery() {
        ExpressionDef def = defs.get("Plural Multiple Source Query");
        assertThat(def, hasTypeAndResult(Query.class, "list<tuple{P1:tuple{id:System.Integer,name:System.String},P2:tuple{id:System.Integer,name:System.String}}>"));
    }

    @Test
    public void testMixedMultipleSourceQuery() {
        ExpressionDef def = defs.get("Mixed Multiple Source Query");
        assertThat(def, hasTypeAndResult(Query.class, "list<tuple{S:tuple{id:System.Integer,name:System.String},P:tuple{id:System.Integer,name:System.String}}>"));
    }

    @Test
    public void testMixedMultipleSourceQuery2() {
        ExpressionDef def = defs.get("Mixed Multiple Source Query 2");
        assertThat(def, hasTypeAndResult(Query.class, "list<tuple{P:tuple{id:System.Integer,name:System.String},S:tuple{id:System.Integer,name:System.String}}>"));
    }

    @Test
    public void testMixedMultipleSourceQueryWithReturn() {
        ExpressionDef def = defs.get("Mixed Multiple Source Query With Return");
        assertThat(def, hasTypeAndResult(Query.class, "list<tuple{id:System.Integer,name:System.String}>"));
    }

    @Test
    public void testSingularSourceWithPluralRelationship() {
        ExpressionDef def = defs.get("Singular Source With Plural Relationship");
        assertThat(def, hasTypeAndResult(Query.class, "tuple{id:System.Integer,name:System.String}"));
    }

    @Test
    public void testPluralSourceWithSingularRelationship() {
        ExpressionDef def = defs.get("Plural Source With Singular Relationship");
        assertThat(def, hasTypeAndResult(Query.class, "list<tuple{id:System.Integer,name:System.String}>"));
    }

    @Test
    public void testSingularSourceWithPluralRelationshipAndReturn() {
        ExpressionDef def = defs.get("Singular Source With Plural Relationship And Return");
        assertThat(def, hasTypeAndResult(Query.class, "tuple{id:System.Integer,name:System.String}"));
    }
}
