library CqlListDistinguishedOverloads


define function toString(value Integer):
  if value is null
    then 'null'
  else ToString(value)

define function toString(value Code):
  if value is null
    then 'null'
  else 'Code { code: ' + value.code + ' system: ' + value.system + ' version: ' + value.version + ' display: ' + value.display + ' }'

define function toString(value List<Code>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

// This is the function that _should_ be selected at runtime for the
// List distinguised overloads test. At the time this CQL was written,
// The engine had a bug that was selecting the List<Code> overload
define function toString(value List<Integer>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define "Test":
  toString({1, 2, 3, 4, 5})
