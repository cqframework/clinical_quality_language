{ Patient } = require './cql-patient'

DT = require './cql-datatypes'
QP = require './fhir/models'
VS = require './vsac-code-service'
cs = new VS.CodeService("rdingwell", "Test#1234","https://vsac.nlm.nih.gov/vsac/ws/Ticket", "https://vsac.nlm.nih.gov/vsac/ws/RetrieveValueSet")
cs.getProxyTicket()
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

Function::property = (prop, desc) ->
  Object.defineProperty @prototype, prop, desc

# Key Classes

class Library
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

class Context


  constructor: (@parent, @_patientSource = null, @_codeService = null, @_parameters = {}) ->
    @context_values = {}

  @property "parameters" ,
    get: -> @_parameters || @parent?.parameters
    set: (params) ->  @_parameters = params

  @property "patientSource" ,
    get: -> @_patientSource || @parent?.patientSource
    set: (ps) -> @_patientSource = ps

  @property "codeService" ,
    get: -> @_codeService || @parent?.codeService
    set: (cs) -> @_codeService = cs

  withPatients: (patientSource) ->
    @patientSource=patientSource
    @

  withParameters: (params) ->
    @parameters = params ? {}
    @

  withCodeService: (cs) ->
    @codeService = cs
    @

  rootContext:  ->
    if (@parent instanceof Library) then @ else @parent?.rootContext()

  childContext: (context_values = {}) ->
    ctx = new Context(@)
    ctx.context_values = context_values
    ctx

  getParameter: (name) ->
    @parent?.getParameter(name)

  getValueSet: (name) ->
    @parent?.getValueSet(name)

  get: (identifier) ->
      @context_values[identifier] || @parent?.get(identifier)

  set: (identifier, value) ->
    @context_values[identifier] = value

  currentPatient: () ->
    @patientSource.currentPatient()

  nextPatient:() ->
    @patientSource.nextPatient()


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
    value = @expression?.exec(ctx)
    ctx.rootContext().set @name,value
    value

class ExpressionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    value = ctx.get(@name)
    if value instanceof Expression
      value = value.exec(ctx)
    value

# ValueSets

class ValueSetDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @id = json.id
    @version = json.version
    #todo: code systems and versions

  exec: (ctx) ->
    valueset = ctx.codeService.findValueSet(@id, @version) ? new DT.ValueSet(@id, @version)
    ctx.rootContext().set @name, valueset
    valueset

class ValueSetRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    # TODO: This calls the code service every time-- should be optimized
    valueset = ctx.getValueSet(@name)
    if valueset instanceof Expression
      valueset = valueset.exec(ctx)
    valueset

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
    ctx.getParameter(@name)?.exec(ctx)

# Logical Operators

class And extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.ThreeValuedLogic.and @execArgs(ctx)...

class Or extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.ThreeValuedLogic.or @execArgs(ctx)...

class Xor extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.ThreeValuedLogic.xor @execArgs(ctx)...

class Not extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    DT.ThreeValuedLogic.not @execArgs(ctx)

class IsNull extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx) == null

# Functions


class FunctionDef extends Expression
  constructor: (json) ->
    super
    @name = json.name
    @expression = build json.expression
    @parameters = json.parameter 

  exec: (ctx) ->
    @
  

class FunctionRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    functionDef = ctx.get(@name)
    args = @execArgs(ctx)
    child_ctx = ctx.childContext()
    if args.length != functionDef.parameters.length
      thow "incorrect number of arguments supplied" 
    for p, i in functionDef.parameters 
      child_ctx.set(p.name,args[i])
    functionDef.expression.exec(child_ctx)

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

class DateTimeFunctionRef extends DateFunctionRef
  constructor: (json) ->
    super

# Comparisons
class Greater extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> DT.Uncertainty.from x
    args[0].greaterThan args[1]

class GreaterOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> DT.Uncertainty.from x
    args[0].greaterThanOrEquals args[1]

class Equal extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> DT.Uncertainty.from x
    args[0].equals args[1]

class LessOrEqual extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> DT.Uncertainty.from x
    args[0].lessThanOrEquals args[1]

class Less extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx).map (x) -> DT.Uncertainty.from x
    args[0].lessThan args[1]

# Lists and Intervals

