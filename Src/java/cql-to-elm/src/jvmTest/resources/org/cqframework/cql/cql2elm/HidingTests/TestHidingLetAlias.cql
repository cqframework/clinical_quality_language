library TestHidingLetAlias

define "AliasHidden":
    ({1, 2, 3}) "Alias"
        let "Alias": "Alias" + 1 // Warn, hides 49
    return "Alias"
