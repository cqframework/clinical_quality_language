library TestHidingFunctionDefinitionWithOverloads

define function IWantToBeHidden(value Integer): value

define function IWantToBeHidden(value String):
    ({1, 2, 3}) "IWantToBeHidden"
        return "IWantToBeHidden" + 1
