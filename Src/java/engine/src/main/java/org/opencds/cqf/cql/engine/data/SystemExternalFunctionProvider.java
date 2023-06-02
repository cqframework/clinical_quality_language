package org.opencds.cqf.cql.engine.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SystemExternalFunctionProvider implements ExternalFunctionProvider {

    private List<Method> staticFunctions;

    public SystemExternalFunctionProvider(List<Method> staticFunctions) {
        this.staticFunctions = staticFunctions;
    }

    // TODO: Support adding more functions to an existing provider object.

    @Override
    public Object evaluate(String staticFunctionName, List<Object> arguments)
    {
        for (Method staticFunction : staticFunctions) {
            if (staticFunction.getName().equals(staticFunctionName)) {
                try {
                    return staticFunction.invoke(staticFunction.getDeclaringClass(), arguments.toArray());
                }
                catch (InvocationTargetException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Unable to invoke function ["+staticFunctionName+"]: " + e.getMessage());
                }
                catch (Exception e) {
                    throw new RuntimeException("Error when executing function ["+staticFunctionName+"]: \n" + e.toString());
                }
            }
        }
        throw new IllegalArgumentException("Unable to find function ["+staticFunctionName+"].");
    }
}
