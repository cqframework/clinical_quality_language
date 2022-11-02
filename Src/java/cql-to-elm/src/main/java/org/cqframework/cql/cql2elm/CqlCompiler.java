package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.cqframework.cql.elm.analyzing.AnalysisVisitor;
import org.cqframework.cql.elm.analyzing.DeprecateAnalyzer;
import org.cqframework.cql.elm.analyzing.VisitorContext;
import org.cqframework.cql.elm.tags.TagSetVisitor;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.fhir.ucum.UcumService;
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
    private UcumService ucumService = null;

    public CqlCompiler(ModelManager modelManager, LibraryManager libraryManager) {
        this(null, null, modelManager, libraryManager, null);
    }

    public CqlCompiler(NamespaceInfo namespaceInfo, ModelManager modelManager, LibraryManager libraryManager) {
        this(namespaceInfo, null, modelManager, libraryManager, null);
    }

    public CqlCompiler(NamespaceInfo namespaceInfo, ModelManager modelManager,
                        LibraryManager libraryManager, UcumService ucumService) {
        this(namespaceInfo, null, modelManager, libraryManager, ucumService);
    }

    public CqlCompiler(NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, ModelManager modelManager,
                        LibraryManager libraryManager, UcumService ucumService) {
        this.sourceInfo = sourceInfo;
        this.namespaceInfo = namespaceInfo;
        this.modelManager = modelManager;
        this.libraryManager = libraryManager;
        this.ucumService = ucumService;

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

        if (libraryManager.getUcumService() == null) {
            libraryManager.setUcumService(this.ucumService);
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

    public Library run(String cqlText,
                       CqlTranslatorOptions.Options... options) throws IOException {
        return run(CharStreams.fromString(cqlText), new CqlTranslatorOptions(options));
    }

    public Library run(String cqlText,
                       CqlCompilerException.ErrorSeverity errorLevel,
                       CqlTranslatorOptions.Options... options) throws IOException {
        return run(CharStreams.fromString(cqlText), new CqlTranslatorOptions(errorLevel, options));
    }

    public Library run(String cqlText,
                       CqlCompilerException.ErrorSeverity errorLevel,
                       LibraryBuilder.SignatureLevel signatureLevel,
                       CqlTranslatorOptions.Options... options) throws IOException {
        return run(CharStreams.fromString(cqlText), new CqlTranslatorOptions(errorLevel, signatureLevel, options));
    }

    public Library run(InputStream is,
                       CqlTranslatorOptions.Options... options) throws IOException {
        return run(CharStreams.fromStream(is), new CqlTranslatorOptions(options));
    }

    public Library run(InputStream is,
                       CqlCompilerException.ErrorSeverity errorLevel,
                       CqlTranslatorOptions.Options... options) throws IOException {
        return run(CharStreams.fromStream(is), new CqlTranslatorOptions(errorLevel, options));
    }

    public Library run(InputStream is,
                       CqlCompilerException.ErrorSeverity errorLevel,
                       LibraryBuilder.SignatureLevel signatureLevel,
                       CqlTranslatorOptions.Options... options) throws IOException {
        return run(CharStreams.fromStream(is), new CqlTranslatorOptions(errorLevel, signatureLevel, options));
    }

    public Library run(CharStream is,
                       CqlTranslatorOptions.Options... options) {
        return run(is, new CqlTranslatorOptions(options));
    }

    public Library run(CharStream is,
                       CqlCompilerException.ErrorSeverity errorLevel,
                       CqlTranslatorOptions.Options... options) {
        return run(is, new CqlTranslatorOptions(errorLevel, LibraryBuilder.SignatureLevel.None, options));
    }

    public Library run(CharStream is,
                       CqlCompilerException.ErrorSeverity errorLevel,
                       LibraryBuilder.SignatureLevel signatureLevel,
                       CqlTranslatorOptions.Options... options) {
        return run(is, new CqlTranslatorOptions(errorLevel, signatureLevel, options));
    }

    public Library run(InputStream is, CqlTranslatorOptions options) throws IOException {
        return run(CharStreams.fromStream(is), options);
    }

    public Library run(CharStream is, CqlTranslatorOptions options) {
        exceptions = new ArrayList<>();
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        messages = new ArrayList<>();
        LibraryBuilder builder = new LibraryBuilder(namespaceInfo, modelManager, libraryManager, ucumService);
        builder.setTranslatorOptions(options);
        Cql2ElmVisitor visitor = new Cql2ElmVisitor(builder);
        builder.setVisitor(visitor);
        visitor.setTranslatorOptions(options);

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

        VisitorContext visitorContext = new VisitorContext();
        TagSetVisitor tagSetVisitor = new TagSetVisitor();
        tagSetVisitor.visitLibrary(library, visitorContext);



        System.out.println("Tag set>>" + visitorContext.getTagSet().delegate());

        visitorContext.getTagSet().select(tagInfo -> tagInfo.name().equals("deprecated")).forEach(item -> System.out.println(item.name()+"|"+ item.expressionName()));

        AnalysisVisitor analysisVisitor = new AnalysisVisitor();
        analysisVisitor.registerAnalyser(new DeprecateAnalyzer());
        analysisVisitor.visitLibrary(library, visitorContext);


        // Run ELM Analyzers
        // visit the ELM tree, and issue warnings or errors based on the tree structure
        // @deprecated
        // @no-warning
        // ELM analysis phase
        // If "elmAnalyzersEnabled" <- New CQL compiler flag
        //  visit ELM and generate the set of tags <- new visitor, partially done on JP' branch
        //  load all analyzers from the classpath <- new ServiceLoader for runtime analyzers
        //  set up error listener <- New / extended error listener that understands "@no-warning", this is the same as the JDKs support for @SuppressWarnings
        //      supporting "@no-warning" means if a warning is issued by an analyzer for an element that is tagged with "@no-warning",
        //      don't report the warning
        //  run all the ELM analyzers <- visit the ELM graph and run analyzers on each node. We need a Visitor that can do this
        //  report all the warnings/errors the analyzers produce.

        // ELM analyzer use cases:
        // * Find deprecations
        // * Suggest fixes for sorting on FHIR resources - sort by Observation.status -> sort by Observation.status.value
        // * Warning for unoptimized retrieves <- Some codepath uses a model attribute that doesn't have a search parameter

        // definitionMatcher(ExpressionDef expressionDef, VisitorContext context)
        //    if (expressionDef.name is greater than 50 charcters long) {
        //        context.warn(expressionDef, "ExpressionName is too long");
        //    }

        //    if (context.parents.contains(Retrieve)) {
        //       context.warn(expressionDef, "ExpressionDef is inside of a retreive. What the heck?")
        //    }
        // }
        // }

        // referenceMatcher(ExpressionRef expressionRef, VisitorContext context)
        //    if (context.tags.forElement(ExpressionRef).contains("deprecated"))
        //      context.warn(ExpressionRef, "expressionRef.name is depracated")
        //
        // }

        /// VistorContext
        ///  {
        ///    stack<ParentNodes> parents
        //     ErrorListener errorListener <- this knows how to suppress warnings for ELM elements
        //     tagSet tags <- this is the list of tags for the whole ELM graph
        //}

        // AnalysisVisitor {
        //    ELMAnalyzers analyzers
        //    VisitorContext context
        //     beforeVisit (context.parents.add(this))
        //     afterVisit (context.parents.pop())
        //     visit(ExpressionDef e ) {
                  // foreach Analyzer : analyzers
                ///   analyzer.definitionMatcher(e, context);
        // }
        //}
        // }


        retrieves = visitor.getRetrieves();
        exceptions.addAll(builder.getExceptions());
        errors.addAll(builder.getErrors());
        warnings.addAll(builder.getWarnings());
        messages.addAll(builder.getMessages());

        return library;
    }
}
