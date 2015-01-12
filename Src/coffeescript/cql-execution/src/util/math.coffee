{ Exception } = require '../datatypes/exception'
{ DateTime } = require '../datatypes/datetime'

module.exports.MAX_INT_VALUE = MAX_INT_VALUE = Math.pow(2,31)-1
module.exports.MIN_INT_VALUE = MIN_INT_VALUE = Math.pow(-2,31)
module.exports.MAX_FLOAT_VALUE = MAX_FLOAT_VALUE = ( Math.pow(10,37)-1 ) / Math.pow(10,8)
module.exports.MIN_FLOAT_VALUE = MIN_FLOAT_VALUE = (Math.pow(-10,37)+1) / Math.pow(10,8)
module.exports.MIN_FLOAT_PRECISION_VALUE = MIN_FLOAT_PRECISION_VALUE = Math.pow(10,-8)
module.exports.MIN_DATE_VALUE = MIN_DATE_VALUE = DateTime.parse("1900-01-01T00:00:00.000")
module.exports.MAX_DATE_VALUE = MAX_DATE_VALUE = DateTime.parse("9999-12-31T23:59:59.999")

module.exports.OverFlowException = OverFlowException = class OverFlowException extends Exception

module.exports.successor = successor = (val) ->
  if typeof val is "number"
    if parseInt(val) is val
      if val is MAX_INT_VALUE then throw  new OverFlowException() else val + 1
    else
      #not bothering with the max float test because javascript does not handle floats at the level
      #very well
      val + MIN_FLOAT_PRECISION_VALUE
  else if val instanceof DateTime
    if val.sameAs(MAX_DATE_VALUE) then throw new OverFlowException() else val.successor()

module.exports.predecessor = predecessor = (val) ->
  if typeof val is "number"
    if parseInt(val) is val
      if val is MIN_INT_VALUE then throw  new OverFlowException() else val - 1
    else
      #not bothering with the min float test because javascript does not handle floats at the level
      #very well
      val - MIN_FLOAT_PRECISION_VALUE
  else if val instanceof DateTime
    if val.sameAs(MIN_DATE_VALUE) then throw new OverFlowException() else val.predecessor()

module.exports.maxValueForInstance = (val) ->
  if typeof val is "number"
    if parseInt(val) is val then MAX_INT_VALUE else MAX_FLOAT_VALUE
  else if val instanceof DateTime
    MAX_DATE_VALUE
  else
    null

module.exports.minValueForInstance = (val) ->
  if typeof val is "number"
    if parseInt(val) is val then MIN_INT_VALUE else MIN_FLOAT_VALUE
  else if val instanceof DateTime
    MIN_DATE_VALUE
  else
    null
