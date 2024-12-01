@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource

object LibraryReaderUtil {
    /** Creates [Source] from various JSON representation. */
    @Suppress("ReturnCount")
    @Throws(IOException::class)
    fun toSource(json: Any?): Source {
        var json: Any? = json ?: throw CqlCompilerException("no JSON is given")
        if (json is String) {
            json =
                try {
                    URI(json)
                } catch (@Suppress("SwallowedException") e: URISyntaxException) {
                    File(json)
                }
        }
        if (json is File) {
            return StreamSource(json as File?)
        }
        if (json is URI) {
            json = json.toURL()
        }
        if (json is URL) {
            return StreamSource(json.toExternalForm())
        }
        if (json is InputStream) {
            return StreamSource(json as InputStream?)
        }
        if (json is Reader) {
            return StreamSource(json as Reader?)
        }
        if (json is Source) {
            return json
        }
        throw CqlCompilerException(
            @Suppress("ImplicitDefaultLocale")
            String.format("Could not determine access path for input of type %s.", json!!.javaClass)
        )
    }
}
