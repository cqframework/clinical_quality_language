cql = require '../../cql'

window.executeSimpleELM = (elm, parameters = {}) ->
  lib = new cql.Library(elm)
  ctx = new cql.Context(lib, null, null, parameters)
  lib.execWithoutPatients(ctx)
