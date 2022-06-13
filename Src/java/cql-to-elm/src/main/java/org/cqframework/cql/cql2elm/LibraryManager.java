package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.fhir.ucum.UcumService;
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

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static org.cqframework.cql.cql2elm.CqlCompilerException.HasErrors;

/**
 * Manages a set of CQL libraries. As new library references are encountered
 * during compilation, the corresponding source is obtained via
 * librarySourceLoader, compiled and cached for later use.
 */
public class LibraryManager {
    private ModelManager modelManager;
    private NamespaceManager namespaceManager;
    private UcumService ucumService;
    private final Map<String, CompiledLibrary> libraries;
    private final Stack<String> compilatonStack;
    private LibrarySourceLoader librarySourceLoader;
    private boolean enableCache;

    public LibraryManager(ModelManager modelManager) {
        if (modelManager == null) {
            throw new IllegalArgumentException("modelManager is null");
        }
        this.modelManager = modelManager;
        if (this.modelManager.getNamespaceManager() != null) {
            this.namespaceManager = modelManager.getNamespaceManager();
        }
        else {
            this.namespaceManager = new NamespaceManager();
        }
        libraries = new HashMap<>();
        compilatonStack = new Stack<>();
        this.enableCache = true;
        this.librarySourceLoader = new PriorityLibrarySourceLoader();
    }

    public ModelManager getModelManager(){
        return this.modelManager;
    }

    public NamespaceManager getNamespaceManager() {
        return this.namespaceManager;
    }

