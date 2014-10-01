package org.cqframework.cql.poc.translator.preprocessor;

import org.cqframework.cql.poc.translator.model.*;

import java.util.*;

public class LibraryInfo {
    private String libraryName;
    private String version;

    private final Map<String, UsingDefinitionInfo> usingDefinitions;
    private final Map<String, IncludeDefinitionInfo> includeDefinitions;
    private final Map<String, ValuesetDefinitionInfo> valuesetDefinitions;
    private final Map<String, ParameterDefinitionInfo> parameterDefinitions;
    private final Map<String, LetStatementInfo> letStatements;
    private final Map<String, FunctionDefinitionInfo> functionDefinitions; // TODO: Overloads...
    private final Map<String, RetrieveDefinitionInfo> retrieveDefinitions;

    public LibraryInfo() {
        usingDefinitions = new LinkedHashMap<>();
        includeDefinitions = new LinkedHashMap<>();
        valuesetDefinitions = new LinkedHashMap<>();
        parameterDefinitions = new LinkedHashMap<>();
        letStatements = new LinkedHashMap<>();
        functionDefinitions = new LinkedHashMap<>();
        retrieveDefinitions = new LinkedHashMap<>();
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
        usingDefinitions.put(usingDefinition.getName(), usingDefinition);
    }

    public UsingDefinitionInfo resolveModelReference(String identifier) {
        return usingDefinitions.get(identifier);
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

    public void addLetStatement(LetStatementInfo letStatement) {
        letStatements.put(letStatement.getName(), letStatement);
    }

    public LetStatementInfo resolveExpressionReference(String identifier) {
        return letStatements.get(identifier);
    }

    public String resolveExpressionName(String identifier) {
        LetStatementInfo letStatement = resolveExpressionReference(identifier);
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

    public void addRetrieveDefinition(RetrieveDefinitionInfo retrieveDefinition) {
        retrieveDefinitions.put(retrieveDefinition.getName(), retrieveDefinition);
    }

    public RetrieveDefinitionInfo resolveRetrieveReference(String identifier) {
        return retrieveDefinitions.get(identifier);
    }
}
