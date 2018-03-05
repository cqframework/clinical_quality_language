{ Expression, UnimplementedExpression } = require './expression'
{ build } = require './builder'

module.exports.Property = class Property extends Expression
  constructor: (json) ->
    super
    @scope = json.scope
    @source = build json.source
    @path = json.path

  exec: (ctx) ->
    obj = if @scope? then ctx.get(@scope) else @source
    if obj instanceof Expression then obj = obj.execute(ctx)
    val = obj?[@path] ? obj?.get?(@path)

    if !val
      parts = @path.split(".")
      curr_obj = obj
      curr_val = null
      for part in parts
        _obj = curr_obj?[part] ? curr_obj?.get?(part)
        curr_obj = if _obj instanceof Function then _obj.call(curr_obj) else _obj
      val = curr_obj ? null # convert undefined to null
    if val instanceof Function then val.call(obj) else val

module.exports.Tuple = class Tuple extends Expression
  constructor: (json) ->
    super
    @elements = for el in json.element
      name: el.name
      value: build el.value

  exec: (ctx) ->
    val = {}
    for el in @elements
      val[el.name] = el.value?.execute(ctx)
    val

module.exports.TupleElement = class TupleElement extends UnimplementedExpression

module.exports.TupleElementDefinition = class TupleElementDefinition extends UnimplementedExpression
