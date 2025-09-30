package org.cqframework.cql.elm.requirements

import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.codesEqual
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.dateRangesEqual
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.stringsEqual
import org.hl7.elm.r1.CodeFilterElement
import org.hl7.elm.r1.DateFilterElement
import org.hl7.elm.r1.IncludeElement
import org.hl7.elm.r1.OtherFilterElement
import org.hl7.elm.r1.Retrieve

class ComparableElmRequirement(val requirement: ElmRequirement) {

    override fun hashCode(): Int {
        // Hashing only by the type/profile
        if (requirement.getElement() is Retrieve) {
            val retrieve = this.requirement.getElement() as Retrieve
            val typeUri =
                if (retrieve.templateId != null) retrieve.templateId
                else
                    (if (retrieve.dataType != null && retrieve.dataType!!.localPart != null)
                        retrieve.dataType!!.localPart
                    else null)
            if (typeUri != null) {
                return typeUri.hashCode()
            }
        }

        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ComparableElmRequirement) {
            return requirementsEquivalent(requirement, other.requirement)
        }

        return false
    }

    companion object {
        @JvmStatic
        fun codeFilterElementsEqual(left: CodeFilterElement, right: CodeFilterElement): Boolean {
            return stringsEqual(left.property, right.property) &&
                stringsEqual(left.search, right.search) &&
                stringsEqual(left.comparator, right.comparator) &&
                stringsEqual(left.valueSetProperty, right.valueSetProperty) &&
                codesEqual(left.value, right.value)
        }

        @JvmStatic
        fun hasCodeFilter(left: Iterable<CodeFilterElement>, right: CodeFilterElement): Boolean {
            for (cfe in left) {
                if (codeFilterElementsEqual(cfe, right)) {
                    return true
                }
            }

            return false
        }

        @JvmStatic
        fun codeFiltersEqual(
            left: Iterable<CodeFilterElement>,
            right: Iterable<CodeFilterElement>,
        ): Boolean {
            // TODO: Don't rely on order dependence here...
            val leftIterator = left.iterator()
            val rightIterator = right.iterator()

            while (leftIterator.hasNext()) {
                val leftElement = leftIterator.next()

                if (!rightIterator.hasNext()) {
                    return false
                }

                val rightElement = rightIterator.next()

                if (!codeFilterElementsEqual(leftElement, rightElement)) {
                    return false
                }
            }

            return !rightIterator.hasNext()
        }

        @JvmStatic
        fun dateFilterElementsEqual(left: DateFilterElement, right: DateFilterElement): Boolean {
            return stringsEqual(left.property, right.property) &&
                stringsEqual(left.lowProperty, right.lowProperty) &&
                stringsEqual(left.highProperty, right.highProperty) &&
                stringsEqual(left.search, right.search) &&
                dateRangesEqual(left.value, right.value)
        }

        @JvmStatic
        fun hasDateFilter(left: Iterable<DateFilterElement>, right: DateFilterElement): Boolean {
            for (dfe in left) {
                if (dateFilterElementsEqual(dfe, right)) {
                    return true
                }
            }

            return false
        }

        @JvmStatic
        fun dateFiltersEqual(
            left: Iterable<DateFilterElement>,
            right: Iterable<DateFilterElement>,
        ): Boolean {
            // TODO: Don't rely on order dependence here...
            val leftIterator = left.iterator()
            val rightIterator = right.iterator()

            while (leftIterator.hasNext()) {
                val leftElement = leftIterator.next()

                if (!rightIterator.hasNext()) {
                    return false
                }

                val rightElement = rightIterator.next()

                if (!dateFilterElementsEqual(leftElement, rightElement)) {
                    return false
                }
            }

            return !rightIterator.hasNext()
        }

        @JvmStatic
        // TODO: Handle types other than strings
        fun otherFilterElementsEqual(left: OtherFilterElement, right: OtherFilterElement): Boolean {
            return stringsEqual(left.property, right.property) &&
                stringsEqual(left.search, right.search) &&
                stringsEqual(left.comparator, right.comparator) &&
                stringsEqual(left.value, right.value)
        }

        @JvmStatic
        fun hasOtherFilter(left: Iterable<OtherFilterElement>, right: OtherFilterElement): Boolean {
            for (ofe in left) {
                if (otherFilterElementsEqual(ofe, right)) {
                    return true
                }
            }

            return false
        }

        @JvmStatic
        fun otherFiltersEqual(
            left: Iterable<OtherFilterElement>,
            right: Iterable<OtherFilterElement>,
        ): Boolean {
            // TODO: Don't rely on order dependence here...
            val leftIterator = left.iterator()
            val rightIterator = right.iterator()

            while (leftIterator.hasNext()) {
                val leftElement = leftIterator.next()

                if (!rightIterator.hasNext()) {
                    return false
                }

                val rightElement = rightIterator.next()

                if (!otherFilterElementsEqual(leftElement, rightElement)) {
                    return false
                }
            }

            return !rightIterator.hasNext()
        }

        @JvmStatic
        fun includeElementsEqual(left: IncludeElement, right: IncludeElement): Boolean {
            return left.relatedDataType != null &&
                right.relatedDataType != null &&
                stringsEqual(
                    left.relatedDataType!!.namespaceURI,
                    right.relatedDataType!!.namespaceURI,
                ) &&
                stringsEqual(left.relatedDataType!!.localPart, right.relatedDataType!!.localPart) &&
                stringsEqual(left.relatedProperty, right.relatedProperty) &&
                stringsEqual(left.relatedSearch, right.relatedSearch)
        }

        @JvmStatic
        fun includeElementsEqual(
            left: Iterable<IncludeElement>,
            right: Iterable<IncludeElement>,
        ): Boolean {
            // TODO: Don't rely on order dependence here...
            val leftIterator = left.iterator()
            val rightIterator = right.iterator()

            while (leftIterator.hasNext()) {
                val leftElement = leftIterator.next()

                if (!rightIterator.hasNext()) {
                    return false
                }

                val rightElement = rightIterator.next()

                if (!includeElementsEqual(leftElement, rightElement)) {
                    return false
                }
            }

            return !rightIterator.hasNext()
        }

        @JvmStatic
        fun requirementsEquivalent(left: ElmRequirement, right: ElmRequirement): Boolean {
            val retrieve = left.getElement() as Retrieve
            val otherRetrieve = right.getElement() as Retrieve

            return retrieve.dataType != null &&
                retrieve.dataType!! == otherRetrieve.dataType &&
                stringsEqual(retrieve.templateId, otherRetrieve.templateId) &&
                stringsEqual(retrieve.context, otherRetrieve.context) &&
                stringsEqual(retrieve.contextProperty, otherRetrieve.contextProperty) &&
                stringsEqual(retrieve.contextSearch, otherRetrieve.contextSearch) &&
                stringsEqual(retrieve.codeProperty, otherRetrieve.codeProperty) &&
                stringsEqual(retrieve.codeSearch, otherRetrieve.codeSearch) &&
                stringsEqual(retrieve.codeComparator, otherRetrieve.codeComparator) &&
                stringsEqual(retrieve.valueSetProperty, otherRetrieve.valueSetProperty) &&
                codesEqual(retrieve.codes, otherRetrieve.codes) &&
                stringsEqual(retrieve.dateProperty, otherRetrieve.dateProperty) &&
                stringsEqual(retrieve.dateLowProperty, otherRetrieve.dateLowProperty) &&
                stringsEqual(retrieve.dateHighProperty, otherRetrieve.dateHighProperty) &&
                stringsEqual(retrieve.dateSearch, otherRetrieve.dateSearch) &&
                dateRangesEqual(retrieve.dateRange, otherRetrieve.dateRange) &&
                stringsEqual(retrieve.idProperty, otherRetrieve.idProperty) &&
                stringsEqual(retrieve.idSearch, otherRetrieve.idSearch) &&
                stringsEqual(retrieve.id, otherRetrieve.id) &&
                codeFiltersEqual(retrieve.codeFilter, otherRetrieve.codeFilter) &&
                dateFiltersEqual(retrieve.dateFilter, otherRetrieve.dateFilter) &&
                otherFiltersEqual(
                    retrieve.otherFilter,
                    otherRetrieve.otherFilter,
                ) // TODO: support for collapsing includes
                &&
                stringsEqual(retrieve.includedIn, otherRetrieve.includedIn) &&
                includeElementsEqual(retrieve.include, otherRetrieve.include)
        }

        @JvmStatic
        private fun hasInclude(retrieve: Retrieve, includeElement: IncludeElement): Boolean {
            for (e in retrieve.include) {
                if (includeElementsEqual(e, includeElement)) {
                    return true
                }
            }

            return false
        }

        @JvmStatic
        fun mergeRequirements(existing: ElmRequirement, required: ElmRequirement): ElmRequirement {
            if (existing is ElmDataRequirement) {
                if (required is ElmDataRequirement) {
                    if (existing.getElement() is Retrieve) {
                        if (required.getElement() is Retrieve) {
                            val existingRetrieve = existing.getElement() as Retrieve
                            val requiredRetrieve = required.getElement() as Retrieve
                            val newRetrieve = ElmCloner.clone(existingRetrieve)

                            val trackbacks = requiredRetrieve.trackbacks
                            val newTrackbacks = newRetrieve.trackbacks
                            newTrackbacks.addAll(trackbacks)

                            val newRequirement =
                                ElmDataRequirement(existing.getLibraryIdentifier(), newRetrieve)
                            if (existing.properties != null) {
                                for (property in existing.properties) {
                                    newRequirement.addProperty(property)
                                }
                            }

                            // Merge mustSupport
                            if (required.properties != null) {
                                for (property in required.properties) {
                                    newRequirement.addProperty(property)
                                }
                            }

                            // Merge includes
                            for (includeElement in requiredRetrieve.include) {
                                if (!hasInclude(newRetrieve, includeElement)) {
                                    newRetrieve.include.add(ElmCloner.clone(includeElement))
                                }
                            }

                            return newRequirement
                        }
                    }
                }
            }

            return existing
        }
    }
}
