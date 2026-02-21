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
// List distinguished overloads test. The engine currently cannot
// distinguish between the List<Integer> and List<Code> overloads
// (and throws the error "Ambiguous call to function 'toString'")
// unless the library is compiled with signature level set to
// Overloads or All.
// See also https://github.com/cqframework/clinical_quality_language/issues/1408.
define function toString(value List<Integer>):
  if value is null
    then 'null'
  else Combine((value V return toString(V)), ', ')

define "Test":
  toString({1, 2, 3, 4, 5})