    public UcumService getUcumService() {
        return this.ucumService;
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

    public void enableCache() { this.enableCache = true; }

    public LibraryManager withEnableCache() {
        enableCache();
        return this;
    }

    public void disableCache() { this.enableCache = false; }

    public LibraryManager withDisableCache() {
        disableCache();
        return this;
    }

    public boolean isCacheEnabled() { return enableCache; }

    public Map<String, CompiledLibrary> getCompiledLibraries() {
        return libraries;
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
        String libraryPath = NamespaceManager.getPath(library.getIdentifier().getSystem(), library.getIdentifier().getId(), library.getIdentifier().getVersion());
        libraries.put(libraryPath, library);
    }

    public boolean canResolveLibrary(VersionedIdentifier libraryIdentifier) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        if (enableCache) {
            CompiledLibrary library = libraries.get(libraryPath);
            if (library != null) {
                return true;
            }
        }

        InputStream source = null;
        try {
            if (librarySourceLoader instanceof LibrarySourceLoaderExt) {
                return ((LibrarySourceLoaderExt) librarySourceLoader).isLibrarySourceAvailable(libraryIdentifier,
                        LibraryContentType.ANY);
            } else {
                source = librarySourceLoader.getLibrarySource(libraryIdentifier);
            }

        } catch (Exception e) {
            throw new CqlTranslatorIncludeException(e.getMessage(), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }

        return source != null;
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options, List<CqlCompilerException> errors) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        CompiledLibrary library = null;
        if (enableCache) {
            library = libraries.get(libraryPath);
        }

        if (library != null
                && libraryIdentifier.getVersion() != null
                && !libraryIdentifier.getVersion().equals(library.getIdentifier().getVersion())) {
            throw new CqlTranslatorIncludeException(String.format("Could not resolve reference to library %s, version %s because version %s is already loaded.",
                    libraryPath, libraryIdentifier.getVersion(), library.getIdentifier().getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        } else if (library != null) {
            if (libraryIdentifier.getSystem() == null && library.getIdentifier().getSystem() != null) {
                libraryIdentifier.setSystem(library.getIdentifier().getSystem());
            }
            return library;
        } else {
            library = compileLibrary(libraryIdentifier, options, errors);
            if (!HasErrors(errors)) {
                libraries.put(libraryPath, library);
            }
        }

        return library;
    }

    private CompiledLibrary compileLibrary(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options, List<CqlCompilerException> errors) {

        CompiledLibrary result = null;
        result = tryCompiledLibraryElm(libraryIdentifier, options);
        if (result != null) {
            return result;
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId() , libraryIdentifier.getVersion());

        try {
            InputStream cqlSource;
            if (librarySourceLoader instanceof LibrarySourceLoaderExt) {
                cqlSource = ((LibrarySourceLoaderExt) librarySourceLoader)
                        .getLibrarySource(libraryIdentifier, LibraryContentType.CQL);
            } else {
                cqlSource = librarySourceLoader.getLibrarySource(libraryIdentifier);
            }

            CqlCompiler compiler = new CqlCompiler(
                    namespaceManager.getNamespaceInfoFromUri(libraryIdentifier.getSystem()),
                    libraryIdentifier, modelManager, this, ucumService);
            compiler.run(cqlSource, options);
            if (errors != null) {
                errors.addAll(compiler.getExceptions());
            }

            result = compiler.getCompiledLibrary();
            if (libraryIdentifier.getVersion() != null && !libraryIdentifier.getVersion().equals(result.getIdentifier().getVersion())) {
                throw new CqlTranslatorIncludeException(String.format("Library %s was included as version %s, but version %s of the library was found.",
                        libraryPath, libraryIdentifier.getVersion(), result.getIdentifier().getVersion()),
                        libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
            }

        } catch (IOException e) {
            throw new CqlTranslatorIncludeException(String.format("Errors occurred translating library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }

        if (result == null) {
            throw new CqlTranslatorIncludeException(String.format("Could not load source for library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        } else {
            return result;
        }
    }

    private CompiledLibrary tryCompiledLibraryElm(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options) {
        CompiledLibrary result = null;
        InputStream librarySource = null;

        if (librarySourceLoader instanceof LibrarySourceLoaderExt) {

            LibrarySourceLoaderExt sourceLoaderExt = (LibrarySourceLoaderExt) librarySourceLoader;
            Set<LibraryContentType> supportedTypes = sourceLoaderExt.getSupportedContentTypes();
            for (LibraryContentType type : supportedTypes) {
                if (type != LibraryContentType.CQL) {
                    try {
                        librarySource = sourceLoaderExt.getLibrarySource(libraryIdentifier, type);
                        if (librarySource == null) {
                            continue;
                        }

                        result = generateCompiledLibraryFromElm(libraryIdentifier, librarySource, type, options);

                        if (result != null) {
                            break;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
        return result;
    }


    private CompiledLibrary generateCompiledLibraryFromElm(VersionedIdentifier libraryIdentifier, InputStream librarySource, LibraryContentType type, CqlTranslatorOptions options) {

        Library library = null;
        CompiledLibrary compiledLibrary = null;
        try {
            if (type.equals(LibraryContentType.JSON)) {
                library = ElmJsonLibraryReader.read(new InputStreamReader(librarySource));
            } else if (type.equals(LibraryContentType.XML)) {
                library = ElmXmlLibraryReader.read(new InputStreamReader(librarySource));
            }
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

    protected Boolean translatorOptionsMatch(Library library, CqlTranslatorOptions options) {
        EnumSet<CqlTranslatorOptions.Options> translatorOptions = TranslatorOptionsUtil.getTranslatorOptions(library);
        if (translatorOptions == null) {
            return false;
        }
        return translatorOptions.equals(options.getOptions());
    }

    public void beginCompilation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        if (compilatonStack.contains(libraryName)) {
            throw new IllegalArgumentException(String.format("Circular library reference %s.", libraryName));
        }

        compilatonStack.push(libraryName);
    }

    public void endCompilation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        String currentLibraryName = compilatonStack.pop();
        if (!libraryName.equals(currentLibraryName)) {
            throw new IllegalArgumentException(String.format("Compilation stack imbalance for library %s.", libraryName));
        }
    }
}
