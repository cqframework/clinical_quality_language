library TestHidingCommaMissingInListConstruction

define "1": 1
define "2": 2
define "3": 3
define "4": 4
define "5": 5

define "Four With Five": "4" "5" // "5" is interpreted as an alias here, result is 4
define "Query Four": ({ "4" }) "X" return "X" //  This is how the above expression looks to the CQL compiler
define "Four With Alias": "4" "15" return Add("15", 1) // "15" doesn't exist, It's a alias for "4". result is 5 (4+1)
define "ListTest6": {"1", "2", "3", "4" "5"} // Valid, "5" is an alias for "4"