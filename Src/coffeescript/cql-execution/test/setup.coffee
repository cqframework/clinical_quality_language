{ Library, Context, PatientSource, CodeService } =  require '../lib/cql'

module.exports = (test, data, patients=[], valuesets={}, parameters={}) ->
  try
    test.lib = new Library(data[test.test.parent.title])
    psource = new PatientSource(patients)
    cservice = new CodeService(valuesets)
    test.ctx = new Context(test.lib, psource, cservice, parameters)
    for k,v of test.lib.valuesets
      test[k[0].toLowerCase() + k[1..-1]] = v
    for k,v of test.lib.expressions
      test[k[0].toLowerCase() + k[1..-1]] = v.expression
  catch e
    e.message = '[' + test.test.parent.title + '] ' + e.message
    throw e
