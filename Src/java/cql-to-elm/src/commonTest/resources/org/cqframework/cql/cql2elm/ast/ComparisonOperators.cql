library ComparisonOperators version '1.0.0'
using System
context Patient

define IntegerEqual: 1 = 1
define DecimalEqual: 1.0 = 1.0
define StringEqual: 'Alpha' = 'Omega'
define BooleanEqual: true = false
define IntegerNotEqual: 1 != 2
define DecimalNotEqual: 1.0 != 2.0
define IntegerEquivalent: 1 ~ 1
define IntegerNotEquivalent: 1 !~ 2
define IntegerLess: 1 < 2
define DecimalLess: 1.0 < 2.0
define StringLess: 'Alpha' < 'Omega'
define IntegerLessOrEqual: 1 <= 2
define IntegerGreater: 2 > 1
define DecimalGreater: 2.0 > 1.0
define IntegerGreaterOrEqual: 2 >= 1
define BooleanAnd: true and false
define BooleanOr: true or false
define BooleanXor: true xor false
define BooleanNot: not true
define BooleanImplies: true implies false
define MixedEqualIntDecimal: 1 = 1.0
