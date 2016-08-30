package org.cqframework.cql.cql2elm.model;

import org.antlr.v4.runtime.misc.NotNull;
import org.cqframework.cql.elm.tracking.*;
import org.hl7.elm_modelinfo.r1.ConversionInfo;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.util.*;

public class Model {
    public Model(@NotNull ModelInfo modelInfo, Model systemModel) throws ClassNotFoundException {
        info = modelInfo;
        index = new HashMap<>();
        nameIndex = new HashMap<>();
        classIndex = new HashMap<>();
        conversions = new ArrayList<>();

        ModelImporter importer = new ModelImporter(info, systemModel != null ? systemModel.nameIndex.values() : null);
        index = importer.getTypes();
        for (Conversion c : importer.getConversions()) {
            conversions.add(c);
        }

        for (DataType t : index.values()) {
            if (t instanceof ClassType) {
                classIndex.put(((ClassType)t).getLabel(), (ClassType)t);
            }

            if (t instanceof NamedType) {
                nameIndex.put(((NamedType)t).getSimpleName(), t);
            }
        }
    }

    private ModelInfo info;
    public ModelInfo getModelInfo() { return info; }

    private Map<String, DataType> index;
    private Map<String, ClassType> classIndex;
    private Map<String, DataType> nameIndex;
    private List<Conversion> conversions;

    public Iterable<Conversion> getConversions() {
        return conversions;
    }

    public DataType resolveTypeName(@NotNull String typeName) {
        DataType result = index.get(typeName);
        if (result == null) {
            result = nameIndex.get(typeName);
        }

        return result;
    }

    public ClassType resolveLabel(@NotNull String label) {
        return classIndex.get(label);
    }

    private DataType internalResolveTypeName(@NotNull String typeName, Model systemModel) {
        DataType result = resolveTypeName(typeName);
        if (result == null) {
            result = systemModel.resolveTypeName(typeName);
            if (result == null) {
                throw new IllegalArgumentException(String.format("Could not resolve type name %s in model %s.",
                        typeName, info.getName()));
            }
        }

        return result;
    }
}
