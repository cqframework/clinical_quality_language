package org.cqframework.cql.tools.xsd2modelinfo

class ModelImporterMapperValue(@JvmField val targetSystemClass: String?, @JvmField val relationship: Relationship?) {
    enum class Relationship {
        RETYPE,
        EXTEND
    }

    val targetClassElementMap: MutableMap<String?, String?>

    init {
        this.targetClassElementMap = HashMap<String?, String?>()
    }

    fun addClassElementMapping(element: String?, targetElement: String?) {
        targetClassElementMap[element] = targetElement
    }

    companion object {
        fun newRetype(targetSystemClass: String?): ModelImporterMapperValue {
            return ModelImporterMapperValue(targetSystemClass, Relationship.RETYPE)
        }

        fun newExtend(targetSystemClass: String?): ModelImporterMapperValue {
            return ModelImporterMapperValue(targetSystemClass, Relationship.EXTEND)
        }
    }
}
