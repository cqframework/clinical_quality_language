package org.cqframework.cql.cql2elm.cli

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import joptsimple.OptionParser
import joptsimple.OptionSpec
import kotlin.system.exitProcess
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.CqlTranslator.Companion.fromFile
import org.cqframework.cql.cql2elm.DefaultLibrarySourceProvider
import org.cqframework.cql.cql2elm.DefaultModelInfoProvider
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.cqframework.fhir.npm.LibraryLoader
import org.cqframework.fhir.npm.NpmLibrarySourceProvider
import org.cqframework.fhir.npm.NpmModelInfoProvider
import org.cqframework.fhir.npm.NpmPackageManager
import org.cqframework.fhir.utilities.IGContext
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml
import org.hl7.fhir.utilities.npm.NpmPackage

object Main {
    @Suppress("PrintStackTrace")
    fun getModelInfoProvider(modelInfoXML: File): ModelInfoProvider {
        try {
            val source = FileInputStream(modelInfoXML).asSource().buffered()
            val modelInfo: ModelInfo = parseModelInfoXml(source)
            return { _: ModelIdentifier -> modelInfo } as ModelInfoProvider
        } catch (e: IOException) {
            System.err.printf("Could not load model-info XML: %s%n", modelInfoXML)
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    private fun outputExceptions(exceptions: Iterable<CqlCompilerException>) {
        for (error in exceptions) {
            val tb = error.locator
            val lines =
                if (tb == null) "[n/a]"
                else ("[${tb.startLine}:${tb.startChar}, ${tb.endLine}:${tb.endChar}]")
            System.err.printf("%s:%s %s%n", error.severity, lines, error.message)
        }
    }

    @Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
    @Throws(IOException::class)
    private fun writeELM(
        inPath: Path,
        outPath: Path,
        format: CqlTranslator.Format,
        modelProvider: ModelInfoProvider?,
        igContext: IGContext?,
        options: CqlCompilerOptions,
    ) {
        System.err.println(
            "================================================================================"
        )
        System.err.printf("TRANSLATE %s%n", inPath)
        val modelManager =
            if (options.options.contains(CqlCompilerOptions.Options.DisableDefaultModelInfoLoad)) {
                ModelManager(false)
            } else {
                ModelManager()
            }

        if (modelProvider != null) {
            modelManager.modelInfoLoader.registerModelInfoProvider(modelProvider)
        }

        val libraryManager = LibraryManager(modelManager, options)
        val parent = kotlinx.io.files.Path(inPath.parent.toString())
        modelManager.modelInfoLoader.registerModelInfoProvider(
            DefaultModelInfoProvider(parent),
            true,
        )
        libraryManager.librarySourceLoader.registerProvider(DefaultLibrarySourceProvider(parent))
        libraryManager.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())

        val namespaceManager = libraryManager.namespaceManager
        var namespaceInfo: NamespaceInfo? = null
        if (igContext != null) {
            val pm = NpmPackageManager(igContext.sourceIg!!)
            @Suppress("ForbiddenComment")
            pm.npmList.forEach { npm: NpmPackage? ->
                val newNamespace = NamespaceInfo(npm!!.id(), npm.canonical())
                if (namespaceManager.resolveNamespaceUri(newNamespace.name) != null) {
                    // TODO: Logger.skip loading
                } else if (namespaceManager.getNamespaceInfoFromUri(newNamespace.uri) != null) {
                    // TODO: Logger.skip loading
                } else {
                    namespaceManager.ensureNamespaceRegistered(newNamespace)
                }
            }
            val reader = LibraryLoader(igContext.fhirVersion)
            val sp = NpmLibrarySourceProvider(pm.npmList, reader, pm)
            libraryManager.librarySourceLoader.registerProvider(sp)

            val mp = NpmModelInfoProvider(pm.npmList, reader, pm)
            modelManager.modelInfoLoader.registerModelInfoProvider(mp)

            val packageId = igContext.packageId
            val canonicalBase = igContext.canonicalBase

            if (!packageId.isNullOrBlank() && !canonicalBase.isNullOrBlank()) {
                namespaceInfo = NamespaceInfo(packageId, canonicalBase)
            }
        }

        val translator: CqlTranslator = fromFile(namespaceInfo, inPath.toString(), libraryManager)
        libraryManager.librarySourceLoader.clearProviders()

        if (!translator.errors.isEmpty()) {
            System.err.println("Translation failed due to errors:")
            outputExceptions(translator.exceptions)
        } else if (!options.verifyOnly) {
            if (translator.exceptions.isEmpty()) {
                System.err.println("Translation completed successfully.")
            } else {
                System.err.println("Translation completed with messages:")
                outputExceptions(translator.exceptions)
            }

            val os = FileOutputStream(outPath.toFile())
            PrintWriter(os).use { pw ->
                when (format) {
                    CqlTranslator.Format.COFFEE -> {
                        pw.print("module.exports = ")
                        pw.println(translator.toJson())
                    }

                    CqlTranslator.Format.JSON -> pw.println(translator.toJson())
                    CqlTranslator.Format.XML -> pw.println(translator.toXml())
                }
                pw.println()
            }
            System.err.println("ELM output written to: $outPath")
        }

        System.err.println()
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "MemberNameEqualsClassName", "MaxLineLength")
    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = OptionParser()
        val input: OptionSpec<File?> =
            parser
                .accepts("input")
                .withRequiredArg()
                .ofType(File::class.java)
                .required()
                .describedAs(
                    "The name of the input file or directory. If a directory is given, all files ending in .cql will be processed"
                )
        val model: OptionSpec<File?> =
            parser
                .accepts("model")
                .withRequiredArg()
                .ofType(File::class.java)
                .describedAs(
                    "The name of an input file containing the model info to use for translation. Model info can also be provided through an implementation of ModelInfoProvider"
                )
        val output: OptionSpec<File?> =
            parser
                .accepts("output")
                .withRequiredArg()
                .ofType(File::class.java)
                .describedAs(
                    "The name of the output file or directory. If no output is given, an output file name is constructed based on the input name and target format"
                )
        val format: OptionSpec<CqlTranslator.Format> =
            parser
                .accepts("format")
                .withRequiredArg()
                .ofType(CqlTranslator.Format::class.java)
                .defaultsTo(CqlTranslator.Format.XML)
                .describedAs("The target format for the output")
        val rootDir: OptionSpec<File?> =
            parser
                .accepts("root-dir")
                .withOptionalArg()
                .ofType(File::class.java)
                .describedAs("Root directory of a FHIR IG project, used to resolve CQL namespaces")
        val disableDefaultModelInfoLoad: OptionSpec<*>? =
            parser.accepts("disable-default-modelinfo-load")
        val verify: OptionSpec<*>? = parser.accepts("verify")
        val optimization: OptionSpec<*>? = parser.accepts("date-range-optimization")
        val annotations: OptionSpec<*>? = parser.accepts("annotations")
        val locators: OptionSpec<*>? = parser.accepts("locators")
        val resultTypes: OptionSpec<*>? = parser.accepts("result-types")
        val detailedErrors: OptionSpec<*>? = parser.accepts("detailed-errors")
        val errorLevel: OptionSpec<*> =
            parser
                .accepts("error-level")
                .withRequiredArg()
                .ofType(CqlCompilerException.ErrorSeverity::class.java)
                .defaultsTo(CqlCompilerException.ErrorSeverity.Info)
                .describedAs(
                    "Indicates the minimum severity message that will be reported. If no error-level is specified, all messages will be output"
                )
        val disableListTraversal: OptionSpec<*>? = parser.accepts("disable-list-traversal")
        val disableListDemotion: OptionSpec<*>? = parser.accepts("disable-list-demotion")
        val disableListPromotion: OptionSpec<*>? = parser.accepts("disable-list-promotion")
        val enableIntervalDemotion: OptionSpec<*>? = parser.accepts("enable-interval-demotion")
        val enableIntervalPromotion: OptionSpec<*>? = parser.accepts("enable-interval-promotion")
        val disableMethodInvocation: OptionSpec<*>? = parser.accepts("disable-method-invocation")
        val requireFromKeyword: OptionSpec<*>? = parser.accepts("require-from-keyword")
        val strict: OptionSpec<*>? = parser.accepts("strict")
        val debug: OptionSpec<*>? = parser.accepts("debug")
        val validateUnits: OptionSpec<*>? = parser.accepts("validate-units")
        val signatures: OptionSpec<SignatureLevel> =
            parser
                .accepts("signatures")
                .withRequiredArg()
                .ofType(SignatureLevel::class.java)
                .defaultsTo(SignatureLevel.None)
                .describedAs(
                    "Indicates whether signatures should be included for invocations in the output ELM. Differing will include invocation signatures that differ from the declared signature. Overloads will include declaration signatures when the operator or function has more than one overload with the same number of arguments as the invocation"
                )
        val compatibilityLevel: OptionSpec<String?> =
            parser
                .accepts("compatibility-level")
                .withRequiredArg()
                .ofType(String::class.java)
                .describedAs(
                    "Compatibility level for the translator, valid values are 1.3, 1.4, and 1.5"
                )

        @Suppress("SpreadOperator") val options = parser.parse(*args)

        val source = input.value(options)!!.toPath()
        val destination =
            if (output.value(options) != null) output.value(options)!!.toPath()
            else if (source.toFile().isDirectory()) source else source.parent
        val outputFormat = format.value(options)
        val signatureLevel = signatures.value(options)

        val inOutMap: MutableMap<Path?, Path?> = HashMap()
        if (source.toFile().isDirectory()) {
            require(!(destination.toFile().exists() && !destination.toFile().isDirectory())) {
                "Output must be a valid folder if input is a folder!"
            }

            Files.walkFileTree(
                source,
                object : SimpleFileVisitor<Path>() {
                    @Throws(IOException::class)
                    override fun visitFile(
                        file: Path,
                        attrs: BasicFileAttributes,
                    ): FileVisitResult {
                        if (
                            file.toFile().getName().endsWith(".cql") ||
                                file.toFile().getName().endsWith(".CQL")
                        ) {
                            val destinationFolder =
                                destination.resolve(source.relativize(file.parent))
                            if (
                                !destinationFolder.toFile().exists() &&
                                    !destinationFolder.toFile().mkdirs()
                            ) {
                                System.err.printf("Problem creating %s%n", destinationFolder)
                            }
                            inOutMap[file] = destinationFolder
                        }
                        return FileVisitResult.CONTINUE
                    }
                },
            )
        } else {
            inOutMap[source] = destination
        }

        for (inOut in inOutMap.entries) {
            val `in`: Path = inOut.key!!
            var out: Path = inOut.value!!
            if (out.toFile().isDirectory()) {
                // Use input filename with ".xml", ".json", or ".coffee" extension
                var name = `in`.toFile().getName()
                if (name.lastIndexOf('.') != -1) {
                    name = name.take(name.lastIndexOf('.'))
                }
                name +=
                    when (outputFormat) {
                        CqlTranslator.Format.JSON -> ".json"
                        CqlTranslator.Format.COFFEE -> ".coffee"
                        CqlTranslator.Format.XML -> ".xml"
                        else -> ".xml"
                    }
                out = out.resolve(name)
            }

            require(out != `in`) { "input and output file must be different!" }

            var igContext: IGContext? = null
            if (options.has(rootDir)) {
                igContext = IGContext()
                igContext.initializeFromIni(
                    options.valueOf("root-dir").toString() + File.separator + "ig.ini"
                )
            }

            var modelProvider: ModelInfoProvider? = null
            if (options.has(model)) {
                val modelFile = options.valueOf<File>(model)
                require(!(!modelFile.exists() || modelFile.isDirectory())) {
                    "model must be a valid file!"
                }
                modelProvider = getModelInfoProvider(modelFile)
            }

            writeELM(
                `in`,
                out,
                outputFormat,
                modelProvider,
                igContext,
                CqlCompilerOptions(
                    options.has(optimization),
                    options.has(debug) || options.has(annotations),
                    options.has(debug) || options.has(locators),
                    options.has(debug) || options.has(resultTypes),
                    options.has(verify),
                    options.has(detailedErrors), // Didn't include in debug, maybe should...
                    if (options.has(errorLevel))
                        options.valueOf(errorLevel) as CqlCompilerException.ErrorSeverity?
                    else CqlCompilerException.ErrorSeverity.Info,
                    options.has(strict) || options.has(disableListTraversal),
                    options.has(strict) || options.has(disableListDemotion),
                    options.has(strict) || options.has(disableListPromotion),
                    options.has(enableIntervalDemotion),
                    options.has(enableIntervalPromotion),
                    options.has(strict) || options.has(disableMethodInvocation),
                    options.has(requireFromKeyword),
                    options.has(validateUnits),
                    options.has(disableDefaultModelInfoLoad),
                    signatureLevel,
                    if (options.has(compatibilityLevel)) options.valueOf(compatibilityLevel)!!
                    else "1.5",
                ),
            )
        }
    }
}
