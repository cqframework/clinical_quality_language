logic = require './logic'
clinical = require './clinical'
uncertainty = require './uncertainty'
datetime = require './datetime'
interval = require './interval'

libs = [logic, clinical, uncertainty, datetime, interval]
for lib in libs
  for element in Object.keys(lib)
    module.exports[element] = lib[element]
