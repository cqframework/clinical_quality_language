{ Expression } = require './expression'
{ FunctionRef } = require './reusable'

{ ValueSet, Code } = require '../datatypes/datatypes'
{ build } = require './builder'
ucum = require  'ucum.js'

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
    if other instanceof Quantity
      other_v = ucum.convert(other.value,ucum_unit(other.unit),ucum_unit(@unit))
      @value <= other_v

  sameOrAfter: (other) ->
    if other instanceof Quantity
      other_v = ucum.convert(other.value,ucum_unit(other.unit),ucum_unit(@unit))
      @value >= other_v

  after: (other) ->
    if other instanceof Quantity
      other_v = ucum.convert(other.value,ucum_unit(other.unit),ucum_unit(@unit))
      @value > other_v

  before: (other) ->
    if other instanceof Quantity
      other_v = ucum.convert(other.value,ucum_unit(other.unit),ucum_unit(@unit))
      @value < other_v

  dividedBy: (other) ->
    @multiplyDivied(other,"/")

  multiplyBy: (other) ->
    @multiplyDivied(other,".") # in ucum . represents multiplication

  multiplyDivied: (other, operator) ->
    if other instanceof Quantity
      if @unit and other.unit
        can_val = ucum.parse(ucum_unit(@unit)) # ucum.canonicalize(@value,ucum_unit(@unit))
        can_val.value = @value
        other_can_value = ucum.parse(ucum_unit(other.unit)) #ucum.canonicalize(other.value,ucum_unit(other.unit))
        other_can_value.value = other.value
        ucum_value = ucum_multiply(can_val,[[operator,other_can_value]])
        createQuantity(ucum_value.value, units_to_string(ucum_value.units))
      else
        value = if operator == "/" then @value / other.value  else @value * other.value
        unit = @unit || other.unit
        createQuantity(value, unit)
    else
      value = if operator == "/" then @value / other  else @value * other
      createQuantity( value, @unit)

<<<<<<< 556abbed77f7788a089af01e65ef106ec369c2f5
time_unit_to_ucum = {'day' : 'd' , 'hour' : 'h', 'minute' : 'min' , 'second': 's' , 'millisecond' :  'ms', 'week' : 'wk', 'weeks' : 'wk' }
=======
time_unit_to_ucum = {'year' : 'a', 'month' : 'mo',  'day' : 'd' , 'hour' : 'h', 'minute' : 'min' , 'second': 's' , 'millisecond' :  'ms', 'week' : 'wk', 'weeks' : 'wk' }
>>>>>>> modified documentation added canonicalize method to quantity

time_unit_dateTime_mapping = {'years':'year',  'months': 'month',  'days' :'day', 'hours': 'hour' , 'minutes': 'minute', 'seconds':'seconds', 'milliseconds' : 'millisecond' }

# this is used to drop the pluralization of unit fields to pass into DateTime objects for addition and subtraction operations
clean_unit = (units) ->
  if time_unit_dateTime_mapping[units] then time_unit_dateTime_mapping[units] else units

# this is used to perform any convertions of CQL date time fileds to their ucum equivalents
ucum_unit = (unit) ->
  # first strip off any pluraizations then attempt to perform a time unit to ucum unit mapping
  # otherwise send back the original value
  u = time_unit_dateTime_mapping[unit] || unit
  time_unit_to_ucum[u] ||  u



module.exports.canonicalize = (value,unit) ->
  return {value: value} if unit == null
  ucv = ucum.canonicalize(value, ucum_unit(unit))
  {value: ucv.value, unit: units_to_string(ucv.units)}



#just a wrapper function to deal with possible exceptions being thrown
convert_value = (value, from ,to ) ->
  try
    ucum.convert(value,ucum_unit(from),ucum_unit(to))
  catch e

module.exports.convert_value = convert_value
# This method will take a ucum.js representation of untis and converth them to a string
# ucum.js units are a has of unit => power values.  For instance m/h (meters per hour) in
# ucum.js will be reprsented by the json object {m: 1, h:-1}  negative values are inverted and
# are akin to denominator values in a fraction.  Positive values are somewhat a kin to numerator
# values in that they preceed the inverted values.  It is possible in ucum to have multiple non inverted
# or inverted values.  This method combines all of the non inverted values and appends them with
# the ucum multiplication operator '.' and then appends the inverted values separated by the ucum
# divisor '/' .
units_to_string = (units = {}) ->
  numer = []
  denom = []
  for key in Object.keys(units)
    v = units[key]
    pow = Math.abs v
    str = if pow == 1 then key  else key + pow
    if v < 0 then denom.push str else numer.push str
  unit_string = ""
  unit_string += numer.join(".")
  if denom.length > 0
    unit_string += "/" + denom.join("/")
  if unit_string == "" then null else unit_string


# this method is taken from the ucum.js library which it does not  export
# so we need to replicate the behavior here in order to perform multiplication
# and division of the ucum values.  
# t:  the ucum quantity being multiplied/divided .  This method modifies the object t that is passed in
# ms: an array of arrays whoes format is [<operator>,<ucum quantity>] an example would be [['.', {value: 1, units: {m:2}}]]
# this would represent multiply t by the value m^2
ucum_multiply = (t, ms=[]) ->
  return t if ms.length == 0
  ret = t
  for mterm in ms
    sign = if mterm[0] == '.' then 1 else -1
    b = mterm[1]
    ret.value *= Math.pow(b.value,sign)
    for k,v of b.units
      ret.units[k] = ret.units[k] || 0
      ret.units[k] = ret.units[k] + sign*v
      if ret.units[k] == 0
        delete ret.units[k]
  ret

module.exports.createQuantity = createQuantity = (value,unit) ->
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
    # we will choose the unit of a to be the unit we return
    val = convert_value(b.value, b.unit, a.unit)
    if val
      new Quantity({unit: a.unit, value: a.value + val})
  else
    a.copy?().add?(b.value, clean_unit(b.unit))

module.exports.doSubtraction = (a,b) ->
  if a instanceof Quantity and b instanceof Quantity
    # we will choose the unit of a to be the unit we return
    val = convert_value(b.value, b.unit, a.unit)
    if val
      new Quantity({unit: a.unit, value: a.value - val})
  else
    a.copy?().add?(b.value * -1 , clean_unit(b.unit))


module.exports.doDivision = (a,b) ->
  if a instanceof Quantity
    a.dividedBy(b)

module.exports.doMultiplication = (a,b) ->
  if a instanceof Quantity and b instanceof Quantity
    a.multiplyBy(b)
  else
    [q,d]  = if a instanceof Quantity then [a,b] else [b,a]
    q.value * d
