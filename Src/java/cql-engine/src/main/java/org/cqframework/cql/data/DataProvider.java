package org.cqframework.cql.data;

import org.cqframework.cql.runtime.Interval;
import org.joda.time.Partial;
import org.cqframework.cql.runtime.Concept;

/**
 * Created by Bryn on 4/15/2016.
 */
public interface DataProvider {
    // TODO: Enable the use of valuesets, rather than requiring expansion to a list of codes
    Iterable<Object> retrieve(String context, String dataType, String templateId, String codePath, Iterable<Concept> codes,
            String datePath, String dateLowPath, String dateHighPath, Interval<Partial> dateRange);
}
