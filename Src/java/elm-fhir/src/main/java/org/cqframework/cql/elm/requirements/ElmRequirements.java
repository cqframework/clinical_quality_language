package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
public class ElmRequirements extends ElmRequirement {

    private HashSet<ElmRequirement> requirements = new LinkedHashSet<ElmRequirement>();
    public Iterable<ElmRequirement> getRequirements() {
        return requirements;
    }

    public ElmRequirements(VersionedIdentifier libraryIdentifier, Element element) {
        super(libraryIdentifier, element);
    }

    public void reportRequirement(ElmRequirement requirement) {
        if (requirement instanceof ElmRequirements) {
            for (ElmRequirement r : ((ElmRequirements)requirement).getRequirements()) {
                reportRequirement(r);
            }
        }
        else {
            if (requirement != null) {
                requirements.add(requirement);
            }
        }
    }

    public Iterable<ElmRequirement> getUsingDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof UsingDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getIncludeDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof IncludeDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getCodeSystemDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof CodeSystemDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getValueSetDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof ValueSetDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getCodeDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof CodeDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getConceptDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof ConceptDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getParameterDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof ParameterDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getExpressionDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof ExpressionDef && !(x.getElement() instanceof FunctionDef)).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getFunctionDefs() {
        return requirements.stream().filter(x -> x.getElement() instanceof FunctionDef).collect(Collectors.toList());
    }

    public Iterable<ElmRequirement> getRetrieves() {
        return requirements.stream().filter(x -> x.getElement() instanceof Retrieve).collect(Collectors.toList());
    }

    /*
    Collapse requirements: Determine the unique set of covering requirements given this set of requirements
    For dependencies, ensure dependencies are unique
    For parameters, unique by qualified name
    For expressions, unique by qualified name
    For data requirements, collapse according to the CQL specification: https://cql.hl7.org/05-languagesemantics.html#artifact-data-requirements
     */
    public ElmRequirements collapse(ElmRequirementsContext context) {
        ElmRequirements result = new ElmRequirements(this.libraryIdentifier, this.element);

        // UsingDefs
        Map<String, ElmRequirement> models = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getUsingDefs()) {
            UsingDef ud = (UsingDef)r.getElement();
            String uri = ud.getUri() + (ud.getVersion() != null ? "|" + ud.getVersion() : "");
            if (!models.containsKey(uri)) {
                models.put(uri, r);
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (ElmRequirement r : models.values()) {
            result.reportRequirement(r);
        }

        // IncludeDefs
        Map<String, ElmRequirement> libraries = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getIncludeDefs()) {
            IncludeDef id = (IncludeDef)r.getElement();
            String uri = id.getPath() + (id.getVersion() != null ? "|" + id.getVersion() : "");
            if (!libraries.containsKey(uri)) {
                libraries.put(uri, r);
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (ElmRequirement r : libraries.values()) {
            result.reportRequirement(r);
        }

        // CodeSystemDefs
        Map<String, ElmRequirement> codeSystems = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getCodeSystemDefs()) {
            CodeSystemDef csd = (CodeSystemDef)r.getElement();
            String uri = csd.getId() + (csd.getVersion() != null ? "|" + csd.getVersion() : "");
            if (!codeSystems.containsKey(uri)) {
                codeSystems.put(uri, r);
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (ElmRequirement r : codeSystems.values()) {
            result.reportRequirement(r);
        }

        // ValueSetDefs
        Map<String, ElmRequirement> valueSets = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getValueSetDefs()) {
            ValueSetDef vsd = (ValueSetDef)r.getElement();
            String uri = vsd.getId() + (vsd.getVersion() != null ? "|" + vsd.getVersion() : "");
            if (!valueSets.containsKey(uri)) {
                valueSets.put(uri, r);
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (ElmRequirement r : valueSets.values()) {
            result.reportRequirement(r);
        }

        // ConceptDefs
        Map<String, ElmRequirement> concepts = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getConceptDefs()) {
            ConceptDef cd = (ConceptDef)r.getElement();
            String uri = String.format("%s%s.%s",
                      r.getLibraryIdentifier().getSystem() != null ? r.getLibraryIdentifier().getSystem() + "." : "",
                      r.getLibraryIdentifier().getId(),
                      cd.getName()
                    );
            if (!concepts.containsKey(uri)) {
                concepts.put(uri, r);
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (ElmRequirement r : concepts.values()) {
            result.reportRequirement(r);
        }

        // CodeDefs
        Map<String, ElmRequirement> codes = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getCodeDefs()) {
            CodeDef cd = (CodeDef)r.getElement();
            String uri = String.format("%s#%s",
                      // TODO: Look up CodeSystemDef to determine code system URI
                      cd.getCodeSystem().getName(),
                      cd.getId()
                    );

            if (!codes.containsKey(uri)) {
                codes.put(uri, r);
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (ElmRequirement r : codes.values()) {
            result.reportRequirement(r);
        }

        // ParameterDefs
        // NOTE: This purposely consolidates on unqualified name, the use case is from the perspective of a particular artifact,
        // parameters of the same name should be bound to the same input (i.e. single input parameter namespace)
        Map<String, ElmRequirement> parameters = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getParameterDefs()) {
            ParameterDef pd = (ParameterDef)r.getElement();
            String uri = pd.getName();

            if (!parameters.containsKey(uri)) {
                parameters.put(uri, r);
                // TODO: How to report duplicate references, potentially warn about different names?
                // TODO: Note that it is potentially a hidden error here if parameters with the same name have different types
            }
        }

        for (ElmRequirement r : parameters.values()) {
            result.reportRequirement(r);
        }

        // ExpressionDefs
        Map<String, ElmRequirement> expressions = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getExpressionDefs()) {
            ExpressionDef ed = (ExpressionDef)r.getElement();
            String uri = String.format("%s%s.%s",
                      r.getLibraryIdentifier().getSystem() != null ? r.getLibraryIdentifier().getSystem() + "." : "",
                      r.getLibraryIdentifier().getId(),
                      ed.getName()
                    );

            if (!expressions.containsKey(uri)) {
                expressions.put(uri, r);
                // TODO: Do we need to report all the libraries that referred to this?
            }
        }

        for (ElmRequirement r : expressions.values()) {
            result.reportRequirement(r);
        }

        // FunctionDefs
        Map<String, ElmRequirement> functions = new LinkedHashMap<String, ElmRequirement>();
        for (ElmRequirement r : getFunctionDefs()) {
            FunctionDef fd = (FunctionDef)r.getElement();
            // TODO: Include overloads...
            String uri = String.format("%s%s.%s()",
                      r.getLibraryIdentifier().getSystem() != null ? r.getLibraryIdentifier().getSystem() + "." : "",
                      r.getLibraryIdentifier().getId(),
                      fd.getName()
                    );

            if (!functions.containsKey(uri)) {
                functions.put(uri, r);
                // TODO: Do we need to report all the libraries that referred to this?
            }
        }

        for (ElmRequirement r : functions.values()) {
            result.reportRequirement(r);
        }

        // Retrieves
        // Sort retrieves by type/profile to reduce search space
        LinkedHashMap<String, List<ElmRequirement>> retrievesByType = new LinkedHashMap<String, List<ElmRequirement>>();
        List<ElmRequirement> unboundRequirements = new ArrayList<>();
        for (ElmRequirement r : getRetrieves()) {
            Retrieve retrieve = (Retrieve)r.getElement();
            if (retrieve.getDataType() != null) {
                String typeUri = retrieve.getTemplateId() != null ? retrieve.getTemplateId() : retrieve.getDataType().getLocalPart();
                List<ElmRequirement> typeRetrieves = null;
                if (retrievesByType.containsKey(typeUri)) {
                    typeRetrieves = retrievesByType.get(typeUri);
                }
                else {
                    typeRetrieves = new ArrayList<ElmRequirement>();
                    retrievesByType.put(typeUri, typeRetrieves);
                }
                typeRetrieves.add(r);
            }
            else {
                unboundRequirements.add(r);
            }
        }

        // Distribute unbound property requirements
        // If an ElmDataRequirement has a retrieve that does not have a dataType (i.e. it is not a direct data access layer retrieve
        // but rather is the result of requirements inference), then distribute the property references it contains to
        // all data layer-bound retrieves of the same type
        // In other words, we can't unambiguously tie the property reference to any particular retrieve of that type,
        // so apply it to all of them
        for (ElmRequirement requirement : unboundRequirements) {
            if (requirement instanceof ElmDataRequirement) {
                ElmDataRequirement dataRequirement = (ElmDataRequirement)requirement;
                if (dataRequirement.hasProperties()) {
                    String typeUri =  context.getTypeResolver().getTypeUri(dataRequirement.getRetrieve().getResultType());
                    if (typeUri != null) {
                        List<ElmRequirement> typeRequirements = retrievesByType.get(typeUri);
                        if (typeRequirements != null) {
                            for (ElmRequirement typeRequirement : typeRequirements) {
                                if (typeRequirement instanceof ElmDataRequirement) {
                                    ElmDataRequirement typeDataRequirement = (ElmDataRequirement)typeRequirement;
                                    for (Property p : dataRequirement.getProperties()) {
                                        typeDataRequirement.addProperty(p);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        // Equivalent
            // Has the same context, type/profile, code path and date path
        // If two retrieves are "equivalent" they can be merged
        // TODO: code/date-range consolidation
        Map<String, String> requirementIdMap = new HashMap<>();
        for (Map.Entry<String, List<ElmRequirement>> entry : retrievesByType.entrySet()) {
            // Determine unique set per type/profile
            CollapsedElmRequirements collapsedRetrieves = new CollapsedElmRequirements();
            for (ElmRequirement requirement : entry.getValue()) {
                collapsedRetrieves.add(requirement);
            }

            // Collect target mappings
            for (Map.Entry<String, String> idMapEntry : collapsedRetrieves.getRequirementIdMap().entrySet()) {
                requirementIdMap.put(idMapEntry.getKey(), idMapEntry.getValue());
            }

            for (ElmRequirement r : collapsedRetrieves.getUniqueRequirements()) {
                result.reportRequirement(r);
            }
        }

        // Fixup references in the resulting requirements
        for (ElmRequirement requirement : result.getRequirements()) {
            if (requirement.getElement() instanceof Retrieve) {
                Retrieve r = ((Retrieve)requirement.getElement());
                if (r.getIncludedIn() != null) {
                    String mappedId = requirementIdMap.get(r.getIncludedIn());
                    if (mappedId != null) {
                        r.setIncludedIn(mappedId);
                    }
                }

                for (IncludeElement includeElement : r.getInclude()) {
                    if (includeElement.getIncludeFrom() != null) {
                        String mappedId = requirementIdMap.get(includeElement.getIncludeFrom());
                        if (mappedId != null) {
                            includeElement.setIncludeFrom(mappedId);
                        }
                    }
                }
            }
        }

        return result;
    }
}
