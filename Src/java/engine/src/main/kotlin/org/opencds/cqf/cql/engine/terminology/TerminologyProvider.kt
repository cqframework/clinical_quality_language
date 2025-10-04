package org.opencds.cqf.cql.engine.terminology

import org.opencds.cqf.cql.engine.runtime.Code

interface TerminologyProvider {
    /**
     * Checks if a given Code is a member of a given ValueSetInfo
     *
     * @param code the code to check
     * @param valueSet the valueSet to check
     * @return true if code is a member of the ValueSet
     */
    fun `in`(code: Code, valueSet: ValueSetInfo): Boolean

    /**
     * Expands the set of Codes for a given ValueSetInfo
     *
     * @param valueSet the ValueSetInfo to expand
     * @return the set of Codes
     */
    fun expand(valueSet: ValueSetInfo): Iterable<Code>

    /**
     * Looks up the display value for a given Code from a given CodeSystemInfo
     *
     * @param code the Code to look up
     * @param codeSystem the CodeSystemInfo to look up from
     * @return the Code with the display value filled
     */
    fun lookup(code: Code, codeSystem: CodeSystemInfo): Code?
}
