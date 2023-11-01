package org.cqframework.cql.elm.requirements;

import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.model.LibraryRef;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.cql.model.ClassType;
import org.hl7.cql.model.DataType;
import org.hl7.cql.model.NamedType;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm.r1.*;

import javax.xml.namespace.QName;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

public class ElmRequirementsContext {

    public ElmRequirementsContext(LibraryManager libraryManager, CqlCompilerOptions options, ElmRequirementsVisitor visitor, Map<String, Object> parameters, ZonedDateTime evaluationDateTime) {
        if (libraryManager == null) {
            throw new IllegalArgumentException("Library Manager required");
        }
        this.libraryManager = libraryManager;
        this.options = options;
        this.typeResolver = new TypeResolver(libraryManager);
        this.typeBuilder = new TypeBuilder(this.libraryManager.getModelManager());

        if (visitor == null) {
            throw new IllegalArgumentException("visitor required");
        }
        this.visitor = visitor;
        this.requirements = new ElmRequirements(new VersionedIdentifier().withId("result"), new Null());
        this.parameters = parameters;
        this.evaluationDateTime = evaluationDateTime;
    }

    private CqlCompilerOptions options;
    public CqlCompilerOptions getOptions() {
        return options;
    }
    public void setOptions(CqlCompilerOptions options) {
        this.options = options;
    }

    private LibraryManager libraryManager;
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    private Map<String, Object> parameters;
    public Map<String, Object> getParameters() {
        return parameters;
    }

    private ZonedDateTime evaluationDateTime;
    public ZonedDateTime getEvaluationDateTime() {
        return evaluationDateTime;
    }

    private TypeResolver typeResolver;
    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    private TypeBuilder typeBuilder;

    // Arbitrary starting point for generated local Ids.
    // If the input ELM does not have local Ids, some of the optimization
    // outputs require references to be established between ELM nodes,
    // so local ids are generated if not present in those cases.
    private int nextLocalId = 10000;
    public String generateLocalId() {
        nextLocalId++;
        return String.format("G%d", nextLocalId);
    }

    private Stack<ElmExpressionDefContext> expressionDefStack = new Stack<ElmExpressionDefContext>();
    public void enterExpressionDef(ExpressionDef expressionDef) {
        if (expressionDef == null) {
            throw new IllegalArgumentException("expressionDef required");
        }
        ElmExpressionDefContext expressionDefContext = new ElmExpressionDefContext(getCurrentLibraryIdentifier(), expressionDef);
        expressionDefStack.push(expressionDefContext);
    }
    public void exitExpressionDef(ElmRequirement inferredRequirements) {
        if (expressionDefStack.empty()) {
            throw new IllegalArgumentException("Not in an expressionDef context");
        }
        ElmExpressionDefContext expressionDefContext = expressionDefStack.pop();
        ExpressionDef ed = expressionDefContext.getExpressionDef();
        reportExpressionDef(ed);
        this.reportedRequirements.put(ed, expressionDefContext.getReportedRequirements());
        this.inferredRequirements.put(ed, inferredRequirements);
    }
    public ElmExpressionDefContext getCurrentExpressionDefContext() {
        if (expressionDefStack.empty()) {
            throw new IllegalArgumentException("Expression definition is not in progress");
        }
        return expressionDefStack.peek();
    }
    public boolean inExpressionDefContext() {
        return !expressionDefStack.empty();
    }

    private List<ElmPertinenceContext> pertinenceContextStack = new ArrayList<ElmPertinenceContext>();

    public boolean enterPertinenceContext(ExpressionDef expressionDef) {
        ElmPertinenceContext pertinenceContext = new ElmPertinenceContext(expressionDef);
        if (pertinenceContext.checkPertinenceTag()) {
            pertinenceContextStack.add(0, pertinenceContext);
            return true;
        }
        return false;
    }

    public ElmPertinenceContext peekPertinenceContext() {
        ElmPertinenceContext context = null;
        for (ElmPertinenceContext c : pertinenceContextStack) {
            context = c;
        }
        return context;
    }

