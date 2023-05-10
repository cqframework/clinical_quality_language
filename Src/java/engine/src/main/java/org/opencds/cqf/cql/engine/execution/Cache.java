package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cache {
    private static Logger logger = LoggerFactory.getLogger(Cache.class);

    private Map<String, List<State.FunctionDesc>> functionCache = new HashMap<>();

    private boolean enableExpressionCache = false;

    @SuppressWarnings("serial")
    private LinkedHashMap<VersionedIdentifier, LinkedHashMap<String, ExpressionResult>> expressions = new LinkedHashMap<VersionedIdentifier, LinkedHashMap<String, ExpressionResult>>(10, 0.9f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<VersionedIdentifier, LinkedHashMap<String, ExpressionResult>> eldestEntry) {
            return size() > 10;
        }
    };

    @SuppressWarnings("serial")
    protected LinkedHashMap<String, ExpressionResult> constructLibraryExpressionHashMap() {
        return new LinkedHashMap<String, ExpressionResult>(15, 0.9f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ExpressionResult> eldestEntry) {
                return size() > 15;
            }
        };
    }

    public Map<String, List<State.FunctionDesc>> getFunctionCache() {
        return functionCache;
    }

    public void setFunctionCache(Map<String, List<State.FunctionDesc>> functionCache) {
        this.functionCache = functionCache;
    }

    public LinkedHashMap<VersionedIdentifier, LinkedHashMap<String, ExpressionResult>> getExpressions() {
        return expressions;
    }

    public void setExpressions(LinkedHashMap<VersionedIdentifier, LinkedHashMap<String, ExpressionResult>> expressions) {
        this.expressions = expressions;
    }

    public void setExpressionCaching(boolean yayOrNay) {
        this.enableExpressionCache = yayOrNay;
    }

    protected Map<String, ExpressionResult> getCacheForLibrary(VersionedIdentifier libraryId) {
        return getExpressions()
                .computeIfAbsent(libraryId, k-> constructLibraryExpressionHashMap());
    }

    public boolean isExpressionCached(VersionedIdentifier libraryId, String name) {
        return getCacheForLibrary(libraryId).containsKey(name);
    }

    public boolean isExpressionCachingEnabled() {
        return this.enableExpressionCache;
    }

    public void cacheExpression(VersionedIdentifier libraryId, String name, ExpressionResult er) {
        getCacheForLibrary(libraryId).put(name, er);
    }

    public ExpressionResult getCachedExpression(VersionedIdentifier libraryId, String name) {
        return getCacheForLibrary(libraryId).get(name);
    }
}
