library TestUnion

// expect {}
define "NullAndNullList":
    null as List<Integer> union null as List<Integer>

// expect null
define "NullAndNullInterval":
    null as Interval<Integer> union null as Interval<Integer>

// expect null
// Based on the CQL conversion precedence rules,
// the compiler _should have_ inferred this as Intervals
// and not Lists.
define "NullAndNullUntyped":
    null union null

// expect {}
define "NullAndEmpty":
    null union {}

// expect {}
define "EmptyAndNull":
    {} union null

// expect {1}
define "NullAndSingle":
    null union {1}

// expect {1}
define "SingleAndNull":
    null union {1}

