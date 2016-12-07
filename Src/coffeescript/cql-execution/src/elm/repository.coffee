{ Library } =  require '../cql'
module.exports.Repository = class Repository
  constructor: (@data) ->
    @libraries = for k,v of @data
       v

  resolve: (library,version) ->
    for lib in @libraries
      if lib.identifier?.id == library && lib.identifier?.version == version
        return new Library(lib,@)
