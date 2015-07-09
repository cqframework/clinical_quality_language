{ Library } = require '../elm/library'

Function::property = (prop, desc) ->
  Object.defineProperty @prototype, prop, desc

module.exports.Context = class Context

  constructor: (@parent, @_codeService = null, @_parameters = {}) ->
    @context_values = {}
    @library_context = {}

  @property "parameters" ,
    get: -> @_parameters || @parent?.parameters
    set: (params) ->  @_parameters = params

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

  get: (identifier) ->
    @context_values[identifier] ? @parent?.get(identifier)

  set: (identifier, value) ->
    @context_values[identifier] = value

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
    throw new Excepetion("Retreives are not currently supported in Population Context")

  getLibraryContext: (library) ->
     throw new Excepetion("Library expressions are not currently supported in Population Context")
  
  get: (identifier) ->
    #First check to see if the identifier is a population context expression that has already been cached
    return @context_values[identifier] if @context_values[identifier]
    #if not look to see if the library has a population expression of that identifier 
    return @library.expressions[identifier] if @library[identifier]?.context == "Population"
    #lastley attempt to gather all patient level results that have that identifier
    # should this compact null values before return ?
    for pid,res of @results.patientResults
      res[identifier]

