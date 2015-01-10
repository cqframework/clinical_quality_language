{ Expression, UnimplementedExpression } = require './expression'
{ ThreeValuedLogic } = require '../datatypes/datatypes'
{ Exception } = require '../datatypes/exception'
{ equals, typeIsArray } = require '../util/util'
{ build } = require './builder'
LIST = require './list'
IVL = require './interval'
STRING = require './string'

module.exports.Equal = class Equal extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    equals @execArgs(ctx)...

module.exports.NotEqual = class NotEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    ThreeValuedLogic.not equals @execArgs(ctx)...

module.exports.Union = class Union extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if not a? or not b? then return null
    lib = switch
      when typeIsArray(a) then LIST
      else IVL
    lib.doUnion(a, b)

module.exports.Except = class Except extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if not a? or not b? then return null
    lib = switch
      when typeIsArray(a) then LIST
      else IVL
    lib.doExcept(a, b)

module.exports.Intersect = class Intersect extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs ctx
    if not a? or not b? then return null
    lib = switch
      when typeIsArray(a) then LIST
      else IVL
    lib.doIntersect(a, b)

module.exports.ArrayIndexOutOfBoundsException = ArrayIndexOutOfBoundsException = class ArrayIndexOutOfBoundsException extends Exception

module.exports.Indexer = class Indexer extends Expression
  constructor: (json) ->
    super
    @index = build json.index

  exec: (ctx) ->
    operand = @execArgs ctx
    index = @index.exec ctx
    if not operand? or not index? then return null
    if index <= 0 or index > operand.length then throw new ArrayIndexOutOfBoundsException()
    operand[index-1]

module.exports.In = class In extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [item, container] = @execArgs ctx
    if not item? or not container? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doIn(item, container)

module.exports.Contains = class Contains extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [container, item] = @execArgs ctx
    if not item? or not container? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doIn(item, container)

module.exports.Includes = class Includes extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [container, contained] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doIncludes(container, contained)

module.exports.IncludedIn = class IncludedIn extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [contained, container] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doIncludes(container, contained)

module.exports.ProperIncludes = class ProperIncludes extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [container, contained] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doProperIncludes(container, contained)

module.exports.ProperIncludedIn = class ProperIncludedIn extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [contained, container] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doProperIncludes(container, contained)
