expressionModules = [
  'logic',
  'clinical',
  'uncertainty'
  'datetime',
  'interval'
]

for name in expressionModules
  imported = require "./#{name}"
  for element in Object.keys(imported)
    module.exports[element] = imported[element]
