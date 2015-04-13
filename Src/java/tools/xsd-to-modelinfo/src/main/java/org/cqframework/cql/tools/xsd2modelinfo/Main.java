package org.cqframework.cql.tools.xsd2modelinfo;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.cqframework.cql.elm.tracking.DataType;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a ModelInfo.xml for the input xsd.
 */
public class Main {
    public static void main(String[] args) throws IOException, JAXBException {
        OptionParser parser = new OptionParser();
        OptionSpec<File> schemaOpt = parser.accepts("schema").withRequiredArg().ofType(File.class).required();
        OptionSpec<String> modelOpt = parser.accepts("model").withRequiredArg().ofType(String.class);
        OptionSpec<File> outputOpt = parser.accepts("output").withRequiredArg().ofType(File.class);
        OptionSpec<String> normalizePrefixOpt = parser.accepts("normalize-prefix").withRequiredArg().ofType(String.class);
        OptionSpec<String> typeMapOpt = parser.accepts("type-map").withRequiredArg().ofType(String.class);
        OptionSpec<ModelImporterOptions.SimpleTypeRestrictionPolicy> stRestrictionsOpt =
                parser.accepts("simpletype-restriction-policy").withRequiredArg().ofType(ModelImporterOptions.SimpleTypeRestrictionPolicy.class);
        OptionSpec<File> optionsFileOpt = parser.accepts("options-file").withRequiredArg().ofType(File.class);

        OptionSet options = parser.parse(args);

        File schemaFile = schemaOpt.value(options);
        InputStream is = new FileInputStream(schemaFile);
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        schemaCol.setBaseUri(schemaFile.getParent());
        XmlSchema schema = schemaCol.read(new StreamSource(is));

        ModelImporterOptions importerOptions = new ModelImporterOptions();
        if (options.has(modelOpt)) {
            importerOptions.setModel(modelOpt.value(options));
        }
        if (options.has(stRestrictionsOpt)) {
            importerOptions.setSimpleTypeRestrictionPolicy(stRestrictionsOpt.value(options));
        }
        if (options.has(normalizePrefixOpt)) {
            importerOptions.setNormalizePrefix(normalizePrefixOpt.value(options));
        }
        if (options.has(typeMapOpt)) {
            String[] mappings = typeMapOpt.value(options).split(",");
            for (String mapping : mappings) {
                String[] kv = mapping.split("=");
                if (kv.length != 2) {
                    throw new IllegalArgumentException("type-map must be of format: {ns1}key1=type1,{ns2}key2=type2,...");
                }
                importerOptions.getTypeMap().put(QName.valueOf(kv[0].trim()), kv[1].trim());
            }
        }
        if (options.has(optionsFileOpt)) {
            importerOptions.loadProperties(optionsFileOpt.value(options));
        }

        ModelInfo modelInfo = ModelImporter.fromXsd(schema, importerOptions);

        JAXBContext jc = JAXBContext.newInstance(ModelInfo.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File outputfile;
        if (! options.has(outputOpt) || outputOpt.value(options).isDirectory()) {
            // construct output filename using modelinfo
            String name = String.format("%s-modelinfo.xml", modelInfo.getTargetQualifier().getLocalPart());
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
            OutputStreamWriter writer = new OutputStreamWriter(os);
            marshaller.marshal(new ObjectFactory().createModelInfo(modelInfo), writer);
        }
        finally {
            os.close();
        }
    }
}
