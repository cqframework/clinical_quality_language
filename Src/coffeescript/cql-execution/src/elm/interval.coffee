{ Expression, UnimplementedExpression } = require './expression'
{ ThreeValuedLogic } = require '../datatypes/logic'
{ build } = require './builder'
dtivl = require '../datatypes/interval'
cmp = require '../util/comparison'


module.exports.Interval = class Interval extends Expression
  constructor: (json) ->
    super
    @lowClosed = json.lowClosed
    @highClosed = json.highClosed
    @low = build(json.low)
    @high = build(json.high)

  exec: (ctx) ->
    new dtivl.Interval(@low.execute(ctx), @high.execute(ctx), @lowClosed, @highClosed)

# Equal is completely handled by overloaded#Equal

# NotEqual is completely handled by overloaded#Equal

# Delegated to by overloaded#Contains and overloaded#In
module.exports.doContains = (interval, item, precision) ->
  interval.contains item, precision

# Delegated to by overloaded#Includes and overloaded#IncludedIn
module.exports.doIncludes = doIncludes = (interval, subinterval, precision) ->
  interval.includes subinterval, precision

# Delegated to by overloaded#ProperIncludes and overloaded@ProperIncludedIn
module.exports.doProperIncludes = (interval, subinterval, precision) ->
  interval.properlyIncludes subinterval, precision

# Delegated to by overloaded#After
module.exports.doAfter = (a, b, precision) ->
  a.after b, precision

# Delegated to by overloaded#Before
module.exports.doBefore = (a, b, precision) ->
  a.before b, precision

module.exports.Meets = class Meets extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.meets(b, @precision) else null

module.exports.MeetsAfter = class MeetsAfter extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.meetsAfter(b, @precision) else null

module.exports.MeetsBefore = class MeetsBefore extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.meetsBefore(b, @precision) else null

module.exports.Overlaps = class Overlaps extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.overlaps(b, @precision) else null

module.exports.OverlapsAfter = class OverlapsAfter extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.overlapsAfter(b, @precision) else null

module.exports.OverlapsBefore = class OverlapsBefore extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.overlapsBefore(b, @precision) else null

# Delegated to by overloaded#Union
module.exports.doUnion = (a, b) ->
  a.union(b)

# Delegated to by overloaded#Except
module.exports.doExcept = (a, b) ->
  if a? and b? then a.except(b) else null

# Delegated to by overloaded#Intersect
module.exports.doIntersect = (a, b) ->
  if a? and b? then a.intersect(b) else null

module.exports.Width = class Width extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @arg.execute(ctx)?.width()

# TODO: Spec has "Begin" defined, but shouldn't it be "Start"?
module.exports.Start = class Start extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @arg.execute(ctx)?.low

module.exports.End = class End  extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @arg.execute(ctx)?.high

# TODO: Spec has "Begins" defined, but shouldn't it be "Starts"?
module.exports.Starts = class Starts extends UnimplementedExpression

module.exports.Ends = class Ends extends UnimplementedExpression

module.exports.Collapse = class Collapse extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    result = @execArgs ctx
    if result?.length > 1
      # we don't handle imprecise intervals at this time
      for a in result
        if a.low.isImprecise?() || a.high.isImprecise?()
          throw new Error("Collapse does not support imprecise dates at this time.")

      # sort intervals by start
      result.sort (a,b)->
        if typeof a.low.before == 'function'
          return -1 if a.low.before b.low
          return 1 if a.low.after b.low
        else
          return -1 if a.low < b.low
          return 1 if a.low > b.low
        0

      # collapse intervals as necessary
      collapsedIntervals = result
      result = []
      a = collapsedIntervals.shift()
      b = collapsedIntervals.shift()
      while b
        if typeof b.low.sameOrBefore == 'function'
          if b.low.sameOrBefore a.high
            a.high = b.high if b.high.after a.high
          else
            result.push a
            a = b
        else
          if b.low <= a.high
            a.high = b.high if b.high > a.high
          else
            result.push a
            a = b
        b = collapsedIntervals.shift()
      result.push a

    result
