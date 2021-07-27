library TestHidden
define A: Interval[DateTime(2014, 1, 1, 0, 0, 0, 0), DateTime(2015, 1, 1, 0, 0, 0, 0))
define B: Interval[DateTime(2014, 6, 1, 0, 0, 0, 0), DateTime(2014, 6, 2, 0, 0, 0, 0))
define PointA: DateTime(2014, 1, 1, 0, 0, 0, 0)
define PointB: DateTime(2014, 6, 1, 0, 0, 0, 0)


define IntervalSameAs: A same as B
define IntervalSameOrAfter: A same or after B
define PointSameOrAfter: PointA same or after PointB
define PointSameOrBefore: PointA same or before PointB


// Should result in a "Hiding operand identifier"
define function TestOperandHiding(testOperand Integer):
  "TestList" testOperand
    where testOperand.id = 1