{ Expression, UnimplementedExpression } = require './expression'
{ FunctionRef } = require './reusable'
{ build } = require './builder'
DT = require '../datatypes/datatypes'

module.exports.DateTime = class DateTime extends Expression
  @PROPERTIES = ['year', 'month', 'day', 'hour', 'minute', 'second', 'millisecond', 'timezoneOffset']
  constructor: (json) ->
    super
    for property in DateTime.PROPERTIES
      if json[property]? then @[property] = build json[property]

  exec: (ctx) ->
    args = ((if @[p]? then @[p].exec(ctx)) for p in DateTime.PROPERTIES)
    new DT.DateTime(args...)

# TODO: Remove functionref when ELM does DateTime natively
module.exports.DateTimeFunctionRef = class DateTimeFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    properties = ['year', 'month', 'day', 'hour', 'minute', 'second', 'millisecond', 'timezoneOffset']
    dateJson = {
      "type" : "DateTime"
    }
    for arg, i in json.operand
      dateJson[properties[i]] = arg
    @datetime = new DateTime(dateJson)

  exec: (ctx) ->
    @datetime.exec(ctx)

module.exports.Today = class Today extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.DateTime.fromDate(new Date()).getDate()

# TODO: Remove functionref when ELM does Today natively
module.exports.TodayFunctionRef = class TodayFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @today = new Today({
      "type" : "Today"
    })

  exec: (ctx) ->
    @today.exec(ctx)

module.exports.Now = class Now extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.DateTime.fromDate(new Date())

# TODO: Remove functionref when ELM does Now natively
module.exports.NowFunctionRef = class NowFunctionRef extends FunctionRef
  constructor: (json) ->
    super
    @now = new Now({
      "type" : "Now"
    })

  exec: (ctx) ->
    @now.exec(ctx)

module.exports.DateTimeComponentFrom = class DateTimeComponentFrom extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    arg = @execArgs(ctx)
    if arg? then arg[@precision.toLowerCase()] else null

module.exports.DateFrom = class DateFrom extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    date = @execArgs(ctx)
    if date? then date.getDate() else null

module.exports.TimeFrom = class TimeFrom extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    date = @execArgs(ctx)
    if date? then date.getTime() else null

module.exports.TimezoneFrom = class TimezoneFrom extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    date = @execArgs(ctx)
    if date? then date.timeZoneOffset else null

module.exports.SameAs = class SameAs extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    [d1, d2] = @execArgs(ctx)
    if d1? and d2? then d1.sameAs(d2, @precision?.toLowerCase()) else null

module.exports.SameOrAfter = class SameOrAfter extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    [d1, d2] = @execArgs(ctx)
    if d1? and d2? then d1.sameOrAfter(d2, @precision?.toLowerCase()) else null

module.exports.SameOrBefore = class SameOrAfter extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    [d1, d2] = @execArgs(ctx)
    if d1? and d2? then d1.sameOrBefore(d2, @precision?.toLowerCase()) else null

module.exports.After = class After extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    [d1, d2] = @execArgs(ctx)
    if d1? and d2? then d1.after(d2, @precision?.toLowerCase()) else null


module.exports.Before = class Before extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    [d1, d2] = @execArgs(ctx)
    if d1? and d2? then d1.before(d2, @precision?.toLowerCase()) else null

module.exports.DurationBetween = class DurationBetween extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    args = @execArgs(ctx)
    result = args[0].durationBetween(args[1], @precision?.toLowerCase())
    if result.isPoint() then result.low else result
