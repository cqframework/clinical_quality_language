package org.cqframework.cql.elm.visiting;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hl7.elm.r1.ByDirection;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Sort;
import org.hl7.elm.r1.SortByItem;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:abstractclassname")
class BaseElmVisitorTest {

    @Test
    void sortByVisited() {
        // set up visitor that returns true if it visits a SortByItem
        var sortByFinder = new BaseElmVisitor<Boolean, Void>() {
            @Override
            public Boolean defaultResult(Element t, Void context) {
                if (t instanceof SortByItem) {
                    return true;
                }

                return false;
            }

            @Override
            public Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
                return aggregate || nextResult;
            }
        };

        var sort = new Sort();
        assertFalse(sortByFinder.visitSort(sort, null));

        sort.getBy().add(new ByDirection());
        assertTrue(sortByFinder.visitSort(sort, null));
    }
}
