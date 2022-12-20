package org.opencds.cqf.cql.engine.retrieve;

import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

public interface RetrieveProvider {
    Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType, String templateId, String codePath,
              Iterable<Code> codes, String valueSet, String datePath, String dateLowPath, String dateHighPath,
			  Interval dateRange);
}
