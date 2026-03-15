library AdvancedFeatures version '1.0.0'
using System
context Patient

// Case expression (condition-style)
define CaseCondition:
  case
    when 1 > 2 then 'gt'
    when 1 < 2 then 'lt'
    else 'eq'
  end

// Case expression (comparand-style)
define CaseComparand:
  case 1
    when 1 then 'one'
    when 2 then 'two'
    else 'other'
  end

// Math functions
define AbsTest: Abs(-1)
define CeilingTest: Ceiling(1.1)
define FloorTest: Floor(1.9)
define TruncateTest: Truncate(1.9)
define RoundTest: Round(1.5, 0)
define LnTest: Ln(1.0)
define ExpTest: Exp(0.0)

// Date/time functions
define NowTest: Now()
define TodayTest: Today()
define TimeOfDayTest: TimeOfDay()