    public void exitPertinenceContext() {
        if (pertinenceContextStack.size() > 0) {
            pertinenceContextStack.remove(0);
        }
    }


    /*
    Reported requirements are collected during the traversal, reported at query boundaries, or at retrieves
    that are outside of a query scope.
    These are collected by the ElmExpressionDefContext as expression defs are visited, and reported to the context after
    the visit is complete
     */
    private Map<ExpressionDef, ElmRequirements> reportedRequirements = new LinkedHashMap<ExpressionDef, ElmRequirements>();
    public Iterable<ElmRequirements> getReportedRequirements() {
        return reportedRequirements.values();
    }
    public ElmRequirements getReportedRequirements(ExpressionDef ed) {
        return reportedRequirements.get(ed);
    }

    /*
    Inferred requirements are the result of the traversal, the computed/inferred data requirements for an expression.
    These are calculated by the visit and reported to the context here after the visit is complete
     */
    private Map<ExpressionDef, ElmRequirement> inferredRequirements = new LinkedHashMap<ExpressionDef, ElmRequirement>();
    public Iterable<ElmRequirement> getInferredRequirements() {
        return inferredRequirements.values();
    }
    public ElmRequirement getInferredRequirements(ExpressionDef ed) {
        return inferredRequirements.get(ed);
    }

