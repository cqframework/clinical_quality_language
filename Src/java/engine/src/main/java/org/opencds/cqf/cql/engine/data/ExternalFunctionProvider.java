package org.opencds.cqf.cql.engine.data;

import java.util.List;

public interface ExternalFunctionProvider {
    Object evaluate(String staticFunctionName, List<Object> arguments);
}
