library LetClauseOutsideQueryContextTest version '1'

define list:
{ 1, 2, 3, 4 }

define "First Position of list":
list L1
let L2: list[0]
return L2

define "Third Position of list With Same Name of Let As First":
list L1
let L2: list[2]
return L2
