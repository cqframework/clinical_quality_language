module.exports.Library = class Library
  constructor: (json) ->
    @parameters = {}
    for param in json.library.parameters?.def ? []
      @parameters[param.name] = new ParameterDef(param)
    @valuesets = {}
    for valueset in json.library.valueSets?.def ? []
      @valuesets[valueset.name] = new ValueSetDef(valueset)
    @expressions = {}
    for expr in json.library.statements?.def ? []
      @expressions[expr.name] = if expr.type == "FunctionDef"  then new FunctionDef(expr) else new ExpressionDef(expr)

  get: (identifier) ->
    @expressions[identifier]

  getValueSet: (identifier) ->
    @valuesets[identifier]

  getParameter: (name) ->
    @parameters[name]

  exec: (ctx) ->
    Results r = new Results()
    while p = ctx.currentPatient()
      patient_ctx = ctx.childContext()
      for key,expr of @expressions when expr.context is "Patient"
        r.recordPatientResult(patient_ctx.currentPatient().id(), key, expr.exec(patient_ctx))
      ctx.nextPatient()
    r

  execWithoutPatients: (ctx) ->
    r = {}
    for key,expr of @expressions
      r[key] = expr.exec(ctx)
    r

# These requires are at the end of the file because having them first in the
# file creates errors due to the order that the libraries are loaded.
{ ExpressionDef, FunctionDef, ParameterDef, ValueSetDef } = require './expressions'
{ Results } = require '../runtime/results'
