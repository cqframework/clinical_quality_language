module.exports.typeIsArray = Array.isArray || ( value ) ->
  return {}.toString.call( value ) is '[object Array]'
