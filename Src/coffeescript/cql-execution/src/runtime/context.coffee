{ Library } = require '../elm/library'
{ Exception } = require '../datatypes/exception'
{ typeIsArray } = require '../util/util'
util = require 'util'
Function::property = (prop, desc) ->
  Object.defineProperty @prototype, prop, desc

module.exports.Context = class Context

  constructor: (@parent, @_codeService = null, _parameters = {}) ->
    @context_values = {}
    @library_context = {}
    @localId_context = {}
    # TODO: If there is an issue with number of parameters look into cql4browsers fix: 387ea77538182833283af65e6341e7a05192304c
    @checkParameters(_parameters) # not crazy about possibly throwing an error in a constructor, but...
    @_parameters = _parameters

  @property "parameters" ,
    get: -> 
      p = @parent?.parameters
      for k, v of p
        # If key (of parent) is not found in current parameters, add it 
        if !(k of @_parameters)
          @_parameters[k] = v
      @_parameters
    
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

  getLocalIdContext: (localId) ->
    @parent?.getLocalIdContext(localId)

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

  setLocalIdWithResult: (localId, value) ->
    # Temporary fix. Real fix will be to return a list of all result values for a given localId.
    if @localId_context[localId]?
      # If boolean values, store the result of an OR on the preexisting value and the new value
      if typeof value is 'boolean'
        @localId_context[localId] = @localId_context[localId] || value
      # Knowing that the value is an array. Check the length of each, store the larger array.
      else if Array.isArray(value) && Array.isArray(@localId_context[localId])
        @localId_context[localId] = if @localId_context[localId].length > value.length then @localId_context[localId] else value
      # Check if value is null or undefined. Ignore if either.
      else if value?
        @localId_context[localId] = value
    else
      @localId_context[localId] = value

  getLocalIdResult: (localId) ->
    @localId_context[localId]

  # Returns an object of objects containing each library name
  # with the localIds and result values
  getAllLocalIds: ->
    localIdResults = {}
    # Add the localIds and result values from the main library
    localIdResults[@parent.source.library.identifier.id] = {}
    localIdResults[@parent.source.library.identifier.id] = @localId_context

    # Iterate over support libraries and store localIds
    for libName, lib of @library_context
      @supportLibraryLocalIds lib, localIdResults
    localIdResults

  # Recursive function that will grab nested support library localId results
  supportLibraryLocalIds: (lib, localIdResults) ->
    # Set library identifier name as the key and the object of localIds with their results as the value
    localIdResults[lib.library.source.library.identifier.id] = lib.localId_context
    # Iterate over any support libraries in the current support library
    for supportLibName, supportLib of lib.library_context
      @supportLibraryLocalIds supportLib, localIdResults

  checkParameters: (params) ->
    for pName, pVal of params
      pDef = @getParameter(pName)
      if ! pVal?
        return # Null can theoretically be any type
      if typeof pDef is "undefined"
        return # This will happen if the parameter is declared in a different (included) library
      else if pDef.parameterTypeSpecifier? && !@matchesTypeSpecifier(pVal, pDef.parameterTypeSpecifier)
        throw new Error("Passed in parameter '#{pName}' is wrong type")
      else if pDef['default']? && !@matchesInstanceType(pVal, pDef['default'])
        throw new Error("Passed in parameter '#{pName}' is wrong type")
    true

  matchesTypeSpecifier: (val, spec) ->
    switch spec.type
      when "NamedTypeSpecifier" then @matchesNamedTypeSpecifier(val, spec)
      when "ListTypeSpecifier" then @matchesListTypeSpecifier(val, spec)
      when "TupleTypeSpecifier" then @matchesTupleTypeSpecifier(val, spec)
      when "IntervalTypeSpecifier" then @matchesIntervalTypeSpecifier(val, spec)
      else true # default to true when we don't know

  matchesListTypeSpecifier: (val, spec) ->
    typeIsArray(val) && val.every (x) => @matchesTypeSpecifier(x, spec.elementType)

  matchesTupleTypeSpecifier: (val, spec) ->
    typeof val is "object" &&
      ! typeIsArray(val) &&
      spec.element.every (x) => (typeof val[x.name] is "undefined" || @matchesTypeSpecifier(val[x.name], x.type))

  matchesIntervalTypeSpecifier: (val, spec) ->
    val.constructor?.name is "Interval" &&
      ((! val.low?) || @matchesTypeSpecifier(val.low, spec.pointType)) &&
      ((! val.high?) || @matchesTypeSpecifier(val.high, spec.pointType))

  matchesNamedTypeSpecifier: (val, spec) ->
    switch spec.name
      when "{urn:hl7-org:elm-types:r1}Boolean" then typeof val is "boolean"
      when "{urn:hl7-org:elm-types:r1}Decimal" then typeof val is "number"
      when "{urn:hl7-org:elm-types:r1}Integer" then typeof val is "number" && Math.floor(val) == val
      when "{urn:hl7-org:elm-types:r1}String" then typeof val is "string"
      when "{urn:hl7-org:elm-types:r1}Concept" then val?.constructor?.name is 'Concept'
      when "{urn:hl7-org:elm-types:r1}DateTime" then val?.constructor?.name is 'DateTime'
      when "{urn:hl7-org:elm-types:r1}Quantity" then val?.constructor?.name is 'Quantity'
      when "{urn:hl7-org:elm-types:r1}Time" then val?.constructor?.name is 'DateTime' && val.isTime()
      else true # TODO: Better checking of custom or complex types

  matchesInstanceType: (val, inst) ->
    switch inst.constructor?.name
      when "BooleanLiteral" then typeof val is "boolean"
      when "DecimalLiteral" then typeof val is "number"
      when "IntegerLiteral" then typeof val is "number" && Math.floor(val) == val
      when "StringLiteral" then typeof val is "string"
      when "Concept" then val?.constructor?.name is "Concept"
      when "DateTime" then val?.constructor?.name is "DateTime"
      when "Quantity" then val?.constructor?.name is "Quantity"
      when "Time" then val?.constructor?.name is "DateTime" && val.isTime()
      when "List" then @matchesListInstanceType(val, inst)
      when "Tuple" then @matchesTupleInstanceType(val, inst)
      when "Interval" then @matchesIntervalInstanceType(val, inst)
      else true # default to true when we don't know for sure

  matchesListInstanceType: (val, list) ->
    typeIsArray(val) && val.every (x) => @matchesInstanceType(x, list.elements[0])

  matchesTupleInstanceType: (val, tpl) ->
    typeof val is "object" &&
      ! typeIsArray(val) &&
      tpl.elements.every (x) => (typeof val[x.name] is "undefined" || @matchesInstanceType(val[x.name], x.value))

  matchesIntervalInstanceType: (val, ivl) ->
    pointType = ivl.low ? ivl.high
    val.constructor?.name is "Interval" &&
      ((! val.low?) || @matchesInstanceType(val.low, pointType)) &&
      ((! val.high?) || @matchesInstanceType(val.high, pointType))

module.exports.PatientContext = class PatientContext extends Context
  constructor: (@library,@patient,codeService,parameters) ->
    super(@library,codeService,parameters)

  rootContext:  -> @

  getLibraryContext: (library) ->
    @library_context[library] ||= new PatientContext(@get(library),@patient,@codeService,@parameters)
    
  getLocalIdContext: (localId) ->
    @localId_context[localId] ||= new PatientContext(@get(library),@patient,@codeService,@parameters)

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

