{ Expression } = require './expression'
{ ValueSet, Code } = require '../datatypes/datatypes'
{ build } = require './builder'

module.exports.Quantity = class Quantity extends Expression
  constructor: (json) ->
    super
    @unit = json.unit
    @value = json.value

  exec: (ctx) ->
    @
  
  toString: () ->
    "#{@value}#{@unit}"
  
  sameOrBefore: (other) ->
    if other instanceof Quantity and other.unit == @unit then @value <= other.value else null

  sameOrAfter: (other) ->
    if other instanceof Quantity and other.unit == @unit then @value >= other.value else null

  after: (other) ->
    if other instanceof Quantity and other.unit == @unit then @value > other.value else null

  before: (other) ->
    if other instanceof Quantity and other.unit == @unit then @value < other.value else null

time_units = {'years':'year',  'months': 'month',  'days' :'day', 'minutes': 'minute', 'seconds':'seconds', 'milliseconds' : 'millisecond' }

clean_unit = (units) ->
  if time_units[units] then time_units[units] else units

module.exports.createQuantity = (value,unit) ->
  new Quantity({value: value, unit: unit})
  
module.exports.parseQuantity = (str) ->
  components = /(\d+\.?\d*)(.+)/.exec str
  if components? and components[1]? and components[2]
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
