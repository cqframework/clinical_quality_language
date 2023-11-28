package org.cqframework.cql.cql2elm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.diff.StringsComparator;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.cqframework.cql.cql2elm.CqlCompilerException.hasErrors;

/**
 * Manages a set of CQL libraries. As new library references are encountered
 * during compilation, the corresponding source is obtained via
 * librarySourceLoader, compiled and cached for later use.
 */
public class LibraryManager {
    public enum CacheMode {
        NONE, READ_ONLY, READ_WRITE
    }

    private static final Logger logger = LoggerFactory.getLogger(LibraryManager.class);

    private final ModelManager modelManager;
    private final NamespaceManager namespaceManager;
    private final CqlCompilerOptions cqlCompilerOptions;
    private final Map<VersionedIdentifier, CompiledLibrary> compiledLibraries;
        private final LibrarySourceLoader librarySourceLoader;

    private UcumService ucumService;


    private static final LibraryContentType[] supportedContentTypes = { LibraryContentType.JSON, LibraryContentType.XML,
            LibraryContentType.CQL };

    public LibraryManager(ModelManager modelManager) {
        this(modelManager, CqlCompilerOptions.defaultOptions(), null);
    }

    public LibraryManager(ModelManager modelManager, CqlCompilerOptions cqlCompilerOptions) {
        this(modelManager, cqlCompilerOptions, null);
    }

    public LibraryManager(ModelManager modelManager, CqlCompilerOptions cqlCompilerOptions,
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

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, List<CqlCompilerException> errors,
            CacheMode cacheMode) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        CompiledLibrary library = null;
        if (cacheMode != CacheMode.NONE) {
            library = compiledLibraries.get(libraryIdentifier);
            if (library != null) {
                return library;
            }
        }

        library = compileLibrary(libraryIdentifier, errors);
        if (!hasErrors(errors) && cacheMode == CacheMode.READ_WRITE) {
            compiledLibraries.put(libraryIdentifier, library);
        }

        return library;
    }

    private CompiledLibrary compileLibrary(VersionedIdentifier libraryIdentifier, List<CqlCompilerException> errors) {

        CompiledLibrary result = null;
        if (!this.cqlCompilerOptions.getEnableCqlOnly()) {
            result = tryCompiledLibraryElm(libraryIdentifier, this.cqlCompilerOptions);
            if (result != null) {
                sortStatements(result);
                return result;
            }
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());

        try {
            InputStream cqlSource = librarySourceLoader.getLibrarySource(libraryIdentifier);
            if (cqlSource == null) {
                throw new CqlIncludeException(String.format("Could not load source for library %s, version %s.",
                        libraryIdentifier.getId(), libraryIdentifier.getVersion()), libraryIdentifier.getSystem(),
                        libraryIdentifier.getId(), libraryIdentifier.getVersion());
            }

            CqlCompiler compiler = new CqlCompiler(
                    namespaceManager.getNamespaceInfoFromUri(libraryIdentifier.getSystem()),
                    libraryIdentifier, this);
            compiler.run(cqlSource);
            if (errors != null) {
                errors.addAll(compiler.getExceptions());
            }

            result = compiler.getCompiledLibrary();
            if (libraryIdentifier.getVersion() != null
                    && !libraryIdentifier.getVersion().equals(result.getIdentifier().getVersion())) {
                throw new CqlIncludeException(
                        String.format("Library %s was included as version %s, but version %s of the library was found.",
                                libraryPath, libraryIdentifier.getVersion(), result.getIdentifier().getVersion()),
                        libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
            }

        } catch (IOException e) {
            throw new CqlIncludeException(String.format("Errors occurred translating library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(),
                    libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }

        if (result == null) {
            throw new CqlIncludeException(String.format("Could not load source for library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(),
                    libraryIdentifier.getId(), libraryIdentifier.getVersion());
        } else {
            sortStatements(result);
            return result;
        }
    }

    private void sortStatements(CompiledLibrary compiledLibrary) {
        if (compiledLibrary == null || compiledLibrary.getLibrary().getStatements() == null) {
            return;
        }

        compiledLibrary.getLibrary().getStatements().getDef().sort((a, b) -> a.getName().compareTo(b.getName()));
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

    private CompiledLibrary generateCompiledLibraryFromElm(VersionedIdentifier libraryIdentifier,
            InputStream librarySource, LibraryContentType type, CqlCompilerOptions options) {

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
                var sig = new FunctionSig(fd.getName(),
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
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FunctionSig other = (FunctionSig) obj;
            return other.name.equals(this.name) && other.numArguments == this.numArguments;
        }
    }

}
