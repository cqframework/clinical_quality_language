library CqlAllFailedTests

define Exp1000  : Exp(1000)
define Exp1000D  : Exp(1000.0)

define Ln0  : Ln(0)
define LnNeg0  : Ln(-0)

define PredecessorUnderflowDt: predecessor of DateTime(0001, 1, 1, 0, 0, 0, 0)
define PredecessorUnderflowT: predecessor of @T00:00:00.000

define SuccessorOverflowDt: successor of DateTime(9999, 12, 31, 23, 59, 59, 999)
define SuccessorOverflowT: successor of @T23:59:59.999

define DateTimeDurationBetweenUncertainInterval: days between DateTime(2014, 1, 15) and DateTime(2014, 2)
define DateTimeDurationBetweenUncertainInterval2: months between DateTime(2005) and DateTime(2006, 5)

define DateTimeAddInvalidYears: DateTime(2005, 10, 10) + 8000 years
define DateTimeDurationBetweenUncertainDiv: DateTimeDurationBetweenUncertainInterval div DateTimeDurationBetweenUncertainInterval2
define DateTimeSubtractInvalidYears: DateTime(2005, 10, 10) - 2005 years

define TestMessageError: Message(3 + 1, true, '400', 'Error', 'This is an error!')
define TestErrorWithNullSource: Message(null, true, '1', 'Error', 'This is a message')

define TestErrorWithNullCondition: Message(1, null, '1', 'Error', 'This is a message')

define TestErrorWithNullCode: Message(1, true, null, 'Error', 'This is a message')

define TestErrorWithNullMessage: Message(1, true, '1', 'Error', null)