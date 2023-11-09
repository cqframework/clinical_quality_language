package org.opencds.cqf.cql.engine.model;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CachingModelResolverDecorator implements ModelResolver {
  private static Map<String, Map<String, Map<String, Optional<Object>>>> perPackageContextResolutions = new ConcurrentHashMap<>();
  private static Map<String, Map<String, Optional<Class<?>>>> perPackageTypeResolutionsByTypeName = new ConcurrentHashMap<>();
  private static Map<String, Map<Class<?>, Optional<Class<?>>>> perPackageTypeResolutionsByClass = new ConcurrentHashMap<>();

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
    if (contextType == null) {
      return null;
    }

    for (var pn : this.getPackageNames()) {
      var packageContextResolutions = perPackageContextResolutions.computeIfAbsent(pn, p -> new ConcurrentHashMap<>());

      var contextTypeResolutions = packageContextResolutions
          .computeIfAbsent(contextType, c -> new ConcurrentHashMap<>());

      var result = contextTypeResolutions
          .computeIfAbsent(targetType, t -> Optional.ofNullable(this.innerResolver.getContextPath(contextType, t)));

      if (result.isPresent()) {
        return result.get();
      }
    }

    return null;
  }

  @Override
  public Class<?> resolveType(String typeName) {
    if (typeName == null) {
      return null;
    }

    for (var pn : this.getPackageNames()) {
      var packageTypeResolutions = perPackageTypeResolutionsByTypeName
          .computeIfAbsent(pn, p -> new ConcurrentHashMap<>());

      var result = packageTypeResolutions
          .computeIfAbsent(typeName, t -> Optional.ofNullable(this.innerResolver.resolveType(t)));

      if (result.isPresent()) {
        return result.get();
      }
    }

    return null;
  }

  @Override
  public Class<?> resolveType(Object value) {
    if (value == null) {
      return null;
    }

    Class<?> valueClass = value.getClass();
    for (var pn : this.getPackageNames()) {
      var packageTypeResolutions = perPackageTypeResolutionsByClass
          .computeIfAbsent(pn, p -> new ConcurrentHashMap<>());

      var result = packageTypeResolutions
          .computeIfAbsent(valueClass, t -> Optional.ofNullable(this.innerResolver.resolveType(value)));

      if (result.isPresent()) {
        return result.get();
      }
    }

    return null;
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
  public String resolveId(Object target) {
    return innerResolver.resolveId(target);
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
