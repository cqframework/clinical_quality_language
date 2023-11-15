package org.cqframework.cql.elm.requirements.fhir;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.elm.evaluation.ElmAnalysisHelper;
import org.cqframework.cql.elm.evaluation.ElmEvaluationHelper;
import org.hl7.cql.model.NamespaceManager;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql.model.IntervalType;
import org.hl7.cql.model.ListType;
import org.hl7.cql.model.NamedType;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.Property;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.model.Library;
import org.hl7.fhir.r5.model.Enumerations.FHIRTypes;
import org.hl7.fhir.utilities.validation.ValidationMessage;
import org.cqframework.cql.elm.requirements.ElmDataRequirement;
import org.cqframework.cql.elm.requirements.ElmPertinenceContext;
import org.cqframework.cql.elm.requirements.ElmRequirement;
import org.cqframework.cql.elm.requirements.ElmRequirements;
import org.cqframework.cql.elm.requirements.ElmRequirementsContext;
import org.cqframework.cql.elm.requirements.ElmRequirementsVisitor;
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverter;
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverterFactory;

import jakarta.xml.bind.JAXBElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataRequirementsProcessor {

    private java.util.List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
    public java.util.List<ValidationMessage> getValidationMessages() {
        return this.validationMessages;
    }

    public Library gatherDataRequirements(LibraryManager libraryManager, CompiledLibrary translatedLibrary,
                                          CqlCompilerOptions options, Set<String> expressions,
                                          boolean includeLogicDefinitions) {
        return gatherDataRequirements(libraryManager, translatedLibrary, options, expressions, includeLogicDefinitions, true);
    }

    public Library gatherDataRequirements(LibraryManager libraryManager, CompiledLibrary translatedLibrary,
                                          CqlCompilerOptions options, Set<String> expressions,
                                          boolean includeLogicDefinitions, boolean recursive) {
        return gatherDataRequirements(libraryManager, translatedLibrary, options, expressions, null, null, includeLogicDefinitions, recursive);
    }

    public Library gatherDataRequirements(LibraryManager libraryManager, CompiledLibrary translatedLibrary,
                                          CqlCompilerOptions options, Set<String> expressions,
                                          Map<String, Object> parameters,
                                          boolean includeLogicDefinitions, boolean recursive) {
        return gatherDataRequirements(libraryManager, translatedLibrary, options, expressions, parameters, null, includeLogicDefinitions, recursive);
    }

    /**
     * Gathers the data requirements for the given translated library, returning a Library resource with the
     * effective data requirements for the given expression or set of expressions in the given compiled library.
     * @param libraryManager The library manager used to support compilation of the library
     * @param translatedLibrary The compiled library to gather data requirements from
     * @param options The translator options used to compile the library
     * @param expressions The expressions to gather data requirements for, null for all expressions in the library
     * @param parameters Any parameters to the expressions to be analyzed. If null, analysis will only be performed on ELM,
     *                   whereas if provided, analysis will be performed by attempting to evaluate compile-time evaluable
     *                   data requirements comparands
     * @param evaluationDateTime The date time of the evaluation (used to provide the request date time to the engine in the
     *                           case that compile-time evaluable expressions are evaluated
     * @param includeLogicDefinitions True to include logic definition extensions in the output containing the source for
     *                                each expression from which data requirements are gathered
     * @param recursive True to indicate the data requirements gather should be recursive
     * @return
     */
    public Library gatherDataRequirements(LibraryManager libraryManager, CompiledLibrary translatedLibrary,
                                          CqlCompilerOptions options, Set<String> expressions,
                                          Map<String, Object> parameters, ZonedDateTime evaluationDateTime,
                                          boolean includeLogicDefinitions, boolean recursive) {
        if (libraryManager == null) {
            throw new IllegalArgumentException("libraryManager required");
        }

        if (translatedLibrary == null) {
            throw new IllegalArgumentException("translatedLibrary required");
        }

        ElmRequirementsVisitor visitor = new ElmRequirementsVisitor();
        ElmRequirementsContext context = new ElmRequirementsContext(libraryManager, options, visitor, parameters, evaluationDateTime);

        List<ExpressionDef> expressionDefs = null;
        if (expressions == null) {
            visitor.visitLibrary(translatedLibrary.getLibrary(), context);
            if (translatedLibrary.getLibrary() != null && translatedLibrary.getLibrary().getStatements() != null) {
                expressionDefs = translatedLibrary.getLibrary().getStatements().getDef();
            }
            else {
                expressionDefs = new ArrayList<ExpressionDef>();
            }
        }
        else {
            if (expressionDefs == null) {
                expressionDefs = new ArrayList<ExpressionDef>();
            }

            context.enterLibrary(translatedLibrary.getIdentifier());
            try {
                for (String expression : expressions) {
                    ExpressionDef ed = translatedLibrary.resolveExpressionRef(expression);
                    if (ed != null) {
                        expressionDefs.add(ed);
                        visitor.visitElement(ed, context);
                    }
                    else {
                        // If the expression is the name of any functions, include those in the gather
                        // TODO: Provide a mechanism to specify a function def (need signature)
                        Iterable<FunctionDef> fds = translatedLibrary.resolveFunctionRef(expression);
                        for (FunctionDef fd : fds) {
                            expressionDefs.add(fd);
                            visitor.visitElement(fd, context);
                        }
                    }
                }
            }
            finally {
                context.exitLibrary();
            }
        }

        // In the non-recursive case
            // Collect top-level dependencies that have the same library identifier as the primary library
            // Collect data requirements reported or inferred on expressions in the library
        // In the recursive case
            // Collect all top-level dependencies
            // Collect all reported or inferred data requirements

        ElmRequirements requirements = new ElmRequirements(translatedLibrary.getIdentifier(), translatedLibrary.getLibrary());
        if (recursive) {
            // Collect all the dependencies
            requirements.reportRequirement(context.getRequirements());
            // Collect reported data requirements from each expression
            for (ElmRequirements reportedRequirements : context.getReportedRequirements()) {
                requirements.reportRequirement(reportedRequirements);
            }
            for (ElmRequirement inferredRequirement : context.getInferredRequirements()) {
                requirements.reportRequirement(inferredRequirement);
            }
        }
        else {
            for (ElmRequirement requirement : context.getRequirements().getRequirements()) {
                if (requirement.getLibraryIdentifier().equals(translatedLibrary.getIdentifier()))
                    requirements.reportRequirement(requirement);
            }
            for (ExpressionDef ed : expressionDefs) {
                // Just being defensive here, can happen when there are errors deserializing the measure
                if (ed != null) {
                    // Collect both inferred and reported requirements here, since reported requirements will not include
                    // directly inferred requirements
                    ElmRequirements reportedRequirements = context.getReportedRequirements(ed);
                    requirements.reportRequirement(reportedRequirements);

                    ElmRequirement inferredRequirement = context.getInferredRequirements(ed);
                    requirements.reportRequirement(inferredRequirement);
                }
            }
        }

        // Collapse the requirements
        if (options.getCollapseDataRequirements()) {
            for (ElmRequirement requirement : requirements.getRequirements()) {
                collapseExtensionReference(context, requirement);
            }
            requirements = requirements.collapse(context);
        }

        return createLibrary(context, requirements, translatedLibrary.getIdentifier(), expressionDefs, parameters, evaluationDateTime, includeLogicDefinitions);
    }

    /**
     * If the requirement has property references to `url` and `extension`, and a code filter on `url` to a literal extension value,
     * replace the `url` and `extension` property references with a new property reference to the tail of the extension
     * Also remove `us-core-` and `qicore-` as wellknown prefixes
     * TODO: Use the structure definition element slice name as the name of the property, rather than the hard-coded removal
     * of well-known prefixes
     * @param requirement
     */
    private void collapseExtensionReference(ElmRequirementsContext context, ElmRequirement requirement) {
        if (requirement instanceof ElmDataRequirement) {
            ElmDataRequirement dataRequirement = (ElmDataRequirement)requirement;
            if (dataRequirement.hasProperties()) {
                Property urlProperty = null;
                Property extensionProperty = null;
                for (Property p : dataRequirement.getProperties()) {
                    if (p.getPath().equals("url")) {
                        urlProperty = p;
                        continue;
                    }

                    if (p.getPath().equals("extension")) {
                        extensionProperty = p;
                        continue;
                    }
                }

                if (urlProperty != null) {
                    Retrieve r = dataRequirement.getRetrieve();
                    if (r != null) {
                        CodeFilterElement extensionFilterElement = null;
                        org.hl7.fhir.r5.model.DataRequirement.DataRequirementCodeFilterComponent extensionFilterComponent = null;
                        for (CodeFilterElement cfe : r.getCodeFilter()) {
                            org.hl7.fhir.r5.model.DataRequirement.DataRequirementCodeFilterComponent cfc =
                                    toCodeFilterComponent(context, dataRequirement.getLibraryIdentifier(), cfe.getProperty(), cfe.getValue());

                            if (cfc.hasPath() && cfc.hasCode() && "url".equals(cfc.getPath()) && cfc.getCodeFirstRep().hasCode() && cfc.getCodeFirstRep().getCode().startsWith("http://")) {
                                extensionFilterElement = cfe;
                                extensionFilterComponent = cfc;
                                break;
                            }
                        }

                        if (extensionFilterElement != null && extensionFilterComponent != null) {
                            String extensionName = extensionFilterComponent.getCodeFirstRep().getCode();
                            int tailIndex = extensionName.lastIndexOf("/");
                            if (tailIndex > 0) {
                                extensionName = extensionName.substring(tailIndex + 1);
                            }
                            if (extensionName.startsWith("us-core-")) {
                                extensionName = extensionName.substring(8);
                            }
                            if (extensionName.startsWith("qicore-")) {
                                extensionName = extensionName.substring(7);
                            }
                            r.getCodeFilter().remove(extensionFilterElement);
                            dataRequirement.removeProperty(urlProperty);
                            if (extensionProperty != null) {
                                dataRequirement.removeProperty(extensionProperty);
                            }
                            dataRequirement.addProperty(new Property().withPath(extensionName));
                        }
                    }
                }
            }
        }
    }
    private Library createLibrary(ElmRequirementsContext context, ElmRequirements requirements,
            VersionedIdentifier libraryIdentifier, Iterable<ExpressionDef> expressionDefs,
            Map<String, Object> parameters, ZonedDateTime evaluationDateTime, boolean includeLogicDefinitions) {
        Library returnLibrary = new Library();
        returnLibrary.setStatus(Enumerations.PublicationStatus.ACTIVE);
        CodeableConcept libraryType = new CodeableConcept();
        Coding typeCoding = new Coding().setCode("module-definition");
        typeCoding.setSystem("http://terminology.hl7.org/CodeSystem/library-type");
        libraryType.addCoding(typeCoding);
        returnLibrary.setName("EffectiveDataRequirements");
        returnLibrary.setType(libraryType);
        returnLibrary.setSubject(extractSubject(context));
        returnLibrary.getExtension().addAll(extractDirectReferenceCodes(context, requirements));
        returnLibrary.getRelatedArtifact().addAll(extractRelatedArtifacts(context, requirements));
        returnLibrary.getDataRequirement().addAll(extractDataRequirements(context, requirements));
        returnLibrary.getParameter().addAll(extractParameters(context, requirements, libraryIdentifier, expressionDefs));
        if (includeLogicDefinitions) {
            returnLibrary.getExtension().addAll(extractLogicDefinitions(context, requirements));
        }
        return returnLibrary;

    }

    private CodeableConcept extractSubject(ElmRequirementsContext context) {
        // TODO: Determine context (defaults to Patient if not set, so not critical until we have a non-patient-context use case)
        return null;
    }

    private List<Extension> extractDirectReferenceCodes(ElmRequirementsContext context, ElmRequirements requirements) {
        List<Extension> result = new ArrayList<>();

        for (ElmRequirement def : requirements.getCodeDefs()) {
            result.add(toDirectReferenceCode(context, def.getLibraryIdentifier(), (CodeDef)def.getElement()));
        }

        return result;
    }

    private Extension toDirectReferenceCode(ElmRequirementsContext context, VersionedIdentifier libraryIdentifier, CodeDef def) {
        Extension e = new Extension();
        // TODO: Promote this extension to the base specification
        e.setUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-directReferenceCode");
        e.setValue(toCoding(context, libraryIdentifier, context.toCode(def)));
        return e;
    }

    private List<RelatedArtifact> extractRelatedArtifacts(ElmRequirementsContext context, ElmRequirements requirements) {
        List<RelatedArtifact> result = new ArrayList<>();

        // Report model dependencies
        // URL for a model info is: [baseCanonical]/Library/[model-name]-ModelInfo
        for (ElmRequirement def : requirements.getUsingDefs()) {
            // System model info is an implicit dependency, do not report
            if (!((UsingDef)def.getElement()).getLocalIdentifier().equals("System")) {
                result.add(toRelatedArtifact(def.getLibraryIdentifier(), (UsingDef)def.getElement()));
            }
        }

        // Report library dependencies
        for (ElmRequirement def : requirements.getIncludeDefs()) {
            result.add(toRelatedArtifact(def.getLibraryIdentifier(), (IncludeDef)def.getElement()));
        }

        // Report CodeSystem dependencies
        for (ElmRequirement def : requirements.getCodeSystemDefs()) {
            result.add(toRelatedArtifact(def.getLibraryIdentifier(), (CodeSystemDef)def.getElement()));
        }

        // Report ValueSet dependencies
        for (ElmRequirement def : requirements.getValueSetDefs()) {
            result.add(toRelatedArtifact(def.getLibraryIdentifier(), (ValueSetDef)def.getElement()));
        }

        return result;
    }

    private boolean isEquivalentDefinition(ParameterDefinition existingPd, ParameterDefinition pd) {
        // TODO: Consider cardinality
        return pd.getType() == existingPd.getType();
    }

    private List<ParameterDefinition> extractParameters(ElmRequirementsContext context, ElmRequirements requirements,
            VersionedIdentifier libraryIdentifier, Iterable<ExpressionDef> expressionDefs) {
        List<ParameterDefinition> result = new ArrayList<>();

        // TODO: Support library qualified parameters
        // Until then, name clashes should result in a warning
        Map<String, ParameterDefinition> pds = new HashMap<String, ParameterDefinition>();
        for (ElmRequirement def : requirements.getParameterDefs()) {
            ParameterDefinition pd = toParameterDefinition(def.getLibraryIdentifier(), (ParameterDef)def.getElement());
            if (pds.containsKey(pd.getName())) {
                ParameterDefinition existingPd = pds.get(pd.getName());
                if (!isEquivalentDefinition(existingPd, pd)) {
                    // Issue a warning that the parameter has a duplicate name but an incompatible type
                    validationMessages.add(new ValidationMessage(ValidationMessage.Source.Publisher, ValidationMessage.IssueType.NOTSUPPORTED, "CQL Library Packaging",
                            String.format("Parameter declaration %s.%s is already defined in a different library with a different type. Parameter binding may result in errors during evaluation.",
                                    def.getLibraryIdentifier().getId(), pd.getName()), ValidationMessage.IssueSeverity.WARNING));
                }
            }
            else {
                pds.put(pd.getName(), pd);
                result.add(pd);
            }
        }

        for (ExpressionDef def : expressionDefs) {
            if (def != null && !(def instanceof FunctionDef) && (def.getAccessLevel() == null
                    || def.getAccessLevel() == AccessModifier.PUBLIC)) {
                result.add(toOutputParameterDefinition(libraryIdentifier, def));
            }
        }

        return result;
    }

    private org.hl7.cql_annotations.r1.Annotation getAnnotation(Element e) {
        for (Object o : e.getAnnotation()) {
            if (o instanceof org.hl7.cql_annotations.r1.Annotation) {
                return (org.hl7.cql_annotations.r1.Annotation)o;
            }
        }

        return null;
    }

    private String toNarrativeText(org.hl7.cql_annotations.r1.Annotation a) {
        StringBuilder sb = new StringBuilder();
        if (a.getS() != null) {
            addNarrativeText(sb, a.getS());
        }
        return sb.toString();
    }

    private void addNarrativeText(StringBuilder sb, org.hl7.cql_annotations.r1.Narrative n) {
        for (Serializable s : n.getContent()) {
            if (s instanceof org.hl7.cql_annotations.r1.Narrative) {
                addNarrativeText(sb, (org.hl7.cql_annotations.r1.Narrative)s);
            }
            else if (s instanceof String) {
                sb.append((String)s);
            }
            // TODO: THIS IS WRONG... SHOULDN'T NEED TO KNOW ABOUT JAXB TO ACCOMPLISH THIS
            else if (s instanceof JAXBElement<?>) {
                JAXBElement<?> j = (JAXBElement<?>)s;
                if (j.getValue() instanceof org.hl7.cql_annotations.r1.Narrative) {
                    addNarrativeText(sb, (org.hl7.cql_annotations.r1.Narrative)j.getValue());
                }
            }
        }
    }

    private List<Extension> extractLogicDefinitions(ElmRequirementsContext context, ElmRequirements requirements) {
        List<Extension> result = new ArrayList<Extension>();

        int sequence = 0;
        for (ElmRequirement req : requirements.getExpressionDefs()) {
            ExpressionDef def = (ExpressionDef)req.getElement();
            org.hl7.cql_annotations.r1.Annotation a = getAnnotation(def);
            if (a != null) {
                result.add(toLogicDefinition(req, def, toNarrativeText(a), sequence++));
            }
        }

        for (ElmRequirement req : requirements.getFunctionDefs()) {
            FunctionDef def = (FunctionDef)req.getElement();
            org.hl7.cql_annotations.r1.Annotation a = getAnnotation(def);
            if (a != null) {
                result.add(toLogicDefinition(req, def, toNarrativeText(a), sequence++));
            }
        }

        return result;
    }

    private StringType toString(String value) {
        StringType result = new StringType();
        result.setValue(value);
        return result;
    }

    private IntegerType toInteger(int value) {
        IntegerType result = new IntegerType();
        result.setValue(value);
        return result;
    }

    private Extension toLogicDefinition(ElmRequirement req, ExpressionDef def, String text, int sequence) {
        Extension e = new Extension();
        e.setUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-logicDefinition");
        // TODO: Include the libraryUrl
        e.addExtension(new Extension().setUrl("libraryName").setValue(toString(req.getLibraryIdentifier().getId())));
        e.addExtension(new Extension().setUrl("name").setValue(toString(def.getName())));
        e.addExtension(new Extension().setUrl("statement").setValue(toString(text)));
        e.addExtension(new Extension().setUrl("displaySequence").setValue(toInteger(sequence)));
        return e;
    }

    private List<DataRequirement> extractDataRequirements(ElmRequirementsContext context, ElmRequirements requirements) {
        List<DataRequirement> result = new ArrayList<>();

        Map<String, Retrieve> retrieveMap = new HashMap<String, Retrieve>();
        for (ElmRequirement retrieve : requirements.getRetrieves()) {
            if (retrieve.getElement().getLocalId() != null) {
                retrieveMap.put(retrieve.getElement().getLocalId(), (Retrieve)retrieve.getElement());
            }
        }

        for (ElmRequirement retrieve : requirements.getRetrieves()) {
            if (((Retrieve)retrieve.getElement()).getDataType() != null) {
                result.add(toDataRequirement(context, retrieve.getLibraryIdentifier(), (Retrieve) retrieve.getElement(),
                        retrieveMap, retrieve instanceof ElmDataRequirement ? ((ElmDataRequirement) retrieve).getProperties() : null,
                        retrieve instanceof ElmDataRequirement ? ((ElmDataRequirement) retrieve).getPertinenceContext() : null));
            }
        }

        return result;
    }

    private org.hl7.fhir.r5.model.RelatedArtifact toRelatedArtifact(VersionedIdentifier libraryIdentifier, UsingDef usingDef) {
        return new org.hl7.fhir.r5.model.RelatedArtifact()
                .setType(RelatedArtifact.RelatedArtifactType.DEPENDSON)
                .setDisplay(usingDef.getLocalIdentifier() != null ? String.format("%s model information", usingDef.getLocalIdentifier()) : null) // Could potentially look for a well-known comment tag too, @description?
                .setResource(getModelInfoReferenceUrl(usingDef.getUri(),
                        usingDef.getLocalIdentifier(), usingDef.getVersion()));
    }

    /*
    Override the referencing URL for the FHIR-ModelInfo library
    This is required because models do not have a "namespace" in the same way that libraries do,
    so there is no way for the UsingDefinition to have a Uri that is different than the expected URI that the
    providers understand. I.e. model names and model URIs are one-to-one.
     */
    private String mapModelInfoUri(String uri, String name) {
        if (name.equals("FHIR") && uri.equals("http://hl7.org/fhir")) {
            return "http://fhir.org/guides/cqf/common";
        }
        return uri;
    }

    private String getModelInfoReferenceUrl(String uri, String name, String version) {
        if (uri != null) {
            return String.format("%s/Library/%s-ModelInfo%s", mapModelInfoUri(uri, name), name, version != null ? ("|" + version) : "");
        }

        return String.format("Library/%-ModelInfo%s", name, version != null ? ("|" + version) : "");
    }

    private org.hl7.fhir.r5.model.RelatedArtifact toRelatedArtifact(VersionedIdentifier libraryIdentifier, IncludeDef includeDef) {
        return new org.hl7.fhir.r5.model.RelatedArtifact()
                .setType(org.hl7.fhir.r5.model.RelatedArtifact.RelatedArtifactType.DEPENDSON)
                .setDisplay(includeDef.getLocalIdentifier() != null ? String.format("Library %s", includeDef.getLocalIdentifier()) : null) // Could potentially look for a well-known comment tag too, @description?
                .setResource(getReferenceUrl(includeDef.getPath(), includeDef.getVersion()));
    }

    private String getReferenceUrl(String path, String version) {
        String uri = NamespaceManager.getUriPart(path);
        String name = NamespaceManager.getNamePart(path);

        if (uri != null) {
            // The translator has no way to correctly infer the namespace of the FHIRHelpers library, since it will happily provide that source to any namespace that wants it
            // So override the declaration here so that it points back to the FHIRHelpers library in the base specification
            //if (name.equals("FHIRHelpers") && !(uri.equals("http://hl7.org/fhir") || uri.equals("http://fhir.org/guides/cqf/common"))) {
            //    uri = "http://fhir.org/guides/cqf/common";
            //}
            return String.format("%s/Library/%s%s", uri, name, version != null ? ("|" + version) : "");
        }

        return String.format("Library/%s%s", path, version != null ? ("|" + version) : "");
    }

    private org.hl7.fhir.r5.model.RelatedArtifact toRelatedArtifact(VersionedIdentifier libraryIdentifier, CodeSystemDef codeSystemDef) {
        return new org.hl7.fhir.r5.model.RelatedArtifact()
                .setType(org.hl7.fhir.r5.model.RelatedArtifact.RelatedArtifactType.DEPENDSON)
                .setDisplay(String.format("Code system %s", codeSystemDef.getName()))
                .setResource(toReference(codeSystemDef));
    }

    private org.hl7.fhir.r5.model.RelatedArtifact toRelatedArtifact(VersionedIdentifier libraryIdentifier, ValueSetDef valueSetDef) {
        return new org.hl7.fhir.r5.model.RelatedArtifact()
                .setType(org.hl7.fhir.r5.model.RelatedArtifact.RelatedArtifactType.DEPENDSON)
                .setDisplay(String.format("Value set %s", valueSetDef.getName()))
                .setResource(toReference(valueSetDef));
    }

    private ParameterDefinition toParameterDefinition(VersionedIdentifier libraryIdentifier, ParameterDef def) {
        AtomicBoolean isList = new AtomicBoolean(false);
        FHIRTypes typeCode = Enumerations.FHIRTypes.fromCode(toFHIRParameterTypeCode(def.getResultType(), def.getName(), isList));

        return new ParameterDefinition()
                .setName(def.getName())
                .setUse(Enumerations.OperationParameterUse.IN)
                .setMin(0)
                .setMax(isList.get() ? "*" : "1")
                .setType(typeCode);
    }

    private ParameterDefinition toOutputParameterDefinition(VersionedIdentifier libraryIdentifier, ExpressionDef def) {
        AtomicBoolean isList = new AtomicBoolean(false);
        Enumerations.FHIRTypes typeCode = null;
        try{
                typeCode = Enumerations.FHIRTypes.fromCode(
                        toFHIRResultTypeCode(def.getResultType(), def.getName(), isList));
        }catch(org.hl7.fhir.exceptions.FHIRException fhirException){
            validationMessages.add(new ValidationMessage(ValidationMessage.Source.Publisher, ValidationMessage.IssueType.NOTSUPPORTED, "CQL Library Packaging",
                    String.format("Result type %s of library %s is not supported; implementations may not be able to use the result of this expression",
                            def.getResultType().toLabel(), libraryIdentifier.getId()), ValidationMessage.IssueSeverity.WARNING));
        }

        return new ParameterDefinition()
                .setName(def.getName())
                .setUse(Enumerations.OperationParameterUse.OUT)
                .setMin(0)
                .setMax(isList.get() ? "*" : "1")
                .setType(typeCode);
    }

    private String toFHIRResultTypeCode(org.hl7.cql.model.DataType dataType, String defName, AtomicBoolean isList) {
        AtomicBoolean isValid = new AtomicBoolean(true);
        String resultCode = toFHIRTypeCode(dataType, isValid, isList);
        if (!isValid.get()) {
            // Issue a warning that the result type is not supported
            validationMessages.add(new ValidationMessage(ValidationMessage.Source.Publisher, ValidationMessage.IssueType.NOTSUPPORTED, "CQL Library Packaging",
                    String.format("Result type %s of definition %s is not supported; implementations may not be able to use the result of this expression",
                            dataType.toLabel(), defName), ValidationMessage.IssueSeverity.WARNING));
        }

        return resultCode;
    }

    private String toFHIRParameterTypeCode(org.hl7.cql.model.DataType dataType, String parameterName, AtomicBoolean isList) {
        AtomicBoolean isValid = new AtomicBoolean(true);
        String resultCode = toFHIRTypeCode(dataType, isValid, isList);
        if (!isValid.get()) {
            // Issue a warning that the parameter type is not supported
            validationMessages.add(new ValidationMessage(ValidationMessage.Source.Publisher, ValidationMessage.IssueType.NOTSUPPORTED, "CQL Library Packaging",
                    String.format("Parameter type %s of parameter %s is not supported; reported as FHIR.Any", dataType.toLabel(), parameterName), ValidationMessage.IssueSeverity.WARNING));
        }

        return resultCode;
    }

    private String toFHIRTypeCode(org.hl7.cql.model.DataType dataType, AtomicBoolean isValid, AtomicBoolean isList) {
        isList.set(false);
        if (dataType instanceof ListType) {
            isList.set(true);
            return toFHIRTypeCode(((ListType)dataType).getElementType(), isValid);
        }

        return toFHIRTypeCode(dataType, isValid);
    }

    private String toFHIRTypeCode(org.hl7.cql.model.DataType dataType, AtomicBoolean isValid) {
        isValid.set(true);
        if (dataType instanceof NamedType) {
            switch (((NamedType)dataType).getName()) {
                case "System.Boolean": return "boolean";
                case "System.Integer": return "integer";
                case "System.Decimal": return "decimal";
                case "System.Date": return "date";
                case "System.DateTime": return "dateTime";
                case "System.Time": return "time";
                case "System.String": return "string";
                case "System.Quantity": return "Quantity";
                case "System.Ratio": return "Ratio";
                case "System.Any": return "Any";
                case "System.Code": return "Coding";
                case "System.Concept": return "CodeableConcept";
            }

            if ("FHIR".equals(((NamedType)dataType).getNamespace())) {
                return ((NamedType)dataType).getSimpleName();
            }
        }

        if (dataType instanceof IntervalType) {
            if (((IntervalType)dataType).getPointType() instanceof NamedType) {
                switch (((NamedType)((IntervalType)dataType).getPointType()).getName()) {
                    case "System.Date":
                    case "System.DateTime": return "Period";
                    case "System.Quantity": return "Range";
                }
            }
        }

        isValid.set(false);
        return "Any";
    }

    /**
     * TODO: This function is used to determine the library identifier in which the reference element was declared
     * This is only possible if the ELM includes trackbacks, which are typically only available in ELM coming straight from the translator
     * (i.e. de-compiled ELM won't have this)
     * The issue is that when code filter expressions are distributed, the references may cross declaration contexts (i.e. a code filter
     * expression from the library in which it was first expressed may be evaluated in the context of a data requirement inferred
     * from a retrieve in a different library. If the library aliases are consistent, this isn't an issue, but if the library aliases
     * are different, this will result in a failure to resolve the reference (or worse, an incorrect reference).
     * This is being reported as a warning currently, but it is really an issue with the data requirement distribution, it should be
     * rewriting references as it distributes (or better yet, ELM should have a library identifier that is fully resolved, rather
     * than relying on library-specific aliases for library referencing elements.
     * @param trackable
     * @param libraryIdentifier
     * @return
     */
    private VersionedIdentifier getDeclaredLibraryIdentifier(Trackable trackable, VersionedIdentifier libraryIdentifier) {
        if (trackable.getTrackbacks() != null) {
            for (TrackBack tb : trackable.getTrackbacks()) {
                if (tb.getLibrary() != null) {
                    return tb.getLibrary();
                }
            }
        }

        validationMessages.add(new ValidationMessage(ValidationMessage.Source.Publisher, ValidationMessage.IssueType.PROCESSING, "Data requirements processing",
                String.format("Library referencing element (%s) is potentially being resolved in a different context than it was declared. Ensure library aliases are consistent", trackable.getClass().getSimpleName()), ValidationMessage.IssueSeverity.WARNING));

        return libraryIdentifier;
    }

    private org.hl7.fhir.r5.model.DataRequirement.DataRequirementCodeFilterComponent toCodeFilterComponent(ElmRequirementsContext context, VersionedIdentifier libraryIdentifier, String property, Expression value) {
        org.hl7.fhir.r5.model.DataRequirement.DataRequirementCodeFilterComponent cfc =
                new org.hl7.fhir.r5.model.DataRequirement.DataRequirementCodeFilterComponent();

        cfc.setPath(property);

        // TODO: Support retrieval when the target is a CodeSystemRef

        if (value instanceof ValueSetRef) {
            ValueSetRef vsr = (ValueSetRef)value;
            VersionedIdentifier declaredLibraryIdentifier = getDeclaredLibraryIdentifier(vsr, libraryIdentifier);
            cfc.setValueSet(toReference(context.resolveValueSetRef(declaredLibraryIdentifier, vsr)));
        }

        if (value instanceof org.hl7.elm.r1.ToList) {
            org.hl7.elm.r1.ToList toList = (org.hl7.elm.r1.ToList)value;
            resolveCodeFilterCodes(context, libraryIdentifier, cfc, toList.getOperand());
        }

        if (value instanceof org.hl7.elm.r1.List) {
            org.hl7.elm.r1.List codeList = (org.hl7.elm.r1.List)value;
            for (Expression e : codeList.getElement()) {
                resolveCodeFilterCodes(context, libraryIdentifier, cfc, e);
            }
        }

        if (value instanceof org.hl7.elm.r1.Literal) {
            org.hl7.elm.r1.Literal literal = (org.hl7.elm.r1.Literal)value;
            cfc.addCode().setCode(literal.getValue());
        }

        return cfc;
    }

    private DataType toFhirValue(ElmRequirementsContext context, Expression value) {
        if (value == null) {
            return null;
        }

        if (context.getParameters() == null) {
            return ElmAnalysisHelper.toFhirValue(context, value);
        }
        else {
            // Attempt to use an evaluation visitor to evaluate the value (must be compile-time literal or this will produce a runtime error)
            Object result = ElmEvaluationHelper.evaluate(context.resolveLibrary(context.getCurrentLibraryIdentifier()).getLibrary(), value, context.getParameters(), context.getEvaluationDateTime());

            if (result instanceof DataType) {
                return (DataType)result;
            }

            if (result == null) {
                return null;
            }

            FhirTypeConverter converter = new FhirTypeConverterFactory().create(FhirVersionEnum.R5);
            IBase fhirResult = converter.toFhirType(result);
            if (fhirResult instanceof DataType) {
                return (DataType)fhirResult;
            }
            throw new IllegalArgumentException(String.format("toFhirValue not implemented for result of type %s", result.getClass().getSimpleName()));
        }
    }

    private org.hl7.fhir.r5.model.DataRequirement.DataRequirementDateFilterComponent toDateFilterComponent(ElmRequirementsContext context, VersionedIdentifier libraryIdentifier, String property, Expression value) {
        org.hl7.fhir.r5.model.DataRequirement.DataRequirementDateFilterComponent dfc =
                new org.hl7.fhir.r5.model.DataRequirement.DataRequirementDateFilterComponent();

        dfc.setPath(property);

        context.enterLibrary(libraryIdentifier);
        try {
            dfc.setValue(toFhirValue(context, value));
        }
        catch (Exception e) {
            Period p = new Period();
            p.addExtension("http://hl7.org/fhir/uv/crmi-analysisException", new StringType(String.format("Error attempting to determine filter value: %s", e.getMessage())));
            dfc.setValue(p);
        }
        finally {
            context.exitLibrary();
        }

        return dfc;
    }

    /**
     * Remove .reference from the path if the path is being used as a reference search
     * @param path
     * @return
     */
    private String stripReference(String path) {
        if (path.endsWith(".reference")) {
            return path.substring(0, path.lastIndexOf("."));
        }
        return path;
    }

    private org.hl7.fhir.r5.model.DataRequirement toDataRequirement(ElmRequirementsContext context,
            VersionedIdentifier libraryIdentifier, Retrieve retrieve, Map<String, Retrieve> retrieveMap, Iterable<Property> properties,
            ElmPertinenceContext pertinenceContext) {
        org.hl7.fhir.r5.model.DataRequirement dr = new org.hl7.fhir.r5.model.DataRequirement();
        try {
            dr.setType(org.hl7.fhir.r5.model.Enumerations.FHIRTypes.fromCode(retrieve.getDataType().getLocalPart()));
        }
        catch(org.hl7.fhir.exceptions.FHIRException fhirException) {
            validationMessages.add(new ValidationMessage(ValidationMessage.Source.Publisher, ValidationMessage.IssueType.NOTSUPPORTED, "CQL Library Packaging",
                    String.format("Result type %s of library %s is not supported; implementations may not be able to use the result of this expression",
                            retrieve.getDataType().getLocalPart(), libraryIdentifier.getId()), ValidationMessage.IssueSeverity.WARNING));
        }

        // Set the id attribute of the data requirement if it will be referenced from an included retrieve
        if (retrieve.getLocalId() != null && retrieve.getInclude() != null && retrieve.getInclude().size() > 0) {
            for (IncludeElement ie : retrieve.getInclude()) {
                if (ie.getIncludeFrom() != null) {
                    dr.setId(retrieve.getLocalId());
                }
            }
        }

        // Set profile if specified
        if (retrieve.getTemplateId() != null) {
            dr.setProfile(Collections.singletonList(new org.hl7.fhir.r5.model.CanonicalType(retrieve.getTemplateId())));
        }

        // collect must supports
        Set<String> ps = new LinkedHashSet<String>();

        // Set code path if specified
        if (retrieve.getCodeProperty() != null) {
            dr.getCodeFilter().add(toCodeFilterComponent(context, libraryIdentifier, retrieve.getCodeProperty(), retrieve.getCodes()));
            ps.add(retrieve.getCodeProperty());
        }

        // Add any additional code filters
        for (CodeFilterElement cfe : retrieve.getCodeFilter()) {
            dr.getCodeFilter().add(toCodeFilterComponent(context, libraryIdentifier, cfe.getProperty(), cfe.getValue()));
        }

        // Set date path if specified
        if (retrieve.getDateProperty() != null) {
            dr.getDateFilter().add(toDateFilterComponent(context, libraryIdentifier, retrieve.getDateProperty(), retrieve.getDateRange()));
            ps.add(retrieve.getDateProperty());
        }

        // Add any additional date filters
        for (DateFilterElement dfe : retrieve.getDateFilter()) {
            dr.getDateFilter().add(toDateFilterComponent(context, libraryIdentifier, dfe.getProperty(), dfe.getValue()));
        }

        // TODO: Add any other filters (use the cqfm-valueFilter extension until the content infrastructure IG is available)

        // Add any related data requirements
        if (retrieve.getIncludedIn() != null) {
            Retrieve relatedRetrieve = retrieveMap.get(retrieve.getIncludedIn());
            if (relatedRetrieve == null) {
                throw new IllegalArgumentException(String.format("Could not resolve related retrieve with localid %s", retrieve.getIncludedIn()));
            }
            IncludeElement includeElement = null;
            for (IncludeElement ie : relatedRetrieve.getInclude()) {
                if (ie.getIncludeFrom() != null && ie.getIncludeFrom().equals(retrieve.getLocalId())) {
                    includeElement = ie;
                    break;
                }
            }
            if (relatedRetrieve != null && includeElement != null) {
                Extension relatedRequirement = new Extension().setUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement");
                relatedRequirement.addExtension("targetId", new StringType(retrieve.getIncludedIn()));
                relatedRequirement.addExtension("targetProperty", new StringType(stripReference(includeElement.getRelatedProperty())));
                dr.addExtension(relatedRequirement);
            }
        }

        // Add any properties as mustSupport items
        if (properties != null) {
            for (Property p : properties) {
                if (!ps.contains(p.getPath())) {
                    ps.add(p.getPath());
                }
            }
        }
        for (String s : ps) {
            dr.addMustSupport(s);
        }

        if (pertinenceContext != null && pertinenceContext.getPertinenceValue() != null
                && !(pertinenceContext.getPertinenceValue().trim().isEmpty())) {
            Extension extension = new Extension();
            extension.setUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence");

            Coding coding = new Coding();
            coding.setSystem("http://hl7.org/fhir/uv/cpg/CodeSystem/cpg-casefeature-pertinence");
            coding.setCode(pertinenceContext.getPertinenceValue());
            extension.setValue(coding);

            dr.getExtension().add(extension);
        }

        return dr;
    }

    private void resolveCodeFilterCodes(ElmRequirementsContext context, VersionedIdentifier libraryIdentifier,
                                        org.hl7.fhir.r5.model.DataRequirement.DataRequirementCodeFilterComponent cfc,
                                        Expression e) {
        if (e instanceof org.hl7.elm.r1.CodeRef) {
            CodeRef cr = (CodeRef)e;
            VersionedIdentifier declaredLibraryIdentifier = getDeclaredLibraryIdentifier(cr, libraryIdentifier);
            cfc.addCode(toCoding(context, libraryIdentifier, context.toCode(context.resolveCodeRef(declaredLibraryIdentifier, cr))));
        }

        if (e instanceof org.hl7.elm.r1.Code) {
            cfc.addCode(toCoding(context, libraryIdentifier, (org.hl7.elm.r1.Code)e));
        }

        if (e instanceof org.hl7.elm.r1.ConceptRef) {
            ConceptRef cr = (ConceptRef)e;
            VersionedIdentifier declaredLibraryIdentifier = getDeclaredLibraryIdentifier(cr, libraryIdentifier);
            org.hl7.fhir.r5.model.CodeableConcept c = toCodeableConcept(context, libraryIdentifier,
                    context.toConcept(libraryIdentifier, context.resolveConceptRef(declaredLibraryIdentifier, cr)));
            for (org.hl7.fhir.r5.model.Coding code : c.getCoding()) {
                cfc.addCode(code);
            }
        }

        if (e instanceof org.hl7.elm.r1.Concept) {
            org.hl7.fhir.r5.model.CodeableConcept c = toCodeableConcept(context, libraryIdentifier, (org.hl7.elm.r1.Concept)e);
            for (org.hl7.fhir.r5.model.Coding code : c.getCoding()) {
                cfc.addCode(code);
            }
        }

        if (e instanceof org.hl7.elm.r1.Literal) {
            org.hl7.elm.r1.Literal literal = (org.hl7.elm.r1.Literal)e;
            cfc.addCode().setCode(literal.getValue());
        }
    }

    private org.hl7.fhir.r5.model.Coding toCoding(ElmRequirementsContext context, VersionedIdentifier libraryIdentifier, Code code) {
        VersionedIdentifier declaredLibraryIdentifier = getDeclaredLibraryIdentifier(code.getSystem(), libraryIdentifier);
        CodeSystemDef codeSystemDef = context.resolveCodeSystemRef(declaredLibraryIdentifier, code.getSystem());
        org.hl7.fhir.r5.model.Coding coding = new org.hl7.fhir.r5.model.Coding();
        coding.setCode(code.getCode());
        coding.setDisplay(code.getDisplay());
        coding.setSystem(codeSystemDef.getId());
        coding.setVersion(codeSystemDef.getVersion());
        return coding;
    }

    private org.hl7.fhir.r5.model.CodeableConcept toCodeableConcept(ElmRequirementsContext context,
                                                                    VersionedIdentifier libraryIdentifier,
                                                                    Concept concept) {
        org.hl7.fhir.r5.model.CodeableConcept codeableConcept = new org.hl7.fhir.r5.model.CodeableConcept();
        codeableConcept.setText(concept.getDisplay());
        for (Code code : concept.getCode()) {
            codeableConcept.addCoding(toCoding(context, libraryIdentifier, code));
        }
        return codeableConcept;
    }

    private String toReference(CodeSystemDef codeSystemDef) {
        return codeSystemDef.getId() + (codeSystemDef.getVersion() != null ? ("|" + codeSystemDef.getVersion()) : "");
    }

    private String toReference(ValueSetDef valueSetDef) {
        return valueSetDef.getId() + (valueSetDef.getVersion() != null ? ("|" + valueSetDef.getVersion()) : "");
    }
}
