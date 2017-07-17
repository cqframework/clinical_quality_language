module.exports.Executor = class Executor

  constructor: (@library,@codeService,@parameters) ->

  withLibrary: (lib) ->
    @library = lib 
    @

  withParameters: (params) ->
    @parameters = params ? {}
    @

  withCodeService: (cs) ->
    @codeService = cs
    @

  exec_expression: (expression, patientSource) ->
    Results r = new Results()
    expr = @library.expressions[expression]
    while expr && p = patientSource.currentPatient()
      patient_ctx = new PatientContext(@library,p,@codeService,@parameters)
      r.recordPatientResult(patient_ctx, expression, expr.execute(patient_ctx))
      patientSource.nextPatient()
    r

  exec: (patientSource) ->
    Results r = @exec_patient_context(patientSource)
    popContext = new PopulationContext(@library,r,@codeService,@parameters)
    for key,expr of @library.expressions when expr.context is "Population"
       r.recordPopulationResult( key, expr.exec(popContext))
    r

  exec_patient_context: (patientSource) ->
    Results r = new Results()
    while p = patientSource.currentPatient()
      patient_ctx = new PatientContext(@library,p,@codeService,@parameters)
      for key,expr of @library.expressions when expr.context is "Patient"
        r.recordPatientResult(patient_ctx, key, expr.execute(patient_ctx))
      patientSource.nextPatient()
    r

{ Results } = require './results'
{ PopulationContext,PatientContext } = require './context'