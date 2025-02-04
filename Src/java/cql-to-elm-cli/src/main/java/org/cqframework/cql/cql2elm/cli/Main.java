package org.cqframework.cql.cql2elm.cli;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.cqframework.cql.cql2elm.CqlTranslator.fromFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.fhir.npm.LibraryLoader;
import org.cqframework.fhir.npm.NpmLibrarySourceProvider;
import org.cqframework.fhir.npm.NpmPackageManager;
import org.cqframework.fhir.utilities.IGContext;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

public class Main {
    public static ModelInfoProvider getModelInfoProvider(File modelInfoXML) {
        try {
            final ModelInfo modelInfo =
                    ModelInfoReaderFactory.getReader("application/xml").read(modelInfoXML);
            return (ModelIdentifier modelIdentifier) -> modelInfo;
        } catch (IOException e) {
            System.err.printf("Could not load model-info XML: %s%n", modelInfoXML);
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    private static void outputExceptions(Iterable<CqlCompilerException> exceptions) {
        for (CqlCompilerException error : exceptions) {
            TrackBack tb = error.getLocator();
            String lines = tb == null
                    ? "[n/a]"
                    : String.format(
                            "[%d:%d, %d:%d]", tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
            System.err.printf("%s:%s %s%n", error.getSeverity(), lines, error.getMessage());
        }
    }

    private static void writeELM(
            Path inPath,
            Path outPath,
            org.cqframework.cql.cql2elm.CqlTranslator.Format format,
            ModelInfoProvider modelProvider,
            IGContext igContext,
            CqlCompilerOptions options)
            throws IOException {

        System.err.println("================================================================================");
        System.err.printf("TRANSLATE %s%n", inPath);

        ModelManager modelManager;
        if (options.getOptions().contains(CqlCompilerOptions.Options.DisableDefaultModelInfoLoad)) {
            modelManager = new ModelManager(false);
        } else {
            modelManager = new ModelManager();
        }

        if (modelProvider != null) {
            modelManager.getModelInfoLoader().registerModelInfoProvider(modelProvider);
        }

        LibraryManager libraryManager = new LibraryManager(modelManager, options);
        modelManager
                .getModelInfoLoader()
                .registerModelInfoProvider(new DefaultModelInfoProvider(inPath.getParent()), true);
        libraryManager.getLibrarySourceLoader().registerProvider(new DefaultLibrarySourceProvider(inPath.getParent()));
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());

        NamespaceManager namespaceManager = libraryManager.getNamespaceManager();
        NamespaceInfo namespaceInfo = null;
        if (igContext != null) {
            NpmPackageManager pm = new NpmPackageManager(igContext.getSourceIg());
            pm.getNpmList().forEach(npm -> {
                NamespaceInfo newNamespace = new NamespaceInfo(npm.id(), npm.canonical());
                namespaceManager.ensureNamespaceRegistered(newNamespace);
            });
            LibraryLoader reader = new LibraryLoader(igContext.getFhirVersion());
            NpmLibrarySourceProvider sp = new NpmLibrarySourceProvider(pm.getNpmList(), reader, pm);
            libraryManager.getLibrarySourceLoader().registerProvider(sp);

            String packageId = igContext.getPackageId();
            String canonicalBase = igContext.getCanonicalBase();

            if (packageId != null && !packageId.isEmpty() && canonicalBase != null && !canonicalBase.isEmpty()) {
                namespaceInfo = new NamespaceInfo(packageId, canonicalBase);
            }
        }

        CqlTranslator translator = fromFile(namespaceInfo, inPath.toFile(), libraryManager);
        libraryManager.getLibrarySourceLoader().clearProviders();

        if (!translator.getErrors().isEmpty()) {
            System.err.println("Translation failed due to errors:");
            outputExceptions(translator.getExceptions());
        } else if (!options.getVerifyOnly()) {
            if (translator.getExceptions().isEmpty()) {
                System.err.println("Translation completed successfully.");
            } else {
                System.err.println("Translation completed with messages:");
                outputExceptions(translator.getExceptions());
            }
            try (PrintWriter pw = new PrintWriter(outPath.toFile(), "UTF-8")) {
                switch (format) {
                    case COFFEE:
                        pw.print("module.exports = ");
                        pw.println(translator.toJson());
                        break;
                    case JSON:
                        pw.println(translator.toJson());
                        break;
                    case XML:
                    default:
                        pw.println(translator.toXml());
                }
                pw.println();
            }
            System.err.println(String.format("ELM output written to: %s", outPath.toString()));
        }

        System.err.println();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void main(String[] args) throws IOException, InterruptedException {
        OptionParser parser = new OptionParser();
        OptionSpec<File> input = parser.accepts("input")
                .withRequiredArg()
                .ofType(File.class)
                .required()
                .describedAs(
                        "The name of the input file or directory. If a directory is given, all files ending in .cql will be processed");
        OptionSpec<File> model = parser.accepts("model")
                .withRequiredArg()
                .ofType(File.class)
                .describedAs(
                        "The name of an input file containing the model info to use for translation. Model info can also be provided through an implementation of ModelInfoProvider");
        OptionSpec<File> output = parser.accepts("output")
                .withRequiredArg()
                .ofType(File.class)
                .describedAs(
                        "The name of the output file or directory. If no output is given, an output file name is constructed based on the input name and target format");
        OptionSpec<org.cqframework.cql.cql2elm.CqlTranslator.Format> format = parser.accepts("format")
                .withRequiredArg()
                .ofType(org.cqframework.cql.cql2elm.CqlTranslator.Format.class)
                .defaultsTo(org.cqframework.cql.cql2elm.CqlTranslator.Format.XML)
                .describedAs("The target format for the output");
        OptionSpec<File> rootDir = parser.accepts("root-dir")
                .withOptionalArg()
                .ofType(File.class)
                .describedAs("Root directory of a FHIR IG project, used to resolve CQL namespaces");
        OptionSpec disableDefaultModelInfoLoad = parser.accepts("disable-default-modelinfo-load");
        OptionSpec verify = parser.accepts("verify");
        OptionSpec optimization = parser.accepts("date-range-optimization");
        OptionSpec annotations = parser.accepts("annotations");
        OptionSpec locators = parser.accepts("locators");
        OptionSpec resultTypes = parser.accepts("result-types");
        OptionSpec detailedErrors = parser.accepts("detailed-errors");
        OptionSpec errorLevel = parser.accepts("error-level")
                .withRequiredArg()
                .ofType(CqlCompilerException.ErrorSeverity.class)
                .defaultsTo(CqlCompilerException.ErrorSeverity.Info)
                .describedAs(
                        "Indicates the minimum severity message that will be reported. If no error-level is specified, all messages will be output");
        OptionSpec disableListTraversal = parser.accepts("disable-list-traversal");
        OptionSpec disableListDemotion = parser.accepts("disable-list-demotion");
        OptionSpec disableListPromotion = parser.accepts("disable-list-promotion");
        OptionSpec enableIntervalDemotion = parser.accepts("enable-interval-demotion");
        OptionSpec enableIntervalPromotion = parser.accepts("enable-interval-promotion");
        OptionSpec disableMethodInvocation = parser.accepts("disable-method-invocation");
        OptionSpec requireFromKeyword = parser.accepts("require-from-keyword");
        OptionSpec strict = parser.accepts("strict");
        OptionSpec debug = parser.accepts("debug");
        OptionSpec validateUnits = parser.accepts("validate-units");
        OptionSpec<LibraryBuilder.SignatureLevel> signatures = parser.accepts("signatures")
                .withRequiredArg()
                .ofType(LibraryBuilder.SignatureLevel.class)
                .defaultsTo(LibraryBuilder.SignatureLevel.None)
                .describedAs(
                        "Indicates whether signatures should be included for invocations in the output ELM. Differing will include invocation signatures that differ from the declared signature. Overloads will include declaration signatures when the operator or function has more than one overload with the same number of arguments as the invocation");
        OptionSpec<String> compatibilityLevel = parser.accepts("compatibility-level")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Compatibility level for the translator, valid values are 1.3, 1.4, and 1.5");

        OptionSet options = parser.parse(args);

        final Path source = input.value(options).toPath();
        final Path destination = output.value(options) != null
                ? output.value(options).toPath()
                : source.toFile().isDirectory() ? source : source.getParent();
        final org.cqframework.cql.cql2elm.CqlTranslator.Format outputFormat = format.value(options);
        final LibraryBuilder.SignatureLevel signatureLevel = signatures.value(options);

        Map<Path, Path> inOutMap = new HashMap<>();
        if (source.toFile().isDirectory()) {
            if (destination.toFile().exists() && !destination.toFile().isDirectory()) {
                throw new IllegalArgumentException("Output must be a valid folder if input is a folder!");
            }

            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toFile().getName().endsWith(".cql")
                            || file.toFile().getName().endsWith(".CQL")) {
                        Path destinationFolder = destination.resolve(source.relativize(file.getParent()));
                        if (!destinationFolder.toFile().exists()
                                && !destinationFolder.toFile().mkdirs()) {
                            System.err.printf("Problem creating %s%n", destinationFolder);
                        }
                        inOutMap.put(file, destinationFolder);
                    }
                    return CONTINUE;
                }
            });
        } else {
            inOutMap.put(source, destination);
        }

