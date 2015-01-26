{ Expression, UnimplementedExpression } = require './expression'
{ ThreeValuedLogic } = require '../datatypes/logic'
{ build } = require './builder'
DT = require '../datatypes/datatypes'
cmp = require '../util/comparison'


module.exports.Interval = class Interval extends Expression
  constructor: (json) ->
    super
    @lowClosed = json.lowClosed
    @highClosed = json.highClosed
    @low = build(json.low)
    @high = build(json.high)

  exec: (ctx) ->
    new DT.Interval(@low.exec(ctx), @high.exec(ctx), @lowClosed, @highClosed)

# Equal is completely handled by overloaded#Equal

# NotEqual is completely handled by overloaded#Equal

# Delegated to by overloaded#Contains and overloaded#In
module.exports.doContains = (interval, item) ->
  interval.contains item

# Delegated to by overloaded#Includes and overloaded#IncludedIn
module.exports.doIncludes = doIncludes = (interval, subinterval) ->
  interval.includes subinterval

# Delegated to by overloaded#ProperIncludes and overloaded@ProperIncludedIn
module.exports.doProperIncludes = (interval, subinterval) ->
  ThreeValuedLogic.and(
    cmp.greaterThan(interval.width(), subinterval.width()),
    doIncludes(interval, subinterval)
  )

# Delegated to by overloaded#After
module.exports.doAfter = (a, b, precision) ->
  a.after b, precision

# Delegated to by overloaded#Before
module.exports.doBefore = (a, b, precision) ->
  a.before b, precision

module.exports.Meets = class Meets extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.meets b else null

module.exports.MeetsAfter = class MeetsAfter extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.meetsAfter b else null

module.exports.MeetsBefore = class MeetsBefore extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.meetsBefore b else null

module.exports.Overlaps = class Overlaps extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.overlaps b else null

module.exports.OverlapsAfter = class OverlapsAfter extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.overlapsAfter b else null

module.exports.OverlapsBefore = class OverlapsBefore extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if a? and b? then a.overlapsBefore b else null

# Delegated to by overloaded#Union
module.exports.doUnion = (a, b) ->
  throw new Error("Not Implemented")

# Delegated to by overloaded#Except
module.exports.doExcept = (a, b) ->
  throw new Error("Not Implemented")

# Delegated to by overloaded#Intersect
module.exports.doIntersect = (a, b) ->
  throw new Error("Not Implemented")

module.exports.Width = class Width extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @arg.exec(ctx).width()

# TODO: Spec has "Begin" defined, but shouldn't it be "Start"?
module.exports.Start = class Start extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @arg.exec(ctx).low

module.exports.End = class End extends UnimplementedExpression

# TODO: Spec has "Begins" defined, but shouldn't it be "Starts"?
class Starts extends UnimplementedExpression

module.exports.Ends = class Ends extends UnimplementedExpression

module.exports.Ends = class Collapse extends UnimplementedExpression

module.exports.Ends = class Width extends UnimplementedExpression
