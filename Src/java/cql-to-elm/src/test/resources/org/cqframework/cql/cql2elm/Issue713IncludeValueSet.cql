library Issue713IncludeValueSet

include Issue713Include called VS

define OneCode:
    Code { code: 'M' } 

define ListOfCodes: { OneCode }

define IncludedIn: OneCode in VS."Female"
define AnyIncludedIn: ListOfCodes in VS."Female"