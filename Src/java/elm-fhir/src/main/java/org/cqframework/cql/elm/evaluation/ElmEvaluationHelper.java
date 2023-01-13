package org.cqframework.cql.elm.evaluation;

import org.cqframework.cql.elm.LibraryMapper;
import org.cqframework.cql.elm.execution.IncludeDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Expression;
import org.opencds.cqf.cql.engine.execution.*;

import java.time.ZonedDateTime;
import java.util.Map;

public class ElmEvaluationHelper {

    // TODO: Improved library loader support...
    private static LibraryLoader libraryLoader = new DefaultLibraryLoader();

    public static Object evaluate(Library library, Expression value, Map<String, Object> parameters, ZonedDateTime evaluationDateTime) {
        // TODO: Cache for libraries?
        org.cqframework.cql.elm.execution.Library engineLibrary = LibraryMapper.INSTANCE.map(library);
        org.cqframework.cql.elm.execution.Expression engineValue = LibraryMapper.INSTANCE.map(value);

        Object result = engineValue.evaluate(getContext(engineLibrary, parameters, evaluationDateTime));
        return result;
    }

    private static Context getContext(org.cqframework.cql.elm.execution.Library library, Map<String, Object> parameters, ZonedDateTime evaluationDateTime) {
        Context context = evaluationDateTime == null ? new Context(library) : new Context(library, evaluationDateTime);
        context.setParameters(library, parameters);
        return context;
    }
}
