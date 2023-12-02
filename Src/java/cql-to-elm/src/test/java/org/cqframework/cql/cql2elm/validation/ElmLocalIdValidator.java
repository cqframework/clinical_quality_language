package org.cqframework.cql.cql2elm.validation;

import java.util.List;

import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.visiting.ElmBaseLibraryVisitor;
import org.hl7.elm.r1.Element;

/**
 * Checks that all elements in a library have a localId.
 */
public class ElmLocalIdValidator extends ElmBaseLibraryVisitor<Boolean, List<MissingIdDescription>>{


    @Override
    protected Boolean defaultResult(Trackable elm, List<MissingIdDescription> context) {
        if (!(elm instanceof Element)) {
            return true;
        }

        Element element = (Element)elm;
        if (element.getLocalId() == null) {
            context.add(new MissingIdDescription(element));
            return false;
        }

        return true;
    }

    @Override
    protected Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
        return aggregate && nextResult;
    }
}
