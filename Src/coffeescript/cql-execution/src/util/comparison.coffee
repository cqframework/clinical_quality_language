{ DateTime } = require '../datatypes/datetime'
{ Uncertainty } = require '../datatypes/uncertainty'

areNumbers = (a, b) ->
  typeof a is 'number' and typeof b is 'number'

areDateTimesOrQuantities = (a, b) ->
  (a instanceof DateTime and b instanceof DateTime) or (a?.constructor?.name == 'Quantity' and b?.constructor?.name == 'Quantity')

isUncertainty = (x) ->
  x instanceof Uncertainty

module.exports.lessThan = (a, b, precision = DateTime.Unit.MILLISECOND) ->
  switch
    when areNumbers a, b then a < b
    when areDateTimesOrQuantities a, b then a.before(b, precision)
    when isUncertainty a then a.lessThan b
    when isUncertainty b then Uncertainty.from(a).lessThan b
    else null

module.exports.lessThanOrEquals = (a, b, precision = DateTime.Unit.MILLISECOND) ->
  switch
    when areNumbers a, b then a <= b
    when areDateTimesOrQuantities a, b then a.sameOrBefore(b, precision)
    when isUncertainty a then a.lessThanOrEquals b
    when isUncertainty b then Uncertainty.from(a).lessThanOrEquals b
    else null

module.exports.greaterThan = (a, b, precision = DateTime.Unit.MILLISECOND) ->
  switch
    when areNumbers a, b then a > b
    when areDateTimesOrQuantities a, b then a.after(b, precision)
    when isUncertainty a then a.greaterThan b
    when isUncertainty b then Uncertainty.from(a).greaterThan b
    else null

module.exports.greaterThanOrEquals = (a, b, precision = DateTime.Unit.MILLISECOND) ->
  switch
    when areNumbers a, b then a >= b
    when areDateTimesOrQuantities a, b then a.sameOrAfter(b, precision)
    when isUncertainty a then a.greaterThanOrEquals b
    when isUncertainty b then Uncertainty.from(a).greaterThanOrEquals b
    else null

module.exports.equivalent = equivalent = (a, b) ->
  return a.hasMatch b if typeof a.hasMatch is 'function'
  return equals a, b

module.exports.equals = equals = (a, b) ->
  # Handle null cases first
  return a is b if not a? or not b?

  # If one is a Quantity, use the Quantity equals function
  return a.equals b if a?.constructor?.name == 'Quantity'

  # If one is an Uncertainty, convert the other to an Uncertainty
  if a instanceof Uncertainty then b = Uncertainty.from(b)
  else if b instanceof Uncertainty then a = Uncertainty.from(a)

  # Use overloaded 'equals' function if it is available
  return a.equals(b) if typeof a.equals is 'function'

  # Return true of the objects are strictly equal
  return true if a is b

  # Return false if they are instances of different classes
  [aClass, bClass] = ({}.toString.call(obj) for obj in [a, b])
  return false if aClass isnt bClass

  switch aClass
    when '[object Date]'
      # Compare the ms since epoch
      return a.getTime() is b.getTime()
    when '[object RegExp]'
      # Compare the components of the regular expression
      return ['source', 'global', 'ignoreCase', 'multiline'].every (p) -> a[p] is b[p]
    when '[object Array]'
      # Compare every item in the array
      return a.length is b.length and a.every (item, i) -> equals(item, b[i])
    when '[object Object]'
      # Return false if they are instances of different classes
      return false unless b instanceof a.constructor and a instanceof b.constructor
      # Do deep comparison of keys and values
      aKeys = (key for key of a unless typeof(key) is 'function')
      bKeys = (key for key of b unless typeof(key) is 'function')
      return aKeys.length is bKeys.length and aKeys.every (key) -> equals(a[key], b[key])

  # If we made it this far, we can't handle it
  return false
