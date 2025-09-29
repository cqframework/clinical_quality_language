package org.hl7.cql.model

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Suppress("LongMethod")
internal class GenericClassSignatureParserTest {
    @Test
    fun parseTest1() {
        val genericClassSignatureParser = GenericClassSignatureParser("MyType<M,N>", mutableMapOf())
        if (genericClassSignatureParser.isValidGenericSignature) {
            val signature = genericClassSignatureParser.parseGenericSignature()
            assertThat(signature.name, `is`("MyType"))
            assertThat(signature.genericParameters.size, `is`(2))
            assertThat(signature.genericParameters[0].identifier, `is`("M"))
            assertThat(signature.genericParameters[1].identifier, `is`("N"))
        } else {
            Assertions.fail<Any?>("Invalid generic class")
        }
    }

    @Test
    fun parseTest2() {
        val collectionType = ClassType("Collection")
        val resolvedTypes = mutableMapOf<String, DataType>()
        resolvedTypes["Collection"] = collectionType
        val genericClassSignatureParser =
            GenericClassSignatureParser("MyType<M extends Collection,N>", resolvedTypes)
        if (genericClassSignatureParser.isValidGenericSignature) {
            val signature = genericClassSignatureParser.parseGenericSignature()
            assertThat(signature.name, `is`("MyType"))
            assertThat(signature.genericParameters.size, `is`(2))
            assertThat(signature.genericParameters[0].identifier, `is`("M"))
            assertThat(
                signature.genericParameters[0].constraint,
                `is`(TypeParameter.TypeParameterConstraint.TYPE),
            )
            assertThat(
                (signature.genericParameters[0].constraintType as ClassType).name,
                `is`("Collection"),
            )
            assertThat(signature.genericParameters[1].identifier, `is`("N"))
        } else {
            Assertions.fail<Any?>("Invalid generic class")
        }
    }

    @Test
    fun parseTest3() {
        val collectionType = ClassType("Collection")
        val objectType = ClassType("Object")
        val resolvedTypes = mutableMapOf<String, DataType>()
        resolvedTypes["Collection"] = collectionType
        resolvedTypes["Object"] = objectType
        val genericClassSignatureParser =
            GenericClassSignatureParser(
                "MyType<M extends Collection,N extends Object>",
                resolvedTypes,
            )
        if (genericClassSignatureParser.isValidGenericSignature) {
            val signature = genericClassSignatureParser.parseGenericSignature()
            assertThat(signature.name, `is`("MyType"))
            assertThat(signature.genericParameters.size, `is`(2))
            assertThat(signature.genericParameters[0].identifier, `is`("M"))
            assertThat(
                signature.genericParameters[0].constraint,
                `is`(TypeParameter.TypeParameterConstraint.TYPE),
            )
            assertThat(
                (signature.genericParameters[0].constraintType as ClassType).name,
                `is`("Collection"),
            )
            assertThat(signature.genericParameters[1].identifier, `is`("N"))
            assertThat(
                signature.genericParameters[1].constraint,
                `is`(TypeParameter.TypeParameterConstraint.TYPE),
            )
            assertThat(
                (signature.genericParameters[1].constraintType as ClassType).name,
                `is`("Object"),
            )
        } else {
            Assertions.fail<Any?>("Invalid generic class")
        }
    }

    @Test
    fun parseTest4() {
        try {
            val collectionType = ClassType("Collection")
            val resolvedTypes = mutableMapOf<String, DataType>()
            resolvedTypes["Collection"] = collectionType
            val genericClassSignatureParser =
                GenericClassSignatureParser("MyType<M constrains Collection>", resolvedTypes)
            genericClassSignatureParser.parseGenericSignature()
            Assertions.fail<Any?>("Exception should be thrown for invalid parameter syntax")
        } catch (e: Exception) {
            assertThat(e.message, `is`("Invalid parameter syntax: M constrains Collection"))
        }
    }

    @Test
    fun parseTest5() {
        val objectType = ClassType("Object")
        val listType = ClassType("List")
        listType.addGenericParameter(TypeParameter("T"))
        listType.elements.add(
            ClassTypeElement(
                "elements",
                TypeParameter("T"),
                prohibited = false,
                oneBased = false,
                target = null,
            )
        )
        val resolvedTypes = mutableMapOf<String, DataType>()
        resolvedTypes["Object"] = objectType
        resolvedTypes["List"] = listType
        val genericClassSignatureParser =
            GenericClassSignatureParser("MyType<M extends List<Object>>", resolvedTypes)
        if (genericClassSignatureParser.isValidGenericSignature) {
            val signature = genericClassSignatureParser.parseGenericSignature()
            assertThat(signature.name, `is`("MyType"))
            assertThat(signature.genericParameters.size, `is`(1))
            assertThat(signature.genericParameters[0].identifier, `is`("M"))
            assertThat(
                signature.genericParameters[0].constraint,
                `is`(TypeParameter.TypeParameterConstraint.TYPE),
            )
            val listOfObject: ClassType = signature.genericParameters[0].constraintType as ClassType
            assertThat(listOfObject.name, `is`("List[Object]"))
            assertThat(listOfObject.elements.size, `is`(1))
            assertThat((listOfObject.elements[0].type as ClassType).name, `is`("Object"))
        } else {
            Assertions.fail<Any?>("Invalid generic class")
        }
    }

