{ Expression } = require './expression'
{ FunctionRef } = require './reusable'

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
    else if other instanceof Quantity and ucum_time_units[other.unit]? and ucum_time_units[@unit]?
      thisDurationInMilliseconds = durationInMilliseconds(@)
      otherDurationInMilliseconds = durationInMilliseconds(other)
      thisDurationInMilliseconds <= otherDurationInMilliseconds
    else null

  sameOrAfter: (other) ->
    if other instanceof Quantity and other.unit == @unit
      @value >= parseFloat other.value
    else if other instanceof Quantity and ucum_time_units[other.unit]? and ucum_time_units[@unit]?
      thisDurationInMilliseconds = durationInMilliseconds(@)
      otherDurationInMilliseconds = durationInMilliseconds(other)
      thisDurationInMilliseconds >= otherDurationInMilliseconds
    else null

  after: (other) ->
    if other instanceof Quantity and other.unit == @unit
      @value > parseFloat other.value
    else if other instanceof Quantity and ucum_time_units[other.unit]? and ucum_time_units[@unit]?
      thisDurationInMilliseconds = durationInMilliseconds(@)
      otherDurationInMilliseconds = durationInMilliseconds(other)
      thisDurationInMilliseconds > otherDurationInMilliseconds
    else null

  before: (other) ->
    if other instanceof Quantity and other.unit == @unit
      @value < parseFloat other.value
    else if other instanceof Quantity and ucum_time_units[other.unit]? and ucum_time_units[@unit]?
      thisDurationInMilliseconds = durationInMilliseconds(@)
      otherDurationInMilliseconds = durationInMilliseconds(other)
      thisDurationInMilliseconds < otherDurationInMilliseconds
    else null

  equals: (other) ->
    if other instanceof Quantity and @unit == other.unit
      @value == parseFloat other.value
    else if other instanceof Quantity and ucum_time_units[other.unit]? and ucum_time_units[@unit]?
      thisDurationInMilliseconds = durationInMilliseconds(@)
      otherDurationInMilliseconds = durationInMilliseconds(other)
      thisDurationInMilliseconds == otherDurationInMilliseconds
    else null


clean_unit = (units) ->
  if ucum_time_units[units] then ucum_to_cql_units[ucum_time_units[units]] else units

# Hash of time units and their UCUM equivalents, both case-sensitive and case-insensitive
# See http://unitsofmeasure.org/ucum.html#para-31
# The CQL specification says that dates are based on the Gregorian calendar
# UCUM says that years should be Julian. As a result, CQL-based year and month identifiers will
# be matched to the UCUM gregorian units. UCUM-based year and month identifiers will be matched
# to the UCUM julian units.
ucum_time_units = {'years': 'a_g', 'year': 'a_g', 'YEARS': 'a_g', 'YEAR': 'a_g', 'a_g': 'a_g'
  , 'a': 'a_j', 'ANN': 'a_j', 'ann': 'a_j', 'A': 'a_j', 'a_j': 'a_j'
  , 'months': 'mo_g', 'month':'mo_g', 'mo_g': 'mo_g'
  , 'mo': 'mo_j', 'MO': 'mo_j', 'mo_j': 'mo_j'
  , 'weeks': 'wk', 'week': 'wk', 'wk': 'wk', 'WK': 'wk'
  , 'days': 'd', 'day':'d', 'd': 'd', 'D': 'd'
  , 'hours': 'h', 'hour': 'h', 'h': 'h', 'H': 'h'
  , 'minutes': 'min', 'minute': 'min', 'min': 'min', 'MIN': 'min'
  , 'seconds':'s', 'second':'s', 's': 's', 'S': 's'
  , 'milliseconds' : 'ms', 'millisecond' : 'ms', 'ms': 'ms', 'MS': 'ms'
  }

ucum_to_cql_units = {
    'a_j':  'year'
  , 'a_g':  'year'
  , 'mo_j': 'month'
  , 'mo_g': 'month'
  , 'wk':   'week'
  , 'd':    'day'
  , 'h':    'hour'
  , 'min':  'minute'
  , 's':    'second'
  , 'ms':   'millisecond'
}

get_ucum_unit = (units) ->
  if ucum_time_units[units]
    ucum_time_units[units]
  else units

# The smallest common duration is the millisecond
# Returns the VALUE of a time duration-based quantity in milliseconds
durationInMilliseconds = (qty) ->
  if parseFloat qty.value
    millivalue = switch
      when get_ucum_unit(qty.unit) == 's' then qty.value * 1000
      when get_ucum_unit(qty.unit) == 'min' then qty.value * 60 * 1000
      when get_ucum_unit(qty.unit) == 'h' then qty.value * 60 * 60 * 1000
      when get_ucum_unit(qty.unit) == 'd' then qty.value * 24 * 60 * 60 * 1000
      when get_ucum_unit(qty.unit) == 'wk' then qty.value * 7 * 24 * 60 * 60 * 1000
      # Support for the UCUM units based on the Gregorian calendar
      when get_ucum_unit(qty.unit) == 'mo_g' then qty.value * 30.436875 * 24 * 60 * 60 * 1000  # Based on a Gregorian mean month length of 30.436875 days
      when get_ucum_unit(qty.unit) == 'a_g' then qty.value * 365.2425 * 24 * 60 * 60 * 1000  # Based on a Gregorian year of 365.2425 days
      # Support for the UCUM units based on the Julian calendar
      when get_ucum_unit(qty.unit) == 'mo_j' then qty.value * 30.4375 * 24 * 60 * 60 * 1000  # Based on a Julian mean month length of 30.4375 days
      when get_ucum_unit(qty.unit) == 'a_j' then qty.value * 365.25 * 24 * 60 * 60 * 1000  # Based on a Julian year of 365.25 days
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
    if a.unit == b.unit
      new Quantity({unit: a.unit, value: a.value * b.value})
  else
    [q,d]  = if a instanceof Quantity then [a,b] else [b,a]
    q.value * d
    
