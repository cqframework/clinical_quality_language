package org.opencds.cqf.cql.engine.execution;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.VersionedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * There are at least two types of data that need to be cached, some that is context dependent, like expression results
 * (and thus can be invalidated during the course of evaluation) and some that is not, like Function resolutions (and thus
 * can be cached for the entire duration of the evaluation).
 */
public class Cache {

    private static final Logger log = LoggerFactory.getLogger(Cache.class);
    private boolean enableExpressionCache = false;

    private static final AtomicLong cacheExpressionRemovalCounter = new AtomicLong(0);
    private static final AtomicLong cacheLibraryExpressionHashMapRemovalCounter = new AtomicLong(0);

    private final Map<FunctionRef, FunctionDef> functionCache = new HashMap<>();

    private final Map<VersionedIdentifier, Map<String, ExpressionResult>> expressions =
            new LinkedHashMap<VersionedIdentifier, Map<String, ExpressionResult>>(10, 0.9f, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<VersionedIdentifier, Map<String, ExpressionResult>> eldestEntry) {
                    final boolean shouldRemoveOldestEntry = size() > 50;

                    if (shouldRemoveOldestEntry) {
                        final long cacheExpressionRemovalCount = cacheExpressionRemovalCounter.incrementAndGet();
                        log.info("Removing expressions cache entry for the {} th time", cacheExpressionRemovalCount);
                    }
                    return shouldRemoveOldestEntry;
                }
            };

    protected Map<String, ExpressionResult> constructLibraryExpressionHashMap() {
        return new LinkedHashMap<String, ExpressionResult>(15, 0.9f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ExpressionResult> eldestEntry) {
                final boolean shouldRemoveOldestEntry = size() > 300;
                if (shouldRemoveOldestEntry) {
                    final long cacheLibraryExpressionHashMapRemovalCount =
                            cacheLibraryExpressionHashMapRemovalCounter.incrementAndGet();
                    log.info(
                            "Removing constructLibraryExpressionHashMap entry for the {} th time",
                            cacheLibraryExpressionHashMapRemovalCount);
                }
                return shouldRemoveOldestEntry;
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
        return getExpressions().computeIfAbsent(libraryId, k -> constructLibraryExpressionHashMap());
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
        final Map<String, ExpressionResult> expressionCache = getExpressionCache(libraryId);
        return expressionCache.get(name);
    }

    private static String rightPad(Object o, int length) {
        if (o == null) {
            return null;
        }
        String s = o.toString();
        if (s.length() >= length) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public Map<FunctionRef, FunctionDef> getFunctionCache() {
        return functionCache;
    }

    public void cacheFunctionDef(FunctionRef functionRef, FunctionDef functionDef) {
        this.functionCache.put(functionRef, functionDef);
    }

    public FunctionDef getCachedFunctionDef(FunctionRef functionRef) {
        return this.functionCache.get(functionRef);
    }
}
