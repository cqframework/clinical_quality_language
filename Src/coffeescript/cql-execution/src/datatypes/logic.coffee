module.exports.ThreeValuedLogic = class ThreeValuedLogic
  @and: (val...) ->
    if false in val then false
    else if null in val then null
    else true

  @or: (val...) ->
    if true in val then true
    else if null in val then null
    else false

  @xor: (val...) ->
    if null in val then null
    else val.reduce (a,b) -> (!a ^ !b) is 1

  @not: (val) ->
    if val? then return not val else return null
