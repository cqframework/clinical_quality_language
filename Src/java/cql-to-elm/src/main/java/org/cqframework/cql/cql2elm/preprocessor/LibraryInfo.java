package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.ResultWithPossibleError;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.elm.r1.OperandDef;

import java.util.*;
import java.util.stream.Collectors;

public class LibraryInfo extends BaseInfo {
    private String namespaceName;
    private String libraryName;
    private String version;

    private UsingDefinitionInfo preferredUsingDefinition;
    private final Map<String, UsingDefinitionInfo> usingDefinitions;
    private final Map<String, IncludeDefinitionInfo> includeDefinitions;
    private final Map<String, CodesystemDefinitionInfo> codesystemDefinitions;
    private final Map<String, ValuesetDefinitionInfo> valuesetDefinitions;
    private final Map<String, CodeDefinitionInfo> codeDefinitions;
    private final Map<String, ConceptDefinitionInfo> conceptDefinitions;
    private final Map<String, ParameterDefinitionInfo> parameterDefinitions;
    private final Map<String, ExpressionDefinitionInfo> expressionDefinitions;
    private final Map<String, List<FunctionDefinitionInfo>> functionDefinitions;
    private final List<ContextDefinitionInfo> contextDefinitions;
    private final Map<Interval, BaseInfo> definitions;

