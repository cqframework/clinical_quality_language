{ Expression } = require './expression'
{ ThreeValuedLogic } = require '../datatypes/datatypes'

module.exports.And = class And extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    ThreeValuedLogic.and @execArgs(ctx)...

module.exports.Or = class Or extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    ThreeValuedLogic.or @execArgs(ctx)...

module.exports.Not = class Not extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    ThreeValuedLogic.not @execArgs(ctx)

module.exports.Xor = class Xor extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    ThreeValuedLogic.xor @execArgs(ctx)...

module.exports.IsTrue = class IsTrue extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    true == @execArgs(ctx)

module.exports.IsFalse = class IsFalse extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    false == @execArgs(ctx)