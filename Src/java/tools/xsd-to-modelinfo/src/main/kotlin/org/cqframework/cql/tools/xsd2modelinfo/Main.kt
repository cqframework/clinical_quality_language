package org.cqframework.cql.tools.xsd2modelinfo

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import javax.xml.transform.stream.StreamSource
import joptsimple.OptionParser
import joptsimple.OptionSpec
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.apache.ws.commons.schema.XmlSchemaCollection
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporter.Companion.fromXsd
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.ChoiceTypePolicy
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.Companion.loadFromProperties
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.ElementRedeclarationPolicy
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.SimpleTypeRestrictionPolicy
import org.cqframework.cql.tools.xsd2modelinfo.ModelImporterOptions.VersionPolicy
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

/** Generates a ModelInfo.xml for the input xsd. */
object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = OptionParser()
        val schemaOpt: OptionSpec<File> =
            parser.accepts("schema").withRequiredArg().ofType(File::class.java).required()
        val modelOpt: OptionSpec<String> =
            parser.accepts("model").withRequiredArg().ofType(String::class.java)
        val configOpt: OptionSpec<File?> =
            parser.accepts("config").withOptionalArg().ofType(File::class.java)
        val outputOpt: OptionSpec<File> =
            parser.accepts("output").withRequiredArg().ofType(File::class.java)
        val normalizePrefixOpt: OptionSpec<String> =
            parser.accepts("normalize-prefix").withRequiredArg().ofType(String::class.java)
        val choiceTypeOpt: OptionSpec<ChoiceTypePolicy> =
            parser
                .accepts("choicetype-policy")
                .withRequiredArg()
                .ofType(ChoiceTypePolicy::class.java)
        val stRestrictionsOpt: OptionSpec<SimpleTypeRestrictionPolicy> =
            parser
                .accepts("simpletype-restriction-policy")
                .withRequiredArg()
                .ofType(SimpleTypeRestrictionPolicy::class.java)
        val redeclarationsOpt: OptionSpec<ElementRedeclarationPolicy> =
            parser
                .accepts("element-redeclaration-policy")
                .withRequiredArg()
                .ofType(ElementRedeclarationPolicy::class.java)
        val versionPolicyOpt: OptionSpec<VersionPolicy> =
            parser.accepts("version-policy").withRequiredArg().ofType(VersionPolicy::class.java)
        val optionsFileOpt: OptionSpec<File> =
            parser.accepts("options-file").withRequiredArg().ofType(File::class.java)

        val options = parser.parse(*args)

        val schemaFile = schemaOpt.value(options)
        val `is`: InputStream = FileInputStream(schemaFile)
        val schemaCol = XmlSchemaCollection()
        schemaCol.setBaseUri(schemaFile.getParent())
        val schema = schemaCol.read(StreamSource(`is`))
        val importerOptions =
            if (options.has(optionsFileOpt)) {
                loadFromProperties(optionsFileOpt.value(options))
            } else {
                ModelImporterOptions()
            }

        if (options.has(modelOpt)) {
            importerOptions.model = modelOpt.value(options)
        }
        if (options.has(choiceTypeOpt)) {
            importerOptions.setChoiceTypePolicy(choiceTypeOpt.value(options))
        }
        if (options.has(stRestrictionsOpt)) {
            importerOptions.setSimpleTypeRestrictionPolicy(stRestrictionsOpt.value(options))
        }
        if (options.has(redeclarationsOpt)) {
            importerOptions.setElementRedeclarationPolicy(redeclarationsOpt.value(options))
        }
        if (options.has(versionPolicyOpt)) {
            importerOptions.setVersionPolicy(versionPolicyOpt.value(options))
        }
        if (options.has(normalizePrefixOpt)) {
            importerOptions.normalizePrefix = normalizePrefixOpt.value(options)
        }

        var config: ModelInfo? = null
        val configFile = configOpt.value(options)
        if (configFile != null) {
            val stream = FileInputStream(configFile)
            val source = stream.asSource().buffered()
            config = parseModelInfoXml(source)
        }

        val modelInfo = fromXsd(schema, importerOptions, config)

        val outputfile: File
        if (!options.has(outputOpt) || outputOpt.value(options).isDirectory()) {
            // construct output filename using modelinfo
            val name = String.format("%s-modelinfo.xml", modelInfo.targetQualifier)
            val basePath =
                if (options.has(outputOpt)) outputOpt.value(options).absolutePath
                else schemaFile.getParent()
            outputfile = File(basePath + File.separator + name)
        } else {
            outputfile = outputOpt.value(options)
        }
        require(outputfile != schemaFile) { "input schema file and output file must be different!" }

        val os: OutputStream = FileOutputStream(outputfile, false)
        try {
            val writer = OutputStreamWriter(os, "UTF-8")
            // TODO: implement ModelInfo writer
            // marshaller.marshal(new ObjectFactory().createModelInfo(modelInfo), writer);
        } finally {
            os.close()
        }
    }
}
