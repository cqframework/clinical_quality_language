package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.cql.model.NamespaceAware;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Retrieve;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CqlCompiler {
    private Library library = null;
    private CompiledLibrary compiledLibrary = null;
    private Object visitResult = null;
    private List<Retrieve> retrieves = null;
    private List<CqlCompilerException> exceptions = null;
    private List<CqlCompilerException> errors = null;
    private List<CqlCompilerException> warnings = null;
    private List<CqlCompilerException> messages = null;
    private VersionedIdentifier sourceInfo = null;
    private NamespaceInfo namespaceInfo = null;
    private ModelManager modelManager = null;
    private LibraryManager libraryManager = null;

    public CqlCompiler(ModelManager modelManager, LibraryManager libraryManager) {
        this(null, null, modelManager, libraryManager);
    }

    public CqlCompiler(NamespaceInfo namespaceInfo, ModelManager modelManager, LibraryManager libraryManager) {
        this(namespaceInfo, null, modelManager, libraryManager);
    }

    public CqlCompiler(NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, ModelManager modelManager,
                        LibraryManager libraryManager) {
        this.sourceInfo = sourceInfo;
        this.namespaceInfo = namespaceInfo;
        this.modelManager = modelManager;
        this.libraryManager = libraryManager;

        if (this.sourceInfo == null) {
            this.sourceInfo = new VersionedIdentifier().withId("Anonymous").withSystem("text/cql");
        }

        if (this.namespaceInfo != null) {
            modelManager.getNamespaceManager().ensureNamespaceRegistered(this.namespaceInfo);
            libraryManager.getNamespaceManager().ensureNamespaceRegistered(this.namespaceInfo);
        }

        if (libraryManager.getNamespaceManager().hasNamespaces() && libraryManager.getLibrarySourceLoader() instanceof NamespaceAware) {
            ((NamespaceAware)libraryManager.getLibrarySourceLoader()).setNamespaceManager(libraryManager.getNamespaceManager());
        }
    }

    public Library getLibrary() {
        return library;
    }
    public CompiledLibrary getCompiledLibrary() {
        return compiledLibrary;
    }
    public Object toObject() {
        return visitResult;
    }
    public List<Retrieve> toRetrieves() {
        return retrieves;
    }
    public Map<String, CompiledLibrary> getCompiledLibraries() {
        return libraryManager.getCompiledLibraries();
    }

    public Map<String, Library> getLibraries() {
        Map<String, Library> result = new HashMap<String, Library>();
        for (String libraryName : libraryManager.getCompiledLibraries().keySet()) {
            result.put(libraryName, libraryManager.getCompiledLibraries().get(libraryName).getLibrary());
        }
        return result;
    }

    public List<CqlCompilerException> getExceptions() { return exceptions; }
    public List<CqlCompilerException> getErrors() { return errors; }
    public List<CqlCompilerException> getWarnings() { return warnings; }
    public List<CqlCompilerException> getMessages() { return messages; }

    private class CqlErrorListener extends BaseErrorListener {

        private LibraryBuilder builder;
        private boolean detailedErrors;

        public CqlErrorListener(LibraryBuilder builder, boolean detailedErrors) {
            this.builder = builder;
            this.detailedErrors = detailedErrors;
        }

        private VersionedIdentifier extractLibraryIdentifier(cqlParser parser) {
            RuleContext context = parser.getContext();
            while (context != null && !(context instanceof cqlParser.LibraryContext)) {
                context = context.parent;
            }

            if (context instanceof cqlParser.LibraryContext) {
                cqlParser.LibraryDefinitionContext ldc = ((cqlParser.LibraryContext)context).libraryDefinition();
                if (ldc != null && ldc.qualifiedIdentifier() != null && ldc.qualifiedIdentifier().identifier() != null) {
                    return new VersionedIdentifier().withId(StringEscapeUtils.unescapeCql(ldc.qualifiedIdentifier().identifier().getText()));
                }
            }

            return null;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            var libraryIdentifier = builder.getLibraryIdentifier();
            if (libraryIdentifier == null) {
                // Attempt to extract a libraryIdentifier from the currently parsed content
                if (recognizer instanceof cqlParser) {
                    libraryIdentifier = extractLibraryIdentifier((cqlParser)recognizer);
                }
                if (libraryIdentifier == null) {
                    libraryIdentifier = sourceInfo;
                }
            }
            TrackBack trackback = new TrackBack(libraryIdentifier, line, charPositionInLine, line, charPositionInLine);

            if (detailedErrors) {
                builder.recordParsingException(new CqlSyntaxException(msg, trackback, e));
                builder.recordParsingException(new CqlCompilerException(msg, trackback, e));
            }
            else {
                if (offendingSymbol instanceof CommonToken) {
                    CommonToken token = (CommonToken) offendingSymbol;
                    builder.recordParsingException(new CqlSyntaxException(String.format("Syntax error at %s", token.getText()), trackback, e));
                } else {
                    builder.recordParsingException(new CqlSyntaxException("Syntax error", trackback, e));
                }
            }
        }
    }

    public Library run(String cqlText) {
        return run(CharStreams.fromString(cqlText));
    }


    public Library run(InputStream is) throws IOException {
        return run(CharStreams.fromStream(is));
    }

    public Library run(CharStream is) {
        exceptions = new ArrayList<>();
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        messages = new ArrayList<>();

        LibraryBuilder builder = new LibraryBuilder(namespaceInfo, modelManager, libraryManager);
        builder.setCompilerOptions(libraryManager.getCqlCompilerOptions());
        Cql2ElmVisitor visitor = new Cql2ElmVisitor(builder);
        builder.setVisitor(visitor);
        visitor.setTranslatorOptions(libraryManager.getCqlCompilerOptions());

        CqlCompiler.CqlErrorListener errorListener = new CqlCompiler.CqlErrorListener(builder, visitor.isDetailedErrorsEnabled());

        cqlLexer lexer = new cqlLexer(is);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);

        parser.removeErrorListeners(); // Clear the default console listener
        parser.addErrorListener(errorListener);
        ParseTree tree = parser.library();

        CqlPreprocessorVisitor preprocessor = new CqlPreprocessorVisitor();
        preprocessor.setTokenStream(tokens);
        preprocessor.visit(tree);

        visitor.setTokenStream(tokens);
        visitor.setLibraryInfo(preprocessor.getLibraryInfo());

        visitResult = visitor.visit(tree);
        library = builder.getLibrary();
        compiledLibrary = builder.getCompiledLibrary();
        retrieves = visitor.getRetrieves();
        exceptions.addAll(builder.getExceptions());
        errors.addAll(builder.getErrors());
        warnings.addAll(builder.getWarnings());
        messages.addAll(builder.getMessages());

        return library;
    }
}
