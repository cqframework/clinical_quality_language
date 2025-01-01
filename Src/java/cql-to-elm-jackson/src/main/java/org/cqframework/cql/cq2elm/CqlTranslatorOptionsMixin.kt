package org.cqframework.cql.cq2elm

import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.cqframework.cql.cql2elm.CqlCompilerOptions

abstract class CqlTranslatorOptionsMixin {
    @get:JsonUnwrapped @set:JsonUnwrapped abstract var cqlCompilerOptions: CqlCompilerOptions?
}
