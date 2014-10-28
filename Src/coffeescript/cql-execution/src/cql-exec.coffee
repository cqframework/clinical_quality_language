{ Patient } = require './cql-patient'
DT = require './cql-datatypes'

typeIsArray = Array.isArray || ( value ) -> return {}.toString.call( value ) is '[object Array]'

functionExists = (name) -> eval("typeof #{name}") is "function"

constructByName = (name, json) -> eval("new #{name}(json)")

build = (json) ->
  if not json? then return json

  if (typeIsArray json)
    return (build child for child in json)

  if json.type is "FunctionRef" then new buildFunctionRef(json)
  else if json.type is "Literal" then buildLiteral(json)
  else if functionExists(json.type) then constructByName(json.type, json)
  else null

buildFunctionRef = (json) ->
  if functionExists("#{json.name}FunctionRef") then constructByName("#{json.name}FunctionRef", json)
  else new FunctionRef(json)

buildLiteral = (json) ->
  switch(json.valueType)
    when "{http://www.w3.org/2001/XMLSchema}bool" then new BooleanLiteral(json)
    when "{http://www.w3.org/2001/XMLSchema}int" then new IntegerLiteral(json)
    when "{http://www.w3.org/2001/XMLSchema}decimal" then new DecimalLiteral(json)
    when "{http://www.w3.org/2001/XMLSchema}string" then new StringLiteral(json)
    else new Literal(json)

# Key Classes

class Library
  constructor: (json) ->
    @parameters = {}
    for param in json.library.parameters?.def ? []
      @parameters[param.name] = new ParameterDef(param)
    @expressions = {}
    for expr in json.library.statements?.def ? []
      @expressions[expr.name] = new ExpressionDef(expr)

  exec: (ctx) ->
    Results r = new Results()
    while ctx.currentPatient()
      for key,expr of @expressions when expr.context is "PATIENT"
        r.recordPatientResult(ctx.currentPatient().id, key, expr.exec(ctx))
      ctx.nextPatient()
    r

class Context
  constructor: (@measure, patients = [], @parameters = {}, @codeService) ->
    @withPatients patients
    @patientIndex = 0

  withPatients: (patients = []) ->
    @patients = (new Patient(p) for p in patients)
    @

  withParameters: (p) ->
    @parameters = p ? {}
    @

  withCodeService: (cs) ->
    @codeService = cs
    @

  currentPatient: () ->
    if @patientIndex < @patients.length then @patients[@patientIndex] else null

  nextPatient:() ->
    @patientIndex++
    @currentPatient()

class Results
  constructor: () ->
    @patientResults = {}
    @populationResults = {}

  recordPatientResult: (patientId, resultName, result) ->
    @patientResults[patientId] ?= {}
    @patientResults[patientId][resultName] = result

  recordPopulationResult: (resultName, result) ->
    @populationResults[resultName] = result

# Expressions

class Expression
  constructor: (json) ->
    if json.operand?
      op = build(json.operand)
      if typeIsArray(json.operand) then @args = op else @arg = op

  exec: (ctx) ->
    this

  execArgs: (ctx) ->
    switch
      when @args? then (arg.exec(ctx) for arg in @args)
      when @arg? then @arg.exec(ctx)
      else null

class ExpressionDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @context = json.context
    @expression = build(json.expression)

  exec: (ctx) ->
    @expression.exec(ctx)

class ExpressionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx.measure.expressions[@name]?.exec(ctx)

# Parameters

class ParameterDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @default = build(json.default)

  exec: (ctx) ->
    if (ctx?.parameters[@name]?) then ctx.parameters[@name]
    else @default?.exec(ctx)

class ParameterRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx.measure.parameters[@name]?.exec(ctx)

# Logical Operators

class And extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    results = @execArgs(ctx)
    results.reduce (a,b) -> a and b

class Or extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    results = @execArgs(ctx)
    results.reduce (a,b) -> a or b

