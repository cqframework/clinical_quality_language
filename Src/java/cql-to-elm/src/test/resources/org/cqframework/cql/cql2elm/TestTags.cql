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
@test @1980-12-01 @val val1
@asof: @2020-10-01
@parameter: abcd
 */
define TestDateMultiTag:
   3 + 3


/*
@test @1980-12-01 @val val1
@asof: @2020-10-01
@parameter: "Measurement Interval" [@2019,@2020]
*/
define TestDateIntervalParameter:
   3 + 3