    public LibraryInfo() {
        usingDefinitions = new LinkedHashMap<>();
        includeDefinitions = new LinkedHashMap<>();
        codesystemDefinitions = new LinkedHashMap<>();
        valuesetDefinitions = new LinkedHashMap<>();
        codeDefinitions = new LinkedHashMap<>();
        conceptDefinitions = new LinkedHashMap<>();
        parameterDefinitions = new LinkedHashMap<>();
        expressionDefinitions = new LinkedHashMap<>();
        functionDefinitions = new LinkedHashMap<>();
        contextDefinitions = new ArrayList<>();
        definitions = new HashMap<>();
    }

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public LibraryInfo withLibraryName(String value) {
        setLibraryName(value);
        return this;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LibraryInfo withVersion(String value) {
        setVersion(value);
        return this;
    }

    private void addDefinition(BaseInfo definition) {
        if (definition != null && definition.getDefinition() != null) {
            Interval sourceInterval = definition.getDefinition().getSourceInterval();
            if (sourceInterval != null) {
                definitions.put(sourceInterval, definition);
            }
        }
    }

    @Override
    public cqlParser.LibraryDefinitionContext getDefinition() {
        return (cqlParser.LibraryDefinitionContext)super.getDefinition();
    }

    public void setDefinition(cqlParser.LibraryDefinitionContext value) {
        super.setDefinition(value);
        addDefinition(this);
    }

    public LibraryInfo withDefinition(cqlParser.LibraryDefinitionContext value) {
        setDefinition(value);
        return this;
    }

    public void addUsingDefinition(UsingDefinitionInfo usingDefinition) {
        // First using definition encountered is "preferred", meaning it will resolve as the default model info
        if (preferredUsingDefinition == null) {
            preferredUsingDefinition = usingDefinition;
        }
        usingDefinitions.put(usingDefinition.getName(), usingDefinition);
        addDefinition(usingDefinition);
    }

    public UsingDefinitionInfo resolveModelReference(String identifier) {
        return usingDefinitions.get(identifier);
    }

    public UsingDefinitionInfo getDefaultUsingDefinition() {
        return preferredUsingDefinition;
    }

    public String getDefaultModelName() {
        UsingDefinitionInfo usingDefinitionInfo = getDefaultUsingDefinition();
        if (usingDefinitionInfo == null) {
            throw new IllegalArgumentException("Could not determine a default model because no usings have been defined.");
        }

        return usingDefinitionInfo.getName();
    }

    public void addIncludeDefinition(IncludeDefinitionInfo includeDefinition) {
        includeDefinitions.put(includeDefinition.getLocalName(), includeDefinition);
        addDefinition(includeDefinition);
    }

    public IncludeDefinitionInfo resolveLibraryReference(String identifier) {
        return includeDefinitions.get(identifier);
    }

    public String resolveLibraryName(String identifier) {
        IncludeDefinitionInfo includeDefinition = resolveLibraryReference(identifier);
        if (includeDefinition != null) {
            return includeDefinition.getLocalName();
        }

        return null;
    }

    public void addParameterDefinition(ParameterDefinitionInfo parameterDefinition) {
        parameterDefinitions.put(parameterDefinition.getName(), parameterDefinition);
        addDefinition(parameterDefinition);
    }

    public ParameterDefinitionInfo resolveParameterReference(String identifier) {
        return parameterDefinitions.get(identifier);
    }

    public String resolveParameterName(String identifier) {
        ParameterDefinitionInfo parameterDefinition = resolveParameterReference(identifier);
        if (parameterDefinition != null) {
            return parameterDefinition.getName();
        }

        return null;
    }

    public void addCodesystemDefinition(CodesystemDefinitionInfo codesystemDefinition) {
        codesystemDefinitions.put(codesystemDefinition.getName(), codesystemDefinition);
        addDefinition(codesystemDefinition);
    }

    public CodesystemDefinitionInfo resolveCodesystemReference(String identifier) {
        return codesystemDefinitions.get(identifier);
    }

    public void addValuesetDefinition(ValuesetDefinitionInfo valuesetDefinition) {
        valuesetDefinitions.put(valuesetDefinition.getName(), valuesetDefinition);
        addDefinition(valuesetDefinition);
    }

    public ValuesetDefinitionInfo resolveValuesetReference(String identifier) {
        return valuesetDefinitions.get(identifier);
    }

    public String resolveValuesetName(String identifier) {
        ValuesetDefinitionInfo valuesetDefinition = resolveValuesetReference(identifier);
        if (valuesetDefinition != null) {
            return valuesetDefinition.getName();
        }

        return null;
    }

    public void addCodeDefinition(CodeDefinitionInfo codeDefinition) {
        codeDefinitions.put(codeDefinition.getName(), codeDefinition);
        addDefinition(codeDefinition);
    }

    public CodeDefinitionInfo resolveCodeReference(String identifier) {
        return codeDefinitions.get(identifier);
    }

    public void addConceptDefinition(ConceptDefinitionInfo conceptDefinition) {
        conceptDefinitions.put(conceptDefinition.getName(), conceptDefinition);
        addDefinition(conceptDefinition);
    }

    public ConceptDefinitionInfo resolveConceptReference(String identifier) {
        return conceptDefinitions.get(identifier);
    }

    public void addExpressionDefinition(ExpressionDefinitionInfo letStatement) {
        expressionDefinitions.put(letStatement.getName(), letStatement);
        addDefinition(letStatement);
    }

    public ExpressionDefinitionInfo resolveExpressionReference(String identifier) {
        return expressionDefinitions.get(identifier);
    }

    public String resolveExpressionName(String identifier) {
        ExpressionDefinitionInfo letStatement = resolveExpressionReference(identifier);
        if (letStatement != null) {
            return letStatement.getName();
        }

        return null;
    }

    public void addFunctionDefinition(FunctionDefinitionInfo functionDefinition) {
        List<FunctionDefinitionInfo> infos = functionDefinitions.get(functionDefinition.getName());
        if (infos == null) {
            infos = new ArrayList<FunctionDefinitionInfo>();
            functionDefinitions.put(functionDefinition.getName(), infos);
        }
        infos.add(functionDefinition);
        addDefinition(functionDefinition);
    }

    public Iterable<FunctionDefinitionInfo> resolveFunctionReference(String identifier) {
        return functionDefinitions.get(identifier);
    }

    public String resolveFunctionName(String identifier) {
        Iterable<FunctionDefinitionInfo> functionDefinitions = resolveFunctionReference(identifier);
        for (FunctionDefinitionInfo functionInfo : functionDefinitions) {
            return functionInfo.getName();
        }

        return null;
    }

    public void addContextDefinition(ContextDefinitionInfo contextDefinition) {
        contextDefinitions.add(contextDefinition);
        addDefinition(contextDefinition);
    }

    public ContextDefinitionInfo resolveContext(cqlParser.ContextDefinitionContext ctx) {
        for (ContextDefinitionInfo cd : contextDefinitions) {
            if (ctx.getSourceInterval().equals(cd.getDefinition().getSourceInterval())) {
                return cd;
            }
        }

        return null;
    }

    public BaseInfo resolveDefinition(ParseTree pt) {
        return definitions.get(pt.getSourceInterval());
    }

    private static boolean isFunctionDefInfoAlreadyPresent(ResultWithPossibleError<FunctionDefinitionInfo> existingFunctionDefInfo, ResultWithPossibleError<FunctionDefinitionInfo> functionDefinition) {
        // equals/hashCode only goes so far because we don't control the entire class hierarchy
        return matchesFunctionDefInfos(existingFunctionDefInfo, functionDefinition);
    }

    private static boolean matchesFunctionDefInfos(ResultWithPossibleError<FunctionDefinitionInfo> existingInfo, ResultWithPossibleError<FunctionDefinitionInfo> newInfo) {
        if (existingInfo == null) {
            return false;
        }

        if (existingInfo.hasError() || newInfo.hasError()) {
            return existingInfo.hasError() && newInfo.hasError();
        }

        final List<OperandDef> existingOperands = existingInfo.getUnderlyingResultIfExists().getPreCompileOutput().getFunctionDef().getOperand();
        final List<OperandDef> newOperands = newInfo.getUnderlyingResultIfExists().getPreCompileOutput().getFunctionDef().getOperand();

        if (existingOperands.size() != newOperands.size()) {
            return false;
        }

        for (int index = 0; index < existingOperands.size(); index++) {
            final OperandDef existingOperand = existingOperands.get(index);
            final OperandDef newOperand = newOperands.get(index);

            if (!matchesOperands(existingOperand, newOperand)) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchesOperands(OperandDef existingOperand, OperandDef newOperand) {
        return existingOperand.getResultType().equals(newOperand.getResultType());
    }
}
