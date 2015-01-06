{ Expression, UnimplementedExpression } = require './expression'
{ ThreeValuedLogic } = require '../datatypes/datatypes'
{ equals } = require '../util/util'

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
