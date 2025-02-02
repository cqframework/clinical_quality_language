library TestHiddenIdentifierFromReturn

define "HiddenIdentifiersFromReturn":
    ({1, 2, 3}) X
        let var : X + 1
        return
            var varalias
            let var : varalias + 2
            return varalias