{ Library } = require '../elm/library'

Function::property = (prop, desc) ->
  Object.defineProperty @prototype, prop, desc

module.exports.Context = class Context

  constructor: (@parent, @_patientSource = null, @_codeService = null, @_parameters = {}) ->
    @context_values = {}
    @library_context = {}

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
  
  getLibraryContext: (library) ->
    if (@parent instanceof Library)  
      new Context(@get(library),@patientSource,@codeService,@parameters)
    else 
      @parent?.getLibraryContext(library)
  
  getParameter: (name) ->
    @parent?.getParameter(name)

  getValueSet: (name) ->
    @parent?.getValueSet(name)

  get: (identifier) ->
    @context_values[identifier] ? @parent?.get(identifier)

  set: (identifier, value) ->
    @context_values[identifier] = value

  currentPatient: () ->
    @patientSource.currentPatient()

  nextPatient:() ->
    @patientSource.nextPatient()
