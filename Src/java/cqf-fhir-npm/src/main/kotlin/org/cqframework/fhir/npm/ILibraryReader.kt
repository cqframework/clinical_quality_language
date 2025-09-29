package org.cqframework.fhir.npm

import java.io.IOException
import java.io.InputStream
import org.hl7.fhir.exceptions.FHIRFormatError
import org.hl7.fhir.r5.model.Library

interface ILibraryReader {
    @Throws(FHIRFormatError::class, IOException::class)
    fun readLibrary(stream: InputStream?): Library?
}
