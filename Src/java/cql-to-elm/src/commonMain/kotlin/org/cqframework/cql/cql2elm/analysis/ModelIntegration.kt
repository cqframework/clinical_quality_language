package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.Library
import org.hl7.cql.ast.UsingDefinition

/**
 * Utilities for integrating model-specific information into the analysis pipeline. Model conversion
 * info may reference helper libraries (e.g., FHIR models reference FHIRHelpers). This class
 * provides methods to discover those references so [LibraryResolution] can pre-compile them.
 *
 * This is model-agnostic: any model that declares library-qualified conversions will be detected.
 * FHIR's FHIRHelpers is the primary case today, but the logic applies equally to future models.
 */
class ModelIntegration(private val modelContext: ModelContext) {

    /**
     * Infer the version for helper libraries from the using declarations and model info. For FHIR,
     * the convention is that FHIRHelpers shares the same version as the FHIR model. We generalize:
     * for each model that declares conversions referencing a library, use that model's version.
     *
     * Returns a map of library name → version string.
     */
    internal fun inferHelperLibraryVersions(library: Library): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val usingDefs = library.definitions.filterIsInstance<UsingDefinition>()

        for (usingDef in usingDefs) {
            val modelName = usingDef.modelIdentifier.simpleName
            if (modelName == "System") continue
            val version = usingDef.version?.value ?: continue

            // Check if this model's conversions reference any libraries.
            val model =
                try {
                    modelContext.resolveModel(modelName, version)
                } catch (_: Exception) {
                    continue
                }
            for (conversion in model.getConversions()) {
                val libName = conversion.operator?.libraryName ?: continue
                result.putIfAbsent(libName, version)
            }
        }
        return result
    }
}
