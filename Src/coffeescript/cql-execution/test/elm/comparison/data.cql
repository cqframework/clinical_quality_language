// @Test: Equal
define AGtB_Int: 5 = 4
define AEqB_Int: 5 = 5
define ALtB_Int: 5 = 6
define EqTuples: Tuple{a: 1, b: Tuple{c: 1}} = Tuple{a: 1, b: Tuple{c: 1}}
define UneqTuples: Tuple{a: 1, b: Tuple{c: 1}} = Tuple{a: 1, b: Tuple{c: -1}}
define EqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define UneqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define EqDateTimesTZ: DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) = DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define UneqDateTimesTZ: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define PossiblyEqualDateTimes: DateTime(2000, 3, 15) = DateTime(2000)
define ImpossiblyEqualDateTimes: DateTime(2000, 3, 15) = DateTime(2000, 4)

// @Test: NotEqual
define AGtB_Int: 5 <> 4
define AEqB_Int: 5 <> 5
define ALtB_Int: 5 <> 6
define EqTuples: Tuple{a: 1, b: Tuple{c: 1}} <> Tuple{a: 1, b: Tuple{c: 1}}
define UneqTuples: Tuple{a: 1, b: Tuple{c: 1}} <> Tuple{a: 1, b: Tuple{c: -1}}
define EqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) <> DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define UneqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) <> DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define EqDateTimesTZ: DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) <> DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define UneqDateTimesTZ: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) <> DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define PossiblyEqualDateTimes: DateTime(2000, 3, 15) <> DateTime(2000)
define ImpossiblyEqualDateTimes: DateTime(2000, 3, 15) <> DateTime(2000, 4)

// @Test: Less
define AGtB_Int: 5 < 4
define AEqB_Int: 5 < 5
define ALtB_Int: 5 < 6

// @Test: LessOrEqual
define AGtB_Int: 5 <= 4
define AEqB_Int: 5 <= 5
define ALtB_Int: 5 <= 6

// @Test: Greater
define AGtB_Int: 5 > 4
define AEqB_Int: 5 > 5
define ALtB_Int: 5 > 6

// @Test: GreaterOrEqual
define AGtB_Int: 5 >= 4
define AEqB_Int: 5 >= 5
define ALtB_Int: 5 >= 6
