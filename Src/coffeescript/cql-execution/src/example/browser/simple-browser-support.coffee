window.cql = require '../../cql'

window.executeSimpleELM = (elm, patientSource, valueSets, libraryName, version, parameters = {}) ->
  if Array.isArray(elm)
    if elm.length > 1
      rep = new cql.Repository(elm)
      lib = rep.resolve(libraryName, version)
    else
      lib = new cql.Library(elm[0])
  else
    lib = new cql.Library(elm)
    
  codeService = new cql.CodeService(valueSets)
  executor = new cql.Executor(lib, codeService, parameters);
  executor.exec(patientSource)
