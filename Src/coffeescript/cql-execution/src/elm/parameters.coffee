{ Expression } = require './expression'
{ build } = require './builder'

module.exports.ParameterDef = class ParameterDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @default = build(json.default)
    @parameterTypeSpecifier = json.parameterTypeSpecifier

  exec: (ctx) ->
    # If context parameters contains the name, return value.
    if (ctx?.parameters[@name]?)
      ctx.parameters[@name]
    # If default type exists, execute the default type
    else if @default?
      @default?.execute(ctx)
    # Else, if context and context's parent exist return the value of the parent's parameters with the given name.
    else
      ctx.getParentParameter @name

module.exports.ParameterRef = class ParameterRef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @library = json.libraryName

  exec: (ctx) ->
    ctx = if @library then ctx.getLibraryContext(@library) else ctx
    ctx.getParameter(@name)?.execute(ctx)