class List extends Expression
  constructor: (json) ->
    super
    @elements = (build json.element) ? []

  exec: (ctx) ->
    (item.exec(ctx) for item in @elements)

class Exists extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx)?.length > 0

class Interval extends Expression
  constructor: (json) ->
    super
    @lowClosed = json.lowClosed
    @highClosed = json.highClosed
    @low = build(json.low)
    @high = build(json.high)

  exec: (ctx) ->
    new DT.Interval(@low.exec(ctx), @high.exec(ctx), @lowClosed, @highClosed)

class Includes extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    args = @execArgs(ctx)
    args[0].includes args[1]

class Identifier extends Expression
  constructor: (json) ->
    super
    @identifier = json

  exec: (ctx) ->
    ctx.get(@identifier)


class Start extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    # assumes this is interval
    @arg.exec(ctx).low

class Union extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    # TODO: Support intervals
    @execArgs(ctx).reduce (x, y) -> x.concat y

class Intersect extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    # TODO: Support intervals
    @execArgs(ctx).reduce (x, y) -> (itm for itm in x when itm in y)

class Distinct extends Expression
  constructor: (json) ->
    super
    @source = build json.source

  exec: (ctx) ->
    container = {}
    container[itm] = itm for itm in @source.exec(ctx)
    value for key, value of container

class SingletonFrom extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    arg = @execArgs ctx
    if arg.length > 1 then throw new Error 'IllegalArgument: \'SingletonFrom\' requires a 0 or 1 arg array'
    else if arg.length is 1 then return arg[0]
    else return null

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

class InValueSet extends Expression
  constructor: (json) ->
    super
    @code = build json.code
    @valueset = new ValueSetRef json.valueset

  exec: (ctx) ->
    code = @code.exec(ctx)
    valueset = @valueset.exec(ctx)
    if code? and valueset? then valueset.hasCode code else false

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

class Negate extends Expression
  constructor: (json) ->
    super

  exec: (ctx) ->
    @execArgs(ctx) * -1

# DateMath

class DurationBetween extends Expression
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    args = @execArgs(ctx)
    result = args[0].durationBetween(args[1], @precision?.toLowerCase())
    if result.isPoint() then result.low else result

class CalculateAgeAt extends FunctionRef
  constructor: (json) ->
    super
    @precision = json.precision

  exec: (ctx) ->
    args = @execArgs(ctx)
    date0 = args[0].toJSDate().getTime()
    date1 = args[1].toJSDate().getTime()
    ageInMS = date1 - date0
    # Not quite precise (leap years, months, etc) but close enough for now
    divisor = switch (@precision)
      when 'Year' then 1000 * 60 * 60 * 24 * 365
      when 'Month' then (1000 * 60 * 60 * 24 * 365) / 12
      when 'Day' then 1000 * 60 * 60 * 24
      when 'Hour' then 1000 * 60 * 60
      when 'Minute' then 1000 * 60
      when 'Second' then 1000
      else 1
    Math.floor(ageInMS / divisor)

class CalculateAgeInYearsAtFunctionRef extends CalculateAgeAt
  constructor: (@json) ->
    @json.precision = "Year"
    super(@json)

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

class Null extends Literal
  constructor: (json) ->
    super

  exec: (ctx) ->
    null

class Quantity extends Expression
  constructor: (json) ->
    super
    @unit = json.unit
    @value = json.value

  exec: (ctx) ->
    @

class IdentifierRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx.get(@name)  


class Property extends Expression
  constructor: (json) ->
    super
    @scope = json.scope
    @source = build json.source
    @path = json.path

  exec: (ctx) ->
    obj = if @scope? then ctx.get(@scope) else @source
    if obj instanceof Expression then obj = obj.exec(ctx)
    val = obj?[@path] ? obj?.get?(@path)

    if !val
      parts = @path.split(".")
      curr_obj = obj
      curr_val = null
      for part in parts
        _obj = curr_obj?[part] ? curr_obj?.get?(part)
        curr_obj = if _obj instanceof Function then _obj.call(curr_obj) else _obj
      val = curr_obj
    if val instanceof Function then val.call(obj) else val

