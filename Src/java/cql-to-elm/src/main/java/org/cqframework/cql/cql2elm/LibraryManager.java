package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.fhir.ucum.UcumService;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.CodeSystemDef;
import org.hl7.elm.r1.ConceptDef;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.IncludeDef;
import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.UsingDef;
import org.hl7.elm.r1.ValueSetDef;
import org.hl7.elm.r1.VersionedIdentifier;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

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
        TranslatedLibrary library = libraries.get(libraryPath);
        if (library != null) {
            return true;
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

    public TranslatedLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options, List<CqlTranslatorException> errors) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is null.");
        }

        if (libraryIdentifier.getId() == null || libraryIdentifier.getId().equals("")) {
            throw new IllegalArgumentException("libraryIdentifier Id is null");
        }

        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());
        TranslatedLibrary library = libraries.get(libraryPath);

        if (library != null
                && libraryIdentifier.getVersion() != null
                && !libraryIdentifier.getVersion().equals(library.getIdentifier().getVersion())) {
            throw new CqlTranslatorIncludeException(String.format("Could not resolve reference to library %s, version %s because version %s is already loaded.",
                    libraryPath, libraryIdentifier.getVersion(), library.getIdentifier().getVersion()), libraryIdentifier.getSystem(), libraryIdentifier.getId(), libraryIdentifier.getVersion());
        }

        else if (library != null) {
            if (libraryIdentifier.getSystem() == null && library.getIdentifier().getSystem() != null) {
                libraryIdentifier.setSystem(library.getIdentifier().getSystem());
            }
            return library;
        }

        else {
            library = translateLibrary(libraryIdentifier, options, errors);
            if (!HasErrors(errors)) {
                libraries.put(libraryPath, library);
            }
        }

        return library;
    }

    private TranslatedLibrary translateLibrary(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options, List<CqlTranslatorException> errors) {

        TranslatedLibrary result = null;
        result = tryTranslatedLibraryFromElm(libraryIdentifier, options);
        if (result != null) {
            return result;
        }
        String libraryPath = NamespaceManager.getPath(libraryIdentifier.getSystem(), libraryIdentifier.getId());

        try {
            InputStream cqlSource;
            if (librarySourceLoader instanceof LibrarySourceLoaderExt) {
                cqlSource = ((LibrarySourceLoaderExt)librarySourceLoader)
                        .getLibrarySource(libraryIdentifier,LibraryContentType.CQL);
            } else {
                cqlSource = librarySourceLoader.getLibrarySource(libraryIdentifier);
            }

            CqlTranslator translator = CqlTranslator.fromStream(namespaceManager.getNamespaceInfoFromUri(libraryIdentifier.getSystem()),
                    libraryIdentifier, cqlSource, modelManager, this, ucumService, options);
            if (errors != null) {
                errors.addAll(translator.getExceptions());
            }

            result = translator.getTranslatedLibrary();
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

    private TranslatedLibrary tryTranslatedLibraryFromElm(VersionedIdentifier libraryIdentifier, CqlTranslatorOptions options) {

        TranslatedLibrary result;
        InputStream librarySource = null;
        boolean loadSuccess = true;
        LibraryContentType contentType= null;
        try {
            if (librarySourceLoader instanceof LibrarySourceLoaderExt) {
                LibrarySourceLoaderExt sourceLoaderExt = (LibrarySourceLoaderExt) librarySourceLoader;
                if (sourceLoaderExt.isLibrarySourceAvailable(libraryIdentifier, LibraryContentType.JXSON)) {
                    contentType = LibraryContentType.JXSON;
                    librarySource = sourceLoaderExt.getLibrarySource(libraryIdentifier, LibraryContentType.JXSON);
                } else if (sourceLoaderExt.isLibrarySourceAvailable(libraryIdentifier, LibraryContentType.JSON)) {
                    contentType = LibraryContentType.JSON;
                    librarySource = sourceLoaderExt.getLibrarySource(libraryIdentifier, LibraryContentType.JSON);
                } else if (sourceLoaderExt.isLibrarySourceAvailable(libraryIdentifier, LibraryContentType.XML)) {
                    contentType = LibraryContentType.XML;
                    librarySource = sourceLoaderExt.getLibrarySource(libraryIdentifier, LibraryContentType.XML);
                }
            } else {
                librarySource = librarySourceLoader.getLibrarySource(libraryIdentifier);
            }
        } catch (Exception e) {
            loadSuccess = false;
        }

        if (librarySource != null && loadSuccess) {
            result = generateTranslatedLibraryFromEml(libraryIdentifier, librarySource, contentType, options);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private TranslatedLibrary generateTranslatedLibraryFromEml(VersionedIdentifier libraryIdentifier, InputStream librarySource, LibraryContentType type, CqlTranslatorOptions options) {

        Library library = null;
        TranslatedLibrary translatedLibrary = null;
        try {
            if (type.equals(LibraryContentType.JXSON)) {
                library = CqlJxsonLibraryReader.read(new InputStreamReader(librarySource));
            } else if (type.equals(LibraryContentType.JSON)) {
                library = CqlJsonLibraryReader.read(new InputStreamReader(librarySource));
            } else if (type.equals(LibraryContentType.XML)) {
                library = CqlLibraryReader.read(new InputStreamReader(librarySource));
            }
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }


        // refer to xml deserializer bug for annotations
        if (library != null &&
                (type.equals(LibraryContentType.XML) ||
                        translatorOptionsMatch(library, options))) {
            translatedLibrary = generateTranslatedLibrary(library);
            if (translatedLibrary != null) {
                this.cacheLibrary(translatedLibrary);
            }
        }

        return translatedLibrary;
    }

    private TranslatedLibrary generateTranslatedLibrary(Library library) {

        if (library == null) {
            return null;
        }
        boolean translationSuccess = true;
        TranslatedLibrary translatedLibrary = new TranslatedLibrary();
        try {
            if (library != null) {
                translatedLibrary.setLibrary(library);
            }
            if (library.getIdentifier() != null) {
                translatedLibrary.setIdentifier(library.getIdentifier());
            }

            if (library.getUsings() != null && library.getUsings().getDef() != null) {
                for (UsingDef usingDef : library.getUsings().getDef()) {
                    translatedLibrary.add(usingDef);
                }
            }
            if (library.getIncludes() != null && library.getIncludes().getDef() != null) {
                for (IncludeDef includeDef : library.getIncludes().getDef()) {
                    translatedLibrary.add(includeDef);
                }
            }
            if (library.getCodeSystems() != null && library.getCodeSystems().getDef() != null) {
                for (CodeSystemDef codeSystemDef : library.getCodeSystems().getDef()) {
                    translatedLibrary.add(codeSystemDef);
                }
            }
            for (ValueSetDef valueSetDef : library.getValueSets().getDef()) {
                translatedLibrary.add(valueSetDef);
            }

            if (library.getCodes() != null && library.getCodes().getDef() != null) {
                for (CodeDef codeDef : library.getCodes().getDef()) {
                    translatedLibrary.add(codeDef);
                }
            }
            if (library.getConcepts() != null && library.getConcepts().getDef() != null) {
                for (ConceptDef conceptDef : library.getConcepts().getDef()) {
                    translatedLibrary.add(conceptDef);
                }
            }
            if (library.getParameters() != null && library.getParameters().getDef() != null) {
                for (ParameterDef parameterDef : library.getParameters().getDef()) {
                    translatedLibrary.add(parameterDef);
                }
            }
            if (library.getStatements() != null && library.getStatements().getDef() != null) {
                for (ExpressionDef expressionDef : library.getStatements().getDef()) {
                    translatedLibrary.add(expressionDef);
                }
            }
        } catch (Exception e) {
            translationSuccess = false;
        }

        if (translationSuccess) {
            return translatedLibrary;
        }

        return null;
    }

    protected Boolean translatorOptionsMatch(Library library, CqlTranslatorOptions options) {
        EnumSet<CqlTranslator.Options> translatorOptions = TranslatorOptionsUtil.getTranslatorOptions(library);
        if (translatorOptions == null) {
            return false;
        }
        return translatorOptions.equals(options.getOptions());
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
