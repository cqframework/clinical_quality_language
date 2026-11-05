package org.cqframework.cql.cql2elm

/**
 * Clone of the [TestLibrarySourceProvider] that does not enforce versioning in the file names and
 * thus will support tests that do not specify a version in the library identifier.
 */
class TestLibrarySourceVersionAgnosticProvider(path: String = "LibraryTests") :
    BaseTestLibrarySourceProvider({ libraryIdentifier, type ->
        "$path/${libraryIdentifier.id}.${type.toString().lowercase()}"
    })
