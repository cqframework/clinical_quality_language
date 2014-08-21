package org.cqframework.cql.poc.translator.model;

import org.antlr.v4.runtime.misc.NotNull;
import org.hl7.elm_modelinfo.r1.ClassInfo;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.util.HashMap;
import java.util.ListIterator;

public class ModelHelper {
    public ModelHelper(@NotNull ModelInfo modelInfo) throws ClassNotFoundException {
        info = modelInfo;
        index = new HashMap<String, ClassDetail>();

        for (int i = 0; i < info.getClassInfo().size(); i++) {
            ClassInfo classInfo = info.getClassInfo().get(i);
            ClassDetail classDetail = new ClassDetail();
            classDetail.setClassInfo(classInfo);
            classDetail.setModelClass(Class.forName("org.hl7.fhir." + classInfo.getName()));
            index.put(
                    computeKey(
                            classInfo.getOccurrenceAxis(),
                            classInfo.getTopicAxis(),
                            classInfo.getModalityAxis()),
                    classDetail);
        }
    }

    private ModelInfo info;
    public ModelInfo getModelInfo() { return info; }

    private HashMap<String, ClassDetail> index;

    private String computeKey(String occurrence, @NotNull String topic, String modality) {
        String key = topic;
        if (modality != null) {
            key += modality;
        }

        if (occurrence != null) {
            key = occurrence + key;
        }

        return key;
    }

    public ClassDetail getClassDetail(String occurrence, @NotNull String topic, String modality) {
        return index.get(computeKey(occurrence, topic, modality));
    }
}
