@file:Suppress("LocalVariableName")

package org.cqframework.fhir.npm

import java.io.IOException
import java.io.InputStream
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_14_50
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_50
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50
import org.hl7.fhir.convertors.conv14_50.VersionConvertor_14_50
import org.hl7.fhir.convertors.conv30_50.VersionConvertor_30_50
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50
import org.hl7.fhir.dstu2016may.formats.JsonParser
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.exceptions.FHIRFormatError
import org.hl7.fhir.r5.model.Library
import org.hl7.fhir.utilities.VersionUtilities

@Suppress("VariableNaming")
class LibraryLoader(private val version: String?) : ILibraryReader {
    @Throws(FHIRFormatError::class, IOException::class)
    override fun readLibrary(stream: InputStream?): Library? {
        return when {
            VersionUtilities.isR2Ver(version) -> {
                throw FHIRException("Library is not supported in R2")
            }
            VersionUtilities.isR2BVer(version) -> {
                val res = JsonParser().parse(stream)
                val versionConvertor_14_50 = VersionConvertor_14_50(BaseAdvisor_14_50())
                versionConvertor_14_50.convertResource(res) as Library?
            }
            VersionUtilities.isR3Ver(version) -> {
                val res = org.hl7.fhir.dstu3.formats.JsonParser().parse(stream)
                val versionConvertor_30_50 = VersionConvertor_30_50(BaseAdvisor_30_50())
                versionConvertor_30_50.convertResource(res) as Library?
            }
            VersionUtilities.isR4Ver(version) -> {
                val res = org.hl7.fhir.r4.formats.JsonParser().parse(stream)
                val versionConvertor_40_50 = VersionConvertor_40_50(BaseAdvisor_40_50())
                versionConvertor_40_50.convertResource(res) as Library?
            }
            VersionUtilities.isR5Ver(version) -> {
                org.hl7.fhir.r5.formats.JsonParser().parse(stream) as Library?
            }
            else -> throw FHIRException("Unknown Version '$version'")
        }
    }
}
