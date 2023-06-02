library TypeOperator version '1'

//As
define AsQuantity: 45.5 'g' as Quantity
define CastAsQuantity: cast 45.5 'g' as Quantity
define AsDateTime: DateTime(2014, 01, 01) as DateTime

//ConvertQuantity
define ConvertQuantity: ConvertQuantity(5 'mg', 'g')
define ConvertSyntax: convert 5 'mg' to 'g'

//Convert
define IntegerToDecimal: convert 5 to Decimal
define IntegerToString: convert 5 to String
define StringToIntegerError: convert 'foo' to Integer
define StringToDateTime: convert '2014-01-01' to DateTime
define StringToTime: convert 'T14:30:00.0' to Time
define StringToDateTimeMalformed: convert '2014/01/01' to DateTime

//ConvertsTo[x]
define ConvertsToBooleanTrue: ConvertsToBoolean('TRUE')
define ConvertsToBooleanFalse: ConvertsToBoolean('AFFIRMATIVE')
define ConvertsToBooleanNull: ConvertsToBoolean(null)

define ConvertsToDateTrue: ConvertsToDate('2014-01')
define ConvertsToDateFalse: ConvertsToDate('January 2014')
define ConvertsToDateNull: ConvertsToDate(null)

define ConvertsToDateTimeStringTrue: ConvertsToDateTime('2014-01-01T01')
define ConvertsToDateTimeDateTrue: ConvertsToDateTime(@2014-01-01)
define ConvertsToDateTimeFalse: ConvertsToDateTime('January 2014')
define ConvertsToDateTimeNull: ConvertsToDateTime(null)

define ConvertsToDecimalTrue: ConvertsToDecimal('+1')
define ConvertsToDecimalFalse: ConvertsToDecimal('+-1')
define ConvertsToDecimalNull: ConvertsToDecimal(null)

define ConvertsToIntegerTrue: ConvertsToInteger('+1')
define ConvertsToIntegerLong: ConvertsToInteger(123L)
define ConvertsToIntegerFalse: ConvertsToInteger('+-1')
define ConvertsToIntegerNull: ConvertsToInteger(null)

define ConvertsToLongTrue: ConvertsToLong('123')
define ConvertsToLongFalse: ConvertsToLong('+-1')
define ConvertsToLongNull: ConvertsToLong(null)

//Quantity
define ConvertsToQuantityStringTrue: ConvertsToQuantity('-0.1 \'mg\'')
define ConvertsToQuantityStringFalse: ConvertsToQuantity('+-0.1 \'mg\'')
define ConvertsToQuantityIntegerTrue: ConvertsToQuantity(1)
define ConvertsToQuantityDecimalTrue: ConvertsToQuantity(1.0)
define ConvertsToQuantityRatioTrue: ConvertsToQuantity(1.0'mg':2.0'mg')
define ConvertsToQuantityNull: ConvertsToQuantity(null)

define ConvertsToStringBoolean: ConvertsToString(true)
define ConvertsToStringInteger: ConvertsToString(1)
define ConvertsToStringLong: ConvertsToString(1L)
define ConvertsToStringDecimal: ConvertsToString(1.1)
define ConvertsToStringQuantity: ConvertsToString(1.1'mg')
define ConvertsToStringRatio: ConvertsToString(1.1'mg':3.1'mg')
define ConvertsToStringDate: ConvertsToString(@2012-01-01)
define ConvertsToStringDateTime: ConvertsToString(@2012-01-01T01:01)
define ConvertsToStringTime: ConvertsToString(@T01:01)
define ConvertsToStringNull: ConvertsToString(null)

define ConvertsToTimeTrue: ConvertsToTime('01:30')
define ConvertsToTimeFalse: ConvertsToTime('01-30')
define ConvertsToTimeNull: ConvertsToTime(null)

//Is
define IntegerIsInteger: 5 is Integer
define StringIsInteger: '5' is Integer

//ToBoolean
define StringNoToBoolean: ToBoolean('NO')

//ToConcept
define CodeToConcept1: ToConcept(Code { code: '8480-6' })

//ToDateTime
define ToDateTime0: ToDateTime('2014-01')
define ToDateTime1: ToDateTime('2014-01-01')
define ToDateTime2: ToDateTime('2014-01-01T12:05')
define ToDateTime3: ToDateTime('2014-01-01T12:05:05.955')
define ToDateTime4: ToDateTime('2014-01-01T12:05:05.955+01:30')
define ToDateTime5: ToDateTime('2014-01-01T12:05:05.955-01:15')
define ToDateTime6: ToDateTime('2014-01-01T12:05:05.955Z')
define ToDateTimeMalformed: ToDateTime('2014/01/01T12:05:05.955Z')

//ToDecimal
define String25D5ToDecimal: ToDecimal('+25.5')

//ToInteger
define StringNeg25ToInteger: ToInteger('-25')

//ToLong
define String123ToLong: ToLong('123')

//ToQuantity
define String5D5CMToQuantity: ToQuantity('5.5 cm')
define StringInvalidToQuantityNull: ToQuantity('+-0.1 \'mg\'')
define String100PerMinPerSqMeterToQuantity: ToQuantity('100 daL/min/m2')
define String100UnitPer10BillionToQuantity: ToQuantity('100 U/10*10{cells}')
define String60DayPer7DayToQuantity: ToQuantity('60 d/(7.d)')
define String60EhrlichPer100gmToQuantity: ToQuantity('60 {Ehrlich\'U}/100.g')
define StringPercentToQuantity: ToQuantity('60 \'%\'')
define StringPercentWithoutQuoteToQuantity: ToQuantity('70 %')
define StringPercentWithTabToQuantity: ToQuantity('80\t%')
define StringPercentWithMultiSpacesToQuantity: ToQuantity('90    %')
define StringPercentWithSpacesUnitToQuantity: ToQuantity('10 m l')
define StringPercentWithQuoteUnitToQuantity: ToQuantity('20 m\'l')

//ToRatio
define ToRatioIsValid: ToRatio('1.0 \'mg\':2.0 \'mg\'')
define ToRatioIsNull: ToRatio('1.0 \'mg\';2.0 \'mg\'')

//ToString
define IntegerNeg5ToString: ToString(-5)
define LongNeg5ToString: ToString(-5L)
define Decimal18D55ToString: ToString(18.55)
define Quantity5D5CMToString: ToString(5.5 'cm')
define BooleanTrueToString: ToString(true)

//ToTime
define ToTime1: ToTime('T14:30:00.0')
define ToTimeMalformed: ToTime('T14-30-00.0')
