package org.cqframework.cql.cql2elm.model;

import org.antlr.v4.runtime.misc.NotNull;
import org.hl7.cql.model.*;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.util.*;

public class Model {
    public Model(@NotNull ModelInfo modelInfo, Model systemModel) throws ClassNotFoundException {
        info = modelInfo;
        index = new HashMap<>();
        nameIndex = new HashMap<>();
        classIndex = new HashMap<>();
        conversions = new ArrayList<>();
        contexts = new ArrayList<>();

        ModelImporter importer = new ModelImporter(info, systemModel != null ? systemModel.nameIndex.values() : null);
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
    private List<Conversion> conversions;
    private List<ModelContext> contexts;
    private String defaultContext;
    public String getDefaultContext() {
        return defaultContext;
    }

    public Iterable<Conversion> getConversions() {
        return conversions;
    }

    public DataType resolveTypeName(@NotNull String typeName) {
        typeName = casify(typeName);
        DataType result = index.get(typeName);
        if (result == null) {
            result = nameIndex.get(typeName);
        }

        return result;
    }

    public ModelContext resolveContextName(@NotNull String contextName) {
        for (ModelContext context : contexts) {
            if (context.getName().equals(contextName)) {
                return context;
            }
        }

        // ERROR:
        throw new IllegalArgumentException(String.format("Could not resolve context name %s in model %s.", contextName, this.info.getName()));
    }

    public ClassType resolveLabel(@NotNull String label) {
        return classIndex.get(casify(label));
    }

    private String casify(String typeName) {
        return (this.info.isCaseSensitive() != null ? this.info.isCaseSensitive() : false) ? typeName.toLowerCase() : typeName;
    }

    private DataType internalResolveTypeName(@NotNull String typeName, Model systemModel) {
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
