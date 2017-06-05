{ Library } = require '../elm/library'
{ Exception } = require '../datatypes/exception'
{ typeIsArray } = require '../util/util'
util = require 'util'
Function::property = (prop, desc) ->
  Object.defineProperty @prototype, prop, desc

module.exports.Context = class Context

  constructor: (@parent, @_codeService = null, @_parameters = {}) ->
    @context_values = {}
    @library_context = {}

  @property "parameters" ,
    get: -> @_parameters || @parent?.parameters
    set: (params) ->
      @checkParameters(params)
      @_parameters = params

  @property "codeService" ,
    get: -> @_codeService || @parent?.codeService
    set: (cs) -> @_codeService = cs


  withParameters: (params) ->
    @parameters = params ? {}
    @

  withCodeService: (cs) ->
    @codeService = cs
    @

  rootContext:  ->
    if ( @parent ) then @parent.rootContext() else @

  findRecords: ( profile) ->
    @parent?.findRecords(profile)

  childContext: (context_values = {}) ->
    ctx = new Context(@)
    ctx.context_values = context_values
    ctx

  getLibraryContext: (library) ->
    @parent?.getLibraryContext(library)

  getParameter: (name) ->
    @parent?.getParameter(name)

  getValueSet: (name) ->
    @parent?.getValueSet(name)

  getCodeSystem: (name) ->
    @parent?.getCodeSystem(name)

  getCode: (name) ->
    @parent?.getCode(name)

  getConcept: (name) ->
    @parent?.getConcept(name)

  get: (identifier) ->
    # Check for undefined because if its null, we actually *do* want to return null (rather than looking at parent),
    # but if it's really undefined, *then* look at the parent
    if typeof @context_values[identifier] isnt 'undefined'
      @context_values[identifier]
    else
      @parent?.get(identifier)

  set: (identifier, value) ->
    @context_values[identifier] = value

  checkParameters: (params) ->
    for pName, pVal of params
      console.log(util.inspect(@getParameter(pName), { colors: true, depth: null }))
      pDef = @getParameter(pName)
      if ! pVal?
        return # Null can theoretically be any type
      if typeof pDef is "undefined"
        throw new Error("Passed in parameter '#{pName}' is not a named parameter in the library")
      else if pDef.parameterTypeSpecifier? && !@isRightType(pVal, pDef.parameterTypeSpecifier)
        throw new Error("Passed in parameter '#{pName}' is wrong type")
    true

  isRightType: (val, typeSpecifier) ->
    switch typeSpecifier.type
      when "NamedTypeSpecifier" then @isNamedType(val, typeSpecifier.name)
      when "ListTypeSpecifier" then @isListType(val, typeSpecifier.elementType)
      when "TupleTypeSpecifier" then @isTupleType(val, typeSpecifier.element)
      when "IntervalTypeSpecifier" then @isIntervalType(val, typeSpecifier.pointType)

  isListType: (val, elementType) ->
    thiz = @
    typeIsArray(val) && val.every (x) -> thiz.isRightType(x, elementType)

  isTupleType: (val, elements) ->
    thiz = @
    typeof val is "object" &&
      ! typeIsArray(val) &&
      elements.every (x) -> (typeof val[x.name] is "undefined" || thiz.isRightType(val[x.name], x.type))

  isIntervalType: (val, pointType) ->
    val.constructor?.name is "Interval" &&
      ((! val.low?) || @isRightType(val.low, pointType)) &&
      ((! val.high?) || @isRightType(val.high, pointType))

  isNamedType: (val, typeName) ->
    # Allow null to always match
    if val is null
      return true
    # It's non-null so check against know / supported types
    switch typeName
      when "{urn:hl7-org:elm-types:r1}Boolean" then typeof val is "boolean"
      when "{urn:hl7-org:elm-types:r1}Decimal" then typeof val is "number"
      when "{urn:hl7-org:elm-types:r1}Integer" then typeof val is "number" && Math.floor(val) == val
      when "{urn:hl7-org:elm-types:r1}String" then typeof val is "string"
      when "{urn:hl7-org:elm-types:r1}Concept" then val?.constructor?.name is 'Concept'
      when "{urn:hl7-org:elm-types:r1}DateTime" then val?.constructor?.name is 'DateTime'
      when "{urn:hl7-org:elm-types:r1}Quantity" then val?.constructor?.name is 'Quantity'
      when "{urn:hl7-org:elm-types:r1}Time" then val?.constructor?.name is 'DateTime' && val.isTime()
      else true # TODO: Better checking of custom or complex types

module.exports.PatientContext = class PatientContext extends Context
  constructor: (@library,@patient,codeService,parameters) ->
    super(@library,codeService,parameters)

  rootContext:  -> @

  getLibraryContext: (library) ->
    @library_context[library] ||= new PatientContext(@get(library),@patient,@codeService,@parameters)

  findRecords: ( profile) ->
    @patient?.findRecords(profile)



module.exports.PopulationContext = class PopulationContext extends Context

  constructor: (@library, @results, codeService, parameters) ->
    super(@library,codeService,parameters)

  rootContext:  -> @

  findRecords: (template) ->
    throw new Exception("Retreives are not currently supported in Population Context")

  getLibraryContext: (library) ->
    throw new Exception("Library expressions are not currently supported in Population Context")

  get: (identifier) ->
    #First check to see if the identifier is a population context expression that has already been cached
    return @context_values[identifier] if @context_values[identifier]
    #if not look to see if the library has a population expression of that identifier
    return @library.expressions[identifier] if @library[identifier]?.context == "Population"
    #lastley attempt to gather all patient level results that have that identifier
    # should this compact null values before return ?
    for pid,res of @results.patientResults
      res[identifier]

