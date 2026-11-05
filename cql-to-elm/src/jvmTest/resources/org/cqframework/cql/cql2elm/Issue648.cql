library Issue648

/*
https://github.com/cqframework/clinical_quality_language/issues/648
Type inference through conditionals (case-when, if-else, etc)
 */

define "Cases": 
  case
    when true then 'true'
    when false then false
    when true then 5
    else null
  end

define "If":
    if true then 'true'
    else if false then false
    else null