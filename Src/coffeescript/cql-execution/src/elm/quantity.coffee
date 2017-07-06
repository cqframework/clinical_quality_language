{ Expression } = require './expression'
{ ValueSet, Code } = require '../datatypes/datatypes'
{ build } = require './builder'

# Unit conversation is currently implemented on for time duration comparison operations
# TODO: Implement unit conversation for time duration mathematical operations
# TODO: Quantity should probably be available as a datatype (not just ELM expression)
module.exports.Quantity = class Quantity extends Expression
  constructor: (json) ->
    super
    @unit = json.unit
    @value = parseFloat json.value

  clone: () ->
    new Quantity({value: @value, unit: @unit})

  exec: (ctx) ->
    @

  toString: () ->
    "#{@value} '#{@unit}'"

  sameOrBefore: (other) ->
    if other instanceof Quantity and other.unit == @unit
      @value <= parseFloat other.value
    else if other instanceof Quantity and time_units[other.unit]? and time_units[@unit]?
      thisSmallestDuration = smallestDuration(@)
      otherSmallestDuration = smallestDuration(other)
      thisSmallestDuration <= otherSmallestDuration
    else null

  sameOrAfter: (other) ->
    if other instanceof Quantity and other.unit == @unit
      @value >= parseFloat other.value
    else if other instanceof Quantity and time_units[other.unit]? and time_units[@unit]?
      thisSmallestDuration = smallestDuration(@)
      otherSmallestDuration = smallestDuration(other)
      thisSmallestDuration >= otherSmallestDuration
    else null

  after: (other) ->
    if other instanceof Quantity and other.unit == @unit
      @value > parseFloat other.value
    else if other instanceof Quantity and time_units[other.unit]? and time_units[@unit]?
      thisSmallestDuration = smallestDuration(@)
      otherSmallestDuration = smallestDuration(other)
      thisSmallestDuration > otherSmallestDuration
    else null

  before: (other) ->
    if other instanceof Quantity and other.unit == @unit
      @value < parseFloat other.value
    else if other instanceof Quantity and time_units[other.unit]? and time_units[@unit]?
      thisSmallestDuration = smallestDuration(@)
      otherSmallestDuration = smallestDuration(other)
      thisSmallestDuration < otherSmallestDuration
    else null
    
  equals: (other) ->
    if other instanceof Quantity and @unit == other.unit
      @value == parseFloat other.value
    else if other instanceof Quantity and time_units[other.unit]? and time_units[@unit]?
      thisSmallestDuration = smallestDuration(@)
      otherSmallestDuration = smallestDuration(other)
      thisSmallestDuration == otherSmallestDuration
    else null

# Hash of time units and their UCUM equivalents, both case-sensitive and case-insensitive
# See http://unitsofmeasure.org/ucum.html#para-31
# The CQL specification says that dates are based on the Gregorian calendar
time_units = {'years': 'a_g', 'year': 'a_g', 'YEARS': 'a_g', 'YEAR': 'a_g'
  , 'a': 'a_j', 'ANN': 'a_j', 'ann': 'a_j', 'A': 'a_j'
  , 'months': 'mo_g', 'month':'mo_g'
  , 'mo': 'mo_j', 'MO': 'mo_j'
  , 'weeks': 'week', 'week': 'week', 'wk', 'week', 'WK', 'week'
  , 'days': 'day', 'day':'day', 'd': 'day', 'D': 'day'
  , 'hours': 'hour', 'hour': 'hour', 'h': 'hour', 'H': 'hour'
  , 'minutes': 'minute', 'minute': 'minute', 'min': 'minute', 'MIN': 'minute'
  , 'seconds':'second', 'second':'second', 's': 'second', 'S': 'second'
  , 'milliseconds' : 'millisecond', 'millisecond' : 'millisecond', 'ms': 'millisecond', 'MS': 'millisecond'
  }

clean_unit = (units) ->
  if time_units[units]
    time_units[units]
  else units

# The smallest common duration is the millisecond
smallestDuration = (qty) ->
  if parseFloat qty.value
    millivalue = switch
      when clean_unit(qty.unit) == 'second' then qty.value * 1000
      when clean_unit(qty.unit) == 'minute' then qty.value * 60 * 1000
      when clean_unit(qty.unit) == 'hour' then qty.value * 60 * 60 * 1000
      when clean_unit(qty.unit) == 'day' then qty.value * 24 * 60 * 60 * 1000
      when clean_unit(qty.unit) == 'week' then qty.value * 7 * 24 * 60 * 60 * 1000
      # Support for the UCUM units based on the Gregorian calendar
      when clean_unit(qty.unit) == 'mo_g' then qty.value * 30.436875 * 24 * 60 * 60 * 1000  # Based on a Gregorian mean month length of 30.436875 days
      when clean_unit(qty.unit) == 'a_g' then qty.value * 365.2425 * 24 * 60 * 60 * 1000  # Based on a Gregorian year of 365.2425 days
      # Support for the UCUM units based on the Julian calendar
      when clean_unit(qty.unit) == 'mo_j' then qty.value * 30.4375 * 24 * 60 * 60 * 1000  # Based on a Julian mean month length of 30.4375 days
      when clean_unit(qty.unit) == 'a_j' then qty.value * 365.25 * 24 * 60 * 60 * 1000  # Based on a Julian year of 365.25 days
      else qty.value
    millivalue
  else
    null

module.exports.createQuantity = (value,unit) ->
  new Quantity({value: value, unit: unit})

module.exports.parseQuantity = (str) ->
  components = /([+|-]?\d+\.?\d*)\s*'(.+)'/.exec str
  if components? and components[1]? and components[2]?
    value = parseFloat(components[1])
    unit = components[2].trim()
    new Quantity({value: value, unit: unit})
  else
    null

module.exports.doAddition = (a,b) ->
  if a instanceof Quantity and b instanceof Quantity
    if a.unit == b.unit
      new Quantity({unit: a.unit, value: a.value + b.value})
  else
    a.copy().add(b.value, clean_unit(b.unit))


module.exports.doSubtraction = (a,b) ->
  if a instanceof Quantity and b instanceof Quantity
    if a.unit == b.unit
      new Quantity({unit: a.unit, value: a.value - b.value})
  else
    a.copy().add(b.value * -1 , clean_unit(b.unit))

module.exports.doDivision = (a,b) ->
  if a instanceof Quantity and b instanceof Quantity
    if a.unit == b.unit
      a.value / b.value
  else
    new Quantity({unit: a.unit, value: a.value / b})

module.exports.doMultiplication = (a,b) ->
  if a instanceof Quantity and b instanceof Quantity
    # TODO: proper conversion of units (e.g., 5 m * 5 m = 5 m^2)
    null
  else
    [q,d]  = if a instanceof Quantity then [a,b] else [b,a]
    new Quantity({unit: q.unit, value: q.value * d})