        for (Map.Entry<Path, Path> inOut : inOutMap.entrySet()) {
            Path in = inOut.getKey();
            Path out = inOut.getValue();
            if (out.toFile().isDirectory()) {
                // Use input filename with ".xml", ".json", or ".coffee" extension
                String name = in.toFile().getName();
                if (name.lastIndexOf('.') != -1) {
                    name = name.substring(0, name.lastIndexOf('.'));
                }
                switch (outputFormat) {
                    case JSON:
                        name += ".json";
                        break;
                    case COFFEE:
                        name += ".coffee";
                        break;
                    case XML:
                    default:
                        name += ".xml";
                        break;
                }
                out = out.resolve(name);
            }

            if (out.equals(in)) {
                throw new IllegalArgumentException("input and output file must be different!");
            }

            IGContext igContext = null;
            if (options.has(rootDir)) {
                igContext = new IGContext();
                igContext.initializeFromIni(options.valueOf("root-dir") + File.separator + "ig.ini");
            }

            ModelInfoProvider modelProvider = null;
            if (options.has(model)) {
                final File modelFile = options.valueOf(model);
                if (!modelFile.exists() || modelFile.isDirectory()) {
                    throw new IllegalArgumentException("model must be a valid file!");
                }
                modelProvider = getModelInfoProvider(modelFile);
            }

            writeELM(
                    in,
                    out,
                    outputFormat,
                    modelProvider,
                    igContext,
                    new CqlCompilerOptions(
                            options.has(optimization),
                            options.has(debug) || options.has(annotations),
                            options.has(debug) || options.has(locators),
                            options.has(debug) || options.has(resultTypes),
                            options.has(verify),
                            options.has(detailedErrors), // Didn't include in debug, maybe should...
                            options.has(errorLevel)
                                    ? (CqlCompilerException.ErrorSeverity) options.valueOf(errorLevel)
                                    : CqlCompilerException.ErrorSeverity.Info,
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
                            options.has(compatibilityLevel) ? options.valueOf(compatibilityLevel) : null));
        }
    }
}
