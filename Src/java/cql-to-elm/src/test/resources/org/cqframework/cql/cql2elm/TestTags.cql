/*
@author: Frederic Chopin
@description: Test tags
@allowFluent: true
*/
library TestTags

/*
@author: Frederic Chopin
@description: Test tags
*/
define TestExpression:
  1 + 1

/*
@author: Frederic Chopin
@description: Test tags
*/
define function TestFunction():
  1 + 1

/*
@author: Frederic Chopin
@description: This is a multi-line description that
 spans multiple lines.
@following: true
 */
define TestMultiline:
  1 + 1

/*
@test  @pertinence: strongly-positive
 */
define TestMultiTagInline:
   3 + 3

/*
@test: @1980-12-01 @val: val1
@asof: @2020-10-01
@parameter: abcd
 */
define TestDateMultiTag:
   3 + 3


/*
@test: @1980-12-01 @val: val1
@asof: @2020-10-01
@parameter: "Measurement Interval" [@2019,@2020]
*/
define TestDateIntervalParameter:
   3 + 3

/*
@test: this is a
multi-line tag value
*/
define TestMultilineValue:
   3 + 1

/*
@parameter: "abcd" [1,10]
@test: this is a
multi-line tag value
@pertinence : weakly-negative
*/
define TestParameterAtFirstLine:
   3 + 1


/**
 *  @tagname: tag value
 *  @tagname2: tag value2 this is
 *  a long tag value
 */
define AnotherTestCase:
   3 + 1


/*@:@*/
define TestInvalid:
  1 + 1