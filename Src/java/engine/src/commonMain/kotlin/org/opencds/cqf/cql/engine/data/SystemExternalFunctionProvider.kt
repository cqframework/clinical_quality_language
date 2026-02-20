package org.opencds.cqf.cql.engine.data

class SystemExternalFunctionProvider(private val staticFunctions: List<StaticFunction>) :
    ExternalFunctionProvider {
    // TODO: Support adding more functions to an existing provider object.
    override fun evaluate(staticFunctionName: String?, arguments: MutableList<Any?>?): Any? {
        for (staticFunction in staticFunctions) {
            if (staticFunction.name == staticFunctionName) {
                try {
                    return staticFunction.function(arguments)
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
