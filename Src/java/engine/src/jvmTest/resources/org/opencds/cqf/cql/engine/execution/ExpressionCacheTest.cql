library ExpressionCacheTest

include ExpressionCacheTestCommon called "Common"

define "Expression":
    5

define "IncludedExpression":
    "Common"."Expression"

