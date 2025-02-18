package org.cqframework.cql.cql2elm.utils

import org.cqframework.cql.cql2elm.LibraryBuilder

actual fun getTranslatorVersion(): String? {
    return LibraryBuilder::class.java.getPackage().implementationVersion
}
