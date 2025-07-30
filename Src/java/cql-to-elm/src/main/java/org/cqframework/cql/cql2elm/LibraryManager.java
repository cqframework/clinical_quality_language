package org.cqframework.cql.cql2elm;

import static org.cqframework.cql.cql2elm.CqlCompilerException.hasErrors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.serializing.ElmLibraryReaderFactory;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeSystemDef;
import org.hl7.elm.r1.ConceptDef;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.IncludeDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.UsingDef;
import org.hl7.elm.r1.ValueSetDef;
import org.hl7.elm.r1.VersionedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a set of CQL libraries. As new library references are encountered
 * during compilation, the corresponding source is obtained via
 * librarySourceLoader, compiled and cached for later use.
 */
public class LibraryManager {
    public enum CacheMode {
        NONE,
        READ_ONLY,
        READ_WRITE
    }

    private static final Logger logger = LoggerFactory.getLogger(LibraryManager.class);

    private final ModelManager modelManager;
    private final NamespaceManager namespaceManager;
    private final CqlCompilerOptions cqlCompilerOptions;
    private final Map<VersionedIdentifier, CompiledLibrary> compiledLibraries;
    private final LibrarySourceLoader librarySourceLoader;

    private UcumService ucumService;

    private static final LibraryContentType[] supportedContentTypes = {
        LibraryContentType.JSON, LibraryContentType.XML, LibraryContentType.CQL
    };

    public LibraryManager(ModelManager modelManager) {
        this(modelManager, CqlCompilerOptions.defaultOptions(), null);
    }

    public LibraryManager(ModelManager modelManager, CqlCompilerOptions cqlCompilerOptions) {
        this(modelManager, cqlCompilerOptions, null);
    }

    public LibraryManager(
            ModelManager modelManager,
            CqlCompilerOptions cqlCompilerOptions,
            Map<VersionedIdentifier, CompiledLibrary> libraryCache) {
        if (modelManager == null) {
            throw new IllegalArgumentException("modelManager is null");
        }
        this.modelManager = modelManager;
        this.cqlCompilerOptions = cqlCompilerOptions;
        if (this.modelManager.getNamespaceManager() != null) {
            this.namespaceManager = modelManager.getNamespaceManager();
        } else {
            this.namespaceManager = new NamespaceManager();
        }

        if (libraryCache != null) {
            this.compiledLibraries = libraryCache;
        } else {
            this.compiledLibraries = new HashMap<>();
        }

        this.librarySourceLoader = new PriorityLibrarySourceLoader();
    }

    public CqlCompilerOptions getCqlCompilerOptions() {
        return this.cqlCompilerOptions;
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public NamespaceManager getNamespaceManager() {
        return this.namespaceManager;
    }

    public Map<VersionedIdentifier, CompiledLibrary> getCompiledLibraries() {
        return this.compiledLibraries;
    }

    public UcumService getUcumService() {
        if (this.ucumService == null) {
            this.ucumService = getDefaultUcumService();
        }

        return ucumService;
    }

    protected synchronized UcumService getDefaultUcumService() {
        try {
            return new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
        } catch (UcumException e) {
            logger.warn("Error creating shared UcumService", e);
        }

        return null;
    }

    public void setUcumService(UcumService ucumService) {
        this.ucumService = ucumService;
    }

    public LibrarySourceLoader getLibrarySourceLoader() {
        return librarySourceLoader;
    }

    /*
     * A "well-known" library name is one that is allowed to resolve without a
     * namespace in a namespace-aware context
     */
    public boolean isWellKnownLibraryName(String unqualifiedIdentifier) {
        if (unqualifiedIdentifier == null) {
            return false;
        }

        switch (unqualifiedIdentifier) {
            case "FHIRHelpers":
                return true;
            default:
                return false;
        }
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, CacheMode cacheMode) {
        return this.resolveLibrary(libraryIdentifier, new ArrayList<>(), cacheMode);
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier) {
        return this.resolveLibrary(libraryIdentifier, new ArrayList<>(), CacheMode.READ_WRITE);
    }

    public boolean canResolveLibrary(VersionedIdentifier libraryIdentifier) {
        var lib = this.resolveLibrary(libraryIdentifier);
        return lib != null;
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, List<CqlCompilerException> errors) {
        return this.resolveLibrary(libraryIdentifier, errors, CacheMode.READ_WRITE);
    }

    // LUKETODO:  call the multilib method here
    public CompiledLibrary resolveLibrary(
            VersionedIdentifier libraryIdentifier, List<CqlCompilerException> errors, CacheMode cacheMode) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().isEmpty()) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        CompiledLibrary library = null;
        if (cacheMode != CacheMode.NONE) {
            library = compiledLibraries.get(libraryIdentifier);
            if (library != null) {
                return library;
            }
        }

