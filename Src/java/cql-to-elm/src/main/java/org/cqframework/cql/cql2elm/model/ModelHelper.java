package org.cqframework.cql.cql2elm.model;

import org.antlr.v4.runtime.misc.NotNull;
import org.cqframework.cql.elm.tracking.ClassType;
import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.SimpleType;
import org.cqframework.cql.elm.tracking.TupleType;
import org.hl7.elm_modelinfo.r1.ClassInfo;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.TypeInfo;

import java.util.HashMap;
import java.util.Map;

public class ModelHelper {
    public ModelHelper(@NotNull ModelInfo modelInfo, ModelHelper systemModel) throws ClassNotFoundException {
        info = modelInfo;
        index = new HashMap<>();
        nameIndex = new HashMap<>();
        classIndex = new HashMap<>();

        ModelImporter importer = new ModelImporter(info, systemModel != null ? systemModel.nameIndex.values() : null);
        index = importer.getTypes();

        for (DataType t : index.values()) {
            if (t instanceof ClassType) {
                classIndex.put(((ClassType)t).getTopic(), (ClassType)t);
            }

            if (t instanceof SimpleType) {
                nameIndex.put(((SimpleType)t).getSimpleName(), t);
            }

            if (t instanceof TupleType) {
                nameIndex.put(((TupleType)t).getSimpleName(), t);
            }
        }
    }

    private ModelInfo info;
    public ModelInfo getModelInfo() { return info; }

    private Map<String, DataType> index;
    private Map<String, ClassType> classIndex;
    private Map<String, DataType> nameIndex;

    public DataType resolveTypeName(@NotNull String typeName) {
        DataType result = index.get(typeName);
        if (result == null) {
            result = nameIndex.get(typeName);
        }

        return result;
    }

    public ClassType resolveTopic(@NotNull String topic) {
        return classIndex.get(topic);
    }
}
