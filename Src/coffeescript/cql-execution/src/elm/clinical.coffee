{ Expression } = require './expression'
dt = require '../datatypes/datatypes'
{ build } = require './builder'

module.exports.ValueSetDef = class ValueSetDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @id = json.id
    @version = json.version
    #todo: code systems and versions

  exec: (ctx) ->
    valueset = ctx.codeService.findValueSet(@id, @version) ? new dt.ValueSet(@id, @version)
    ctx.rootContext().set @name, valueset
    valueset

module.exports.ValueSetRef = class ValueSetRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    # TODO: This calls the code service every time-- should be optimized
    valueset = ctx.getValueSet(@name)
    if valueset instanceof Expression
      valueset = valueset.execute(ctx)
    valueset

module.exports.InValueSet = class InValueSet extends Expression
  constructor: (json) ->
    super
    @code = build json.code
    @valueset = new ValueSetRef json.valueset

  exec: (ctx) ->
    # Bonnie-633 Added null check
    return false unless @code? and @valueset?
    code = @code.execute(ctx)
    valueset = @valueset.execute(ctx)
    if code? and valueset? then valueset.hasCode code else false

module.exports.CodeSystemDef = class CodeSystemDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @id = json.id
    @version = json.version

  exec: (ctx) ->
    new dt.CodeSystem(@id, @version)

module.exports.CodeDef = class CodeDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @id = json.id
    @systemName = json.codeSystem.name
    @display = json.display

  exec: (ctx) ->
    system = ctx.getCodeSystem(@systemName)?.execute(ctx)
    new dt.Code(@id, system.id, system.version, @display)

module.exports.CodeRef = class CodeRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx.getCode(@name)?.execute(ctx)

module.exports.ConceptDef = class ConceptDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @display = json.display
    @codes = json.code

  exec: (ctx) ->
    codes = (ctx.getCode(code.name)?.execute(ctx) for code in @codes)
    new dt.Concept(codes, @display)

module.exports.ConceptRef = class ConceptRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx.getConcept(@name)?.execute(ctx)

module.exports.Concept = class Concept extends Expression
  constructor: (json) ->
    super
    @codes = json.code
    @display = json.display

  toCode: (ctx, code) ->
    system = ctx.getCodeSystem(code.system.name)?.id
    return new dt.Code(code.code, system, code.version, code.display)

  exec: (ctx) ->
    codes = (@toCode(ctx, code) for code in @codes)
    new dt.Concept(codes, @display)

calculateAge = (date1, date2, precision) ->
  if date1.getTime() - date2.getTime() > 0 then return 0
  value = if precision is "Year"
    monthsDiff(date1,date2) / 12
  else if  precision is "Month"
    monthsDiff(date1,date2)
  else
    ageInMS = date2.getTime() - date1.getTime()
    divisor = switch (precision)
      when 'Day' then 1000 * 60 * 60 * 24
      when 'Hour' then 1000 * 60 * 60
      when 'Minute' then 1000 * 60
      when 'Second' then 1000
      else 1
    ageInMS / divisor
  Math.floor(value)

monthsDiff = (date1, date2) ->
  [high,low] = if date1.getTime() > date2.getTime() then [date1,date2] else [date2,date1]
  #Rough approximation not taking day into account yet.  This may be +1 month
  months = ((high.getFullYear() - low.getFullYear()) * 12) + (high.getMonth() - low.getMonth())
  return 0 if months is 0

  date3 = new Date(low.getTime())
  #add the number of months to the low date clone to bring it up to the current month and year
  # note however that this may push the date into the next month.  If the low date was in a month
  # with 31 days and the high date is in a month with less then 31 days this will cause the date to
  #be pushed forward into the next month.
  date3.setMonth(low.getMonth() + months)
  # If the months are equal and the adjusted dated is greater than the high date we havn't
  # reached the actual turn over day so remove a month from the count
  if date3.getMonth() == high.getMonth() && (date3.getDate() - high.getDate() > 0)
    months--

  months


module.exports.CalculateAge = class CalculateAge extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    date1 = @execArgs(ctx).toJSDate()
    date2 = new Date()
    calculateAge date1, date2, @precision

module.exports.CalculateAgeAt = class CalculateAgeAt extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    args = @execArgs(ctx)
    # Add null check
    if args[0]? && args[1]?
      date1 = args[0]
      date2 = args[1]
      # Ignore time, only focus on dates.
      date1.hour = 0
      date1.minute = 0
      date1.second = 0
      date1.millisecond = 0
      
      date1 = date1.toJSDate(true)
      date2 = date2.toJSDate(true)
      calculateAge date1, date2, @precision
    else
      null
