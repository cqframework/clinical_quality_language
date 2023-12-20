package org.cqframework.fhir.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r5.model.ImplementationGuide;
import org.hl7.fhir.utilities.Utilities;

public class IGUtils {

    public static String getImplementationGuideCanonicalBase(String url) {
        String canonicalBase = null;

        if (url != null && !url.isEmpty()) {
            canonicalBase = url.substring(0, url.indexOf("/ImplementationGuide/"));
        }

        return canonicalBase;
    }

    public static ArrayList<String> extractResourcePaths(String rootDir, ImplementationGuide sourceIg)
            throws IOException {
        ArrayList<String> result = new ArrayList<>();
        for (ImplementationGuide.ImplementationGuideDefinitionParameterComponent p :
                sourceIg.getDefinition().getParameter()) {
            if (p.getCode().equals("path-resource")) {
                result.add(Utilities.path(rootDir, p.getValue()));
            }
        }

        File resources = new File(Utilities.path(rootDir, "input/resources"));
        if (resources.exists() && resources.isDirectory()) {
            result.add(resources.getAbsolutePath());
        }

        return result;
    }

    /*
    Determines the CQL content path for the given implementation guide
    @rootDir: The root directory of the implementation guide source
    @sourceIg: The implementationGuide (as an R5 resource)
     */
    public static List<String> extractBinaryPaths(String rootDir, ImplementationGuide sourceIg) {
        List<String> result = new ArrayList<>();

        // Although this is the correct way to read the cql path from an implementation guide,
        // the tooling cannot use this method, because if it's present in the IG, the publisher will
        // redo the CQL translation work. Instead, assume a path of input/cql, or input/pagecontent/cql
        /*
        for (ImplementationGuide.ImplementationGuideDefinitionParameterComponent p : sourceIg.getDefinition().getParameter()) {
            // documentation for this list: https://confluence.hl7.org/display/FHIR/Implementation+Guide+Parameters
            if (p.getCode().equals("path-binary")) {
                result.add(Utilities.path(rootDir, p.getValue()));
            }
        }
        */

        File input = tryDirectory(rootDir, "input/cql");
        if (input != null && input.exists() && input.isDirectory()) {
            result.add(input.getAbsolutePath());
        }

        input = tryDirectory(rootDir, "input/pagecontent/cql");
        if (input != null && input.exists() && input.isDirectory()) {
            result.add(input.getAbsolutePath());
        }

        return result;
    }

    /**
     * Tries to create a File for a path that may not exist.
     *
     * @param rootDir base directory for path
     * @param path child directories for path
     * @return File if directory exists, null otherwise
     */
    protected static File tryDirectory(String rootDir, String path) {
        String combinedPath = null;
        try {
            combinedPath = Utilities.path(rootDir, path);
        } catch (IOException e) {
            return null;
        }

        return new File(combinedPath);
    }
}
