package org.opencds.cqf.cql.engine.data;

import java.util.List;

import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

public class CompositeDataProvider implements DataProvider {

    protected ModelResolver modelResolver;
    protected RetrieveProvider retrieveProvider;

    public CompositeDataProvider(ModelResolver modelResolver, RetrieveProvider retrieveProvider) {
        this.modelResolver = modelResolver;
        this.retrieveProvider = retrieveProvider;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getPackageName() {
        return this.modelResolver.getPackageName();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setPackageName(String packageName) {
        this.modelResolver.setPackageName(packageName);
    }

    @Override
    public List<String> getPackageNames() {
        return this.modelResolver.getPackageNames();
    }

    @Override
    public void setPackageNames(List<String> packageNames) {
        this.modelResolver.setPackageNames(packageNames);
    }

    @Override
    public Object resolvePath(Object target, String path) {
        return this.modelResolver.resolvePath(target, path);
    }

    @Override
    public Object getContextPath(String contextType, String targetType) {
        return this.modelResolver.getContextPath(contextType, targetType);
    }

    @Override
    public Class<?> resolveType(String typeName) {
        return this.modelResolver.resolveType(typeName);
    }

    @Override
    public Class<?> resolveType(Object value) {
        return this.modelResolver.resolveType(value);
	}

    @Override
    public Boolean is(Object value, Class<?> type) {
        return this.modelResolver.is(value, type);
    }

    @Override
    public Object as(Object value, Class<?> type, boolean isStrict) {
        return this.modelResolver.as(value, type, isStrict);
    }

    @Override
    public Object createInstance(String typeName) {
        return this.modelResolver.createInstance(typeName);
    }

    @Override
    public void setValue(Object target, String path, Object value) {
        this.modelResolver.setValue(target, path, value);
    }

    @Override
    public Boolean objectEqual(Object left, Object right) {
        return this.modelResolver.objectEqual(left, right);
    }

    @Override
    public Boolean objectEquivalent(Object left, Object right) {
        return this.modelResolver.objectEquivalent(left, right);
    }

    @Override
    public String resolveId(Object target) {
        return this.modelResolver.resolveId(target);
    }

    @Override
    public Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType,
            String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath,
            String dateLowPath, String dateHighPath, Interval dateRange) {
        return this.retrieveProvider.retrieve(context, contextPath, contextValue, dataType, templateId, codePath, codes, valueSet, datePath, dateLowPath, dateHighPath, dateRange);
    }
}
