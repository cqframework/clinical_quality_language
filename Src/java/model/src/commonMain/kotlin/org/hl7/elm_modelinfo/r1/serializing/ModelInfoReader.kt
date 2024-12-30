@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import org.hl7.elm_modelinfo.r1.ModelInfo

interface ModelInfoReader {
    @Throws(IOException::class) fun read(src: File): ModelInfo?

    @Throws(IOException::class) fun read(src: Reader): ModelInfo?

    @Throws(IOException::class) fun read(src: InputStream): ModelInfo?

    @Throws(IOException::class) fun read(url: URL): ModelInfo?

    @Throws(IOException::class) fun read(uri: URI): ModelInfo?

    @Throws(IOException::class) fun read(string: String): ModelInfo?
}