class Xor extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    results = @execArgs(ctx)
    results.reduce (a,b) -> (!a ^ !b) is 1

# Functions

class FunctionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

class AgeAtFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    date = @execArgs(ctx)[0].toJSDate()
    ageInMS = date.getTime() - ctx.currentPatient().birthdate.toJSDate().getTime()
    # Doesn't account for leap year, but close enough for now
    Math.floor(ageInMS / (1000 * 60 * 60 * 24 * 365))

class CodeFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    new DT.Code(@execArgs(ctx)...)

class InValueSetFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    item = args[0]
    valueSet = ctx.codeService.findValueSet(args[1..]...)
    if valueSet? then valueSet.hasCode item else false

class DateFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    new DT.DateTime(@execArgs(ctx)...)

class ValueSetFunctionRef extends FunctionRef
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    ctx.codeService.findValueSet(args...) ? new DT.ValueSet(args...)

# Comparisons
class Greater extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    args[0] > args[1]

class GreaterOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    args[0] >= args[1]

class Equal extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    args[0] == args[1]

class LessOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    args[0] <= args[1]

class Less extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    args[0] < args[1]

# Lists and Intervals

class List extends Expression
  constructor: (json) ->
    super
    @elements = (build json.element) ? []

  exec: (ctx) ->
    (item.exec(ctx) for item in @elements)

class IsNotEmpty extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx)?.length > 0

class Interval extends Expression
  constructor: (json) ->
    super
    @beginOpen = json.beginOpen
    @endOpen = json.endOpen
    @begin = build(json.begin)
    @end = build(json.end)

  exec: (ctx) ->
    new DT.Interval(@begin.exec(ctx), @end.exec(ctx), @beginOpen, @endOpen)

class Begin extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    # assumes this is interval
    @arg.exec(ctx).begin

# Membership

class In extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    [item, container] = @execArgs(ctx)

    switch
      when typeIsArray container
        return item in container
      when container instanceof DT.ValueSet
        return container.hasCode item

# Math

class Add extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x + y

class Subtract extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x - y

class Multiply extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x * y

class Divide extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx).reduce (x,y) -> x / y

# Literals

class Literal extends Expression
  constructor: (json) ->
    super
    @valueType = json.valueType
    @value = json.value

  exec: (ctx) ->
    @value

class BooleanLiteral extends Literal
  constructor: (json) ->
    super
    @value = @value is 'true'

  exec: (ctx) ->
    @value

class IntegerLiteral extends Literal
  constructor: (json) ->
    super
    @value = parseInt(@value, 10)

  exec: (ctx) ->
    @value

class DecimalLiteral extends Literal
  constructor: (json) ->
    super
    @value = parseFloat(@value)

  exec: (ctx) ->
    @value

class StringLiteral extends Literal
  constructor: (json) ->
    super

  exec: (ctx) ->
    @value

# Clinical Requests and Queries

class ClinicalRequest extends Expression
  constructor: (json) ->
    super
    @datatype = json.dataType
    @codeProperty = json.codeProperty
    @codes = build json.codes
    @dateProperty = json.dateProperty
    @dateRange = build json.dateRange

  exec: (ctx) ->
    if @datatype[...21] is '{http://org.hl7.fhir}' then name = @datatype[21..]
    else name = @datatype

    records = ctx.currentPatient()?.findRecords([name])
    if @codes
      valueset = @codes.exec(ctx)
      records = (r for r in records when valueset.hasCode(r.getCode(@codeProperty)))
    if @dateRange
      range = @dateRange.exec(ctx)
      records = (r for r in records when range.includes(r.getDateOrInterval(@dateProperty)))

    records

class Query extends Expression
  constructor: (json) ->
    super
    @sourceAlias = json.source.alias
    @source = build json.source.expression
    @relationship = build json.relationship

  exec: (ctx) ->
    @source.exec(ctx)

module.exports.Library = Library
module.exports.Context = Context
module.exports.Results = Results