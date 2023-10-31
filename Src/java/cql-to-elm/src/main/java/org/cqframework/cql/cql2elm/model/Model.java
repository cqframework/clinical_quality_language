package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.cql.model.*;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.util.*;

public class Model {
    public Model(ModelInfo modelInfo, ModelManager modelManager) throws ClassNotFoundException {
        info = modelInfo;
        index = new HashMap<>();
        nameIndex = new HashMap<>();
        classIndex = new HashMap<>();
        conversions = new ArrayList<>();
        contexts = new ArrayList<>();

        ModelImporter importer = new ModelImporter(info, modelManager);
        index = importer.getTypes();
        for (Conversion c : importer.getConversions()) {
            conversions.add(c);
        }

        for (ModelContext c : importer.getContexts()) {
            contexts.add(c);
        }

        defaultContext = importer.getDefaultContextName();

        for (DataType t : index.values()) {
            if (t instanceof ClassType && ((ClassType)t).getLabel() != null) {
                classIndex.put(casify(((ClassType)t).getLabel()), (ClassType)t);
            }

            if (t instanceof NamedType) {
                nameIndex.put(casify(((NamedType)t).getSimpleName()), t);
            }
        }
    }

    private ModelInfo info;
    public ModelInfo getModelInfo() { return info; }

    private Map<String, DataType> index;
    private Map<String, ClassType> classIndex;
    private Map<String, DataType> nameIndex;
    protected Map<String, DataType> getNameIndex() {
        return nameIndex;
    }
    private List<Conversion> conversions;
    private List<ModelContext> contexts;
    private String defaultContext;
    public String getDefaultContext() {
        return defaultContext;
    }

    public Iterable<Conversion> getConversions() {
        return conversions;
    }

    public DataType resolveTypeName(String typeName) {
        typeName = casify(typeName);
        DataType result = index.get(typeName);
        if (result == null) {
            result = nameIndex.get(typeName);
        }

        return result;
    }

    public ModelContext resolveContextName(String contextName) {
        return resolveContextName(contextName, true);
    }

    public ModelContext resolveContextName(String contextName, boolean mustResolve) {
        for (ModelContext context : contexts) {
            if (context.getName().equals(contextName)) {
                return context;
            }
        }

        // Resolve to a "default" context definition if the context name matches a type name exactly
        DataType contextType = resolveTypeName(contextName);
        if (contextType != null && contextType instanceof ClassType) {
            ClassType contextClassType = (ClassType)contextType;
            String keyName = null;
            for (ClassTypeElement cte : ((ClassType)contextType).getElements()) {
                if (cte.getName().equals("id")) {
                    keyName = cte.getName();
                    break;
                }
            }
            ModelContext modelContext = new ModelContext(contextName, (ClassType)contextType, keyName != null ? Arrays.asList(keyName) : null, null);
            return modelContext;
        }

        if (mustResolve) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve context name %s in model %s.", contextName, this.info.getName()));
        }

        return null;
    }

    public ClassType resolveLabel(String modelName, String label) {
        // LUKETODO:  why is this wrong?
//        if (! (info.isCaseSensitive() != null ? info.isCaseSensitive() : false)) {
//            if (! classIndex.containsKey(label)) {
//                classIndex.keySet()
//                        .stream()
//                        .filter(key -> key.equalsIgnoreCase(label))
//                        .findFirst()
//                        .ifPresent(actualKey -> {
//                            throw new IllegalArgumentException(String.format("Invalid case for library: %s and %s (should be %s)", modelName, label, actualKey));
//                        });
//            }
//        }

        return classIndex.get(casify(label));
    }

    // TODO: LD: Do we actually support this concept of cane insensitivity?
    private String casify(String typeName) {
        return (this.info.isCaseSensitive() != null ? this.info.isCaseSensitive() : false) ? typeName.toLowerCase() : typeName;
    }

    private DataType internalResolveTypeName(String typeName, Model systemModel) {
        DataType result = resolveTypeName(typeName);
        if (result == null) {
            result = systemModel.resolveTypeName(typeName);
            if (result == null) {
                // ERROR:
                throw new IllegalArgumentException(String.format("Could not resolve type name %s in model %s.",
                        typeName, info.getName()));
            }
        }

        return result;
    }
}
