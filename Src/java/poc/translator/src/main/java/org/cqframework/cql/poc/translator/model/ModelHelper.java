package org.cqframework.cql.poc.translator.model;

import com.sun.istack.internal.NotNull;
import org.hl7.elm_modelinfo.r1.ClassInfo;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by Bryn on 8/19/2014.
 */
public class ModelHelper {
    public ModelHelper(@NotNull ModelInfo info) throws ClassNotFoundException {
        _info = info;
        _index = new HashMap<String, ClassDetail>();

        for (int i = 0; i < _info.getClassInfo().size(); i++) {
            ClassInfo classInfo = _info.getClassInfo().get(i);
            ClassDetail classDetail = new ClassDetail();
            classDetail.setClassInfo(classInfo);
            classDetail.setModelClass(Class.forName("org.hl7.fhir." + classInfo.getName()));
            _index.put(
                    computeKey(
                            classInfo.getOccurrenceAxis(),
                            classInfo.getTopicAxis(),
                            classInfo.getModalityAxis()),
                    classDetail);
        }
    }

    private ModelInfo _info;
    public ModelInfo getModelInfo() { return _info; }

    private HashMap<String, ClassDetail> _index;

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
        return _index.get(computeKey(occurrence, topic, modality));
    }
}
