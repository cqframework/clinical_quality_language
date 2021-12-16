package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.fhir.ucum.UcumService;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.cqframework.cql.cql2elm.CqlTranslatorException.HasErrors;

/**
 * Manages a set of CQL libraries. As new library references are encountered
 * during translation, the corresponding source is obtained via
 * librarySourceLoader, translated and cached for later use.
 */
public class LibraryManager {
    private ModelManager modelManager;
    private NamespaceManager namespaceManager;
    private UcumService ucumService;
    private final Map<String, TranslatedLibrary> libraries;
    private final Stack<String> translationStack;
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
        translationStack = new Stack<>();
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

    public Map<String, TranslatedLibrary> getTranslatedLibraries() {
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

    public void cacheLibrary(TranslatedLibrary library) {
        String libraryPath = NamespaceManager.getPath(library.getIdentifier().getSystem(), library.getIdentifier().getId());
        libraries.put(libraryPath, library);
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
            TranslatedLibrary library = libraries.get(libraryPath);
            if (library != null) {
                return true;
            }
        }

        InputStream librarySource = null;
        try {
            librarySource = librarySourceLoader.getLibrarySource(libraryIdentifier);
        }
        catch (Exception e) {
            throw new CqlTranslatorIncludeException(e.getMessage(), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }

        return librarySource != null;
    }

    public TranslatedLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options, List<CqlTranslatorException> errors) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());
        TranslatedLibrary library = null;
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
            library = translateLibrary(libraryIdentifier, options, errors);
            if (!HasErrors(errors)) {
                libraries.put(libraryPath, library);
            }
        }


        return library;
    }

    private TranslatedLibrary translateLibrary(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options, List<CqlTranslatorException> errors) {
        InputStream librarySource = null;
        try {
            librarySource = librarySourceLoader.getLibrarySource(libraryIdentifier);
        }
        catch (Exception e) {
            throw new CqlTranslatorIncludeException(e.getMessage(), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());
        if (librarySource == null) {
            throw new CqlTranslatorIncludeException(String.format("Could not load source for library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        }

        try {
            CqlTranslator translator = CqlTranslator.fromStream(namespaceManager.getNamespaceInfoFromUri(libraryIdentifier.getSystem()),
                    libraryIdentifier, librarySource, modelManager, this, ucumService, options);
            if (errors != null) {
                errors.addAll(translator.getExceptions());
            }

            TranslatedLibrary result = translator.getTranslatedLibrary();
            if (libraryIdentifier.getVersion() != null && !libraryIdentifier.getVersion().equals(result.getIdentifier().getVersion())) {
                throw new CqlTranslatorIncludeException(String.format("Library %s was included as version %s, but version %s of the library was found.",
                        libraryPath, libraryIdentifier.getVersion(), result.getIdentifier().getVersion()),
                        libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
            }

            return result;
        } catch (IOException e) {
            throw new CqlTranslatorIncludeException(String.format("Errors occurred translating library %s, version %s.",
                    libraryPath, libraryIdentifier.getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion(), e);
        }
    }

    public void beginTranslation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        if (translationStack.contains(libraryName)) {
            throw new IllegalArgumentException(String.format("Circular library reference %s.", libraryName));
        }

        translationStack.push(libraryName);
    }

    public void endTranslation(String libraryName) {
        if (libraryName == null || libraryName.equals("")) {
            throw new IllegalArgumentException("libraryName is null.");
        }

        String currentLibraryName = translationStack.pop();
        if (!libraryName.equals(currentLibraryName)) {
            throw new IllegalArgumentException(String.format("Translation stack imbalance for library %s.", libraryName));
        }
    }
}