class Tuple extends Expression
  constructor: (json) ->
    super
    @elements = for el in json.element
      name: el.name
      value: build el.value

  exec: (ctx) ->
    val = {}
    for el in @elements
      val[el.name] = el.value?.exec(ctx)
    val

# Retreives and Queries

class Retrieve extends Expression
  constructor: (json) ->
    super
    @datatype = json.dataType
    @templateId = json.templateId
    @codeProperty = json.codeProperty
    @codes = build json.codes
    @dateProperty = json.dateProperty
    @dateRange = build json.dateRange

  exec: (ctx) ->
    records = ctx.currentPatient()?.findRecords(@templateId)
    if @codes
      valueset = @codes.exec(ctx)
      records = (r for r in records when valueset.hasCode(r.getCode(@codeProperty)))
    if @dateRange
      range = @dateRange.exec(ctx)
      records = (r for r in records when range.includes(r.getDateOrInterval(@dateProperty)))

    records

class AliasRef extends Expression
  constructor: (json) ->
    super
    @name = json.name

  exec: (ctx) ->
    ctx?.get(@name)

class QueryDefineRef extends AliasRef
  constructor: (json) ->
    super

class With extends Expression
  constructor: (json) ->
    super
    @alias = json.alias
    @expression = build json.expression
    @suchThat = build json.suchThat
  exec: (ctx) ->
    records = @expression.exec(ctx)
    returns = for rec in records
      childCtx = ctx.childContext()
      childCtx.set @alias, rec
      @suchThat.exec(childCtx)
    returns.some (x) -> x

class Without extends With
  constructor: (json) ->
    super
  exec: (ctx) ->
    !super(ctx)

class ByExpression extends Expression
  constructor: (json) ->
    super
    @expression = build json.expression
    @direction = json.direction
    @low_order = if @direction == "asc" then -1 else 1
    @high_order = @low_order * -1

  exec: (a,b) ->
    ctx = new Context()
    ctx.context_values = a
    a_val = @expression.exec(ctx)
    ctx.context_values = b
    b_val = @expression.exec(ctx)

    if a_val == b_val
      0
    else if a_val < b_val
      @low_order
    else
      @high_order

class Sort
  constructor:(json) ->
    @by = build json?.by

  sort: (values) ->
    self = @
    if @by
      values.sort (a,b) ->
        order = 0
        for item in self.by
          order = item.exec(a,b)
          if order != 0 then break
        order

class MultiSource
  constructor: (@sources) ->
    @sources = if typeIsArray(@sources) then @sources else [@sources]
    @alias = @sources[0].alias
    @expression = build @sources[0].expression

    if @sources.length > 1
      @rest = new MultiSource(@sources.slice(1))

  aliases: ->
    a = [@alias]
    if @rest
      a = a.concat @rest.aliases()
    a

  forEach: (ctx, func) ->
    @records?= @expression.exec(ctx)
    for rec in @records
      rctx = new Context(ctx)
      rctx.set(@alias,rec)
      if @rest
        @rest.forEach(rctx,func)
      else
        func(rctx)


allTrue = (things) ->
  if typeIsArray things
    things.every (x) -> x
  else
    things

class Query extends Expression
  constructor: (json) ->
    super
    @sources = new MultiSource(json.source)
    @definitions = for d in json.define ? []
      identifier: d.identifier
      expression: build d.expression

    @relationship = build json.relationship
    @where = build json.where
    @return = build json.return?.expression
    @aliases = @sources.aliases()
    @sort = new Sort(json.sort)
  exec: (ctx) ->
    self = @
    returnedValues = []
    @sources.forEach(ctx, (rctx) ->
      for def in self.definitions
        rctx.set def.identifier, def.expression.exec(rctx)

      relations = for rel in self.relationship
        child_ctx = rctx.childContext()
        rel.exec(child_ctx)
      passed = allTrue(relations)
      passed = passed && if self.where then self.where.exec(rctx) else passed
      if passed
        if self.return
          val = self.return.exec(rctx)
          if returnedValues.indexOf(val) == -1
            returnedValues.push val
        else
          if self.aliases.length == 1
            returnedValues.push rctx.get(self.aliases[0])
          else
            returnedValues.push rctx.context_values
    )

    @sort?.sort(returnedValues)
    returnedValues



module.exports.Library = Library
module.exports.Context = Context
module.exports.Results = Results