    @Test
    fun parseTest6() {
        val objectType = ClassType("Object")
        val stringType = SimpleType("String")
        val listType = ClassType("List")
        listType.addGenericParameter(TypeParameter("T"))
        listType.elements.add(
            ClassTypeElement(
                "elements",
                TypeParameter("T"),
                prohibited = false,
                oneBased = false,
                target = null,
            )
        )
        val mapType = ClassType("Map")
        mapType.addGenericParameter(TypeParameter("K"))
        mapType.addGenericParameter(TypeParameter("V"))
        mapType.addElement(
            ClassTypeElement(
                "keys",
                TypeParameter("K"),
                prohibited = false,
                oneBased = false,
                target = null,
            )
        )
        mapType.addElement(
            ClassTypeElement(
                "values",
                TypeParameter("V"),
                prohibited = false,
                oneBased = false,
                target = null,
            )
        )
        val resolvedTypes = mutableMapOf<String, DataType>()
        resolvedTypes["Object"] = objectType
        resolvedTypes["String"] = stringType
        resolvedTypes["List"] = listType
        resolvedTypes["Map"] = mapType
        val genericClassSignatureParser =
            GenericClassSignatureParser("MyType<M extends Map<String,List<Object>>>", resolvedTypes)
        if (genericClassSignatureParser.isValidGenericSignature) {
            val signature = genericClassSignatureParser.parseGenericSignature()
            assertThat(signature.name, `is`("MyType"))
            assertThat(signature.genericParameters.size, `is`(1))
            assertThat(signature.genericParameters[0].identifier, `is`("M"))
            assertThat(
                signature.genericParameters[0].constraint,
                `is`(TypeParameter.TypeParameterConstraint.TYPE),
            )
            val map: ClassType = signature.genericParameters[0].constraintType as ClassType
            assertThat(map.name, `is`("Map[String,List[Object]]"))
            assertThat(map.elements.size, `is`(2))
            assertThat((map.elements[0].type as SimpleType).name, `is`("String"))
            assertThat((map.elements[1].type as ClassType).name, `is`("List[Object]"))
        } else {
            Assertions.fail<Any?>("Invalid generic class")
        }
    }

    @Test
    fun parseTest7() {
        val integerType = SimpleType("Integer")
        val stringType = SimpleType("String")
        val listType = ClassType("List")
        listType.addGenericParameter(TypeParameter("T"))
        listType.elements.add(
            ClassTypeElement(
                "elements",
                TypeParameter("T"),
                prohibited = false,
                oneBased = false,
                target = null,
            )
        )
        val mapType = ClassType("Map")
        mapType.addGenericParameter(TypeParameter("K"))
        mapType.addGenericParameter(TypeParameter("V"))
        mapType.addElement(
            ClassTypeElement(
                "keys",
                TypeParameter("K", TypeParameter.TypeParameterConstraint.NONE, null),
                prohibited = false,
                oneBased = false,
                target = null,
            )
        )
        mapType.addElement(
            ClassTypeElement(
                "values",
                TypeParameter("V", TypeParameter.TypeParameterConstraint.NONE, null),
                prohibited = false,
                oneBased = false,
                target = null,
            )
        )
        val resolvedTypes = mutableMapOf<String, DataType>()
        resolvedTypes["Integer"] = integerType
        resolvedTypes["String"] = stringType
        resolvedTypes["List"] = listType
        resolvedTypes["Map"] = mapType
        val genericClassSignatureParser =
            GenericClassSignatureParser(
                "MyType<M extends Map<String,List<Map<String,Integer>>>>",
                resolvedTypes,
            )
        if (genericClassSignatureParser.isValidGenericSignature) {
            val signature = genericClassSignatureParser.parseGenericSignature()
            assertThat(signature.name, `is`("MyType"))
            assertThat(signature.genericParameters.size, `is`(1))
            assertThat(signature.genericParameters[0].identifier, `is`("M"))
            assertThat(
                signature.genericParameters[0].constraint,
                `is`(TypeParameter.TypeParameterConstraint.TYPE),
            )
            val map: ClassType = signature.genericParameters[0].constraintType as ClassType
            assertThat(map.name, `is`("Map[String,List[Map[String,Integer]]]"))
            assertThat(map.elements.size, `is`(2))
            assertThat((map.elements[0].type as SimpleType).name, `is`("String"))
            assertThat((map.elements[1].type as ClassType).name, `is`("List[Map[String,Integer]]"))
        } else {
            Assertions.fail<Any?>("Invalid generic class")
        }
    }
}
