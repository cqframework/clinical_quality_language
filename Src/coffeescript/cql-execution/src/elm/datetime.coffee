{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
DT = require '../datatypes/datatypes'

module.exports.DateTimeComponentFrom = class DateTimeComponentFrom extends UnimplementedExpression

module.exports.Today = class Today extends UnimplementedExpression

module.exports.Now = class Now extends UnimplementedExpression

module.exports.DateTime = class DateTime extends UnimplementedExpression

module.exports.DateFrom = class DateFrom extends UnimplementedExpression

module.exports.TimeFrom = class TimeFrom extends UnimplementedExpression

module.exports.TimezoneFrom = class TimezoneFrom extends UnimplementedExpression

module.exports.SameAs = class SameAs extends UnimplementedExpression

module.exports.SameOrAfter = class SameOrAfter extends UnimplementedExpression

module.exports.SameOrBefore = class SameOrBefore extends UnimplementedExpression

module.exports.Before = class Before extends UnimplementedExpression

module.exports.After = class After extends UnimplementedExpression

# TODO: Should these still be functionrefs?
module.exports.DateTimeFunctionRef = class DateTimeFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    new DT.DateTime(@execArgs(ctx)...)

# TODO: Deprecate
module.exports.DateFunctionRef = class DateFunctionRef extends DateTimeFunctionRef
  constructor: (json) ->
    super

# TODO: Not in ELM Spec
module.exports.DurationBetween = class DurationBetween extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    args = @execArgs(ctx)
    result = args[0].durationBetween(args[1], @precision?.toLowerCase())
    if result.isPoint() then result.low else result
