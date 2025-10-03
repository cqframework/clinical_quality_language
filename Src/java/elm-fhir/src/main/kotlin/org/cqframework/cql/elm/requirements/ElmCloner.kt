package org.cqframework.cql.elm.requirements

import kotlin.IllegalArgumentException
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.elm.r1.CodeFilterElement
import org.hl7.elm.r1.DateFilterElement
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.IncludeElement
import org.hl7.elm.r1.OtherFilterElement
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Retrieve

// TODO: Consider a cloning visitor?
object ElmCloner {
    @JvmStatic
    fun clone(elm: Retrieve): Retrieve {
        val clonedElm = Retrieve()
        cloneElement(elm, clonedElm)
        clonedElm.dataType = elm.dataType
        clonedElm.templateId = elm.templateId
        clonedElm.context = ElmCloner.clone(elm.context)
        clonedElm.contextProperty = elm.contextProperty
        clonedElm.contextSearch = elm.contextSearch
        // Do not clone includedIn, contains a reference that would need to be updated
        clonedElm.codeProperty = elm.codeProperty
        clonedElm.valueSetProperty = elm.valueSetProperty
        clonedElm.codeSearch = elm.codeSearch
        clonedElm.codeComparator = elm.codeComparator
        clonedElm.codes = ElmCloner.clone(elm.codes)

        clonedElm.dateProperty = elm.dateProperty
        clonedElm.dateSearch = elm.dateSearch
        clonedElm.dateLowProperty = elm.dateLowProperty
        clonedElm.dateHighProperty = elm.dateHighProperty
        clonedElm.dateRange = ElmCloner.clone(elm.dateRange)

        clonedElm.idProperty = elm.idProperty
        clonedElm.idSearch = elm.idSearch
        clonedElm.id = ElmCloner.clone(elm.id)

        for (codeFilterElement in elm.codeFilter) {
            clonedElm.codeFilter.add(ElmCloner.clone(codeFilterElement))
        }

        for (dateFilterElement in elm.dateFilter) {
            clonedElm.dateFilter.add(ElmCloner.clone(dateFilterElement))
        }

        for (otherFilterElement in elm.otherFilter) {
            clonedElm.otherFilter.add(ElmCloner.clone(otherFilterElement))
        }

        for (includeElement in elm.include) {
            clonedElm.include.add(ElmCloner.clone(includeElement))
        }

        return clonedElm
    }

    @JvmStatic
    fun clone(elm: Property): Property {
        val clonedElm = Property()
        cloneElement(elm, clonedElm)
        clonedElm.path = elm.path
        clonedElm.scope = elm.scope
        clonedElm.source = elm.source
        return clonedElm
    }

    fun clone(elm: Expression?): Expression? {
        return clone(elm as Element?) as Expression?
    }

    private fun cloneElement(elm: Element, clonedElm: Element) {
        clonedElm.localId = elm.localId
        clonedElm.locator = elm.locator
        clonedElm.resultTypeName = elm.resultTypeName
        clonedElm.resultTypeSpecifier = elm.resultTypeSpecifier
        clonedElm.resultType = elm.resultType
    }

    fun clone(elm: CodeFilterElement): CodeFilterElement {
        val clonedElm = CodeFilterElement()
        cloneElement(elm, clonedElm)
        clonedElm.property = elm.property
        clonedElm.valueSetProperty = elm.valueSetProperty
        clonedElm.search = elm.search
        clonedElm.comparator = elm.comparator
        clonedElm.value = ElmCloner.clone(elm.value)
        return clonedElm
    }

    fun clone(elm: DateFilterElement): DateFilterElement {
        val clonedElm = DateFilterElement()
        cloneElement(elm, clonedElm)
        clonedElm.property = elm.property
        clonedElm.search = elm.search
        clonedElm.value = ElmCloner.clone(elm.value)
        return clonedElm
    }

    fun clone(elm: OtherFilterElement): OtherFilterElement {
        val clonedElm = OtherFilterElement()
        cloneElement(elm, clonedElm)
        clonedElm.property = elm.property
        clonedElm.search = elm.search
        clonedElm.comparator = elm.comparator
        clonedElm.value = ElmCloner.clone(elm.value)
        return clonedElm
    }

    @JvmStatic
    fun clone(elm: IncludeElement): IncludeElement {
        val clonedElm = IncludeElement()
        cloneElement(elm, clonedElm)
        // Do not set includeFrom, contains a reference that would need to be updated
        clonedElm.relatedDataType = elm.relatedDataType
        clonedElm.relatedProperty = elm.relatedProperty
        clonedElm.relatedSearch = elm.relatedSearch
        clonedElm.withIsReverse(elm.isIsReverse())
        return clonedElm
    }

    fun clone(elm: Element?): Element? {
        if (elm == null) return null
        return when (elm) {
            is Retrieve -> ElmCloner.clone(elm)
            is CodeFilterElement -> ElmCloner.clone(elm)
            is DateFilterElement -> ElmCloner.clone(elm)
            is OtherFilterElement -> ElmCloner.clone(elm)
            is Property -> ElmCloner.clone(elm)
            is Expression -> elm
            else -> {
                throw IllegalArgumentException(
                    "clone of ${elm.javaClass.simpleName} not implemented"
                )
            }
        }
    }
}
