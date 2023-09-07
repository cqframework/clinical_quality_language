package org.opencds.cqf.cql.engine.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachingModelResolverDecorator implements ModelResolver {
  private static Map<String, Map<String, Map<String, Object>>> perPackageContextResolutions =
      new ConcurrentHashMap<>();
  private static Map<String, Map<String, Class<?>>> perPackageTypeResolutionsByTypeName =
      new ConcurrentHashMap<>();
  private static Map<String, Map<Class<?>, Class<?>>> perPackageTypeResolutionsByClass =
      new ConcurrentHashMap<>();

  private ModelResolver innerResolver;

  public CachingModelResolverDecorator(ModelResolver modelResolver) {
    this.innerResolver = modelResolver;
  }

  @Override
  @SuppressWarnings("deprecation")
  public String getPackageName() {
    return this.innerResolver.getPackageName();
  }

  @Override
  @SuppressWarnings("deprecation")
  public void setPackageName(String packageName) {
    this.innerResolver.setPackageName(packageName);
  }

  @Override
  public Object resolvePath(Object target, String path) {
    return this.innerResolver.resolvePath(target, path);
  }

  @Override
  public Object getContextPath(String contextType, String targetType) {
    if (!perPackageContextResolutions.containsKey(this.getPackageName())) {
      perPackageContextResolutions.put(this.getPackageName(), new ConcurrentHashMap<>());
    }

    Map<String, Map<String, Object>> packageContextResolutions =
        perPackageContextResolutions.get(this.getPackageName());

    var contextTypeResolutions = packageContextResolutions
      .computeIfAbsent(contextType, c -> new ConcurrentHashMap<>());
    return contextTypeResolutions
      .computeIfAbsent(targetType, t -> this.innerResolver.getContextPath(contextType, t));
  }

  @Override
  public Class<?> resolveType(String typeName) {
    var packageTypeResolutions = perPackageTypeResolutionsByTypeName
      .computeIfAbsent(this.getPackageName(), p -> new ConcurrentHashMap<>());
    return packageTypeResolutions
      .computeIfAbsent(typeName, t -> this.innerResolver.resolveType(t));
  }

  @Override
  public Class<?> resolveType(Object value) {
    if (!perPackageTypeResolutionsByClass.containsKey(this.getPackageName())) {
      perPackageTypeResolutionsByClass.put(this.getPackageName(), new ConcurrentHashMap<>());
    }

    Map<Class<?>, Class<?>> packageTypeResolutions =
        perPackageTypeResolutionsByClass.get(this.getPackageName());

    Class<?> valueClass = value.getClass();
    return packageTypeResolutions
      .computeIfAbsent(valueClass, v -> this.innerResolver.resolveType(v));
  }

  @Override
  public Object createInstance(String typeName) {
    return this.innerResolver.createInstance(typeName);
  }

  @Override
  public void setValue(Object target, String path, Object value) {
    this.innerResolver.setValue(target, path, value);
  }

  @Override
  public Boolean objectEqual(Object left, Object right) {
    return this.innerResolver.objectEqual(left, right);
  }

  @Override
  public Boolean objectEquivalent(Object left, Object right) {
    return this.innerResolver.objectEquivalent(left, right);
  }

  @Override
  public Boolean is(Object value, Class<?> type) {
    return this.innerResolver.is(value, type);
  }

  @Override
  public Object as(Object value, Class<?> type, boolean isStrict) {
    return this.innerResolver.as(value, type, isStrict);
  }

  public ModelResolver getInnerResolver() {
    return this.innerResolver;
  }
}
