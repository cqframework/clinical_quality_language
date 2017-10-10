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
define AGtB_Quantity: 5 'm' = 4 'm'
define AEqB_Quantity: 5 'm' = 5 'm'
define ALtB_Quantity: 5 'm' = 6 'm'
define AGtB_Quantity_diff: 5 'm' = 5 'cm'
define AEqB_Quantity_diff: 5 'm' = 500 'cm'
define ALtB_Quantity_diff: 5 'm' = 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' = 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' = 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' = 40 'm'

// @Test: NotEqual
define AGtB_Int: 5 != 4
define AEqB_Int: 5 != 5
define ALtB_Int: 5 != 6
define EqTuples: Tuple{a: 1, b: Tuple{c: 1}} != Tuple{a: 1, b: Tuple{c: 1}}
define UneqTuples: Tuple{a: 1, b: Tuple{c: 1}} != Tuple{a: 1, b: Tuple{c: -1}}
define EqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define UneqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define EqDateTimesTZ: DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) != DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define UneqDateTimesTZ: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define PossiblyEqualDateTimes: DateTime(2000, 3, 15) != DateTime(2000)
define ImpossiblyEqualDateTimes: DateTime(2000, 3, 15) != DateTime(2000, 4)
define AGtB_Quantity: 5 'm' != 4 'm'
define AEqB_Quantity: 5 'm' != 5 'm'
define ALtB_Quantity: 5 'm' != 6 'm'
define AGtB_Quantity_diff: 5 'm' != 5 'cm'
define AEqB_Quantity_diff: 5 'm' != 500 'cm'
define ALtB_Quantity_diff: 5 'm' != 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' != 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' != 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' != 40 'm'

// @Test: Equivalent
define ANull_BDefined: null ~ 4
define ADefined_BNull: 5 ~ null
define ANull_BNull: null ~ null
define ADefined_BDefined: 3 ~ 3

// @Test: Less
define AGtB_Int: 5 < 4
define AEqB_Int: 5 < 5
define ALtB_Int: 5 < 6
define AGtB_Quantity: 5 'm' < 4 'm'
define AEqB_Quantity: 5 'm' < 5 'm'
define ALtB_Quantity: 5 'm' < 6 'm'
define AGtB_Quantity_diff: 5 'm' < 5 'cm'
define AEqB_Quantity_diff: 5 'm' < 500 'cm'
define ALtB_Quantity_diff: 5 'm' < 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' < 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' < 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' < 40 'm'

// @Test: LessOrEqual
define AGtB_Int: 5 <= 4
define AEqB_Int: 5 <= 5
define ALtB_Int: 5 <= 6
define AGtB_Quantity: 5 'm' <= 4 'm'
define AEqB_Quantity: 5 'm' <= 5 'm'
define ALtB_Quantity: 5 'm' <= 6 'm'
define AGtB_Quantity_diff: 5 'm' <= 4 'm'
define AEqB_Quantity_diff: 5 'm' <= 500 'cm'
define ALtB_Quantity_diff: 5 'm' <= 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' <= 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' <= 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' <= 40 'm'


// @Test: Greater
define AGtB_Int: 5 > 4
define AEqB_Int: 5 > 5
define ALtB_Int: 5 > 6
define AGtB_Quantity: 5 'm' > 4 'm'
define AEqB_Quantity: 5 'm' > 5 'm'
define ALtB_Quantity: 5 'm' > 6 'm'
define AGtB_Quantity_diff: 5 'm' > 5 'cm'
define AEqB_Quantity_diff: 5 'm' > 500 'cm'
define ALtB_Quantity_diff: 5 'm' > 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' > 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' > 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' > 40 'm'

// @Test: GreaterOrEqual
define AGtB_Int: 5 >= 4
define AEqB_Int: 5 >= 5
define ALtB_Int: 5 >= 6
define AGtB_Quantity: 5 'm' >= 4 'm'
define AEqB_Quantity: 5 'm' >= 5 'm'
define ALtB_Quantity: 5 'm' >= 6 'm'
define AGtB_Quantity_diff: 5 'm' >= 5 'cm'
define AEqB_Quantity_diff: 5 'm' >= 500 'cm'
define ALtB_Quantity_diff: 5 'm' >= 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' >= 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' >= 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' >= 40 'm'
define DivideUcum_incompatible: (100 '[nmi_i]' / 2 'h') > 49 'mg/[lb_av]'
define DivideUcum: (100 'mg' / 2 '[lb_av]') > 49 'mg/[lb_av]'
