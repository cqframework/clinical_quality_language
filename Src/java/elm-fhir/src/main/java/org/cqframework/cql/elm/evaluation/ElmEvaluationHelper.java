package org.cqframework.cql.elm.evaluation;

import java.time.ZonedDateTime;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Library;
import org.opencds.cqf.cql.engine.execution.*;

public class ElmEvaluationHelper {

    public static Object evaluate(
            Library library, Expression value, Map<String, Object> parameters, ZonedDateTime evaluationDateTime) {
        // TODO: Cache for libraries?

        CqlEngine engine = getEngine(library, parameters, evaluationDateTime);
        return engine.getEvaluationVisitor().visitExpression(value, engine.getState());
    }

    private static CqlEngine getEngine(
            Library library, Map<String, Object> parameters, ZonedDateTime evaluationDateTime) {
        Environment environment = new Environment(getLibraryManager());
        CqlEngine engine = new CqlEngine(environment);
        if (evaluationDateTime != null) {
            engine.getState().setEvaluationDateTime(evaluationDateTime);
        }
        engine.getState().setParameters(library, parameters);
        return engine;
    }

    protected static LibraryManager getLibraryManager() {
        LibraryManager libraryManager = new LibraryManager(getModelManager());
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider());
        return libraryManager;
    }

    protected static ModelManager getModelManager() {
        return new ModelManager();
    }
}
