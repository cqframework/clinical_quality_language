@file:Suppress("PackageNaming")

package org.cqframework.cql.elm.serializing

interface ElmLibraryWriterProvider {
    fun create(contentType: String): ElmLibraryWriter
}
