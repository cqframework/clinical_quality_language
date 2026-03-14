library CqlInternalTypeRepresentationSuite version '1'

/*
    NOTES:
    This test suite is meant to serve as a comprehensive, portable test suite for Any implementation of a CQL execution engine.

    There are 3 components of this suite (of which this file is one):
      1. Optional Internal Type/value tests (this file)
      2. Valid operator tests
      3. Invalid operator tests (All these tests result in a runtime error)

    This suite is an extension of the Bonnie tests, which can be found here:
       https://github.com/cqframework/cql-execution/tree/master/test/elm
*/

// Basic
define BoolTrue: true
define BoolFalse: false
define IntOne: 1
define DecimalTenth: 0.1
define StringTrue: 'true'
define DateTimeX: @2012-02-15T12:10:59.456Z
define DateTimeFX: DateTime(2012, 2, 15, 12, 10, 59, 456, 0)
define TimeX: @T12:10:59.456

// DateTime
define DateTime_Year: DateTime(2012)
define DateTime_Month: DateTime(2012, 2)
define DateTime_Day: DateTime(2012, 2, 15)
define DateTime_Hour: DateTime(2012, 2, 15, 12)
define DateTime_Minute: DateTime(2012, 2, 15, 12, 10)
define DateTime_Second: DateTime(2012, 2, 15, 12, 10, 59)
define DateTime_Millisecond: DateTime(2012, 2, 15, 12, 10, 59, 456)
define DateTime_TimezoneOffset: DateTime(2012, 2, 15, 12, 10, 59, 456, -8.0)

// Time
define Time_Hour: Time(12)
define Time_Minute: Time(12, 10)
define Time_Second: Time(12, 10, 59)
define Time_Millisecond: Time(12, 10, 59, 456)

// Clinical
define Clinical_quantity: 12 'a'

define Clinical_QuantityA:
  Quantity {
    unit: 'a',
    value: 12
  }

define Clinical_CodeA:
  Code {
    system: 'http://loinc.org',
    code: '12345',
    version: '1',
    display: 'Test Code'
  }

define Clinical_ConceptA:
  Concept {
    codes: { Clinical_CodeA },
    display: 'Test Concept'
  }

// Structured
define Structured_tuple: { a: 1, b: 2 }

define Structured_TupleA:
  Tuple {
    class: 'Portable CQL Test Suite',
    versionNum: 1.0,
    date: DateTime(2018, 7, 18),
    developer: 'Christopher Schuler'
  }

// Interval
define Interval_Open: Interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define Interval_LeftOpen: Interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
define Interval_RightOpen: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define Interval_Closed: Interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]

// List
define Code1: Code { system: 'http://loinc.org', code: '12345', version: '1', display: 'Test Code' }
define Code2: Code { system: 'http://loinc.org', code: '123456', version: '1', display: 'Another Test Code' }
define Three: 1 + 2

define List_BoolList: { true, false, true }
define List_IntList: { 9, 7, 8 }
define List_DecimalList: { 1.0, 2.1, 3.2 }
define List_StringList: { 'a', 'bee', 'see' }
define List_DateTimeList: { @2012-02-15T12:10:59.456Z, @2012-03-15T12:10:59.456Z, @2012-04-15T12:10:59.456Z }
define List_TimeList: { @T12:10:59.456, @T13:10:59.456, @T14:10:59.456 }
define List_QuantityList: { 1.0'm', 2.1'm', 3.2'm' }
define List_CodeList: { Code1, Code2 }
define List_ConceptList: { Concept { codes: { Code1 }, display: 'Test Concept' }, Concept { codes: { Code2 }, display: 'Another Test Concept' } }
define List_TupleList: { Tuple { a: 1, b: '2' }, Tuple { x: 2, z: '3' } }
define List_ListList: { { 1, 2, 3 }, { 'a', 'b', 'c' } }
define List_IntervalList: { Interval[1, 5], Interval(5, 9), Interval[8, 10) }
define List_MixedList: List<Any>{ 1, 'two', Three }
define List_EmptyList: List<Integer>{}
