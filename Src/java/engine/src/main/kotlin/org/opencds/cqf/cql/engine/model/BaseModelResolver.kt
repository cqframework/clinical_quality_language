package org.opencds.cqf.cql.engine.model

import org.opencds.cqf.cql.engine.exception.InvalidCast

abstract class BaseModelResolver : ModelResolver {
    override fun `is`(value: Any?, type: Class<*>?): Boolean? {
        if (value == null) {
            return null
        }

        return type!!.isAssignableFrom(value.javaClass)
    }

    override fun `as`(value: Any?, type: Class<*>?, isStrict: Boolean): Any? {
        if (value == null) {
            return null
        }

        if (type!!.isAssignableFrom(value.javaClass)) {
            return value
        }

        if (isStrict) {
            throw InvalidCast(
                "Cannot cast a value of type ${value.javaClass.getName()} as ${type.getName()}."
            )
        }

        return null
    }
}
