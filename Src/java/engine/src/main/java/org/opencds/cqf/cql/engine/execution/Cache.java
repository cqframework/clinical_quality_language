package org.opencds.cqf.cql.engine.execution;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.TypeSpecifier;
import org.hl7.elm.r1.VersionedIdentifier;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy2;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy2;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;

/**
 * There are at least two types of data that need to be cached, some that is context dependent, like expression results
 * (and thus can be invalidated during the course of evaluation) and some that is not, like Function resolutions (and thus
 * can be cache for the entire duration of the evaluation).
 */
public class Cache {

    private boolean enableExpressionCache = false;

    static class RapidFunctionRef {
        private final FunctionRef functionRef;

        RapidFunctionRef(FunctionRef functionRef) {
            this.functionRef = functionRef;
        }

        @Override
        public int hashCode() {
            int currentHashCode = 31;
            final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.getInstance();
            {
                List<TypeSpecifier> theSignature;
                theSignature = (((this.functionRef.getSignature() != null)
                                && (!this.functionRef.getSignature().isEmpty()))
                        ? this.functionRef.getSignature()
                        : null);
                currentHashCode = strategy.hashCode(
                        LocatorUtils.property(null, "signature", theSignature),
                        currentHashCode,
                        theSignature,
                        ((this.getFunctionRef().getSignature() != null)
                                && (!this.getFunctionRef().getSignature().isEmpty())));
            }

            return currentHashCode;
        }

        public boolean equals(
                ObjectLocator thisLocator,
                ObjectLocator thatLocator,
                RapidFunctionRef object,
                EqualsStrategy2 strategy) {
            if (object == null) {
                return false;
            }

            if (this == object) {
                return true;
            }

            {
                List<TypeSpecifier> lhsSignature;
                lhsSignature = (((this.functionRef.getSignature() != null)
                                && (!this.functionRef.getSignature().isEmpty()))
                        ? this.functionRef.getSignature()
                        : null);
                List<TypeSpecifier> rhsSignature;
                rhsSignature = (((object.functionRef.getSignature() != null)
                                && (!object.functionRef.getSignature().isEmpty()))
                        ? object.functionRef.getSignature()
                        : null);
                return strategy.equals(
                        LocatorUtils.property(thisLocator, "signature", lhsSignature),
                        LocatorUtils.property(thatLocator, "signature", rhsSignature),
                        lhsSignature,
                        rhsSignature,
                        ((this.functionRef.getSignature() != null)
                                && (!this.functionRef.getSignature().isEmpty())),
                        ((object.functionRef.getSignature() != null)
                                && (!object.functionRef.getSignature().isEmpty())));
            }
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof RapidFunctionRef)) {
                return false;
            }

            final EqualsStrategy2 strategy = JAXBEqualsStrategy.getInstance();
            return equals(null, null, (RapidFunctionRef) object, strategy);
        }

        public FunctionRef getFunctionRef() {
            return functionRef;
        }
    }

    private final Map<RapidFunctionRef, FunctionDef> functionCache = new HashMap<>();

    private final Map<VersionedIdentifier, Map<String, ExpressionResult>> expressions =
            new LinkedHashMap<VersionedIdentifier, Map<String, ExpressionResult>>(10, 0.9f, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<VersionedIdentifier, Map<String, ExpressionResult>> eldestEntry) {
                    return size() > 50;
                }
            };

    protected Map<String, ExpressionResult> constructLibraryExpressionHashMap() {
        return new LinkedHashMap<String, ExpressionResult>(15, 0.9f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ExpressionResult> eldestEntry) {
                return size() > 300;
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
        return getExpressionCache(libraryId).get(name);
    }

    public boolean containsFunctionRef(FunctionRef functionRef) {
        var rfr = new RapidFunctionRef(functionRef);
        return this.functionCache.containsKey(rfr);
    }

    public void cacheFunctionDef(FunctionRef functionRef, FunctionDef functionDef) {
        var rfr = new RapidFunctionRef(functionRef);
        this.functionCache.put(rfr, functionDef);
    }

    public FunctionDef getCachedFunctionDef(FunctionRef functionRef) {
        var rfr = new RapidFunctionRef(functionRef);
        return this.functionCache.get(rfr);
    }
}
