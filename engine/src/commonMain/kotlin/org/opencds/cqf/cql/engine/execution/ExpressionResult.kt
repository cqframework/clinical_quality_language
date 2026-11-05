package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.Value

@JsOnlyExport
class ExpressionResult(val value: Value?, val evaluatedResources: MutableSet<Value?>?)
