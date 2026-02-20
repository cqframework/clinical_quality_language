package org.opencds.cqf.cql.engine.data

// import java.lang.reflect.InvocationTargetException
// import java.lang.reflect.Method

class SystemExternalFunctionProvider(
    //    private val staticFunctions: List<Method>
    private val staticFunctions: List<StaticFunction>
) : ExternalFunctionProvider {
    // TODO: Support adding more functions to an existing provider object.
    override fun evaluate(staticFunctionName: String?, arguments: MutableList<Any?>?): Any? {
        for (staticFunction in staticFunctions) {
            if (staticFunction.name == staticFunctionName) {
                try {
                    return staticFunction.function(arguments)

                    //                    return staticFunction.invoke(
                    //                        staticFunction.getDeclaringClass(),
                    //                        *arguments!!.toTypedArray(),
                    //                    )
                    //                } catch (e: InvocationTargetException) {
                    //                    throw IllegalArgumentException(
                    //                        "Unable to invoke function [" + staticFunctionName +
                    // "]: " + e.message
                    //                    )
                    //                } catch (e: IllegalAccessException) {
                    //                    throw IllegalArgumentException(
                    //                        "Unable to invoke function [" + staticFunctionName +
                    // "]: " + e.message
                    //                    )
                } catch (e: Exception) {
                    throw RuntimeException(
                        "Error when executing function [" +
                            staticFunctionName +
                            "]: \n" +
                            e.toString()
                    )
                }
            }
        }
        throw IllegalArgumentException("Unable to find function [" + staticFunctionName + "].")
    }
}

data class StaticFunction(val name: String, val function: (MutableList<Any?>?) -> Any?)
