package org.hl7.cql.model

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.elm_modelinfo.r1.ChoiceTypeInfo
import org.hl7.elm_modelinfo.r1.ChoiceTypeSpecifier
import org.hl7.elm_modelinfo.r1.ClassInfo
import org.hl7.elm_modelinfo.r1.ClassInfoElement
import org.hl7.elm_modelinfo.r1.ContextInfo
import org.hl7.elm_modelinfo.r1.ConversionInfo
import org.hl7.elm_modelinfo.r1.IntervalTypeInfo
import org.hl7.elm_modelinfo.r1.IntervalTypeSpecifier
import org.hl7.elm_modelinfo.r1.ListTypeInfo
import org.hl7.elm_modelinfo.r1.ListTypeSpecifier
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.ModelSpecifier
import org.hl7.elm_modelinfo.r1.NamedTypeSpecifier
import org.hl7.elm_modelinfo.r1.SimpleTypeInfo
import org.hl7.elm_modelinfo.r1.TupleTypeInfo
import org.hl7.elm_modelinfo.r1.TupleTypeSpecifier
import org.hl7.elm_modelinfo.r1.TypeInfo
import org.hl7.elm_modelinfo.r1.TypeSpecifier
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml
import org.junit.jupiter.api.Test

@Suppress(
    "ImplicitDefaultLocale",
    "ReturnCount",
    "ForbiddenComment",
    "MaxLineLength",
    "CyclomaticComplexMethod",
    "LongMethod"
)
class ModelInfoComparerTest {
    private fun readModelInfo(resourceName: String): ModelInfo {
        val stream = ModelInfoComparerTest::class.java.getResourceAsStream(resourceName)
        return parseModelInfoXml(stream!!.asSource().buffered())
    }

    @Test
    fun compareModelInfo() {
        val a = readModelInfo("a-modelinfo.xml")
        val b = readModelInfo("b-modelinfo.xml")

        val differences = ModelInfoCompareContext()
        compareModelInfo(differences, a, b)
        MatcherAssert.assertThat(
            differences.toString(),
            Matchers.`is`(
                String.format(
                    ("ModelInfo.Type info allowedUnits in left only%n" +
                        "ModelInfo.Conversion from FHIR.Age to System.Quantity in left only%n" +
                        "ModelInfo.Conversion from FHIR.Count to System.Quantity in left only%n" +
                        "ModelInfo.Conversion from FHIR.Distance to System.Quantity in left only%n" +
                        "ModelInfo.Conversion from FHIR.Duration to System.Quantity in left only%n" +
                        "ModelInfo.Conversion from FHIR.MoneyQuantity to System.Quantity in left only%n" +
                        "ModelInfo.Conversion from FHIR.SimpleQuantity to System.Quantity in left only%n" +
                        "ModelInfo.Conversion from FHIR.id to System.String in left only%n")
                )
            )
        )
        // assertThat(differences.length(), is(0));
    }

    // @Test
    // Not an actual test, Used to determine differences between current and updated model info from
    // the MAT team
    fun compareMATModelInfo() {
        val a = readModelInfo("fhir-modelinfo-4.0.1.xml")
        val b = readModelInfo("mat-fhir-modelinfo-4.0.1.xml")
        val differences = ModelInfoCompareContext()
        compareModelInfo(differences, a, b)
        MatcherAssert.assertThat(differences.length(), Matchers.`is`(0))
    }

    @Test
    fun compareNewModelInfo() {
        val a = readModelInfo("fhir-modelinfo-4.0.1.xml")
        val b = readModelInfo("new-fhir-modelinfo-4.0.1.xml")
        val differences = ModelInfoCompareContext()
        compareModelInfo(differences, a, b)
        MatcherAssert.assertThat(
            differences.toString(),
            Matchers.`is`(
                String.format(
                    ("ModelInfo.AdverseEvent.primaryCodePath: type <> event%n" +
                        "ModelInfo.BodyStructure.primaryCodePath: null <> location%n" +
                        "ModelInfo.DetectedIssue.primaryCodePath: category <> code%n" +
                        "ModelInfo.DeviceRequest.primaryCodePath: codeCodeableConcept <> code%n" +
                        "ModelInfo.Location.primaryCodePath: null <> type%n" +
                        "ModelInfo.PractitionerRole.primaryCodePath: null <> code%n" +
                        "ModelInfo.RelatedPerson.primaryCodePath: null <> relationship%n")
                )
            )
        )
        // assertThat(differences.length(), is(0));
    }

