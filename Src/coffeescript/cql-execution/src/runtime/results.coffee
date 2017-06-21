module.exports.Results = class Results
  constructor: () ->
    @patientResults = {}
    @populationResults = {}
    @localIdPatientResultsMap = {}

  recordPatientResult: (patientId, resultName, localId_hash, result) ->
    @patientResults[patientId] ?= {}
    @localIdPatientResultsMap[patientId] ?= {}
    @patientResults[patientId][resultName] = result
    for localId, value of localId_hash
      @localIdPatientResultsMap[patientId][localId] = value

  recordPopulationResult: (resultName, result) ->
    @populationResults[resultName] = result
