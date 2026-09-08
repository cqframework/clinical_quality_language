package org.cqframework.cql.cql2elm

class TestLibrarySourceProvider(path: String = "LibraryTests") :
    BaseTestLibrarySourceProvider({ libraryIdentifier, type ->
        "$path/${libraryIdentifier.id}${libraryIdentifier.version?.let { "-$it" } ?: ""}.${type.toString().lowercase()}"
    })
