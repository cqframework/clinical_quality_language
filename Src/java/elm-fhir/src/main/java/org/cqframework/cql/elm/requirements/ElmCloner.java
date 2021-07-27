package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.*;

// TODO: Consider a cloning visitor?
public class ElmCloner {
    public static Retrieve clone(Retrieve elm) {
        Retrieve clonedElm = new Retrieve();
        cloneElement(elm, clonedElm);
        clonedElm.setDataType(elm.getDataType());
        clonedElm.setTemplateId(elm.getTemplateId());
        clonedElm.setContext(clone(elm.getContext()));
        clonedElm.setContextProperty(elm.getContextProperty());
        clonedElm.setCodeProperty(elm.getCodeProperty());
        clonedElm.setValueSetProperty(elm.getValueSetProperty());
        clonedElm.setCodeSearch(elm.getCodeSearch());
        clonedElm.setCodeComparator(elm.getCodeComparator());
        clonedElm.setCodes(clone(elm.getCodes()));

        clonedElm.setDateProperty(elm.getDateProperty());
        clonedElm.setDateSearch(elm.getDateSearch());
        clonedElm.setDateLowProperty(elm.getDateLowProperty());
        clonedElm.setDateHighProperty(elm.getDateHighProperty());
        clonedElm.setDateRange(clone(elm.getDateRange()));

        clonedElm.setIdProperty(elm.getIdProperty());
        clonedElm.setIdSearch(elm.getIdSearch());
        clonedElm.setId(clone(elm.getId()));

        for (CodeFilterElement codeFilterElement : elm.getCodeFilter()) {
            clonedElm.getCodeFilter().add(clone(codeFilterElement));
        }

        for (DateFilterElement dateFilterElement : elm.getDateFilter()) {
            clonedElm.getDateFilter().add(clone(dateFilterElement));
        }

        for (OtherFilterElement otherFilterElement : elm.getOtherFilter()) {
            clonedElm.getOtherFilter().add(clone(otherFilterElement));
        }

        for (IncludeElement includeElement : elm.getInclude()) {
            clonedElm.getInclude().add(clone(includeElement));
        }

        return clonedElm;
    }

    public static Property clone(Property elm) {
        Property clonedElm = new Property();
        cloneElement(elm, clonedElm);
        clonedElm.setPath(elm.getPath());
        clonedElm.setScope(elm.getScope());
        clonedElm.setSource(elm.getSource());
        return clonedElm;
    }

    public static Expression clone(Expression elm) {
        return (Expression)clone((Element)elm);
    }

    private static void cloneElement(Element elm, Element clonedElm) {
        clonedElm.setLocalId(elm.getLocalId());
        clonedElm.setLocator(elm.getLocator());
        clonedElm.setResultTypeName(elm.getResultTypeName());
        clonedElm.setResultTypeSpecifier(elm.getResultTypeSpecifier());
        clonedElm.setResultType(elm.getResultType());
    }

    public static CodeFilterElement clone(CodeFilterElement elm) {
        CodeFilterElement clonedElm = new CodeFilterElement();
        cloneElement(elm, clonedElm);
        clonedElm.setProperty(elm.getProperty());
        clonedElm.setValueSetProperty(elm.getValueSetProperty());
        clonedElm.setSearch(elm.getSearch());
        clonedElm.setComparator(elm.getComparator());
        clonedElm.setValue(clone(elm.getValue()));
        return clonedElm;
    }

    public static DateFilterElement clone(DateFilterElement elm) {
        DateFilterElement clonedElm = new DateFilterElement();
        cloneElement(elm, clonedElm);
        clonedElm.setProperty(elm.getProperty());
        clonedElm.setSearch(elm.getSearch());
        clonedElm.setValue(clone(elm.getValue()));
        return clonedElm;
    }

    public static OtherFilterElement clone(OtherFilterElement elm) {
        OtherFilterElement clonedElm = new OtherFilterElement();
        cloneElement(elm, clonedElm);
        clonedElm.setProperty(elm.getProperty());
        clonedElm.setSearch(elm.getSearch());
        clonedElm.setComparator(elm.getComparator());
        clonedElm.setValue(clone(elm.getValue()));
        return clonedElm;
    }

    public static IncludeElement clone(IncludeElement elm) {
        IncludeElement clonedElm = new IncludeElement();
        cloneElement(elm, clonedElm);
        clonedElm.setRelatedDataType(elm.getRelatedDataType());
        clonedElm.setRelatedProperty(elm.getRelatedProperty());
        clonedElm.setRelatedSearch(elm.getRelatedSearch());
        clonedElm.setIsReverse(elm.isIsReverse());
        return clonedElm;
    }

    public static Element clone(Element elm) {
        if (elm == null) return null;
        if (elm instanceof Retrieve) return clone((Retrieve)elm);
        else if (elm instanceof CodeFilterElement) return clone((CodeFilterElement)elm);
        else if (elm instanceof DateFilterElement) return clone((DateFilterElement)elm);
        else if (elm instanceof OtherFilterElement) return clone((OtherFilterElement)elm);
        // TODO: Cloning of expressions is not necessary at this point because there would be no impact
        // If that assumption changes, this will need to be built out
        else if (elm instanceof Property) return clone((Property)elm);
        else if (elm instanceof Expression) return elm;
        else {
            throw new IllegalArgumentException(String.format("clone of %s not implemented", elm.getClass().getSimpleName()));
        }
    }
}
