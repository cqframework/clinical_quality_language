{ Expression } = require './expression'
{ build } = require './builder'

module.exports.ExpressionDef = class ExpressionDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @context = json.context
    @expression = build json.expression
  exec: (ctx) ->
    value = @expression?.execute(ctx)
    ctx.rootContext().set @name,value
    value

module.exports.ExpressionRef = class ExpressionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @library = json.libraryName
  exec: (ctx) ->
    ctx = if @library then ctx.getLibraryContext(@library) else ctx
    value = ctx.get(@name)
    if value instanceof Expression
      value = value.execute(ctx)
    value

module.exports.FunctionDef = class FunctionDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @expression = build json.expression
    @parameters = json.operand
  exec: (ctx) ->
    @

module.exports.FunctionRef = class FunctionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @library = json.libraryName
  exec: (ctx) ->
    functionDef = if @library then ctx.get(@library)?.get(@name) else ctx.get(@name)
    args = @execArgs(ctx)
    child_ctx = if @library then ctx.getLibraryContext(@library)?.childContext() else ctx.childContext()
    if args.length != functionDef.parameters.length
      throw new Error("incorrect number of arguments supplied")
    for p, i in functionDef.parameters
      child_ctx.set(p.name,args[i])
    functionDef.expression.execute(child_ctx)

module.exports.OperandRef = class OperandRef extends Expression
  constructor: (json) ->
    @name = json.name
  exec: (ctx) ->
    ctx.get(@name) 

     
module.exports.IdentifierRef = class IdentifierRef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @library = json.libraryName
  exec: (ctx) ->
    # TODO: Technically, the ELM Translator should never output one of these
    # but this code is needed since it does, as a work-around to get queries
    # to work properly when sorting by a field in a tuple
    val = if @library then ctx.get(@library)?.get(@name) else ctx.get(@name)

    if not val?
      parts = @name.split(".")
      val = ctx.get(part)
      if val? and parts.length > 1
        curr_obj = val
        curr_val = null
        for part in parts[1..]
          _obj = curr_obj?[part] ? curr_obj?.get?(part)
          curr_obj = if _obj instanceof Function then _obj.call(curr_obj) else _obj
        val = curr_obj
    if val instanceof Function then val.call(ctx.context_values) else val
