package org.cqframework.cql.cql2elm.preprocessor;

import java.util.*;

public class LibraryInfo {
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
    private final Map<String, FunctionDefinitionInfo> functionDefinitions; // TODO: Overloads...

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
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LibraryInfo withLibraryName(String value) {
        setLibraryName(value);
        return this;
    }

    public LibraryInfo withVersion(String value) {
        setVersion(value);
        return this;
    }

    public void addUsingDefinition(UsingDefinitionInfo usingDefinition) {
        // First using definition encountered is "preferred", meaning it will resolve as the default model info
        if (preferredUsingDefinition == null) {
            preferredUsingDefinition = usingDefinition;
        }
        usingDefinitions.put(usingDefinition.getName(), usingDefinition);
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
    }

    public CodesystemDefinitionInfo resolveCodesystemReference(String identifier) {
        return codesystemDefinitions.get(identifier);
    }

    public void addValuesetDefinition(ValuesetDefinitionInfo valuesetDefinition) {
        valuesetDefinitions.put(valuesetDefinition.getName(), valuesetDefinition);
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
    }

    public CodeDefinitionInfo resolveCodeReference(String identifier) {
        return codeDefinitions.get(identifier);
    }

    public void addConceptDefinition(ConceptDefinitionInfo conceptDefinition) {
        conceptDefinitions.put(conceptDefinition.getName(), conceptDefinition);
    }

    public ConceptDefinitionInfo resolveConceptReference(String identifier) {
        return conceptDefinitions.get(identifier);
    }

    public void addExpressionDefinition(ExpressionDefinitionInfo letStatement) {
        expressionDefinitions.put(letStatement.getName(), letStatement);
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
        functionDefinitions.put(functionDefinition.getName(), functionDefinition);
    }

    public FunctionDefinitionInfo resolveFunctionReference(String identifier) {
        return functionDefinitions.get(identifier);
    }

    public String resolveFunctionName(String identifier) {
        FunctionDefinitionInfo functionDefinition = resolveFunctionReference(identifier);
        if (functionDefinition != null) {
            return functionDefinition.getName();
        }

        return null;
    }
}
