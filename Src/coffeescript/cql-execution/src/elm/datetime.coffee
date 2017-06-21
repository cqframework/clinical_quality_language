{ Expression } = require './expression'
{ build } = require './builder'
DT = require '../datatypes/datatypes'

module.exports.DateTime = class DateTime extends Expression
  @PROPERTIES = ['year', 'month', 'day', 'hour', 'minute', 'second', 'millisecond', 'timezoneOffset']
  constructor: (json) ->
    super
    for property in DateTime.PROPERTIES
      if json[property]? then @[property] = build json[property]

  exec: (ctx) ->
    args = ((if @[p]? then @[p].execute(ctx)) for p in DateTime.PROPERTIES)
    new DT.DateTime(args...)

module.exports.Time = class Time extends Expression
  @PROPERTIES = ['hour', 'minute', 'second', 'millisecond', 'timezoneOffset']
  constructor: (json) ->
    super
    for property in Time.PROPERTIES
      if json[property]? then @[property] = build json[property]

  exec: (ctx) ->
    args = ((if @[p]? then @[p].execute(ctx)) for p in Time.PROPERTIES)
    (new DT.DateTime(0, 1, 1, args...)).getTime()

# TODO: Update to use timestamp of request, per the spec
module.exports.Today = class Today extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.DateTime.fromDate(new Date()).getDate()

# TODO: Update to use timestamp of request, per the spec
module.exports.Now = class Now extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.DateTime.fromDate(new Date())

# TODO: Update to use timestamp of request, per the spec
module.exports.TimeOfDay = class TimeOfDay extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.DateTime.fromDate(new Date()).getTime()

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
    if date? then date.timezoneOffset else null

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

module.exports.SameOrBefore = class SameOrBefore extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    [d1, d2] = @execArgs(ctx)
    if d1? and d2? then d1.sameOrBefore(d2, @precision?.toLowerCase()) else null

# Delegated to by overloaded#After
module.exports.doAfter = (a, b, precision) ->
  a.after b, precision

# Delegated to by overloaded#Before
module.exports.doBefore = (a, b, precision) ->
  a.before b, precision

module.exports.DurationBetween = class DurationBetween extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    args = @execArgs(ctx)
    # Check to make sure args exist and that they have durationBetween functions so that they can be compared to one another
    if !args[0]? || !args[1]? || typeof args[0].durationBetween != 'function' || typeof args[1].durationBetween != 'function'
      return null
    result = args[0].durationBetween(args[1], @precision?.toLowerCase())
    if result? && result.isPoint() then result.low else result
