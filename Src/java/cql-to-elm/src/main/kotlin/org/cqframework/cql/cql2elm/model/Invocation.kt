package org.cqframework.cql.cql2elm.model

import org.hl7.cql.model.DataType
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.TypeSpecifier

/**
 * The Invocation interface is used to represent an invocation of an operator or function in the ELM
 * model. The ELM classes have named properties for their operands, but the Invocation interface
 * uses a list of expressions to represent the operands. The implementations of this interface are
 * responsible for managing the mapping between the list of expressions and the properties of the
 * ELM class. For example, the DateInvocation class maps properties for year, month, and day to the
 * first, second, and third expressions in the list of operands. This allows Invocations to be
 * handled generically in the CQL-to-ELM translation process.
 */
interface Invocation {

    var signature: List<@JvmSuppressWildcards TypeSpecifier>

    var operands: List<@JvmSuppressWildcards Expression>

    var resultType: DataType?

    val expression: Expression

    var resolution: OperatorResolution?
}
