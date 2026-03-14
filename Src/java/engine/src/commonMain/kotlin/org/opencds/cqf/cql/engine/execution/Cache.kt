package org.opencds.cqf.cql.engine.execution

import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.util.createLinkedHashMap

/**
 * There are at least two types of data that need to be cached, some that is context dependent, like
 * expression results (and thus can be invalidated during the course of evaluation) and some that is
 * not, like Function resolutions (and thus can be cached for the entire duration of the
 * evaluation).
 */
@Suppress("MagicNumber")
class Cache {
    var isExpressionCachingEnabled: Boolean = false
        private set

    val functionCache = mutableMapOf<FunctionRef?, FunctionDef?>()

    val expressions =
        createLinkedHashMap<VersionedIdentifier?, MutableMap<String?, ExpressionResult?>?>(
            10,
            0.9f,
            true,
            50,
        )

    protected fun constructLibraryExpressionHashMap(): MutableMap<String?, ExpressionResult?> {

        return createLinkedHashMap(15, 0.9f, true, 300)
    }

    fun setExpressionCaching(yayOrNay: Boolean) {
        this.isExpressionCachingEnabled = yayOrNay
    }

    protected fun getExpressionCache(
        libraryId: VersionedIdentifier?
    ): MutableMap<String?, ExpressionResult?>? {
        return this.expressions.getOrPut(libraryId) { constructLibraryExpressionHashMap() }
    }

    fun isExpressionCached(libraryId: VersionedIdentifier?, name: String?): Boolean {
        return getExpressionCache(libraryId)!!.containsKey(name)
    }

    fun cacheExpression(libraryId: VersionedIdentifier?, name: String?, er: ExpressionResult?) {
        getExpressionCache(libraryId)!![name] = er
    }

    fun getCachedExpression(libraryId: VersionedIdentifier?, name: String?): ExpressionResult? {
        return getExpressionCache(libraryId)!![name]
    }

    fun cacheFunctionDef(functionRef: FunctionRef?, functionDef: FunctionDef?) {
        this.functionCache[functionRef] = functionDef
    }

    fun getCachedFunctionDef(functionRef: FunctionRef?): FunctionDef? {
        return this.functionCache[functionRef]
    }
}
