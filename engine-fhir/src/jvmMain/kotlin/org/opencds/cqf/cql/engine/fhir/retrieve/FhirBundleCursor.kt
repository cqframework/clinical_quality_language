package org.opencds.cqf.cql.engine.fhir.retrieve

import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.util.BundleUtil
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseResource
import org.opencds.cqf.cql.engine.fhir.exception.UnknownElement

class FhirBundleCursor // This constructor filters the bundle based on dataType
// If templateId is provided, this is a trusted cursor, meaning that it will only return results
// for resources that declare they conform to the given profile
@JvmOverloads
constructor(
    private val fhirClient: IGenericClient,
    private val results: IBaseBundle?,
    private val dataType: String? = null,
    private val templateId: String? = null,
) : Iterable<Any?> {
    /**
     * Returns an iterator over elements of type `T`.
     *
     * @return an Iterator.
     */
    override fun iterator(): Iterator<Any?> {
        return FhirBundleIterator(fhirClient, results, dataType, templateId)
    }

    private inner class FhirBundleIterator(
        private var fhirClient: IGenericClient,
        private var results: IBaseBundle?,
        private var dataType: String?,
        private var templateId: String?,
    ) : Iterator<Any?> {
        private var current: Int
        private var dataTypeClass: Class<out IBaseResource>? = null
        private var currentEntry: MutableList<out IBaseResource>

        init {
            this.current = -1

            // Do not test templateId for base resource "profiles"
            if (
                this.templateId != null &&
                    this.templateId!!.startsWith(
                        "http://hl7.org/fhir/StructureDefinition/$dataType"
                    )
            ) {
                this.templateId = null
            }

            if (dataType != null) {
                this.dataTypeClass =
                    this.fhirClient.fhirContext
                        .getResourceDefinition(this.dataType)
                        .implementingClass
            }

            this.currentEntry = this.entry
        }

        /**
         * Returns `true` if the iteration has more elements. (In other words, returns `true` if
         * [.next] would return an element rather than throwing an exception.)
         *
         * @return `true` if the iteration has more elements
         */
        override fun hasNext(): Boolean {
            return current < this.currentEntry.size - 1 || this.link != null
        }

        val entry: MutableList<out IBaseResource>
            get() {
                return if (this.dataTypeClass != null) {
                    val entries: MutableList<out IBaseResource> =
                        BundleUtil.toListOfResourcesOfType(
                            this.fhirClient.fhirContext,
                            this.results,
                            this.dataTypeClass,
                        )
                    if (templateId != null) {
                        getTrustedEntries(entries, templateId)
                    } else {
                        entries
                    }
                } else {
                    BundleUtil.toListOfResources(this.fhirClient.fhirContext, this.results)
                }
            }

        fun getTrustedEntries(
            entries: MutableList<out IBaseResource>,
            templateId: String?,
        ): MutableList<out IBaseResource> {
            val trustedEntries: MutableList<IBaseResource> = ArrayList()
            for (entry in entries) {
                if (entry.meta != null && entry.meta.profile != null) {
                    for (profile in entry.meta.profile) {
                        if (profile.hasValue() && profile.valueAsString == templateId) {
                            trustedEntries.add(entry)
                        }
                    }
                }
            }
            return trustedEntries
        }

        val link: String?
            get() =
                BundleUtil.getLinkUrlOfType(
                    this.fhirClient.fhirContext,
                    this.results,
                    IBaseBundle.LINK_NEXT,
                )

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws UnknownElement if the iteration has no more elements
         */
        override fun next(): Any {
            current++
            if (current < this.currentEntry.size) {
                return this.currentEntry[current]
            } else {
                this.results = fhirClient.loadPage().next<IBaseBundle?>(results).execute()
                this.currentEntry = this.entry
                current = 0
                if (this.currentEntry.isNotEmpty()) {
                    return this.currentEntry[current]
                }
            }

            // TODO: It would be possible to get here if the next link was present, but the returned
            // page had 0
            // entries...
            // NOTE: This is especially true if the page has only data that is not conformant to the
            // given profile
            throw UnknownElement("The iteration has no more elements.")
        }
    }
}
