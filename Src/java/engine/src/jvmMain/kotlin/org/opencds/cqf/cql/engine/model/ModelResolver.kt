package org.opencds.cqf.cql.engine.model

import kotlin.reflect.KClass

/**
 * Provides support for mapping a logical model (e.g. QDM or FHIR) onto a Java implementation of
 * that model. Different implementations of the same model might map to different implementation
 * schemes with the simplest example being classes in different package names, but also possibly
 * with different property naming schemes, etc.
 */
interface ModelResolver : BaseModelResolver {
    /**
     * Resolve the Java class that corresponds to the given model type
     *
     * @param typeName Model type name. In the ELM, model objects are namespaced (e.g.
     *   FHIR.Patient), but the namespace is removed prior to calling this method, so the input
     *   would just be Patient.
     * @return Class object that represents the specified model type
     */
    fun resolveType(typeName: String?): Class<*>?

    override fun resolveKType(typeName: String?): KClass<*>? {
        val javaType = resolveType(typeName)
        return javaType?.kotlin
    }

    /**
     * Resolve the Java class that corresponds to the given model object instance.
     *
     * @param value Object instance
     * @return Class object that represents the specified value
     */
    fun resolveType(value: Any?): Class<*>?

    override fun resolveKType(value: Any?): KClass<*>? {
        val javaType = resolveType(value)
        return javaType?.kotlin
    }

    /**
     * Check whether or not a specified value instance is of the specified type.
     *
     * @param value
     * @param type
     * @return true when the value is of the specified type, otherwise false.
     */
    fun `is`(value: Any?, type: Class<*>?): Boolean?

    override fun `is`(value: Any?, type: KClass<*>?): Boolean? {
        val javaType = type?.java
        return `is`(value, javaType)
    }

    /**
     * Cast the specified value to the specified type. When type conversion is not possible, null
     * should be returned unless the isStrict flag is set to true wherein an Exception will be
     * thrown.
     *
     * @param value model object instance
     * @param type type to which the value should be case
     * @param isStrict flag indicating how to handle invalid type conversion
     * @return the result of the value conversion or null if conversion is not possible.
     */
    fun `as`(value: Any?, type: Class<*>?, isStrict: Boolean): Any?

    override fun `as`(value: Any?, type: KClass<*>?, isStrict: Boolean): Any? {
        val javaType = type?.java
        return `as`(value, javaType, isStrict)
    }
}
