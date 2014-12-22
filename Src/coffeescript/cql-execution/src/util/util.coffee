module.exports.compact = (things)-> things.filter (x)-> x != null # need to explictily look for null seeing how 0 values will be dropped

module.exports.numerical_sort = (things, direction="asc") -> 
  things.sort (a,b)-> 
    if direction=="asc"
      a - b 
    else 
      b-a  

module.exports.typeIsArray = Array.isArray || ( value ) ->
  return {}.toString.call( value ) is '[object Array]'

module.exports.allTrue = (things) ->
  if module.exports.typeIsArray things
    things.every (x) -> x
  else
    things

module.exports.anyTrue = (things) ->
  if module.exports.typeIsArray things
    things.reduce (x,y) -> x || y
  else
    things