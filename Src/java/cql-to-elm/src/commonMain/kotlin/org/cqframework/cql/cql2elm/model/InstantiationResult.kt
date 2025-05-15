package org.cqframework.cql.cql2elm.model

/** Created by Bryn on 12/22/2016. */
data class InstantiationResult(
    val genericOperator: GenericOperator,
    val operator: Operator?,
    val conversionScore: Int
)