    private Stack<VersionedIdentifier> libraryStack = new Stack<VersionedIdentifier>();
    public void enterLibrary(VersionedIdentifier libraryIdentifier) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("Library Identifier must be provided");
        }
        libraryStack.push(libraryIdentifier);
    }
    public void exitLibrary() {
        libraryStack.pop();
    }
    public VersionedIdentifier getCurrentLibraryIdentifier() {
        if (libraryStack.empty()) {
            throw new IllegalArgumentException("Not in a library context");
        }

        return libraryStack.peek();
    }

    /*
    Prepares a library visit if necessary (i.e. localLibraryName is not null) and returns the associated translated
    library. If there is no localLibraryName, returns the current library.
     */
    private CompiledLibrary prepareLibraryVisit(VersionedIdentifier libraryIdentifier, String localLibraryName) {
        CompiledLibrary targetLibrary = resolveLibrary(libraryIdentifier);
        if (localLibraryName != null) {
            IncludeDef includeDef = targetLibrary.resolveIncludeRef(localLibraryName);
            if (!visited.contains(includeDef)) {
                visitor.visitElement(includeDef, this);
            }
            targetLibrary = resolveLibraryFromIncludeDef(includeDef);
            enterLibrary(targetLibrary.getIdentifier());
        }
        return targetLibrary;
    }

    private void unprepareLibraryVisit(String localLibraryName) {
        if (localLibraryName != null) {
            exitLibrary();
        }
    }

    public void enterQueryContext(Query query) {
        getCurrentExpressionDefContext().enterQueryContext(query);
    }
    public ElmQueryContext exitQueryContext() {
        return getCurrentExpressionDefContext().exitQueryContext();
    }
    public ElmQueryContext getCurrentQueryContext() {
        return getCurrentExpressionDefContext().getCurrentQueryContext();
    }
    public boolean inQueryContext() {
        return getCurrentExpressionDefContext().inQueryContext();
    }

    public ElmQueryAliasContext resolveAlias(String aliasName) {
        return getCurrentExpressionDefContext().resolveAlias(aliasName);
    }

    public ElmQueryLetContext resolveLet(String letName) {
        return getCurrentExpressionDefContext().resolveLet(letName);
    }

    private Set<Element> visited = new LinkedHashSet<Element>();

    private ElmRequirements requirements;
    public ElmRequirements getRequirements() {
        return requirements;
    }

    private ElmRequirementsVisitor visitor;
    public ElmRequirementsVisitor getVisitor() {
        return visitor;
    }

    private boolean isDefinition(Element elm) {
        return elm instanceof Library
                || elm instanceof UsingDef
                || elm instanceof IncludeDef
                || elm instanceof CodeSystemDef
                || elm instanceof ValueSetDef
                || elm instanceof CodeDef
                || elm instanceof ConceptDef
                || elm instanceof ParameterDef
                || elm instanceof ContextDef
                || elm instanceof ExpressionDef;
    }

    private void reportRequirement(ElmRequirement requirement) {
        if (isDefinition(requirement.getElement())) {
            visited.add(requirement.getElement());
            requirements.reportRequirement(requirement);
        }
        else {
            if (expressionDefStack.empty()) {
                requirements.reportRequirement(requirement);
            }
            else {
                expressionDefStack.peek().reportRequirement(requirement);
            }
        }
    }

    private void reportRequirement(Element element) {
        reportRequirement(new ElmRequirement(getCurrentLibraryIdentifier(), element));
    }

    public void reportUsingDef(UsingDef usingDef) {
        reportRequirement(usingDef);
    }

    public void reportIncludeDef(IncludeDef includeDef) {
        reportRequirement(includeDef);
    }

    public void reportContextDef(ContextDef contextDef) {
        reportRequirement(contextDef);
    }

    public void reportCodeDef(CodeDef codeDef) {
        reportRequirement(codeDef);
    }

    public void reportCodeSystemDef(CodeSystemDef codeSystemDef) {
        reportRequirement(codeSystemDef);
    }

    public void reportConceptDef(ConceptDef conceptDef) {
        reportRequirement(conceptDef);
    }

    public void reportParameterDef(ParameterDef parameterDef) {
        reportRequirement(parameterDef);
    }

    public void reportValueSetDef(ValueSetDef valueSetDef) {
        reportRequirement(valueSetDef);
    }

    public void reportExpressionDef(ExpressionDef expressionDef) {
        if (!(expressionDef instanceof FunctionDef)) {
            reportRequirement(expressionDef);
        }
    }

    public void reportFunctionDef(FunctionDef functionDef) {
        reportRequirement(functionDef);
    }

    public void reportCodeRef(CodeRef codeRef) {
        CompiledLibrary targetLibrary = prepareLibraryVisit(getCurrentLibraryIdentifier(), codeRef.getLibraryName());
        try {
            CodeDef cd = targetLibrary.resolveCodeRef(codeRef.getName());
            if (!visited.contains(cd)) {
                visitor.visitElement(cd, this);
            }
        }
        finally {
            unprepareLibraryVisit(codeRef.getLibraryName());
        }
    }

    public void reportCodeSystemRef(CodeSystemRef codeSystemRef) {
        CompiledLibrary targetLibrary = prepareLibraryVisit(getCurrentLibraryIdentifier(), codeSystemRef.getLibraryName());
        try {
            CodeSystemDef csd = targetLibrary.resolveCodeSystemRef(codeSystemRef.getName());
            if (!visited.contains(csd)) {
                visitor.visitElement(csd, this);
            }
        }
        finally {
            unprepareLibraryVisit(codeSystemRef.getLibraryName());
        }
    }

    public void reportConceptRef(ConceptRef conceptRef) {
        CompiledLibrary targetLibrary = prepareLibraryVisit(getCurrentLibraryIdentifier(), conceptRef.getLibraryName());
        try {
            ConceptDef cd = targetLibrary.resolveConceptRef(conceptRef.getName());
            if (!visited.contains(cd)) {
                visitor.visitElement(cd, this);
            }
        }
        finally {
            unprepareLibraryVisit(conceptRef.getLibraryName());
        }
    }

    public void reportParameterRef(ParameterRef parameterRef) {
        CompiledLibrary targetLibrary = prepareLibraryVisit(getCurrentLibraryIdentifier(), parameterRef.getLibraryName());
        try {
            ParameterDef pd = targetLibrary.resolveParameterRef(parameterRef.getName());
            if (!visited.contains(pd)) {
                visitor.visitElement(pd, this);
            }
        }
        finally {
            unprepareLibraryVisit(parameterRef.getLibraryName());
        }
    }

    public void reportValueSetRef(ValueSetRef valueSetRef) {
        CompiledLibrary targetLibrary = prepareLibraryVisit(getCurrentLibraryIdentifier(), valueSetRef.getLibraryName());
        try {
            ValueSetDef vsd = targetLibrary.resolveValueSetRef(valueSetRef.getName());
            if (!visited.contains(vsd)) {
                visitor.visitElement(vsd, this);
            }
        }
        finally {
            unprepareLibraryVisit(valueSetRef.getLibraryName());
        }
    }

    public ElmRequirement reportExpressionRef(ExpressionRef expressionRef) {
        CompiledLibrary targetLibrary = prepareLibraryVisit(getCurrentLibraryIdentifier(), expressionRef.getLibraryName());
        try {
            ExpressionDef ed = targetLibrary.resolveExpressionRef(expressionRef.getName());
            if (!visited.contains(ed)) {
                visitor.visitElement(ed, this);
            }
            ElmRequirement inferredRequirements = getInferredRequirements(ed);

            // Report data requirements for this expression def to the current context (that are not already part of the inferred requirements
            ElmRequirements reportedRequirements = getReportedRequirements(ed);
            if (reportedRequirements != null) {
                reportRequirements(reportedRequirements, inferredRequirements);
            }
            // Return the inferred requirements for the expression def
            return inferredRequirements;
        }
        finally {
            unprepareLibraryVisit(expressionRef.getLibraryName());
        }
    }

    public void reportFunctionRef(FunctionRef functionRef) {
        CompiledLibrary targetLibrary = prepareLibraryVisit(getCurrentLibraryIdentifier(), functionRef.getLibraryName());
        try {

            List<DataType> signature;
            signature = new ArrayList<DataType>();
            for (TypeSpecifier ts : functionRef.getSignature()) {
                signature.add(typeResolver.resolveTypeSpecifier(ts));
            }
            // Signature sizes will only be different in the case that the signature is not present in the ELM, so needs to be constructed
            if (signature.size() != functionRef.getOperand().size()) {
                for (Expression e : functionRef.getOperand()) {
                    if (e.getResultType() != null) {
                        signature.add(e.getResultType());
                    }
                    else if (e.getResultTypeName() != null) {
                        signature.add(typeResolver.resolveTypeName(e.getResultTypeName()));
                    }
                    else if (e.getResultTypeSpecifier() != null) {
                        signature.add(typeResolver.resolveTypeSpecifier(e.getResultTypeSpecifier()));
                    }
                    else {
                        // Signature could not be constructed, fall back to reporting all function defs
                        signature = null;
                        break;
                    }
                }
            }

            Iterable<FunctionDef> fds = targetLibrary.resolveFunctionRef(functionRef.getName(), signature);
            for (FunctionDef fd : fds) {
                if (!visited.contains(fd)) {
                    visitor.visitElement(fd, this);
                }
            }
        }
        finally {
            unprepareLibraryVisit(functionRef.getLibraryName());
        }
    }

    public void reportRetrieve(Retrieve retrieve) {
        // Report the retrieve as an overall data requirement
        reportRequirement(retrieve);
        // Data Requirements analysis is done within the query processing
        /*
        ElmDataRequirement retrieveRequirement = new ElmDataRequirement(getCurrentLibraryIdentifier(), retrieve);
        if (!queryStack.empty()) {
            getCurrentQueryContext().reportRetrieve(retrieveRequirement);
        }
        else {
            reportRequirement(retrieveRequirement);
        }
        */
    }

    /*
    Report the requirements inferred from visit of an expression tree, typically an ExpressionDef
    Except do not report a requirement if it is present in the inferred requirements for the expression,
    or if it can be correlated with a data requirement in the current query context
    (The alternative is to calculate total requirements as part of the inference mechanism, but that
    complicates the inferencing calculations, as they would always have to be based on a collection
    of requirements, rather than the current focus of either a DataRequirement or a QueryRequirement)
     */
    public void reportRequirements(ElmRequirement requirement, ElmRequirement inferredRequirements) {
        if (requirement instanceof ElmRequirements) {
            for (ElmRequirement childRequirement : ((ElmRequirements)requirement).getRequirements()) {
                if (inferredRequirements == null || !inferredRequirements.hasRequirement(childRequirement)) {
                    reportRequirement(childRequirement);
                }
            }
        }
        else if (requirement instanceof ElmQueryRequirement) {
            ElmQueryRequirement queryRequirement = (ElmQueryRequirement)requirement;
            for (ElmDataRequirement dataRequirement : queryRequirement.getDataRequirements()) {
                if (inferredRequirements == null || !inferredRequirements.hasRequirement(dataRequirement)) {
                    reportRequirement(dataRequirement);
                }
            }
        }
        else if (requirement instanceof ElmOperatorRequirement) {
            ElmOperatorRequirement operatorRequirement = (ElmOperatorRequirement)requirement;
            for (ElmRequirement r : operatorRequirement.getRequirements()) {
                if (inferredRequirements == null || !inferredRequirements.hasRequirement(r)) {
                    reportRequirements(r, inferredRequirements);
                }
            }
        }
        else {
            reportRequirement(requirement);
        }
    }

    private QName getType(Expression expression) {
        if (expression != null) {
            if (expression.getResultTypeName() != null) {
                return expression.getResultTypeName();
            }
            else if (expression.getResultTypeSpecifier() instanceof NamedTypeSpecifier) {
                return ((NamedTypeSpecifier)expression.getResultTypeSpecifier()).getName();
            }
        }

        return null;
    }

    private QName getProfiledType(DataType type) {
        return typeResolver.dataTypeToProfileQName(type);
    }

    private Map<QName, ElmDataRequirement> unboundDataRequirements = new LinkedHashMap<QName, ElmDataRequirement>();

    private ElmDataRequirement getDataRequirementForTypeName(QName typeName, QName profiledTypeName) {
        DataType type = null;
        try {
            type = typeResolver.resolveTypeName(typeName);
        }
        catch (Exception e) {
            // ignore an exception resolving the type, just don't attempt to build an unbound requirement
            // We should only be building unbound requirements for retrievable types, so if we can't determine
            // retrievability, ignore the requirement
        }

        if (type != null && type instanceof ClassType && ((ClassType)type).isRetrievable()) {
            ElmDataRequirement requirement = unboundDataRequirements.get(profiledTypeName != null ? profiledTypeName : typeName);
            if (requirement == null) {
                Retrieve retrieve = new Retrieve();
                retrieve.setDataType(typeName);
                if (profiledTypeName != null && profiledTypeName.getNamespaceURI() != null && profiledTypeName.getLocalPart() != null) {
                    retrieve.setTemplateId(profiledTypeName.getNamespaceURI() + "/" + profiledTypeName.getLocalPart());
                }
                else if (typeName.getNamespaceURI() != null && typeName.getLocalPart() != null) {
                    retrieve.setTemplateId(typeName.getNamespaceURI() + "/" + typeName.getLocalPart());
                }
                requirement = new ElmDataRequirement(getCurrentLibraryIdentifier(), retrieve);
                unboundDataRequirements.put(typeName, requirement);
                reportRequirement(requirement);
            }

            return requirement;
        }

        return null;
    }

    public ElmPropertyRequirement reportProperty(Property property) {
        // if scope is specified, it's a reference to an alias in a current query context
        // if source is an AliasRef, it's a reference to an alias in a current query context
        // if source is a LetRef, it's a reference to a let in a current query context
        // if source is a Property, add the current property to a qualifier
        // Otherwise, report it as an unbound property reference to the type of source
        if (property.getScope() != null || property.getSource() instanceof AliasRef) {
            String aliasName = property.getScope() != null ? property.getScope() : ((AliasRef)property.getSource()).getName();
            ElmQueryAliasContext aliasContext = getCurrentQueryContext().resolveAlias(aliasName);
            boolean inCurrentScope = true;
            if (aliasContext == null) {
                // This is a reference to an alias in an outer scope
                aliasContext = resolveAlias(aliasName);
                inCurrentScope = false;
            }
            ElmPropertyRequirement propertyRequirement = new ElmPropertyRequirement(getCurrentLibraryIdentifier(),
                    property, aliasContext.getQuerySource(), inCurrentScope);

            aliasContext.reportProperty(propertyRequirement);
            return propertyRequirement;
        }

        if (property.getSource() instanceof QueryLetRef) {
            String letName = ((QueryLetRef)property.getSource()).getName();
            ElmQueryLetContext letContext = getCurrentQueryContext().resolveLet(letName);
            boolean inCurrentScope = true;
            if (letContext == null) {
                // This is a reference to a let definition in an outer scope
                letContext = resolveLet(letName);
                inCurrentScope = false;
            }
            ElmPropertyRequirement propertyRequirement = new ElmPropertyRequirement(getCurrentLibraryIdentifier(),
                    property, letContext.getLetClause(), inCurrentScope);

            letContext.reportProperty(propertyRequirement);
            return propertyRequirement;
        }

        if (property.getSource() instanceof Property) {
            Property sourceProperty = (Property)property.getSource();
            Property qualifiedProperty = new Property();
            qualifiedProperty.setSource(sourceProperty.getSource());
            qualifiedProperty.setScope(sourceProperty.getScope());
            qualifiedProperty.setResultType(property.getResultType());
            qualifiedProperty.setResultTypeName(property.getResultTypeName());
            qualifiedProperty.setResultTypeSpecifier(property.getResultTypeSpecifier());
            qualifiedProperty.setLocalId(sourceProperty.getLocalId());
            qualifiedProperty.setPath(sourceProperty.getPath() + "." + property.getPath());
            return reportProperty(qualifiedProperty);
        }
        else {
            QName typeName = getType(property.getSource());
            if (typeName != null) {
                ElmDataRequirement requirement = getDataRequirementForTypeName(typeName, getProfiledType(property.getSource().getResultType()));
                if (requirement != null) {
                    ElmPropertyRequirement propertyRequirement = new ElmPropertyRequirement(getCurrentLibraryIdentifier(),
                            property, property.getSource(), false);
                    requirement.reportProperty(propertyRequirement);
                    return propertyRequirement;
                }
            }
        }

        return null;
    }

    public Concept toConcept(ElmRequirement conceptDef) {
        return toConcept(conceptDef.getLibraryIdentifier(), (ConceptDef)conceptDef.getElement());
    }

    public org.hl7.elm.r1.Concept toConcept(VersionedIdentifier libraryIdentifier, ConceptDef conceptDef) {
        org.hl7.elm.r1.Concept concept = new org.hl7.elm.r1.Concept();
        concept.setDisplay(conceptDef.getDisplay());
        for (org.hl7.elm.r1.CodeRef codeRef : conceptDef.getCode()) {
            concept.getCode().add(toCode(resolveCodeRef(libraryIdentifier, codeRef)));
        }
        return concept;
    }

    public org.hl7.elm.r1.Code toCode(CodeDef codeDef) {
        return new org.hl7.elm.r1.Code().withCode(codeDef.getId()).withSystem(codeDef.getCodeSystem()).withDisplay(codeDef.getDisplay());
    }

    public CodeDef resolveCodeRef(ElmRequirement codeRef) {
        return resolveCodeRef(codeRef.getLibraryIdentifier(), (CodeRef)codeRef.getElement());
    }

    public org.hl7.elm.r1.CodeDef resolveCodeRef(VersionedIdentifier libraryIdentifier, CodeRef codeRef) {
        // If the reference is to another library, resolve to that library
        if (codeRef.getLibraryName() != null) {
            return resolveLibrary(libraryIdentifier, codeRef.getLibraryName()).resolveCodeRef(codeRef.getName());
        }

        return resolveLibrary(libraryIdentifier).resolveCodeRef(codeRef.getName());
    }

    public org.hl7.elm.r1.ConceptDef resolveConceptRef(ElmRequirement conceptRef) {
        return resolveConceptRef(conceptRef.getLibraryIdentifier(), (ConceptRef)conceptRef.getElement());
    }

    public org.hl7.elm.r1.ConceptDef resolveConceptRef(VersionedIdentifier libraryIdentifier, ConceptRef conceptRef) {
        if (conceptRef.getLibraryName() != null) {
            return resolveLibrary(libraryIdentifier, conceptRef.getLibraryName()).resolveConceptRef(conceptRef.getName());
        }

        return resolveLibrary(libraryIdentifier).resolveConceptRef(conceptRef.getName());
    }

    public CodeSystemDef resolveCodeSystemRef(ElmRequirement codeSystemRef) {
        return resolveCodeSystemRef(codeSystemRef.getLibraryIdentifier(), (CodeSystemRef)codeSystemRef.getElement());
    }

    public CodeSystemDef resolveCodeSystemRef(VersionedIdentifier libraryIdentifier, CodeSystemRef codeSystemRef) {
        if (codeSystemRef.getLibraryName() != null) {
            return resolveLibrary(libraryIdentifier, codeSystemRef.getLibraryName()).resolveCodeSystemRef(codeSystemRef.getName());
        }

        return resolveLibrary(libraryIdentifier).resolveCodeSystemRef(codeSystemRef.getName());
    }

    public ValueSetDef resolveValueSetRef(ElmRequirement valueSetRef) {
        return resolveValueSetRef(valueSetRef.getLibraryIdentifier(), (ValueSetRef)valueSetRef.getElement());
    }

    public ValueSetDef resolveValueSetRef(VersionedIdentifier libraryIdentifier, ValueSetRef valueSetRef) {
        if (valueSetRef.getLibraryName() != null) {
            return resolveLibrary(libraryIdentifier, valueSetRef.getLibraryName()).resolveValueSetRef(valueSetRef.getName());
        }

        return resolveLibrary(libraryIdentifier).resolveValueSetRef(valueSetRef.getName());
    }

    public CompiledLibrary resolveLibrary(ElmRequirement libraryRef) {
        return resolveLibrary(libraryRef.getLibraryIdentifier(), ((LibraryRef)libraryRef.getElement()).getLibraryName());
    }

    public IncludeDef resolveIncludeRef(VersionedIdentifier libraryIdentifier, String localLibraryName) {
        CompiledLibrary targetLibrary = resolveLibrary(libraryIdentifier);
        return targetLibrary.resolveIncludeRef(localLibraryName);
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier, String localLibraryName) {
        IncludeDef includeDef = resolveIncludeRef(libraryIdentifier, localLibraryName);
        return resolveLibraryFromIncludeDef(includeDef);
    }

    public CompiledLibrary resolveLibraryFromIncludeDef(IncludeDef includeDef) {
        VersionedIdentifier targetLibraryIdentifier = new VersionedIdentifier()
                .withSystem(NamespaceManager.getUriPart(includeDef.getPath()))
                .withId(NamespaceManager.getNamePart(includeDef.getPath()))
                .withVersion(includeDef.getVersion());

        return resolveLibrary(targetLibraryIdentifier);
    }

    public CompiledLibrary resolveLibrary(VersionedIdentifier libraryIdentifier) {
        // TODO: Need to support loading from ELM so we don't need options.
        CompiledLibrary referencedLibrary = libraryManager.resolveLibrary(libraryIdentifier);
        // TODO: Report translation errors here...
        //for (CqlTranslatorException error : errors) {
        //    this.recordParsingException(error);
        //}

        return referencedLibrary;
    }
}
