{ Expression, UnimplementedExpression } = require './expression'
{ equals } = require '../util/util'

module.exports.Equal = class Equal extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    equals @execArgs(ctx)...
