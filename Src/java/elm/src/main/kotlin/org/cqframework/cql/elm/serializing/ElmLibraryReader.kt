package org.cqframework.cql.elm.serializing

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import org.hl7.elm.r1.Library

interface ElmLibraryReader {
    @Throws(IOException::class) fun read(file: File): Library?

    @Throws(IOException::class) fun read(url: URL): Library?

    @Throws(IOException::class) fun read(uri: URI): Library?

    @Throws(IOException::class) fun read(string: String): Library?

    @Throws(IOException::class) fun read(inputStream: InputStream): Library?

    @Throws(IOException::class) fun read(reader: Reader): Library?
}
