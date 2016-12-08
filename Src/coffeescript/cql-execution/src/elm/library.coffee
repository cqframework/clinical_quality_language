module.exports.Library = class Library
  constructor: (json, libraryManager) ->
    @source = json
    @usings = []
    for u in json.library.usings?.def ? []
      @usings.push {"name" : u.localIdentifier, "version" : u.version} if u.localIdentifier != "System"
    @parameters = {}
    for param in json.library.parameters?.def ? []
      @parameters[param.name] = new ParameterDef(param)
    @valuesets = {}
    for valueset in json.library.valueSets?.def ? []
      @valuesets[valueset.name] = new ValueSetDef(valueset)
    @expressions = {}
    for expr in json.library.statements?.def ? []
      @expressions[expr.name] = if expr.type == "FunctionDef"  then new FunctionDef(expr) else new ExpressionDef(expr)
    @includes = {}
    for expr in json.library.includes?.def ? []
      if libraryManager then @includes[expr.localIdentifier] =  libraryManager.resolve(expr.path,expr.version)

  get: (identifier) ->
    @expressions[identifier] || @includes[identifier]

  getValueSet: (identifier) ->
    @valuesets[identifier]

  getParameter: (name) ->
    @parameters[name]
# These requires are at the end of the file because having them first in the
# file creates errors due to the order that the libraries are loaded.
{ ExpressionDef, FunctionDef, ParameterDef, ValueSetDef } = require './expressions'
{ Results } = require '../runtime/results'
