library Issue208 version '1'

define "List 1":
({1, 2, 3}) i
return i

define "List 2":
({4, 5, 6}) i
return i

define "List of Lists":
{ "List 1", "List 2" }

define "Let Test 1":
"List of Lists" IntegerList
//let renamedIntegerList: (IntegerList i where 1 = 1)
let renamedIntegerList: IntegerList
return renamedIntegerList

define "Let Test 2":
"List of Lists" IntegerList
let renamedIntegerList: (IntegerList i where 1 = 1)
//let renamedIntegerList: IntegerList
return renamedIntegerList
