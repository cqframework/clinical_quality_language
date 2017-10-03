{ Expression } = require './expression'
{ IncompatibleTypesException } = require './quantity'
{ ThreeValuedLogic } = require '../datatypes/logic'
{ DateTime } = require '../datatypes/datetime'
{ Exception } = require '../datatypes/exception'
{ typeIsArray } = require '../util/util'
{ equals, equivalent } = require '../util/comparison'
{ build } = require './builder'
DT = require './datetime'
LIST = require './list'
IVL = require './interval'
STRING = require './string'

module.exports.Equal = class Equal extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    try
      equals @execArgs(ctx)...
    catch error
      if error instanceof IncompatibleTypesException
        return null
      else
        throw error

module.exports.Equivalent = class Equivalent extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [a, b] = @execArgs(ctx)
    if not a? or not b?
      false
    equivalent(a, b)

module.exports.NotEqual = class NotEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    try
      ThreeValuedLogic.not equals @execArgs(ctx)...
    catch error
      if error instanceof IncompatibleTypesException
        return null
      else
        throw error

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

  exec: (ctx) ->
    [operand, index] = @execArgs ctx
    if not operand? or not index? then return null
    if index < 0 or index >= operand.length then throw new ArrayIndexOutOfBoundsException()
    operand[index]

module.exports.In = class In extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [item, container] = @execArgs ctx
    if not item? or not container? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doContains(container, item, @precision)

module.exports.Contains = class Contains extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [container, item] = @execArgs ctx
    if not item? or not container? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doContains(container, item, @precision)

module.exports.Includes = class Includes extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [container, contained] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doIncludes(container, contained, @precision)

module.exports.IncludedIn = class IncludedIn extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [contained, container] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doIncludes(container, contained, @precision)

module.exports.ProperIncludes = class ProperIncludes extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [container, contained] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doProperIncludes(container, contained, @precision)

module.exports.ProperIncludedIn = class ProperIncludedIn extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [contained, container] = @execArgs ctx
    if not container? or not contained? then return null
    lib = switch
      when typeIsArray(container) then LIST
      else IVL
    lib.doProperIncludes(container, contained, @precision)

module.exports.Length = class Length extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg? then arg.length else null

module.exports.After = class After extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs(ctx)
    if not a? or not b? then return null
    lib = switch
      when a instanceof DateTime then DT
      else IVL
    lib.doAfter(a, b, @precision)

module.exports.Before = class After extends Expression
  constructor: (json) ->
    super
    @precision = json.precision?.toLowerCase()

  exec: (ctx) ->
    [a, b] = @execArgs(ctx)
    if not a? or not b? then return null
    lib = switch
      when a instanceof DateTime then DT
      else IVL
    lib.doBefore(a, b, @precision)
