library TestIdentifierDoesNotResolveCaseMismatchExistIdentifier

define "iAmAdIfErEnTcAsE" : 1

define doesNotResolveExact :
    "NonExistent" + 1

define doesNotResolveCaseSensitive :
//    "iAmAdIfErEnTcAsE" + 1  make sure comment isn't considered
    "IaMaDiFeReNtCaSe" + 1
