package org.hl7.cql.ast

/**
 * An annotation classes that are part of the Intermediate Representation (IR) of CQL. These classes
 * do not correspond directly to constructs in the CQL grammar, but are used to represent the
 * structure and semantics of CQL expressions and statements in a way that is more suitable for
 * analysis, transformation, and code generation.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Ir
