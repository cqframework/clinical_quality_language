package org.hl7.cql.ast.analysis

import org.hl7.cql.ast.Identifier
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.Locator
import org.hl7.cql.ast.Problem

interface AstAnalysis {
    fun reset() {}

    fun beginLibrary(library: Library) {}

    fun endLibrary(library: Library) {}

    fun enterScope() {}

    fun exitScope() {}

    fun onDeclaration(identifier: Identifier, locator: Locator, previous: Locator?) {}

    fun collectProblems(): List<Problem>
}
