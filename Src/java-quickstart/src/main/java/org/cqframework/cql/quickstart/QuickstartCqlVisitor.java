package org.cqframework.cql.quickstart;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class QuickstartCqlVisitor extends cqlBaseVisitor {
    private String library;
    private String version;
    private HashSet<String> variables;
    private HashMap<String, ValueSet> valuesets;
    private HashSet<Retrieve> retrieves;

    public QuickstartCqlVisitor() {
        library = null;
        version = null;
        variables = new HashSet<>();
        valuesets = new HashMap<>();
        retrieves = new HashSet<>();
    }

    public String getLibrary() {
        return library;
    }

    public String getVersion() {
        return version;
    }

    public Collection<String> getVariables() {
        return Collections.unmodifiableSet(variables);
    }

    public Map<String, ValueSet> getValuesets() {
        return Collections.unmodifiableMap(valuesets);
    }

    public Collection<Retrieve> getRetrieves() {
        return Collections.unmodifiableSet(retrieves);
    }

    @Override
    public Object visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        library = ctx.IDENTIFIER().getText();
        version = unquote(ctx.STRING());

        return library;
    }

    @Override
    public Object visitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        variables.add(ctx.IDENTIFIER().getText());

        // TODO: parse the right-hand side
        return super.visitLetStatement(ctx);
    }

    @Override
    public ValueSet visitValueset(@NotNull cqlParser.ValuesetContext ctx) {
        String valuesetHandle = unquote(ctx.VALUESET());
        // TODO: support valuesetPathIdentifier

        ValueSet vs = valuesets.get(valuesetHandle);
        if (vs == null) {
            throw new IllegalArgumentException("Reference to undefined valueset: " + valuesetHandle);
        }

        return vs;
    }

    @Override
    public Object visitValuesetDefinition(@NotNull cqlParser.ValuesetDefinitionContext ctx) {
        String handle = unquote(ctx.VALUESET());
        ValueSet valueset = (ValueSet) visit(ctx.expression());

        valuesets.put(handle, valueset);

        return valueset;
    }

    @Override
    public Object visitMethodExpressionTerm(@NotNull cqlParser.MethodExpressionTermContext ctx) {
        Object rtn;
        // TODO: Support more than just the ValueSet constructor
        switch (ctx.expressionTerm().getText()) {
            case "ValueSet":
                rtn = new ValueSet((String) visit(ctx.expression(0)));
                break;
            default:
                rtn = null;
        }
        return rtn;
    }

    @Override
    public Object visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        Retrieve.Existence existence = Retrieve.Existence.valueOf(nullsafeText(ctx.occurrence(), "Occurrence"));
        String topic = nullsafeText(ctx.topic());
        String modality = nullsafeText(ctx.modality());
        ValueSet valueset = (ValueSet) visit(ctx.valueset());
        // TODO: support "during" filter

        Retrieve retrieve = new Retrieve(existence, topic, modality, valueset);
        retrieves.add(retrieve);

        return retrieve;
    }

    @Override
    public String visitStringLiteral(@NotNull cqlParser.StringLiteralContext ctx) {
        return unquote(ctx.STRING());
    }

    private String nullsafeText(ParserRuleContext ctx) {
        return nullsafeText(ctx, null);
    }

    private String nullsafeText(ParserRuleContext ctx, String defaultValue) {
        return ctx != null ? ctx.getText() : defaultValue;
    }

    private String unquote(TerminalNode node) {
        if (node == null) return null;

        String text = node.getText();
        int tokenType = node.getSymbol().getType();
        if (cqlLexer.STRING == tokenType || cqlLexer.VALUESET == tokenType) {
            // chop off leading and trailing ' or "
            text = text.substring(1, text.length() - 1);
        }

        return text;
    }

    public static void main(String[] args) throws IOException {
        String inputFile = null;
        if (args.length > 0) inputFile = args[0];
        InputStream is = System.in;
        if (inputFile != null) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.logic();

        QuickstartCqlVisitor visitor = new QuickstartCqlVisitor();
        visitor.visit(tree);

        System.out.println("library: " + visitor.getLibrary());
        System.out.println("version: " + visitor.getVersion());
        System.out.println("\nDefined Variables:");
        for (String v : visitor.getVariables()) {
            System.out.println("  " + v);
        }
        System.out.println("\nDefined ValueSets:");
        for (Map.Entry e : visitor.getValuesets().entrySet()) {
            System.out.println("  " + e.getKey() + " --> " + e.getValue());
        }
        System.out.println("\nDefined Retrieves:");
        for (Retrieve r : visitor.getRetrieves()) {
            System.out.println("  " + r);
        }
    }
}