    @Test
    fun compareMetadataModelInfo() {
        val a = readModelInfo("fhir-modelinfo-4.0.1-1.5.1.xml")
        val b = readModelInfo("fhir-modelinfo-4.0.1-with-metadata.xml")
        val differences = ModelInfoCompareContext()
        compareModelInfo(differences, a, b)
        /*
        Comparison of 1.5.1 model info with 1.5.2, the only difference is the addition of metadata:
         */
        MatcherAssert.assertThat(
            differences.toString(),
            Matchers.`is`(
                String.format(
                    ("ModelInfo.ElementDefinition.Type.targetProfile.name: targetProfile <> profile%n" + // backwards compatible, but more accurate ElementDefinition.Type
                        "ModelInfo.ElementDefinition.Type.versioning.name: versioning <> targetProfile%n" + // redeclaration for metadata
                        "ModelInfo.ElementDefinition.Type.versioning.type: FHIR.ReferenceVersionRules <> List<FHIR.canonical>%n" + // ditto
                        "ModelInfo.ElementDefinition.Type.Element aggregation in right only%n" + // ditto
                        "ModelInfo.ElementDefinition.Type.Element versioning in right only%n" + // ditto
                        "ModelInfo.MoneyQuantity.primaryCodePath: code <> null%n" + // primaryCodePath should not be set on a non-retrievable type
                        "ModelInfo.MoneyQuantity.Element value in left only%n" + // MoneyQuantity is
                        // derived from
                        // Quantity, no
                        // need to
                        // re-declare
                        // elements
                        "ModelInfo.MoneyQuantity.Element comparator in left only%n" + // redeclaration for metadata
                        "ModelInfo.MoneyQuantity.Element unit in left only%n" + // ditto
                        "ModelInfo.MoneyQuantity.Element system in left only%n" + // ditto
                        "ModelInfo.MoneyQuantity.Element code in left only%n" + // ditto
                        "ModelInfo.SimpleQuantity.primaryCodePath: code <> null%n" + // primaryCodePath should not be set on a non-retrievable type
                        "ModelInfo.SimpleQuantity.Element value in left only%n" + // SimpleQuantity
                        // is derived from
                        // Quantity, no
                        // need to
                        // re-declare
                        // elements
                        "ModelInfo.SimpleQuantity.Element unit in left only%n" + // redeclaration
                        // for metadata
                        "ModelInfo.SimpleQuantity.Element system in left only%n" + // ditto
                        "ModelInfo.SimpleQuantity.Element code in left only%n" + // ditto
                        "ModelInfo.canonical.Element value in right only%n" + // redeclaration for
                        // metadata
                        "ModelInfo.code.Element value in right only%n" + // redeclaration for
                        // metadata
                        "ModelInfo.id.Element value in right only%n" + // redeclaration for metadata
                        "ModelInfo.markdown.Element value in right only%n" + // redeclaration for
                        // metadata
                        "ModelInfo.oid.Element value in right only%n" + // redeclaration for
                        // metadata
                        "ModelInfo.positiveInt.Element value in right only%n" +
                        "ModelInfo.unsignedInt.Element value in right only%n" + // redeclaration for
                        // metadata
                        "ModelInfo.url.Element value in right only%n" + // redeclaration for
                        // metadata
                        "ModelInfo.uuid.Element value in right only%n" // redeclaration for metadata
                    )
                )
            )
        ) // redeclaration for metadata
    }

    class ModelInfoCompareContext {
        private val differences = StringBuilder()
        private val focusList: MutableList<String> = ArrayList()

        init {
            pushFocus("ModelInfo")
        }

        val focus: String
            get() = focusList.joinToString(".")

        fun pushFocus(newFocus: String) {
            focusList.add(newFocus)
        }

        fun popFocus() {
            if (!focusList.isEmpty()) {
                focusList.removeAt(focusList.size - 1)
            }
        }

