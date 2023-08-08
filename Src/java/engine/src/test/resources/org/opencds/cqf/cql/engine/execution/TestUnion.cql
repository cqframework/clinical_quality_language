library TestUnion

// expect {}
define "NullAndNull":
    null as List<Integer> union null as List<Integer>

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

