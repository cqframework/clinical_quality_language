package org.cqframework.cql.elm.visiting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.elm.r1.ByDirection;
import org.hl7.elm.r1.Sort;
import org.hl7.elm.r1.SortByItem;
import org.junit.Test;

public class ElmBaseVisitorTest {

    @Test
    public void sortByVisited() {

        // set up visitor that returns true if it visits a SortByItem
        var sortByFinder = new ElmBaseVisitor<Boolean, Void>() {
            @Override
            public Boolean defaultResult(Trackable t, Void context) {
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
