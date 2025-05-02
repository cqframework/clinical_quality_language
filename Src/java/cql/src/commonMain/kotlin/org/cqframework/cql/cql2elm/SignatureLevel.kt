package org.cqframework.cql.cql2elm

enum class SignatureLevel {
    /** Indicates signatures will always be included in operator invocations */
    None,

    /**
     * Indicates signatures will only be included in invocations if the declared signature of the
     * resolve operator is the same as the invocation signature
     */
    Differing,

    /**
     * Indicates signatures will only be included in invocations if the function has multiple
     * overloads with the same number of arguments as the invocation
     */
    Overloads,

    /** Indicates signatures will always be included in invocations */
    All
}
