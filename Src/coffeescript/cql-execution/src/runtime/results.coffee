module.exports.Results = class Results
  constructor: () ->
    @patientResults = {}
    @populationResults = {}
    @localIdPatientResultsMap = {}

  recordPatientResult: (patient_ctx, resultName, result) ->
    patientId = patient_ctx.patient.id()
    @patientResults[patientId] ?= {}
    @patientResults[patientId][resultName] = result
    @localIdPatientResultsMap[patientId] = patient_ctx.getAllLocalIds()

  recordPopulationResult: (resultName, result) ->
    @populationResults[resultName] = result
