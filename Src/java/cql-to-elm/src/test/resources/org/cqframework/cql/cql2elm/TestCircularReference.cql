library TestCircularReference

// This was resulting in a circular reference error for the cql compiler
// because it was detecting cycles by name only (toString() in this case)
// It needs to consider types as well.
define function toString(value Concept):
  if value is null
    then 'null'
  else 'Concept { ' + toString(value.codes) + ' display: ' + value.display + ' }'

define function toString(value List<System.Code>):
    Combine(
        value C return 'Code: ' + C.display,
        ', '
    )