cql = require '../../cql'

window.executeSimpleELM = (elm, parameters = {}) ->
  lib = new cql.Library(elm)
  patientSource = new cql.PatientSource([])
  executor = new cql.Executor(lib, null, parameters)
  executor.exec(patientSource)
