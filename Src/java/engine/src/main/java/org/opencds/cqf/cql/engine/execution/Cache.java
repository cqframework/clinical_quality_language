package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * There are at least two types of data that need to be cached, some that is context dependent, like expression results
 * (and thus can be invalidated during the course of evaluation) and some that is not, like Function resolutions (and thus
 * can be cache for the entire duration of the evaluation).
 */
public class Cache {

    private boolean enableExpressionCache = false;

    private Map<FunctionRef, FunctionDef> functionCache = new HashMap<>();

    @SuppressWarnings("serial")
    private Map<VersionedIdentifier, Map<String, ExpressionResult>> expressions = new LinkedHashMap<VersionedIdentifier, Map<String, ExpressionResult>>(10, 0.9f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<VersionedIdentifier, Map<String, ExpressionResult>> eldestEntry) {
            return size() > 10;
        }
    };

    @SuppressWarnings("serial")
    protected Map<String, ExpressionResult> constructLibraryExpressionHashMap() {
        return new LinkedHashMap<String, ExpressionResult>(15, 0.9f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ExpressionResult> eldestEntry) {
                return size() > 15;
            }
        };
    }

    public Map<VersionedIdentifier, Map<String, ExpressionResult>> getExpressions() {
        return expressions;
    }

    public void setExpressionCaching(boolean yayOrNay) {
        this.enableExpressionCache = yayOrNay;
    }

    protected Map<String, ExpressionResult> getExpressionCache(VersionedIdentifier libraryId) {
        return getExpressions()
                .computeIfAbsent(libraryId, k-> constructLibraryExpressionHashMap());
    }

    public boolean isExpressionCached(VersionedIdentifier libraryId, String name) {
        return getExpressionCache(libraryId).containsKey(name);
    }

    public boolean isExpressionCachingEnabled() {
        return this.enableExpressionCache;
    }

    public void cacheExpression(VersionedIdentifier libraryId, String name, ExpressionResult er) {
        getExpressionCache(libraryId).put(name, er);
    }

    public ExpressionResult getCachedExpression(VersionedIdentifier libraryId, String name) {
        return getExpressionCache(libraryId).get(name);
    }

    public Map<FunctionRef, FunctionDef> getFunctionCache() {
        return functionCache;
    }

    public void cacheFunctionDef(FunctionRef functionRef, FunctionDef functionDef) {
        this.functionCache.put(functionRef, functionDef);
    }

    public FunctionDef getCachedFunctionDef(FunctionRef functionRef) {
        return this.getCachedFunctionDef(functionRef);
    }
}
