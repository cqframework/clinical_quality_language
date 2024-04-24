package org.cqframework.cql.cql2elm.operators;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
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

class QueryTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(
                QueryTest.class.getResourceAsStream("../OperatorTests/Query.cql"), new LibraryManager(modelManager));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    void singularSource() {
        ExpressionDef def = defs.get("Singular Source");
        assertThat(def, hasTypeAndResult(Tuple.class, "tuple{id:System.Integer,name:System.String}"));
    }

    @Test
    void pluralSource() {
        ExpressionDef def = defs.get("Plural Source");
        assertThat(def, hasTypeAndResult(List.class, "list<tuple{id:System.Integer,name:System.String}>"));
    }

    @Test
    void singularSourceQuery() {
        ExpressionDef def = defs.get("Singular Source Query");
        assertThat(def, hasTypeAndResult(Query.class, "tuple{id:System.Integer,name:System.String}"));
    }

    @Test
    void singularMultipleSourceQuery() {
        ExpressionDef def = defs.get("Singular Multiple Source Query");
        assertThat(
                def,
                hasTypeAndResult(
                        Query.class,
                        "tuple{S1:tuple{id:System.Integer,name:System.String},S2:tuple{id:System.Integer,name:System.String}}"));
    }

    @Test
    void pluralMultipleSourceQuery() {
        ExpressionDef def = defs.get("Plural Multiple Source Query");
        assertThat(
                def,
                hasTypeAndResult(
                        Query.class,
                        "list<tuple{P1:tuple{id:System.Integer,name:System.String},P2:tuple{id:System.Integer,name:System.String}}>"));
    }

    @Test
    void mixedMultipleSourceQuery() {
        ExpressionDef def = defs.get("Mixed Multiple Source Query");
        assertThat(
                def,
                hasTypeAndResult(
                        Query.class,
                        "list<tuple{S:tuple{id:System.Integer,name:System.String},P:tuple{id:System.Integer,name:System.String}}>"));
    }

    @Test
    void mixedMultipleSourceQuery2() {
        ExpressionDef def = defs.get("Mixed Multiple Source Query 2");
        assertThat(
                def,
                hasTypeAndResult(
                        Query.class,
                        "list<tuple{P:tuple{id:System.Integer,name:System.String},S:tuple{id:System.Integer,name:System.String}}>"));
    }

    @Test
    void mixedMultipleSourceQueryWithReturn() {
        ExpressionDef def = defs.get("Mixed Multiple Source Query With Return");
        assertThat(def, hasTypeAndResult(Query.class, "list<tuple{id:System.Integer,name:System.String}>"));
    }

    @Test
    void singularSourceWithPluralRelationship() {
        ExpressionDef def = defs.get("Singular Source With Plural Relationship");
        assertThat(def, hasTypeAndResult(Query.class, "tuple{id:System.Integer,name:System.String}"));
    }

    @Test
    void pluralSourceWithSingularRelationship() {
        ExpressionDef def = defs.get("Plural Source With Singular Relationship");
        assertThat(def, hasTypeAndResult(Query.class, "list<tuple{id:System.Integer,name:System.String}>"));
    }

    @Test
    void singularSourceWithPluralRelationshipAndReturn() {
        ExpressionDef def = defs.get("Singular Source With Plural Relationship And Return");
        assertThat(def, hasTypeAndResult(Query.class, "tuple{id:System.Integer,name:System.String}"));
    }
}