        fun append(message: String?) {
            differences.append(this.focus)
            differences.append(".")
            differences.append(message)
            differences.append(System.lineSeparator())
        }

        fun length(): Int {
            return differences.length
        }

        override fun toString(): String {
            return differences.toString()
        }
    }

    companion object {
        fun compareModelInfo(context: ModelInfoCompareContext, a: ModelInfo, b: ModelInfo) {
            compareAttribute(context, "url", a.url, b.url)
            compareAttribute(context, "version", a.version, b.version)
            compareAttribute(context, "targetUrl", a.targetUrl, b.targetUrl)
            compareAttribute(context, "targetVersion", a.targetVersion, b.targetVersion)
            compareAttribute(context, "defaultContext", a.defaultContext, b.defaultContext)
            compareAttribute(context, "patientClassName", a.patientClassName, b.patientClassName)
            compareAttribute(
                context,
                "patientClassIdentifier",
                a.patientClassIdentifier,
                b.patientClassIdentifier
            )
            compareAttribute(
                context,
                "patientBirthDatePropertyName",
                a.patientBirthDatePropertyName,
                b.patientBirthDatePropertyName
            )

            val msa = a.requiredModelInfo.associateBy { it.name!! }
            val msb = b.requiredModelInfo.associateBy { it.name!! }
            for (ms in msa.entries.sortedBy { it.key }) {
                val msOther = msb.getOrDefault(ms.key, null)
                compareModelSpecifier(context, ms.value, msOther)
            }

            for (ms in msb.entries.sortedBy { it.key }) {
                val msOther = msa.getOrDefault(ms.key, null)
                if (msOther == null) {
                    compareModelSpecifier(context, msOther, ms.value)
                }
            }

            // typeInfo
            val tia =
                a.typeInfo
                    .filter { x -> x is ClassInfo || x is SimpleTypeInfo }
                    .associateBy { k ->
                        if (k is ClassInfo) k.name!! else (k as SimpleTypeInfo).name!!
                    }
            val tib =
                b.typeInfo
                    .filter { x -> x is ClassInfo || x is SimpleTypeInfo }
                    .associateBy { k ->
                        if (k is ClassInfo) k.name!! else (k as SimpleTypeInfo).name!!
                    }

            for (ti in tia.entries.sortedBy { it.key }) {
                val tiOther = tib.getOrDefault(ti.key, null)
                compareTypeInfo(context, ti.value, tiOther)
            }

            for (ti in tib.entries.sortedBy { it.key }) {
                val tiOther = tia.getOrDefault(ti.key, null)
                if (tiOther == null) {
                    compareTypeInfo(context, tiOther, ti.value)
                }
            }

            // conversionInfo
            val cia = a.conversionInfo.associateBy { it.fromType!! }
            val cib = b.conversionInfo.associateBy { it.fromType!! }

            for (ci in cia.entries.sortedBy { it.key }) {
                val ciOther = cib.getOrDefault(ci.key, null)
                compareConversionInfo(context, ci.value, ciOther)
            }

            for (ci in cib.entries.sortedBy { it.key }) {
                val ciOther = cia.getOrDefault(ci.key, null)
                if (ciOther == null) {
                    compareConversionInfo(context, ciOther, ci.value)
                }
            }

            // contextInfo
            val cxa = a.contextInfo.associateBy { it.name!! }
            val cxb = b.contextInfo.associateBy { it.name!! }

            for (ci in cxa.entries.sortedBy { it.key }) {
                val ciOther = cxb.getOrDefault(ci.key, null)
                compareContextInfo(context, ci.value, ciOther)
            }

            for (ci in cxb.entries.sortedBy { it.key }) {
                val ciOther = cxa.getOrDefault(ci.key, null)
                if (ciOther == null) {
                    compareContextInfo(context, ciOther, ci.value)
                }
            }
        }

        fun compareAttribute(
            context: ModelInfoCompareContext,
            attributeName: String?,
            a: String?,
            b: String?
        ) {
            if (a == null || a != b) {
                if (a == null && b == null) {
                    return
                }
                context.append(String.format("%s: %s <> %s", attributeName, a, b))
            }
        }

        fun compareAttribute(
            context: ModelInfoCompareContext,
            attributeName: String?,
            a: Boolean?,
            b: Boolean?
        ) {
            if (a == null || a != b) {
                if (a == null && b == null) {
                    return
                }
                context.append(String.format("%s: %s <> %s", attributeName, a, b))
            }
        }

        fun compareModelSpecifier(
            context: ModelInfoCompareContext,
            a: ModelSpecifier?,
            b: ModelSpecifier?
        ) {
            if (a == null) {
                context.append(
                    String.format("Model specifier %s|%s in right only", b!!.name, b.version)
                )
            } else if (b == null) {
                context.append(
                    String.format("Model specifier %s|%s in left only", a.name, a.version)
                )
            } else {
                compareAttribute(context, "version", a.version, b.version)
            }
        }

        fun descriptor(namedTypeSpecifier: NamedTypeSpecifier?): String? {
            if (namedTypeSpecifier != null) {
                if (namedTypeSpecifier.namespace != null) {
                    return String.format(
                        "%s.%s",
                        namedTypeSpecifier.namespace,
                        namedTypeSpecifier.name
                    )
                }

                if (namedTypeSpecifier.modelName != null) {
                    return String.format(
                        "%s.%s",
                        namedTypeSpecifier.modelName,
                        namedTypeSpecifier.name
                    )
                }

                return namedTypeSpecifier.name
            }

            return null
        }

        fun descriptor(intervalTypeSpecifier: IntervalTypeSpecifier?): String? {
            if (intervalTypeSpecifier != null) {
                return String.format(
                    "Interval<%s>",
                    descriptor(
                        intervalTypeSpecifier.pointType,
                        intervalTypeSpecifier.pointTypeSpecifier
                    )
                )
            }

            return null
        }

        fun descriptor(listTypeSpecifier: ListTypeSpecifier?): String? {
            if (listTypeSpecifier != null) {
                return String.format(
                    "List<%s>",
                    descriptor(
                        listTypeSpecifier.elementType,
                        listTypeSpecifier.elementTypeSpecifier
                    )
                )
            }

            return null
        }

        fun descriptor(tupleTypeSpecifier: TupleTypeSpecifier?): String? {
            if (tupleTypeSpecifier != null) {
                // TODO: Expand this...
                return "Tuple<...>"
            }

            return null
        }

        fun descriptor(choiceTypeSpecifier: ChoiceTypeSpecifier?): String? {
            if (choiceTypeSpecifier != null) {
                // TODO: Expand this
                return "Choice<...>"
            }

            return null
        }

        fun descriptor(typeSpecifier: TypeSpecifier?): String? {
            if (typeSpecifier is NamedTypeSpecifier) {
                return descriptor(typeSpecifier)
            }

            if (typeSpecifier is IntervalTypeSpecifier) {
                return descriptor(typeSpecifier)
            }

            if (typeSpecifier is ListTypeSpecifier) {
                return descriptor(typeSpecifier)
            }

            if (typeSpecifier is TupleTypeSpecifier) {
                return descriptor(typeSpecifier)
            }

            if (typeSpecifier is ChoiceTypeSpecifier) {
                return descriptor(typeSpecifier)
            }

            return null
        }

        fun descriptor(elementType: String?, elementTypeSpecifier: TypeSpecifier?): String? {
            if (elementType != null) {
                return elementType
            }

            return descriptor(elementTypeSpecifier)
        }

        fun descriptor(listTypeInfo: ListTypeInfo?): String? {
            if (listTypeInfo != null) {
                return String.format(
                    "List<%s>",
                    descriptor(listTypeInfo.elementType, listTypeInfo.elementTypeSpecifier)
                )
            }

            return null
        }

        fun descriptor(intervalTypeInfo: IntervalTypeInfo?): String? {
            if (intervalTypeInfo != null) {
                return String.format(
                    "Interval<%s>",
                    descriptor(intervalTypeInfo.pointType, intervalTypeInfo.pointTypeSpecifier)
                )
            }

            return null
        }

        fun descriptor(tupleTypeInfo: TupleTypeInfo?): String? {
            if (tupleTypeInfo != null) {
                // TODO: Expand this
                return "Tuple<...>"
            }

            return null
        }

        fun descriptor(choiceTypeInfo: ChoiceTypeInfo?): String? {
            if (choiceTypeInfo != null) {
                // TODO: Expand this
                return "Choice<...>"
            }

            return null
        }

        fun descriptor(typeInfo: TypeInfo?): String? {
            if (typeInfo is ClassInfo) {
                return typeInfo.name
            }
            if (typeInfo is SimpleTypeInfo) {
                return typeInfo.name
            }
            if (typeInfo is ListTypeInfo) {
                return descriptor(typeInfo)
            }
            if (typeInfo is IntervalTypeInfo) {
                return descriptor(typeInfo)
            }
            if (typeInfo is TupleTypeInfo) {
                return descriptor(typeInfo)
            }
            if (typeInfo is ChoiceTypeInfo) {
                return descriptor(typeInfo)
            }

            return null
        }

        fun compareTypeInfo(
            context: ModelInfoCompareContext,
            a: SimpleTypeInfo?,
            b: SimpleTypeInfo?
        ) {
            val descriptorA: String? = descriptor(a)
            val descriptorB: String? = descriptor(b)
            if (descriptorA == null || descriptorA != descriptorB) {
                context.append(String.format("%s <> %s", descriptorA, descriptorB))
            }
        }

        fun compareTypeInfo(context: ModelInfoCompareContext, a: ClassInfo?, b: ClassInfo?) {
            if (a == null || b == null) {
                context.append(String.format("%s <> %s", descriptor(a), descriptor(b)))
            }

            context.pushFocus(a!!.name!!)
            try {
                compareAttribute(
                    context,
                    "baseType",
                    descriptor(a.baseType, a.baseTypeSpecifier),
                    descriptor(b!!.baseType, b.baseTypeSpecifier)
                )
                compareAttribute(context, "label", a.label, b.label)
                compareAttribute(context, "identifier", a.identifier, b.identifier)
                compareAttribute(context, "primaryCodePath", a.primaryCodePath, b.primaryCodePath)
                compareAttribute(
                    context,
                    "primaryValueSetPath",
                    a.primaryValueSetPath,
                    b.primaryValueSetPath
                )
                compareAttribute(context, "target", a.target, b.target)

                for (i in 0 ..< a.element.size.coerceAtLeast(b.element.size)) {
                    if (i >= a.element.size) {
                        context.append(
                            java.lang.String.format("Element %s in right only", b.element[i].name)
                        )
                    } else if (i >= b.element.size) {
                        context.append(
                            java.lang.String.format("Element %s in left only", a.element[i].name)
                        )
                    } else {
                        compareClassInfoElement(context, a.element[i], b.element[i])
                    }
                }
            } finally {
                context.popFocus()
            }
        }

        fun compareClassInfoElement(
            context: ModelInfoCompareContext,
            a: ClassInfoElement,
            b: ClassInfoElement
        ) {
            context.pushFocus(a.name!!)
            try {
                compareAttribute(context, "name", a.name, b.name)
                compareAttribute(
                    context,
                    "type",
                    descriptor(a.elementType, a.elementTypeSpecifier),
                    descriptor(b.elementType, b.elementTypeSpecifier)
                )
                compareAttribute(context, "target", a.target, b.target)
                compareAttribute(context, "isOneBased", a.isOneBased(), b.isOneBased())
                compareAttribute(context, "isProhibited", a.isProhibited(), b.isProhibited())
            } finally {
                context.popFocus()
            }
        }

        fun compareTypeInfo(
            context: ModelInfoCompareContext,
            a: IntervalTypeInfo?,
            b: IntervalTypeInfo?
        ) {
            val descriptorA: String? = descriptor(a)
            val descriptorB: String? = descriptor(b)
            if (descriptorA == null || descriptorA != descriptorB) {
                context.append(String.format("%s <> %s", descriptorA, descriptorB))
            }
        }

        fun compareTypeInfo(context: ModelInfoCompareContext, a: ListTypeInfo?, b: ListTypeInfo?) {
            val descriptorA: String? = descriptor(a)
            val descriptorB: String? = descriptor(b)
            if (descriptorA == null || descriptorA != descriptorB) {
                context.append(String.format("%s <> %s", descriptorA, descriptorB))
            }
        }

        fun compareTypeInfo(
            context: ModelInfoCompareContext,
            a: TupleTypeInfo?,
            b: TupleTypeInfo?
        ) {
            val descriptorA: String? = descriptor(a)
            val descriptorB: String? = descriptor(b)
            if (descriptorA == null || descriptorA != descriptorB) {
                context.append(String.format("%s <> %s", descriptorA, descriptorB))
            }
        }

        fun compareTypeInfo(
            context: ModelInfoCompareContext,
            a: ChoiceTypeInfo?,
            b: ChoiceTypeInfo?
        ) {
            val descriptorA: String? = descriptor(a)
            val descriptorB: String? = descriptor(b)
            if (descriptorA == null || descriptorA != descriptorB) {
                context.append(String.format("%s <> %s", descriptorA, descriptorB))
            }
        }

        fun compareTypeInfo(context: ModelInfoCompareContext, a: TypeInfo?, b: TypeInfo?) {
            if (a == null) {
                context.append(String.format("Type info %s in right only", descriptor(b)))
            } else if (b == null) {
                context.append(String.format("Type info %s in left only", descriptor(a)))
            } else if (a.javaClass != b.javaClass) {
                context.append(
                    java.lang.String.format(
                        "Type info %s is %s in left, but %s in right",
                        descriptor(a),
                        a.javaClass.simpleName,
                        descriptor(b)
                    )
                )
            } else if (a is SimpleTypeInfo) {
                compareTypeInfo(context, a, b as SimpleTypeInfo)
            } else if (a is ClassInfo) {
                compareTypeInfo(context, a, b as ClassInfo)
            } else if (a is IntervalTypeInfo) {
                compareTypeInfo(context, a, b as IntervalTypeInfo)
            } else if (a is ListTypeInfo) {
                compareTypeInfo(context, a, b as ListTypeInfo)
            } else if (a is ChoiceTypeInfo) {
                compareTypeInfo(context, a, b as ChoiceTypeInfo)
            } else if (a is TupleTypeInfo) {
                compareTypeInfo(context, a, b as TupleTypeInfo)
            }
        }

        fun descriptor(conversionInfo: ConversionInfo?): String {
            if (conversionInfo != null) {
                return String.format(
                    "Conversion from %s to %s",
                    descriptor(conversionInfo.fromType, conversionInfo.fromTypeSpecifier),
                    descriptor(conversionInfo.toType, conversionInfo.toTypeSpecifier)
                )
            }

            return ""
        }

        fun compareConversionInfo(
            context: ModelInfoCompareContext,
            a: ConversionInfo?,
            b: ConversionInfo?
        ) {
            if (a == null) {
                context.append(String.format("%s in right only", descriptor(b)))
            } else if (b == null) {
                context.append(String.format("%s in left only", descriptor(a)))
            } else {
                val descriptorA: String = descriptor(a)
                val descriptorB: String = descriptor(b)
                if (descriptorA != descriptorB) {
                    context.append(String.format("%s <> %s", descriptorA, descriptorB))
                }
                compareAttribute(context, "functionName", a.functionName, b.functionName)
            }
        }

        fun descriptor(contextInfo: ContextInfo?): String? {
            if (contextInfo != null) {
                return String.format("Context %s", contextInfo.name)
            }

            return null
        }

        fun compareContextInfo(context: ModelInfoCompareContext, a: ContextInfo?, b: ContextInfo?) {
            if (a == null) {
                context.append(String.format("%s in right only", descriptor(b)))
            } else if (b == null) {
                context.append(String.format("%s in left only", descriptor(a)))
            } else {
                compareAttribute(
                    context,
                    "contextType",
                    descriptor(a.contextType),
                    descriptor(b.contextType)
                )
                compareAttribute(context, "keyElement", a.keyElement, b.keyElement)
                compareAttribute(
                    context,
                    "birthDateElement",
                    a.birthDateElement,
                    b.birthDateElement
                )
            }
        }
    }
}
