package org.cqframework.cql.cql2elm

/**
 * A helper function for enabling a compiler option (useful for JS environments).
 *
 * @param option the compiler option to add
 */
fun BaseLibraryManager.addCompilerOptionInner(option: String) {
    this.cqlCompilerOptions.options.add(CqlCompilerOptions.Options.valueOf(option))
}

/**
 * A helper function for disabling a compiler option (useful for JS environments).
 *
 * @param option the compiler option to remove
 */
fun BaseLibraryManager.removeCompilerOptionInner(option: String) {
    this.cqlCompilerOptions.options.remove(CqlCompilerOptions.Options.valueOf(option))
}
