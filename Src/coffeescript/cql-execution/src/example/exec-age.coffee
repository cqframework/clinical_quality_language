{ Library, Context } = require '../cql-exec'
measure = require './age'

lib = new Library(measure)
ctx = new Context(lib)
ctx.patients = [ {
    "id": 1,
    "name": "John Smith",
    "birthdate" : new Date(1980, 2, 17, 6, 15)
  }, {
    "id": 2,
    "name": "Sally Smith",
    "birthdate" : new Date(2007, 8, 2, 11, 47)
} ]

result = lib.exec(ctx)
console.log JSON.stringify(result, undefined, 2)
