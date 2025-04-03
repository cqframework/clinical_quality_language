package org.cqframework.cql.elm.evaluating

import kotlin.jvm.JvmStatic
import nl.adaptivity.xmlutil.QName
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.TypeSpecifier

@Suppress("TooManyFunctions")
object SimpleElmEvaluator {
    private val engine = SimpleElmEngine()

    fun simpleElmEngine(): SimpleElmEngine {
        return engine
    }

    fun booleansEqual(left: Expression?, right: Expression?): Boolean {
        return engine.booleansEqual(left, right)
    }

    fun integersEqual(left: Expression?, right: Expression?): Boolean {
        return engine.integersEqual(left, right)
    }

    fun decimalsEqual(left: Expression?, right: Expression?): Boolean {
        return engine.decimalsEqual(left, right)
    }

    @JvmStatic
    fun stringsEqual(left: Expression?, right: Expression?): Boolean {
        return engine.stringsEqual(left, right)
    }

    @JvmStatic
    fun stringsEqual(left: String?, right: String?): Boolean {
        return engine.stringsEqual(left, right)
    }

    fun dateTimesEqual(left: Expression?, right: Expression?): Boolean {
        return engine.dateTimesEqual(left, right)
    }

    @JvmStatic
    fun dateRangesEqual(left: Expression?, right: Expression?): Boolean {
        return engine.dateRangesEqual(left, right)
    }

    @JvmStatic
    fun codesEqual(left: Expression?, right: Expression?): Boolean {
        return engine.codesEqual(left, right)
    }

    @JvmStatic
    fun typeSpecifiersEqual(left: TypeSpecifier?, right: TypeSpecifier?): Boolean {
        return engine.typeSpecifiersEqual(left, right)
    }

    @JvmStatic
    fun qnamesEqual(left: QName?, right: QName?): Boolean {
        return engine.qnamesEqual(left, right)
    }
}
