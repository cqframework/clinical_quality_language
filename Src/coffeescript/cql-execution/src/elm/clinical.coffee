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
    # spec indicates to return null if code is null, false is value set is null
    return null unless @code?
    return false unless @valueset?
    code = @code.execute(ctx)
    # spec indicates to return null if code is null, false is value set is null
    return null unless code?
    valueset = @valueset.execute(ctx)
    if valueset? then valueset.hasMatch code else false

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

module.exports.CalculateAge = class CalculateAge extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    date1 = @execArgs(ctx)
    date2 = dt.DateTime.fromDate(new Date())
    result = date1?.durationBetween(date2, @precision.toLowerCase())
    if result? && result.isPoint() then result.low else result

module.exports.CalculateAgeAt = class CalculateAgeAt extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    [date1, date2] = @execArgs(ctx)
    if date1? && date2?
      result = date1.durationBetween(date2, @precision.toLowerCase())
      if result? && result.isPoint() then result.low else result
    else
      null
