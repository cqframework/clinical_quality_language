package org.cqframework.cql.cql2elm;

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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.cqframework.cql.cql2elm.CqlCompilerException.HasErrors;

/**
 * Manages a set of CQL libraries. As new library references are encountered
 * during compilation, the corresponding source is obtained via
 * librarySourceLoader, compiled and cached for later use.
 */
public class LibraryManager {
    private static final Logger logger = LoggerFactory.getLogger(LibraryManager.class);

    private final ModelManager modelManager;
    private final NamespaceManager namespaceManager;
    private UcumService ucumService;
    private final Map<String, CompiledLibrary> compiledLibraries;

    private Map<VersionedIdentifier, Library> libraries;
    private final Stack<String> compilationStack;
    private LibrarySourceLoader librarySourceLoader;
    private boolean enableCache;

    private final CqlCompilerOptions cqlCompilerOptions;

    private static final LibraryContentType[] supportedContentTypes = {LibraryContentType.JSON, LibraryContentType.XML, LibraryContentType.CQL};

    public LibraryManager(ModelManager modelManager) {
        this(modelManager, CqlCompilerOptions.defaultOptions());
    }

    public LibraryManager(ModelManager modelManager, CqlCompilerOptions cqlCompilerOptions) {
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
        compiledLibraries = new HashMap<>();
        libraries = new HashMap<>();
        compilationStack = new Stack<>();
        this.enableCache = true;
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

    public void setLibrarySourceLoader(LibrarySourceLoader librarySourceLoader) {
        this.librarySourceLoader = librarySourceLoader;
    }

    public void enableCache() {
        this.enableCache = true;
    }

    public LibraryManager withEnableCache() {
        enableCache();
        return this;
    }

    public void disableCache() {
        this.enableCache = false;
    }

    public LibraryManager withDisableCache() {
        disableCache();
        return this;
    }

    public boolean isCacheEnabled() {
        return enableCache;
    }

    public Map<String, CompiledLibrary> getCompiledLibraries() {
        return compiledLibraries;
    }

    /*
    A "well-known" library name is one that is allowed to resolve without a namespace in a namespace-aware context
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

    public void cacheLibrary(CompiledLibrary library) {
        String libraryPath = NamespaceManager.getPath(library.getIdentifier().getSystem(), library.getIdentifier().getId());
        compiledLibraries.put(libraryPath, library);
        libraries.put(library.getIdentifier(), library.getLibrary());
    }

    public void cacheLibrary(Library library) {
        libraries.put(library.getIdentifier(), library);
    }

    public Library getCachedLibrary(VersionedIdentifier libraryIdentifier) {
        if (enableCache) {
            if (libraries.containsKey(libraryIdentifier)) {
                return libraries.get(libraryIdentifier);
            } else if (true) {
                String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());
                if(compiledLibraries.containsKey(libraryPath)) {
                    return compiledLibraries.get(libraryPath).getLibrary();
                }
            }
        }
        return null;
    }

    public boolean canResolveLibrary(VersionedIdentifier libraryIdentifier) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());
        if (enableCache) {
            CompiledLibrary library = compiledLibraries.get(libraryPath);
            if (library != null) {
                return true;
            }
            else if(libraries.containsKey(libraryIdentifier)) {
                return true;
            }
        }

        for (LibraryContentType type : supportedContentTypes) {
            if (librarySourceLoader.isLibraryContentAvailable(libraryIdentifier, type)) {
                return true;
            }
        }

        return false;
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier) {
        return this.resolveLibrary(libraryIdentifier, new ArrayList<>());
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, List<CqlCompilerException> errors) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());
        CompiledLibrary library = null;
        if (enableCache) {
            library = compiledLibraries.get(libraryPath);
            if(library == null && libraries.containsKey(libraryIdentifier)) {
                library = generateCompiledLibrary(libraries.get(libraryIdentifier));
            }
        }

        if (library != null
                && libraryIdentifier.getVersion() != null
                && !libraryIdentifier.getVersion().equals(library.getIdentifier().getVersion())) {
            throw new CqlIncludeException(String.format("Could not resolve reference to library %s, version %s because version %s is already loaded.",
                    libraryPath, libraryIdentifier.getVersion(), library.getIdentifier().getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        } else if (library != null) {
            if (libraryIdentifier.getSystem() == null && library.getIdentifier().getSystem() != null) {
                libraryIdentifier.setSystem(library.getIdentifier().getSystem());
            }
            return library;
        } else {
            library = compileLibrary(libraryIdentifier, errors);
            if (!HasErrors(errors)) {
                compiledLibraries.put(libraryPath, library);
                libraries.put(libraryIdentifier, library.getLibrary());
            }
        }


        return library;
    }

    private CompiledLibrary compileLibrary(VersionedIdentifier libraryIdentifier, List<CqlCompilerException> errors) {

        CompiledLibrary result = null;
        if(!this.cqlCompilerOptions.getEnableCqlOnly()) {
            result = tryCompiledLibraryElm(libraryIdentifier, this.cqlCompilerOptions);
            if (result != null) {
                return result;
            }
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());

        try {
            InputStream cqlSource = librarySourceLoader.getLibrarySource(libraryIdentifier);
            if (cqlSource == null) {
                throw new CqlIncludeException(String.format("Could not load source for library %s, version %s.",
                        libraryIdentifier.getId(), libraryIdentifier.getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
            }

            CqlCompiler compiler = new CqlCompiler(
                    namespaceManager.getNamespaceInfoFromUri(libraryIdentifier.getSystem()),
                    libraryIdentifier, modelManager, this);
            compiler.run(cqlSource);
            if (errors != null) {
                errors.addAll(compiler.getExceptions());
            }

            result = compiler.getCompiledLibrary();
            if (libraryIdentifier.getVersion() != null && !libraryIdentifier.getVersion().equals(result.getIdentifier().getVersion())) {
                throw new CqlIncludeException(String.format("Library %s was included as version %s, but version %s of the library was found.",
                        libraryPath, libraryIdentifier.getVersion(), result.getIdentifier().getVersion()),
                        libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
            }

        } catch (IOException e) {
            throw new CqlIncludeException(String.format("Errors occurred translating library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }

        if (result == null) {
            throw new CqlIncludeException(String.format("Could not load source for library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        } else {
            return result;
        }
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


    private CompiledLibrary generateCompiledLibraryFromElm(VersionedIdentifier libraryIdentifier, InputStream librarySource, LibraryContentType type, CqlCompilerOptions options) {

        Library library = null;
        CompiledLibrary compiledLibrary = null;
        try {
            library = ElmLibraryReaderFactory.getReader(type.mimeType()).read(new InputStreamReader(librarySource));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (library != null && translatorOptionsMatch(library, options)) {
            compiledLibrary = generateCompiledLibrary(library);
            if (compiledLibrary != null) {
                this.cacheLibrary(compiledLibrary);
            }
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

                    //to do implement an ElmTypeInferencingVisitor; make sure that the resultType is set for each node
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

    protected Boolean translatorOptionsMatch(Library library, CqlCompilerOptions options) {
        EnumSet<CqlCompilerOptions.Options> translatorOptions = TranslatorOptionsUtil.getTranslatorOptions(library);
        if (translatorOptions == null) {
            return false;
        }
        return translatorOptions.equals(options.getOptions());
    }

    public void beginCompilation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        if (compilationStack.contains(libraryName)) {
            throw new IllegalArgumentException(String.format("Circular library reference %s.", libraryName));
        }

        compilationStack.push(libraryName);
    }

    public void endCompilation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        String currentLibraryName = compilationStack.pop();
        if (!libraryName.equals(currentLibraryName)) {
            throw new IllegalArgumentException(String.format("Compilation stack imbalance for library %s.", libraryName));
        }
    }
}
