package org.cqframework.fhir.utilities;

import org.cqframework.fhir.utilities.exception.IGInitializationException;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_50;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50;
import org.hl7.fhir.convertors.conv30_50.VersionConvertor_30_50;
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r5.context.ILoggingService;
import org.hl7.fhir.r5.elementmodel.Manager;
import org.hl7.fhir.r5.model.ImplementationGuide;
import org.hl7.fhir.utilities.IniFile;
import org.hl7.fhir.utilities.TextFile;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.VersionUtilities;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IGContext {

    private ILoggingService logger;
    public ILoggingService getLogger() {
        return logger;
    }

    protected String rootDir;
    public String getRootDir() {
        return rootDir;
    }

    protected ImplementationGuide sourceIg;
    public ImplementationGuide getSourceIg() {
        return sourceIg;
    }

    protected String fhirVersion;
    public String getFhirVersion() {
        return fhirVersion;
    }

    protected String packageId;
    public String getPackageId() {
        return packageId;
    }

    protected String canonicalBase;
    public String getCanonicalBase() {
        return canonicalBase;
    }

    private List<String> binaryPaths;
    public List<String> getBinaryPaths() {
        return binaryPaths;
    }
    protected void setBinaryPaths(List<String> binaryPaths) {
        this.binaryPaths = binaryPaths;
    }

    public IGContext(ILoggingService logger) {
        this.logger = logger;
    }

    public void initializeFromIg(String rootDir, String igPath, String fhirVersion) {
        this.rootDir = rootDir;

        try {
            igPath = Utilities.path(rootDir, igPath);
        } catch (IOException e) {
            String message = String.format(
                    "Exceptions occurred creating igPath from source rootDir: %s, and igPath: %s", rootDir, igPath);
            logMessage(message);
            throw new IGInitializationException(message, e);
        }

        if (fhirVersion != null) {
            loadSourceIG(igPath, fhirVersion);
        } else {
            loadSourceIG(igPath);
        }

        // TODO: Perhaps we should validate the passed in fhirVersion against the
        // fhirVersion in the IG?

        this.fhirVersion = sourceIg.getFhirVersion().get(0).getCode();
        packageId = sourceIg.getPackageId();
        canonicalBase = IGUtils.getImplementationGuideCanonicalBase(sourceIg.getUrl());

        /*
        try {
            packageManager = new NpmPackageManager(sourceIg, this.fhirVersion);
        } catch (IOException e) {
            String message = String.format("Exceptions occurred loading npm package manager from source Ig: %s",
                    sourceIg.getName());
            logMessage(message);
            throw new IGInitializationException(message, e);
        }
        */

        // Setup binary paths (cql source directories)
        binaryPaths = IGUtils.extractBinaryPaths(rootDir, sourceIg);
    }

    /*
     * Initializes from an ig.ini file in the root directory
     */
    public void initializeFromIni(String iniFile) {
        IniFile ini = new IniFile(new File(iniFile).getAbsolutePath());
        String rootDir = Utilities.getDirectoryForFile(ini.getFileName());
        String igPath = ini.getStringProperty("IG", "ig");
        String specifiedFhirVersion = ini.getStringProperty("IG", "fhir-version");
        if (specifiedFhirVersion == null || specifiedFhirVersion == "") {
            logMessage("fhir-version was not specified in the ini file. Trying FHIR version 4.0.1");
            specifiedFhirVersion = "4.0.1";
        }
        try {
            initializeFromIg(rootDir, igPath, specifiedFhirVersion);
        } catch (Exception e) {
            String message = String.format("Exceptions occurred initializing refresh from ini file '%s':%s", iniFile,
                    e.getMessage());
            logMessage(message);
            throw new IGInitializationException(message, e);
        }
    }
    private ImplementationGuide loadSourceIG(String igPath) {
        try {
            try {
                sourceIg = (ImplementationGuide) org.hl7.fhir.r5.formats.FormatUtilities.loadFile(igPath);
            } catch (IOException | FHIRException e) {
                try {
                    VersionConvertor_40_50 versionConvertor_40_50 = new VersionConvertor_40_50(new BaseAdvisor_40_50());
                    sourceIg = (ImplementationGuide) versionConvertor_40_50
                            .convertResource(org.hl7.fhir.r4.formats.FormatUtilities.loadFile(igPath));
                } catch (IOException | FHIRException ex) {
                    byte[] src = TextFile.fileToBytes(igPath);
                    Manager.FhirFormat fmt = org.hl7.fhir.r5.formats.FormatUtilities.determineFormat(src);

                    org.hl7.fhir.dstu3.formats.ParserBase parser = org.hl7.fhir.dstu3.formats.FormatUtilities
                            .makeParser(fmt.toString());
                    VersionConvertor_30_50 versionConvertor_30_50 = new VersionConvertor_30_50(new BaseAdvisor_30_50());
                    sourceIg = (ImplementationGuide) versionConvertor_30_50.convertResource(parser.parse(src));
                }
            }
        } catch (IOException | FHIRException e) {
            throw new IGInitializationException(String.format("error initializing IG from igPath: %s", igPath), e);
        }

        return sourceIg;
    }

    private ImplementationGuide loadSourceIG(String igPath, String specifiedFhirVersion) {
        try {
            if (VersionUtilities.isR3Ver(specifiedFhirVersion)) {
                byte[] src = TextFile.fileToBytes(igPath);
                Manager.FhirFormat fmt = org.hl7.fhir.r5.formats.FormatUtilities.determineFormat(src);
                org.hl7.fhir.dstu3.formats.ParserBase parser = org.hl7.fhir.dstu3.formats.FormatUtilities
                        .makeParser(fmt.toString());
                VersionConvertor_30_50 versionConvertor_30_50 = new VersionConvertor_30_50(new BaseAdvisor_30_50());
                sourceIg = (ImplementationGuide) versionConvertor_30_50.convertResource(parser.parse(src));
            } else if (VersionUtilities.isR4Ver(specifiedFhirVersion)) {
                org.hl7.fhir.r4.model.Resource res = org.hl7.fhir.r4.formats.FormatUtilities.loadFile(igPath);
                VersionConvertor_40_50 versionConvertor_40_50 = new VersionConvertor_40_50(new BaseAdvisor_40_50());
                sourceIg = (ImplementationGuide) versionConvertor_40_50.convertResource(res);
            } else if (VersionUtilities.isR5Ver(specifiedFhirVersion)) {
                sourceIg = (ImplementationGuide) org.hl7.fhir.r5.formats.FormatUtilities.loadFile(igPath);
            } else {
                throw new FHIRException("Unknown Version '" + specifiedFhirVersion + "'");
            }
        } catch (IOException e) {
            String message = String.format("Exceptions occurred loading IG path: %s", igPath);
            logMessage(message);
            throw new IGInitializationException(message, e);
        }

        return sourceIg;
    }

    public void logMessage(String msg) {
        if (logger != null) {
            logger.logMessage(msg);
        }
        /*
        else {
            // Maybe????
            System.out.println(msg);
        }
         */
    }
}
