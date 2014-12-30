{ Uncertainty } = require '../datatypes/uncertainty'

module.exports.compact = (things)-> things.filter (x)-> x?

module.exports.numerical_sort = (things, direction="asc") ->
  things.sort (a,b)->
    if direction=="asc"
      a - b
    else
      b-a

module.exports.typeIsArray  = typeIsArray  = Array.isArray || ( value ) ->
  return {}.toString.call( value ) is '[object Array]'

module.exports.allTrue = (things) ->
  if typeIsArray things
    things.every (x) -> x
  else
    things

module.exports.anyTrue = (things) ->
  if typeIsArray things
    things.some (x) -> x
  else
    things

module.exports.equals = equals = (a, b) ->
  # Handle null cases first
  return a is b if not a? or not b?

  # If one is an Uncertainty, convert the other to an Uncertainty
  if a instanceof Uncertainty then b = Uncertainty.from(b)
  else if b instanceof Uncertainty then a = Uncertainty.from(a)

  # Use overloaded 'equals' function if it is available
  return a.equals(b) if typeof a.equals is 'function'

  # Return true of the objects are strictly equal
  return true if a is b

  # Return false if they are instances of different classes
  [aClass, bClass] = ({}.toString.call(obj) for obj in [a, b])
  return false if aClass isnt bClass

  switch aClass
    when '[object Date]'
      # Compare the ms since epoch
      return a.getTime() is b.getTime()
    when '[object RegExp]'
      # Compare the components of the regular expression
      return ['source', 'global', 'ignoreCase', 'multiline'].every (p) -> a[p] is b[p]
    when '[object Array]'
      # Compare every item in the array
      return a.length is b.length and a.every (item, i) -> equals(item, b[i])
    when '[object Object]'
      # Return false if they are instances of different classes
      return false unless b instanceof a.constructor
      # Do deep comparison of keys and values
      aKeys = (key for key of a unless typeof(key) is 'function')
      bKeys = (key for key of b unless typeof(key) is 'function')
      return aKeys.length is bKeys.length and aKeys.every (key) -> equals(a[key], b[key])

  # If we made it this far, we can't handle it
  return false
