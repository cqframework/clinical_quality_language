package org.cqframework.cql.tools.xsd2modelinfo;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.ObjectFactory;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Generates a ModelInfo.xml for the input xsd.
 */
public class Main {
    public static void main(String[] args) throws IOException, JAXBException {
        OptionParser parser = new OptionParser();
        OptionSpec<File> schemaOpt = parser.accepts("schema").withRequiredArg().ofType(File.class).required();
        OptionSpec<String> modelOpt = parser.accepts("model").withRequiredArg().ofType(String.class);
        OptionSpec<File> configOpt = parser.accepts("config").withOptionalArg().ofType(File.class);
        OptionSpec<File> outputOpt = parser.accepts("output").withRequiredArg().ofType(File.class);
        OptionSpec<String> normalizePrefixOpt = parser.accepts("normalize-prefix").withRequiredArg().ofType(String.class);
        OptionSpec<ModelImporterOptions.ChoiceTypePolicy> choiceTypeOpt =
                parser.accepts("choicetype-policy").withRequiredArg().ofType(ModelImporterOptions.ChoiceTypePolicy.class);
        OptionSpec<ModelImporterOptions.SimpleTypeRestrictionPolicy> stRestrictionsOpt =
                parser.accepts("simpletype-restriction-policy").withRequiredArg().ofType(ModelImporterOptions.SimpleTypeRestrictionPolicy.class);
        OptionSpec<ModelImporterOptions.ElementRedeclarationPolicy> redeclarationsOpt =
                parser.accepts("element-redeclaration-policy").withRequiredArg().ofType(ModelImporterOptions.ElementRedeclarationPolicy.class);
        OptionSpec<ModelImporterOptions.VersionPolicy> versionPolicyOpt =
                parser.accepts("version-policy").withRequiredArg().ofType(ModelImporterOptions.VersionPolicy.class);
        OptionSpec<File> optionsFileOpt = parser.accepts("options-file").withRequiredArg().ofType(File.class);

        OptionSet options = parser.parse(args);

        File schemaFile = schemaOpt.value(options);
        InputStream is = new FileInputStream(schemaFile);
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.setBaseUri(schemaFile.getParent());
        XmlSchema schema = schemaCol.read(new StreamSource(is));

        ModelImporterOptions importerOptions;
        if (options.has(optionsFileOpt)) {
            importerOptions = ModelImporterOptions.loadFromProperties(optionsFileOpt.value(options));
        }
        else {
            importerOptions = new ModelImporterOptions();
        }

        if (options.has(modelOpt)) {
            importerOptions.setModel(modelOpt.value(options));
        }
        if (options.has(choiceTypeOpt)) {
            importerOptions.setChoiceTypePolicy(choiceTypeOpt.value(options));
        }
        if (options.has(stRestrictionsOpt)) {
            importerOptions.setSimpleTypeRestrictionPolicy(stRestrictionsOpt.value(options));
        }
        if (options.has(redeclarationsOpt)) {
            importerOptions.setElementRedeclarationPolicy(redeclarationsOpt.value(options));
        }
        if (options.has(versionPolicyOpt)) {
            importerOptions.setVersionPolicy(versionPolicyOpt.value(options));
        }
        if (options.has(normalizePrefixOpt)) {
            importerOptions.setNormalizePrefix(normalizePrefixOpt.value(options));
        }

        ModelInfo config = null;
        if (configOpt != null) {
            File configFile = configOpt.value(options);
            if (configFile != null) {
                config = JAXB.unmarshal(configFile, ModelInfo.class);
            }
        }

        ModelInfo modelInfo = ModelImporter.fromXsd(schema, importerOptions, config);

        JAXBContext jc = JAXBContext.newInstance(ModelInfo.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File outputfile;
        if (! options.has(outputOpt) || outputOpt.value(options).isDirectory()) {
            // construct output filename using modelinfo
            String name = String.format("%s-modelinfo.xml", modelInfo.getTargetQualifier());
            String basePath = options.has(outputOpt) ? outputOpt.value(options).getAbsolutePath() : schemaFile.getParent();
            outputfile = new File(basePath + File.separator + name);
        } else {
            outputfile = outputOpt.value(options);
        }
        if (outputfile.equals(schemaFile)) {
            throw new IllegalArgumentException("input schema file and output file must be different!");
        }

        OutputStream os = new FileOutputStream(outputfile, false);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
            marshaller.marshal(new ObjectFactory().createModelInfo(modelInfo), writer);
        }
        finally {
            os.close();
        }
    }
}
