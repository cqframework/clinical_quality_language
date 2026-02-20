package org.opencds.cqf.cql.engine.model

import org.opencds.cqf.cql.engine.exception.InvalidCast
import org.opencds.cqf.cql.engine.util.JavaClass
import org.opencds.cqf.cql.engine.util.javaClassName

abstract class BaseModelResolver : ModelResolver {
    override fun `is`(value: Any?, type: JavaClass<*>?): Boolean? {
        if (value == null) {
            return null
        }

        return type!!.isInstance(value)
    }

    override fun `as`(value: Any?, type: JavaClass<*>?, isStrict: Boolean): Any? {
        if (value == null) {
            return null
        }

        if (type!!.isInstance(value)) {
            return value
        }

        if (isStrict) {
            throw InvalidCast(
                "Cannot cast a value of type ${value.javaClassName} as ${type.getName()}."
            )
        }

        return null
    }
}
