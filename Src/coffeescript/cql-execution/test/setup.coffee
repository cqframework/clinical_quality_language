{ Library, Context, PatientSource, CodeService, PatientContext, PopulationContext, Executor} =  require '../lib/cql'

module.exports = (test, data, patients=[], valuesets={}, parameters={}, repository=null) ->
  try
    test.lib = new Library(data[test.test.parent.title],repository)
    cservice = new CodeService(valuesets)
    psource = new PatientSource(patients)
    test.ctx = new PatientContext(test.lib, psource.currentPatient(), cservice, parameters)
    test.executor = new Executor(test.lib,cservice,parameters)
    test.patientSource = psource
    for k,v of test.lib.valuesets
      test[k[0].toLowerCase() + k[1..-1]] = v
    for k,v of test.lib.expressions
      test[k[0].toLowerCase() + k[1..-1]] = v.expression
  catch e
    e.message = '[' + test.test.parent.title + '] ' + e.message
    throw e