        var compileLibraryResult = compileLibrary(libraryIdentifier);
        library = compileLibraryResult.compiledLibrary();
        if (!hasErrors(compileLibraryResult.errors()) && cacheMode == CacheMode.READ_WRITE) {
            compiledLibraries.put(libraryIdentifier, library);
        } else {
            // LUKETODO: can we just return the result record instead ?
            errors.addAll(compileLibraryResult.errors());
        }

        return library;
    }

    public List<CompiledLibrary> resolveLibraries(
            List<VersionedIdentifier> libraryIdentifiers,
            Map<VersionedIdentifier, List<CqlCompilerException>> errorsById) {

        return resolveLibraries(libraryIdentifiers, errorsById, CacheMode.READ_WRITE);
    }

    public List<CompiledLibrary> resolveLibraries(
            List<VersionedIdentifier> libraryIdentifiers,
            Map<VersionedIdentifier, List<CqlCompilerException>> errorsById,
            CacheMode cacheMode) {

        if (libraryIdentifiers == null || libraryIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("libraryIdentifier is null or empty.");
        }

        if (libraryIdentifiers.stream()
                .anyMatch(libraryIdentifier -> libraryIdentifier.getId() == null
                        || libraryIdentifier.getId().isEmpty())) {
            throw new IllegalArgumentException("at least one libraryIdentifier Id is null");
        }

        // LUKETODO:  do we need to order these?
        var libs = new ArrayList<CompiledLibrary>();

        if (cacheMode != CacheMode.NONE) {

            // Ensure that cache retrieved libraries are in the same order as the input identifiers so we
            // don't get a mismatch later
            var libraries = libraryIdentifiers.stream()
                    .filter(compiledLibraries::containsKey)
                    .map(compiledLibraries::get)
                    .toList();

            if (libraries.size() == libraryIdentifiers.size()) {
                return libraries;
            }

            libs.addAll(libraries);
        }

        for (VersionedIdentifier libraryIdentifier : libraryIdentifiers) {
            if (libs.stream().map(CompiledLibrary::getIdentifier).toList().contains(libraryIdentifier)) {
                logger.debug("library {} already in cache, skipping compilation", libraryIdentifier.getId());
                continue;
            }

            var compiledlibraryResult = compileLibrary(libraryIdentifier);

            // If we have any errors, ignore the compiled library altogether just like in the single lib case
            if (!hasErrors(compiledlibraryResult.errors())) {
                // add to returned libs regardless of cache mode
                libs.add(compiledlibraryResult.compiledLibrary());
                if (cacheMode == CacheMode.READ_WRITE) {
                    logger.debug("adding library to cache: {}", libraryIdentifier.getId());
                    compiledLibraries.put(libraryIdentifier, compiledlibraryResult.compiledLibrary());
                }
            }

            // We can have both successfully compiled libraries and errors, especially among multiple libraries
            if (hasErrors(compiledlibraryResult.errors())) {
                errorsById.put(libraryIdentifier, compiledlibraryResult.errors());
            }
        }

        return List.copyOf(libs);
    }

    private CompiledLibraryResult compileLibrary(VersionedIdentifier libraryIdentifier) {

        var libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());

        if (!this.cqlCompilerOptions.getEnableCqlOnly()) {
            var elmCompiledLibrary = tryCompiledLibraryElm(libraryIdentifier, this.cqlCompilerOptions);
            if (elmCompiledLibrary != null) {
                validateIdentifiers(libraryIdentifier, elmCompiledLibrary, libraryPath);
                sortStatements(elmCompiledLibrary);
                return new CompiledLibraryResult(elmCompiledLibrary, List.of());
            }
        }

        CompiledLibrary compiledLibrary;
        List<CqlCompilerException> errors;

        try {
            InputStream cqlSource = librarySourceLoader.getLibrarySource(libraryIdentifier);
            if (cqlSource == null) {
                throw new CqlIncludeException(
                        String.format(
                                "Could not load source for library %s, version %s, namespace uri %s.",
                                libraryIdentifier.getId(),
                                libraryIdentifier.getVersion(),
                                libraryIdentifier.getSystem()),
                        libraryIdentifier.getSystem(),
                        libraryIdentifier.getId(),
                        libraryIdentifier.getVersion());
            }

            CqlCompiler compiler = new CqlCompiler(
                    namespaceManager.getNamespaceInfoFromUri(libraryIdentifier.getSystem()), libraryIdentifier, this);
            compiler.run(cqlSource);

            // LUKETODO:  ensure these errors get passed all the way up to the measure report
            // LUKETODO:  errors on one of the using'd libraries are not obviously related to that library, as opposed
            // to the
            // downstream library
            errors = List.copyOf(compiler.getExceptions());
            compiledLibrary = compiler.getCompiledLibrary();

            if (compiledLibrary == null) {
                throw new CqlIncludeException(
                        String.format(
                                "Could not load source for library %s, version %s.",
                                libraryPath, libraryIdentifier.getVersion()),
                        libraryIdentifier.getSystem(),
                        libraryIdentifier.getId(),
                        libraryIdentifier.getVersion());
            }

            validateIdentifiers(libraryIdentifier, compiledLibrary, libraryPath);

        } catch (IOException e) {
            throw new CqlIncludeException(
                    String.format(
                            "Errors occurred translating library %s, version %s.",
                            libraryPath, libraryIdentifier.getVersion()),
                    libraryIdentifier.getSystem(),
                    libraryIdentifier.getId(),
                    libraryIdentifier.getVersion(),
                    e);
        }

        sortStatements(compiledLibrary);
        return new CompiledLibraryResult(compiledLibrary, errors);
    }

    // LUKETODO:  look at newly unused private methods and get rid of them

    private void validateIdentifiers(
            VersionedIdentifier libraryIdentifier, CompiledLibrary result, String libraryPath) {

        var resultIdentifier = result.getIdentifier();

        var areIdsEqual = libraryIdentifier.getId().equals(resultIdentifier.getId());
        var libraryIdentifierVersion = libraryIdentifier.getVersion();
        var resultIdentifierVersion = resultIdentifier.getVersion();

        // If the library VersionedIdentifier used to query is null, then don't compare to the result library version,
        // since we're doing a broader search
        final boolean areIdentifiersValid;
        if (libraryIdentifierVersion == null) {
            areIdentifiersValid = areIdsEqual;
        } else {
            var areVersionsEqual = libraryIdentifierVersion.equals(resultIdentifier.getVersion());
            areIdentifiersValid = areIdsEqual && areVersionsEqual;
        }

        if (!areIdentifiersValid) {
            throw new CqlIncludeException(
                    String.format(
                            "Library %s was included with version %s, but id: %s and version %s of the library was found.",
                            libraryPath, libraryIdentifierVersion, resultIdentifier.getId(), resultIdentifierVersion),
                    libraryIdentifier.getSystem(),
                    libraryIdentifier.getId(),
                    libraryIdentifierVersion == null ? "null" : libraryIdentifierVersion);
        }
    }

    private void sortStatements(CompiledLibrary compiledLibrary) {
        if (compiledLibrary == null || compiledLibrary.getLibrary().getStatements() == null) {
            return;
        }

        compiledLibrary.getLibrary().getStatements().getDef().sort((a, b) -> a.getName()
                .compareTo(b.getName()));
    }

    private CompiledLibrary tryCompiledLibraryElm(VersionedIdentifier libraryIdentifier, CqlCompilerOptions options) {
        InputStream elm = null;
        for (LibraryContentType type : supportedContentTypes) {
            if (LibraryContentType.CQL == type) {
                continue;
            }

            elm = librarySourceLoader.getLibraryContent(libraryIdentifier, type);
            if (elm == null) {
                continue;
            }

            return generateCompiledLibraryFromElm(libraryIdentifier, elm, type, options);
        }

        return null;
    }

    private CompiledLibrary generateCompiledLibraryFromElm(
            VersionedIdentifier libraryIdentifier,
            InputStream librarySource,
            LibraryContentType type,
            CqlCompilerOptions options) {

        Library library = null;
        CompiledLibrary compiledLibrary = null;
        try {
            library = ElmLibraryReaderFactory.getReader(type.mimeType()).read(new InputStreamReader(librarySource));
        } catch (IOException e) {
            // intentionally ignored
        }

        if (library != null && checkBinaryCompatibility(library)) {
            compiledLibrary = generateCompiledLibrary(library);
        }

        return compiledLibrary;
    }

    private CompiledLibrary generateCompiledLibrary(Library library) {

        if (library == null) {
            return null;
        }
        boolean compilationSuccess = true;
        CompiledLibrary compiledLibrary = new CompiledLibrary();
        try {
            if (library != null) {
                compiledLibrary.setLibrary(library);
            }
            if (library.getIdentifier() != null) {
                compiledLibrary.setIdentifier(library.getIdentifier());
            }

            if (library.getUsings() != null && library.getUsings().getDef() != null) {
                for (UsingDef usingDef : library.getUsings().getDef()) {
                    compiledLibrary.add(usingDef);
                }
            }
            if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
                for (IncludeDef includeDef : library.getIncludes().getDef()) {
                    compiledLibrary.add(includeDef);
                }
            }
            if (library.getCodeSystems() != null && library.getCodeSystems().getDef() != null) {
                for (CodeSystemDef codeSystemDef : library.getCodeSystems().getDef()) {
                    compiledLibrary.add(codeSystemDef);
                }
            }
            for (ValueSetDef valueSetDef : library.getValueSets().getDef()) {
                compiledLibrary.add(valueSetDef);
            }

            if (library.getCodes() != null && library.getCodes().getDef() != null) {
                for (CodeDef codeDef : library.getCodes().getDef()) {
                    compiledLibrary.add(codeDef);
                }
            }
            if (library.getConcepts() != null && library.getConcepts().getDef() != null) {
                for (ConceptDef conceptDef : library.getConcepts().getDef()) {
                    compiledLibrary.add(conceptDef);
                }
            }
            if (library.getParameters() != null && library.getParameters().getDef() != null) {
                for (ParameterDef parameterDef : library.getParameters().getDef()) {
                    compiledLibrary.add(parameterDef);
                }
            }
            if (library.getStatements() != null && library.getStatements().getDef() != null) {
                for (ExpressionDef expressionDef : library.getStatements().getDef()) {

                    // to do implement an ElmTypeInferencingVisitor; make sure that the resultType
                    // is set for each node
                    if (expressionDef.getResultType() != null) {
                        compiledLibrary.add(expressionDef);
                    } else {
                        compilationSuccess = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            compilationSuccess = false;
        }

        if (compilationSuccess) {
            return compiledLibrary;
        }

        return null;
    }

    protected Boolean compilerOptionsMatch(Library library) {
        Set<CqlCompilerOptions.Options> compilerOptions = CompilerOptions.getCompilerOptions(library);
        if (compilerOptions == null) {
            return false;
        }
        return compilerOptions.equals(this.cqlCompilerOptions.getOptions());
    }

    private boolean checkBinaryCompatibility(Library library) {
        if (library == null) {
            return false;
        }

        return this.isSignatureCompatible(library)
                && this.isVersionCompatible(library)
                && this.compilerOptionsMatch(library);
    }

    private boolean isSignatureCompatible(Library library) {
        return !hasOverloadedFunctions(library) || hasSignature(library);
    }

    private boolean hasOverloadedFunctions(Library library) {
        if (library == null || library.getStatements() == null) {
            return false;
        }

        Set<FunctionSig> functionNames = new HashSet<>();
        for (ExpressionDef ed : library.getStatements().getDef()) {
            if (ed instanceof FunctionDef) {
                FunctionDef fd = (FunctionDef) ed;
                var sig = new FunctionSig(
                        fd.getName(),
                        fd.getOperand() == null ? 0 : fd.getOperand().size());
                if (functionNames.contains(sig)) {
                    return true;
                } else {
                    functionNames.add(sig);
                }
            }
        }
        return false;
    }

    boolean hasSignature(Library library) {
        if (library != null && library.getStatements() != null) {
            // Just a quick top-level scan for signatures. To fully verify we'd have to
            // recurse all
            // the way down. At that point, let's just translate.
            for (ExpressionDef ed : library.getStatements().getDef()) {
                if (ed.getExpression() instanceof FunctionRef) {
                    FunctionRef fr = (FunctionRef) ed.getExpression();
                    if (fr.getSignature() != null && !fr.getSignature().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isVersionCompatible(Library library) {
        if (!StringUtils.isEmpty(this.cqlCompilerOptions.getCompatibilityLevel())) {
            if (library.getAnnotation() != null) {
                String version = CompilerOptions.getCompilerVersion(library);
                if (version != null) {
                    return version.equals(this.cqlCompilerOptions.getCompatibilityLevel());
                }
            }
        }

        return false;
    }

    static class FunctionSig {

        private final String name;
        private final int numArguments;

        public FunctionSig(String name, int numArguments) {
            this.name = name;
            this.numArguments = numArguments;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + name.hashCode();
            result = prime * result + numArguments;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            FunctionSig other = (FunctionSig) obj;
            return other.name.equals(this.name) && other.numArguments == this.numArguments;
        }
    }
}
